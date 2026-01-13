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
package org.realmeds.tissue.moduller;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.compiere.model.*;
import org.compiere.util.KeyNamePair;

/** Generated Interface for TC_cultureLabel
 *  @author iDempiere (generated) 
 *  @version Release 10
 */
@SuppressWarnings("all")
public interface I_TC_cultureLabel 
{

    /** TableName=TC_cultureLabel */
    public static final String Table_Name = "TC_cultureLabel";

    /** AD_Table_ID=1000052 */
    public static final int Table_ID = MTable.getTable_ID(Table_Name);

    KeyNamePair Model = new KeyNamePair(Table_ID, Table_Name);

    /** AccessLevel = 3 - Client - Org 
     */
    BigDecimal accessLevel = BigDecimal.valueOf(3);

    /** Load Meta Data */

    /** Column name AD_Client_ID */
    public static final String COLUMNNAME_AD_Client_ID = "AD_Client_ID";

	/** Get Tenant.
	  * Tenant for this installation.
	  */
	public int getAD_Client_ID();

    /** Column name AD_Org_ID */
    public static final String COLUMNNAME_AD_Org_ID = "AD_Org_ID";

	/** Set Organization.
	  * Organizational entity within tenant
	  */
	public void setAD_Org_ID (int AD_Org_ID);

	/** Get Organization.
	  * Organizational entity within tenant
	  */
	public int getAD_Org_ID();

    /** Column name Created */
    public static final String COLUMNNAME_Created = "Created";

	/** Get Created.
	  * Date this record was created
	  */
	public Timestamp getCreated();
	
	 /** Column name discardreason */
    public static final String COLUMNNAME_discardreason = "discardreason";

	/** Set discardreason	  */
	public void setdiscardreason (String discardreason);

	/** Get discardreason	  */
	public String getdiscardreason();
	
	/** Column name personalcode2 */
    public static final String COLUMNNAME_personalcode2 = "personalcode2";
	
	/** Set personalcode2	  */
	public void setpersonalcode2 (String personalcode2);

	/** Get personalcode2	  */
	public String getpersonalcode2();

	/** Column name tcpf2 */
    public static final String COLUMNNAME_tcpf2 = "tcpf2";

	/** Set tcpf2	  */
	public void settcpf2 (String tcpf2);

	/** Get tcpf2	  */
	public String gettcpf2();
	
	/** Column name discarddate */
    public static final String COLUMNNAME_discarddate = "discarddate";

	/** Set discarddate	  */
	public void setdiscarddate (Timestamp discarddate);

	/** Get discarddate	  */
	public Timestamp getdiscarddate();

    /** Column name CreatedBy */
    public static final String COLUMNNAME_CreatedBy = "CreatedBy";

	/** Get Created By.
	  * User who created this records
	  */
	public int getCreatedBy();
	
	/** Column name Description */
    public static final String COLUMNNAME_Description = "Description";

	/** Set Description.
	  * Optional short description of the record
	  */
	public void setDescription (String Description);

	/** Get Description.
	  * Optional short description of the record
	  */
	public String getDescription();
	
	/** Column name TC_DiscardType_ID */
    public static final String COLUMNNAME_TC_DiscardType_ID = "TC_DiscardType_ID";

	/** Set TC_DiscardType	  */
	public void setTC_DiscardType_ID (int TC_DiscardType_ID);

	/** Get TC_DiscardType	  */
	public int getTC_DiscardType_ID();

	public org.realmeds.tissue.moduller.I_TC_DiscardType getTC_DiscardType() throws RuntimeException;

    /** Column name culturedate */
    public static final String COLUMNNAME_culturedate = "culturedate";

	/** Set culturedate	  */
	public void setculturedate (Timestamp culturedate);

	/** Get culturedate	  */
	public Timestamp getculturedate();

    /** Column name cultureoperationdate */
    public static final String COLUMNNAME_cultureoperationdate = "cultureoperationdate";

	/** Set cultureoperationdate	  */
	public void setcultureoperationdate (Timestamp cultureoperationdate);

