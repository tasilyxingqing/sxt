package com.ruoyi.isup.service.streamService;

import com.ruoyi.isup.common.osSelect;
import com.sun.jna.Native;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component("hCNetService")
public class HCNet {
    public static HCNetSDK hCNetSDK = null;

    /**
     * 动态库加载
     *
     * @return
     */
    private static boolean CreateSDKInstance() {
        if (hCNetSDK == null) {
            synchronized (HCNetSDK.class) {
                String strDllPath = "";
                try {
                    //win系统加载SDK库路径
                    if (osSelect.isWindows()) {
                        strDllPath = System.getProperty("user.dir") + "\\ruoyi-isup\\win-lib\\HCNetSDK.dll";
                    } else if (osSelect.isLinux()) {
                        //Linux系统加载SDK库路径
                        strDllPath = System.getProperty("user.dir") + "/ruoyi-isup/linux-lib/libhcnetsdk.so";
                    }
                    hCNetSDK = (HCNetSDK) Native.loadLibrary(strDllPath, HCNetSDK.class);
                } catch (Exception ex) {
                    System.out.println("loadLibrary: " + strDllPath + " Error: " + ex.getMessage());
                    return false;
                }
            }
        }
        return true;
    }

    public void hCNetSDK_Init() {
        if (hCNetSDK == null) {
            if (!CreateSDKInstance()) {
                System.out.println("Load HCNetSDK SDK fail");
                return;
            }
        }

        boolean initSuc = hCNetSDK.NET_DVR_Init();
        if (initSuc != true) {
            log.info("初始化HCNetSDK失败, errCode= " + hCNetSDK.NET_DVR_GetLastError());
        }
    }
}
