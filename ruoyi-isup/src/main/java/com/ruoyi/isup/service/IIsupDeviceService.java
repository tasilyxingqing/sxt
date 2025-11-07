package com.ruoyi.isup.service;

import com.ruoyi.isup.domain.IsupDevice;

import java.util.List;

/**
 * isup设备Service接口
 *
 * @author fengcheng
 * @date 2025-04-22
 */
public interface IIsupDeviceService {
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
     * 批量删除isup设备
     *
     * @param ids 需要删除的isup设备主键集合
     * @return 结果
     */
    public int deleteIsupDeviceByIds(Long[] ids);

    /**
     * 删除isup设备信息
     *
     * @param id isup设备主键
     * @return 结果
     */
    public int deleteIsupDeviceById(Long id);

    /**
     * 更新设备状态
     *
     * @param lUserID   lUserID
     * @param ipAddress 地址
     * @param status    状态
     */
    int updateIsupDeviceStatusByLuserId(int lUserID, String ipAddress, String status);

    /**
     * 根据lUserID和ip查询设备
     *
     * @param lUserID   lUserID
     * @param ipAddress ipAddress
     * @return
     */
    IsupDevice selectIsupDeviceByLuserIdAndIp(int lUserID, String ipAddress);

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
