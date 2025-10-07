package com.market.feed.service;

import com.market.feed.model.MarketData;
import com.sf.xts.api.sdk.marketdata.response.TouchlineBinaryResposne;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RedisService {

	@Autowired
	@Qualifier("redisTemplateList")
	private RedisTemplate<String, String> redisTemplate;

	@Autowired
	@Qualifier("redisTemplateObject")
	private RedisTemplate<String, Object> redisTemplateObject;

	@Autowired
	@Qualifier("redisTemplateMarketData")
	private RedisTemplate<String, MarketData> redisTemplateMarketData;

	public void flushAll() {
		redisTemplate.getConnectionFactory().getConnection().flushAll();
	}

	public void saveStringToRedis(String key, String value) {
		redisTemplate.opsForValue().set(key, value); // Save the string value to Redis at the given key
	}

	public String getStringFromRedis(String key) {
		return redisTemplate.opsForValue().get(key); // Retrieve the string value from Redis at the given key
	}

	// Save List<String> to Redis
	public void saveListToRedis(String key, List<String> result) {
		redisTemplate.delete(key); // deleting the key and saving the new data
		redisTemplate.opsForList().rightPushAll(key, result); // Saves the entire list at once
	}

	// Retrieve List<String> from Redis
	public List<String> getListFromRedis(String key) {
		return redisTemplate.opsForList().range(key, 0, -1); // Get all elements from the list
	}

	// Save an Object to Redis
	public void saveObjectToRedis(String key, Object object) {
		redisTemplateObject.opsForValue().set(key, object); // Save object as a value
	}

	// Retrieve an Object from Redis
	public Object getObjectFromRedis(String key) {
		return redisTemplateObject.opsForValue().get(key); // Get object by key
	}

	public void saveMarketData(String key, MarketData marketData) {
		if (marketData.getExchangeInstrumentId() == 43851){

			System.out.println("Id: "+marketData.getExchangeInstrumentId());
		}
		try {
			redisTemplateMarketData.opsForValue().set(key, marketData);
		} catch (Exception e) {
			// Log the exception details
			System.err.println("Error setting value in Redis: " + e.getMessage());
			e.printStackTrace();
		}

	}

	public MarketData findMarketData(String key) {
		MarketData marketData = null;
		try {
			marketData = redisTemplateMarketData.opsForValue().get(key);
		}
		catch (Exception e) {
			// Log the exception details
			System.err.println("Error getting value in Redis: " + e.getMessage());
			e.printStackTrace();
		}
		return marketData;
	}

}
