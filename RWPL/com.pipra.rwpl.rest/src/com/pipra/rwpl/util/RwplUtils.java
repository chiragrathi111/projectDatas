package com.pipra.rwpl.util;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.compiere.model.I_AD_User;
import org.compiere.model.MMessage;
import org.compiere.model.MNote;
import org.compiere.model.MOrderLine;
import org.compiere.model.MRole;
import org.compiere.model.MUserRoles;
import org.compiere.model.PO;
import org.compiere.model.Query;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.idempiere.webservices.model.MWebService;
import org.idempiere.webservices.model.X_WS_WebServiceType;
import org.springframework.scheduling.annotation.Async;

import com.pipra.rwpl.entity.model.AdRole_Custom;
import com.pipra.rwpl.entity.model.PiUserToken;
import com.pipra.rwpl.fcm.FCMService;

public class RwplUtils {

	private static final ExecutorService executor = Executors.newCachedThreadPool();

	public static int getTopRoleForUser(int clientId, int userId, Properties ctx, String trxName) {

		int topRoleId = 0;

		String sql = "SELECT ur.ad_role_id\n" + "FROM ad_user u\n" + "JOIN ad_user_roles ur \n"
				+ "    ON u.ad_user_id = ur.ad_user_id\n" + "JOIN ad_role ar \n"
				+ "    ON ur.ad_role_id = ar.ad_role_id\n" + "WHERE u.ad_user_id    = ?\n"
				+ "  AND u.ad_client_id  = ?\n" + "  AND ur.ad_client_id = ?\n" + "  AND ur.isactive     = 'Y'\n"
				+ "LIMIT 1;\n" + "";

		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			pstmt = DB.prepareStatement(sql, trxName);

			pstmt.setInt(1, userId);
			pstmt.setInt(2, clientId);
			pstmt.setInt(3, clientId);

			rs = pstmt.executeQuery();
			if (rs.next()) {
				topRoleId = rs.getInt("ad_role_id");
			}

		} catch (Exception e) {
			return topRoleId;
		} finally {
			DB.close(rs, pstmt);
		}

		return topRoleId;
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

	public static List<MUserRoles> getUsersByClientAndRole(Properties ctx, int clientId, int roleId) {
		String sql = " AD_Client_ID = ? AND AD_Role_ID = ?";
		List<MUserRoles> muserList = new Query(ctx, MUserRoles.Table_Name, sql, null).setParameters(clientId, roleId)
				.setOrderBy(MUserRoles.COLUMNNAME_AD_User_ID).list();
		return muserList;
	}
	
	public static String authenticate(String webServiceValue, String methodValue, Properties ctx) {

        MWebService m_webservice = new Query(ctx, MWebService.Table_Name, "Value=?", null)
                .setParameters(webServiceValue)
                .setOnlyActiveRecords(true)
                .first();

        if (m_webservice == null) {
            return "Web Service '" + webServiceValue + "' not registered or inactive.";
        }

        X_WS_WebServiceType m_webservicetype = new Query(ctx, X_WS_WebServiceType.Table_Name, "WS_WebService_ID=? AND Value=?", null)
                .setParameters(m_webservice.getWS_WebService_ID(), methodValue)
                .setOnlyActiveRecords(true)
                .first();

        if (m_webservicetype == null) {
            return "Method '" + methodValue + "' not registered or inactive under service '" + webServiceValue + "'.";
        }

        int AD_Role_ID = Env.getAD_Role_ID(ctx);

        String sql = "SELECT COUNT(*) FROM WS_WebServiceTypeAccess " +
                     "WHERE AD_Role_ID=? AND WS_WebServiceType_ID=?";

        int accessCount = DB.getSQLValue(null, sql, AD_Role_ID, m_webservicetype.getWS_WebServiceType_ID());

        if (accessCount <= 0) {
            return "Web Service Error: Login role (" + AD_Role_ID + ") does not have access to method '" + methodValue + "'.";
        }

        return null;
    }
	
	public static Date convertTimestampToDate(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        }
        return new Date(timestamp.getTime());
    }
	
	public static BigDecimal getReceivedQty(MOrderLine line, String trxName) {
        if (line.getC_OrderLine_ID() == 0) {
            return BigDecimal.ZERO;
        }

        String sql = "SELECT COALESCE(SUM(QtyEntered), 0) FROM M_InOutLine WHERE C_OrderLine_ID=?";
        
        return DB.getSQLValueBD(trxName, sql, line.getC_OrderLine_ID());
    }
	
    private static final String DEFAULT_DATE_FORMAT = "dd/MM/yyyy";
    public static String formatTimestamp(Timestamp timestamp) {
        if (timestamp == null) {
            return "";
        }
        
        // SimpleDateFormat is NOT thread-safe, so it must be created locally for each call.
        SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
        
        // Since Timestamp extends Date, we can cast and format it.
        return sdf.format(new Date(timestamp.getTime()));
    }

}
