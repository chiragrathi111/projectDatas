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

import java.sql.ResultSet;
import java.util.Properties;
import org.compiere.model.*;

/** Generated Model for pi_import
 *  @author iDempiere (generated) 
 *  @version Release 10 - $Id$ */
@org.adempiere.base.Model(table="pi_import")
public class X_pi_import extends PO implements I_pi_import, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20251007L;

    /** Standard Constructor */
    public X_pi_import (Properties ctx, int pi_import_ID, String trxName)
    {
      super (ctx, pi_import_ID, trxName);
      /** if (pi_import_ID == 0)
        {
			setpi_import_ID (0);
        } */
    }

    /** Standard Constructor */
    public X_pi_import (Properties ctx, int pi_import_ID, String trxName, String ... virtualColumns)
    {
      super (ctx, pi_import_ID, trxName, virtualColumns);
      /** if (pi_import_ID == 0)
        {
			setpi_import_ID (0);
        } */
    }

    /** Load Constructor */
    public X_pi_import (Properties ctx, ResultSet rs, String trxName)
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
      StringBuilder sb = new StringBuilder ("X_pi_import[")
        .append(get_ID()).append("]");
      return sb.toString();
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

	/** Set pi_import_UU.
		@param pi_import_UU pi_import_UU
	*/
	public void setpi_import_UU (String pi_import_UU)
	{
		set_ValueNoCheck (COLUMNNAME_pi_import_UU, pi_import_UU);
	}

	/** Get pi_import_UU.
		@return pi_import_UU	  */
	public String getpi_import_UU()
	{
		return (String)get_Value(COLUMNNAME_pi_import_UU);
	}

	/** Set Process Now.
		@param Processing Process Now
	*/
	public void setProcessing (boolean Processing)
	{
		set_Value (COLUMNNAME_Processing, Boolean.valueOf(Processing));
	}

	/** Get Process Now.
		@return Process Now	  */
	public boolean isProcessing()
	{
		Object oo = get_Value(COLUMNNAME_Processing);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}
	
	/**
	 * Set Status.
	 * 
	 * @param Status Status of the currently running check
	 */
	public void setStatus(String Status) {

		set_Value(COLUMNNAME_Status, Status);
	}

	/**
	 * Get Status.
	 * 
	 * @return Status of the currently running check
	 */
	public String getStatus() {
		return (String) get_Value(COLUMNNAME_Status);
	}
}