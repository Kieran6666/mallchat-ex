package com.kieran.mallchat.common.common.aspect;

import cn.hutool.core.util.StrUtil;
import com.kieran.mallchat.common.common.annotation.RedissonLock;
import com.kieran.mallchat.common.common.service.LockService;
import com.kieran.mallchat.common.common.utils.SpElUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;

@Aspect
@Component
@Order(0) // 确保比事务注解先执行，分布式锁在事务外
public class RedissonLockAspect {

    @Resource
    private LockService lockService;

//    /**
//     * 第一种写法
//     * @param joinPoint
//     * @return
//     * @throws Throwable
//     */
//    @Around("@annotation(com.kieran.mallchat.common.common.annotation.RedissonLock)")
//    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
//        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
//        RedissonLock redissonLock = method.getAnnotation(RedissonLock.class);
//        // 写法通用
//    }

    @Around("@annotation(redissonLock)")
    public Object around(ProceedingJoinPoint joinPoint, RedissonLock redissonLock) throws Throwable {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        String prefix = StrUtil.isBlank(redissonLock.prefixKey()) ? SpElUtils.getMethodKey(method)
                : redissonLock.prefixKey();
        String key = SpElUtils.parseSpEl(method, joinPoint.getArgs(), redissonLock.key());
        return lockService.executeWithLock(prefix + ":" + key, redissonLock.waitTime(), redissonLock.timeUnit(),
                joinPoint::proceed);
    }
}
