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
import org.compiere.model.*;

/**
 * Generated Model for TC_devicedata
 * 
 * @author iDempiere (generated)
 * @version Release 10 - $Id$
 */
@org.adempiere.base.Model(table = "TC_devicedata")
public class X_TC_devicedata extends PO implements I_TC_devicedata, I_Persistent {

	/**
	 *
	 */
	private static final long serialVersionUID = 20240625L;

	/** Standard Constructor */
	public X_TC_devicedata(Properties ctx, int TC_devicedata_ID, String trxName) {
		super(ctx, TC_devicedata_ID, trxName);
		/**
		 * if (TC_devicedata_ID == 0) { setTC_devicedata_ID (0); }
		 */
	}

	/** Standard Constructor */
	public X_TC_devicedata(Properties ctx, int TC_devicedata_ID, String trxName, String... virtualColumns) {
		super(ctx, TC_devicedata_ID, trxName, virtualColumns);
		/**
		 * if (TC_devicedata_ID == 0) { setTC_devicedata_ID (0); }
		 */
	}

	/** Load Constructor */
	public X_TC_devicedata(Properties ctx, ResultSet rs, String trxName) {
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
		StringBuilder sb = new StringBuilder("X_TC_devicedata[").append(get_ID()).append(",Name=").append(getName())
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
	 * Set Frequency.
	 * 
	 * @param Frequency Frequency of events
	 */
	public void setFrequency(String Frequency) {
		set_Value(COLUMNNAME_Frequency, Frequency);
	}

	/**
	 * Get Frequency.
	 * 
	 * @return Frequency of events
	 */
	public String getFrequency() {
		return (String) get_Value(COLUMNNAME_Frequency);
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
	 * Set deviceid.
	 * 
	 * @param deviceid deviceid
	 */
	public void setdeviceid(String deviceid) {
		set_Value(COLUMNNAME_deviceid, deviceid);
	}

	/**
	 * Get deviceid.
	 * 
	 * @return deviceid
	 */
	public String getdeviceid() {
		return (String) get_Value(COLUMNNAME_deviceid);
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
	 * Set TC_devicedata_UU.
	 * 
	 * @param TC_devicedata_UU TC_devicedata_UU
	 */
	public void setTC_devicedata_UU(String TC_devicedata_UU) {
		set_ValueNoCheck(COLUMNNAME_TC_devicedata_UU, TC_devicedata_UU);
	}

	/**
	 * Get TC_devicedata_UU.
	 * 
	 * @return TC_devicedata_UU
	 */
	public String getTC_devicedata_UU() {
		return (String) get_Value(COLUMNNAME_TC_devicedata_UU);
	}

	public org.realmeds.tissue.moduller.I_TC_temperatureposition getTC_temperatureposition() throws RuntimeException {
		return (org.realmeds.tissue.moduller.I_TC_temperatureposition) MTable
				.get(getCtx(), org.realmeds.tissue.moduller.I_TC_temperatureposition.Table_ID)
				.getPO(getTC_temperatureposition_ID(), get_TrxName());
	}

	/**
	 * Set TC_temperatureposition.
	 * 
	 * @param TC_temperatureposition_ID TC_temperatureposition
	 */
	public void setTC_temperatureposition_ID(int TC_temperatureposition_ID) {
		if (TC_temperatureposition_ID < 1)
			set_ValueNoCheck(COLUMNNAME_TC_temperatureposition_ID, null);
		else
			set_ValueNoCheck(COLUMNNAME_TC_temperatureposition_ID, Integer.valueOf(TC_temperatureposition_ID));
	}

	/**
	 * Get TC_temperatureposition.
	 * 
	 * @return TC_temperatureposition
	 */
	public int getTC_temperatureposition_ID() {
		Integer ii = (Integer) get_Value(COLUMNNAME_TC_temperatureposition_ID);
		if (ii == null)
			return 0;
		return ii.intValue();
	}

	public org.realmeds.tissue.moduller.I_TC_SensorType getTC_SensorType() throws RuntimeException {
		return (org.realmeds.tissue.moduller.I_TC_SensorType) MTable
				.get(getCtx(), org.realmeds.tissue.moduller.I_TC_SensorType.Table_ID)
				.getPO(getTC_SensorType_ID(), get_TrxName());
	}

	/**
	 * Set TC_SensorType.
	 * 
	 * @param TC_SensorType_ID TC_SensorType
	 */
	public void setTC_SensorType_ID(int TC_SensorType_ID) {
		if (TC_SensorType_ID < 1)
			set_ValueNoCheck(COLUMNNAME_TC_SensorType_ID, null);
		else
			set_ValueNoCheck(COLUMNNAME_TC_SensorType_ID, Integer.valueOf(TC_SensorType_ID));
	}

	/**
	 * Get TC_SensorType.
	 * 
	 * @return TC_SensorType
	 */
	public int getTC_SensorType_ID() {
		Integer ii = (Integer) get_Value(COLUMNNAME_TC_SensorType_ID);
		if (ii == null)
			return 0;
		return ii.intValue();
	}

	/** Set Search Key.
		@param Value Search key for the record in the format required - must be unique
	*/
	public void setValue (String Value)
	{
		set_Value (COLUMNNAME_Value, Value);
	}

	/** Get Search Key.
		@return Search key for the record in the format required - must be unique
	  */
	public String getValue()
	{
		return (String)get_Value(COLUMNNAME_Value);
	}
}