package com.market.feed.model;
import lombok.Data;

@Data
public class MarketData {

    int exchangeSegment;
    int exchangeInstrumentId ;
    long exchangeTimestamp  ;
    long lut ;
    double LTP;
    double open;
    double high;
    double low;
    double close;
    double averageTradedPrice;
    int marketType;
    double delta;
    double IV;
}
