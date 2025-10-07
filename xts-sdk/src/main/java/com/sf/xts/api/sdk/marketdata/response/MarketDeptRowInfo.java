package com.sf.xts.api.sdk.marketdata.response;

import java.nio.ByteBuffer;

public class MarketDeptRowInfo {

    private long size;
    private double rowPrice;
    private int totalOrders;
    private short backMarketMakerFlag;
    // private ByteBuffer reader; // Assuming you have a ByteReader class similar to the one used earlier
    private int count;
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

    public short getBackMarketMakerFlag() {
        return backMarketMakerFlag;
    }

    public void setBackMarketMakerFlag(short backMarketMakerFlag) {
        this.backMarketMakerFlag = backMarketMakerFlag;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }



    public MarketDeptRowInfo() {
    }



}
