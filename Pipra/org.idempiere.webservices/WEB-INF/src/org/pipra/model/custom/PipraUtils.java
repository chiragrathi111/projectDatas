package org.pipra.model.custom;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.compiere.model.I_AD_User;
import org.compiere.model.MBPartner;
import org.compiere.model.MChangeLog;
import org.compiere.model.MLocator;
import org.compiere.model.MMessage;
import org.compiere.model.MNote;
import org.compiere.model.MProduct;
import org.compiere.model.MRole;
import org.compiere.model.MShipper;
import org.compiere.model.MUser;
import org.compiere.model.MUserRoles;
import org.compiere.model.MWarehouse;
import org.compiere.model.PO;
import org.compiere.model.Query;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.pipra.custom.fcm.FCMService;
import org.springframework.scheduling.annotation.Async;

public class PipraUtils {

	private static final ExecutorService executor = Executors.newCachedThreadPool();

	public static int getQntyonHandByProductAndLocator(int mLocatorId, int mProductId, int clientId) {
		PreparedStatement pstm = null;
		ResultSet rs = null;
		int totalQnty = 0;
		try {
			String sql = "SELECT SUM(qtyonhand)  as qtyonhand FROM M_StorageOnHand WHERE AD_Client_ID =" + clientId
					+ "";
			if (mLocatorId != 0 & mProductId != 0)
				sql += " AND M_Locator_ID=" + mLocatorId + " AND M_Product_ID=" + mProductId + "";
			else if (mLocatorId == 0 & mProductId != 0)
				sql += "AND M_Product_ID=" + mProductId + "";
			else if (mLocatorId != 0 & mProductId == 0)
				sql += " AND M_Locator_ID=" + mLocatorId + "";

			pstm = DB.prepareStatement(sql.toString(), null);
			rs = pstm.executeQuery();
			while (rs.next()) {
				totalQnty = rs.getInt("qtyonhand");
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeDbCon(pstm, rs);
		}
		return totalQnty;
	}

	public static Integer getSumOfQntyonHandByLocator(int mLocatorId, int clientId) {
		Integer qtyonhand = 0;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			String sql = "SELECT SUM(qtyonhand) as qtyonhand FROM adempiere.M_StorageOnHand WHERE M_Locator_ID= " + mLocatorId
					+ " AND AD_Client_ID =" + clientId + ";";

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

	public static int getQntyBookedForShipment(int clientId, int mproductId) {
		PreparedStatement pstm = null;
		ResultSet rs = null;
		int qtyBooked = 0;
		try {
			String sql = "SELECT SUM(b.qtyentered) as qtyBooked FROM adempiere.m_inoutline b\n"
					+ "JOIN adempiere.m_inout c ON c.m_inout_id = b.m_inout_id \n"
					+ "WHERE c.docstatus !='CO' AND c.AD_Client_ID =" + clientId + " AND m_product_id = " + mproductId
					+ ";";

			pstm = DB.prepareStatement(sql.toString(), null);
			rs = pstm.executeQuery();
			while (rs.next()) {
				qtyBooked = rs.getInt("qtyBooked");
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeDbCon(pstm, rs);
		}
		return qtyBooked;
	}

	public static List<PO> getMChangeLogs(Properties ctx, int clientId, int recordId, int tableId, int columnId,
			String oldvalue, String newValue) {
		String sql = " Record_ID = ? AND AD_Client_ID = ? AND AD_Table_ID = ? AND AD_Column_ID = ? ";
		List<Object> parameters = new ArrayList<>();
		parameters.add(recordId);
		parameters.add(clientId);
		parameters.add(tableId);
		parameters.add(columnId);

		if (oldvalue != null) {
			sql += "AND oldValue = ? ";
			parameters.add(oldvalue);
		}
		if (newValue != null) {
			sql += "AND newValue = ?";
			parameters.add(newValue);
		}
		List<PO> poList = new Query(ctx, MChangeLog.Table_Name, sql, null).setParameters(parameters.toArray())
				.setOrderBy(MChangeLog.COLUMNNAME_AD_ChangeLog_ID).list();
		return poList;
	}

	public static List<PO> getShipperListForClient(Properties ctx, int clientId) {
		String sql = " AD_Client_ID = ?";
		List<PO> poList = new Query(ctx, MShipper.Table_Name, sql, null).setParameters(clientId)
				.setOrderBy(MShipper.COLUMNNAME_M_Shipper_ID).list();
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

	public static List<PO> getUsersForClient(Properties ctx, int clientId) {
		String sql = " AD_Client_ID = ?";
		List<PO> poList = new Query(ctx, MUser.Table_Name, sql, null).setParameters(clientId)
				.setOrderBy(MUser.COLUMNNAME_AD_User_ID).list();
		return poList;
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

	@Async
	public static void sendNotificationAsync(String userType, int tableId, int recordId, Properties ctx, String trxName,
			String title, String body, String tableName, Map<String, String> data, int clientId, String messageTitle) {

		executor.submit(() -> {
			sendNotification(userType, tableId, recordId, ctx, trxName, title, body, tableName, data, clientId,
					messageTitle);
		});

	}

	private static void sendNotification(String userType, int tableId, int recordId, Properties ctx, String trxName,
			String title, String body, String tableName, Map<String, String> data, int clientId, String messageTitle) {

		MRole mRole = getRoleByClientAndName(ctx, clientId, userType);
		if (mRole == null || mRole.getAD_Role_ID() == 0)
		    return;

		List<MUserRoles> mUserRoleList = getUsersByClientAndRole(ctx, clientId, mRole.getAD_Role_ID());
		if (mUserRoleList.size() == 0)
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
		                    if (hasError.get()) return; // Exit if there's already an error

		                    List<PO> batch = batchIndexes.stream().map(userTokenList::get).collect(Collectors.toList());

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

		        if (hasError.get()) {
		            break; // Exit the outer loop if an error occurred
		        }
		    }
		}
}

	public static MLocator getLocatorForWarehouseByName(Properties ctx, String trxName, int warehouseId,
			String locatorName) {

		PO po = new Query(ctx, MLocator.Table_Name, "m_warehouse_id =? AND value = ?", trxName)
				.setParameters(warehouseId, locatorName).setOrderBy(MLocator.COLUMNNAME_M_Locator_ID).firstOnly();
		MLocator mLocator = (MLocator) po;

		return mLocator;
	}
	
	public static List<PO> getWarehousesByClientOrg(Properties ctx, String trxName, int clientId, int orgId) {

		List<PO> poList = new Query(ctx, MWarehouse.Table_Name, "ad_client_ID =? AND ad_org_ID = ?", trxName)
				.setParameters(clientId, orgId).setOrderBy(MWarehouse.COLUMNNAME_M_Warehouse_ID).list();

		return poList;
	}
	
	public static List<PO> getProductsByClientOrg(Properties ctx, String trxName, int clientId, int orgId) {

		List<PO> poList = new Query(ctx, MProduct.Table_Name, "ad_client_ID =? AND ad_org_ID = ?", trxName)
				.setParameters(clientId, orgId).setOrderBy(MProduct.COLUMNNAME_M_Product_ID).list();

		return poList;
	}
	
	public static List<PO> getBPartnersByClientOrg(Properties ctx, String trxName, int clientId, int orgId) {

		List<PO> poList = new Query(ctx, MBPartner.Table_Name, "ad_client_ID =? AND ad_org_ID = ?", trxName)
				.setParameters(clientId, orgId).setOrderBy(MBPartner.COLUMNNAME_BPartner_Parent_ID).list();

		return poList;
	}
	
	public static List<PO> getusersByClientOrg(Properties ctx, String trxName, int clientId, String orgIds) {

		List<PO> poList = new Query(ctx, MUser.Table_Name, "ad_client_ID =? AND ad_org_ID IN (" + orgIds + ")", trxName)
				.setParameters(clientId).setOrderBy(MUser.COLUMNNAME_AD_User_ID).list();

		return poList;
	}
}
