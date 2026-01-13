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

/** Generated Interface for pi_items_inout
 *  @author iDempiere (generated) 
 *  @version Release 10
 */
@SuppressWarnings("all")
public interface I_pi_items_inout 
{

    /** TableName=pi_items_inout */
    public static final String Table_Name = "pi_items_inout";

    /** AD_Table_ID=1000025 */
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

    /** Column name in_qty */
    public static final String COLUMNNAME_in_qty = "in_qty";

	/** Set In Qty	  */
	public void setin_qty (BigDecimal in_qty);

	/** Get In Qty	  */
	public BigDecimal getin_qty();

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
	
	 public static final String COLUMNNAME_IsProcessed = "processed";

		/** Set Active.
		  * The record is active in the system
		  */
		public void setIsProcessed (boolean processed);

		/** Get Active.
		  * The record is active in the system
		  */
		public boolean isProcess();

    /** Column name item_id */
    public static final String COLUMNNAME_item_id = "item_id";

	/** Set Item Id	  */
	public void setitem_id (String item_id);

	/** Get Item Id	  */
	public String getitem_id();

    /** Column name out_qty */
    public static final String COLUMNNAME_out_qty = "out_qty";

	/** Set Out Qty	  */
	public void setout_qty (BigDecimal out_qty);

	/** Get Out Qty	  */
	public BigDecimal getout_qty();

    /** Column name pi_items_inout_ID */
    public static final String COLUMNNAME_pi_items_inout_ID = "pi_items_inout_ID";

	/** Set Items Inout	  */
	public void setpi_items_inout_ID (int pi_items_inout_ID);

	/** Get Items Inout	  */
	public int getpi_items_inout_ID();

    /** Column name pi_items_inout_UU */
    public static final String COLUMNNAME_pi_items_inout_UU = "pi_items_inout_UU";

	/** Set pi_items_inout_UU	  */
	public void setpi_items_inout_UU (String pi_items_inout_UU);

	/** Get pi_items_inout_UU	  */
	public String getpi_items_inout_UU();

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
