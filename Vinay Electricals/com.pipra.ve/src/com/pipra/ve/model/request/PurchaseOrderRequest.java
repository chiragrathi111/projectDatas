package com.pipra.ve.model.request;

import java.sql.Timestamp;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true) 
public class PurchaseOrderRequest {

	@JsonProperty("ID")
	private int id;

	@JsonProperty("vNo")
	private String vNo;
	
	@JsonProperty("CompanyName")
	private String companyName;

	@JsonProperty("BranchName")
	private String branchName;

	@JsonProperty("DeptName")
	private String deptName;

	@JsonProperty("Narration")
	private String narration;

	@JsonProperty("vDate")
	private Timestamp vDate;

	@JsonProperty("AcHeadName")
	private String acHeadName;

	@JsonProperty("ShipTo")
	private String shipTo;

	@JsonProperty("BillTo")
	private String billTo;

	@JsonProperty("SubTotal")
	private double subTotal;

	@JsonProperty("Amount")
	private double amount;

	@JsonProperty("dtItems")
	private List<DTItem> dtItems;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getvNo() {
		return vNo;
	}

	public void setvNo(String vNo) {
		this.vNo = vNo;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	public String getNarration() {
		return narration;
	}

	public void setNarration(String narration) {
		this.narration = narration;
	}

	public Timestamp getvDate() {
		return vDate;
	}

	public void setvDate(Timestamp vDate) {
		this.vDate = vDate;
	}

	public String getAcHeadName() {
		return acHeadName;
	}

	public void setAcHeadName(String acHeadName) {
		this.acHeadName = acHeadName;
	}

	public String getShipTo() {
		return shipTo;
	}

	public void setShipTo(String shipTo) {
		this.shipTo = shipTo;
	}

	public String getBillTo() {
		return billTo;
	}

	public void setBillTo(String billTo) {
		this.billTo = billTo;
	}

	public double getSubTotal() {
		return subTotal;
	}

	public void setSubTotal(double subTotal) {
		this.subTotal = subTotal;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public List<DTItem> getDtItems() {
		return dtItems;
	}

	public void setDtItems(List<DTItem> dtItems) {
		this.dtItems = dtItems;
	}

	@JsonIgnoreProperties(ignoreUnknown = true) 
	public static class DTItem {

		@JsonProperty("ID")
		private int id;

		@JsonProperty("MaterialName")
		private String materialName;

		@JsonProperty("Narration")
		private String narration;

		@JsonProperty("Qty")
		private int qty;

		@JsonProperty("Unit")
		private String unit;

		@JsonProperty("TaxRate")
		private Double taxRate; // Can be null

		@JsonProperty("DiscAmt")
		private double discAmt;

		@JsonProperty("ExpiryDate")
		private String expiryDate;

		@JsonProperty("Rate")
		private double rate;

		@JsonProperty("DiscPer")
		private double discPer;

		@JsonProperty("Amount")
		private double amount;

		@JsonProperty("TaxAmt")
		private double taxAmt;

		@JsonProperty("SGST")
		private double sgst;

		@JsonProperty("CGST")
		private double cgst;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getMaterialName() {
			return materialName;
		}

		public void setMaterialName(String materialName) {
			this.materialName = materialName;
		}

		public String getNarration() {
			return narration;
		}

		public void setNarration(String narration) {
			this.narration = narration;
		}

		public int getQty() {
			return qty;
		}

		public void setQty(int qty) {
			this.qty = qty;
		}

		public String getUnit() {
			return unit;
		}

		public void setUnit(String unit) {
			this.unit = unit;
		}

		public Double getTaxRate() {
			return taxRate;
		}

		public void setTaxRate(Double taxRate) {
			this.taxRate = taxRate;
		}

		public double getDiscAmt() {
			return discAmt;
		}

		public void setDiscAmt(double discAmt) {
			this.discAmt = discAmt;
		}

		public String getExpiryDate() {
			return expiryDate;
		}

		public void setExpiryDate(String expiryDate) {
			this.expiryDate = expiryDate;
		}

		public double getRate() {
			return rate;
		}

		public void setRate(double rate) {
			this.rate = rate;
		}

		public double getDiscPer() {
			return discPer;
		}

		public void setDiscPer(double discPer) {
			this.discPer = discPer;
		}

		public double getAmount() {
			return amount;
		}

		public void setAmount(double amount) {
			this.amount = amount;
		}

		public double getTaxAmt() {
			return taxAmt;
		}

		public void setTaxAmt(double taxAmt) {
			this.taxAmt = taxAmt;
		}

		public double getSgst() {
			return sgst;
		}

		public void setSgst(double sgst) {
			this.sgst = sgst;
		}

		public double getCgst() {
			return cgst;
		}

		public void setCgst(double cgst) {
			this.cgst = cgst;
		}

	}
}