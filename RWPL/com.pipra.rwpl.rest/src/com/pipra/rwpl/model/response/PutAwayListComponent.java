package com.pipra.rwpl.model.response;

public class PutAwayListComponent {
	private String documentNo;
	private int mInoutID;
	private String orderDate;
	private String orderDocumentno;
	private String supplier;
	private int warehouseId;
	private String warehouseName;
	private String description;
	private boolean toMarkForPutAway;
	private int quantityToPick;
	private int totalQuantity;

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

	public boolean isToMarkForPutAway() {
		return toMarkForPutAway;
	}

	public void setToMarkForPutAway(boolean toMarkForPutAway) {
		this.toMarkForPutAway = toMarkForPutAway;
	}

	public int getQuantityToPick() {
		return quantityToPick;
	}

	public void setQuantityToPick(int quantityToPick) {
		this.quantityToPick = quantityToPick;
	}

	public int getTotalQuantity() {
		return totalQuantity;
	}

	public void setTotalQuantity(int totalQuantity) {
		this.totalQuantity = totalQuantity;
	}
}
