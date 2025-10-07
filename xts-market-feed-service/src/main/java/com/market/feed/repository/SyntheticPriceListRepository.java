package com.market.feed.repository;


import com.market.feed.model.SyntheticForList;
import com.market.feed.model.SyntheticPrice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SyntheticPriceListRepository {



    @Autowired
    @Qualifier("redisTemplateSyntheticList")
    private final RedisTemplate<String, SyntheticForList> redisTemplate;

    public SyntheticPriceListRepository(RedisTemplate<String, SyntheticForList> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void save(String key, SyntheticForList syntheticPrice) {
//        redisTemplate.opsForValue().set("LIST_SYNTHETIC_"+key, syntheticPrice);
        ListOperations<String, SyntheticForList> listOps = redisTemplate.opsForList();
        listOps.rightPushAll("LIST_SYNTHETIC_"+key, syntheticPrice);

    }



    public List<SyntheticForList> findAll(String key) {
        ListOperations<String, SyntheticForList> listOps = redisTemplate.opsForList();
        return listOps.range("LIST_SYNTHETIC_"+key, 0, -1);
    }

    public void clearList(String key) {
        redisTemplate.delete(key);
    }


    public SyntheticForList find(String key) {
        return redisTemplate.opsForValue().get("SYNTHETIC_"+key);
    }


}

