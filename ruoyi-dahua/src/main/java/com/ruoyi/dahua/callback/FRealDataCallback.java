package com.ruoyi.dahua.callback;

import com.aizuda.zlm4j.structure.MK_MEDIA;
import com.ruoyi.dahua.lib.NetSDKLib;
import com.ruoyi.dahua.lib.NetSDKLib.*;
import com.sun.jna.*;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static com.ruoyi.common.service.ZlmService.ZLM_API;

/**
 * 大华设备实时流处理类
 *
 * @author 陈江灿
 * @date 2025-09-17
 */
public class FRealDataCallback implements NetSDKLib.fRealDataCallBackEx {

    private final MK_MEDIA mkMedia;
    private final double fps;
    private final long timeBaseMs;
    private final int headerLen = 13;
    private final BlockingQueue<byte[]> queue;
    private final ExecutorService worker;
    private final ByteArrayOutputStream partialBuf = new ByteArrayOutputStream();
    private volatile boolean running = true;

    private volatile int videoCodec = 0;
    private volatile long pts = 0;

    private static final int QUEUE_CAPACITY = 400;

    public FRealDataCallback(MK_MEDIA mkMedia, double fps) {
        this.mkMedia = mkMedia;
        this.fps = fps;
        this.timeBaseMs = Math.max(1, Math.round(1000.0 / fps));
        this.queue = new LinkedBlockingQueue<>(QUEUE_CAPACITY);
        this.worker = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "DaHua-Worker");
            t.setDaemon(true);
            return t;
        });
        worker.submit(this::processLoop);
        Native.setCallbackThreadInitializer((Callback) this, new CallbackThreadInitializer(true, false, "DHRealStream"));
    }

    @Override
    public void invoke(NetSDKLib.LLong lRealHandle, int dwDataType, Pointer pBuffer, int dwBufSize, int param, Pointer dwUser) {
        try {
            if (dwBufSize <= headerLen) return;
            byte[] magic = new byte[4];
            pBuffer.read(0, magic, 0, 4);
            if (magic[0] != 68 || magic[1] != 72 || magic[2] != 65 || magic[3] != 86) {
                return;
            }
            int payloadSize = dwBufSize - headerLen;
            if (payloadSize <= 0) return;
            byte[] payload = new byte[payloadSize];
            pBuffer.share(headerLen).read(0, payload, 0, payloadSize);
            boolean offered = queue.offer(payload);
            if (!offered) {
                byte[] removed = queue.poll();
                if (removed != null) {
                    queue.offer(payload);
                } else {
                    System.err.println("推送队列已满，丢弃当前帧");
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private void processLoop() {
        try {
            while (running || !queue.isEmpty()) {
                byte[] payload = queue.poll(200, TimeUnit.MILLISECONDS);
                if (payload == null) {
                    continue;
                }
                try {
                    handlePayload(payload);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        } catch (InterruptedException e) {
        } finally {
            // 清理
        }
    }

    private void handlePayload(byte[] payload) throws Exception {
        if (payload == null || payload.length == 0) return;
        if (partialBuf.size() > 0) {
            partialBuf.write(payload);
            byte[] combined = partialBuf.toByteArray();
            partialBuf.reset();
            parseAndPushNalUnits(combined);
        } else {
            parseAndPushNalUnits(payload);
        }
    }

    private void parseAndPushNalUnits(byte[] data) {
        int len = data.length;
        if (len < 3) {
            try {
                partialBuf.write(data);
            } catch (Exception e) { e.printStackTrace(); }
            return;
        }
        List<Integer> startCodes = new ArrayList<>();
        for (int i = 0; i <= len - 3; i++) {
            if (data[i] == 0 && data[i + 1] == 0 && data[i + 2] == 1) {
                startCodes.add(i);
                i += 2;
            } else if (i <= len - 4 && data[i] == 0 && data[i + 1] == 0 && data[i + 2] == 0 && data[i + 3] == 1) {
                startCodes.add(i);
                i += 3;
            }
        }
        if (startCodes.isEmpty()) {
            try {
                partialBuf.write(data);
            } catch (Exception e) { e.printStackTrace(); }
            return;
        }
        for (int idx = 0; idx < startCodes.size(); idx++) {
            int start = startCodes.get(idx);
            int nextStart = (idx + 1 < startCodes.size()) ? startCodes.get(idx + 1) : -1;
            if (nextStart == -1) {
                try {
                    partialBuf.write(data, start, len - start);
                } catch (Exception e) { e.printStackTrace(); }
                break;
            } else {
                int naluLen = nextStart - start;
                byte[] nalu = new byte[naluLen];
                System.arraycopy(data, start, nalu, 0, naluLen);
                pushNalu(nalu);
            }
        }
    }

    private void pushNalu(byte[] nalu) {
        if (nalu == null || nalu.length < 4) return;
        int scLen = detectStartCodeLength(nalu, 0);
        if (scLen == 0 || nalu.length <= scLen) return;

        byte nalHeader = nalu[scLen];
        int naluTypeH264 = nalHeader & 0x1F;
        int naluTypeH265 = (nalHeader >> 1) & 0x3F;

        try {
            if (videoCodec == 0) {
                if (naluTypeH264 == 7 || naluTypeH264 == 8 || (naluTypeH264 >= 1 && naluTypeH264 <= 5)) {
                    videoCodec = 1;
                } else if (naluTypeH265 == 32 || naluTypeH265 == 33 || naluTypeH265 == 34 || (naluTypeH265 >= 0 && naluTypeH265 <= 40)) {
                    videoCodec = 2;
                }
            }

            if (videoCodec == 1) {
                if (naluTypeH264 == 7 || naluTypeH264 == 8) {
                    pushToZlmH264(nalu, false);
                } else {
                    pushToZlmH264(nalu, true);
                }
            } else if (videoCodec == 2) {
                if (naluTypeH265 == 32 || naluTypeH265 == 33 || naluTypeH265 == 34) {
                    pushToZlmH265(nalu, false);
                } else {
                    pushToZlmH265(nalu, true);
                }
            } else {
                System.err.println("未知视频编码，尝试以H264推送 naluTypeH264=" + naluTypeH264 + " naluTypeH265=" + naluTypeH265);
                pushToZlmH264(nalu, true);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private void pushToZlmH264(byte[] nalu, boolean increasePts) {
        Memory mem = new Memory(nalu.length);
        mem.write(0, nalu, 0, nalu.length);
        try {
            ZLM_API.mk_media_input_h264(mkMedia, mem, nalu.length, pts, pts);
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
        }
        if (increasePts) {
            pts += timeBaseMs;
        }
    }

    private void pushToZlmH265(byte[] nalu, boolean increasePts) {
        Memory mem = new Memory(nalu.length);
        mem.write(0, nalu, 0, nalu.length);
        try {
            ZLM_API.mk_media_input_h265(mkMedia, mem, nalu.length, pts, pts);
        } catch (NoSuchMethodError | UnsatisfiedLinkError e) {
            System.err.println("ZLM不支持 mk_media_input_h265，改为尝试 mk_media_input_h264");
            try {
                ZLM_API.mk_media_input_h264(mkMedia, mem, nalu.length, pts, pts);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        if (increasePts) {
            pts += timeBaseMs;
        }
    }

    private int detectStartCodeLength(byte[] buf, int pos) {
        if (pos + 3 < buf.length && buf[pos] == 0 && buf[pos + 1] == 0 && buf[pos + 2] == 0 && buf[pos + 3] == 1) {
            return 4;
        } else if (pos + 2 < buf.length && buf[pos] == 0 && buf[pos + 1] == 0 && buf[pos + 2] == 1) {
            return 3;
        }
        return 0;
    }

    public void shutdown() {
        running = false;
        worker.shutdownNow();
        queue.clear();
        try {
            worker.awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException ignored) {}
    }
}
