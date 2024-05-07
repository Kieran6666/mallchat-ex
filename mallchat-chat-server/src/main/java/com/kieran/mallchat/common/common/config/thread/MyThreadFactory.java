package com.kieran.mallchat.common.common.config.thread;


import lombok.AllArgsConstructor;

import java.util.concurrent.ThreadFactory;

/**
 * 装饰器模式
 *
 * 保证原有ThreadFactory功能不变的前提下，给ThreadFactory加上异常日志打印
 */
@AllArgsConstructor
public class MyThreadFactory implements ThreadFactory {

    private static final MyUncaughtExceptionHandler MY_UNCAUGHT_EXCEPTION_HANDLER = new MyUncaughtExceptionHandler();

    // 使用的是 组合， 把它设置为成员变量，进行设置
    private ThreadFactory origin;

    /**
     *
     * @param r
     * @return
     */
    @Override
    public Thread newThread(Runnable r) {
        Thread thread = origin.newThread(r);
        thread.setUncaughtExceptionHandler(MY_UNCAUGHT_EXCEPTION_HANDLER);
        return thread;
    }
}
