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

/** Generated Model for pi_return
 *  @author iDempiere (generated) 
 *  @version Release 10 - $Id$ */
@org.adempiere.base.Model(table="pi_return")
public class X_pi_return extends PO implements I_pi_return, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20250722L;

    /** Standard Constructor */
    public X_pi_return (Properties ctx, int pi_return_ID, String trxName)
    {
      super (ctx, pi_return_ID, trxName);
      /** if (pi_return_ID == 0)
        {
			setpi_return_ID (0);
        } */
    }

    /** Standard Constructor */
    public X_pi_return (Properties ctx, int pi_return_ID, String trxName, String ... virtualColumns)
    {
      super (ctx, pi_return_ID, trxName, virtualColumns);
      /** if (pi_return_ID == 0)
        {
			setpi_return_ID (0);
        } */
    }

    /** Load Constructor */
    public X_pi_return (Properties ctx, ResultSet rs, String trxName)
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
      StringBuilder sb = new StringBuilder ("X_pi_return[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	public org.compiere.model.I_C_BPartner getC_BPartner() throws RuntimeException
	{
		return (org.compiere.model.I_C_BPartner)MTable.get(getCtx(), org.compiere.model.I_C_BPartner.Table_ID)
			.getPO(getC_BPartner_ID(), get_TrxName());
	}

	/** Set Business Partner.
		@param C_BPartner_ID Identifies a Business Partner
	*/
	public void setC_BPartner_ID (int C_BPartner_ID)
	{
		if (C_BPartner_ID < 1)
			set_ValueNoCheck (COLUMNNAME_C_BPartner_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_C_BPartner_ID, Integer.valueOf(C_BPartner_ID));
	}

	/** Get Business Partner.
		@return Identifies a Business Partner
	  */
	public int getC_BPartner_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_BPartner_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
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

	/** Set pi_return_UU.
		@param pi_return_UU pi_return_UU
	*/
	public void setpi_return_UU (String pi_return_UU)
	{
		set_ValueNoCheck (COLUMNNAME_pi_return_UU, pi_return_UU);
	}

	/** Get pi_return_UU.
		@return pi_return_UU	  */
	public String getpi_return_UU()
	{
		return (String)get_Value(COLUMNNAME_pi_return_UU);
	}

	/** Set Status.
		@param Status Status of the currently running check
	*/
	public void setStatus (String Status)
	{
		set_Value (COLUMNNAME_Status, Status);
	}

	/** Get Status.
		@return Status of the currently running check
	  */
	public String getStatus()
	{
		return (String)get_Value(COLUMNNAME_Status);
	}
}