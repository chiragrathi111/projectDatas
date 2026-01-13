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

/** Generated Model for TC_intermediatejoinplant
 *  @author iDempiere (generated) 
 *  @version Release 10 - $Id$ */
@org.adempiere.base.Model(table="TC_intermediatejoinplant")
public class X_TC_intermediatejoinplant extends PO implements I_TC_intermediatejoinplant, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20240521L;

    /** Standard Constructor */
    public X_TC_intermediatejoinplant (Properties ctx, int TC_intermediatejoinplant_ID, String trxName)
    {
      super (ctx, TC_intermediatejoinplant_ID, trxName);
      /** if (TC_intermediatejoinplant_ID == 0)
        {
			setTC_intermediatejoinplant_ID (0);
        } */
    }

    /** Standard Constructor */
    public X_TC_intermediatejoinplant (Properties ctx, int TC_intermediatejoinplant_ID, String trxName, String ... virtualColumns)
    {
      super (ctx, TC_intermediatejoinplant_ID, trxName, virtualColumns);
      /** if (TC_intermediatejoinplant_ID == 0)
        {
			setTC_intermediatejoinplant_ID (0);
        } */
    }

    /** Load Constructor */
    public X_TC_intermediatejoinplant (Properties ctx, ResultSet rs, String trxName)
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
      StringBuilder sb = new StringBuilder ("X_TC_intermediatejoinplant[")
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

	/** Set TC_intermediatejoinplant.
		@param TC_intermediatejoinplant_ID TC_intermediatejoinplant
	*/
	public void setTC_intermediatejoinplant_ID (int TC_intermediatejoinplant_ID)
	{
		if (TC_intermediatejoinplant_ID < 1)
			set_ValueNoCheck (COLUMNNAME_TC_intermediatejoinplant_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_TC_intermediatejoinplant_ID, Integer.valueOf(TC_intermediatejoinplant_ID));
	}

	/** Get TC_intermediatejoinplant.
		@return TC_intermediatejoinplant	  */
	public int getTC_intermediatejoinplant_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_TC_intermediatejoinplant_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set TC_intermediatejoinplant_UU.
		@param TC_intermediatejoinplant_UU TC_intermediatejoinplant_UU
	*/
	public void setTC_intermediatejoinplant_UU (String TC_intermediatejoinplant_UU)
	{
		set_ValueNoCheck (COLUMNNAME_TC_intermediatejoinplant_UU, TC_intermediatejoinplant_UU);
	}

	/** Get TC_intermediatejoinplant_UU.
		@return TC_intermediatejoinplant_UU	  */
	public String getTC_intermediatejoinplant_UU()
	{
		return (String)get_Value(COLUMNNAME_TC_intermediatejoinplant_UU);
	}

	public org.realmeds.tissue.moduller.I_TC_IntermediateVisit getTC_IntermediateVisit() throws RuntimeException
	{
		return (org.realmeds.tissue.moduller.I_TC_IntermediateVisit)MTable.get(getCtx(), org.realmeds.tissue.moduller.I_TC_IntermediateVisit.Table_ID)
			.getPO(getTC_IntermediateVisit_ID(), get_TrxName());
	}

	/** Set TC_IntermediateVisit.
		@param TC_IntermediateVisit_ID TC_IntermediateVisit
	*/
	public void setTC_IntermediateVisit_ID (int TC_IntermediateVisit_ID)
	{
		if (TC_IntermediateVisit_ID < 1)
			set_ValueNoCheck (COLUMNNAME_TC_IntermediateVisit_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_TC_IntermediateVisit_ID, Integer.valueOf(TC_IntermediateVisit_ID));
	}

	/** Get TC_IntermediateVisit.
		@return TC_IntermediateVisit	  */
	public int getTC_IntermediateVisit_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_TC_IntermediateVisit_ID);
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