package com.ruoyi.onvif.domain.bo;

import lombok.Data;

/**
 * onvif 云台控制
 *
 * @FileName OnvifPZT
 * @Description
 * @Author fengcheng
 * @date 2025-06-26
 **/
@Data
public class OnvifPZT {

    /**
     * id
     */
    private Long id;

    /**
     * 方向（upper=上，below=下，left=左，right=右）
     */
    private String direction;

    /**
     * 速度（大于0.1小于1）
     */
    private Double controSpeed;
}
