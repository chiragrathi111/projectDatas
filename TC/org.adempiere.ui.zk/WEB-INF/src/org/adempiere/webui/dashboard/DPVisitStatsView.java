//package org.adempiere.webui.dashboard;
////
////import java.sql.PreparedStatement;
////import java.sql.ResultSet;
////import java.sql.SQLException;
////import java.util.ArrayList;
////import java.util.LinkedHashMap;
////import java.util.List;
////import java.util.Properties;
////
////import org.compiere.util.DB;
////import org.compiere.util.Env;
////import org.idempiere.zk.billboard.Billboard;
////import org.zkoss.zul.Div;
////import org.zkoss.zul.Label;
////import org.zkoss.zul.PieModel;
////import org.zkoss.zul.SimplePieModel;
////
////public class DPVisitStatsView extends DashboardPanel {
////
////    private static final long serialVersionUID = 1L;
////    private List<Billboard> charts = new ArrayList<>();
////
////    public DPVisitStatsView() {
////        super();
////        this.setSclass("activities-box");
////
////        // Initialize and populate charts
////        initCharts();
////
////        // Create panels for each chart
////        createActivitiesPanels();
////
////        // Add charts to the view
////        for (Billboard chart : charts) {
////            this.appendChild(chart);
////        }
////    }
////
////    private void initCharts() {
////        Properties ctx = Env.getCtx();
////        int clientId = Env.getAD_Client_ID(ctx);
////
////        // Execute SQL queries for each chart
////        charts.add(buildChart(fetchDataForChart(clientId, 1000001), "First Visit Graph"));
////        charts.add(buildChart(fetchDataForChart(clientId, 1000002), "Intermediate Visit Graph"));
////        charts.add(buildChart(fetchDataForChart(clientId, 1000003), "Collection Visit Graph"));
////    }
////
////    private LinkedHashMap<String, Object[]> fetchDataForChart(int clientId, int visitTypeId) {
////        LinkedHashMap<String, Object[]> userData = new LinkedHashMap<>();
////        String sql = getSQLQuery(visitTypeId);
////
////        // Define color codes
////        String completedColor = "#4CAF50"; // Green color for completed
////        String cancelColor = "#F44336";    // Red color for cancelled
////        String inProgressColor = "#FFC107"; // Orange color for in progress
////
////        try (PreparedStatement pstmt = DB.prepareStatement(sql, null)) {
////            pstmt.setInt(1, clientId);
////            pstmt.setInt(2, visitTypeId); // This line binds the second parameter
////            pstmt.setInt(3, clientId);    // This line binds the third parameter
////            pstmt.setInt(4, visitTypeId); // This line binds the fourth parameter
////            pstmt.setInt(5, clientId);    // This line binds the fifth parameter
////            pstmt.setInt(6, visitTypeId);
////            try (ResultSet rs = pstmt.executeQuery()) {
////                while (rs.next()) {
////                    String status = rs.getString("Status");
////                    int count = rs.getInt("Count");
////                    switch (status) {
////                        case "Completed":
////                            userData.put(status, new Object[]{count, completedColor});
////                            break;
////                        case "Cancelled":
////                            userData.put(status, new Object[]{count, cancelColor});
////                            break;
////                        case "Upcoming":
////                            userData.put(status, new Object[]{count, inProgressColor});
////                            break;
////                        default:
////                            userData.put(status, new Object[]{count, "#000000"}); // Default color
////                            break;
////                    }
////                }
////            }
////        } catch (SQLException e) {
////            e.printStackTrace();
////        }
////
////        return userData;
////    }
////
////
////    private String getSQLQuery(int visitTypeId) {
////        return "SELECT 'Completed' AS Status, COUNT(CASE WHEN v.description = 'completed' THEN 1 END) AS Count "
////                + "FROM adempiere.tc_visit v "
////                + "JOIN adempiere.tc_visittype vt ON vt.tc_visittype_id = v.tc_visittype_id "
////                + "WHERE v.ad_client_id = ? and v.tc_visittype_id = ? "
////                + "UNION ALL "
////                + "SELECT 'Cancelled' AS Status, COUNT(CASE WHEN v.description = 'cancel' THEN 1 END) AS Count "
////                + "FROM adempiere.tc_visit v "
////                + "JOIN adempiere.tc_visittype vt ON vt.tc_visittype_id = v.tc_visittype_id "
////                + "WHERE v.ad_client_id = ? and v.tc_visittype_id = ? "
////                + "UNION ALL "
////                + "SELECT 'Upcoming' AS Status, COUNT(CASE WHEN v.description IS NULL THEN 1 END) AS Count "
////                + "FROM adempiere.tc_visit v "
////                + "JOIN adempiere.tc_visittype vt ON vt.tc_visittype_id = v.tc_visittype_id "
////                + "WHERE v.ad_client_id = ? and v.tc_visittype_id = ?";
////    }
////
////    private Billboard buildChart(LinkedHashMap<String, Object> data, String title) {
////        Billboard billboard = new Billboard();
////        billboard.setLegend(true, false);
////        billboard.addLegendOptions("location", "bottom");
////        billboard.setTickAxisLabel("Name");
////        billboard.setValueAxisLabel("Quantity");
////        billboard.setType("donut"); // Set chart type to pie
////
////        PieModel pieModel = new SimplePieModel();
////        for (String status : data.keySet()) {
////            pieModel.setValue(status, (Number) data.get(status));
////        }
////        billboard.setModel(pieModel);
////
////        return billboard;
////    }
////
////    private void createActivitiesPanels() {
////        for (int i = 0; i < charts.size(); i++) {
////            Div panel = createPanelForChart(charts.get(i), "Chart " + (i + 1));
////            this.appendChild(panel);
////        }
////    }
////
////    private Div createPanelForChart(Billboard chart, String title) {
////        Div panel = new Div();
////        panel.setSclass("pipra-charts-title-indicator");
////
////        Div titleDiv = new Div();
////        titleDiv.setSclass("pipra-charts-title-indicator");
////        Label titleLabel = new Label(title);
////        titleDiv.appendChild(titleLabel);
////
////        panel.appendChild(titleDiv);
////
////        // Append chart to the panel
////        Div chartContainer = new Div();
////        chartContainer.appendChild(chart);
////        panel.appendChild(chartContainer);
////
////        return panel;
////    }
////}
////
////
////
//
//
////import java.sql.PreparedStatement;
////import java.sql.ResultSet;
////import java.sql.SQLException;
////import java.util.ArrayList;
////import java.util.LinkedHashMap;
////import java.util.List;
////import java.util.Properties;
////
////import org.compiere.util.DB;
////import org.compiere.util.Env;
////import org.idempiere.zk.billboard.Billboard;
////import org.zkoss.zul.Div;
////import org.zkoss.zul.Label;
////import org.zkoss.zul.PieModel;
////import org.zkoss.zul.SimplePieModel;
////
////public class DPVisitStatsView extends DashboardPanel {
////
////    private static final long serialVersionUID = 1L;
////    private List<Billboard> charts = new ArrayList<>();
////
////    public DPVisitStatsView() {
////        super();
////        this.setSclass("activities-box");
////
////        // Initialize and populate charts
////        initCharts();
////
////        // Create panels for each chart
////        createActivitiesPanels();
////
////        // Add charts to the view
////        for (Billboard chart : charts) {
////            this.appendChild(chart);
////        }
////    }
////
////    private void initCharts() {
////        Properties ctx = Env.getCtx();
////        int clientId = Env.getAD_Client_ID(ctx);
////
////        // Execute SQL queries for each chart
////        charts.add(buildChart(fetchDataForChart(clientId, 1000001), "First Visit"));
////        charts.add(buildChart(fetchDataForChart(clientId, 1000002), "Intermediate"));
////        charts.add(buildChart(fetchDataForChart(clientId, 1000003), "Collection Visit"));
////    }
////
////    private LinkedHashMap<String, Integer> fetchDataForChart(int clientId, int visitTypeId) {
////        LinkedHashMap<String, Integer> userData = new LinkedHashMap<>();
////        String sql = getSQLQuery(visitTypeId);
////
////        try (PreparedStatement pstmt = DB.prepareStatement(sql, null)) {
////            pstmt.setInt(1, clientId);
////            pstmt.setInt(2, visitTypeId);
////            pstmt.setInt(3, clientId);
////            pstmt.setInt(4, visitTypeId);
////            pstmt.setInt(5, clientId);
////            pstmt.setInt(6, visitTypeId);
////            try (ResultSet rs = pstmt.executeQuery()) {
////                while (rs.next()) {
////                    String status = rs.getString("Status");
////                    int count = rs.getInt("Count");
////                    userData.put(status, count);
////                }
////            }
////        } catch (SQLException e) {
////            e.printStackTrace();
////        }
////
////        return userData;
////    }
////
////
////    private String getSQLQuery(int visitTypeId) {
////        return "SELECT 'Completed' AS Status, COUNT(CASE WHEN v.description = 'completed' THEN 1 END) AS Count "
////                + "FROM adempiere.tc_visit v "
////                + "JOIN adempiere.tc_visittype vt ON vt.tc_visittype_id = v.tc_visittype_id "
////                + "WHERE v.ad_client_id = ? and v.tc_visittype_id = ? "
////                + "UNION ALL "
////                + "SELECT 'Cancelled' AS Status, COUNT(CASE WHEN v.description = 'cancel' THEN 1 END) AS Count "
////                + "FROM adempiere.tc_visit v "
////                + "JOIN adempiere.tc_visittype vt ON vt.tc_visittype_id = v.tc_visittype_id "
////                + "WHERE v.ad_client_id = ? and v.tc_visittype_id = ? "
////                + "UNION ALL "
////                + "SELECT 'Upcoming' AS Status, COUNT(CASE WHEN v.description IS NULL THEN 1 END) AS Count "
////                + "FROM adempiere.tc_visit v "
////                + "JOIN adempiere.tc_visittype vt ON vt.tc_visittype_id = v.tc_visittype_id "
////                + "WHERE v.ad_client_id = ? and v.tc_visittype_id = ?";
////    }
////
////    private Billboard buildChart(LinkedHashMap<String, Integer> data, String title) {
////    	String[] colors = {"#4CAF50", "#F44336", "#FFC107"}; // Green, Red, Orange for Completed, Cancelled, Upcoming
////
////        // Create pie chart
////        Billboard billboard = new Billboard();
////        billboard.setTitle(title);
////        PieModel pieModel = new SimplePieModel();
////
////        int index = 0;
////        for (String status : data.keySet()) {
////            pieModel.setValue(status, data.get(status));
////            if (index < colors.length) {
//////                billboard.setSeriesColor(status, colors[index]);
////                billboard.setSeriesColors(colors);
////            }
////            index++;
////        }
////
////        billboard.setModel(pieModel);
////        return billboard;
////    }
////
////    private void createActivitiesPanels() {
////        Div mainContainer = new Div();
////        mainContainer.setStyle("display: flex; justify-content: space-between;");
////
////        String[] chartTitles = {"First Visit", "Intermediate Visit", "Collection Visit"};
////
////        // Create panels for each chart
////        for (int i = 0; i < charts.size(); i++) {
////            Div panel = createPanelForChart(charts.get(i), chartTitles[i], i);
////            mainContainer.appendChild(panel);
////        }
////
////        this.appendChild(mainContainer);
////    }
////
////    private Div createPanelForChart(Billboard chart, String title, int index) {
////        Div panel = new Div();
////        panel.setStyle("flex: 0 0 30%;");
////        panel.setSclass("pipra-charts-title-indicator");
////
////        // Title Label
////        Div titleDiv = new Div();
////        titleDiv.setSclass("pipra-charts-title-indicator");
////        Label titleLabel = new Label(title);
////        titleDiv.appendChild(titleLabel);
////
////        panel.appendChild(titleDiv);
////
////        // Chart Container
////        Div chartContainer = new Div();
////        chartContainer.appendChild(chart);
////        panel.appendChild(chartContainer);
////
////        // Set alignment for the first, second, and third charts respectively
////        if (index == 0) {
////            panel.setStyle("text-align: left;");
////        } else if (index == 1) {
////            panel.setStyle("text-align: center;");
////        } else if (index == 2) {
////            panel.setStyle("text-align: right;");
////        }
////        return panel;
////    }
////}
//
//
//
//
//
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.Properties;
//import java.util.Set;
//import org.compiere.util.DB;
//import org.compiere.util.Env;
//import org.idempiere.zk.billboard.Billboard;
//import org.zkoss.zul.Div;
//import org.zkoss.zul.Label;
//import org.zkoss.zul.PieModel;
//import org.zkoss.zul.SimplePieModel;
//
//public class DPVisitStatsView extends DashboardPanel {
//
//    private static final long serialVersionUID = 1L;
//    private List<Billboard> charts = new ArrayList<>();
//
//    public DPVisitStatsView() {
//        super();
//        this.setSclass("activities-box");
//
//        // Initialize and populate charts
//        initCharts();
//
//        // Create panels for each chart
//        createActivitiesPanels();
//
//        // Add charts to the view
//        for (Billboard chart : charts) {
//            this.appendChild(chart);
//        }
//    }
//
//    private void initCharts() {
//        Properties ctx = Env.getCtx();
//        int clientId = Env.getAD_Client_ID(ctx);
//
//        // Execute SQL queries for each chart
//        charts.add(buildChart(fetchDataForChart(clientId, 1000001), "First Visit"));
//        charts.add(buildChart(fetchDataForChart(clientId, 1000002), "Intermediate"));
//        charts.add(buildChart(fetchDataForChart(clientId, 1000003), "Collection Visit"));
//    }
//
//    private LinkedHashMap<String, Integer> fetchDataForChart(int clientId, int visitTypeId) {
//        LinkedHashMap<String, Integer> userData = new LinkedHashMap<>();
//        String sql = getSQLQuery(visitTypeId);
//
//        try (PreparedStatement pstmt = DB.prepareStatement(sql, null)) {
//        	pstmt.setInt(1, clientId);
//            pstmt.setInt(2, visitTypeId); // This line binds the second parameter
//            pstmt.setInt(3, clientId);    // This line binds the third parameter
//            pstmt.setInt(4, visitTypeId); // This line binds the fourth parameter
//            pstmt.setInt(5, clientId);    // This line binds the fifth parameter
//            pstmt.setInt(6, visitTypeId);
//            try (ResultSet rs = pstmt.executeQuery()) {
//                while (rs.next()) {
//                    String status = rs.getString("Status");
//                    int count = rs.getInt("Count");
//                    userData.put(status, count);
//                }
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        return userData;
//    }
//    
//
//    private String getSQLQuery(int visitTypeId) {
//        return "SELECT 'Completed' AS Status, COUNT(CASE WHEN v.description = 'completed' THEN 1 END) AS Count "
//                + "FROM adempiere.tc_visit v "
//                + "JOIN adempiere.tc_visittype vt ON vt.tc_visittype_id = v.tc_visittype_id "
//                + "WHERE v.ad_client_id = ? and v.tc_visittype_id = ? "
//                + "UNION ALL "
//                + "SELECT 'Cancelled' AS Status, COUNT(CASE WHEN v.description = 'cancel' THEN 1 END) AS Count "
//                + "FROM adempiere.tc_visit v "
//                + "JOIN adempiere.tc_visittype vt ON vt.tc_visittype_id = v.tc_visittype_id "
//                + "WHERE v.ad_client_id = ? and v.tc_visittype_id = ? "
//                + "UNION ALL "
//                + "SELECT 'Upcoming' AS Status, COUNT(CASE WHEN v.description IS NULL THEN 1 END) AS Count "
//                + "FROM adempiere.tc_visit v "
//                + "JOIN adempiere.tc_visittype vt ON vt.tc_visittype_id = v.tc_visittype_id "
//                + "WHERE v.ad_client_id = ? and v.tc_visittype_id = ?";
//    }
//
//    private Billboard buildChart(LinkedHashMap<String, Integer> data, String title) {
////    	String[] colors = {"#4CAF50", "#F44336", "#FFC107",};
//    	String color = "#4CAF50";
////    	Set<String> wNames = data.keySet();
//        Billboard billboard = new Billboard();
//        billboard.setLegend(true, false);
//        billboard.addLegendOptions("location", "bottom");
//        billboard.setTickAxisLabel("Name");
//        billboard.setValueAxisLabel("Quantity");
//        billboard.setType("pie");
//        billboard.setTitle(title);
//       
//
//        PieModel pieModel = new SimplePieModel();
//        int colorIndex = 0;   
//        for (String status : data.keySet()) {
//            pieModel.setValue(status, data.get(status));
//            colorIndex++;
//        }
//
////        String colorOptions = "";
////        for (int i = 0; i < colors.length; i++) {
////            colorOptions += "'" + colors[i] + "'";
////            if (i < colors.length - 1) {
////                colorOptions += ",";
////            }
////        }
//        billboard.addRendererOptions("color", "[" + color + "]");
//        billboard.setAction("vertical");
//        billboard.setModel(pieModel);
////        billboard.setOrient(color);
//        return billboard;
//    }
//
//    private void createActivitiesPanels() {
//        Div mainContainer = new Div();
//        mainContainer.setStyle("display: flex; justify-content: space-between;");
//
////        String[] chartTitles = {"First Visit", "Intermediate Visit", "Collection Visit"};
////        
////        // Create panels for each chart
////        for (int i = 0; i < charts.size(); i++) {
////            Div panel = createPanelForChart(charts.get(i), chartTitles[i], i);
////            mainContainer.appendChild(panel);
////        }
//
//        this.appendChild(mainContainer);
//    }
//
//    private Div createPanelForChart(Billboard chart, String title, int index) {
//        Div panel = new Div();
//        panel.setStyle("flex: 0 0 30%;");
//        panel.setSclass("pipra-charts-title-indicator");
//        // Title Label
//        Div titleDiv = new Div();
//        titleDiv.setSclass("pipra-charts-title-indicator");
//        Label titleLabel = new Label(title);
//        titleDiv.appendChild(titleLabel);
//
//        panel.appendChild(titleDiv);
//
//        // Chart Container
//        Div chartContainer = new Div();
//        chartContainer.appendChild(chart);
//        panel.appendChild(chartContainer);
//
////        // Set alignment for the first, second, and third charts respectively
////        if (index == 0) {
////            panel.setStyle("text-align: left;");
////        } else if (index == 1) {
////            panel.setStyle("text-align: center;");
////        } else if (index == 2) {
////            panel.setStyle("text-align: right;");
////        }
//        return panel;
//    }
//}
//
//
//
////import java.sql.PreparedStatement;
////import java.sql.ResultSet;
////import java.sql.SQLException;
////import java.util.ArrayList;
////import java.util.LinkedHashMap;
////import java.util.List;
////import java.util.Properties;
////
////import org.compiere.util.DB;
////import org.compiere.util.Env;
////import org.idempiere.zk.billboard.Billboard;
////import org.zkoss.zul.Div;
////import org.zkoss.zul.Label;
////import org.zkoss.zul.PieModel;
////import org.zkoss.zul.SimplePieModel;
////
////public class DPVisitStatsView extends DashboardPanel {
////
////    private static final long serialVersionUID = 1L;
////    private List<Billboard> charts = new ArrayList<>();
////
////    public DPVisitStatsView() {
////        super();
////        this.setSclass("activities-box");
////
////        // Initialize and populate charts
////        initCharts();
////
////        // Create panels for each chart
////        createActivitiesPanels();
////
////        // Add charts to the view
////        for (Billboard chart : charts) {
////            this.appendChild(chart);
////        }
////    }
////
////    private void initCharts() {
////        Properties ctx = Env.getCtx();
////        int clientId = Env.getAD_Client_ID(ctx);
////
////        // Execute SQL queries for each chart
////        charts.add(buildChart(fetchDataForChart(clientId, 1000001), "First Visit Graph"));
////        charts.add(buildChart(fetchDataForChart(clientId, 1000002), "Intermediate Visit Graph"));
////        charts.add(buildChart(fetchDataForChart(clientId, 1000003), "Collection Visit Graph"));
////    }
////
////    private LinkedHashMap<String, Integer> fetchDataForChart(int clientId, int visitTypeId) {
////        LinkedHashMap<String, Integer> userData = new LinkedHashMap<>();
////        String sql = getSQLQuery(visitTypeId);
////
////        try (PreparedStatement pstmt = DB.prepareStatement(sql, null)) {
////        	pstmt.setInt(1, clientId);
////            pstmt.setInt(2, visitTypeId); // This line binds the second parameter
////            pstmt.setInt(3, clientId);    // This line binds the third parameter
////            pstmt.setInt(4, visitTypeId); // This line binds the fourth parameter
////            pstmt.setInt(5, clientId);    // This line binds the fifth parameter
////            pstmt.setInt(6, visitTypeId);
////            try (ResultSet rs = pstmt.executeQuery()) {
////                while (rs.next()) {
////                    String status = rs.getString("Status");
////                    int count = rs.getInt("Count");
////                    userData.put(status, count);
////                }
////            }
////        } catch (SQLException e) {
////            e.printStackTrace();
////        }
////
////        return userData;
////    }
////    
////
////    private String getSQLQuery(int visitTypeId) {
////        return "SELECT 'Completed' AS Status, COUNT(CASE WHEN v.description = 'completed' THEN 1 END) AS Count "
////                + "FROM adempiere.tc_visit v "
////                + "JOIN adempiere.tc_visittype vt ON vt.tc_visittype_id = v.tc_visittype_id "
////                + "WHERE v.ad_client_id = ? and v.tc_visittype_id = ? "
////                + "UNION ALL "
////                + "SELECT 'Cancelled' AS Status, COUNT(CASE WHEN v.description = 'cancel' THEN 1 END) AS Count "
////                + "FROM adempiere.tc_visit v "
////                + "JOIN adempiere.tc_visittype vt ON vt.tc_visittype_id = v.tc_visittype_id "
////                + "WHERE v.ad_client_id = ? and v.tc_visittype_id = ? "
////                + "UNION ALL "
////                + "SELECT 'Upcoming' AS Status, COUNT(CASE WHEN v.description IS NULL THEN 1 END) AS Count "
////                + "FROM adempiere.tc_visit v "
////                + "JOIN adempiere.tc_visittype vt ON vt.tc_visittype_id = v.tc_visittype_id "
////                + "WHERE v.ad_client_id = ? and v.tc_visittype_id = ?";
////    }
////
////    private Billboard buildChart(LinkedHashMap<String, Integer> data, String title) {
////        Billboard billboard = new Billboard();
////        billboard.setLegend(true, false);
////        billboard.addLegendOptions("location", "bottom");
////        billboard.setTickAxisLabel("Name");
////        billboard.setValueAxisLabel("Quantity");
////        billboard.setType("donut");
////
////        PieModel pieModel = new SimplePieModel();
////        for (String status : data.keySet()) {
////            pieModel.setValue(status, data.get(status));
////        }
////        billboard.setModel(pieModel);
////     // Set colors for each series
//////        billboard.setSeriesColor("Completed", "#0000FF");
//////        billboard.setSeriesColor("Cancelled", "#FFA500");
//////        billboard.setSeriesColor("Upcoming", "#008000");
////        return billboard;
////    }
////
////    private void createActivitiesPanels() {
////        for (int i = 0; i < charts.size(); i++) {
////            Div panel = createPanelForChart(charts.get(i), "Chart " + (i + 1));
////            this.appendChild(panel);
////        }
////    }
////
////    private Div createPanelForChart(Billboard chart, String title) {
////        Div panel = new Div();
////        panel.setSclass("pipra-charts-title-indicator");
////
////        Div titleDiv = new Div();
////        titleDiv.setSclass("pipra-charts-title-indicator");
////        Label titleLabel = new Label(title);
////        titleDiv.appendChild(titleLabel);
////
////        panel.appendChild(titleDiv);
////
////        // Append chart to the panel
////        Div chartContainer = new Div();
////        chartContainer.appendChild(chart);
////        panel.appendChild(chartContainer);
////
////        return panel;
////    }
////}
//
//
//
////
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
import org.zkoss.zul.PieModel;
import org.zkoss.zul.SimplePieModel;

