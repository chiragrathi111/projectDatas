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

/** Generated Model for pi_returnline
 *  @author iDempiere (generated) 
 *  @version Release 10 - $Id$ */
@org.adempiere.base.Model(table="pi_returnline")
public class X_pi_returnline extends PO implements I_pi_returnline, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20250722L;

    /** Standard Constructor */
    public X_pi_returnline (Properties ctx, int pi_returnline_ID, String trxName)
    {
      super (ctx, pi_returnline_ID, trxName);
      /** if (pi_returnline_ID == 0)
        {
			setpi_return_ID (0);
			setpi_returnline_ID (0);
        } */
    }

    /** Standard Constructor */
    public X_pi_returnline (Properties ctx, int pi_returnline_ID, String trxName, String ... virtualColumns)
    {
      super (ctx, pi_returnline_ID, trxName, virtualColumns);
      /** if (pi_returnline_ID == 0)
        {
			setpi_return_ID (0);
			setpi_returnline_ID (0);
        } */
    }

    /** Load Constructor */
    public X_pi_returnline (Properties ctx, ResultSet rs, String trxName)
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
      StringBuilder sb = new StringBuilder ("X_pi_returnline[")
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

	public com.pipra.ve.model.I_pi_return getpi_return() throws RuntimeException
	{
		return (com.pipra.ve.model.I_pi_return)MTable.get(getCtx(), com.pipra.ve.model.I_pi_return.Table_ID)
			.getPO(getpi_return_ID(), get_TrxName());
	}

	/** Set Return.
		@param pi_return_ID Return
	*/
	public void setpi_return_ID (int pi_return_ID)
	{
		if (pi_return_ID < 1)
			set_ValueNoCheck (COLUMNNAME_pi_return_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_pi_return_ID, Integer.valueOf(pi_return_ID));
	}

	/** Get Return.
		@return Return	  */
	public int getpi_return_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_pi_return_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Return Line.
		@param pi_returnline_ID Return Line
	*/
	public void setpi_returnline_ID (int pi_returnline_ID)
	{
		if (pi_returnline_ID < 1)
			set_ValueNoCheck (COLUMNNAME_pi_returnline_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_pi_returnline_ID, Integer.valueOf(pi_returnline_ID));
	}

	/** Get Return Line.
		@return Return Line	  */
	public int getpi_returnline_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_pi_returnline_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set pi_returnline_UU.
		@param pi_returnline_UU pi_returnline_UU
	*/
	public void setpi_returnline_UU (String pi_returnline_UU)
	{
		set_ValueNoCheck (COLUMNNAME_pi_returnline_UU, pi_returnline_UU);
	}

	/** Get pi_returnline_UU.
		@return pi_returnline_UU	  */
	public String getpi_returnline_UU()
	{
		return (String)get_Value(COLUMNNAME_pi_returnline_UU);
	}

	/** Set qtyissued.
		@param qtyissued qtyissued
	*/
	public void setqtyissued (BigDecimal qtyissued)
	{
		set_Value (COLUMNNAME_qtyissued, qtyissued);
	}

	/** Get qtyissued.
		@return qtyissued	  */
	public BigDecimal getqtyissued()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_qtyissued);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set qtytotal.
		@param qtytotal qtytotal
	*/
	public void setqtytotal (BigDecimal qtytotal)
	{
		set_Value (COLUMNNAME_qtytotal, qtytotal);
	}

	/** Get qtytotal.
		@return qtytotal	  */
	public BigDecimal getqtytotal()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_qtytotal);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}
}