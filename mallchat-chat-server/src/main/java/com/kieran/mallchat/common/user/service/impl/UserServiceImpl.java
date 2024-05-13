package com.kieran.mallchat.common.user.service.impl;

import com.kieran.mallchat.common.common.annotation.RedissonLock;
import com.kieran.mallchat.common.common.event.UserRegisterEvent;
import com.kieran.mallchat.common.common.utils.AssertUtil;
import com.kieran.mallchat.common.user.dao.UserBackpackDao;
import com.kieran.mallchat.common.user.dao.UserDao;
import com.kieran.mallchat.common.user.domain.entity.ItemConfig;
import com.kieran.mallchat.common.user.domain.entity.User;
import com.kieran.mallchat.common.user.domain.entity.UserBackpack;
import com.kieran.mallchat.common.user.domain.enums.ItemEnum;
import com.kieran.mallchat.common.user.domain.enums.ItemTypeEnum;
import com.kieran.mallchat.common.user.domain.vo.req.WearBadgeReq;
import com.kieran.mallchat.common.user.domain.vo.resp.BadgesResp;
import com.kieran.mallchat.common.user.domain.vo.resp.UserInfoResp;
import com.kieran.mallchat.common.user.service.UserService;
import com.kieran.mallchat.common.user.service.adapter.UserAdapter;
import com.kieran.mallchat.common.user.service.cache.ItemCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserDao userDao;

    @Resource
    private UserBackpackDao userBackpackDao;

    @Override
    public User getByOpenId(String openId) {
        return userDao.getByOpenId(openId);
    }

    @Resource
    private ItemCache itemCache;

    /**
     * spring event  观察者模式
     */
    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    /**
     * 这里给用户发送物品，不能影响用户注册，因此在spring event的生效节点，应该设置在用户注册事务成功提交之后
     */
    @Override
    @Transactional
    public Long register(User user) {
        boolean save = userDao.save(user);

        applicationEventPublisher.publishEvent(new UserRegisterEvent(this, user));

        return save ? user.getId() : 0;
    }

    @Override
    public User getByUid(Long uid) {
        return userDao.getById(uid);
    }

    @Override
    public UserInfoResp getUserInfo(Long uid) {
        // TODO:: 这里要走用户缓存
        User user = getByUid(uid);
        Integer countByValidItemId = userBackpackDao.getCountByValidItemId(uid, ItemEnum.MODIFY_NAME_CARD.getId());
        return UserAdapter.buildUserInfoResp(user, countByValidItemId);
    }

    @RedissonLock(key = "#uid")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void modifyName(Long uid, String name) {
        User user = userDao.getByName(name);
        AssertUtil.isEmpty(user, "名字已存在，换一个名字8");
        UserBackpack userBackpack = userBackpackDao.getFirstValidItem(uid, ItemEnum.MODIFY_NAME_CARD.getId());
        AssertUtil.isNotEmpty(userBackpack, "改名卡已经用光啦，参与下次活动领取8");

        // 更新改名卡为已使用
        boolean ifSuccess = userBackpackDao.invalidModifyNameCard(userBackpack.getId());
        if (ifSuccess) {
            boolean result = userDao.modifyName(uid, name);
            AssertUtil.isTrue(result, "修改名称失败，请稍后重试");
        }
    }

    @Override
    public List<BadgesResp> getBadges(Long uid) {
        // 获取所有的徽章
        List<ItemConfig> badges = itemCache.getByType(ItemTypeEnum.BADGE.getType());
        // 获取用户拥有的徽章
        List<UserBackpack> userBadges = userBackpackDao.getItemIds(uid,
                badges.stream().map(ItemConfig::getId).collect(Collectors.toList()));
        // 获取用户佩戴的徽章
        User user = userDao.getById(uid);
        // 交给适配器返回VO
        return UserAdapter.buildUserBadges(badges, userBadges, user);

    }

    @Override
    public void wearBadge(Long uid, WearBadgeReq req) {
        // 查询是否有这个徽章
        ItemConfig badge = itemCache.getById(req.getBadgeId());
        AssertUtil.isNotEmpty(badge, "徽章不存在");
        // 查询用户是否拥有这个徽章
        UserBackpack userBadge = userBackpackDao.getFirstValidItem(uid, req.getBadgeId());
        AssertUtil.isNotEmpty(userBadge, "您未拥有此徽章，无法佩戴");
        // 佩戴徽章
        boolean result = userDao.wearBadge(uid, req.getBadgeId());
        AssertUtil.isTrue(result, "佩戴失败，请稍微重试");
    }
}