public class DPVisitStatsView extends DashboardPanel {

    private static final long serialVersionUID = 1L;
    private Billboard userDatas;

    private LinkedHashMap<String, Integer> userData = null;
    private static String User_Data = "userName";

    public DPVisitStatsView() {
        super();
        this.setSclass("activities-box");
        initOptions();

        buildChart(userData, User_Data);

        ZoomListener listener = new ZoomListener(null, userDatas.getModel());
        userDatas.addEventListener("onDataClick", listener);
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
        list.add(userDatas);

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

//            Button btnNotice = new Button();
//            if (i == 0) {
//                btnNotice.setLabel("ALL");
//                btnNotice.setTooltiptext("ALl Warehouse");
//                btnNotice.setImage(ThemeManager.getThemeResource("images/StepBack24.png"));
//                btnNotice.setName("All");
//                btnNotice.setStyle("float: right;");
//
//                ZoomListener listener = new ZoomListener(null, userDatas.getModel());
//                btnNotice.addEventListener(Events.ON_CLICK, listener);
//            }
//            div.appendChild(btnNotice);
        }
        return grid;
    }

    private void initOptions() {

        Properties ctx = Env.getCtx();
        int clientId = Env.getAD_Client_ID(ctx);

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            String sql = null;
            sql = "SELECT 'Completed' AS Status,\n"
                    + "    COUNT(CASE WHEN v.description = 'completed' THEN 1 END) AS Count\n"
                    + "FROM adempiere.tc_visit v\n"
                    + "JOIN adempiere.tc_visittype vt ON vt.tc_visittype_id = v.tc_visittype_id\n"
                    + "WHERE v.ad_client_id = " + clientId + " \n"
                    + "UNION ALL\n"
                    + "SELECT 'Cancelled' AS Status,\n"
                    + "    COUNT(CASE WHEN v.description = 'cancel' THEN 1 END) AS Count\n"
                    + "FROM adempiere.tc_visit v\n"
                    + "JOIN adempiere.tc_visittype vt ON vt.tc_visittype_id = v.tc_visittype_id\n"
                    + "WHERE v.ad_client_id = " + clientId + " \n"
                    + "UNION ALL\n"
                    + "SELECT 'Upcoming' AS Status,\n"
                    + "    COUNT(CASE WHEN v.description IS NULL THEN 1 END) AS Count\n"
                    + "FROM adempiere.tc_visit v\n"
                    + "JOIN adempiere.tc_visittype vt ON vt.tc_visittype_id = v.tc_visittype_id\n"
                    + "WHERE v.ad_client_id = " + clientId + ";\n"
                    + "";

            pstmt = DB.prepareStatement(sql.toString(), null);
            rs = pstmt.executeQuery();
            userData = new LinkedHashMap<String, Integer>();
            while (rs.next()) {
                String Status = rs.getString("Status");
                int Count = rs.getInt("Count");
                userData.put(Status, Count);
            }
            DB.close(rs, pstmt);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateUI() {
        userDatas.setModel(initChartModel(userData, User_Data));
        userDatas.invalidate();
    }

    @Override
    public void refresh(ServerPushTemplate template) {
        initOptions();
        template.executeAsync(this);
    }

    private void buildChart(LinkedHashMap<String, Integer> data, String type) {

    	String completedColor = "#4CAF50"; // Green color for completed
        String cancelColor = "#F44336"; // Red color for cancel
        String inProgressColor = "#FFC107"; // Orange color for in progress
        String barColor = "";

        Set<String> wNames = data.keySet();
        Billboard billboard = new Billboard();
        billboard.setLegend(true, false);
        billboard.addLegendOptions("location", "bottom");
        billboard.setTickAxisLabel("Name");
        billboard.setValueAxisLabel("Quantity");
        billboard.setType("donut"); // Set chart type to pie
        billboard.setWidth("20%");
        billboard.setHeight("200px");


        String[] rgbColors = new String[wNames.size()];
        Arrays.fill(rgbColors, barColor);
        int i = 0;
        for (String status : wNames) {
            switch (status) {
                case "Completed":
                    rgbColors[i++] = completedColor;
                    break;
                case "Cancelled":
                    rgbColors[i++] = cancelColor;
                    break;
                case "Upcoming":
                    rgbColors[i++] = inProgressColor;
                    break;
                default:
                    rgbColors[i++] = "#000000"; // Default color
                    break;
            }
        }
        
        billboard.addRendererOptions("intervalColors", rgbColors);

        billboard.setModel(initChartModel(data, type));
        billboard.setOrient("vertical");
        if (type.equalsIgnoreCase(User_Data))
            userDatas = billboard;
    }

    private ChartModel initChartModel(LinkedHashMap<String, Integer> data, String type) {
        PieModel pieModel = new SimplePieModel();
        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            pieModel.setValue(entry.getKey(), entry.getValue());
        }
        return pieModel;
    }

    private class ZoomListener implements EventListener<Event> {
        private org.zkoss.zul.ChartModel model;

        private ZoomListener(Map<String, MQuery> queries, org.zkoss.zul.ChartModel model) {
            this.model = model;
        }

        @Override
        public void onEvent(Event event) throws Exception {

            Component comp = event.getTarget();
            String eventName = event.getName();
            int userId = 0;
            if (eventName.equals(Events.ON_CLICK)) {
                if (comp instanceof Button)
                    userId = 0;

            } else {

                JSONObject json = (JSONObject) event.getData();
                Number pointIndex = (Number) json.get("pointIndex");
                if (pointIndex == null)
                    pointIndex = Integer.valueOf(0);

                if (model instanceof CategoryModel) {
                    // Add logic here if needed
                }
            }
            Env.setContext(Env.getCtx(), Env.AD_USER_ID, userId);
            DashboardRunnable dashboardRunnable = DPWarehouseSelection.refreshDashboardPanels(getDesktop());
            dashboardRunnable.refreshDashboard(false);
        }
    }
}
