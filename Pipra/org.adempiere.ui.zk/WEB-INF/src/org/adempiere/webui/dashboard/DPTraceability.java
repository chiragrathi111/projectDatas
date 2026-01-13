package org.adempiere.webui.dashboard;

import java.util.List;
import java.util.Properties;

import org.compiere.model.MChangeLog;
import org.compiere.model.MColumn;
import org.compiere.model.MInOut;
import org.compiere.model.MLocator;
import org.compiere.model.MUser;
import org.compiere.model.PO;
import org.compiere.util.Env;
import org.pipra.model.custom.PiProductLabel;
import org.pipra.model.custom.PipraUtils;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class DPTraceability extends DashboardPanel implements EventListener<Event> {

	private static final long serialVersionUID = 1L;

	public DPTraceability() {
		super();
		this.setSclass("activities-box");
	}

	public static JsonArray getDataByProductOrBin(String id) {
		JsonArray dataObject = null;
		if (id != null)
			dataObject = init(id);
		return dataObject;
	}

	private static JsonArray init(String id) {
		String who = "who";
		String what = "what";
		String where = "where";
		String start = "start";
		String received = "received";
		String qcaccepted = "qcaccepted";
		String stored = "stored";
		String internalmove = "inernalmove";
		String picked = "picked";
		String putAway = "putAway";
		String dispatched = "dispatched";
		String toBin = "toBin";
		String fromBin = "fromBin";
		Properties ctx = Env.getCtx();
		int clientId = Env.getAD_Client_ID(ctx);

		JsonArray dataObject = new JsonArray();

		String columnName = "m_product_id";
		if (id.matches(".*[a-zA-Z].*"))
			columnName = "labelUUId";
		Object idObj = null;
		if (columnName.equalsIgnoreCase("m_product_id"))
			idObj = Integer.valueOf(id);
		else
			idObj = id;
		List<PO> poList = PiProductLabel.getPiProductLabel(columnName, idObj, ctx, null, null);

		for (PO po : poList) {
			PiProductLabel label = new PiProductLabel(ctx, po.get_ID(), null);
			MInOut minoutLabel = new MInOut(ctx, label.getM_InOutLine().getM_InOut_ID(), null);

			JsonObject receivedOrPicked = new JsonObject();
			MUser receivedOrPickedUser = new MUser(ctx, label.getCreatedBy(), null);

			if (label.isSOTrx() != true) {
				receivedOrPicked.addProperty(who, receivedOrPickedUser.getName());
				receivedOrPicked.addProperty(what, received);
				receivedOrPicked.addProperty(where, label.getM_Locator().getM_Warehouse().getValue());
				receivedOrPicked.addProperty(start, label.getCreated().toString());
				dataObject.add(receivedOrPicked);

				MColumn mColumn = MColumn.get(ctx, MInOut.Table_Name, MInOut.COLUMNNAME_DocStatus);
				List<PO> changeLogs = PipraUtils.getMChangeLogs(ctx, clientId, minoutLabel.getM_InOut_ID(),
						minoutLabel.get_Table_ID(), mColumn.getAD_Column_ID(), "IP", "CO");
				if (changeLogs.size() != 0) {
					MChangeLog mChangeLog = (MChangeLog) changeLogs.get(0);
					MUser mChangeLogUser = new MUser(ctx, mChangeLog.getCreatedBy(), null);

					JsonObject qcObj = new JsonObject();
					qcObj.addProperty(who, mChangeLogUser.getName());
					qcObj.addProperty(what, qcaccepted);
					qcObj.addProperty(where, label.getM_Locator().getM_Warehouse().getValue());
					qcObj.addProperty(start, mChangeLogUser.getCreated().toString());
					dataObject.add(qcObj);
				}
			} else {
				JsonObject dispatchedObj = new JsonObject();
				dispatchedObj.addProperty(who, receivedOrPickedUser.getName());
				dispatchedObj.addProperty(what, dispatched);
				dispatchedObj.addProperty(where, label.getM_Locator().getM_Warehouse().getValue());
				dispatchedObj.addProperty(start, label.getCreated().toString());
				dataObject.add(dispatchedObj);
			}

			MColumn mColumn = MColumn.get(ctx, PiProductLabel.Table_Name, PiProductLabel.COLUMNNAME_M_Locator_ID);
			List<PO> changeLogs = PipraUtils.getMChangeLogs(ctx, clientId, label.getpi_productLabel_ID(),
					label.get_Table_ID(), mColumn.getAD_Column_ID(), null, null);
			for (PO log : changeLogs) {
				MChangeLog mChangeLog = (MChangeLog) log;
				MLocator fromBinlocator = new MLocator(ctx, Integer.valueOf(mChangeLog.getOldValue()), null);
				MLocator toBinlocator = new MLocator(ctx, Integer.valueOf(mChangeLog.getNewValue()), null);
				MUser internalMoveuser = new MUser(ctx, mChangeLog.getCreatedBy(), po.get_TrxName());
				JsonObject internalMove = new JsonObject();
				internalMove.addProperty(who, internalMoveuser.getName());

				if(fromBinlocator.getValue().equalsIgnoreCase("Receiving Locator")) {
					internalMove.addProperty(what, stored);
					internalMove.addProperty(toBin, toBinlocator.getValue());
				}
				else if (!toBinlocator.getValue().equalsIgnoreCase("Dispatch Locator")) {
					internalMove.addProperty(what, internalmove);
					internalMove.addProperty(toBin, toBinlocator.getValue());
				} else
					internalMove.addProperty(what, picked);
				internalMove.addProperty(where, label.getM_Locator().getM_Warehouse().getValue());

				internalMove.addProperty(fromBin, fromBinlocator.getValue());
				internalMove.addProperty(start, mChangeLog.getCreated().toString());

				dataObject.add(internalMove);
			}

		}
		return dataObject;
	}

	@Override
	public void onEvent(Event event) throws Exception {

	}

}
