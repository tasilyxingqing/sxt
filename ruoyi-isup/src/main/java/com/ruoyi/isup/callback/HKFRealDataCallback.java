package com.ruoyi.isup.callback;

import com.aizuda.zlm4j.structure.MK_MEDIA;
import com.ruoyi.common.codec.G711ACodec;
import com.ruoyi.isup.service.streamService.HCNetSDK;
import com.sun.jna.CallbackThreadInitializer;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.ByteByReference;

import java.io.IOException;

import static com.ruoyi.common.service.ZlmService.ZLM_API;

public class HKFRealDataCallback implements HCNetSDK.FRealDataCallBack_V30 {

    private final MK_MEDIA mkMedia;
    private final Memory buffer = new Memory(1024 * 1024);
    private int bufferSize = 0;
    private long pts;
    private double fps;
    private long time_base;
    private int videoType = 0;
    private int audioType = 0;

    public HKFRealDataCallback(MK_MEDIA mkMedia, double fps) {
        this.mkMedia = mkMedia;
        this.fps = fps;
        //ZLM以1000为时间基准
        time_base = (long) (1000 / fps);
        //回调使用同一个线程
        Native.setCallbackThreadInitializer(this, new CallbackThreadInitializer(true, false, "HikRealStream"));
    }


    @Override
    public void invoke(int lRealHandle, int dwDataType, Pointer pBuffer, int dwBufSize, Pointer pUser) {
        if (dwDataType == HCNetSDK.NET_DVR_STREAMDATA) {
            int offset = 0;
            offset = readPSHAndPSMAndPSMT(pBuffer, offset);
            //读取pes数据
            readPES(pBuffer, offset, dwBufSize);
        }
    }


    /**
     * 读取pes及数据
     *
     * @param pointer
     * @param offset
     */
    private void readPES(Pointer pointer, int offset, int dwSize) {
        //pes header
        byte[] pesHeaderStartCode = new byte[3];
        pointer.read(offset, pesHeaderStartCode, 0, pesHeaderStartCode.length);
        if ((pesHeaderStartCode[0] & 0xFF) == 0x00 && (pesHeaderStartCode[1] & 0xFF) == 0x00 && (pesHeaderStartCode[2] & 0xFF) == 0x01) {
            offset = offset + pesHeaderStartCode.length;
            byte[] streamTypeByte = new byte[1];
            pointer.read(offset, streamTypeByte, 0, streamTypeByte.length);
            offset = offset + streamTypeByte.length;
            int streamType = streamTypeByte[0] & 0xFF;
            //视频流
            if (streamType >= 0xE0 && streamType <= 0xEF) {
                //视频数据
                readVideoES(pointer, offset, dwSize);
            } else if (streamType >= 0xC0 & streamType <= 0xDF) {
                //音频数据
                //readAudioES(pointer, offset);
            }
        }
    }

