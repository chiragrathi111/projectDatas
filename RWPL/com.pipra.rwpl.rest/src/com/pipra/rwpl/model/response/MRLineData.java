package com.pipra.rwpl.model.response;

import java.math.BigDecimal;

public class MRLineData {
	private int productID;
	private String productName;
	private int locatorID;
	private String locatorName;
	private int mInoutLineID;
	private BigDecimal movementQty;
	private int uomID;
	private int cOrderLineID;

	public int getProductID() {
		return productID;
	}

	public void setProductID(int productID) {
		this.productID = productID;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public int getLocatorID() {
		return locatorID;
	}

	public void setLocatorID(int locatorID) {
		this.locatorID = locatorID;
	}

	public String getLocatorName() {
		return locatorName;
	}

	public void setLocatorName(String locatorName) {
		this.locatorName = locatorName;
	}

	public int getmInoutLineID() {
		return mInoutLineID;
	}

	public void setmInoutLineID(int mInoutLineID) {
		this.mInoutLineID = mInoutLineID;
	}

	public BigDecimal getMovementQty() {
		return movementQty;
	}

	public void setMovementQty(BigDecimal movementQty) {
		this.movementQty = movementQty;
	}

	public int getUomID() {
		return uomID;
	}

	public void setUomID(int uomID) {
		this.uomID = uomID;
	}

	public int getcOrderLineID() {
		return cOrderLineID;
	}

	public void setcOrderLineID(int cOrderLineID) {
		this.cOrderLineID = cOrderLineID;
	}

}