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
 * Generated Model for tc_temperatureStatus
 * 
 * @author iDempiere (generated)
 * @version Release 10 - $Id$
 */
@org.adempiere.base.Model(table = "tc_temperatureStatus")
public class X_tc_temperatureStatus extends PO implements I_tc_temperatureStatus, I_Persistent {

	/**
	 *
	 */
	private static final long serialVersionUID = 20240607L;

	/** Standard Constructor */
	public X_tc_temperatureStatus(Properties ctx, int tc_temperatureStatus_ID, String trxName) {
		super(ctx, tc_temperatureStatus_ID, trxName);
		/**
		 * if (tc_temperatureStatus_ID == 0) { settc_temperatureStatus_ID (0); }
		 */
	}

	/** Standard Constructor */
	public X_tc_temperatureStatus(Properties ctx, int tc_temperatureStatus_ID, String trxName,
			String... virtualColumns) {
		super(ctx, tc_temperatureStatus_ID, trxName, virtualColumns);
		/**
		 * if (tc_temperatureStatus_ID == 0) { settc_temperatureStatus_ID (0); }
		 */
	}

	/** Load Constructor */
	public X_tc_temperatureStatus(Properties ctx, ResultSet rs, String trxName) {
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
		StringBuilder sb = new StringBuilder("X_tc_temperatureStatus[").append(get_ID()).append(",Name=")
				.append(getName()).append("]");
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
	 * Set Humidity.
	 * 
	 * @param Humidity Humidity
	 */
	public void setHumidity(String Humidity) {
		set_Value(COLUMNNAME_Humidity, Humidity);
	}

	/**
	 * Get Humidity.
	 * 
	 * @return Humidity
	 */
	public String getHumidity() {
		return (String) get_Value(COLUMNNAME_Humidity);
	}

	public org.compiere.model.I_M_LocatorType getM_LocatorType() throws RuntimeException {
		return (org.compiere.model.I_M_LocatorType) MTable.get(getCtx(), org.compiere.model.I_M_LocatorType.Table_ID)
				.getPO(getM_LocatorType_ID(), get_TrxName());
	}

	public org.realmeds.tissue.moduller.I_TC_devicedata getTC_devicedata() throws RuntimeException {
		return (org.realmeds.tissue.moduller.I_TC_devicedata) MTable
				.get(getCtx(), org.realmeds.tissue.moduller.I_TC_devicedata.Table_ID)
				.getPO(getTC_devicedata_ID(), get_TrxName());
	}

	/**
	 * Set TC_devicedata.
	 * 
	 * @param TC_devicedata_ID TC_devicedata
	 */
	public void setTC_devicedata_ID(int TC_devicedata_ID) {
		if (TC_devicedata_ID < 1)
			set_ValueNoCheck(COLUMNNAME_TC_devicedata_ID, null);
		else
			set_ValueNoCheck(COLUMNNAME_TC_devicedata_ID, Integer.valueOf(TC_devicedata_ID));
	}

	/**
	 * Get TC_devicedata.
	 * 
	 * @return TC_devicedata
	 */
	public int getTC_devicedata_ID() {
		Integer ii = (Integer) get_Value(COLUMNNAME_TC_devicedata_ID);
		if (ii == null)
			return 0;
		return ii.intValue();
	}

	/**
	 * Set Locator Type.
	 * 
	 * @param M_LocatorType_ID Locator Type
	 */
	public void setM_LocatorType_ID(int M_LocatorType_ID) {
		if (M_LocatorType_ID < 1)
			set_Value(COLUMNNAME_M_LocatorType_ID, null);
		else
			set_Value(COLUMNNAME_M_LocatorType_ID, Integer.valueOf(M_LocatorType_ID));
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

	/**
	 * Set tc_temperatureStatus.
	 * 
	 * @param tc_temperatureStatus_ID tc_temperatureStatus
	 */
	public void settc_temperatureStatus_ID(int tc_temperatureStatus_ID) {
		if (tc_temperatureStatus_ID < 1)
			set_ValueNoCheck(COLUMNNAME_tc_temperatureStatus_ID, null);
		else
			set_ValueNoCheck(COLUMNNAME_tc_temperatureStatus_ID, Integer.valueOf(tc_temperatureStatus_ID));
	}

	/**
	 * Get tc_temperatureStatus.
	 * 
	 * @return tc_temperatureStatus
	 */
	public int gettc_temperatureStatus_ID() {
		Integer ii = (Integer) get_Value(COLUMNNAME_tc_temperatureStatus_ID);
		if (ii == null)
			return 0;
		return ii.intValue();
	}

	/**
	 * Set tc_temperatureStatus_UU.
	 * 
	 * @param tc_temperatureStatus_UU tc_temperatureStatus_UU
	 */
	public void settc_temperatureStatus_UU(String tc_temperatureStatus_UU) {
		set_ValueNoCheck(COLUMNNAME_tc_temperatureStatus_UU, tc_temperatureStatus_UU);
	}

	/**
	 * Get tc_temperatureStatus_UU.
	 * 
	 * @return tc_temperatureStatus_UU
	 */
	public String gettc_temperatureStatus_UU() {
		return (String) get_Value(COLUMNNAME_tc_temperatureStatus_UU);
	}

	public org.realmeds.tissue.moduller.I_tc_tempstatus gettc_tempstatus() throws RuntimeException {
		return (org.realmeds.tissue.moduller.I_tc_tempstatus) MTable
				.get(getCtx(), org.realmeds.tissue.moduller.I_tc_tempstatus.Table_ID)
				.getPO(gettc_tempstatus_ID(), get_TrxName());
	}

	/**
	 * Set tc_tempstatus.
	 * 
	 * @param tc_tempstatus_ID tc_tempstatus
	 */
	public void settc_tempstatus_ID(int tc_tempstatus_ID) {
		if (tc_tempstatus_ID < 1)
			set_ValueNoCheck(COLUMNNAME_tc_tempstatus_ID, null);
		else
			set_ValueNoCheck(COLUMNNAME_tc_tempstatus_ID, Integer.valueOf(tc_tempstatus_ID));
	}

	/**
	 * Get tc_tempstatus.
	 * 
	 * @return tc_tempstatus
	 */
	public int gettc_tempstatus_ID() {
		Integer ii = (Integer) get_Value(COLUMNNAME_tc_tempstatus_ID);
		if (ii == null)
			return 0;
		return ii.intValue();
	}

	/**
	 * Set Temperature.
	 * 
	 * @param Temperature Temperature
	 */
	public void setTemperature(String Temperature) {
		set_Value(COLUMNNAME_Temperature, Temperature);
	}

	/**
	 * Get Temperature.
	 * 
	 * @return Temperature
	 */
	public String getTemperature() {
		return (String) get_Value(COLUMNNAME_Temperature);
	}

	/** Normal = N */
	public static final String HUMIDITYSTATUS_Normal = "Normal";
	/** Over Cool = OC */
	public static final String HUMIDITYSTATUS_LessHumidity = "Less Humidity";
	/** Over Heat = OH */
	public static final String HUMIDITYSTATUS_HighHumidity = "High Humidity";

	/**
	 * Set humiditystatus.
	 * 
	 * @param humiditystatus humiditystatus
	 */
	public void sethumiditystatus(String humiditystatus) {

		set_Value(COLUMNNAME_humiditystatus, humiditystatus);
	}

	/**
	 * Get humiditystatus.
	 * 
	 * @return humiditystatus
	 */
	public String gethumiditystatus() {
		return (String) get_Value(COLUMNNAME_humiditystatus);
	}

	/**
	 * Set battery_percentage.
	 * 
	 * @param battery_percentage battery_percentage
	 */
	public void setbattery_percentage(String battery_percentage) {
		set_Value(COLUMNNAME_battery_percentage, battery_percentage);
	}

	/**
	 * Get battery_percentage.
	 * 
	 * @return battery_percentage
	 */
	public String getbattery_percentage() {
		return (String) get_Value(COLUMNNAME_battery_percentage);
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