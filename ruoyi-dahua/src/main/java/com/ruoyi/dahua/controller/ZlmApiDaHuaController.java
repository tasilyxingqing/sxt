package com.ruoyi.dahua.controller;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.dahua.domain.bo.ProxyPlayBo;
import com.ruoyi.dahua.zlmApi.ZlmProxyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dahua/zlmApi")
public class ZlmApiDaHuaController {

    @Autowired
    private ZlmProxyService service;

    /**
     * 代理播放
     * @param bo
     * @return
     */
    @PreAuthorize("@ss.hasPermi('dahua:zlmApi:play')")
    @PostMapping("/proxyPlay")
    public AjaxResult proxyPlay(@RequestBody ProxyPlayBo bo){
        return AjaxResult.success(service.startProxy(
                bo.getId(),
                bo.getUrl(),
                "dahua",
                bo.getId()
        ));
    }

    @PreAuthorize("@ss.hasPermi('dahua:zlmApi:stop')")
    @GetMapping("/stopProxy/{id}")
    public AjaxResult stopProxy(@PathVariable("id") String id){
        service.stopProxy(id);
        return AjaxResult.success();
    }
}
