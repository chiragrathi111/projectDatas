package org.adempiere.webui.dashboard;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Properties;

import org.adempiere.webui.util.ServerPushTemplate;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Toolbarbutton;

import tools.dynamia.zk.addons.chartjs.Chartjs;
import tools.dynamia.zk.addons.chartjs.ChartjsData;
import tools.dynamia.zk.addons.chartjs.ChartjsOptions;
import tools.dynamia.zk.addons.chartjs.Dataset;

public class DPWarehouseView extends DashboardPanel implements EventListener<Event> {
    
    private static final long serialVersionUID = 1L;
    private Chartjs chartjs;   // Pie chart component
    private Datebox startDateBox;
    private Datebox endDateBox;
    private Toolbarbutton refreshButton;
    private LinkedHashMap<String, Integer> visitData;  // Store visit data for pie chart
    private LinkedHashMap<String, Integer> UserIdAndName = new LinkedHashMap<String, Integer>();
    ChartjsData chartData = new ChartjsData();
    
    public DPWarehouseView() {
        super();
        this.setSclass("activities-box");
        visitData = new LinkedHashMap<>();
        
        this.appendChild(createDateRangePanel());
        this.appendChild(createChartPanel());
        
        // Load initial data
        loadChartData();
        updateChart();
    }

    private Div createDateRangePanel() {
    	Div header = new Div();
        header.setStyle("margin-bottom:10px;");

        Hbox box = new Hbox();
        box.setSpacing("6px");
        box.setAlign("center");

        startDateBox = new Datebox();
        startDateBox.setWidth("110px");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -7);
        startDateBox.setValue(cal.getTime());

        endDateBox = new Datebox();
        endDateBox.setWidth("110px");
        endDateBox.setValue(new Date());

        refreshButton = new Toolbarbutton();
        refreshButton.setIconSclass("z-icon-refresh");
        refreshButton.setTooltiptext("Refresh");
        refreshButton.setStyle(
                "font-size:16px;" +
                "color:#2e7d32;" +
                "cursor:pointer;" +
                "margin-left:6px;"
            );
        refreshButton.addEventListener("onClick", this);

        box.appendChild(startDateBox);
        box.appendChild(endDateBox);
        box.appendChild(refreshButton);