	/** Get cultureoperationdate	  */
	public Timestamp getcultureoperationdate();

	/** Column name c_uuid */
    public static final String COLUMNNAME_c_uuid = "c_uuid";

	/** Set c_uuid	  */
	public void setc_uuid (String c_uuid);

	/** Get c_uuid	  */
	public String getc_uuid();

	/** Column name isDiscarded */
    public static final String COLUMNNAME_isDiscarded = "isDiscarded";

	/** Set Discard	  */
	public void setisDiscarded (boolean isDiscarded);

	/** Get Discard	  */
	public boolean isDiscarded();
	
	/** Column name tosubculturecheck */
    public static final String COLUMNNAME_tosubculturecheck = "tosubculturecheck";

	/** Set tosubculturecheck	  */
	public void settosubculturecheck (boolean tosubculturecheck);

	/** Get tosubculturecheck	  */
	public boolean istosubculturecheck();

	
    /** Column name cycleno */
    public static final String COLUMNNAME_cycleno = "cycleno";

	/** Set cycleno	  */
	public void setcycleno (int cycleno);

	/** Get cycleno	  */
	public int getcycleno();
	
	 /** Column name TC_in_ID */
    public static final String COLUMNNAME_TC_in_ID = "TC_in_ID";

	/** Set TC_in	  */
	public void setTC_in_ID (int TC_in_ID);

	/** Get TC_in	  */
	public int getTC_in_ID();

	public org.realmeds.tissue.moduller.I_TC_in getTC_in() throws RuntimeException;
	
	/** Column name TC_out_ID */
    public static final String COLUMNNAME_TC_out_ID = "TC_out_ID";

	/** Set TC_out	  */
	public void setTC_out_ID (int TC_out_ID);

	/** Get TC_out	  */
	public int getTC_out_ID();

	public org.realmeds.tissue.moduller.I_TC_out getTC_out() throws RuntimeException;

    /** Column name IsActive */
    public static final String COLUMNNAME_IsActive = "IsActive";

	/** Set Active.
	  * The record is active in the system
	  */
	public void setIsActive (boolean IsActive);

	/** Get Active.
	  * The record is active in the system
	  */
	public boolean isActive();

    /** Column name parentcultureline */
    public static final String COLUMNNAME_parentcultureline = "parentcultureline";

	/** Set parentcultureline	  */
	public void setparentcultureline (String parentcultureline);

	/** Get parentcultureline	  */
	public String getparentcultureline();

    /** Column name personal_code */
    public static final String COLUMNNAME_personal_code = "personal_code";

	/** Set personal_code	  */
	public void setpersonal_code (String personal_code);

	/** Get personal_code	  */
	public String getpersonal_code();

    /** Column name TC_cultureLabel_ID */
    public static final String COLUMNNAME_TC_cultureLabel_ID = "TC_cultureLabel_ID";

	/** Set TC_cultureLabel	  */
	public void setTC_cultureLabel_ID (int TC_cultureLabel_ID);

	/** Get TC_cultureLabel	  */
	public int getTC_cultureLabel_ID();

    /** Column name TC_cultureLabel_UU */
    public static final String COLUMNNAME_TC_cultureLabel_UU = "TC_cultureLabel_UU";

	/** Set TC_cultureLabel_UU	  */
	public void setTC_cultureLabel_UU (String TC_cultureLabel_UU);

	/** Get TC_cultureLabel_UU	  */
	public String getTC_cultureLabel_UU();
	
	/** Column name parentuuid */
    public static final String COLUMNNAME_parentuuid = "parentuuid";

	/** Set parentuuid	  */
	public void setparentuuid (String parentuuid);

	/** Get parentuuid	  */
	public String getparentuuid();

    /** Column name TC_CultureStage_ID */
    public static final String COLUMNNAME_TC_CultureStage_ID = "TC_CultureStage_ID";

	/** Set TC_CultureStage	  */
	public void setTC_CultureStage_ID (int TC_CultureStage_ID);

	/** Get TC_CultureStage	  */
	public int getTC_CultureStage_ID();

