package com.pipra.rwpl.dashboard;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.adempiere.webui.component.Grid;
import org.adempiere.webui.component.Row;
import org.adempiere.webui.component.Rows;
import org.adempiere.webui.dashboard.DPWarehouseSelection;
import org.adempiere.webui.dashboard.DashboardPanel;
import org.adempiere.webui.util.ServerPushTemplate;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.Vlayout;

import tools.dynamia.zk.addons.chartjs.Axe;
import tools.dynamia.zk.addons.chartjs.CategoryChartjsData;
import tools.dynamia.zk.addons.chartjs.Chartjs;
import tools.dynamia.zk.addons.chartjs.ChartjsOptions;
import tools.dynamia.zk.addons.chartjs.Dataset;
import tools.dynamia.zk.addons.chartjs.ScaleLabel;
import tools.dynamia.zk.addons.chartjs.Scales;
import tools.dynamia.zk.addons.chartjs.Ticks;

public class PurchaseChart extends DashboardPanel implements EventListener<Event> {

	private static final long serialVersionUID = 1L;
	private Chartjs warehouseChartJs;
	private Chartjs productChartJs;
	CategoryChartjsData warehouseData;
	CategoryChartjsData productData;

//	private String warehouseDefaultName;
	private String ordersProcessedCount;
	private String linesProcessedCount;
	private int unitsProcessedCount;
	private String amount;

	private Label labelAmount;
	private Label labelUnitsProcessedCount;
	private Label labelLinesProcessedCount;
	private Label labelOrdersProcessedCount;

	private LinkedHashMap<String, Integer> warehouseListData = null;
	private LinkedHashMap<String, Integer> productListData = null;
	private int wMin = 0;
	private int wMax = 1;
	private int pMin = 0;
	private int pMax = 1;
	private Grid grid;

	public PurchaseChart() {
		super();
		this.setSclass("activities-box");
		initOptions();
		warehouseCategoryModel();
		productCategoryModel();
		grid = createActivitiesPanel();
		this.appendChild(grid);
	}

	private Grid createActivitiesPanel() {

		warehouseChartJs = new Chartjs(Chartjs.TYPE_BAR);
		warehouseChartJs.setData(warehouseData);
		warehouseChartJs.setOptions(ChartjsOptions.Builder.init()
				.scales(new Scales().addY(Axe.Builder.init().scaleLabel(ScaleLabel.Builder.init().display(true).build())
						.ticks(Ticks.Builder.init().min(wMin).max(wMax).build()).build()))
				.build());
		warehouseChartJs.setWidth("90%");

		productChartJs = new Chartjs(Chartjs.TYPE_BAR);
		productChartJs.setData(productData);
		productChartJs.setOptions(ChartjsOptions.Builder.init()
				.scales(new Scales().addY(Axe.Builder.init().scaleLabel(ScaleLabel.Builder.init().display(true).build())
						.ticks(Ticks.Builder.init().min(pMin).max(pMax).build()).build()))
				.build());
		productChartJs.setWidth("90%");

		Grid grid = new Grid();
		grid.setSclass("pipra-charts-title-indicator");
		appendChild(grid);
		grid.makeNoStrip();

		Rows rows = new Rows();
		grid.appendChild(rows);

		Row row = null;
		List<Chartjs> list = new ArrayList<>();
		list.add(warehouseChartJs);
		list.add(productChartJs);
		for (int i = 0; i < list.size(); i++) {
			if (row == null || i % 2 == 0) {
				row = new Row();
				rows.appendChild(row);

				Vlayout vlayout = new Vlayout();
				vlayout.setSclass("pipra-charts-vLayout");
				Label x = new Label("Amount");
				x.setSclass("pipra-charts-vLayout-LabelText");
				labelAmount = new Label("\u20B9 " + amount);
				labelAmount.setSclass("pipra-charts-vLayout-LabelNumber");
				Label a = new Label("Orders");
				a.setSclass("pipra-charts-vLayout-LabelText");
				labelOrdersProcessedCount = new Label(ordersProcessedCount);
				labelOrdersProcessedCount.setSclass("pipra-charts-vLayout-LabelNumber");
				Label c = new Label("Lines");
				c.setSclass("pipra-charts-vLayout-LabelText");
				labelLinesProcessedCount = new Label(linesProcessedCount);
				labelLinesProcessedCount.setSclass("pipra-charts-vLayout-LabelNumber");
				Label e = new Label("Units");
				e.setSclass("pipra-charts-vLayout-LabelText");
				labelUnitsProcessedCount = new Label(unitsProcessedCount + "");
				labelUnitsProcessedCount.setSclass("pipra-charts-vLayout-LabelNumber");
				vlayout.appendChild(x);
				vlayout.appendChild(labelAmount);
				vlayout.appendChild(a);
				vlayout.appendChild(labelOrdersProcessedCount);
				vlayout.appendChild(c);
				vlayout.appendChild(labelLinesProcessedCount);
				vlayout.appendChild(e);
				vlayout.appendChild(labelUnitsProcessedCount);
				row.appendCellChild(vlayout, 1);
			}

			Div div = new Div();
			row.appendCellChild(div, 5);
			div.setSclass("pipra-charts-title-indicator");

			div.appendChild(list.get(i));
			Div titleDiv = new Div();
			titleDiv.setSclass("pipra-charts-title-indicator");
			Label label = null;
			if (i == 0)
				label = new Label("Warehouse");
			else
				label = new Label("Product");
			div.appendChild(titleDiv);
			label.setSclass("pipra-charts-vLayout-LabelText");
			titleDiv.appendChild(label);
		}

		return grid;
	}

