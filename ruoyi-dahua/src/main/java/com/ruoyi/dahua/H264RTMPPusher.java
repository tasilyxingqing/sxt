package com.ruoyi.dahua;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.IOException;

public class H264RTMPPusher {

    private Process ffmpegProcess;
    private OutputStream inputStream; // 写入到 FFmpeg 的 stdin
    private boolean isStarted = false;

    private final String rtmpUrl;
    private final int width;
    private final int height;
    private final int fps;

    public H264RTMPPusher(String rtmpUrl, int width, int height, int fps) {
        this.rtmpUrl = rtmpUrl;
        this.width = width;
        this.height = height;
        this.fps = fps;
    }

    /**
     * 启动 FFmpeg 推流进程
     */
    public synchronized void start() throws IOException {
        if (isStarted) return;
        ProcessBuilder pb = new ProcessBuilder(
                "D:\\\\ffmpeg\\\\ffmpeg-7.1.1-essentials_build\\\\bin\\\\ffmpeg.exe",
                "-y",                                   // 覆盖输出
                "-f", "h264",                            // 输入格式：H.264 裸流
                "-r", String.valueOf(fps),               // 输入帧率
                "-i", "-",                               // 从 stdin 读取输入
                "-c:v", "copy",                          // 不转码，直接复制 H.264 流
                "-f", "flv",                             // 输出封装格式：FLV（用于 RTMP）
                rtmpUrl                                  // 输出地址
        );
        pb.redirectErrorStream(true);

        try {
            ffmpegProcess = pb.start();
            inputStream = ffmpegProcess.getOutputStream();
            isStarted = true;
            new Thread(() -> {
                BufferedReader br = null;
                try {
                    br = new BufferedReader(new InputStreamReader(ffmpegProcess.getInputStream()));
                    String line;
                    while ((line = br.readLine()) != null) {
                        System.out.println("FFmpeg: " + line);
                    }
                } catch (IOException e) {
                    System.err.println("读取 FFmpeg 输出流时发生异常");
                    e.printStackTrace();
                } finally {
                    if (br != null) {
                        try {
                            br.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();

            System.out.println("FFmpeg 推流进程已启动: " + rtmpUrl);

        } catch (IOException e) {
            throw new IOException("启动 FFmpeg 失败，请检查是否已安装 FFmpeg", e);
        }
    }

    /**
     * 推送一帧 H.264 数据
     */
    public synchronized void push(byte[] h264Bytes) {
        if (!isStarted || h264Bytes == null || h264Bytes.length == 0) return;

        try {
            inputStream.write(h264Bytes);
            inputStream.flush(); // 必须 flush，否则数据不会立即发送
        } catch (IOException e) {
            System.err.println("推送 H.264 数据失败，可能 FFmpeg 已退出");
            e.printStackTrace();
            stop();
        }
    }

    /**
     * 停止推流
     */
    public synchronized void stop() {
        if (isStarted) {
            try {
                inputStream.close();
                if (ffmpegProcess.isAlive()) {
                    ffmpegProcess.destroyForcibly();
                }
                ffmpegProcess.waitFor();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                isStarted = false;
            }
            System.out.println("FFmpeg 推流已停止");
        }
    }

    public boolean isStarted() {
        return isStarted;
    }
}
