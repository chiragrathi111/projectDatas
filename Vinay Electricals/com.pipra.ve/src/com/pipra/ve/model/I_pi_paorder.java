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
package com.pipra.ve.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.compiere.model.*;
import org.compiere.util.KeyNamePair;

/** Generated Interface for pi_paorder
 *  @author iDempiere (generated) 
 *  @version Release 10
 */
@SuppressWarnings("all")
public interface I_pi_paorder 
{

    /** TableName=pi_paorder */
    public static final String Table_Name = "pi_paorder";

    /** AD_Table_ID=1000010 */
    public static final int Table_ID = MTable.getTable_ID(Table_Name);

    KeyNamePair Model = new KeyNamePair(Table_ID, Table_Name);

    /** AccessLevel = 7 - System - Client - Org 
     */
    BigDecimal accessLevel = BigDecimal.valueOf(7);

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

    /** Column name contractor */
    public static final String COLUMNNAME_contractor = "contractor";

	/** Set contractor	  */
	public void setcontractorId (int contractor);

	/** Get contractor	  */
	public int getcontractorId();

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

    /** Column name M_Product_ID */
    public static final String COLUMNNAME_M_Product_ID = "M_Product_ID";

	/** Set Product.
	  * Product, Service, Item
	  */
	public void setM_Product_ID (int M_Product_ID);

	/** Get Product.
	  * Product, Service, Item
	  */
	public int getM_Product_ID();

	public org.compiere.model.I_M_Product getM_Product() throws RuntimeException;

    /** Column name packingorder */
    public static final String COLUMNNAME_packingorder = "packingorder";

	/** Set packingorder	  */
	public void setpackingorder (boolean packingorder);

	/** Get packingorder	  */
	public boolean ispackingorder();

    /** Column name PI_Deptartment_ID */
    public static final String COLUMNNAME_PI_Deptartment_ID = "PI_Deptartment_ID";

	/** Set PI_Department	  */
	public void setPI_Deptartment_ID (int PI_Deptartment_ID);

	/** Get PI_Department	  */
	public int getPI_Deptartment_ID();

	public I_PI_Deptartment getPI_Deptartment() throws RuntimeException;

    /** Column name pi_paorder_ID */
    public static final String COLUMNNAME_pi_paorder_ID = "pi_paorder_ID";

	/** Set pi_paorder	  */
	public void setpi_paorder_ID (int pi_paorder_ID);

	/** Get pi_paorder	  */
	public int getpi_paorder_ID();

    /** Column name Processed */
    public static final String COLUMNNAME_Processed = "Processed";

	/** Set Processed.
	  * The document has been processed
	  */
	public void setProcessed (boolean Processed);

	/** Get Processed.
	  * The document has been processed
	  */
	public boolean isProcessed();

    /** Column name quantity */
    public static final String COLUMNNAME_quantity = "quantity";

	/** Set quantity	  */
	public void setquantity (BigDecimal quantity);

	/** Get quantity	  */
	public BigDecimal getquantity();

    /** Column name Status */
    public static final String COLUMNNAME_Status = "Status";

	/** Set Status.
	  * Status of the currently running check
	  */
	public void setStatus (String Status);

	/** Get Status.
	  * Status of the currently running check
	  */
	public String getStatus();

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
	
	/** Column name isrmpicked */
    public static final String COLUMNNAME_isrmpicked = "isrmpicked";

	/** Set isrmpicked	  */
	public void setisrmpicked (boolean isrmpicked);

	/** Get isrmpicked	  */
	public boolean isrmpicked();
	
	/** Column name Status */
    public static final String COLUMNNAME_isautomation = "isautomation";

	/** Set Status.
	  * Status of the currently running check
	  */
	public void setIsAutomation (boolean isautomation);

	/** Get Status.
	  * Status of the currently running check
	  */
	public boolean getIsAutomation();
}
