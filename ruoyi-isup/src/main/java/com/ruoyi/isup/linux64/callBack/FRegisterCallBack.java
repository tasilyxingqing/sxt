package com.ruoyi.isup.linux64.callBack;


import com.alibaba.fastjson2.JSON;
import com.ruoyi.isup.linux64.interfaces.HcisupCms;
import com.ruoyi.isup.linux64.service.CmsService;
import com.sun.jna.Pointer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
/***
 *  注册服务回调
 **/
@Component
@Slf4j
public class FRegisterCallBack implements HcisupCms.DEVICE_REGISTER_CB_Linux64 {

    @Autowired
    CmsService cmsService;

    public boolean invoke(int lUserID, int dwDataType, Pointer pOutBuffer, int dwOutLen, Pointer pInBuffer, int dwInLen, Pointer pUser) {
        log.info(" 注册回调 lUserID：" + lUserID);
        log.info("dwDataType:" + dwDataType);
        log.info("pOutBuffer：" + JSON.toJSONString(pOutBuffer));
        log.info("dwOutLen:" + dwOutLen);
        log.info("pInBuffer:" + JSON.toJSONString(pInBuffer));
        log.info("dwInLen:" + dwInLen);
        log.info("pUser:" + JSON.toJSONString(pUser));
        return cmsService.registerCallBack(lUserID, dwDataType, pOutBuffer, dwOutLen, pInBuffer, dwInLen, pUser);
    }
}
