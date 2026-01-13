package com.pipra.rwpl.model;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.List;
import java.util.Properties;

import org.compiere.model.MInOut;
import org.compiere.model.MInOutLine;
import org.compiere.model.MLocator;
import org.compiere.model.PO;
import org.compiere.model.Query;
import org.compiere.util.Env;
import org.compiere.util.Util;

@org.adempiere.base.Model(table = MInOutLine.Table_Name)
public class MInOutLine_Custom extends MInOutLine {

	private static final long serialVersionUID = 2623456608571958169L;

	public MInOutLine_Custom(Properties ctx, int M_InOutLine_ID, String trxName) {
		super(ctx, M_InOutLine_ID, trxName);
		// TODO Auto-generated constructor stub
	}

	public MInOutLine_Custom(Properties ctx, int M_InOutLine_ID, String trxName, String... virtualColumns) {
		super(ctx, M_InOutLine_ID, trxName, virtualColumns);
		// TODO Auto-generated constructor stub
	}

	public MInOutLine_Custom(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
		// TODO Auto-generated constructor stub
	}

	public MInOutLine_Custom(MInOut inout) {
		super(inout);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected boolean beforeSave(boolean newRecord) {
		// TODO Auto-generated method stub
		return super.beforeSave(newRecord);
	}

	@Override
	protected boolean beforeDelete() {
		// TODO Auto-generated method stub
		 List<PO> poList = PiProductLabel.getProductLabelForInoutLine(getM_InOutLine_ID(), p_ctx, null);
		 if(poList != null && poList.size() !=0)
			 for(PO po : poList) 
				 po.deleteEx(true);
		return super.beforeDelete();
	}

	@Override
	protected boolean afterSave(boolean newRecord, boolean success) {

		int M_Locator_ID = getM_Locator_ID();
		MLocator_Custom locator = new MLocator_Custom(p_ctx, M_Locator_ID, get_TrxName());
		MLocatorType_Custom locatorType = new MLocatorType_Custom(p_ctx, locator.get_ID(), get_TrxName());
		
		if (!locatorType.isReceiving()) {
			List<MLocator> locators = MLocatorType_Custom.getLocatorsByType(getCtx(), get_TrxName(),
					getM_Warehouse_ID(), "receiving", "Y");
			if (locators != null && locators.size() != 0) {
				M_Locator_ID = locators.get(0).get_ID();
			}
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
		List<MInOutLine_Custom> list = new Query(ctx, Table_Name, whereClause, trxName).setParameters(M_InOut_ID).list();
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
	public void setM_Locator_ID(int M_Locator_ID) {
		
		List<MLocator> locators = MLocatorType_Custom.getLocatorsByType(getCtx(), get_TrxName(), getM_Warehouse_ID(), "receiving", "Y");
		if (locators != null && locators.size() != 0) {
			M_Locator_ID = locators.get(0).get_ID();	
		}
		
		super.setM_Locator_ID(M_Locator_ID);
	}

}