	private void warehouseCategoryModel() {
		warehouseData = new CategoryChartjsData();
		String barColor = "#2563EB";
		Set<String> wNames = warehouseListData.keySet();
		List<String> wNamesList = new ArrayList<>(wNames);
		Collection<Integer> wValues = warehouseListData.values();
		List<Integer> wValuesList = new ArrayList<>(wValues);
		if (wValuesList.size() != 0) {
			wMax = Collections.max(wValuesList);
			wMin = Collections.min(wValuesList);
			wMax = wMax + wMax/4;
			if (wMin == 1)
				wMin = 0;
			else {
				wMin = wMin - wMin / 2;
				if (wMin % 2 != 0)
					wMin--;
			}
			for (String warehouseName : wNamesList) {
				warehouseData.add(warehouseName, ThreadLocalRandom.current().nextInt(wMin, wMax), barColor);
			}
			warehouseData.addDataset(Dataset.Builder.init().fill(false).borderColor("#2563EB")
					.label("#No. of Orders Today").backgroundColor("#2563EB").data(wValuesList).build());
		} else {
			warehouseData.addDataset(Dataset.Builder.init().fill(false).borderColor("#2563EB").label("#No. Orders Found")
					.backgroundColor("#2563EB").data(wValuesList).build());
		}

		warehouseData.getDatasets().remove(0);
	}

	private void productCategoryModel() {
		productData = new CategoryChartjsData();
		String barColor = "#A0D7E7";
		Set<String> pNames = productListData.keySet();
		List<String> pNamesList = new ArrayList<>(pNames);
		Collection<Integer> pValues = productListData.values();
		List<Integer> pValuesList = new ArrayList<>(pValues);
		if (pValuesList.size() != 0) {
			pMax = Collections.max(pValuesList);
			pMin = Collections.min(pValuesList);
			pMax = pMax + pMax/4;
			if (pMin == 1)
				pMin = 0;
			else {
				pMin = pMin - pMin / 2;
				if (pMin % 2 != 0)
					pMin--;
			}
			for (String productName : pNamesList) {
				List<List<String>> labelsData = new ArrayList<>();
				labelsData.add(Arrays.asList("Jake", "Active: 2 hrs", "Score: 1", "Expected: 127", "Attempts: 4"));

				productData.add(productName, ThreadLocalRandom.current().nextInt(pMin, pMax), barColor);
			}
			productData.addDataset(Dataset.Builder.init().fill(false).borderColor("#A0D7E7")
					.label("#No. of Orders Today").backgroundColor("#A0D7E7").data(pValuesList).build());
		} else {
			productData.addDataset(Dataset.Builder.init().fill(false).borderColor("#A0D7E7").label("#No. Orders Found")
					.backgroundColor("#A0D7E7").data(pValuesList).build());
		}
		productData.getDatasets().remove(0);
	}

