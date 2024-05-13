package com.kieran.mallchat.common.common.service;

import com.kieran.mallchat.common.common.exception.BusinessException;
import com.kieran.mallchat.common.common.exception.ExceptionErrorNum;
import lombok.SneakyThrows;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Service
public class LockService {

    @Resource
    private RedissonClient redissonClient;

    /**
     * 优化1
     * function 既有出参数又有入参
     * supplier 只有出参没有入参
     */
    @SneakyThrows
    public <T> T executeWithLock(String key, long waitTime, TimeUnit timeUnit, Supplier<T> supplier) {
        RLock lock = redissonClient.getLock(key);
        boolean success = lock.tryLock(waitTime, timeUnit);
        if (!success) {
            throw new BusinessException(ExceptionErrorNum.LOCK_LIMIT);
        }
        try {
            // 执行锁内代码逻辑mallchat
            return supplier.get();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 优化2，优化掉【等待上锁时间】
     */
    public <T> T executeWithLock(String key, Supplier<T> supplier) {
        return executeWithLock(key, -1, TimeUnit.MILLISECONDS, supplier);
    }

    /**
     * 优化3，优化掉 supplier的return
     */
    public <T> T executeWithLock(String key, Runnable runnable) {
        return executeWithLock(key, -1, TimeUnit.MILLISECONDS, () -> {
            runnable.run();
            return null;
        });
    }

    /**
     * 自带的supplier不抛异常，这里需要重写一份
     */
    @FunctionalInterface
    public interface Supplier<T> {

        /**
         * Gets a result.
         *
         * @return a result
         */
        T get() throws Throwable;
    }

}
