package com.pipra.rwpl.model.response;

public class SetDocActionResponse {
    private boolean isError;
    private String error;
    private int recordId;

    public boolean isError() {
        return isError;
    }

    public void setError(boolean isError) {
        this.isError = isError;
    }

    public String getErrorMessage() {
        return error;
    }

    public void setErrorMessage(String error) {
        this.error = error;
    }

    public int getRecordId() {
        return recordId;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }
}