package com.sf.xts.api.sdk.marketdata.util;

import com.sf.xts.api.sdk.marketdata.response.*;

import java.nio.ByteBuffer;
public class BinaryUtil {
    public static double PRICE_DIVISOR = 100.0;

    public static TouchlineBinaryResposne deserializeTouchline(ByteBuffer reader, int count) {
        TouchlineBinaryResposne touchline = new TouchlineBinaryResposne();
        count += 2;
        int messageVersion = reader.getShort();
        touchline.setMessageVersion(messageVersion);

        int applicationType = reader.getShort();
        touchline.setApplicationType(applicationType);

        long tokenID = (long) reader.getDouble(); // stays same (8 bytes double → long)
        touchline.setTokenID(tokenID);
        count += 8;

        long sequenceNumber = 0L;
        if (messageVersion >= ApplicationMessageVersion.VERSION_1_0_1_2983.value) {
            sequenceNumber = reader.getLong();
            touchline.setSequenceNumber(sequenceNumber);
            count += 8;

            int skipBytes = reader.getInt();
            touchline.setSkipBytes(skipBytes);
            count += 4;
        }

        int exchangeSegment = reader.getShort();
        touchline.setExchangeSegment(exchangeSegment);
        count += 2;

        int exchangeInstrumentId = reader.getInt();
        touchline.setExchangeInstrumentId(exchangeInstrumentId);
        count += 4;

        long exchangeTimestamp = reader.getLong();
        touchline.setExchangeTimestamp(exchangeTimestamp);
        count += 8;

        // Bid Row
        MarketDeptRowInfo bid = new MarketDeptRowInfo();
        count = BinaryUtil.deserialize(reader, count, bid);
        touchline.setBidMarketDeptRow(bid);

        // Ask Row
        MarketDeptRowInfo ask = new MarketDeptRowInfo();
        count = BinaryUtil.deserialize(reader, count, ask);
        touchline.setAskMarketDeptRow(ask);

        long lut = reader.getLong();
        touchline.setLut(lut);
        count += 8;

        // LTP changed double → int (divide by divisor)
        int rawLTP = reader.getInt();
        touchline.setLTP((double) (rawLTP / PRICE_DIVISOR));
        count += 4;

        long ltq = reader.getLong(); // uint32 → long
        touchline.setLtq(ltq);
        count += 8;

        long totalBuyQuantity = reader.getLong(); // uint32 → long
        touchline.setTotalBuyQuantity(totalBuyQuantity);
        count += 8;

        long totalSellQuantity = reader.getLong(); // uint32 → long
        touchline.setTotalSellQuantity(totalSellQuantity);
        count += 8;

        long totalTradedQuantity = reader.getLong(); // uint32 → long
        touchline.setTotalTradedQuantity(totalTradedQuantity);
        count += 8;

        // AverageTradedPrice double → int / divisor
        int rawAvgPrice = reader.getInt();
        touchline.setAverageTradedPrice(rawAvgPrice / PRICE_DIVISOR);
        count += 4;

        long lastTradedTime = reader.getLong();
        touchline.setLastTradedTime(lastTradedTime);
        count += 8;

        // PercentChange double → int / divisor
        int rawPercentChange = reader.getInt();
        touchline.setPercentChange(rawPercentChange / PRICE_DIVISOR);
        count += 4;

        int rawOpen = reader.getInt();
        touchline.setOpen(rawOpen / PRICE_DIVISOR);
        count += 4;

        int rawHigh = reader.getInt();
        touchline.setHigh(rawHigh / PRICE_DIVISOR);
        count += 4;

        int rawLow = reader.getInt();
        touchline.setLow(rawLow / PRICE_DIVISOR);
        count += 4;

        int rawClose = reader.getInt();
        touchline.setClose(rawClose / PRICE_DIVISOR);
        count += 4;

        // TotalValueTraded removed (skip reading!)

        int bbTotalBuy = reader.getShort();
        touchline.setBbTotalBuy(bbTotalBuy);
        count += 2;

        int bbTotalSell = reader.getShort();
        touchline.setBbTotalSell(bbTotalSell);
        count += 2;

        int bookType = reader.getShort();
        touchline.setBookType(bookType);
        count += 2;

        int marketType = reader.getShort();
        touchline.setMarketType(marketType);
        count += 2;

        return touchline;
    }

