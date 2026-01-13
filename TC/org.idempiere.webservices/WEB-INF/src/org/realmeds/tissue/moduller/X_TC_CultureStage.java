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
 * Generated Model for TC_CultureStage
 * 
 * @author iDempiere (generated)
 * @version Release 10 - $Id$
 */
@org.adempiere.base.Model(table = "TC_CultureStage")
public class X_TC_CultureStage extends PO implements I_TC_CultureStage, I_Persistent {

	/**
	 *
	 */
	private static final long serialVersionUID = 20240304L;

	/** Standard Constructor */
	public X_TC_CultureStage(Properties ctx, int TC_CultureStage_ID, String trxName) {
		super(ctx, TC_CultureStage_ID, trxName);
		/**
		 * if (TC_CultureStage_ID == 0) { setIsDefault (false); setName (null);
		 * setTC_CultureStage_ID (0); }
		 */
	}

	/** Standard Constructor */
	public X_TC_CultureStage(Properties ctx, int TC_CultureStage_ID, String trxName, String... virtualColumns) {
		super(ctx, TC_CultureStage_ID, trxName, virtualColumns);
		/**
		 * if (TC_CultureStage_ID == 0) { setIsDefault (false); setName (null);
		 * setTC_CultureStage_ID (0); }
		 */
	}

	/** Load Constructor */
	public X_TC_CultureStage(Properties ctx, ResultSet rs, String trxName) {
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
		StringBuilder sb = new StringBuilder("X_TC_CultureStage[").append(get_ID()).append(",Name=").append(getName())
				.append("]");
		return sb.toString();
	}

	/**
	 * Set codeno.
	 * 
	 * @param codeno codeno
	 */
	public void setcodeno(String codeno) {
		set_Value(COLUMNNAME_codeno, codeno);
	}

	/**
	 * Get codeno.
	 * 
	 * @return codeno
	 */
	public String getcodeno() {
		return (String) get_Value(COLUMNNAME_codeno);
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
	 * Set period.
	 * 
	 * @param period period
	 */
	public void setperiod(String period) {
		set_Value(COLUMNNAME_period, period);
	}

	/**
	 * Get period.
	 * 
	 * @return period
	 */
	public String getperiod() {
		return (String) get_Value(COLUMNNAME_period);
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
	 * Set TC_CultureStage.
	 * 
	 * @param TC_CultureStage_ID TC_CultureStage
	 */
	public void setTC_CultureStage_ID(int TC_CultureStage_ID) {
		if (TC_CultureStage_ID < 1)
			set_ValueNoCheck(COLUMNNAME_TC_CultureStage_ID, null);
		else
			set_ValueNoCheck(COLUMNNAME_TC_CultureStage_ID, Integer.valueOf(TC_CultureStage_ID));
	}

	/**
	 * Get TC_CultureStage.
	 * 
	 * @return TC_CultureStage
	 */
	public int getTC_CultureStage_ID() {
		Integer ii = (Integer) get_Value(COLUMNNAME_TC_CultureStage_ID);
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
}