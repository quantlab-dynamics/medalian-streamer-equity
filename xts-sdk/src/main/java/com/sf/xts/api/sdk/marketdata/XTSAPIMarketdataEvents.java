package com.sf.xts.api.sdk.marketdata;

import com.sf.xts.api.sdk.marketdata.response.*;

/**
 * It provides an interface for XTSAPIMarketdataEvents
 *
 * @author SymphonyFintech
 */
public interface XTSAPIMarketdataEvents {

	/**
	 * it provides marketDataResponseCandle
	 * @param
	 */
	void onMarketDataResponseTouchLine(TouchlineBinaryResposne touchlineBinaryResposne);

	void onMarketDataResponseMarketDepth(MarketDepthBinaryResponse marketDepthBinaryResposne);

	void onMarketDataResponseOpenInterest(OpenInterestBinaryResponse openInterestBinaryResponse);/**
	 * it provides marketDataResponseCandle
	 * @param marketDataResponseCandle marketDataResponseCandle object
	 */
	void onMarketDataResponseCandle(MarketDataResponseCandle marketDataResponseCandle);

	/**
	 * it provides marketDataResponseDepth
	 * @param marketDataResponseDepth  object of marketDataResponseDepth
	 */
	void onMarketDataResponseDepth(MarketDataResponseDepth marketDataResponseDepth);

	/**
	 * it provides marketDataResponseIndex
	 * @param marketDataResponseIndex  object of marketDataResponseIndex
	 */
	void onMarketDataResponseIndex(MarketDataResponseIndex marketDataResponseIndex);

	/**
	 * it provides marketDataResponseOI
	 * @param marketDataResponseOI  object of marketDataResponseOI
	 */
	void onMarketDataResponseOI(MarketDataResponseOI marketDataResponseOI);

	void onInstrumentPropertyChangeEvent(Object args);

	void onDisconnect();

}
