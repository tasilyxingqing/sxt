package com.ruoyi.web.controller.rtsp;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.rtsp.service.IRtspStreamPushService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: 陈江灿
 * @CreateTime: 2025-06-10
 */
@RestController
@RequestMapping("/rtsp/test")
public class RtspTestController {

    @Autowired
    private IRtspStreamPushService service;

    @Anonymous
    @GetMapping("/start")
    public boolean start() {
        String rtmpUrl = "rtmp://45.207.208.187:1935/live/start?sign=41db35390ddad33f83944f44b8b75ded";
        return service.startPush("start",
                "rtsp://admin:hx147258@192.168.158.189:554/Streaming/Channels/102",
                rtmpUrl);
    }

    @Anonymous
    @PostMapping("/ai")
    public void ai(@RequestBody Object o) {






        System.out.println(o);
    }


}
