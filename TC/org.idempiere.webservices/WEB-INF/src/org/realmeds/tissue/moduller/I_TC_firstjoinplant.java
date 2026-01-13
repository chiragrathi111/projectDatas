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

/** Generated Interface for TC_firstjoinplant
 *  @author iDempiere (generated) 
 *  @version Release 10
 */
@SuppressWarnings("all")
public interface I_TC_firstjoinplant 
{

    /** TableName=TC_firstjoinplant */
    public static final String Table_Name = "TC_firstjoinplant";

    /** AD_Table_ID=1000071 */
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

    /** Column name TC_firstjoinplant_ID */
    public static final String COLUMNNAME_TC_firstjoinplant_ID = "TC_firstjoinplant_ID";

	/** Set TC_firstjoinplant	  */
	public void setTC_firstjoinplant_ID (int TC_firstjoinplant_ID);

	/** Get TC_firstjoinplant	  */
	public int getTC_firstjoinplant_ID();

    /** Column name TC_firstjoinplant_UU */
    public static final String COLUMNNAME_TC_firstjoinplant_UU = "TC_firstjoinplant_UU";

	/** Set TC_firstjoinplant_UU	  */
	public void setTC_firstjoinplant_UU (String TC_firstjoinplant_UU);

	/** Get TC_firstjoinplant_UU	  */
	public String getTC_firstjoinplant_UU();

    /** Column name TC_FirstVisit_ID */
    public static final String COLUMNNAME_TC_FirstVisit_ID = "TC_FirstVisit_ID";

	/** Set TC_FirstVisit	  */
	public void setTC_FirstVisit_ID (int TC_FirstVisit_ID);

	/** Get TC_FirstVisit	  */
	public int getTC_FirstVisit_ID();

	public org.realmeds.tissue.moduller.I_TC_FirstVisit getTC_FirstVisit() throws RuntimeException;

    /** Column name TC_PlantDetails_ID */
    public static final String COLUMNNAME_TC_PlantDetails_ID = "TC_PlantDetails_ID";

	/** Set TC_PlantDetails	  */
	public void setTC_PlantDetails_ID (int TC_PlantDetails_ID);

	/** Get TC_PlantDetails	  */
	public int getTC_PlantDetails_ID();

	public org.realmeds.tissue.moduller.I_TC_PlantDetails getTC_PlantDetails() throws RuntimeException;

    /** Column name TC_plantstatus_ID */
    public static final String COLUMNNAME_TC_plantstatus_ID = "TC_plantstatus_ID";

	/** Set TC_plantstatus	  */
	public void setTC_plantstatus_ID (int TC_plantstatus_ID);

	/** Get TC_plantstatus	  */
	public int getTC_plantstatus_ID();

	public org.realmeds.tissue.moduller.I_TC_plantstatus getTC_plantstatus() throws RuntimeException;

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
