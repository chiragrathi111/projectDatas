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

/** Generated Interface for TC_devicedata
 *  @author iDempiere (generated) 
 *  @version Release 10
 */
@SuppressWarnings("all")
public interface I_TC_devicedata 
{

    /** TableName=TC_devicedata */
    public static final String Table_Name = "TC_devicedata";

    /** AD_Table_ID=1000075 */
    public static final int Table_ID = MTable.getTable_ID(Table_Name);

    KeyNamePair Model = new KeyNamePair(Table_ID, Table_Name);

    /** AccessLevel = 3 - Client - Org 
     */
    BigDecimal accessLevel = BigDecimal.valueOf(3);

    /** Load Meta Data */

    /** Column name AD_Client_ID */
    public static final String COLUMNNAME_AD_Client_ID = "AD_Client_ID";

	/** Get Tenant.
	  * Tenant for this installation.
	  */
	public int getAD_Client_ID();

    /** Column name AD_Org_ID */
    public static final String COLUMNNAME_AD_Org_ID = "AD_Org_ID";

	/** Set Organization.
	  * Organizational entity within tenant
	  */
	public void setAD_Org_ID (int AD_Org_ID);

	/** Get Organization.
	  * Organizational entity within tenant
	  */
	public int getAD_Org_ID();

    /** Column name Created */
    public static final String COLUMNNAME_Created = "Created";

	/** Get Created.
	  * Date this record was created
	  */
	public Timestamp getCreated();

    /** Column name CreatedBy */
    public static final String COLUMNNAME_CreatedBy = "CreatedBy";

	/** Get Created By.
	  * User who created this records
	  */
	public int getCreatedBy();
	
	/** Column name Frequency */
    public static final String COLUMNNAME_Frequency = "Frequency";

	/** Set Frequency.
	  * Frequency of events
	  */
	public void setFrequency (String Frequency);

	/** Get Frequency.
	  * Frequency of events
	  */
	public String getFrequency();

    /** Column name c_uuid */
    public static final String COLUMNNAME_c_uuid = "c_uuid";

	/** Set c_uuid	  */
	public void setc_uuid (String c_uuid);

	/** Get c_uuid	  */
	public String getc_uuid();

    /** Column name Description */
    public static final String COLUMNNAME_Description = "Description";

	/** Set Description.
	  * Optional short description of the record
	  */
	public void setDescription (String Description);

	/** Get Description.
	  * Optional short description of the record
	  */
	public String getDescription();

    /** Column name deviceid */
    public static final String COLUMNNAME_deviceid = "deviceid";

	/** Set deviceid	  */
	public void setdeviceid (String deviceid);

	/** Get deviceid	  */
	public String getdeviceid();

    /** Column name IsActive */
    public static final String COLUMNNAME_IsActive = "IsActive";

	/** Set Active.
	  * The record is active in the system
	  */
	public void setIsActive (boolean IsActive);

	/** Get Active.
	  * The record is active in the system
	  */
	public boolean isActive();

    /** Column name M_LocatorType_ID */
    public static final String COLUMNNAME_M_LocatorType_ID = "M_LocatorType_ID";

	/** Set Locator Type	  */
	public void setM_LocatorType_ID (int M_LocatorType_ID);

	/** Get Locator Type	  */
	public int getM_LocatorType_ID();

	public org.compiere.model.I_M_LocatorType getM_LocatorType() throws RuntimeException;

    /** Column name Name */
    public static final String COLUMNNAME_Name = "Name";

	/** Set Name.
	  * Alphanumeric identifier of the entity
	  */
	public void setName (String Name);

	/** Get Name.
	  * Alphanumeric identifier of the entity
	  */
	public String getName();

    /** Column name TC_devicedata_ID */
    public static final String COLUMNNAME_TC_devicedata_ID = "TC_devicedata_ID";

	/** Set TC_devicedata	  */
	public void setTC_devicedata_ID (int TC_devicedata_ID);

	/** Get TC_devicedata	  */
	public int getTC_devicedata_ID();

    /** Column name TC_devicedata_UU */
    public static final String COLUMNNAME_TC_devicedata_UU = "TC_devicedata_UU";

	/** Set TC_devicedata_UU	  */
	public void setTC_devicedata_UU (String TC_devicedata_UU);

	/** Get TC_devicedata_UU	  */
	public String getTC_devicedata_UU();

    /** Column name TC_temperatureposition_ID */
    public static final String COLUMNNAME_TC_temperatureposition_ID = "TC_temperatureposition_ID";

	/** Set TC_temperatureposition	  */
	public void setTC_temperatureposition_ID (int TC_temperatureposition_ID);

	/** Get TC_temperatureposition	  */
	public int getTC_temperatureposition_ID();

	public org.realmeds.tissue.moduller.I_TC_temperatureposition getTC_temperatureposition() throws RuntimeException;

    /** Column name Updated */
    public static final String COLUMNNAME_Updated = "Updated";

	/** Get Updated.
	  * Date this record was updated
	  */
	public Timestamp getUpdated();

    /** Column name UpdatedBy */
    public static final String COLUMNNAME_UpdatedBy = "UpdatedBy";

	/** Get Updated By.
	  * User who updated this records
	  */
	public int getUpdatedBy();
	
	/** Column name TC_SensorType_ID */
    public static final String COLUMNNAME_TC_SensorType_ID = "TC_SensorType_ID";

	/** Set TC_SensorType	  */
	public void setTC_SensorType_ID (int TC_SensorType_ID);

	/** Get TC_SensorType	  */
	public int getTC_SensorType_ID();

	public org.realmeds.tissue.moduller.I_TC_SensorType getTC_SensorType() throws RuntimeException;
	
	public static final String COLUMNNAME_Value = "Value";

	/** Set Search Key.
	  * Search key for the record in the format required - must be unique
	  */
	public void setValue (String Value);

	/** Get Search Key.
	  * Search key for the record in the format required - must be unique
	  */
	public String getValue();

//    /** Column name Value */
//    public static final String COLUMNNAME_Value = "Value";
//
//	/** Set Search Key.
//	  * Search key for the record in the format required - must be unique
//	  */
//	public void setValue (String Value);
//
//	/** Get Search Key.
//	  * Search key for the record in the format required - must be unique
//	  */
//	public String getValue();
}
