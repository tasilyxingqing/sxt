package com.ruoyi.isup.task;


import cn.hutool.core.util.ObjUtil;
import com.ruoyi.isup.domain.IsupDevice;
import com.ruoyi.isup.service.IIsupDeviceService;
import com.ruoyi.isup.service.cmsService.CMS;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * isup设备状态定时任务
 *
 * @author fengcheng
 */
@Slf4j
@Component
public class IsupDeviceTask {

    @Autowired
    private IIsupDeviceService isupDeviceService;

    @Autowired
    private CMS cms;

    @Scheduled(cron = "0 0/4 * * * ?")
    public void deviceTask() {
        log.info("isup设备状态定时任务开始执行");

        if(ObjUtil.isNull(cms)){
            return;
        }

        // 查询所有设备列
        List<IsupDevice> isupDevices = isupDeviceService.selectAllIsupDeviceList();

        if (isupDevices != null && isupDevices.size() > 0) {
            for (IsupDevice isupDevice : isupDevices) {
                if (cms.getDevInfo(isupDevice.getLuserId())) {
                    isupDeviceService.updateIsupDeviceonLineById(isupDevice.getId());
                    log.info("设备{}在线", isupDevice.getDeviceId());
                } else {
                    isupDeviceService.updateIsupDeviceOfflineById(isupDevice.getId());
                    log.info("设备{}离线", isupDevice.getDeviceId());
                }
            }
        }
    }
}
