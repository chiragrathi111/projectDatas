package com.pipra.rwpl.model.response;

public class ProductData {
	private int productId;
	private String productName;
	private int cOrderlineId;
	private int uomId;
	private int outstandingQnty;
	private int totalQuantity;
	private int suggestedLocator;
	private String locatorName;

	public int getProductId() {
		return productId;
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public int getcOrderlineId() {
		return cOrderlineId;
	}

	public void setcOrderlineId(int cOrderlineId) {
		this.cOrderlineId = cOrderlineId;
	}

	public int getUomId() {
		return uomId;
	}

	public void setUomId(int uomId) {
		this.uomId = uomId;
	}

	public int getOutstandingQnty() {
		return outstandingQnty;
	}

	public void setOutstandingQnty(int outstandingQnty) {
		this.outstandingQnty = outstandingQnty;
	}

	public int getTotalQuantity() {
		return totalQuantity;
	}

	public void setTotalQuantity(int totalQuantity) {
		this.totalQuantity = totalQuantity;
	}

	public int getSuggestedLocator() {
		return suggestedLocator;
	}

	public void setSuggestedLocator(int suggestedLocator) {
		this.suggestedLocator = suggestedLocator;
	}

	public String getLocatorName() {
		return locatorName;
	}

	public void setLocatorName(String locatorName) {
		this.locatorName = locatorName;
	}

}