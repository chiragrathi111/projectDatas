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

/** Generated Interface for TC_IntermediateVisit
 *  @author iDempiere (generated) 
 *  @version Release 10
 */
@SuppressWarnings("all")
public interface I_TC_IntermediateVisit 
{

    /** TableName=TC_IntermediateVisit */
    public static final String Table_Name = "TC_IntermediateVisit";

    /** AD_Table_ID=1000013 */
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

    /** Column name nextvisitdate */
    public static final String COLUMNNAME_nextvisitdate = "nextvisitdate";

	/** Set Next Visit Date	  */
	public void setnextvisitdate (Timestamp nextvisitdate);

	/** Get Next Visit Date	  */
	public Timestamp getnextvisitdate();

    /** Column name reasondetails */
    public static final String COLUMNNAME_reasondetails = "reasondetails";

	/** Set Reason Details	  */
	public void setreasondetails (String reasondetails);

	/** Get Reason Details	  */
	public String getreasondetails();

    /** Column name reviewdetails */
    public static final String COLUMNNAME_reviewdetails = "reviewdetails";

	/** Set Review Details	  */
	public void setreviewdetails (String reviewdetails);

	/** Get Review Details	  */
	public String getreviewdetails();

    /** Column name TC_Decision_ID */
    public static final String COLUMNNAME_TC_Decision_ID = "TC_Decision_ID";

	/** Set Decision	  */
	public void setTC_Decision_ID (int TC_Decision_ID);

	/** Get Decision	  */
	public int getTC_Decision_ID();

	public org.realmeds.tissue.moduller.I_TC_Decision getTC_Decision() throws RuntimeException;

    /** Column name TC_Farmer_ID */
    public static final String COLUMNNAME_TC_Farmer_ID = "TC_Farmer_ID";

	/** Set TC Farmer	  */
	public void setTC_Farmer_ID (int TC_Farmer_ID);

	/** Get TC Farmer	  */
	public int getTC_Farmer_ID();

	public org.realmeds.tissue.moduller.I_TC_Farmer getTC_Farmer() throws RuntimeException;

    /** Column name TC_FirstVisit_ID */
    public static final String COLUMNNAME_TC_FirstVisit_ID = "TC_FirstVisit_ID";

	/** Set TC First Visit	  */
	public void setTC_FirstVisit_ID (int TC_FirstVisit_ID);

	/** Get TC First Visit	  */
	public int getTC_FirstVisit_ID();

	public org.realmeds.tissue.moduller.I_TC_FirstVisit getTC_FirstVisit() throws RuntimeException;

    /** Column name TC_IntermediateVisit_ID */
    public static final String COLUMNNAME_TC_IntermediateVisit_ID = "TC_IntermediateVisit_ID";

	/** Set TC_IntermediateVisit	  */
	public void setTC_IntermediateVisit_ID (int TC_IntermediateVisit_ID);

	/** Get TC_IntermediateVisit	  */
	public int getTC_IntermediateVisit_ID();

    /** Column name TC_PlantDetails_ID */
    public static final String COLUMNNAME_TC_PlantDetails_ID = "TC_PlantDetails_ID";

	/** Set TC Plant Details	  */
	public void setTC_PlantDetails_ID (int TC_PlantDetails_ID);

	/** Get TC Plant Details	  */
	public int getTC_PlantDetails_ID();

	public org.realmeds.tissue.moduller.I_TC_PlantDetails getTC_PlantDetails() throws RuntimeException;

    /** Column name TC_Visit_ID */
    public static final String COLUMNNAME_TC_Visit_ID = "TC_Visit_ID";

	/** Set TC Visit	  */
	public void setTC_Visit_ID (int TC_Visit_ID);

	/** Get TC Visit	  */
	public int getTC_Visit_ID();

	public org.realmeds.tissue.moduller.I_TC_Visit getTC_Visit() throws RuntimeException;

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
	
	/** Column name isattachedocollection */
    public static final String COLUMNNAME_isattachedocollection = "isattachedocollection";

	/** Set isattachedintermediatetocollection	  */
	public void setisattachedocollection (boolean isattachedocollection);

	/** Get isattachedintermediatetocollection	  */
	public boolean isattachedocollection();
}
