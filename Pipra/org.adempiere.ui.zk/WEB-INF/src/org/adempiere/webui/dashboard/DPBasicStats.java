package org.adempiere.webui.dashboard;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.adempiere.webui.component.Label;
import org.adempiere.webui.theme.ITheme;
import org.adempiere.webui.util.ServerPushTemplate;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Div;

public class DPBasicStats extends DashboardPanel implements EventListener<Event> {

	private Label labelSKU;
	private String ordersText;
	private Label labelSkuCount;
	private Label labelBorder;

//	private String occupanyText;
//	private Label labelOccupancy;
//	private Label labelOccupancyCount;

	private Label labelWarehouse;
	private String warehouseText;
	private Label labelWarehouseCount;

	private Label labelUsers;
	private Label labelUsersCount;
	private String userText;

	private Label labelStockCheck;
	private Label labelStockCheckCount;
	private String stockCheckText;
	private Label labelSubText;
	private String lastStockCheck;

	private String warehouseDefaultName;
	private int ordersProcessedCount;
	private String warehouseCount;
	private String userCount;
	private String occupancyPercent;
	private int stockCheckdays;

	private static final long serialVersionUID = 1L;
	List<String> warehouseNames = new ArrayList<String>();

	public DPBasicStats() {
		super();
		this.setSclass("activities-box");
		initOptions();
		this.appendChild(createActivitiesPanel());
	}

	private Div createActivitiesPanel() {
		Div div = new Div();
		div.setSclass(ITheme.WARE_HOUSE_DATA_WIDGET);

		Div childDivFirst = new Div();
		Div subChildFirst = new Div();

		labelSKU = new Label();
		labelSKU.setSclass(ITheme.DASHBOARD_WIDGET_LABELS);
		ordersText = "SKUs Active";

		labelBorder = new Label();
		labelBorder.setSclass(ITheme.DASHBOARD_WIDGET_LABELS_BORDER_PURPLE);

		labelSKU.setValue(ordersText);

		subChildFirst.appendChild(labelBorder);
		subChildFirst.appendChild(labelSKU);

		childDivFirst.appendChild(subChildFirst);
		labelSkuCount = new Label();
		labelSkuCount.setSclass(ITheme.DASHBOARD_WIDGET_COUNT);
		labelSkuCount.setValue(" " + ordersProcessedCount);
		childDivFirst.appendChild(labelSkuCount);
		childDivFirst.setSclass(ITheme.DASHBOARD_WIDGET_Text_COUNT_CONT_BASIC_STATS);
		div.appendChild(childDivFirst);

//		Div childDivSec = new Div();
//		Div subChildSec = new Div();

//		labelOccupancy = new Label();
//		labelOccupancy.setSclass(ITheme.DASHBOARD_WIDGET_LABELS);
//		occupanyText = "Occupancy";
//		labelOccupancy.setValue(occupanyText);

//		labelBorder = new Label();
//		labelBorder.setSclass(ITheme.DASHBOARD_WIDGET_LABELS_BORDER_LIGHT_BLUE);

//		subChildSec.appendChild(labelBorder);
//		subChildSec.appendChild(labelOccupancy);
//		childDivSec.appendChild(subChildSec);
//		labelOccupancyCount = new Label();
//		labelOccupancyCount.setSclass(ITheme.DASHBOARD_WIDGET_COUNT);
//		childDivSec.appendChild(labelOccupancyCount);
//		labelOccupancyCount.setValue(" " + occupancyPercent );
//		childDivSec.setSclass(ITheme.DASHBOARD_WIDGET_Text_COUNT_CONT_BASIC_STATS);
//		div.appendChild(childDivSec);

		Div childDivThird = new Div();
		Div subChildThird = new Div();

		labelWarehouse = new Label();
		labelWarehouse.setSclass(ITheme.DASHBOARD_WIDGET_LABELS);
		warehouseText = "Warehouse";
		labelWarehouse.setValue(warehouseText);

		labelBorder = new Label();
		labelBorder.setSclass(ITheme.DASHBOARD_WIDGET_LABELS_BORDER_PURPLE);

		subChildThird.appendChild(labelBorder);
		subChildThird.appendChild(labelWarehouse);
		childDivThird.appendChild(subChildThird);
		labelWarehouseCount = new Label();
		labelWarehouseCount.setSclass(ITheme.DASHBOARD_WIDGET_COUNT);
		labelWarehouseCount.setValue("  " + warehouseCount);
		childDivThird.appendChild(labelWarehouseCount);
		childDivThird.setSclass(ITheme.DASHBOARD_WIDGET_Text_COUNT_CONT_BASIC_STATS);
		div.appendChild(childDivThird);

		Div childDivFour = new Div();
		Div subChildFour = new Div();

		labelUsers = new Label();
		labelUsers.setSclass(ITheme.DASHBOARD_WIDGET_LABELS);
		userText = "Users";
		labelUsers.setValue(userText);

		labelBorder = new Label();
		labelBorder.setSclass(ITheme.DASHBOARD_WIDGET_LABELS_BORDER_LIGHT_BLUE);

		subChildFour.appendChild(labelBorder);
		subChildFour.appendChild(labelUsers);
		childDivFour.appendChild(subChildFour);

		labelUsersCount = new Label();
		labelUsersCount.setSclass(ITheme.DASHBOARD_WIDGET_COUNT);
		childDivFour.appendChild(labelUsersCount);
		labelUsersCount.setValue(" " + userCount);
		childDivFour.setSclass(ITheme.DASHBOARD_WIDGET_Text_COUNT_CONT_BASIC_STATS);
		div.appendChild(childDivFour);

		Div childDivFive = new Div();
		Div subChildFive = new Div();

		labelStockCheck = new Label();
		labelStockCheck.setSclass(ITheme.DASHBOARD_WIDGET_LABELS);
		stockCheckText = "Stock Check";
		labelStockCheck.setValue(stockCheckText);

		labelBorder = new Label();
		labelBorder.setSclass(ITheme.DASHBOARD_WIDGET_LABELS_BORDER_ORANGE);

		subChildFive.appendChild(labelBorder);
		subChildFive.appendChild(labelStockCheck);

		childDivFive.appendChild(subChildFive);

		Div stockCheckDiv = new Div();
		stockCheckDiv.setSclass(ITheme.DASHBOARD_WIDGET_Text_COUNT_CONT_BASIC_STATS);
		labelStockCheckCount = new Label();
		labelStockCheckCount.setValue("  " + stockCheckdays);
		labelStockCheckCount.setSclass(ITheme.DASHBOARD_WIDGET_COUNT);
		stockCheckDiv.appendChild(labelStockCheckCount);
		labelSubText = new Label();
		labelSubText.setSclass(ITheme.DASHBOARD_WIDGET_LABELS);
		labelSubText.setSclass(ITheme.DASHBOARD_WIDGET_LABELS_COUNT_TEXT);
		lastStockCheck = "days since last check";
		labelSubText.setValue(lastStockCheck);
		stockCheckDiv.appendChild(labelSubText);
		childDivFive.appendChild(stockCheckDiv);

		childDivFive.setSclass(ITheme.DASHBOARD_WIDGET_Text_COUNT_CONT_BASIC_STATS);
		div.appendChild(childDivFive);

		return div;
	}

