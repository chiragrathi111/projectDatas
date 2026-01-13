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
 * Generated Model for TC_MediaLabelQr
 * 
 * @author iDempiere (generated)
 * @version Release 10 - $Id$
 */
@org.adempiere.base.Model(table = "TC_MediaLabelQr")
public class X_TC_MediaLabelQr extends PO implements I_TC_MediaLabelQr, I_Persistent {

	/**
	 *
	 */
	private static final long serialVersionUID = 20240419L;

	/** Standard Constructor */
	public X_TC_MediaLabelQr(Properties ctx, int TC_MediaLabelQr_ID, String trxName) {
		super(ctx, TC_MediaLabelQr_ID, trxName);
		/**
		 * if (TC_MediaLabelQr_ID == 0) { setTC_mediaLabelQr_ID (0); }
		 */
	}

	/** Standard Constructor */
	public X_TC_MediaLabelQr(Properties ctx, int TC_MediaLabelQr_ID, String trxName, String... virtualColumns) {
		super(ctx, TC_MediaLabelQr_ID, trxName, virtualColumns);
		/**
		 * if (TC_MediaLabelQr_ID == 0) { setTC_mediaLabelQr_ID (0); }
		 */
	}

	/** Load Constructor */
	public X_TC_MediaLabelQr(Properties ctx, ResultSet rs, String trxName) {
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
		StringBuilder sb = new StringBuilder("X_TC_MediaLabelQr[").append(get_ID()).append("]");
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
	 * Set discarddate.
	 * 
	 * @param discarddate discarddate
	 */
	public void setdiscarddate(Timestamp discarddate) {
		set_Value(COLUMNNAME_discarddate, discarddate);
	}

	/**
	 * Get discarddate.
	 * 
	 * @return discarddate
	 */
	public Timestamp getdiscarddate() {
		return (Timestamp) get_Value(COLUMNNAME_discarddate);
	}

	/**
	 * Set personalcode2.
	 * 
	 * @param personalcode2 personalcode2
	 */
	public void setpersonalcode2(String personalcode2) {
		set_Value(COLUMNNAME_personalcode2, personalcode2);
	}

	/**
	 * Get personalcode2.
	 * 
	 * @return personalcode2
	 */
	public String getpersonalcode2() {
		return (String) get_Value(COLUMNNAME_personalcode2);
	}
	
	public org.realmeds.tissue.moduller.I_TC_MediaDiscardType getTC_MediaDiscardType() throws RuntimeException
	{
		return (org.realmeds.tissue.moduller.I_TC_MediaDiscardType)MTable.get(getCtx(), org.realmeds.tissue.moduller.I_TC_MediaDiscardType.Table_ID)
			.getPO(getTC_MediaDiscardType_ID(), get_TrxName());
	}

	/** Set TC_MediaDiscardType.
		@param TC_MediaDiscardType_ID TC_MediaDiscardType
	*/
	public void setTC_MediaDiscardType_ID (int TC_MediaDiscardType_ID)
	{
		if (TC_MediaDiscardType_ID < 1)
			set_ValueNoCheck (COLUMNNAME_TC_MediaDiscardType_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_TC_MediaDiscardType_ID, Integer.valueOf(TC_MediaDiscardType_ID));
	}

	/** Get TC_MediaDiscardType.
		@return TC_MediaDiscardType	  */
	public int getTC_MediaDiscardType_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_TC_MediaDiscardType_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/**
	 * Set tcpf2.
	 * 
	 * @param tcpf2 tcpf2
	 */
	public void settcpf2(String tcpf2) {
		set_Value(COLUMNNAME_tcpf2, tcpf2);
	}

	/**
	 * Get tcpf2.
	 * 
	 * @return tcpf2
	 */
	public String gettcpf2() {
		return (String) get_Value(COLUMNNAME_tcpf2);
	}

	/**
	 * Set discardreason.
	 * 
	 * @param discardreason discardreason
	 */
	public void setdiscardreason(String discardreason) {
		set_Value(COLUMNNAME_discardreason, discardreason);
	}

	/**
	 * Get discardreason.
	 * 
	 * @return discardreason
	 */
	public String getdiscardreason() {
		return (String) get_Value(COLUMNNAME_discardreason);
	}

	/**
	 * Set operationdate.
	 * 
	 * @param operationdate operationdate
	 */
	public void setoperationdate(Timestamp operationdate) {
		set_Value(COLUMNNAME_operationdate, operationdate);
	}

	/**
	 * Set Discard.
	 * 
	 * @param isDiscarded Discard
	 */
	public void setisDiscarded(boolean isDiscarded) {
		set_Value(COLUMNNAME_isDiscarded, Boolean.valueOf(isDiscarded));
	}

	/**
	 * Get Discard.
	 * 
	 * @return Discard
	 */
	public boolean isDiscarded() {
		Object oo = get_Value(COLUMNNAME_isDiscarded);
		if (oo != null) {
			if (oo instanceof Boolean)
				return ((Boolean) oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}

	/**
	 * Get operationdate.
	 * 
	 * @return operationdate
	 */
	public Timestamp getoperationdate() {
		return (Timestamp) get_Value(COLUMNNAME_operationdate);
	}

	/**
	 * Set personalcode.
	 * 
	 * @param personalcode personalcode
	 */
	public void setpersonalcode(String personalcode) {
		set_Value(COLUMNNAME_personalcode, personalcode);
	}

	/**
	 * Get personalcode.
	 * 
	 * @return personalcode
	 */
	public String getpersonalcode() {
		return (String) get_Value(COLUMNNAME_personalcode);
	}

	public org.realmeds.tissue.moduller.I_TC_MachineType getTC_MachineType() throws RuntimeException {
		return (org.realmeds.tissue.moduller.I_TC_MachineType) MTable
				.get(getCtx(), org.realmeds.tissue.moduller.I_TC_MachineType.Table_ID)
				.getPO(getTC_MachineType_ID(), get_TrxName());
	}

	/**
	 * Set TC_MachineType.
	 * 
	 * @param TC_MachineType_ID TC_MachineType
	 */
	public void setTC_MachineType_ID(int TC_MachineType_ID) {
		if (TC_MachineType_ID < 1)
			set_ValueNoCheck(COLUMNNAME_TC_MachineType_ID, null);
		else
			set_ValueNoCheck(COLUMNNAME_TC_MachineType_ID, Integer.valueOf(TC_MachineType_ID));
	}

	/**
	 * Get TC_MachineType.
	 * 
	 * @return TC_MachineType
	 */
	public int getTC_MachineType_ID() {
		Integer ii = (Integer) get_Value(COLUMNNAME_TC_MachineType_ID);
		if (ii == null)
			return 0;
		return ii.intValue();
	}

	/**
	 * Set TC_mediaLabelQr.
	 * 
	 * @param TC_mediaLabelQr_ID TC_mediaLabelQr
	 */
	public void setTC_mediaLabelQr_ID(int TC_mediaLabelQr_ID) {
		if (TC_mediaLabelQr_ID < 1)
			set_ValueNoCheck(COLUMNNAME_TC_mediaLabelQr_ID, null);
		else
			set_ValueNoCheck(COLUMNNAME_TC_mediaLabelQr_ID, Integer.valueOf(TC_mediaLabelQr_ID));
	}

	/**
	 * Get TC_mediaLabelQr.
	 * 
	 * @return TC_mediaLabelQr
	 */
	public int getTC_mediaLabelQr_ID() {
		Integer ii = (Integer) get_Value(COLUMNNAME_TC_mediaLabelQr_ID);
		if (ii == null)
			return 0;
		return ii.intValue();
	}

	/**
	 * Set TC_mediaLabelQr_UU.
	 * 
	 * @param TC_mediaLabelQr_UU TC_mediaLabelQr_UU
	 */
	public void setTC_mediaLabelQr_UU(String TC_mediaLabelQr_UU) {
		set_ValueNoCheck(COLUMNNAME_TC_mediaLabelQr_UU, TC_mediaLabelQr_UU);
	}

	/**
	 * Get TC_mediaLabelQr_UU.
	 * 
	 * @return TC_mediaLabelQr_UU
	 */
	public String getTC_mediaLabelQr_UU() {
		return (String) get_Value(COLUMNNAME_TC_mediaLabelQr_UU);
	}

	public org.realmeds.tissue.moduller.I_TC_MediaLine getTC_MediaLine() throws RuntimeException {
		return (org.realmeds.tissue.moduller.I_TC_MediaLine) MTable
				.get(getCtx(), org.realmeds.tissue.moduller.I_TC_MediaLine.Table_ID)
				.getPO(getTC_MediaLine_ID(), get_TrxName());
	}

	/**
	 * Set TC_MediaLine.
	 * 
	 * @param TC_MediaLine_ID TC_MediaLine
	 */
	public void setTC_MediaLine_ID(int TC_MediaLine_ID) {
		if (TC_MediaLine_ID < 1)
			set_ValueNoCheck(COLUMNNAME_TC_MediaLine_ID, null);
		else
			set_ValueNoCheck(COLUMNNAME_TC_MediaLine_ID, Integer.valueOf(TC_MediaLine_ID));
	}

	/**
	 * Get TC_MediaLine.
	 * 
	 * @return TC_MediaLine
	 */
	public int getTC_MediaLine_ID() {
		Integer ii = (Integer) get_Value(COLUMNNAME_TC_MediaLine_ID);
		if (ii == null)
			return 0;
		return ii.intValue();
	}

	public org.realmeds.tissue.moduller.I_TC_MediaType getTC_MediaType() throws RuntimeException {
		return (org.realmeds.tissue.moduller.I_TC_MediaType) MTable
				.get(getCtx(), org.realmeds.tissue.moduller.I_TC_MediaType.Table_ID)
				.getPO(getTC_MediaType_ID(), get_TrxName());
	}

	/**
	 * Set TC_MediaType.
	 * 
	 * @param TC_MediaType_ID TC_MediaType
	 */
	public void setTC_MediaType_ID(int TC_MediaType_ID) {
		if (TC_MediaType_ID < 1)
			set_ValueNoCheck(COLUMNNAME_TC_MediaType_ID, null);
		else
			set_ValueNoCheck(COLUMNNAME_TC_MediaType_ID, Integer.valueOf(TC_MediaType_ID));
	}

	/**
	 * Get TC_MediaType.
	 * 
	 * @return TC_MediaType
	 */
	public int getTC_MediaType_ID() {
		Integer ii = (Integer) get_Value(COLUMNNAME_TC_MediaType_ID);
		if (ii == null)
			return 0;
		return ii.intValue();
	}

	/**
	 * Set tcpf.
	 * 
	 * @param tcpf tcpf
	 */
	public void settcpf(String tcpf) {
		set_Value(COLUMNNAME_tcpf, tcpf);
	}

	/**
	 * Get tcpf.
	 * 
	 * @return tcpf
	 */
	public String gettcpf() {
		return (String) get_Value(COLUMNNAME_tcpf);
	}
}