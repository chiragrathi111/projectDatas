package com.pipra.rwpl.mode.request;

public class ShipmentLine {
	private int mrLineId;
	private int productId;
	private String productName;
	private int cOrderlineId;
	private int uomId;
	private int qnty;
	private int locator;

	public int getMrLineId() {
		return mrLineId;
	}

	public void setMrLineId(int mrLineId) {
		this.mrLineId = mrLineId;
	}

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

	public int getQnty() {
		return qnty;
	}

	public void setQnty(int qnty) {
		this.qnty = qnty;
	}

	public int getLocator() {
		return locator;
	}

	public void setLocator(int locator) {
		this.locator = locator;
	}
}