package com.pipra.rwpl.model.response;

public class QcCheckResponse {
    private boolean isError;
    private String error;

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
}
