package com.ruoyi.wvp.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.wvp.service.bean.WvpFavoritesChannel;
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
import com.ruoyi.wvp.service.IWvpFavoritesChannelService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 收藏夹国标通道Controller
 *
 * @author 陈江灿
 * @date 2025-08-21
 */
@RestController
@RequestMapping("/wvp/favoritesChannel")
public class WvpFavoritesChannelController extends BaseController {
    @Autowired
    private IWvpFavoritesChannelService wvpFavoritesChannelService;

    /**
     * 查询收藏夹国标通道列表
     */
    @PreAuthorize("@ss.hasPermi('wvp:favoritesChannel:list')")
    @GetMapping("/list")
    public TableDataInfo list(WvpFavoritesChannel wvpFavoritesChannel) {
        startPage();
        List<WvpFavoritesChannel> list = wvpFavoritesChannelService.selectWvpFavoritesChannelList(wvpFavoritesChannel);
        return getDataTable(list);
    }

    /**
     * 导出收藏夹国标通道列表
     */
    @PreAuthorize("@ss.hasPermi('wvp:favoritesChannel:export')")
    @Log(title = "收藏夹国标通道", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, WvpFavoritesChannel wvpFavoritesChannel) {
        List<WvpFavoritesChannel> list = wvpFavoritesChannelService.selectWvpFavoritesChannelList(wvpFavoritesChannel);
        ExcelUtil<WvpFavoritesChannel> util = new ExcelUtil<WvpFavoritesChannel>(WvpFavoritesChannel.class);
        util.exportExcel(response, list, "收藏夹国标通道数据");
    }

    /**
     * 获取收藏夹国标通道详细信息
     */
    @PreAuthorize("@ss.hasPermi('wvp:favoritesChannel:query')")
    @GetMapping(value = "/{favoritesId}")
    public AjaxResult getInfo(@PathVariable("favoritesId") Long favoritesId) {
        return success(wvpFavoritesChannelService.selectWvpFavoritesChannelByFavoritesId(favoritesId));
    }

    /**
     * 新增收藏夹国标通道
     */
    @PreAuthorize("@ss.hasPermi('wvp:favoritesChannel:add')")
    @Log(title = "收藏夹国标通道", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody WvpFavoritesChannel wvpFavoritesChannel) {
        return toAjax(wvpFavoritesChannelService.insertWvpFavoritesChannel(wvpFavoritesChannel));
    }

    /**
     * 修改收藏夹国标通道
     */
    @PreAuthorize("@ss.hasPermi('wvp:favoritesChannel:edit')")
    @Log(title = "收藏夹国标通道", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody WvpFavoritesChannel wvpFavoritesChannel) {
        return toAjax(wvpFavoritesChannelService.updateWvpFavoritesChannel(wvpFavoritesChannel));
    }

    /**
     * 删除收藏夹国标通道
     */
    @PreAuthorize("@ss.hasPermi('wvp:favoritesChannel:remove')")
    @Log(title = "收藏夹国标通道", businessType = BusinessType.DELETE)
    @DeleteMapping("/{favoritesIds}")
    public AjaxResult remove(@PathVariable Long[] favoritesIds) {
        return toAjax(wvpFavoritesChannelService.deleteWvpFavoritesChannelByFavoritesIds(favoritesIds));
    }
}
