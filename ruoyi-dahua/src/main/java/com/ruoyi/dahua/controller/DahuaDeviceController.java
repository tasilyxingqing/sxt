package com.ruoyi.dahua.controller;

import cn.hutool.core.util.ObjUtil;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.dahua.domain.DahuaDevice;
import com.ruoyi.dahua.domain.DahuaDeviceScreenshot;
import com.ruoyi.dahua.runner.DahuaCommandLineRunnerImpl;
import com.ruoyi.dahua.service.IDahuaDeviceScreenshotService;
import com.ruoyi.dahua.service.IDahuaDeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 大华设备Controller
 *
 * @author fengcheng
 * @date 2025-06-06
 */
@RestController
@RequestMapping("/dahua/device")
public class DahuaDeviceController extends BaseController {
    @Autowired
    private IDahuaDeviceService dahuaDeviceService;

    @Autowired
    private DahuaCommandLineRunnerImpl commandLineRunnerimpl;

    @Autowired
    private IDahuaDeviceScreenshotService dahuaDeviceScreenshotService;

    /**
     * 查询大华设备列表（分页）
     */
    @PreAuthorize("@ss.hasPermi('dahua:device:list')")
    @GetMapping("/list")
    public TableDataInfo list(DahuaDevice dahuaDevice) {
        startPage();
        List<DahuaDevice> list = dahuaDeviceService.selectDahuaDeviceList(dahuaDevice);
        return getDataTable(list);
    }

    /**
     * 查询大华设备列表（不分页）
     */
    @PreAuthorize("@ss.hasPermi('dahua:device:list')")
    @GetMapping("/listDahuaDevice")
    public AjaxResult listDahuaDevice(DahuaDevice dahuaDevice, String mapType) {
        List<DahuaDevice> list = new ArrayList<>();
        if ("map".equals(mapType)) {
            list = dahuaDeviceService.selectDahuaDeviceListMap(dahuaDevice);
        } else {
            list = dahuaDeviceService.selectDahuaDeviceList(dahuaDevice);
        }

        return success(list);
    }

    /**
     * 导出大华设备列表
     */
    @PreAuthorize("@ss.hasPermi('dahua:device:export')")
    @Log(title = "大华设备", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DahuaDevice dahuaDevice) {
        List<DahuaDevice> list = dahuaDeviceService.selectDahuaDeviceList(dahuaDevice);
        ExcelUtil<DahuaDevice> util = new ExcelUtil<DahuaDevice>(DahuaDevice.class);
        util.exportExcel(response, list, "大华设备数据");
    }

    /**
     * 获取大华设备详细信息
     */
    @PreAuthorize("@ss.hasPermi('dahua:device:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(dahuaDeviceService.selectDahuaDeviceById(id));
    }

    /**
     * 新增大华设备
     */
    @PreAuthorize("@ss.hasPermi('dahua:device:add')")
    @Log(title = "大华设备", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DahuaDevice dahuaDevice) {
        return toAjax(dahuaDeviceService.insertDahuaDevice(dahuaDevice));
    }

    /**
     * 修改大华设备
     */
    @PreAuthorize("@ss.hasPermi('dahua:device:edit')")
    @Log(title = "大华设备", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DahuaDevice dahuaDevice) {
        return toAjax(dahuaDeviceService.updateDahuaDevice(dahuaDevice));
    }

    /**
     * 删除大华设备
     */
    @PreAuthorize("@ss.hasPermi('dahua:device:remove')")
    @Log(title = "大华设备", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(dahuaDeviceService.deleteDahuaDeviceByIds(ids));
    }

    /**
     * 大华设备登录
     */
    @PreAuthorize("@ss.hasPermi('dahua:device:login')")
    @Log(title = "大华设备", businessType = BusinessType.INSERT)
    @PostMapping("/login")
    public AjaxResult login(@RequestBody DahuaDevice dahuaDevice) {
        return success(dahuaDeviceService.login(dahuaDevice));
    }

    /**
     * 获取自动注册设备列表
     */
    @PreAuthorize("@ss.hasPermi('dahua:device:add')")
    @GetMapping("/getRegisterDeviceList")
    public AjaxResult getRegisterDeviceList() {
        // 自动注册设备列表
        List<DahuaDevice> registerDeviceList = commandLineRunnerimpl.getRegisterDeviceList();

        // 设备列表
        List<DahuaDevice> list = dahuaDeviceService.selectDahuaDeviceList(new DahuaDevice());

        return success(filterDevicesByIp(registerDeviceList, list));
    }

    /**
     * 删除自动注册设备列表
     */
    @PreAuthorize("@ss.hasPermi('dahua:device:remove')")
    @DeleteMapping("/delRegisterDevice/{ips}")
    public AjaxResult delRegisterDevice(@PathVariable String[] ips) {
        commandLineRunnerimpl.delRegisterDevice(ips);
        return success();
    }

