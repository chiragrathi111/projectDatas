package com.pipra.rwpl.model.response;

import java.util.List;

public class PutAwayDetailResponse {
	private boolean isError;
	private String error;
	private String documentNo;
	private int mInoutID;
	private String orderDate;
	private String orderDocumentno;
	private String supplier;
	private int warehouseId;
	private String warehouseName;
	private String description;
	private List<PutAwayDetailComponent> putAwayDetail;

	public boolean isError() {
		return isError;
	}

	public void setIsError(boolean isError) {
		this.isError = isError;
	}

	public String getError() {
		return error;
	}

	public void setError(String errorMessage) {
		this.error = errorMessage;
	}

	public String getDocumentNo() {
		return documentNo;
	}

	public void setDocumentNo(String documentNo) {
		this.documentNo = documentNo;
	}

	public int getMInoutID() {
		return mInoutID;
	}

	public void setMInoutID(int mInoutID) {
		this.mInoutID = mInoutID;
	}

	public String getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}

	public String getOrderDocumentno() {
		return orderDocumentno;
	}

	public void setOrderDocumentno(String orderDocumentno) {
		this.orderDocumentno = orderDocumentno;
	}

	public String getSupplier() {
		return supplier;
	}

	public void setSupplier(String supplier) {
		this.supplier = supplier;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<PutAwayDetailComponent> getPutAwayDetail() {
		return putAwayDetail;
	}

	public void setPutAwayDetail(List<PutAwayDetailComponent> putAwayDetail) {
		this.putAwayDetail = putAwayDetail;
	}
}