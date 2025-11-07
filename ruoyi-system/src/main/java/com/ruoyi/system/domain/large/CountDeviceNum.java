package com.ruoyi.system.domain.large;

/**
 * @FileName CountDeviceNum
 * @Description
 * @Author fengcheng
 * @date 2025-05-01
 **/
public class CountDeviceNum {

    /**
     * 国标设备数量
     */
    private Integer totalGbNum;

    /**
     * ISUP设备数量
     */
    private Integer totalIsupNum;

    /**
     * ONVIF设备数量
     */
    private Integer totalRtspNum;

    /**
     * ONVIF设备数量
     */
    private Integer totalOnvifNum;

    /**
     * 大华设备数量
     */
    private Integer totalDahuaNum;

    public Integer getTotalGbNum() {
        return totalGbNum;
    }

    public void setTotalGbNum(Integer totalGbNum) {
        this.totalGbNum = totalGbNum;
    }

    public Integer getTotalIsupNum() {
        return totalIsupNum;
    }

    public void setTotalIsupNum(Integer totalIsupNum) {
        this.totalIsupNum = totalIsupNum;
    }

    public Integer getTotalRtspNum() {
        return totalRtspNum;
    }

    public void setTotalRtspNum(Integer totalRtspNum) {
        this.totalRtspNum = totalRtspNum;
    }

    public Integer getTotalOnvifNum() {
        return totalOnvifNum;
    }

    public void setTotalOnvifNum(Integer totalOnvifNum) {
        this.totalOnvifNum = totalOnvifNum;
    }

    public Integer getTotalDahuaNum() {
        return totalDahuaNum;
    }

    public void setTotalDahuaNum(Integer totalDahuaNum) {
        this.totalDahuaNum = totalDahuaNum;
    }

    @Override
    public String toString() {
        return "CountDeviceNum{" +
                "totalGbNum=" + totalGbNum +
                ", totalIsupNum=" + totalIsupNum +
                ", totalRtspNum=" + totalRtspNum +
                ", totalOnvifNum=" + totalOnvifNum +
                ", totalDahuaNum=" + totalDahuaNum +
                '}';
    }
}
