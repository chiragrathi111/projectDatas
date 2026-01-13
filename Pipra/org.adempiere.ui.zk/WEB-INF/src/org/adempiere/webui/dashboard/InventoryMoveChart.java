package org.adempiere.webui.dashboard;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import org.adempiere.webui.util.ServerPushTemplate;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Div;

import tools.dynamia.zk.addons.chartjs.Axe;
import tools.dynamia.zk.addons.chartjs.CategoryChartjsData;
import tools.dynamia.zk.addons.chartjs.Chartjs;
import tools.dynamia.zk.addons.chartjs.ChartjsData;
import tools.dynamia.zk.addons.chartjs.ChartjsOptions;
import tools.dynamia.zk.addons.chartjs.Dataset;
import tools.dynamia.zk.addons.chartjs.ScaleLabel;
import tools.dynamia.zk.addons.chartjs.Scales;
import tools.dynamia.zk.addons.chartjs.Ticks;

public class InventoryMoveChart extends DashboardPanel implements EventListener<Event> {

	private static final long serialVersionUID = 1L;
	private ChartjsData lineModel;
	private Chartjs chartjs;
	private Div div;
	private List<Integer> dataList;
	private List<Integer> dispatch ;
	private List<Integer> internalMove;
	private Component parent;
	CategoryChartjsData data;

	public InventoryMoveChart() {
		super();
		this.setSclass("activities-box");
		initLineModel();
		this.appendChild(createActivitiesPanel());
	}

