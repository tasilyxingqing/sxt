package com.ruoyi.isup.linux64.callBack;

import com.alibaba.fastjson2.JSON;

import com.ruoyi.isup.linux64.interfaces.HcisupAlarm;
import com.ruoyi.isup.linux64.service.AlarmService;
import com.sun.jna.Pointer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
/***
 * 报警服务回调
 **/
@Component
@Slf4j
public class EHomeMsgCallBack implements HcisupAlarm.EHomeMsgCallBackLinux64 {


    @Autowired
    AlarmService alarmService;

    @Override
    public boolean invoke(int iHandle, HcisupAlarm.NET_EHOME_ALARM_MSG pAlarmMsg, Pointer pUser) {
        log.info("报警服务回调：" + iHandle);
        log.info("pAlarmMsg：" + JSON.toJSONString(pAlarmMsg));
        log.info("pUser ：" + JSON.toJSONString(pUser));
        return alarmService.homeMsgCallBack(iHandle, pAlarmMsg, pUser);
    }
}
