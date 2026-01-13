package com.pipra.rwpl.model.response;

public class PutAwayDetailComponent {
	private int productId;
	private String productName;
	private int cOrderlineId;
	private int mInoutLineId;
	private int quantityInRecevingLocator;
	private int totalQuantity;

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

	public int getCOrderlineId() {
		return cOrderlineId;
	}

	public void setCOrderlineId(int cOrderlineId) {
		this.cOrderlineId = cOrderlineId;
	}

	public int getMInoutLineId() {
		return mInoutLineId;
	}

	public void setMInoutLineId(int mInoutLineId) {
		this.mInoutLineId = mInoutLineId;
	}

	public int getQuantityInRecevingLocator() {
		return quantityInRecevingLocator;
	}

	public void setQuantityInRecevingLocator(int quantityInRecevingLocator) {
		this.quantityInRecevingLocator = quantityInRecevingLocator;
	}

	public int getTotalQuantity() {
		return totalQuantity;
	}

	public void setTotalQuantity(int totalQuantity) {
		this.totalQuantity = totalQuantity;
	}
}