package com.ruoyi.onvif.domain.bo;

import lombok.Data;

/**
 * 预置位 bo
 * @Author: 陈江灿
 * @CreateTime: 2025-04-26
 */
@Data
public class PresetsBo {

    private String ip;

    private String username;

    private String password;

    private String profileToken;

    private String presetToken;
}
