package org.pipra.webservices.custom;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.compiere.model.MInOut;
import org.compiere.model.MInOutLine;
import org.compiere.model.MLocator;
import org.compiere.model.MUser;
import org.compiere.model.PO;
import org.compiere.model.Query;
import org.compiere.util.DB;

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

	public static List<PO> getMInoutsByCOrderId(int cOrderId, Properties ctx, String trxName) {

		List<PO> list = new Query(ctx, MInOut.Table_Name, " C_Order_ID =?", trxName).setParameters(cOrderId)
				.setOrderBy(MInOut.COLUMNNAME_M_InOut_ID).list();
		return list;
	}
	
	public static int getTotalQuantityForMInout( Properties ctx, String trxName, int mInoutId) {
		MInOut mi = new MInOut(ctx, mInoutId, trxName);
		int totalQuantity = 0;
		for(MInOutLine line : mi.getLines()) {
			totalQuantity += line.getQtyEntered().intValue();
		}
		
		return totalQuantity;
	}
	
	public static List<PO> getLocatorByName(Object obj, Properties ctx, String trxName, int warehouseId) {

		List<PO> list = new Query(ctx, MLocator.Table_Name, " value =?", trxName).setParameters(obj)
				.setOrderBy(MLocator.COLUMNNAME_M_Locator_ID).list();
		return list;
	}

	public static List<Integer> getLocatorIDsByName(String obj, int clientId) {
		List<Integer> list = new ArrayList<Integer>();
		try {
			PreparedStatement pstm = null;
			ResultSet rs = null;
			String query = "select m_locator_id from m_locator where value = '"+obj+"' AND ad_client_id = "+clientId+"";
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
}
