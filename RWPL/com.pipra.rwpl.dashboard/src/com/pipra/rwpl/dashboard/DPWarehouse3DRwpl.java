package com.pipra.rwpl.dashboard;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.adempiere.webui.dashboard.DPWarehouseSelection;
import org.adempiere.webui.dashboard.DashboardPanel;
import org.adempiere.webui.dashboard.DashboardRunnable;
import org.adempiere.webui.util.ServerPushTemplate;
import org.adempiere.webui.util.ZKUpdateUtil;
import org.compiere.model.MLocator;
import org.compiere.model.MProduct;
import org.compiere.model.MSysConfig;
import org.compiere.model.MWarehouse;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.zkoss.zk.au.out.AuScript;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Div;
import org.zkoss.zul.Iframe;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.pipra.rwpl.model.MLocator_Custom;

public class DPWarehouse3DRwpl extends DashboardPanel implements EventListener<Event> {

	private static final long serialVersionUID = 1L;
	private final String warehouseKey = "warehouseName";
	private static final String URL = MSysConfig.getValue("WAREHOUSE_3D_CONFIG_URL");
	private Iframe iframe;
	private int selectedWarehouseId;
	private Div iframeContainer;

	private JsonObject warehouseData;
	private LinkedHashMap<String, Integer> warehouseNames = new LinkedHashMap<String, Integer>();;
	private LinkedHashMap<String, Integer> warehouseWiseQnty;
	private LinkedHashMap<String, Integer> occupiedLocatorList;
	private List<Integer> occupiedLocatorIdsList ;

	private boolean initialLoad = false;

	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	public DPWarehouse3DRwpl() {
		super();
		this.setSclass("activities-box");
		initOptions();

		iframe = new Iframe(URL);
		ZKUpdateUtil.setWidth(iframe, "100%");
		ZKUpdateUtil.setHeight(iframe, "800px");

		iframeContainer = new Div();
		iframeContainer.setStyle("display: flex; flex-direction: column; align-items: left; gap: 16px;");
		Div iframeInnerContainer = new Div();
		iframeInnerContainer.setWidth("1550px");
		iframeInnerContainer.appendChild(iframe);
		iframeContainer.appendChild(iframeInnerContainer);

		this.appendChild(iframeContainer);
//		scheduleSendData();
		sendDataAsync();
	}

	public CompletableFuture<Void> sendDataAsync() {
		return CompletableFuture.runAsync(() -> {
			try {
				// Delay for 10 seconds
				TimeUnit.SECONDS.sleep(20);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt(); // Restore interrupted status
			}
			sendDataToURL(); // Call the method after the delay
		});
	}

	public void scheduleSendData() {
		// Schedule the method to run once after 10 seconds
		scheduler.schedule(this::sendDataToURL, 30, TimeUnit.SECONDS);
	}
//
//    public void shutdown() {
//        scheduler.shutdown();
//    }

//	public void sendDataToURL() {
//
//		MWarehouse mWarehouse = new MWarehouse(Env.getCtx(), selectedWarehouseId, null);
//		MAttachment mAttachment = mWarehouse.getAttachment(true);
//
//		if (mAttachment != null) {
//			MAttachmentEntry[] mAttachmentEntry = mAttachment.getEntries();
//			if (mAttachmentEntry != null && mAttachmentEntry.length != 0) {
//				MAttachmentEntry entry = mAttachment.getEntry(0);
//				File file = entry.getFile();
//				JsonObject jsonObject = readJsonFromFile(file);
//				if (jsonObject != null) {
//					iframe.focus();
//
//					JsonObject finalObject = new JsonObject();
//					finalObject.add("layoutView", jsonObject);
//					finalObject.add("layoutData", warehouseData);
//
//					String script = "frames['" + iframe.getUuid() + "'].contentWindow.postMessage(" + finalObject
//							+ ", '*');";
//
//					if (Executions.getCurrent() != null) {
//						AuScript aus = new AuScript(iframe, script);
//						Clients.response(aus);
//
//					}
//
//				}
//			}}
//	}

