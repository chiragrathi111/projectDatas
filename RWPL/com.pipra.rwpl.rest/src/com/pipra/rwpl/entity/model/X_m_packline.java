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
package com.pipra.rwpl.entity.model;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Properties;
import org.compiere.model.*;
import org.compiere.util.Env;
import org.compiere.util.KeyNamePair;

/** Generated Model for m_packline
 *  @author iDempiere (generated) 
 *  @version Release 10 - $Id$ */
@org.adempiere.base.Model(table="m_packline")
public class X_m_packline extends PO implements I_m_packline, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20250424L;

    /** Standard Constructor */
    public X_m_packline (Properties ctx, int m_packline_ID, String trxName)
    {
      super (ctx, m_packline_ID, trxName);
      /** if (m_packline_ID == 0)
        {
			setM_InOutLine_ID (0);
			setm_packline_ID (0);
        } */
    }

    /** Standard Constructor */
    public X_m_packline (Properties ctx, int m_packline_ID, String trxName, String ... virtualColumns)
    {
      super (ctx, m_packline_ID, trxName, virtualColumns);
      /** if (m_packline_ID == 0)
        {
			setM_InOutLine_ID (0);
			setm_packline_ID (0);
        } */
    }

    /** Load Constructor */
    public X_m_packline (Properties ctx, ResultSet rs, String trxName)
    {
      super (ctx, rs, trxName);
    }

    /** AccessLevel
      * @return 1 - Org 
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
      StringBuilder sb = new StringBuilder ("X_m_packline[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	/** Set Label.
		@param label Label
	*/
	public void setlabel (String label)
	{
		set_Value (COLUMNNAME_label, label);
	}

	/** Get Label.
		@return Label	  */
	public String getlabel()
	{
		return (String)get_Value(COLUMNNAME_label);
	}

	public org.compiere.model.I_M_InOutLine getM_InOutLine() throws RuntimeException
	{
		return (org.compiere.model.I_M_InOutLine)MTable.get(getCtx(), org.compiere.model.I_M_InOutLine.Table_ID)
			.getPO(getM_InOutLine_ID(), get_TrxName());
	}

	/** Set Shipment/Receipt Line.
		@param M_InOutLine_ID Line on Shipment or Receipt document
	*/
	public void setM_InOutLine_ID (int M_InOutLine_ID)
	{
		if (M_InOutLine_ID < 1)
			set_ValueNoCheck (COLUMNNAME_M_InOutLine_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_M_InOutLine_ID, Integer.valueOf(M_InOutLine_ID));
	}

	/** Get Shipment/Receipt Line.
		@return Line on Shipment or Receipt document
	  */
	public int getM_InOutLine_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_InOutLine_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

    /** Get Record ID/ColumnName
        @return ID/ColumnName pair
      */
    public KeyNamePair getKeyNamePair() 
    {
        return new KeyNamePair(get_ID(), String.valueOf(getM_InOutLine_ID()));
    }

	/** Set Pack Line.
		@param m_packline_ID Pack Line
	*/
	public void setm_packline_ID (int m_packline_ID)
	{
		if (m_packline_ID < 1)
			set_ValueNoCheck (COLUMNNAME_m_packline_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_m_packline_ID, Integer.valueOf(m_packline_ID));
	}

	/** Get Pack Line.
		@return Pack Line	  */
	public int getm_packline_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_m_packline_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set m_packline_UU.
		@param m_packline_UU m_packline_UU
	*/
	public void setm_packline_UU (String m_packline_UU)
	{
		set_ValueNoCheck (COLUMNNAME_m_packline_UU, m_packline_UU);
	}

	/** Get m_packline_UU.
		@return m_packline_UU	  */
	public String getm_packline_UU()
	{
		return (String)get_Value(COLUMNNAME_m_packline_UU);
	}

	/** Set Quantity.
		@param quantity Quantity
	*/
	public void setquantity (BigDecimal quantity)
	{
		set_Value (COLUMNNAME_quantity, quantity);
	}

	/** Get Quantity.
		@return Quantity	  */
	public BigDecimal getquantity()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_quantity);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}
}