package com.ruoyi.isup.service.impl;

import com.ruoyi.common.annotation.DataScope;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.isup.domain.IsupDevice;
import com.ruoyi.isup.mapper.IsupDeviceMapper;
import com.ruoyi.isup.service.IIsupDeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * isup设备Service业务层处理
 *
 * @author fengcheng
 * @date 2025-04-22
 */
@Service
public class IsupDeviceServiceImpl implements IIsupDeviceService {
    @Autowired
    private IsupDeviceMapper isupDeviceMapper;

    /**
     * 查询isup设备
     *
     * @param id isup设备主键
     * @return isup设备
     */
    @Override
    public IsupDevice selectIsupDeviceById(Long id) {
        return isupDeviceMapper.selectIsupDeviceById(id);
    }

    /**
     * 查询isup设备列表
     *
     * @param isupDevice isup设备
     * @return isup设备
     */
    @Override
    @DataScope(deptAlias = "d")
    public List<IsupDevice> selectIsupDeviceList(IsupDevice isupDevice) {
        return isupDeviceMapper.selectIsupDeviceList(isupDevice);
    }

    /**
     * 新增isup设备
     *
     * @param isupDevice isup设备
     * @return 结果
     */
    @Override
    public int insertIsupDevice(IsupDevice isupDevice) {
        isupDevice.setCreateTime(DateUtils.getNowDate());
        return isupDeviceMapper.insertIsupDevice(isupDevice);
    }

    /**
     * 修改isup设备
     *
     * @param isupDevice isup设备
     * @return 结果
     */
    @Override
    public int updateIsupDevice(IsupDevice isupDevice) {
        isupDevice.setUpdateTime(DateUtils.getNowDate());
        String port = "554";
        String url = String.format("rtsp://%s:%s@%s:%s/Streaming/Channels/%s", isupDevice.getUserName(), isupDevice.getPassword(), isupDevice.getIpAddress(), port, isupDevice.getChannel());
        isupDevice.setUrl(url);
        return isupDeviceMapper.updateIsupDevice(isupDevice);
    }

    /**
     * 批量删除isup设备
     *
     * @param ids 需要删除的isup设备主键
     * @return 结果
     */
    @Override
    public int deleteIsupDeviceByIds(Long[] ids) {
        return isupDeviceMapper.deleteIsupDeviceByIds(ids);
    }

    /**
     * 删除isup设备信息
     *
     * @param id isup设备主键
     * @return 结果
     */
    @Override
    public int deleteIsupDeviceById(Long id) {
        return isupDeviceMapper.deleteIsupDeviceById(id);
    }

    /**
     * 更新设备状态
     *
     * @param lUserID   lUserID
     * @param ipAddress 地址
     * @param status    状态
     */
    @Override
    public int updateIsupDeviceStatusByLuserId(int lUserID, String ipAddress, String status) {
        return isupDeviceMapper.updateIsupDeviceStatusByLuserId(lUserID, ipAddress, status);
    }

    /**
     * 根据lUserID和ip查询设备
     *
     * @param lUserID   lUserID
     * @param ipAddress ipAddress
     * @return
     */
    @Override
    public IsupDevice selectIsupDeviceByLuserIdAndIp(int lUserID, String ipAddress) {
        return isupDeviceMapper.selectIsupDeviceByLuserIdAndIp(lUserID, ipAddress);
    }

    /**
     * 更新设备在线状态
     *
     * @param id
     */
    @Override
    public void updateIsupDeviceonLineById(Long id) {
        isupDeviceMapper.updateIsupDeviceonLineById(id);
    }

    /**
     * 更新设备离线状态
     *
     * @param id
     */
    @Override
    public void updateIsupDeviceOfflineById(Long id) {
        isupDeviceMapper.updateIsupDeviceOfflineById(id);
    }

    /**
     * 查询所有设备
     *
     * @return 设备列表
     */
    @Override
    public List<IsupDevice> selectAllIsupDeviceList() {
       return isupDeviceMapper.selectAllIsupDeviceList();
    }

    /**
     * 查询地图isup设备列表
     *
     * @param isupDevice
     * @return
     */
    @Override
    public List<IsupDevice> selectIsupDeviceListMap(IsupDevice isupDevice) {
        return isupDeviceMapper.selectIsupDeviceListMap(isupDevice);
    }
}
