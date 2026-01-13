package com.pipra.rwpl.model.response;

public class UpdateProductLabelResponse {
    private boolean isError;
    private String error;
    private int productLabelId;
    private String productLabelUUId;
    private String productName;
    private int quantity;

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

    public int getProductLabelId() {
        return productLabelId;
    }

    public void setProductLabelId(int productLabelId) {
        this.productLabelId = productLabelId;
    }

    public String getProductLabelUUId() {
        return productLabelUUId;
    }

    public void setProductLabelUUId(String productLabelUUId) {
        this.productLabelUUId = productLabelUUId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}