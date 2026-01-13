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

public class DPFirstVisit extends DashboardPanel implements EventListener<Event> {
    
    private static final long serialVersionUID = 1L;
    private Chartjs chartjs;   // Pie chart component
    private Datebox startDateBox;
    private Datebox endDateBox;
//    private Button refreshButton;
    private Toolbarbutton refreshButton;
    private LinkedHashMap<String, Integer> visitData;  // Store visit data for pie chart
    ChartjsData chartData = new ChartjsData();
    
    public DPFirstVisit() {
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
    }

    private Div createChartPanel() {
        Div chartDiv = new Div();
        chartjs = new Chartjs(Chartjs.TYPE_PIE);
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
        String visitTypeName = "First Visit";
        int totalCount = 0;

        String sql = "SELECT 'Completed' AS Status, COUNT(CASE WHEN s.name = 'Completed' THEN 1 END) AS Count " +
                "FROM adempiere.tc_visit v " +
                "JOIN adempiere.tc_status s ON s.tc_status_id = v.tc_status_id " +
                "JOIN adempiere.tc_visittype vt ON vt.tc_visittype_id = v.tc_visittype_id " +
                "WHERE v.ad_client_id = " + clientId + " AND vt.name = '"+visitTypeName+"' AND v.updated::DATE BETWEEN ? AND ?  " +
                "UNION ALL " +
                "SELECT 'Cancelled' AS Status, COUNT(CASE WHEN s.name = 'Cancelled' THEN 1 END) AS Count " +
                "FROM adempiere.tc_visit v " +
                "JOIN adempiere.tc_status s ON s.tc_status_id = v.tc_status_id " +
                "JOIN adempiere.tc_visittype vt ON vt.tc_visittype_id = v.tc_visittype_id " +
                "WHERE v.ad_client_id = " + clientId + " AND vt.name = '"+visitTypeName+"' AND v.updated::DATE BETWEEN ? AND ?" +
                "UNION ALL " +
                "SELECT 'Upcoming' AS Status, COUNT(CASE WHEN s.name = 'In Progress' THEN 1 END) AS Count " +
                "FROM adempiere.tc_visit v " +
                "JOIN adempiere.tc_status s ON s.tc_status_id = v.tc_status_id " +
                "JOIN adempiere.tc_visittype vt ON vt.tc_visittype_id = v.tc_visittype_id " +
                "WHERE v.ad_client_id = " + clientId + " AND vt.name = '"+visitTypeName+"'AND v.created::DATE BETWEEN ? AND ?;";

        try (PreparedStatement pstmt = DB.prepareStatement(sql, null)) {
        	pstmt.setDate(1, new java.sql.Date(startDate.getTime()));
            pstmt.setDate(2, new java.sql.Date(endDate.getTime()));
            pstmt.setDate(3, new java.sql.Date(startDate.getTime()));
            pstmt.setDate(4, new java.sql.Date(endDate.getTime()));
            pstmt.setDate(5, new java.sql.Date(startDate.getTime()));
            pstmt.setDate(6, new java.sql.Date(endDate.getTime()));

            try (ResultSet rs = pstmt.executeQuery()) {
                visitData.clear();
                while (rs.next()) {
                	int count = rs.getInt("Count");
                    visitData.put(rs.getString("Status"), rs.getInt("Count"));
                    totalCount += count;
                }
                if (totalCount == 0) {
                	visitData.clear();
                	visitData.put("No Record Found", 0);
                }
                DB.close(rs, pstmt);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateChart() {
    	chartData.getDatasets().clear();
    	chartData.getLabels().clear();
        Dataset<Integer> dataset = new Dataset<>();
       
        if (visitData.isEmpty()) {
            visitData.put("No Record Found", 0);
        }
        dataset.setData(new ArrayList<>(visitData.values()));
        dataset.setBackgroundColors(new String[]{"#4CAF50", "#FF5733", "#FFC107"});
        dataset.setLabel("Visit Status");

        chartData.setLabels(visitData.keySet().toArray(new String[0]));
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
//import java.util.Arrays;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.LinkedHashMap;
//import java.util.Map;
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
//import org.zkoss.zul.Datebox;
//import org.zkoss.zul.Div;
//import org.zkoss.zul.PieModel;
//import org.zkoss.zul.SimplePieModel;
//
//public class DPFirstVisit extends DashboardPanel implements EventListener<Event> {
//
//    private static final long serialVersionUID = 1L;
//    private Billboard userDatas;
//    private LinkedHashMap<String, Integer> userData = null;
//    private static String User_Data = "userName";
//    private Datebox startDateBox;
//    private Datebox endDateBox;
//    private Button refreshButton;
//
//    public DPFirstVisit() {
//        super();
//        this.setSclass("activities-box");
//        this.appendChild(createDateRangePanel());
//        initOptions();
//        buildChart(userData, User_Data);
//        this.appendChild(createActivitiesPanel());
//    }
//    
//    private Div createDateRangePanel() {
//        Div dateRangeDiv = new Div();
//        dateRangeDiv.setSclass("date-range-panel");
//
//        Label startDateLabel = new Label("From Date:");
//        startDateBox = new Datebox();
//        Calendar calendar = Calendar.getInstance();
//        calendar.add(Calendar.DAY_OF_YEAR, -60);  // Set start date to current date minus 30 days
//        startDateBox.setValue(calendar.getTime());
//
//        Label endDateLabel = new Label("To Date:");
//        endDateBox = new Datebox();
//        endDateBox.setValue(new Date()); // Default to today
//        
//        refreshButton = new Button("Refresh");
//        refreshButton.addEventListener("onClick", this);
//        
//        dateRangeDiv.appendChild(startDateLabel);
//        dateRangeDiv.appendChild(startDateBox);
//        dateRangeDiv.appendChild(endDateLabel);
//        dateRangeDiv.appendChild(endDateBox);
//        dateRangeDiv.appendChild(refreshButton);
//        return dateRangeDiv;
//    }
//
//    private Grid createActivitiesPanel() {
//        Grid grid = new Grid();
////        grid.setSclass("pipra-charts-title-indicator");
//        appendChild(grid);
//        grid.makeNoStrip();
//        
//        Rows rows = new Rows();
//        grid.appendChild(rows);
//
//        Row row = new Row();
//        rows.appendChild(row);
//
//        Div div = new Div();
//        row.appendCellChild(div, 5);
//        div.appendChild(userDatas);
//        userDatas.removeSclass();
//        div.setWidth("400px");
//        div.setHeight("200px");
//        return grid;
//    }
//
//    private void initOptions() {
//        Properties ctx = Env.getCtx();
//        int clientId = Env.getAD_Client_ID(ctx);
//        String visitTypeName = "First Visit";
//
//        PreparedStatement pstmt = null;
//        ResultSet rs = null;
//        try {
//        	Date startDate = startDateBox.getValue();
//            Date endDate = endDateBox.getValue();
//            String sql = "SELECT 'Completed' AS Status, COUNT(CASE WHEN s.name = 'Completed' THEN 1 END) AS Count " +
//                         "FROM adempiere.tc_visit v " +
//                         "JOIN adempiere.tc_status s ON s.tc_status_id = v.tc_status_id " +
//                         "JOIN adempiere.tc_visittype vt ON vt.tc_visittype_id = v.tc_visittype_id " +
//                         "WHERE v.ad_client_id = " + clientId + " AND vt.name = '"+visitTypeName+"' AND v.updated::DATE BETWEEN ? AND ?  " +
//                         "UNION ALL " +
//                         "SELECT 'Cancelled' AS Status, COUNT(CASE WHEN s.name = 'Cancelled' THEN 1 END) AS Count " +
//                         "FROM adempiere.tc_visit v " +
//                         "JOIN adempiere.tc_status s ON s.tc_status_id = v.tc_status_id " +
//                         "JOIN adempiere.tc_visittype vt ON vt.tc_visittype_id = v.tc_visittype_id " +
//                         "WHERE v.ad_client_id = " + clientId + " AND vt.name = '"+visitTypeName+"' AND v.updated::DATE BETWEEN ? AND ?" +
//                         "UNION ALL " +
//                         "SELECT 'Upcoming' AS Status, COUNT(CASE WHEN s.name = 'In Progress' THEN 1 END) AS Count " +
//                         "FROM adempiere.tc_visit v " +
//                         "JOIN adempiere.tc_status s ON s.tc_status_id = v.tc_status_id " +
//                         "JOIN adempiere.tc_visittype vt ON vt.tc_visittype_id = v.tc_visittype_id " +
//                         "WHERE v.ad_client_id = " + clientId + " AND vt.name = '"+visitTypeName+"'AND v.created::DATE BETWEEN ? AND ?;";
//
//            pstmt = DB.prepareStatement(sql, null);
//            pstmt.setDate(1, new java.sql.Date(startDate.getTime()));
//            pstmt.setDate(2, new java.sql.Date(endDate.getTime()));
//            pstmt.setDate(3, new java.sql.Date(startDate.getTime()));
//            pstmt.setDate(4, new java.sql.Date(endDate.getTime()));
//            pstmt.setDate(5, new java.sql.Date(startDate.getTime()));
//            pstmt.setDate(6, new java.sql.Date(endDate.getTime()));
//            rs = pstmt.executeQuery();
//            userData = new LinkedHashMap<>();
//            int totalCount = 0;
//            while (rs.next()) {
//                String status = rs.getString("Status");
//                int count = rs.getInt("Count");
//                userData.put(status, count);
//                totalCount += count;
//            }
//            if (totalCount == 0) {
//                userData.clear();
//                userData.put("No Record Found", 0);
//            }
//            DB.close(rs, pstmt);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }finally {
//        	DB.close(rs, pstmt);
//		}
//    }
//    
//    private void refreshChartData() {
//        initOptions();
//        updateUI();
//    }
//
//    @Override
//    public void updateUI() {
////       
//    	
//    	initOptions();  // Update dashboard data based on date range
////    	userDatas.removeSclass("bb-tooltip-container");
////    	userDatas.removeSclass("bb-tooltip-container");
////    	this.removeSclass("bb-tooltip-container");
////    	this.removeSclass("bb-tooltip-container");
////    	userDatas.removeSclass();
//		userDatas.setModel(initChartModel(userData, User_Data));
//		userDatas.invalidate();
////		this.removeSclass();
////		userDatas.setClass("bb-tooltip-container");
////		userDatas.removeSclass("z-billboard");
////        userDatas.setStyle("max-width: 100%; max-height: 200px; overflow: hidden;"); // Prevent overflow
////		 userDatas.setStyle("width: 400px; height: 200px; overflow: hidden;");
////		this.setStyle("width: 400px !important;");
////		userDatas.setStyle("width: 400px !important; height: 200px !important; overflow: hidden !important;");
////		
////    	userDatas.removeSclass("bb-tooltip-container");
////    	userDatas.removeSclass("bb-tooltip-container");
////    	this.removeSclass("bb-tooltip-container");
////    	this.removeSclass("bb-tooltip-container");
//
//
//        
//    }
//
//    @Override
//    public void refresh(ServerPushTemplate template) {
//    	
////    	userDatas.removeSclass("bb-tooltip-container");
////    	userDatas.removeSclass("bb-tooltip-container");
////    	this.removeSclass("bb-tooltip-container");
////    	this.removeSclass("bb-tooltip-container");
////        initOptions();
////    	refreshChartData();
////        userDatas.setModel(initChartModel(userData, User_Data));
////		userDatas.invalidate();
////        Executions.schedule(getDesktop(), null, null);
//     // Ensure the desktop is still valid before scheduling the UI update
////        Desktop desktop = Executions.getCurrent().getDesktop();
////        if (desktop == null || !desktop.isAlive()) {
////            return;  // Stop if the session is invalid
////        }
////        Executions.schedule(desktop, new EventListener<Event>() {
////            public void onEvent(Event evt) throws Exception {
////                userDatas.setModel(initChartModel(userData, User_Data));
////                userDatas.invalidate();
////            }
////        }, null);
//        template.executeAsync(this);
//    }
//
//    @Override
//    public void onEvent(Event event) throws Exception {
//    	if (event.getTarget() == refreshButton) {
//    		initOptions();  
//    		userDatas.setModel(initChartModel(userData, User_Data));
//    		userDatas.invalidate();
//    	}
//    }
//
//    private void buildChart(LinkedHashMap<String, Integer> data, String type) {
//        String completedColor = "#4CAF50"; // Green color for completed
//        String cancelColor = "#F44336"; // Red color for cancel
//        String inProgressColor = "#FFC107"; // Orange color for in progress
//        String defaultColor = "#808080"; // Grey color for no records found
//
//        userDatas = new Billboard();
//        userDatas.setWidth("90%");
//
//        PieModel model = initChartModel(data, type);
//        userDatas.setModel(model);
//        Set<String> wNames = data.keySet();
//        Billboard billboard = new Billboard();
//        billboard.setLegend(true, false);
//        billboard.addLegendOptions("location", "bottom");
//        billboard.setTickAxisLabel("Name");
//        billboard.setValueAxisLabel("Quantity");
//        billboard.setType("pie");
//        billboard.setWidth("400px");
//        billboard.setHeight("200px"); // Adjust height as needed
//        billboard.setStyle("max-width:100%;");
//
//        String[] rgbColors = new String[wNames.size()];
//        Arrays.fill(rgbColors, defaultColor);
//        int i = 0;
//        for (String status : wNames) {
//            switch (status) {
//                case "Completed":
//                    rgbColors[i++] = completedColor;
//                    break;
//                case "Cancelled":
//                    rgbColors[i++] = cancelColor;
//                    break;
//                case "Upcoming":
//                    rgbColors[i++] = inProgressColor;
//                    break;
//                case "No Record Found":
//                    rgbColors[i++] = defaultColor;
//                    break;
//                default:
//                    rgbColors[i++] = "#000000"; // Default color
//                    break;
//            }
//        }
//        billboard.addRendererOptions("intervalColors", rgbColors);
//        billboard.setModel(initChartModel(data, type));
//        billboard.setOrient("vertical");
//        if (type.equalsIgnoreCase(User_Data))
//            userDatas = billboard;
//    }
//
//    private PieModel initChartModel(LinkedHashMap<String, Integer> data, String type) {
//        PieModel model = new SimplePieModel();
//        for (Map.Entry<String, Integer> entry : data.entrySet()) {
//            model.setValue(entry.getKey(), entry.getValue());
//        }
//        return model;
//    }
//}