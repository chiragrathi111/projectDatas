package com.pipra.rwpl.model.response;

public class BusinessPartnerComponent {
	private int businessPartnerID;
	private String businessPartnerName;
	private String address;

	public int getBusinessPartnerID() {
		return businessPartnerID;
	}

	public void setBusinessPartnerID(int businessPartnerID) {
		this.businessPartnerID = businessPartnerID;
	}

	public String getBusinessPartnerName() {
		return businessPartnerName;
	}

	public void setBusinessPartnerName(String businessPartnerName) {
		this.businessPartnerName = businessPartnerName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

}