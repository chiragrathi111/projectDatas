package com.pipra.rwpl.model;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.compiere.model.MOrder;

import com.pipra.rwpl.utils.RwplUtils;

@org.adempiere.base.Model(table = MOrder.Table_Name)
public class COrder_Custom extends MOrder {

	private static final long serialVersionUID = 1101071347949960064L;

	public COrder_Custom(Properties ctx, int C_Order_ID, String trxName) {
		super(ctx, C_Order_ID, trxName);
	}

	public COrder_Custom(Properties ctx, int C_Order_ID, String trxName, String... virtualColumns) {
		super(ctx, C_Order_ID, trxName, virtualColumns);
	}

	public COrder_Custom(Properties ctx, ResultSet rs, String trxName) {
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

	public static final String COLUMNNAME_PUTSTATUS = "putStatus";

	public void setPutStatus(String putStatus) {
		set_Value(COLUMNNAME_PUTSTATUS, putStatus);
	}

	public String getPutStatus() {
		String bd = (String) get_Value(COLUMNNAME_PUTSTATUS);
		return bd;
	}

	@Override
	public String completeIt() {
		String result = super.completeIt();

		String title = null;
		String body = null;
//		String userType = null;
		String messageTitle = null;
		Map<String, String> data = new HashMap<>();

		data.put("recordId", String.valueOf(getC_Order_ID()));
		data.put("documentNo", getDocumentNo());

		boolean supervisor = false;
		boolean labour = false;
		
		if (isSOTrx()) {
			title = "New Sales Order: " + getDocumentNo() + "";
			body = "Sales Order - " + getDocumentNo() + " added to process";
//			userType = "Supervisor";
			supervisor = true;
			messageTitle = "salesOrderCreated";

			data.put("path1", "/pick_screen");
			data.put("path2", "/pick_detail_screen");
			
		} else if (!isSOTrx()) {
			title = "New Purchase Order: " + getDocumentNo() + "";
			body = "Purchase order - " + getDocumentNo() + " added to process";
//			userType = "Supervisor";
			supervisor = true;
			messageTitle = "purchaseOrderCreated";

			data.put("path1", "/po_screen");
			data.put("path2", "/po_detail_screen");
		}

		RwplUtils.sendNotificationAsync(supervisor, labour, get_Table_ID(), getC_Order_ID(), getCtx(), get_TrxName(), title,
				body, get_TableName(), data, getAD_Client_ID(), messageTitle);

		return result;
	}

}
