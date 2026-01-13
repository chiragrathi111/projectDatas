package com.pipra.rwpl.mode.request;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties; 

@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateShipmentRequest {

	private int cOrderId;
	private String status;
	private String description;
	private List<ShipmentLine> shipmentLines;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getcOrderId() {
		return cOrderId;
	}

	public void setcOrderId(int cOrderId) {
		this.cOrderId = cOrderId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<ShipmentLine> getShipmentLines() {
		return shipmentLines;
	}

	public void setShipmentLines(List<ShipmentLine> shipmentLines) {
		this.shipmentLines = shipmentLines;
	}
}