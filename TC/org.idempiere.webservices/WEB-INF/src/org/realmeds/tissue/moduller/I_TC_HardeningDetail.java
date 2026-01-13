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

/** Generated Interface for TC_HardeningDetail
 *  @author iDempiere (generated) 
 *  @version Release 10
 */
@SuppressWarnings("all")
public interface I_TC_HardeningDetail 
{

    /** TableName=TC_HardeningDetail */
    public static final String Table_Name = "TC_HardeningDetail";

    /** AD_Table_ID=1000027 */
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

    /** Column name cultureprocessedno */
    public static final String COLUMNNAME_cultureprocessedno = "cultureprocessedno";

	/** Set cultureprocessedno	  */
	public void setcultureprocessedno (String cultureprocessedno);

	/** Get cultureprocessedno	  */
	public String getcultureprocessedno();

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

    /** Column name rackno */
    public static final String COLUMNNAME_rackno = "rackno";

	/** Set rackno	  */
	public void setrackno (String rackno);

	/** Get rackno	  */
	public String getrackno();

    /** Column name TC_CultureDetails_ID */
    public static final String COLUMNNAME_TC_CultureDetails_ID = "TC_CultureDetails_ID";

	/** Set TC_CultureDetails	  */
	public void setTC_CultureDetails_ID (int TC_CultureDetails_ID);

	/** Get TC_CultureDetails	  */
	public int getTC_CultureDetails_ID();

	public org.realmeds.tissue.moduller.I_TC_CultureDetails getTC_CultureDetails() throws RuntimeException;

    /** Column name TC_CultureOperationDetails_ID */
    public static final String COLUMNNAME_TC_CultureOperationDetails_ID = "TC_CultureOperationDetails_ID";

	/** Set TC_CultureOperationDetails	  */
	public void setTC_CultureOperationDetails_ID (int TC_CultureOperationDetails_ID);

	/** Get TC_CultureOperationDetails	  */
	public int getTC_CultureOperationDetails_ID();

	public org.realmeds.tissue.moduller.I_TC_CultureOperationDetails getTC_CultureOperationDetails() throws RuntimeException;

    /** Column name TC_Cycle_ID */
    public static final String COLUMNNAME_TC_Cycle_ID = "TC_Cycle_ID";

	/** Set TC_Cycle	  */
	public void setTC_Cycle_ID (int TC_Cycle_ID);

	/** Get TC_Cycle	  */
	public int getTC_Cycle_ID();

	public org.realmeds.tissue.moduller.I_TC_Cycle getTC_Cycle() throws RuntimeException;

    /** Column name TC_HardeningDetail_ID */
    public static final String COLUMNNAME_TC_HardeningDetail_ID = "TC_HardeningDetail_ID";

	/** Set TC_HardeningDetail	  */
	public void setTC_HardeningDetail_ID (int TC_HardeningDetail_ID);

	/** Get TC_HardeningDetail	  */
	public int getTC_HardeningDetail_ID();

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
}
