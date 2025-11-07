package com.ruoyi.isup.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * isup设备对象 isup_device
 *
 * @author fengcheng
 * @date 2025-04-22
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IsupDevice extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 自增ID
     */
    private Long id;

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 设备ID
     */
    private String deviceId;

    /**
     * 名称
     */
    private String name;

    /**
     * 通道
     */
    private String channel;

    /**
     * 播放类型（1=本地，2=推流，3=EasyNTS）
     */
    private String playType;

    /**
     * 直播流播放地址
     */
    private String url;

    /**
     * 流id
     */
    private String streamId;

    /**
     * 直播流播放地址
     */
    private String easyNTSUrl;

    /**
     * 设备注册信息的大小
     */
    private Integer dwSize;

    /**
     * 网络单元类型
     */
    private Integer dwNetUnitType;

    /**
     * 固件版本
     */
    private String firmwareVersion;

    /**
     * 设备的 IP 地址
     */
    private String ipAddress;

    /**
     * 设备的端口号
     */
    private Short port;

    /**
     * 设备的保留字段
     */
    private String deviceRes;

    /**
     * 设备类型
     */
    private Integer devType;

    /**
     * 设备的制造商标识
     */
    private Integer manufacture;

    /**
     * 用户名
     */
    private String userName;


    /**
     * 密码
     */
    private String password;

    /**
     * 设备的序列号
     */
    private String deviceSerial;

    /**
     * 可靠传输标志
     */
    private byte reliableTransmission;

    /**
     * WebSocket 传输标志
     */
    private byte websocketTransmission;

    /**
     * 设备支持重定向注册 0-不支持 1-支持
     */
    private byte supportRedirect;

    /**
     * 设备协议版本
     */
    private String devProtocolVersion;

    /**
     * Ehome5.0设备SessionKey
     */
    private String sessionKey;

    /**
     * 0-无效（未知类型）,1-经销型，2-行业型
     */
    private String res;

    /**
     * 保留字段
     */
    private byte marketType;

    /**
     * 用户ID
     */
    private Integer luserId;

    /**
     * 状态（1=在线,2=离线）
     */
    private String status;

    /**
     * 部门名称
     */
    private String deptName;

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
    private String addressMap;
}
