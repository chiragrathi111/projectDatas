package com.pipra.rwpl.utils;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.I_AD_User;
import org.compiere.model.MBPartner;
import org.compiere.model.MMessage;
import org.compiere.model.MNote;
import org.compiere.model.MProduct;
import org.compiere.model.MProductCategory;
import org.compiere.model.MRole;
import org.compiere.model.MStorageOnHand;
import org.compiere.model.MTransaction;
import org.compiere.model.MUserRoles;
import org.compiere.model.MWarehouse;
import org.compiere.model.PO;
import org.compiere.model.Query;
import org.compiere.model.X_M_LocatorType;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.idempiere.adInterface.x10.ADLoginRequest;
import org.springframework.scheduling.annotation.Async;

import com.pipra.rwpl.fcm.FCMService;
import com.pipra.rwpl.model.AdRole_Custom;
import com.pipra.rwpl.model.MLocator_Custom;
import com.pipra.rwpl.model.PiUserToken;
import com.pipra.rwpl.model.X_pi_productLabel;

public class RwplUtils {

	private static final ExecutorService executor = Executors.newCachedThreadPool();

	public static ADLoginRequest convertAdLogin(com.pipra.rwpl.x10.ADLoginRequest login) {
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

	@Async
	public static void sendNotificationAsync(boolean supervisor, boolean labour, int tableId, int recordId,
			Properties ctx, String trxName, String title, String body, String tableName, Map<String, String> data,
			int clientId, String messageTitle) {

		executor.submit(() -> {
			sendNotification(supervisor, labour, tableId, recordId, ctx, trxName, title, body, tableName, data,
					clientId, messageTitle);
		});

	}

	private static void sendNotification(boolean supervisor, boolean labour, int tableId, int recordId, Properties ctx,
			String trxName, String title, String body, String tableName, Map<String, String> data, int clientId,
			String messageTitle) {

		List<MRole> roleList = new ArrayList<MRole>();
		if (supervisor)
			roleList = AdRole_Custom.getRoleBySupervisor(ctx, clientId);

		if (labour)
			roleList.addAll(AdRole_Custom.getRoleBySupervisor(ctx, clientId));

//		MRole mRole = getRoleByClientAndName(ctx, clientId, userType);
		if (roleList == null || roleList.size() == 0)
			return;

		List<MUserRoles> mUserRoleList = new ArrayList<MUserRoles>();
		roleList.forEach(it -> {
			mUserRoleList.addAll(getUsersByClientAndRole(ctx, clientId, it.getAD_Role_ID()));
		});

		if (mUserRoleList == null || mUserRoleList.size() == 0)
			return;

		for (MUserRoles mUserRole : mUserRoleList) {

			I_AD_User user = mUserRole.getAD_User();
			List<PO> userTokenList = PiUserToken.getTokensForUser(Env.getAD_Client_ID(Env.getCtx()),
					user.getAD_User_ID(), ctx, null);

			if (userTokenList.size() != 0) {

				FCMService fcm = new FCMService();
				int batchSize = 50;
				AtomicBoolean hasError = new AtomicBoolean(false); // Use AtomicBoolean for error handling

				IntStream.range(0, userTokenList.size()).boxed().collect(Collectors.groupingBy(i -> i / batchSize))
						.values().parallelStream().forEach(batchIndexes -> {
							if (hasError.get())
								return; // Exit if there's already an error

							List<PO> batch = batchIndexes.stream().map(userTokenList::get).collect(Collectors.toList());

							batch.forEach(uToken -> {
								PiUserToken piUserToken = new PiUserToken(ctx, uToken.get_ID(), null);

								if (piUserToken.getdevicetoken() != null) {
									boolean error = fcm.sendFCMMessage(piUserToken.getdevicetoken(), title, body, null,
											data);
									if (error) {
										hasError.set(true); // Set error flag
										return; // Exit the inner loop
									}

//									MNote mNoteS = new MNote(ctx, 0, null);
//									mNoteS.setRecord(tableId, recordId);
//									mNoteS.setAD_Message_ID(messageTitle);
//									mNoteS.setTextMsg(body);
//									mNoteS.setDescription(title + " " + body);
////									mNoteS.setAD_User_ID(piUserToken.getAD_User_ID());
//
//									if (mNoteS.getAD_Message_ID() == 240) {
//										MMessage mMessage = new MMessage(ctx, 0, null);
//										mMessage.setValue(messageTitle);
//										mMessage.setMsgText(messageTitle);
//										mMessage.setMsgType("M");
//										mMessage.saveEx();
//
//										mNoteS.setAD_Message_ID(mMessage.getAD_Message_ID());
//									}
//
//									mNoteS.saveEx();
								}
							});
						});

				if (hasError.get()) {
					break; // Exit the outer loop if an error occurred
				}
			}
		}	
				MNote mNoteS = new MNote(ctx, 0, null);
				mNoteS.setRecord(tableId, recordId);
				mNoteS.setAD_Message_ID(messageTitle);
				mNoteS.setTextMsg(body);
				mNoteS.setDescription(title + " " + body);
//				mNoteS.setAD_User_ID(user.getAD_User_ID()); // assign to the user

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

	public static MRole getRoleByClientAndName(Properties ctx, int clientId, String rolename) {
		String sql = " AD_Client_ID = ? AND name = ?";
		return new Query(ctx, MRole.Table_Name, sql, null).setParameters(clientId, rolename).first();
	}

	public static List<MUserRoles> getUsersByClientAndRole(Properties ctx, int clientId, int roleId) {
		String sql = " AD_Client_ID = ? AND AD_Role_ID = ?";
		List<MUserRoles> muserList = new Query(ctx, MUserRoles.Table_Name, sql, null).setParameters(clientId, roleId)
				.setOrderBy(MUserRoles.COLUMNNAME_AD_User_ID).list();
		return muserList;
	}

	public static List<PO> getWarehousesByClientOrg(Properties ctx, String trxName, int clientId, String orgIds) {

		List<PO> poList = new Query(ctx, MWarehouse.Table_Name, "ad_client_ID =? AND ad_org_ID IN (" + orgIds + ")",
				trxName).setParameters(clientId).setOrderBy(MWarehouse.COLUMNNAME_M_Warehouse_ID).list();

		return poList;
	}

	public static List<PO> getProductsByClientOrg(Properties ctx, String trxName, int clientId, String orgIds) {

		List<PO> poList = new Query(ctx, MProduct.Table_Name, "ad_client_ID =? AND ad_org_ID IN (" + orgIds + ")",
				trxName).setParameters(clientId).setOrderBy(MProduct.COLUMNNAME_M_Product_ID).list();

		return poList;
	}

	public static List<PO> getBPartnersByClientOrg(Properties ctx, String trxName, int clientId, String orgIds) {

		List<PO> poList = new Query(ctx, MBPartner.Table_Name,
				"ad_client_ID =? AND isvendor = 'Y' AND ad_org_ID IN (" + orgIds + ") ", trxName)
				.setParameters(clientId).setOrderBy(MBPartner.COLUMNNAME_BPartner_Parent_ID).list();

		return poList;
	}

	public static List<PO> getProductCategoryClientOrg(Properties ctx, String trxName, int clientId, String orgIds) {

		List<PO> poList = new Query(ctx, MProductCategory.Table_Name,
				"ad_client_ID =? AND ad_org_ID IN (" + orgIds + ") ", trxName).setParameters(clientId)
				.setOrderBy(MProductCategory.COLUMNNAME_M_Product_Category_ID).list();

		return poList;
	}

	public static Integer getSumOfQntyonHandByLocator(int mLocatorId, int clientId) {
		Integer qtyonhand = 0;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			String sql = "SELECT SUM(qtyonhand) as qtyonhand FROM adempiere.M_StorageOnHand WHERE M_Locator_ID= "
					+ mLocatorId + " AND AD_Client_ID =" + clientId + ";";

			pstm = DB.prepareStatement(sql.toString(), null);
			rs = pstm.executeQuery();
			while (rs.next()) {
				qtyonhand = rs.getInt("qtyonhand");
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeDbCon(pstm, rs);
		}
		return qtyonhand;
	}
	
//	public static List<PO> getPiProductLabelById(String labelUUID, Properties ctx, String trxName) {
//		List<PO> poList = new Query(ctx, X_pi_productLabel.Table_Name, "labeluuid =? AND issotrx = 'N'", trxName).setParameters(labelUUID).list();
//		return poList;
//	}
	
	public static List<PO> getPiProductLabelById(String labelUUID, Properties ctx, String trxName) {
	    // First, check if there are any sales transactions for the given UUID
	    int salesTransactionCount = new Query(ctx, X_pi_productLabel.Table_Name, "labeluuid = ? AND issotrx = 'Y'", trxName)
	        .setParameters(labelUUID)
	        .count();

	    // If there are sales transactions, return an empty list
	    if (salesTransactionCount > 0) {
	        return new ArrayList<>(); // No details to return
	    }

	    // If no sales transactions, proceed to get the product labels with issotrx = 'N'
	    List<PO> poList = new Query(ctx, X_pi_productLabel.Table_Name, "labeluuid = ? AND issotrx = 'N'", trxName)
	        .setParameters(labelUUID)
	        .list();

	    return poList;
	}

	private static void closeDbCon(PreparedStatement pstm, ResultSet rs) {
		try {
			if (pstm != null)
				pstm.close();
			if (rs != null)
				rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static int getLocatorIdByType(Properties ctx, String trxName, int clientID, String type) {
		
		PO po = new Query(ctx, X_M_LocatorType.Table_Name, ""+type+" = 'Y' AND AD_Client_ID = "+clientID+"", trxName)
				.setOrderBy(X_M_LocatorType.COLUMNNAME_M_LocatorType_ID).firstOnly();
		
		if (po != null && po.get_ID() != 0) {
			PO locator = new Query(ctx, MLocator_Custom.Table_Name, "M_LocatorType_ID=? AND AD_Client_ID=?", trxName)
					.setParameters(po.get_ID(),clientID).first();
			
//			PO locatorPo = new Query(ctx, MLocator_Custom.Table_Name, "M_LocatorType_ID = " + po.get_ID() + " AND AD_Client_ID = "+clientID+"", trxName)
//					.setOrderBy(MLocator_Custom.COLUMNNAME_M_Locator_ID).firstOnly();
			
			if (locator != null && locator.get_ID() != 0)
				return locator.get_ID();

		}
		return 0;
	}
	
	public static void addOrReduceInventory(MProduct product, int qnty, int locatorId, int orgId, Properties ctx,
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
}
