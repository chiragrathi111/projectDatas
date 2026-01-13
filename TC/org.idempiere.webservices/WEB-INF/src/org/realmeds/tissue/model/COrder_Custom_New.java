package org.realmeds.tissue.model;

import java.sql.ResultSet;
import java.util.List;
import java.util.Properties;
import org.compiere.model.MOrder;
import org.compiere.model.Query;
import org.realmeds.tissue.custom.TCUtills;
import org.realmeds.tissue.moduller.C_OrderLine_custom;

public class COrder_Custom_New extends MOrder {

	private static final long serialVersionUID = 1101071347949960064L;

	public COrder_Custom_New(Properties ctx, int C_Order_ID, String trxName) {
		super(ctx, C_Order_ID, trxName);
	}

	public COrder_Custom_New(Properties ctx, int C_Order_ID, String trxName, String... virtualColumns) {
		super(ctx, C_Order_ID, trxName, virtualColumns);
	}

	public COrder_Custom_New(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}

	@Override
	protected boolean beforeSave(boolean newRecord) {
		return super.beforeSave(newRecord);
	}

	@Override
	protected boolean beforeDelete() {
		return super.beforeDelete();
	}

	@Override
	protected boolean afterSave(boolean newRecord, boolean success) {
		return super.afterSave(newRecord, success);
	}

	@Override
	protected boolean afterDelete(boolean success) {
		return super.afterDelete(success);
	}
	
	@Override
	public String completeIt() {
		String status = super.completeIt();

		if (DOCSTATUS_Completed.equals(status)) {
			int orderId = getC_Order_ID();

			List<C_OrderLine_custom> lines = new Query(getCtx(), C_OrderLine_custom.Table_Name,
					C_OrderLine_custom.COLUMNNAME_C_Order_ID + "=?", get_TrxName())
					.setParameters(orderId)
					.list();

			for (C_OrderLine_custom line : lines) {
				String cultureUUID = line.getCultureUUId();

				if (cultureUUID == null || cultureUUID.isEmpty()) {
					continue;
				}

				int cultureId = TCUtills.getValidCultureId(cultureUUID, getAD_Client_ID());
				if (cultureId > 0) {
					TCCultureLabel culture = new TCCultureLabel(getCtx(), cultureId, get_TrxName());
					culture.setIsSold(true);
					culture.saveEx();
				}
			}
		}

		return status;
	}

	public static final String COLUMNNAME_PUTSTATUS = "putStatus";

	public void setPutStatus(String putStatus) {
		set_Value(COLUMNNAME_PUTSTATUS, putStatus);
	}

	public String getPutStatus() {
		String bd = (String) get_Value(COLUMNNAME_PUTSTATUS);
		return bd;
	}
	
    public static final String COLUMNNAME_secondaryhardeninguuid = "secondaryhardeninguuid";

	public void setsecondaryhardeninguuid (String secondaryhardeninguuid) {
		set_Value(COLUMNNAME_secondaryhardeninguuid, secondaryhardeninguuid);
	}

	public String getsecondaryhardeninguuid() {
		String bd = (String) get_Value(COLUMNNAME_secondaryhardeninguuid);
		return bd;
	}
	
	public static final String COLUMNNAME_isRooting = "isculture";

	public void setIsRooting(boolean isculture) {
		set_Value(COLUMNNAME_isRooting, Boolean.valueOf(isculture));
	}

	public boolean isRooting() {
		Object oo = get_Value(COLUMNNAME_isRooting);
		if (oo != null) {
			if (oo instanceof Boolean)
				return ((Boolean) oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}

}
