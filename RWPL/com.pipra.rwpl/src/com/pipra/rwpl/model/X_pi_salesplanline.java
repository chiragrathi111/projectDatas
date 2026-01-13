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

/** Generated Model for pi_salesplanline
 *  @author iDempiere (generated) 
 *  @version Release 10 - $Id$ */
@org.adempiere.base.Model(table="pi_salesplanline")
public class X_pi_salesplanline extends PO implements I_pi_salesplanline, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20250427L;

    /** Standard Constructor */
    public X_pi_salesplanline (Properties ctx, int pi_salesplanline_ID, String trxName)
    {
      super (ctx, pi_salesplanline_ID, trxName);
      /** if (pi_salesplanline_ID == 0)
        {
			setC_BPartner_ID (0);
			setpi_salesplanline_ID (0);
        } */
    }

    /** Standard Constructor */
    public X_pi_salesplanline (Properties ctx, int pi_salesplanline_ID, String trxName, String ... virtualColumns)
    {
      super (ctx, pi_salesplanline_ID, trxName, virtualColumns);
      /** if (pi_salesplanline_ID == 0)
        {
			setC_BPartner_ID (0);
			setpi_salesplanline_ID (0);
        } */
    }

    /** Load Constructor */
    public X_pi_salesplanline (Properties ctx, ResultSet rs, String trxName)
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
      StringBuilder sb = new StringBuilder ("X_pi_salesplanline[")
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

	public com.pipra.rwpl.model.I_pi_salesplan getpi_salesplan() throws RuntimeException
	{
		return (com.pipra.rwpl.model.I_pi_salesplan)MTable.get(getCtx(), com.pipra.rwpl.model.I_pi_salesplan.Table_ID)
			.getPO(getpi_salesplan_ID(), get_TrxName());
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

	/** Set pi_salesplanline_UU.
		@param pi_salesplanline_UU pi_salesplanline_UU
	*/
	public void setpi_salesplanline_UU (String pi_salesplanline_UU)
	{
		set_ValueNoCheck (COLUMNNAME_pi_salesplanline_UU, pi_salesplanline_UU);
	}

	/** Get pi_salesplanline_UU.
		@return pi_salesplanline_UU	  */
	public String getpi_salesplanline_UU()
	{
		return (String)get_Value(COLUMNNAME_pi_salesplanline_UU);
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
	
	public org.compiere.model.I_M_Warehouse getM_Warehouse() throws RuntimeException
	{
		return (org.compiere.model.I_M_Warehouse)MTable.get(getCtx(), org.compiere.model.I_M_Warehouse.Table_ID)
			.getPO(getM_Warehouse_ID(), get_TrxName());
	}

	/** Set Warehouse.
		@param M_Warehouse_ID Storage Warehouse and Service Point
	*/
	public void setM_Warehouse_ID (int M_Warehouse_ID)
	{
		if (M_Warehouse_ID < 1)
			set_ValueNoCheck (COLUMNNAME_M_Warehouse_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_M_Warehouse_ID, Integer.valueOf(M_Warehouse_ID));
	}

	/** Get Warehouse.
		@return Storage Warehouse and Service Point
	  */
	public int getM_Warehouse_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_Warehouse_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}
}