package com.kieran.mallchat.common.user.dao;

import com.kieran.mallchat.common.user.domain.entity.User;
import com.kieran.mallchat.common.user.mapper.UserMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author <a href="https://github.com/zongzibinbin">abin</a>
 * @since 2024-04-23
 */
@Service
public class UserDao extends ServiceImpl<UserMapper, User> {

    public User getByOpenId(String openId) {
        return lambdaQuery().eq(User::getOpenId, openId).one();
    }

    public User getByName(String name) {
        return lambdaQuery().eq(User::getName, name).one();
    }

    public boolean modifyName(Long uid, String name) {
        User update = new User();
        update.setId(uid);
        update.setName(name);
        return updateById(update);

    }

    public boolean wearBadge(Long uid, Long badgeId) {
        return lambdaUpdate().eq(User::getId, uid)
                .set(User::getItemId, badgeId)
                .update();
    }
}
