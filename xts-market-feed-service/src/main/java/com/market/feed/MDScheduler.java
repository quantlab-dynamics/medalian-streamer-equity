package com.market.feed;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import com.market.feed.model.Jwt;
import com.market.feed.model.MarketData;
import com.market.feed.model.SyntheticForList;
import com.market.feed.model.SyntheticPrice;
import com.market.feed.repository.SyntheticPriceListRepository;
import com.market.feed.repository.SyntheticPriceRepository;
import com.market.feed.service.*;
import com.market.feed.service.EmailService;
import com.market.feed.util.Delta;

import com.market.feed.util.ImpliedVolatility;
import com.market.feed.util.MarketFormData;
import com.market.feed.util.NewImpliedVolatility;
import com.sf.xts.api.sdk.main.api.InteractiveClient;
import com.sf.xts.api.sdk.marketdata.master.MasterResponse;
import com.sf.xts.api.sdk.marketdata.subscriptionRequest.UnsubscribeResponse;
import jakarta.annotation.PostConstruct;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;

import org.springframework.stereotype.Service;

import com.sf.xts.api.sdk.ConfigurationProvider;
import com.sf.xts.api.sdk.main.api.APIException;
import com.sf.xts.api.sdk.main.api.MarketdataClient;
import com.sf.xts.api.sdk.marketdata.Instrument;
import com.sf.xts.api.sdk.marketdata.XTSAPIMarketdataEvents;
import com.sf.xts.api.sdk.marketdata.InstrumentByID.InstrumentByIDResponse;
import com.sf.xts.api.sdk.marketdata.InstrumentByID.Result;
import com.sf.xts.api.sdk.marketdata.expiryDate.ExpiryDateRequest;
import com.sf.xts.api.sdk.marketdata.expiryDate.ExpiryDateResponse;
import com.sf.xts.api.sdk.marketdata.indexList.IndexListResponse;
import com.sf.xts.api.sdk.marketdata.master.MasterResponseFO;
import com.sf.xts.api.sdk.marketdata.response.MarketDataResponseCandle;
import com.sf.xts.api.sdk.marketdata.response.MarketDataResponseDepth;
import com.sf.xts.api.sdk.marketdata.response.MarketDataResponseIndex;
import com.sf.xts.api.sdk.marketdata.response.MarketDataResponseOI;
import com.sf.xts.api.sdk.marketdata.response.MarketDepthBinaryResponse;
import com.sf.xts.api.sdk.marketdata.response.OpenInterestBinaryResponse;
import com.sf.xts.api.sdk.marketdata.response.TouchlineBinaryResposne;
import com.sf.xts.api.sdk.marketdata.subscriptionRequest.SubscribeResponse;

@Service
public class MDScheduler implements XTSAPIMarketdataEvents {

    private MasterService masterService;

    private TouchLineService touchLineService;

    private MarketDepthService marketDepthService;

    private MarketdataClient marketDataClient;

    private RedisService redisService;

    @Value("${appKey}")
    private String appKey;

    @Value("${secretKey}")
    private String secretKey;

    @Autowired
    MarketFormData marketFormData;


    @Autowired
    private EmailService emailService;

    @Autowired
    private Environment environment;

    @Autowired
    private Delta delta;

    @Autowired
    private NewImpliedVolatility impliedVolatility;

    @Autowired
    private SyntheticPriceRepository syntheticPriceRepository;

    @Autowired
    private SyntheticPriceListRepository syntheticPriceListRepository;

    public static Map<String, String> indexNamesMap = new HashMap<>();

    public static Map<String, Double> underlyingSpotPriceMap = new HashMap<>();

    public static Map<String, String> exchangeSegmentIDMap = new HashMap<>();

    public static Map<String, String> seriesNamesMap = new HashMap<>();

    public static final Set<LocalDate> HOLIDAYS = new HashSet<>();

    public static Map<String, List<Integer>> redisIndexStrikePrices = new HashMap<>();

    public static Long subscribeCount = 0L;

    public static Logger logger = LoggerFactory.getLogger(MDScheduler.class);

    public MDScheduler(MasterService masterService, TouchLineService touchLineService, MarketDepthService marketDepthService, RedisService redisService) {
        this.masterService = masterService;
        this.touchLineService = touchLineService;
        this.marketDepthService = marketDepthService;
        this.redisService = redisService;
    }
    @PostConstruct  // Ensure this runs after Spring has injected dependencies
    public void init() {
        try {
            String profile = environment.getActiveProfiles().length > 0
                    ? environment.getActiveProfiles()[0]
                    : " ";
            System.setProperty("spring.profiles.active", profile);
            marketDataClient = new MarketdataClient(this);
        } catch (IOException e) {
            logger.error("Error occured in init() :"+ e.getMessage() );
        }
    }

    public void loadStaticProperties() {

        List<String> indexKeys = Arrays.asList(ConfigurationProvider.indexData.split(","));
        List<String> indexValues = Arrays.asList(ConfigurationProvider.indexDataMasterValues.split(","));
        List<String> seriesValues = Arrays.asList(ConfigurationProvider.seriesList.split(","));
        List<String> exchangeSegmentValues = Arrays.asList(ConfigurationProvider.exchangeSegmentList.split(","));
        if ((indexKeys.size() == indexValues.size()) && (indexKeys.size() == seriesValues.size()) && (indexKeys.size() == exchangeSegmentValues.size())) {
            for (int i = 0; i < indexKeys.size(); i++) {
                indexNamesMap.put(indexKeys.get(i).trim(), indexValues.get(i).trim());
                exchangeSegmentIDMap.put(indexKeys.get(i).trim(), exchangeSegmentValues.get(i).trim());
                seriesNamesMap.put(indexKeys.get(i).trim(), seriesValues.get(i).trim());
            }
        }
    }

