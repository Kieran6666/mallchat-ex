package com.kieran.mallchat.common.common.config.thread;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;


/**
 * 继承 AsyncConfigurer ，可以重写@Async注解的默认调用
 */
@Configuration
@EnableAsync
public class ThreadPoolConfig implements AsyncConfigurer {

    /**
     * # @Async的重写方法
     */
    @Override
    public Executor getAsyncExecutor() {
        return mallChatExecutor();
    }

    public static final String MALLCHAT_EXECUTOR = "mallchatExecutor";


    @Primary
    @Bean(MALLCHAT_EXECUTOR)
    public static ThreadPoolTaskExecutor mallChatExecutor() {

        // Executors使用的是无界队列，有oom的风险，禁止使用
//        Executors.newScheduledThreadPool();

        int coreNum = Runtime.getRuntime().availableProcessors();

        // 这个是Spring的线程池，用spring的理由是包含jvm的shutdownHook，可以优雅停机
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();



        executor.setCorePoolSize(coreNum);
        executor.setMaxPoolSize(coreNum * 2);
        executor.setQueueCapacity(200);
        executor.setThreadNamePrefix("mallchat-executor");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        // 根据源码，发现的重点配置，关注DisposableBean的destroy方法
        executor.setWaitForTasksToCompleteOnShutdown(true); // 默认是false，如果不设置为true 优雅停机就无效
        executor.setAwaitTerminationSeconds(30); // 线程30秒超时未关闭，则强制关闭

        // 装饰器模式，给子线程加上日志打印
        executor.setThreadFactory(new MyThreadFactory(executor));

        executor.initialize();
        return executor;


    }


}
