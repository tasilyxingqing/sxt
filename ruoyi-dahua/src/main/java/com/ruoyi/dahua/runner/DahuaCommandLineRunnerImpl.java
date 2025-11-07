package com.ruoyi.dahua.runner;

import com.ruoyi.dahua.domain.DahuaDevice;
import com.ruoyi.dahua.lib.NetSDKLib.*;
import com.ruoyi.dahua.lib.NetSDKLib.LLong;
import com.ruoyi.dahua.module.AutoRegisterModule;
import com.ruoyi.dahua.module.LoginModule;
import com.sun.jna.Pointer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 开启大华自动监听
 *
 * @FileName DahuaCommandLineRunnerImpl
 * @Description
 * @Author fengcheng
 * @date 2025-06-07
 **/
@Component
public class DahuaCommandLineRunnerImpl implements CommandLineRunner {

    @Value("${dahua.ip}")
    private String ip;

    @Value("${dahua.port}")
    private int port;

    @Value("${dahua.isPublicNetwork}")
    private boolean isPublicNetwork;

    private LLong mServerHandler = new LLong(0);

    Map<String, DahuaDevice> deviceMap = new HashMap<>();

    /**
     * 侦听服务器回调函数
     */
    private DahuaCommandLineRunnerImpl.ServiceCB servicCallback = new DahuaCommandLineRunnerImpl.ServiceCB();

    @Override
    public void run(String... args) throws Exception {
        // 打开工程，初始化，设置断线回调
        LoginModule.init(null, null);

        if (isPublicNetwork) {
            InetAddress address = InetAddress.getLocalHost();
            mServerHandler = AutoRegisterModule.startServer(address.getHostAddress(), port, servicCallback);
        } else {
            mServerHandler = AutoRegisterModule.startServer(ip, port, servicCallback);
        }

    }


    /**
     * 侦听服务器回调函数
     */
    public class ServiceCB implements fServiceCallBack {
        @Override
        public int invoke(LLong lHandle, final String pIp, final int wPort, int lCommand, Pointer pParam, int dwParamLen, Pointer dwUserData) {

            // 将 pParam 转化为序列号
            byte[] buffer = new byte[dwParamLen];
            pParam.read(0, buffer, 0, dwParamLen);
            String deviceId = "";
            try {
                deviceId = new String(buffer, "GBK").trim();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            System.out.printf("Register Device Info [Device address %s][port %s][DeviceID %s] \n", pIp, wPort, deviceId);

            if (isPublicNetwork) {
                addOrUpdateDevice(deviceMap, pIp, deviceId, wPort);
            }else {
                addOrUpdateDevice(deviceMap, pIp, deviceId, 37777);
            }

            System.out.println("设备列表--------" + getRegisterDeviceList());
            return 0;
        }
    }

    /**
     * 添加或更新 设备
     *
     * @param deviceMap
     * @param ip
     * @param deviceId
     * @param port
     */
    public static void addOrUpdateDevice(Map<String, DahuaDevice> deviceMap, String ip, String deviceId, int port) {
        DahuaDevice device = new DahuaDevice();
        device.setIp(ip);
        device.setDeviceId(deviceId);
        device.setPort(String.valueOf(port));
        deviceMap.put(ip, device);
    }

    /**
     * 获取设备列表
     *
     * @return
     */
    public List<DahuaDevice> getRegisterDeviceList() {
        // 最终转成 list
        List<DahuaDevice> deviceList = new ArrayList<>(deviceMap.values());

        return deviceList;
    }

    /**
     * 获取设备列表
     *
     * @return
     */
    public void delRegisterDevice(String[] ips) {
        for (String ip : ips) {
            deviceMap.remove(ip);
        }
    }
}
