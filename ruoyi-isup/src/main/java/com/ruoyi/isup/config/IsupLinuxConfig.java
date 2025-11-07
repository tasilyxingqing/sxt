package com.ruoyi.isup.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * ISUP linux64配置
 *
 * @author cjc
 */
@Component
@ConfigurationProperties(prefix = "isup-linux64")
@Data
public class IsupLinuxConfig {
    /**
     * 注册服务器监听地址（服务器本地地址）
     */
    private String cmsServerIP;

    /**
     * 注册服务器端口号
     */
    private short cmsServerPort;

    /**
     * DAS地址
     */
    private String dasServerIP;

    private short dasServerPort;

    /**
     * 报警服务器地址（公网对接时，填入公网地址和端口）
     */
    private String alarmServerIP;

    /**
     * 报警服务器 TCP 端口号
     */
    private short alarmServerTCPPort;

    /**
     * 报警服务器 UDP 端口号
     */
    private short alarmServerUDPPort;

    /**
     * 报警服务器类型：
     * 0 - 只支持 UDP 协议上报
     * 1 - 支持 UDP 和 TCP 两种协议上报（ISUP4.0）
     * 2 - 支持 MQTT 协议（ISUP5.0）
     */
    private int alarmServerType;

    /**
     * 报警服务器监听地址（监听端口）
     */
    private String alarmServerListenIP;

    /**
     * 报警服务器监听 TCP 端口号
     */
    private short alarmServerListenTCPPort;

    /**
     * 报警服务器监听 UDP 端口号
     */
    private short alarmServerListenUDPPort;

    /**
     * 图片存储服务器地址（IUSP5.0版本接入端口建议固定为6011）
     */
    private String picServerIP;

    /**
     * 图片存储服务器端口号
     */
    private short picServerPort;

    /**
     * 图片存储服务器类型：
     * 0 - Tomcat
     * 1 - VRB
     * 2 - 云存储
     * 3 - KMS
     * 4 - ISUP5.0
     * 选择存储服务器类型，根据版本和设备类型而不同
     */
    private int picServerType;

    /**
     * 图片存储服务器监听地址（监听端口）
     */
    private String picServerListenIP;

    /**
     * 图片存储服务器监听端口号
     */
    private short picServerListenPort;

    /**
     * ISUP5.0 登录秘钥
     */
    private String iSUPKey;

    /**
     * 报警服务器打印事件模式：
     * file - 事件保存到文件中
     * console - 事件打印在控制台上
     */
    private String eventInfoPrintType;


    /**
     * CMS 日志路径
     */
    private String cmsLogPath;

    /**
     * 报警日志路径
     */
    private String alarmLogPath;

    /**
     * SS 日志路径
     */
    private String ssLogPath;
}
