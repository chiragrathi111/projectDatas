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
import java.sql.Timestamp;
import java.util.Properties;
import org.compiere.model.*;

/** Generated Model for pi_salesplan
 *  @author iDempiere (generated) 
 *  @version Release 10 - $Id$ */
@org.adempiere.base.Model(table="pi_salesplan")
public class X_pi_salesplan extends PO implements I_pi_salesplan, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20250427L;

    /** Standard Constructor */
    public X_pi_salesplan (Properties ctx, int pi_salesplan_ID, String trxName)
    {
      super (ctx, pi_salesplan_ID, trxName);
      /** if (pi_salesplan_ID == 0)
        {
			setpi_salesplan_ID (0);
        } */
    }

    /** Standard Constructor */
    public X_pi_salesplan (Properties ctx, int pi_salesplan_ID, String trxName, String ... virtualColumns)
    {
      super (ctx, pi_salesplan_ID, trxName, virtualColumns);
      /** if (pi_salesplan_ID == 0)
        {
			setpi_salesplan_ID (0);
        } */
    }

    /** Load Constructor */
    public X_pi_salesplan (Properties ctx, ResultSet rs, String trxName)
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
      StringBuilder sb = new StringBuilder ("X_pi_salesplan[")
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

	/** Set Sales Plan.
		@param pi_salesplan_ID Sales Plan
	*/
	public void setpi_salesplan_ID (int pi_salesplan_ID)
	{
		if (pi_salesplan_ID < 1)
			set_ValueNoCheck (COLUMNNAME_pi_salesplan_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_pi_salesplan_ID, Integer.valueOf(pi_salesplan_ID));
	}

	/** Get Sales Plan.
		@return Sales Plan	  */
	public int getpi_salesplan_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_pi_salesplan_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set pi_salesplan_UU.
		@param pi_salesplan_UU pi_salesplan_UU
	*/
	public void setpi_salesplan_UU (String pi_salesplan_UU)
	{
		set_ValueNoCheck (COLUMNNAME_pi_salesplan_UU, pi_salesplan_UU);
	}

	/** Get pi_salesplan_UU.
		@return pi_salesplan_UU	  */
	public String getpi_salesplan_UU()
	{
		return (String)get_Value(COLUMNNAME_pi_salesplan_UU);
	}

	/** Set Sales Plan Date.
		@param salesplandate Sales Plan Date
	*/
	public void setsalesplandate (Timestamp salesplandate)
	{
		set_Value (COLUMNNAME_salesplandate, salesplandate);
	}

	/** Get Sales Plan Date.
		@return Sales Plan Date	  */
	public Timestamp getsalesplandate()
	{
		return (Timestamp)get_Value(COLUMNNAME_salesplandate);
	}

	/** Status AD_Reference_ID=53239 */
	public static final int STATUS_AD_Reference_ID=53239;
	/** Completed = CO */
	public static final String STATUS_Completed = "CO";
	/** Error = ER */
	public static final String STATUS_Error = "ER";
	/** In Progress = IP */
	public static final String STATUS_InProgress = "IP";
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