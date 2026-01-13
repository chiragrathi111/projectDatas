package com.pipra.ve.utils;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.I_AD_Workflow;
import org.compiere.model.I_M_Locator;
import org.compiere.model.MBPartner;
import org.compiere.model.MLocator;
import org.compiere.model.MMessage;
import org.compiere.model.MNote;
import org.compiere.model.MPriceListVersion;
import org.compiere.model.MProductCategory;
import org.compiere.model.MRole;
import org.compiere.model.MStorageOnHand;
import org.compiere.model.MTaxCategory;
import org.compiere.model.MTransaction;
import org.compiere.model.MUser;
import org.compiere.model.MWarehouse;
import org.compiere.model.PO;
import org.compiere.model.Query;
import org.compiere.model.X_AD_User;
import org.compiere.model.X_C_DocType;
import org.compiere.model.X_M_LocatorType;
import org.compiere.model.X_S_Resource;
import org.eevolution.model.X_PP_Order;
import org.eevolution.model.X_PP_Product_BOM;
import org.eevolution.model.X_PP_Product_BOMLine;
import org.idempiere.adInterface.x10.ADLoginRequest;
//import org.libero.model.MPPOrderBOMLine;
import org.springframework.scheduling.annotation.Async;

import com.pipra.ve.fcm.FCMService;
import com.pipra.ve.model.MLocatorType_Custom;
import com.pipra.ve.model.MProduct_Custom;
import com.pipra.ve.model.PiUserToken;
import com.pipra.ve.model.X_PI_Deptartment;
import com.pipra.ve.model.X_pi_machineconf;
import com.pipra.ve.model.X_pi_paorder;
import com.pipra.ve.model.X_pi_return;
import com.pipra.ve.model.X_pi_returnline;

public class VeUtils {

	private static final ExecutorService executor = Executors.newCachedThreadPool();

	public static List<PO> getWarehouseList(int clientId, int orgId, Properties ctx, String trxName) {
		List<PO> list  = new Query(ctx, MWarehouse.Table_Name, "ad_client_ID =? AND ad_org_ID = ?", trxName)
					.setParameters(clientId, orgId).setOrderBy(MWarehouse.COLUMNNAME_M_Warehouse_ID + " desc").list();
		return list;
	}
	
	public static List<PO> getResourceForWarehouse(int warehouseId, Properties ctx, String trxName) {
		List<PO> list  = new Query(ctx, X_S_Resource.Table_Name, "m_warehouse_ID =?", trxName)
					.setParameters(warehouseId).setOrderBy(X_S_Resource.COLUMNNAME_S_Resource_ID + " desc").list();
		return list;
	}
	
	public static List<PO> getWorkflowList(int clientId, int orgId, Properties ctx, String trxName) {
		List<PO> list  = new Query(ctx, I_AD_Workflow.Table_Name, "ad_client_ID =? AND ad_org_ID = ? AND workflowtype = 'M'", trxName)
					.setParameters(clientId, orgId).setOrderBy(I_AD_Workflow.COLUMNNAME_AD_Workflow_ID + " desc").list();
		return list;
	}
	
	public static int getDocTypeId(int clientId, Properties ctx, String trxName) {
		PO po  = new Query(ctx, X_C_DocType.Table_Name, "ad_client_ID = ? AND name = 'Manufacturing Order'", trxName)
				.setParameters(clientId).firstOnly();
		return po.get_ID();
	}
	
	public static String getpriorityRule(String rule) {

		if (rule.equalsIgnoreCase("High"))
			return X_PP_Order.PRIORITYRULE_High;
		else if (rule.equalsIgnoreCase("Medium"))
			return X_PP_Order.PRIORITYRULE_Medium;
		else if (rule.equalsIgnoreCase("Low"))
			return X_PP_Order.PRIORITYRULE_Low;
		else if (rule.equalsIgnoreCase("Urgent"))
			return X_PP_Order.PRIORITYRULE_Urgent;
		return null;
	}
	
