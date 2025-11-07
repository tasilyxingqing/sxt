package com.ruoyi.isup.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/***
 * isup配置
 *
 * @author fengcheng
 */
@Configuration
@Data
public class IsupConfig {

    @Value("${isup.IP}")
    private String ip;

    @Value("${media.http-port}")
    private String zlmHttpPort;

    @Value("${isup.route}")
    private String route;

    @Value("${isup.cmsServer.Port}")
    private int cmsServerPort;

    @Value("${isup.smsServer.Port}")
    private int smsServerPort;

    @Value("${isup.smsServer.ListenPort}")
    private int smsServerListenPort;

    @Value("${isup.ssService.Port}")
    private int ssServicePort;

    @Value("${isup.alarmService.Port}")
    private int alarmServicePort;

    @Value("${isup.isupKey}")
    private String isupKey;

    @Value("${isup.alarmService.Type}")
    private String alarmServerType;

    @Value("${isup.picServer.Port}")
    private String picServerPort;

    @Value("${isup.picServer.Type}")
    private String picServerType;

    @Value("${isup.smsBackServerListe.Port}")
    private String smsBackServerListenPort;
}
