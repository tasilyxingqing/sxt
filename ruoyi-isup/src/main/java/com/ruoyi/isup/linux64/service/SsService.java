package com.ruoyi.isup.linux64.service;

import com.ruoyi.isup.config.IsupLinuxConfig;
import com.ruoyi.isup.linux64.callBack.PssMessageCallback;
import com.ruoyi.isup.linux64.callBack.PssStorageCallback;
import com.ruoyi.isup.linux64.interfaces.HcisupSs;
import com.ruoyi.isup.linux64.utils.HkUtils;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/***
 * 存储服务
 **/
@Service("linux64-ss")
@Slf4j
public class SsService {
    public static HcisupSs hCEhomeSS = null;

    public static int ssHandle = -1; //存储服务监听句柄
    static HcisupSs.NET_EHOME_SS_LISTEN_PARAM pSSListenParam = new HcisupSs.NET_EHOME_SS_LISTEN_PARAM();


    @Autowired
    PssMessageCallback pssMessageCallback;

    @Autowired
    PssStorageCallback pssStorageCallback;

    @Autowired
    IsupLinuxConfig isupLinuxConfig;


    /**
     * 根据不同操作系统选择不同的库文件和库路径 目前只实现了 linux64
     */
    private static boolean createInstance() {
        if (hCEhomeSS == null) {
            synchronized (HcisupSs.class) {
                try {
                    hCEhomeSS = (HcisupSs) Native.loadLibrary(HkUtils.getSsPath(), HcisupSs.class);
                } catch (Exception ex) {
                    log.info("ss加载不同的操作库 Error: " + ex.getMessage());
                    return false;
                }
            }
        }
        return true;
    }


    public void init() {
        if (hCEhomeSS == null) {
            if (!createInstance()) {
                log.info("初始化ss服务失败");
                return;
            }
        }
        String strPathCrypto = HkUtils.getCryptoPath();
        int iPathCryptoLen = strPathCrypto.getBytes().length;
        HcisupSs.BYTE_ARRAY ptrByteArrayCrypto = new HcisupSs.BYTE_ARRAY(256);
        System.arraycopy(strPathCrypto.getBytes(), 0, ptrByteArrayCrypto.byValue, 0, iPathCryptoLen);
        ptrByteArrayCrypto.write();
        hCEhomeSS.NET_ESS_SetSDKInitCfg(4, ptrByteArrayCrypto.getPointer());

        //设置libssl.so所在路径
        String strPathSsl = HkUtils.getSslPath();
        int iPathSslLen = strPathSsl.getBytes().length;
        HcisupSs.BYTE_ARRAY ptrByteArraySsl = new HcisupSs.BYTE_ARRAY(256);
        System.arraycopy(strPathSsl.getBytes(), 0, ptrByteArraySsl.byValue, 0, iPathSslLen);
        ptrByteArraySsl.write();
        hCEhomeSS.NET_ESS_SetSDKInitCfg(5, ptrByteArraySsl.getPointer());

        //设置sqlite3库的路径
        String strPathSqlite = HkUtils.getSqlite3Path();
        int iPathSqliteLen = strPathSqlite.getBytes().length;
        HcisupSs.BYTE_ARRAY ptrByteArraySqlite = new HcisupSs.BYTE_ARRAY(256);
        System.arraycopy(strPathSqlite.getBytes(), 0, ptrByteArraySqlite.byValue, 0, iPathSqliteLen);
        ptrByteArraySqlite.write();
        hCEhomeSS.NET_ESS_SetSDKInitCfg(6, ptrByteArraySqlite.getPointer());
        //SDK初始化
        boolean sinit = hCEhomeSS.NET_ESS_Init();
        if (!sinit) {
            log.info("ss初始化失败，错误码：" + hCEhomeSS.NET_ESS_GetLastError());
        }
        //设置图片存储服务器公网地址 （当存在内外网映射时使用
        HcisupSs.NET_EHOME_IPADDRESS ipaddress = new HcisupSs.NET_EHOME_IPADDRESS();
        System.arraycopy(isupLinuxConfig.getPicServerIP().getBytes(), 0, ipaddress.szIP, 0, isupLinuxConfig.getPicServerIP().length());
        ipaddress.wPort = isupLinuxConfig.getPicServerPort();
        ipaddress.write();
        boolean b = hCEhomeSS.NET_ESS_SetSDKInitCfg(3, ipaddress.getPointer());
        if (!b) {
            log.info("ss 设置图片存储服务器公网地址失败，错误码：" + hCEhomeSS.NET_ESS_GetLastError());
        }

        //启用写日志
        hCEhomeSS.NET_ESS_SetLogToFile(3, isupLinuxConfig.getSsLogPath(), false);
    }

