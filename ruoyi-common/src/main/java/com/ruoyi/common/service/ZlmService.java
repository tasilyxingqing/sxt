package com.ruoyi.common.service;

import com.aizuda.zlm4j.core.ZLMApi;
import com.ruoyi.common.config.ZlmApiProperties;
import com.sun.jna.Native;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class ZlmService {

    @Autowired
    private ZlmApiProperties zlmApiProperties;

    public static ZLMApi ZLM_API = Native.load("mk_api", ZLMApi.class);


    private static volatile boolean zlmInitialized = false;


    private static short httpPort, rtspPort, rtmpPort;

    @PostConstruct
    public synchronized void initZlm() {
        if (zlmInitialized) return;
        ZLM_API.mk_env_init2(1, 1, 1, null, 0, 0, null, 0, null, null);
        httpPort = ZLM_API.mk_http_server_start((short) zlmApiProperties.getDhHttpPort(), 0);
        rtspPort = ZLM_API.mk_rtsp_server_start((short) zlmApiProperties.getDhRtspPort(), 0);
        rtmpPort = ZLM_API.mk_rtmp_server_start((short) zlmApiProperties.getDhRtmpPort(), 0);
        System.out.println("推拉流 -- 服务已启动：HTTP=" + httpPort + ", RTSP=" + rtspPort + ", RTMP=" + rtmpPort);
        zlmInitialized = true;
    }

}
