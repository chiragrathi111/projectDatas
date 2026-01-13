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

import java.sql.ResultSet;
import java.util.Properties;
import org.compiere.model.*;

/** Generated Model for pi_importline
 *  @author iDempiere (generated) 
 *  @version Release 10 - $Id$ */
@org.adempiere.base.Model(table="pi_importline")
public class X_pi_importline extends PO implements I_pi_importline, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20251007L;

    /** Standard Constructor */
    public X_pi_importline (Properties ctx, int pi_importline_ID, String trxName)
    {
      super (ctx, pi_importline_ID, trxName);
      /** if (pi_importline_ID == 0)
        {
			setpi_importline_ID (0);
        } */
    }

    /** Standard Constructor */
    public X_pi_importline (Properties ctx, int pi_importline_ID, String trxName, String ... virtualColumns)
    {
      super (ctx, pi_importline_ID, trxName, virtualColumns);
      /** if (pi_importline_ID == 0)
        {
			setpi_importline_ID (0);
        } */
    }

    /** Load Constructor */
    public X_pi_importline (Properties ctx, ResultSet rs, String trxName)
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
      StringBuilder sb = new StringBuilder ("X_pi_importline[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	public com.pipra.rwpl.model.I_pi_import getpi_import() throws RuntimeException
	{
		return (com.pipra.rwpl.model.I_pi_import)MTable.get(getCtx(), I_pi_import.Table_ID)
			.getPO(getpi_import_ID(), get_TrxName());
	}

	/** Set Import Storage.
		@param pi_import_ID Import Storage
	*/
	public void setpi_import_ID (int pi_import_ID)
	{
		if (pi_import_ID < 1)
			set_ValueNoCheck (COLUMNNAME_pi_import_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_pi_import_ID, Integer.valueOf(pi_import_ID));
	}

	/** Get Import Storage.
		@return Import Storage	  */
	public int getpi_import_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_pi_import_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Import Storage Line.
		@param pi_importline_ID Import Storage Line
	*/
	public void setpi_importline_ID (int pi_importline_ID)
	{
		if (pi_importline_ID < 1)
			set_ValueNoCheck (COLUMNNAME_pi_importline_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_pi_importline_ID, Integer.valueOf(pi_importline_ID));
	}

	/** Get Import Storage Line.
		@return Import Storage Line	  */
	public int getpi_importline_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_pi_importline_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set pi_importline_UU.
		@param pi_importline_UU pi_importline_UU
	*/
	public void setpi_importline_UU (String pi_importline_UU)
	{
		set_ValueNoCheck (COLUMNNAME_pi_importline_UU, pi_importline_UU);
	}

	/** Get pi_importline_UU.
		@return pi_importline_UU	  */
	public String getpi_importline_UU()
	{
		return (String)get_Value(COLUMNNAME_pi_importline_UU);
	}

	public I_pi_productLabel getpi_productLabel() throws RuntimeException
	{
		return (I_pi_productLabel)MTable.get(getCtx(), I_pi_productLabel.Table_ID)
			.getPO(getpi_productLabel_ID(), get_TrxName());
	}

	/** Set Product Label.
		@param pi_productLabel_ID Product Label
	*/
	public void setpi_productLabel_ID (int pi_productLabel_ID)
	{
		if (pi_productLabel_ID < 1)
			set_ValueNoCheck (COLUMNNAME_pi_productLabel_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_pi_productLabel_ID, Integer.valueOf(pi_productLabel_ID));
	}

	/** Get Product Label.
		@return Product Label	  */
	public int getpi_productLabel_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_pi_productLabel_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}
}