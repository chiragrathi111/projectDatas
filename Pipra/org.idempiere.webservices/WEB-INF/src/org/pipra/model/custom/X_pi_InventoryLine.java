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
package org.pipra.model.custom;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Properties;

import org.compiere.model.I_M_Locator;
import org.compiere.model.I_Persistent;
import org.compiere.model.MTable;
import org.compiere.model.PO;
import org.compiere.model.POInfo;
import org.compiere.util.Env;

/** Generated Model for pi_InventoryLine
 *  @author iDempiere (generated) 
 *  @version Release 10 - $Id$ */
@org.adempiere.base.Model(table="pi_InventoryLine")
public class X_pi_InventoryLine extends PO implements I_pi_InventoryLine, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20251212L;

    /** Standard Constructor */
    public X_pi_InventoryLine (Properties ctx, int pi_InventoryLine_ID, String trxName)
    {
      super (ctx, pi_InventoryLine_ID, trxName);
      /** if (pi_InventoryLine_ID == 0)
        {
			setM_Locator_ID (0);
			setM_Product_ID (0);
			setpi_InventoryLine_ID (0);
        } */
    }

    /** Standard Constructor */
    public X_pi_InventoryLine (Properties ctx, int pi_InventoryLine_ID, String trxName, String ... virtualColumns)
    {
      super (ctx, pi_InventoryLine_ID, trxName, virtualColumns);
      /** if (pi_InventoryLine_ID == 0)
        {
			setM_Locator_ID (0);
			setM_Product_ID (0);
			setpi_InventoryLine_ID (0);
        } */
    }

    /** Load Constructor */
    public X_pi_InventoryLine (Properties ctx, ResultSet rs, String trxName)
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
      StringBuilder sb = new StringBuilder ("X_pi_InventoryLine[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	/** Set Line No.
		@param Line Unique line for this document
	*/
	public void setLine (int Line)
	{
		set_ValueNoCheck (COLUMNNAME_Line, Integer.valueOf(Line));
	}

	/** Get Line No.
		@return Unique line for this document
	  */
	public int getLine()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Line);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_M_Locator getM_Locator() throws RuntimeException
	{
		return (I_M_Locator)MTable.get(getCtx(), I_M_Locator.Table_ID)
			.getPO(getM_Locator_ID(), get_TrxName());
	}

	/** Set Locator.
		@param M_Locator_ID Warehouse Locator
	*/
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

	/** Set pi_InventoryLine.
		@param pi_InventoryLine_ID pi_InventoryLine
	*/
	public void setpi_InventoryLine_ID (int pi_InventoryLine_ID)
	{
		if (pi_InventoryLine_ID < 1)
			set_ValueNoCheck (COLUMNNAME_pi_InventoryLine_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_pi_InventoryLine_ID, Integer.valueOf(pi_InventoryLine_ID));
	}

	/** Get pi_InventoryLine.
		@return pi_InventoryLine	  */
	public int getpi_InventoryLine_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_pi_InventoryLine_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set pi_InventoryLine_UU.
		@param pi_InventoryLine_UU pi_InventoryLine_UU
	*/
	public void setpi_InventoryLine_UU (String pi_InventoryLine_UU)
	{
		set_ValueNoCheck (COLUMNNAME_pi_InventoryLine_UU, pi_InventoryLine_UU);
	}

	/** Get pi_InventoryLine_UU.
		@return pi_InventoryLine_UU	  */
	public String getpi_InventoryLine_UU()
	{
		return (String)get_Value(COLUMNNAME_pi_InventoryLine_UU);
	}

	public I_pi_LabelInventory getpi_LabelInventory() throws RuntimeException
	{
		return (I_pi_LabelInventory)MTable.get(getCtx(), I_pi_LabelInventory.Table_ID)
			.getPO(getpi_LabelInventory_ID(), get_TrxName());
	}

	/** Set pi_LabelInventory.
		@param pi_LabelInventory_ID pi_LabelInventory
	*/
	public void setpi_LabelInventory_ID (int pi_LabelInventory_ID)
	{
		if (pi_LabelInventory_ID < 1)
			set_ValueNoCheck (COLUMNNAME_pi_LabelInventory_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_pi_LabelInventory_ID, Integer.valueOf(pi_LabelInventory_ID));
	}

	/** Get pi_LabelInventory.
		@return pi_LabelInventory	  */
	public int getpi_LabelInventory_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_pi_LabelInventory_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Quantity book.
		@param QtyBook Book Quantity
	*/
	public void setQtyBook (BigDecimal QtyBook)
	{
		set_ValueNoCheck (COLUMNNAME_QtyBook, QtyBook);
	}

	/** Get Quantity book.
		@return Book Quantity
	  */
	public BigDecimal getQtyBook()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_QtyBook);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Quantity count.
		@param QtyCount Counted Quantity
	*/
	public void setQtyCount (BigDecimal QtyCount)
	{
		set_Value (COLUMNNAME_QtyCount, QtyCount);
	}

	/** Get Quantity count.
		@return Counted Quantity
	  */
	public BigDecimal getQtyCount()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_QtyCount);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}
}