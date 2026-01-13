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
import java.sql.Timestamp;
import java.util.Properties;
import org.compiere.model.*;

/**
 * Generated Model for tc_light
 * 
 * @author iDempiere (generated)
 * @version Release 10 - $Id$
 */
@org.adempiere.base.Model(table = "tc_light")
public class X_tc_light extends PO implements I_tc_light, I_Persistent {

	/**
	 *
	 */
	private static final long serialVersionUID = 20250409L;

	/** Standard Constructor */
	public X_tc_light(Properties ctx, int tc_light_ID, String trxName) {
		super(ctx, tc_light_ID, trxName);
		/**
		 * if (tc_light_ID == 0) { setislightonandoff (false); settc_light_ID (0); }
		 */
	}

	/** Standard Constructor */
	public X_tc_light(Properties ctx, int tc_light_ID, String trxName, String... virtualColumns) {
		super(ctx, tc_light_ID, trxName, virtualColumns);
		/**
		 * if (tc_light_ID == 0) { setislightonandoff (false); settc_light_ID (0); }
		 */
	}

	/** Load Constructor */
	public X_tc_light(Properties ctx, ResultSet rs, String trxName) {
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
		StringBuilder sb = new StringBuilder("X_tc_light[").append(get_ID()).append(",Name=").append(getName())
				.append("]");
		return sb.toString();
	}

	/**
	 * Set c_uuid.
	 * 
	 * @param c_uuid c_uuid
	 */
	public void setc_uuid(String c_uuid) {
		set_Value(COLUMNNAME_c_uuid, c_uuid);
	}

