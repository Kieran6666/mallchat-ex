package com.kieran.mallchat.common.user.service.impl;

import com.kieran.mallchat.common.user.dao.UserDao;
import com.kieran.mallchat.common.user.domain.entity.User;
import com.kieran.mallchat.common.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserDao userDao;

    @Override
    public User getByOpenId(String openId) {
        return userDao.getByOpenId(openId);
    }

    @Override
    public Long register(User user) {
        boolean save = userDao.save(user);
        return save ? user.getId() : 0;
    }

    @Override
    public User getByUid(Long uid) {
        return userDao.getById(uid);
    }
}
