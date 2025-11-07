package com.ruoyi.isup.linux64.callBack;

import com.alibaba.fastjson2.JSON;
import com.ruoyi.isup.linux64.interfaces.HcisupSs;
import com.ruoyi.isup.linux64.service.SsService;
import com.sun.jna.Pointer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
/***
 *  存储服务消息回调
 **/
@Component
@Slf4j
public class PssMessageCallback implements HcisupSs.EHomeSSMsgCallBackLinux64 {

    @Autowired
    SsService ssService;

    public boolean invoke(int iHandle, int enumType, Pointer pOutBuffer, int dwOutLen, Pointer pInBuffer,
                          int dwInLen, Pointer pUser) {
        log.info(" 存储服务 消息回调函数 iHandle：" + iHandle);
        log.info("enumType:" + enumType);
        log.info("pOutBuffer:" + JSON.toJSONString(pOutBuffer));
        log.info("dwOutLen:" + dwOutLen);
        log.info("pInBuffer:" + JSON.toJSONString(pInBuffer));
        log.info("dwInLen:" + dwInLen);
        log.info("pUser:" + JSON.toJSONString(pUser));
        return ssService.pssMessageCallback(iHandle, enumType, pOutBuffer, dwOutLen, pInBuffer, dwInLen, pUser);
    }
}
