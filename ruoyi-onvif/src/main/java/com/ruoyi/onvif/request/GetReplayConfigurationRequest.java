package com.ruoyi.onvif.request;

import be.teletask.onvif.models.OnvifType;
import be.teletask.onvif.requests.OnvifRequest;

/**
 * 自定义onvif请求类
 *
 * @author 陈江灿
 */
public class GetReplayConfigurationRequest implements OnvifRequest {

    @Override
    public String getXml() {
        return "<GetServiceCapabilities xmlns=\"http://www.onvif.org/ver10/replay/wsdl\"></GetServiceCapabilities>";
    }

    @Override
    public OnvifType getType() {
        return OnvifType.CUSTOM;
    }
}
