package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.large.CountDeviceNum;
import com.ruoyi.system.domain.large.CountGbNum;
import com.ruoyi.system.domain.large.LeftGbDevice;
import com.ruoyi.system.mapper.SysLargeServiceMapper;
import com.ruoyi.system.service.ISysLargeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @FileName SysLargeServiceImpl
 * @Description
 * @Author fengcheng
 * @date 2025-05-01
 **/
@Service
public class SysLargeServiceImpl implements ISysLargeService {

    @Autowired
    private SysLargeServiceMapper sysLargeServiceMapper;

    /**
     * 获取总设备数量
     *
     * @return
     */
    @Override
    public CountDeviceNum countDeviceNum() {
        return sysLargeServiceMapper.countDeviceNum();
    }

    /**
     * 获取国标总览
     *
     * @return
     */
    @Override
    public CountGbNum countGbNum() {
        return sysLargeServiceMapper.countGbNum();
    }

    /**
     * 获取国标设备提醒
     *
     * @return
     */
    @Override
    public List<LeftGbDevice> leftGbDevice() {
        return sysLargeServiceMapper.leftGbDevice();
    }
}