        header.appendChild(box);
        return header;
//    	Div dateRangeDiv = new Div();
//        dateRangeDiv.setSclass("date-range-panel");
//
//        // Create a vertical layout for "From" date
//        Vbox fromBox = new Vbox();
//        Label fromLabel = new Label("From");
//        fromLabel.setStyle("font-weight: bold;"); 
//        startDateBox = new Datebox();
//        startDateBox.setValue(new Date(System.currentTimeMillis() - (7L * 24 * 60 * 60 * 1000))); // Default: last 60 days
//        startDateBox.setStyle("margin-bottom: 10px;"); // Add space below
//        fromBox.appendChild(fromLabel);
//        fromBox.appendChild(startDateBox);
//
//        // Create a vertical layout for "To" date
//        Vbox toBox = new Vbox();
//        Label toLabel = new Label("To");
//        toLabel.setStyle("font-weight: bold;"); 
//        endDateBox = new Datebox();
//        endDateBox.setValue(new Date()); // Default: today
//        toBox.appendChild(toLabel);
//        toBox.appendChild(endDateBox);
//
//        // Refresh button
//        refreshButton = new Button("Refresh");
//        refreshButton.addEventListener("onClick", this);
//        refreshButton.setStyle("margin-top: 23px; font-weight: bold;  background-color: green; color: white;");
//
//        // Use Hbox for horizontal layout with spacing
//        Hbox dateBoxContainer = new Hbox();
//        dateBoxContainer.setSpacing("10px"); // Space between date fields
//        dateBoxContainer.appendChild(fromBox);
//        dateBoxContainer.appendChild(toBox);
//        dateBoxContainer.appendChild(refreshButton);
//
//        // Add components to the main div
//        dateRangeDiv.appendChild(dateBoxContainer);
//        return dateRangeDiv;
    }

    private Div createChartPanel() {
        Div chartDiv = new Div();
        chartjs = new Chartjs(Chartjs.TYPE_BAR);
        chartjs.setWidth("90%");
        chartjs.setOptions(ChartjsOptions.Builder.init().responsive(true).build());

        chartDiv.appendChild(chartjs);
        return chartDiv;
    }

    private void loadChartData() {
        Properties ctx = Env.getCtx();
        int clientId = Env.getAD_Client_ID(ctx);
        Date startDate = startDateBox.getValue();
        Date endDate = endDateBox.getValue();
        String sql = null;
        int totalCount = 0;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        sql = "WITH user_totals AS (\n"
				+ "    SELECT \n"
				+ "        sr.ad_user_id AS userId,\n"
				+ "        sr.name AS userNames,\n"
				+ "        COALESCE(SUM(ou.quantity), 0) AS totalQuantity,\n"
				+ "        COALESCE(SUM(ou.discardqty), 0) AS discardQty,\n"
				+ "        COALESCE(SUM(ou.quantity), 0) - COALESCE(SUM(ou.discardqty), 0) AS actualQty\n"
				+ "    FROM \n"
				+ "        adempiere.tc_order o\n"
				+ "    JOIN \n"
				+ "        adempiere.ad_user sr ON o.createdby = sr.ad_user_id\n"
				+ "    JOIN \n"
				+ "        adempiere.tc_out ou ON ou.tc_order_id = o.tc_order_id\n"
				+ "    WHERE \n"
				+ "        o.ad_client_id = ? \n"
				+ "        AND ou.created::DATE BETWEEN ? AND ?\n"
				+ "    GROUP BY \n"
				+ "        sr.ad_user_id, sr.name\n"
				+ "),\n"
				+ "max_user AS (\n"
				+ "    SELECT \n"
				+ "        userId, userNames, totalQuantity\n"
				+ "    FROM \n"
				+ "        user_totals \n"
				+ "    ORDER BY \n"
				+ "        totalQuantity DESC \n"
				+ "    LIMIT 1\n"
				+ ")\n"
				+ "SELECT \n"
				+ "    ut.userNames AS UserName,\n"
				+ "    ut.userId AS UserId,\n"
				+ "    ut.totalQuantity AS TotalQuantity,\n"
				+ "    ut.discardQty AS DiscardQty,\n"
				+ "    ut.actualQty AS ActualQty\n"
				+ "FROM \n"
				+ "    user_totals ut\n"
				+ "CROSS JOIN \n"
				+ "    max_user mu\n"
				+ "GROUP BY \n"
				+ "    ut.userId, ut.userNames, ut.totalQuantity, ut.discardQty, ut.actualQty\n"
				+ "ORDER BY \n"
				+ "    ut.userId;";

        try {
        	pstmt = DB.prepareStatement(sql, null);
        	pstmt.setInt(1, clientId);
            pstmt.setDate(2, new java.sql.Date(startDate.getTime()));
            pstmt.setDate(3, new java.sql.Date(endDate.getTime()));

            rs = pstmt.executeQuery();
                visitData.clear();
                while (rs.next()) {
                	int UserId = rs.getInt("userId");
    				String UserName = rs.getString("userName");
    				int Qnty = rs.getInt("actualQty");
    				visitData.put(UserName, Qnty);
    				totalCount += Qnty;
    				
    				if(!UserIdAndName.containsKey(UserName))
    					UserIdAndName.put(UserName, UserId);
    			}
    			if (totalCount == 0) {
    				visitData.clear();
    				visitData.put("No Record Found", 0);
                }
                DB.close(rs, pstmt);
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
			DB.close(rs, pstmt);
		}
    }

    private void updateChart() {
    	chartData.getDatasets().clear();
        chartData.getLabels().clear();
        
        Dataset<Integer> dataset = new Dataset<>();
        
        if (visitData.isEmpty()) {
            visitData.put("No Record Found", 0);
        }
        
        ArrayList<Integer> values = new ArrayList<>(visitData.values());
        String[] labels = visitData.keySet().toArray(new String[0]);
        
        // Define colors
        String primaryColor = "#4CAF50";  // Green for a specific user
        String secondaryColor = "#298f60"; // Default for others

        String[] backgroundColors = new String[labels.length];
        for (int i = 0; i < labels.length; i++) {
            if ("User A".equalsIgnoreCase(labels[i])) {
                backgroundColors[i] = primaryColor; // Assign green to User A
            } else {
                backgroundColors[i] = secondaryColor; // Assign a different color for others
            }
        }

        dataset.setData(values);
        dataset.setBackgroundColors(backgroundColors);
        dataset.setLabel("Users");

        chartData.setLabels(labels);
        chartData.addDataset(dataset);
        
        chartjs.setData(chartData);
        chartjs.invalidate(); // Refresh UI    
        }
    
    @Override
	public void updateUI() {
    	loadChartData();
    	updateChart();
	}

    @Override
    public void onEvent(Event event) {
        if (event.getTarget() == refreshButton) {
        	updateUI();
        }
    }
    
    @Override
    public void refresh(ServerPushTemplate template) {
    	loadChartData();
        template.executeAsync(this);
    }
}

