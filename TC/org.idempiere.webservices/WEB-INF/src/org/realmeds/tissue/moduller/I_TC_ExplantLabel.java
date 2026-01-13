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

/** Generated Interface for TC_ExplantLabel
 *  @author iDempiere (generated) 
 *  @version Release 10
 */
@SuppressWarnings("all")
public interface I_TC_ExplantLabel 
{

    /** TableName=TC_ExplantLabel */
    public static final String Table_Name = "TC_ExplantLabel";

    /** AD_Table_ID=1000054 */
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
	
	/** Column name parentuuid */
    public static final String COLUMNNAME_parentuuid = "parentuuid";

	/** Set parentuuid	  */
	public void setparentuuid (String parentuuid);

	/** Get parentuuid	  */
	public String getparentuuid();

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

    /** Column name operationdate */
    public static final String COLUMNNAME_operationdate = "operationdate";

	/** Set operationdate	  */
	public void setoperationdate (Timestamp operationdate);

	/** Get operationdate	  */
	public Timestamp getoperationdate();

    /** Column name parentcultureline */
    public static final String COLUMNNAME_parentcultureline = "parentcultureline";

	/** Set parentcultureline	  */
	public void setparentcultureline (String parentcultureline);

	/** Get parentcultureline	  */
	public String getparentcultureline();

    /** Column name personalcode */
    public static final String COLUMNNAME_personalcode = "personalcode";

	/** Set personalcode	  */
	public void setpersonalcode (String personalcode);

	/** Get personalcode	  */
	public String getpersonalcode();

    /** Column name sourcingdate */
    public static final String COLUMNNAME_sourcingdate = "sourcingdate";

	/** Set sourcingdate	  */
	public void setsourcingdate (Timestamp sourcingdate);

	/** Get sourcingdate	  */
	public Timestamp getsourcingdate();

    /** Column name TC_ExplantLabel_ID */
    public static final String COLUMNNAME_TC_ExplantLabel_ID = "TC_ExplantLabel_ID";

	/** Set TC_ExplantLabel	  */
	public void setTC_ExplantLabel_ID (int TC_ExplantLabel_ID);

	/** Get TC_ExplantLabel	  */
	public int getTC_ExplantLabel_ID();

    /** Column name TC_ExplantLabel_UU */
    public static final String COLUMNNAME_TC_ExplantLabel_UU = "TC_ExplantLabel_UU";

	/** Set TC_ExplantLabel_UU	  */
	public void setTC_ExplantLabel_UU (String TC_ExplantLabel_UU);

	/** Get TC_ExplantLabel_UU	  */
	public String getTC_ExplantLabel_UU();

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
}
