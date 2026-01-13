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
 * Generated Model for TC_Visit
 * 
 * @author iDempiere (generated)
 * @version Release 10 - $Id$
 */
@org.adempiere.base.Model(table = "TC_Visit")
public class X_TC_Visit extends PO implements I_TC_Visit, I_Persistent {

	/**
	 *
	 */
	private static final long serialVersionUID = 20240304L;

	/** Standard Constructor */
	public X_TC_Visit(Properties ctx, int TC_Visit_ID, String trxName) {
		super(ctx, TC_Visit_ID, trxName);
		/**
		 * if (TC_Visit_ID == 0) { setIsDefault (false); setName (null); setTC_Visit_ID
		 * (0); }
		 */
	}

	/** Standard Constructor */
	public X_TC_Visit(Properties ctx, int TC_Visit_ID, String trxName, String... virtualColumns) {
		super(ctx, TC_Visit_ID, trxName, virtualColumns);
		/**
		 * if (TC_Visit_ID == 0) { setIsDefault (false); setName (null); setTC_Visit_ID
		 * (0); }
		 */
	}

	/** Load Constructor */
	public X_TC_Visit(Properties ctx, ResultSet rs, String trxName) {
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
		StringBuilder sb = new StringBuilder("X_TC_Visit[").append(get_ID()).append(",Name=").append(getName())
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
	 * Set reason.
	 * 
	 * @param reason reason
	 */
	public void setreason(String reason) {
		set_Value(COLUMNNAME_reason, reason);
	}

	/**
	 * Get reason.
	 * 
	 * @return reason
	 */
	public String getreason() {
		return (String) get_Value(COLUMNNAME_reason);
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
	 * Set mobileno.
	 * 
	 * @param mobileno mobileno
	 */
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

	public org.realmeds.tissue.moduller.I_TC_Farmer getTC_Farmer() throws RuntimeException {
		return (org.realmeds.tissue.moduller.I_TC_Farmer) MTable
				.get(getCtx(), org.realmeds.tissue.moduller.I_TC_Farmer.Table_ID)
				.getPO(getTC_Farmer_ID(), get_TrxName());
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
	 * Set TC_Visit.
	 * 
	 * @param TC_Visit_ID TC_Visit
	 */
	public void setTC_Visit_ID(int TC_Visit_ID) {
		if (TC_Visit_ID < 1)
			set_ValueNoCheck(COLUMNNAME_TC_Visit_ID, null);
		else
			set_ValueNoCheck(COLUMNNAME_TC_Visit_ID, Integer.valueOf(TC_Visit_ID));
	}

	/**
	 * Get TC_Visit.
	 * 
	 * @return TC_Visit
	 */
	public int getTC_Visit_ID() {
		Integer ii = (Integer) get_Value(COLUMNNAME_TC_Visit_ID);
		if (ii == null)
			return 0;
		return ii.intValue();
	}

	public org.realmeds.tissue.moduller.I_TC_VisitType getTC_VisitType() throws RuntimeException {
		return (org.realmeds.tissue.moduller.I_TC_VisitType) MTable
				.get(getCtx(), org.realmeds.tissue.moduller.I_TC_VisitType.Table_ID)
				.getPO(getTC_VisitType_ID(), get_TrxName());
	}

	/**
	 * Set TC_VisitType.
	 * 
	 * @param TC_VisitType_ID TC_VisitType
	 */
	public void setTC_VisitType_ID(int TC_VisitType_ID) {
		if (TC_VisitType_ID < 1)
			set_ValueNoCheck(COLUMNNAME_TC_VisitType_ID, null);
		else
			set_ValueNoCheck(COLUMNNAME_TC_VisitType_ID, Integer.valueOf(TC_VisitType_ID));
	}

	/**
	 * Get TC_VisitType.
	 * 
	 * @return TC_VisitType
	 */
	public int getTC_VisitType_ID() {
		Integer ii = (Integer) get_Value(COLUMNNAME_TC_VisitType_ID);
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

	public org.realmeds.tissue.moduller.I_TC_Status getTC_Status() throws RuntimeException {
		return (org.realmeds.tissue.moduller.I_TC_Status) MTable
				.get(getCtx(), org.realmeds.tissue.moduller.I_TC_Status.Table_ID)
				.getPO(getTC_Status_ID(), get_TrxName());
	}

	/**
	 * Set TC_Status.
	 * 
	 * @param TC_Status_ID TC_Status
	 */
	public void setTC_Status_ID(int TC_Status_ID) {
		if (TC_Status_ID < 1)
			set_ValueNoCheck(COLUMNNAME_TC_Status_ID, null);
		else
			set_ValueNoCheck(COLUMNNAME_TC_Status_ID, Integer.valueOf(TC_Status_ID));
	}

	/**
	 * Get TC_Status.
	 * 
	 * @return TC_Status
	 */
	public int getTC_Status_ID() {
		Integer ii = (Integer) get_Value(COLUMNNAME_TC_Status_ID);
		if (ii == null)
			return 0;
		return ii.intValue();
	}
	
	/**
	 * Set visitdone.
	 * 
	 * @param visitdone visitdone
	 */
	public void setvisitdone(boolean visitdone) {
		set_Value(COLUMNNAME_visitdone, Boolean.valueOf(visitdone));
	}

	/**
	 * Get visitdone.
	 * 
	 * @return visitdone
	 */
	public boolean isvisitdone() {
		Object oo = get_Value(COLUMNNAME_visitdone);
		if (oo != null) {
			if (oo instanceof Boolean)
				return ((Boolean) oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}
	
	/**
	 * Set cycleno.
	 * 
	 * @param cycleno cycleno
	 */
	public void setcycleno(int cycleno) {
		set_Value(COLUMNNAME_cycleno, Integer.valueOf(cycleno));
	}

	/**
	 * Get cycleno.
	 * 
	 * @return cycleno
	 */
	public int getcycleno() {
		Integer ii = (Integer) get_Value(COLUMNNAME_cycleno);
		if (ii == null)
			return 0;
		return ii.intValue();
	}
}