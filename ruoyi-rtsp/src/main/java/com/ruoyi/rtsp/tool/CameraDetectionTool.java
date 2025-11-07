package com.ruoyi.rtsp.tool;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import com.ruoyi.rtsp.config.ODConfig;
import com.ruoyi.rtsp.domain.ODResult;
import com.ruoyi.rtsp.domain.bo.RtspDeviceBo;
import com.ruoyi.rtsp.utils.ImageUtil;
import com.ruoyi.rtsp.utils.Letterbox;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;
import org.opencv.core.Point;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.HashMap;

/**
 * @Author: 陈江灿
 * @CreateTime: 2025-04-30
 */
public class CameraDetectionTool {

    private static volatile boolean running = false;

    /**
     * 启动摄像头检测和推流
     *
     * @param bo 摄像头设备信息
     * @throws OrtException
     * @throws IOException
     */
    public static void cameraDetection(RtspDeviceBo bo) throws OrtException, IOException {
        running = true;
        new Thread(() -> {
            try {
                nu.pattern.OpenCV.loadLocally();
                String OS = System.getProperty("os.name").toLowerCase();
                if (OS.contains("win")) {
                    System.load(ClassLoader.getSystemResource("lib/opencv_videoio_ffmpeg470_64.dll").getPath());
                }
                String model_path = "D:\\java\\iot\\ry\\RuoYi-Wvp\\ruoyi-rtsp\\src\\main\\resources\\model\\yolov7-tiny.onnx";
                String[] labels = {
                        "person", "bicycle", "car", "motorcycle", "airplane", "bus", "train",
                        "truck", "boat", "traffic light", "fire hydrant", "stop sign", "parking meter",
                        "bench", "bird", "cat", "dog", "horse", "sheep", "cow", "elephant", "bear",
                        "zebra", "giraffe", "backpack", "umbrella", "handbag", "tie", "suitcase",
                        "frisbee", "skis", "snowboard", "sports ball", "kite", "baseball bat",
                        "baseball glove", "skateboard", "surfboard", "tennis racket", "bottle",
                        "wine glass", "cup", "fork", "knife", "spoon", "bowl", "banana", "apple",
                        "sandwich", "orange", "broccoli", "carrot", "hot dog", "pizza", "donut",
                        "cake", "chair", "couch", "potted plant", "bed", "dining table", "toilet",
                        "tv", "laptop", "mouse", "remote", "keyboard", "cell phone", "microwave",
                        "oven", "toaster", "sink", "refrigerator", "book", "clock", "vase", "scissors",
                        "teddy bear", "hair drier", "toothbrush"};
                OrtEnvironment environment = OrtEnvironment.getEnvironment();
                OrtSession.SessionOptions sessionOptions = new OrtSession.SessionOptions();
                OrtSession session = environment.createSession(model_path, sessionOptions);
                ODConfig odConfig = new ODConfig();
                VideoCapture video = new VideoCapture();
                video.open(bo.getUrl());
                if (!video.isOpened()) {
                    System.err.println("打开视频流失败！");
                    return;
                }
                int frameWidth = (int) video.get(Videoio.CAP_PROP_FRAME_WIDTH);
                int frameHeight = (int) video.get(Videoio.CAP_PROP_FRAME_HEIGHT);
                String rtmpUrl = "rtmp://192.168.158.199:1935/live/" + bo.getMediaId() + "?sign=41db35390ddad33f83944f44b8b75ded";
                ProcessBuilder processBuilder = new ProcessBuilder(
                        "ffmpeg",
                        "-y",
                        "-f", "rawvideo",
                        "-vcodec", "rawvideo",
                        "-pix_fmt", "bgr24",
                        "-s", frameWidth + "x" + frameHeight,
                        "-r", String.valueOf(video.get(Videoio.CAP_PROP_FPS)),
                        "-i", "-",
                        "-c:v", "libx264",
                        "-preset", "ultrafast",
                        "-f", "flv",
                        rtmpUrl
                );

                Process ffmpegProcess = processBuilder.start();

                new Thread(() -> {
                    try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(ffmpegProcess.getErrorStream()))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            System.err.println("[FFmpeg] " + line);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();

                java.io.OutputStream ffmpegInput = ffmpegProcess.getOutputStream();
                Mat img = new Mat();
                Letterbox letterbox = new Letterbox();
                OnnxTensor tensor = null;
                OrtSession.Result output = null;

                while (running && video.read(img)) {
                    try {
                        Mat image = img.clone();
                        image = letterbox.letterbox(image);
                        Imgproc.cvtColor(image, image, Imgproc.COLOR_BGR2RGB);
                        image.convertTo(image, CvType.CV_32FC1, 1. / 255);
                        float[] whc = new float[3 * 640 * 640];
                        image.get(0, 0, whc);
                        float[] chw = ImageUtil.whc2cwh(whc);
                        FloatBuffer inputBuffer = FloatBuffer.wrap(chw);
                        tensor = OnnxTensor.createTensor(environment, inputBuffer, new long[]{1, 3, 640, 640});
                        HashMap<String, OnnxTensor> stringOnnxTensorHashMap = new HashMap<>();
                        stringOnnxTensorHashMap.put(session.getInputInfo().keySet().iterator().next(), tensor);
                        output = session.run(stringOnnxTensorHashMap);
                        float[][] outputData = (float[][]) output.get(0).getValue();

                        for (float[] x : outputData) {
                            ODResult odResult = new ODResult(x);
                            Point topLeft = new Point((odResult.getX0() - letterbox.getDw()) / letterbox.getRatio(), (odResult.getY0() - letterbox.getDh()) / letterbox.getRatio());
                            Point bottomRight = new Point((odResult.getX1() - letterbox.getDw()) / letterbox.getRatio(), (odResult.getY1() - letterbox.getDh()) / letterbox.getRatio());
                            Scalar color = new Scalar(odConfig.getOtherColor(odResult.getClsId()));
                            Imgproc.rectangle(img, topLeft, bottomRight, color, 2);
                            String boxName = labels[odResult.getClsId()];
                            Point boxNameLoc = new Point((odResult.getX0() - letterbox.getDw()) / letterbox.getRatio(), (odResult.getY0() - letterbox.getDh()) / letterbox.getRatio() - 3);
                            Imgproc.putText(img, boxName, boxNameLoc, Imgproc.FONT_HERSHEY_SIMPLEX, 0.7, color, 2);
                        }

                        byte[] imageData = new byte[(int) (img.total() * img.channels())];
                        img.get(0, 0, imageData);
                        ffmpegInput.write(imageData);
                        ffmpegInput.flush();
                    } catch (OrtException | IOException e) {
                        System.err.println("推理或写入 FFmpeg 输入流失败：" + e.getMessage());
                        break;
                    } finally {
                        if (output != null) {
                            output.close();
                        }
                        if (tensor != null) {
                            tensor.close();
                        }
                    }
                }
                ffmpegInput.close();
                ffmpegProcess.destroy();
                video.release();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                running = false;
            }
        }).start();
    }

    /**
     * 停止摄像头检测和推流
     */
    public static void stopCameraDetection() {
        running = false;
        System.out.println("已请求停止摄像头检测...");
    }


}
