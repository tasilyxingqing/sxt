package com.ruoyi.dahua.domain;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.dahua.lib.NetSDKLib.LLong;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 大华设备对象 dahua_device
 *
 * @author fengcheng
 * @date 2025-06-06
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DahuaDevice extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private Long id;

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 所属部门
     */
    @Excel(name = "所属部门")
    private String deptName;

    /**
     * ip
     */
    @Excel(name = "ip")
    private String ip;

    /**
     * 设备id
     */
    @Excel(name = "设备id")
    private String deviceId;

    /**
     * 摄像头名称
     */
    @Excel(name = "摄像头名称")
    private String name;

    /**
     * 端口
     */
    @Excel(name = "端口")
    private String port;

    /**
     * 用户名
     */
    @Excel(name = "用户名")
    private String userName;

    /**
     * 密码
     */
    @Excel(name = "密码")
    private String password;

    /**
     * 通道号
     */
    private Integer channel;

    /**
     * 播放类型（1=本地，2=推流，3=EasyNTS）
     */
    @Excel(name = "播放类型")
    private String playType;

    /**
     * 直播流播放地址
     */
    @Excel(name = "直播流播放地址")
    private String url;

    /**
     * 流id
     */
    @Excel(name = "流id")
    private String streamId;

    /**
     * 直播流播放地址
     */
    @Excel(name = "EasyNTS播放地址")
    private String easyNTSUrl;

    /**
     * 纬度
     */
    private String lat;

    /**
     * 经度
     */
    private String lng;

    /**
     * 地图定位地址
     */
    @Excel(name = "地图定位地址")
    private String addressMap;

    /**
     * 登录句柄
     */
    private LLong m_hLoginHandle;
}
