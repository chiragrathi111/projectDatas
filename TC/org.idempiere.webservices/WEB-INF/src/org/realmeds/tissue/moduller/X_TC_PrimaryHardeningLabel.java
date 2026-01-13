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
 * Generated Model for TC_PrimaryHardeningLabel
 * 
 * @author iDempiere (generated)
 * @version Release 10 - $Id$
 */
@org.adempiere.base.Model(table = "TC_PrimaryHardeningLabel")
public class X_TC_PrimaryHardeningLabel extends PO implements I_TC_PrimaryHardeningLabel, I_Persistent {

	/**
	 *
	 */
	private static final long serialVersionUID = 20240501L;

	/** Standard Constructor */
	public X_TC_PrimaryHardeningLabel(Properties ctx, int TC_PrimaryHardeningLabel_ID, String trxName) {
		super(ctx, TC_PrimaryHardeningLabel_ID, trxName);
		/**
		 * if (TC_PrimaryHardeningLabel_ID == 0) { setTC_PrimaryHardeningLabel_ID (0); }
		 */
	}

	/** Standard Constructor */
	public X_TC_PrimaryHardeningLabel(Properties ctx, int TC_PrimaryHardeningLabel_ID, String trxName,
			String... virtualColumns) {
		super(ctx, TC_PrimaryHardeningLabel_ID, trxName, virtualColumns);
		/**
		 * if (TC_PrimaryHardeningLabel_ID == 0) { setTC_PrimaryHardeningLabel_ID (0); }
		 */
	}

	/** Load Constructor */
	public X_TC_PrimaryHardeningLabel(Properties ctx, ResultSet rs, String trxName) {
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
		StringBuilder sb = new StringBuilder("X_TC_PrimaryHardeningLabel[").append(get_ID()).append("]");
		return sb.toString();
	}

	/**
	 * Set cultureprocessednumber.
	 * 
	 * @param cultureprocessednumber cultureprocessednumber
	 */
	public void setcultureprocessednumber(String cultureprocessednumber) {
		set_Value(COLUMNNAME_cultureprocessednumber, cultureprocessednumber);
	}

