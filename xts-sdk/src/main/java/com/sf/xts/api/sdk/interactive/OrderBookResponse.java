package com.sf.xts.api.sdk.interactive;

/**
 * Object class for OrderBookResponse
 * 
 * @author SymphonyFintech
 */
public class OrderBookResponse {

	private String LoginID;
	private String ClientID;
	private Long AppOrderID;
	private String OrderReferenceID;
	private String GeneratedBy;
	private String ExchangeOrderID;
	private String OrderCategoryType;
	private String ExchangeSegment;
	private Long ExchangeInstrumentID;
	private String OrderSide;
	private String OrderType;
	private String ProductType;
	private String TimeInForce;
	private Long OrderPrice;
	private Long OrderQuantity;
	private Long OrderStopPrice;
	private String OrderStatus;
	private String OrderAverageTradedPrice;
	private Long LeavesQuantity;
	private Long CumulativeQuantity;
	private Long OrderDisclosedQuantity;
	private String OrderGeneratedDateTime;
	private String ExchangeTransactTime;
	private String LastUpdateDateTime;
	private String OrderExpiryDate;
	private String CancelRejectReason;
	private String OrderUniqueIdentifier;
	private String OrderLegStatus;
	private Long MessageCode;
	private Long MessageVersion;
	private Long TokenID;
	private Long ApplicationType;
	private int RetryCount;

	public int getRetryCount() {
		return RetryCount;
	}

	public void setRetryCount(int retryCount) {
		RetryCount = retryCount;
	}

	/**
	 * it return loginID
	 * @return String 
	 */
	public String getLoginID() {
		return LoginID;
	}
	
	/**
	 * it set loginID
	 * @param loginID String
	 */
	public void setLoginID(String loginID) {
		LoginID = loginID;
	}
	
	/**
	 * it return clientID
	 * @return String
	 */
	public String getClientID() {
		return ClientID;
	}
	
	/**
	 * it set clientID
	 * @param clientID String
	 */
	public void setClientID(String clientID) {
		ClientID = clientID;
	}
	
	/**
	 * it return appOrdeID
	 * @return Long
	 */
	public Long getAppOrderID() {
		return AppOrderID;
	}
	
	/**
	 * it set appOrderID
	 * @param appOrderID Long
	 */
	public void setAppOrderID(Long appOrderID) {
		AppOrderID = appOrderID;
	}
	
	/**
	 * it return orderReferenceID
	 * @return String
	 */
	public String getOrderReferenceID() {
		return OrderReferenceID;
	}
	
	/**
	 * it set orderReferenceID
	 * @param orderReferenceID String
	 */
	public void setOrderReferenceID(String orderReferenceID) {
		OrderReferenceID = orderReferenceID;
	}
	
	/**
	 * it return generatedBy
	 * @return  String
	 */
	public String getGeneratedBy() {
		return GeneratedBy;
	}
	
	/**
	 * it set generatedBy
	 * @param generatedBy String
	 */
	public void setGeneratedBy(String generatedBy) {
		GeneratedBy = generatedBy;
	}
	
	/**
	 * it return exchangeOrderID
	 * @return String
	 */
	public String getExchangeOrderID() {
		return ExchangeOrderID;
	}
	
	/**
	 * it set exchangeOrderID
	 * @param exchangeOrderID String
	 */
	public void setExchangeOrderID(String exchangeOrderID) {
		ExchangeOrderID = exchangeOrderID;
	}
	
	/**
	 * it return orderCategoryType
	 * @return String
	 */
	public String getOrderCategoryType() {
		return OrderCategoryType;
	}
	
	/**
	 * it set orderCategoryType
	 * @param orderCategoryType String
	 */
	public void setOrderCategoryType(String orderCategoryType) {
		OrderCategoryType = orderCategoryType;
	}
	
	/**
	 * it return exchangeSegment
	 * @return String
	 */
	public String getExchangeSegment() {
		return ExchangeSegment;
	}
	
	/**
	 * it set exchangeSegment
	 * @param exchangeSegment String
	 */
	public void setExchangeSegment(String exchangeSegment) {
		ExchangeSegment = exchangeSegment;
	}
	
