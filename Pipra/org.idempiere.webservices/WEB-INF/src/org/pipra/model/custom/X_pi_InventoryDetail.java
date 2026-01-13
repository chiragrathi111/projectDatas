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

/** Generated Model for pi_InventoryDetail
 *  @author iDempiere (generated) 
 *  @version Release 10 - $Id$ */
@org.adempiere.base.Model(table="pi_InventoryDetail")
public class X_pi_InventoryDetail extends PO implements I_pi_InventoryDetail, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20251212L;

    /** Standard Constructor */
    public X_pi_InventoryDetail (Properties ctx, int pi_InventoryDetail_ID, String trxName)
    {
      super (ctx, pi_InventoryDetail_ID, trxName);
      /** if (pi_InventoryDetail_ID == 0)
        {
			setpi_InventoryDetail_ID (0);
        } */
    }

    /** Standard Constructor */
    public X_pi_InventoryDetail (Properties ctx, int pi_InventoryDetail_ID, String trxName, String ... virtualColumns)
    {
      super (ctx, pi_InventoryDetail_ID, trxName, virtualColumns);
      /** if (pi_InventoryDetail_ID == 0)
        {
			setpi_InventoryDetail_ID (0);
        } */
    }

    /** Load Constructor */
    public X_pi_InventoryDetail (Properties ctx, ResultSet rs, String trxName)
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
      StringBuilder sb = new StringBuilder ("X_pi_InventoryDetail[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	/** Set LabelUUID.
		@param LabelUUID LabelUUID
	*/
	public void setLabelUUID (String LabelUUID)
	{
		set_ValueNoCheck (COLUMNNAME_LabelUUID, LabelUUID);
	}

	/** Get LabelUUID.
		@return LabelUUID	  */
	public String getLabelUUID()
	{
		return (String)get_Value(COLUMNNAME_LabelUUID);
	}

	/** Set pi_InventoryDetail.
		@param pi_InventoryDetail_ID pi_InventoryDetail
	*/
	public void setpi_InventoryDetail_ID (int pi_InventoryDetail_ID)
	{
		if (pi_InventoryDetail_ID < 1)
			set_ValueNoCheck (COLUMNNAME_pi_InventoryDetail_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_pi_InventoryDetail_ID, Integer.valueOf(pi_InventoryDetail_ID));
	}

	/** Get pi_InventoryDetail.
		@return pi_InventoryDetail	  */
	public int getpi_InventoryDetail_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_pi_InventoryDetail_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set pi_InventoryDetail_UU.
		@param pi_InventoryDetail_UU pi_InventoryDetail_UU
	*/
	public void setpi_InventoryDetail_UU (String pi_InventoryDetail_UU)
	{
		set_ValueNoCheck (COLUMNNAME_pi_InventoryDetail_UU, pi_InventoryDetail_UU);
	}

	/** Get pi_InventoryDetail_UU.
		@return pi_InventoryDetail_UU	  */
	public String getpi_InventoryDetail_UU()
	{
		return (String)get_Value(COLUMNNAME_pi_InventoryDetail_UU);
	}

	public I_pi_InventoryLine getpi_InventoryLine() throws RuntimeException
	{
		return (I_pi_InventoryLine)MTable.get(getCtx(), I_pi_InventoryLine.Table_ID)
			.getPO(getpi_InventoryLine_ID(), get_TrxName());
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

	public I_pi_productLabel getpi_productLabel() throws RuntimeException
	{
		return (I_pi_productLabel)MTable.get(getCtx(), I_pi_productLabel.Table_ID)
			.getPO(getpi_productLabel_ID(), get_TrxName());
	}

	/** Set pi_productLabel.
		@param pi_productLabel_ID pi_productLabel
	*/
	public void setpi_productLabel_ID (int pi_productLabel_ID)
	{
		if (pi_productLabel_ID < 1)
			set_ValueNoCheck (COLUMNNAME_pi_productLabel_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_pi_productLabel_ID, Integer.valueOf(pi_productLabel_ID));
	}

	/** Get pi_productLabel.
		@return pi_productLabel	  */
	public int getpi_productLabel_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_pi_productLabel_ID);
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