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
 * Generated Model for TC_FirstVisit
 * 
 * @author iDempiere (generated)
 * @version Release 10 - $Id$
 */
@org.adempiere.base.Model(table = "TC_FirstVisit")
public class X_TC_FirstVisit extends PO implements I_TC_FirstVisit, I_Persistent {

	/**
	 *
	 */
	private static final long serialVersionUID = 20240304L;

	/** Standard Constructor */
	public X_TC_FirstVisit(Properties ctx, int TC_FirstVisit_ID, String trxName) {
		super(ctx, TC_FirstVisit_ID, trxName);
		/**
		 * if (TC_FirstVisit_ID == 0) { setIsDefault (false); setName (null); setplantno
		 * (0); setTC_FirstVisit_ID (0); }
		 */
	}

	/** Standard Constructor */
	public X_TC_FirstVisit(Properties ctx, int TC_FirstVisit_ID, String trxName, String... virtualColumns) {
		super(ctx, TC_FirstVisit_ID, trxName, virtualColumns);
		/**
		 * if (TC_FirstVisit_ID == 0) { setIsDefault (false); setName (null); setplantno
		 * (0); setTC_FirstVisit_ID (0); }
		 */
	}

	/** Load Constructor */
	public X_TC_FirstVisit(Properties ctx, ResultSet rs, String trxName) {
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

	public String toString() {
		StringBuilder sb = new StringBuilder("X_TC_FirstVisit[").append(get_ID()).append(",Name=").append(getName())
				.append("]");
		return sb.toString();
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
	 * Set enterdetailsofinfestation.
	 * 
	 * @param enterdetailsofinfestation enterdetailsofinfestation
	 */
	public void setenterdetailsofinfestation(String enterdetailsofinfestation) {
		set_Value(COLUMNNAME_enterdetailsofinfestation, enterdetailsofinfestation);
	}

	/**
	 * Get enterdetailsofinfestation.
	 * 
	 * @return enterdetailsofinfestation
	 */
	public String getenterdetailsofinfestation() {
		return (String) get_Value(COLUMNNAME_enterdetailsofinfestation);
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
	 * Set nextvisitdate.
	 * 
	 * @param nextvisitdate nextvisitdate
	 */
	public void setnextvisitdate(Timestamp nextvisitdate) {
		set_Value(COLUMNNAME_nextvisitdate, nextvisitdate);
	}

	/**
	 * Get nextvisitdate.
	 * 
	 * @return nextvisitdate
	 */
	public Timestamp getnextvisitdate() {
		return (Timestamp) get_Value(COLUMNNAME_nextvisitdate);
	}

	/**
	 * Set pesthistory.
	 * 
	 * @param pesthistory pesthistory
	 */
	public void setpesthistory(String pesthistory) {
		set_Value(COLUMNNAME_pesthistory, pesthistory);
	}

	/**
	 * Get pesthistory.
	 * 
	 * @return pesthistory
	 */
	public String getpesthistory() {
		return (String) get_Value(COLUMNNAME_pesthistory);
	}

	/**
	 * Set plantno.
	 * 
	 * @param plantno plantno
	 */
	public void setplantno(int plantno) {
		set_Value(COLUMNNAME_plantno, Integer.valueOf(plantno));
	}

	/**
	 * Get plantno.
	 * 
	 * @return plantno
	 */
	public int getplantno() {
		Integer ii = (Integer) get_Value(COLUMNNAME_plantno);
		if (ii == null)
			return 0;
		return ii.intValue();
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

	public org.realmeds.tissue.moduller.I_TC_FieldManagement getTC_FieldManagement() throws RuntimeException {
		return (org.realmeds.tissue.moduller.I_TC_FieldManagement) MTable
				.get(getCtx(), org.realmeds.tissue.moduller.I_TC_FieldManagement.Table_ID)
				.getPO(getTC_FieldManagement_ID(), get_TrxName());
	}

	/**
	 * Set TC_FieldManagement.
	 * 
	 * @param TC_FieldManagement_ID TC_FieldManagement
	 */
	public void setTC_FieldManagement_ID(int TC_FieldManagement_ID) {
		if (TC_FieldManagement_ID < 1)
			set_ValueNoCheck(COLUMNNAME_TC_FieldManagement_ID, null);
		else
			set_ValueNoCheck(COLUMNNAME_TC_FieldManagement_ID, Integer.valueOf(TC_FieldManagement_ID));
	}

	/**
	 * Get TC_FieldManagement.
	 * 
	 * @return TC_FieldManagement
	 */
	public int getTC_FieldManagement_ID() {
		Integer ii = (Integer) get_Value(COLUMNNAME_TC_FieldManagement_ID);
		if (ii == null)
			return 0;
		return ii.intValue();
	}

	public org.realmeds.tissue.moduller.I_TC_FieldSelection getTC_FieldSelection() throws RuntimeException {
		return (org.realmeds.tissue.moduller.I_TC_FieldSelection) MTable
				.get(getCtx(), org.realmeds.tissue.moduller.I_TC_FieldSelection.Table_ID)
				.getPO(getTC_FieldSelection_ID(), get_TrxName());
	}

	/**
	 * Set TC_FieldSelection.
	 * 
	 * @param TC_FieldSelection_ID TC_FieldSelection
	 */
	public void setTC_FieldSelection_ID(int TC_FieldSelection_ID) {
		if (TC_FieldSelection_ID < 1)
			set_ValueNoCheck(COLUMNNAME_TC_FieldSelection_ID, null);
		else
			set_ValueNoCheck(COLUMNNAME_TC_FieldSelection_ID, Integer.valueOf(TC_FieldSelection_ID));
	}

	/**
	 * Get TC_FieldSelection.
	 * 
	 * @return TC_FieldSelection
	 */
	public int getTC_FieldSelection_ID() {
		Integer ii = (Integer) get_Value(COLUMNNAME_TC_FieldSelection_ID);
		if (ii == null)
			return 0;
		return ii.intValue();
	}

	/**
	 * Set TC_FirstVisit.
	 * 
	 * @param TC_FirstVisit_ID TC_FirstVisit
	 */
	public void setTC_FirstVisit_ID(int TC_FirstVisit_ID) {
		if (TC_FirstVisit_ID < 1)
			set_ValueNoCheck(COLUMNNAME_TC_FirstVisit_ID, null);
		else
			set_ValueNoCheck(COLUMNNAME_TC_FirstVisit_ID, Integer.valueOf(TC_FirstVisit_ID));
	}

	/**
	 * Get TC_FirstVisit.
	 * 
	 * @return TC_FirstVisit
	 */
	public int getTC_FirstVisit_ID() {
		Integer ii = (Integer) get_Value(COLUMNNAME_TC_FirstVisit_ID);
		if (ii == null)
			return 0;
		return ii.intValue();
	}

	public org.realmeds.tissue.moduller.I_TC_SoilType getTC_SoilType() throws RuntimeException {
		return (org.realmeds.tissue.moduller.I_TC_SoilType) MTable
				.get(getCtx(), org.realmeds.tissue.moduller.I_TC_SoilType.Table_ID)
				.getPO(getTC_SoilType_ID(), get_TrxName());
	}

	/**
	 * Set TC_SoilType.
	 * 
	 * @param TC_SoilType_ID TC_SoilType
	 */
	public void setTC_SoilType_ID(int TC_SoilType_ID) {
		if (TC_SoilType_ID < 1)
			set_ValueNoCheck(COLUMNNAME_TC_SoilType_ID, null);
		else
			set_ValueNoCheck(COLUMNNAME_TC_SoilType_ID, Integer.valueOf(TC_SoilType_ID));
	}

	/**
	 * Get TC_SoilType.
	 * 
	 * @return TC_SoilType
	 */
	public int getTC_SoilType_ID() {
		Integer ii = (Integer) get_Value(COLUMNNAME_TC_SoilType_ID);
		if (ii == null)
			return 0;
		return ii.intValue();
	}

	public org.realmeds.tissue.moduller.I_TC_Visit getTC_Visit() throws RuntimeException {
		return (org.realmeds.tissue.moduller.I_TC_Visit) MTable
				.get(getCtx(), org.realmeds.tissue.moduller.I_TC_Visit.Table_ID).getPO(getTC_Visit_ID(), get_TrxName());
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

	public org.realmeds.tissue.moduller.I_TC_WateringType getTC_WateringType() throws RuntimeException {
		return (org.realmeds.tissue.moduller.I_TC_WateringType) MTable
				.get(getCtx(), org.realmeds.tissue.moduller.I_TC_WateringType.Table_ID)
				.getPO(getTC_WateringType_ID(), get_TrxName());
	}

	/**
	 * Set TC_WateringType.
	 * 
	 * @param TC_WateringType_ID TC_WateringType
	 */
	public void setTC_WateringType_ID(int TC_WateringType_ID) {
		if (TC_WateringType_ID < 1)
			set_ValueNoCheck(COLUMNNAME_TC_WateringType_ID, null);
		else
			set_ValueNoCheck(COLUMNNAME_TC_WateringType_ID, Integer.valueOf(TC_WateringType_ID));
	}

	/**
	 * Get TC_WateringType.
	 * 
	 * @return TC_WateringType
	 */
	public int getTC_WateringType_ID() {
		Integer ii = (Integer) get_Value(COLUMNNAME_TC_WateringType_ID);
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
	 * Set visitdate.
	 * 
	 * @param visitdate visitdate
	 */
	public void setvisitdate(Timestamp visitdate) {
		set_Value(COLUMNNAME_visitdate, visitdate);
	}

	/**
	 * Get visitdate.
	 * 
	 * @return visitdate
	 */
	public Timestamp getvisitdate() {
		return (Timestamp) get_Value(COLUMNNAME_visitdate);
	}

	/**
	 * Set isattachedintermediate.
	 * 
	 * @param isattachedintermediate isattachedintermediate
	 */
	public void setisattachedintermediate(boolean isattachedintermediate) {
		set_Value(COLUMNNAME_isattachedintermediate, Boolean.valueOf(isattachedintermediate));
	}

	/**
	 * Get isattachedintermediate.
	 * 
	 * @return isattachedintermediate
	 */
	public boolean isattachedintermediate() {
		Object oo = get_Value(COLUMNNAME_isattachedintermediate);
		if (oo != null) {
			if (oo instanceof Boolean)
				return ((Boolean) oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}
}