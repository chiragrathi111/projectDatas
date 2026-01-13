package org.pipra.webservices.custom;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import org.adempiere.base.IColumnCallout;
import org.compiere.model.GridField;
import org.compiere.model.GridTab;
import org.compiere.model.MInOut;
import org.compiere.model.MMessage;
import org.compiere.model.MNote;
import org.compiere.model.MOrder;
import org.compiere.model.MUser;
import org.compiere.model.PO;
import org.compiere.process.DocAction;
import org.compiere.util.Env;
import org.pipra.custom.fcm.FCMService;
import org.pipra.model.custom.PiUserToken;
import org.pipra.model.custom.PipraUtils;

public class PiColumnCalloutFactory implements IColumnCallout {

	@Override
	public String start(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue) {

		String title = null;
		String body = null;
		String userType = null;
		String documentNo = null;
		String tableName = null;
		String messageTitle = null;
		if (mTab.getTableName().equalsIgnoreCase(MOrder.Table_Name)) {
			COrder_Custom cOrder = new COrder_Custom(ctx, mTab.getRecord_ID(), null);
			documentNo = cOrder.getDocumentNo();
			if (cOrder.getC_Order_ID() != 0 && mField.getColumnName().equalsIgnoreCase(MOrder.COLUMNNAME_DocAction)) {
				if (value.toString().equalsIgnoreCase(DocAction.ACTION_Complete)) {
					tableName = MOrder.Table_Name;
					if (cOrder.isSOTrx() && !cOrder.getDocStatus().equalsIgnoreCase(value.toString())) {
						title = "New Sales Order: " + documentNo + "";
						body = "Sales Order - " + documentNo + " added to process";
						userType = "Supervisor";
						messageTitle = "salesOrderCreated";
					} else if (!cOrder.isSOTrx() && !cOrder.getDocStatus().equalsIgnoreCase(value.toString())) {
						title = "New Purchase Order: " + documentNo + "";
						body = "Purchase order - " + documentNo + " added to process";
						userType = "Supervisor";
						messageTitle = "purchaseOrderCreated";
					}
				}
			} else if (mField.getColumnName().equalsIgnoreCase(COrder_Custom.COLUMNNAME_PUTSTATUS)
					&& MOrder.COLUMNNAME_DocStatus.equalsIgnoreCase(DocAction.ACTION_Complete)) {

				title = "Marked Sales Order ";
				body = "Ready To Pick";
				userType = "Supervisor";
				messageTitle = "MarkedSalesOrderReadyToPick";
			}
		} else if (mTab.getTableName().equalsIgnoreCase(MInOut.Table_Name)) {
			MInOut_Custom mInout = new MInOut_Custom(ctx, mTab.getRecord_ID(), null);
			documentNo = mInout.getDocumentNo();
			if (mInout.getM_InOut_ID() != 0 && mField.getColumnName().equalsIgnoreCase(MInOut.COLUMNNAME_DocAction)) {
				if (value.toString().equalsIgnoreCase(DocAction.ACTION_Complete)) {
					tableName = MInOut.Table_Name;
					if (mInout.isSOTrx()) {
						title = "Products dispatched with shipment - " + documentNo + " ";
						body = "Products dispatched with Shipment No: " + documentNo + " for Order - "
								+ mInout.getC_Order().getDocumentNo() + "";
						userType = "Supervisor";
						messageTitle = "shipmentDisptahced";
					} else {
						title = "Products added for Put away";
						body = "New products ready for Put away";
						userType = "Labour";
						messageTitle = "productsAddedForPutaway";
					}
				}
			}
		}

		if (userType != null)
			setTimerToSendNotification(userType, documentNo, mTab.getRecord_ID(), ctx, title, body, tableName, messageTitle);

		return null;
	}

	private void setTimerToSendNotification(String userType, String documentNo, int recordID, Properties ctx,
			String title, String body, String tableName, String messageTitle) {
		Timer timer = new Timer();
		int loopCount = 0;
		int MAX_LOOP_COUNT = 100;
		int clientId = Env.getAD_Client_ID(Env.getCtx());
		if (userType != null) {
			Map<String, String> data = new HashMap<>();
			if (userType.equalsIgnoreCase("Supervisor")) {
				data.put("recordId", String.valueOf(recordID));
				data.put("documentNo", documentNo);
			}
			timer.scheduleAtFixedRate(new TimerTask() {
				@Override
				public void run() {
					boolean notificationSent = sendNotification(userType, recordID, ctx, title, body, tableName, data, clientId, messageTitle);
					if (notificationSent || loopCount >= MAX_LOOP_COUNT) {
						timer.cancel();
					}
				}
			}, 0, 5000);

		}
	}

	private boolean sendNotification(String userType, int recordID, Properties ctx, String title, String body,
			String tableName, Map<String, String> data, int clientId, String messageTitle) {
		boolean notificationSent = false;
		boolean docStatus = false;
		int tableId = 0;
		if (tableName != null && tableName.equalsIgnoreCase(MOrder.Table_Name)) {
			COrder_Custom cOrder = new COrder_Custom(ctx, recordID, null);
			if (cOrder.getDocStatus().equalsIgnoreCase(DocAction.ACTION_Complete)) {
				tableId = cOrder.get_Table_ID();
				docStatus = true;
			}
		} else if (tableName != null & tableName.equalsIgnoreCase(MInOut.Table_Name)) {
			MInOut_Custom inout = new MInOut_Custom(ctx, recordID, null);
			if (inout.getDocStatus().equalsIgnoreCase(DocAction.ACTION_Complete)) {
				tableId = inout.get_Table_ID();
				docStatus = true;
			}
		}
		if (docStatus) {
			List<PO> poList = PipraUtils.getUsersForClient(ctx, clientId);
			if (poList.size() != 0) {
				for (PO po : poList) {
					MUser user = (MUser) po;
					List<PO> userTokenList = PiUserToken.getTokensForUser(Env.getAD_Client_ID(Env.getCtx()),
							user.getAD_User_ID(), ctx, null);
					if (userTokenList.size() != 0) {
						for (PO uToken : userTokenList) {
							PiUserToken piUserToken = new PiUserToken(ctx, uToken.get_ID(), null);
							FCMService fcm = new FCMService();
							if (user.getC_Job().getName().equalsIgnoreCase(userType)) {
								fcm.sendFCMMessage(piUserToken.getdevicetoken(), title, body, null, data);

								MNote mNoteS = new MNote(ctx, 0, null);
								mNoteS.setRecord(tableId, recordID);
								mNoteS.setAD_Message_ID(messageTitle);
								mNoteS.setTextMsg(body);
								mNoteS.setDescription(title + " " + body);
								mNoteS.setAD_User_ID(piUserToken.getAD_User_ID());
								mNoteS.saveEx();
								
								notificationSent = true;
							}

						}
					}

				}
			}
		}
		return notificationSent;
	}

}