    /**
     * 读取视频数据
     *
     * @param pointer
     * @param offset
     */
    private void readVideoES(Pointer pointer, int offset, int dwSize) {
        byte[] pesLengthByte = new byte[2];
        pointer.read(offset, pesLengthByte, 0, pesLengthByte.length);
        offset = offset + pesLengthByte.length;
        int pesLength = (pesLengthByte[0] & 0xFF) << 8 | (pesLengthByte[1] & 0xFF);
        //pes数据
        if (pesLength > 0) {
            byte[] pts_dts_length_info = new byte[3];
            pointer.read(offset, pts_dts_length_info, 0, pts_dts_length_info.length);
            offset = offset + pts_dts_length_info.length;
            int pesHeaderLength = (pts_dts_length_info[2] & 0xFF);
            //判断是否是有pts 忽略dts
            int i = (pts_dts_length_info[1] & 0xFF) >> 6;
            if (i == 0x02 || i == 0x03) {
                //byte[] pts_dts = new byte[5];
                //pointer.read(offset, pts_dts, 0, pts_dts.length);
                //这里获取的是以90000为时间基的 需要转为 1/1000为基准的 但是pts还是不够平滑导致画面卡顿 所以不采用读取的pts
                //long pts_90000 = ((pts_dts[0] & 0x0e) << 29) | (((pts_dts[1] << 8 | pts_dts[2]) & 0xfffe) << 14) | (((pts_dts[3] << 8 | pts_dts[4]) & 0xfffe) >> 1);
                pts = time_base + pts;
            }
            offset = offset + pesHeaderLength;
            byte[] naluStart = new byte[4];
            pointer.read(offset, naluStart, 0, naluStart.length);
            //nalu起始标志
            if ((naluStart[0] & 0xFF) == 0x00 && (naluStart[1] & 0xFF) == 0x00 && (naluStart[2] & 0xFF) == 0x00 && (naluStart[3] & 0xFF) == 0x01) {
                if (bufferSize != 0) {
                    //获取nalu类型
                    int naluType = buffer.getByte(4) & 0x1F;
                    //如果是sps pps不需要变化pts
                    if (naluType == 7 || naluType == 8) {
                        pts = pts - time_base;
                    }
                    if (videoType == 0x1B) {
                        //推送帧数据
                        ZLM_API.mk_media_input_h264(mkMedia, buffer.share(0), bufferSize, pts, pts);
                    } else if (videoType == 0x24) {
                        //推送帧数据
                        ZLM_API.mk_media_input_h265(mkMedia, buffer.share(0), bufferSize, pts, pts);
                    }
                    bufferSize = 0;
                }
            }
            int naluLength = pesLength - pts_dts_length_info.length - pesHeaderLength;
            byte[] temp = new byte[naluLength];
            pointer.read(offset, temp, 0, naluLength);
            buffer.write(bufferSize, temp, 0, naluLength);
            bufferSize = naluLength + bufferSize;
        }
    }

    /**
     * 读取音频数据
     *
     * @param pointer
     * @param offset
     */
    private void readAudioES(Pointer pointer, int offset) {
        byte[] pesLengthByte = new byte[2];
        pointer.read(offset, pesLengthByte, 0, pesLengthByte.length);
        offset = offset + pesLengthByte.length;
        int pesLength = (pesLengthByte[0] & 0xFF) << 8 | (pesLengthByte[1] & 0xFF);
        //pes数据
        if (pesLength > 0) {
            byte[] pts_dts_length_info = new byte[3];
            pointer.read(offset, pts_dts_length_info, 0, pts_dts_length_info.length);
            offset = offset + pts_dts_length_info.length;
            int pesHeaderLength = (pts_dts_length_info[2] & 0xFF);
            //判断是否是有pts 忽略dts
            int i = (pts_dts_length_info[1] & 0xFF) >> 6;
            long pts_90000 = 0;
            if (i == 0x02 || i == 0x03) {
                byte[] pts_dts = new byte[5];
                pointer.read(offset, pts_dts, 0, pts_dts.length);
                //这里获取的是以90000为时间基的 需要转为 1/1000为基准的 但是pts还是不够平滑导致画面卡顿 所以不采用读取的pts
                pts_90000 = ((pts_dts[0] & 0x0e) << 29) | (((pts_dts[1] << 8 | pts_dts[2]) & 0xfffe) << 14) | (((pts_dts[3] << 8 | pts_dts[4]) & 0xfffe) >> 1);
                //pts = time_base + pts;
            }
            offset = offset + pesHeaderLength;
            int audioLength = pesLength - pts_dts_length_info.length - pesHeaderLength;
            byte[] bytes = G711ACodec._toPCM(pointer.getByteArray(offset, audioLength));
            Memory temp = new Memory(bytes.length);
            temp.write(0, bytes, 0, bytes.length);
            ZLM_API.mk_media_input_pcm(mkMedia, temp.share(0), bytes.length, pts_90000);
//            temp.close();
        }
    }

