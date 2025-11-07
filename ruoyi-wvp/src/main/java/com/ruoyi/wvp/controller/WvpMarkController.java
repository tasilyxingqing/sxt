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
import com.ruoyi.wvp.service.bean.WvpMark;
import com.ruoyi.wvp.service.IWvpMarkService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * wvp通道标记Controller
 *
 * @author 陈江灿
 * @date 2025-08-22
 */
@RestController
@RequestMapping("/wvp/mark")
public class WvpMarkController extends BaseController {
    @Autowired
    private IWvpMarkService wvpMarkService;

    /**
     * 查询wvp通道标记列表
     */
    @PreAuthorize("@ss.hasPermi('wvp:mark:list')")
    @GetMapping("/list")
    public TableDataInfo list(WvpMark wvpMark) {
        startPage();
        List<WvpMark> list = wvpMarkService.selectWvpMarkList(wvpMark);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('wvp:mark:list')")
    @GetMapping("/listAll")
    public AjaxResult listAll(WvpMark wvpMark) {
        List<WvpMark> list = wvpMarkService.selectWvpMarkList(wvpMark);
        return success(list);
    }

    /**
     * 导出wvp通道标记列表
     */
    @PreAuthorize("@ss.hasPermi('wvp:mark:export')")
    @Log(title = "wvp通道标记", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, WvpMark wvpMark) {
        List<WvpMark> list = wvpMarkService.selectWvpMarkList(wvpMark);
        ExcelUtil<WvpMark> util = new ExcelUtil<WvpMark>(WvpMark.class);
        util.exportExcel(response, list, "wvp通道标记数据");
    }

    /**
     * 获取wvp通道标记详细信息
     */
    @PreAuthorize("@ss.hasPermi('wvp:mark:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(wvpMarkService.selectWvpMarkById(id));
    }

    /**
     * 新增wvp通道标记
     */
    @PreAuthorize("@ss.hasPermi('wvp:mark:add')")
    @Log(title = "wvp通道标记", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody WvpMark wvpMark) {
        return toAjax(wvpMarkService.insertWvpMark(wvpMark));
    }

    /**
     * 修改wvp通道标记
     */
    @PreAuthorize("@ss.hasPermi('wvp:mark:edit')")
    @Log(title = "wvp通道标记", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody WvpMark wvpMark) {
        return toAjax(wvpMarkService.updateWvpMark(wvpMark));
    }

    /**
     * 删除wvp通道标记
     */
    @PreAuthorize("@ss.hasPermi('wvp:mark:remove')")
    @Log(title = "wvp通道标记", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(wvpMarkService.deleteWvpMarkByIds(ids));
    }
}
