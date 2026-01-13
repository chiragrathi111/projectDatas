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

/** Generated Interface for tc_primaryHardeningcultureS
 *  @author iDempiere (generated) 
 *  @version Release 10
 */
@SuppressWarnings("all")
public interface I_tc_primaryHardeningcultureS 
{

    /** TableName=tc_primaryHardeningcultureS */
    public static final String Table_Name = "tc_primaryHardeningcultureS";

    /** AD_Table_ID=1000073 */
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

    /** Column name CreatedBy */
    public static final String COLUMNNAME_CreatedBy = "CreatedBy";

	/** Get Created By.
	  * User who created this records
	  */
	public int getCreatedBy();

    /** Column name cultureuuid */
    public static final String COLUMNNAME_cultureuuid = "cultureuuid";

	/** Set cultureuuid	  */
	public void setcultureuuid (String cultureuuid);

	/** Get cultureuuid	  */
	public String getcultureuuid();

    /** Column name c_uuid */
    public static final String COLUMNNAME_c_uuid = "c_uuid";

	/** Set c_uuid	  */
	public void setc_uuid (String c_uuid);

	/** Get c_uuid	  */
	public String getc_uuid();

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

    /** Column name TC_cultureLabel_ID */
    public static final String COLUMNNAME_TC_cultureLabel_ID = "TC_cultureLabel_ID";

	/** Set TC_cultureLabel	  */
	public void setTC_cultureLabel_ID (int TC_cultureLabel_ID);

	/** Get TC_cultureLabel	  */
	public int getTC_cultureLabel_ID();

	public org.realmeds.tissue.moduller.I_TC_cultureLabel getTC_cultureLabel() throws RuntimeException;

    /** Column name tc_primaryHardeningcultureS_ID */
    public static final String COLUMNNAME_tc_primaryHardeningcultureS_ID = "tc_primaryHardeningcultureS_ID";

	/** Set tc_primaryHardeningcultureS	  */
	public void settc_primaryHardeningcultureS_ID (int tc_primaryHardeningcultureS_ID);

	/** Get tc_primaryHardeningcultureS	  */
	public int gettc_primaryHardeningcultureS_ID();

    /** Column name tc_primaryHardeningcultureS_UU */
    public static final String COLUMNNAME_tc_primaryHardeningcultureS_UU = "tc_primaryHardeningcultureS_UU";

	/** Set tc_primaryHardeningcultureS_UU	  */
	public void settc_primaryHardeningcultureS_UU (String tc_primaryHardeningcultureS_UU);

	/** Get tc_primaryHardeningcultureS_UU	  */
	public String gettc_primaryHardeningcultureS_UU();

    /** Column name TC_PrimaryHardeningLabel_ID */
    public static final String COLUMNNAME_TC_PrimaryHardeningLabel_ID = "TC_PrimaryHardeningLabel_ID";

	/** Set TC_PrimaryHardeningLabel	  */
	public void setTC_PrimaryHardeningLabel_ID (int TC_PrimaryHardeningLabel_ID);

	/** Get TC_PrimaryHardeningLabel	  */
	public int getTC_PrimaryHardeningLabel_ID();

	public org.realmeds.tissue.moduller.I_TC_PrimaryHardeningLabel getTC_PrimaryHardeningLabel() throws RuntimeException;

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
}
