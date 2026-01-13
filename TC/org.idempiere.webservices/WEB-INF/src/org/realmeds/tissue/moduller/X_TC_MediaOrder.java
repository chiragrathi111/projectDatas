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

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Properties;
import org.compiere.model.*;
import org.compiere.util.Env;
import org.compiere.util.KeyNamePair;

/**
 * Generated Model for TC_MediaOrder
 * 
 * @author iDempiere (generated)
 * @version Release 10 - $Id$
 */
@org.adempiere.base.Model(table = "TC_MediaOrder")
public class X_TC_MediaOrder extends PO implements I_TC_MediaOrder, I_Persistent {

	/**
	 *
	 */
	private static final long serialVersionUID = 20240317L;

	/** Standard Constructor */
	public X_TC_MediaOrder(Properties ctx, int TC_MediaOrder_ID, String trxName) {
		super(ctx, TC_MediaOrder_ID, trxName);
		/**
		 * if (TC_MediaOrder_ID == 0) { setC_DocType_ID (0); // 0 setC_DocTypeTarget_ID
		 * (0); setDocAction (null); // CO setDocStatus (null); // DR setDocumentNo
		 * (null); setIsApproved (false); setIsDefault (false); setM_Warehouse_ID (0);
		 * setName (null); setProcessed (false); setTC_mediaorder_ID (0); }
		 */
	}

	/** Standard Constructor */
	public X_TC_MediaOrder(Properties ctx, int TC_MediaOrder_ID, String trxName, String... virtualColumns) {
		super(ctx, TC_MediaOrder_ID, trxName, virtualColumns);
		/**
		 * if (TC_MediaOrder_ID == 0) { setC_DocType_ID (0); // 0 setC_DocTypeTarget_ID
		 * (0); setDocAction (null); // CO setDocStatus (null); // DR setDocumentNo
		 * (null); setIsApproved (false); setIsDefault (false); setM_Warehouse_ID (0);
		 * setName (null); setProcessed (false); setTC_mediaorder_ID (0); }
		 */
	}

	/** Load Constructor */
	public X_TC_MediaOrder(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}