	/**
	 * it return exchangeInstrumentID
	 * @return Long
	 */
	public Long getExchangeInstrumentID() {
		return ExchangeInstrumentID;
	}
	
	/**
	 * it set exchangeInstrumentID
	 * @param exchangeInstrumentID Long
	 */
	public void setExchangeInstrumentID(Long exchangeInstrumentID) {
		ExchangeInstrumentID = exchangeInstrumentID;
	}
	
	/**
	 * it return orderSide
	 * @return String
	 */
	public String getOrderSide() {
		return OrderSide;
	}
	
	/**
	 * it set orderSide either buy or sell
	 * @param orderSide String
	 */
	public void setOrderSide(String orderSide) {
		OrderSide = orderSide;
	}
	
	/**
	 * it return orderType
	 * @return String
	 */
	public String getOrderType() {
		return OrderType;
	}
	
	/**
	 * it set orderType
	 * @param orderType String
	 */
	public void setOrderType(String orderType) {
		OrderType = orderType;
	}
	
	/**
	 * it return productType like MIS
	 * @return String
	 */
	public String getProductType() {
		return ProductType;
	}
	
	/**
	 * it set productType
	 * @param productType String
	 */
	public void setProductType(String productType) {
		ProductType = productType;
	}
	
	/**
	 * it return timeInForce
	 * @return String
	 */
	public String getTimeInForce() {
		return TimeInForce;
	}
	
	/**
	 *  it set timeInForce
	 * @param timeInForce String
	 */
	public void setTimeInForce(String timeInForce) {
		TimeInForce = timeInForce;
	}
	
	/**
	 * it return orderPrice
	 * @return Long
	 */
	public Long getOrderPrice() {
		return OrderPrice;
	}
	
	/**
	 * it set orderPrice
	 * @param orderPrice  Long
	 */
	public void setOrderPrice(Long orderPrice) {
		OrderPrice = orderPrice;
	}
	
	/**
	 * it return orderQuantity
	 * @return Long
	 */
	public Long getOrderQuantity() {
		return OrderQuantity;
	}
	
	/**
	 * it set orderQuantity
	 * @param orderQuantity Long
	 */
	public void setOrderQuantity(Long orderQuantity) {
		OrderQuantity = orderQuantity;
	}
	
	/**
	 * it return orderStopPrice
	 * @return Long
	 */
	public Long getOrderStopPrice() {
		return OrderStopPrice;
	}
	
	/**
	 * it set orderStopPrice
	 * @param orderStopPrice Long
	 */
	public void setOrderStopPrice(Long orderStopPrice) {
		OrderStopPrice = orderStopPrice;
	}
	
	/**
	 * it return orderStatus
	 * @return String
	 */
	public String getOrderStatus() {
		return OrderStatus;
	}
	
	/**
	 * it set orderStatus
	 * @param orderStatus String
	 */
	public void setOrderStatus(String orderStatus) {
		OrderStatus = orderStatus;
	}
	
	/**
	 * it return orderAverageTradedPrice
	 * @return String
	 */
	public String getOrderAverageTradedPrice() {
		return OrderAverageTradedPrice;
	}
	
	/**
	 * it set orderAverageTradedPrice
	 * @param orderAverageTradedPrice String
	 */
	public void setOrderAverageTradedPrice(String orderAverageTradedPrice) {
		OrderAverageTradedPrice = orderAverageTradedPrice;
	}
	
	/**
	 * it return leavesQuantity
	 * @return Long
	 */
	public Long getLeavesQuantity() {
		return LeavesQuantity;
	}
	
	/**
	 *  it set leavesQuantity
	 * @param leavesQuantity Long
	 */
	public void setLeavesQuantity(Long leavesQuantity) {
		LeavesQuantity = leavesQuantity;
	}
	
	/**
	 * it return cumulativeQuantity
	 * @return Long
	 */
	public Long getCumulativeQuantity() {
		return CumulativeQuantity;
	}
	
	/**
	 * it set cumulativeQuantity
	 * @param cumulativeQuantity Long
	 */
	public void setCumulativeQuantity(Long cumulativeQuantity) {
		CumulativeQuantity = cumulativeQuantity;
	}
	
	/**
	 * it return orderDisclosedQuantity
	 * @return Long
	 */
	public Long getOrderDisclosedQuantity() {
		return OrderDisclosedQuantity;
	}
	
