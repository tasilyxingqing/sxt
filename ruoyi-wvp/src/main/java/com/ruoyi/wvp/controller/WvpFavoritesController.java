package com.ruoyi.wvp.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.wvp.service.bean.WvpFavorites;
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
import com.ruoyi.wvp.service.IWvpFavoritesService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 国标通道收藏Controller
 *
 * @author 陈江灿
 * @date 2025-08-21
 */
@RestController
@RequestMapping("/wvp/favorites")
public class WvpFavoritesController extends BaseController {
    @Autowired
    private IWvpFavoritesService wvpFavoritesService;

    @PreAuthorize("@ss.hasPermi('wvp:favorites:list')")
    @GetMapping("/listAll")
    public AjaxResult listAll(WvpFavorites wvpFavorites) {
        return success(wvpFavoritesService.selectWvpFavoritesList(wvpFavorites));
    }

    /**
     * 查询国标通道收藏列表
     */
    @PreAuthorize("@ss.hasPermi('wvp:favorites:list')")
    @GetMapping("/list")
    public TableDataInfo list(WvpFavorites wvpFavorites) {
        startPage();
        List<WvpFavorites> list = wvpFavoritesService.selectWvpFavoritesList(wvpFavorites);
        return getDataTable(list);
    }

    /**
     * 导出国标通道收藏列表
     */
    @PreAuthorize("@ss.hasPermi('wvp:favorites:export')")
    @Log(title = "国标通道收藏", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, WvpFavorites wvpFavorites) {
        List<WvpFavorites> list = wvpFavoritesService.selectWvpFavoritesList(wvpFavorites);
        ExcelUtil<WvpFavorites> util = new ExcelUtil<WvpFavorites>(WvpFavorites.class);
        util.exportExcel(response, list, "国标通道收藏数据");
    }

    /**
     * 获取国标通道收藏详细信息
     */
    @PreAuthorize("@ss.hasPermi('wvp:favorites:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(wvpFavoritesService.selectWvpFavoritesById(id));
    }

    /**
     * 新增国标通道收藏
     */
    @PreAuthorize("@ss.hasPermi('wvp:favorites:add')")
    @Log(title = "国标通道收藏", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody WvpFavorites wvpFavorites) {
        return toAjax(wvpFavoritesService.insertWvpFavorites(wvpFavorites));
    }

    /**
     * 修改国标通道收藏
     */
    @PreAuthorize("@ss.hasPermi('wvp:favorites:edit')")
    @Log(title = "国标通道收藏", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody WvpFavorites wvpFavorites) {
        return toAjax(wvpFavoritesService.updateWvpFavorites(wvpFavorites));
    }

    /**
     * 删除国标通道收藏
     */
    @PreAuthorize("@ss.hasPermi('wvp:favorites:remove')")
    @Log(title = "国标通道收藏", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(wvpFavoritesService.deleteWvpFavoritesByIds(ids));
    }


}
