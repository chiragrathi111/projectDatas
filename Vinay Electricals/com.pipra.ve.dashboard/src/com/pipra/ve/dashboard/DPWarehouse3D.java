package com.pipra.ve.dashboard;

import java.math.BigDecimal;
import java.math.MathContext;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Properties;

import org.adempiere.webui.component.Button;
import org.adempiere.webui.component.Combobox;
import org.adempiere.webui.dashboard.DashboardPanel;
import org.adempiere.webui.theme.ITheme;
import org.adempiere.webui.util.ServerPushTemplate;
import org.adempiere.webui.util.ZKUpdateUtil;
import org.compiere.model.I_M_Locator;
import org.compiere.model.MLocator;
import org.compiere.model.MProduct;
import org.compiere.model.MSysConfig;
import org.compiere.model.Query;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.zkoss.zk.au.out.AuScript;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Div;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Vbox;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.pipra.ve.model.MLocator_Custom;

public class DPWarehouse3D extends DashboardPanel implements EventListener<Event> {

	public Combobox comboboxDepartmentData;
	public Button btnShow3D;
	private static final long serialVersionUID = 1L;
	private Div warehouseLabels;
	private Div iframeContainer;
	private Iframe iframe;
	private JsonObject departmentData;
	private int selectedDepartmentId;
	private LinkedHashMap<String, Integer> occupiedLocatorList;
	private List<Integer> occupiedLocatorIdsList;
	private String selectedDept = "";

	// private static final String URL =
	private static final String URL = MSysConfig.getValue("WAREHOUSE_3D_CONFIG_URL");
//	private static final String URL = "http://localhost:3000/";
	public LinkedHashMap<String, Integer> departmentNames = new LinkedHashMap<>(); // department name to its id

	public DPWarehouse3D() {
		super();
		this.setSclass("activities-box");
		initDepartmentOptions(); // Fills departmentNames
		warehouseLabels = createActivitiesPanel();
		warehouseLabels.setSclass(ITheme.WARE_HOUSE_DATA_WIDGET);

		iframe = new Iframe(URL);
		ZKUpdateUtil.setWidth(iframe, "100%");
		ZKUpdateUtil.setHeight(iframe, "500px");

		iframeContainer = new Div();
		iframeContainer.setStyle("display: flex; flex-direction: column; align-items: left; gap: 16px;");
		Div iframeInnerContainer = new Div();
		iframeInnerContainer.setWidth("1300px");
		iframeInnerContainer.appendChild(iframe);
		iframeContainer.appendChild(iframeInnerContainer);

		this.appendChild(warehouseLabels);
		this.appendChild(iframeContainer);
	}

	private Div createActivitiesPanel() {
		Div rootDiv = new Div();

		Vbox vbox = new Vbox();
		vbox.setSclass(ITheme.WARE_HOUSE_SELECT_WIDGET);

		Div div = new Div();
		div.setSclass(ITheme.WARE_HOUSE_SELECT_WIDGET_CONT);
		vbox.appendChild(div);

		comboboxDepartmentData = new Combobox();
		div.appendChild(comboboxDepartmentData);
		comboboxDepartmentData.setAutocomplete(true);
		comboboxDepartmentData.setAutodrop(true);
		comboboxDepartmentData.setPlaceholder("All Department");
		comboboxDepartmentData.appendItem("All Department");
		comboboxDepartmentData.setSelectedIndex(0);
		comboboxDepartmentData.setId("comboboxDepartmentData");
		ZKUpdateUtil.setWidth(comboboxDepartmentData, "30%");

		for (String depName : departmentNames.keySet()) {
			comboboxDepartmentData.appendItem(depName);
		}

		btnShow3D = new Button("Search");
		btnShow3D.setId("btnShow3D");
		btnShow3D.setStyle(
				"background-color: #007bff; color: white; padding: 6px 12px; border-radius: 4px; border: none; cursor: pointer;");
		btnShow3D.addEventListener(Events.ON_CLICK, this);
		div.appendChild(btnShow3D);

		rootDiv.appendChild(vbox);
		return rootDiv;
	}

	public void initDepartmentOptions() {
		Properties ctx = Env.getCtx();
		int clientId = Env.getAD_Client_ID(ctx);
		departmentNames.clear();
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			String sql = "SELECT pi_deptartment_id,value FROM adempiere.pi_deptartment WHERE ad_client_id = ? AND value IS NOT NULL AND isactive = 'Y'";
			pstm = DB.prepareStatement(sql, null);
			pstm.setInt(1, clientId);
			rs = pstm.executeQuery();
			while (rs.next()) {
				int deptId = rs.getInt("pi_deptartment_id");
				String deptName = rs.getString("value");
				departmentNames.put(deptName, deptId);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DB.close(rs, pstm);
		}
	}