	/**
	 * Get c_uuid.
	 * 
	 * @return c_uuid
	 */
	public String getc_uuid() {
		return (String) get_Value(COLUMNNAME_c_uuid);
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
	 * Set islightonandoff.
	 * 
	 * @param islightonandoff islightonandoff
	 */
	public void setislightonandoff(boolean islightonandoff) {
		set_Value(COLUMNNAME_islightonandoff, Boolean.valueOf(islightonandoff));
	}

	/**
	 * Get islightonandoff.
	 * 
	 * @return islightonandoff
	 */
	public boolean islightonandoff() {
		Object oo = get_Value(COLUMNNAME_islightonandoff);
		if (oo != null) {
			if (oo instanceof Boolean)
				return ((Boolean) oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}

	/**
	 * Set lightoff.
	 * 
	 * @param lightoff lightoff
	 */
	public void setlightoff(String lightoff) {
		set_Value(COLUMNNAME_lightoff, lightoff);
	}

	/**
	 * Get lightoff.
	 * 
	 * @return lightoff
	 */
	public String getlightoff() {
		return (String) get_Value(COLUMNNAME_lightoff);
	}

	/**
	 * Set lighton.
	 * 
	 * @param lighton lighton
	 */
	public void setlighton(String lighton) {
		set_Value(COLUMNNAME_lighton, lighton);
	}

	/**
	 * Get lighton.
	 * 
	 * @return lighton
	 */
	public String getlighton() {
		return (String) get_Value(COLUMNNAME_lighton);
	}

	public org.compiere.model.I_M_LocatorType getM_LocatorType() throws RuntimeException {
		return (org.compiere.model.I_M_LocatorType) MTable.get(getCtx(), org.compiere.model.I_M_LocatorType.Table_ID)
				.getPO(getM_LocatorType_ID(), get_TrxName());
	}

	/**
	 * Set Locator Type.
	 * 
	 * @param M_LocatorType_ID Locator Type
	 */
	public void setM_LocatorType_ID(int M_LocatorType_ID) {
		if (M_LocatorType_ID < 1)
			set_ValueNoCheck(COLUMNNAME_M_LocatorType_ID, null);
		else
			set_ValueNoCheck(COLUMNNAME_M_LocatorType_ID, Integer.valueOf(M_LocatorType_ID));
	}

	/**
	 * Get Locator Type.
	 * 
	 * @return Locator Type
	 */
	public int getM_LocatorType_ID() {
		Integer ii = (Integer) get_Value(COLUMNNAME_M_LocatorType_ID);
		if (ii == null)
			return 0;
		return ii.intValue();
	}

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

	public org.realmeds.tissue.moduller.I_TC_devicedata getTC_devicedata() throws RuntimeException {
		return (org.realmeds.tissue.moduller.I_TC_devicedata) MTable
				.get(getCtx(), org.realmeds.tissue.moduller.I_TC_devicedata.Table_ID)
				.getPO(getTC_devicedata_ID(), get_TrxName());
	}

	/**
	 * Set Device ID.
	 * 
	 * @param TC_devicedata_ID Device ID
	 */
	public void setTC_devicedata_ID(int TC_devicedata_ID) {
		if (TC_devicedata_ID < 1)
			set_ValueNoCheck(COLUMNNAME_TC_devicedata_ID, null);
		else
			set_ValueNoCheck(COLUMNNAME_TC_devicedata_ID, Integer.valueOf(TC_devicedata_ID));
	}

	/**
	 * Get Device ID.
	 * 
	 * @return Device ID
	 */
	public int getTC_devicedata_ID() {
		Integer ii = (Integer) get_Value(COLUMNNAME_TC_devicedata_ID);
		if (ii == null)
			return 0;
		return ii.intValue();
	}

	/**
	 * Set tc_light.
	 * 
	 * @param tc_light_ID tc_light
	 */
	public void settc_light_ID(int tc_light_ID) {
		if (tc_light_ID < 1)
			set_ValueNoCheck(COLUMNNAME_tc_light_ID, null);
		else
			set_ValueNoCheck(COLUMNNAME_tc_light_ID, Integer.valueOf(tc_light_ID));
	}

	/**
	 * Get tc_light.
	 * 
	 * @return tc_light
	 */
	public int gettc_light_ID() {
		Integer ii = (Integer) get_Value(COLUMNNAME_tc_light_ID);
		if (ii == null)
			return 0;
		return ii.intValue();
	}

	public org.realmeds.tissue.moduller.I_TC_LightStatus getTC_LightStatus() throws RuntimeException {
		return (org.realmeds.tissue.moduller.I_TC_LightStatus) MTable
				.get(getCtx(), org.realmeds.tissue.moduller.I_TC_LightStatus.Table_ID)
				.getPO(getTC_LightStatus_ID(), get_TrxName());
	}

	/**
	 * Set TC_LightStatus.
	 * 
	 * @param TC_LightStatus_ID TC_LightStatus
	 */
	public void setTC_LightStatus_ID(int TC_LightStatus_ID) {
		if (TC_LightStatus_ID < 1)
			set_ValueNoCheck(COLUMNNAME_TC_LightStatus_ID, null);
		else
			set_ValueNoCheck(COLUMNNAME_TC_LightStatus_ID, Integer.valueOf(TC_LightStatus_ID));
	}

	/**
	 * Get TC_LightStatus.
	 * 
	 * @return TC_LightStatus
	 */
	public int getTC_LightStatus_ID() {
		Integer ii = (Integer) get_Value(COLUMNNAME_TC_LightStatus_ID);
		if (ii == null)
			return 0;
		return ii.intValue();
	}

	/**
	 * Set tc_light_UU.
	 * 
	 * @param tc_light_UU tc_light_UU
	 */
	public void settc_light_UU(String tc_light_UU) {
		set_ValueNoCheck(COLUMNNAME_tc_light_UU, tc_light_UU);
	}

	/**
	 * Get tc_light_UU.
	 * 
	 * @return tc_light_UU
	 */
	public String gettc_light_UU() {
		return (String) get_Value(COLUMNNAME_tc_light_UU);
	}

	/**
	 * Set Appearance.
	 * 
	 * @param appearance Appearance
	 */
	public void setappearance(String appearance) {
		set_Value(COLUMNNAME_appearance, appearance);
	}

	/**
	 * Get Appearance.
	 * 
	 * @return Appearance
	 */
	public String getappearance() {
		return (String) get_Value(COLUMNNAME_appearance);
	}
	
	/**
	 * Set custom_timestamp.
	 * 
	 * @param custom_timestamp custom_timestamp
	 */
	public void setcustom_timestamp(Timestamp custom_timestamp) {
		set_Value(COLUMNNAME_custom_timestamp, custom_timestamp);
	}

	/**
	 * Get custom_timestamp.
	 * 
	 * @return custom_timestamp
	 */
	public Timestamp getcustom_timestamp() {
		return (Timestamp) get_Value(COLUMNNAME_custom_timestamp);
	}

}