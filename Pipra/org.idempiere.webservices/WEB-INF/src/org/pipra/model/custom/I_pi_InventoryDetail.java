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
package org.pipra.model.custom;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.compiere.model.*;
import org.compiere.util.KeyNamePair;

/** Generated Interface for pi_InventoryDetail
 *  @author iDempiere (generated) 
 *  @version Release 10
 */
@SuppressWarnings("all")
public interface I_pi_InventoryDetail 
{

    /** TableName=pi_InventoryDetail */
    public static final String Table_Name = "pi_InventoryDetail";

    /** AD_Table_ID=1000026 */
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

    /** Column name LabelUUID */
    public static final String COLUMNNAME_LabelUUID = "LabelUUID";

	/** Set LabelUUID	  */
	public void setLabelUUID (String LabelUUID);

	/** Get LabelUUID	  */
	public String getLabelUUID();

    /** Column name pi_InventoryDetail_ID */
    public static final String COLUMNNAME_pi_InventoryDetail_ID = "pi_InventoryDetail_ID";

	/** Set pi_InventoryDetail	  */
	public void setpi_InventoryDetail_ID (int pi_InventoryDetail_ID);

	/** Get pi_InventoryDetail	  */
	public int getpi_InventoryDetail_ID();

    /** Column name pi_InventoryDetail_UU */
    public static final String COLUMNNAME_pi_InventoryDetail_UU = "pi_InventoryDetail_UU";

	/** Set pi_InventoryDetail_UU	  */
	public void setpi_InventoryDetail_UU (String pi_InventoryDetail_UU);

	/** Get pi_InventoryDetail_UU	  */
	public String getpi_InventoryDetail_UU();

    /** Column name pi_InventoryLine_ID */
    public static final String COLUMNNAME_pi_InventoryLine_ID = "pi_InventoryLine_ID";

	/** Set pi_InventoryLine	  */
	public void setpi_InventoryLine_ID (int pi_InventoryLine_ID);

	/** Get pi_InventoryLine	  */
	public int getpi_InventoryLine_ID();

	public I_pi_InventoryLine getpi_InventoryLine() throws RuntimeException;

    /** Column name pi_productLabel_ID */
    public static final String COLUMNNAME_pi_productLabel_ID = "pi_productLabel_ID";

	/** Set pi_productLabel	  */
	public void setpi_productLabel_ID (int pi_productLabel_ID);

	/** Get pi_productLabel	  */
	public int getpi_productLabel_ID();

	public I_pi_productLabel getpi_productLabel() throws RuntimeException;

    /** Column name QtyBook */
    public static final String COLUMNNAME_QtyBook = "QtyBook";

	/** Set Quantity book.
	  * Book Quantity
	  */
	public void setQtyBook (BigDecimal QtyBook);

	/** Get Quantity book.
	  * Book Quantity
	  */
	public BigDecimal getQtyBook();

    /** Column name QtyCount */
    public static final String COLUMNNAME_QtyCount = "QtyCount";

	/** Set Quantity count.
	  * Counted Quantity
	  */
	public void setQtyCount (BigDecimal QtyCount);

	/** Get Quantity count.
	  * Counted Quantity
	  */
	public BigDecimal getQtyCount();

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
