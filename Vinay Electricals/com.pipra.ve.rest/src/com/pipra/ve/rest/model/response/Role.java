package com.pipra.ve.rest.model.response;

import java.util.List;

/**
 * 
 * @author Mahendhar Reddy
 *
 */
public class Role {
	private String roleId;
	private String role;
	private List<Organization> orgList;

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public List<Organization> getOrgList() {
		return orgList;
	}

	public void setOrgList(List<Organization> orgList) {
		this.orgList = orgList;
	}
}
