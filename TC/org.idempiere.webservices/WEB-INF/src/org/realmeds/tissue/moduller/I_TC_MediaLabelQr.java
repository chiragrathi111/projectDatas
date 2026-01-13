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

/** Generated Interface for TC_MediaLabelQr
 *  @author iDempiere (generated) 
 *  @version Release 10
 */
@SuppressWarnings("all")
public interface I_TC_MediaLabelQr 
{

    /** TableName=TC_MediaLabelQr */
    public static final String Table_Name = "TC_MediaLabelQr";

    /** AD_Table_ID=1000053 */
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
	
	/** Column name c_uuid */
    public static final String COLUMNNAME_c_uuid = "c_uuid";

	/** Set c_uuid	  */
	public void setc_uuid (String c_uuid);

	/** Get c_uuid	  */
	public String getc_uuid();
	
	/** Column name personalcode2 */
    public static final String COLUMNNAME_personalcode2 = "personalcode2";
	
	/** Set personalcode2	  */
	public void setpersonalcode2 (String personalcode2);

	/** Get personalcode2	  */
	public String getpersonalcode2();

	/** Column name tcpf2 */
    public static final String COLUMNNAME_tcpf2 = "tcpf2";

	/** Set tcpf2	  */
	public void settcpf2 (String tcpf2);

	/** Get tcpf2	  */
	public String gettcpf2();
	
	/** Column name discarddate */
    public static final String COLUMNNAME_discarddate = "discarddate";

	/** Set discarddate	  */
	public void setdiscarddate (Timestamp discarddate);

	/** Get discarddate	  */
	public Timestamp getdiscarddate();

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
	
	/** Column name discardreason */
    public static final String COLUMNNAME_discardreason = "discardreason";

	/** Set discardreason	  */
	public void setdiscardreason (String discardreason);

	/** Get discardreason	  */
	public String getdiscardreason();

    /** Column name Created */
    public static final String COLUMNNAME_Created = "Created";

	/** Get Created.
	  * Date this record was created
	  */
	public Timestamp getCreated();
	
	/** Column name TC_MediaDiscardType_ID */
    public static final String COLUMNNAME_TC_MediaDiscardType_ID = "TC_MediaDiscardType_ID";

	/** Set TC_MediaDiscardType	  */
	public void setTC_MediaDiscardType_ID (int TC_MediaDiscardType_ID);

	/** Get TC_MediaDiscardType	  */
	public int getTC_MediaDiscardType_ID();

	public org.realmeds.tissue.moduller.I_TC_MediaDiscardType getTC_MediaDiscardType() throws RuntimeException;

    /** Column name CreatedBy */
    public static final String COLUMNNAME_CreatedBy = "CreatedBy";

	/** Get Created By.
	  * User who created this records
	  */
	public int getCreatedBy();

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

    /** Column name operationdate */
    public static final String COLUMNNAME_operationdate = "operationdate";

	/** Set operationdate	  */
	public void setoperationdate (Timestamp operationdate);

	/** Get operationdate	  */
	public Timestamp getoperationdate();

    /** Column name personalcode */
    public static final String COLUMNNAME_personalcode = "personalcode";

	/** Set personalcode	  */
	public void setpersonalcode (String personalcode);

	/** Get personalcode	  */
	public String getpersonalcode();
	
	/** Column name isDiscarded */
    public static final String COLUMNNAME_isDiscarded = "isDiscarded";

	/** Set Discard	  */
	public void setisDiscarded (boolean isDiscarded);

	/** Get Discard	  */
	public boolean isDiscarded();

    /** Column name TC_MachineType_ID */
    public static final String COLUMNNAME_TC_MachineType_ID = "TC_MachineType_ID";

	/** Set TC_MachineType	  */
	public void setTC_MachineType_ID (int TC_MachineType_ID);

	/** Get TC_MachineType	  */
	public int getTC_MachineType_ID();

	public org.realmeds.tissue.moduller.I_TC_MachineType getTC_MachineType() throws RuntimeException;

    /** Column name TC_mediaLabelQr_ID */
    public static final String COLUMNNAME_TC_mediaLabelQr_ID = "TC_mediaLabelQr_ID";

	/** Set TC_mediaLabelQr	  */
	public void setTC_mediaLabelQr_ID (int TC_mediaLabelQr_ID);

	/** Get TC_mediaLabelQr	  */
	public int getTC_mediaLabelQr_ID();

    /** Column name TC_mediaLabelQr_UU */
    public static final String COLUMNNAME_TC_mediaLabelQr_UU = "TC_mediaLabelQr_UU";

	/** Set TC_mediaLabelQr_UU	  */
	public void setTC_mediaLabelQr_UU (String TC_mediaLabelQr_UU);

	/** Get TC_mediaLabelQr_UU	  */
	public String getTC_mediaLabelQr_UU();

    /** Column name TC_MediaLine_ID */
    public static final String COLUMNNAME_TC_MediaLine_ID = "TC_MediaLine_ID";

	/** Set TC_MediaLine	  */
	public void setTC_MediaLine_ID (int TC_MediaLine_ID);

	/** Get TC_MediaLine	  */
	public int getTC_MediaLine_ID();

	public org.realmeds.tissue.moduller.I_TC_MediaLine getTC_MediaLine() throws RuntimeException;

    /** Column name TC_MediaType_ID */
    public static final String COLUMNNAME_TC_MediaType_ID = "TC_MediaType_ID";

	/** Set TC_MediaType	  */
	public void setTC_MediaType_ID (int TC_MediaType_ID);

	/** Get TC_MediaType	  */
	public int getTC_MediaType_ID();

	public org.realmeds.tissue.moduller.I_TC_MediaType getTC_MediaType() throws RuntimeException;

    /** Column name tcpf */
    public static final String COLUMNNAME_tcpf = "tcpf";

	/** Set tcpf	  */
	public void settcpf (String tcpf);

	/** Get tcpf	  */
	public String gettcpf();

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
}
