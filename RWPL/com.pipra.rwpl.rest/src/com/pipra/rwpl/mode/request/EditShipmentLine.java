package com.pipra.rwpl.mode.request;

public class EditShipmentLine {
	private String labelUUID;
	private int productId;
	private int usedQuantity;

	public String getLabelUUID() {
		return labelUUID;
	}

	public void setLabelUUID(String labelUUID) {
		this.labelUUID = labelUUID;
	}

	public int getProductId() {
		return productId;
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}

	public int getUsedQuantity() {
		return usedQuantity;
	}

	public void setUsedQuantity(int usedQuantity) {
		this.usedQuantity = usedQuantity;
	}
}