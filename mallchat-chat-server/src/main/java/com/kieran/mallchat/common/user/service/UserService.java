package com.kieran.mallchat.common.user.service;


import com.kieran.mallchat.common.user.domain.entity.User;
import com.kieran.mallchat.common.user.domain.vo.req.WearBadgeReq;
import com.kieran.mallchat.common.user.domain.vo.resp.BadgesResp;
import com.kieran.mallchat.common.user.domain.vo.resp.UserInfoResp;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;

import java.util.List;

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

    UserInfoResp getUserInfo(Long uid);

    void modifyName(Long uid, String name);

    List<BadgesResp> getBadges(Long uid);

    void wearBadge(Long uid, WearBadgeReq req);

}
