package com.ruoyi.wvp.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.wvp.service.bean.WvpMarkChannel;
import com.ruoyi.wvp.service.IWvpMarkChannelService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * wvp国标通道标记Controller
 *
 * @author 陈江灿
 * @date 2025-08-22
 */
@RestController
@RequestMapping("/wvp/wvpMarkChannel")
public class WvpMarkChannelController extends BaseController {
    @Autowired
    private IWvpMarkChannelService wvpMarkChannelService;


    /**
     * 查询wvp国标通道标记列表
     */
    @PreAuthorize("@ss.hasPermi('wvp:wvpMarkChannel:list')")
    @GetMapping("/list")
    public TableDataInfo list(WvpMarkChannel wvpMarkChannel) {
        startPage();
        List<WvpMarkChannel> list = wvpMarkChannelService.selectWvpMarkChannelList(wvpMarkChannel);
        return getDataTable(list);
    }

    /**
     * 导出wvp国标通道标记列表
     */
    @PreAuthorize("@ss.hasPermi('wvp:wvpMarkChannel:export')")
    @Log(title = "wvp国标通道标记", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, WvpMarkChannel wvpMarkChannel) {
        List<WvpMarkChannel> list = wvpMarkChannelService.selectWvpMarkChannelList(wvpMarkChannel);
        ExcelUtil<WvpMarkChannel> util = new ExcelUtil<WvpMarkChannel>(WvpMarkChannel.class);
        util.exportExcel(response, list, "wvp国标通道标记数据");
    }

    /**
     * 获取wvp国标通道标记详细信息
     */
    @PreAuthorize("@ss.hasPermi('wvp:wvpMarkChannel:query')")
    @GetMapping(value = "/{channelId}")
    public AjaxResult getInfo(@PathVariable("channelId") Long channelId) {
        return success(wvpMarkChannelService.selectWvpMarkChannelByChannelId(channelId));
    }

    /**
     * 新增wvp国标通道标记
     */
    @PreAuthorize("@ss.hasPermi('wvp:wvpMarkChannel:add')")
    @Log(title = "wvp国标通道标记", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody WvpMarkChannel wvpMarkChannel) {
        return toAjax(wvpMarkChannelService.insertWvpMarkChannel(wvpMarkChannel));
    }

    /**
     * 修改wvp国标通道标记
     */
    @PreAuthorize("@ss.hasPermi('wvp:wvpMarkChannel:edit')")
    @Log(title = "wvp国标通道标记", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody WvpMarkChannel wvpMarkChannel) {
        return toAjax(wvpMarkChannelService.updateWvpMarkChannel(wvpMarkChannel));
    }

    /**
     * 删除wvp国标通道标记
     */
    @PreAuthorize("@ss.hasPermi('wvp:wvpMarkChannel:remove')")
    @Log(title = "wvp国标通道标记", businessType = BusinessType.DELETE)
    @DeleteMapping("/{channelIds}")
    public AjaxResult remove(@PathVariable Long[] channelIds) {
        return toAjax(wvpMarkChannelService.deleteWvpMarkChannelByChannelIds(channelIds));
    }
}
