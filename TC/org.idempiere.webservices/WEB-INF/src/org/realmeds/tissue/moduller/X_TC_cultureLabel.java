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
 * Generated Model for TC_cultureLabel
 * 
 * @author iDempiere (generated)
 * @version Release 10 - $Id$
 */
@org.adempiere.base.Model(table = "TC_cultureLabel")
public class X_TC_cultureLabel extends PO implements I_TC_cultureLabel, I_Persistent {

	/**
	 *
	 */
	private static final long serialVersionUID = 20240415L;

	/** Standard Constructor */
	public X_TC_cultureLabel(Properties ctx, int TC_cultureLabel_ID, String trxName) {
		super(ctx, TC_cultureLabel_ID, trxName);
		/**
		 * if (TC_cultureLabel_ID == 0) { setTC_cultureLabel_ID (0); }
		 */
	}

	/** Standard Constructor */
	public X_TC_cultureLabel(Properties ctx, int TC_cultureLabel_ID, String trxName, String... virtualColumns) {
		super(ctx, TC_cultureLabel_ID, trxName, virtualColumns);
		/**
		 * if (TC_cultureLabel_ID == 0) { setTC_cultureLabel_ID (0); }
		 */
	}

	/** Load Constructor */
	public X_TC_cultureLabel(Properties ctx, ResultSet rs, String trxName) {
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
		StringBuilder sb = new StringBuilder("X_TC_cultureLabel[").append(get_ID()).append("]");
		return sb.toString();
	}

	/**
	 * Set parentuuid.
	 * 
	 * @param parentuuid parentuuid
	 */
	public void setparentuuid(String parentuuid) {
		set_Value(COLUMNNAME_parentuuid, parentuuid);
	}

