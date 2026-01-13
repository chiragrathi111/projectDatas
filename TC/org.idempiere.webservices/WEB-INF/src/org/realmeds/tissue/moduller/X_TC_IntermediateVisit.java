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

/**
 * Generated Model for TC_IntermediateVisit
 * 
 * @author iDempiere (generated)
 * @version Release 10 - $Id$
 */
@org.adempiere.base.Model(table = "TC_IntermediateVisit")
public class X_TC_IntermediateVisit extends PO implements I_TC_IntermediateVisit, I_Persistent {

	/**
	 *
	 */
	private static final long serialVersionUID = 20240304L;

	/** Standard Constructor */
	public X_TC_IntermediateVisit(Properties ctx, int TC_IntermediateVisit_ID, String trxName) {
		super(ctx, TC_IntermediateVisit_ID, trxName);
		/**
		 * if (TC_IntermediateVisit_ID == 0) { setIsDefault (false); setName (null);
		 * setTC_IntermediateVisit_ID (0); }
		 */
	}

	/** Standard Constructor */
	public X_TC_IntermediateVisit(Properties ctx, int TC_IntermediateVisit_ID, String trxName,
			String... virtualColumns) {
		super(ctx, TC_IntermediateVisit_ID, trxName, virtualColumns);
		/**
		 * if (TC_IntermediateVisit_ID == 0) { setIsDefault (false); setName (null);
		 * setTC_IntermediateVisit_ID (0); }
		 */
	}

	/** Load Constructor */
	public X_TC_IntermediateVisit(Properties ctx, ResultSet rs, String trxName) {
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
		StringBuilder sb = new StringBuilder("X_TC_IntermediateVisit[").append(get_ID()).append(",Name=")
				.append(getName()).append("]");
		return sb.toString();
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
	 * Set Next Visit Date.
	 * 
	 * @param nextvisitdate Next Visit Date
	 */
	public void setnextvisitdate(Timestamp nextvisitdate) {
		set_Value(COLUMNNAME_nextvisitdate, nextvisitdate);
	}

	/**
	 * Get Next Visit Date.
	 * 
	 * @return Next Visit Date
	 */
	public Timestamp getnextvisitdate() {
		return (Timestamp) get_Value(COLUMNNAME_nextvisitdate);
	}

	/**
	 * Set Reason Details.
	 * 
	 * @param reasondetails Reason Details
	 */
	public void setreasondetails(String reasondetails) {
		set_Value(COLUMNNAME_reasondetails, reasondetails);
	}

	/**
	 * Get Reason Details.
	 * 
	 * @return Reason Details
	 */
	public String getreasondetails() {
		return (String) get_Value(COLUMNNAME_reasondetails);
	}

	/**
	 * Set Review Details.
	 * 
	 * @param reviewdetails Review Details
	 */
	public void setreviewdetails(String reviewdetails) {
		set_Value(COLUMNNAME_reviewdetails, reviewdetails);
	}

	/**
	 * Get Review Details.
	 * 
	 * @return Review Details
	 */
	public String getreviewdetails() {
		return (String) get_Value(COLUMNNAME_reviewdetails);
	}

	public org.realmeds.tissue.moduller.I_TC_Decision getTC_Decision() throws RuntimeException {
		return (org.realmeds.tissue.moduller.I_TC_Decision) MTable
				.get(getCtx(), org.realmeds.tissue.moduller.I_TC_Decision.Table_ID)
				.getPO(getTC_Decision_ID(), get_TrxName());
	}

	/**
	 * Set Decision.
	 * 
	 * @param TC_Decision_ID Decision
	 */
	public void setTC_Decision_ID(int TC_Decision_ID) {
		if (TC_Decision_ID < 1)
			set_ValueNoCheck(COLUMNNAME_TC_Decision_ID, null);
		else
			set_ValueNoCheck(COLUMNNAME_TC_Decision_ID, Integer.valueOf(TC_Decision_ID));
	}

	/**
	 * Get Decision.
	 * 
	 * @return Decision
	 */
	public int getTC_Decision_ID() {
		Integer ii = (Integer) get_Value(COLUMNNAME_TC_Decision_ID);
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
	 * Set TC Farmer.
	 * 
	 * @param TC_Farmer_ID TC Farmer
	 */
	public void setTC_Farmer_ID(int TC_Farmer_ID) {
		if (TC_Farmer_ID < 1)
			set_ValueNoCheck(COLUMNNAME_TC_Farmer_ID, null);
		else
			set_ValueNoCheck(COLUMNNAME_TC_Farmer_ID, Integer.valueOf(TC_Farmer_ID));
	}

	/**
	 * Get TC Farmer.
	 * 
	 * @return TC Farmer
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
	 * Set TC First Visit.
	 * 
	 * @param TC_FirstVisit_ID TC First Visit
	 */
	public void setTC_FirstVisit_ID(int TC_FirstVisit_ID) {
		if (TC_FirstVisit_ID < 1)
			set_ValueNoCheck(COLUMNNAME_TC_FirstVisit_ID, null);
		else
			set_ValueNoCheck(COLUMNNAME_TC_FirstVisit_ID, Integer.valueOf(TC_FirstVisit_ID));
	}

	/**
	 * Get TC First Visit.
	 * 
	 * @return TC First Visit
	 */
	public int getTC_FirstVisit_ID() {
		Integer ii = (Integer) get_Value(COLUMNNAME_TC_FirstVisit_ID);
		if (ii == null)
			return 0;
		return ii.intValue();
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
	 * Set TC Plant Details.
	 * 
	 * @param TC_PlantDetails_ID TC Plant Details
	 */
	public void setTC_PlantDetails_ID(int TC_PlantDetails_ID) {
		if (TC_PlantDetails_ID < 1)
			set_ValueNoCheck(COLUMNNAME_TC_PlantDetails_ID, null);
		else
			set_ValueNoCheck(COLUMNNAME_TC_PlantDetails_ID, Integer.valueOf(TC_PlantDetails_ID));
	}

	/**
	 * Get TC Plant Details.
	 * 
	 * @return TC Plant Details
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
	 * Set TC Visit.
	 * 
	 * @param TC_Visit_ID TC Visit
	 */
	public void setTC_Visit_ID(int TC_Visit_ID) {
		if (TC_Visit_ID < 1)
			set_ValueNoCheck(COLUMNNAME_TC_Visit_ID, null);
		else
			set_ValueNoCheck(COLUMNNAME_TC_Visit_ID, Integer.valueOf(TC_Visit_ID));
	}

	/**
	 * Get TC Visit.
	 * 
	 * @return TC Visit
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
	 * Set isattachedintermediatetocollection.
	 * 
	 * @param isattachedintermediatetocollec isattachedintermediatetocollection
	 */
	public void setisattachedocollection(boolean isattachedocollection) {
		set_Value(COLUMNNAME_isattachedocollection, Boolean.valueOf(isattachedocollection));
	}

	/**
	 * Get isattachedintermediatetocollection.
	 * 
	 * @return isattachedintermediatetocollection
	 */
	public boolean isattachedocollection() {
		Object oo = get_Value(COLUMNNAME_isattachedocollection);
		if (oo != null) {
			if (oo instanceof Boolean)
				return ((Boolean) oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}
}