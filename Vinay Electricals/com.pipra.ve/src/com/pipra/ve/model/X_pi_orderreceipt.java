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
/** Generated Model - DO NOT CHANGE */
package com.pipra.ve.model;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.List;
import java.util.Properties;
import org.compiere.model.*;
import org.compiere.util.Env;

/** Generated Model for pi_orderreceipt
 *  @author iDempiere (generated) 
 *  @version Release 10 - $Id$ */
@org.adempiere.base.Model(table="pi_orderreceipt")
public class X_pi_orderreceipt extends PO implements I_pi_orderreceipt, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20241003L;

    /** Standard Constructor */
    public X_pi_orderreceipt (Properties ctx, int pi_orderreceipt_ID, String trxName)
    {
      super (ctx, pi_orderreceipt_ID, trxName);
      /** if (pi_orderreceipt_ID == 0)
        {
			setdepttransfered (false);
			setM_Product_ID (0);
			setPI_Deptartment_ID (0);
			setpi_orderreceipt_ID (0);
			setPP_Order_ID (0);
			setProcessed (false);
        } */
    }

    /** Standard Constructor */
    public X_pi_orderreceipt (Properties ctx, int pi_orderreceipt_ID, String trxName, String ... virtualColumns)
    {
      super (ctx, pi_orderreceipt_ID, trxName, virtualColumns);
      /** if (pi_orderreceipt_ID == 0)
        {
			setdepttransfered (false);
			setM_Product_ID (0);
			setPI_Deptartment_ID (0);
			setpi_orderreceipt_ID (0);
			setPP_Order_ID (0);
			setProcessed (false);
        } */
    }

    /** Load Constructor */
    public X_pi_orderreceipt (Properties ctx, ResultSet rs, String trxName)
    {
      super (ctx, rs, trxName);
    }

    /** AccessLevel
      * @return 7 - System - Client - Org 
      */
    protected int get_AccessLevel()
    {
      return accessLevel.intValue();
    }

    /** Load Meta Data */
    protected POInfo initPO (Properties ctx)
    {
      POInfo poi = POInfo.getPOInfo (ctx, Table_ID, get_TrxName());
      return poi;
    }

    public String toString()
    {
      StringBuilder sb = new StringBuilder ("X_pi_orderreceipt[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	/** Set depttransfered.
		@param depttransfered depttransfered
	*/
	public void setdepttransfered (boolean depttransfered)
	{
		set_Value (COLUMNNAME_depttransfered, Boolean.valueOf(depttransfered));
	}

	/** Get depttransfered.
		@return depttransfered	  */
	public boolean isdepttransfered()
	{
		Object oo = get_Value(COLUMNNAME_depttransfered);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Description.
		@param Description Optional short description of the record
	*/
	public void setDescription (String Description)
	{
		set_Value (COLUMNNAME_Description, Description);
	}

	/** Get Description.
		@return Optional short description of the record
	  */
	public String getDescription()
	{
		return (String)get_Value(COLUMNNAME_Description);
	}
	
	@Override
	public void setReceiptStatus(String receiptStatus) {
		set_Value(COLUMNNAME_ReceiptStatus, receiptStatus);
		
	}
	
	@Override
	public String getReceiptStatus() {
		return (String)get_Value(COLUMNNAME_ReceiptStatus);
	}

	public org.compiere.model.I_M_Product getM_Product() throws RuntimeException
	{
		return (org.compiere.model.I_M_Product)MTable.get(getCtx(), org.compiere.model.I_M_Product.Table_ID)
			.getPO(getM_Product_ID(), get_TrxName());
	}

	/** Set Product.
		@param M_Product_ID Product, Service, Item
	*/
	public void setM_Product_ID (int M_Product_ID)
	{
		if (M_Product_ID < 1)
			set_Value (COLUMNNAME_M_Product_ID, null);
		else
			set_Value (COLUMNNAME_M_Product_ID, Integer.valueOf(M_Product_ID));
	}

	/** Get Product.
		@return Product, Service, Item
	  */
	public int getM_Product_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_Product_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_PI_Deptartment getPI_Deptartment() throws RuntimeException
	{
		return (I_PI_Deptartment)MTable.get(getCtx(), I_PI_Deptartment.Table_ID)
			.getPO(getPI_Deptartment_ID(), get_TrxName());
	}

	/** Set PI_Department.
		@param PI_Deptartment_ID PI_Department
	*/
	public void setPI_Deptartment_ID (int PI_Deptartment_ID)
	{
		if (PI_Deptartment_ID < 1)
			set_Value (COLUMNNAME_PI_Deptartment_ID, null);
		else
			set_Value (COLUMNNAME_PI_Deptartment_ID, Integer.valueOf(PI_Deptartment_ID));
	}

	/** Get PI_Department.
		@return PI_Department	  */
	public int getPI_Deptartment_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_PI_Deptartment_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set pi_orderreceipt.
		@param pi_orderreceipt_ID pi_orderreceipt
	*/
	public void setpi_orderreceipt_ID (int pi_orderreceipt_ID)
	{
		if (pi_orderreceipt_ID < 1)
			set_ValueNoCheck (COLUMNNAME_pi_orderreceipt_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_pi_orderreceipt_ID, Integer.valueOf(pi_orderreceipt_ID));
	}

	/** Get pi_orderreceipt.
		@return pi_orderreceipt	  */
	public int getpi_orderreceipt_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_pi_orderreceipt_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.eevolution.model.I_PP_Order getPP_Order() throws RuntimeException
	{
		return (org.eevolution.model.I_PP_Order)MTable.get(getCtx(), org.eevolution.model.I_PP_Order.Table_ID)
			.getPO(getPP_Order_ID(), get_TrxName());
	}

	/** Set Manufacturing Order.
		@param PP_Order_ID Manufacturing Order
	*/
	public void setPP_Order_ID (int PP_Order_ID)
	{
		if (PP_Order_ID < 1)
			set_ValueNoCheck (COLUMNNAME_PP_Order_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_PP_Order_ID, Integer.valueOf(PP_Order_ID));
	}

	/** Get Manufacturing Order.
		@return Manufacturing Order
	  */
	public int getPP_Order_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_PP_Order_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Processed.
		@param Processed The document has been processed
	*/
	public void setProcessed (boolean Processed)
	{
		set_Value (COLUMNNAME_Processed, Boolean.valueOf(Processed));
	}

	/** Get Processed.
		@return The document has been processed
	  */
	public boolean isProcessed()
	{
		Object oo = get_Value(COLUMNNAME_Processed);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set quantity.
		@param quantity quantity
	*/
	public void setquantity (BigDecimal quantity)
	{
		set_Value (COLUMNNAME_quantity, quantity);
	}

	/** Get quantity.
		@return quantity	  */
	public BigDecimal getquantity()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_quantity);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}
	
	
	public static List<PO> getOrderReceiptList(int clientId, int orgId, int deptId, Properties ctx, String trxName) {
		List<PO> list  = new Query(ctx, X_pi_orderreceipt.Table_Name, "ad_client_ID =? AND ad_org_ID = ? AND pi_deptartment_ID =? AND processed = 'N'", trxName)
					.setParameters(clientId, orgId, deptId).setOrderBy(X_pi_orderreceipt.COLUMNNAME_pi_orderreceipt_ID + " desc").list();
		return list;
	}
	
	public void setM_Locator_ID (int M_Locator_ID)
	{
		if (M_Locator_ID < 1)
			set_ValueNoCheck (COLUMNNAME_M_Locator_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_M_Locator_ID, Integer.valueOf(M_Locator_ID));
	}

	/** Get Locator.
		@return Warehouse Locator
	  */
	public int getM_Locator_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_Locator_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}
}