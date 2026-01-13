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
package com.pipra.rwpl.model;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Properties;
import org.compiere.model.*;
import org.compiere.util.Env;

/** Generated Model for pi_planitem
 *  @author iDempiere (generated) 
 *  @version Release 10 - $Id$ */
@org.adempiere.base.Model(table="pi_planitem")
public class X_pi_planitem extends PO implements I_pi_planitem, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20250427L;

    /** Standard Constructor */
    public X_pi_planitem (Properties ctx, int pi_planitem_ID, String trxName)
    {
      super (ctx, pi_planitem_ID, trxName);
      /** if (pi_planitem_ID == 0)
        {
			setpi_planitem_ID (0);
        } */
    }

    /** Standard Constructor */
    public X_pi_planitem (Properties ctx, int pi_planitem_ID, String trxName, String ... virtualColumns)
    {
      super (ctx, pi_planitem_ID, trxName, virtualColumns);
      /** if (pi_planitem_ID == 0)
        {
			setpi_planitem_ID (0);
        } */
    }

    /** Load Constructor */
    public X_pi_planitem (Properties ctx, ResultSet rs, String trxName)
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
      StringBuilder sb = new StringBuilder ("X_pi_planitem[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	/** Set Completed Quantity.
		@param completedqnty Completed Quantity
	*/
	public void setcompletedqnty (BigDecimal completedqnty)
	{
		set_Value (COLUMNNAME_completedqnty, completedqnty);
	}

	/** Get Completed Quantity.
		@return Completed Quantity	  */
	public BigDecimal getcompletedqnty()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_completedqnty);
		if (bd == null)
			 return Env.ZERO;
		return bd;
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

	/** Set Plan Item.
		@param pi_planitem_ID Plan Item
	*/
	public void setpi_planitem_ID (int pi_planitem_ID)
	{
		if (pi_planitem_ID < 1)
			set_ValueNoCheck (COLUMNNAME_pi_planitem_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_pi_planitem_ID, Integer.valueOf(pi_planitem_ID));
	}

	/** Get Plan Item.
		@return Plan Item	  */
	public int getpi_planitem_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_pi_planitem_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set pi_planitem_UU.
		@param pi_planitem_UU pi_planitem_UU
	*/
	public void setpi_planitem_UU (String pi_planitem_UU)
	{
		set_ValueNoCheck (COLUMNNAME_pi_planitem_UU, pi_planitem_UU);
	}

	/** Get pi_planitem_UU.
		@return pi_planitem_UU	  */
	public String getpi_planitem_UU()
	{
		return (String)get_Value(COLUMNNAME_pi_planitem_UU);
	}

	public com.pipra.rwpl.model.I_pi_salesplanline getpi_salesplanline() throws RuntimeException
	{
		return (com.pipra.rwpl.model.I_pi_salesplanline)MTable.get(getCtx(), com.pipra.rwpl.model.I_pi_salesplanline.Table_ID)
			.getPO(getpi_salesplanline_ID(), get_TrxName());
	}

	/** Set Sales Plan Line.
		@param pi_salesplanline_ID Sales Plan Line
	*/
	public void setpi_salesplanline_ID (int pi_salesplanline_ID)
	{
		if (pi_salesplanline_ID < 1)
			set_ValueNoCheck (COLUMNNAME_pi_salesplanline_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_pi_salesplanline_ID, Integer.valueOf(pi_salesplanline_ID));
	}

	/** Get Sales Plan Line.
		@return Sales Plan Line	  */
	public int getpi_salesplanline_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_pi_salesplanline_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Total Quantity.
		@param totalqnty Total Quantity
	*/
	public void settotalqnty (BigDecimal totalqnty)
	{
		set_Value (COLUMNNAME_totalqnty, totalqnty);
	}

	/** Get Total Quantity.
		@return Total Quantity	  */
	public BigDecimal gettotalqnty()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_totalqnty);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}
}