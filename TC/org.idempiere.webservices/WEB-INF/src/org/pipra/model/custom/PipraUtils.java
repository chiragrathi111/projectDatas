package org.pipra.model.custom;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.compiere.model.MNote;
import org.compiere.model.MUser;
import org.compiere.model.PO;
import org.compiere.model.Query;
import org.compiere.util.Env;
import org.pipra.custom.fcm.FCMService;
import org.springframework.scheduling.annotation.Async;

public class PipraUtils {
	
	private static final ExecutorService executor = Executors.newCachedThreadPool();

	public static List<PO> getUsersForClient(Properties ctx, int clientId) {
		String sql = " AD_Client_ID = ?";
		List<PO> poList = new Query(ctx, MUser.Table_Name, sql, null).setParameters(clientId)
				.setOrderBy(MUser.COLUMNNAME_AD_User_ID).list();
		return poList;
	}
	
	@Async
	public static void sendNotificationAsync(Properties ctx, int clientId, String title, String body,
			String userType, String trxName, int tableId, int recordId, Map<String, String> data, String messageTitle) {
		
	}

	public static void sendNotification(Properties ctx, int clientId, String title, String body,
			String userType, String trxName, int tableId, int recordId, Map<String, String> data, String messageTitle) {
		List<PO> poList = PipraUtils.getUsersForClient(ctx, clientId);
		if (poList.size() != 0) {
			for (PO po : poList) {
				MUser user = (MUser) po;
				List<PO> userTokenList = PiUserToken.getTokensForUser(Env.getAD_Client_ID(Env.getCtx()),
						user.getAD_User_ID(), ctx, null);
				if (userTokenList.size() != 0) {
					FCMService fcm = new FCMService();

					for (PO uToken : userTokenList) {
						PiUserToken piUserToken = new PiUserToken(ctx, uToken.get_ID(), null);
						if (user.getC_Job().getName().equalsIgnoreCase(userType)) {
							
							fcm.sendFCMMessage(piUserToken.getdevicetoken(), title, body, null, data);
							
							MNote mNoteS = new MNote(ctx, 0, null);
							mNoteS.setRecord(tableId, recordId);							
							mNoteS.setAD_Message_ID(messageTitle);
							mNoteS.setTextMsg(body);
							mNoteS.setDescription(title + " " +  body);
							mNoteS.setAD_User_ID(piUserToken.getAD_User_ID());
							mNoteS.saveEx();
						}
					}
				}

			}
		}
	}
}