    public static MarketDepthBinaryResponse deserializeMarketDepthEvent(ByteBuffer buffer,int count) {

        MarketDepthBinaryResponse marketDepth= new MarketDepthBinaryResponse();
        count += 2;
        int messageVersion = buffer.getShort();
        marketDepth.setMessageVersion(messageVersion);
        int applicationType = buffer.getShort();
        marketDepth.setApplicationType(applicationType);
        Long tokenID = buffer.getLong();
        marketDepth.setTokenID(tokenID);
        count += 8;

        Long sequenceNumber = 0L;
        if (messageVersion >= ApplicationMessageVersion.VERSION_1_0_1_2983.getValue()) {
            sequenceNumber = buffer.getLong();
            count += 8;
            int skipBytes = buffer.getInt();
            marketDepth.setSkipBytes(skipBytes);
            count += 4;
        }

        int exchangeSegment = buffer.getShort();
        marketDepth.setExchangeSegment(exchangeSegment);
        count += 2;

        int exchangeInstrumentId = buffer.getInt();
        marketDepth.setExchangeInstrumentId(exchangeInstrumentId);
        count += 4;

        Long exchangeTimestamp = buffer.getLong();
        marketDepth.setExchangeTimestamp(exchangeTimestamp);
        count += 8;

        int bidCount = buffer.getInt();
        marketDepth.setBidCount(bidCount);
        count += 4;

        for (int x = 0; x < bidCount; x++) {
            MarketDeptRowInfo md = new MarketDeptRowInfo();
            count = deserialize(buffer, count,md);
        }

        int askCount = buffer.getInt();
        count += 4;

        for (int x = 0; x < askCount; x++) {
            MarketDeptRowInfo md = new MarketDeptRowInfo();
            count = deserialize(buffer, count,md);
        }

        MarketDeptRowInfo md = new MarketDeptRowInfo();
        count = deserialize(buffer, count,md);

        marketDepth.setBidMarketDeptRow(md);

        MarketDeptRowInfo md1 = new MarketDeptRowInfo();
        count = deserialize(buffer, count,md1);

        marketDepth.setAskMarketDeptRow(md1);

        Long lut = buffer.getLong();
        marketDepth.setLut(lut);
        count += 8;

        double LTP = buffer.getDouble();
        marketDepth.setLTP(LTP);
        count += 8;

        int ltq = buffer.getInt();
        marketDepth.setLtq(ltq);
        count += 4;

        int totalBuyQuantity =  buffer.getInt();
        marketDepth.setTotalBuyQuantity(totalBuyQuantity);
        count += 4;

        int totalSellQuantity = buffer.getInt();
        marketDepth.setTotalSellQuantity(totalSellQuantity);
        count += 4;

        int totalTradedQuantity = buffer.getInt();
        marketDepth.setTotalTradedQuantity(totalTradedQuantity);
        count += 4;

        double averageTradedPrice = buffer.getDouble();
        marketDepth.setAverageTradedPrice(averageTradedPrice);
        count += 8;

        Long lastTradedTime = buffer.getLong();
        marketDepth.setLastTradedTime(lastTradedTime);
        count += 8;

        Long percentChange = buffer.getLong();
        marketDepth.setPercentChange(percentChange);
        count += 8;

        double open = buffer.getDouble();
        marketDepth.setOpen(open);
        count += 8;

        double high = buffer.getDouble();
        marketDepth.setHigh(high);
        count += 8;

        double low = buffer.getDouble();
        marketDepth.setLow(low);
        count += 8;

        double close = buffer.getDouble();
        marketDepth.setClose(close);
        count += 8;

        double totalValueTraded = buffer.getDouble();
        marketDepth.setTotalValueTraded(totalValueTraded);
        count += 8;

        int bbTotalBuy = buffer.getShort();
        marketDepth.setBbTotalBuy(bbTotalBuy);
        count += 2;

        int bbTotalSell = buffer.getShort();
        marketDepth.setBbTotalBuy(bbTotalSell);
        count += 2;

        int bookType = buffer.getShort();
        marketDepth.setBookType(bookType);
        count += 2;

        int marketType = buffer.getShort();
        marketDepth.setMarketType(marketType);



        return marketDepth;
    }




