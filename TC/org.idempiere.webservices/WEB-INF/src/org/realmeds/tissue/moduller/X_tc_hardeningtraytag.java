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

/** Generated Model for tc_hardeningtraytag
 *  @author iDempiere (generated) 
 *  @version Release 10 - $Id$ */
@org.adempiere.base.Model(table="tc_hardeningtraytag")
public class X_tc_hardeningtraytag extends PO implements I_tc_hardeningtraytag, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20240425L;

    /** Standard Constructor */
    public X_tc_hardeningtraytag (Properties ctx, int tc_hardeningtraytag_ID, String trxName)
    {
      super (ctx, tc_hardeningtraytag_ID, trxName);
      /** if (tc_hardeningtraytag_ID == 0)
        {
			setDocumentNo (null);
			settc_hardeningtraytag_ID (0);
			settc_hardeningtraytag_UU (null);
        } */
    }

    /** Standard Constructor */
    public X_tc_hardeningtraytag (Properties ctx, int tc_hardeningtraytag_ID, String trxName, String ... virtualColumns)
    {
      super (ctx, tc_hardeningtraytag_ID, trxName, virtualColumns);
      /** if (tc_hardeningtraytag_ID == 0)
        {
			setDocumentNo (null);
			settc_hardeningtraytag_ID (0);
			settc_hardeningtraytag_UU (null);
        } */
    }

    /** Load Constructor */
    public X_tc_hardeningtraytag (Properties ctx, ResultSet rs, String trxName)
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
      StringBuilder sb = new StringBuilder ("X_tc_hardeningtraytag[")
        .append(get_ID()).append(",Name=").append(getName()).append("]");
      return sb.toString();
    }

	/** Set c_uuid.
		@param c_uuid c_uuid
	*/
	public void setc_uuid (String c_uuid)
	{
		set_Value (COLUMNNAME_c_uuid, c_uuid);
	}

	/** Get c_uuid.
		@return c_uuid	  */
	public String getc_uuid()
	{
		return (String)get_Value(COLUMNNAME_c_uuid);
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

	/** Set Document No.
		@param DocumentNo Document sequence number of the document
	*/
	public void setDocumentNo (String DocumentNo)
	{
		set_ValueNoCheck (COLUMNNAME_DocumentNo, DocumentNo);
	}

	/** Get Document No.
		@return Document sequence number of the document
	  */
	public String getDocumentNo()
	{
		return (String)get_Value(COLUMNNAME_DocumentNo);
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

	/** Set tc_hardeningtraytag.
		@param tc_hardeningtraytag_ID tc_hardeningtraytag
	*/
	public void settc_hardeningtraytag_ID (int tc_hardeningtraytag_ID)
	{
		if (tc_hardeningtraytag_ID < 1)
			set_ValueNoCheck (COLUMNNAME_tc_hardeningtraytag_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_tc_hardeningtraytag_ID, Integer.valueOf(tc_hardeningtraytag_ID));
	}

	/** Get tc_hardeningtraytag.
		@return tc_hardeningtraytag	  */
	public int gettc_hardeningtraytag_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_tc_hardeningtraytag_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set tc_hardeningtraytag_UU.
		@param tc_hardeningtraytag_UU tc_hardeningtraytag_UU
	*/
	public void settc_hardeningtraytag_UU (String tc_hardeningtraytag_UU)
	{
		set_ValueNoCheck (COLUMNNAME_tc_hardeningtraytag_UU, tc_hardeningtraytag_UU);
	}

	/** Get tc_hardeningtraytag_UU.
		@return tc_hardeningtraytag_UU	  */
	public String gettc_hardeningtraytag_UU()
	{
		return (String)get_Value(COLUMNNAME_tc_hardeningtraytag_UU);
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
}