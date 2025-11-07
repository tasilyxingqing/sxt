package com.ruoyi.isup.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.isup.domain.IsupDevice;
import com.ruoyi.isup.service.IIsupDeviceService;
import com.ruoyi.isup.service.cmsService.CMS;
import com.ruoyi.isup.service.smsService.SMS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * isup设备Controller
 *
 * @author fengcheng
 * @date 2025-04-22
 */
@RestController
@RequestMapping("/isup/lsupDevice")
public class IsupDeviceController extends BaseController {
    @Autowired
    private IIsupDeviceService isupDeviceService;

    @Autowired
    private CMS cms;


    @Autowired
    private SMS smsService;

    /**
     * 云台控制（开始）
     *
     * @param direction   方向 1-右 2-左 3-上 4-下
     * @param controSpeed 速度
     * @return
     */
    @PreAuthorize("@ss.hasPermi('isup:lsupDevice:ptzCtrl')")
    @GetMapping("/ptzCtrlStart/{id}")
    public AjaxResult ptzCtrlStart(@PathVariable Long id, Integer direction, Integer controSpeed) {
        cms.ptzCtrlStart(id, direction, controSpeed);
        return success();
    }

    /**
     * 云台控制（结束）
     *
     * @return
     */
    @PreAuthorize("@ss.hasPermi('isup:lsupDevice:ptzCtrl')")
    @GetMapping("/ptzCtrlEnd/{id}")
    public AjaxResult ptzCtrlEnd(@PathVariable Long id) {
        cms.ptzCtrlEnd(id);
        return success();
    }

    /**
     * 云台控制（聚焦）
     */
    @PreAuthorize("@ss.hasPermi('isup:lsupDevice:ptzCtrl')")
    @GetMapping("/ptzCtrlFocus/{id}")
    public AjaxResult ptzCtrlFocus(@PathVariable Long id, Integer controSpeed) {
        cms.ptzCtrlFocus(id, controSpeed);
        return success();
    }

    /**
     * 云台控制（3D定位）
     *
     * @return
     */
    @PreAuthorize("@ss.hasPermi('isup:lsupDevice:ptzCtrl')")
    @GetMapping("/ptz3dPositioningControl/{id}")
    public AjaxResult ptz3dPositioningControl(@PathVariable Long id, Integer startx, Integer starty, Integer endx, Integer endy) {
        cms.ptz3dPositioningControl(id, startx, starty, endx, endy);
        return success();
    }

    /**
     * 获取单一预置点参数
     *
     * @return
     */
    @GetMapping("/retrievePresetPoint/{id}")
    public AjaxResult retrievePresetPoint(@PathVariable Long id, Integer presetId) {
        cms.retrievePresetPoint(id, presetId);
        return success();
    }

    /**
     * 配置单一预置点参数
     *
     * @return
     */
    @GetMapping("/configurePresetPoint/{id}")
    public AjaxResult configurePresetPoint(@PathVariable Long id, Integer presetId, String presetName, boolean enabled) {
        cms.configurePresetPoint(id, presetId, presetName, enabled);
        return success();
    }

    /**
     * 删除单一预置点参数
     *
     * @return
     */
    @GetMapping("/deletePresetPointConfiguration/{id}")
    public AjaxResult deletePresetPointConfiguration(@PathVariable Long id, Integer presetId) {
        cms.deletePresetPointConfiguration(id, presetId);
        return success();
    }

    /**
     * 配置单一预置点控制
     *
     * @return
     */
    @GetMapping("/gotoPresetPoint/{id}")
    public AjaxResult gotoPresetPoint(@PathVariable Long id, Integer presetId) {
        cms.gotoPresetPoint(id, presetId);
        return success();
    }

    /**
     * 获取所有数字通道状态
     *
     * @return
     */
    @GetMapping("/getAllDigitalChannelStatus/{id}")
    public AjaxResult getAllDigitalChannelStatus(@PathVariable Long id) {
        cms.getAllDigitalChannelStatus(id);
        return success();
    }

    /**
     * 配置单个数字通道接入参数
     *
     * @return
     */
    @GetMapping("/configureDigitalChannel/{id}")
    public AjaxResult configureDigitalChannel(@PathVariable Long id) {
        cms.configureDigitalChannel(id);
        return success();
    }


    /**
     * 查询isup设备列表
     */
    @PreAuthorize("@ss.hasPermi('isup:lsupDevice:list')")
    @GetMapping("/list")
    public TableDataInfo list(IsupDevice isupDevice) {
        startPage();
        List<IsupDevice> list = isupDeviceService.selectIsupDeviceList(isupDevice);
        return getDataTable(list);
    }

    /**
     * 查询isup设备列表
     */
    @PreAuthorize("@ss.hasPermi('isup:lsupDevice:list')")
    @GetMapping("/lsupDeviceList")
    public AjaxResult lsupDeviceList(IsupDevice isupDevice, String mapType) {
        List<IsupDevice> list = new ArrayList<>();
        if ("map".equals(mapType)) {
            list = isupDeviceService.selectIsupDeviceListMap(isupDevice);
        } else {
            list = isupDeviceService.selectIsupDeviceList(isupDevice);
        }
        return success(list);
    }

    /**
     * 获取isup设备详细信息
     */
    @PreAuthorize("@ss.hasPermi('isup:lsupDevice:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(isupDeviceService.selectIsupDeviceById(id));
    }

    /**
     * 播放
     * @param luserId 登录句柄
     * @return
     */
    @PreAuthorize("@ss.hasPermi('isup:lsupDevice:start')")
    @GetMapping(value = "/start/{luserId}")
    public AjaxResult start(@PathVariable("luserId") int luserId) {
        CompletableFuture<String> completableFuture = new CompletableFuture<>();
        smsService.RealPlay(luserId, completableFuture);
        try {
            String result = completableFuture.get();
            if(Objects.equals(result, "true")){
                return AjaxResult.success();
            }
            return AjaxResult.error();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 停止播放
     * @param luserId 登录句柄
     * @return
     */
    @PreAuthorize("@ss.hasPermi('isup:lsupDevice:start')")
    @GetMapping(value = "/stopRealPlay/{luserId}")
    public AjaxResult stopRealPlay(@PathVariable("luserId") int luserId) {
        Integer i = SMS.LuserIDandSessionMap.get(luserId);
        smsService.StopRealPlay(luserId, i, SMS.SessionIDAndPreviewHandleMap.get(i));
        return success();
    }



    /**
     * 修改isup设备
     */
    @PreAuthorize("@ss.hasPermi('isup:lsupDevice:edit')")
    @Log(title = "isup设备", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody IsupDevice isupDevice) {
        return toAjax(isupDeviceService.updateIsupDevice(isupDevice));
    }

    /**
     * 删除isup设备
     */
    @PreAuthorize("@ss.hasPermi('isup:lsupDevice:remove')")
    @Log(title = "isup设备", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(isupDeviceService.deleteIsupDeviceByIds(ids));
    }
}
