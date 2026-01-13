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

/** Generated Model for pi_paorderreceiveqty
 *  @author iDempiere (generated) 
 *  @version Release 10 - $Id$ */
@org.adempiere.base.Model(table="pi_paorderreceiveqty")
public class X_pi_paorderreceiveqty extends PO implements I_pi_paorderreceiveqty, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20250623L;

    /** Standard Constructor */
    public X_pi_paorderreceiveqty (Properties ctx, int pi_paorderreceiveqty_ID, String trxName)
    {
      super (ctx, pi_paorderreceiveqty_ID, trxName);
      /** if (pi_paorderreceiveqty_ID == 0)
        {
			setpi_paorderreceiveqty_ID (0);
        } */
    }

    /** Standard Constructor */
    public X_pi_paorderreceiveqty (Properties ctx, int pi_paorderreceiveqty_ID, String trxName, String ... virtualColumns)
    {
      super (ctx, pi_paorderreceiveqty_ID, trxName, virtualColumns);
      /** if (pi_paorderreceiveqty_ID == 0)
        {
			setpi_paorderreceiveqty_ID (0);
        } */
    }

    /** Load Constructor */
    public X_pi_paorderreceiveqty (Properties ctx, ResultSet rs, String trxName)
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
      StringBuilder sb = new StringBuilder ("X_pi_paorderreceiveqty[")
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

	/** Set FG Received Qty.
		@param fgreceivedqty FG Received Qty
	*/
	public void setfgreceivedqty (BigDecimal fgreceivedqty)
	{
		set_Value (COLUMNNAME_fgreceivedqty, fgreceivedqty);
	}

	/** Get FG Received Qty.
		@return FG Received Qty	  */
	public BigDecimal getfgreceivedqty()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_fgreceivedqty);
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

	/** Set pi_paorderreceiveqty.
		@param pi_paorderreceiveqty_ID pi_paorderreceiveqty
	*/
	public void setpi_paorderreceiveqty_ID (int pi_paorderreceiveqty_ID)
	{
		if (pi_paorderreceiveqty_ID < 1)
			set_ValueNoCheck (COLUMNNAME_pi_paorderreceiveqty_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_pi_paorderreceiveqty_ID, Integer.valueOf(pi_paorderreceiveqty_ID));
	}

	/** Get pi_paorderreceiveqty.
		@return pi_paorderreceiveqty	  */
	public int getpi_paorderreceiveqty_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_pi_paorderreceiveqty_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set pi_paorderreceiveqty_UU.
		@param pi_paorderreceiveqty_UU pi_paorderreceiveqty_UU
	*/
	public void setpi_paorderreceiveqty_UU (String pi_paorderreceiveqty_UU)
	{
		set_ValueNoCheck (COLUMNNAME_pi_paorderreceiveqty_UU, pi_paorderreceiveqty_UU);
	}

	/** Get pi_paorderreceiveqty_UU.
		@return pi_paorderreceiveqty_UU	  */
	public String getpi_paorderreceiveqty_UU()
	{
		return (String)get_Value(COLUMNNAME_pi_paorderreceiveqty_UU);
	}
}