	public void initOptions() {

		Properties ctx = Env.getCtx();
		int clientId = Env.getAD_Client_ID(ctx);

		departmentData = new JsonObject();
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {

			String sql = "SELECT pp.m_locator_id,pp.m_product_id,lt.m_locatortype_id,ml.value AS locator_name,\n"
					+ "SUM(CASE WHEN pp.issotrx = 'N' THEN pp.quantity ELSE 0 END) AS quantity,\n"
					+ "SUM(SUM(CASE WHEN pp.issotrx = 'N' THEN pp.quantity ELSE 0 END)) OVER () AS total_quantity\n"
					+ "FROM adempiere.pi_productlabel pp\n"
					+ "JOIN adempiere.m_locator ml ON ml.m_locator_id = pp.m_locator_id\n"
					+ "JOIN adempiere.m_locatortype lt ON ml.m_locatortype_id = lt.m_locatortype_id "
					+ "JOIN adempiere.pi_deptartment d ON d.pi_deptartment_id = lt.pi_deptartment_id\n"
					+ "where pp.ad_client_id = " + clientId + " AND\n"
					+ "NOT EXISTS (SELECT 1 FROM adempiere.pi_productlabel pp_sales WHERE pp_sales.labeluuid = pp.labeluuid\n"
					+ "AND pp_sales.issotrx = 'Y') AND pp.m_product_id IS NOT NULL AND pp.m_locator_id IS NOT NULL\n"
					+ "AND lt.pi_deptartment_id = " + selectedDepartmentId
					+ " GROUP BY pp.m_locator_id,pp.m_product_id,lt.m_locatortype_id,ml.value;";
			pstm = DB.prepareStatement(sql.toString(), null);
			rs = pstm.executeQuery();

			while (rs.next()) {
				String locatorName = rs.getString("locator_name");
				int totalQty = rs.getInt("quantity");
				int locatorId = rs.getInt("m_locator_id");
				int productId = rs.getInt("m_product_id");

				// Check if the locator already exists in the warehouse data
				if (departmentData.get(locatorName) != null) {
					totalQty += departmentData.get(locatorName).getAsInt(); // Update totalQty with existing product
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
						percentageOccupied = totalProductVolume.divide(lVolume, mc).multiply(BigDecimal.valueOf(100));
					}
				} else {
					// Calculate weight if weight constraint is applied
					BigDecimal lWeight = locator.getWeight();
					BigDecimal pWeight = product.getWeight();
					BigDecimal totalProductWeight = pWeight.multiply(BigDecimal.valueOf(totalQty));

					// Check for division by zero
					if (lWeight.compareTo(BigDecimal.ZERO) > 0) {
						// Calculate percentage of weight occupied
						percentageOccupied = totalProductWeight.divide(lWeight, mc).multiply(BigDecimal.valueOf(100));
					}
				}

				// Output the percentage based on the context
				if (percentageOccupied.compareTo(BigDecimal.valueOf(100)) > 0)
					percentageOccupied = BigDecimal.valueOf(100);
				departmentData.addProperty(locatorName, percentageOccupied);

				if (occupiedLocatorList == null)
					occupiedLocatorList = new LinkedHashMap<String, Integer>();
				int percent = percentageOccupied.intValue();
				if (occupiedLocatorList.containsKey(locatorName))
					percent += occupiedLocatorList.get(locatorName);
				occupiedLocatorList.put(locatorName, percent);

				if (occupiedLocatorIdsList == null)
					occupiedLocatorIdsList = new ArrayList<Integer>();
				if (!occupiedLocatorIdsList.contains(locatorId))
					occupiedLocatorIdsList.add(locatorId);

			}

		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			DB.close(rs, pstm);
		}
	}

//	@Override
	public void updateUI() {
		initOptions();

		if (selectedDept.equalsIgnoreCase("FG-Wire")) {
			sendDataToURL("WireAreaFG");
		} else if (selectedDept.equalsIgnoreCase("FG-Switch Gear")) {
			sendDataToURL("mcbLayout");
		} else if (selectedDept.equalsIgnoreCase("Brass-Switch & Acc.")) {
			sendDataToURL("BrassAreaCRN");
		} else if (selectedDept.equalsIgnoreCase("FG-Light")) {
			sendDataToURL("lightLayout");
		} else if (selectedDept.equalsIgnoreCase("RM-Switch & Acc.")) {
			sendDataToURL("rmLocatorLayout");
		} else if (selectedDept.equalsIgnoreCase("Moulding-Switch & Acc.")) {
			sendDataToURL("MouldingBuilding");
		} else if (selectedDept.equalsIgnoreCase("FG-Switch & Acc.")) {
			sendDataToURL("onlyFGSwitchAndSocketLayout");
		} else if (selectedDept.equalsIgnoreCase("FG-Automation")) {
			sendDataToURL("automationLayout");	
		} else {
			sendDataToURL("BrassAreaCRN");
		}

	}

	@Override
	public void refresh(ServerPushTemplate template) {
		template.executeAsync(this);
	}

	@Override
	public void onEvent(Event event) throws Exception {
		if (Events.ON_CLICK.equals(event.getName()) && event.getTarget() == btnShow3D) {
			selectedDept = comboboxDepartmentData.getValue();
			selectedDepartmentId = departmentNames.get(selectedDept);
			updateUI();
		}
	}

	public void sendDataToURL(String selectedDeptS) {

		iframe.focus();
		JsonObject finalObject = new JsonObject();
		finalObject.add("departmentName", new JsonPrimitive(selectedDeptS));

		finalObject.add("layoutData", departmentData);

		List<MLocator> locatorsList = getLocatorsByDepartment();

		int emptyCount = 0;
		int partialCount = 0;
		int occupiedCount = 0;

		for (MLocator locator : locatorsList) {
			if (!occupiedLocatorIdsList.contains(locator.get_ID()))
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

	public List<MLocator> getLocatorsByDepartment() {

		String tblName = I_M_Locator.Table_Name;
		List<MLocator> list = new Query(Env.getCtx(), tblName, "ml.pi_deptartment_ID = ?", null)
				.setParameters(selectedDepartmentId)
				.addJoinClause("JOIN M_locatorType ml on  ml.m_locatortype_ID = " + tblName + ".m_locatortype_ID")
				.setOnlyActiveRecords(true).setOrderBy("X,Y,Z").list();
		if (list.size() > 0)
			list.stream().forEach(e -> e.markImmutable());
		return list;
	}
}