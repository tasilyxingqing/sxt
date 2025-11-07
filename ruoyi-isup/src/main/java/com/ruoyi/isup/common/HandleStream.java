package com.ruoyi.isup.common;

import lombok.extern.slf4j.Slf4j;
import org.bytedeco.ffmpeg.avcodec.AVPacket;
import org.bytedeco.ffmpeg.avformat.AVFormatContext;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@Slf4j
public class HandleStream {

    public static class BlockingQueueInputStream extends InputStream {
        private final LinkedBlockingQueue<byte[]> queue = new LinkedBlockingQueue<>();
        private static final byte[] EOF = new byte[0];
        private volatile boolean closed = false;

        private byte[] current = null;
        private int pos = 0;

        public void write(byte[] data) throws IOException, InterruptedException {
            if (data == null || data.length == 0) return;
            if (closed) throw new IOException("Stream closed");
            byte[] copy = Arrays.copyOf(data, data.length);
            queue.put(copy);
        }

        public void closeStream() {
            if (closed) return;
            closed = true;
            for (int i = 0; i < 3; i++) {
                boolean offered = queue.offer(EOF);
                if (offered) return;
                try {
                    TimeUnit.MILLISECONDS.sleep(5);
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                }
            }
            try {
                queue.put(EOF);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        @Override
        public int read() throws IOException {
            byte[] one = new byte[1];
            int r = read(one, 0, 1);
            if (r == -1) return -1;
            return one[0] & 0xFF;
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            if (b == null) throw new NullPointerException();
            if (off < 0 || len < 0 || off + len > b.length) throw new IndexOutOfBoundsException();
            int totalRead = 0;
            while (totalRead < len) {
                if (current == null || pos >= current.length) {
                    try {
                        byte[] next;
                        next = queue.take();
                        if (next == EOF) {
                            if (totalRead == 0) {
                                return -1;
                            } else {
                                break;
                            }
                        }
                        current = next;
                        pos = 0;
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new IOException("Interrupted while reading from stream", e);
                    }
                }
                int toCopy = Math.min(len - totalRead, current.length - pos);
                System.arraycopy(current, pos, b, off + totalRead, toCopy);
                pos += toCopy;
                totalRead += toCopy;
                if (pos >= current.length) {
                    current = null;
                    pos = 0;
                }
            }
            return totalRead == 0 ? -1 : totalRead;
        }

        @Override
        public void close() throws IOException {
            closeStream();
            super.close();
        }
    }

    private final BlockingQueueInputStream inputStream;
    private FFmpegFrameGrabber grabber;
    private FFmpegFrameRecorder recorder;
    private volatile boolean running;

    public Thread thread;
    private int count;
    public String pushAddress;

    private final CompletableFuture<String> completableFutureString;
    private volatile boolean alreadyCompleted = false;

    public HandleStream(String address, CompletableFuture<String> completableFuture) {
        this.pushAddress = address;
        this.completableFutureString = completableFuture;
        this.inputStream = new BlockingQueueInputStream();
        this.running = true;
        log.info("创建视频流处理类对象, BlockingQueueInputStream@" + inputStream.hashCode());
        startProcessing();
    }

    // 设备回调或上层调用此方法写入数据
    public void processStream(byte[] data) {
        if (data == null || data.length == 0) return;
        if (!running) {
            log.warn("processStream called but handler not running, dropping data");
            return;
        }
        try {
            inputStream.write(data);
        } catch (IOException e) {
            log.error("写入 BlockingQueueInputStream 失败: {}", e.getMessage());
            safeComplete("false");
            running = false;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("写入被中断: {}", e.getMessage());
            safeComplete("false");
            running = false;
        }
    }

    private void startProcessing() {
        thread = new Thread(() -> {
            try {
                grabber = new FFmpegFrameGrabber(inputStream, 0);
                grabber.setFormat("mpeg");
                grabber.setOption("probesize", "32768");
                grabber.setOption("analyzeduration", "0");
                grabber.setOption("flags", "low_delay");
                grabber.setOption("strict", "experimental");
                grabber.start();
                log.info("视频宽度:" + grabber.getImageWidth());
                log.info("视频高度:" + grabber.getImageHeight());
                log.info("音频通道:" + grabber.getAudioChannels());
                recorder = new FFmpegFrameRecorder(pushAddress, grabber.getImageWidth(), grabber.getImageHeight(), grabber.getAudioChannels());
                recorder.setInterleaved(true);
                recorder.setVideoOption("crf", "28");
                recorder.setFormat("flv");
                recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
                recorder.setSampleRate(grabber.getSampleRate());
                recorder.setFrameRate(grabber.getFrameRate());
                recorder.setVideoBitrate(3000000);
                recorder.setGopSize((int) grabber.getFrameRate());
                recorder.setPixelFormat(avutil.AV_PIX_FMT_YUV420P);
                recorder.setVideoOption("tune", "zerolatency");
                recorder.setVideoOption("preset", "ultrafast");
                recorder.setVideoOption("x264opts", "sync-lookahead=0:rc-lookahead=0");
                recorder.setVideoOption("bf", "0");
                recorder.setVideoOption("flush_packets", "1");
                recorder.setVideoOption("threads", "1");
                recorder.setOption("rtmp_buffer", "100");
                recorder.setOption("rtmp_live", "live");
                recorder.start(grabber.getFormatContext());
                count = 0;
                AVPacket packet;
                while (running && (packet = grabber.grabPacket()) != null) {
                    count++;
                    recorder.recordPacket(packet);
                    safeComplete("true");
                    if (count % 100 == 0) {
                        log.info("JavaCV 推流帧====>" + count);
                    }
                }
            } catch (Exception e) {
                log.error("grabber/recorder 异常", e);
                safeComplete("false");
            } finally {
                try {
                    if (grabber != null) {
                        try {
                            grabber.stop();
                        } catch (Exception ex) {
                            log.warn("grabber stop 异常: {}", ex.getMessage());
                        }
                        try {
                            grabber.release();
                        } catch (Exception ex) {
                            log.warn("grabber release 异常: {}", ex.getMessage());
                        }
                    }
                    if (recorder != null) {
                        try {
                            recorder.stop();
                        } catch (Exception ex) {
                            log.warn("recorder stop 异常: {}", ex.getMessage());
                        }
                        try {
                            recorder.release();
                        } catch (Exception ex) {
                            log.warn("recorder release 异常: {}", ex.getMessage());
                        }
                    }
                } catch (Exception e) {
                    log.error("释放 grabber/recorder 异常: {}", e.getMessage());
                } finally {
                    try {
                        inputStream.closeStream();
                    } catch (Exception e) {
                        log.warn("关闭 inputStream 异常: {}", e.getMessage());
                    }
                }
            }
        }, "HandleStream-Processor");
        thread.start();
    }

    private void safeComplete(String result) {
        if (completableFutureString == null) return;
        if (!alreadyCompleted) {
            synchronized (this) {
                if (!alreadyCompleted) {
                    try {
                        completableFutureString.complete(result);
                    } catch (Exception e) {
                        log.warn("complete future 异常: {}", e.getMessage());
                    }
                    alreadyCompleted = true;
                }
            }
        }
    }

    public void stopProcessing() {
        running = false;
        try {
            inputStream.closeStream();
        } catch (Exception e) {
            log.warn("stopProcessing closeStream 异常: {}", e.getMessage());
        }
        if (thread != null) {
            thread.interrupt();
        }
        log.info("已关闭 javacv 视频处理线程");
    }
}
