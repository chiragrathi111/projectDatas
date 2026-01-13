package com.pipra.rwpl.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.compiere.model.MBPartner;
import org.compiere.model.MDocType;
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
	
	public MInOut_Custom (Properties ctx, String trxName, int clientId, int orgId, int userId, int bPartnerId, int warehouseId, MDocType mDocType, Timestamp movementDate, String description) {
		super(ctx, 0, trxName);
		
		setClientOrg(clientId, orgId);
		setC_BPartner_ID (bPartnerId);
		MBPartner partner = new MBPartner(ctx, bPartnerId, trxName);
		System.out.println(partner.getPrimaryC_BPartner_Location_ID());
		setC_BPartner_Location_ID (partner.getPrimaryC_BPartner_Location_ID());	//	shipment address
		setAD_User_ID(userId);
		setM_Warehouse_ID (warehouseId);
		setIsSOTrx (false);
		setC_DocType_ID (mDocType.get_ID());
		setMovementType();	
		setMovementDate (movementDate);
		setDateAcct (movementDate);
		
		setDateOrdered(movementDate);
		setDescription(description);

//		setC_Order_ID();
//		setDeliveryRule (order.getDeliveryRule());
//		setDeliveryViaRule (order.getDeliveryViaRule());
//		setM_Shipper_ID(order.getM_Shipper_ID());
//		setFreightCostRule (order.getFreightCostRule());
//		setFreightAmt(order.getFreightAmt());
//		setSalesRep_ID(order.getSalesRep_ID());
//		//
//		setC_Activity_ID(order.getC_Activity_ID());
//		setC_Campaign_ID(order.getC_Campaign_ID());
//		setC_Charge_ID(order.getC_Charge_ID());
//		setChargeAmt(order.getChargeAmt());
//		//
//		setC_Project_ID(order.getC_Project_ID());
//
//		setPOReference(order.getPOReference());
//		setSalesRep_ID(order.getSalesRep_ID());
//		setAD_OrgTrx_ID(order.getAD_OrgTrx_ID());
//		setUser1_ID(order.getUser1_ID());
//		setUser2_ID(order.getUser2_ID());
//		setPriorityRule(order.getPriorityRule());
//		// Drop shipment
//		setIsDropShip(order.isDropShip());
//		setDropShip_BPartner_ID(order.getDropShip_BPartner_ID());
//		setDropShip_Location_ID(order.getDropShip_Location_ID());
//		setDropShip_User_ID(order.getDropShip_User_ID());
	
	}
	
//	public void createLineFrom(int M_Product_ID, int C_UOM_ID, BigDecimal Qty, int M_Locator_ID)
//	{
//		
//		MInOutLine iol = new MInOutLine (this);
//		iol.setM_Product_ID(M_Product_ID, C_UOM_ID);	//	Line UOM
//		iol.setQty(Qty);	
//		//	Movement/Entered
//
////			iol.setC_OrderLine_ID(C_OrderLine_ID);
////			ol = new MOrderLine (Env.getCtx(), C_OrderLine_ID, get_TrxName());
//			if (ol.getQtyEntered().compareTo(ol.getQtyOrdered()) != 0)
//			{
//				iol.setMovementQty(Qty
//						.multiply(ol.getQtyOrdered())
//						.divide(ol.getQtyEntered(), 12, RoundingMode.HALF_UP));
//				iol.setC_UOM_ID(C_UOM_ID);
//			}
////			iol.setM_AttributeSetInstance_ID(ol.getM_AttributeSetInstance_ID());
////			iol.setDescription(ol.getDescription());
//			//
////			iol.setC_Project_ID(ol.getC_Project_ID());
////			iol.setC_ProjectPhase_ID(ol.getC_ProjectPhase_ID());
////			iol.setC_ProjectTask_ID(ol.getC_ProjectTask_ID());
////			iol.setC_Activity_ID(ol.getC_Activity_ID());
////			iol.setC_Campaign_ID(ol.getC_Campaign_ID());
////			iol.setAD_OrgTrx_ID(ol.getAD_OrgTrx_ID());
////			iol.setUser1_ID(ol.getUser1_ID());
////			iol.setUser2_ID(ol.getUser2_ID());
//		
//		iol.setM_Locator_ID(M_Locator_ID);
//		iol.saveEx();
//
//	}
	
}
