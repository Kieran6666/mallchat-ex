package com.kieran.mallchat.common.user.service.impl;

import com.kieran.mallchat.common.common.annotation.RedissonLock;
import com.kieran.mallchat.common.common.domain.enums.IdempotentEnum;
import com.kieran.mallchat.common.common.domain.enums.YesOrNoEnum;
import com.kieran.mallchat.common.common.service.LockService;
import com.kieran.mallchat.common.user.dao.UserBackpackDao;
import com.kieran.mallchat.common.user.domain.entity.UserBackpack;
import com.kieran.mallchat.common.user.service.UserBackpackService;
import org.springframework.aop.framework.AopContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

@Service
public class UserBackpackServiceImpl implements UserBackpackService {

    @Resource
    private UserBackpackDao userBackpackDao;

    @Resource
    private LockService lockService;

    @Lazy
    @Resource
    private UserBackpackServiceImpl userBackpackServiceImpl;

    @Override
    public void acquireItem(Long uid, Long itemId, IdempotentEnum idempotentEnum, String businessId) {
        String idempotent = getIdempotent(itemId, idempotentEnum, businessId);

        // 这里直接调用acquireItem是无法触发注解上的内容
        // 第一种：需要在本类中注入自己@Resource UserBackpackServiceImpl，但会有循环依赖的问题出行，但可以用@Lazy解决
//        userBackpackServiceImpl.doAcquireItem(uid, itemId, idempotent);

        // 第二种：使用AOP代理
        ((UserBackpackServiceImpl)AopContext.currentProxy()).doAcquireItem(uid, itemId, idempotent);

    }

    /**
     * 发放用户物品，此处需要加锁
     *
     *
     * @param uid 用户ID
     * @param itemId 物品ID
     * @param idempotent 幂等号
     */
    @RedissonLock(key = "#idempotent", waitTime = 5000)
    public void doAcquireItem(Long uid, Long itemId, String idempotent) {
        /**
         * 使用分布式锁注解，就不需要在此处加入分布式锁的编程了
         */
//        // 加锁发放物品
//        lockService.executeWithLock(idempotent, () -> {
//            UserBackpack ifExist = userBackpackDao.getByIdempotent(idempotent);
//            if (Objects.nonNull(ifExist)) {
//                return true;
//            }
//            UserBackpack insert = UserBackpack.builder()
//                    .uid(uid)
//                    .itemId(itemId)
//                    .status(YesOrNoEnum.NO.getStatus())
//                    .idempotent(idempotent)
//                    .build();
//            return userBackpackDao.save(insert);
//        });

        UserBackpack ifExist = userBackpackDao.getByIdempotent(idempotent);
        if (Objects.nonNull(ifExist)) {
            return;
        }

        UserBackpack insert = UserBackpack.builder()
                .uid(uid)
                .itemId(itemId)
                .status(YesOrNoEnum.NO.getStatus())
                .idempotent(idempotent)
                .build();
        userBackpackDao.save(insert);
    }



    private String getIdempotent(Long itemId, IdempotentEnum idempotentEnum, String businessId) {
        return String.format("%d_%d_%s", itemId, idempotentEnum.getType(), businessId);
    }
}
