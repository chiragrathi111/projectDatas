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

/** Generated Interface for pi_paorderreceiveqty
 *  @author iDempiere (generated) 
 *  @version Release 10
 */
@SuppressWarnings("all")
public interface I_pi_paorderreceiveqty 
{

    /** TableName=pi_paorderreceiveqty */
    public static final String Table_Name = "pi_paorderreceiveqty";

    /** AD_Table_ID=1000015 */
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

    /** Column name fgreceivedqty */
    public static final String COLUMNNAME_fgreceivedqty = "fgreceivedqty";

	/** Set FG Received Qty	  */
	public void setfgreceivedqty (BigDecimal fgreceivedqty);

	/** Get FG Received Qty	  */
	public BigDecimal getfgreceivedqty();

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

    /** Column name pi_paorder_ID */
    public static final String COLUMNNAME_pi_paorder_ID = "pi_paorder_ID";

	/** Set packing &amp;
 Assembly order	  */
	public void setpi_paorder_ID (int pi_paorder_ID);

	/** Get packing &amp;
 Assembly order	  */
	public int getpi_paorder_ID();

	public com.pipra.ve.model.I_pi_paorder getpi_paorder() throws RuntimeException;

    /** Column name pi_paorderreceiveqty_ID */
    public static final String COLUMNNAME_pi_paorderreceiveqty_ID = "pi_paorderreceiveqty_ID";

	/** Set pi_paorderreceiveqty	  */
	public void setpi_paorderreceiveqty_ID (int pi_paorderreceiveqty_ID);

	/** Get pi_paorderreceiveqty	  */
	public int getpi_paorderreceiveqty_ID();

    /** Column name pi_paorderreceiveqty_UU */
    public static final String COLUMNNAME_pi_paorderreceiveqty_UU = "pi_paorderreceiveqty_UU";

	/** Set pi_paorderreceiveqty_UU	  */
	public void setpi_paorderreceiveqty_UU (String pi_paorderreceiveqty_UU);

	/** Get pi_paorderreceiveqty_UU	  */
	public String getpi_paorderreceiveqty_UU();

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
