package com.pipra.rwpl.model.response;

public class SODetailLocator {
	private int quantityAvailable;
	private int locatorId;
	private String locatorName;

	public int getQuantityAvailable() {
		return quantityAvailable;
	}

	public void setQuantityAvailable(int quantityAvailable) {
		this.quantityAvailable = quantityAvailable;
	}

	public int getLocatorId() {
		return locatorId;
	}

	public void setLocatorId(int locatorId) {
		this.locatorId = locatorId;
	}

	public String getLocatorName() {
		return locatorName;
	}

	public void setLocatorName(String locatorName) {
		this.locatorName = locatorName;
	}
}