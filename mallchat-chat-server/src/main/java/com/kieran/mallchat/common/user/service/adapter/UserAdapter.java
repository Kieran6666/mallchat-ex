package com.kieran.mallchat.common.user.service.adapter;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import com.kieran.mallchat.common.common.domain.enums.YesOrNoEnum;
import com.kieran.mallchat.common.user.domain.entity.ItemConfig;
import com.kieran.mallchat.common.user.domain.entity.User;
import com.kieran.mallchat.common.user.domain.entity.UserBackpack;
import com.kieran.mallchat.common.user.domain.vo.resp.BadgesResp;
import com.kieran.mallchat.common.user.domain.vo.resp.UserInfoResp;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class UserAdapter {


    public static User buildAuthorizedUser(Long id, WxOAuth2UserInfo userInfo) {
        User user = new User();
        user.setId(id);
        user.setAvatar(userInfo.getHeadImgUrl());
        user.setName(userInfo.getNickname());
        user.setSex(userInfo.getSex());
        if (userInfo.getNickname().length() > 6) {
            user.setName("名字过长" + RandomUtil.randomInt(100000));
        } else {
            user.setName(userInfo.getNickname());
        }
        return user;
    }

    public static UserInfoResp buildUserInfoResp(User user, Integer countByValidItemId) {
        UserInfoResp resp = new UserInfoResp();
        BeanUtil.copyProperties(user, resp);
        resp.setModifyNameChance(countByValidItemId);
        return resp;
    }

    public static List<BadgesResp> buildUserBadges(List<ItemConfig> badges, List<UserBackpack> userBadges, User user) {
        if (Objects.isNull(user)) {
            return Collections.emptyList();
        }

        // 转为set，加快匹配速度
        Set<Long> userBadgesSet = userBadges.stream().map(UserBackpack::getItemId).collect(Collectors.toSet());

        return badges.stream().map(z -> {
            BadgesResp resp = new BadgesResp();
            BeanUtil.copyProperties(z, resp);
            resp.setObtain(userBadgesSet.contains(z.getId()) ? YesOrNoEnum.YES.getStatus() : YesOrNoEnum.NO.getStatus());
            resp.setWearing(Objects.equals(user.getItemId(), z.getId()) ? YesOrNoEnum.YES.getStatus() : YesOrNoEnum.NO.getStatus());
            return resp;
        }).sorted(Comparator.comparing(BadgesResp::getWearing, Comparator.reverseOrder())
                .thenComparing(BadgesResp::getObtain, Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }
}
