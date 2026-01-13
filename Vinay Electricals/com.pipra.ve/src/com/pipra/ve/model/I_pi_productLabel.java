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

import org.compiere.model.I_M_Locator;
import org.compiere.model.MTable;
import org.compiere.util.KeyNamePair;

import com.pipra.ve.model.I_pi_productLabel;

/** Generated Interface for pi_productLabel
 *  @author iDempiere (generated) 
 *  @version Release 10
 */
@SuppressWarnings("all")
public interface I_pi_productLabel 
{

    /** TableName=pi_productLabel */
    public static final String Table_Name = "pi_productLabel";

    /** AD_Table_ID=1000015 */
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

    /** Column name C_OrderLine_ID */
    public static final String COLUMNNAME_C_OrderLine_ID = "C_OrderLine_ID";

	/** Set Sales Order Line.
	  * Sales Order Line
	  */
	public void setC_OrderLine_ID (int C_OrderLine_ID);

	/** Get Sales Order Line.
	  * Sales Order Line
	  */
	public int getC_OrderLine_ID();

	public org.compiere.model.I_C_OrderLine getC_OrderLine() throws RuntimeException;

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

    /** Column name IsSOTrx */
    public static final String COLUMNNAME_IsSOTrx = "IsSOTrx";

	/** Set Sales Transaction.
	  * This is a Sales Transaction
	  */
	public void setIsSOTrx (boolean IsSOTrx);

	/** Get Sales Transaction.
	  * This is a Sales Transaction
	  */
	public boolean isSOTrx();

    /** Column name LabelUUID */
    public static final String COLUMNNAME_LabelUUID = "LabelUUID";

	/** Set LabelUUID	  */
	public void setLabelUUID (String LabelUUID);

	/** Get LabelUUID	  */
	public String getLabelUUID();

    /** Column name M_InOutLine_ID */
    public static final String COLUMNNAME_M_InOutLine_ID = "M_InOutLine_ID";

	/** Set Shipment/Receipt Line.
	  * Line on Shipment or Receipt document
	  */
	public void setM_InOutLine_ID (int M_InOutLine_ID);

	/** Get Shipment/Receipt Line.
	  * Line on Shipment or Receipt document
	  */
	public int getM_InOutLine_ID();

	public org.compiere.model.I_M_InOutLine getM_InOutLine() throws RuntimeException;

    /** Column name M_Locator_ID */
    public static final String COLUMNNAME_M_Locator_ID = "M_Locator_ID";

	/** Set Locator.
	  * Warehouse Locator
	  */
	public void setM_Locator_ID (int M_Locator_ID);

	/** Get Locator.
	  * Warehouse Locator
	  */
	public int getM_Locator_ID();

	public I_M_Locator getM_Locator() throws RuntimeException;

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

    /** Column name parentlabel */
    public static final String COLUMNNAME_parentlabel = "parentlabel";

	/** Set parentlabel	  */
	public void setparentlabel (int parentlabel);

	/** Get parentlabel	  */
	public int getparentlabel();

	public I_pi_productLabel getparentla() throws RuntimeException;

    /** Column name pi_labeltype_ID */
    public static final String COLUMNNAME_pi_labeltype_ID = "pi_labeltype_ID";

	/** Set pi_labeltype_ID	  */
	public void setpi_labeltype_ID (int pi_labeltype_ID);

	/** Get pi_labeltype_ID	  */
	public int getpi_labeltype_ID();

	public I_pi_labeltype getpi_labeltype() throws RuntimeException;

    /** Column name pi_productLabel_ID */
    public static final String COLUMNNAME_pi_productLabel_ID = "pi_productLabel_ID";

	/** Set pi_productLabel	  */
	public void setpi_productLabel_ID (int pi_productLabel_ID);

	/** Get pi_productLabel	  */
	public int getpi_productLabel_ID();

    /** Column name QcPassed */
    public static final String COLUMNNAME_QcPassed = "QcPassed";

	/** Set qcpassed	  */
    
	public void setQcpassed (boolean qcpassed);

	/** Get qcpassed	  */
	
	public boolean qcpassed();

    /** Column name Quantity */
    public static final String COLUMNNAME_Quantity = "Quantity";

	/** Set Quantity	  */
	public void setQuantity (BigDecimal Quantity);

	/** Get Quantity	  */
	public BigDecimal getQuantity();

    /** Column name repacked */
    public static final String COLUMNNAME_repacked = "repacked";

	/** Set repacked	  */
	public void setrepacked (boolean repacked);

	/** Get repacked	  */
	public boolean isrepacked();

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
	
	/** Column name ismanufacturing */
    public static final String COLUMNNAME_ismanufacturing = "ismanufacturing";

	/** Set ismanufacturing	  */
	public void setismanufacturing (boolean ismanufacturing);

	/** Get ismanufacturing	  */
	public boolean ismanufacturing();
	
	/** Column name PP_Order_ID */
    public static final String COLUMNNAME_PP_Order_ID = "PP_Order_ID";

	/** Set Manufacturing Order.
	  * Manufacturing Order
	  */
	public void setPP_Order_ID (int PP_Order_ID);

	/** Get Manufacturing Order.
	  * Manufacturing Order
	  */
	public int getPP_Order_ID();
	
	/** Column name PP_Order_ID */
    public static final String COLUMNNAME_pi_paorder_ID = "pi_paorder_ID";

	/** Set Manufacturing Order.
	  * Manufacturing Order
	  */
	public void setPi_paorder_ID (int pi_paorder_ID);

	/** Get Manufacturing Order.
	  * Manufacturing Order
	  */
	public int getPi_paorder_ID();

	public org.eevolution.model.I_PP_Order getPP_Order() throws RuntimeException;
	
	/** Column name PP_Order_ID */
    public static final String COLUMNNAME_pi_orderreceipt_ID = "pi_orderreceipt_ID";

	/** Set Manufacturing Order.
	  * Manufacturing Order
	  */
	public void setpi_orderreceipt_ID (int pi_orderreceipt_ID);

	/** Get Manufacturing Order.
	  * Manufacturing Order
	  */
	public int getPi_orderreceipt_ID();
	
	public X_pi_orderreceipt getpi_orderreceipt() throws RuntimeException;
	
	/** Column name finaldispatch */
    public static final String COLUMNNAME_finaldispatch = "finaldispatch";

	/** Set finaldispatch	  */
	public void setfinaldispatch (boolean finaldispatch);

	/** Get finaldispatch	  */
	public boolean isfinaldispatch();
	
	/** Column name reason */
    public static final String COLUMNNAME_reason = "reason";

	/** Set reason	  */
	public void setreason (String reason);

	/** Get reason	  */
	public String getreason();
	
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

}
