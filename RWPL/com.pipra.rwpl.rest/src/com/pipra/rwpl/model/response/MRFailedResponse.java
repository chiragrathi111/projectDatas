package com.pipra.rwpl.model.response;

public class MRFailedResponse {
    private boolean isError;
    private String errorMessage;
    private String createConfirmationDocumentNumber;
    private int createConfirmationId;

    public boolean isError() {
        return isError;
    }

    public void setError(boolean isError) {
        this.isError = isError;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getCreateConfirmationDocumentNumber() {
        return createConfirmationDocumentNumber;
    }

    public void setCreateConfirmationDocumentNumber(String createConfirmationDocumentNumber) {
        this.createConfirmationDocumentNumber = createConfirmationDocumentNumber;
    }

    public int getCreateConfirmationId() {
        return createConfirmationId;
    }

    public void setCreateConfirmationId(int createConfirmationId) {
        this.createConfirmationId = createConfirmationId;
    }
}
