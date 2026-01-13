package com.pipra.rwpl.mode.request;

import java.util.List;

public class MRLineRequest {
	private int productId;
	private int uomId;
	private int qnty;
	private int locator;
	private int cOrderlineId;
	private List<PackLineRequest> packLine;

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

	public int getcOrderlineId() {
		return cOrderlineId;
	}

	public void setcOrderlineId(int cOrderlineId) {
		this.cOrderlineId = cOrderlineId;
	}

	public List<PackLineRequest> getPackLine() {
		return packLine;
	}

	public void setPackLine(List<PackLineRequest> packLine) {
		this.packLine = packLine;
	}

}