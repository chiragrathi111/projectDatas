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

/** Generated Model for tc_primaryHardeningcultureS
 *  @author iDempiere (generated) 
 *  @version Release 10 - $Id$ */
@org.adempiere.base.Model(table="tc_primaryHardeningcultureS")
public class X_tc_primaryHardeningcultureS extends PO implements I_tc_primaryHardeningcultureS, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20240927L;

    /** Standard Constructor */
    public X_tc_primaryHardeningcultureS (Properties ctx, int tc_primaryHardeningcultureS_ID, String trxName)
    {
      super (ctx, tc_primaryHardeningcultureS_ID, trxName);
      /** if (tc_primaryHardeningcultureS_ID == 0)
        {
			settc_primaryHardeningcultureS_ID (0);
        } */
    }

    /** Standard Constructor */
    public X_tc_primaryHardeningcultureS (Properties ctx, int tc_primaryHardeningcultureS_ID, String trxName, String ... virtualColumns)
    {
      super (ctx, tc_primaryHardeningcultureS_ID, trxName, virtualColumns);
      /** if (tc_primaryHardeningcultureS_ID == 0)
        {
			settc_primaryHardeningcultureS_ID (0);
        } */
    }

    /** Load Constructor */
    public X_tc_primaryHardeningcultureS (Properties ctx, ResultSet rs, String trxName)
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
      StringBuilder sb = new StringBuilder ("X_tc_primaryHardeningcultureS[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	/** Set cultureuuid.
		@param cultureuuid cultureuuid
	*/
	public void setcultureuuid (String cultureuuid)
	{
		set_Value (COLUMNNAME_cultureuuid, cultureuuid);
	}

	/** Get cultureuuid.
		@return cultureuuid	  */
	public String getcultureuuid()
	{
		return (String)get_Value(COLUMNNAME_cultureuuid);
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

	public org.realmeds.tissue.moduller.I_TC_cultureLabel getTC_cultureLabel() throws RuntimeException
	{
		return (org.realmeds.tissue.moduller.I_TC_cultureLabel)MTable.get(getCtx(), org.realmeds.tissue.moduller.I_TC_cultureLabel.Table_ID)
			.getPO(getTC_cultureLabel_ID(), get_TrxName());
	}

	/** Set TC_cultureLabel.
		@param TC_cultureLabel_ID TC_cultureLabel
	*/
	public void setTC_cultureLabel_ID (int TC_cultureLabel_ID)
	{
		if (TC_cultureLabel_ID < 1)
			set_ValueNoCheck (COLUMNNAME_TC_cultureLabel_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_TC_cultureLabel_ID, Integer.valueOf(TC_cultureLabel_ID));
	}

	/** Get TC_cultureLabel.
		@return TC_cultureLabel	  */
	public int getTC_cultureLabel_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_TC_cultureLabel_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set tc_primaryHardeningcultureS.
		@param tc_primaryHardeningcultureS_ID tc_primaryHardeningcultureS
	*/
	public void settc_primaryHardeningcultureS_ID (int tc_primaryHardeningcultureS_ID)
	{
		if (tc_primaryHardeningcultureS_ID < 1)
			set_ValueNoCheck (COLUMNNAME_tc_primaryHardeningcultureS_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_tc_primaryHardeningcultureS_ID, Integer.valueOf(tc_primaryHardeningcultureS_ID));
	}

	/** Get tc_primaryHardeningcultureS.
		@return tc_primaryHardeningcultureS	  */
	public int gettc_primaryHardeningcultureS_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_tc_primaryHardeningcultureS_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set tc_primaryHardeningcultureS_UU.
		@param tc_primaryHardeningcultureS_UU tc_primaryHardeningcultureS_UU
	*/
	public void settc_primaryHardeningcultureS_UU (String tc_primaryHardeningcultureS_UU)
	{
		set_ValueNoCheck (COLUMNNAME_tc_primaryHardeningcultureS_UU, tc_primaryHardeningcultureS_UU);
	}

	/** Get tc_primaryHardeningcultureS_UU.
		@return tc_primaryHardeningcultureS_UU	  */
	public String gettc_primaryHardeningcultureS_UU()
	{
		return (String)get_Value(COLUMNNAME_tc_primaryHardeningcultureS_UU);
	}

	public org.realmeds.tissue.moduller.I_TC_PrimaryHardeningLabel getTC_PrimaryHardeningLabel() throws RuntimeException
	{
		return (org.realmeds.tissue.moduller.I_TC_PrimaryHardeningLabel)MTable.get(getCtx(), org.realmeds.tissue.moduller.I_TC_PrimaryHardeningLabel.Table_ID)
			.getPO(getTC_PrimaryHardeningLabel_ID(), get_TrxName());
	}

	/** Set TC_PrimaryHardeningLabel.
		@param TC_PrimaryHardeningLabel_ID TC_PrimaryHardeningLabel
	*/
	public void setTC_PrimaryHardeningLabel_ID (int TC_PrimaryHardeningLabel_ID)
	{
		if (TC_PrimaryHardeningLabel_ID < 1)
			set_ValueNoCheck (COLUMNNAME_TC_PrimaryHardeningLabel_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_TC_PrimaryHardeningLabel_ID, Integer.valueOf(TC_PrimaryHardeningLabel_ID));
	}

	/** Get TC_PrimaryHardeningLabel.
		@return TC_PrimaryHardeningLabel	  */
	public int getTC_PrimaryHardeningLabel_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_TC_PrimaryHardeningLabel_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}
}