	private void initOptions() {

		Properties ctx = Env.getCtx();
		int clientId = Env.getAD_Client_ID(ctx);
		int wareHouseId = DPWarehouseSelection.getWareHouse_ID(ctx);
		int productId = DPWarehouseSelection.getProduct_ID(ctx);

		PreparedStatement pstmt = null;
		ResultSet RS = null;
		try {
			String warehousesql = "select name from m_warehouse where m_warehouse_id =" + wareHouseId + ";";
			pstmt = DB.prepareStatement(warehousesql.toString(), null);
			RS = pstmt.executeQuery();
			while (RS.next()) {
				String name = RS.getString("name");
				warehouseDefaultName = null;
				if (name != null) {
					warehouseDefaultName = name;
				}
			}
			DB.close(RS, pstmt);
			RS = null;
			pstmt = null;

			String warehouseCountql = "select count(*) from m_warehouse where ad_client_id =" + clientId + ";";
			pstmt = DB.prepareStatement(warehouseCountql.toString(), null);
			RS = pstmt.executeQuery();
			while (RS.next()) {
				warehouseCount = RS.getString("count");
			}
			DB.close(RS, pstmt);
			RS = null;
			pstmt = null;

			String skuCountSql = null;
			String occupancySql = null;
			String stockCheckSql = null;
			if (wareHouseId == 0 && productId == 0) {
				skuCountSql = "select count(distinct ms.m_product_id) from m_storageonhand ms\n"
						+ "join m_locator ml on ml.m_locator_id = ms.m_locator_id \n"
						+ "join m_warehouse mw on mw.m_warehouse_id = ml.m_warehouse_id \n" + "and "
						+ "ms.ad_client_id=" + clientId + ";";

				occupancySql = "SELECT count(distinct ml) as occupiedLocator,(SELECT count(ml) from m_locator ml \n"
						+ "join m_warehouse mw on mw.m_warehouse_id = ml.m_warehouse_id\n" + "where mw.ad_client_id = "
						+ clientId + ") as locatorCount from m_locator ml\n"
						+ "join m_inoutline mi on mi.m_locator_id = ml.m_locator_id \n"
						+ "join m_warehouse mw on mw.m_warehouse_id = ml.m_warehouse_id \n" + "where "
						+ "mw.ad_client_id = " + clientId + ";";
				stockCheckSql = "SELECT EXTRACT(DAY FROM CURRENT_DATE - i.created) AS stockCheckDays\n"
						+ "FROM adempiere.m_inventory i \n"
						+ "JOIN adempiere.m_inventoryline li ON i.m_inventory_id = li.m_inventory_id\n"
						+ "WHERE i.ad_client_id = "+ clientId +" AND i.docStatus = 'CO' ORDER BY i.created DESC LIMIT 1;";
			} else if (wareHouseId != 0 && productId == 0) {
				skuCountSql = "select count(distinct ms.m_product_id) from m_storageonhand ms\n"
						+ "join m_locator ml on ml.m_locator_id = ms.m_locator_id \n"
						+ "join m_warehouse mw on mw.m_warehouse_id = ml.m_warehouse_id \n" + "and mw.name='"
						+ warehouseDefaultName + "'and ms.ad_client_id=" + clientId + ";";

				occupancySql = "SELECT count(distinct ml) as occupiedLocator,(SELECT count(ml) from m_locator ml \n"
						+ "join m_warehouse mw on mw.m_warehouse_id = ml.m_warehouse_id\n" + "where mw.name= '"
						+ warehouseDefaultName + "' and mw.ad_client_id = " + clientId
						+ ") as locatorCount from m_locator ml\n"
						+ "join m_inoutline mi on mi.m_locator_id = ml.m_locator_id \n"
						+ "join m_warehouse mw on mw.m_warehouse_id = ml.m_warehouse_id \n" + "where mw.name= '"
						+ warehouseDefaultName + "' and mw.ad_client_id = " + clientId + ";";
				
				stockCheckSql = "SELECT EXTRACT(DAY FROM CURRENT_DATE - i.created) AS stockCheckDays\n"
				+ "FROM adempiere.m_inventory i JOIN adempiere.m_inventoryline li ON i.m_inventory_id = li.m_inventory_id\n"
				+ "JOIN adempiere.m_warehouse wh ON wh.m_warehouse_id = i.m_warehouse_id WHERE i.ad_client_id = "+ clientId +" \n"
				+ "AND wh.m_warehouse_id = "+ wareHouseId +" AND i.docStatus = 'CO'ORDER BY i.created DESC LIMIT 1";
			} else if (wareHouseId == 0 && productId != 0) {
				skuCountSql = "select count(distinct  ms.m_product_id) from m_storageonhand ms\n"
						+ "join m_product mp on mp.m_product_id = ms.m_product_id \n" + "where mp.m_product_id = "
						+ productId + " and ms.ad_client_id="+clientId+";";

				occupancySql = "SELECT count(distinct ms.m_locator_id) as occupiedLocator,(SELECT count(mlo) from m_locator mlo\n"
						+ "where mlo.ad_client_id = "+clientId+") as locatorCount from m_storageonhand ms \n"
						+ " where ms.m_product_id= " + productId + " and ms.ad_client_id = " + clientId + "\n" + "";
				
				stockCheckSql = "SELECT EXTRACT(DAY FROM CURRENT_DATE - i.created) AS stockCheckDays FROM adempiere.m_inventory i \n"
						+ "JOIN adempiere.m_inventoryline li ON i.m_inventory_id = li.m_inventory_id\n"
						+ "JOIN adempiere.m_product pr ON pr.m_product_id = li.m_product_id WHERE i.ad_client_id = "+ clientId +" \n"
						+ "AND pr.m_product_id = "+ productId +" AND i.docStatus = 'CO'ORDER BY i.created DESC LIMIT 1";
			}

			pstmt = DB.prepareStatement(stockCheckSql.toString(), null);// get_TrxName());
			RS = pstmt.executeQuery();
			while (RS.next()) {
				stockCheckdays = 0;
				stockCheckdays = RS.getInt("stockCheckDays");
			}
			DB.close(RS, pstmt);
			RS = null;
			pstmt = null;
			
			pstmt = DB.prepareStatement(skuCountSql.toString(), null);// get_TrxName());
			RS = pstmt.executeQuery();
			while (RS.next()) {
				ordersProcessedCount = 0;
				ordersProcessedCount = RS.getInt("count");
			}
			DB.close(RS, pstmt);
			RS = null;
			pstmt = null;

			String userCountSql = "select count(*) from ad_user where ad_client_id=" + clientId
					+ " and isActive = 'Y';";
			pstmt = DB.prepareStatement(userCountSql.toString(), null);// get_TrxName());
			RS = pstmt.executeQuery();
			while (RS.next()) {
				userCount = RS.getString("count");
			}
			DB.close(RS, pstmt);
			RS = null;
			pstmt = null;

			pstmt = DB.prepareStatement(occupancySql.toString(), null);// get_TrxName());
			RS = pstmt.executeQuery();
			while (RS.next()) {
				int occupiedLocator = RS.getInt("occupiedLocator");
				int locatorCount = RS.getInt("locatorCount");
				if (occupiedLocator != 0)
					occupancyPercent = String.valueOf(occupiedLocator * 100 / locatorCount);
				else
					occupancyPercent = "0";
			}
			DB.close(RS, pstmt);
			RS = null;
			pstmt = null;

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void refresh(ServerPushTemplate template) {
		initOptions();
		template.executeAsync(this);
	}

	@Override
	public void updateUI() {
		labelSkuCount.setValue(" " + ordersProcessedCount);
//		labelOccupancyCount.setValue(" " + occupancyPercent );
		labelWarehouseCount.setValue("  " + warehouseCount);
		labelUsersCount.setValue(" " + userCount);
		labelStockCheckCount.setValue("  " + stockCheckdays);

	}

	@Override
	public void onEvent(Event event) throws Exception {

	}
}