	/**
	 * AccessLevel
	 * 
	 * @return 7 - System - Client - Org
	 */
	protected int get_AccessLevel() {
		return accessLevel.intValue();
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

	/** Load Meta Data */
	protected POInfo initPO(Properties ctx) {
		POInfo poi = POInfo.getPOInfo(ctx, Table_ID, get_TrxName());
		return poi;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder("X_TC_MediaOrder[").append(get_ID()).append(",Name=").append(getName())
				.append("]");
		return sb.toString();
	}

	public org.compiere.model.I_C_DocType getC_DocType() throws RuntimeException {
		return (org.compiere.model.I_C_DocType) MTable.get(getCtx(), org.compiere.model.I_C_DocType.Table_ID)
				.getPO(getC_DocType_ID(), get_TrxName());
	}

	/**
	 * Set Document Type.
	 * 
	 * @param C_DocType_ID Document type or rules
	 */
	public void setC_DocType_ID(int C_DocType_ID) {
		if (C_DocType_ID < 0)
			set_ValueNoCheck(COLUMNNAME_C_DocType_ID, null);
		else
			set_ValueNoCheck(COLUMNNAME_C_DocType_ID, Integer.valueOf(C_DocType_ID));
	}

	/**
	 * Get Document Type.
	 * 
	 * @return Document type or rules
	 */
	public int getC_DocType_ID() {
		Integer ii = (Integer) get_Value(COLUMNNAME_C_DocType_ID);
		if (ii == null)
			return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_DocType getC_DocTypeTarget() throws RuntimeException {
		return (org.compiere.model.I_C_DocType) MTable.get(getCtx(), org.compiere.model.I_C_DocType.Table_ID)
				.getPO(getC_DocTypeTarget_ID(), get_TrxName());
	}

	/**
	 * Set Target Document Type.
	 * 
	 * @param C_DocTypeTarget_ID Target document type for conversing documents
	 */
	public void setC_DocTypeTarget_ID(int C_DocTypeTarget_ID) {
		if (C_DocTypeTarget_ID < 1)
			set_ValueNoCheck(COLUMNNAME_C_DocTypeTarget_ID, null);
		else
			set_ValueNoCheck(COLUMNNAME_C_DocTypeTarget_ID, Integer.valueOf(C_DocTypeTarget_ID));
	}

	/**
	 * Get Target Document Type.
	 * 
	 * @return Target document type for conversing documents
	 */
	public int getC_DocTypeTarget_ID() {
		Integer ii = (Integer) get_Value(COLUMNNAME_C_DocTypeTarget_ID);
		if (ii == null)
			return 0;
		return ii.intValue();
	}

	/**
	 * Set Date Ordered.
	 * 
	 * @param DateOrdered Date of Order
	 */
	public void setDateOrdered(Timestamp DateOrdered) {
		set_ValueNoCheck(COLUMNNAME_DateOrdered, DateOrdered);
	}

	/**
	 * Get Date Ordered.
	 * 
	 * @return Date of Order
	 */
	public Timestamp getDateOrdered() {
		return (Timestamp) get_Value(COLUMNNAME_DateOrdered);
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

	/** DocAction AD_Reference_ID=135 */
	public static final int DOCACTION_AD_Reference_ID = 135;
	/** &lt;None&gt; = -- */
	public static final String DOCACTION_None = "--";
	/** Approve = AP */
	public static final String DOCACTION_Approve = "AP";
	/** Close = CL */
	public static final String DOCACTION_Close = "CL";
	/** Complete = CO */
	public static final String DOCACTION_Complete = "CO";
	/** Invalidate = IN */
	public static final String DOCACTION_Invalidate = "IN";
	/** Post = PO */
	public static final String DOCACTION_Post = "PO";
	/** Prepare = PR */
	public static final String DOCACTION_Prepare = "PR";
	/** Reverse - Accrual = RA */
	public static final String DOCACTION_Reverse_Accrual = "RA";
	/** Reverse - Correct = RC */
	public static final String DOCACTION_Reverse_Correct = "RC";
	/** Re-activate = RE */
	public static final String DOCACTION_Re_Activate = "RE";
	/** Reject = RJ */
	public static final String DOCACTION_Reject = "RJ";
	/** Void = VO */
	public static final String DOCACTION_Void = "VO";
	/** Wait Complete = WC */
	public static final String DOCACTION_WaitComplete = "WC";
	/** Unlock = XL */
	public static final String DOCACTION_Unlock = "XL";

	/**
	 * Set Document Action.
	 * 
	 * @param DocAction The targeted status of the document
	 */
	public void setDocAction(String DocAction) {

		set_Value(COLUMNNAME_DocAction, DocAction);
	}

	/**
	 * Get Document Action.
	 * 
	 * @return The targeted status of the document
	 */
	public String getDocAction() {
		return (String) get_Value(COLUMNNAME_DocAction);
	}

	/** DocStatus AD_Reference_ID=131 */
	public static final int DOCSTATUS_AD_Reference_ID = 131;
	/** Unknown = ?? */
	public static final String DOCSTATUS_Unknown = "??";
	/** Approved = AP */
	public static final String DOCSTATUS_Approved = "AP";
	/** Closed = CL */
	public static final String DOCSTATUS_Closed = "CL";
	/** Completed = CO */
	public static final String DOCSTATUS_Completed = "CO";
	/** Drafted = DR */
	public static final String DOCSTATUS_Drafted = "DR";
	/** Invalid = IN */
	public static final String DOCSTATUS_Invalid = "IN";
	/** In Progress = IP */
	public static final String DOCSTATUS_InProgress = "IP";
	/** Not Approved = NA */
	public static final String DOCSTATUS_NotApproved = "NA";
	/** Reversed = RE */
	public static final String DOCSTATUS_Reversed = "RE";
	/** Voided = VO */
	public static final String DOCSTATUS_Voided = "VO";
	/** Waiting Confirmation = WC */
	public static final String DOCSTATUS_WaitingConfirmation = "WC";
	/** Waiting Payment = WP */
	public static final String DOCSTATUS_WaitingPayment = "WP";

	/**
	 * Set Document Status.
	 * 
	 * @param DocStatus The current status of the document
	 */
	public void setDocStatus(String DocStatus) {

		set_Value(COLUMNNAME_DocStatus, DocStatus);
	}

	/**
	 * Get Document Status.
	 * 
	 * @return The current status of the document
	 */
	public String getDocStatus() {
		return (String) get_Value(COLUMNNAME_DocStatus);
	}

	/**
	 * Set Document No.
	 * 
	 * @param DocumentNo Document sequence number of the document
	 */
	public void setDocumentNo(String DocumentNo) {
		set_ValueNoCheck(COLUMNNAME_DocumentNo, DocumentNo);
	}

	/**
	 * Get Document No.
	 * 
	 * @return Document sequence number of the document
	 */
	public String getDocumentNo() {
		return (String) get_Value(COLUMNNAME_DocumentNo);
	}

	/**
	 * Get Record ID/ColumnName
	 * 
	 * @return ID/ColumnName pair
	 */
	public KeyNamePair getKeyNamePair() {
		return new KeyNamePair(get_ID(), getDocumentNo());
	}

	/**
	 * Set Approved.
	 * 
	 * @param IsApproved Indicates if this document requires approval
	 */
	public void setIsApproved(boolean IsApproved) {
		set_ValueNoCheck(COLUMNNAME_IsApproved, Boolean.valueOf(IsApproved));
	}

	/**
	 * Get Approved.
	 * 
	 * @return Indicates if this document requires approval
	 */
	public boolean isApproved() {
		Object oo = get_Value(COLUMNNAME_IsApproved);
		if (oo != null) {
			if (oo instanceof Boolean)
				return ((Boolean) oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
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

	public org.compiere.model.I_M_Warehouse getM_Warehouse() throws RuntimeException {
		return (org.compiere.model.I_M_Warehouse) MTable.get(getCtx(), org.compiere.model.I_M_Warehouse.Table_ID)
				.getPO(getM_Warehouse_ID(), get_TrxName());
	}
	
	public org.compiere.model.I_AD_User getAD_User() throws RuntimeException
	{
		return (org.compiere.model.I_AD_User)MTable.get(getCtx(), org.compiere.model.I_AD_User.Table_ID)
			.getPO(getAD_User_ID(), get_TrxName());
	}

	/** Set User/Contact.
		@param AD_User_ID User within the system - Internal or Business Partner Contact
	*/
	public void setAD_User_ID (int AD_User_ID)
	{
		if (AD_User_ID < 1)
			set_ValueNoCheck (COLUMNNAME_AD_User_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_AD_User_ID, Integer.valueOf(AD_User_ID));
	}

	/** Get User/Contact.
		@return User within the system - Internal or Business Partner Contact
	  */
	public int getAD_User_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_AD_User_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	

	/**
	 * Set Warehouse.
	 * 
	 * @param M_Warehouse_ID Storage Warehouse and Service Point
	 */
	public void setM_Warehouse_ID(int M_Warehouse_ID) {
		if (M_Warehouse_ID < 1)
			set_ValueNoCheck(COLUMNNAME_M_Warehouse_ID, null);
		else
			set_ValueNoCheck(COLUMNNAME_M_Warehouse_ID, Integer.valueOf(M_Warehouse_ID));
	}

	/**
	 * Get Warehouse.
	 * 
	 * @return Storage Warehouse and Service Point
	 */
	public int getM_Warehouse_ID() {
		Integer ii = (Integer) get_Value(COLUMNNAME_M_Warehouse_ID);
		if (ii == null)
			return 0;
		return ii.intValue();
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
	 * Set Quantity.
	 * 
	 * @param Quantity Quantity
	 */
	public void setQuantity(BigDecimal Quantity) {
		set_ValueNoCheck(COLUMNNAME_Quantity, Quantity);
	}

	/**
	 * Get Quantity.
	 * 
	 * @return Quantity
	 */
	public BigDecimal getQuantity() {
		BigDecimal bd = (BigDecimal) get_Value(COLUMNNAME_Quantity);
		if (bd == null)
			return Env.ZERO;
		return bd;
	}

	public org.compiere.model.I_M_Product getM_Product() throws RuntimeException {
		return (org.compiere.model.I_M_Product) MTable.get(getCtx(), org.compiere.model.I_M_Product.Table_ID)
				.getPO(getM_Product_ID(), get_TrxName());
	}

	/**
	 * Set Product.
	 * 
	 * @param M_Product_ID Product, Service, Item
	 */
	public void setM_Product_ID(int M_Product_ID) {
		if (M_Product_ID < 1)
			set_Value(COLUMNNAME_M_Product_ID, null);
		else
			set_Value(COLUMNNAME_M_Product_ID, Integer.valueOf(M_Product_ID));
	}

	/**
	 * Get Product.
	 * 
	 * @return Product, Service, Item
	 */
	public int getM_Product_ID() {
		Integer ii = (Integer) get_Value(COLUMNNAME_M_Product_ID);
		if (ii == null)
			return 0;
		return ii.intValue();
	}

	/**
	 * Set Posted.
	 * 
	 * @param Posted Posting status
	 */
	public void setPosted(boolean Posted) {
		set_ValueNoCheck(COLUMNNAME_Posted, Boolean.valueOf(Posted));
	}

	/**
	 * Get Posted.
	 * 
	 * @return Posting status
	 */
	public boolean isPosted() {
		Object oo = get_Value(COLUMNNAME_Posted);
		if (oo != null) {
			if (oo instanceof Boolean)
				return ((Boolean) oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}

	/**
	 * Set Processed.
	 * 
	 * @param Processed The document has been processed
	 */
	public void setProcessed(boolean Processed) {
		set_ValueNoCheck(COLUMNNAME_Processed, Boolean.valueOf(Processed));
	}

	/**
	 * Get Processed.
	 * 
	 * @return The document has been processed
	 */
	public boolean isProcessed() {
		Object oo = get_Value(COLUMNNAME_Processed);
		if (oo != null) {
			if (oo instanceof Boolean)
				return ((Boolean) oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}

	/**
	 * Set Process Now.
	 * 
	 * @param Processing Process Now
	 */
	public void setProcessing(boolean Processing) {
		set_Value(COLUMNNAME_Processing, Boolean.valueOf(Processing));
	}

	/**
	 * Get Process Now.
	 * 
	 * @return Process Now
	 */
	public boolean isProcessing() {
		Object oo = get_Value(COLUMNNAME_Processing);
		if (oo != null) {
			if (oo instanceof Boolean)
				return ((Boolean) oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}

	public org.compiere.model.I_AD_User getSalesRep() throws RuntimeException {
		return (org.compiere.model.I_AD_User) MTable.get(getCtx(), org.compiere.model.I_AD_User.Table_ID)
				.getPO(getSalesRep_ID(), get_TrxName());
	}

	/**
	 * Set Sales Representative.
	 * 
	 * @param SalesRep_ID Sales Representative or Company Agent
	 */
	public void setSalesRep_ID(int SalesRep_ID) {
		if (SalesRep_ID < 1)
			set_ValueNoCheck(COLUMNNAME_SalesRep_ID, null);
		else
			set_ValueNoCheck(COLUMNNAME_SalesRep_ID, Integer.valueOf(SalesRep_ID));
	}

	/**
	 * Get Sales Representative.
	 * 
	 * @return Sales Representative or Company Agent
	 */
	public int getSalesRep_ID() {
		Integer ii = (Integer) get_Value(COLUMNNAME_SalesRep_ID);
		if (ii == null)
			return 0;
		return ii.intValue();
	}

	/**
	 * Set TC_mediaorder.
	 * 
	 * @param TC_mediaorder_ID TC_mediaorder
	 */
	public void setTC_mediaorder_ID(int TC_mediaorder_ID) {
		if (TC_mediaorder_ID < 1)
			set_ValueNoCheck(COLUMNNAME_TC_mediaorder_ID, null);
		else
			set_ValueNoCheck(COLUMNNAME_TC_mediaorder_ID, Integer.valueOf(TC_mediaorder_ID));
	}

	/**
	 * Get TC_mediaorder.
	 * 
	 * @return TC_mediaorder
	 */
	public int getTC_mediaorder_ID() {
		Integer ii = (Integer) get_Value(COLUMNNAME_TC_mediaorder_ID);
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