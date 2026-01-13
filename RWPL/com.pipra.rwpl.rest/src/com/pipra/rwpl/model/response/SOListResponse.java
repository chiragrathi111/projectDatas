package com.pipra.rwpl.model.response;

import java.util.List;

public class SOListResponse {

	private boolean isError;
	private String error;
	private int count;
	private List<SOListComponent> soList;

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
		this.error= errorMessage;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public List<SOListComponent> getSoList() {
		return soList;
	}

	public void setSoList(List<SOListComponent> soList) {
		this.soList = soList;
	}
}