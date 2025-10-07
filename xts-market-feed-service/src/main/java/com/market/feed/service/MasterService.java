package com.market.feed.service;

import com.market.feed.repository.MasterRepository;
import com.sf.xts.api.sdk.marketdata.master.MasterResponseFO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class MasterService {

    private static final Logger logger = LoggerFactory.getLogger(MasterService.class);

    @Autowired
    private MasterRepository repository;
    public static Map<Integer, MasterResponseFO> masterDataMap;
    private final MasterRepository masterRepository;

    @Autowired
    public MasterService(MasterRepository masterRepository) {
        this.masterRepository = masterRepository;
    }

    public void saveResponseList(String key, List<MasterResponseFO> responses) {
        try {
            repository.saveList(key, responses);
            processMasterResponses(responses);
        } catch (Exception e) {
            logger.error("Error processing and saving response list to repository with key: " + key, e);
            throw new RuntimeException("Failed to process and save response list to repository", e);
        }
    }

    public List<MasterResponseFO> getResponseList(String key) {
        try {
            return repository.findAll(key);
        } catch (Exception e) {
            logger.error("Error retrieving response list from repository with key: " + key, e);
            throw new RuntimeException("Failed to retrieve response list from repository", e);
        }
    }

    public void clearResponseList(String key) {
        try {
            repository.clearList(key);
        } catch (Exception e) {
            logger.error("Error clearing response list from repository with key: " + key, e);
            throw new RuntimeException("Failed to clear response list from repository", e);
        }
    }

    public void saveMaster(String key, MasterResponseFO masterResponseFO) {
        try {
            repository.save(key, masterResponseFO);
        } catch (Exception e) {
            logger.error("Error saving master response to repository with key: " + key, e);
            throw new RuntimeException("Failed to save master response to repository", e);
        }
    }

    public void processMasterResponses(List<MasterResponseFO> response) {
        try {
            Map<String, List<MasterResponseFO>> masterResponseMap = new HashMap<>();
            Map<String, MasterResponseFO> masterResponseMapNames = new HashMap<>();
            masterDataMap = new HashMap<>();
            for (MasterResponseFO masterResponse : response) {
                masterResponseMapNames.put("MASTER_"+masterResponse.getExchangeInstrumentID(),masterResponse);
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                Date date = inputFormat.parse(masterResponse.getContractExpiration());
                SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
                if (masterResponse.getOptionType() != null) {
                    String key =masterResponse.getInstrumentType() + outputFormat.format(date);
                    if (masterResponse.getExchangeSegmentId() == 1 && masterResponse.getOptionType().equalsIgnoreCase("1")){
                        key = key+"FUTURE";
                    }else if (masterResponse.getOptionType().equalsIgnoreCase("3")){
                        key = key+"-"+(int)masterResponse.getStrikePrice()+"CE";
                    }else if (masterResponse.getOptionType().equalsIgnoreCase("4")){
                        key = key+"-"+(int)masterResponse.getStrikePrice()+"PE";
                    }
                    masterResponseMapNames.put(key, masterResponse);
                }
                masterDataMap.put(masterResponse.getExchangeInstrumentID(), masterResponse);
                String key = masterResponse.getInstrumentType() + "_" + masterResponse.getContractExpiration();
                masterResponse.setInstrumentKey(key);

                if (masterResponseMap.containsKey(key)) {
                    List<MasterResponseFO> newdata = masterResponseMap.get(key);
                    newdata.add(masterResponse);
                    masterResponseMap.put(key, newdata);
                } else {
                    List<MasterResponseFO> newdata = new ArrayList<>();
                    newdata.add(masterResponse);
                    masterResponseMap.put(key, newdata);
                }
            }

            masterResponseMap.forEach((key, value) -> {
                try {
                    masterRepository.saveList(key, value);
                } catch (Exception e) {
                    logger.error("Error saving master response list to repository for key: " + key, e);
                    throw new RuntimeException("Failed to save master response list to repository for key: " + key, e);
                }
            });

            masterResponseMapNames.forEach((key, value) -> {
                try {
                    masterRepository.save(key, value);
                } catch (Exception e) {
                    logger.error("Error saving individual master response to repository for key: " + key, e);
                    throw new RuntimeException("Failed to save individual master response to repository for key: " + key, e);
                }
            });

        } catch (Exception e) {
            logger.error("Error processing master responses", e);
            throw new RuntimeException("Failed to process master responses", e);
        }
    }
}
