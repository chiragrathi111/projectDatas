package com.pipra.rwpl.model.response;

public class PIQtyChangeResponse {
	private boolean isError;
	private String error;

    public boolean isError() {
        return isError;
    }

    public void setError(boolean isError) {
        this.isError = isError;
    }

    public String getError() {
        return error;
    }

    public void setError(String errorMessage) {
        this.error = errorMessage;
    }
}