package com.pipra.rwpl.model.response;

import java.util.List;

import com.pipra.rwpl.mode.request.ShipmentLine;

public class CreateShipmentResponse {
	private boolean isError;
	private String error;
	private String shipmentDocumentNumber;
	private int shipmentId;
	private List<ShipmentLine> shipmentLines;

	public boolean isError() {
		return isError;
	}

	public void setError(boolean isError) {
		this.isError = isError;
	}

	public String getErrorMessage() {
		return error;
	}

	public void setErrorMessage(String errorMessage) {
		this.error = errorMessage;
	}

	public String getShipmentDocumentNumber() {
		return shipmentDocumentNumber;
	}

	public void setShipmentDocumentNumber(String shipmentDocumentNumber) {
		this.shipmentDocumentNumber = shipmentDocumentNumber;
	}

	public int getShipmentId() {
		return shipmentId;
	}

	public void setShipmentId(int shipmentId) {
		this.shipmentId = shipmentId;
	}

	public List<ShipmentLine> getShipmentLines() {
		return shipmentLines;
	}

	public void setShipmentLines(List<ShipmentLine> shipmentLines) {
		this.shipmentLines = shipmentLines;
	}
}