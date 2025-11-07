package com.ruoyi.isup.service.ssService;

import com.ruoyi.isup.common.osSelect;
import com.ruoyi.isup.config.IsupConfig;
import com.ruoyi.isup.service.cmsService.HCISUPCMS;
import com.ruoyi.isup.utils.CommonMethod;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.ByteBuffer;

@Slf4j
@Component("ssService")
public class Ss {
    public static HCISUPSS hCEhomeSS = null;
    String PSS_CLIENT_FILE_PATH_PARAM_NAME = "File-Path"; //图片文件路径
    String PSS_CLIENT_VRB_FILENAME_CODE = "Filename-Code";//VRB协议的FilenameCode
    String PSS_CLIENT_KMS_USER_NAME = "KMS-Username"; //KMS图片服务器用户名
    String PSS_CLIENT_KMS_PASSWORD = "KMS-Password"; //KMS图片服务器密码
    String PSS_CLIENT_CLOUD_AK_NAME = "Access-Key"; //云存储协议AcessKey
    String PSS_CLIENT_CLOUD_SK_NAME = "Secret-Key"; //云存储协议SecretKey
    static PSS_Message_Callback pSS_Message_Callback;// 信息回调函数(上报)
    static PSS_Storage_Callback pSS_Storage_Callback;// 文件保存回调函数(下载)
    static cbEHomeSSRWCallBackEx fEHomeSSRWCallBackEx; //读写扩展回调函数
    static HCISUPSS.NET_EHOME_SS_LISTEN_PARAM pSSListenParam = new HCISUPSS.NET_EHOME_SS_LISTEN_PARAM();
    public static int SsHandle = -1; //存储服务监听句柄
    int client = -1;
    static int iCount = 0;
    byte[] szUrl = new byte[HCISUPSS.MAX_URL_LEN_SS];
    String url = "";

    @Autowired
    private IsupConfig isupConfig;