	public static List<PO> getMOList(int clientId, int orgId, Properties ctx, String trxName, String searchKey, boolean isManufacturing) {
		String isMan = "N";
		if(isManufacturing)
			isMan = "Y";

		String tblName = X_PP_Order.Table_Name;
		List<PO> list  = new Query(ctx, tblName, ""+tblName+".ad_client_ID =? AND "+tblName+".ad_org_ID = ? AND "+tblName+".isManufacturing = ? AND "+tblName+".QtyOrdered - "+tblName+".QtyDelivered > 0 AND (\n"
				+ "			    "+tblName+".documentno ILIKE '%' || COALESCE(?, "+tblName+".documentno) || '%'\n"
				+ "			    OR aw.name ILIKE '%' || COALESCE(?, aw.name) || '%'\n"
				+ "			    OR mp.name ILIKE '%' || COALESCE(?, mp.name) || '%'\n"
				+ "			    OR "+tblName+".description ILIKE '%' || COALESCE(?, "+tblName+".description) || '%'\n"
				+ "			)", trxName)
				.addJoinClause("JOIN M_product mp on mp.M_Product_ID = "+tblName+".M_Product_ID "
						+ "JOIN AD_Workflow aw on aw.AD_Workflow_ID = "+tblName+".AD_Workflow_ID")
					.setParameters(clientId, orgId, isMan, searchKey, searchKey, searchKey, searchKey).setOrderBy(X_PP_Order.COLUMNNAME_PP_Order_ID + " desc").list();
		return list;
	}
	
	public static List<PO> getPAOrderList(int clientId, int orgId, Properties ctx, String trxName, String searchKey, boolean isPackagingOrder) {
	    String isPack = isPackagingOrder ? "Y" : "N";

	    String tblName = X_pi_paorder.Table_Name;
	    List<PO> list = new Query(ctx, tblName, 
	        "" + tblName + ".ad_client_ID = ? AND " +
	        tblName + ".ad_org_ID = ? AND " +
	        tblName + ".packingOrder = ? AND " +
	        tblName + ".processed = 'N' AND (" + 
	        tblName + ".status ILIKE '%' || COALESCE(?, " + tblName + ".status) || '%' OR " +
	        "mp.name ILIKE '%' || COALESCE(?, mp.name) || '%' OR " +
	        tblName + ".description ILIKE '%' || COALESCE(?, " + tblName + ".description) || '%')", // Corrected here
	        trxName)
	        .addJoinClause("JOIN M_product mp ON mp.M_Product_ID = " + tblName + ".M_Product_ID ")
	        .setParameters(clientId, orgId, isPack, searchKey, searchKey, searchKey)
	        .setOrderBy(X_pi_paorder.COLUMNNAME_pi_paorder_ID + " DESC").list();
	    
	    return list;
	}

	
//	public static List<PO> getOrderBomLines(int clientId, int orgId, int orderid, Properties ctx, String trxName) {
//		List<PO> list = new Query(ctx, MPPOrderBOMLine.Table_Name,
//				"ad_client_ID =? AND ad_org_ID = ? AND " + MPPOrderBOMLine.COLUMNNAME_PP_Order_BOM_ID + " =?", trxName)
//				.setParameters(clientId, orgId, orderid).setOrderBy(MPPOrderBOMLine.COLUMNNAME_PP_Order_BOMLine_ID+ " desc").list();
//		return list;
//	}
//	
	public static ADLoginRequest convertAdLogin(com.pipra.ve.x10.ADLoginRequest login) {
		ADLoginRequest adLoginRequest = ADLoginRequest.Factory.newInstance();
		adLoginRequest.setClientID(login.getClientID());
		adLoginRequest.setOrgID(login.getOrgID());
		adLoginRequest.setRoleID(login.getRoleID());
		adLoginRequest.setWarehouseID(login.getWarehouseID());
		adLoginRequest.setUser(login.getUser());
		adLoginRequest.setPass(login.getPass());
		adLoginRequest.setLang(login.getLang());
		adLoginRequest.setStage(login.getStage());
		return adLoginRequest;
	}
	
	
	public static PO getProductBom(int productId, Properties ctx, String trxName) {
		PO po  = new Query(ctx, X_PP_Product_BOM.Table_Name, "m_product_ID = ? AND bomtype = 'A'", trxName)
				.setParameters(productId).firstOnly();
		return po;
	}
	
	public static List<PO> getProductBomLine(int productBomId, Properties ctx, String trxName) {
		List<PO> poList  = new Query(ctx, X_PP_Product_BOMLine.Table_Name, "PP_Product_BOM_ID = ?", trxName).setOrderBy(X_PP_Product_BOMLine.COLUMNNAME_PP_Product_BOMLine_ID)
				.setParameters(productBomId).list();
		return poList;
	}
	
