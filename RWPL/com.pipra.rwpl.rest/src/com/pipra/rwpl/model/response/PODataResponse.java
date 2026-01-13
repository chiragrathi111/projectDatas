package com.pipra.rwpl.model.response;

import java.util.Date;
import java.util.List;

public class PODataResponse {
	private boolean isError;
	private String error;
	private String documentNo;
	private int cOrderId;
	private String docstatus;
	private Date orderDate;
	private String supplier;
	private String warehouseName;
	private String description;
	private boolean orderStatus;
	private int overallQnty;
	private int defaultLocatorId;
	private List<ProductData> productData;

	public boolean isError() {
		return isError;
	}

	public void setError(boolean isError) {
		this.isError = isError;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getDocumentNo() {
		return documentNo;
	}

	public void setDocumentNo(String documentNo) {
		this.documentNo = documentNo;
	}

	public int getcOrderId() {
		return cOrderId;
	}

	public void setcOrderId(int cOrderId) {
		this.cOrderId = cOrderId;
	}

	public String getDocstatus() {
		return docstatus;
	}

	public void setDocstatus(String docstatus) {
		this.docstatus = docstatus;
	}

	public Date getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
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

	public int getOverallQnty() {
		return overallQnty;
	}

	public void setOverallQnty(int overallQnty) {
		this.overallQnty = overallQnty;
	}

	public int getDefaultLocatorId() {
		return defaultLocatorId;
	}

	public void setDefaultLocatorId(int defaultLocatorId) {
		this.defaultLocatorId = defaultLocatorId;
	}

	public List<ProductData> getProductData() {
		return productData;
	}

	public void setProductData(List<ProductData> productData) {
		this.productData = productData;
	}
}