    public boolean checkSwitchOverTime() {
        try {
            MarketData marketData = redisService.findMarketData("MD_" + "26000");
            long exchangeTimestamp = marketData.getExchangeTimestamp(); // example timestamp in seconds
            Instant currentTime = Instant.now();
            Instant exchangeInstant = Instant.ofEpochSecond(exchangeTimestamp);
            Duration duration = Duration.between(exchangeInstant, currentTime);
            if (duration.toMillis() < 3000) {
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            logger.error("Exception occurred in checkSwitchOverTime() method: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Scheduled(cron = "0 00 08 * * MON-FRI")
    public void flushALL() {

        try {
            redisService.flushAll();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
    @Scheduled(cron = "0 20 09 * * MON-FRI")
    public void subscribeSocket(){
        logger.info("inside subscribeSocket() ");
        Login();
    }

    @Scheduled(cron = "0 01 08 * * MON-FRI")
    public void executeMDfeed() {
        logger.info("Inside executeMDfeed() method");
        try {
         //  createSyntheticExcel();
            loadStaticProperties();
           boolean loginStatus = Login();
           if(loginStatus) {
               masterData();
               holidayCheck();
               InstrumentByIDResponse instrumentByIDResponseLists = searchInstrumentsbyId();
                if (instrumentByIDResponseLists.getResult().length > 0) {
                   for (Result result : instrumentByIDResponseLists.getResult()) {
                       String[] series = seriesNamesMap.get(result.getName()).trim().split("#");
                       for (int i = 0; i < series.length; i++) {
                           ExpiryDateResponse expiryDateResponse = getExpiryDates(Integer.parseInt(exchangeSegmentIDMap.get(result.getName())), series[i], indexNamesMap.get(result.getName()));
                           subscribeInstrumentData(result, expiryDateResponse, series[i], "y");
                       }
                       redisService.saveStringToRedis("LOT_" + indexNamesMap.get(result.getName()), String.valueOf(result.getLotSize()));
                       redisService.saveObjectToRedis("InstrumentByIDResponse" + result.getExchangeInstrumentID(), result);
                   }
               }
           }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            logger.error("Exception occured in executeMDfeed() method :" + e.getMessage());
        }

    }

    public void  createSyntheticExcel (){
        try {
            List<SyntheticForList> syntheticPrices = syntheticPriceListRepository.findAll("NIFTY");
            if (syntheticPrices != null && !syntheticPrices.isEmpty()) {
                ExcelExporter.writeToExcel(syntheticPrices, "/Users/bhargava_g/quantlab_dynamics/mfeedEngineDhani/synthetic.xlsx");
                System.out.println("Synthetic prices exported to Excel successfully.");
            } else {
                System.out.println("No synthetic prices found to export.");
            }
        } catch (IOException e) {
            System.out.println("Error exporting synthetic prices to Excel: " + e.getMessage());
        }
    }


    public boolean holidayCheck() {

        String currentTime = LocalDate.now().format(DateTimeFormatter.ofPattern("ddMMMyyyy")); // e.g., "26Jan2024"

        LocalTime startTime = LocalTime.of(9, 15); // 09:15 AM
        LocalTime endTime = LocalTime.of(15, 30); // 03:30 PM
        DayOfWeek dayOfWeek = LocalDateTime.now().getDayOfWeek();
        try {

//            boolean loginStatus = Login();
//            if (!loginStatus) return false;

            String holidayList = marketDataClient.getHolidays();
            redisService.saveStringToRedis("HOLIDAY", holidayList);
            JSONObject holidayJson = new JSONObject(holidayList);

            String exchange = "NSEFO";
            // Now check for weekdays (Monday to Friday)
            if (dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY) {

                if (holidayJson.getJSONObject("result").has(exchange) && holidayJson.getJSONObject("result").getJSONObject(exchange).has("holiday")) {

                    boolean isholiday = holidayJson.getJSONObject("result").getJSONObject(exchange).getJSONArray("holiday").toList().contains(currentTime);
                    if (!isholiday) {
                        logger.info("Today is a working day.");
                        return true;
                    }
                }
            } else if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
                if (holidayJson.getJSONObject("result").has(exchange) && holidayJson.getJSONObject("result").getJSONObject(exchange).has("working")) {
                    boolean isWorkingWeekend = holidayJson.getJSONObject("result").getJSONObject(exchange).getJSONArray("working").toList().contains(currentTime);
                    if (isWorkingWeekend) {
                        logger.info("Today is a working weekend.");
                        return true; // It's a working weekend
                    } else return false;
                }
            }
        } catch (APIException e) {
            logger.error("Exception occured in holidayCheck() method : " + e.getMessage());
            throw new RuntimeException(e);
        }

        return false;
    }


    public boolean Login() {

        int maxLoginRetries = 1;  // Maximum number of login retries
        int maxOTPValidationRetries = 1;  // Maximum number of OTP validation retries
        int loginRetryCount = 0;
        int otpRetryCount = 0;
        String description = "";
        Jwt jwt = null;
        try {

            Object object =  redisService.getObjectFromRedis("Jwt");
            if (object==null){
                String token = marketDataClient.LoginAppKey(secretKey, appKey, null, null);
                jwt = new Jwt();
                jwt.setDate(LocalDate.now());
                jwt.setJwt(token);
                jwt.setUserId(MarketdataClient.user);
                redisService.saveObjectToRedis("Jwt", jwt);
                return true;
            }else {
                Map<String, Object> map = (Map<String, Object>) object;
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate date = LocalDate.parse((String) map.get("date"), formatter);
                jwt = new Jwt();
                jwt.setJwt((String) map.get("jwt"));
                jwt.setDate(date);
                jwt.setUserId((String) map.get("userId"));
                if (jwt != null && jwt.getJwt() != null && jwt.getDate() != null && jwt.getDate().equals(LocalDate.now())) {
                    marketDataClient.LoginAppKey(secretKey, appKey, jwt.getJwt(), jwt.getUserId());
                    return false;
                } else {
                    String token = marketDataClient.LoginAppKey(secretKey, appKey, null, null);
                    jwt = new Jwt();
                    jwt.setDate(LocalDate.now());
                    jwt.setJwt(token);
                    jwt.setUserId(MarketdataClient.user);
                    redisService.saveObjectToRedis("Jwt", jwt);
                    return true;
                }
            }
        } catch (Exception e) {
            logger.error("Exception occured inside login :"+e.getMessage());
        }
        return false;

//
//		while (loginRetryCount < maxLoginRetries) {
//			try {
//				// Attempt to log in
//				String loginResponse = marketDataClient.Login(userID, password);
//				JSONObject jsonObject = new JSONObject(loginResponse);
//				description = jsonObject.optString("description");
//				if (!"success".equals(jsonObject.optString("type"))) {
//					// If login failed, send failure email and retry after delay
//					loginRetryCount++;
//				//	emailService.sendEmail("failure", loginRetryCount, description);
//					if (loginRetryCount < maxLoginRetries) {
//						// Wait for 15 minutes (900,000 milliseconds) before retrying login
//						logger.info("Login failed. Retrying in 15 minutes...");
//						Thread.sleep(900000);  // 15 minutes delay
//					}
//					continue;  // Retry login
//				}
//
//				// Login successful, process OTP validation
//				JSONObject result = jsonObject.optJSONObject("result");
//				String user = result.optString("userID");
//				otpRetryCount = 0;  // Reset OTP retries on successful login
//
//				while (otpRetryCount < maxOTPValidationRetries) {
//					// Attempt OTP validation
//					String validateOTPResponse = marketDataClient.validateOTP(userID, pin);
//					JSONObject validateOTPJson = new JSONObject(validateOTPResponse);
//					description = validateOTPJson.optString("description");
//					if ("success".equals(validateOTPJson.optString("type"))) {
//						// OTP validation successful
//					//	emailService.sendEmail("success", otpRetryCount, description);
//						return true;  // Success
//					} else {
//						// OTP validation failed, send failure email and retry after delay
//						otpRetryCount++;
//						//emailService.sendEmail("failure", otpRetryCount, description);
//
//						if (otpRetryCount < maxOTPValidationRetries) {
//							// Wait for 15 minutes before retrying OTP validation
//							logger.info("OTP validation failed. Retrying in 15 minutes...");
//							Thread.sleep(900000);  // 15 minutes delay
//						}
//					}
//				}
//				// If OTP validation fails after all retries, return false
//				//emailService.sendEmail("failure", otpRetryCount, description);
//				return false;
//
//			} catch (APIException e) {
//				logger.error("APIException during login: ", e);
//				break;  // Break the loop in case of API exceptions
//			} catch (InterruptedException e) {
//				logger.error("Retry interrupted: ", e);
//				break;  // Break the loop if sleep is interrupted
//			} catch (Exception e) {
//				logger.error("Unexpected exception during login: ", e);
//				break;  // Break the loop on other exceptions
//			}
//		}
//
//        // If login fails after all retries, return false
//        //emailService.sendEmail("failure", loginRetryCount, description);

    }

    public void masterData() {
        try {
            String[] exchanges = new String[]{"BSEFO", "NSEFO"};
            if (MarketdataClient.authToken == null) {
                logger.error(".....Master Data Error No login token......");
            }
            MasterResponse masterResponse = marketDataClient.getMaster(exchanges);
            String[] lines = masterResponse.getResult().split("\n");
            List<MasterResponseFO> masterData = new ArrayList<>();

            for (String line : lines) {
                String[] values = line.split("\\|");

                if (!values[3].contains("NIFTY") && !values[3].contains("SENSEX") && !values[3].contains("BANKEX")) {
                    continue;
                }
                if (values[0].equals("NSEFO")) {
                    MasterResponseFO masterResponseFO = buildMasterResponseFO(values);
                    masterData.add(masterResponseFO);
                } else if (values[0].equals("BSEFO")) {
                    if (values[3].contains("SENSEX") || values[3].contains("BANKEX")) {
                        MasterResponseFO masterResponseFO = buildMasterResponseFO(values);
                        masterData.add(masterResponseFO);
                    }
                }
            }

           // ExcelExporter.exportToExcel(masterData, "C:\\\\softwares\\\\master.xlsx");
            masterService.saveResponseList("MasterData", masterData);

        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

    private static MasterResponseFO mergeResponses(MasterResponseFO existing, MasterResponseFO replacement) {
        // Example logic: Keep the existing and ignore the replacement, or combine
        // fields as needed
        return existing; // Modify this line to implement your custom merging logic
    }

    public List<String> getIndexList(String exchangeSegmentID) {
        List<String> exchnageInstumentIDList = null;
        try {
            if (MarketdataClient.authToken == null) {
                logger.error(".....Error in getIndexList - auth token is null ......");
            }
            IndexListResponse indexListResponse = marketDataClient.getIndexList(exchangeSegmentID);
            List<String> indexList = indexListResponse.getResult().getIndexList();
            List<String> keywords = Arrays.asList(ConfigurationProvider.indexData.split(",")).stream().map(keyword -> keyword + "_").collect(Collectors.toList());

            if (indexList.size() > 0) redisService.saveListToRedis("INDEX_LIST_" + exchangeSegmentID, indexList);
            else logger.error("No index list returned for segment :" + exchangeSegmentID);

            exchnageInstumentIDList = indexList.stream().filter(index -> keywords.stream().anyMatch(index::startsWith)).map(index -> index.split("_")[1]) // Extract the value after "_"
                    .collect(Collectors.toList());

            logger.info("Filtered values after '_': " + exchnageInstumentIDList);

            logger.info("IndexListResponse : " + "ExchangeSegment: " + indexListResponse.getResult().getExchangeSegment() + " IndexList: " + indexListResponse.getResult().getIndexList());
        } catch (APIException e) {
            logger.error ("Exception occured inside getInexList : "+e.getMessage());
        }
        return exchnageInstumentIDList;
    }

    public InstrumentByIDResponse searchInstrumentsbyId() {

        InstrumentByIDResponse instrumentByIDResponse = null;
        try {

            List<String> nseExchnageInstumentIDList = getIndexList("1");
            List<String> bseExchnageInstumentIDList = getIndexList("11");
            List<Instrument> instrumentList = new ArrayList<>();
            for (String exchnageInstumentID : nseExchnageInstumentIDList) {
                Instrument instrument = new Instrument(); // *
                instrument.setExchangeInstrumentID(Integer.parseInt(exchnageInstumentID));
                instrument.setExchangeSegment(1);
                instrumentList.add(instrument);
            }
            for (String exchnageInstumentID : bseExchnageInstumentIDList) {
                Instrument instrument = new Instrument(); // *
                instrument.setExchangeInstrumentID(Integer.parseInt(exchnageInstumentID));
                instrument.setExchangeSegment(11);
                instrumentList.add(instrument);
            }
            instrumentByIDResponse = marketDataClient.searchInstrumentByID(instrumentList);
            logger.info("ExpiryDateResponse : " + instrumentByIDResponse.getResult().toString());
            List<Instrument> instrumentListSubscribe = new ArrayList<>();
            for (String exchnageInstumentID : nseExchnageInstumentIDList) {
                Instrument instrument = new Instrument(); // *
                instrument.setExchangeInstrumentID(Integer.parseInt(exchnageInstumentID));
                instrument.setExchangeSegment(2);
                instrumentListSubscribe.add(instrument);
            }
            for (String exchnageInstumentID : bseExchnageInstumentIDList) {
                Instrument instrument = new Instrument(); // *
                instrument.setExchangeInstrumentID(Integer.parseInt(exchnageInstumentID));
                instrument.setExchangeSegment(12);
                instrumentListSubscribe.add(instrument);
            }

//            UnsubscribeResponse  unsubscribeResponse = marketDataClient.unsubscribeTouchLine(instrumentList);
//            System.out.println("TouchLineUnSubscribeResponse : " + unsubscribeResponse.getResult());
            SubscribeResponse subscribeTouchLineResponse = marketDataClient.subscribeTouchLineEvent(instrumentList);
            logger.info("TouchLineSubscribeResponse : " + subscribeTouchLineResponse.getResult().getListQuotes());

        } catch (APIException e) {
            logger.error ("Exception occured inside searchInstrumentsbyId() : "+e.getMessage());
        } catch (UnsupportedEncodingException e) {
            logger.error ("Exception occured inside searchInstrumentsbyId() : "+e.getMessage());
            throw new RuntimeException(e);
        }
        return instrumentByIDResponse;
    }

    public ExpiryDateResponse getExpiryDates(int ExchangeSegment, String Series, String Symbol) {

        ExpiryDateResponse expiryDateResponse = null;
        try {
            ExpiryDateRequest expiryDateRequest = new ExpiryDateRequest() {
                {
                    exchangeSegment = ExchangeSegment;
                    series = Series;
                    symbol = Symbol;
                }
            };
            expiryDateResponse = marketDataClient.getExpiryDate(expiryDateRequest);
            if (expiryDateResponse.getResult().size() > 0)
                redisService.saveListToRedis("EXP_" + Symbol + "_" + Series, expiryDateResponse.getResult());
            else logger.info("NO ExpiryDateResponse for symbol  : " + Symbol);

            //List<String> expiryList = redisService.getListFromRedis("EXP_" + Symbol);

            logger.info("ExpiryDateResponse : " + expiryDateResponse.getResult().toString());

        } catch (APIException e) {
            logger.error ("Exception occured inside getExpiryDates() : "+e.getMessage());

        }

        return expiryDateResponse;
    }

    public void subscribeInstrumentData(Result result, ExpiryDateResponse expiryDateResponse, String series ,String subScribeField) {


        try {
            expiryDateResponse.setResult(expiryDateResponse.getResult().stream().distinct().collect(Collectors.toList()));
            double LTP = result.getBhavcopy().getClose();

            DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
            List<LocalDateTime> expiryDatesFormatted = expiryDateResponse.getSortedExpiryDates().stream().map(date -> LocalDateTime.parse(date, formatter))  // Convert each date string to LocalDateTime
                    .sorted()  // Sort the LocalDateTime objects
                    .collect(Collectors.toList());
            LocalDateTime currentWeek = expiryDatesFormatted.get(0);
            LocalDateTime nextWeek = expiryDatesFormatted.get(1);
            LocalDateTime lastDateInCurrentMonth = expiryDatesFormatted.stream().filter(date -> date.getYear() == currentWeek.getYear() && date.getMonth() == currentWeek.getMonth()).max(Comparator.naturalOrder()).orElse(null);
            LocalDateTime lastDateInNextMonth = expiryDatesFormatted.stream().filter(date -> date.isAfter(currentWeek) && (date.getMonthValue() != currentWeek.getMonthValue() || date.getYear() != currentWeek.getYear())).map(date -> LocalDateTime.of(date.getYear(), date.getMonth(), 1, 0, 0)).distinct().sorted().findFirst().flatMap(nextMonth -> expiryDatesFormatted.stream().filter(date -> date.getYear() == nextMonth.getYear() && date.getMonth() == nextMonth.getMonth()).max(Comparator.naturalOrder())).orElse(null);
            List<String> expiryDates = new ArrayList<>();
            expiryDates.add(currentWeek.format(formatter));
            expiryDates.add(nextWeek.format(formatter));
            assert lastDateInCurrentMonth != null;
            expiryDates.add(lastDateInCurrentMonth.format(formatter));
            assert lastDateInNextMonth != null;
            expiryDates.add(lastDateInNextMonth.format(formatter));
            expiryDates = expiryDates.stream()
                    .distinct()
                    .collect(Collectors.toList());

//            int counter = 0;
////            if (!expiryDates.isEmpty() && expiryDates.size() >= 3)
////                counter = 3;
////            else
////                counter = expiryDates.size();

            for (int j = 0; j < expiryDates.size(); j++) {

                String expiryDateKey = indexNamesMap.get(result.getName()) + "_" + expiryDates.get(j);
                List<MasterResponseFO> RedismasterResponse = masterService.getResponseList(expiryDateKey);

                List<MasterResponseFO> RedismasterResponseWithOutDuplicates = RedismasterResponse.stream().filter(MasterResponseFO -> MasterResponseFO.getExchangeInstrumentID()==48236).collect(Collectors.toList());

                if (RedismasterResponseWithOutDuplicates.size() > 0) {
                    System.out.println(RedismasterResponseWithOutDuplicates);
                }
                List<MasterResponseFO> RedismasterResponseWithOutDuplicatesEid = RedismasterResponse.stream().collect(Collectors.toMap(MasterResponseFO::getExchangeInstrumentID, // Key
                        masterResponse -> masterResponse, // Value
                        (existing, replacement) -> mergeResponses(existing, replacement) // Custom merge
                        // function
                )).values().stream().filter(MasterResponseFO -> MasterResponseFO.getSeries().equalsIgnoreCase(series)).collect(Collectors.toList());

                List<MasterResponseFO> RedismasterResponsefilteredList;

                if (series.equalsIgnoreCase("OPTIDX") || series.equalsIgnoreCase("IO")) {
                    if (LTP > 0) {
                        RedismasterResponsefilteredList = RedismasterResponseWithOutDuplicatesEid.stream().filter(o -> (((double) o.getStrikePrice() > (LTP - LTP * 0.05)) && ((double) o.getStrikePrice() < (LTP + LTP * 0.05)))).collect(Collectors.toList());
                    } else {
                        logger.warn("LTP is 0 for " + result.getName() + ". Subscribing to all instruments as fallback.");
                        RedismasterResponsefilteredList = RedismasterResponseWithOutDuplicatesEid;
                    }
                } else
                    RedismasterResponsefilteredList = RedismasterResponseWithOutDuplicatesEid.stream().filter(o -> o.getExchangeSegmentId() == 1).collect(Collectors.toMap(
                            o -> o.getExchangeInstrumentID(), // Key: Name
                            o -> o,      // Value: Person object
                            (existing, replacement) -> existing // Keep first occurrence
                    )).values().stream().toList();

                List<Instrument> instrumentListSubscribe = new ArrayList<Instrument>();
                subscribeCount = subscribeCount + RedismasterResponsefilteredList.size();
                for (MasterResponseFO masterResponseFO : RedismasterResponsefilteredList) {
                    Instrument instruments = new Instrument();
                    instruments.setExchangeInstrumentID(masterResponseFO.getExchangeInstrumentID());
                    if (indexNamesMap.get(result.getName()).equalsIgnoreCase("SENSEX") || indexNamesMap.get(result.getName()).equalsIgnoreCase("BANKEX"))
                        instruments.setExchangeSegment(12);
                    else if (series.equalsIgnoreCase("FUTIDX") || series.equalsIgnoreCase("OPTIDX")) {
                        instruments.setExchangeSegment(2);
                    } else instruments.setExchangeSegment(masterResponseFO.getExchangeSegmentId());

                    instrumentListSubscribe.add(instruments);
                }
                int batchSize = 100;


                SubscribeResponse subscribeTouchLineResponse;
                try {
                    for (int i = 0; i < instrumentListSubscribe.size(); i += batchSize) {
                        // Determine the end index for the current batch
                        int end = Math.min(i + batchSize, instrumentListSubscribe.size());
                        // Get the sublist for the current batch
                        List<Instrument> batchList = instrumentListSubscribe.subList(i, end);

                        // Call the subscribe method for the current batch
                        if (subScribeField.equalsIgnoreCase("n")){
                            UnsubscribeResponse  unsubscribeResponse = marketDataClient.unsubscribeTouchLine(batchList);
                            System.out.println("TouchLineUnSubscribeResponse : " + unsubscribeResponse.getResult());

                        }else if (subScribeField.equalsIgnoreCase("y")) {
                            try {

                                subscribeTouchLineResponse = marketDataClient.subscribeTouchLineEvent(batchList);
                                logger.info("TouchLineSubscribeResponse for batch starting at index " + i + ": " + subscribeTouchLineResponse.getResult().getListQuotes());
                                System.out.println("TouchLineSubscribeResponse for batch starting at index " + i + ": " + subscribeTouchLineResponse.getResult().getListQuotes());

                            }catch (Exception e){
                                System.out.println("subscribeTouchLineResponse Error : " + e.getMessage());
                                System.out.println(batchList);
                                UnsubscribeResponse  unsubscribeResponse = marketDataClient.unsubscribeTouchLine(batchList);
                                System.out.println("TouchLineUnSubscribeResponse : " + unsubscribeResponse.getResult());
                                subscribeTouchLineResponse = marketDataClient.subscribeTouchLineEvent(batchList);

                            }
                            logger.info("TouchLineSubscribeResponse for batch starting at index " + i + ": " + subscribeTouchLineResponse.getResult().getListQuotes());
                            System.out.println("TouchLineSubscribeResponse for batch starting at index " + i + ": " + subscribeTouchLineResponse.getResult().getListQuotes());


                        }
                    }
                } catch (UnsupportedEncodingException e) {
                    logger.error("Error in subscribing to touch line event: ", e);
                } catch (APIException e) {
                    logger.error("API exception occurred while subscribing: ", e);
                }

            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void subscribeTouchline() {
        String[] instrumentArraySubscribe = "26000,26001,26005".split(",");
        List<Instrument> instrumentListSubscribe = new ArrayList<Instrument>();
        for (String instrumentId : instrumentArraySubscribe) {
            Instrument instruments = new Instrument();
            instruments.setExchangeInstrumentID(Integer.parseInt(instrumentId));
            instruments.setExchangeSegment(1);
            instrumentListSubscribe.add(instruments);
        }
        try {
            UnsubscribeResponse  unsubscribeResponse = marketDataClient.unsubscribeTouchLine(instrumentListSubscribe);

            SubscribeResponse subscribeTouchLineResponse = marketDataClient.subscribeTouchLineEvent(instrumentListSubscribe);
            logger.info("TouchLineSubscribeResponse : " + subscribeTouchLineResponse.getResult().getListQuotes());

        } catch (APIException e) {
            logger.info(e.toString());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private void buildSyntheticPrice(TouchlineBinaryResposne touchlineBinaryResposne, String instrumentType) {

        double synthetic = marketFormData.getSyntheticPrice(instrumentType ,touchlineBinaryResposne);
        SyntheticPrice syntheticPrice = new SyntheticPrice();
        syntheticPrice.setExchangeInstrumentId(touchlineBinaryResposne.getExchangeInstrumentId());
        syntheticPrice.setPrice(synthetic);
        syntheticPrice.setName(instrumentType);
        syntheticPrice.setLut(touchlineBinaryResposne.getLut());
        syntheticPrice.setExchangeSegment(touchlineBinaryResposne.getExchangeSegment());
        syntheticPrice.setExchangeTimestamp(touchlineBinaryResposne.getExchangeTimestamp());
        syntheticPriceRepository.save(  instrumentType , syntheticPrice);
         }

    @Override
    public void onMarketDataResponseTouchLine(TouchlineBinaryResposne touchlineBinaryResposne) {


        try {
            if (touchlineBinaryResposne.getExchangeInstrumentId() == 26000) {
                buildSyntheticPrice(touchlineBinaryResposne, "NIFTY");
//                marketFormData.getSyntheticList("NIFTY", touchlineBinaryResposne);
                underlyingSpotPriceMap.put("NIFTY", touchlineBinaryResposne.getLTP());
                logger.info("NIFTY Synthetic price : " + syntheticPriceRepository.find("NIFTY"));
            } else if (touchlineBinaryResposne.getExchangeInstrumentId() == 26001) {
//                marketFormData.getSyntheticList("BANKNIFTY", touchlineBinaryResposne);
                buildSyntheticPrice(touchlineBinaryResposne, "BANKNIFTY");
                underlyingSpotPriceMap.put("BANKNIFTY", touchlineBinaryResposne.getLTP());
            } else if (touchlineBinaryResposne.getExchangeInstrumentId() == 26034) {
//                marketFormData.getSyntheticList("FINNIFTY", touchlineBinaryResposne);
                buildSyntheticPrice(touchlineBinaryResposne, "FINNIFTY");
                underlyingSpotPriceMap.put("FINNIFTY", touchlineBinaryResposne.getLTP());
            } else if (touchlineBinaryResposne.getExchangeInstrumentId() == 26065) {
                buildSyntheticPrice(touchlineBinaryResposne, "SENSEX");
//                marketFormData.getSyntheticList("SENSEX", touchlineBinaryResposne);
                underlyingSpotPriceMap.put("SENSEX", touchlineBinaryResposne.getLTP());
            } else if (touchlineBinaryResposne.getExchangeInstrumentId() == 26118) {
                buildSyntheticPrice(touchlineBinaryResposne, "BANKEX");
//                marketFormData.getSyntheticList("BANKEX", touchlineBinaryResposne);
                underlyingSpotPriceMap.put("BANKEX", touchlineBinaryResposne.getLTP());
            }

            MarketData md = new MarketData();
            md.setExchangeSegment(touchlineBinaryResposne.getExchangeSegment());
            md.setExchangeInstrumentId(touchlineBinaryResposne.getExchangeInstrumentId());
            md.setHigh(touchlineBinaryResposne.getHigh());
            md.setLow(touchlineBinaryResposne.getLow());
            md.setOpen(touchlineBinaryResposne.getOpen());
            md.setClose(touchlineBinaryResposne.getClose());
            md.setLTP(touchlineBinaryResposne.getLTP());
            md.setMarketType(touchlineBinaryResposne.getMarketType());
            md.setAverageTradedPrice(touchlineBinaryResposne.getAverageTradedPrice());
            md.setExchangeTimestamp(touchlineBinaryResposne.getExchangeTimestamp());
            md.setLut(touchlineBinaryResposne.getLut());
            if (MasterService.masterDataMap == null) {
                List<MasterResponseFO> masterResponseFOList = masterService.getResponseList("MasterData");
                MasterService.masterDataMap = new HashMap<>();
                for (MasterResponseFO masterResponseFO : masterResponseFOList) {
                    MasterService.masterDataMap.put(masterResponseFO.getExchangeInstrumentID(), masterResponseFO);
                }
            }
            MasterResponseFO masterResponseFO = MasterService.masterDataMap.get(touchlineBinaryResposne.getExchangeInstrumentId());

         //   System.out.println("Id: "+touchlineBinaryResposne.getExchangeInstrumentId());

            if (masterResponseFO != null) {
                double marketPrice = touchlineBinaryResposne.getLTP();
                int strikePrice =(int) masterResponseFO.getStrikePrice();
                double spotPrice = 0;
                if (underlyingSpotPriceMap.get(masterResponseFO.getInstrumentType()) != null) {
                    spotPrice = underlyingSpotPriceMap.get(masterResponseFO.getInstrumentType());
                    spotPrice = marketFormData.getSynthaticAtm(masterResponseFO, spotPrice);
                    LocalDateTime now = LocalDateTime.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
                    LocalDateTime formattedExpirationDate = LocalDateTime.parse(masterResponseFO.getContractExpiration(), formatter);
                    LocalDateTime newDateTime = formattedExpirationDate.withHour(15).withMinute(30).withSecond(0);
                    double totalHours = ChronoUnit.HOURS.between(LocalDateTime.now(), newDateTime);
                    double noOfDaysToExpiry = totalHours/24.0;
                    double timeToExpiry = (double) noOfDaysToExpiry / 365;
                    if (timeToExpiry <= 0) {
                        timeToExpiry = 1/(Math.expm1(6)+1);
                    }
                    double riskFreeInterestRate = 0.0675;

                    String name = masterResponseFO.getName();
                    String optionType = name.substring(name.length() - 2);
                    double iv = 0;
                    double deltaValue = 0;
                    if (optionType.equals("CE")) {
                        iv = impliedVolatility.calculateImpliedVolatility(marketPrice, spotPrice, strikePrice, timeToExpiry, riskFreeInterestRate, true);
                        if (Double.isNaN(iv)){
                            System.out.println( "IV value : "+iv);
                        }
                        deltaValue = delta.calculateDelta(spotPrice, strikePrice, timeToExpiry,iv,riskFreeInterestRate,'c');
                    } else if (optionType.equals("PE")) {
                        iv = impliedVolatility.calculateImpliedVolatility(marketPrice, spotPrice, strikePrice, timeToExpiry, riskFreeInterestRate, false);
                        deltaValue = delta.calculateDelta(spotPrice, strikePrice, timeToExpiry,iv,riskFreeInterestRate,'p');
                    }
                    md.setIV(iv);
                    md.setDelta(deltaValue);
                } else {
                    md.setIV(0.0);
                    md.setDelta(0.0);
                }
            }

            touchLineService.saveTouchLine("TL_" + String.valueOf(touchlineBinaryResposne.getExchangeInstrumentId()), touchlineBinaryResposne);
            redisService.saveMarketData("MD_" + String.valueOf(md.getExchangeInstrumentId()), md);

        } catch (Exception e) {
            logger.error("TouchLine Exception" + e.getMessage());
        }
    }

    @Override
    public void onMarketDataResponseMarketDepth(MarketDepthBinaryResponse marketDepthBinaryResposne) {

        logger.info("marketDepthBinaryResposne" + marketDepthBinaryResposne.toString());
        try {
            marketDepthService.saveMarketDepth("MarketDepth", marketDepthBinaryResposne);
        } catch (Exception e) {
            logger.info("MarketDepthBinaryResponse Exception" + e.toString());
        }
        MarketDepthBinaryResponse md = marketDepthService.getMarketDepth("MarketDepth");
        logger.info("MarketDepthBinaryResponse" + md.toString());

    }


    @Override
    public void onMarketDataResponseCandle(MarketDataResponseCandle marketDataResponseCandle) {
        System.out.println("MarketDataResponseCandle  Instrumentid : " + marketDataResponseCandle.getExchangeInstrumentID() + "Exchange Segment : " + marketDataResponseCandle.getExchangeSegment() + "High : " + marketDataResponseCandle.getHigh() + " Low : " + marketDataResponseCandle.getLow() + " High : " + marketDataResponseCandle.getOpen() + " CLose : " + marketDataResponseCandle.getClose());
    }

    @Override
    public void onMarketDataResponseDepth(MarketDataResponseDepth marketDataResponseDepth) {
        System.out.println("MarketDataResponseDepth Asks : " + marketDataResponseDepth.getAsks() + "Bids : " + marketDataResponseDepth.getBids() + " Instrumenid : " + marketDataResponseDepth.getExchangeInstrumentID());
    }

    @Override
    public void onMarketDataResponseIndex(MarketDataResponseIndex marketDataResponseIndex) {
        System.out.println("MarketDataResponseIndex Indexname : " + marketDataResponseIndex.getIndexName() + " HighIndexValue : " + marketDataResponseIndex.getHighIndexValue() + " lowIndexValue : " + marketDataResponseIndex.getLowIndexValue() + " PercentageChange : " + marketDataResponseIndex.getPercentChange());
    }

    @Override
    public void onMarketDataResponseOI(MarketDataResponseOI marketDataResponseOI) {
        System.out.println("MarketDataResponseOI OpenInterest : " + marketDataResponseOI.getOpenInterest() + " InstrumentId :" + marketDataResponseOI.getExchangeInstrumentID());
    }

    @Override
    public void onMarketDataResponseOpenInterest(OpenInterestBinaryResponse openInterestBinaryResponse) {

    }

    public static LocalDateTime convertEpochMilliesToDateTime(int epochtime) {

        LocalDateTime localDateTime = Instant.ofEpochMilli((epochtime) * 1000L).atZone(ZoneId.of("Asia/Kolkata")).toLocalDateTime();
        return localDateTime;

    }

    @Override
    public void onInstrumentPropertyChangeEvent(Object instrumentPropertyChange) {
        // TODO Auto-generated method stub
        System.out.println("InstrumentPropertyChange:  " + instrumentPropertyChange);
    }

    @Override
    public void onDisconnect() {
        logger.info("--------------------------------- Re connecting to market data server... ---------------------------------------");

        if (LocalTime.now().isAfter(LocalTime.of(9, 15)) && LocalTime.now().isBefore(LocalTime.of(15, 30))) {

            logger.info("------------------------------ Market is open, attempting to reconnect... -----------------------------------");
            this.executeMDfeed();
        }else {
            logger.info("------------------------------ Market is closed, not attempting to reconnect.... ------------------------------------------");
        }
    }

    private MasterResponseFO buildMasterResponseFO(String[] values) {
        MasterResponseFO masterResponseFO = new MasterResponseFO();

        if (values[0] != null && !values[0].isEmpty()) {
            masterResponseFO.setExchangeSegment(values[0]);
        }
        if (values[1] != null && !values[1].isEmpty()) {
            masterResponseFO.setExchangeInstrumentID(Integer.parseInt(values[1]));
        }
        if (values[2] != null && !values[2].isEmpty()) {
            masterResponseFO.setExchangeSegmentId(Integer.parseInt(values[2]));
        }
        if (values[3] != null && !values[3].isEmpty()) {
            masterResponseFO.setInstrumentType(values[3]);
        }
        if (values[4] != null && !values[4].isEmpty()) {
            masterResponseFO.setName(values[4]);
        }
        if (values[5] != null && !values[5].isEmpty()) {
            masterResponseFO.setSeries(values[5]);
        }
        if (values[6] != null && !values[6].isEmpty()) {
            masterResponseFO.setNameWithSeries(values[6]);
        }
        if (values[7] != null && !values[7].isEmpty()) {
            masterResponseFO.setInstrumentID(Long.parseLong(values[7]));
        }
        if (values[8] != null && !values[8].isEmpty()) {
            masterResponseFO.setPriceBandHigh(Double.parseDouble(values[8]));
        }
        if (values[9] != null && !values[9].isEmpty()) {
            masterResponseFO.setPriceBandLow(Double.parseDouble(values[9]));
        }
        if (values[10] != null && !values[10].isEmpty()) {
            masterResponseFO.setFreezeQty(Integer.parseInt(values[10]));
        }
        if (values[11] != null && !values[11].isEmpty()) {
            masterResponseFO.setTickSize(Double.parseDouble(values[11]));
        }
        if (values[12] != null && !values[12].isEmpty()) {
            masterResponseFO.setLotSize(Integer.parseInt(values[12]));
        }
        if (values[13] != null && !values[13].isEmpty()) {
            masterResponseFO.setMultiplier(Double.parseDouble(values[13]));
        }
        if (values[14] != null && !values[14].isEmpty()) {
            masterResponseFO.setUnderlyingInstrumentId(Integer.parseInt(values[14]));
        }
        if (values[15] != null && !values[15].isEmpty()) {
            masterResponseFO.setUnderlyingIndexName(values[15]);
        }
        if (values[16] != null && !values[16].isEmpty()) {
            masterResponseFO.setContractExpiration(values[16]);
        }

        if (values.length == 23) {

            if (values[17] != null && !values[17].isEmpty()) {
                masterResponseFO.setStrikePrice(Integer.parseInt(values[17]));
            }

            if (values[18] != null && !values[18].isEmpty()) {
                masterResponseFO.setOptionType(values[18]); // Strike Price should be at index 20
            }

            if (values[19] != null && !values[19].isEmpty()) {
                masterResponseFO.setDisplayName(values[19]);
            }

            if (values[20] != null && !values[20].isEmpty()) {
                masterResponseFO.setPriceNumerator(values[20]);
            }

            if (values[21] != null && !values[21].isEmpty()) {
                masterResponseFO.setPriceDenominator(values[21]);
            }

            // Correct index for option type (it should be at index 21)
            if (values[22] != null && !values[22].isEmpty()) {
                masterResponseFO.setName(values[22]); // Option Type should be at index 21
            }

        } else if (values.length == 21) {

            if (values[17] != null && !values[17].isEmpty()) {
                masterResponseFO.setDisplayName(values[17]);
            }

            if (values[18] != null && !values[18].isEmpty()) {
                masterResponseFO.setOptionType(values[18]);
            }

            if (values[19] != null && !values[19].isEmpty()) {
                masterResponseFO.setPriceDenominator(values[19]);
            }

            if (values[20] != null && !values[20].isEmpty()) {
                masterResponseFO.setPriceDenominator(values[20]); // Option Type should be at index 21
            }

        }
        return masterResponseFO;
    }


}
