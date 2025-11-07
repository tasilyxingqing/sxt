package com.ruoyi.dahua.domain;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 大华设备抓图对象 dahua_device_screenshot
 *
 * @author fengcheng
 * @date 2025-06-12
 */
public class DahuaDeviceScreenshot extends BaseEntity {
    private static final long serialVersionUID = 1L;


    /**
     * 编号
     */
    private Long id;

    /**
     * 大华设备编号
     */
    @Excel(name = "大华设备编号")
    private Long dahuaDeviceId;

    /**
     * 抓图路径
     */
    @Excel(name = "抓图路径")
    private String image;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDahuaDeviceId() {
        return dahuaDeviceId;
    }

    public void setDahuaDeviceId(Long dahuaDeviceId) {
        this.dahuaDeviceId = dahuaDeviceId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "DahuaDeviceScreenshot{" +
                "id=" + id +
                ", dahuaDeviceId=" + dahuaDeviceId +
                ", image='" + image + '\'' +
                '}';
    }
}
