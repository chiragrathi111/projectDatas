package com.pipra.rwpl.mode.request;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties; 

@JsonIgnoreProperties(ignoreUnknown = true)
public class MRCreateRequest {
	private int warehouseId;
	private int bPartnerId;
	private int cOrderId;
	private String description;
	private String movementDate;
	private List<MRLineRequest> mrLines;

	public int getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(int warehouseId) {
		this.warehouseId = warehouseId;
	}

	public int getbPartnerId() {
		return bPartnerId;
	}

	public void setbPartnerId(int bPartnerId) {
		this.bPartnerId = bPartnerId;
	}

	public int getcOrderId() {
		return cOrderId;
	}

	public void setcOrderId(int cOrderId) {
		this.cOrderId = cOrderId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getMovementDate() {
		return movementDate;
	}

	public void setMovementDate(String movementDate) {
		this.movementDate = movementDate;
	}

	public List<MRLineRequest> getMrLines() {
		return mrLines;
	}

	public void setMrLines(List<MRLineRequest> mrLines) {
		this.mrLines = mrLines;
	}
}