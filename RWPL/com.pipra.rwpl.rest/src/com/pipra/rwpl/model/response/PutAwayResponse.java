package com.pipra.rwpl.model.response;

public class PutAwayResponse {
    private boolean isError;
    private String error;

    public boolean isError() {
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
}