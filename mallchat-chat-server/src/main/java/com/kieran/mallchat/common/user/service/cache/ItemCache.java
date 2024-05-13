package com.kieran.mallchat.common.user.service.cache;

import com.kieran.mallchat.common.user.dao.ItemConfigDao;
import com.kieran.mallchat.common.user.domain.entity.ItemConfig;
import io.swagger.models.auth.In;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class ItemCache {

    @Resource
    private ItemConfigDao itemConfigDao;

    @Cacheable(cacheNames = "item", key = "'itemById:' + #itemId" )
    public ItemConfig getById(Long itemId) {
        return itemConfigDao.getById(itemId);
    }

    @Cacheable(cacheNames = "item", key = "'itemByType:' + #itemType")
    public List<ItemConfig> getByType(Integer itemType) {
        return itemConfigDao.getByType(itemType);
    }

    @CacheEvict(cacheNames = "item", key = "'itemByType:' + #itemType")
    public void evictType(Integer itemType) {

    }



}
