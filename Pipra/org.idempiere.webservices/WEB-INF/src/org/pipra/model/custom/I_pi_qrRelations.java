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

/** Generated Interface for pi_qrRelations
 *  @author iDempiere (generated) 
 *  @version Release 10
 */
@SuppressWarnings("all")
public interface I_pi_qrRelations 
{

    /** TableName=pi_qrRelations */
    public static final String Table_Name = "pi_qrRelations";

    /** AD_Table_ID=1000020 */
    public static final int Table_ID = MTable.getTable_ID(Table_Name);

    KeyNamePair Model = new KeyNamePair(Table_ID, Table_Name);

    /** AccessLevel = 1 - Org 
     */
    BigDecimal accessLevel = BigDecimal.valueOf(1);

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

    /** Column name corderlineid */
    public static final String COLUMNNAME_corderlineid = "corderlineid";

	/** Set corderlineid	  */
	public void setcorderlineid (int corderlineid);

	/** Get corderlineid	  */
	public int getcorderlineid();

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

    /** Column name isinlocator */
    public static final String COLUMNNAME_isinlocator = "isinlocator";

	/** Set isinlocator	  */
	public void setisinlocator (int isinlocator);

	/** Get isinlocator	  */
	public int getisinlocator();

    /** Column name isshippedout */
    public static final String COLUMNNAME_isshippedout = "isshippedout";

	/** Set isshippedout	  */
	public void setisshippedout (int isshippedout);

	/** Get isshippedout	  */
	public int getisshippedout();

    /** Column name locatorid */
    public static final String COLUMNNAME_locatorid = "locatorid";

	/** Set locatorid	  */
	public void setlocatorid (int locatorid);

	/** Get locatorid	  */
	public int getlocatorid();

    /** Column name minoutlineid */
    public static final String COLUMNNAME_minoutlineid = "minoutlineid";

	/** Set minoutlineid	  */
	public void setminoutlineid (int minoutlineid);

	/** Get minoutlineid	  */
	public int getminoutlineid();

    /** Column name palletuuid */
    public static final String COLUMNNAME_palletuuid = "palletuuid";

	/** Set palletuuid	  */
	public void setpalletuuid (String palletuuid);

	/** Get palletuuid	  */
	public String getpalletuuid();

    /** Column name pi_qrRelations_ID */
    public static final String COLUMNNAME_pi_qrRelations_ID = "pi_qrRelations_ID";

	/** Set pi_qrRelations	  */
	public void setpi_qrRelations_ID (int pi_qrRelations_ID);

	/** Get pi_qrRelations	  */
	public int getpi_qrRelations_ID();

    /** Column name productid */
    public static final String COLUMNNAME_productid = "productid";

	/** Set productid	  */
	public void setproductid (int productid);

	/** Get productid	  */
	public int getproductid();

    /** Column name pstatus */
    public static final String COLUMNNAME_pstatus = "pstatus";

	/** Set pstatus	  */
	public void setpstatus (String pstatus);

	/** Get pstatus	  */
	public String getpstatus();

    /** Column name quantity */
    public static final String COLUMNNAME_quantity = "quantity";

	/** Set quantity	  */
	public void setquantity (BigDecimal quantity);

	/** Get quantity	  */
	public BigDecimal getquantity();

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
