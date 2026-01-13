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
 * Generated Model for TC_QualityCheck
 * 
 * @author iDempiere (generated)
 * @version Release 10 - $Id$
 */
@org.adempiere.base.Model(table = "TC_QualityCheck")
public class X_TC_QualityCheck extends PO implements I_TC_QualityCheck, I_Persistent {

	/**
	 *
	 */
	private static final long serialVersionUID = 20240304L;

	/** Standard Constructor */
	public X_TC_QualityCheck(Properties ctx, int TC_QualityCheck_ID, String trxName) {
		super(ctx, TC_QualityCheck_ID, trxName);
		/**
		 * if (TC_QualityCheck_ID == 0) { setIsDefault (false); setName (null);
		 * setTC_QualityCheck_ID (0); }
		 */
	}

	/** Standard Constructor */
	public X_TC_QualityCheck(Properties ctx, int TC_QualityCheck_ID, String trxName, String... virtualColumns) {
		super(ctx, TC_QualityCheck_ID, trxName, virtualColumns);
		/**
		 * if (TC_QualityCheck_ID == 0) { setIsDefault (false); setName (null);
		 * setTC_QualityCheck_ID (0); }
		 */
	}

	/** Load Constructor */
	public X_TC_QualityCheck(Properties ctx, ResultSet rs, String trxName) {
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
		StringBuilder sb = new StringBuilder("X_TC_QualityCheck[").append(get_ID()).append(",Name=").append(getName())
				.append("]");
		return sb.toString();
	}

	/**
	 * Set date.
	 * 
	 * @param date date
	 */
	public void setdate(Timestamp date) {
		set_Value(COLUMNNAME_date, date);
	}

	/**
	 * Set culturelabeluuid.
	 * 
	 * @param culturelabeluuid culturelabeluuid
	 */
	public void setculturelabeluuid(String culturelabeluuid) {
		set_Value(COLUMNNAME_culturelabeluuid, culturelabeluuid);
	}

	/**
	 * Get culturelabeluuid.
	 * 
	 * @return culturelabeluuid
	 */
	public String getculturelabeluuid() {
		return (String) get_Value(COLUMNNAME_culturelabeluuid);
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
	 * Set discardreason.
	 * 
	 * @param discardreason discardreason
	 */
	public void setdiscardreason(String discardreason) {
		set_Value(COLUMNNAME_discardreason, discardreason);
	}

	/**
	 * Get discardreason.
	 * 
	 * @return discardreason
	 */
	public String getdiscardreason() {
		return (String) get_Value(COLUMNNAME_discardreason);
	}

	/**
	 * Get c_uuid.
	 * 
	 * @return c_uuid
	 */
	public String getc_uuid() {
		return (String) get_Value(COLUMNNAME_c_uuid);
	}

	public org.realmeds.tissue.moduller.I_TC_DiscardType getTC_DiscardType() throws RuntimeException {
		return (org.realmeds.tissue.moduller.I_TC_DiscardType) MTable
				.get(getCtx(), org.realmeds.tissue.moduller.I_TC_DiscardType.Table_ID)
				.getPO(getTC_DiscardType_ID(), get_TrxName());
	}

	/**
	 * Set TC_DiscardType.
	 * 
	 * @param TC_DiscardType_ID TC_DiscardType
	 */
	public void setTC_DiscardType_ID(int TC_DiscardType_ID) {
		if (TC_DiscardType_ID < 1)
			set_ValueNoCheck(COLUMNNAME_TC_DiscardType_ID, null);
		else
			set_ValueNoCheck(COLUMNNAME_TC_DiscardType_ID, Integer.valueOf(TC_DiscardType_ID));
	}

	/**
	 * Get TC_DiscardType.
	 * 
	 * @return TC_DiscardType
	 */
	public int getTC_DiscardType_ID() {
		Integer ii = (Integer) get_Value(COLUMNNAME_TC_DiscardType_ID);
		if (ii == null)
			return 0;
		return ii.intValue();
	}

	/**
	 * Get date.
	 * 
	 * @return date
	 */
	public Timestamp getdate() {
		return (Timestamp) get_Value(COLUMNNAME_date);
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
	 * Set personalcode.
	 * 
	 * @param personalcode personalcode
	 */
	public void setpersonalcode(String personalcode) {
		set_Value(COLUMNNAME_personalcode, personalcode);
	}

	/**
	 * Get personalcode.
	 * 
	 * @return personalcode
	 */
	public String getpersonalcode() {
		return (String) get_Value(COLUMNNAME_personalcode);
	}

	/**
	 * Set tcpf.
	 * 
	 * @param tcpf tcpf
	 */
	public void settcpf(String tcpf) {
		set_Value(COLUMNNAME_tcpf, tcpf);
	}

	/**
	 * Get tcpf.
	 * 
	 * @return tcpf
	 */
	public String gettcpf() {
		return (String) get_Value(COLUMNNAME_tcpf);
	}

	/**
	 * Set TC_QualityCheck.
	 * 
	 * @param TC_QualityCheck_ID TC_QualityCheck
	 */
	public void setTC_QualityCheck_ID(int TC_QualityCheck_ID) {
		if (TC_QualityCheck_ID < 1)
			set_ValueNoCheck(COLUMNNAME_TC_QualityCheck_ID, null);
		else
			set_ValueNoCheck(COLUMNNAME_TC_QualityCheck_ID, Integer.valueOf(TC_QualityCheck_ID));
	}

	/**
	 * Get TC_QualityCheck.
	 * 
	 * @return TC_QualityCheck
	 */
	public int getTC_QualityCheck_ID() {
		Integer ii = (Integer) get_Value(COLUMNNAME_TC_QualityCheck_ID);
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
}