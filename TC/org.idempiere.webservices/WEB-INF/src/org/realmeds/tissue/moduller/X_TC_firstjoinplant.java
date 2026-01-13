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

/** Generated Model for TC_firstjoinplant
 *  @author iDempiere (generated) 
 *  @version Release 10 - $Id$ */
@org.adempiere.base.Model(table="TC_firstjoinplant")
public class X_TC_firstjoinplant extends PO implements I_TC_firstjoinplant, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20240529L;

    /** Standard Constructor */
    public X_TC_firstjoinplant (Properties ctx, int TC_firstjoinplant_ID, String trxName)
    {
      super (ctx, TC_firstjoinplant_ID, trxName);
      /** if (TC_firstjoinplant_ID == 0)
        {
			setTC_firstjoinplant_ID (0);
			setTC_FirstVisit_ID (0);
        } */
    }

    /** Standard Constructor */
    public X_TC_firstjoinplant (Properties ctx, int TC_firstjoinplant_ID, String trxName, String ... virtualColumns)
    {
      super (ctx, TC_firstjoinplant_ID, trxName, virtualColumns);
      /** if (TC_firstjoinplant_ID == 0)
        {
			setTC_firstjoinplant_ID (0);
			setTC_FirstVisit_ID (0);
        } */
    }

    /** Load Constructor */
    public X_TC_firstjoinplant (Properties ctx, ResultSet rs, String trxName)
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
      StringBuilder sb = new StringBuilder ("X_TC_firstjoinplant[")
        .append(get_ID()).append("]");
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

	/** Set TC_firstjoinplant.
		@param TC_firstjoinplant_ID TC_firstjoinplant
	*/
	public void setTC_firstjoinplant_ID (int TC_firstjoinplant_ID)
	{
		if (TC_firstjoinplant_ID < 1)
			set_ValueNoCheck (COLUMNNAME_TC_firstjoinplant_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_TC_firstjoinplant_ID, Integer.valueOf(TC_firstjoinplant_ID));
	}

	/** Get TC_firstjoinplant.
		@return TC_firstjoinplant	  */
	public int getTC_firstjoinplant_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_TC_firstjoinplant_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set TC_firstjoinplant_UU.
		@param TC_firstjoinplant_UU TC_firstjoinplant_UU
	*/
	public void setTC_firstjoinplant_UU (String TC_firstjoinplant_UU)
	{
		set_ValueNoCheck (COLUMNNAME_TC_firstjoinplant_UU, TC_firstjoinplant_UU);
	}

	/** Get TC_firstjoinplant_UU.
		@return TC_firstjoinplant_UU	  */
	public String getTC_firstjoinplant_UU()
	{
		return (String)get_Value(COLUMNNAME_TC_firstjoinplant_UU);
	}

	public org.realmeds.tissue.moduller.I_TC_FirstVisit getTC_FirstVisit() throws RuntimeException
	{
		return (org.realmeds.tissue.moduller.I_TC_FirstVisit)MTable.get(getCtx(), org.realmeds.tissue.moduller.I_TC_FirstVisit.Table_ID)
			.getPO(getTC_FirstVisit_ID(), get_TrxName());
	}

	/** Set TC_FirstVisit.
		@param TC_FirstVisit_ID TC_FirstVisit
	*/
	public void setTC_FirstVisit_ID (int TC_FirstVisit_ID)
	{
		if (TC_FirstVisit_ID < 1)
			set_ValueNoCheck (COLUMNNAME_TC_FirstVisit_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_TC_FirstVisit_ID, Integer.valueOf(TC_FirstVisit_ID));
	}

	/** Get TC_FirstVisit.
		@return TC_FirstVisit	  */
	public int getTC_FirstVisit_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_TC_FirstVisit_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.realmeds.tissue.moduller.I_TC_PlantDetails getTC_PlantDetails() throws RuntimeException
	{
		return (org.realmeds.tissue.moduller.I_TC_PlantDetails)MTable.get(getCtx(), org.realmeds.tissue.moduller.I_TC_PlantDetails.Table_ID)
			.getPO(getTC_PlantDetails_ID(), get_TrxName());
	}

	/** Set TC_PlantDetails.
		@param TC_PlantDetails_ID TC_PlantDetails
	*/
	public void setTC_PlantDetails_ID (int TC_PlantDetails_ID)
	{
		if (TC_PlantDetails_ID < 1)
			set_ValueNoCheck (COLUMNNAME_TC_PlantDetails_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_TC_PlantDetails_ID, Integer.valueOf(TC_PlantDetails_ID));
	}

	/** Get TC_PlantDetails.
		@return TC_PlantDetails	  */
	public int getTC_PlantDetails_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_TC_PlantDetails_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.realmeds.tissue.moduller.I_TC_plantstatus getTC_plantstatus() throws RuntimeException
	{
		return (org.realmeds.tissue.moduller.I_TC_plantstatus)MTable.get(getCtx(), org.realmeds.tissue.moduller.I_TC_plantstatus.Table_ID)
			.getPO(getTC_plantstatus_ID(), get_TrxName());
	}

	/** Set TC_plantstatus.
		@param TC_plantstatus_ID TC_plantstatus
	*/
	public void setTC_plantstatus_ID (int TC_plantstatus_ID)
	{
		if (TC_plantstatus_ID < 1)
			set_ValueNoCheck (COLUMNNAME_TC_plantstatus_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_TC_plantstatus_ID, Integer.valueOf(TC_plantstatus_ID));
	}

	/** Get TC_plantstatus.
		@return TC_plantstatus	  */
	public int getTC_plantstatus_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_TC_plantstatus_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}
}