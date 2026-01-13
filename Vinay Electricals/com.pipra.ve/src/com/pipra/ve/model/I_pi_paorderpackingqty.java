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

/** Generated Interface for pi_paorderpackingqty
 *  @author iDempiere (generated) 
 *  @version Release 10
 */
@SuppressWarnings("all")
public interface I_pi_paorderpackingqty 
{

    /** TableName=pi_paorderpackingqty */
    public static final String Table_Name = "pi_paorderpackingqty";

    /** AD_Table_ID=1000014 */
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

    /** Column name packingqty */
    public static final String COLUMNNAME_packingqty = "packingqty";

	/** Set Packing Qty	  */
	public void setpackingqty (BigDecimal packingqty);

	/** Get Packing Qty	  */
	public BigDecimal getpackingqty();

    /** Column name pi_paorder_ID */
    public static final String COLUMNNAME_pi_paorder_ID = "pi_paorder_ID";

	/** Set packing &amp;
 Assembly order	  */
	public void setpi_paorder_ID (int pi_paorder_ID);

	/** Get packing &amp;
 Assembly order	  */
	public int getpi_paorder_ID();

	public com.pipra.ve.model.I_pi_paorder getpi_paorder() throws RuntimeException;

    /** Column name pi_paorderpackingqty_ID */
    public static final String COLUMNNAME_pi_paorderpackingqty_ID = "pi_paorderpackingqty_ID";

	/** Set pi_paorderpackingqty	  */
	public void setpi_paorderpackingqty_ID (int pi_paorderpackingqty_ID);

	/** Get pi_paorderpackingqty	  */
	public int getpi_paorderpackingqty_ID();

    /** Column name pi_paorderpackingqty_UU */
    public static final String COLUMNNAME_pi_paorderpackingqty_UU = "pi_paorderpackingqty_UU";

	/** Set pi_paorderpackingqty_UU	  */
	public void setpi_paorderpackingqty_UU (String pi_paorderpackingqty_UU);

	/** Get pi_paorderpackingqty_UU	  */
	public String getpi_paorderpackingqty_UU();

    /** Column name received */
    public static final String COLUMNNAME_received = "received";

	/** Set Received	  */
	public void setreceived (boolean received);

	/** Get Received	  */
	public boolean isreceived();

    /** Column name statusline */
    public static final String COLUMNNAME_statusline = "statusline";

	/** Set Status Line	  */
	public void setstatusline (String statusline);

	/** Get Status Line	  */
	public String getstatusline();

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
