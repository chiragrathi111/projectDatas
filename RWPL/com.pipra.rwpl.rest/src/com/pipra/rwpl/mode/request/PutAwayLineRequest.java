package com.pipra.rwpl.mode.request;

public class PutAwayLineRequest {
    private String productLabelUUId;
    private int locatorId;
    private int newLabelQnty;

    public String getProductLabelUUId() {
        return productLabelUUId;
    }

    public void setProductLabelUUId(String productLabelUUId) {
        this.productLabelUUId = productLabelUUId;
    }

    public int getLocatorId() {
        return locatorId;
    }

    public void setLocatorId(int locatorId) {
        this.locatorId = locatorId;
    }

    public int getNewLabelQnty() {
        return newLabelQnty;
    }

    public void setNewLabelQnty(int newLabelQnty) {
        this.newLabelQnty = newLabelQnty;
    }
}