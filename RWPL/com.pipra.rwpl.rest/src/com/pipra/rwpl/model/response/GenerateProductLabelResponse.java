package com.pipra.rwpl.model.response;

import java.util.List;

public class GenerateProductLabelResponse {
	private boolean isError;
	private String error;
	private List<ProductLabelLine> productLabelLine;

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

	public List<ProductLabelLine> getProductLabelLine() {
		return productLabelLine;
	}

	public void setProductLabelLine(List<ProductLabelLine> productLabelLine) {
		this.productLabelLine = productLabelLine;
	}

}