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

/**
 * Generated Model for TC_CollectionDetails
 * 
 * @author iDempiere (generated)
 * @version Release 10 - $Id$
 */
@org.adempiere.base.Model(table = "TC_CollectionDetails")
public class X_TC_CollectionDetails extends PO implements I_TC_CollectionDetails, I_Persistent {

	/**
	 *
	 */
	private static final long serialVersionUID = 20240304L;

	/** Standard Constructor */
	public X_TC_CollectionDetails(Properties ctx, int TC_CollectionDetails_ID, String trxName) {
		super(ctx, TC_CollectionDetails_ID, trxName);
		/**
		 * if (TC_CollectionDetails_ID == 0) { setIsDefault (false); setName (null);
		 * setTC_CollectionDetails_ID (0); }
		 */
	}

	/** Standard Constructor */
	public X_TC_CollectionDetails(Properties ctx, int TC_CollectionDetails_ID, String trxName,
			String... virtualColumns) {
		super(ctx, TC_CollectionDetails_ID, trxName, virtualColumns);
		/**
		 * if (TC_CollectionDetails_ID == 0) { setIsDefault (false); setName (null);
		 * setTC_CollectionDetails_ID (0); }
		 */
	}

	/** Load Constructor */
	public X_TC_CollectionDetails(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}

	/**
	 * AccessLevel
	 * 
	 * @return 3 - Client - Org
	 */
	protected int get_AccessLevel() {
		return accessLevel.intValue();
	}

