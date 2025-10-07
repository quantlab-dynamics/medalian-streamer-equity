package com.market.feed.util;

import com.market.feed.model.ExpiryDatesDTO;
import com.market.feed.model.MarketData;
import com.market.feed.model.SyntheticForList;
import com.market.feed.repository.MasterRepository;
import com.market.feed.repository.SyntheticPriceListRepository;
import com.market.feed.service.MasterService;
import com.market.feed.service.TouchLineService;
import com.sf.xts.api.sdk.marketdata.master.MasterResponseFO;
import com.sf.xts.api.sdk.marketdata.response.TouchlineBinaryResposne;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class MarketFormData {
    private static final Logger logger = LoggerFactory.getLogger(MarketFormData.class);

    public static final ArrayList<String> EXPIRYVALUE = new ArrayList<>(List.of("EXP_SENSEX_IO", "EXP_NIFTY_FUTIDX", "EXP_NIFTY_OPTIDX", "EXP_BANKEX_IF", "EXP_BANKNIFTY_FUTIDX", "EXP_FINNIFTY_OPTIDX", "EXP_BANKNIFTY_OPTIDX", "EXP_FINNIFTY_FUTIDX", "EXP_BANKEX_IO", "EXP_SENSEX_IF"));

    public static Map<String, MasterResponseFO> redisMasterResponseFOData = new HashMap<>();
    public static Map<String, ArrayList<LocalDateTime>> redisFetchedData= new HashMap<>();
    public static Map<String, List<String>> redisFetchedIndexExpiry;
    //    private List<Strategy> allStrategys = new ArrayList<>();
    public static Map<String, ExpiryDatesDTO> indexExpiryDates = new HashMap<>();
    public static Map<String, List<Integer>> redisIndexStrikePrices = new HashMap<>();

    @Autowired
    TouchLineService touchLineService;

    @Autowired
    MasterRepository masterRepository;

    @Autowired
    SyntheticPriceListRepository syntheticPriceListRepository;


    public Map<String, List<String>> getInstrumentExpiryDates(ArrayList<String> expiryInstrumentIds) {
        Map<String, List<String>> redisData = new HashMap<>();
        for (String instrumentId : expiryInstrumentIds) {
            List<String> dates = touchLineService.getExpiryDates(instrumentId);
            redisData.put(instrumentId,dates);
        }
        return redisData;
    }
    void getRedisDates() {
        redisFetchedIndexExpiry = getInstrumentExpiryDates(EXPIRYVALUE);
    }

    public double getSynthaticAtm(MasterResponseFO master ,double spotprice) {
        if (redisFetchedData.isEmpty() || redisIndexStrikePrices.isEmpty()  || indexExpiryDates.isEmpty()) {
            getRedisDates();
            redisFetchedData= processRedisData();
            getRedisMasterResponseFO();
        }
        try {
            List<Integer> strikeList = redisIndexStrikePrices.get(master.getInstrumentType());
            String strikeType = "CE";
            if (master.getOptionType().equals("3"))
                strikeType = "CE";
            else if (master.getOptionType().equals("4"))
                strikeType = "PE";
            else return spotprice;
            Integer atmStrike = findClosestStrike(strikeList, spotprice);
            if (atmStrike == 0) {
                return spotprice; // No valid strike found
            }
            String callKey = master.getInstrumentType().toUpperCase(Locale.ROOT) +
                    getExpiryShotDateByIndex(master.getContractExpiration()) +
                    "-" + atmStrike + "CE";
            String putKey = master.getInstrumentType().toUpperCase(Locale.ROOT) +
                    getExpiryShotDateByIndex(master.getContractExpiration()) +
                    "-" + atmStrike + "PE";
            TouchlineBinaryResposne callData = touchLineService.getTouchLine(String.valueOf(getMasterResponse(callKey).getExchangeInstrumentID()));
            TouchlineBinaryResposne putData = touchLineService.getTouchLine(String.valueOf(getMasterResponse(putKey).getExchangeInstrumentID()));

            double adjustment = callData.getLTP() - putData.getLTP();

            // Calculate synthetic spot(Mahir changed the spotprice to atmStrike as Rajesh sir asked )
            return atmStrike + adjustment;
        }catch (Exception e){
         //   e.printStackTrace();
            return spotprice;
        }
    }

    public double getSyntheticPrice(String underling , TouchlineBinaryResposne marketData) {
        if (redisFetchedData.isEmpty()) {
            getRedisDates();
            redisFetchedData= processRedisData();
            getRedisMasterResponseFO();
        }
        try {
//            System.out.println("underling = " + getIndexName(underling));

            ExpiryDatesDTO master = indexExpiryDates.get(getIndexName(underling));
            List<Integer> strikeList = redisIndexStrikePrices.get(underling);
            Integer atmStrike = findClosestStrike(strikeList, marketData.getOpen());
            logger.info("### Synthetic{},  atmStrike = {} ", underling, atmStrike);
            String callKey = underling.toUpperCase(Locale.ROOT) +
                    getExpiryShotDateByIndex(master.getCurrentWeek().toString()) +
                    "-" + atmStrike + "CE";
            String putKey = underling.toUpperCase(Locale.ROOT) +
                    getExpiryShotDateByIndex(master.getCurrentWeek().toString()) +
                    "-" + atmStrike + "PE";
            TouchlineBinaryResposne callData = touchLineService.getTouchLine(String.valueOf(getMasterResponse(callKey).getExchangeInstrumentID()));
            TouchlineBinaryResposne putData = touchLineService.getTouchLine(String.valueOf(getMasterResponse(putKey).getExchangeInstrumentID()));

            double adjustment = callData.getLTP() - putData.getLTP();

            // Calculate synthetic spot
            return atmStrike + adjustment;
        }catch (Exception e){
           // e.printStackTrace();
            return marketData.getLTP();
        }

    }



    public void getSyntheticList(String underling , TouchlineBinaryResposne marketData) {
        if (redisFetchedData.isEmpty()) {
            getRedisDates();
            redisFetchedData= processRedisData();
            getRedisMasterResponseFO();
        }
        try {
            SyntheticForList syntheticForList = new SyntheticForList();

            ExpiryDatesDTO master = indexExpiryDates.get(getIndexName(underling));

            List<Integer> strikeList = redisIndexStrikePrices.get(underling);
            Integer aatmStrike = findClosestStrike(strikeList, marketData.getOpen());
            String acallKey = underling.toUpperCase(Locale.ROOT) +
                    getExpiryShotDateByIndex(master.getCurrentWeek().toString()) +
                    "-" + aatmStrike + "CE";
            String aputKey = underling.toUpperCase(Locale.ROOT) +
                    getExpiryShotDateByIndex(master.getCurrentWeek().toString()) +
                    "-" + aatmStrike + "PE";
            TouchlineBinaryResposne acallData = touchLineService.getTouchLine(String.valueOf(getMasterResponse(acallKey).getExchangeInstrumentID()));
            TouchlineBinaryResposne aputData = touchLineService.getTouchLine(String.valueOf(getMasterResponse(aputKey).getExchangeInstrumentID()));


            Integer atmStrike = findClosestStrike(strikeList, marketData.getLTP());
            String callKey = underling.toUpperCase(Locale.ROOT) +
                    getExpiryShotDateByIndex(master.getCurrentWeek().toString()) +
                    "-" + atmStrike + "CE";
            String putKey = underling.toUpperCase(Locale.ROOT) +
                    getExpiryShotDateByIndex(master.getCurrentWeek().toString()) +
                    "-" + atmStrike + "PE";
            TouchlineBinaryResposne callData = touchLineService.getTouchLine(String.valueOf(getMasterResponse(callKey).getExchangeInstrumentID()));
            TouchlineBinaryResposne putData = touchLineService.getTouchLine(String.valueOf(getMasterResponse(putKey).getExchangeInstrumentID()));

            String futureKey = underling.toUpperCase(Locale.ROOT) +
                    getExpiryShotDateByIndex(master.getCurrentMonth().toString()) + "FUTURE";
            double adjustment = callData.getLTP() - putData.getLTP();
            TouchlineBinaryResposne futureData = touchLineService.getTouchLine(String.valueOf(getMasterResponse(futureKey).getExchangeInstrumentID()));

            syntheticForList.setLut(marketData.getLut());
            syntheticForList.setPrice( marketData.getLTP());
            syntheticForList.setSPrice(marketData.getLTP()+(acallData.getLTP() - aputData.getLTP()));
            syntheticForList.setSpotPE(putData.getLTP());
            syntheticForList.setSpotCE(callData.getLTP());
            syntheticForList.setAncherCE(acallData.getLTP());
            syntheticForList.setAncherPE(aputData.getLTP());
            syntheticForList.setFuture(futureData.getLTP());
            syntheticForList.setTimestamp(LocalDateTime.now().toString());
            syntheticForList.setAncherParity( acallData.getLTP() - aputData.getLTP());
            syntheticForList.setAncherATM(aatmStrike);
            syntheticForList.setSpotATM(atmStrike);

            syntheticPriceListRepository.save(underling, syntheticForList);

            // Calculate synthetic spot
//            return marketData.getLTP() + adjustment;
        }catch (Exception e){
            e.printStackTrace();
//            return marketData.getLTP();
        }

    }


    public int findClosestStrike(List<Integer> strikeList, double ltp) throws Exception {
        if (strikeList == null || strikeList.isEmpty()) {
            return 0;
        }
        int left = 0, right = strikeList.size() - 1;

        if (ltp <= strikeList.get(0)) return strikeList.get(0);
        if (ltp >= strikeList.get(right)) return strikeList.get(right);

        // Binary Search to find closest strikes
        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (strikeList.get(mid) == ltp) {
                return strikeList.get(mid);
            } else if (strikeList.get(mid) < ltp) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        // left is now the index of the closest higher value
        // right is the index of the closest lower value
        int lower = strikeList.get(right);
        int higher = strikeList.get(left);

        // Return the nearest value
        return (ltp - lower <= higher - ltp) ? lower : higher;
    }


    public Map<String, ArrayList<LocalDateTime>> processRedisData(){

        Map<String,ArrayList<LocalDateTime>> finalData = new HashMap<>();
        try {
            for (Map.Entry<String, List<String>> indexEntry : redisFetchedIndexExpiry.entrySet()) {
                List<String> stringArray = indexEntry.getValue();
                ArrayList<LocalDateTime> dateTimeList = new ArrayList<>();
                for (String dateTimeStr : stringArray) {
                    dateTimeList.add(LocalDateTime.parse(dateTimeStr));
                }
                dateTimeList.sort((d1, d2) -> d1.compareTo(d2));
                finalData.put(indexEntry.getKey(), dateTimeList);
            }

            for (Map.Entry<String, ArrayList<LocalDateTime>> indexData : finalData.entrySet()) {
                ArrayList<LocalDateTime> dates = indexData.getValue();
                ExpiryDatesDTO expiryDatesDTO = new ExpiryDatesDTO();
                expiryDatesDTO.setCurrentWeek(indexData.getValue().get(0));
                expiryDatesDTO.setNextWeek(indexData.getValue().get(1));

                LocalDateTime lastDateInCurrentMonth = dates.stream()
                        .filter(date -> date.getYear() == expiryDatesDTO.getCurrentWeek().getYear() && date.getMonth() == expiryDatesDTO.getCurrentWeek().getMonth())
                        .max(Comparator.naturalOrder())
                        .orElse(null);

                LocalDateTime lastDateInNextMonth = dates.stream()
                        .filter(date -> date.isAfter(expiryDatesDTO.getCurrentWeek()) && (date.getMonthValue() != expiryDatesDTO.getCurrentWeek().getMonthValue() || date.getYear() != expiryDatesDTO.getCurrentWeek().getYear()))
                        .map(date -> LocalDateTime.of(date.getYear(), date.getMonth(), 1, 0, 0))
                        .distinct()
                        .sorted()
                        .findFirst()
                        .flatMap(nextMonth -> dates.stream()
                                .filter(date -> date.getYear() == nextMonth.getYear() && date.getMonth() == nextMonth.getMonth())
                                .max(Comparator.naturalOrder()))
                        .orElse(null);
                String indexName = getIndexName(indexData.getKey());
                expiryDatesDTO.setCurrentMonth(lastDateInCurrentMonth);
                expiryDatesDTO.setNextMonth(lastDateInNextMonth);
             //   logger.info("the current index = " + indexData.getKey());
              //  logger.info("the expiryDatedDTO = " + expiryDatesDTO.toString());
                indexExpiryDates.put(indexName, expiryDatesDTO);
            }
            return finalData;
        }catch (Exception e){
           // logger.error("Error while processing Redis data: " + e.getMessage());
            return Collections.emptyMap();
        }
    }

    void getRedisMasterResponseFO() {
        try {
            List<MasterResponseFO> redisMasterResponse = new ArrayList<>();
            indexExpiryDates.forEach((key, value) -> {
                List<MasterResponseFO> masterResponseFOS = fetchAllMasterResponses(key, value);
                redisMasterResponse.addAll(masterResponseFOS);
                Set<Integer> strikeList = new HashSet<>();
                for (MasterResponseFO master : masterResponseFOS) {
                    String suffix = "";
                    if (master.getOptionType() == null)
                        suffix = "FU";
                    else if (master.getOptionType().equals("3"))
                        suffix = "CE";
                    else if (master.getOptionType().equals("4"))
                        suffix = "PE";
                    else
                        suffix = "OTHERS";
                    strikeList.add((int) master.getStrikePrice());
                    String masterKey = master.getInstrumentType() + master.getContractExpiration() + master.getStrikePrice() + suffix;
                    redisMasterResponseFOData.put(masterKey, master);
                }
                ArrayList<Integer> sortedStrikesList = new ArrayList<>(strikeList);
                Collections.sort(sortedStrikesList);
                redisIndexStrikePrices.put(key, sortedStrikesList);
            });
        } catch (Exception e) {
          //  logger.error("error in the getRedisMasterResponseFO ,"+e.getMessage());
        }
    }


    public List<MasterResponseFO> fetchAllMasterResponses(String key, ExpiryDatesDTO value) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        List<MasterResponseFO> redisMasterResponse = new ArrayList<>();
        try {
            String timeStamp = value.getCurrentWeek().format(formatter);
            List<MasterResponseFO> limited = masterRepository.fetchMasterResponseFO(key + "_" + timeStamp);
            if (limited != null)
                redisMasterResponse.addAll(limited);

            timeStamp = value.getNextWeek().format(formatter);
            limited = masterRepository.fetchMasterResponseFO(key + "_" + timeStamp);
            if (limited != null)
                redisMasterResponse.addAll(limited);

            timeStamp = value.getCurrentMonth().format(formatter);
            limited = masterRepository.fetchMasterResponseFO(key + "_" + timeStamp);
            if (limited != null)
                redisMasterResponse.addAll(limited);

            timeStamp = value.getNextMonth().format(formatter);
            limited = masterRepository.fetchMasterResponseFO(key + "_" + timeStamp);
            if (limited != null)
                redisMasterResponse.addAll(limited);
        }catch (Exception e) {
           // logger.error("Error fetching Redis master data, "+e.getMessage());
        }
        return redisMasterResponse;
    }

    private String getIndexName(String indexName) {
        String finalName = indexName;
        String[] parts = indexName.split("_");
        if (parts.length > 2) {
            if (parts[2].equalsIgnoreCase("FUTIDX") || parts[2].equalsIgnoreCase("IF"))
                finalName = parts[1] + "_FUTURE";
            else
                finalName = parts[1];
        }
        return finalName;
    }


    public String getExpiryShotDateByIndex(String date) {

        return LocalDateTime.parse(date).toLocalDate().toString();
    }

    public MasterResponseFO getMasterResponse(String key) {
        try {
            MasterResponseFO responseFO = masterRepository.find(key);
            if (responseFO != null)
                return responseFO;
//            throw new RuntimeException("no Master data found for key = "+key);
        } catch (Exception e) {
       //     e.printStackTrace();
//            logger.error("Error fetching master data for key: " +key );
//            logger.error(e.getMessage());

        }
//        return null;
        return null;
    }

}
