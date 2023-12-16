package com.lzy.serverproject.utils;

import java.util.concurrent.*;

/**
 * 自定义线程池，实现单例模式
 */
public class NewsThreadProcessor {

    private static ExecutorService executorService;

    private NewsThreadProcessor() {
        // 私有构造函数，防止外部实例化
    }

    public static ExecutorService getExecutorService() {
        if (executorService == null) {
            // 双重检查锁定，确保在多线程环境下仍然安全
            synchronized (NewsThreadProcessor.class) {
                if (executorService == null) {
                    // 显式配置线程池参数
                    int corePoolSize = 4;
                    int maxPoolSize = 4;
                    long keepAliveTime = 0L;
                    TimeUnit unit = TimeUnit.MILLISECONDS;
                    BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(4);

                    // 创建ThreadPoolExecutor
                    executorService = new ThreadPoolExecutor(
                            corePoolSize,
                            maxPoolSize,
                            keepAliveTime,
                            unit,
                            workQueue
                    );
                }
            }
        }
        return executorService;
    }
}