	private void initOptions() {

		Properties ctx = Env.getCtx();
		int clientId = Env.getAD_Client_ID(ctx);
		int wareHouseId = DPWarehouseSelection.getWareHouse_ID(ctx);
		int productId = DPWarehouseSelection.getProduct_ID(ctx);

		PreparedStatement pstmt = null;
		ResultSet RS = null;
		try {

			DB.close(RS, pstmt);
			RS = null;
			pstmt = null;

			String sql = null;
			String warehouseChartSql = null;
			String productChartSql = null;
			if (wareHouseId == 0 && productId == 0) {
				sql = "\n"
						+ "SELECT count(distinct co.m_inout_id) as orderCount, col.count as orderLineCount, sum(col.qtyentered) as qnty, \n"
						+ "0 as totalamount FROM adempiere.m_inout co\n"
						+ "join adempiere.m_inoutline col on col.m_inout_id = co.m_inout_id \n"
						+ "where co.issotrx='N' and co.ad_client_id="+clientId+" and co.created >= CURRENT_DATE and col.qtyentered !=0 AND co.docstatus = 'CO';";
				
				warehouseChartSql = "SELECT COUNT(co.m_inout_id) AS order_count, mw.name\n"
						+ "FROM adempiere.m_inout co\n"
						+ "JOIN adempiere.m_warehouse mw ON mw.m_warehouse_id = co.m_warehouse_id\n"
						+ "WHERE co.ad_client_id ="+clientId+"\n"
						+ "  AND co.created >= CURRENT_DATE \n"
						+ "  AND co.docstatus = 'CO' \n"
						+ "  AND co.issotrx = 'N'\n"
						+ "GROUP BY mw.name\n"
						+ "ORDER BY MAX(co.created) DESC\n"
						+ "LIMIT 5;";
				
				productChartSql = "\n"
						+ "SELECT COUNT(co.m_inout_id) AS order_count, mp.name, SUM(col.qtyentered) AS qntyOrdered\n"
						+ "FROM adempiere.m_inout co\n"
						+ "JOIN adempiere.m_inoutline col ON col.m_inout_id = co.m_inout_id\n"
						+ "JOIN adempiere.m_product mp ON mp.m_product_id = col.m_product_id\n"
						+ "WHERE co.ad_client_id = "+clientId+"\n"
						+ "  AND co.created >= CURRENT_DATE \n"
						+ "  AND co.docstatus = 'CO' \n"
						+ "  AND co.issotrx = 'N'\n"
						+ "GROUP BY mp.name\n"
						+ "ORDER BY MAX(co.created) DESC\n"
						+ "LIMIT 5;";
				
			} else if (wareHouseId != 0 && productId == 0) {
				sql = "SELECT COUNT(DISTINCT co.m_inout_id) AS orderCount, \n"
						+ "       col.count AS orderLineCount, \n"
						+ "       SUM(col.qtyentered) AS qnty, \n"
						+ "       0 AS totalamount \n"
						+ "FROM adempiere.m_inout co\n"
						+ "JOIN adempiere.m_inoutline col ON col.m_inout_id = co.m_inout_id \n"
						+ "WHERE co.issotrx = 'N' \n"
						+ "  AND co.m_warehouse_id ="+wareHouseId+"\n"
						+ "  AND co.docstatus = 'CO' \n"
						+ "  AND co.created >= CURRENT_DATE \n"
						+ "  AND co.ad_client_id = "+clientId+"\n"
						+ "  AND col.qtyentered != 0;";
				
				warehouseChartSql = "SELECT COUNT(co.m_inout_id) AS order_count, \n"
						+ "       mw.name\n"
						+ "FROM adempiere.m_inout co\n"
						+ "JOIN adempiere.m_warehouse mw ON mw.m_warehouse_id = co.m_warehouse_id\n"
						+ "WHERE co.ad_client_id = "+clientId+" \n"
						+ "  AND co.issotrx = 'N' \n"
						+ "  AND co.docstatus = 'CO' \n"
						+ "  AND co.created >= CURRENT_DATE \n"
						+ "  AND mw.m_warehouse_id = "+wareHouseId+"\n"
						+ "GROUP BY mw.name\n"
						+ "ORDER BY MAX(co.created) DESC\n"
						+ "LIMIT 5;";
				
				productChartSql = "\n"
						+ "SELECT COUNT(co.m_inout_id) AS order_count, \n"
						+ "       mp.name, \n"
						+ "       SUM(col.qtyentered) AS qntyOrdered\n"
						+ "FROM adempiere.m_inout co\n"
						+ "JOIN adempiere.m_inoutline col ON col.m_inout_id = co.m_inout_id\n"
						+ "JOIN adempiere.m_product mp ON mp.m_product_id = col.m_product_id\n"
						+ "WHERE co.ad_client_id = "+clientId+"\n"
						+ "  AND co.issotrx = 'N' \n"
						+ "  AND co.docstatus = 'CO' \n"
						+ "  AND co.created >= CURRENT_DATE \n"
						+ "  AND co.m_warehouse_id = "+wareHouseId+"\n"
						+ "GROUP BY mp.name\n"
						+ "ORDER BY MAX(co.created) DESC\n"
						+ "LIMIT 5;";
			} else if (wareHouseId == 0 && productId != 0) {
				sql = "  \n"
						+ "  SELECT COUNT(DISTINCT co.m_inout_id) AS orderCount, \n"
						+ "       col.count AS orderLineCount, \n"
						+ "       SUM(col.qtyentered) AS qnty, \n"
						+ "       0 AS totalamount \n"
						+ "FROM adempiere.m_inout co \n"
						+ "JOIN adempiere.m_inoutline col ON col.m_inout_id = co.m_inout_id \n"
						+ "WHERE co.issotrx = 'N' \n"
						+ "  AND col.m_product_id = "+productId+"\n"
						+ "  AND co.docstatus = 'CO' \n"
						+ "  AND co.created >= CURRENT_DATE \n"
						+ "  AND co.ad_client_id ="+clientId+"\n"
						+ "  AND col.qtyentered != 0;";
				
				warehouseChartSql = "SELECT COUNT(co.m_inout_id) AS order_count, \n"
						+ "       mw.name\n"
						+ "FROM adempiere.m_inout co\n"
						+ "JOIN adempiere.m_warehouse mw ON mw.m_warehouse_id = co.m_warehouse_id\n"
						+ "JOIN adempiere.m_inoutline col ON col.m_inout_id = co.m_inout_id\n"
						+ "JOIN adempiere.m_product mp ON mp.m_product_id = col.m_product_id\n"
						+ "WHERE co.ad_client_id = "+clientId+"\n"
						+ "  AND co.issotrx = 'N' \n"
						+ "  AND co.docstatus = 'CO' \n"
						+ "  AND co.created >= CURRENT_DATE \n"
						+ "  AND mp.m_product_id = "+productId+"\n"
						+ "GROUP BY mw.name\n"
						+ "ORDER BY MAX(co.created) DESC\n"
						+ "LIMIT 5;\n"
						+ "";
				
				productChartSql = "\n"
						+ "SELECT COUNT(co.m_inout_id) AS order_count, \n"
						+ "       mp.name, \n"
						+ "       SUM(col.qtyentered) AS qntyOrdered\n"
						+ "FROM adempiere.m_inout co\n"
						+ "JOIN adempiere.m_inoutline col ON col.m_inout_id = co.m_inout_id\n"
						+ "JOIN adempiere.m_product mp ON mp.m_product_id = col.m_product_id\n"
						+ "WHERE co.ad_client_id ="+clientId+"\n"
						+ "  AND co.issotrx = 'N' \n"
						+ "  AND co.docstatus = 'CO' \n"
						+ "  AND co.created >= CURRENT_DATE \n"
						+ "  AND mp.m_product_id = "+productId+"\n"
						+ "GROUP BY mp.name\n"
						+ "ORDER BY MAX(co.created) DESC\n"
						+ "LIMIT 5;";
			}

			pstmt = DB.prepareStatement(sql.toString(), null);
			RS = pstmt.executeQuery();
			while (RS.next()) {
				ordersProcessedCount = RS.getString("orderCount");
				linesProcessedCount = RS.getString("orderLineCount");
				String units = RS.getString("qnty");
				if (units != null)
					unitsProcessedCount = Integer.parseInt(units.split("\\.")[0]);
				else
					unitsProcessedCount = 0;
				amount = RS.getString("totalamount");
				if (amount == null)
					amount = "0";

			}
			DB.close(RS, pstmt);
			RS = null;
			pstmt = null;
			
			pstmt = DB.prepareStatement(warehouseChartSql.toString(), null);
			RS = pstmt.executeQuery();
			warehouseListData = new LinkedHashMap<String, Integer>();
			while (RS.next()) {
				int orderCount = RS.getInt("order_count");
				String wareHousename = RS.getString("name");
				warehouseListData.put(wareHousename, orderCount);

			}
			DB.close(RS, pstmt);
			RS = null;
			pstmt = null;
			
			pstmt = DB.prepareStatement(productChartSql.toString(), null);
			RS = pstmt.executeQuery();
			productListData = new LinkedHashMap<String, Integer>();
			while (RS.next()) {
//				int orderCount = RS.getInt("order_count");
				String productName = RS.getString("name");
				int qntyOrdered = RS.getInt("qntyOrdered");
				productListData.put(productName, qntyOrdered);

			}
			DB.close(RS, pstmt);

		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			DB.close(RS, pstmt);
		}
	}

	@Override
	public void updateUI() {
		
		initOptions();
		warehouseCategoryModel();
		productCategoryModel();
		
		this.removeChild(grid);
		grid = createActivitiesPanel();
		this.appendChild(grid);
	}

	@Override
	public void refresh(ServerPushTemplate template) {
		template.executeAsync(this);
	}

	@Override
	public void onEvent(Event event) throws Exception {
	}
}
