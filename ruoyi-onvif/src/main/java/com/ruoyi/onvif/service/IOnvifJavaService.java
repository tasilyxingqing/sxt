package com.ruoyi.onvif.service;

import com.ruoyi.onvif.domain.OnvifJavaPZT;

/**
 * onvif-Java 接口类
 *
 * @author 陈江灿
 */
public interface IOnvifJavaService {

    /**
     * 云台控制
     * @param onvifJavaPZT
     */
    void ptz(OnvifJavaPZT onvifJavaPZT);

}
