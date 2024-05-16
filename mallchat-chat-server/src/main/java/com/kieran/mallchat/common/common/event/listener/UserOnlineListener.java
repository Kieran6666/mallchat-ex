package com.kieran.mallchat.common.common.event.listener;

import com.kieran.mallchat.common.common.event.UserOnlineEvent;
import com.kieran.mallchat.common.user.dao.UserDao;
import com.kieran.mallchat.common.user.domain.entity.User;
import com.kieran.mallchat.common.user.domain.enums.ActiveStatusEnum;
import com.kieran.mallchat.common.user.service.IpService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import javax.annotation.Resource;

@Component
public class UserOnlineListener {

    @Resource
    private UserDao userDao;

    @Resource
    private IpService ipService;

    /**
     * phase = TransactionPhase.AFTER_COMMIT 在事务提交后，触发
     * fallbackExecution = true 如果被监听的event不执行事务，那么此处是否触发，默认不触发
     */
    @Async
    @TransactionalEventListener(value = UserOnlineEvent.class, phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void saveDB(UserOnlineEvent event) {
        User user = event.getUser();

        User update = new User();
        update.setId(user.getId());
        update.setIpInfo(user.getIpInfo()); // 这一步只保存了IP，并没有解析最新的IP数据
        update.setLastOptTime(user.getLastOptTime());
        update.setActiveStatus(ActiveStatusEnum.ONLINE.getStatus());
        userDao.updateById(update);

        // 用户IP解析
        ipService.refreshIpAsync(user.getId());

    }
}
