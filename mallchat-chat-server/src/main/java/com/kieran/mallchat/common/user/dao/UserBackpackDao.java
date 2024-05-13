package com.kieran.mallchat.common.user.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kieran.mallchat.common.common.domain.enums.YesOrNoEnum;
import com.kieran.mallchat.common.user.domain.entity.UserBackpack;
import com.kieran.mallchat.common.user.mapper.UserBackpackMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 用户背包表 服务实现类
 * </p>
 *
 * @author <a href="https://github.com/zongzibinbin">abin</a>
 * @since 2024-05-07
 */
@Service
public class UserBackpackDao extends ServiceImpl<UserBackpackMapper, UserBackpack> {

    public Integer getCountByValidItemId(Long uid, Long itemId) {
        return lambdaQuery().eq(UserBackpack::getUid, uid)
                .eq(UserBackpack::getItemId, itemId)
                .eq(UserBackpack::getStatus, YesOrNoEnum.NO.getStatus())
                .count();
    }

    public UserBackpack getFirstValidItem(Long uid, Long itemId) {
        LambdaQueryWrapper<UserBackpack> wrapper = new QueryWrapper<UserBackpack>().lambda()
                .eq(UserBackpack::getUid, uid)
                .eq(UserBackpack::getItemId, itemId)
                .eq(UserBackpack::getStatus, YesOrNoEnum.NO.getStatus())
                .last("limit 1");
        return getOne(wrapper);
    }

    public boolean invalidModifyNameCard(Long id) {
        return lambdaUpdate().eq(UserBackpack::getId, id)
                .eq(UserBackpack::getStatus, YesOrNoEnum.NO.getStatus())
                .set(UserBackpack::getStatus, YesOrNoEnum.YES.getStatus())
                .set(UserBackpack::getUpdateTime, new Date())
                .update();
    }

    public List<UserBackpack> getItemIds(Long uid, List<Long> itemIds) {
        return lambdaQuery().eq(UserBackpack::getUid, uid)
                .in(UserBackpack::getItemId, itemIds)
                .list();
    }

    public UserBackpack getByIdempotent(String idempotent) {
        return lambdaQuery().eq(UserBackpack::getIdempotent, idempotent)
                .one();
    }
}
