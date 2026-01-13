package com.pipra.ve.rest.model.response;

import java.util.List;

/**
 * 
 * @author Mahendhar Reddy
 *
 */
public class LoginResponse {
	private List<Client> clientList;
	private boolean isError;

	public List<Client> getClientList() {
		return clientList;
	}

	public void setClientList(List<Client> clientList) {
		this.clientList = clientList;
	}

	public boolean isError() {
		return isError;
	}

	public void setError(boolean isError) {
		this.isError = isError;
	}
}
