package com.pipra.rwpl.model.response;

public class MRLineResponse {
	private int mrLineId;
	private int productId;
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