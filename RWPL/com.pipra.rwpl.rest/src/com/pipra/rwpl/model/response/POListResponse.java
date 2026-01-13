package com.pipra.rwpl.model.response;

import java.util.List;

public class POListResponse {
	private boolean isError;
	private String error;
	private List<POListAccess> listAccess;

	public boolean getIsError() {
		return isError;
	}

	public void setIsError(boolean isError) {
		this.isError = isError;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public List<POListAccess> getListAccess() {
		return listAccess;
	}

	public void setListAccess(List<POListAccess> listAccess) {
		this.listAccess = listAccess;
	}
}