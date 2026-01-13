package com.pipra.rwpl.model.response;

import java.util.ArrayList;
import java.util.List;

public class LocatorDetailResponse {
    private boolean isError;
    private String error;
    private int locatorId;
    private String locatorName;
    private String aisle;
    private String level;
    private String bin;
    private int locatorTypeId;
    private String locatorType;
    private int warehouseId;
    private String warehouseName;
    private List<ProductLabelLine> labelLines;
    private List<ProductLine> productLines;

    public LocatorDetailResponse() {
        this.labelLines = new ArrayList<>();
        this.productLines = new ArrayList<>();
    }

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

    public String getAisle() {
        return aisle;
    }

    public void setAisle(String aisle) {
        this.aisle = aisle;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getBin() {
        return bin;
    }

    public void setBin(String bin) {
        this.bin = bin;
    }

    public int getLocatorTypeId() {
        return locatorTypeId;
    }

    public void setLocatorTypeId(int locatorTypeId) {
        this.locatorTypeId = locatorTypeId;
    }

    public String getLocatorType() {
        return locatorType;
    }

    public void setLocatorType(String locatorType) {
        this.locatorType = locatorType;
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

    public List<ProductLabelLine> getLabelLines() {
        return labelLines;
    }

    public void setLabelLines(List<ProductLabelLine> labelLines) {
        this.labelLines = labelLines;
    }

    public List<ProductLine> getProductLines() {
        return productLines;
    }

    public void setProductLines(List<ProductLine> productLines) {
        this.productLines = productLines;
    }

    public static class ProductLine {
        private int productId;
        private String productName;
        private int quantity;

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
    }
}
