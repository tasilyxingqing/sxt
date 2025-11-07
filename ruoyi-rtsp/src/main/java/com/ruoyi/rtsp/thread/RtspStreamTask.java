package com.ruoyi.rtsp.thread;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import com.ruoyi.rtsp.config.ODConfig;
import com.ruoyi.rtsp.domain.ODResult;
import com.ruoyi.rtsp.domain.bo.RtspDeviceBo;
import com.ruoyi.rtsp.utils.ImageUtil;
import com.ruoyi.rtsp.utils.Letterbox;
import nu.pattern.OpenCV;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.HashMap;

/**
 * 推流任务-逻辑
 *
 * @Author: 陈江灿
 * @CreateTime: 2025-04-28
 */
public class RtspStreamTask implements Runnable {
    private final RtspDeviceBo bo;

    public RtspStreamTask(RtspDeviceBo bo) {
        this.bo = bo;
    }

    @Override
    public void run() {
        try {
            processStream();
        } catch (Exception e) {
            System.err.println("推流任务发生异常：" + e.getMessage());
            e.printStackTrace();
        }
    }

    private void processStream() throws OrtException, IOException {
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
        session.getInputInfo().keySet().forEach(x -> {
            try {
                System.out.println("input name = " + x);
                System.out.println(session.getInputInfo().get(x).getInfo().toString());
            } catch (OrtException e) {
                throw new RuntimeException(e);
            }
        });
        ODConfig odConfig = new ODConfig();
        VideoCapture video = new VideoCapture();
        video.open(bo.getUrl());
        if (!video.isOpened()) {
            System.err.println("打开视频流失败！");
        }
        int minDwDh = Math.min((int)video.get(Videoio.CAP_PROP_FRAME_WIDTH), (int)video.get(Videoio.CAP_PROP_FRAME_HEIGHT));
        int thickness = minDwDh / ODConfig.lineThicknessRatio;
        double fontSize = minDwDh / ODConfig.fontSizeRatio;
        int fontFace = Imgproc.FONT_HERSHEY_SIMPLEX;
        // 推流地址
        String rtmpUrl = "rtmp://192.168.2.199/live/livestream/" + bo.getMediaId();
        int frameWidth = (int) video.get(Videoio.CAP_PROP_FRAME_WIDTH);
        int frameHeight = (int) video.get(Videoio.CAP_PROP_FRAME_HEIGHT);
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
        int detect_skip = 4;
        int detect_skip_index = 1;
        float[][] outputData   = null;
        Letterbox letterbox = new Letterbox();
        OnnxTensor tensor = null;
        OrtSession.Result output = null;
        while (video.read(img)) {
            if ((detect_skip_index % detect_skip == 0) || outputData == null) {
                try {
                    Mat image = img.clone();
                    image = letterbox.letterbox(image);
                    Imgproc.cvtColor(image, image, Imgproc.COLOR_BGR2RGB);
                    image.convertTo(image, CvType.CV_32FC1, 1. / 255);
                    float[] whc = new float[3 * 640 * 640];
                    image.get(0, 0, whc);
                    float[] chw = ImageUtil.whc2cwh(whc);
                    detect_skip_index = 1;
                    FloatBuffer inputBuffer = FloatBuffer.wrap(chw);
                    tensor = OnnxTensor.createTensor(environment, inputBuffer, new long[]{1, 3, 640, 640});
                    HashMap<String, OnnxTensor> stringOnnxTensorHashMap = new HashMap<>();
                    stringOnnxTensorHashMap.put(session.getInputInfo().keySet().iterator().next(), tensor);
                    output = session.run(stringOnnxTensorHashMap);
                    outputData = (float[][]) output.get(0).getValue();
                } catch (OrtException e) {
                    System.err.println("推理出错：" + e.getMessage());
                } finally {
                    if (output != null) {
                        output.close();
                    }
                    if (tensor != null) {
                        tensor.close();
                    }
                }
            } else {
                detect_skip_index++;
            }
            for (float[] x : outputData) {
                ODResult odResult = new ODResult(x);
                Point topLeft = new Point((odResult.getX0() - letterbox.getDw()) / letterbox.getRatio(), (odResult.getY0() - letterbox.getDh()) / letterbox.getRatio());
                Point bottomRight = new Point((odResult.getX1() - letterbox.getDw()) / letterbox.getRatio(), (odResult.getY1() - letterbox.getDh()) / letterbox.getRatio());
                Scalar color = new Scalar(odConfig.getOtherColor(odResult.getClsId()));
                Imgproc.rectangle(img, topLeft, bottomRight, color, thickness);
                String boxName = labels[odResult.getClsId()];
                Point boxNameLoc = new Point((odResult.getX0() - letterbox.getDw()) / letterbox.getRatio(), (odResult.getY0() - letterbox.getDh()) / letterbox.getRatio() - 3);
                Imgproc.putText(img, boxName, boxNameLoc, fontFace, 0.7, color, thickness);
            }
            try {
                byte[] imageData = new byte[(int) (img.total() * img.channels())];
                img.get(0, 0, imageData);
                ffmpegInput.write(imageData);
                ffmpegInput.flush();
            } catch (IOException e) {
                System.err.println("写入 FFmpeg 输入流失败：" + e.getMessage());
                break;
            }
        }
        ffmpegInput.close();
        ffmpegProcess.destroy();
        video.release();
    }
}
