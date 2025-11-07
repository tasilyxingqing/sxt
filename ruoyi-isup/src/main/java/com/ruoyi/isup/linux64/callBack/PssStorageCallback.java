package com.ruoyi.isup.linux64.callBack;

import com.alibaba.fastjson2.JSON;
import com.ruoyi.isup.linux64.interfaces.HcisupSs;
import com.ruoyi.isup.linux64.service.SsService;
import com.sun.jna.Pointer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
/***
 * 存储服务 存储回调
 **/
@Component
@Slf4j
public class PssStorageCallback implements HcisupSs.EHomeSSStorageCallBackLinux64 {

    @Autowired
    SsService ssService;

    public boolean invoke(int iHandle, String pFileName, Pointer pFileBuf, int dwFileLen, Pointer pFilePath, Pointer pUser) {
        log.info(" 存储服务 存储回调 iHandle：" + iHandle);
        log.info("pFileName:" + pFileName);
        log.info("pFileBuf:" + JSON.toJSONString(pFileBuf));
        log.info("dwFileLen:" + dwFileLen);
        log.info("pFilePath:" + JSON.toJSONString(pFilePath));
        log.info("pUser:" + JSON.toJSONString(pUser));
        return ssService.pssStorageCallback(iHandle, pFileName, pFileBuf, dwFileLen, pFilePath, pUser);
    }
}
