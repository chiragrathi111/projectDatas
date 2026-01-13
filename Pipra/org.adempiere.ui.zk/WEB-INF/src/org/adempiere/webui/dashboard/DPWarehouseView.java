package org.adempiere.webui.dashboard;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.adempiere.webui.component.Button;
import org.adempiere.webui.component.Grid;
import org.adempiere.webui.component.Row;
import org.adempiere.webui.component.Rows;
import org.adempiere.webui.theme.ThemeManager;
import org.adempiere.webui.util.ServerPushTemplate;
import org.compiere.model.MQuery;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.idempiere.zk.billboard.Billboard;
import org.zkoss.json.JSONObject;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.CategoryModel;
import org.zkoss.zul.ChartModel;
import org.zkoss.zul.Div;
import org.zkoss.zul.SimpleCategoryModel;

public class DPWarehouseView extends DashboardPanel {

	private static final long serialVersionUID = 1L;

	private Billboard warehoouseBillboard;
	private Billboard lTypeBillboard;

	private LinkedHashMap<String, Integer> warehouseListData = null;
	private LinkedHashMap<String, Integer> warehouseIdAndName = new LinkedHashMap<String, Integer>();;
	private LinkedHashMap<String, Integer> locatorTypeListData = null;
	private static String WAREHOUSE = "warehouse";
	private static String Locator_TYPE = "locatorType";

	public DPWarehouseView() {
		super();
		this.setSclass("activities-box");
		initOptions();

		buildChart(warehouseListData, WAREHOUSE);
		buildChart(locatorTypeListData, Locator_TYPE);

		ZoomListener listener = new ZoomListener(null, warehoouseBillboard.getModel());
		warehoouseBillboard.addEventListener("onDataClick", listener);

		this.appendChild(createActivitiesPanel());
	}

	private Grid createActivitiesPanel() {

		Grid grid = new Grid();
		grid.setSclass("pipra-charts-title-indicator");
		appendChild(grid);
		grid.makeNoStrip();

		Rows rows = new Rows();
		grid.appendChild(rows);

		Row row = null;
		List<Billboard> list = new ArrayList<>();

		list.add(warehoouseBillboard);
		list.add(lTypeBillboard);

		for (int i = 0; i < list.size(); i++) {
			if (row == null || i % 2 == 0) {
				row = new Row();
				rows.appendChild(row);
			}

			Div div = new Div();
			row.appendCellChild(div, 5);
			div.setSclass("pipra-charts-title-indicator");

			div.appendChild(list.get(i));
			Div titleDiv = new Div();
			titleDiv.setSclass("pipra-charts-title-indicator");

			div.appendChild(titleDiv);

			Button btnNotice = new Button();
			if (i == 0) {
				btnNotice.setLabel("ALL");
				btnNotice.setTooltiptext("ALl Warehouse");
				btnNotice.setImage(ThemeManager.getThemeResource("images/StepBack24.png"));
				btnNotice.setName("All");
				btnNotice.setStyle("float: right;");

				ZoomListener listener = new ZoomListener(null, warehoouseBillboard.getModel());
				btnNotice.addEventListener(Events.ON_CLICK, listener);
			}
			div.appendChild(btnNotice);
			
		}	

		return grid;
	}