	public static List<MLocator> getLocatorsByDepartment(Properties ctx, String trxName, int warehouseId, int departmentId, Object columnName, Object columnValue) {

		String tblName = I_M_Locator.Table_Name;
		String whereClause = ""+tblName+".M_Warehouse_ID=? AND ml.pi_deptartment_ID = "+departmentId+"";
		if(columnName != null && columnValue != null)
			whereClause += " AND ml."+columnName+" = '"+columnValue+"'";
		else
			whereClause += " AND ml.name not in ('Receiving', 'Dispatch', 'Returns')";
		List<MLocator> list = new Query(ctx, tblName, whereClause, trxName).setParameters(warehouseId)
				.addJoinClause("JOIN M_locatorType ml on  ml.m_locatortype_ID = "+tblName+".m_locatortype_ID")
				.setOnlyActiveRecords(true).setOrderBy("X,Y,Z").list();
		if (list.size() > 0)
			list.stream().forEach(e -> e.markImmutable());
		return list;
	}
	
	public static X_PI_Deptartment getDefaultLocatorType(Properties ctx, String trxName) {
		List<PO> poList = new Query(ctx, X_M_LocatorType.Table_Name, "pi_deptartment_ID = 0", trxName)
				.setOrderBy(X_M_LocatorType.COLUMNNAME_M_LocatorType_ID).list();
		if (poList.size() != 0) 
			return new X_PI_Deptartment(ctx, poList.get(0).get_ID(), trxName);
		return null;
	}
	
	public static String getLocatorTypeName(Properties ctx, String trxName, int id) {
		MLocatorType_Custom mlc= new MLocatorType_Custom(ctx, id, trxName);
		if (mlc == null || mlc.getM_LocatorType_ID() == 0)
			return "";
		if (mlc.isdispatch())
			return MLocatorType_Custom.COLUMNNAME_DISPATCH;
		if (mlc.isPacking())
			return MLocatorType_Custom.COLUMNNAME_PACKING;
		if (mlc.isReceiving())
			return MLocatorType_Custom.COLUMNNAME_RECEIVING;
		if (mlc.isReturns())
			return MLocatorType_Custom.COLUMNNAME_RETURNS;
		if (mlc.isStorage())
			return MLocatorType_Custom.COLUMNNAME_STORAGE;

		return "";
	}
	
	public static MTaxCategory getDefaultTaxCategory(Properties ctx, String trxName, int adClientId) {
		MTaxCategory mTaxCategory = new MTaxCategory(ctx, 0, trxName);

		List<MTaxCategory> list = new Query(ctx, MTaxCategory.Table_Name,
				"ad_client_id = ? AND isDefault = 'Y'", trxName)
				.setOrderBy(MTaxCategory.COLUMNNAME_C_TaxCategory_ID).setParameters(adClientId)
				.list();
		if (list.size() != 0)
			mTaxCategory = list.get(0);

		return mTaxCategory;
	}
	
	public static MProductCategory getDefaultProductCategory(Properties ctx, String trxName, int adClientId) {
		MProductCategory mProductCategory = new MProductCategory(ctx, 0, trxName);

		List<MProductCategory> list = new Query(ctx, MProductCategory.Table_Name,
				"ad_client_id = ? AND isDefault = 'Y'", trxName)
				.setOrderBy(MProductCategory.COLUMNNAME_M_Product_Category_ID).setParameters(adClientId)
				.list();
		if (list.size() != 0)
			mProductCategory = list.get(0);

		return mProductCategory;
	}
	
	public static MPriceListVersion getDefaultPriceListVersion(Properties ctx, String trxName, int adClientId, String isSales) {
		MPriceListVersion mProductCategory = new MPriceListVersion(ctx, 0, trxName);

		String tblName = MPriceListVersion.Table_Name;
		List<MPriceListVersion> list = new Query(ctx, MPriceListVersion.Table_Name,
				""+tblName+".ad_client_id = ? AND mp.issopricelist = '"+isSales+"' AND mp.isDefault = 'Y'", trxName)
				.setOrderBy(MPriceListVersion.COLUMNNAME_M_PriceList_Version_ID).setParameters(adClientId)
				.addJoinClause("JOIN M_PriceList mp on mp.M_PriceList_ID = "+tblName+".M_PriceList_ID ")
				.list();
		if (list.size() != 0)
			mProductCategory = list.get(0);

		return mProductCategory;
	}
	
	public static List<PO> getMachineConfList(int clientId, int orgId, Properties ctx, String trxName) {
		List<PO> list  = new Query(ctx, X_pi_machineconf.Table_Name, "ad_client_ID =? AND ad_org_ID = ?", trxName)
					.setParameters(clientId, orgId).setOrderBy(X_pi_machineconf.COLUMNNAME_pi_machineconf_ID+ " asc").list();
		return list;
	}
	
