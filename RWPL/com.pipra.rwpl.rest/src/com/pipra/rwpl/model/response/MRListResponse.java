package com.pipra.rwpl.model.response;

import java.util.List;

public class MRListResponse {
	private boolean isError;
	private String error;
	private int count;
	private List<MRComponent> mrList;

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

	public List<MRComponent> getMrList() {
		return mrList;
	}

	public void setMrList(List<MRComponent> mrList) {
		this.mrList = mrList;
	}

}