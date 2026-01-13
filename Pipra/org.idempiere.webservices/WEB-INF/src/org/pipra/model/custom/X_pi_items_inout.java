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
import org.compiere.model.*;
import org.compiere.util.Env;

/** Generated Model for pi_items_inout
 *  @author iDempiere (generated) 
 *  @version Release 10 - $Id$ */
@org.adempiere.base.Model(table="pi_items_inout")
public class X_pi_items_inout extends PO implements I_pi_items_inout, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20250917L;

    /** Standard Constructor */
    public X_pi_items_inout (Properties ctx, int pi_items_inout_ID, String trxName)
    {
      super (ctx, pi_items_inout_ID, trxName);
      /** if (pi_items_inout_ID == 0)
        {
			setitem_id (null);
			setpi_items_inout_ID (0);
        } */
    }

    /** Standard Constructor */
    public X_pi_items_inout (Properties ctx, int pi_items_inout_ID, String trxName, String ... virtualColumns)
    {
      super (ctx, pi_items_inout_ID, trxName, virtualColumns);
      /** if (pi_items_inout_ID == 0)
        {
			setitem_id (null);
			setpi_items_inout_ID (0);
        } */
    }

    /** Load Constructor */
    public X_pi_items_inout (Properties ctx, ResultSet rs, String trxName)
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

    public String toString()
    {
      StringBuilder sb = new StringBuilder ("X_pi_items_inout[")
        .append(get_ID()).append("]");
      return sb.toString();
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

	/** Set In Qty.
		@param in_qty In Qty
	*/
	public void setin_qty (BigDecimal in_qty)
	{
		set_Value (COLUMNNAME_in_qty, in_qty);
	}

	/** Get In Qty.
		@return In Qty	  */
	public BigDecimal getin_qty()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_in_qty);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Item Id.
		@param item_id Item Id
	*/
	public void setitem_id (String item_id)
	{
		set_Value (COLUMNNAME_item_id, item_id);
	}

	/** Get Item Id.
		@return Item Id	  */
	public String getitem_id()
	{
		return (String)get_Value(COLUMNNAME_item_id);
	}

	/** Set Out Qty.
		@param out_qty Out Qty
	*/
	public void setout_qty (BigDecimal out_qty)
	{
		set_Value (COLUMNNAME_out_qty, out_qty);
	}

	/** Get Out Qty.
		@return Out Qty	  */
	public BigDecimal getout_qty()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_out_qty);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Items Inout.
		@param pi_items_inout_ID Items Inout
	*/
	public void setpi_items_inout_ID (int pi_items_inout_ID)
	{
		if (pi_items_inout_ID < 1)
			set_ValueNoCheck (COLUMNNAME_pi_items_inout_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_pi_items_inout_ID, Integer.valueOf(pi_items_inout_ID));
	}

	/** Get Items Inout.
		@return Items Inout	  */
	public int getpi_items_inout_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_pi_items_inout_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set pi_items_inout_UU.
		@param pi_items_inout_UU pi_items_inout_UU
	*/
	public void setpi_items_inout_UU (String pi_items_inout_UU)
	{
		set_ValueNoCheck (COLUMNNAME_pi_items_inout_UU, pi_items_inout_UU);
	}

	/** Get pi_items_inout_UU.
		@return pi_items_inout_UU	  */
	public String getpi_items_inout_UU()
	{
		return (String)get_Value(COLUMNNAME_pi_items_inout_UU);
	}

	@Override
	public void setIsProcessed(boolean processed) {
		set_ValueNoCheck (COLUMNNAME_IsProcessed, Boolean.valueOf(processed));
	}

	@Override
	public boolean isProcess() {
		Object oo = get_Value(COLUMNNAME_IsProcessed);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}
}