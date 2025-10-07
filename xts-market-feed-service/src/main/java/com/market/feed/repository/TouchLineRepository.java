package com.market.feed.repository;
import com.sf.xts.api.sdk.marketdata.response.TouchlineBinaryResposne;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

    @Repository
    public class TouchLineRepository {

        @Autowired
        @Qualifier("redisTemplate1")
        private final RedisTemplate<String, TouchlineBinaryResposne> redisTemplate;

		@Autowired
        @Qualifier("redisTemplateList2")
        private final RedisTemplate<String, String> redisTemplateList2;

        public TouchLineRepository(RedisTemplate<String, TouchlineBinaryResposne> redisTemplate ,
								   @Qualifier("redisTemplateList2") RedisTemplate<String, String> redisExpiryDatesTemplate) {
            this.redisTemplate = redisTemplate;
			this.redisTemplateList2 = redisExpiryDatesTemplate;
        }

        public void save(String key, TouchlineBinaryResposne touchLine) {
        	try {
        		redisTemplate.opsForValue().set(key, touchLine);
        	} catch (Exception e) {
        	    // Log the exception details
        	    System.err.println("Error setting value in Redis: " + e.getMessage());
        	    e.printStackTrace();
        	}

        }

		public List<String> fetchExpiryDates(String listKey) {
			List<String> redisData = null;
			try {
				redisData = redisTemplateList2.opsForList().range(listKey, 0, -1);
			}
			catch (Exception e) {
				// Log the exception details
			//	logger.error("Error getting value in Redis: {}", e.getMessage());
            e.printStackTrace();
			}
			return redisData;
		}


		public TouchlineBinaryResposne find(String key) {
        	TouchlineBinaryResposne touchlineBinaryResposne = null;
        	try {
        		touchlineBinaryResposne = redisTemplate.opsForValue().get(key);
        	}
        	catch (Exception e) {
        	    // Log the exception details
        	    System.err.println("Error getting value in Redis: " + e.getMessage());
        	    e.printStackTrace();
        	}
			return touchlineBinaryResposne;
        }

        public void delete(String key) {
        	try {
        		redisTemplate.delete(key);
        	} catch (Exception e) {
        	    // Log the exception details
        	    System.err.println("Error deleting value in Redis: " + e.getMessage());
        	    e.printStackTrace();
        	}
        }
    }



//}
//public void saveList(String key, TouchlineBinaryResposne touchLine) {
//            redisTemplate.opsForValue().set(key, touchLine);
//
//        }
//
//        public List<TouchlineBinaryResposne> findAll(String key) {
//            ListOperations<String, TouchlineBinaryResposne> listOps = redisTemplate.opsForList();
//            return listOps.range(key, 0, -1);
//        }
//
//        public void clearList(String key) {
//            redisTemplate.delete(key);
//        }
//    }