//package org.adempiere.webui.dashboard;
//
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.Properties;
//import java.util.Set;
//
//import org.adempiere.webui.component.Button;
//import org.adempiere.webui.component.Grid;
//import org.adempiere.webui.component.Label;
//import org.adempiere.webui.component.Row;
//import org.adempiere.webui.component.Rows;
//import org.adempiere.webui.util.ServerPushTemplate;
//import org.compiere.util.DB;
//import org.compiere.util.Env;
//import org.idempiere.zk.billboard.Billboard;
//import org.zkoss.zk.ui.event.Event;
//import org.zkoss.zk.ui.event.EventListener;
//import org.zkoss.zk.ui.event.Events;
//import org.zkoss.zul.CategoryModel;
//import org.zkoss.zul.ChartModel;
//import org.zkoss.zul.Datebox;
//import org.zkoss.zul.Div;
//import org.zkoss.zul.Hbox;
//import org.zkoss.zul.SimpleCategoryModel;
//import org.zkoss.zul.Vbox;
//
//public class DPWarehouseView extends DashboardPanel implements EventListener<Event>{
//
//	private static final long serialVersionUID = 1L;
//	private Billboard userDatas;
//	private LinkedHashMap<String, Integer> UserIdAndName = new LinkedHashMap<String, Integer>();
//	private LinkedHashMap<String, Integer> userData = null;
//	private static String User_Data = "userName";
//	private Datebox startDateBox;
//    private Datebox endDateBox;
//    private Button refreshButton;
//
//	public DPWarehouseView() {
//		super();
//		this.setSclass("activities-box");
//        Div dateRangeDiv = createDateRangePanel();
//        this.appendChild(dateRangeDiv);
//		initOptions();
//		buildChart(userData, User_Data);
//		this.appendChild(createActivitiesPanel());
//	}
//	
//	private Div createDateRangePanel() {
//		Div dateRangeDiv = new Div();
//        dateRangeDiv.setSclass("date-range-panel");
//
//        // Create a vertical layout for "From" date
//        Vbox fromBox = new Vbox();
//        Label fromLabel = new Label("From");
//        fromLabel.setStyle("font-weight: bold;"); 
//        startDateBox = new Datebox();
//        startDateBox.setValue(new Date(System.currentTimeMillis() - (60L * 24 * 60 * 60 * 1000))); // Default: last 60 days
//        startDateBox.setStyle("margin-bottom: 10px;"); // Add space below
//        fromBox.appendChild(fromLabel);
//        fromBox.appendChild(startDateBox);
//
//        // Create a vertical layout for "To" date
//        Vbox toBox = new Vbox();
//        Label toLabel = new Label("To");
//        toLabel.setStyle("font-weight: bold;"); 
//        endDateBox = new Datebox();
//        endDateBox.setValue(new Date()); // Default: today
//        toBox.appendChild(toLabel);
//        toBox.appendChild(endDateBox);
//
//        // Refresh button
//        refreshButton = new Button("Refresh");
//        refreshButton.addEventListener("onClick", this);
//        refreshButton.setStyle("margin-top: 23px; font-weight: bold;  background-color: green; color: white;");
//
//        // Use Hbox for horizontal layout with spacing
//        Hbox dateBoxContainer = new Hbox();
//        dateBoxContainer.setSpacing("10px"); // Space between date fields
//        dateBoxContainer.appendChild(fromBox);
//        dateBoxContainer.appendChild(toBox);
//        dateBoxContainer.appendChild(refreshButton);
//
//        // Add components to the main div
//        dateRangeDiv.appendChild(dateBoxContainer);
//        return dateRangeDiv;
//    }
//
//	private Grid createActivitiesPanel() {
//		Grid grid = new Grid();
//		grid.setSclass("pipra-charts-title-indicator");
//		appendChild(grid);
//		grid.makeNoStrip();
//
//		Rows rows = new Rows();
//		grid.appendChild(rows);
//		Row row = null;
//		List<Billboard> list = new ArrayList<>();
//		list.add(userDatas);
//
//		for (int i = 0; i < list.size(); i++) {
//			if (row == null || i % 2 == 0) {
//				row = new Row();
//				rows.appendChild(row);
//			}
//			Div div = new Div();
//			row.appendCellChild(div, 5);
//			div.appendChild(list.get(i));
//			div.setWidth("600px");
//            div.setHeight("250px");
//		}	
//		return grid;
//	}
//
//	private void initOptions() {
//		Properties ctx = Env.getCtx();
//		int clientId = Env.getAD_Client_ID(ctx);
//		PreparedStatement pstmt = null;
//		ResultSet rs = null;
//		try {
//			Date startDate = startDateBox.getValue();
//            Date endDate = endDateBox.getValue();
//			String sql = null;
//				
//				sql = "WITH user_totals AS (\n"
//						+ "    SELECT \n"
//						+ "        sr.ad_user_id AS userId,\n"
//						+ "        sr.name AS userNames,\n"
//						+ "        COALESCE(SUM(ou.quantity), 0) AS totalQuantity,\n"
//						+ "        COALESCE(SUM(ou.discardqty), 0) AS discardQty,\n"
//						+ "        COALESCE(SUM(ou.quantity), 0) - COALESCE(SUM(ou.discardqty), 0) AS actualQty\n"
//						+ "    FROM \n"
//						+ "        adempiere.tc_order o\n"
//						+ "    JOIN \n"
//						+ "        adempiere.ad_user sr ON o.createdby = sr.ad_user_id\n"
//						+ "    JOIN \n"
//						+ "        adempiere.tc_out ou ON ou.tc_order_id = o.tc_order_id\n"
//						+ "    WHERE \n"
//						+ "        o.ad_client_id = ? \n"
//						+ "        AND ou.created::DATE BETWEEN ? AND ?\n"
//						+ "    GROUP BY \n"
//						+ "        sr.ad_user_id, sr.name\n"
//						+ "),\n"
//						+ "max_user AS (\n"
//						+ "    SELECT \n"
//						+ "        userId, userNames, totalQuantity\n"
//						+ "    FROM \n"
//						+ "        user_totals \n"
//						+ "    ORDER BY \n"
//						+ "        totalQuantity DESC \n"
//						+ "    LIMIT 1\n"
//						+ ")\n"
//						+ "SELECT \n"
//						+ "    ut.userNames AS UserName,\n"
//						+ "    ut.userId AS UserId,\n"
//						+ "    ut.totalQuantity AS TotalQuantity,\n"
//						+ "    ut.discardQty AS DiscardQty,\n"
//						+ "    ut.actualQty AS ActualQty\n"
//						+ "FROM \n"
//						+ "    user_totals ut\n"
//						+ "CROSS JOIN \n"
//						+ "    max_user mu\n"
//						+ "GROUP BY \n"
//						+ "    ut.userId, ut.userNames, ut.totalQuantity, ut.discardQty, ut.actualQty\n"
//						+ "ORDER BY \n"
//						+ "    ut.userId;";
////
//			pstmt = DB.prepareStatement(sql.toString(), null);
//			pstmt.setInt(1, clientId);
//            pstmt.setDate(2, new java.sql.Date(startDate.getTime()));
//            pstmt.setDate(3, new java.sql.Date(endDate.getTime()));
//			rs = pstmt.executeQuery();
//			userData = new LinkedHashMap<String, Integer>();
//			int totalCount = 0;
//			while (rs.next()) {
//				int UserId = rs.getInt("userId");
//				String UserName = rs.getString("userName");
//				int Qnty = rs.getInt("actualQty");
//				userData.put(UserName, Qnty);
//				totalCount += Qnty;
//				if(!UserIdAndName.containsKey(UserName))
//					UserIdAndName.put(UserName, UserId);
//			}
//			if (totalCount == 0) {
//                userData.clear();
//                userData.put("No Record Found", 0);
//            }
//			DB.close(rs, pstmt);
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}finally {
//			DB.close(rs, pstmt);
//		}
//	}
//	private void refreshChartData() {
//        initOptions();
//        updateUI();
//    }
//
//	@Override
//	public void updateUI() {
//		userDatas.removeSclass("z-billboard");
//	}
//
//	@Override
//	public void refresh(ServerPushTemplate template) {
//		initOptions();
//		template.executeAsync(this);
//	}
//
//	private void buildChart(LinkedHashMap<String, Integer> data, String type) {
//
//		String barColor = "#298f60";            //"#2563EB";
//		Set<String> wNames = data.keySet();
//		Billboard billboard = new Billboard();
//		billboard.setLegend(false, false);
//		billboard.addLegendOptions("location", "bottom");
//		billboard.setValueAxisLabel("Qauntity");
//		billboard.setType("bar");
//		 billboard.setWidth("100%");
//	     billboard.setHeight("100%"); // Adjust height as needed
//	     billboard.setStyle("max-width:100%;");
//		
//		String[] rgbColors = new String[wNames.size()];
//		Arrays.fill(rgbColors, barColor);		
//		billboard.addRendererOptions("intervalColors", rgbColors);
//		billboard.setModel(initChartModel(data, type));
//		billboard.setOrient("vertical");
//		if (type.equalsIgnoreCase(User_Data))
//			userDatas = billboard;
//
//	}
//
//	private ChartModel initChartModel(LinkedHashMap<String, Integer> data, String type) {
//		Set<String> wNames = data.keySet();
//		ChartModel chartModel = new SimpleCategoryModel();
//		for (String key : wNames) {
//			((CategoryModel) chartModel).setValue(type, key, data.get(key));
//
//		}
//		return chartModel;
//	}
//	
//	@Override
//	public void onEvent(Event event) throws Exception {
//		if (event.getTarget() == refreshButton) {
//    		initOptions();  // Update dashboard data based on date range
//    		userDatas.setModel(initChartModel(userData, User_Data));
//    		userDatas.invalidate();
//    	}
//	}
//}
