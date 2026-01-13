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

import java.sql.ResultSet;
import java.util.Properties;
import org.compiere.model.*;

/** Generated Model for pi_labeltype
 *  @author iDempiere (generated) 
 *  @version Release 10 - $Id$ */
@org.adempiere.base.Model(table="pi_labeltype")
public class X_pi_labeltype extends PO implements I_pi_labeltype, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20240807L;

    /** Standard Constructor */
    public X_pi_labeltype (Properties ctx, int pi_labeltype_ID, String trxName)
    {
      super (ctx, pi_labeltype_ID, trxName);
      /** if (pi_labeltype_ID == 0)
        {
			setpi_labeltype_ID (0);
        } */
    }

    /** Standard Constructor */
    public X_pi_labeltype (Properties ctx, int pi_labeltype_ID, String trxName, String ... virtualColumns)
    {
      super (ctx, pi_labeltype_ID, trxName, virtualColumns);
      /** if (pi_labeltype_ID == 0)
        {
			setpi_labeltype_ID (0);
        } */
    }

    /** Load Constructor */
    public X_pi_labeltype (Properties ctx, ResultSet rs, String trxName)
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
      StringBuilder sb = new StringBuilder ("X_pi_labeltype[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	/** Set labeltype.
		@param labeltype labeltype
	*/
	public void setlabeltype (String labeltype)
	{
		set_Value (COLUMNNAME_labeltype, labeltype);
	}

	/** Get labeltype.
		@return labeltype	  */
	public String getlabeltype()
	{
		return (String)get_Value(COLUMNNAME_labeltype);
	}

	public I_pi_labeltype getpi_labeltype() throws RuntimeException
	{
		return (I_pi_labeltype)MTable.get(getCtx(), I_pi_labeltype.Table_ID)
			.getPO(getpi_labeltype_ID(), get_TrxName());
	}

	/** Set pi_labeltype_ID.
		@param pi_labeltype_ID pi_labeltype_ID
	*/
	public void setpi_labeltype_ID (int pi_labeltype_ID)
	{
		if (pi_labeltype_ID < 1)
			set_Value (COLUMNNAME_pi_labeltype_ID, null);
		else
			set_Value (COLUMNNAME_pi_labeltype_ID, Integer.valueOf(pi_labeltype_ID));
	}

	/** Get pi_labeltype_ID.
		@return pi_labeltype_ID	  */
	public int getpi_labeltype_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_pi_labeltype_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}
}