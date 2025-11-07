package com.ruoyi.dahua.mapper;

import java.util.List;
import com.ruoyi.dahua.domain.DahuaDevice;

/**
 * 大华设备Mapper接口
 * 
 * @author fengcheng
 * @date 2025-06-06
 */
public interface DahuaDeviceMapper 
{
    /**
     * 查询大华设备
     * 
     * @param id 大华设备主键
     * @return 大华设备
     */
    public DahuaDevice selectDahuaDeviceById(Long id);

    /**
     * 查询大华设备列表
     * 
     * @param dahuaDevice 大华设备
     * @return 大华设备集合
     */
    public List<DahuaDevice> selectDahuaDeviceList(DahuaDevice dahuaDevice);

    /**
     * 新增大华设备
     * 
     * @param dahuaDevice 大华设备
     * @return 结果
     */
    public int insertDahuaDevice(DahuaDevice dahuaDevice);

    /**
     * 修改大华设备
     * 
     * @param dahuaDevice 大华设备
     * @return 结果
     */
    public int updateDahuaDevice(DahuaDevice dahuaDevice);

    /**
     * 删除大华设备
     * 
     * @param id 大华设备主键
     * @return 结果
     */
    public int deleteDahuaDeviceById(Long id);

    /**
     * 批量删除大华设备
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDahuaDeviceByIds(Long[] ids);

    /**
     * 查询地图大华设备列表
     *
     * @param dahuaDevice
     * @return
     */
    List<DahuaDevice> selectDahuaDeviceListMap(DahuaDevice dahuaDevice);
}
