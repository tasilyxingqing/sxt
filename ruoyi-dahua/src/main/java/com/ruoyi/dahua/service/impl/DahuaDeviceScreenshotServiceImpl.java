package com.ruoyi.dahua.service.impl;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.dahua.domain.DahuaDeviceScreenshot;
import com.ruoyi.dahua.mapper.DahuaDeviceScreenshotMapper;
import com.ruoyi.dahua.service.IDahuaDeviceScreenshotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 大华设备抓图Service业务层处理
 *
 * @author fengcheng
 * @date 2025-06-12
 */
@Service
public class DahuaDeviceScreenshotServiceImpl implements IDahuaDeviceScreenshotService {
    @Autowired
    private DahuaDeviceScreenshotMapper dahuaDeviceScreenshotMapper;

    /**
     * 查询大华设备抓图列表
     *
     * @param dahuaDeviceScreenshot 大华设备抓图
     * @return 大华设备抓图
     */
    @Override
    public List<DahuaDeviceScreenshot> selectDahuaDeviceScreenshotList(DahuaDeviceScreenshot dahuaDeviceScreenshot) {
        return dahuaDeviceScreenshotMapper.selectDahuaDeviceScreenshotList(dahuaDeviceScreenshot);
    }

    /**
     * 新增大华设备抓图
     *
     * @param dahuaDeviceScreenshot 大华设备抓图
     * @return 结果
     */
    @Override
    public int insertDahuaDeviceScreenshot(DahuaDeviceScreenshot dahuaDeviceScreenshot) {
        dahuaDeviceScreenshot.setCreateTime(DateUtils.getNowDate());
        return dahuaDeviceScreenshotMapper.insertDahuaDeviceScreenshot(dahuaDeviceScreenshot);
    }

    /**
     * 批量删除大华设备抓图
     *
     * @param ids 需要删除的主键集合
     * @return 结果
     */
    @Override
    public int deleteDahuaDeviceScreenshotByIds(Long[] ids) {
        return dahuaDeviceScreenshotMapper.deleteDahuaDeviceScreenshotByIds(ids);
    }
}
