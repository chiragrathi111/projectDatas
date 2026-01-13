package com.pipra.rwpl.model.response;

import java.util.Date;
import java.util.List;

public class PIDetailsResponse {
	private boolean isError;
	private String error;
    private int piId;
    private String documentNo;
    private int mInventoryId;
    private Date orderDate;
    private String orgName;
    private String warehouseName;
    private String description;
    private List<PIDetailsLine> piDetailsLines;

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

    public int getPiId() {
        return piId;
    }

    public void setPiId(int piId) {
        this.piId = piId;
    }

    public String getDocumentNo() {
        return documentNo;
    }

    public void setDocumentNo(String documentNo) {
        this.documentNo = documentNo;
    }

    public int getmInventoryId() {
        return mInventoryId;
    }

    public void setmInventoryId(int mInventoryId) {
        this.mInventoryId = mInventoryId;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<PIDetailsLine> getPiDetailsLines() {
        return piDetailsLines;
    }

    public void setPiDetailsLines(List<PIDetailsLine> piDetailsLines) {
        this.piDetailsLines = piDetailsLines;
    }
}