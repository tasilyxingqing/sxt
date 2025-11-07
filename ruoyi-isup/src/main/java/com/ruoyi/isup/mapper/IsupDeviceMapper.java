package com.ruoyi.isup.mapper;

import com.ruoyi.isup.domain.IsupDevice;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * isup设备Mapper接口
 *
 * @author fengcheng
 * @date 2025-04-22
 */
public interface IsupDeviceMapper {
    /**
     * 查询isup设备
     *
     * @param id isup设备主键
     * @return isup设备
     */
    public IsupDevice selectIsupDeviceById(Long id);

    /**
     * 查询isup设备列表
     *
     * @param isupDevice isup设备
     * @return isup设备集合
     */
    public List<IsupDevice> selectIsupDeviceList(IsupDevice isupDevice);

    /**
     * 新增isup设备
     *
     * @param isupDevice isup设备
     * @return 结果
     */
    public int insertIsupDevice(IsupDevice isupDevice);

    /**
     * 修改isup设备
     *
     * @param isupDevice isup设备
     * @return 结果
     */
    public int updateIsupDevice(IsupDevice isupDevice);

    /**
     * 删除isup设备
     *
     * @param id isup设备主键
     * @return 结果
     */
    public int deleteIsupDeviceById(Long id);

    /**
     * 批量删除isup设备
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteIsupDeviceByIds(Long[] ids);

    /**
     * 更新设备状态
     *
     * @param lUserID   lUserID
     * @param ipAddress 地址
     * @param status    状态
     */
    int updateIsupDeviceStatusByLuserId(@Param("lUserID") int lUserID, @Param("ipAddress") String ipAddress, @Param("status") String status);

    /**
     * 根据lUserID和ip查询设备
     *
     * @param lUserID   lUserID
     * @param ipAddress ipAddress
     * @return
     */
    IsupDevice selectIsupDeviceByLuserIdAndIp(@Param("lUserID") int lUserID, @Param("ipAddress") String ipAddress);

    /**
     * 更新设备在线状态
     *
     * @param id
     */
    void updateIsupDeviceonLineById(Long id);

    /**
     * 更新设备离线状态
     *
     * @param id
     */
    void updateIsupDeviceOfflineById(Long id);

    /**
     * 查询所有设备
     *
     * @return 设备列表
     */
    List<IsupDevice> selectAllIsupDeviceList();

    /**
     * 查询地图isup设备列表
     *
     * @param isupDevice
     * @return
     */
    List<IsupDevice> selectIsupDeviceListMap(IsupDevice isupDevice);
}
