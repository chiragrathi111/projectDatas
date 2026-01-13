package com.pipra.ve.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.compiere.model.MInOut;
import org.compiere.model.MInOutLine;
import org.compiere.model.MUser;
import org.compiere.model.PO;
import org.compiere.model.Query;
import org.compiere.util.DB;
import org.pipra.model.custom.PipraUtils;

@org.adempiere.base.Model(table = MInOut.Table_Name)
public class MInOut_Custom extends MInOut {

	private static final long serialVersionUID = 1101071347949960064L;

	public MInOut_Custom(Properties ctx, int M_InOut_ID, String trxName) {
		super(ctx, M_InOut_ID, trxName);
	}

	public MInOut_Custom(Properties ctx, int M_InOut_ID, String trxName, String... virtualColumns) {
		super(ctx, M_InOut_ID, trxName, virtualColumns);
	}

	public MInOut_Custom(Properties ctx, ResultSet rs, String trxName) {
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

	public static final String COLUMNNAME_PICKSTATUS = "pickStatus";

	public void setPickStatus(String pickStatus) {
		set_Value(COLUMNNAME_PICKSTATUS, pickStatus);
	}

	public String getPickStatus() {
		String bd = (String) get_Value(COLUMNNAME_PICKSTATUS);
		return bd;
	}
	
	public static final String COLUMNNAME_MARKING = "marking";

	public void setMarking(String marking) {
		set_Value(COLUMNNAME_MARKING, marking);
	}

	public String getMarking() {
		String bd = (String) get_Value(COLUMNNAME_MARKING);
		return bd;
	}

	public static List<PO> getMInoutsByCOrderId(int cOrderId, Properties ctx, String trxName) {

		List<PO> list = new Query(ctx, MInOut.Table_Name, " C_Order_ID =?", trxName).setParameters(cOrderId)
				.setOrderBy(MInOut.COLUMNNAME_M_InOut_ID).list();
		return list;
	}

	public static int getTotalQuantityForMInout(Properties ctx, String trxName, int mInoutId) {
		MInOut mi = new MInOut(ctx, mInoutId, trxName);
		int totalQuantity = 0;
		for (MInOutLine line : mi.getLines()) {
			MInOutLine_Custom lineCustom = new MInOutLine_Custom(ctx, line.get_ID(), trxName);
			int qcFailedQty = lineCustom.getQCFailedQty().intValue();
			totalQuantity += line.getQtyEntered().intValue() - qcFailedQty;
		}

		return totalQuantity;
	}

	public static List<Integer> getLocatorIDsByName(String obj, int clientId, int warehousId) {
		List<Integer> list = new ArrayList<Integer>();
		try {
			PreparedStatement pstm = null;
			ResultSet rs = null;
			String query = null;
			if(warehousId == 0)
			 query = "select m_locator_id from m_locator where value = '" + obj + "' AND ad_client_id = "
					+ clientId + "";
			else
				query = "select m_locator_id from m_locator where value = '" + obj + "' AND ad_client_id = "
						+ clientId + " AND m_warehouse_id = "+warehousId+"";
			pstm = DB.prepareStatement(query.toString(), null);
			rs = pstm.executeQuery();

			while (rs.next()) {
				int locatorId = rs.getInt("m_locator_id");
				list.add(locatorId);
			}
			pstm.close();
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public static List<PO> getUserByName(Object obj, Properties ctx, String trxName) {

		List<PO> list = new Query(ctx, MUser.Table_Name, " name =?", trxName).setParameters(obj)
				.setOrderBy(MUser.COLUMNNAME_AD_User_ID).list();
		return list;
	}

	@Override
	public String completeIt() {
		String result = super.completeIt();

		String title = null;
		String body = null;
		String userType = null;
		String messageTitle = null;
		Map<String, String> data = new HashMap<>();

		if (isSOTrx()) {
			title = "Products dispatched with shipment - " + getDocumentNo() + " ";
			body = "Products dispatched with Shipment No: " + getDocumentNo() + " for Order - "
					+ getC_Order().getDocumentNo() + "";
			userType = "Supervisor";
			messageTitle = "shipmentDisptahced";

			data.put("path1", "/dispatch_screen");
			data.put("path2", "/dispatch_detail_screen");
		} else {
			title = "Products added for Put away";
			body = "New products ready for Put away";
			userType = "Labour";
			messageTitle = "productsAddedForPutaway";

			data.put("path1", "/labour_put_away_screen");
		}

		PipraUtils.sendNotificationAsync(userType, get_Table_ID(), getC_Order_ID(), getCtx(), get_TrxName(), title,
				body, get_TableName(), data, getAD_Client_ID(), messageTitle);

		return result;
	}
}