    /**
     * 开启存储服务监听
     */
    public void startSsListen() {
        String SSIP = isupLinuxConfig.getPicServerListenIP();
        System.arraycopy(SSIP.getBytes(), 0, pSSListenParam.struAddress.szIP, 0, SSIP.length());
        pSSListenParam.struAddress.wPort = isupLinuxConfig.getPicServerListenPort();
        String strKMS_UserName = "test";
        System.arraycopy(strKMS_UserName.getBytes(), 0, pSSListenParam.szKMS_UserName, 0, strKMS_UserName.length());
        String strKMS_Password = "12345";
        System.arraycopy(strKMS_Password.getBytes(), 0, pSSListenParam.szKMS_Password, 0, strKMS_Password.length());
        String strAccessKey = "test";
        System.arraycopy(strAccessKey.getBytes(), 0, pSSListenParam.szAccessKey, 0, strAccessKey.length());
        String strSecretKey = "12345";
        System.arraycopy(strSecretKey.getBytes(), 0, pSSListenParam.szSecretKey, 0, strSecretKey.length());
        pSSListenParam.byHttps = 0;
        /******************************************************************
         * 存储信息回调
         */
        pSSListenParam.fnSSMsgCb = pssMessageCallback;

        /******************************************************************
         * 存储数据回调
         * fnSStorageCb或者fnSSRWCbEx，只需要设置一种回调函数
         * 简单功能测试可以使用存储回调(SDK底层使用db数据库自动存取数据，因此会受到db数据库的性能瓶颈影响)
         * 需要自定义URL或者自己读写图片数据，则使用读写扩展回调(推荐)
         */
        //存储信息回调
        pSSListenParam.fnSStorageCb = pssStorageCallback;

        pSSListenParam.bySecurityMode = 1;
        pSSListenParam.write();
        ssHandle = hCEhomeSS.NET_ESS_StartListen(pSSListenParam);
        if (ssHandle < 0) {
            log.info(" 存储服务监听 失败, error:" + hCEhomeSS.NET_ESS_GetLastError());
            hCEhomeSS.NET_ESS_Fini();
            return;
        }
        String SsListenInfo = new String(pSSListenParam.struAddress.szIP).trim() + "_" + pSSListenParam.struAddress.wPort;
        log.info(" 存储服务器：" + SsListenInfo + ",ss 监听成功 ssHandle:" + ssHandle);
    }

    //存储消息回调
    public boolean pssMessageCallback(int iHandle, int enumType, Pointer pOutBuffer, int dwOutLen, Pointer pInBuffer, int dwInLen, Pointer pUser) {
        if (1 == enumType) {
            HcisupSs.NET_EHOME_SS_TOMCAT_MSG pTomcatMsg = new HcisupSs.NET_EHOME_SS_TOMCAT_MSG();
            String szDevUri = new String(pTomcatMsg.szDevUri).trim();
            int dwPicNum = pTomcatMsg.dwPicNum;
            String pPicURLs = pTomcatMsg.pPicURLs;
            log.info("szDevUri = " + szDevUri + "   dwPicNum= " + dwPicNum + "   pPicURLs=" + pPicURLs);
        } else if (2 == enumType) {


        } else if (3 == enumType) {
        }
        return true;
    }

    //存储文件回调
    public boolean pssStorageCallback(int iHandle, String pFileName, Pointer pFileBuf, int dwFileLen, Pointer pFilePath, Pointer pUser) {
        if (dwFileLen <= 0 || pFileBuf == null) {
            log.info(" 存储服务 存储回调 文件长度无效");
            return false;  // 文件为空或长度无效，直接返回
        }
        // 从Pointer中提取附件字节数据
        String strPath = System.getProperty("user.dir") + isupLinuxConfig.getSsLogPath() + "files/";
        String strFilePath = strPath + pFileName;
        log.info(" 存储服务 存储路径为：" + strFilePath);

        //若此目录不存在，则创建之
        File myPath = new File(strPath);
        if (!myPath.exists()) {
            myPath.mkdir();
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


        pFilePath.write(0, strFilePath.getBytes(), 0, strFilePath.getBytes().length);
        return true;
    }

    @PreDestroy
    public void destroy() {
        //停止监听释放
        if (ssHandle >= 0) {
            log.info(" 停止报警Alarm服务" + ssHandle);
            hCEhomeSS.NET_ESS_StopListen(ssHandle);
            hCEhomeSS.NET_ESS_Fini();
        }
    }
}