    /**
     * 过滤设备
     *
     * @param registerDeviceList
     * @param list
     * @return
     */
    public List<DahuaDevice> filterDevicesByIp(
            List<DahuaDevice> registerDeviceList,
            List<DahuaDevice> list) {
        // 提取 list 中所有的 ip，用于判断是否已存在
        Set<String> existingIps = list.stream()
                .map(DahuaDevice::getIp)
                .collect(Collectors.toSet());
        // 过滤 registerDeviceList：只保留那些 ip 不在 existingIps 中的设备
        return registerDeviceList.stream()
                .filter(device -> !existingIps.contains(device.getIp()))
                .collect(Collectors.toList());
    }


    /**
     * 大华设备实时预览
     */
    @Anonymous
    @GetMapping("/startRealPlay/{id}")
    public AjaxResult startRealPlay(@PathVariable Long id) {
        return success(dahuaDeviceService.startRealPlay(id));
    }

    /**
     * 大华设备停止预览
     */
    @Anonymous
    @GetMapping("/stopRealPlay/{id}")
    public AjaxResult stopRealPlay(@PathVariable Long id) {
        return success(dahuaDeviceService.stopRealPlay(id));
    }

    /**
     * 大华设备云台控制（开始）
     */

    @PreAuthorize("@ss.hasPermi('dahua:device:ptzCtrl')")
    @GetMapping("/ptzControlUpStart/{id}")
    public AjaxResult ptzControlUpStart(@PathVariable Long id, String direction, Integer speed) {
        if (StringUtils.isEmpty(direction)) {
            direction = "up";
        }
        if (ObjUtil.isNull(speed)) {
            speed = 5;
        }
        return success(dahuaDeviceService.ptzControlStart(direction, id, speed));
    }

    /**
     * 大华设备云台控制（停止）
     */
    @PreAuthorize("@ss.hasPermi('dahua:device:ptzCtrl')")
    @GetMapping("/ptzControlUpEnd/{id}")
    public AjaxResult ptzControlUpEnd(String direction, @PathVariable Long id) {
        if (StringUtils.isEmpty(direction)) {
            direction = "up";
        }
        return success(dahuaDeviceService.ptzControlUpEnd(direction, id));
    }

    /**
     * 查询大华设备抓图列表
     */
    @PreAuthorize("@ss.hasPermi('dahua:device:listScreenshot')")
    @GetMapping("/listScreenshot")
    public TableDataInfo list(DahuaDeviceScreenshot dahuaDeviceScreenshot) {
        startPage();
        List<DahuaDeviceScreenshot> list = dahuaDeviceScreenshotService.selectDahuaDeviceScreenshotList(dahuaDeviceScreenshot);
        return getDataTable(list);
    }

    /**
     * 删除大华设备抓图
     */
    @PreAuthorize("@ss.hasPermi('dahua:device:removeScreenshot')")
    @Log(title = "大华设备抓图", businessType = BusinessType.DELETE)
    @DeleteMapping("/removeScreenshot/{ids}")
    public AjaxResult removeScreenshot(@PathVariable Long[] ids) {
        return toAjax(dahuaDeviceScreenshotService.deleteDahuaDeviceScreenshotByIds(ids));
    }

    /**
     * 大华设备抓图
     */
    @PreAuthorize("@ss.hasPermi('dahua:device:snapPicture')")
    @GetMapping("/snapPicture/{id}")
    public AjaxResult snapPicture(@PathVariable Long id) {
        return success(dahuaDeviceService.snapPicture(id));
    }

    /**
     * 大华设备定时抓图
     */
    @PreAuthorize("@ss.hasPermi('dahua:device:timerCapturePicture')")
    @GetMapping("/timerCapturePicture/{id}")
    public AjaxResult timerCapturePicture(@PathVariable Long id) {
        return success(dahuaDeviceService.timerCapturePicture(id));
    }

    /**
     * 大华设备停止定时抓图
     */
    @PreAuthorize("@ss.hasPermi('dahua:device:stopCapturePicture')")
    @GetMapping("/stopCapturePicture/{id}")
    public AjaxResult stopCapturePicture(@PathVariable Long id) {
        return success(dahuaDeviceService.stopCapturePicture(id));
    }

    /**
     * 大华设备获取时间
     */
    @PreAuthorize("@ss.hasPermi('dahua:device:control')")
    @GetMapping("/getTime/{id}")
    public AjaxResult getTime(@PathVariable Long id) {
        return success(dahuaDeviceService.getTime(id));
    }

    /**
     * 大华设备设置时间
     */
    @PreAuthorize("@ss.hasPermi('dahua:device:control')")
    @GetMapping("/setTime/{id}")
    public AjaxResult setTime(@PathVariable Long id, String date, boolean type) {
        return success(dahuaDeviceService.setTime(id, date, type));
    }

    /**
     * 大华设备重启
     */
    @PreAuthorize("@ss.hasPermi('dahua:device:control')")
    @GetMapping("/reboot/{id}")
    public AjaxResult reboot(@PathVariable Long id) {
        return success(dahuaDeviceService.reboot(id));
    }
}
