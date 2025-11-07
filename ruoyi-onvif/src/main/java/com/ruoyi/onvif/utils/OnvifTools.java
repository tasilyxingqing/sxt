package com.ruoyi.onvif.utils;

import be.teletask.onvif.DiscoveryManager;
import be.teletask.onvif.OnvifManager;
import be.teletask.onvif.listeners.DiscoveryListener;
import be.teletask.onvif.listeners.OnvifDeviceInformationListener;
import be.teletask.onvif.listeners.OnvifResponseListener;
import be.teletask.onvif.models.Device;
import be.teletask.onvif.models.OnvifDevice;
import be.teletask.onvif.models.OnvifDeviceInformation;
import be.teletask.onvif.responses.OnvifResponse;

import java.util.List;

public class OnvifTools {

    public static void main(String[] args) {
        String ipAddress = "192.168.2.205";
        String username = "admin";
        String password = "hx147258";
        OnvifManager onvifManager = new OnvifManager();
        onvifManager.setOnvifResponseListener(new OnvifResponseListener() {
            @Override
            public void onResponse(OnvifDevice onvifDevice, OnvifResponse response) {
                System.out.println("-------连接成功----------------------------------------");
                System.out.println("----onvifDevice----" + onvifDevice);
                System.out.println("----response----" + response);
            }

            @Override
            public void onError(OnvifDevice onvifDevice, int errorCode, String errorMessage) {
                System.out.println("------连接失败----------------------------------------");
            }
        });
        OnvifDevice device = new OnvifDevice(ipAddress, username, password);
        onvifManager.getDeviceInformation(device, new OnvifDeviceInformationListener() {
            @Override
            public void onDeviceInformationReceived(OnvifDevice onvifDevice, OnvifDeviceInformation onvifDeviceInformation) {
                System.out.println("-------onDeviceInformationReceived----------------------------------------" + onvifDeviceInformation);
                System.out.println(onvifDeviceInformation.getModel());
            }

        });
    }

}