    /**
     * 读取psh头 psm头 psm标题 及数据
     *
     * @param pointer
     * @param offset
     * @return
     */
    private int readPSHAndPSMAndPSMT(Pointer pointer, int offset) {
        //ps头起始标志
        byte[] psHeaderStartCode = new byte[4];
        pointer.read(offset, psHeaderStartCode, 0, psHeaderStartCode.length);
        //判断是否是ps头
        if ((psHeaderStartCode[0] & 0xFF) == 0x00 && (psHeaderStartCode[1] & 0xFF) == 0x00 && (psHeaderStartCode[2] & 0xFF) == 0x01 && (psHeaderStartCode[3] & 0xFF) == 0xBA) {
            byte[] stuffingLengthByte = new byte[1];
            offset = 13;
            pointer.read(offset, stuffingLengthByte, 0, stuffingLengthByte.length);
            int stuffingLength = stuffingLengthByte[0] & 0x07;
            offset = offset + stuffingLength + 1;
            //ps头起始标志
            byte[] psSystemHeaderStartCode = new byte[4];
            pointer.read(offset, psSystemHeaderStartCode, 0, psSystemHeaderStartCode.length);
            //PS system header 系统标题
            if ((psSystemHeaderStartCode[0] & 0xFF) == 0x00 && (psSystemHeaderStartCode[1] & 0xFF) == 0x00 && (psSystemHeaderStartCode[2] & 0xFF) == 0x01 && (psSystemHeaderStartCode[3] & 0xFF) == 0xBB) {
                offset = offset + psSystemHeaderStartCode.length;
                byte[] psSystemLengthByte = new byte[1];
                //ps系统头长度
                pointer.read(offset, psSystemLengthByte, 0, psSystemLengthByte.length);
                int psSystemLength = psSystemLengthByte[0] & 0xFF;
                //跳过ps系统头
                offset = offset + psSystemLength;
                pointer.read(offset, psSystemHeaderStartCode, 0, psSystemHeaderStartCode.length);
            }
            //判断是否是psm系统头 则为IDR帧
            if ((psSystemHeaderStartCode[0] & 0xFF) == 0x00 && (psSystemHeaderStartCode[1] & 0xFF) == 0x00 && (psSystemHeaderStartCode[2] & 0xFF) == 0x01 && (psSystemHeaderStartCode[3] & 0xFF) == 0xBC) {
                offset = offset + psSystemHeaderStartCode.length;
                //psm头长度可以
                byte[] psmLengthByte = new byte[2];
                pointer.read(offset, psmLengthByte, 0, psmLengthByte.length);
                int psmLength = (psmLengthByte[0] & 0xFF) << 8 | (psmLengthByte[1] & 0xFF);
                //获取音视频类型
                if (videoType == 0 || audioType == 0) {
                    //自定义复合流描述
                    byte[] detailStreamLengthByte = new byte[2];
                    int tempOffset = offset + psmLengthByte.length + 2;
                    pointer.read(tempOffset, detailStreamLengthByte, 0, detailStreamLengthByte.length);
                    int detailStreamLength = (detailStreamLengthByte[0] & 0xFF) << 8 | (detailStreamLengthByte[1] & 0xFF);
                    tempOffset = detailStreamLength + detailStreamLengthByte.length + tempOffset + 2;
                    byte[] videoStreamTypeByte = new byte[1];
                    pointer.read(tempOffset, videoStreamTypeByte, 0, videoStreamTypeByte.length);
                    videoType = videoStreamTypeByte[0] & 0xFF;
                    tempOffset = tempOffset + videoStreamTypeByte.length + 1;
                    byte[] videoStreamDetailLengthByte = new byte[2];
                    pointer.read(tempOffset, videoStreamDetailLengthByte, 0, videoStreamDetailLengthByte.length);
                    int videoStreamDetailLength = (videoStreamDetailLengthByte[0] & 0xFF) << 8 | (videoStreamDetailLengthByte[1] & 0xFF);
                    tempOffset = tempOffset + videoStreamDetailLengthByte.length + videoStreamDetailLength;
                    byte[] audioStreamTypeByte = new byte[1];
                    pointer.read(tempOffset, audioStreamTypeByte, 0, audioStreamTypeByte.length);
                    audioType = audioStreamTypeByte[0] & 0xFF;
                }
                offset = offset + psmLengthByte.length + psmLength;
            }
        }
        return offset;
    }

    /**
     * 释放资源
     *
     * @return
     */
    public void release() {
        ZLM_API.mk_media_release(mkMedia);
//        buffer.close();
    }
}
