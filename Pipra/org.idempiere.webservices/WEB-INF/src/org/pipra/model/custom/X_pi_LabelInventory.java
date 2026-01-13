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
package org.pipra.model.custom;

import java.io.File;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.List;
import java.util.Properties;

import org.compiere.model.I_Persistent;
import org.compiere.model.MTable;
import org.compiere.model.PO;
import org.compiere.model.POInfo;
import org.compiere.process.DocAction;
import org.compiere.process.IDocsPostProcess;

/**
 * Generated Model for pi_LabelInventory
 * 
 * @author iDempiere (generated)
 * @version Release 10 - $Id$
 */
@org.adempiere.base.Model(table = "pi_LabelInventory")
public class X_pi_LabelInventory extends PO implements I_pi_LabelInventory, I_Persistent, DocAction, IDocsPostProcess {

	/**
	 *
	 */
	private static final long serialVersionUID = 20251212L;

	/** Standard Constructor */
	public X_pi_LabelInventory(Properties ctx, int pi_LabelInventory_ID, String trxName) {
		super(ctx, pi_LabelInventory_ID, trxName);
		/**
		 * if (pi_LabelInventory_ID == 0) { setDocumentNo (null);
		 * setpi_LabelInventory_ID (0); }
		 */
	}

	/** Standard Constructor */
	public X_pi_LabelInventory(Properties ctx, int pi_LabelInventory_ID, String trxName, String... virtualColumns) {
		super(ctx, pi_LabelInventory_ID, trxName, virtualColumns);
		/**
		 * if (pi_LabelInventory_ID == 0) { setDocumentNo (null);
		 * setpi_LabelInventory_ID (0); }
		 */
	}

	/** Load Constructor */
	public X_pi_LabelInventory(Properties ctx, ResultSet rs, String trxName) {
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

	/** Load Meta Data */
	protected POInfo initPO(Properties ctx) {
		POInfo poi = POInfo.getPOInfo(ctx, Table_ID, get_TrxName());
		return poi;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder("X_pi_LabelInventory[").append(get_ID()).append("]");
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
	 * Set Movement Date.
	 * 
	 * @param MovementDate Date a product was moved in or out of inventory
	 */
	public void setMovementDate(Timestamp MovementDate) {
		set_ValueNoCheck(COLUMNNAME_MovementDate, MovementDate);
	}

	/**
	 * Get Movement Date.
	 * 
	 * @return Date a product was moved in or out of inventory
	 */
	public Timestamp getMovementDate() {
		return (Timestamp) get_Value(COLUMNNAME_MovementDate);
	}

	public org.compiere.model.I_M_Warehouse getM_Warehouse() throws RuntimeException {
		return (org.compiere.model.I_M_Warehouse) MTable.get(getCtx(), org.compiere.model.I_M_Warehouse.Table_ID)
				.getPO(getM_Warehouse_ID(), get_TrxName());
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
	 * Set pi_LabelInventory.
	 * 
	 * @param pi_LabelInventory_ID pi_LabelInventory
	 */
	public void setpi_LabelInventory_ID(int pi_LabelInventory_ID) {
		if (pi_LabelInventory_ID < 1)
			set_ValueNoCheck(COLUMNNAME_pi_LabelInventory_ID, null);
		else
			set_ValueNoCheck(COLUMNNAME_pi_LabelInventory_ID, Integer.valueOf(pi_LabelInventory_ID));
	}

	/**
	 * Get pi_LabelInventory.
	 * 
	 * @return pi_LabelInventory
	 */
	public int getpi_LabelInventory_ID() {
		Integer ii = (Integer) get_Value(COLUMNNAME_pi_LabelInventory_ID);
		if (ii == null)
			return 0;
		return ii.intValue();
	}

	/**
	 * Set pi_LabelInventory_UU.
	 * 
	 * @param pi_LabelInventory_UU pi_LabelInventory_UU
	 */
	public void setpi_LabelInventory_UU(String pi_LabelInventory_UU) {
		set_ValueNoCheck(COLUMNNAME_pi_LabelInventory_UU, pi_LabelInventory_UU);
	}

	/**
	 * Get pi_LabelInventory_UU.
	 * 
	 * @return pi_LabelInventory_UU
	 */
	public String getpi_LabelInventory_UU() {
		return (String) get_Value(COLUMNNAME_pi_LabelInventory_UU);
	}

	/**
	 * Set Processed.
	 * 
	 * @param Processed The document has been processed
	 */
	public void setProcessed(boolean Processed) {
		set_Value(COLUMNNAME_Processed, Boolean.valueOf(Processed));
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

	@Override
	public List<PO> getDocsPostProcess() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean processIt(String action) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean unlockIt() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean invalidateIt() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String prepareIt() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean approveIt() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean rejectIt() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String completeIt() {
		setDocStatus(DocAction.ACTION_Complete);
		setDocAction(DocAction.ACTION_Close);
		setProcessed(true);
		completeIt();
		saveEx();
		return null;
	}

	@Override
	public boolean voidIt() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean closeIt() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean reverseCorrectIt() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean reverseAccrualIt() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean reActivateIt() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getSummary() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDocumentInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public File createPDF() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getProcessMsg() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getDoc_User_ID() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getC_Currency_ID() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public BigDecimal getApprovalAmt() {
		// TODO Auto-generated method stub
		return null;
	}

}