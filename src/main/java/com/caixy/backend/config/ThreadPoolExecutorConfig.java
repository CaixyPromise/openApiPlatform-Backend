package com.caixy.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.NotNull;
import java.util.concurrent.*;

/**
 * 线程池配置
 *
 * @name: com.caixy.backend.config.ThreadPoolExecutorConfig
 * @author: CAIXYPROMISE
 * @since: 2024-01-09 19:36
 **/
@Configuration
public class ThreadPoolExecutorConfig
{
    private final int corePoolSize = 10;
    private final int maximumPoolSize = 20;
    private final long keepAliveTime = 60;
    private final TimeUnit unit = TimeUnit.SECONDS;
    private final int queueCapacity = 50;
    private final ArrayBlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(queueCapacity);

    @Bean
    public ThreadPoolExecutor threadPoolExecutor()
    {
        ThreadFactory threadFactory = new ThreadFactory()
        {
            private int count = 0;

            @Override
            public Thread newThread(@NotNull Runnable r)
            {
                Thread thread = new Thread(r);
                thread.setName("thread-" + count++);
                return thread;
            }
        };
        return new ThreadPoolExecutor(
                corePoolSize,
                maximumPoolSize,
                keepAliveTime,
                unit,
                workQueue,
                threadFactory,
                new ThreadPoolExecutor.AbortPolicy()
        );
    }
}
