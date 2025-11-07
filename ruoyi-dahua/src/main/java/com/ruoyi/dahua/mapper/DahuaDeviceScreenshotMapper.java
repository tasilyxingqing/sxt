package com.ruoyi.dahua.mapper;

import java.util.List;
import com.ruoyi.dahua.domain.DahuaDeviceScreenshot;

/**
 * 大华设备抓图Mapper接口
 * 
 * @author fengcheng
 * @date 2025-06-12
 */
public interface DahuaDeviceScreenshotMapper 
{
    /**
     * 查询大华设备抓图列表
     * 
     * @param dahuaDeviceScreenshot 大华设备抓图
     * @return 大华设备抓图集合
     */
    public List<DahuaDeviceScreenshot> selectDahuaDeviceScreenshotList(DahuaDeviceScreenshot dahuaDeviceScreenshot);

    /**
     * 新增大华设备抓图
     * 
     * @param dahuaDeviceScreenshot 大华设备抓图
     * @return 结果
     */
    public int insertDahuaDeviceScreenshot(DahuaDeviceScreenshot dahuaDeviceScreenshot);

    /**
     * 批量删除大华设备抓图
     *
     * @param ids 需要删除的主键集合
     * @return 结果
     */
    public int deleteDahuaDeviceScreenshotByIds(Long[] ids);
}
