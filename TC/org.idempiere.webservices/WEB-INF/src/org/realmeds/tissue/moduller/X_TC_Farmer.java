/******************************************************************************
 * Product: iDempiere ERP & CRM Smart Business Solution                       *
 * Copyright (C) 1999-2012 ComPiere, Inc. All Rights Reserved.                *
 * This program is free software, you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY, without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program, if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 * For the text or an alternative of this public license, you may reach us    *
 * ComPiere, Inc., 2620 Augustine Dr. #245, Santa Clara, CA 95054, USA        *
 * or via info@compiere.org or http://www.compiere.org/license.html           *
 *****************************************************************************/
/** Generated Model - DO NOT CHANGE */
package org.realmeds.tissue.moduller;

import java.sql.ResultSet;
import java.util.Properties;
import org.compiere.model.I_Persistent;
import org.compiere.model.PO;
import org.compiere.model.POInfo;

/**
 * Generated Model for TC_Farmer
 * 
 * @author iDempiere (generated)
 * @version Release 10 - $Id$
 */
@org.adempiere.base.Model(table = "TC_Farmer")
public class X_TC_Farmer extends PO implements I_TC_Farmer, I_Persistent {

	/**
	 *
	 */
	private static final long serialVersionUID = 20240304L;

	/** Standard Constructor */
	public X_TC_Farmer(Properties ctx, int TC_Farmer_ID, String trxName) {
		super(ctx, TC_Farmer_ID, trxName);
		/**
		 * if (TC_Farmer_ID == 0) { setIsDefault (false); setName (null);
		 * setTC_Farmer_ID (0); }
		 */
	}

	/** Standard Constructor */
	public X_TC_Farmer(Properties ctx, int TC_Farmer_ID, String trxName, String... virtualColumns) {
		super(ctx, TC_Farmer_ID, trxName, virtualColumns);
		/**
		 * if (TC_Farmer_ID == 0) { setIsDefault (false); setName (null);
		 * setTC_Farmer_ID (0); }
		 */
	}

	/** Load Constructor */
	public X_TC_Farmer(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}

	/**
	 * AccessLevel
	 * 
	 * @return 3 - Client - Org
	 */
	protected int get_AccessLevel() {
		return accessLevel.intValue();
	}

	/** Load Meta Data */
	protected POInfo initPO(Properties ctx) {
		POInfo poi = POInfo.getPOInfo(ctx, Table_ID, get_TrxName());
		return poi;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder("X_TC_Farmer[").append(get_ID()).append(",Name=").append(getName())
				.append("]");
		return sb.toString();
	}

	/**
	 * Set Address.
	 * 
	 * @param Address Address
	 */
	public void setAddress(String Address) {
		set_ValueNoCheck(COLUMNNAME_Address, Address);
	}

	/**
	 * Get Address.
	 * 
	 * @return Address
	 */
	public String getAddress() {
		return (String) get_Value(COLUMNNAME_Address);
	}

	/**
	 * Set Description.
	 * 
	 * @param Description Optional short description of the record
	 */
	public void setDescription(String Description) {
		set_Value(COLUMNNAME_Description, Description);
	}

	/**
	 * Get Description.
	 * 
	 * @return Optional short description of the record
	 */
	public String getDescription() {
		return (String) get_Value(COLUMNNAME_Description);
	}

	/**
	 * Set Default.
	 * 
	 * @param IsDefault Default value
	 */
	public void setIsDefault(boolean IsDefault) {
		set_Value(COLUMNNAME_IsDefault, Boolean.valueOf(IsDefault));
	}

