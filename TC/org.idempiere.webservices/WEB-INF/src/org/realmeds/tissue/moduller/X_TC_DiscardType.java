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
package org.realmeds.tissue.moduller;

import java.sql.ResultSet;
import java.util.Properties;
import org.compiere.model.*;

/** Generated Model for TC_DiscardType
 *  @author iDempiere (generated) 
 *  @version Release 10 - $Id$ */
@org.adempiere.base.Model(table="TC_DiscardType")
public class X_TC_DiscardType extends PO implements I_TC_DiscardType, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20240711L;

    /** Standard Constructor */
    public X_TC_DiscardType (Properties ctx, int TC_DiscardType_ID, String trxName)
    {
      super (ctx, TC_DiscardType_ID, trxName);
      /** if (TC_DiscardType_ID == 0)
        {
			setTC_DiscardType_ID (0);
        } */
    }

    /** Standard Constructor */
    public X_TC_DiscardType (Properties ctx, int TC_DiscardType_ID, String trxName, String ... virtualColumns)
    {
      super (ctx, TC_DiscardType_ID, trxName, virtualColumns);
      /** if (TC_DiscardType_ID == 0)
        {
			setTC_DiscardType_ID (0);
        } */
    }

    /** Load Constructor */
    public X_TC_DiscardType (Properties ctx, ResultSet rs, String trxName)
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
      StringBuilder sb = new StringBuilder ("X_TC_DiscardType[")
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

	/** Set TC_DiscardType.
		@param TC_DiscardType_ID TC_DiscardType
	*/
	public void setTC_DiscardType_ID (int TC_DiscardType_ID)
	{
		if (TC_DiscardType_ID < 1)
			set_ValueNoCheck (COLUMNNAME_TC_DiscardType_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_TC_DiscardType_ID, Integer.valueOf(TC_DiscardType_ID));
	}

	/** Get TC_DiscardType.
		@return TC_DiscardType	  */
	public int getTC_DiscardType_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_TC_DiscardType_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set TC_DiscardType_UU.
		@param TC_DiscardType_UU TC_DiscardType_UU
	*/
	public void setTC_DiscardType_UU (String TC_DiscardType_UU)
	{
		set_ValueNoCheck (COLUMNNAME_TC_DiscardType_UU, TC_DiscardType_UU);
	}

	/** Get TC_DiscardType_UU.
		@return TC_DiscardType_UU	  */
	public String getTC_DiscardType_UU()
	{
		return (String)get_Value(COLUMNNAME_TC_DiscardType_UU);
	}
}