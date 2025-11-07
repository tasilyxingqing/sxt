package com.ruoyi.isup.linux64.interfaces;

import com.sun.jna.Callback;
import com.sun.jna.Library;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;

/***
 * 注册服务 动态库
 **/
public interface HcisupCms extends Library {
    public static final int MAX_NAMELEN = 16;    //DVR本地登陆名
    public static final int MAX_RIGHT = 32;    //设备支持的权限（1-12表示本地权限，13-32表示远程权限）
    public static final int NAME_LEN = 32;    //用户名长度
    public static final int PASSWD_LEN = 16;    //密码长度
    public static final int MAX_MASTER_KEY_LEN = 16;
    public static final int MAX_DEVICE_ID_LEN = 256;    //设备ID长度
    public static final int MAX_DEVNAME_LEN = 32; //设备名最大长度
    public static final int MAX_DEVNAME_LEN_EX = 64; //设备名最大长度

    public static final int MAX_FULL_SERIAL_NUM_LEN = 64;      //最大完整序列号长度
    public static final int NET_EHOME_SERIAL_LEN = 12;  //序列号长度

    public static final int MAX_ANALOG_CHANNUM = 32;
    public static final int MAX_DIGIT_CHANNUM = 480;
    public static final int MAX_ANALOG_ALARMOUT = 32;

    //初始化注册模块库。返回TRUE表示成功，返回FALSE表示失败。
    boolean NET_ECMS_Init();

    // 反初始化注册库并释放中央管理服务器(CMS)占用的资源。  返回TRUE表示成功，返回FALSE表示失败。
    boolean NET_ECMS_Fini();

    // 设置中心管理服务器（CMS）初始化参数。
    boolean NET_ECMS_SetSDKInitCfg(int enumType, Pointer lpInBuff);

    // 设置中心管理服务器（CMS）的本地配置参数。
    boolean NET_ECMS_SetSDKLocalCfg(int enumType, Pointer lpInBuff);

    // 设置启用中心管理服务器（CMS）日志功能的参数。
    boolean NET_ECMS_SetLogToFile(int iLogLevel, String strLogDir, boolean bAutoDel);

    // 设置支持5.0版本ISUP的设备会话密钥。
    boolean NET_ECMS_SetDeviceSessionKey(Pointer pDeviceKey);

    //开启监听
    int NET_ECMS_StartListen(NET_EHOME_CMS_LISTEN_PARAM lpCMSListenPara);

    //获取错误码
    int NET_ECMS_GetLastError();

    //停止中心管理服务器（CMS）的监听。
    boolean NET_ECMS_StopListen(int iHandle);

    //传输命令，包括请求URL和操作方法（如GET、PUT、POST、DELETE）。
    boolean NET_ECMS_ISAPIPassThrough(int lUserID, NET_EHOME_PTXML_PARAM lpParam);


    //注册回调函数 linux
    public static interface DEVICE_REGISTER_CB_Linux64 extends Callback {
        public boolean invoke(int lUserID, int dwDataType, Pointer pOutBuffer, int dwOutLen, Pointer pInBuffer, int dwInLen, Pointer pUser);
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

    public static class NET_EHOME_CMS_LISTEN_PARAM extends Structure {
        public NET_EHOME_IPADDRESS struAddress;  //本地监听信息，IP为0.0.0.0的情况下，默认为本地地址，多个网卡的情况下，默认为从操作系统获取到的第一个
        public DEVICE_REGISTER_CB_Linux64 fnCB; //报警信息回调函数
        public Pointer pUserData;   //用户数据
        public int dwKeepAliveSec; //心跳间隔（单位：秒,0:默认为30S）
        public int dwTimeOutCount; //心跳超时次数（0：默认为3）
        public byte[] byRes = new byte[24];

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("struAddress", "fnCB", "pUserData", "dwKeepAliveSec", "dwTimeOutCount", "byRes");
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