	/**
	 * Get parentuuid.
	 * 
	 * @return parentuuid
	 */
	public String getparentuuid() {
		return (String) get_Value(COLUMNNAME_parentuuid);
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
	 * Set discarddate.
	 * 
	 * @param discarddate discarddate
	 */
	public void setdiscarddate(Timestamp discarddate) {
		set_Value(COLUMNNAME_discarddate, discarddate);
	}

	/**
	 * Get discarddate.
	 * 
	 * @return discarddate
	 */
	public Timestamp getdiscarddate() {
		return (Timestamp) get_Value(COLUMNNAME_discarddate);
	}

	/**
	 * Set personalcode2.
	 * 
	 * @param personalcode2 personalcode2
	 */
	public void setpersonalcode2(String personalcode2) {
		set_Value(COLUMNNAME_personalcode2, personalcode2);
	}

	/**
	 * Get personalcode2.
	 * 
	 * @return personalcode2
	 */
	public String getpersonalcode2() {
		return (String) get_Value(COLUMNNAME_personalcode2);
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
	 * Set tcpf2.
	 * 
	 * @param tcpf2 tcpf2
	 */
	public void settcpf2(String tcpf2) {
		set_Value(COLUMNNAME_tcpf2, tcpf2);
	}

	/**
	 * Get tcpf2.
	 * 
	 * @return tcpf2
	 */
	public String gettcpf2() {
		return (String) get_Value(COLUMNNAME_tcpf2);
	}

	/**
	 * Set tosubculturecheck.
	 * 
	 * @param tosubculturecheck tosubculturecheck
	 */
	public void settosubculturecheck(boolean tosubculturecheck) {
		set_Value(COLUMNNAME_tosubculturecheck, Boolean.valueOf(tosubculturecheck));
	}

	/**
	 * Get tosubculturecheck.
	 * 
	 * @return tosubculturecheck
	 */
	public boolean istosubculturecheck() {
		Object oo = get_Value(COLUMNNAME_tosubculturecheck);
		if (oo != null) {
			if (oo instanceof Boolean)
				return ((Boolean) oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}

	/**
	 * Set culturedate.
	 * 
	 * @param culturedate culturedate
	 */
	public void setculturedate(Timestamp culturedate) {
		set_Value(COLUMNNAME_culturedate, culturedate);
	}

	/**
	 * Get culturedate.
	 * 
	 * @return culturedate
	 */
	public Timestamp getculturedate() {
		return (Timestamp) get_Value(COLUMNNAME_culturedate);
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
	 * Set Discard.
	 * 
	 * @param isDiscarded Discard
	 */
	public void setisDiscarded(boolean isDiscarded) {
		set_Value(COLUMNNAME_isDiscarded, Boolean.valueOf(isDiscarded));
	}

	/**
	 * Get Discard.
	 * 
	 * @return Discard
	 */
	public boolean isDiscarded() {
		Object oo = get_Value(COLUMNNAME_isDiscarded);
		if (oo != null) {
			if (oo instanceof Boolean)
				return ((Boolean) oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
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
	 * Set cultureoperationdate.
	 * 
	 * @param cultureoperationdate cultureoperationdate
	 */
	public void setcultureoperationdate(Timestamp cultureoperationdate) {
		set_Value(COLUMNNAME_cultureoperationdate, cultureoperationdate);
	}

	/**
	 * Get cultureoperationdate.
	 * 
	 * @return cultureoperationdate
	 */
	public Timestamp getcultureoperationdate() {
		return (Timestamp) get_Value(COLUMNNAME_cultureoperationdate);
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
	 * Set cycleno.
	 * 
	 * @param i cycleno
	 */
	public void setcycleno(int i) {
		set_Value(COLUMNNAME_cycleno, i);
	}

	/**
	 * Get cycleno.
	 * 
	 * @return cycleno
	 */
	public int getcycleno() {
		return (int) get_Value(COLUMNNAME_cycleno);
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
	 * Set personal_code.
	 * 
	 * @param personal_code personal_code
	 */
	public void setpersonal_code(String personal_code) {
		set_Value(COLUMNNAME_personal_code, personal_code);
	}

	/**
	 * Get personal_code.
	 * 
	 * @return personal_code
	 */
	public String getpersonal_code() {
		return (String) get_Value(COLUMNNAME_personal_code);
	}

	/**
	 * Set TC_cultureLabel.
	 * 
	 * @param TC_cultureLabel_ID TC_cultureLabel
	 */
	public void setTC_cultureLabel_ID(int TC_cultureLabel_ID) {
		if (TC_cultureLabel_ID < 1)
			set_ValueNoCheck(COLUMNNAME_TC_cultureLabel_ID, null);
		else
			set_ValueNoCheck(COLUMNNAME_TC_cultureLabel_ID, Integer.valueOf(TC_cultureLabel_ID));
	}

	/**
	 * Get TC_cultureLabel.
	 * 
	 * @return TC_cultureLabel
	 */
	public int getTC_cultureLabel_ID() {
		Integer ii = (Integer) get_Value(COLUMNNAME_TC_cultureLabel_ID);
		if (ii == null)
			return 0;
		return ii.intValue();
	}

	/**
	 * Set TC_cultureLabel_UU.
	 * 
	 * @param TC_cultureLabel_UU TC_cultureLabel_UU
	 */
	public void setTC_cultureLabel_UU(String TC_cultureLabel_UU) {
		set_ValueNoCheck(COLUMNNAME_TC_cultureLabel_UU, TC_cultureLabel_UU);
	}

	/**
	 * Get TC_cultureLabel_UU.
	 * 
	 * @return TC_cultureLabel_UU
	 */
	public String getTC_cultureLabel_UU() {
		return (String) get_Value(COLUMNNAME_TC_cultureLabel_UU);
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

	public org.realmeds.tissue.moduller.I_TC_MachineType getTC_MachineType() throws RuntimeException {
		return (org.realmeds.tissue.moduller.I_TC_MachineType) MTable
				.get(getCtx(), org.realmeds.tissue.moduller.I_TC_MachineType.Table_ID)
				.getPO(getTC_MachineType_ID(), get_TrxName());
	}

	/**
	 * Set TC_MachineType.
	 * 
	 * @param TC_MachineType_ID TC_MachineType
	 */
	public void setTC_MachineType_ID(int TC_MachineType_ID) {
		if (TC_MachineType_ID < 1)
			set_ValueNoCheck(COLUMNNAME_TC_MachineType_ID, null);
		else
			set_ValueNoCheck(COLUMNNAME_TC_MachineType_ID, Integer.valueOf(TC_MachineType_ID));
	}

	/**
	 * Get TC_MachineType.
	 * 
	 * @return TC_MachineType
	 */
	public int getTC_MachineType_ID() {
		Integer ii = (Integer) get_Value(COLUMNNAME_TC_MachineType_ID);
		if (ii == null)
			return 0;
		return ii.intValue();
	}

	public org.realmeds.tissue.moduller.I_TC_MediaType getTC_MediaType() throws RuntimeException {
		return (org.realmeds.tissue.moduller.I_TC_MediaType) MTable
				.get(getCtx(), org.realmeds.tissue.moduller.I_TC_MediaType.Table_ID)
				.getPO(getTC_MediaType_ID(), get_TrxName());
	}

	/**
	 * Set TC_MediaType.
	 * 
	 * @param TC_MediaType_ID TC_MediaType
	 */
	public void setTC_MediaType_ID(int TC_MediaType_ID) {
		if (TC_MediaType_ID < 1)
			set_ValueNoCheck(COLUMNNAME_TC_MediaType_ID, null);
		else
			set_ValueNoCheck(COLUMNNAME_TC_MediaType_ID, Integer.valueOf(TC_MediaType_ID));
	}

	/**
	 * Get TC_MediaType.
	 * 
	 * @return TC_MediaType
	 */
	public int getTC_MediaType_ID() {
		Integer ii = (Integer) get_Value(COLUMNNAME_TC_MediaType_ID);
		if (ii == null)
			return 0;
		return ii.intValue();
	}

	public org.realmeds.tissue.moduller.I_TC_NatureSample getTC_NatureSample() throws RuntimeException {
		return (org.realmeds.tissue.moduller.I_TC_NatureSample) MTable
				.get(getCtx(), org.realmeds.tissue.moduller.I_TC_NatureSample.Table_ID)
				.getPO(getTC_NatureSample_ID(), get_TrxName());
	}

	/**
	 * Set TC_NatureSample.
	 * 
	 * @param TC_NatureSample_ID TC_NatureSample
	 */
	public void setTC_NatureSample_ID(int TC_NatureSample_ID) {
		if (TC_NatureSample_ID < 1)
			set_ValueNoCheck(COLUMNNAME_TC_NatureSample_ID, null);
		else
			set_ValueNoCheck(COLUMNNAME_TC_NatureSample_ID, Integer.valueOf(TC_NatureSample_ID));
	}

	/**
	 * Get TC_NatureSample.
	 * 
	 * @return TC_NatureSample
	 */
	public int getTC_NatureSample_ID() {
		Integer ii = (Integer) get_Value(COLUMNNAME_TC_NatureSample_ID);
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

	public org.realmeds.tissue.moduller.I_TC_VirusTesting getTC_VirusTesting() throws RuntimeException {
		return (org.realmeds.tissue.moduller.I_TC_VirusTesting) MTable
				.get(getCtx(), org.realmeds.tissue.moduller.I_TC_VirusTesting.Table_ID)
				.getPO(getTC_VirusTesting_ID(), get_TrxName());
	}

	/**
	 * Set TC_VirusTesting.
	 * 
	 * @param TC_VirusTesting_ID TC_VirusTesting
	 */
	public void setTC_VirusTesting_ID(int TC_VirusTesting_ID) {
		if (TC_VirusTesting_ID < 1)
			set_ValueNoCheck(COLUMNNAME_TC_VirusTesting_ID, null);
		else
			set_ValueNoCheck(COLUMNNAME_TC_VirusTesting_ID, Integer.valueOf(TC_VirusTesting_ID));
	}

	/**
	 * Get TC_VirusTesting.
	 * 
	 * @return TC_VirusTesting
	 */
	public int getTC_VirusTesting_ID() {
		Integer ii = (Integer) get_Value(COLUMNNAME_TC_VirusTesting_ID);
		if (ii == null)
			return 0;
		return ii.intValue();
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
	 * Set Sold.
	 * 
	 * @param IsSold Organization sells this product
	 */
	public void setIsSold(boolean IsSold) {
		set_Value(COLUMNNAME_IsSold, Boolean.valueOf(IsSold));
	}

	/**
	 * Get Sold.
	 * 
	 * @return Organization sells this product
	 */
	public boolean isSold() {
		Object oo = get_Value(COLUMNNAME_IsSold);
		if (oo != null) {
			if (oo instanceof Boolean)
				return ((Boolean) oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}
}