package com.ruoyi.rtsp.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 线程池管理
 *
 * @Author: 陈江灿
 * @CreateTime: 2025-04-28
 */
public class StreamThreadPool {
    private static final int THREAD_POOL_SIZE = 5;
    private static ExecutorService executorService;

    static {
        executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    }

    /**
     * 提交推流任务到线程池
     *
     * @param task 推流任务
     */
    public static void submitTask(Runnable task) {
        executorService.submit(task);
    }

    /**
     * 关闭线程池
     */
    public static void shutdown() {
        if (!executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}
