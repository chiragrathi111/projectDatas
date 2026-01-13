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

/** Generated Model for TC_HardeningDetail
 *  @author iDempiere (generated) 
 *  @version Release 10 - $Id$ */
@org.adempiere.base.Model(table="TC_HardeningDetail")
public class X_TC_HardeningDetail extends PO implements I_TC_HardeningDetail, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20240304L;

    /** Standard Constructor */
    public X_TC_HardeningDetail (Properties ctx, int TC_HardeningDetail_ID, String trxName)
    {
      super (ctx, TC_HardeningDetail_ID, trxName);
      /** if (TC_HardeningDetail_ID == 0)
        {
			setIsDefault (false);
			setName (null);
			setTC_HardeningDetail_ID (0);
        } */
    }

    /** Standard Constructor */
    public X_TC_HardeningDetail (Properties ctx, int TC_HardeningDetail_ID, String trxName, String ... virtualColumns)
    {
      super (ctx, TC_HardeningDetail_ID, trxName, virtualColumns);
      /** if (TC_HardeningDetail_ID == 0)
        {
			setIsDefault (false);
			setName (null);
			setTC_HardeningDetail_ID (0);
        } */
    }

    /** Load Constructor */
    public X_TC_HardeningDetail (Properties ctx, ResultSet rs, String trxName)
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
    
    /**
	 * Set c_uuid.
	 * 
	 * @param c_uuid c_uuid
	 */
	public void setc_uuid(String c_uuid) {
		set_Value(COLUMNNAME_c_uuid, c_uuid);
	}

	/**
	 * Get c_uuid.
	 * 
	 * @return c_uuid
	 */
	public String getc_uuid() {
		return (String) get_Value(COLUMNNAME_c_uuid);
	}

    public String toString()
    {
      StringBuilder sb = new StringBuilder ("X_TC_HardeningDetail[")
        .append(get_ID()).append(",Name=").append(getName()).append("]");
      return sb.toString();
    }

	/** Set cultureprocessedno.
		@param cultureprocessedno cultureprocessedno
	*/
	public void setcultureprocessedno (String cultureprocessedno)
	{
		set_Value (COLUMNNAME_cultureprocessedno, cultureprocessedno);
	}

	/** Get cultureprocessedno.
		@return cultureprocessedno	  */
	public String getcultureprocessedno()
	{
		return (String)get_Value(COLUMNNAME_cultureprocessedno);
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

	/** Set Default.
		@param IsDefault Default value
	*/
	public void setIsDefault (boolean IsDefault)
	{
		set_Value (COLUMNNAME_IsDefault, Boolean.valueOf(IsDefault));
	}

	/** Get Default.
		@return Default value
	  */
	public boolean isDefault()
	{
		Object oo = get_Value(COLUMNNAME_IsDefault);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
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

	/** Set rackno.
		@param rackno rackno
	*/
	public void setrackno (String rackno)
	{
		set_Value (COLUMNNAME_rackno, rackno);
	}

	/** Get rackno.
		@return rackno	  */
	public String getrackno()
	{
		return (String)get_Value(COLUMNNAME_rackno);
	}

	public org.realmeds.tissue.moduller.I_TC_CultureDetails getTC_CultureDetails() throws RuntimeException
	{
		return (org.realmeds.tissue.moduller.I_TC_CultureDetails)MTable.get(getCtx(), org.realmeds.tissue.moduller.I_TC_CultureDetails.Table_ID)
			.getPO(getTC_CultureDetails_ID(), get_TrxName());
	}

	/** Set TC_CultureDetails.
		@param TC_CultureDetails_ID TC_CultureDetails
	*/
	public void setTC_CultureDetails_ID (int TC_CultureDetails_ID)
	{
		if (TC_CultureDetails_ID < 1)
			set_ValueNoCheck (COLUMNNAME_TC_CultureDetails_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_TC_CultureDetails_ID, Integer.valueOf(TC_CultureDetails_ID));
	}

	/** Get TC_CultureDetails.
		@return TC_CultureDetails	  */
	public int getTC_CultureDetails_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_TC_CultureDetails_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.realmeds.tissue.moduller.I_TC_CultureOperationDetails getTC_CultureOperationDetails() throws RuntimeException
	{
		return (org.realmeds.tissue.moduller.I_TC_CultureOperationDetails)MTable.get(getCtx(), org.realmeds.tissue.moduller.I_TC_CultureOperationDetails.Table_ID)
			.getPO(getTC_CultureOperationDetails_ID(), get_TrxName());
	}

	/** Set TC_CultureOperationDetails.
		@param TC_CultureOperationDetails_ID TC_CultureOperationDetails
	*/
	public void setTC_CultureOperationDetails_ID (int TC_CultureOperationDetails_ID)
	{
		if (TC_CultureOperationDetails_ID < 1)
			set_ValueNoCheck (COLUMNNAME_TC_CultureOperationDetails_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_TC_CultureOperationDetails_ID, Integer.valueOf(TC_CultureOperationDetails_ID));
	}

	/** Get TC_CultureOperationDetails.
		@return TC_CultureOperationDetails	  */
	public int getTC_CultureOperationDetails_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_TC_CultureOperationDetails_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.realmeds.tissue.moduller.I_TC_Cycle getTC_Cycle() throws RuntimeException
	{
		return (org.realmeds.tissue.moduller.I_TC_Cycle)MTable.get(getCtx(), org.realmeds.tissue.moduller.I_TC_Cycle.Table_ID)
			.getPO(getTC_Cycle_ID(), get_TrxName());
	}

	/** Set TC_Cycle.
		@param TC_Cycle_ID TC_Cycle
	*/
	public void setTC_Cycle_ID (int TC_Cycle_ID)
	{
		if (TC_Cycle_ID < 1)
			set_ValueNoCheck (COLUMNNAME_TC_Cycle_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_TC_Cycle_ID, Integer.valueOf(TC_Cycle_ID));
	}

	/** Get TC_Cycle.
		@return TC_Cycle	  */
	public int getTC_Cycle_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_TC_Cycle_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set TC_HardeningDetail.
		@param TC_HardeningDetail_ID TC_HardeningDetail
	*/
	public void setTC_HardeningDetail_ID (int TC_HardeningDetail_ID)
	{
		if (TC_HardeningDetail_ID < 1)
			set_ValueNoCheck (COLUMNNAME_TC_HardeningDetail_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_TC_HardeningDetail_ID, Integer.valueOf(TC_HardeningDetail_ID));
	}

	/** Get TC_HardeningDetail.
		@return TC_HardeningDetail	  */
	public int getTC_HardeningDetail_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_TC_HardeningDetail_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
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