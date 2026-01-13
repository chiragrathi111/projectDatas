package com.pipra.rwpl.model.response;

public class SOListComponent {

	private String documentNumber;
	private int cOrderId;
	private String customerName;
	private String orderDate;
	private int warehouseId;
	private String warehouseName;
	private String description;
	private String status;
	private boolean salesOrderStatus;
	private int quantityPicked;
	private int quantityTotal;
	private int remainingQuantityToPick;

	public String getDocumentNumber() {
		return documentNumber;
	}

	public void setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
	}

	public int getcOrderId() {
		return cOrderId;
	}

	public void setcOrderId(int cOrderId) {
		this.cOrderId = cOrderId;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public boolean isSalesOrderStatus() {
		return salesOrderStatus;
	}

	public void setSalesOrderStatus(boolean salesOrderStatus) {
		this.salesOrderStatus = salesOrderStatus;
	}

	public int getQuantityPicked() {
		return quantityPicked;
	}

	public void setQuantityPicked(int quantityPicked) {
		this.quantityPicked = quantityPicked;
	}

	public int getQuantityTotal() {
		return quantityTotal;
	}

	public void setQuantityTotal(int quantityTotal) {
		this.quantityTotal = quantityTotal;
	}

	public int getRemainingQuantityToPick() {
		return remainingQuantityToPick;
	}

	public void setRemainingQuantityToPick(int remainingQuantityToPick) {
		this.remainingQuantityToPick = remainingQuantityToPick;
	}
}