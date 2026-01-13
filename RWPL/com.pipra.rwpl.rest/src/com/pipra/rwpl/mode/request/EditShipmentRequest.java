package com.pipra.rwpl.mode.request;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties; 

@JsonIgnoreProperties(ignoreUnknown = true)
public class EditShipmentRequest {
	private int mInoutId;
	private List<EditShipmentLine> lines;

	public int getMInoutId() {
		return mInoutId;
	}

	public void setMInoutId(int mInoutId) {
		this.mInoutId = mInoutId;
	}

	public List<EditShipmentLine> getLines() {
		return lines;
	}

	public void setLines(List<EditShipmentLine> lines) {
		this.lines = lines;
	}

}