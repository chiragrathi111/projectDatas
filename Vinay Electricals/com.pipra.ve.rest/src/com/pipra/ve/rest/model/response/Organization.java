package com.pipra.ve.rest.model.response;

import java.util.List;

/**
 * 
 * @author Mahendhar Reddy
 *
 */
public class Organization {
	private String orgId;
	private String org;
	private List<Warehouse> warehouseList;

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public String getOrg() {
		return org;
	}

	public void setOrg(String org) {
		this.org = org;
	}

	public List<Warehouse> getWarehouseList() {
		return warehouseList;
	}

	public void setWarehouseList(List<Warehouse> warehouseList) {
		this.warehouseList = warehouseList;
	}
}