	/**
	 * Get Default.
	 * 
	 * @return Default value
	 */
	public boolean isDefault() {
		Object oo = get_Value(COLUMNNAME_IsDefault);
		if (oo != null) {
			if (oo instanceof Boolean)
				return ((Boolean) oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}

	/**
	 * Set landmark.
	 * 
	 * @param landmark landmark
	 */
	public void setlandmark(String landmark) {
		set_Value(COLUMNNAME_landmark, landmark);
	}

	/**
	 * Get landmark.
	 * 
	 * @return landmark
	 */
	public String getlandmark() {
		return (String) get_Value(COLUMNNAME_landmark);
	}

	/**
	 * Set mobileno.
	 * 
	 * @param mobileno mobileno
	 */

	/**
	 * Set Name.
	 * 
	 * @param Name Alphanumeric identifier of the entity
	 */
	public void setName(String Name) {
		set_Value(COLUMNNAME_Name, Name);
	}

	/**
	 * Get Name.
	 * 
	 * @return Alphanumeric identifier of the entity
	 */
	public String getName() {
		return (String) get_Value(COLUMNNAME_Name);
	}

	/**
	 * Set surveyno.
	 * 
	 * @param surveyno surveyno
	 */
	public void setsurveyno(String surveyno) {
		set_Value(COLUMNNAME_surveyno, surveyno);
	}

	/**
	 * Get surveyno.
	 * 
	 * @return surveyno
	 */
	public String getsurveyno() {
		return (String) get_Value(COLUMNNAME_surveyno);
	}

	/**
	 * Set TC_Farmer.
	 * 
	 * @param TC_Farmer_ID TC_Farmer
	 */
	public void setTC_Farmer_ID(int TC_Farmer_ID) {
		if (TC_Farmer_ID < 1)
			set_ValueNoCheck(COLUMNNAME_TC_Farmer_ID, null);
		else
			set_ValueNoCheck(COLUMNNAME_TC_Farmer_ID, Integer.valueOf(TC_Farmer_ID));
	}

	/**
	 * Get TC_Farmer.
	 * 
	 * @return TC_Farmer
	 */
	public int getTC_Farmer_ID() {
		Integer ii = (Integer) get_Value(COLUMNNAME_TC_Farmer_ID);
		if (ii == null)
			return 0;
		return ii.intValue();
	}

	/**
	 * Set Search Key.
	 * 
	 * @param Value Search key for the record in the format required - must be
	 *              unique
	 */
	public void setValue(String Value) {
		set_Value(COLUMNNAME_Value, Value);
	}

	/**
	 * Get Search Key.
	 * 
	 * @return Search key for the record in the format required - must be unique
	 */
	public String getValue() {
		return (String) get_Value(COLUMNNAME_Value);
	}

	/**
	 * Set villagename.
	 * 
	 * @param villagename villagename
	 */
	public void setvillagename(String villagename) {
		set_Value(COLUMNNAME_villagename, villagename);
	}

	/**
	 * Get villagename.
	 * 
	 * @return villagename
	 */
	public String getvillagename() {
		return (String) get_Value(COLUMNNAME_villagename);
	}

	@Override
	public void setmobileno(String mobileno) {
		// TODO Auto-generated method stub
		set_Value(COLUMNNAME_mobileno, String.valueOf(mobileno));

	}

	@Override
	public String getmobileno() {
		// TODO Auto-generated method stub
		return (String) get_Value(COLUMNNAME_mobileno);
	}

	/**
	 * Set City.
	 * 
	 * @param City Identifies a City
	 */
	public void setCity(String City) {
		set_ValueNoCheck(COLUMNNAME_City, City);
	}

	/**
	 * Get City.
	 * 
	 * @return Identifies a City
	 */
	public String getCity() {
		return (String) get_Value(COLUMNNAME_City);
	}

	/**
	 * Set district.
	 * 
	 * @param district district
	 */
	public void setdistrict(String district) {
		set_Value(COLUMNNAME_district, district);
	}

	/**
	 * Get district.
	 * 
	 * @return district
	 */
	public String getdistrict() {
		return (String) get_Value(COLUMNNAME_district);
	}

	/**
	 * Set pincode.
	 * 
	 * @param pincode pincode
	 */
	public void setpincode(String pincode) {
		set_Value(COLUMNNAME_pincode, pincode);
	}

	/**
	 * Get pincode.
	 * 
	 * @return pincode
	 */
	public String getpincode() {
		return (String) get_Value(COLUMNNAME_pincode);
	}

	/**
	 * Set state.
	 * 
	 * @param state state
	 */
	public void setstate(String state) {
		set_Value(COLUMNNAME_state, state);
	}

	/**
	 * Get state.
	 * 
	 * @return state
	 */
	public String getstate() {
		return (String) get_Value(COLUMNNAME_state);
	}

	/**
	 * Set talukname.
	 * 
	 * @param talukname talukname
	 */
	public void settalukname(String talukname) {
		set_Value(COLUMNNAME_talukname, talukname);
	}

	/**
	 * Get talukname.
	 * 
	 * @return talukname
	 */
	public String gettalukname() {
		return (String) get_Value(COLUMNNAME_talukname);
	}

	/**
	 * Set villagename2.
	 * 
	 * @param villagename2 villagename2
	 */
	public void setvillagename2(String villagename2) {
		set_Value(COLUMNNAME_villagename2, villagename2);
	}

	/**
	 * Get villagename2.
	 * 
	 * @return villagename2
	 */
	public String getvillagename2() {
		return (String) get_Value(COLUMNNAME_villagename2);
	}

	/**
	 * Set latitude.
	 * 
	 * @param latitude latitude
	 */
	public void setlatitude(String latitude) {
		set_Value(COLUMNNAME_latitude, latitude);
	}

	/**
	 * Get latitude.
	 * 
	 * @return latitude
	 */
	public String getlatitude() {
		return (String) get_Value(COLUMNNAME_latitude);
	}

	/**
	 * Set longitude.
	 * 
	 * @param longitude longitude
	 */
	public void setlongitude(String longitude) {
		set_Value(COLUMNNAME_longitude, longitude);
	}

	/**
	 * Get longitude.
	 * 
	 * @return longitude
	 */
	public String getlongitude() {
		return (String) get_Value(COLUMNNAME_longitude);
	}

}