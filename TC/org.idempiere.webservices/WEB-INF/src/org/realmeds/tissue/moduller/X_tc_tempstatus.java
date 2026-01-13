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
 * Generated Model for tc_tempstatus
 * 
 * @author iDempiere (generated)
 * @version Release 10 - $Id$
 */
@org.adempiere.base.Model(table = "tc_tempstatus")
public class X_tc_tempstatus extends PO implements I_tc_tempstatus, I_Persistent {

	/**
	 *
	 */
	private static final long serialVersionUID = 20240607L;

	/** Standard Constructor */
	public X_tc_tempstatus(Properties ctx, int tc_tempstatus_ID, String trxName) {
		super(ctx, tc_tempstatus_ID, trxName);
		/**
		 * if (tc_tempstatus_ID == 0) { settc_tempstatus_ID (0); }
		 */
	}

	/** Standard Constructor */
	public X_tc_tempstatus(Properties ctx, int tc_tempstatus_ID, String trxName, String... virtualColumns) {
		super(ctx, tc_tempstatus_ID, trxName, virtualColumns);
		/**
		 * if (tc_tempstatus_ID == 0) { settc_tempstatus_ID (0); }
		 */
	}

	/** Load Constructor */
	public X_tc_tempstatus(Properties ctx, ResultSet rs, String trxName) {
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
		StringBuilder sb = new StringBuilder("X_tc_tempstatus[").append(get_ID()).append(",Name=").append(getName())
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
	 * Set tc_tempstatus_UU.
	 * 
	 * @param tc_tempstatus_UU tc_tempstatus_UU
	 */
	public void settc_tempstatus_UU(String tc_tempstatus_UU) {
		set_ValueNoCheck(COLUMNNAME_tc_tempstatus_UU, tc_tempstatus_UU);
	}

	/**
	 * Get tc_tempstatus_UU.
	 * 
	 * @return tc_tempstatus_UU
	 */
	public String gettc_tempstatus_UU() {
		return (String) get_Value(COLUMNNAME_tc_tempstatus_UU);
	}

	/**
	 * Set max_humidity.
	 * 
	 * @param max_humidity max_humidity
	 */
	public void setmax_humidity(String max_humidity) {
		set_Value(COLUMNNAME_max_humidity, max_humidity);
	}

	/**
	 * Get max_humidity.
	 * 
	 * @return max_humidity
	 */
	public String getmax_humidity() {
		return (String) get_Value(COLUMNNAME_max_humidity);
	}

	/**
	 * Set max_temperature.
	 * 
	 * @param max_temperature max_temperature
	 */
	public void setmax_temperature(String max_temperature) {
		set_Value(COLUMNNAME_max_temperature, max_temperature);
	}

	/**
	 * Get max_temperature.
	 * 
	 * @return max_temperature
	 */
	public String getmax_temperature() {
		return (String) get_Value(COLUMNNAME_max_temperature);
	}

	/**
	 * Set min_humidity.
	 * 
	 * @param min_humidity min_humidity
	 */
	public void setmin_humidity(String min_humidity) {
		set_Value(COLUMNNAME_min_humidity, min_humidity);
	}

	/**
	 * Get min_humidity.
	 * 
	 * @return min_humidity
	 */
	public String getmin_humidity() {
		return (String) get_Value(COLUMNNAME_min_humidity);
	}

	/**
	 * Set min_temperature.
	 * 
	 * @param min_temperature min_temperature
	 */
	public void setmin_temperature(String min_temperature) {
		set_Value(COLUMNNAME_min_temperature, min_temperature);
	}

	/**
	 * Get min_temperature.
	 * 
	 * @return min_temperature
	 */
	public String getmin_temperature() {
		return (String) get_Value(COLUMNNAME_min_temperature);
	}

	/**
	 * Set dead_value.
	 * 
	 * @param dead_value dead_value
	 */
	public void setdead_value(String dead_value) {
		set_Value(COLUMNNAME_dead_value, dead_value);
	}

	/**
	 * Get dead_value.
	 * 
	 * @return dead_value
	 */
	public String getdead_value() {
		return (String) get_Value(COLUMNNAME_dead_value);
	}

	/**
	 * Set high_value.
	 * 
	 * @param high_value high_value
	 */
	public void sethigh_value(String high_value) {
		set_Value(COLUMNNAME_high_value, high_value);
	}

	/**
	 * Get high_value.
	 * 
	 * @return high_value
	 */
	public String gethigh_value() {
		return (String) get_Value(COLUMNNAME_high_value);
	}
}