	public static List<PO> getAgentsListByRole(int clientId, int orgId, Properties ctx, String trxName) {
		List<PO> list  = new Query(ctx, X_pi_machineconf.Table_Name, "ad_client_ID =? AND ad_org_ID = ?", trxName)
					.setParameters(clientId, orgId).setOrderBy(X_pi_machineconf.COLUMNNAME_pi_machineconf_ID+ " asc").list();
		return list;
	}
	
	public static List<PO> getDispatchDepartment(int clientId, int orgId, Properties ctx, String trxName) {
		List<PO> list  = new Query(ctx, X_PI_Deptartment.Table_Name, "ad_client_ID =? AND dispatch = 'Y'", trxName)
					.setParameters(clientId).setOrderBy(X_PI_Deptartment.COLUMNNAME_PI_Deptartment_ID).list();
		return list;
	}

	public static X_PI_Deptartment getFGSwitchAndSocketDepartment(int clientId, Properties ctx, String trxName) {
		PO po = new Query(ctx, X_PI_Deptartment.Table_Name, "ad_client_ID =? AND deptname = 'FG-Switch & Acc.'", trxName)
				.setParameters(clientId).first();

		if (po == null || po.get_ID() == 0)
			return null;

		return new X_PI_Deptartment(ctx, po.get_ID(), trxName);
	}
	
	public static List<PO> getContractorUser(int clientId, Properties ctx, String trxName) {
		List<PO> list  = new Query(ctx, MUser.Table_Name, "ad_client_ID =? AND contractor = 'Y'", trxName)
					.setParameters(clientId).setOrderBy(X_AD_User.COLUMNNAME_AD_User_ID).list();
		return list;
	}
	
	public static void addOrReduceInventory(MProduct_Custom product, int qnty, int locatorId, int orgId, Properties ctx,
			String trxName, boolean negate) {

		BigDecimal QtyMA = BigDecimal.valueOf(qnty);
		if(negate)
			QtyMA = QtyMA.negate();
		int productId = product.getM_Product_ID();

		Timestamp dateMPolicy = MStorageOnHand.getDateMaterialPolicy(productId, 0, trxName);
		// Update Storage - see also VMatch.createMatchRecord
		if (!MStorageOnHand.add(ctx, locatorId, productId, 0, QtyMA, dateMPolicy, trxName)) {
			String m_processMsg = "Cannot correct Inventory OnHand (MA) [" + product.getValue() + "] - ";
			throw new AdempiereException(m_processMsg);
		}

		// Create Transaction
		MTransaction mtrx = new MTransaction(ctx, orgId, MTransaction.MOVEMENTTYPE_WorkOrder_, locatorId, productId, 0,
				QtyMA, new Timestamp(System.currentTimeMillis()), trxName);
		if (!mtrx.save()) {
			String m_processMsg = "Could not create Material Transaction (MA) [" + product.getValue() + "]";
			throw new AdempiereException(m_processMsg);
		}

	}
	
	public static List<PO> getProductList(int clientId,String user,Properties ctx, String trxName) {
		String tblName = MProduct_Custom.Table_Name;
		List<PO> list  = new Query(ctx,tblName, ""+tblName+".ad_client_id = ? AND u.name = ?", trxName)
					.setParameters(clientId,user).setOrderBy(X_AD_User.COLUMNNAME_AD_User_ID)
					.addJoinClause("JOIN AD_User u on u.PI_Deptartment_id = "+tblName+".PI_Deptartment_id ")
					.list();
		return list;
	}
	
	public static List<PO> getReturnrderList(int clientId, int orgId, Properties ctx, String trxName, String searchKey) {
	    String tblName = X_pi_return.Table_Name;

	    List<PO> list = new Query(ctx, tblName, 
	        "" + tblName + ".ad_client_ID = ? AND " +
	        tblName + ".ad_org_ID = ? AND (" +  // <-- open parenthesis here
	        tblName + ".status ILIKE '%' || COALESCE(?, " + tblName + ".status) || '%' OR " +
	        "mp.name ILIKE '%' || COALESCE(?, mp.name) || '%' OR " +
	        tblName + ".description ILIKE '%' || COALESCE(?, " + tblName + ".description) || '%')", // <-- match with open
	        trxName)
	        .addJoinClause("JOIN C_BPartner mp ON mp.C_BPartner_ID = " + tblName + ".C_BPartner_ID ")
	        .setParameters(clientId, orgId, searchKey, searchKey, searchKey)
	        .setOrderBy(tblName + "." + X_pi_return.COLUMNNAME_pi_return_ID + " DESC")
	        .list();

	    return list;
	}
	
