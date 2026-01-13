package org.realmeds.tissue.custom;

public class Record {
	
	String CropType;
	String Variety;
	String ParentCultureLine;
	String Date;
	public String getCropType() {
		return CropType;
	}
	public void setCropType(String cropType) {
		CropType = cropType;
	}
	public String getVariety() {
		return Variety;
	}
	public void setVariety(String variety) {
		Variety = variety;
	}
	public String getParentCultureLine() {
		return ParentCultureLine;
	}
	public void setParentCultureLine(String parentCultureLine) {
		ParentCultureLine = parentCultureLine;
	}
	public String getDate() {
		return Date;
	}
	public void setDate(String date) {
		this.Date = date;
	}

}
