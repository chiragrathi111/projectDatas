package com.pipra.rwpl.model.response;

public class POListAccess {
	private String cOrderId;
	private String documentNumber;
	private String supplierName;
	private String orderDate;
	private String warehouseName;
	private String description;

	public String getcOrderId() {
		return cOrderId;
	}

	public void setcOrderId(String cOrderId) {
		this.cOrderId = cOrderId;
	}

	public String getDocumentNumber() {
		return documentNumber;
	}

	public void setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
	}

	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	public String getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
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
}