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
import java.util.Properties;
import org.compiere.model.*;
import org.compiere.util.Env;

/** Generated Model for pi_paorderpackingqty
 *  @author iDempiere (generated) 
 *  @version Release 10 - $Id$ */
@org.adempiere.base.Model(table="pi_paorderpackingqty")
public class X_pi_paorderpackingqty extends PO implements I_pi_paorderpackingqty, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20250623L;

    /** Standard Constructor */
    public X_pi_paorderpackingqty (Properties ctx, int pi_paorderpackingqty_ID, String trxName)
    {
      super (ctx, pi_paorderpackingqty_ID, trxName);
      /** if (pi_paorderpackingqty_ID == 0)
        {
			setpi_paorderpackingqty_ID (0);
			setreceived (false);
        } */
    }

    /** Standard Constructor */
    public X_pi_paorderpackingqty (Properties ctx, int pi_paorderpackingqty_ID, String trxName, String ... virtualColumns)
    {
      super (ctx, pi_paorderpackingqty_ID, trxName, virtualColumns);
      /** if (pi_paorderpackingqty_ID == 0)
        {
			setpi_paorderpackingqty_ID (0);
			setreceived (false);
        } */
    }

    /** Load Constructor */
    public X_pi_paorderpackingqty (Properties ctx, ResultSet rs, String trxName)
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
      StringBuilder sb = new StringBuilder ("X_pi_paorderpackingqty[")
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

	/** Set Packing Qty.
		@param packingqty Packing Qty
	*/
	public void setpackingqty (BigDecimal packingqty)
	{
		set_Value (COLUMNNAME_packingqty, packingqty);
	}

	/** Get Packing Qty.
		@return Packing Qty	  */
	public BigDecimal getpackingqty()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_packingqty);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	public com.pipra.ve.model.I_pi_paorder getpi_paorder() throws RuntimeException
	{
		return (com.pipra.ve.model.I_pi_paorder)MTable.get(getCtx(), com.pipra.ve.model.I_pi_paorder.Table_ID)
			.getPO(getpi_paorder_ID(), get_TrxName());
	}

	/** Set packing &amp; Assembly order.
		@param pi_paorder_ID packing &amp; Assembly order
	*/
	public void setpi_paorder_ID (int pi_paorder_ID)
	{
		if (pi_paorder_ID < 1)
			set_ValueNoCheck (COLUMNNAME_pi_paorder_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_pi_paorder_ID, Integer.valueOf(pi_paorder_ID));
	}

	/** Get packing &amp; Assembly order.
		@return packing &amp; Assembly order	  */
	public int getpi_paorder_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_pi_paorder_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set pi_paorderpackingqty.
		@param pi_paorderpackingqty_ID pi_paorderpackingqty
	*/
	public void setpi_paorderpackingqty_ID (int pi_paorderpackingqty_ID)
	{
		if (pi_paorderpackingqty_ID < 1)
			set_ValueNoCheck (COLUMNNAME_pi_paorderpackingqty_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_pi_paorderpackingqty_ID, Integer.valueOf(pi_paorderpackingqty_ID));
	}

	/** Get pi_paorderpackingqty.
		@return pi_paorderpackingqty	  */
	public int getpi_paorderpackingqty_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_pi_paorderpackingqty_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set pi_paorderpackingqty_UU.
		@param pi_paorderpackingqty_UU pi_paorderpackingqty_UU
	*/
	public void setpi_paorderpackingqty_UU (String pi_paorderpackingqty_UU)
	{
		set_ValueNoCheck (COLUMNNAME_pi_paorderpackingqty_UU, pi_paorderpackingqty_UU);
	}

	/** Get pi_paorderpackingqty_UU.
		@return pi_paorderpackingqty_UU	  */
	public String getpi_paorderpackingqty_UU()
	{
		return (String)get_Value(COLUMNNAME_pi_paorderpackingqty_UU);
	}

	/** Set Received.
		@param received Received
	*/
	public void setreceived (boolean received)
	{
		set_Value (COLUMNNAME_received, Boolean.valueOf(received));
	}

	/** Get Received.
		@return Received	  */
	public boolean isreceived()
	{
		Object oo = get_Value(COLUMNNAME_received);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Status Line.
		@param statusline Status Line
	*/
	public void setstatusline (String statusline)
	{
		set_Value (COLUMNNAME_statusline, statusline);
	}

	/** Get Status Line.
		@return Status Line	  */
	public String getstatusline()
	{
		return (String)get_Value(COLUMNNAME_statusline);
	}
}