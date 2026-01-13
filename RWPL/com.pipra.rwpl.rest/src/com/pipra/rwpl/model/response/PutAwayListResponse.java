package com.pipra.rwpl.model.response;

import java.util.List;

public class PutAwayListResponse {
	private boolean isError;
	private String error; 
	private int count;
	private List<PutAwayListComponent> putAwayList;

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

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public List<PutAwayListComponent> getPutAwayList() {
		return putAwayList;
	}

	public void setPutAwayList(List<PutAwayListComponent> putAwayList) {
		this.putAwayList = putAwayList;
	}
}