    public static OpenInterestBinaryResponse deserializeOpenInterest(ByteBuffer buffer, int count) {
        OpenInterestBinaryResponse openInterestBinaryResponse = new OpenInterestBinaryResponse();
        count += 2;
        int messageVersion = buffer.getShort();
        openInterestBinaryResponse.setMessageVersion(messageVersion);
        int applicationType = buffer.getShort();
        openInterestBinaryResponse.setApplicationType(applicationType);
        Long tokenID = buffer.getLong();
        openInterestBinaryResponse.setTokenID(tokenID);
        count += 8;
        Long sequenceNumber = 0L;

        int skipBytes = 0;
        if (messageVersion >= ApplicationMessageVersion. VERSION_1_0_1_2983.getValue()) {
            sequenceNumber = buffer.getLong();
            openInterestBinaryResponse.setSequenceNumber(sequenceNumber);
            count += 8;
            skipBytes = buffer.getInt();
            openInterestBinaryResponse.setSkipBytes(skipBytes);
            count += 4;
        }

        int exchangeSegment = buffer.getShort();
        openInterestBinaryResponse.setExchangeSegment(exchangeSegment);
        count += 2;

        int exchangeInstrumentId = buffer.getInt();
        openInterestBinaryResponse.setExchangeInstrumentId(exchangeInstrumentId);
        count += 4;

        Long exchangeTimestamp = buffer.getLong();
        openInterestBinaryResponse.setExchangeTimestamp(exchangeTimestamp);
        count += 8;

        int marketType = buffer.getShort();
        openInterestBinaryResponse.setMarketType(marketType);
        count += 2;

        int openInterest = buffer.getInt();
        openInterestBinaryResponse.setOpenInterest(openInterest);
        count += 4;

        int underlyingExchangeSegment = buffer.getShort();
        openInterestBinaryResponse.setUnderlyingExchangeSegment(underlyingExchangeSegment);
        count += 2;

        Long underlyingInstrumentID = buffer.getLong();
        openInterestBinaryResponse.setUnderlyingInstrumentID(underlyingInstrumentID);
        count += 8;

        byte isStringExists = buffer.get();
        openInterestBinaryResponse.setIsStringExists(isStringExists);
        count += 1;

        if (isStringExists == 1) {
            byte stringLength = buffer.get();
            openInterestBinaryResponse.setStringLength(stringLength);
            count += 1;
            count += stringLength; // Note: You might want to handle the string data here.
        }

        int underlyingTotalOpenInterest = buffer.getInt();
        openInterestBinaryResponse.setUnderlyingTotalOpenInterest(underlyingTotalOpenInterest);
        count += 4;

        return openInterestBinaryResponse;
    }

    public static void deserializeLTPEvent(ByteBuffer buffer) {
    }

    public static int deserialize(ByteBuffer reader, int count, MarketDeptRowInfo info) {
        int counter = count;

        // Size: int → long
        info.setSize(reader.getLong());
        counter += 8;

        // Price: double → int (divide with divisor)
        int rawPrice = reader.getInt();
        info.setRowPrice((int) (rawPrice / PRICE_DIVISOR));
        counter += 4;

        info.setTotalOrders(reader.getInt());
        counter += 4;

        info.setBackMarketMakerFlag(reader.getShort());
        counter += 2;

        info.setCount(counter);

        return counter;
    }

    public static String convertTuple(String[] tup) {
        StringBuilder str = new StringBuilder();
        for (String s : tup) {
            str.append(s);
        }
        return str.toString();
    }

    public enum ApplicationMessageVersion {
        VERSION_1(1),
        VERSION_1_0_1_0969(2),
        VERSION_1_0_1_2879(3),
        VERSION_1_0_1_2983(4);

        private final int value;

        ApplicationMessageVersion(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }


}
