package com.ruoyi.isup.linux64.utils;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class HkUtils {

    //cms 引入
    public static String getCmsPath() {
        String resource = null;
        //win64系统加载库路径
        if (isWindows64()) {
            resource = System.getProperty("user.dir") + "/lib/win64/HCISUPCMS.dll";
        }
        //Linux64系统加载库路径
        if (isLinux64()) {
            resource = System.getProperty("user.dir") + "/ruoyi-isup/linux-lib/libHCISUPCMS.so";
        }
        if (resource == null) {
            log.info("cms未找到加载库文件!");
            return "";
        }
        return resource;
    }

    //AMS 引入
    public static String getAmsPath() {
        String resource = null;
        //win64系统加载库路径
        if (isWindows64()) {
            resource = System.getProperty("user.dir") + "/lib/win64/HCISUPAlarm.dll";
        }
        //Linux64系统加载库路径
        if (isLinux64()) {
            resource = System.getProperty("user.dir") + "/ruoyi-isup/linux-lib/libHCISUPAlarm.so";
        }
        if (resource == null) {
            log.info("Ams未找到加载库文件!");
            return "";
        }
        return resource;
    }

    //ss 引入
    public static String getSsPath() {
        String resource = null;
        //win64系统加载库路径
        if (isWindows64()) {
            resource = System.getProperty("user.dir") + "/lib/win64/HCISUPSS.dll";
        }
        //Linux64系统加载库路径
        if (isLinux64()) {
            resource = System.getProperty("user.dir") + "/ruoyi-isup/linux-lib/libHCISUPSS.so";
        }
        if (resource == null) {
            log.info("ss未找到加载库文件!");
            return "";
        }
        return resource;
    }

    //加载 crypto 库
    public static String getCryptoPath() {
        String resource = null;
        //win64系统加载库路径
        if (isWindows64()) {
            resource = System.getProperty("user.dir") + "/lib/win64/libeay32.dll";
        }
        //Linux64系统加载库路径
        if (isLinux64()) {
            resource = System.getProperty("user.dir") + "/ruoyi-isup/linux-lib/libcrypto.so";
        }

        if (resource == null) {
            log.info(" crypto 未找到加载库文件!");
            return "";
        }
        return resource;
    }

    //加载 ssl 库
    public static String getSslPath() {
        String resource = null;
        //win64系统加载库路径
        if (isWindows64()) {
            resource = System.getProperty("user.dir") + "/lib/win64/ssleay32.dll";
        }
        //Linux64系统加载库路径
        if (isLinux64()) {
            resource = System.getProperty("user.dir") + "/ruoyi-isup/linux-lib/libssl.so";
        }
        if (resource == null) {
            log.info(" ssl 未找到加载库文件!");
            return "";
        }
        return resource;
    }

    // 设置 HCAapSDKCom 文件夹
    public static String getHCAapSDKComPath() {
        String resource = null;
        //win64系统加载库路径
        if (isWindows64()) {
            resource = System.getProperty("user.dir") + "/lib/win64/HCAapSDKCom/";
        }
        //Linux64系统加载库路径
        if (isLinux64()) {
            resource = System.getProperty("user.dir") + "/ruoyi-isup/linux-lib/HCAapSDKCom/";
        }
        if (resource == null) {
            log.info(" HCAapSDKCom 未找到加载库文件!");
            return "";
        }
        return resource;
    }

    //加载 sqlite3 库
    public static String getSqlite3Path() {
        String resource = null;
        //win64系统加载库路径
        if (isWindows64()) {
            resource = System.getProperty("user.dir") + "/lib/win64/sqlite3.dll";
        }
        //Linux64系统加载库路径
        if (isLinux64()) {
            resource = System.getProperty("user.dir") + "/ruoyi-isup/linux-lib/libsqlite3.so";
        }
        if (resource == null) {
            log.info(" sqlite3 未找到加载库文件!");
            return "";
        }
        return resource;
    }

    public static boolean isLinux64() {
        String osName = System.getProperty("os.name").toLowerCase();
        String osArch = System.getProperty("os.arch").toLowerCase();
        return osName.contains("linux") && osArch.contains("64");
    }

    public static boolean isWindows64() {
        String osName = System.getProperty("os.name").toLowerCase();
        String osArch = System.getProperty("os.arch").toLowerCase();
        return osName.contains("windows") && osArch.contains("64");
    }



}
