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

/** Generated Interface for TC_PlantDetails
 *  @author iDempiere (generated) 
 *  @version Release 10
 */
@SuppressWarnings("all")
public interface I_TC_PlantDetails 
{

    /** TableName=TC_PlantDetails */
    public static final String Table_Name = "TC_PlantDetails";

    /** AD_Table_ID=1000011 */
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
	
	/** Column name c_uuid */
    public static final String COLUMNNAME_c_uuid = "c_uuid";

	/** Set c_uuid	  */
	public void setc_uuid (String c_uuid);

	/** Get c_uuid	  */
	public String getc_uuid();

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
	
	/** Column name parentcultureline */
    public static final String COLUMNNAME_parentcultureline = "parentcultureline";

	/** Set parentcultureline	  */
	public void setparentcultureline (String parentcultureline);

	/** Get parentcultureline	  */
	public String getparentcultureline();
	
	/** Column name reason */
    public static final String COLUMNNAME_reason = "reason";

	/** Set reason	  */
	public void setreason (String reason);

	/** Get reason	  */
	public String getreason();

    /** Column name bunceweight */
    public static final String COLUMNNAME_bunceweight = "bunceweight";

	/** Set bunceweight	  */
	public void setbunceweight (String bunceweight);

	/** Get bunceweight	  */
	public String getbunceweight();
	
	/** Column name planttaguuid */
    public static final String COLUMNNAME_planttaguuid = "planttaguuid";

	/** Set planttaguuid	  */
	public void setplanttaguuid (String planttaguuid);

	/** Get planttaguuid	  */
	public String getplanttaguuid();

    /** Column name bunchesno */
    public static final String COLUMNNAME_bunchesno = "bunchesno";

	/** Set bunchesno	  */
	public void setbunchesno (String bunchesno);

	/** Get bunchesno	  */
	public String getbunchesno();

    /** Column name codeno */
    public static final String COLUMNNAME_codeno = "codeno";

	/** Set codeno	  */
	public void setcodeno (String codeno);

	/** Get codeno	  */
	public String getcodeno();
	
	/** Column name isRejected */
    public static final String COLUMNNAME_isRejected = "isRejected";

	/** Set Rejected	  */
	public void setisRejected (boolean isRejected);

	/** Get Rejected	  */
	public boolean isRejected();

    /** Column name Created */
    public static final String COLUMNNAME_Created = "Created";

	/** Get Created.
	  * Date this record was created
	  */
	public Timestamp getCreated();

    /** Column name CreatedBy */
    public static final String COLUMNNAME_CreatedBy = "CreatedBy";

	/** Get Created By.
	  * User who created this records
	  */
	public int getCreatedBy();

    /** Column name date */
    public static final String COLUMNNAME_date = "date";

	/** Set date	  */
	public void setdate (Timestamp date);

	/** Get date	  */
	public Timestamp getdate();

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

    /** Column name diseasename */
    public static final String COLUMNNAME_diseasename = "diseasename";

	/** Set diseasename	  */
	public void setdiseasename (String diseasename);

	/** Get diseasename	  */
	public String getdiseasename();

    /** Column name Height */
    public static final String COLUMNNAME_Height = "Height";

	/** Set Height	  */
	public void setHeight (String Height);

	/** Get Height	  */
	public String getHeight();

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

    /** Column name IsDefault */
    public static final String COLUMNNAME_IsDefault = "IsDefault";

	/** Set Default.
	  * Default value
	  */
	public void setIsDefault (boolean IsDefault);

	/** Get Default.
	  * Default value
	  */
	public boolean isDefault();

    /** Column name leavesno */
    public static final String COLUMNNAME_leavesno = "leavesno";

	/** Set leavesno	  */
	public void setleavesno (int leavesno);

	/** Get leavesno	  */
	public int getleavesno();

    /** Column name medicinedetails */
    public static final String COLUMNNAME_medicinedetails = "medicinedetails";

	/** Set medicinedetails	  */
	public void setmedicinedetails (String medicinedetails);

	/** Get medicinedetails	  */
	public String getmedicinedetails();

    /** Column name Name */
    public static final String COLUMNNAME_Name = "Name";

	/** Set Name.
	  * Alphanumeric identifier of the entity
	  */
	public void setName (String Name);

	/** Get Name.
	  * Alphanumeric identifier of the entity
	  */
	public String getName();

    /** Column name stature */
    public static final String COLUMNNAME_stature = "stature";

	/** Set stature	  */
	public void setstature (String stature);

	/** Get stature	  */
	public String getstature();

    /** Column name tagno */
    public static final String COLUMNNAME_tagno = "tagno";

	/** Set tagno	  */
	public void settagno (String tagno);

	/** Get tagno	  */
	public String gettagno();

    /** Column name TC_Farmer_ID */
    public static final String COLUMNNAME_TC_Farmer_ID = "TC_Farmer_ID";

	/** Set TC_Farmer	  */
	public void setTC_Farmer_ID (int TC_Farmer_ID);

	/** Get TC_Farmer	  */
	public int getTC_Farmer_ID();

	public org.realmeds.tissue.moduller.I_TC_Farmer getTC_Farmer() throws RuntimeException;

    /** Column name TC_PlantDetails_ID */
    public static final String COLUMNNAME_TC_PlantDetails_ID = "TC_PlantDetails_ID";

	/** Set TC_PlantDetails	  */
	public void setTC_PlantDetails_ID (int TC_PlantDetails_ID);

	/** Get TC_PlantDetails	  */
	public int getTC_PlantDetails_ID();

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

    /** Column name Value */
    public static final String COLUMNNAME_Value = "Value";

	/** Set Search Key.
	  * Search key for the record in the format required - must be unique
	  */
	public void setValue (String Value);

	/** Get Search Key.
	  * Search key for the record in the format required - must be unique
	  */
	public String getValue();

    /** Column name Weight */
    public static final String COLUMNNAME_Weight = "Weight";

	/** Set Weight.
	  * Weight of a product
	  */
	public void setWeight (String Weight);

	/** Get Weight.
	  * Weight of a product
	  */
	public String getWeight();
	
	/** Column name TC_Variety_ID */
    public static final String COLUMNNAME_TC_Variety_ID = "TC_Variety_ID";

	/** Set TC_Variety	  */
	public void setTC_Variety_ID (int TC_Variety_ID);

	/** Get TC_Variety	  */
	public int getTC_Variety_ID();

	public org.realmeds.tissue.moduller.I_TC_Variety getTC_Variety() throws RuntimeException;
	
	/** Column name raw */
    public static final String COLUMNNAME_raw = "raw";

	/** Set raw	  */
	public void setraw (String raw);

	/** Get raw	  */
	public String getraw();
	
	/** Column name Columns */
    public static final String COLUMNNAME_Columns = "Columns";

	/** Set Columns.
	  * Number of columns
	  */
	public void setColumns (String Columns);

	/** Get Columns.
	  * Number of columns
	  */
	public String getColumns();
}