    public static class NET_EHOME_PTXML_PARAM extends Structure {
        public Pointer pRequestUrl;        //请求URL
        public int dwRequestUrlLen;    //请求URL长度
        public Pointer pCondBuffer;        //条件缓冲区（XML格式数据）
        public int dwCondSize;         //条件缓冲区大小
        public Pointer pInBuffer;          //输入缓冲区（XML格式数据）
        public int dwInSize;           //输入缓冲区大小
        public Pointer pOutBuffer;         //输出缓冲区（XML格式数据）
        public int dwOutSize;          //输出缓冲区大小
        public int dwReturnedXMLLen;   //实际从设备接收到的XML数据的长度
        public int dwRecvTimeOut;      //默认5000ms
        public int dwHandle;           //（输出参数）设置了回放异步回调之后，该值为消息句柄，回调中用于标识（新增）
        public byte[] byRes = new byte[24];          //保留

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("pRequestUrl", "dwRequestUrlLen", "pCondBuffer", "dwCondSize",
                    "pInBuffer", "dwInSize", "pOutBuffer", "dwOutSize", "dwReturnedXMLLen",
                    "dwRecvTimeOut", "dwHandle", "byRes");
        }
    }

    public static class NET_EHOME_DEV_REG_INFO_V12 extends Structure {
        public NET_EHOME_DEV_REG_INFO struRegInfo;
        public NET_EHOME_IPADDRESS struRegAddr;
        public byte[] sDevName = new byte[MAX_DEVNAME_LEN_EX];
        public byte[] byDeviceFullSerial = new byte[MAX_FULL_SERIAL_NUM_LEN];
        public byte[] byRes = new byte[128];

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("struRegInfo", "struRegAddr", "sDevName", "byDeviceFullSerial", "byRes");
        }
    }

