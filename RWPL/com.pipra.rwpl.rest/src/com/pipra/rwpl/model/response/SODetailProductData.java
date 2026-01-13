package com.pipra.rwpl.model.response;

import java.util.List;

public class SODetailProductData {
	private int productId;
	private String productName;
	private int cOrderlineId;
	private int uomId;
	private int quantityPicked;
	private int quantityTotal;
	private int remainingQuantityToPick;
	private List<SODetailLocator> qntyAvailableInLocator;

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

	public int getQuantityPicked() {
		return quantityPicked;
	}

	public void setQuantityPicked(int quantityPicked) {
		this.quantityPicked = quantityPicked;
	}

	public int getQuantityTotal() {
		return quantityTotal;
	}

	public void setQuantityTotal(int quantityTotal) {
		this.quantityTotal = quantityTotal;
	}

	public int getRemainingQuantityToPick() {
		return remainingQuantityToPick;
	}

	public void setRemainingQuantityToPick(int remainingQuantityToPick) {
		this.remainingQuantityToPick = remainingQuantityToPick;
	}

	public List<SODetailLocator> getQntyAvailableInLocator() {
		return qntyAvailableInLocator;
	}

	public void setQntyAvailableInLocator(List<SODetailLocator> qntyAvailableInLocator) {
		this.qntyAvailableInLocator = qntyAvailableInLocator;
	}
}