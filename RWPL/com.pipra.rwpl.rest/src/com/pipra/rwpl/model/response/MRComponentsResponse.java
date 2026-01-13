package com.pipra.rwpl.model.response;

import java.util.List;

public class MRComponentsResponse {
	private boolean isError;
	private String error;

	private List<WarehouseComponent> warehouse;
	private List<ProductCategoryComponent> productCategory;
	private List<ProductComponent> product;
	private List<BusinessPartnerComponent> businessPartner;

	public boolean isError() {
		return isError;
	}

	public void setError(boolean isError) {
		this.isError = isError;
	}

	public List<WarehouseComponent> getWarehouse() {
		return warehouse;
	}

	public void setWarehouse(List<WarehouseComponent> warehouse) {
		this.warehouse = warehouse;
	}

	public List<ProductCategoryComponent> getProductCategory() {
		return productCategory;
	}

	public void setProductCategory(List<ProductCategoryComponent> productCategory) {
		this.productCategory = productCategory;
	}

	public List<ProductComponent> getProduct() {
		return product;
	}

	public void setProduct(List<ProductComponent> product) {
		this.product = product;
	}

	public List<BusinessPartnerComponent> getBusinessPartner() {
		return businessPartner;
	}

	public void setBusinessPartner(List<BusinessPartnerComponent> businessPartner) {
		this.businessPartner = businessPartner;
	}

	public String getErrorMessage() {
		return error;
	}

	public void setErrorMessage(String errorMessage) {
		this.error = errorMessage;
	}

}