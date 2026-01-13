package com.pipra.rwpl.model.response;

public class PIDetailsLine {
    private int locatorId;
    private String locatorName;
    private int productId;
    private String productName;
    private int qtyCount;
    private int qntyBook;
    private int piLineId;

    public int getLocatorId() {
        return locatorId;
    }

    public void setLocatorId(int locatorId) {
        this.locatorId = locatorId;
    }

    public String getLocatorName() {
        return locatorName;
    }

    public void setLocatorName(String locatorName) {
        this.locatorName = locatorName;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQtyCount() {
        return qtyCount;
    }

    public void setQtyCount(int qtyCount) {
        this.qtyCount = qtyCount;
    }

    public int getQntyBook() {
        return qntyBook;
    }

    public void setQntyBook(int qntyBook) {
        this.qntyBook = qntyBook;
    }

    public int getPiLineId() {
        return piLineId;
    }

    public void setPiLineId(int piLineId) {
        this.piLineId = piLineId;
    }
}