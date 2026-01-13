package com.pipra.ve.rest.model.request;

/**
 * 
 * @author Mahendhar Reddy
 *
 */
public class LoginCredential {
	private String userName;
	private String password;
	private LoginParameters parameters;
		
	public LoginCredential() {
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public LoginParameters getParameters() {
		return parameters;
	}

	public void setParameters(LoginParameters parameters) {
		this.parameters = parameters;
	}
}
