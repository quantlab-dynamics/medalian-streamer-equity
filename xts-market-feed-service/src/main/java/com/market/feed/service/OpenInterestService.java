package com.market.feed.service;

import com.market.feed.repository.OpenInterestRepository;
import com.sf.xts.api.sdk.marketdata.response.OpenInterestBinaryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OpenInterestService {

	private static final Logger logger = LoggerFactory.getLogger(OpenInterestService.class);

	@Autowired
	private OpenInterestRepository repository;

	public void saveOpenInterest(String key, List<OpenInterestBinaryResponse> openInterest) {
		try {
			repository.saveList(key, openInterest);
		} catch (Exception e) {
			logger.error("Error saving open interest data to Redis with key: " + key, e);
			throw new RuntimeException("Failed to save open interest data to Redis", e); // Wrap exception for higher
																							// layers
		}
	}

	public List<OpenInterestBinaryResponse> getOpenInterest(String key) {
		try {
			return repository.findAll(key);
		} catch (Exception e) {
			logger.error("Error retrieving open interest data from Redis with key: " + key, e);
			throw new RuntimeException("Failed to retrieve open interest data from Redis", e); // Wrap exception for
																								// higher layers
		}
	}

	public void deleteOpenInterest(String key) {
		try {
			repository.clearList(key);
		} catch (Exception e) {
			logger.error("Error deleting open interest data from Redis with key: " + key, e);
			throw new RuntimeException("Failed to delete open interest data from Redis", e); // Wrap exception for
																								// higher layers
		}
	}
}
