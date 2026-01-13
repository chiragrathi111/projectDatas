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
import java.sql.Timestamp;
import java.util.Properties;
import org.compiere.model.*;

/** Generated Model for TC_CultureDetails
 *  @author iDempiere (generated) 
 *  @version Release 10 - $Id$ */
@org.adempiere.base.Model(table="TC_CultureDetails")
public class X_TC_CultureDetails extends PO implements I_TC_CultureDetails, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20240304L;

    /** Standard Constructor */
    public X_TC_CultureDetails (Properties ctx, int TC_CultureDetails_ID, String trxName)
    {
      super (ctx, TC_CultureDetails_ID, trxName);
      /** if (TC_CultureDetails_ID == 0)
        {
			setIsDefault (false);
			setName (null);
			setTC_CultureDetails_ID (0);
        } */
    }

    /** Standard Constructor */
    public X_TC_CultureDetails (Properties ctx, int TC_CultureDetails_ID, String trxName, String ... virtualColumns)
    {
      super (ctx, TC_CultureDetails_ID, trxName, virtualColumns);
      /** if (TC_CultureDetails_ID == 0)
        {
			setIsDefault (false);
			setName (null);
			setTC_CultureDetails_ID (0);
        } */
    }

    /** Load Constructor */
    public X_TC_CultureDetails (Properties ctx, ResultSet rs, String trxName)
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
      StringBuilder sb = new StringBuilder ("X_TC_CultureDetails[")
        .append(get_ID()).append(",Name=").append(getName()).append("]");
      return sb.toString();
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
    
	/** Set cycleno.
		@param cycleno cycleno
	*/
	public void setcycleno (int cycleno)
	{
		set_Value (COLUMNNAME_cycleno, Integer.valueOf(cycleno));
	}

	/** Get cycleno.
		@return cycleno	  */
	public int getcycleno()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_cycleno);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set date.
		@param date date
	*/
	public void setdate (Timestamp date)
	{
		set_Value (COLUMNNAME_date, date);
	}

	/** Get date.
		@return date	  */
	public Timestamp getdate()
	{
		return (Timestamp)get_Value(COLUMNNAME_date);
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

	/** Set parentcultureline.
		@param parentcultureline parentcultureline
	*/
	public void setparentcultureline (String parentcultureline)
	{
		set_Value (COLUMNNAME_parentcultureline, parentcultureline);
	}

	/** Get parentcultureline.
		@return parentcultureline	  */
	public String getparentcultureline()
	{
		return (String)get_Value(COLUMNNAME_parentcultureline);
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

	public org.realmeds.tissue.moduller.I_TC_CultureStage getTC_CultureStage() throws RuntimeException
	{
		return (org.realmeds.tissue.moduller.I_TC_CultureStage)MTable.get(getCtx(), org.realmeds.tissue.moduller.I_TC_CultureStage.Table_ID)
			.getPO(getTC_CultureStage_ID(), get_TrxName());
	}

	/** Set TC_CultureStage.
		@param TC_CultureStage_ID TC_CultureStage
	*/
	public void setTC_CultureStage_ID (int TC_CultureStage_ID)
	{
		if (TC_CultureStage_ID < 1)
			set_ValueNoCheck (COLUMNNAME_TC_CultureStage_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_TC_CultureStage_ID, Integer.valueOf(TC_CultureStage_ID));
	}

	/** Get TC_CultureStage.
		@return TC_CultureStage	  */
	public int getTC_CultureStage_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_TC_CultureStage_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.realmeds.tissue.moduller.I_TC_NatureSample getTC_NatureSample() throws RuntimeException
	{
		return (org.realmeds.tissue.moduller.I_TC_NatureSample)MTable.get(getCtx(), org.realmeds.tissue.moduller.I_TC_NatureSample.Table_ID)
			.getPO(getTC_NatureSample_ID(), get_TrxName());
	}

	/** Set TC_NatureSample.
		@param TC_NatureSample_ID TC_NatureSample
	*/
	public void setTC_NatureSample_ID (int TC_NatureSample_ID)
	{
		if (TC_NatureSample_ID < 1)
			set_ValueNoCheck (COLUMNNAME_TC_NatureSample_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_TC_NatureSample_ID, Integer.valueOf(TC_NatureSample_ID));
	}

	/** Get TC_NatureSample.
		@return TC_NatureSample	  */
	public int getTC_NatureSample_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_TC_NatureSample_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.realmeds.tissue.moduller.I_TC_PlantSpecies gettc_species() throws RuntimeException
	{
		return (org.realmeds.tissue.moduller.I_TC_PlantSpecies)MTable.get(getCtx(), org.realmeds.tissue.moduller.I_TC_PlantSpecies.Table_ID)
			.getPO(gettc_species_id(), get_TrxName());
	}

	/** Set tc_species_id.
		@param tc_species_id tc_species_id
	*/
	public void settc_species_id (int tc_species_id)
	{
		set_Value (COLUMNNAME_tc_species_id, Integer.valueOf(tc_species_id));
	}

	/** Get tc_species_id.
		@return tc_species_id	  */
	public int gettc_species_id()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_tc_species_id);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.realmeds.tissue.moduller.I_TC_PlantSpecies gettc_species_() throws RuntimeException
	{
		return (org.realmeds.tissue.moduller.I_TC_PlantSpecies)MTable.get(getCtx(), org.realmeds.tissue.moduller.I_TC_PlantSpecies.Table_ID)
			.getPO(gettc_species_ids(), get_TrxName());
	}

	/** Set tc_species_ids.
		@param tc_species_ids tc_species_ids
	*/
	public void settc_species_ids (int tc_species_ids)
	{
		set_Value (COLUMNNAME_tc_species_ids, Integer.valueOf(tc_species_ids));
	}

	/** Get tc_species_ids.
		@return tc_species_ids	  */
	public int gettc_species_ids()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_tc_species_ids);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.realmeds.tissue.moduller.I_TC_VirusTesting getTC_VirusTesting() throws RuntimeException
	{
		return (org.realmeds.tissue.moduller.I_TC_VirusTesting)MTable.get(getCtx(), org.realmeds.tissue.moduller.I_TC_VirusTesting.Table_ID)
			.getPO(getTC_VirusTesting_ID(), get_TrxName());
	}

	/** Set TC_VirusTesting.
		@param TC_VirusTesting_ID TC_VirusTesting
	*/
	public void setTC_VirusTesting_ID (int TC_VirusTesting_ID)
	{
		if (TC_VirusTesting_ID < 1)
			set_ValueNoCheck (COLUMNNAME_TC_VirusTesting_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_TC_VirusTesting_ID, Integer.valueOf(TC_VirusTesting_ID));
	}

	/** Get TC_VirusTesting.
		@return TC_VirusTesting	  */
	public int getTC_VirusTesting_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_TC_VirusTesting_ID);
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