	/**
	 * it set orderDisclosedQuantity
	 * @param orderDisclosedQuantity Long
	 */
	public void setOrderDisclosedQuantity(Long orderDisclosedQuantity) {
		OrderDisclosedQuantity = orderDisclosedQuantity;
	}
	
	/**
	 * it return orderGeneratedDateTime
	 * @return String
	 */
	public String getOrderGeneratedDateTime() {
		return OrderGeneratedDateTime;
	}
	
	/**
	 *  it set orderGeneratedDateTime
	 * @param orderGeneratedDateTime String
	 */
	public void setOrderGeneratedDateTime(String orderGeneratedDateTime) {
		OrderGeneratedDateTime = orderGeneratedDateTime;
	}
	
	/**
	 *  it return exchangeTransactTime
	 * @return String
	 */
	public String getExchangeTransactTime() {
		return ExchangeTransactTime;
	}
	
	/**
	 * it set exchangeTransactTime
	 * @param exchangeTransactTime String
	 */
	public void setExchangeTransactTime(String exchangeTransactTime) {
		ExchangeTransactTime = exchangeTransactTime;
	}
	
	/**
	 * it return LastUpdateDateTime
	 * @return String
	 */
	public String getLastUpdateDateTime() {
		return LastUpdateDateTime;
	}
	
	/**
	 * it set lastUpdateDateTime
	 * @param lastUpdateDateTime String
	 */
	public void setLastUpdateDateTime(String lastUpdateDateTime) {
		LastUpdateDateTime = lastUpdateDateTime;
	}
	
	/**
	 * it return orderExpiryDate
	 * @return String
	 */
	public String getOrderExpiryDate() {
		return OrderExpiryDate;
	}
	
	/**
	 * it set orderExpiryDate
	 * @param orderExpiryDate String
	 */
	public void setOrderExpiryDate(String orderExpiryDate) {
		OrderExpiryDate = orderExpiryDate;
	}
	
	/**
	 * it return cancelRejectReason
	 * @return String
	 */
	public String getCancelRejectReason() {
		return CancelRejectReason;
	}
	
	/** 
	 * it set cancelRejectReason
	 * @param cancelRejectReason String
	 */
	public void setCancelRejectReason(String cancelRejectReason) {
		CancelRejectReason = cancelRejectReason;
	}
	
	/**
	 * it return orderUniqueIdentifier
	 * @return String
	 */
	public String getOrderUniqueIdentifier() {
		return OrderUniqueIdentifier;
	}
	
	/**
	 * it set orderUniqueIdentifier
	 * @param orderUniqueIdentifier String
	 */
	public void setOrderUniqueIdentifier(String orderUniqueIdentifier) {
		OrderUniqueIdentifier = orderUniqueIdentifier;
	}
	
	/**
	 * it return orderLegStatus
	 * @return String
	 */
	public String getOrderLegStatus() {
		return OrderLegStatus;
	}
	
	/**
	 * it set orderLegStatus
	 * @param orderLegStatus String
	 */
	public void setOrderLegStatus(String orderLegStatus) {
		OrderLegStatus = orderLegStatus;
	}
	
	/**
	 * it return messageCode
	 * @return Long
	 */
	public Long getMessageCode() {
		return MessageCode;
	}
	
	/**
	 * it set messageCode
	 * @param messageCode Long
	 */
	public void setMessageCode(Long messageCode) {
		MessageCode = messageCode;
	}
	
	/**
	 * it return messageVersion
	 * @return Long
	 */
	public Long getMessageVersion() {
		return MessageVersion;
	}
	
	/**
	 * it set messageVersion
	 * @param messageVersion Long
	 */
	public void setMessageVersion(Long messageVersion) {
		MessageVersion = messageVersion;
	}
	
	/**
	 * it return tokenID
	 * @return Long
	 */
	public Long getTokenID() {
		return TokenID;
	}
	
	/**
	 * it set tokenID
	 * @param tokenID Long
	 */
	public void setTokenID(Long tokenID) {
		TokenID = tokenID;
	}
	
	/**
	 * it return applicationType
	 * @return Long
	 */
	public Long getApplicationType() {
		return ApplicationType;
	}
	
