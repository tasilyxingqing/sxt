package com.ruoyi.isup.linux64.interfaces;

import com.sun.jna.Callback;
import com.sun.jna.Library;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;

/***
 * 报警服务 动态库
 **/
public interface HcisupAlarm extends Library {
    /***宏定义***/
    //常量
    public static final int MAX_DEVICE_ID_LEN = 256;    //设备ID长度
    public static final int NAME_LEN = 32;      //用户名长度（在HCNetSDK的头文件中也有定义）
    public static final int NET_EHOME_SERIAL_LEN = 12;  //设备序列号长度

    public static final int MAX_TIME_LEN = 32;       //时间字符串长度
    public static final int MAX_REMARK_LEN = 64;     //报警备注长度
    public static final int MAX_URL_LEN = 512;       //URL长度
    public static final int CID_DES_LEN = 32;        //CID报警描述长度
    public static final int MAX_FILE_PATH_LEN = 256;
    public static final int MAX_UUID_LEN = 64;       //最大UUID长度
    public static final int CID_DES_LEN_EX = 256;    //CID报警描述长度扩展
    public static final int MAX_PICTURE_NUM = 5;     //最大图片数量
    public static final int MAX_VIDEO_TYPE_LEN = 16;
    public static final int MAX_SUBSYSTEM_LEN = 64;  //子系统个数最大值

    // 报警事件
    public static final int EHOME_ALARM_UNKNOWN = 0;   //未知报警类型
    public static final int EHOME_ALARM = 1;   //Ehome基本报警
    public static final int EHOME_ALARM_HEATMAP_REPORT = 2;   //热度图报告
    public static final int EHOME_ALARM_FACESNAP_REPORT = 3;   //图片抓拍报告
    public static final int EHOME_ALARM_GPS = 4;   //GPS信息上传
    public static final int EHOME_ALARM_CID_REPORT = 5;   //报警主机CID告警上传
    public static final int EHOME_ALARM_NOTICE_PICURL = 6;   //图片URL上报
    public static final int EHOME_ALARM_NOTIFY_FAIL = 7;   //异步失败通知
    public static final int EHOME_ALARM_SELFDEFINE = 9;   //自定义报警上传
    public static final int EHOME_ALARM_DEVICE_NETSWITCH_REPORT = 10;    //设备网络切换上传
    public static final int EHOME_ALARM_ACS = 11;  //门禁事件上报
    public static final int EHOME_ALARM_WIRELESS_INFO = 12;  //无线网络信息上传
    public static final int EHOME_ISAPI_ALARM = 13;  //ISAPI报警上传
    public static final int EHOME_INFO_RELEASE_PRIVATE = 14;  //为了兼容信息发布产品的私有EHome协议报警（不再维护）
    public static final int EHOME_ALARM_MPDCDATA = 15;  //车载设备的客流数据
    public static final int EHOME_ALARM_QRCODE = 20;  //二维码报警上传
    public static final int EHOME_ALARM_FACETEMP = 21;  //人脸测温报警上传

    public static final int ALARM_TYPE_DEV_CHANGED_STATUS = 700;    //700-设备状态改变报警上传
    public static final int ALARM_TYPE_CHAN_CHANGED_STATUS = 701;   //701-通道状态改变报警上报
    public static final int ALARM_TYPE_HD_CHANGED_STATUS = 702;     //702-磁盘状态改变报警上报
    public static final int ALARM_TYPE_DEV_TIMING_STATUS = 703;     //703-定时上报设备状态信息
    public static final int ALARM_TYPE_CHAN_TIMING_STATUS = 704;    //704-定时上报通道状态信息
    public static final int ALARM_TYPE_HD_TIMING_STATUS = 705;      //705-定时上报磁盘状态信息
    public static final int ALARM_TYPE_RECORD_ABNORMAL = 706;       //706-录像异常，当前时间点本来应该是在执行录像的，结果因为异常原因导致设备无法正常录像。
    public static final int ALARM_TYPE_HIGH_TEMP = 40961;  //温度过高报警
    //设置报警管理服务器（AMS）的初始化参数。
    boolean NET_EALARM_SetSDKInitCfg(int enumType, Pointer lpInBuff);