	private void initOptions() {

		Properties ctx = Env.getCtx();
		int clientId = Env.getAD_Client_ID(ctx);
		int warehouseId = DPWarehouseSelection.getWareHouse_ID(ctx);
		int productId = DPWarehouseSelection.getProduct_ID(ctx);

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {

			String warehouseChartSql = null;
			String locatorTypeChartSql = null;
			if (warehouseId == 0 && productId == 0) {

				warehouseChartSql = "SELECT \n" + "    wh.name AS warehouseName, wh.m_warehouse_id, \n"
						+ "    COALESCE(SUM(sh.qtyonhand), 0) AS Qty \n" + "FROM \n" + "    adempiere.m_warehouse wh\n"
						+ "LEFT JOIN \n" + "    adempiere.m_locator lo ON lo.m_warehouse_id = wh.m_warehouse_id\n"
						+ "LEFT JOIN \n" + "    adempiere.m_storageonhand sh ON sh.m_locator_id = lo.m_locator_id\n"
						+ "WHERE \n" + "    wh.ad_client_id = " + clientId + " \n" + "GROUP BY \n"
						+ "    wh.name, wh.m_warehouse_id;";

				locatorTypeChartSql = "SELECT ly.name AS locatorTypeName, ly.m_locatortype_id ,SUM(sh.qtyonhand) AS Qty FROM adempiere.m_locatortype ly \n"
						+ "JOIN adempiere.m_locator lo ON lo.m_locatortype_id = ly.m_locatortype_id\n"
						+ "JOIN adempiere.m_storageonhand sh ON sh.m_locator_id = lo.m_locator_id\n"
						+ "WHERE ly.ad_client_id = " + clientId + " GROUP BY ly.name, ly.m_locatortype_id";

			} else if (warehouseId != 0 && productId == 0) {
				warehouseChartSql = "SELECT \n" + "    wh.name AS warehouseName,wh.m_warehouse_id ,\n"
						+ "    COALESCE(SUM(sh.qtyonhand), 0) AS Qty \n" + "FROM \n" + "    adempiere.m_warehouse wh\n"
						+ "LEFT JOIN \n" + "    adempiere.m_locator lo ON lo.m_warehouse_id = wh.m_warehouse_id\n"
						+ "LEFT JOIN \n" + "    adempiere.m_storageonhand sh ON sh.m_locator_id = lo.m_locator_id\n"
						+ "WHERE \n" + "    wh.ad_client_id = " + clientId + " AND wh.m_warehouse_id = " + warehouseId
						+ " \n" + "GROUP BY \n" + "    wh.name,wh.m_warehouse_id;";

				locatorTypeChartSql = "SELECT ly.name AS locatorTypeName,ly.m_locatortype_id,SUM(sh.qtyonhand) AS Qty FROM adempiere.m_locatortype ly \n"
						+ "JOIN adempiere.m_locator lo ON lo.m_locatortype_id = ly.m_locatortype_id\n"
						+ "JOIN adempiere.m_storageonhand sh ON sh.m_locator_id = lo.m_locator_id\n"
						+ "WHERE ly.ad_client_id = " + clientId + " and lo.m_warehouse_id = " + warehouseId
						+ " GROUP BY ly.name,ly.m_locatortype_id;";

			} else if (warehouseId == 0 && productId != 0) {

				locatorTypeChartSql = "SELECT ly.name AS locatorTypeName,ly.m_locatortype_id,SUM(sh.qtyonhand) AS Qty FROM adempiere.m_locatortype ly \n"
						+ "JOIN adempiere.m_locator lo ON lo.m_locatortype_id = ly.m_locatortype_id\n"
						+ "JOIN adempiere.m_storageonhand sh ON sh.m_locator_id = lo.m_locator_id\n"
						+ "WHERE ly.ad_client_id = " + clientId + "  and sh.m_product_id = " + productId
						+ " GROUP BY ly.name,ly.m_locatortype_id;";

				warehouseChartSql = "SELECT \n" + "    wh.name AS warehouseName,wh.m_warehouse_id ,\n"
						+ "    COALESCE(SUM(sh.qtyonhand), 0) AS Qty \n" + "FROM \n" + "    adempiere.m_warehouse wh\n"
						+ "LEFT JOIN \n" + "    adempiere.m_locator lo ON lo.m_warehouse_id = wh.m_warehouse_id\n"
						+ "LEFT JOIN \n" + "    adempiere.m_storageonhand sh ON sh.m_locator_id = lo.m_locator_id\n"
						+ "WHERE \n" + "    wh.ad_client_id = " + clientId + " AND sh.m_product_id = " + productId
						+ " \n" + "GROUP BY \n" + "    wh.name,wh.m_warehouse_id;";
			}

			pstmt = DB.prepareStatement(warehouseChartSql.toString(), null);
			rs = pstmt.executeQuery();
			warehouseListData = new LinkedHashMap<String, Integer>();
			while (rs.next()) {
				int warehouseWiseQnty = rs.getInt("Qty");
				String wareHousename = rs.getString("warehouseName");
				int mWarehouseId = rs.getInt("m_warehouse_id");
				warehouseListData.put(wareHousename, warehouseWiseQnty);

				if (!warehouseIdAndName.containsKey(wareHousename))
					warehouseIdAndName.put(wareHousename, mWarehouseId);

			}
			DB.close(rs, pstmt);

			pstmt = DB.prepareStatement(locatorTypeChartSql.toString(), null);
			rs = pstmt.executeQuery();
			locatorTypeListData = new LinkedHashMap<String, Integer>();
			while (rs.next()) {
				int locatorTypeWiseQnty = rs.getInt("Qty");
				String locatorTypeName = rs.getString("locatorTypeName");
				locatorTypeListData.put(locatorTypeName, locatorTypeWiseQnty);

			}
			DB.close(rs, pstmt);

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void updateUI() {

		warehoouseBillboard.setModel(initChartModel(warehouseListData, WAREHOUSE));
		lTypeBillboard.setModel(initChartModel(locatorTypeListData, Locator_TYPE));

		warehoouseBillboard.invalidate();
		lTypeBillboard.invalidate();
	}

	@Override
	public void refresh(ServerPushTemplate template) {
		initOptions();

		template.executeAsync(this);
	}

	private void buildChart(LinkedHashMap<String, Integer> data, String type) {

		String barColor = "#2563EB";

		Set<String> wNames = data.keySet();
		Billboard billboard = new Billboard();
		billboard.setLegend(true, false);
		billboard.addLegendOptions("location", "bottom");
		billboard.setTickAxisLabel(type + " Name");
		billboard.setValueAxisLabel("Qauntity");
//		billboard.setTitle("tittle");
		billboard.setType("bar");

		String[] rgbColors = new String[wNames.size()];
		Arrays.fill(rgbColors, barColor);		
		billboard.addRendererOptions("intervalColors", rgbColors);
		
		billboard.setModel(initChartModel(data, type));
		billboard.setOrient("vertical");

		if (type.equalsIgnoreCase(WAREHOUSE))
			warehoouseBillboard = billboard;
		else if (type.equalsIgnoreCase(Locator_TYPE))
			lTypeBillboard = billboard;

	}

	private ChartModel initChartModel(LinkedHashMap<String, Integer> data, String type) {
		Set<String> wNames = data.keySet();
		ChartModel chartModel = new SimpleCategoryModel();
		for (String key : wNames) {
			((CategoryModel) chartModel).setValue(type, key, data.get(key));

		}
		return chartModel;
	}

	private class ZoomListener implements EventListener<Event> {
//		private Map<String, MQuery> queries;
		private org.zkoss.zul.ChartModel model;

		private ZoomListener(Map<String, MQuery> queries, org.zkoss.zul.ChartModel model) {
//			this.queries = queries;
			this.model = model;
		}

		@Override
		public void onEvent(Event event) throws Exception {

			Component comp = event.getTarget();
			String eventName = event.getName();

			int wareHouseId = 0;
			if (eventName.equals(Events.ON_CLICK)) {
				if (comp instanceof Button)
					wareHouseId = 0;

			} else {

				JSONObject json = (JSONObject) event.getData();
				Number seriesIndex = (Number) json.get("seriesIndex");
				Number pointIndex = (Number) json.get("pointIndex");
				if (pointIndex == null)
					pointIndex = Integer.valueOf(0);

//				MQuery query = null;
				if (model instanceof CategoryModel) {
					CategoryModel categoryModel = (CategoryModel) model;
//					Object series = categoryModel.getSeries(seriesIndex.intValue());
					Object category = categoryModel.getCategory(pointIndex.intValue());

					wareHouseId = warehouseIdAndName.get(category);

//				query = queries.get(series.toString()+"__"+category.toString());
				}
//				if (query != null)
//					AEnv.zoom(query);
			}

			Env.setContext(Env.getCtx(), Env.M_WAREHOUSE_ID, wareHouseId);
			Env.setContext(Env.getCtx(), DPWarehouseSelection.M_PRODUCT_ID, 0);
			DashboardRunnable dashboardRunnable = DPWarehouseSelection.refreshDashboardPanels(getDesktop());
			dashboardRunnable.refreshDashboard(false);
		}
	}

}
