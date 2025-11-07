package com.ruoyi.isup.linux64.interfaces;

import com.sun.jna.Callback;
import com.sun.jna.Library;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Union;
import com.sun.jna.ptr.IntByReference;

import java.util.Arrays;
import java.util.List;

/***
 * 存储服务 动态库
 **/
public interface HcisupSs extends Library {

    /***宏定义***/
    public static final int MAX_URL_LEN_SS = 4096; //图片服务器回调URL长度
    public static final int MAX_KMS_USER_LEN = 512; //KMS用户名最大长度
    public static final int MAX_KMS_PWD_LEN = 512; //KMS密码最大长度
    public static final int MAX_CLOUD_AK_SK_LEN = 64; //EHome5.0存储协议AK SK最大长度
    public static final int MAX_PATH = 260; //设备ID长度

    // 设置存储服务器（SS）初始化参数。
    boolean NET_ESS_SetSDKInitCfg(int enumType, Pointer lpInBuff);

    //初始化
    boolean NET_ESS_Init();

    // 获取错误码
    int NET_ESS_GetLastError();

    // 设置启用存储服务器（SS）日志功能的参数。
    boolean NET_ESS_SetLogToFile(int iLogLevel, String strLogDir, boolean bAutoDel);

    //开启监听
    int NET_ESS_StartListen(NET_EHOME_SS_LISTEN_PARAM pSSListenParam);

    //释放存储服务器（SS）占用的资源。
    boolean NET_ESS_Fini();

    // 关闭监听
    boolean NET_ESS_StopListen(int lListenHandle);

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

    /**
     * 监听参数
     */
    public static class NET_EHOME_SS_LISTEN_PARAM extends Structure {
        public NET_EHOME_IPADDRESS struAddress = new NET_EHOME_IPADDRESS(); //本地监听信息
        public byte[] szKMS_UserName = new byte[MAX_KMS_USER_LEN]; //KMS用户名
        public byte[] szKMS_Password = new byte[MAX_KMS_PWD_LEN]; //KMS密码
        public EHomeSSStorageCallBackLinux64 fnSStorageCb; //存储回调
        public EHomeSSMsgCallBackLinux64 fnSSMsgCb; //Tomcat回调
        public byte[] szAccessKey = new byte[MAX_CLOUD_AK_SK_LEN]; //AK
        public byte[] szSecretKey = new byte[MAX_CLOUD_AK_SK_LEN]; //SK
        public Pointer pUserData; //用户参数
        public byte byHttps; //是否启用HTTPS
        public byte[] byRes1 = new byte[3];
        public EHomeSSRWCallBack fnSSRWCb; //读写回调
        public EHomeSSRWCallBackEx fnSSRWCbEx;
        public byte bySecurityMode;
        public byte[] byRes = new byte[51];

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("struAddress", "szKMS_UserName", "szKMS_Password", "fnSStorageCb",
                    "fnSSMsgCb", "szAccessKey", "szSecretKey", "pUserData", "byHttps", "byRes1",
                    "fnSSRWCb", "fnSSRWCbEx", "bySecurityMode", "byRes");
        }
    }

    //======================回调开始====================

    /**
     * 信息回调函数
     */
    public static interface EHomeSSMsgCallBackLinux64 extends Callback {
        public boolean invoke(int iHandle, int enumType, Pointer pOutBuffer, int dwOutLen, Pointer pInBuffer,
                              int dwInLen, Pointer pUser);
    }

    /**
     * 存储回调函数
     */
    public static interface EHomeSSStorageCallBackLinux64 extends Callback {
        public boolean invoke(int iHandle, String pFileName, Pointer pFileBuf, int dwOutLen, Pointer pFilePath,
                              Pointer pUser);
    }

    /**
     * 读写回调函数 byAct 0-读 1-写 2-删
     */
    public static interface EHomeSSRWCallBack extends Callback {
        public boolean invoke(int iHandle, byte byAct, String pFileName, Pointer pFileBuf, int dwFileLen, String pFileUrl,
                              Pointer pUser);
    }

    public static interface EHomeSSRWCallBackEx extends Callback {
        public boolean invoke(int iHandle, NET_EHOME_SS_RW_PARAM pRwParam, NET_EHOME_SS_EX_PARAM pExStruct);
    }

    //======================回调结束====================

    /**
     * Tomcat图片服务器回调信息
     */
    public static class NET_EHOME_SS_TOMCAT_MSG extends Structure {
        public byte[] szDevUri = new byte[MAX_URL_LEN_SS]; //设备请求的URI
        public int dwPicNum; //图片数
        public String pPicURLs; //图片URL
        public byte[] byRes = new byte[64];

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("szDevUri", "dwPicNum", "pPicURLs", "byRes");
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

    public static class NET_EHOME_SS_RW_PARAM extends Structure {
        public Pointer pFileName;   //文件名
        public Pointer pFileBuf;          //文件内容
        public IntByReference dwFileLen;   //文件大小
        public Pointer pFileUrl;    //文件url
        public Pointer pUser;             //
        public byte byAct; //读写操作：0-写文件，1-读文件
        public byte byUseRetIndex;  //是否使用上层返回的pRetIndex：0-不使用，1-使用
        public byte[] byRes1 = new byte[2];
        public Pointer pRetIndex;     //上层设置的索引
        public byte[] byRes = new byte[56];

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("pFileName", "pFileBuf", "dwFileLen", "pFileUrl", "pUser",
                    "byAct", "byUseRetIndex", "byRes1", "pRetIndex", "byRes");
        }
    }

    public static class NET_EHOME_SS_EX_PARAM extends Structure {
        public byte byProtoType;
        public byte[] byRes = new byte[23];
        public NET_EHOME_SS_Union unionStoreInfo = new NET_EHOME_SS_Union();

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("byProtoType", "byRes", "unionStoreInfo");
        }
    }

    public static class NET_EHOME_SS_Union extends Union {
        public NET_EHOME_SS_CLOUD_PARAM struCloud = new NET_EHOME_SS_CLOUD_PARAM();

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("struCloud");
        }
    }

    public static class NET_EHOME_SS_CLOUD_PARAM extends Structure {
        public String pPoolId;
        public byte byPoolIdLength;
        public int dwErrorCode;
        public byte[] byRes = new byte[503];

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("pPoolId", "byPoolIdLength", "dwErrorCode", "byRes");
        }
    }
}
