package com.market.feed.repository;
import com.sf.xts.api.sdk.marketdata.master.MasterResponseFO;
import com.sf.xts.api.sdk.marketdata.response.TouchlineBinaryResposne;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class MasterRepository {

        @Autowired
        private final RedisTemplate<String, MasterResponseFO> redisTemplate;

       public MasterRepository(RedisTemplate<String, MasterResponseFO> redisTemplate) {
        this.redisTemplate = redisTemplate;
        }
       public void save(String key, MasterResponseFO masterResponseFO) {
           redisTemplate.opsForValue().set(key, masterResponseFO);
//           ListOperations<String, MasterResponseFO> listOps = redisTemplate.opsForList();
//           listOps.rightPushAll(key, masterResponseFO);

       }

        public void saveList(String key, List<MasterResponseFO> responses) {
          // redisTemplate.delete(key);
//         //  redisTemplate.opsForList().set(key,responses);
            ListOperations<String, MasterResponseFO> listOps = redisTemplate.opsForList();
            listOps.rightPushAll(key, responses);
        }


    public List<MasterResponseFO> findAll(String key) {
        ListOperations<String, MasterResponseFO> listOps = redisTemplate.opsForList();
        return listOps.range(key, 0, -1);
    }

    public void clearList(String key) {
        redisTemplate.delete(key);
    }


    public List<MasterResponseFO> fetchMasterResponseFO(String instrumentExpiryDateKey) {
        List<MasterResponseFO> redisData = null;
        try {
            redisData = redisTemplate.opsForList().range(instrumentExpiryDateKey, 0, -1);
        }
        catch (Exception e) {
            // Log the exception details
            //logger.error("Error getting value in Redis: {}", e.getMessage());
            e.printStackTrace();
        }
        return redisData;
    }
    public MasterResponseFO find(String key) {
        return redisTemplate.opsForValue().get(key);
    }

}










