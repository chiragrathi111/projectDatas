package com.pipra.rwpl.model.response;

public class PutAwayLabourComponent {
    private int productId;
    private String productName;
    private int quantity;
    private int locatorId;
    private String locatorName;
    private int warehouseId;
    private String warehouseName;

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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

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

    public int getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(int warehouseId) {
        this.warehouseId = warehouseId;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }
}