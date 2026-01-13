package com.pipra.rwpl.model.response;

import java.util.List;

public class SODetailResponse {
	private boolean isError;
	private String error;
	private String documentNo;
	private String docStatus;
	private int cOrderId;
	private String orderDate;
	private String customer;
	private String warehouseName;
	private String description;
	private boolean orderStatus;
	private String locationName;
	private int quantityPicked;
	private String dispatchLocatorName;
	private int dispatchLocatorId;
	private int quantityTotal;
	private List<SODetailProductData> productData;

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

	public String getDocStatus() {
		return docStatus;
	}

	public void setDocStatus(String docStatus) {
		this.docStatus = docStatus;
	}

	public int getcOrderId() {
		return cOrderId;
	}

	public void setcOrderId(int cOrderId) {
		this.cOrderId = cOrderId;
	}

	public String getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}

	public String getCustomer() {
		return customer;
	}

	public void setCustomer(String customer) {
		this.customer = customer;
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

	public boolean isOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(boolean orderStatus) {
		this.orderStatus = orderStatus;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public int getQuantityPicked() {
		return quantityPicked;
	}

	public void setQuantityPicked(int quantityPicked) {
		this.quantityPicked = quantityPicked;
	}

	public String getDispatchLocatorName() {
		return dispatchLocatorName;
	}

	public void setDispatchLocatorName(String dispatchLocatorName) {
		this.dispatchLocatorName = dispatchLocatorName;
	}

	public int getDispatchLocatorId() {
		return dispatchLocatorId;
	}

	public void setDispatchLocatorId(int dispatchLocatorId) {
		this.dispatchLocatorId = dispatchLocatorId;
	}

	public int getQuantityTotal() {
		return quantityTotal;
	}

	public void setQuantityTotal(int quantityTotal) {
		this.quantityTotal = quantityTotal;
	}

	public List<SODetailProductData> getProductData() {
		return productData;
	}

	public void setProductData(List<SODetailProductData> productData) {
		this.productData = productData;
	}
}