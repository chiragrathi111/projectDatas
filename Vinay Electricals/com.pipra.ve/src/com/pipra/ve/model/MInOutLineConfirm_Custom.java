package com.pipra.ve.model;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.List;
import java.util.Properties;

import org.compiere.model.MInOutConfirm;
import org.compiere.model.MInOutLine;
import org.compiere.model.MInOutLineConfirm;
import org.compiere.model.Query;
import org.compiere.util.Env;
import org.compiere.util.Util;

public class MInOutLineConfirm_Custom extends MInOutLineConfirm {

	private static final long serialVersionUID = 1086583915391686903L;

	public MInOutLineConfirm_Custom(Properties ctx, int M_InOutLineConfirm_ID, String trxName) {
		super(ctx, M_InOutLineConfirm_ID, trxName);
		// TODO Auto-generated constructor stub
	}

	public MInOutLineConfirm_Custom(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
		// TODO Auto-generated constructor stub
	}

	public MInOutLineConfirm_Custom(MInOutConfirm header) {
		super(header);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected boolean beforeDelete() {
		// TODO Auto-generated method stub
		return super.beforeDelete();
	}

	@Override
	protected boolean beforeSave(boolean newRecord) {
		// TODO Auto-generated method stub
		return super.beforeSave(newRecord);
	}

	@Override
	protected boolean afterSave(boolean newRecord, boolean success) {
		// TODO Auto-generated method stub
		
		if(success && is_ValueChanged(COLUMNNAME_QCFailedQty)) {
			MInOutLine_Custom line = new MInOutLine_Custom(getCtx(), getM_InOutLine_ID(), get_TrxName());
			line.setQCFailedQty(getQCFailedQty());
			line.saveEx();
			line = null;
		}
		
		return super.afterSave(newRecord, success);
	}

	@Override
	protected boolean afterDelete(boolean success) {
		// TODO Auto-generated method stub
		return super.afterDelete(success);
	}
	
	public static MInOutLine_Custom[] getInOutLine(Properties ctx, int M_InOut_ID, String where, String trxName) {
		String whereClause = "M_InOut_ID=? " + (!Util.isEmpty(where, true) ? " AND " + where : "");
		List<MInOutLine> list = new Query(ctx, Table_Name, whereClause, trxName).setParameters(M_InOut_ID).list();
		return list.toArray(new MInOutLine_Custom[list.size()]);
	} // getInOutLine

	public static final String COLUMNNAME_QCFailedQty = "QCFailedQty";

	public void setQCFailedQty(BigDecimal QCFailedQty) {
		set_Value(COLUMNNAME_QCFailedQty, QCFailedQty);
	}

	/**
	 * Get Confirmed Quantity.
	 * 
	 * @return Confirmation of a received quantity
	 */
	public BigDecimal getQCFailedQty() {
		BigDecimal bd = (BigDecimal) get_Value(COLUMNNAME_QCFailedQty);
		if (bd == null)
			return Env.ZERO;
		return bd;
	}
	
	@Override
	public boolean processLine (boolean isSOTrx, String confirmType)
	{
		MInOutLine_Custom line = getLine();
		
		//	Customer
		if (MInOutConfirm.CONFIRMTYPE_CustomerConfirmation.equals(confirmType))
		{
			line.setConfirmedQty(getConfirmedQty());
		}
		
		//	Drop Ship
		else if (MInOutConfirm.CONFIRMTYPE_DropShipConfirm.equals(confirmType))
		{
			
		}
		
		//	Pick or QA
		else if (MInOutConfirm.CONFIRMTYPE_PickQAConfirm.equals(confirmType))
		{
			line.setTargetQty(getTargetQty());
			line.setMovementQty(getConfirmedQty());	//	Entered NOT changed
			line.setPickedQty(getConfirmedQty());
			line.setScrappedQty(getScrappedQty());
			
			line.setConfirmedQty(getConfirmedQty());
			line.setQCFailedQty(getQCFailedQty());
		}
		
		//	Ship or Receipt
		else if (MInOutConfirm.CONFIRMTYPE_ShipReceiptConfirm.equals(confirmType))
		{
			line.setTargetQty(getTargetQty());
			BigDecimal qty = getConfirmedQty();
			if (!isSOTrx)	//	In PO, we have the responsibility for scapped
				qty = qty.add(getScrappedQty());
			line.setMovementQty(qty);				//	Entered NOT changed
			//
			line.setScrappedQty(getScrappedQty());
			
			line.setConfirmedQty(getConfirmedQty());
			line.setQCFailedQty(getQCFailedQty());
		}
		//	Vendor
		else if (MInOutConfirm.CONFIRMTYPE_VendorConfirmation.equals(confirmType))
		{
			line.setConfirmedQty(getConfirmedQty());
		}
		
		return line.save(get_TrxName());
	}	//	processConfirmation
	
	/** Ship Line				*/
	private MInOutLine_Custom 	m_line = null;
	
	public MInOutLine_Custom getLine()
	{
		if (m_line == null)
			m_line = new MInOutLine_Custom (getCtx(), getM_InOutLine_ID(), get_TrxName());
		return m_line;
	}	//	getLine

}
