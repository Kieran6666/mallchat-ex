package com.kieran.mallchat.common.common.event.listener;

import com.kieran.mallchat.common.common.domain.enums.IdempotentEnum;
import com.kieran.mallchat.common.common.event.UserRegisterEvent;
import com.kieran.mallchat.common.user.dao.UserDao;
import com.kieran.mallchat.common.user.domain.entity.User;
import com.kieran.mallchat.common.user.domain.enums.ItemEnum;
import com.kieran.mallchat.common.user.service.UserBackpackService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import javax.annotation.Resource;

@Component
public class UserRegisterListener {

    @Resource
    private UserBackpackService userBackpackService;

    @Resource
    private UserDao userDao;

    /**
     * 异步执行
     * 在前面流程的事务提交之后执行
     */
    @Async
    @TransactionalEventListener(classes = UserRegisterEvent.class, phase = TransactionPhase.AFTER_COMMIT)
    public void sendModifyNameCard(UserRegisterEvent event) {
        User user = event.getUser();
        // 收到用户注册事件，向用户发送徽章
        userBackpackService.acquireItem(user.getId(), ItemEnum.MODIFY_NAME_CARD.getId(), IdempotentEnum.UID,
                user.getId().toString());
    }

    /**
     * 如果使用 @EventListener，事务失败，也会触发该事件
     */
    @TransactionalEventListener(classes = UserRegisterEvent.class, phase = TransactionPhase.AFTER_COMMIT)
    public void sendBadge(UserRegisterEvent event) {
        User user = event.getUser();
        int registerCount = userDao.count();
        if (registerCount < 10) {
            userBackpackService.acquireItem(user.getId(), ItemEnum.REG_TOP10_BADGE.getId(), IdempotentEnum.UID,
                    user.getId().toString());
        }

        if (registerCount < 100) {
            userBackpackService.acquireItem(user.getId(), ItemEnum.REG_TOP100_BADGE.getId(), IdempotentEnum.UID,
                    user.getId().toString());
        }
    }
}
