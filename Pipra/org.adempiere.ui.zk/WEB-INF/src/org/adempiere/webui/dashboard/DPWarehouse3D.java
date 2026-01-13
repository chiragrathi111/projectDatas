//package org.adempiere.webui.dashboard;
//
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileReader;
//import java.io.IOException;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.util.LinkedHashMap;
//import java.util.Properties;
//
//import org.adempiere.webui.component.Label;
//import org.adempiere.webui.theme.ITheme;
//import org.adempiere.webui.util.ServerPushTemplate;
//import org.adempiere.webui.util.ZKUpdateUtil;
//import org.compiere.model.MAttachment;
//import org.compiere.model.MAttachmentEntry;
//import org.compiere.model.MSysConfig;
//import org.compiere.model.MWarehouse;
//import org.compiere.util.DB;
//import org.compiere.util.Env;
//import org.zkoss.zk.au.out.AuScript;
//import org.zkoss.zk.ui.Component;
//import org.zkoss.zk.ui.Executions;
//import org.zkoss.zk.ui.event.Event;
//import org.zkoss.zk.ui.event.EventListener;
//import org.zkoss.zk.ui.event.Events;
//import org.zkoss.zk.ui.util.Clients;
//import org.zkoss.zul.Div;
//import org.zkoss.zul.Iframe;
//
//import com.google.gson.Gson;
//import com.google.gson.JsonObject;
//
//public class DPWarehouse3D extends DashboardPanel implements EventListener<Event> {
//
//	private static final long serialVersionUID = 1L;
//	private final String warehouseKey = "warehouseName";
//	private static final String URL = MSysConfig.getValue("WAREHOUSE_3D_CONFIG_URL");
//	private Iframe iframe;
//	private int selectedWarehouseId;
//	private Div warehouseLabels;
//	private Div iframeContainer;
//
//	private JsonObject warehouseData;
//	private LinkedHashMap<String, Integer> warehouseNames = new LinkedHashMap<String, Integer>();;
//	private LinkedHashMap<String, Integer> warehouseWiseQnty;
//
//	public DPWarehouse3D() {
//		super();
//		this.setSclass("activities-box");
//		initOptions();
//		warehouseLabels = createActivitiesPanel();
//		warehouseLabels.setSclass(ITheme.WARE_HOUSE_DATA_WIDGET);
//
//		iframe = new Iframe(URL);
//		ZKUpdateUtil.setWidth(iframe, "100%");
//		ZKUpdateUtil.setHeight(iframe, "500px");
//
//		iframeContainer = new Div();
//		iframeContainer.setStyle("display: flex; flex-direction: column; align-items: left; gap: 16px;");
//		Div iframeInnerContainer = new Div();
//		iframeInnerContainer.setWidth("1300px");
//		iframeInnerContainer.appendChild(iframe);
//		iframeContainer.appendChild(iframeInnerContainer);
//
//		addEventsToIframe();
//		this.appendChild(warehouseLabels);
//		this.appendChild(iframeContainer);
//	}
//
//	private void addEventsToIframe() {
//		iframe.addEventListener(Events.ON_AFTER_SIZE, this);
//		iframe.addEventListener(Events.ON_BLUR, this);
//		iframe.addEventListener(Events.ON_BOOKMARK_CHANGE, this);
//		iframe.addEventListener(Events.ON_CANCEL, this);
//		iframe.addEventListener(Events.ON_CHANGE, this);
//		iframe.addEventListener(Events.ON_CHANGING, this);
//		iframe.addEventListener(Events.ON_CHECK, this);
//		iframe.addEventListener(Events.ON_CLICK, this);
//		iframe.addEventListener(Events.ON_CLIENT_INFO, this);
//		iframe.addEventListener(Events.ON_CLOSE, this);
//		iframe.addEventListener(Events.ON_CREATE, this);
//		iframe.addEventListener(Events.ON_CTRL_KEY, this);
//		iframe.addEventListener(Events.ON_DEFERRED_EVALUATION, this);
//		iframe.addEventListener(Events.ON_DESKTOP_RECYCLE, this);
//		iframe.addEventListener(Events.ON_DOUBLE_CLICK, this);
//		iframe.addEventListener(Events.ON_DROP, this);
//		iframe.addEventListener(Events.ON_ERROR, this);
//		iframe.addEventListener(Events.ON_FOCUS, this);
//		iframe.addEventListener(Events.ON_FULFILL, this);
//		iframe.addEventListener(Events.ON_GROUP, this);
//		iframe.addEventListener(Events.ON_MAXIMIZE, this);
//		iframe.addEventListener(Events.ON_MINIMIZE, this);
//		iframe.addEventListener(Events.ON_MODAL, this);
//		iframe.addEventListener(Events.ON_MOUSE_OUT, this);
//		iframe.addEventListener(Events.ON_MOUSE_OVER, this);
//		iframe.addEventListener(Events.ON_MOVE, this);
//		iframe.addEventListener(Events.ON_NOTIFY, this);
//		iframe.addEventListener(Events.ON_OK, this);
//		iframe.addEventListener(Events.ON_OPEN, this);
//		iframe.addEventListener(Events.ON_PIGGYBACK, this);
//		iframe.addEventListener(Events.ON_RENDER, this);
//		iframe.addEventListener(Events.ON_RIGHT_CLICK, this);
//		iframe.addEventListener(Events.ON_SCRIPT_ERROR, this);
//		iframe.addEventListener(Events.ON_SCROLL, this);
//		iframe.addEventListener(Events.ON_SCROLLING, this);
//		iframe.addEventListener(Events.ON_SEARCHING, this);
//		iframe.addEventListener(Events.ON_SELECT, this);
//		iframe.addEventListener(Events.ON_SELECTION, this);
//		iframe.addEventListener(Events.ON_SIZE, this);
//		iframe.addEventListener(Events.ON_SLIDE, this);
//		iframe.addEventListener(Events.ON_SORT, this);
//		iframe.addEventListener(Events.ON_STUB, this);
//		iframe.addEventListener(Events.ON_STATE_CHANGE, this);
//		iframe.addEventListener(Events.ON_TARGET_CLICK, this);
//		iframe.addEventListener(Events.ON_TIMER, this);
//		iframe.addEventListener(Events.ON_UNGROUP, this);
//		iframe.addEventListener(Events.ON_UPLOAD, this);
//		iframe.addEventListener(Events.ON_URI_CHANGE, this);
//		iframe.addEventListener(Events.ON_USER, this);
//		iframe.addEventListener(Events.ON_VISIBILITY_CHANGE, this);
//		iframe.addEventListener(Events.ON_Z_INDEX, this);
//	}
//
//	private Div createActivitiesPanel() {
//		Div labelContainer = new Div();
//		labelContainer.setStyle("display: flex; flex-direction: row; align-items: center; gap: 16px;");
//
//		int warehouseSize = 0;
//		for (String warehouseName : warehouseWiseQnty.keySet()) {
//			int warehouseCount = warehouseWiseQnty.get(warehouseName);
//
//			Div redBox = new Div();
//			String bgColour = "rgb(252, 25, 7)";
//			if (warehouseCount > 0)
//				bgColour = "rgb(188, 244, 164)";
//
//			redBox.setStyle("background-color: " + bgColour
//					+ "; color: #000000; padding: 8px 16px; font-size: 24px; font-weight: bold; width: 200px; height: 100px; display: flex; justify-content: center; align-items: center;");
//
//			Label labelCount = new Label();
//			labelCount.setValue(String.valueOf(warehouseCount));
//			labelCount.setStyle("font-size: 32px;");
//			redBox.appendChild(labelCount);
//
//			Div ashBox = new Div();
//			ashBox.setStyle(
//					"background-color: #CCCCCC; color: #FF0000; padding: 8px 16px; font-size: 16px; width: 200px; display: flex; justify-content: center; align-items: center;");
//
//			Label labelName = new Label();
//			labelName.setValue(warehouseName);
//			labelName.setStyle("font-size: 16px;");
//			ashBox.appendChild(labelName);
//
//			Div boxContainer = new Div();
//			boxContainer.appendChild(redBox);
//			boxContainer.appendChild(ashBox);
//
//			boxContainer.setAttribute(warehouseKey, warehouseName);
//			boxContainer.addEventListener(Events.ON_CLICK, this);
//			boxContainer.setStyle("display: flex; flex-direction: column; align-items: center; gap: -8px;");
//
//			if (warehouseSize == 6) {
//				warehouseSize = 0;
//				Div innerDiv = new Div();
//				innerDiv.setStyle("display: flex; flex-direction: row; align-items: center; gap: -8px;");
//				innerDiv.appendChild(boxContainer);
//				labelContainer.appendChild(innerDiv);
//			} else
//				labelContainer.appendChild(boxContainer);
//
//			warehouseSize++;
//		}
//
//		return labelContainer;
//	}
//
//	public void sendDataToURL() {
//
//		MWarehouse mWarehouse = new MWarehouse(Env.getCtx(), selectedWarehouseId, null);
//		MAttachment mAttachment = mWarehouse.getAttachment(true);
//
//		MAttachmentEntry[] mAttachmentEntry = mAttachment.getEntries();
//		if (mAttachmentEntry != null && mAttachmentEntry.length != 0) {
//			MAttachmentEntry entry = mAttachment.getEntry(0);
//			File file = entry.getFile();
//			JsonObject jsonObject = readJsonFromFile(file);
//			if (jsonObject != null) {
//				iframe.focus();
//
//				JsonObject finalObject = new JsonObject();
//				finalObject.add("layoutView", jsonObject);
//				finalObject.add("layoutData", warehouseData);
//
//				String script = "frames['" + iframe.getUuid() + "'].contentWindow.postMessage(" + finalObject
//						+ ", '*');";
//
//				if (Executions.getCurrent() != null) {
//					AuScript aus = new AuScript(iframe, script);
//					Clients.response(aus);
//
//				}
//
//			}
//		}
//	}
//
//	public JsonObject readJsonFromFile(File file) {
//		Gson gson = new Gson();
//		JsonObject jsonObject = null;
//
//		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
//			StringBuilder jsonBuilder = new StringBuilder();
//			String line;
//			while ((line = br.readLine()) != null) {
//				jsonBuilder.append(line);
//			}
//			jsonObject = gson.fromJson(jsonBuilder.toString(), JsonObject.class);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return jsonObject;
//	}
//
//	public void initOptions() {
//
//		Properties ctx = Env.getCtx();
//		int clientId = Env.getAD_Client_ID(ctx);
//
//		warehouseData = new JsonObject();
//		PreparedStatement pstm = null;
//		ResultSet rs = null;
//		try {
//
//			String sql = "SELECT \n" + "    w.m_warehouse_id AS warehouseId,\n" + "    w.name AS warehouseName,\n"
//					+ "	ml.m_locatortype_id,\n" + "    ml.isdefault,\n" + "    lt.name AS locatorType,\n"
//					+ "    ml.value AS locatorName,\n" + "	ml.m_locator_id as locatorId,\n" + "    COALESCE((\n"
//					+ "        SELECT SUM(QtyOnHand)\n" + "        FROM M_StorageOnHand\n"
//					+ "        WHERE M_Locator_ID = ml.m_locator_id\n" + "    ), 0) AS totalQty\n" + "FROM\n"
//					+ "    m_warehouse w\n" + "    JOIN m_locator ml ON ml.m_warehouse_id = w.m_warehouse_id\n"
//					+ "    JOIN m_locatortype lt ON lt.m_locatortype_id = ml.m_locatortype_id\n" + "WHERE\n"
//					+ "    ml.ad_client_id = " + clientId + "\n" + "GROUP BY\n" + "    w.m_warehouse_id,\n"
//					+ "    lt.name,\n" + "    ml.m_locator_id;";
//
//			pstm = DB.prepareStatement(sql.toString(), null);
//			rs = pstm.executeQuery();
//
//			warehouseWiseQnty = new LinkedHashMap<String, Integer>();
//
//			while (rs.next()) {
//				String warehouseId = rs.getString("warehouseId");
//				String warehouseName = rs.getString("warehouseName");
//				String locatorName = rs.getString("locatorName");
//				int totalQty = rs.getInt("totalQty");
//				int m_locatortype_id = rs.getInt("m_locatortype_id");
//
//				warehouseNames.put(warehouseName, Integer.valueOf(warehouseId));
//				int availableQnty = totalQty;
//
//				if (warehouseWiseQnty.containsKey(warehouseName))
//					availableQnty = totalQty + warehouseWiseQnty.get(warehouseName);
//				warehouseWiseQnty.put(warehouseName, availableQnty);
//				if (selectedWarehouseId == 0)
//					selectedWarehouseId = Integer.valueOf(warehouseId);
//
//				if (selectedWarehouseId == Integer.valueOf(warehouseId)) {
//
//					MLocatorType_Custom locatorTypeCustom = new MLocatorType_Custom(ctx, m_locatortype_id, null);
//
//					if (locatorTypeCustom.isReceiving()) {
//						int qnty = 0;
//						if (warehouseData.get("Receiving") != null)
//							warehouseData.get("Receiving").getAsInt();
//						qnty += totalQty;
//						warehouseData.addProperty("Receiving", qnty);
//					} else if (locatorTypeCustom.isPacking()) {
//						int qnty = 0;
//						if (warehouseData.get("Packaging") != null)
//							qnty = warehouseData.get("Packaging").getAsInt();
//						qnty += totalQty;
//						warehouseData.addProperty("Packaging", qnty);
//					} else if (locatorTypeCustom.isdispatch()) {
//						int qnty = 0;
//						if (warehouseData.get("Dispatch") != null)
//							qnty = warehouseData.get("Dispatch").getAsInt();
//						qnty += totalQty;
//						warehouseData.addProperty("Dispatch", qnty);
//					} else if (locatorTypeCustom.isReturns()) {
//						int qnty = 0;
//						if (warehouseData.get("Returns") != null)
//							qnty = warehouseData.get("Returns").getAsInt();
//						qnty += totalQty;
//						warehouseData.addProperty("Returns", qnty);
//					} else if (locatorTypeCustom.isStorage()) {
//
//						if (warehouseData.get(locatorName) == null) {
//							warehouseData.addProperty(locatorName, totalQty);
//						} else {
//							warehouseData.addProperty(locatorName,
//									warehouseData.get(locatorName).getAsInt() + totalQty);
//						}
//					}
//
//				}
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//
//		} finally {
//			DB.close(rs, pstm);
//		}
//	}
//
//	@Override
//	public void updateUI() {
//		initOptions();
//
//		Div labelDiv = createActivitiesPanel();
//		labelDiv.setSclass(ITheme.WARE_HOUSE_DATA_WIDGET);
//
//		this.removeChild(warehouseLabels);
//		this.insertBefore(labelDiv, iframeContainer);
//		warehouseLabels = labelDiv;
//
//		sendDataToURL();
//
//	}
//
//	@Override
//	public void refresh(ServerPushTemplate template) {
//		template.executeAsync(this);
//	}
//
//	@Override
//	public void onEvent(Event event) throws Exception {
//		Component comp = event.getTarget();
//		String eventName = event.getName();
//
//		if (eventName.equals(Events.ON_CLICK) && comp instanceof Div) {
//			Div fieldDiv = (Div) event.getTarget();
//			String name = (String) fieldDiv.getAttribute(warehouseKey);
//			selectedWarehouseId = warehouseNames.get(name);
//
//			DashboardRunnable dashboardRunnable = DPWarehouseSelection.refreshDashboardPanels(getDesktop());
//			dashboardRunnable.refreshDashboard(false);
//		}
//	}
//}
