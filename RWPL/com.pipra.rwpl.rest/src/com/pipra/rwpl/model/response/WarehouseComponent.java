package com.pipra.rwpl.model.response;

public class WarehouseComponent {
	private int warehouseId;
	private String warehouse;
	private int defaultLocatorId;

	public int getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(int warehouseId) {
		this.warehouseId = warehouseId;
	}

	public String getWarehouse() {
		return warehouse;
	}

	public void setWarehouse(String warehouse) {
		this.warehouse = warehouse;
	}

	public int getDefaultLocatorId() {
		return defaultLocatorId;
	}

	public void setDefaultLocatorId(int defaultLocatorId) {
		this.defaultLocatorId = defaultLocatorId;
	}

}