package com.ruoyi.onvif.domain.bo;

import lombok.Data;

/**
 * 绝对位置移动 bo类
 *
 * @Author: 陈江灿
 * @CreateTime: 2025-04-23
 */
@Data
public class AbsoluteMoveBo {
    private String ip;

    private String username;

    private String password;

    private String profileToken;

    private double x;

    private double y;
}
