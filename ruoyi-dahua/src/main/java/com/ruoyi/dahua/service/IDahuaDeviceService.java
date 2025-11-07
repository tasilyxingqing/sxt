package com.ruoyi.dahua.service;

import com.ruoyi.dahua.domain.DahuaDevice;

import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * 大华设备Service接口
 *
 * @author fengcheng
 * @date 2025-06-06
 */
public interface IDahuaDeviceService {
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
     * 批量删除大华设备
     *
     * @param ids 需要删除的大华设备主键集合
     * @return 结果
     */
    public int deleteDahuaDeviceByIds(Long[] ids);

    /**
     * 删除大华设备信息
     *
     * @param id 大华设备主键
     * @return 结果
     */
    public int deleteDahuaDeviceById(Long id);

    /**
     * 大华设备登录
     *
     * @param dahuaDevice 大华设备
     * @return
     */
    Vector<Integer> login(DahuaDevice dahuaDevice);


    Map<String, String> startRealPlay(Long id);

    int stopRealPlay(Long id);

    /**
     * 大华设备云台控制（开始）
     *
     * @param direction 方向
     * @param id        设备id
     * @param speed     速度
     */
    boolean ptzControlStart(String direction, Long id, int speed);

    /**
     * 大华设备抓图
     *
     * @param id 设备id
     * @return
     */
    boolean snapPicture(Long id);

    /**
     * 大华设备定时抓图
     *
     * @param id       设备id
     * @return
     */
    boolean timerCapturePicture(Long id);

    /**
     * 大华设备停止定时抓图
     *
     * @param id 设备id
     * @return
     */
    boolean stopCapturePicture(Long id);

    /**
     * 大华设备获取时间
     *
     * @param id 设备id
     * @return
     */
    String getTime(Long id);

    /**
     * 大华设备设置时间
     *
     * @param id   设备id
     * @param date 时间
     * @param type 时间类型（true=当前时间,false=设置时间）
     * @return
     */
    boolean setTime(Long id, String date, boolean type);

    /**
     * 大华设备重启
     *
     * @param id 设备id
     * @return
     */
    boolean reboot(Long id);

    /**
     * 大华设备云台控制（停止）
     *
     * @param direction 方向
     * @param id        设备id
     * @return
     */
    boolean ptzControlUpEnd(String direction, Long id);

    /**
     * 查询地图大华设备列表
     *
     * @param dahuaDevice
     * @return
     */
    List<DahuaDevice> selectDahuaDeviceListMap(DahuaDevice dahuaDevice);


}