    //初始化报警管理服务器（AMS）的报警库。
    boolean NET_EALARM_Init();

    //释放报警管理服务器（AMS）占用的资源。
    boolean NET_EALARM_Fini();

    //设置报警管理服务器（AMS）的本地配置参数。
    boolean NET_EALARM_SetSDKLocalCfg(int enumType, Pointer lpInbuffer);

    //设置参数以启用报警管理服务器（AMS）的日志功能。
    boolean NET_EALARM_SetLogToFile(int iLogLevel, String strLogDir, boolean bAutoDel);

    // 启用报警管理服务器（AMS）监听并注册回调函数以接收报警信息。
    int NET_EALARM_StartListen(NET_EHOME_ALARM_LISTEN_PARAM pAlarmListenParam);

    // 如果调用失败或完成，可获取错误码。
    int NET_EALARM_GetLastError();

    //停止报警管理服务器（AMS）的监听。
    boolean NET_EALARM_StopListen(int iListenHandle);

    //为设备设置会话密钥。
    boolean NET_EALARM_SetDeviceSessionKey(Pointer pDeviceKey);

    public static class NET_EHOME_ALARM_MSG extends Structure {
        public int dwAlarmType;      //报警类型，见EN_ALARM_TYPE
        public Pointer pAlarmInfo;       //报警内容（结构体）
        public int dwAlarmInfoLen;   //结构体报警内容长度
        public Pointer pXmlBuf;          //报警内容（XML）
        public int dwXmlBufLen;      //xml报警内容长度
        public byte[] sSerialNumber = new byte[NET_EHOME_SERIAL_LEN]; //设备序列号，用于进行Token认证
        public Pointer pHttpUrl;
        public int dwHttpUrlLen;
        public byte[] byRes = new byte[12];

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("dwAlarmType", "pAlarmInfo", "dwAlarmInfoLen", "pXmlBuf", "dwXmlBufLen",
                    "sSerialNumber", "pHttpUrl", "dwHttpUrlLen", "byRes");
        }
    }

    public static interface EHomeMsgCallBackLinux64 extends Callback {
        public boolean invoke(int iHandle, NET_EHOME_ALARM_MSG pAlarmMsg, Pointer pUser);
    }

    public static class NET_EHOME_ALARM_LISTEN_PARAM extends Structure {
        public NET_EHOME_IPADDRESS struAddress;
        public EHomeMsgCallBackLinux64 fnMsgCb; //报警信息回调函数
        public Pointer pUserData;   //用户数据
        public byte byProtocolType;    //协议类型，0-TCP,1-UDP
        public byte byUseCmsPort; //是否复用CMS端口,0-不复用，非0-复用，如果复用cms端口，协议类型字段无效（此时本地监听信息struAddress填本地回环地址）
        public byte byUseThreadPool;  //0-回调报警时，使用线程池，1-回调报警时，不使用线程池，默认情况下，报警回调的时候，使用线程池
        public byte[] byRes = new byte[29];

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("struAddress", "fnMsgCb", "pUserData", "byProtocolType",
                    "byUseCmsPort", "byUseThreadPool", "byRes");
        }
    }

    public static class NET_EHOME_IPADDRESS extends Structure {
        public byte[] szIP = new byte[128];
        public short wPort;     //端口
        public byte[] byRes = new byte[2];

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("szIP", "wPort", "byRes");
        }
    }

    public static class BYTE_ARRAY extends Structure {
        public byte[] byValue;

        public BYTE_ARRAY(int iLen) {
            byValue = new byte[iLen];
        }

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("byValue");
        }
    }
}
