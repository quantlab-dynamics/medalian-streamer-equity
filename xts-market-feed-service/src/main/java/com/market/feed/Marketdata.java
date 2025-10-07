//package com.market.feed;
//
//import java.io.IOException;
//import java.io.UnsupportedEncodingException;
//import java.time.Instant;
//import java.time.LocalDateTime;
//import java.time.ZoneId;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import java.util.concurrent.atomic.AtomicBoolean;
//import java.util.stream.Collectors;
//
//import com.market.feed.service.MarketDepthService;
//import com.market.feed.service.MasterService;
//import com.market.feed.service.TouchLineService;
//import com.sf.xts.api.sdk.marketdata.master.MasterResponseFO;
//import com.sf.xts.api.sdk.marketdata.response.*;
//import org.apache.log4j.BasicConfigurator;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import com.sf.xts.api.sdk.main.api.APIException;
//import com.sf.xts.api.sdk.main.api.MarketdataClient;
//import com.sf.xts.api.sdk.marketdata.Instrument;
//import com.sf.xts.api.sdk.marketdata.XTSAPIMarketdataEvents;
//import com.sf.xts.api.sdk.marketdata.InstrumentByID.InstrumentByIDResponse;
//import com.sf.xts.api.sdk.marketdata.clientConfig.ClientConfigResponse;
//import com.sf.xts.api.sdk.marketdata.expiryDate.ExpiryDateRequest;
//import com.sf.xts.api.sdk.marketdata.expiryDate.ExpiryDateResponse;
//import com.sf.xts.api.sdk.marketdata.indexList.IndexListResponse;
//import com.sf.xts.api.sdk.marketdata.subscriptionRequest.SubscribeResponse;
//import org.springframework.stereotype.Service;
//
//@Service
//public class Marketdata implements XTSAPIMarketdataEvents {
//
//	private final MasterService masterService;
//
//	private final TouchLineService touchLineService;
//
//	private final MarketDepthService marketDepthService;
//
//	MarketdataClient marketDataClient;
//
//	public static Logger logger = LoggerFactory.getLogger(Marketdata.class);
//
//	public Marketdata(MasterService masterService, TouchLineService touchLineService,
//			MarketDepthService marketDepthService) {
//		this.masterService = masterService;
//		this.touchLineService = touchLineService;
//		this.marketDepthService = marketDepthService;
//		// this.messagingTemplate = messagingTemplate;
//	}
//
//	public void invokeMarketfeed() throws InterruptedException {
//		BasicConfigurator.configure();
//
//		try {
//			marketDataClient = new MarketdataClient(this);
//
//			// LOGIN
//			/**
//			 * it login with provided secretKey, appKey
//			 * 
//			 * @param secretKey provided by the broker
//			 * @param appKey    provided by the broker
//			 * @return authToken, userID
//			 * @throws APIException catch the exception in your implementation catch the
//			 *                      exception in your implementation
//			 */
//
//			String userID = "DEMO20";
//			String password = "Raj@123";
//			String pin = "111111";
//			// String secretKey = "Xifl762#uh";
//			// String appKey = "ce7d465a8072a6caf9a933";
//			marketDataClient.LoginforBigul(userID, password);
//			marketDataClient.Login(userID, pin);
//			// FINDOC
//			if (MarketdataClient.authToken == null) {
//				logger.error(".....Login error......");
//				return;
//			}
//
//			// CLIENTCONFIG
//			/**
//			 * it return client config
//			 * 
//			 * @return Map object of ClientConfigResponse
//			 * @throws APIException catch the exception in your implementation catch the
//			 *                      exception in your implementation
//			 */
//
//			try {
//				ClientConfigResponse clientConfigResponse = marketDataClient.getClientConfig();
//				logger.info("ClientConfigResponse : " + clientConfigResponse.toString());
//				logger.info("ClientConfigResponse ExchangeSegment"
//						+ clientConfigResponse.getResult().getExchangeSegments());
//				logger.info(
//						"ClientConfigResponse InstrumentType" + clientConfigResponse.getResult().getInstrumentType());
//
//			} catch (APIException e) {
//				logger.info(e.toString());
//			}
//
//			/*
//			 * //OHLC
//			 *//**
//				 * it return ohlc
//				 * 
//				 * @param exchangeSegment      like NSECM
//				 * @param exchangeInstrumentID like 22
//				 * @param startTime            May 18 2020 090000
//				 * @param endTime              May 21 2020 150000
//				 * @param compressionValue     60
//				 * @return Map return object of OHLCResponse
//				 * @throws APIException                 catch the exception in your
//				 *                                      implementation catch the exception in
//				 *                                      your implementation
//				 * @throws UnsupportedEncodingException
//				 */
//
//			/*
//			 * try { OHLCRequest ohlcRequest = new OHLCRequest() { { exchange = "NSECM";
//			 * exchangeInstrumentId = 26000; startTime = "Aug 03 2020 090000"; endTime =
//			 * "Aug 03 2020 150000"; compressionType = 300; } }; OHLCResponse ohlcResponse =
//			 * marketDataClient.getOHLC(ohlcRequest); logger.info("OhlcResponse : " +
//			 * ohlcResponse.getResult().toString()); logger.info("OhlcDataResponse : " +
//			 * ohlcResponse.getResult().getDataReponse()); String[] ohlcData =
//			 * ohlcResponse.getResult().getDataReponse().split(","); String[] ohlc =
//			 * ohlcData[0].split("\\|"); LocalDateTime datetime =
//			 * convertEpochMilliesToDateTime(Integer.parseInt(ohlc[0]));
//			 * logger.info("Ohlc DateTime : " + datetime + " \n Open : " + ohlc[1] +
//			 * " \n High : " + ohlc[2] + " \n Low : " + ohlc[3] + " \n Close : " + ohlc[4] +
//			 * " \n Volume : " + ohlc[5]);
//			 * 
//			 * } catch (APIException e) { logger.info(e.toString()); }
//			 */
//
//			/*
//			 * MASTER // /** // * it returns master of provided exchangesegmentList //
//			 * * @param exchangeSegmentList like [NSECM, NSEFO] // * @return Map Object of
//			 * MasterResponse // * @throws APIException catch the exception in your
//			 * implementation //
//			 */
//
//			try {
//				String[] exchanges = new String[] { "NSEFO" };
//
//				List<MasterResponseFO> masterResponse = marketDataClient.getMaster(exchanges);
//				// ExcelExporter.exportToExcel(masterResponse,
//				// "C:\\\\softwares\\\\workspace\\\\master.xlsx");
//				masterService.saveResponseList("masterData", masterResponse);
//				/*
//				 * long startTime = System.nanoTime(); List<MasterResponseFO>
//				 * RedismasterResponse =
//				 * masterService.getResponseList("NIFTY_2024-11-21T14:30:00"); long endTime =
//				 * System.nanoTime(); long durationInMillis = (endTime - startTime) / 1_000_000;
//				 * // Convert to milliseconds System.out.println("Time taken: " +
//				 * durationInMillis + " ms"); logger.info("RedismasterResponse : " +
//				 * RedismasterResponse.toString());
//				 */
//				// TouchlineBinaryResposne touchlineBinaryResposne =
//				// touchLineService.getTouchLine("TL_49018");
//				// onMarketDataResponseTouchLine(touchlineBinaryResposne);
//			} catch (Exception e) {
//				logger.info(e.toString());
//			}
//
//			// SUBSCRIBE MARKETDATA
//			/**
//			 * it subscribe list on instrument provided
//			 * 
//			 * @param instrumentList list of subscribe instrument
//			 * @return Map Object of SubscribeResponse
//			 * @throws APIException                 catch the exception in your
//			 *                                      implementation
//			 * @throws UnsupportedEncodingException
//			 */
//
//			// List<MasterResponseFO> RedismasterResponseFiltered =
//			// RedismasterResponse.stream().filter(o->o.getName().equalsIgnoreCase("NIFTY")).toList();
//			// 26000 -- 1 -- LTP - + 10% - 10%
//
//			String[] instrumentArraySubscribe = "26000,26001,26005".split(",");
//			List<Instrument> instrumentListSubscribe = new ArrayList<Instrument>();
//			for (String instrumentId : instrumentArraySubscribe) {
//				Instrument instruments = new Instrument();
//				instruments.setExchangeInstrumentID(Integer.parseInt(instrumentId));
//				instruments.setExchangeSegment(1);
//				instrumentListSubscribe.add(instruments);
//			}
//			Thread.sleep(2000);
//			/*
//			 * try { SubscribeResponse subscribeMarketDataResponse = marketDataClient
//			 * .subscribeMarketDataEvent(instrumentListSubscribe);
//			 * logger.info("MarketdataSubscribeResponse : " +
//			 * subscribeMarketDataResponse.getResult().getListQuotes().toString());
//			 * 
//			 * } catch (APIException e) { logger.info(e.toString()); }
//			 */
//
//			// SUBSCRIBE TOUCHLINE
//			/**
//			 * it subscribe to TouchLine event on instrument provided
//			 * 
//			 * @param instrumentList list of subscribe instrument
//			 * @return Map Object of SubscribeResponse
//			 * @throws APIException                 catch the exception in your
//			 *                                      implementation
//			 * @throws UnsupportedEncodingException
//			 */
//
//			Thread.sleep(2000);
//			try {
//				SubscribeResponse subscribeTouchLineResponse = marketDataClient
//						.subscribeTouchLineEvent(instrumentListSubscribe);
//				logger.info("TouchLineSubscribeResponse : " + subscribeTouchLineResponse.getResult().getListQuotes());
//
//			} catch (APIException e) {
//				logger.info(e.toString());
//			}
//
////
////		//			//SUBSCRIBE CANDLEDATAEVENT
////			/**
////			 * it subscribe to CandleData event on instrument provided
////			 * @param instrumentList list of subscribe instrument
////			 * @return Map Object of SubscribeResponse
////			 * @throws APIException catch the exception in your implementation
////			 * @throws UnsupportedEncodingException
////			 */
////			Thread.sleep(2000);
////			try {
////				SubscribeResponse subscribeCandleDataResponse = marketDataClient.subscribeCandleDataEvent(instrumentListSubscribe);
////				logger.info("CandleDataSubscribeResponse : " + subscribeCandleDataResponse. getResult().getListQuotes().toString());
////
////
////			} catch (APIException e) {
////				logger.info(e.toString());
////			}
////
////			//SUBSCRIBE OPENINTEREST
//
////			 * it subscribe to OpenInterest event on instrument provided
////			 * @param instrumentList list of subscribe instrument
////			 * @return Map Object of SubscribeResponse
////			 * @throws APIException catch the exception in your implementation
////			 * @throws UnsupportedEncodingException
//			/*
//			 * Thread.sleep(2000); try { SubscribeResponse subscribeOIResponse =
//			 * marketDataClient.subscribeOpenInterestEvent(instrumentListSubscribe); //
//			 * logger.info("OpenInterestSubscribeResponse : " + subscribeOIResponse.
//			 * getResult().getListQuotes().toString());
//			 * 
//			 * } catch (APIException e) { logger.info(e.toString()); } //
//			 */// //SUBSCRIBE INDEX
////			/**
////			 * it subscribe to Index event on instrument provided
////			 * @param instrumentList list of subscribe instrument
////			 * @return Map Object of SubscribeResponse
////			 * @throws APIException catch the exception in your implementation
////			 * @throws UnsupportedEncodingException
////			 */
////			Thread.sleep(2000);
////			try {
////				SubscribeResponse subscribeIndexResponse = marketDataClient.subscribeIndexEvent(instrumentListSubscribe);
////				logger.info("IndexSubscribeResponse : " + subscribeIndexResponse. getResult().getListQuotes().toString());
////
////
////			} catch (APIException e) {
////				logger.info(e.toString());
////			}
////
////			//EQUITYSYMBOL
////			/**
////			 * it return equity symbol
////			 * @param exchangeSegment = 1 for NSECM
////			 * @param series like EQ
////			 * @param symbol scrip name like ACC
////			 * @return Map object of EquitySymbolResponse
////			 * @throws APIException catch the exception in your implementation
////			 */
////			try {
////				EquitySymbolRequest equitySymbolRequest = new EquitySymbolRequest() {{
////					 exchangeSegment = 1;
////					 series = "EQ";
////					 symbol = "ACC";
////				}};
////				EquitySymbolResponse equitysymbolResponse = marketDataClient.getEquitySymbol(equitySymbolRequest);
////				logger.info("EquitysymbolResponse : " + equitysymbolResponse.getResult().toString());
////				if(equitysymbolResponse.getDescription().toString() .equals("ok")) {
////					logger.info("EquitysymbolResponse  Description: "+equitysymbolResponse.getResult().get(0).getDescription()+ " PriceBand "+equitysymbolResponse.getResult().get(0).getPriceBand());
////				}
////			} catch (APIException e) {
////				logger.info(e.toString());
////			}
////
////			//EXPIRYDATE
////			/**
////			 * it return expiry date
////			 * @param exchangeSegment like 2 for NSEFO
////			 * @param series like OPTIDX
////			 * @param symbol scrip name like NIFTY
////			 * @return
////			 * @return Map object of ExpiryDateResponse
////			 * @throws APIException catch the exception in your implementation
////			 */
////			 ;
//			try {
//				ExpiryDateRequest expiryDateRequest = new ExpiryDateRequest() {
//					{
//						exchangeSegment = 2;
//						series = "OPTIDX";
//						symbol = "NIFTY";
//					}
//				};
//				ExpiryDateResponse expiryDateResponse = marketDataClient.getExpiryDate(expiryDateRequest);
//				logger.info("ExpiryDateResponse : " + expiryDateResponse.getResult().toString());
//				for (int expiry = 0; expiry < expiryDateResponse.getResult().size(); expiry++) {
//					logger.info("ExpiryDateResponse :" + expiryDateResponse.getResult().get(expiry));
//				}
//			} catch (APIException e) {
//				logger.info(e.toString());
//			}
////
////			//FUTURESYMBOL
////			/**
////			 * it return future symbol
////			 * @param exchangeSegment like 2 for NSEFO
////			 * @param series like OPTIDX
////			 * @param symbol scrip name like NIFTY
////			 * @param expiryDate symbol expiryDate like 26Mar2020
////			 * @return Map object of FutureSymbolResponse
////			 * @throws APIException catch the exception in your implementation
////			 */
////
////			try {
////				FutureSymbolRequest futureSymbolRequest = new FutureSymbolRequest() {{
////					 exchangeSegment = 2;
////					 series = "FUTSTK";
////					 symbol = "ACC";
////					 expiryDate = "28May2020";
////				}};
////
////				FutureSymbolResponse futureSymbolResponse = marketDataClient.getFutureSymbol(futureSymbolRequest);
////				logger.info(" FutureSymbolResponse : " + futureSymbolResponse.getResult().toString());
////				if(futureSymbolResponse.getDescription().toString() .equals("ok")) {
////					logger.info("FutureSymbolResponse  Description: "+futureSymbolResponse.getResult().get(0).getDescription()+ " PriceBand "+futureSymbolResponse.getResult().get(0).getPriceBand());
////				}
////			} catch (APIException e) {
////				logger.info(e.toString());
////			}
////
////			//OPTIONSYMBOL
////			/**
////			 * it return option symbols
////			 * @param exchangeSegment like 2 for NSEFO
////			 * @param series like OPTIDX
////			 * @param symbol scrip name like NIFTY
////			 * @param expiryDate symbol expiryDate like 26Mar2020
////			 * @param optionType PE / CE
////			 * @param strikePrice Strike price is the price at which a derivative contract can be bought or sold
////			 * @return Map object of OptionTypeResponse
////			 * @throws APIException catch the exception in your implementation
////			 */
////
////			try {
////				OptionSymbolRequest optionSymbolRequest = new OptionSymbolRequest() {{
////					 exchangeSegment = 2;
////					 series = "OPTIDX";
////					 symbol = "NIFTY";
////					 expiryDate = "28May2020";
////					 optionType = "CE";
////					 strikePrice = 10000;
////				}};
////
////				OptionSymbolResponse optionSymbolResponse = marketDataClient.getOptionSymbol(optionSymbolRequest);
////				logger.info("OptionSymbolResponse : " + optionSymbolResponse.getResult().toString());
////				if(optionSymbolResponse.getDescription().toString() .equals("ok")) {
////					logger.info("OptionSymbolResponse  Description: "+optionSymbolResponse.getResult().get(0).getDescription()+ " PriceBand "+optionSymbolResponse.getResult().get(0).getPriceBand());
////				}
////			} catch (APIException e) {
////				logger.info(e.toString());
////			}
////
////			//OPTIONTYPE
////			/**
////			 * it return option type
////			 * @param exchangeSegment like 2 for NSEFO
////			 * @param series like OPTIDX
////			 * @param symbol scrip name like NIFTY
////			 * @param expiryDate symbol expiryDate like 26Mar2020
////			 * @return Map object of OptionTypeResponse
////			 * @throws APIException catch the exception in your implementation
////			 */
////
////			try {
////				OptionTypeRequest optionTypeRequest = new OptionTypeRequest() {{
////					 exchangeSegment = 2;
////					 series = "OPTIDX";
////					 symbol = "NIFTY";
////					 expiryDate = "28May2020";
////				}};
////				OptionTypeResponse optionTypeResponse = marketDataClient.getOptionType(optionTypeRequest);
////				logger.info("OptionTypeResponse : " + optionTypeResponse.getResult().toString());
////				for(int option=0; option<optionTypeResponse.getResult().size(); option++) {
////					logger.info("SeriesResponse :"+optionTypeResponse.getResult().get(option));
////				}
////			} catch (APIException e) {
////				logger.info(e.toString());
////			}
////
////
////			//SERIES
////			/**
////			 * it return series
////			 * @param exchangeSegment like NSECM, NSEFO
////			 * @return Map object of SeriesResponse
////			 * @throws APIException catch the exception in your implementation
////			 */
////			try {
////				SeriesResponse seriesResponse = marketDataClient.getSeries(1);
////				logger.info("SeriesResponse : " + seriesResponse.getResult().toString());
////				for(int series=0; series<seriesResponse.getResult().size(); series++) {
////					logger.info("SeriesResponse :"+seriesResponse.getResult().get(series));
////				}
////			} catch (APIException e) {
////				logger.info(e.toString());
////			}
////
////			//INDEXLIST
////			/**
////			 * it return index list
////			 * @param exchangeSegment like NSECM, NSEFO etc
////			 * @return Map object of IndexListResponse
////			 * @throws APIException catch the exception in your implementation
////			 */
////
//			/*
//			 * try { String exchange = "1"; IndexListResponse indexListResponse =
//			 * marketDataClient.getIndexList(exchange); logger.info("IndexListResponse : " +
//			 * "ExchangeSegment: " + indexListResponse.getResult().getExchangeSegment() +
//			 * " IndexList: " + indexListResponse.getResult().getIndexList()); } catch
//			 * (APIException e) { logger.info(e.toString()); }
//			 */
////
//
////			//QUOTE OpenInterest
////			/**
////			 * it return OI quotes of provided instrumentList
////			 * @param listInstrument list of instrument
////			 * @return Map Object of QuotesResponse
////			 * @throws APIException catch the exception in your implementation
////			 * @throws UnsupportedEncodingException
////			 */
////			String[] instrumentArray = "10,22".split(",");
////			List<Instrument> instrumentList = new ArrayList<Instrument>();
////			int i = 0;
////			for (String instrumentId : instrumentArray) {
////				i++;
////				Instrument instrument = new Instrument();
////				instrument.setExchangeInstrumentID(Integer.parseInt(instrumentId));
////				instrument.setExchangeSegment(1);
////				instrumentList.add(instrument);
////				if(i == 2)
////					break;
////			}
////			Thread.sleep(2000);
////			try {
////				QuotesResponse OIQuoteResponse = marketDataClient.getQuoteOpenInterest(instrumentList);
////				logger.info("OI QuotesResponse : " + OIQuoteResponse.getResult().getListQuotes());
////				JSONObject quoteobject;
////				String[] quotes = OIQuoteResponse.getResult().getListQuotes();
////				for(int j=0; j<= quotes.length;j++) {
////					quoteobject = new JSONObject(quotes[0]);
////					logger.info(j+" OIQuoteResponse ExchangeInstrumentid: "+quoteobject.get("ExchangeInstrumentID"));
////					logger.info(j+" OIQuoteResponse Bids: "+quoteobject.get("Bids"));
////					logger.info(j+" OIQuoteResponse Asks: "+quoteobject.get("Asks"));
////					}
////
////			} catch (APIException e) {
////				logger.info(e.toString());
////			}
////
////
////
////			//QUOTE MARKETDATA
////			/**
////			 * it return marketdata quotes of provided instrumentList
////			 * @param listInstrument list of instrument
////			 * @return Map Object of QuotesResponse
////			 * @throws APIException catch the exception in your implementation
////			 * @throws UnsupportedEncodingException
////			 */
////			try {
////				QuotesResponse markedtDataQuoteResponse = marketDataClient.getQuoteMarketData(instrumentList);
////				logger.info("MarketDataQuotesResponse : " + markedtDataQuoteResponse.getResult().getListQuotes());
////				JSONObject quoteobject;
////				String[] quotes = markedtDataQuoteResponse.getResult().getListQuotes();
////				for(int j=0; j<= quotes.length;j++) {
////					quoteobject = new JSONObject(quotes[0]);
////					logger.info(j+" MarketDataQuotesResponse ExchangeInstrumentid: "+quoteobject.get("ExchangeInstrumentID"));
////					logger.info(j+" MarketDataQuotesResponse Bids: "+quoteobject.get("Bids"));
////					logger.info(j+" MarketDataQuotesResponse Asks: "+quoteobject.get("Asks"));
////					}
////
////			} catch (APIException e) {
////				logger.info(e.toString());
////			}
////
////			//QUOTE TouchLine
////			/**
////			 * it return touchline quotes of provided instrumentList
////			 * @param listInstrument list of instrument
////			 * @return Map Object of QuotesResponse
////			 * @throws APIException catch the exception in your implementation
////			 * @throws UnsupportedEncodingException
////			 */
////			try {
////				QuotesResponse touchlineQuoteResponse = marketDataClient.getQuoteTouchLine(instrumentList);
////				logger.info("TouchLineQuotesResponse : " + touchlineQuoteResponse.getResult().getMdp());
////				JSONObject quoteobject;
////				String[] quotes = touchlineQuoteResponse.getResult().getListQuotes();
////				for(int j=0; j<= quotes.length;j++) {
////					quoteobject = new JSONObject(quotes[0]);
////					logger.info(j+" TouchLineQuotesResponse ExchangeInstrumentid: "+quoteobject.get("ExchangeInstrumentID"));
////					logger.info(j+" TouchLineQuotesResponse Bids: "+quoteobject.get("Bids"));
////					logger.info(j+" TouchLineQuotesResponse Asks: "+quoteobject.get("Asks"));
////					}
////
////			} catch (APIException e) {
////				logger.info(e.toString());
////			}
////
////			//QUOTE CandleData
////			/**
////			 * it return candledata quotes of provided instrumentList
////			 * @param listInstrument list of instrument
////			 * @return Map Object of QuotesResponse
////			 * @throws APIException catch the exception in your implementation
////			 * @throws UnsupportedEncodingException
////			 */
////			try {
////				QuotesResponse candleDataQuoteResponse = marketDataClient.getQuoteCandleData(instrumentList);
////				logger.info("CandleDataQuotesResponse : " + candleDataQuoteResponse.getResult().getMdp());
////				JSONObject quoteobject;
////				String[] quotes = candleDataQuoteResponse.getResult().getListQuotes();
////				for(int j=0; j<= quotes.length;j++) {
////					quoteobject = new JSONObject(quotes[0]);
////					logger.info(j+" CandleDataQuotesResponse ExchangeInstrumentid: "+quoteobject.get("ExchangeInstrumentID"));
////					logger.info(j+" CandleDataQuotesResponse Bids: "+quoteobject.get("Bids"));
////					logger.info(j+" CandleDataQuotesResponse Asks: "+quoteobject.get("Asks"));
////					}
////
////			} catch (APIException e) {
////				logger.info(e.toString());
////			}
////
////			//QUOTE Index
////			/**
////			 * it return index quotes of provided instrumentList
////			 * @param listInstrument list of instrument
////			 * @return Map Object of QuotesResponse
////			 * @throws APIException catch the exception in your implementation
////			 * @throws UnsupportedEncodingException
////			 */
////			try {
////				QuotesResponse indexQuoteResponse = marketDataClient.getQuoteIndex(instrumentList);
////				logger.info("IndexQuotesResponse : " + indexQuoteResponse.getResult().getListQuotes());
////				JSONObject quoteobject;
////				String[] quotes = indexQuoteResponse.getResult().getListQuotes();
////				for(int j=0; j<= quotes.length;j++) {
////					quoteobject = new JSONObject(quotes[0]);
////					logger.info(j+" IndexQuotesResponse ExchangeInstrumentid: "+quoteobject.get("ExchangeInstrumentID"));
////					logger.info(j+" IndexQuotesResponse Bids: "+quoteobject.get("Bids"));
////					logger.info(j+" IndexQuotesResponse Asks: "+quoteobject.get("Asks"));
////					}
////
////			} catch (APIException e) {
////				logger.info(e.toString());
////			}
////
////			//UNSUBSCRIBE MARKETDATA
////			/**
////			 * it Unsubscribe list on instrument provided
////			 * @param instrumentList list of unsubscribe instrumentID
////			 * @return Map Object of UnsubscribeResponse
////			 * @throws APIException catch the exception in your implementation
////			 * @throws UnsupportedEncodingException
////			 */
////
////			try {
////				UnsubscribeResponse unsubscribeMarketDataResponse = marketDataClient.unsubscribeMarketData(instrumentListSubscribe);
////				logger.info("UnsubscribeResponse : " + unsubscribeMarketDataResponse.getResult().toString());
////			} catch (APIException e) {
////				logger.info(e.toString());
////			}
////
////			//UNSUBSCRIBE TouchLine
////			/**
////			 * it Unsubscribe Touchline on instrument provided
////			 * @param instrumentList list of unsubscribe instrumentID
////			 * @return Map Object of UnsubscribeResponse
////			 * @throws APIException catch the exception in your implementation
////			 * @throws UnsupportedEncodingException
////			 */
////
////			try {
////				UnsubscribeResponse unsubscribeTouchLineResponse = marketDataClient.unsubscribeTouchLine(instrumentListSubscribe);
////				logger.info("UnsubscribeResponse TouchLine : " + unsubscribeTouchLineResponse.getResult().toString());
////			} catch (APIException e) {
////				logger.info(e.toString());
////			}
////
////			//UNSUBSCRIBE CandleData
////			/**
////			 * it Unsubscribe CandleData on instrument provided
////			 * @param instrumentList list of unsubscribe instrumentID
////			 * @return Map Object of UnsubscribeResponse
////			 * @throws APIException catch the exception in your implementation
////			 * @throws UnsupportedEncodingException
////			 */
////
////			try {
////				UnsubscribeResponse unsubscribeCandleDateResponse = marketDataClient.unsubscribeCandleData(instrumentListSubscribe);
////				logger.info("UnsubscribeResponse CandleData : " + unsubscribeCandleDateResponse.getResult().toString());
////			} catch (APIException e) {
////				logger.info(e.toString());
////			}
////
////			//UNSUBSCRIBE INDEX
////			/**
////			 * it Unsubscribe Index on instrument provided
////			 * @param instrumentList list of unsubscribe instrumentID
////			 * @return Map Object of UnsubscribeResponse
////			 * @throws APIException catch the exception in your implementation
////			 * @throws UnsupportedEncodingException
////			 */
////
////			try {
////				UnsubscribeResponse unsubscribeIndexResponse = marketDataClient.unsubscribeIndex(instrumentListSubscribe);
////				logger.info("UnsubscribeResponse Index : " + unsubscribeIndexResponse.getResult().toString());
////			} catch (APIException e) {
////				logger.info(e.toString());
////			}
////
////			//UNSUBSCRIBE OPENINTEREST
////			/**
////			 * it Unsubscribe OpenInterest on instrument provided
////			 * @param instrumentList list of unsubscribe instrumentID
////			 * @return Map Object of UnsubscribeResponse
////			 * @throws APIException catch the exception in your implementation
////			 * @throws UnsupportedEncodingException
////			 */
////
////			try {
////				UnsubscribeResponse unsubscribeOIResponse = marketDataClient.unsubscribeOpenInterest(instrumentListSubscribe);
////				logger.info("UnsubscribeResponse OpenInterest : " + unsubscribeOIResponse.getResult().toString());
////			} catch (APIException e) {
////				logger.info(e.toString());
////			}
////
////
////
////
////			//SEARCHINSTRUMENT
////			/**
////			 * it search instrument by passing scrip name
////			 * @param searchString it should be scrip name trsder want to search
////			 * @return Map Object of SearchInstrumentResponse
////			 * @throws APIException catch the exception in your implementation
////			 */
////			try {
////				String searchStr = "ACCORD";
////				SearchInstrumentResponse searchInstrumentResponse = marketDataClient.searchInstrument(searchStr);
////				logger.info("SearchInstrumentResponse : " + searchInstrumentResponse.getResult()[0].toString());
////			} catch (APIException e) {
////				logger.info(e.toString());
////			}
////
////			//INSTRUMENTBYID
////			/**
////			 * it provide instrument details using it ID
////			 * @param instrumentList list if instrumentID for which trader wants it details
////			 * @return Map Object of InstrumentByIDResponse
////			 * @throws APIException catch the exception in your implementation
////			 */
////			String[] instrumentArray1 = "2885,22".split(",");
////			List<Instrument> instrumentList1 = new ArrayList<Instrument>();
////			int j1 = 0;
////			for (String instrumentId : instrumentArray1) {
////				j1++;
////				Instrument instruments = new Instrument();
////				instruments.setExchangeInstrumentID(Integer.parseInt(instrumentId));
////				instruments.setExchangeSegment(1);
////				instrumentList1.add(instruments);
////				if(j1 == 2)
////					break;
////			}
////			Thread.sleep(2000);
////			try {
////				InstrumentByIDResponse instrumentByIDResponse = marketDataClient.searchInstrumentByID(instrumentList1);
////				logger.info("InstrumentByIDResponse : " + instrumentByIDResponse.toString());
////			} catch (APIException e) {
////				logger.info(e.toString());
////			}
////
////			//LOGOUT
////			/**
////			 * it terminate logged in session
////			 * @return boolean return true / false based on whether logout successfully or not
////			 * @throws APIException catch the exception in your implementation catch the exception in your implementation
////			 */
////			marketDataClient.Logout();
////			logger.info("LOGOUT");
//
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		} catch (APIException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//	}
//
//	@Override
//	public void onMarketDataResponseCandle(MarketDataResponseCandle marketDataResponseCandle) {
//		System.out.println("MarketDataResponseCandle  Instrumentid : "
//				+ marketDataResponseCandle.getExchangeInstrumentID() + "Exchange Segment : "
//				+ marketDataResponseCandle.getExchangeSegment() + "High : " + marketDataResponseCandle.getHigh()
//				+ " Low : " + marketDataResponseCandle.getLow() + " High : " + marketDataResponseCandle.getOpen()
//				+ " CLose : " + marketDataResponseCandle.getClose());
//	}
//
//	@Override
//	public void onMarketDataResponseDepth(MarketDataResponseDepth marketDataResponseDepth) {
//		System.out.println("MarketDataResponseDepth Asks : " + marketDataResponseDepth.getAsks() + "Bids : "
//				+ marketDataResponseDepth.getBids() + " Instrumenid : "
//				+ marketDataResponseDepth.getExchangeInstrumentID());
//	}
//
//	@Override
//	public void onMarketDataResponseIndex(MarketDataResponseIndex marketDataResponseIndex) {
//		System.out.println("MarketDataResponseIndex Indexname : " + marketDataResponseIndex.getIndexName()
//				+ " HighIndexValue : " + marketDataResponseIndex.getHighIndexValue() + " lowIndexValue : "
//				+ marketDataResponseIndex.getLowIndexValue() + " PercentageChange : "
//				+ marketDataResponseIndex.getPercentChange());
//
//	}
//
//	@Override
//	public void onMarketDataResponseOI(MarketDataResponseOI marketDataResponseOI) {
//		System.out.println("MarketDataResponseOI OpenInterest : " + marketDataResponseOI.getOpenInterest()
//				+ " InstrumentId :" + marketDataResponseOI.getExchangeInstrumentID());
//	}
//
//	private final AtomicBoolean isNiftyExecuted = new AtomicBoolean(false);
//	private final AtomicBoolean isFinniftyExecuted = new AtomicBoolean(false);
//	private final AtomicBoolean isBankNiftyExecuted = new AtomicBoolean(false);
//
//	@Override
//	public void onMarketDataResponseTouchLine(TouchlineBinaryResposne touchlineBinaryResposne) {
//
//		double LTP = touchlineBinaryResposne.getLTP();
//		List<String> indexes = Arrays.asList("NIFTY", "FINNIFTY", "BANKNIFTY");
//
//		if (touchlineBinaryResposne.getExchangeInstrumentId() == 26000 && isNiftyExecuted.compareAndSet(false, true)) {
//			// Execute logic for NIFTY if not already executed
//			logger.info("Executing logic for NIFTY");
//			ExpiryDateResponse expiryDateResponse = getExpiryDates(2, "OPTIDX", "NIFTY");
//			List<String> expiryDates = expiryDateResponse.getSortedExpiryDates();
//			subscribeInstrumentData(touchlineBinaryResposne, expiryDateResponse, "NIFTY");
//		} else if (touchlineBinaryResposne.getExchangeInstrumentId() == 26001
//				&& isFinniftyExecuted.compareAndSet(false, true)) {
//			// Execute logic for FINNIFTY if not already executed
//			logger.info("Executing logic for FINNIFTY");
//			ExpiryDateResponse expiryDateResponse = getExpiryDates(2, "OPTIDX", "BANKNIFTY");
//			subscribeInstrumentData(touchlineBinaryResposne, expiryDateResponse, "BANKNIFTY");
//		} else if (touchlineBinaryResposne.getExchangeInstrumentId() == 26005
//				&& isBankNiftyExecuted.compareAndSet(false, true)) {
//			// Execute logic for BANKNIFTY if not already executed
//			logger.info("Executing logic for BANKNIFTY");
//			ExpiryDateResponse expiryDateResponse = getExpiryDates(2, "OPTIDX", "MIDCPNIFTY");
//			subscribeInstrumentData(touchlineBinaryResposne, expiryDateResponse, "MIDCPNIFTY");
//
//		}
//		try {
//			touchLineService.saveTouchLine("TL_" + String.valueOf(touchlineBinaryResposne.getExchangeInstrumentId()),
//					touchlineBinaryResposne);
//
//		} catch (Exception e) {
//			logger.info("TouchLine Exception" + e.toString());
//		}
//		TouchlineBinaryResposne t1 = touchLineService
//				.getTouchLine(String.valueOf(touchlineBinaryResposne.getExchangeInstrumentId()));
//		logger.info("TouchLine" + touchlineBinaryResposne.toString());
//
//	}
//
//	private static MasterResponseFO mergeResponses(MasterResponseFO existing, MasterResponseFO replacement) {
//		// Example logic: Keep the existing and ignore the replacement, or combine
//		// fields as needed
//		return existing; // Modify this line to implement your custom merging logic
//	}
//
//	public void Login() {
//
//		String userID = "DEMO20";
//		String password = "Raj@123";
//		String pin = "111111";
//		try {
//			marketDataClient.LoginforBigul(userID, password);
//		} catch (APIException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		try {
//			marketDataClient.Login(userID, pin);
//		} catch (APIException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		if (MarketdataClient.authToken == null) {
//			logger.error(".....Login error......");
//			return;
//		}
//
//	}
//
//	public List<String> getIndexList() {
//		List<String> exchnageInstumentIDList = null;
//		try {
//			String exchange = "1";
//			IndexListResponse indexListResponse = marketDataClient.getIndexList(exchange);
//			List<String> indexList = indexListResponse.getResult().getIndexList();
//
//			// Read from property file
//			List<String> keywords = Arrays.asList("NIFTY 50", "NIFTY BANK", "NIFTY IT");
//
//			// Filter the indexList and extract the value after "_"
//			exchnageInstumentIDList = indexList.stream().filter(index -> keywords.stream().anyMatch(index::startsWith))
//					.map(index -> index.split("_")[1]) // Extract the value after "_"
//					.collect(Collectors.toList());
//
//			// Print the filtered and extracted values
//			System.out.println("Filtered values after '_': " + exchnageInstumentIDList);
//
//			logger.info(
//					"IndexListResponse : " + "ExchangeSegment: " + indexListResponse.getResult().getExchangeSegment()
//							+ " IndexList: " + indexListResponse.getResult().getIndexList());
//		} catch (APIException e) {
//			logger.info(e.toString());
//		}
//		return exchnageInstumentIDList;
//	}
//
//	public InstrumentByIDResponse searchInstrumentsbyId(int ExchangeSegment, String Series, String Symbol) {
//
//		InstrumentByIDResponse instrumentByIDResponse = null;
//		try {
//			List<String> exchnageInstumentIDList = getIndexList();
//			for (String exchnageInstumentID : exchnageInstumentIDList) {
//				Instrument instrument = new Instrument(); // *
//				instrument.setExchangeInstrumentID(Integer.parseInt(exchnageInstumentID));
//				instrument.setExchangeSegment(1);
//				List<Instrument> instrumentList = null;
//				instrumentByIDResponse = marketDataClient.searchInstrumentByID(instrumentList);
//				logger.info("ExpiryDateResponse : " + instrumentByIDResponse.getResult().toString());
//			}
//		} catch (APIException e) {
//			logger.info(e.toString());
//		}
//		return instrumentByIDResponse;
//	}
//
//	public ExpiryDateResponse getExpiryDates(int ExchangeSegment, String Series, String Symbol) {
//
//		ExpiryDateResponse expiryDateResponse = null;
//		try {
//			ExpiryDateRequest expiryDateRequest = new ExpiryDateRequest() {
//				{
//					exchangeSegment = ExchangeSegment;
//					series = Series;
//					symbol = Symbol;
//				}
//			};
//			expiryDateResponse = marketDataClient.getExpiryDate(expiryDateRequest);
//			logger.info("ExpiryDateResponse : " + expiryDateResponse.getResult().toString());
//			for (int expiry = 0; expiry < expiryDateResponse.getResult().size(); expiry++) {
//				logger.info("ExpiryDateResponse :" + expiryDateResponse.getResult().get(expiry));
//			}
//		} catch (APIException e) {
//			logger.info(e.toString());
//		}
//
//		return expiryDateResponse;
//	}
//
//	@Override
//	public void onMarketDataResponseMarketDepth(MarketDepthBinaryResponse marketDepthBinaryResposne) {
//
//		logger.info("marketDepthBinaryResposne" + marketDepthBinaryResposne.toString());
//		try {
//			marketDepthService.saveMarketDepth("MarketDepth", marketDepthBinaryResposne);
//
//		} catch (Exception e) {
//			logger.info("MarketDepthBinaryResponse Exception" + e.toString());
//		}
//		MarketDepthBinaryResponse md = marketDepthService.getMarketDepth("MarketDepth");
//		logger.info("MarketDepthBinaryResponse" + md.toString());
//
//	}
//
//	@Override
//	public void onMarketDataResponseOpenInterest(OpenInterestBinaryResponse openInterestBinaryResponse) {
//
//	}
//
//	public static LocalDateTime convertEpochMilliesToDateTime(int epochtime) {
//
//		LocalDateTime localDateTime = Instant.ofEpochMilli((epochtime) * 1000L).atZone(ZoneId.of("Asia/Kolkata"))
//				.toLocalDateTime();
//		return localDateTime;
//
//	}
//
//	@Override
//	public void onInstrumentPropertyChangeEvent(Object instrumentPropertyChange) {
//		// TODO Auto-generated method stub
//		System.out.println("InstrumentPropertyChange:  " + instrumentPropertyChange);
//	}
//
//	 void subscribeInstrumentData(TouchlineBinaryResposne touchlineBinaryResposne,
//			ExpiryDateResponse expiryDateResponse, String index) {
//
//		// Code to execute only once
//
//		double LTP = touchlineBinaryResposne.getLTP();
//
//		List<String> expiryDates = expiryDateResponse.getSortedExpiryDates();
//		int counter = 0;
//		if (!expiryDates.isEmpty() && expiryDates.size() >= 3)
//			counter = 3;
//		else
//			counter = expiryDates.size();
//
//		for (int j = 0; j < counter; j++) {
//			String expirayDateKey = index + "_" + expiryDates.get(j);
//			List<MasterResponseFO> RedismasterResponse = masterService.getResponseList(expirayDateKey);
//			List<MasterResponseFO> RedismasterResponsewithOutDuplicates = RedismasterResponse.stream()
//					.collect(Collectors.toMap(MasterResponseFO::getExchangeInstrumentID, // Key
//							masterResponse -> masterResponse, // Value
//							(existing, replacement) -> mergeResponses(existing, replacement) // Custom merge function
//					)).values().stream().collect(Collectors.toList());
//			;
//			/*
//			 * List<MasterResponseFO> RedismasterResponsefilteredList =
//			 * RedismasterResponsewithOutDuplicates.stream() .filter(o -> (((double)
//			 * o.getStrikePrice() > (LTP - LTP * 0.1)) && ((double) o.getStrikePrice() <
//			 * (LTP + LTP * 0.1)))) .collect(Collectors.toList());
//			 */
//			List<Instrument> instrumentListSubscribe = new ArrayList<Instrument>();
//			for (MasterResponseFO masterResponseFO : RedismasterResponsewithOutDuplicates) {
//				Instrument instruments = new Instrument();
//				instruments.setExchangeInstrumentID(masterResponseFO.getExchangeInstrumentID());
//				instruments.setExchangeSegment(masterResponseFO.getExchangeSegmentId());
//				instrumentListSubscribe.add(instruments);
//			}
//			int batchSize = 100;
//			SubscribeResponse subscribeTouchLineResponse;
//			try {
//				for (int i = 0; i < instrumentListSubscribe.size(); i += batchSize) {
//					// Determine the end index for the current batch
//					int end = Math.min(i + batchSize, instrumentListSubscribe.size());
//					// Get the sublist for the current batch
//					List<Instrument> batchList = instrumentListSubscribe.subList(i, end);
//
//					// Call the subscribe method for the current batch
//					subscribeTouchLineResponse = marketDataClient.subscribeTouchLineEvent(batchList);
//					logger.info("TouchLineSubscribeResponse for batch starting at index " + i + ": "
//							+ subscribeTouchLineResponse.getResult().getListQuotes());
//				}
//			} catch (UnsupportedEncodingException e) {
//				logger.error("Error in subscribing to touch line event: Unsupported encoding", e);
//			} catch (APIException e) {
//				logger.error("API exception occurred while subscribing: ", e);
//			}
//
//		}
//	}
//
//}
