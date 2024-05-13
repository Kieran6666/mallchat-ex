package com.kieran.mallchat.common.user.service;

import com.kieran.mallchat.common.common.domain.enums.IdempotentEnum;

/**
 * <p>
 * 用户背包表 服务类
 * </p>
 *
 * @author <a href="https://github.com/zongzibinbin">abin</a>
 * @since 2024-05-07
 */
public interface UserBackpackService {
    /**
     * 用户发放物品
     *
     * @param uid 用户ID
     * @param itemId 物品ID
     * @param idempotentEnum 幂等类型
     * @param businessId 幂等唯一标识
     */
    void acquireItem(Long uid, Long itemId, IdempotentEnum idempotentEnum, String businessId);

}