    /**
     * 根据不同操作系统选择不同的库文件和库路径
     *
     * @return
     */
    private static boolean CreateSDKInstance() {
        if (hCEhomeSS == null) {
            synchronized (HCISUPSS.class) {
                String strDllPath = "";
                try {
                    //System.setProperty("jna.debug_load", "true");
                    if (osSelect.isWindows())
                        //win系统加载库路径(路径不要带中文)
                        strDllPath = System.getProperty("user.dir") + "\\ruoyi-isup\\win-lib\\HCISUPSS.dll";
                    else if (osSelect.isLinux())
                        //Linux系统加载库路径(路径不要带中文)
                        strDllPath = System.getProperty("user.dir") + "/ruoyi-isup/linux-lib/libHCISUPSS.so";
                    hCEhomeSS = (HCISUPSS) Native.loadLibrary(strDllPath, HCISUPSS.class);
                } catch (Exception ex) {
                    System.out.println("loadLibrary: " + strDllPath + " Error: " + ex.getMessage());
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * SS服务初始化，开启监听
     *
     * @throws UnsupportedEncodingException
     */
    public void eSS_Init() throws UnsupportedEncodingException {
        if (hCEhomeSS == null) {
            if (!CreateSDKInstance()) {
                System.out.println("Load SS SDK fail");
                return;
            }
        }
        if (osSelect.isWindows()) {
            String strPathCrypto = System.getProperty("user.dir") + "\\ruoyi-isup\\win-lib\\libeay32.dll"; //Linux版本是libcrypto.so库文件的路径
            int iPathCryptoLen = strPathCrypto.getBytes().length;
            HCISUPCMS.BYTE_ARRAY ptrByteArrayCrypto = new HCISUPCMS.BYTE_ARRAY(iPathCryptoLen + 1);
            System.arraycopy(strPathCrypto.getBytes(), 0, ptrByteArrayCrypto.byValue, 0, iPathCryptoLen);
            ptrByteArrayCrypto.write();
            System.out.println(new String(ptrByteArrayCrypto.byValue));
            hCEhomeSS.NET_ESS_SetSDKInitCfg(4, ptrByteArrayCrypto.getPointer());

            //设置libssl.so所在路径
            String strPathSsl = System.getProperty("user.dir") + "\\ruoyi-isup\\win-lib\\ssleay32.dll";    //Linux版本是libssl.so库文件的路径
            int iPathSslLen = strPathSsl.getBytes().length;
            HCISUPCMS.BYTE_ARRAY ptrByteArraySsl = new HCISUPCMS.BYTE_ARRAY(iPathSslLen + 1);
            System.arraycopy(strPathSsl.getBytes(), 0, ptrByteArraySsl.byValue, 0, iPathSslLen);
            ptrByteArraySsl.write();
            System.out.println(new String(ptrByteArraySsl.byValue));
            hCEhomeSS.NET_ESS_SetSDKInitCfg(5, ptrByteArraySsl.getPointer());

            //设置sqlite3库的路径
            String strPathSqlite = System.getProperty("user.dir") + "\\ruoyi-isup\\win-lib\\sqlite3.dll";
            int iPathSqliteLen = strPathSqlite.getBytes().length;
            HCISUPCMS.BYTE_ARRAY ptrByteArraySqlite = new HCISUPCMS.BYTE_ARRAY(iPathSqliteLen + 1);
            System.arraycopy(strPathSqlite.getBytes(), 0, ptrByteArraySqlite.byValue, 0, iPathSqliteLen);
            ptrByteArraySqlite.write();
            System.out.println(new String(ptrByteArraySqlite.byValue));
            hCEhomeSS.NET_ESS_SetSDKInitCfg(6, ptrByteArraySqlite.getPointer());
            //SDK初始化
            boolean sinit = hCEhomeSS.NET_ESS_Init();
            if (!sinit) {
                System.out.println("NET_ESS_Init失败，错误码：" + hCEhomeSS.NET_ESS_GetLastError());
            }else {
                System.out.println("SS服务初始化成功");
            }
            //设置图片存储服务器公网地址 （当存在内外网映射时使用
            HCISUPCMS.NET_EHOME_IPADDRESS ipaddress = new HCISUPCMS.NET_EHOME_IPADDRESS();
            String ServerIP = isupConfig.getIp();
            System.arraycopy(ServerIP.getBytes(), 0, ipaddress.szIP, 0, ServerIP.length());
            ipaddress.wPort = Short.parseShort(String.valueOf(isupConfig.getSsServicePort()));
            ipaddress.write();
            boolean b = hCEhomeSS.NET_ESS_SetSDKInitCfg(3, ipaddress.getPointer());
            if (!b) {
                System.out.println("NET_ESS_SetSDKInitCfg失败，错误码：" + hCEhomeSS.NET_ESS_GetLastError());
            }
        } else if (osSelect.isLinux()) {
            HCISUPCMS.BYTE_ARRAY ptrByteArrayCrypto = new HCISUPCMS.BYTE_ARRAY(256);
            String strPathCrypto = System.getProperty("user.dir") + "/ruoyi-isup/linux-lib/libcrypto.so"; //Linux版本是libcrypto.so库文件的路径
            System.out.println(strPathCrypto);
            System.arraycopy(strPathCrypto.getBytes(), 0, ptrByteArrayCrypto.byValue, 0, strPathCrypto.length());
            ptrByteArrayCrypto.write();
            hCEhomeSS.NET_ESS_SetSDKInitCfg(4, ptrByteArrayCrypto.getPointer());

            //设置libssl.so所在路径
            HCISUPCMS.BYTE_ARRAY ptrByteArraySsl = new HCISUPCMS.BYTE_ARRAY(256);
            String strPathSsl = System.getProperty("user.dir") + "/ruoyi-isup/linux-lib/libssl.so";    //Linux版本是libssl.so库文件的路径
            System.out.println(strPathSsl);
            System.arraycopy(strPathSsl.getBytes(), 0, ptrByteArraySsl.byValue, 0, strPathSsl.length());
            ptrByteArraySsl.write();
            hCEhomeSS.NET_ESS_SetSDKInitCfg(5, ptrByteArraySsl.getPointer());

            //设置splite3.so所在路径
            HCISUPCMS.BYTE_ARRAY ptrByteArraysplite = new HCISUPCMS.BYTE_ARRAY(256);
            String strPathsplite = System.getProperty("user.dir") + "/ruoyi-isup/linux-lib/libsqlite3.so";    //Linux版本是libsqlite3.so库文件的路径
            System.out.println(strPathsplite);
            System.arraycopy(strPathsplite.getBytes(), 0, ptrByteArraysplite.byValue, 0, strPathsplite.length());
            ptrByteArraysplite.write();
            hCEhomeSS.NET_ESS_SetSDKInitCfg(6, ptrByteArraysplite.getPointer());
            //SDK初始化
            boolean sinit = hCEhomeSS.NET_ESS_Init();
            if (!sinit) {
                System.out.println("NET_ESS_Init失败，错误码：" + hCEhomeSS.NET_ESS_GetLastError());
            }else {
                System.out.println("SS服务初始化成功");
            }
            //设置图片存储服务器公网地址 （当存在内外网映射时使用
            HCISUPCMS.NET_EHOME_IPADDRESS ipaddress = new HCISUPCMS.NET_EHOME_IPADDRESS();
            String ServerIP = isupConfig.getIp();
            System.arraycopy(ServerIP.getBytes(), 0, ipaddress.szIP, 0, ServerIP.length());
            ipaddress.wPort = Short.parseShort(String.valueOf(isupConfig.getSsServicePort()));
            ipaddress.write();
            boolean b = hCEhomeSS.NET_ESS_SetSDKInitCfg(3, ipaddress.getPointer());
            if (!b) {
                System.out.println("NET_ESS_SetSDKInitCfg失败，错误码：" + hCEhomeSS.NET_ESS_GetLastError());
            }
        }
    }

    /**
     * 开启存储服务监听
     */
    public void startSsListen() {
        System.out.println("开启存储服务监听");

        // 从配置中获取存储服务监听的IP地址
        String SSIP = isupConfig.getIp();

        // 将获取的IP地址转换为字节数组，并复制到pSSListenParam的struAddress.szIP中
        // 从SSIP的字节数组起始位置开始复制，复制到struAddress.szIP的起始位置，复制长度为SSIP的长度
        System.arraycopy(SSIP.getBytes(), 0, pSSListenParam.struAddress.szIP, 0, SSIP.length());

        // 从配置中获取存储服务的端口号，并将其转换为short类型，赋值给pSSListenParam的struAddress.wPort
        pSSListenParam.struAddress.wPort = Short.parseShort(String.valueOf(isupConfig.getSsServicePort()));

        // 定义KMS用户名为"test"
        String strKMS_UserName = "test";

        // 将KMS用户名转换为字节数组，并复制到pSSListenParam的szKMS_UserName中
        // 从strKMS_UserName的字节数组起始位置开始复制，复制到szKMS_UserName的起始位置，复制长度为strKMS_UserName的长度
        System.arraycopy(strKMS_UserName.getBytes(), 0, pSSListenParam.szKMS_UserName, 0, strKMS_UserName.length());

        // 定义KMS密码为"12345"
        String strKMS_Password = "12345";

        // 将KMS密码转换为字节数组，并复制到pSSListenParam的szKMS_Password中
        // 从strKMS_Password的字节数组起始位置开始复制，复制到szKMS_Password的起始位置，复制长度为strKMS_Password的长度
        System.arraycopy(strKMS_Password.getBytes(), 0, pSSListenParam.szKMS_Password, 0, strKMS_Password.length());

        // 定义访问密钥为"test"
        String strAccessKey = "test";

        // 将访问密钥转换为字节数组，并复制到pSSListenParam的szAccessKey中
        // 从strAccessKey的字节数组起始位置开始复制，复制到szAccessKey的起始位置，复制长度为strAccessKey的长度
        System.arraycopy(strAccessKey.getBytes(), 0, pSSListenParam.szAccessKey, 0, strAccessKey.length());

        // 定义安全密钥为"12345"
        String strSecretKey = "12345";

        // 将安全密钥转换为字节数组，并复制到pSSListenParam的szSecretKey中
        // 从strSecretKey的字节数组起始位置开始复制，复制到szSecretKey的起始位置，复制长度为strSecretKey的长度
        System.arraycopy(strSecretKey.getBytes(), 0, pSSListenParam.szSecretKey, 0, strSecretKey.length());

        // 设置pSSListenParam的byHttps为0，表示不使用HTTPS
        pSSListenParam.byHttps = 0;

        /******************************************************************
         * 存储信息回调
         */
        if (pSS_Message_Callback == null) {
            pSS_Message_Callback = new PSS_Message_Callback();
        }
        pSSListenParam.fnSSMsgCb = pSS_Message_Callback;

        /******************************************************************
         * 存储数据回调
         * fnSStorageCb或者fnSSRWCbEx，只需要设置一种回调函数
         * 简单功能测试可以使用存储回调(SDK底层使用db数据库自动存取数据，因此会受到db数据库的性能瓶颈影响)
         * 需要自定义URL或者自己读写图片数据，则使用读写扩展回调(推荐)
         */
        //存储信息回调
        if (pSS_Storage_Callback == null) {
            pSS_Storage_Callback = new PSS_Storage_Callback();
        }
        pSSListenParam.fnSStorageCb = pSS_Storage_Callback;
        //读写扩展回调
//        if (fEHomeSSRWCallBackEx == null) {
//            fEHomeSSRWCallBackEx = new cbEHomeSSRWCallBackEx();
//        }
//        pSSListenParam.fnSSRWCbEx = fEHomeSSRWCallBackEx;
        pSSListenParam.bySecurityMode = 1;
        pSSListenParam.write();
        SsHandle = hCEhomeSS.NET_ESS_StartListen(pSSListenParam);
        if (SsHandle == -1) {
            int err = hCEhomeSS.NET_ESS_GetLastError();
            System.out.println("NET_ESS_StartListen failed,error:" + err);
            hCEhomeSS.NET_ESS_Fini();
            return;
        } else {
            String SsListenInfo = new String(pSSListenParam.struAddress.szIP).trim() + "_" + pSSListenParam.struAddress.wPort;
            System.out.println("存储服务器：" + SsListenInfo + ",NET_ESS_StartListen succeed!\n");
        }
    }

    /**
     * 创建存储客户端
     */
    public void ssCreateClient(String filePath) {
        HCISUPSS.NET_EHOME_SS_CLIENT_PARAM pClientParam = new HCISUPSS.NET_EHOME_SS_CLIENT_PARAM();
        //存储服务器类型
        switch (Integer.parseInt(isupConfig.getPicServerType())) {
            case 1:
                pClientParam.enumType = HCISUPSS.NET_EHOME_SS_CLIENT_TYPE_VRB;
                break;
            case 2:
                pClientParam.enumType = HCISUPSS.NET_EHOME_SS_CLIENT_TYPE_CLOUD;
                break;
            case 3:
                pClientParam.enumType = HCISUPSS.NET_EHOME_SS_CLIENT_TYPE_KMS;
                break;
            default:
                System.out.println("存储服务器类型选择错误");
                break;
        }
        pClientParam.struAddress.szIP = isupConfig.getIp().getBytes();
        pClientParam.struAddress.wPort = Short.parseShort(isupConfig.getPicServerPort());
        pClientParam.byHttps = 0;
        pClientParam.write();

        client = hCEhomeSS.NET_ESS_CreateClient(pClientParam);
        if (client < 0) {
            int err = hCEhomeSS.NET_ESS_GetLastError();
            System.out.println("创建图片上传/下载客户端出错,错误号：" + err + "  ,client=" + client);
            return;
        } else {
            if (!hCEhomeSS.NET_ESS_ClientSetTimeout(client, 6000, 6000)) {
                int err = hCEhomeSS.NET_ESS_GetLastError();
                System.out.println("NET_ESS_ClientSetTimeout失败,错误号：" + err);
            }
            boolean bSetParam = hCEhomeSS.NET_ESS_ClientSetParam(client, PSS_CLIENT_FILE_PATH_PARAM_NAME, filePath);
            boolean bKMS_UserName = hCEhomeSS.NET_ESS_ClientSetParam(client, PSS_CLIENT_KMS_USER_NAME, "test");
            boolean bKMS_PassWord = hCEhomeSS.NET_ESS_ClientSetParam(client, PSS_CLIENT_KMS_PASSWORD, "12345");
            boolean bCloud_AK = hCEhomeSS.NET_ESS_ClientSetParam(client, PSS_CLIENT_CLOUD_AK_NAME, "test");
            boolean bCloud_SK = hCEhomeSS.NET_ESS_ClientSetParam(client, PSS_CLIENT_CLOUD_SK_NAME, "12345");
            System.out.println("ssCreateClient: bSetParam:  " + bSetParam +
                    "  bKMS_UserName" + bKMS_UserName +
                    "  bKMS_PassWord" + bKMS_PassWord +
                    "  bCloud_AK" + bCloud_AK +
                    "  bCloud_SK" + bCloud_SK);
        }
    }

    /**
     * 上传图片到存储服务，返回URL
     */
    public void ssUploadPic() {
        iCount++;
        szUrl = new byte[HCISUPSS.MAX_URL_LEN_SS];

        boolean doUpload = hCEhomeSS.NET_ESS_ClientDoUpload(client, szUrl, HCISUPSS.MAX_URL_LEN_SS - 1);
        if (!doUpload) {
            int err = hCEhomeSS.NET_ESS_GetLastError();
            System.out.println("NET_ESS_ClientDoUpload失败，错误号：" + err);
        } else {
            url = "http://" + isupConfig.getIp() + ":" + isupConfig.getPicServerPort() + new String(szUrl).trim();
            System.out.println("NET_ESS_ClientDoUpload succeed, Count: " + iCount + ",Pic Url: " + url);
            return;
        }
    }

    /**
     * 销毁存储客户端
     */
    public void ssDestroyClient() {
        if (hCEhomeSS.NET_ESS_DestroyClient(client))//释放资源
        {
            client = -1;
        }
        return;
    }

    public class PSS_Message_Callback implements HCISUPSS.EHomeSSMsgCallBack {

        public boolean invoke(int iHandle, int enumType, Pointer pOutBuffer, int dwOutLen, Pointer pInBuffer,
                              int dwInLen, Pointer pUser) {
            System.out.println("进入信息回调函数");
            if (1 == enumType) {
                HCISUPSS.NET_EHOME_SS_TOMCAT_MSG pTomcatMsg = new HCISUPSS.NET_EHOME_SS_TOMCAT_MSG();
                String szDevUri = new String(pTomcatMsg.szDevUri).trim();
                int dwPicNum = pTomcatMsg.dwPicNum;
                String pPicURLs = pTomcatMsg.pPicURLs;
                System.out.println("szDevUri = " + szDevUri + "   dwPicNum= " + dwPicNum + "   pPicURLs=" + pPicURLs);
            } else if (2 == enumType) {


            } else if (3 == enumType) {

            }
            return true;
        }
    }

    public class PSS_Storage_Callback implements HCISUPSS.EHomeSSStorageCallBack {

        public boolean invoke(int iHandle, String pFileName, Pointer pFileBuf, int dwFileLen, Pointer pFilePath, Pointer pUser) {
            System.out.println("进入存储信息回调函数");
            String strPath = System.getProperty("user.dir") + "/isup/ISUPPicServer/";
            String strFilePath = strPath + pFileName;

            //若此目录不存在，则创建之
            File myPath = new File(strPath);
            if (!myPath.exists()) {
                myPath.mkdir();
                System.out.println("创建文件夹路径为：" + strPath);
            }

            if (dwFileLen > 0 && pFileBuf != null) {
                FileOutputStream fout;
                try {
                    fout = new FileOutputStream(strFilePath);
                    //将字节写入文件
                    long offset = 0;
                    ByteBuffer buffers = pFileBuf.getByteBuffer(offset, dwFileLen);
                    byte[] bytes = new byte[dwFileLen];
                    buffers.rewind();
                    buffers.get(bytes);
                    fout.write(bytes);
                    fout.close();
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            return true;
        }
    }

    public class cbEHomeSSRWCallBackEx implements HCISUPSS.EHomeSSRWCallBackEx {
        public boolean invoke(int iHandle, HCISUPSS.NET_EHOME_SS_RW_PARAM pRwParam, HCISUPSS.NET_EHOME_SS_EX_PARAM pExStruct) {
            // 注意: 回调的处理逻辑超过5s设备会重新上报，需要保证您的读写逻辑在5s内处理完成
            System.out.println("cbEHomeSSRWCallBackEx, byAct：" + pRwParam.byAct);
            String strPath = System.getProperty("user.dir") + "/ISUPPicServer";
            // 写文件操作
            if (pRwParam.byAct == 0) {
                byte[] byFileName = new byte[256];
                System.arraycopy(pRwParam.pFileName.getByteArray(0, 256), 0, byFileName, 0, 256);
                String strFilePath = strPath + "/" + new String(byFileName).trim();

                byte[] byFileURL = new byte[256];
                System.arraycopy(pRwParam.pFileUrl.getByteArray(0, 256), 0, byFileURL, 0, 256);
                String strFileURL = new String(byFileURL).trim();
                System.out.println("文件路径为：" + strFilePath + "，URL：" + strFileURL);

                //若此目录不存在，则创建之
                File myPath = new File(strPath);
                if (!myPath.exists()) {
                    myPath.mkdir();
                    System.out.println("创建文件夹路径为：" + strPath);
                }

                if (pRwParam.dwFileLen.getValue() > 0 && pRwParam.pFileBuf != null) {
                    FileOutputStream fout;
                    try {
                        fout = new FileOutputStream(strFilePath);
                        //将字节写入文件
                        long offset = 0;
                        ByteBuffer buffers = pRwParam.pFileBuf.getByteBuffer(offset, pRwParam.dwFileLen.getValue());
                        byte[] bytes = new byte[pRwParam.dwFileLen.getValue()];
                        buffers.rewind();
                        buffers.get(bytes);
                        fout.write(bytes);
                        fout.close();
                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                //URL由平台自行生成赋值给pRwParam.pRetIndex, 示例demo写死了12345.jpg，第三方可以根据业务自己修改
                pRwParam.byUseRetIndex = 1;
                String strRetPath = "ISUPPicServer/12345.jpg"; //自定义URL,URL前面的第一个“/”不需要传入
                int iLenURL = strRetPath.getBytes().length;
                pRwParam.pRetIndex.write(0, strRetPath.getBytes(), 0, iLenURL);
                pRwParam.write();
            }

            // 读文件操作
            if (pRwParam.byAct == 1) {
                byte[] byFileURL = new byte[256];
                System.arraycopy(pRwParam.pFileUrl.getByteArray(0, 256), 0, byFileURL, 0, 256);
                String strFileURL = new String(byFileURL).trim();
                System.out.println("URL：" + strFileURL);

                try {
                    //根据返回的URL，平台自己关联返回哪张图片，Demo中写死路径演示用，实际客户根据自己需求来实现。
                    String strFilePath = System.getProperty("user.dir") + "/ISUPPicServer/12345.jpg";

                    int picdataLength = 0;
                    FileInputStream picfile = null;

                    picfile = new FileInputStream(new File(strFilePath));
                    picdataLength = picfile.available();
                    System.out.println("picdataLength:" + picdataLength);

                    if (picdataLength < 0) {
                        System.out.println("input file dataSize < 0");
                        return false;
                    }

                    if (pRwParam.pFileBuf == null) //第一次回调
                    {
                        pRwParam.dwFileLen.setValue(picdataLength);
                        System.out.println("aaa");
                    }

                    if (pRwParam.pFileBuf != null) {
                        HCISUPSS.StringPointer ptrpicByte = new HCISUPSS.StringPointer(picdataLength);
                        int dwReadCount = 0;
                        while (dwReadCount < picdataLength) {
                            dwReadCount += picfile.read(ptrpicByte.sData, dwReadCount, picdataLength - dwReadCount);
                        }
                        CommonMethod.WriteBuffToPointer(ptrpicByte.sData, pRwParam.pFileBuf);
                    }
                    pRwParam.write();
                } catch (IOException ex) {
                    //  Logger.getLogger(NewJDialogSS.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
            return true;
        }
    }
}