	/**
	 * it set applicationType
	 * @param applicationType Long
	 */
	public void setApplicationType(Long applicationType) {
		ApplicationType = applicationType;
	}
	
	@Override
	public String toString() {
		return "OrderBookResponse [LoginID=" + LoginID + ", ClientID=" + ClientID + ", AppOrderID=" + AppOrderID
				+ ", OrderReferenceID=" + OrderReferenceID + ", GeneratedBy=" + GeneratedBy + ", ExchangeOrderID="
				+ ExchangeOrderID + ", OrderCategoryType=" + OrderCategoryType + ", ExchangeSegment=" + ExchangeSegment
				+ ", ExchangeInstrumentID=" + ExchangeInstrumentID + ", OrderSide=" + OrderSide + ", OrderType=" + OrderType
				+ ", ProductType=" + ProductType + ", TimeInForce=" + TimeInForce + ", OrderPrice=" + OrderPrice
				+ ", OrderQuantity=" + OrderQuantity + ", OrderStopPrice=" + OrderStopPrice + ", OrderStatus=" + OrderStatus
				+ ", OrderAverageTradedPrice=" + OrderAverageTradedPrice + ", LeavesQuantity=" + LeavesQuantity
				+ ", CumulativeQuantity=" + CumulativeQuantity + ", OrderDisclosedQuantity=" + OrderDisclosedQuantity
				+ ", OrderGeneratedDateTime=" + OrderGeneratedDateTime + ", ExchangeTransactTime=" + ExchangeTransactTime
				+ ", LastUpdateDateTime=" + LastUpdateDateTime + ", OrderExpiryDate=" + OrderExpiryDate
				+ ", CancelRejectReason=" + CancelRejectReason + ", OrderUniqueIdentifier=" + OrderUniqueIdentifier
				+ ", OrderLegStatus=" + OrderLegStatus + ", MessageCode=" + MessageCode + ", MessageVersion="
				+ MessageVersion + ", TokenID=" + TokenID + ", ApplicationType=" + ApplicationType + ", getLoginID()="
				+ getLoginID() + ", getClientID()=" + getClientID() + ", getAppOrderID()=" + getAppOrderID()
				+ ", getOrderReferenceID()=" + getOrderReferenceID() + ", getGeneratedBy()=" + getGeneratedBy()
				+ ", getExchangeOrderID()=" + getExchangeOrderID() + ", getOrderCategoryType()=" + getOrderCategoryType()
				+ ", getExchangeSegment()=" + getExchangeSegment() + ", getExchangeInstrumentID()="
				+ getExchangeInstrumentID() + ", getOrderSide()=" + getOrderSide() + ", getOrderType()=" + getOrderType()
				+ ", getProductType()=" + getProductType() + ", getTimeInForce()=" + getTimeInForce() + ", getOrderPrice()="
				+ getOrderPrice() + ", getOrderQuantity()=" + getOrderQuantity() + ", getOrderStopPrice()="
				+ getOrderStopPrice() + ", getOrderStatus()=" + getOrderStatus() + ", getOrderAverageTradedPrice()="
				+ getOrderAverageTradedPrice() + ", getLeavesQuantity()=" + getLeavesQuantity()
				+ ", getCumulativeQuantity()=" + getCumulativeQuantity() + ", getOrderDisclosedQuantity()="
				+ getOrderDisclosedQuantity() + ", getOrderGeneratedDateTime()=" + getOrderGeneratedDateTime()
				+ ", getExchangeTransactTime()=" + getExchangeTransactTime() + ", getLastUpdateDateTime()="
				+ getLastUpdateDateTime() + ", getOrderExpiryDate()=" + getOrderExpiryDate() + ", getCancelRejectReason()="
				+ getCancelRejectReason() + ", getOrderUniqueIdentifier()=" + getOrderUniqueIdentifier()
				+ ", getOrderLegStatus()=" + getOrderLegStatus() + ", getMessageCode()=" + getMessageCode()
				+ ", getMessageVersion()=" + getMessageVersion() + ", getTokenID()=" + getTokenID()
				+ ", getApplicationType()=" + getApplicationType() + ", getClass()=" + getClass() + ", hashCode()="
				+ hashCode() + ", toString()=" + super.toString() + "]";
	}






}
