package com.market.feed.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SyntheticForList {
    private double sPrice;
    private String name;
    private double price;
    private double ancherCE;
    private double ancherPE;
    private double spotCE;
    private double spotPE;
    private double future;
    private Integer ancherATM;
    private Integer spotATM;
    private double ancherParity;
    private String timestamp;
    private int exchangeSegment;
    private int exchangeInstrumentId;
    private Long exchangeTimestamp;
    private Long lut;


}
