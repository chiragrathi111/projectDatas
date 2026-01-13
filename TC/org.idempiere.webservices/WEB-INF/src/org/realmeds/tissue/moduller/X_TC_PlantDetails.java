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

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Properties;

import org.compiere.model.I_Persistent;
import org.compiere.model.MTable;
import org.compiere.model.PO;
import org.compiere.model.POInfo;
import org.compiere.util.Env;

/**
 * Generated Model for TC_PlantDetails
 * 
 * @author iDempiere (generated)
 * @version Release 10 - $Id$
 */
@org.adempiere.base.Model(table = "TC_PlantDetails")
public class X_TC_PlantDetails extends PO implements I_TC_PlantDetails, I_Persistent {

	/**
	 *
	 */
	private static final long serialVersionUID = 20240304L;

	/** Standard Constructor */
	public X_TC_PlantDetails(Properties ctx, int TC_PlantDetails_ID, String trxName) {
		super(ctx, TC_PlantDetails_ID, trxName);
		/**
		 * if (TC_PlantDetails_ID == 0) { setcodeno (null); setIsDefault (false);
		 * setName (null); setTC_PlantDetails_ID (0); }
		 */
	}

	/** Standard Constructor */
	public X_TC_PlantDetails(Properties ctx, int TC_PlantDetails_ID, String trxName, String... virtualColumns) {
		super(ctx, TC_PlantDetails_ID, trxName, virtualColumns);
		/**
		 * if (TC_PlantDetails_ID == 0) { setcodeno (null); setIsDefault (false);
		 * setName (null); setTC_PlantDetails_ID (0); }
		 */
	}

