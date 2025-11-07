package com.ruoyi.web.controller.onvif;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.onvif.domain.FetchMainAndSubStreamUris;
import com.ruoyi.onvif.domain.bo.AbsoluteMoveBo;
import com.ruoyi.onvif.domain.bo.FetchMainAndSubStreamUrisBo;
import com.ruoyi.onvif.domain.bo.OnvifPZT;
import com.ruoyi.onvif.domain.bo.PresetsBo;
import com.ruoyi.onvif.service.IOnvifService;
import com.ruoyi.onvif.utils.OnvifUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * onvif 控制器
 *
 * @Author: 陈江灿
 * @CreateTime: 2025-04-09
 */
@RestController
@RequestMapping("/onvif/service")
public class OnvifServiceController extends BaseController {

    @Autowired
    private IOnvifService service;



    /**
     * 获取数字通道
     *
     * @return
     */
    @GetMapping("/getDigitalChannel")
    public AjaxResult getDigitalChannel(FetchMainAndSubStreamUrisBo bo) {
        return success(OnvifUtil.getDigitalChannel(bo));
    }

    /**
     * 获取onvif设备信息
     *
     * @return
     */
    @PreAuthorize("@ss.hasPermi('onvif:service:getInfo')")
    @GetMapping("/getInfo")
    public AjaxResult getOnvifDeviceInfo(FetchMainAndSubStreamUrisBo bo) {
        FetchMainAndSubStreamUris fetchMainAndSubStreamUris = service.getOnvifDeviceInfo(bo);
        return success(fetchMainAndSubStreamUris);
    }

    /**
     * 获取通道token
     */
    @PreAuthorize("@ss.hasPermi('onvif:service:getChannelToken')")
    @GetMapping("/getChannelToken")
    public AjaxResult getChannelToken(FetchMainAndSubStreamUrisBo bo) {
        return success(service.getChannelToken(bo));
    }

//    /**
//     * 绝对位置移动
//     *
//     * @param bo
//     * @return
//     */
//    @PreAuthorize("@ss.hasPermi('onvif:service:absoluteMove')")
//    @GetMapping("/absoluteMove")
//    public AjaxResult absoluteMove(AbsoluteMoveBo bo) {
//        return success(service.generateAbsoluteMove(bo));
//    }

    /**
     * 云台开始
     *
     * @param onvifPZT
     * @return
     */
    @PreAuthorize("@ss.hasPermi('onvif:service:continuousMove')")
    @GetMapping("/onvifPZTStart")
    public AjaxResult onvifPZTStart(OnvifPZT onvifPZT) {
        service.onvifPZTStart(onvifPZT);
        return success();
    }

    /**
     * 云台结束
     *
     * @param onvifPZT
     * @return
     */
    @PreAuthorize("@ss.hasPermi('onvif:service:continuousMoveStop')")
    @GetMapping("/onvifPZTEnd")
    public AjaxResult onvifPZTEnd(OnvifPZT onvifPZT) {
        service.onvifPZTEnd(onvifPZT);
        return success();
    }

    /**
     * 获取预置点列表
     *
     * @param bo
     * @return
     */
    @PreAuthorize("@ss.hasPermi('onvif:service:getPresets')")
    @GetMapping("/getPresets")
    public AjaxResult getPresets(AbsoluteMoveBo bo) {
        return success(service.getPresetList(bo));
    }


    /**
     * goto预置点
     *
     * @param bo
     * @return
     */
    @PreAuthorize("@ss.hasPermi('onvif:service:gotoPreset')")
    @GetMapping("/gotoPreset")
    public AjaxResult gotoPreset(PresetsBo bo) {
        service.gotoPreset(bo);
        return success();
    }

    /**
     * 删除预置点
     *
     * @param bo
     * @return
     */
    @PreAuthorize("@ss.hasPermi('onvif:service:removePreset')")
    @GetMapping("/removePreset")
    public AjaxResult removePreset(PresetsBo bo) {
        service.removePreset(bo);
        return success();
    }

    /**
     * 添加预置点
     *
     * @param bo
     * @return
     */
    @PreAuthorize("@ss.hasPermi('onvif:service:addPreset')")
    @GetMapping("/addPreset")
    public AjaxResult addPreset(PresetsBo bo) {
        service.addPreset(bo);
        return success();
    }

}
