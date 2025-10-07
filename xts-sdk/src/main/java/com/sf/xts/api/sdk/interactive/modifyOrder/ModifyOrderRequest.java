package com.sf.xts.api.sdk.interactive.modifyOrder;

public class ModifyOrderRequest {
	public String appOrderid;
	public String productType;
	public String orderType;
	public int modifiedOrderQuantity ;
	public int modifiedDisclosedQuantity;
	public double modifiedLimitPrice;
	public double modifiedStopPrice;
	public String timeInForce;
	public String orderUniqueIdentifier;

}
