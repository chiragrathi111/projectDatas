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
package org.realmeds.tissue.moduller;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.compiere.model.*;
import org.compiere.util.KeyNamePair;

/**
 * Generated Interface for TC_Farmer
 * 
 * @author iDempiere (generated)
 * @version Release 10
 */
@SuppressWarnings("all")
public interface I_TC_Farmer {

	/** TableName=TC_Farmer */
	public static final String Table_Name = "TC_Farmer";

	/** AD_Table_ID=1000001 */
	public static final int Table_ID = MTable.getTable_ID(Table_Name);

	KeyNamePair Model = new KeyNamePair(Table_ID, Table_Name);

	/**
	 * AccessLevel = 3 - Client - Org
	 */
	BigDecimal accessLevel = BigDecimal.valueOf(3);

	/** Load Meta Data */

	/** Column name AD_Client_ID */
	public static final String COLUMNNAME_AD_Client_ID = "AD_Client_ID";

	/**
	 * Get Tenant. Tenant for this installation.
	 */
	public int getAD_Client_ID();

	/** Column name Address */
	public static final String COLUMNNAME_Address = "Address";

	/** Set Address */
	public void setAddress(String Address);

	/** Get Address */
	public String getAddress();

	/** Column name AD_Org_ID */
	public static final String COLUMNNAME_AD_Org_ID = "AD_Org_ID";

	/**
	 * Set Organization. Organizational entity within tenant
	 */
	public void setAD_Org_ID(int AD_Org_ID);

	/**
	 * Get Organization. Organizational entity within tenant
	 */
	public int getAD_Org_ID();

	/** Column name Created */
	public static final String COLUMNNAME_Created = "Created";

	/**
	 * Get Created. Date this record was created
	 */
	public Timestamp getCreated();

	/** Column name CreatedBy */
	public static final String COLUMNNAME_CreatedBy = "CreatedBy";

	/**
	 * Get Created By. User who created this records
	 */
	public int getCreatedBy();

	/** Column name Description */
	public static final String COLUMNNAME_Description = "Description";

	/**
	 * Set Description. Optional short description of the record
	 */
	public void setDescription(String Description);

	/**
	 * Get Description. Optional short description of the record
	 */
	public String getDescription();

	/** Column name IsActive */
	public static final String COLUMNNAME_IsActive = "IsActive";

	/**
	 * Set Active. The record is active in the system
	 */
	public void setIsActive(boolean IsActive);

	/**
	 * Get Active. The record is active in the system
	 */
	public boolean isActive();

	/** Column name IsDefault */
	public static final String COLUMNNAME_IsDefault = "IsDefault";

	/**
	 * Set Default. Default value
	 */
	public void setIsDefault(boolean IsDefault);

	/**
	 * Get Default. Default value
	 */
	public boolean isDefault();

	/** Column name landmark */
	public static final String COLUMNNAME_landmark = "landmark";

	/** Set landmark */
	public void setlandmark(String landmark);

	/** Get landmark */
	public String getlandmark();

	/** Column name mobileno */
	public static final String COLUMNNAME_mobileno = "mobileno";

	/** Set mobileno */
	public void setmobileno(String mobileno);

	/** Get mobileno */
	public String getmobileno();

	/** Column name Name */
	public static final String COLUMNNAME_Name = "Name";

	/**
	 * Set Name. Alphanumeric identifier of the entity
	 */
	public void setName(String Name);

	/**
	 * Get Name. Alphanumeric identifier of the entity
	 */
	public String getName();

	/** Column name surveyno */
	public static final String COLUMNNAME_surveyno = "surveyno";

	/** Set surveyno */
	public void setsurveyno(String surveyno);

	/** Get surveyno */
	public String getsurveyno();

	/** Column name TC_Farmer_ID */
	public static final String COLUMNNAME_TC_Farmer_ID = "TC_Farmer_ID";

	/** Set TC_Farmer */
	public void setTC_Farmer_ID(int TC_Farmer_ID);

	/** Get TC_Farmer */
	public int getTC_Farmer_ID();

	/** Column name Updated */
	public static final String COLUMNNAME_Updated = "Updated";

	/**
	 * Get Updated. Date this record was updated
	 */
	public Timestamp getUpdated();

	/** Column name UpdatedBy */
	public static final String COLUMNNAME_UpdatedBy = "UpdatedBy";

	/**
	 * Get Updated By. User who updated this records
	 */
	public int getUpdatedBy();

	/** Column name Value */
	public static final String COLUMNNAME_Value = "Value";

	/**
	 * Set Search Key. Search key for the record in the format required - must be
	 * unique
	 */
	public void setValue(String Value);

	/**
	 * Get Search Key. Search key for the record in the format required - must be
	 * unique
	 */
	public String getValue();

	/** Column name villagename */
	public static final String COLUMNNAME_villagename = "villagename";

	/** Set villagename */
	public void setvillagename(String villagename);

	/** Get villagename */
	public String getvillagename();

	/** Column name City */
	public static final String COLUMNNAME_City = "City";

	/**
	 * Set City. Identifies a City
	 */
	public void setCity(String City);

	/**
	 * Get City. Identifies a City
	 */
	public String getCity();

	/** Column name district */
	public static final String COLUMNNAME_district = "district";

	/** Set district */
	public void setdistrict(String district);

	/** Get district */
	public String getdistrict();

	/** Column name pincode */
	public static final String COLUMNNAME_pincode = "pincode";

	/** Set pincode */
	public void setpincode(String pincode);

	/** Get pincode */
	public String getpincode();

	/** Column name state */
	public static final String COLUMNNAME_state = "state";

	/** Set state */
	public void setstate(String state);

	/** Get state */
	public String getstate();

	public static final String COLUMNNAME_talukname = "talukname";

	/** Set talukname */
	public void settalukname(String talukname);

	/** Get talukname */
	public String gettalukname();

	/** Column name villagename2 */
	public static final String COLUMNNAME_villagename2 = "villagename2";

	/** Set villagename2 */
	public void setvillagename2(String villagename2);

	/** Get villagename2 */
	public String getvillagename2();
	
	/** Column name latitude */
    public static final String COLUMNNAME_latitude = "latitude";

	/** Set latitude	  */
	public void setlatitude (String latitude);

	/** Get latitude	  */
	public String getlatitude();

    /** Column name longitude */
    public static final String COLUMNNAME_longitude = "longitude";

	/** Set longitude	  */
	public void setlongitude (String longitude);

	/** Get longitude	  */
	public String getlongitude();
}