	public static List<PO> getReturnLineList(int clientId, int orgId, Properties ctx, String trxName, int piReturnId) {
	    String tableName = X_pi_returnline.Table_Name;

	    List<PO> list = new Query(ctx, tableName,
	    		tableName +".ad_client_ID = ? AND "+ tableName +".ad_org_ID = ? AND "+ tableName +".pi_return_id = ?", trxName)
	            .addJoinClause("JOIN m_product p ON p.m_product_id = "+ tableName +".m_product_id")
	            .setParameters(clientId, orgId, piReturnId)
	            .setOrderBy(tableName + "." + X_pi_returnline.COLUMNNAME_pi_returnline_ID+" DESC")
	            .list();

	    return list;
	}

	public static List<PO> getBusinessPartnerList(int clientId, int orgId, Properties ctx, String trxName){
		List<PO> list = new Query(ctx, MBPartner.Table_Name,"ad_client_ID =? AND ad_org_ID = ?", trxName)
				.setParameters(clientId,orgId).setOrderBy(MBPartner.COLUMNNAME_C_BPartner_ID + " desc").list();
		return list;
	}
	
	public static List<MRole> getRoleByClientAndName(Properties ctx, int clientId, String appName) {
		String sql = " AD_Client_ID = ? AND "+appName+" = 'Y'";
		return new Query(ctx, MRole.Table_Name, sql, null).setParameters(clientId).list();
	}
	
	@Async
	public static void sendNotificationAsync(String appName, int tableId, int recordId, Properties ctx, String trxName,
			String title, String body, String tableName, Map<String, String> data, int clientId, String messageTitle, int departmentId) {

		executor.submit(() -> {
			sendNotification(appName, tableId, recordId, ctx, trxName, title, body, tableName, data, clientId,
					messageTitle, departmentId);
		});

	}

	private static void sendNotification(String appName, int tableId, int recordId, Properties ctx, String trxName,
			String title, String body, String tableName, Map<String, String> data, int clientId, String messageTitle, int departmentId) {

		List<MRole> mRolList = getRoleByClientAndName(ctx, clientId, appName);
		if (mRolList == null || mRolList.size() == 0)
			return;

		List<PO> userTokens = new ArrayList<PO>();
		for (MRole role : mRolList)
			userTokens.addAll(PiUserToken.getTokensForRole(clientId, role.getAD_Role_ID(), departmentId, ctx, trxName));
		
		if (userTokens == null || userTokens.size() == 0)
			return;

		FCMService fcm = new FCMService();
		int batchSize = 50;
		AtomicBoolean hasError = new AtomicBoolean(false); // Use AtomicBoolean for error handling

		IntStream.range(0, userTokens.size()).boxed().collect(Collectors.groupingBy(i -> i / batchSize)).values()
				.parallelStream().forEach(batchIndexes -> {
					if (hasError.get())
						return; // Exit if there's already an error

					List<PO> batch = batchIndexes.stream().map(userTokens::get).collect(Collectors.toList());

					batch.forEach(uToken -> {
						PiUserToken piUserToken = new PiUserToken(ctx, uToken.get_ID(), null);

						if (piUserToken.getdevicetoken() != null) {
							boolean error = fcm.sendFCMMessage(piUserToken.getdevicetoken(), title, body, null, data);
							if (error) {
								hasError.set(true); // Set error flag
								return; // Exit the inner loop
							}

							MNote mNoteS = new MNote(ctx, 0, null);
							mNoteS.setRecord(tableId, recordId);
							mNoteS.setAD_Message_ID(messageTitle);
							mNoteS.setTextMsg(body);
							mNoteS.setDescription(title + " " + body);
							mNoteS.setAD_User_ID(piUserToken.getAD_User_ID());

							if (mNoteS.getAD_Message_ID() == 240) {
								MMessage mMessage = new MMessage(ctx, 0, null);
								mMessage.setValue(messageTitle);
								mMessage.setMsgText(messageTitle);
								mMessage.setMsgType("M");
								mMessage.saveEx();

								mNoteS.setAD_Message_ID(mMessage.getAD_Message_ID());
							}

							mNoteS.saveEx();
						}
					});
				});

	}

}