	/** Load Meta Data */
	protected POInfo initPO(Properties ctx) {
		POInfo poi = POInfo.getPOInfo(ctx, Table_ID, get_TrxName());
		return poi;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder("X_TC_CollectionDetails[").append(get_ID()).append(",Name=")
				.append(getName()).append("]");
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

	/**
	 * Set Description.
	 * 
	 * @param Description Optional short description of the record
	 */
	public void setDescription(String Description) {
		set_Value(COLUMNNAME_Description, Description);
	}

	/**
	 * Get Description.
	 * 
	 * @return Optional short description of the record
	 */
	public String getDescription() {
		return (String) get_Value(COLUMNNAME_Description);
	}

	/**
	 * Set Default.
	 * 
	 * @param IsDefault Default value
	 */
	public void setIsDefault(boolean IsDefault) {
		set_Value(COLUMNNAME_IsDefault, Boolean.valueOf(IsDefault));
	}

	/**
	 * Get Default.
	 * 
	 * @return Default value
	 */
	public boolean isDefault() {
		Object oo = get_Value(COLUMNNAME_IsDefault);
		if (oo != null) {
			if (oo instanceof Boolean)
				return ((Boolean) oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}

	/**
	 * Set Name.
	 * 
	 * @param Name Alphanumeric identifier of the entity
	 */
	public void setName(String Name) {
		set_Value(COLUMNNAME_Name, Name);
	}

	/**
	 * Get Name.
	 * 
	 * @return Alphanumeric identifier of the entity
	 */
	public String getName() {
		return (String) get_Value(COLUMNNAME_Name);
	}

	/**
	 * Set suckerno.
	 * 
	 * @param suckerno suckerno
	 */
	public void setsuckerno(int suckerno) {
		set_Value(COLUMNNAME_suckerno, Integer.valueOf(suckerno));
	}

	/**
	 * Get suckerno.
	 * 
	 * @return suckerno
	 */
	public int getsuckerno() {
		Integer ii = (Integer) get_Value(COLUMNNAME_suckerno);
		if (ii == null)
			return 0;
		return ii.intValue();
	}

	/**
	 * Set TC_CollectionDetails.
	 * 
	 * @param TC_CollectionDetails_ID TC_CollectionDetails
	 */
	public void setTC_CollectionDetails_ID(int TC_CollectionDetails_ID) {
		if (TC_CollectionDetails_ID < 1)
			set_ValueNoCheck(COLUMNNAME_TC_CollectionDetails_ID, null);
		else
			set_ValueNoCheck(COLUMNNAME_TC_CollectionDetails_ID, Integer.valueOf(TC_CollectionDetails_ID));
	}

	/**
	 * Get TC_CollectionDetails.
	 * 
	 * @return TC_CollectionDetails
	 */
	public int getTC_CollectionDetails_ID() {
		Integer ii = (Integer) get_Value(COLUMNNAME_TC_CollectionDetails_ID);
		if (ii == null)
			return 0;
		return ii.intValue();
	}

	public org.realmeds.tissue.moduller.I_TC_Farmer getTC_Farmer() throws RuntimeException {
		return (org.realmeds.tissue.moduller.I_TC_Farmer) MTable
				.get(getCtx(), org.realmeds.tissue.moduller.I_TC_Farmer.Table_ID)
				.getPO(getTC_Farmer_ID(), get_TrxName());
	}

	/**
	 * Set TC_Farmer.
	 * 
	 * @param TC_Farmer_ID TC_Farmer
	 */
	public void setTC_Farmer_ID(int TC_Farmer_ID) {
		if (TC_Farmer_ID < 1)
			set_ValueNoCheck(COLUMNNAME_TC_Farmer_ID, null);
		else
			set_ValueNoCheck(COLUMNNAME_TC_Farmer_ID, Integer.valueOf(TC_Farmer_ID));
	}

	/**
	 * Get TC_Farmer.
	 * 
	 * @return TC_Farmer
	 */
	public int getTC_Farmer_ID() {
		Integer ii = (Integer) get_Value(COLUMNNAME_TC_Farmer_ID);
		if (ii == null)
			return 0;
		return ii.intValue();
	}

	public org.realmeds.tissue.moduller.I_TC_FirstVisit getTC_FirstVisit() throws RuntimeException {
		return (org.realmeds.tissue.moduller.I_TC_FirstVisit) MTable
				.get(getCtx(), org.realmeds.tissue.moduller.I_TC_FirstVisit.Table_ID)
				.getPO(getTC_FirstVisit_ID(), get_TrxName());
	}

	/**
	 * Set TC_FirstVisit.
	 * 
	 * @param TC_FirstVisit_ID TC_FirstVisit
	 */
	public void setTC_FirstVisit_ID(int TC_FirstVisit_ID) {
		if (TC_FirstVisit_ID < 1)
			set_ValueNoCheck(COLUMNNAME_TC_FirstVisit_ID, null);
		else
			set_ValueNoCheck(COLUMNNAME_TC_FirstVisit_ID, Integer.valueOf(TC_FirstVisit_ID));
	}

	/**
	 * Get TC_FirstVisit.
	 * 
	 * @return TC_FirstVisit
	 */
	public int getTC_FirstVisit_ID() {
		Integer ii = (Integer) get_Value(COLUMNNAME_TC_FirstVisit_ID);
		if (ii == null)
			return 0;
		return ii.intValue();
	}

	public org.realmeds.tissue.moduller.I_TC_IntermediateVisit getTC_IntermediateVisit() throws RuntimeException {
		return (org.realmeds.tissue.moduller.I_TC_IntermediateVisit) MTable
				.get(getCtx(), org.realmeds.tissue.moduller.I_TC_IntermediateVisit.Table_ID)
				.getPO(getTC_IntermediateVisit_ID(), get_TrxName());
	}

	/**
	 * Set TC_IntermediateVisit.
	 * 
	 * @param TC_IntermediateVisit_ID TC_IntermediateVisit
	 */
	public void setTC_IntermediateVisit_ID(int TC_IntermediateVisit_ID) {
		if (TC_IntermediateVisit_ID < 1)
			set_ValueNoCheck(COLUMNNAME_TC_IntermediateVisit_ID, null);
		else
			set_ValueNoCheck(COLUMNNAME_TC_IntermediateVisit_ID, Integer.valueOf(TC_IntermediateVisit_ID));
	}

	/**
	 * Get TC_IntermediateVisit.
	 * 
	 * @return TC_IntermediateVisit
	 */
	public int getTC_IntermediateVisit_ID() {
		Integer ii = (Integer) get_Value(COLUMNNAME_TC_IntermediateVisit_ID);
		if (ii == null)
			return 0;
		return ii.intValue();
	}

	public org.realmeds.tissue.moduller.I_TC_PlantDetails getTC_PlantDetails() throws RuntimeException {
		return (org.realmeds.tissue.moduller.I_TC_PlantDetails) MTable
				.get(getCtx(), org.realmeds.tissue.moduller.I_TC_PlantDetails.Table_ID)
				.getPO(getTC_PlantDetails_ID(), get_TrxName());
	}

	/**
	 * Set TC_PlantDetails.
	 * 
	 * @param TC_PlantDetails_ID TC_PlantDetails
	 */
	public void setTC_PlantDetails_ID(int TC_PlantDetails_ID) {
		if (TC_PlantDetails_ID < 1)
			set_ValueNoCheck(COLUMNNAME_TC_PlantDetails_ID, null);
		else
			set_ValueNoCheck(COLUMNNAME_TC_PlantDetails_ID, Integer.valueOf(TC_PlantDetails_ID));
	}

	/**
	 * Get TC_PlantDetails.
	 * 
	 * @return TC_PlantDetails
	 */
	public int getTC_PlantDetails_ID() {
		Integer ii = (Integer) get_Value(COLUMNNAME_TC_PlantDetails_ID);
		if (ii == null)
			return 0;
		return ii.intValue();
	}

	public org.realmeds.tissue.moduller.I_TC_Visit getTC_Visit() throws RuntimeException {
		return (org.realmeds.tissue.moduller.I_TC_Visit) MTable
				.get(getCtx(), org.realmeds.tissue.moduller.I_TC_Visit.Table_ID).getPO(getTC_Visit_ID(), get_TrxName());
	}

	/**
	 * Set TC_Visit.
	 * 
	 * @param TC_Visit_ID TC_Visit
	 */
	public void setTC_Visit_ID(int TC_Visit_ID) {
		if (TC_Visit_ID < 1)
			set_ValueNoCheck(COLUMNNAME_TC_Visit_ID, null);
		else
			set_ValueNoCheck(COLUMNNAME_TC_Visit_ID, Integer.valueOf(TC_Visit_ID));
	}

	/**
	 * Get TC_Visit.
	 * 
	 * @return TC_Visit
	 */
	public int getTC_Visit_ID() {
		Integer ii = (Integer) get_Value(COLUMNNAME_TC_Visit_ID);
		if (ii == null)
			return 0;
		return ii.intValue();
	}

	/**
	 * Set Search Key.
	 * 
	 * @param Value Search key for the record in the format required - must be
	 *              unique
	 */
	public void setValue(String Value) {
		set_Value(COLUMNNAME_Value, Value);
	}

	/**
	 * Get Search Key.
	 * 
	 * @return Search key for the record in the format required - must be unique
	 */
	public String getValue() {
		return (String) get_Value(COLUMNNAME_Value);
	}

	/**
	 * Set yieldweight.
	 * 
	 * @param yieldweight yieldweight
	 */
	public void setyieldweight(String yieldweight) {
		set_Value(COLUMNNAME_yieldweight, yieldweight);
	}

	/**
	 * Get yieldweight.
	 * 
	 * @return yieldweight
	 */
	public String getyieldweight() {
		return (String) get_Value(COLUMNNAME_yieldweight);
	}

	/**
	 * Set issuckercollectcollection.
	 * 
	 * @param issuckercollectcollection issuckercollectcollection
	 */
	public void setissuckercollectcollection(boolean issuckercollectcollection) {
		set_Value(COLUMNNAME_issuckercollectcollection, Boolean.valueOf(issuckercollectcollection));
	}

	/**
	 * Get issuckercollectcollection.
	 * 
	 * @return issuckercollectcollection
	 */
	public boolean issuckercollectcollection() {
		Object oo = get_Value(COLUMNNAME_issuckercollectcollection);
		if (oo != null) {
			if (oo instanceof Boolean)
				return ((Boolean) oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}
}