	/** Load Constructor */
	public X_TC_PlantDetails(Properties ctx, ResultSet rs, String trxName) {
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
	 * Set planttaguuid.
	 * 
	 * @param planttaguuid planttaguuid
	 */
	public void setplanttaguuid(String planttaguuid) {
		set_Value(COLUMNNAME_planttaguuid, planttaguuid);
	}

	/**
	 * Get planttaguuid.
	 * 
	 * @return planttaguuid
	 */
	public String getplanttaguuid() {
		return (String) get_Value(COLUMNNAME_planttaguuid);
	}

	/**
	 * Set parentcultureline.
	 * 
	 * @param parentcultureline parentcultureline
	 */
	public void setparentcultureline(String parentcultureline) {
		set_Value(COLUMNNAME_parentcultureline, parentcultureline);
	}

	/**
	 * Get parentcultureline.
	 * 
	 * @return parentcultureline
	 */
	public String getparentcultureline() {
		return (String) get_Value(COLUMNNAME_parentcultureline);
	}

	public String toString() {
		StringBuilder sb = new StringBuilder("X_TC_PlantDetails[").append(get_ID()).append(",Name=").append(getName())
				.append("]");
		return sb.toString();
	}

	/**
	 * Set bunceweight.
	 * 
	 * @param bunceweight bunceweight
	 */
	public void setbunceweight(String bunceweight) {
		set_Value(COLUMNNAME_bunceweight, bunceweight);
	}

	/**
	 * Get bunceweight.
	 * 
	 * @return bunceweight
	 */
	public String getbunceweight() {
		return (String) get_Value(COLUMNNAME_bunceweight);
	}

	/**
	 * Set Rejected.
	 * 
	 * @param isRejected Rejected
	 */
	public void setisRejected(boolean isRejected) {
		set_Value(COLUMNNAME_isRejected, Boolean.valueOf(isRejected));
	}

	/**
	 * Get Rejected.
	 * 
	 * @return Rejected
	 */
	public boolean isRejected() {
		Object oo = get_Value(COLUMNNAME_isRejected);
		if (oo != null) {
			if (oo instanceof Boolean)
				return ((Boolean) oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}

	/**
	 * Set bunchesno.
	 * 
	 * @param bunchesno bunchesno
	 */
	public void setbunchesno(String bunchesno) {
		set_Value(COLUMNNAME_bunchesno, bunchesno);
	}

	/**
	 * Get bunchesno.
	 * 
	 * @return bunchesno
	 */
	public String getbunchesno() {
		return (String) get_Value(COLUMNNAME_bunchesno);
	}

	/**
	 * Set codeno.
	 * 
	 * @param codeno codeno
	 */
	public void setcodeno(String codeno) {
		set_Value(COLUMNNAME_codeno, codeno);
	}

	/**
	 * Get codeno.
	 * 
	 * @return codeno
	 */
	public String getcodeno() {
		return (String) get_Value(COLUMNNAME_codeno);
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
	 * Set diseasename.
	 * 
	 * @param diseasename diseasename
	 */
	public void setdiseasename(String diseasename) {
		set_Value(COLUMNNAME_diseasename, diseasename);
	}

	/**
	 * Get diseasename.
	 * 
	 * @return diseasename
	 */
	public String getdiseasename() {
		return (String) get_Value(COLUMNNAME_diseasename);
	}

	/**
	 * Set Height.
	 * 
	 * @param height Height
	 */
	public void setHeight(String height) {
		set_Value(COLUMNNAME_Height, height);
	}

	/**
	 * Get Height.
	 * 
	 * @return Height
	 */
	public String getHeight() {
		return (String) get_Value(COLUMNNAME_Height);
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
	 * Set leavesno.
	 * 
	 * @param leavesno leavesno
	 */
	public void setleavesno(int leavesno) {
		set_Value(COLUMNNAME_leavesno, Integer.valueOf(leavesno));
	}

	/**
	 * Get leavesno.
	 * 
	 * @return leavesno
	 */
	public int getleavesno() {
		Integer ii = (Integer) get_Value(COLUMNNAME_leavesno);
		if (ii == null)
			return 0;
		return ii.intValue();
	}

	/**
	 * Set medicinedetails.
	 * 
	 * @param medicinedetails medicinedetails
	 */
	public void setmedicinedetails(String medicinedetails) {
		set_Value(COLUMNNAME_medicinedetails, medicinedetails);
	}

	/**
	 * Get medicinedetails.
	 * 
	 * @return medicinedetails
	 */
	public String getmedicinedetails() {
		return (String) get_Value(COLUMNNAME_medicinedetails);
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
	 * Set stature.
	 * 
	 * @param stature stature
	 */
	public void setstature(String stature) {
		set_Value(COLUMNNAME_stature, stature);
	}

	/**
	 * Get stature.
	 * 
	 * @return stature
	 */
	public String getstature() {
		return (String) get_Value(COLUMNNAME_stature);
	}

	/**
	 * Set tagno.
	 * 
	 * @param tagno tagno
	 */
	public void settagno(String tagno) {
		set_Value(COLUMNNAME_tagno, tagno);
	}

	/**
	 * Get tagno.
	 * 
	 * @return tagno
	 */
	public String gettagno() {
		return (String) get_Value(COLUMNNAME_tagno);
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
	 * Set TC_PlantDetails.
	 * 
	 * @param TC_PlantDetails_ID TC_PlantDetails
	 */
	public void setTC_PlantDetails_ID(int TC_PlantDetails_ID) {
		if (TC_PlantDetails_ID < 1)
			set_ValueNoCheck(COLUMNNAME_TC_PlantDetails_ID, null);
		else
			set_ValueNoCheck(COLUMNNAME_TC_PlantDetails_ID, Integer.valueOf(TC_PlantDetails_ID));
	}

	/**
	 * Get TC_PlantDetails.
	 * 
	 * @return TC_PlantDetails
	 */
	public int getTC_PlantDetails_ID() {
		Integer ii = (Integer) get_Value(COLUMNNAME_TC_PlantDetails_ID);
		if (ii == null)
			return 0;
		return ii.intValue();
	}

	public org.realmeds.tissue.moduller.I_TC_PlantSpecies gettc_species() throws RuntimeException {
		return (org.realmeds.tissue.moduller.I_TC_PlantSpecies) MTable
				.get(getCtx(), org.realmeds.tissue.moduller.I_TC_PlantSpecies.Table_ID)
				.getPO(gettc_species_id(), get_TrxName());
	}

	/**
	 * Set tc_species_id.
	 * 
	 * @param tc_species_id tc_species_id
	 */
	public void settc_species_id(int tc_species_id) {
		set_Value(COLUMNNAME_tc_species_id, Integer.valueOf(tc_species_id));
	}

	/**
	 * Get tc_species_id.
	 * 
	 * @return tc_species_id
	 */
	public int gettc_species_id() {
		Integer ii = (Integer) get_Value(COLUMNNAME_tc_species_id);
		if (ii == null)
			return 0;
		return ii.intValue();
	}

	public org.realmeds.tissue.moduller.I_TC_PlantSpecies gettc_species_() throws RuntimeException {
		return (org.realmeds.tissue.moduller.I_TC_PlantSpecies) MTable
				.get(getCtx(), org.realmeds.tissue.moduller.I_TC_PlantSpecies.Table_ID)
				.getPO(gettc_species_ids(), get_TrxName());
	}

	/**
	 * Set tc_species_ids.
	 * 
	 * @param tc_species_ids tc_species_ids
	 */
	public void settc_species_ids(int tc_species_ids) {
		set_Value(COLUMNNAME_tc_species_ids, Integer.valueOf(tc_species_ids));
	}

	/**
	 * Get tc_species_ids.
	 * 
	 * @return tc_species_ids
	 */
	public int gettc_species_ids() {
		Integer ii = (Integer) get_Value(COLUMNNAME_tc_species_ids);
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
	 * Set Weight.
	 * 
	 * @param Weight Weight of a product
	 */
	public void setWeight(String Weight) {
		set_ValueNoCheck(COLUMNNAME_Weight, Weight);
	}

	/**
	 * Get Weight.
	 * 
	 * @return Weight of a product
	 */
	public String getWeight() {
		return (String) get_Value(COLUMNNAME_Weight);
	}
	
	public org.realmeds.tissue.moduller.I_TC_Variety getTC_Variety() throws RuntimeException
	{
		return (org.realmeds.tissue.moduller.I_TC_Variety)MTable.get(getCtx(), org.realmeds.tissue.moduller.I_TC_Variety.Table_ID)
			.getPO(getTC_Variety_ID(), get_TrxName());
	}

	/** Set TC_Variety.
		@param TC_Variety_ID TC_Variety
	*/
	public void setTC_Variety_ID (int TC_Variety_ID)
	{
		if (TC_Variety_ID < 1)
			set_ValueNoCheck (COLUMNNAME_TC_Variety_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_TC_Variety_ID, Integer.valueOf(TC_Variety_ID));
	}

	/** Get TC_Variety.
		@return TC_Variety	  */
	public int getTC_Variety_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_TC_Variety_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}
	
	/**
	 * Set raw.
	 * 
	 * @param raw raw
	 */
	public void setraw(String raw) {
		set_Value(COLUMNNAME_raw, raw);
	}

	/**
	 * Get raw.
	 * 
	 * @return raw
	 */
	public String getraw() {
		return (String) get_Value(COLUMNNAME_raw);
	}

	/**
	 * Set Columns.
	 * 
	 * @param Columns Number of columns
	 */
	public void setColumns(String Columns) {
		set_Value(COLUMNNAME_Columns, Columns);
	}

	/**
	 * Get Columns.
	 * 
	 * @return Number of columns
	 */
	public String getColumns() {
		return (String) get_Value(COLUMNNAME_Columns);
	}
}