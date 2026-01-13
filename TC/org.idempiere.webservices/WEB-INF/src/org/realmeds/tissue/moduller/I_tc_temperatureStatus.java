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

/** Generated Interface for tc_temperatureStatus
 *  @author iDempiere (generated) 
 *  @version Release 10
 */
@SuppressWarnings("all")
public interface I_tc_temperatureStatus 
{

    /** TableName=tc_temperatureStatus */
    public static final String Table_Name = "tc_temperatureStatus";

    /** AD_Table_ID=1000061 */
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

    /** Column name Humidity */
    public static final String COLUMNNAME_Humidity = "Humidity";

	/** Set Humidity	  */
	public void setHumidity (String Humidity);

	/** Get Humidity	  */
	public String getHumidity();

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
	
	/** Column name TC_devicedata_ID */
    public static final String COLUMNNAME_TC_devicedata_ID = "TC_devicedata_ID";

	/** Set TC_devicedata	  */
	public void setTC_devicedata_ID (int TC_devicedata_ID);

	/** Get TC_devicedata	  */
	public int getTC_devicedata_ID();

	public org.realmeds.tissue.moduller.I_TC_devicedata getTC_devicedata() throws RuntimeException;

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

    /** Column name tc_temperatureStatus_ID */
    public static final String COLUMNNAME_tc_temperatureStatus_ID = "tc_temperatureStatus_ID";

	/** Set tc_temperatureStatus	  */
	public void settc_temperatureStatus_ID (int tc_temperatureStatus_ID);

	/** Get tc_temperatureStatus	  */
	public int gettc_temperatureStatus_ID();

    /** Column name tc_temperatureStatus_UU */
    public static final String COLUMNNAME_tc_temperatureStatus_UU = "tc_temperatureStatus_UU";

	/** Set tc_temperatureStatus_UU	  */
	public void settc_temperatureStatus_UU (String tc_temperatureStatus_UU);

	/** Get tc_temperatureStatus_UU	  */
	public String gettc_temperatureStatus_UU();

    /** Column name tc_tempstatus_ID */
    public static final String COLUMNNAME_tc_tempstatus_ID = "tc_tempstatus_ID";

	/** Set tc_tempstatus	  */
	public void settc_tempstatus_ID (int tc_tempstatus_ID);

	/** Get tc_tempstatus	  */
	public int gettc_tempstatus_ID();

	public org.realmeds.tissue.moduller.I_tc_tempstatus gettc_tempstatus() throws RuntimeException;

    /** Column name Temperature */
    public static final String COLUMNNAME_Temperature = "Temperature";

	/** Set Temperature	  */
	public void setTemperature (String Temperature);

	/** Get Temperature	  */
	public String getTemperature();

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
	
	/** Column name humiditystatus */
    public static final String COLUMNNAME_humiditystatus = "humiditystatus";

	/** Set humiditystatus	  */
	public void sethumiditystatus (String humiditystatus);

	/** Get humiditystatus	  */
	public String gethumiditystatus();
	
	/** Column name battery_percentage */
    public static final String COLUMNNAME_battery_percentage = "battery_percentage";

	/** Set battery_percentage	  */
	public void setbattery_percentage (String battery_percentage);

	/** Get battery_percentage	  */
	public String getbattery_percentage();
	
	/** Column name custom_timestamp */
    public static final String COLUMNNAME_custom_timestamp = "custom_timestamp";

	/** Set custom_timestamp	  */
	public void setcustom_timestamp (Timestamp custom_timestamp);

	/** Get custom_timestamp	  */
	public Timestamp getcustom_timestamp();
}
