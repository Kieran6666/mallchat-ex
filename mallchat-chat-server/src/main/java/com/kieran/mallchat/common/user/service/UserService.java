package com.kieran.mallchat.common.user.service;


import com.kieran.mallchat.common.user.domain.entity.User;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author <a href="https://github.com/zongzibinbin">abin</a>
 * @since 2024-04-23
 */
public interface UserService {

    User getByOpenId(String openId);

    Long register(User user);

    User getByUid(Long uid);



}