	/**
	 * Get cultureprocessednumber.
	 * 
	 * @return cultureprocessednumber
	 */
	public String getcultureprocessednumber() {
		return (String) get_Value(COLUMNNAME_cultureprocessednumber);
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
	 * Set lotnumber.
	 * 
	 * @param lotnumber lotnumber
	 */
	public void setlotnumber(String lotnumber) {
		set_Value(COLUMNNAME_lotnumber, lotnumber);
	}

	/**
	 * Get lotnumber.
	 * 
	 * @return lotnumber
	 */
	public String getlotnumber() {
		return (String) get_Value(COLUMNNAME_lotnumber);
	}

	/**
	 * Set operationdate.
	 * 
	 * @param operationdate operationdate
	 */
	public void setoperationdate(Timestamp operationdate) {
		set_Value(COLUMNNAME_operationdate, operationdate);
	}

	/**
	 * Get operationdate.
	 * 
	 * @return operationdate
	 */
	public Timestamp getoperationdate() {
		return (Timestamp) get_Value(COLUMNNAME_operationdate);
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
	 * Set plotnumbertray.
	 * 
	 * @param plotnumbertray plotnumbertray
	 */
	public void setplotnumbertray(String plotnumbertray) {
		set_Value(COLUMNNAME_plotnumbertray, plotnumbertray);
	}

	/**
	 * Get plotnumbertray.
	 * 
	 * @return plotnumbertray
	 */
	public String getplotnumbertray() {
		return (String) get_Value(COLUMNNAME_plotnumbertray);
	}

	/**
	 * Set sourcingdate.
	 * 
	 * @param sourcingdate sourcingdate
	 */
	public void setsourcingdate(Timestamp sourcingdate) {
		set_Value(COLUMNNAME_sourcingdate, sourcingdate);
	}

	/**
	 * Get sourcingdate.
	 * 
	 * @return sourcingdate
	 */
	public Timestamp getsourcingdate() {
		return (Timestamp) get_Value(COLUMNNAME_sourcingdate);
	}

	public org.realmeds.tissue.moduller.I_TC_CultureStage getTC_CultureStage() throws RuntimeException {
		return (org.realmeds.tissue.moduller.I_TC_CultureStage) MTable
				.get(getCtx(), org.realmeds.tissue.moduller.I_TC_CultureStage.Table_ID)
				.getPO(getTC_CultureStage_ID(), get_TrxName());
	}

	/**
	 * Set TC_CultureStage.
	 * 
	 * @param TC_CultureStage_ID TC_CultureStage
	 */
	public void setTC_CultureStage_ID(int TC_CultureStage_ID) {
		if (TC_CultureStage_ID < 1)
			set_ValueNoCheck(COLUMNNAME_TC_CultureStage_ID, null);
		else
			set_ValueNoCheck(COLUMNNAME_TC_CultureStage_ID, Integer.valueOf(TC_CultureStage_ID));
	}

	/**
	 * Get TC_CultureStage.
	 * 
	 * @return TC_CultureStage
	 */
	public int getTC_CultureStage_ID() {
		Integer ii = (Integer) get_Value(COLUMNNAME_TC_CultureStage_ID);
		if (ii == null)
			return 0;
		return ii.intValue();
	}

	public org.realmeds.tissue.moduller.I_TC_in getTC_in() throws RuntimeException {
		return (org.realmeds.tissue.moduller.I_TC_in) MTable
				.get(getCtx(), org.realmeds.tissue.moduller.I_TC_in.Table_ID).getPO(getTC_in_ID(), get_TrxName());
	}

	/**
	 * Set TC_in.
	 * 
	 * @param TC_in_ID TC_in
	 */
	public void setTC_in_ID(int TC_in_ID) {
		if (TC_in_ID < 1)
			set_ValueNoCheck(COLUMNNAME_TC_in_ID, null);
		else
			set_ValueNoCheck(COLUMNNAME_TC_in_ID, Integer.valueOf(TC_in_ID));
	}

	/**
	 * Get TC_in.
	 * 
	 * @return TC_in
	 */
	public int getTC_in_ID() {
		Integer ii = (Integer) get_Value(COLUMNNAME_TC_in_ID);
		if (ii == null)
			return 0;
		return ii.intValue();
	}

	public org.realmeds.tissue.moduller.I_TC_out getTC_out() throws RuntimeException {
		return (org.realmeds.tissue.moduller.I_TC_out) MTable
				.get(getCtx(), org.realmeds.tissue.moduller.I_TC_out.Table_ID).getPO(getTC_out_ID(), get_TrxName());
	}

	/**
	 * Set TC_out.
	 * 
	 * @param TC_out_ID TC_out
	 */
	public void setTC_out_ID(int TC_out_ID) {
		if (TC_out_ID < 1)
			set_ValueNoCheck(COLUMNNAME_TC_out_ID, null);
		else
			set_ValueNoCheck(COLUMNNAME_TC_out_ID, Integer.valueOf(TC_out_ID));
	}

	/**
	 * Get TC_out.
	 * 
	 * @return TC_out
	 */
	public int getTC_out_ID() {
		Integer ii = (Integer) get_Value(COLUMNNAME_TC_out_ID);
		if (ii == null)
			return 0;
		return ii.intValue();
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
	 * Set TC_PrimaryHardeningLabel.
	 * 
	 * @param TC_PrimaryHardeningLabel_ID TC_PrimaryHardeningLabel
	 */
	public void setTC_PrimaryHardeningLabel_ID(int TC_PrimaryHardeningLabel_ID) {
		if (TC_PrimaryHardeningLabel_ID < 1)
			set_ValueNoCheck(COLUMNNAME_TC_PrimaryHardeningLabel_ID, null);
		else
			set_ValueNoCheck(COLUMNNAME_TC_PrimaryHardeningLabel_ID, Integer.valueOf(TC_PrimaryHardeningLabel_ID));
	}

	/**
	 * Get TC_PrimaryHardeningLabel.
	 * 
	 * @return TC_PrimaryHardeningLabel
	 */
	public int getTC_PrimaryHardeningLabel_ID() {
		Integer ii = (Integer) get_Value(COLUMNNAME_TC_PrimaryHardeningLabel_ID);
		if (ii == null)
			return 0;
		return ii.intValue();
	}

	/**
	 * Set TC_PrimaryHardeningLabel_UU.
	 * 
	 * @param TC_PrimaryHardeningLabel_UU TC_PrimaryHardeningLabel_UU
	 */
	public void setTC_PrimaryHardeningLabel_UU(String TC_PrimaryHardeningLabel_UU) {
		set_ValueNoCheck(COLUMNNAME_TC_PrimaryHardeningLabel_UU, TC_PrimaryHardeningLabel_UU);
	}

	/**
	 * Get TC_PrimaryHardeningLabel_UU.
	 * 
	 * @return TC_PrimaryHardeningLabel_UU
	 */
	public String getTC_PrimaryHardeningLabel_UU() {
		return (String) get_Value(COLUMNNAME_TC_PrimaryHardeningLabel_UU);
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
	 * Set yearcode.
	 * 
	 * @param yearcode yearcode
	 */
	public void setyearcode(String yearcode) {
		set_Value(COLUMNNAME_yearcode, yearcode);
	}

	/**
	 * Get yearcode.
	 * 
	 * @return yearcode
	 */
	public String getyearcode() {
		return (String) get_Value(COLUMNNAME_yearcode);
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
}