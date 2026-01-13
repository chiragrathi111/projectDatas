package com.pipra.rwpl.model.response;

import java.util.Date;

public class MRComponent {
	private int mInoutID;
	private String documentNo;
	private String supplier;
	private String warehouseName;
	private String pickStatus;
	private Date orderDate;
	private String orderDocumentno;
	private String description;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getmInoutID() {
		return mInoutID;
	}

	public void setmInoutID(int mInoutID) {
		this.mInoutID = mInoutID;
	}

	public String getDocumentNo() {
		return documentNo;
	}

	public void setDocumentNo(String documentNo) {
		this.documentNo = documentNo;
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

	public String getPickStatus() {
		return pickStatus;
	}

	public void setPickStatus(String pickStatus) {
		this.pickStatus = pickStatus;
	}

	public Date getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}

	public String getOrderDocumentno() {
		return orderDocumentno;
	}

	public void setOrderDocumentno(String orderDocumentno) {
		this.orderDocumentno = orderDocumentno;
	}

}