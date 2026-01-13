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
package org.realmeds.tissue.moduller;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Properties;
import org.compiere.model.*;
import org.compiere.util.Env;
import org.compiere.util.KeyNamePair;

/** Generated Model for TC_out
 *  @author iDempiere (generated) 
 *  @version Release 10 - $Id$ */
@org.adempiere.base.Model(table="TC_out")
public class X_TC_out extends PO implements I_TC_out, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20240304L;

	/** Standard Constructor */
	public X_TC_out (Properties ctx, int TC_out_ID, String trxName)
	{
		super (ctx, TC_out_ID, trxName);
		/** if (TC_out_ID == 0)
        {
			setM_Locator_ID (0);
// @M_Locator_ID@
			setM_Product_ID (0);
			setQuantity (Env.ZERO);
// 1
			setTC_order_ID (0);
			setTC_out_ID (0);
        } */
	}

	/** Standard Constructor */
	public X_TC_out (Properties ctx, int TC_out_ID, String trxName, String ... virtualColumns)
	{
		super (ctx, TC_out_ID, trxName, virtualColumns);
		/** if (TC_out_ID == 0)
        {
			setM_Locator_ID (0);
// @M_Locator_ID@
			setM_Product_ID (0);
			setQuantity (Env.ZERO);
// 1
			setTC_order_ID (0);
			setTC_out_ID (0);
        } */
	}

	/** Load Constructor */
	public X_TC_out (Properties ctx, ResultSet rs, String trxName)
	{
		super (ctx, rs, trxName);
	}

	/** AccessLevel
	 * @return 3 - Client - Org 
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
	
	/**
	 * Set c_uuid.
	 * 
	 * @param c_uuid c_uuid
	 */
	public void setc_uuid(String c_uuid) {
		set_Value(COLUMNNAME_c_uuid, c_uuid);
	}

	/**
	 * Get c_uuid.
	 * 
	 * @return c_uuid
	 */
	public String getc_uuid() {
		return (String) get_Value(COLUMNNAME_c_uuid);
	}

	public String toString()
	{
		StringBuilder sb = new StringBuilder ("X_TC_out[")
				.append(get_ID()).append("]");
		return sb.toString();
	}

	public org.compiere.model.I_C_UOM getC_UOM() throws RuntimeException
	{
		return (org.compiere.model.I_C_UOM)MTable.get(getCtx(), org.compiere.model.I_C_UOM.Table_ID)
				.getPO(getC_UOM_ID(), get_TrxName());
	}

	/** Set UOM.
		@param C_UOM_ID Unit of Measure
	 */
	public void setC_UOM_ID (int C_UOM_ID)
	{
		if (C_UOM_ID < 1)
			set_Value (COLUMNNAME_C_UOM_ID, null);
		else
			set_Value (COLUMNNAME_C_UOM_ID, Integer.valueOf(C_UOM_ID));
	}

	/** Get UOM.
		@return Unit of Measure
	 */
	public int getC_UOM_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_UOM_ID);
		if (ii == null)
			return 0;
		return ii.intValue();
	}

	/** Set Discarded Qty.
	@param discardqty Discarded Qty
	 */
	public void setdiscardqty (BigDecimal discardqty)
	{
		set_Value (COLUMNNAME_discardqty, discardqty);
	}

	/** Get Discarded Qty.
	@return Discarded Qty
	 */
	public BigDecimal getdiscardqty()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_discardqty);
		if (bd == null)
			return Env.ZERO;
		return bd;
	}

	/** Set cycle.
		@param cycle cycle
	 */
	public void setcycle (int cycle)
	{
		set_Value (COLUMNNAME_cycle, Integer.valueOf(cycle));
	}

	/** Get cycle.
		@return cycle	  */
	public int getcycle()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_cycle);
		if (ii == null)
			return 0;
		return ii.intValue();
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
			set_Value (COLUMNNAME_M_Locator_ID, null);
		else
			set_Value (COLUMNNAME_M_Locator_ID, Integer.valueOf(M_Locator_ID));
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

	/** Set Quantity.
		@param Quantity Quantity
	 */
	public void setQuantity (BigDecimal Quantity)
	{
		set_Value (COLUMNNAME_Quantity, Quantity);
	}

	/** Get Quantity.
		@return Quantity	  */
	public BigDecimal getQuantity()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_Quantity);
		if (bd == null)
			return Env.ZERO;
		return bd;
	}

	public org.realmeds.tissue.moduller.I_TC_in getTC_in() throws RuntimeException
	{
		return (org.realmeds.tissue.moduller.I_TC_in)MTable.get(getCtx(), org.realmeds.tissue.moduller.I_TC_in.Table_ID)
				.getPO(getTC_in_ID(), get_TrxName());
	}

	/** Set TC_in.
		@param TC_in_ID TC_in
	 */
	public void setTC_in_ID (int TC_in_ID)
	{
		if (TC_in_ID < 1)
			set_ValueNoCheck (COLUMNNAME_TC_in_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_TC_in_ID, Integer.valueOf(TC_in_ID));
	}

	/** Get TC_in.
		@return TC_in	  */
	public int getTC_in_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_TC_in_ID);
		if (ii == null)
			return 0;
		return ii.intValue();
	}

	public org.realmeds.tissue.moduller.I_TC_order getTC_order() throws RuntimeException
	{
		return (org.realmeds.tissue.moduller.I_TC_order)MTable.get(getCtx(), org.realmeds.tissue.moduller.I_TC_order.Table_ID)
				.getPO(getTC_order_ID(), get_TrxName());
	}

	/** Set TC_order.
		@param TC_order_ID TC_order
	 */
	public void setTC_order_ID (int TC_order_ID)
	{
		if (TC_order_ID < 1)
			set_ValueNoCheck (COLUMNNAME_TC_order_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_TC_order_ID, Integer.valueOf(TC_order_ID));
	}

	/** Get TC_order.
		@return TC_order	  */
	public int getTC_order_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_TC_order_ID);
		if (ii == null)
			return 0;
		return ii.intValue();
	}

	/** Get Record ID/ColumnName
        @return ID/ColumnName pair
	 */
	public KeyNamePair getKeyNamePair() 
	{
		return new KeyNamePair(get_ID(), String.valueOf(getTC_order_ID()));
	}

	/** Set TC_out.
		@param TC_out_ID TC_out
	 */
	public void setTC_out_ID (int TC_out_ID)
	{
		if (TC_out_ID < 1)
			set_ValueNoCheck (COLUMNNAME_TC_out_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_TC_out_ID, Integer.valueOf(TC_out_ID));
	}

	/** Get TC_out.
		@return TC_out	  */
	public int getTC_out_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_TC_out_ID);
		if (ii == null)
			return 0;
		return ii.intValue();
	}

	/** Set TC_out_UU.
		@param TC_out_UU TC_out_UU
	 */
	public void setTC_out_UU (String TC_out_UU)
	{
		set_ValueNoCheck (COLUMNNAME_TC_out_UU, TC_out_UU);
	}

	/** Get TC_out_UU.
		@return TC_out_UU	  */
	public String getTC_out_UU()
	{
		return (String)get_Value(COLUMNNAME_TC_out_UU);
	}
}