    public static class NET_EHOME_SERVER_INFO_V50 extends Structure {
        public int dwSize;
        public int dwKeepAliveSec;         //心跳间隔（单位：秒,0:默认为15S）
        public int dwTimeOutCount;         //心跳超时次数（0：默认为6）
        public NET_EHOME_IPADDRESS struTCPAlarmSever = new NET_EHOME_IPADDRESS();      //报警服务器地址（TCP协议）
        public NET_EHOME_IPADDRESS struUDPAlarmSever = new NET_EHOME_IPADDRESS();      //报警服务器地址（UDP协议）
        public int dwAlarmServerType;      //报警服务器类型0-只支持UDP协议上报，1-支持UDP、TCP两种协议上报
        public NET_EHOME_IPADDRESS struNTPSever = new NET_EHOME_IPADDRESS();           //NTP服务器地址
        public int dwNTPInterval;          //NTP校时间隔（单位：秒）
        public NET_EHOME_IPADDRESS struPictureSever = new NET_EHOME_IPADDRESS();       //图片服务器地址
        public int dwPicServerType;        //图片服务器类型图片服务器类型，1-VRB图片服务器，0-Tomcat图片服务,2-云存储3,3-KMS
        public NET_EHOME_BLACKLIST_SEVER struBlackListServer = new NET_EHOME_BLACKLIST_SEVER();//黑名单服务器
        public NET_EHOME_IPADDRESS struRedirectSever = new NET_EHOME_IPADDRESS();      //Redirect Server
        public byte[] byClouldAccessKey = new byte[64];  //云存储AK
        public byte[] byClouldSecretKey = new byte[64];  //云存储SK
        public byte byClouldHttps;          //云存储HTTPS使能 1-HTTPS 0-HTTP
        public byte[] byRes1 = new byte[3];
        public int dwAlarmKeepAliveSec;    //报警心跳间隔（单位：秒,0:默认为30s）
        public int dwAlarmTimeOutCount;    //报警心跳超时次数（0：默认为3）
        public int dwClouldPoolId;         //云存储PoolId
        public byte[] byRes = new byte[368];

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("dwSize", "dwKeepAliveSec", "dwTimeOutCount", "struTCPAlarmSever",
                    "struUDPAlarmSever", "dwAlarmServerType", "struNTPSever", "dwNTPInterval",
                    "struPictureSever", "dwPicServerType", "struBlackListServer", "struRedirectSever",
                    "byClouldAccessKey", "byClouldSecretKey", "byClouldHttps", "byRes1",
                    "dwAlarmKeepAliveSec", "dwAlarmTimeOutCount", "dwClouldPoolId", "byRes");
        }
    }

    public static class NET_EHOME_BLACKLIST_SEVER extends Structure {
        public NET_EHOME_IPADDRESS struAdd = new NET_EHOME_IPADDRESS();
        public byte[] byServerName = new byte[32];
        public byte[] byUserName = new byte[32];
        public byte[] byPassWord = new byte[32];
        public byte[] byRes = new byte[64];

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("struAdd", "byServerName", "byUserName", "byPassWord", "byRes");
        }
    }

    public static interface EHOME_REGISTER_TYPE {
        public static final int ENUM_DEV_ON = 0;  //设备上线回调
        public static final int ENUM_DEV_OFF = 1; //设备下线回调
        public static final int ENUM_DEV_ADDRESS_CHANGED = 2;  //设备地址发生变化
        public static final int ENUM_DEV_AUTH = 3;  //Ehome5.0设备认证回调
        public static final int ENUM_DEV_SESSIONKEY = 4;  //Ehome5.0设备Sessionkey回调
        public static final int ENUM_DEV_DAS_REQ = 5; //Ehome5.0设备重定向请求回调
        public static final int ENUM_DEV_SESSIONKEY_REQ = 6;  //EHome5.0设备sessionkey请求回调
        public static final int ENUM_DEV_DAS_REREGISTER = 7;  //设备重注册回调
        public static final int ENUM_DEV_DAS_PINGREO = 8; //设备注册心跳
        public static final int ENUM_DEV_DAS_EHOMEKEY_ERROR = 9; //校验密码失败
        public static final int ENUM_DEV_SESSIONKEY_ERROR = 10;  //Sessionkey交互异常
        public static final int ENUM_DEV_SLEEP = 11; //设备进入休眠状态（注：休眠状态下，设备无法做预览、回放、语音对讲、配置等CMS中的信令作响应；设备可通过NET_ECMS_WakeUp接口进行唤醒）
    }

    public static class NET_EHOME_DEV_REG_INFO extends Structure {
        public int dwSize;
        public int dwNetUnitType;
        public byte[] byDeviceID = new byte[MAX_DEVICE_ID_LEN];
        public byte[] byFirmwareVersion = new byte[24];
        public NET_EHOME_IPADDRESS struDevAdd;
        public int dwDevType;
        public int dwManufacture;
        public byte[] byPassWord = new byte[32];
        public byte[] sDeviceSerial = new byte[NET_EHOME_SERIAL_LEN];
        public byte byReliableTransmission;
        public byte byWebSocketTransmission;
        public byte bySupportRedirect;               //设备支持重定向注册 0-不支持 1-支持
        public byte[] byDevProtocolVersion = new byte[6];         //设备协议版本
        public byte[] bySessionKey = new byte[16];//Ehome5.0设备SessionKey
        public byte byMarketType; //0-无效（未知类型）,1-经销型，2-行业型
        public byte[] byRes = new byte[26];

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("dwSize", "dwNetUnitType", "byDeviceID", "byFirmwareVersion",
                    "struDevAdd", "dwDevType", "dwManufacture", "byPassWord", "sDeviceSerial",
                    "byReliableTransmission", "byWebSocketTransmission", "bySupportRedirect",
                    "byDevProtocolVersion", "bySessionKey", "byMarketType", "byRes");
        }
    }

    public static class NET_EHOME_DEV_SESSIONKEY extends Structure {
        public byte[] sDeviceID = new byte[MAX_DEVICE_ID_LEN];        //设备ID/*256*/
        public byte[] sSessionKey = new byte[MAX_MASTER_KEY_LEN];     //设备Sessionkey/*16*/

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("sDeviceID", "sSessionKey");
        }
    }
}
