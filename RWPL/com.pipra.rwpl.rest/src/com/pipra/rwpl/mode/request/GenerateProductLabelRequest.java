package com.pipra.rwpl.mode.request;

import java.util.List;

import com.pipra.rwpl.model.response.ProductLabelLine;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties; 

@JsonIgnoreProperties(ignoreUnknown = true)
public class GenerateProductLabelRequest {
	private boolean finalDispatch;
	private int mInoutId;

	private List<ProductLabelLine> productLabelLine;

	public boolean isFinalDispatch() {
		return finalDispatch;
	}

	public void setFinalDispatch(boolean finalDispatch) {
		this.finalDispatch = finalDispatch;
	}

	public int getmInoutId() {
		return mInoutId;
	}

	public void setmInoutId(int mInoutId) {
		this.mInoutId = mInoutId;
	}

	public List<ProductLabelLine> getProductLabelLine() {
		return productLabelLine;
	}

	public void setProductLabelLine(List<ProductLabelLine> productLabelLine) {
		this.productLabelLine = productLabelLine;
	}

}