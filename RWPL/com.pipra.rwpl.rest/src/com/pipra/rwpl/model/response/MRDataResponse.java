package com.pipra.rwpl.model.response;

import java.util.Date;
import java.util.List;

public class MRDataResponse { 
	private boolean isError;
	private String error;
    
    private String documentNo;
    private String description;
    private String docStatus;
    private Date movementDate; 
    private int mInoutID;
    private String supplier;
    private String supplierLocationName;
    private String warehouseName;
    private int totalQty; 
    private int lineCount;
    private int cOrderId;
    private String orderDocumentno;

    // Lines
    private List<MRLineData> lines;

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

	public String getDocumentNo() {
		return documentNo;
	}

	public void setDocumentNo(String documentNo) {
		this.documentNo = documentNo;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDocStatus() {
		return docStatus;
	}

	public void setDocStatus(String docStatus) {
		this.docStatus = docStatus;
	}

	public Date getMovementDate() {
		return movementDate;
	}

	public void setMovementDate(Date movementDate) {
		this.movementDate = movementDate;
	}

	public int getmInoutID() {
		return mInoutID;
	}

	public void setmInoutID(int mInoutID) {
		this.mInoutID = mInoutID;
	}

	public String getSupplier() {
		return supplier;
	}

	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}

	public String getWarehouseName() {
		return warehouseName;
	}

	public void setWarehouseName(String warehouseName) {
		this.warehouseName = warehouseName;
	}

	public int getTotalQty() {
		return totalQty;
	}

	public void setTotalQty(int totalQty) {
		this.totalQty = totalQty;
	}

	public int getLineCount() {
		return lineCount;
	}

	public void setLineCount(int lineCount) {
		this.lineCount = lineCount;
	}

	public List<MRLineData> getLines() {
		return lines;
	}

	public void setLines(List<MRLineData> lines) {
		this.lines = lines;
	}

	public String getSupplierLocationName() {
		return supplierLocationName;
	}

	public void setSupplierLocationName(String supplierLocationName) {
		this.supplierLocationName = supplierLocationName;
	}

	public int getcOrderId() {
		return cOrderId;
	}

	public void setcOrderId(int cOrderId) {
		this.cOrderId = cOrderId;
	}
	
	public String getOrderDocumentno() {
		return orderDocumentno;
	}

	public void setOrderDocumentno(String orderDocumentno) {
		this.orderDocumentno = orderDocumentno;
	}
}