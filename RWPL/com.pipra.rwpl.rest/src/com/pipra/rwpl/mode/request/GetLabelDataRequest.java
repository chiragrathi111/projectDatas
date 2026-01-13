package com.pipra.rwpl.mode.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties; 

@JsonIgnoreProperties(ignoreUnknown = true)
public class GetLabelDataRequest {

	private boolean isPutAway;
	private boolean receiving;
	private boolean internalMove;
	private boolean pickList;
	private boolean finalDispatch;
	private String labelType;
	private String labelUUID;
	private boolean labelAvailableInWarehouse;

	public boolean isPutAway() {
		return isPutAway;
	}

	public void setPutAway(boolean isPutAway) {
		this.isPutAway = isPutAway;
	}

	public boolean isReceiving() {
		return receiving;
	}

	public void setReceiving(boolean receiving) {
		this.receiving = receiving;
	}

	public boolean isInternalMove() {
		return internalMove;
	}

	public void setInternalMove(boolean internalMove) {
		this.internalMove = internalMove;
	}

	public boolean isPickList() {
		return pickList;
	}

	public void setPickList(boolean pickList) {
		this.pickList = pickList;
	}

	public boolean isFinalDispatch() {
		return finalDispatch;
	}

	public void setFinalDispatch(boolean finalDispatch) {
		this.finalDispatch = finalDispatch;
	}

	public String getLabelType() {
		return labelType;
	}

	public void setLabelType(String labelType) {
		this.labelType = labelType;
	}

	public String getLabelUUID() {
		return labelUUID;
	}

	public void setLabelUUID(String labelUUID) {
		this.labelUUID = labelUUID;
	}

	public boolean isLabelAvailableInWarehouse() {
		return labelAvailableInWarehouse;
	}

	public void setLabelAvailableInWarehouse(boolean labelAvailableInWarehouse) {
		this.labelAvailableInWarehouse = labelAvailableInWarehouse;
	}

}
