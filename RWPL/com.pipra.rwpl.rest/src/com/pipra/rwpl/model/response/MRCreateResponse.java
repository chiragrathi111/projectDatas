package com.pipra.rwpl.model.response;

import java.util.List;

public class MRCreateResponse {
	private boolean isError;
	private String error;
	private String mrDocumentNumber;
	private int mrId;
	private List<MRLineResponse> mrLines;

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

	public String getMrDocumentNumber() {
		return mrDocumentNumber;
	}

	public void setMrDocumentNumber(String mrDocumentNumber) {
		this.mrDocumentNumber = mrDocumentNumber;
	}

	public int getMrId() {
		return mrId;
	}

	public void setMrId(int mrId) {
		this.mrId = mrId;
	}

	public List<MRLineResponse> getMrLines() {
		return mrLines;
	}

	public void setMrLines(List<MRLineResponse> mrLines) {
		this.mrLines = mrLines;
	}

}
