package com.ruoyi.onvif.service.impl;

import be.teletask.onvif.OnvifManager;
import be.teletask.onvif.listeners.OnvifResponseListener;
import be.teletask.onvif.models.OnvifDevice;
import be.teletask.onvif.models.OnvifType;
import be.teletask.onvif.responses.OnvifResponse;
import com.ruoyi.onvif.domain.OnvifJavaPZT;
import com.ruoyi.onvif.domain.bo.OnvifTypeBo;
import com.ruoyi.onvif.service.IOnvifJavaService;
import org.springframework.stereotype.Service;

/**
 * onvif-java 接口实现类
 *
 * @author 陈江灿
 * @date 2020-09-02
 */
@Service
public class OnvifJavaServiceImpl implements IOnvifJavaService {

    public static void main(String[] args) {
        ptz2();
    }

    private static void ptz2() {

        OnvifManager onvifManager = new OnvifManager();
        onvifManager.setOnvifResponseListener(new OnvifResponseListener() {
            @Override
            public void onResponse(OnvifDevice onvifDevice, OnvifResponse response) {
            }
            @Override
            public void onError(OnvifDevice onvifDevice, int errorCode, String errorMessage) {
            }
        });

        OnvifDevice device = new OnvifDevice("192.168.2.204", "admin", "hx147258");

        onvifManager.setOnvifResponseListener(new OnvifResponseListener() {
            @Override
            public void onResponse(be.teletask.onvif.models.OnvifDevice onvifDevice, be.teletask.onvif.responses.OnvifResponse onvifResponse) {
                System.out.println("onvifResponse = " + onvifResponse.getXml());
                OnvifType requestType = onvifResponse.request().getType();
                System.out.println("requestType = " + requestType);
            }

            //请求失败处理
            @Override
            public void onError(be.teletask.onvif.models.OnvifDevice onvifDevice, int i, String s) {
                System.out.println("error = " + s);
                System.out.println(("失败"));
            }
        });

    }

    /**
     * 云台控制
     * @param onvifJavaPZT
     */
    @Override
    public void ptz(OnvifJavaPZT onvifJavaPZT) {
        OnvifManager onvifManager = new OnvifManager();
        onvifManager.setOnvifResponseListener(new OnvifResponseListener() {
            @Override
            public void onResponse(OnvifDevice onvifDevice, OnvifResponse response) {
            }
            @Override
            public void onError(OnvifDevice onvifDevice, int errorCode, String errorMessage) {
            }
        });

        OnvifDevice device = new OnvifDevice(onvifJavaPZT.getIp(), onvifJavaPZT.getUserName(), onvifJavaPZT.getPassword());

        onvifManager.setOnvifResponseListener(new OnvifResponseListener() {
            @Override
            public void onResponse(be.teletask.onvif.models.OnvifDevice onvifDevice, be.teletask.onvif.responses.OnvifResponse onvifResponse) {
                System.out.println("onvifResponse = " + onvifResponse.getXml());
            }

            //请求失败处理
            @Override
            public void onError(be.teletask.onvif.models.OnvifDevice onvifDevice, int i, String s) {
                System.out.println("error = " + s);
                System.out.println(("失败"));
            }
        });

    }
}
