package com.pipra.ve.rest.model.response;

/**
 * 
 * @author Mahendhar Reddy
 *
 */
public class Warehouse {
	private String warehouseId;
	private String warehouse;
	private int defaultLocatorId;

	public String getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(String warehouseId) {
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
