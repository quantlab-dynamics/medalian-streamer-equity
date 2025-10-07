package com.sf.xts.api.sdk.marketdata.response;

import java.nio.ByteBuffer;

public class MarketDeptRowInfo {

    private long size;                  // changed to long
    private double rowPrice;            // protocol sends int → divided by PRICE_DIVISOR
    private int totalOrders;            // unchanged
    private int backMarketMakerFlag;    // unchanged (short in protocol)
    private int count;                  // tracking position (if needed)

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public double getRowPrice() {
        return rowPrice;
    }

    public void setRowPrice(double rowPrice) {
        this.rowPrice = rowPrice;
    }

    public int getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(int totalOrders) {
        this.totalOrders = totalOrders;
    }

    public int getBackMarketMakerFlag() {
        return backMarketMakerFlag;
    }

    public void setBackMarketMakerFlag(int backMarketMakerFlag) {
        this.backMarketMakerFlag = backMarketMakerFlag;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}

