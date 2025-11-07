package com.ruoyi.onvif.service;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.onvif.domain.FetchMainAndSubStreamUris;
import com.ruoyi.onvif.domain.bo.AbsoluteMoveBo;
import com.ruoyi.onvif.domain.bo.FetchMainAndSubStreamUrisBo;
import com.ruoyi.onvif.domain.bo.OnvifPZT;
import com.ruoyi.onvif.domain.bo.PresetsBo;

import java.util.List;
import java.util.Map;

/**
 * onvif 接口类
 *
 * @Author: 陈江灿
 * @CreateTime: 2025-04-21
 */
public interface IOnvifService {

    /**
     * 获取设备信息
     */
    FetchMainAndSubStreamUris getOnvifDeviceInfo(FetchMainAndSubStreamUrisBo bo);

    /**
     * 获取通道token
     */
    List<Map<String, String>> getChannelToken(FetchMainAndSubStreamUrisBo bo);

    /**
     * 绝对位置云台
     * @param bo
     * @return
     */
    R generateAbsoluteMove(AbsoluteMoveBo bo);

    /**
     *
     * @param bo
     * @return
     */
    R generateContinuousMove(AbsoluteMoveBo bo);

    /**
     * 连续移动停止
     * @param bo
     * @return
     */
    R continuousMoveStop(AbsoluteMoveBo bo);

    /**
     * 获取预置点列表
     * @param bo
     * @return
     */
    List<Map<String, String>> getPresetList(AbsoluteMoveBo bo);

    /**
     * goto预置点
     * @param bo
     */
    void gotoPreset(PresetsBo bo);

    /**
     * 删除预置点
     * @param bo
     */
    void removePreset(PresetsBo bo);

    /**
     * 添加预置点
     * @param bo
     */
    void addPreset(PresetsBo bo);

    /**
     * 云台开始
     *
     * @param onvifPZT
     */
    void onvifPZTStart(OnvifPZT onvifPZT);

    /**
     * 云台结束
     *
     * @param onvifPZT
     */
    void onvifPZTEnd(OnvifPZT onvifPZT);
}
