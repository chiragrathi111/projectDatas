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

/** Generated Model for pi_printerconfig
 *  @author iDempiere (generated) 
 *  @version Release 10 - $Id$ */
@org.adempiere.base.Model(table="pi_printerconfig")
public class X_pi_printerconfig extends PO implements I_pi_printerconfig, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20241220L;

    /** Standard Constructor */
    public X_pi_printerconfig (Properties ctx, int pi_printerconfig_ID, String trxName)
    {
      super (ctx, pi_printerconfig_ID, trxName);
      /** if (pi_printerconfig_ID == 0)
        {
			setName (null);
			setValue (null);
			setpi_printerconfig_ID (0);
        } */
    }

    /** Standard Constructor */
    public X_pi_printerconfig (Properties ctx, int pi_printerconfig_ID, String trxName, String ... virtualColumns)
    {
      super (ctx, pi_printerconfig_ID, trxName, virtualColumns);
      /** if (pi_printerconfig_ID == 0)
        {
			setName (null);
			setValue (null);
			setpi_printerconfig_ID (0);
        } */
    }

    /** Load Constructor */
    public X_pi_printerconfig (Properties ctx, ResultSet rs, String trxName)
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
      StringBuilder sb = new StringBuilder ("X_pi_printerconfig[")
        .append(get_ID()).append(",Name=").append(getName()).append("]");
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

	/** Set Height.
		@param Height Height
	*/
	public void setHeight (BigDecimal Height)
	{
		set_Value (COLUMNNAME_Height, Height);
	}

	/** Get Height.
		@return Height	  */
	public BigDecimal getHeight()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_Height);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Name.
		@param Name Alphanumeric identifier of the entity
	*/
	public void setName (String Name)
	{
		set_Value (COLUMNNAME_Name, Name);
	}

	/** Get Name.
		@return Alphanumeric identifier of the entity
	  */
	public String getName()
	{
		return (String)get_Value(COLUMNNAME_Name);
	}

	/** Set Script.
		@param Script Dynamic Java Language Script to calculate result
	*/
	public void setScript (String Script)
	{
		set_Value (COLUMNNAME_Script, Script);
	}

	/** Get Script.
		@return Dynamic Java Language Script to calculate result
	  */
	public String getScript()
	{
		return (String)get_Value(COLUMNNAME_Script);
	}

	/** Set Search Key.
		@param Value Search key for the record in the format required - must be unique
	*/
	public void setValue (String Value)
	{
		set_Value (COLUMNNAME_Value, Value);
	}

	/** Get Search Key.
		@return Search key for the record in the format required - must be unique
	  */
	public String getValue()
	{
		return (String)get_Value(COLUMNNAME_Value);
	}

	/** Set Width.
		@param Width Width
	*/
	public void setWidth (BigDecimal Width)
	{
		set_Value (COLUMNNAME_Width, Width);
	}

	/** Get Width.
		@return Width	  */
	public BigDecimal getWidth()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_Width);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Printer Config.
		@param pi_printerconfig_ID Printer Config
	*/
	public void setpi_printerconfig_ID (int pi_printerconfig_ID)
	{
		if (pi_printerconfig_ID < 1)
			set_ValueNoCheck (COLUMNNAME_pi_printerconfig_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_pi_printerconfig_ID, Integer.valueOf(pi_printerconfig_ID));
	}

	/** Get Printer Config.
		@return Printer Config	  */
	public int getpi_printerconfig_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_pi_printerconfig_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set pi_printerconfig_UU.
		@param pi_printerconfig_UU pi_printerconfig_UU
	*/
	public void setpi_printerconfig_UU (String pi_printerconfig_UU)
	{
		set_ValueNoCheck (COLUMNNAME_pi_printerconfig_UU, pi_printerconfig_UU);
	}

	/** Get pi_printerconfig_UU.
		@return pi_printerconfig_UU	  */
	public String getpi_printerconfig_UU()
	{
		return (String)get_Value(COLUMNNAME_pi_printerconfig_UU);
	}
}