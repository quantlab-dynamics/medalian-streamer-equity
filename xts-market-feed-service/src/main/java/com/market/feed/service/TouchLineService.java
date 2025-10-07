package com.market.feed.service;

import com.sf.xts.api.sdk.marketdata.response.TouchlineBinaryResposne;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.market.feed.repository.TouchLineRepository;

import java.util.List;

@Service
public class TouchLineService {

	private static final Logger logger = LoggerFactory.getLogger(TouchLineService.class);

	private final TouchLineRepository repository;

	@Autowired
	public TouchLineService(TouchLineRepository touchLineRepository) {
		this.repository = touchLineRepository;
	}

	/**
	 * Save a touch line object in the repository (Redis, DB, etc.).
	 *
	 * @param key       The key to associate with the touchline object
	 * @param touchLine The touchline object to save
	 */
	public void saveTouchLine(String key, TouchlineBinaryResposne touchLine) {
		try {
//			if (touchLine.getExchangeInstrumentId() == 43851){
//
//				System.out.println("Id: "+touchLine.getExchangeInstrumentId());
//			}
			repository.save(key, touchLine);
		} catch (Exception e) {
			logger.error("Error saving touchline with key: {}", key, e);
			throw new RuntimeException("Failed to save touchline with key: " + key, e);
		}
	}

	/**
	 * Retrieve a touch line object from the repository by key.
	 *
	 * @param key The key of the touchline object to retrieve
	 * @return The touchline object if found, or null
	 */
	public TouchlineBinaryResposne getTouchLine(String key) {
		try {
			return repository.find("TL_"+key);
		} catch (Exception e) {
			logger.error("Error retrieving touchline with key: {}", key, e);
			throw new RuntimeException("Failed to retrieve touchline with key: " + key, e);
		}
	}

	public List<String> getExpiryDates(String key) {
		return repository.fetchExpiryDates(key);
	}

	/**
	 * Delete a touch line object from the repository by key.
	 *
	 * @param key The key of the touchline object to delete
	 */
	public void deleteTouchLine(String key) {
		try {
			repository.delete(key);
		} catch (Exception e) {
			logger.error("Error deleting touchline with key: {}", key, e);
			throw new RuntimeException("Failed to delete touchline with key: " + key, e);
		}
	}
}