	public org.realmeds.tissue.moduller.I_TC_CultureStage getTC_CultureStage() throws RuntimeException;

    /** Column name TC_MachineType_ID */
    public static final String COLUMNNAME_TC_MachineType_ID = "TC_MachineType_ID";

	/** Set TC_MachineType	  */
	public void setTC_MachineType_ID (int TC_MachineType_ID);

	/** Get TC_MachineType	  */
	public int getTC_MachineType_ID();

	public org.realmeds.tissue.moduller.I_TC_MachineType getTC_MachineType() throws RuntimeException;

    /** Column name TC_MediaType_ID */
    public static final String COLUMNNAME_TC_MediaType_ID = "TC_MediaType_ID";

	/** Set TC_MediaType	  */
	public void setTC_MediaType_ID (int TC_MediaType_ID);

	/** Get TC_MediaType	  */
	public int getTC_MediaType_ID();

	public org.realmeds.tissue.moduller.I_TC_MediaType getTC_MediaType() throws RuntimeException;

    /** Column name TC_NatureSample_ID */
    public static final String COLUMNNAME_TC_NatureSample_ID = "TC_NatureSample_ID";

	/** Set TC_NatureSample	  */
	public void setTC_NatureSample_ID (int TC_NatureSample_ID);

	/** Get TC_NatureSample	  */
	public int getTC_NatureSample_ID();

	public org.realmeds.tissue.moduller.I_TC_NatureSample getTC_NatureSample() throws RuntimeException;

    /** Column name tcpf */
    public static final String COLUMNNAME_tcpf = "tcpf";

	/** Set tcpf	  */
	public void settcpf (String tcpf);

	/** Get tcpf	  */
	public String gettcpf();

    /** Column name tc_species_id */
    public static final String COLUMNNAME_tc_species_id = "tc_species_id";

	/** Set tc_species_id	  */
	public void settc_species_id (int tc_species_id);

	/** Get tc_species_id	  */
	public int gettc_species_id();

	public org.realmeds.tissue.moduller.I_TC_PlantSpecies gettc_species() throws RuntimeException;

    /** Column name tc_species_ids */
    public static final String COLUMNNAME_tc_species_ids = "tc_species_ids";

	/** Set tc_species_ids	  */
	public void settc_species_ids (int tc_species_ids);

	/** Get tc_species_ids	  */
	public int gettc_species_ids();

	public org.realmeds.tissue.moduller.I_TC_PlantSpecies gettc_species_() throws RuntimeException;

    /** Column name TC_VirusTesting_ID */
    public static final String COLUMNNAME_TC_VirusTesting_ID = "TC_VirusTesting_ID";

	/** Set TC_VirusTesting	  */
	public void setTC_VirusTesting_ID (int TC_VirusTesting_ID);

	/** Get TC_VirusTesting	  */
	public int getTC_VirusTesting_ID();

	public org.realmeds.tissue.moduller.I_TC_VirusTesting getTC_VirusTesting() throws RuntimeException;

    /** Column name Updated */
    public static final String COLUMNNAME_Updated = "Updated";

	/** Get Updated.
	  * Date this record was updated
	  */
	public Timestamp getUpdated();

    /** Column name UpdatedBy */
    public static final String COLUMNNAME_UpdatedBy = "UpdatedBy";

	/** Get Updated By.
	  * User who updated this records
	  */
	public int getUpdatedBy();
	
	/** Column name TC_Variety_ID */
    public static final String COLUMNNAME_TC_Variety_ID = "TC_Variety_ID";

	/** Set TC_Variety	  */
	public void setTC_Variety_ID (int TC_Variety_ID);

	/** Get TC_Variety	  */
	public int getTC_Variety_ID();

	public org.realmeds.tissue.moduller.I_TC_Variety getTC_Variety() throws RuntimeException;
	
	/** Column name IsSold */
    public static final String COLUMNNAME_IsSold = "IsSold";

	/** Set Sold.
	  * Organization sells this product
	  */
	public void setIsSold (boolean IsSold);

	/** Get Sold.
	  * Organization sells this product
	  */
	public boolean isSold();
}
