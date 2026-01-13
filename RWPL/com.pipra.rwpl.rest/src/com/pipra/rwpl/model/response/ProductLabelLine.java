package com.pipra.rwpl.model.response;

public class ProductLabelLine {
	private int labelId;
	private String productLabelUUId;
	private int cOrderlineId;
	private int mInoutlineId;
	private int productId;
	private String productName;
	private int locatorId;
	private String locatorName;
	private String status;
	private boolean finalDispatch;
	private int quantity;
	private boolean qcPassed;
	private boolean isSalesTransaction;
	private int warehouseId;
	private String warehouseName;

	public int getLabelId() {
		return labelId;
	}

	public void setLabelId(int labelId) {
		this.labelId = labelId;
	}

	public String getProductLabelUUId() {
		return productLabelUUId;
	}

	public void setProductLabelUUId(String productLabelUUId) {
		this.productLabelUUId = productLabelUUId;
	}

	public int getcOrderlineId() {
		return cOrderlineId;
	}

	public void setcOrderlineId(int cOrderlineId) {
		this.cOrderlineId = cOrderlineId;
	}

	public int getmInoutlineId() {
		return mInoutlineId;
	}

	public void setmInoutlineId(int mInoutlineId) {
		this.mInoutlineId = mInoutlineId;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public boolean isFinalDispatch() {
		return finalDispatch;
	}

	public void setFinalDispatch(boolean finalDispatch) {
		this.finalDispatch = finalDispatch;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public boolean isQcPassed() {
		return qcPassed;
	}

	public void setQcPassed(boolean qcPassed) {
		this.qcPassed = qcPassed;
	}

	public boolean isSalesTransaction() {
		return isSalesTransaction;
	}

	public void setSalesTransaction(boolean isSalesTransaction) {
		this.isSalesTransaction = isSalesTransaction;
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