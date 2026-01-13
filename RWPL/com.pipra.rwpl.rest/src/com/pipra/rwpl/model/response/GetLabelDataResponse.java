package com.pipra.rwpl.model.response;

import java.util.List;

public class GetLabelDataResponse {
	private boolean isError;
	private String error;
	private String labelType;
	private String labelUUID;
	private int productCount;
	private List<ProductLabelLine> labelLine;

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

	public String getLabelType() {
		return labelType;
	}

	public void setLabelType(String labelType) {
		this.labelType = labelType;
	}

	public String getLabelUUID() {
		return labelUUID;
	}

	public void setLabelUUID(String labelUUID) {
		this.labelUUID = labelUUID;
	}

	public int getProductCount() {
		return productCount;
	}

	public void setProductCount(int productCount) {
		this.productCount = productCount;
	}

	public List<ProductLabelLine> getLabelLine() {
		return labelLine;
	}

	public void setLabelLine(List<ProductLabelLine> labelLine) {
		this.labelLine = labelLine;
	}

}