	private Div createActivitiesPanel() {
		div = new Div();
		
		chartjs = new Chartjs(Chartjs.TYPE_LINE);
		chartjs.setData(data);
				
		chartjs.setOptions(ChartjsOptions.Builder.init()
				.scales(new Scales().addY(Axe.Builder.init().scaleLabel(ScaleLabel.Builder.init().display(true).build())
						.ticks(Ticks.Builder.init().min(0).max(100).build()).build()))
				.build());
		chartjs.setWidth("90%");
		chartjs.setParent(parent);
		div.appendChild(chartjs);
		return div;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void initLineModel() {

		int januaryIn = 0;
		int febrauryIn = 0;
		int marchIn = 0;
		int aprilIn = 0;
		int mayIn = 0;
		int juneIn = 0;
		int julyIn = 0;
		int augustIn = 0;
		int septempberIn = 0;
		int octoberIn = 0;
		int novemberIn = 0;
		int decemberIn = 0;

		int januarySales = 0;
		int febraurySales = 0;
		int marchSales = 0;
		int aprilSales = 0;
		int maySales = 0;
		int juneSales = 0;
		int julySales = 0;
		int augustSales = 0;
		int septempberSales = 0;
		int octoberSales = 0;
		int novemberSales = 0;
		int decemberSales = 0;

		Properties ctx = Env.getCtx();
		int clientId = Env.getAD_Client_ID(ctx);
		int wareHouseId = DPWarehouseSelection.getWareHouse_ID(ctx);
		int productId = DPWarehouseSelection.getProduct_ID(ctx);

		try {
			String sqlIn = null;
			String salesSql = null;
			if (wareHouseId == 0 && productId == 0) {
				sqlIn = "SELECT TO_CHAR(mr.created, 'Month') AS month_name, COUNT(mr) AS returnCount \n"
						+ "FROM m_rma mr WHERE mr.ad_client_id = " + clientId
						+ " AND mr.created BETWEEN (CURRENT_DATE - INTERVAL '6 MONTH') \n"
						+ "AND CURRENT_DATE GROUP BY month_name;";

				salesSql = "SELECT TO_CHAR(created, 'Month') AS month_name, count(distinct c_order_id) from m_inout where ad_client_id ="
						+ clientId + " and \n"
						+ "issotrx ='Y' AND created BETWEEN (CURRENT_DATE - INTERVAL '6 MONTH') \n"
						+ "AND CURRENT_DATE GROUP BY month_name;";
			} else if (wareHouseId != 0 && productId == 0) {
				sqlIn = "SELECT TO_CHAR(mr.created, 'Month') AS month_name, COUNT(mr) AS returnCount \n"
						+ "FROM m_rma mr JOIN m_inout mi ON mi.m_inout_id = mr.inout_id\n"
						+ "WHERE mr.isactive = 'Y' AND mi.m_warehouse_id=" + wareHouseId
						+ " AND mr.ad_client_id = 11 AND mr.created BETWEEN (CURRENT_DATE - INTERVAL '6 MONTH') \n"
						+ "AND CURRENT_DATE GROUP BY month_name;";
				salesSql = "SELECT TO_CHAR(created, 'Month') AS month_name, count(distinct c_order_id) from m_inout where ad_client_id ="
						+ clientId + " and \n" + "issotrx ='Y' AND m_warehouse_id = " + wareHouseId
						+ " AND created BETWEEN (CURRENT_DATE - INTERVAL '6 MONTH') \n"
						+ "AND CURRENT_DATE GROUP BY month_name;";
			} else if (wareHouseId == 0 && productId != 0) {
				sqlIn = "SELECT TO_CHAR(mr.created, 'Month') AS month_name, COUNT(mr) AS returnCount \n"
						+ "FROM m_rma mr JOIN m_rmaline mrl on mrl.m_rma_id = mr.m_rma_id\n"
						+ "WHERE mr.ad_client_id = " + clientId + " AND mrl.m_product_id=" + productId
						+ " AND mr.created BETWEEN (CURRENT_DATE - INTERVAL '6 MONTH') \n"
						+ "AND CURRENT_DATE GROUP BY month_name;";
				salesSql = "SELECT TO_CHAR(mi.created, 'Month') AS month_name, count(distinct mi.c_order_id) from m_inout mi \n"
						+ "join m_inoutline ml on ml.m_inout_id = mi.m_inout_id where mi.ad_client_id =" + clientId
						+ " and \n" + "ml.m_product_id = " + productId
						+ " and mi.issotrx ='Y' AND mi.created BETWEEN (CURRENT_DATE - INTERVAL '6 MONTH') \n"
						+ "AND CURRENT_DATE GROUP BY month_name;";
			}

			PreparedStatement pstmtIn = DB.prepareStatement(sqlIn.toString(), null);
			ResultSet listIn = pstmtIn.executeQuery();

			while (listIn.next()) {
				int returnCount = listIn.getInt("returncount");
				String monthNameDB = listIn.getString("month_name");
				String monthName = monthNameDB.trim();
				if (monthName.equalsIgnoreCase("January"))
					januaryIn = returnCount;
				else if (monthName.equalsIgnoreCase("Febraury"))
					febrauryIn = returnCount;
				else if (monthName.equalsIgnoreCase("March"))
					marchIn = returnCount;
				else if (monthName.equalsIgnoreCase("April"))
					aprilIn = returnCount;
				else if (monthName.equalsIgnoreCase("May"))
					mayIn = returnCount;
				else if (monthName.equalsIgnoreCase("June"))
					juneIn = returnCount;
				else if (monthName.equalsIgnoreCase("July"))
					julyIn = returnCount;
				else if (monthName.equalsIgnoreCase("August"))
					augustIn = returnCount;
				else if (monthName.equalsIgnoreCase("September"))
					septempberIn = returnCount;
				else if (monthName.equalsIgnoreCase("October"))
					octoberIn = returnCount;
				else if (monthName.equalsIgnoreCase("November"))
					novemberIn = returnCount;
				else if (monthName.equalsIgnoreCase("December"))
					decemberIn = returnCount;
			}
			DB.close(listIn, pstmtIn);
			listIn = null;
			pstmtIn = null;

			pstmtIn = DB.prepareStatement(salesSql.toString(), null);
			listIn = pstmtIn.executeQuery();

			while (listIn.next()) {
				int count = listIn.getInt("count");
				String monthNameDB = listIn.getString("month_name");
				String monthName = monthNameDB.trim();
				if (monthName.equalsIgnoreCase("January"))
					januarySales = count;
				else if (monthName.equalsIgnoreCase("Febraury"))
					febraurySales = count;
				else if (monthName.equalsIgnoreCase("March"))
					marchSales = count;
				else if (monthName.equalsIgnoreCase("April"))
					aprilSales = count;
				else if (monthName.equalsIgnoreCase("May"))
					maySales = count;
				else if (monthName.equalsIgnoreCase("June"))
					juneSales = count;
				else if (monthName.equalsIgnoreCase("July"))
					julySales = count;
				else if (monthName.equalsIgnoreCase("August"))
					augustSales = count;
				else if (monthName.equalsIgnoreCase("September"))
					septempberSales = count;
				else if (monthName.equalsIgnoreCase("October"))
					octoberSales = count;
				else if (monthName.equalsIgnoreCase("November"))
					novemberSales = count;
				else if (monthName.equalsIgnoreCase("December"))
					decemberSales = count;
			}
			DB.close(listIn, pstmtIn);
			listIn = null;
			pstmtIn = null;

		} catch (SQLException e) {
			e.printStackTrace();
		}

		LocalDateTime localDate = LocalDateTime.now();
		Month currentMonth = localDate.getMonth();
		currentMonth.minus(1);
		List<Integer> monthReturnsList = new ArrayList<>(Arrays.asList(januaryIn, febrauryIn, marchIn, aprilIn, mayIn,
				juneIn, julyIn, augustIn, septempberIn, octoberIn, novemberIn, decemberIn));

		List<Integer> monthSalesList = new ArrayList<>(
				Arrays.asList(januarySales, febraurySales, marchSales, aprilSales, maySales, juneSales, julySales,
						augustSales, septempberSales, octoberSales, novemberSales, decemberSales));

//		dataList = new ArrayList<>(Arrays.asList(
//				monthSalesList.get(currentMonth.getValue()) != 0 ? (monthReturnsList.get(currentMonth.getValue()) * 100)
//						/ monthSalesList.get(currentMonth.getValue()) : 0,
//				monthSalesList.get(currentMonth.minus(1).getValue()) != 0
//						? (monthReturnsList.get(currentMonth.minus(1).getValue()) * 100)
//								/ monthSalesList.get(currentMonth.minus(1).getValue())
//						: 0,
//				monthSalesList.get(currentMonth.minus(2).getValue()) != 0
//						? (monthReturnsList.get(currentMonth.minus(2).getValue()) * 100)
//								/ monthSalesList.get(currentMonth.minus(2).getValue())
//						: 0,
//				monthSalesList.get(currentMonth.minus(3).getValue()) != 0
//						? (monthReturnsList.get(currentMonth.minus(3).getValue()) * 100)
//								/ monthSalesList.get(currentMonth.minus(3).getValue())
//						: 0,
//				monthSalesList.get(currentMonth.minus(4).getValue()) != 0
//						? (monthReturnsList.get(currentMonth.minus(4).getValue()) * 100)
//								/ monthSalesList.get(currentMonth.minus(4).getValue())
//						: 0,
//				monthSalesList.get(currentMonth.minus(5).getValue()) != 0
//						? (monthReturnsList.get(currentMonth.minus(5).getValue()) * 100)
//								/ monthSalesList.get(currentMonth.minus(5).getValue())
//						: 0));


		dataList =  new ArrayList<>(
				Arrays.asList(30, 50, 30, 40, 70, 50));
		
		dispatch =  new ArrayList<>(
				Arrays.asList(80, 30, 60, 50, 35, 80));
		
		internalMove = new ArrayList<>(
				Arrays.asList(30, 10, 40, 25,50, 40));
		
		if (data == null || data.isEmpty()) {
			data = new CategoryChartjsData();
			data.setLabels(currentMonth.getDisplayName(TextStyle.SHORT, Locale.ENGLISH),
					currentMonth.minus(1).getDisplayName(TextStyle.SHORT, Locale.ENGLISH),
					currentMonth.minus(2).getDisplayName(TextStyle.SHORT, Locale.ENGLISH),
					currentMonth.minus(3).getDisplayName(TextStyle.SHORT, Locale.ENGLISH),
					currentMonth.minus(4).getDisplayName(TextStyle.SHORT, Locale.ENGLISH),
					currentMonth.minus(5).getDisplayName(TextStyle.SHORT, Locale.ENGLISH));

			data.addDataset(Dataset.Builder.init().fill(false).borderColor("#2563EB").label("Receiving")
					.backgroundColor("#2563EB").data(dataList).build());
			
			data.addDataset(Dataset.Builder.init().fill(false).borderColor("#A0D7E7").label("Dsipacth")
					.backgroundColor("#A0D7E7").data(dispatch).build());
			
			data.addDataset(Dataset.Builder.init().fill(false).borderColor("#8F95B2").label("Internal Move")
					.backgroundColor("#8F95B2").data(internalMove).build());
			
			data.getDatasets().remove(0);
		} else {
			Dataset dataset1 = data.getDatasets().get(0);
			dataset1.setData(dataList);
			data.getDatasets().set(0, dataset1);
			
			Dataset dataset2 = data.getDatasets().get(1);
			dataset2.setData(dispatch);
			data.getDatasets().set(1, dataset2);
			
			Dataset dataset3 = data.getDatasets().get(2);
			dataset3.setData(dataList);
			data.getDatasets().set(2, dataset3);
		}
	}

	@Override
	public void updateUI() {
		initLineModel();
		chartjs.setData(data);
		chartjs.invalidate();
	}

	@Override
	public void refresh(ServerPushTemplate template) {
		template.executeAsync(this);
	}

	@Override
	public void onEvent(Event event) throws Exception {
	}
}