	public void sendDataToURL() {

		iframe.focus();
		JsonObject finalObject = new JsonObject();
		finalObject.add("layoutData", warehouseData);
		
		MWarehouse mWarehouse = MWarehouse.get(selectedWarehouseId);
		MLocator[] locatorsList = mWarehouse.getLocators(true);
		
		int emptyCount = 0;
		int partialCount = 0;
		int occupiedCount = 0;
		
		for(MLocator locator:  locatorsList) {
			if(!occupiedLocatorIdsList.contains(locator.get_ID()))
					emptyCount++;
		}
		
		for (String key : occupiedLocatorList.keySet()) {
			int percent = occupiedLocatorList.get(key);
			if (percent >= 99)
				occupiedCount++;
			else
				partialCount++;

		}
		
		finalObject.add("empty", new JsonPrimitive(emptyCount));
		finalObject.add("partial", new JsonPrimitive(partialCount));
		finalObject.add("occupied", new JsonPrimitive(occupiedCount));
		
		String script = "frames['" + iframe.getUuid() + "'].contentWindow.postMessage(" + finalObject + ", '*');";

		if (Executions.getCurrent() != null) {
			AuScript aus = new AuScript(iframe, script);
			Clients.response(aus);

		}
	}

	public JsonObject readJsonFromFile(File file) {
		Gson gson = new Gson();
		JsonObject jsonObject = null;

		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			StringBuilder jsonBuilder = new StringBuilder();
			String line;
			while ((line = br.readLine()) != null) {
				jsonBuilder.append(line);
			}
			jsonObject = gson.fromJson(jsonBuilder.toString(), JsonObject.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return jsonObject;
	}

	public void initOptions() {

		Properties ctx = Env.getCtx();
		int clientId = Env.getAD_Client_ID(ctx);

		warehouseData = new JsonObject();
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {

			String sql = "\n" + "\n" + " SELECT \n" + "    pp.m_locator_id,\n" + "	pp.m_product_id,\n"
					+ "    mt.m_locatortype_id,\n" + "    ml.m_warehouse_id,\n" + "    mw.name,\n"
					+ "    ml.value AS locatorName,\n" + "    SUM(CASE \n"
					+ "        WHEN pp.issotrx = 'N' THEN pp.quantity \n" + "        ELSE 0 \n"
					+ "    END) AS quantity\n" + "FROM \n" + "    adempiere.pi_productlabel pp\n" + "JOIN \n"
					+ "    adempiere.m_locator ml ON ml.m_locator_id = pp.m_locator_id\n" + "JOIN \n"
					+ "    adempiere.m_warehouse mw ON mw.m_warehouse_id = ml.m_warehouse_id\n" + "JOIN \n"
					+ "    adempiere.m_locatortype mt ON mt.m_locatortype_id = ml.m_locatortype_id\n" + "WHERE \n"
					+ "    pp.ad_client_id = " + clientId + " AND \n" + "	NOT EXISTS (\n" + "        SELECT 1 \n"
					+ "        FROM adempiere.pi_productlabel pp_sales\n"
					+ "        WHERE pp_sales.labeluuid = pp.labeluuid\n" + "        AND pp_sales.issotrx = 'Y'\n"
					+ "    )\n" + "    AND pp.m_product_id IS NOT NULL\n" + "    AND pp.m_locator_id IS NOT NULL\n"
					+ "GROUP BY \n" + "    pp.m_product_id,\n" + "    pp.m_locator_id,\n" + "    mt.m_locatortype_id,\n"
					+ "    ml.m_warehouse_id,\n" + "    locatorName,\n" + "    mw.name;\n" + "";
			pstm = DB.prepareStatement(sql.toString(), null);
			rs = pstm.executeQuery();

			warehouseWiseQnty = new LinkedHashMap<String, Integer>();

			while (rs.next()) {
				int warehouseId = rs.getInt("m_warehouse_id");
				String warehouseName = rs.getString("name");
				String locatorName = rs.getString("locatorName");
				int totalQty = rs.getInt("quantity");
				int locatorId = rs.getInt("m_locator_id");
				int productId = rs.getInt("m_product_id");

				warehouseNames.put(warehouseName, Integer.valueOf(warehouseId));
				int availableQnty = totalQty;

				if (warehouseWiseQnty.containsKey(warehouseName))
					availableQnty = totalQty + warehouseWiseQnty.get(warehouseName);
				warehouseWiseQnty.put(warehouseName, availableQnty);
				if (selectedWarehouseId == 0)
					selectedWarehouseId = Integer.valueOf(warehouseId);

				if (selectedWarehouseId == Integer.valueOf(warehouseId)) {

//					if (warehouseData.get(locatorName) == null) {
//						warehouseData.addProperty(locatorName, totalQty);
//					} else {
//						warehouseData.addProperty(locatorName, warehouseData.get(locatorName).getAsInt() + totalQty);
//					}

					// Check if the locator already exists in the warehouse data
					if (warehouseData.get(locatorName) != null) {
						totalQty += warehouseData.get(locatorName).getAsInt(); // Update totalQty with existing product
																				// quantity
					}

					MLocator_Custom locator = new MLocator_Custom(ctx, locatorId, null);
					MProduct product = new MProduct(ctx, productId, null);

					BigDecimal percentageOccupied = BigDecimal.valueOf(0);
					MathContext mc = new MathContext(4); // Specify precision (4 decimal places)

					if (!locator.getWeightConstraint()) {
						// Calculate volume if weight constraint is not applied
						BigDecimal lHeight = locator.getHeight();
						BigDecimal lWidth = locator.getWidth();
						BigDecimal lDepth = locator.getDepth();

						if (lHeight == null)
							lHeight = BigDecimal.valueOf(120);
						
						if (lWidth == null)
							lWidth = BigDecimal.valueOf(80);
						
						if (lDepth == null)
							lDepth = BigDecimal.valueOf(120);
						BigDecimal lVolume = lHeight.multiply(lWidth).multiply(lDepth);

						BigDecimal pHeight = product.getShelfHeight();
						BigDecimal pWidth = BigDecimal.valueOf(product.getShelfWidth());
						BigDecimal pDepth = BigDecimal.valueOf(product.getShelfDepth());


						if (pHeight.compareTo(BigDecimal.ZERO) <= 0)
							pHeight = BigDecimal.valueOf(100);
						
						if (product.getShelfWidth() == 0)
							pWidth = BigDecimal.valueOf(3);
						
						if (product.getShelfDepth() == 0)
							pDepth = BigDecimal.valueOf(100);
						BigDecimal pVolume = pHeight.multiply(pWidth).multiply(pDepth);

						// Calculate the total volume occupied by products
						BigDecimal totalProductVolume = pVolume.multiply(BigDecimal.valueOf(totalQty));

						// Check for division by zero
						if (lVolume.compareTo(BigDecimal.ZERO) > 0) {
							// Calculate the percentage of locator volume occupied by products
							percentageOccupied = totalProductVolume.divide(lVolume, mc)
									.multiply(BigDecimal.valueOf(100));
						}
					} else {
						// Calculate weight if weight constraint is applied
						BigDecimal lWeight = locator.getWeight();
						BigDecimal pWeight = product.getWeight();
						BigDecimal totalProductWeight = pWeight.multiply(BigDecimal.valueOf(totalQty));

						// Check for division by zero
						if (lWeight.compareTo(BigDecimal.ZERO) > 0) {
							// Calculate percentage of weight occupied
							percentageOccupied = totalProductWeight.divide(lWeight, mc)
									.multiply(BigDecimal.valueOf(100));
						}
					}

					// Output the percentage based on the context
					if (percentageOccupied.compareTo(BigDecimal.valueOf(100)) > 0)
						percentageOccupied = BigDecimal.valueOf(100);
					warehouseData.addProperty(locatorName, percentageOccupied);
					
					if(occupiedLocatorList == null)
						occupiedLocatorList = new LinkedHashMap<String, Integer>();
					int percent = percentageOccupied.intValue();
					if (occupiedLocatorList.containsKey(locatorName))
						percent += occupiedLocatorList.get(locatorName);
					occupiedLocatorList.put(locatorName, percent);

					if(occupiedLocatorIdsList == null)
						occupiedLocatorIdsList = new ArrayList<Integer>();
					if(!occupiedLocatorIdsList.contains(locatorId))
						occupiedLocatorIdsList.add(locatorId);

				}
			}

		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			DB.close(rs, pstm);
		}
	}

	@Override
	public void updateUI() {
		initOptions();

//		this.insertBefore(labelDiv, iframeContainer);

//		if (!initialLoad) {
		sendDataToURL();
		initialLoad = true;
//		}

	}

	@Override
	public void refresh(ServerPushTemplate template) {
		template.executeAsync(this);
	}

	@Override
	public void onEvent(Event event) throws Exception {
		Component comp = event.getTarget();
		String eventName = event.getName();

		if (eventName.equals(Events.ON_CLICK) && comp instanceof Div) {
			Div fieldDiv = (Div) event.getTarget();
			String name = (String) fieldDiv.getAttribute(warehouseKey);
			selectedWarehouseId = warehouseNames.get(name);

			DashboardRunnable dashboardRunnable = DPWarehouseSelection.refreshDashboardPanels(getDesktop());
			dashboardRunnable.refreshDashboard(false);
		}
	}
}
