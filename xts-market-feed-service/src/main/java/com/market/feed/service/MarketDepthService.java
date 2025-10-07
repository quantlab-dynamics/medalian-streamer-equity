package com.market.feed.service;

import com.market.feed.repository.MarketDepthRepository;
import com.sf.xts.api.sdk.marketdata.response.MarketDepthBinaryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MarketDepthService {

    private static final Logger logger = LoggerFactory.getLogger(MarketDepthService.class);

    @Autowired
    private MarketDepthRepository repository;

    public void saveMarketDepthList(String key, List<MarketDepthBinaryResponse> marketDepth) {
        try {
            repository.saveList(key, marketDepth);
        } catch (Exception e) {
            logger.error("Error saving market depth list to Redis with key: " + key, e);
            throw new RuntimeException("Failed to save market depth list to Redis", e);
        }
    }

    public List<MarketDepthBinaryResponse> getMarketDepthList(String key) {
        try {
            return repository.findAll(key);
        } catch (Exception e) {
            logger.error("Error retrieving market depth list from Redis with key: " + key, e);
            throw new RuntimeException("Failed to retrieve market depth list from Redis", e);
        }
    }

    public void deleteMarketDepth(String key) {
        try {
            repository.clearList(key);
        } catch (Exception e) {
            logger.error("Error deleting market depth list from Redis with key: " + key, e);
            throw new RuntimeException("Failed to delete market depth list from Redis", e);
        }
    }

    public void saveMarketDepth(String key, MarketDepthBinaryResponse marketDepth) {
        try {
            repository.save(key, marketDepth);
        } catch (Exception e) {
            logger.error("Error saving market depth to Redis with key: " + key, e);
            throw new RuntimeException("Failed to save market depth to Redis", e);
        }
    }

    public MarketDepthBinaryResponse getMarketDepth(String key) {
        try {
            return repository.find(key);
        } catch (Exception e) {
            logger.error("Error retrieving market depth from Redis with key: " + key, e);
            throw new RuntimeException("Failed to retrieve market depth from Redis", e);
        }
    }
}
