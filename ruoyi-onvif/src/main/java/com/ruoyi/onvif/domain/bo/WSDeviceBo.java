package com.ruoyi.onvif.domain.bo;

import lombok.Data;


@Data
public class WSDeviceBo {
    private String ip;
    private String auth;
    private String hostName;
    private String username;
    private String password;
}
