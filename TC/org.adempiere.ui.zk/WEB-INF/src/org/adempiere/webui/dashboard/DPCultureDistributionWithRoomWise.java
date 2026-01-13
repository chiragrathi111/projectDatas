package org.adempiere.webui.dashboard;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import org.adempiere.webui.component.Label;
import org.adempiere.webui.util.ServerPushTemplate;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.*;

public class DPCultureDistributionWithRoomWise
        extends DashboardPanel
        implements EventListener<Event> {

    private static final long serialVersionUID = 1L;

    private Toolbarbutton refreshBtn;
    private Grid grid;

    private Map<String, Map<String, Integer>> data = new LinkedHashMap<>();

    private final List<String> stages = Arrays.asList(
            "Initiation", "Callusing", "Multiplication", "Elongation", "Rooting"
    );

    public DPCultureDistributionWithRoomWise() {
        super();
        setSclass("dashboard-card");
        setStyle("padding:10px;");

        appendChild(createTable());

        loadData();
        refreshTable();
    }

    private Grid createTable() {
        grid = new Grid();
        grid.setWidth("100%");
        grid.setSclass("nice-table");

        Columns cols = new Columns();

        cols.appendChild(createColumn("Room"));
        for (String s : stages) {
            cols.appendChild(createColumn(s));
        }

        grid.appendChild(cols);
        grid.appendChild(new Rows());

        return grid;
    }

    private Column createColumn(String title) {
        Column c = new Column();
        Label lbl = new Label(title);
        lbl.setStyle("font-weight:bold; text-align:center;");

        c.appendChild(lbl);
        c.setAlign("center");
        return c;
    }

    private void loadData() {
        data.clear();

        Properties ctx = Env.getCtx();
        int clientId = Env.getAD_Client_ID(ctx);

        String sql =
        		"WITH stages AS (\n"
        		+ "    SELECT 'Initiation'     AS stage, 1 AS seq\n"
        		+ "    UNION ALL SELECT 'Callusing',        2\n"
        		+ "    UNION ALL SELECT 'Multiplication',   3\n"
        		+ "    UNION ALL SELECT 'Elongation',        4\n"
        		+ "    UNION ALL SELECT 'Rooting',           5\n"
        		+ "),\n"
        		+ "\n"
        		+ "rooms AS (\n"
        		+ "    SELECT\n"
        		+ "        lt.name AS room\n"
        		+ "    FROM adempiere.m_locatortype lt\n"
        		+ "    WHERE lt.description LIKE 'Room'\n"
        		+ "),\n"
        		+ "\n"
        		+ "data AS (\n"
        		+ "    SELECT\n"
        		+ "        lt.name      AS room,\n"
        		+ "        cs.name      AS stage,\n"
        		+ "        COUNT(cl.tc_culturelabel_id) AS qty\n"
        		+ "    FROM adempiere.tc_culturelabel cl\n"
        		+ "    JOIN adempiere.tc_culturestage cs\n"
        		+ "        ON cs.tc_culturestage_id = cl.tc_culturestage_id\n"
        		+ "    JOIN adempiere.tc_out o\n"
        		+ "        ON o.tc_out_id = cl.tc_out_id\n"
        		+ "    JOIN adempiere.m_locator l\n"
        		+ "        ON l.m_locator_id = o.m_locator_id\n"
        		+ "    JOIN adempiere.m_locatortype lt\n"
        		+ "        ON lt.m_locatortype_id = l.m_locatortype_id\n"
        		+ "    WHERE cl.ad_client_id = ? \n"
        		+ "      AND cl.isdiscarded = 'N'\n"
        		+ "      AND cl.tosubculturecheck = 'N'\n"
        		+ "    GROUP BY\n"
        		+ "        lt.name,\n"
        		+ "        cs.name\n"
        		+ ")\n"
        		+ "\n"
        		+ "SELECT\n"
        		+ "    r.room,\n"
        		+ "    s.stage,\n"
        		+ "    COALESCE(d.qty, 0) AS qty\n"
        		+ "FROM rooms r\n"
        		+ "CROSS JOIN stages s\n"
        		+ "LEFT JOIN data d\n"
        		+ "    ON d.room  = r.room\n"
        		+ "   AND d.stage = s.stage\n"
        		+ "ORDER BY\n"
        		+ "    r.room,\n"
        		+ "    s.seq;\n"
        		+ "";

        try (PreparedStatement ps = DB.prepareStatement(sql, null)) {
            ps.setInt(1, clientId);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                data
                  .computeIfAbsent(rs.getString("room"), k -> new HashMap<>())
                  .put(rs.getString("stage"), rs.getInt("qty"));
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void refreshTable() {
        Rows rows = grid.getRows();
        rows.getChildren().clear();

        for (String room : data.keySet()) {
            Row row = new Row();
            row.setStyle("text-align:center;");

            row.appendChild(center(room));

            for (String s : stages) {
                row.appendChild(center(
                        String.valueOf(data.get(room).getOrDefault(s, 0))
                ));
            }
            rows.appendChild(row);
        }
    }

    private Label center(String val) {
        Label l = new Label(val);
        l.setStyle("text-align:center;");
        return l;
    }

    @Override
    public void onEvent(Event event) {
        if (event.getTarget() == refreshBtn) {
            loadData();
            refreshTable();
        }
    }

    @Override
    public void refresh(ServerPushTemplate template) {
        loadData();
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
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Properties;
//import java.util.TreeMap;
//
//import org.adempiere.webui.component.Label;
//import org.adempiere.webui.theme.ITheme;
//import org.adempiere.webui.util.ServerPushTemplate;
//import org.compiere.util.DB;
//import org.compiere.util.Env;
//import org.zkoss.zk.ui.event.Event;
//import org.zkoss.zk.ui.event.EventListener;
//import org.zkoss.zul.Button;
//import org.zkoss.zul.Datebox;
//import org.zkoss.zul.Div;
//import org.zkoss.zul.Hbox;
//import org.zkoss.zul.Toolbarbutton;
//import org.zkoss.zul.Vbox;
//
//public class DPCultureDistributionWithRoomWise extends DashboardPanel implements EventListener<Event> {
//
//    private static final long serialVersionUID = 1L;
//
//    private Map<String, Map<String, Integer>> roomCategoryQuantities = new TreeMap<>(new RoomTypeComparator());
//    private List<String> categories = Arrays.asList(
//        "Initiation", "Callusing", "Multiplication", "Elongation", "Rooting"
//    );
//    private Datebox startDateBox;
//    private Datebox endDateBox;
////    private Button okButton;
//    private Toolbarbutton okButton;
//
//    public DPCultureDistributionWithRoomWise() {
//        super();
//        this.setSclass("activities-box");
//        this.appendChild(createDatePanel());  // Add date panel
//        initOptions();
//        this.appendChild(createActivitiesPanel());
//    }
//    
//    private Div createDatePanel() {
//    	 Div dateRangeDiv = new Div();
//         dateRangeDiv.setSclass("date-range-panel");
//
//         Hbox dateBoxContainer = new Hbox();
//         dateBoxContainer.setSpacing("6px");
//         dateBoxContainer.setAlign("center");
//         dateBoxContainer.setStyle("padding:5px;");
//
//         // From
//         Vbox fromBox = new Vbox();
////         fromBox.appendChild(new Label("From"));
////         startDateBox = new Datebox();
////         startDateBox.setValue(new Date(System.currentTimeMillis() - (7L * 24 * 60 * 60 * 1000)));
////         fromBox.appendChild(startDateBox);
//         startDateBox = new Datebox();
//         startDateBox.setValue(new Date(System.currentTimeMillis() - (7L * 24 * 60 * 60 * 1000)));
//         startDateBox.setWidth("115px");
//         startDateBox.setStyle("font-size:12px;");
//         fromBox.appendChild(startDateBox);
//         
//         // To
//         Vbox toBox = new Vbox();
////         toBox.appendChild(new Label("To"));
////         endDateBox = new Datebox();
////         endDateBox.setValue(new Date());
////         toBox.appendChild(endDateBox);
//         endDateBox = new Datebox();
//         endDateBox.setValue(new Date());
//         endDateBox.setWidth("115px");
//         endDateBox.setStyle("font-size:12px;");
//         toBox.appendChild(endDateBox);
//         
//         okButton = new Toolbarbutton();
//         okButton.setIconSclass("z-icon-refresh");
////         okButton.setImage("/images/realmedslogo.png");   // iDempiere standard icon
//         okButton.setTooltiptext("Refresh");
//         okButton.setStyle(
//                 "font-size:16px;" +
//                 "color:#2e7d32;" +
//                 "cursor:pointer;" +
//                 "margin-left:6px;"
//             );
//         okButton.addEventListener("onClick", this);
//
//         dateBoxContainer.appendChild(fromBox);
//         dateBoxContainer.appendChild(toBox);
//         dateBoxContainer.appendChild(okButton);
//
//         dateRangeDiv.appendChild(dateBoxContainer);
//         return dateRangeDiv;
//    	
//    	
////    	Div dateRangeDiv = new Div();
////        dateRangeDiv.setSclass("date-range-panel");
////
////        // Create a vertical layout for "From" date
////        Vbox fromBox = new Vbox();
////        Label fromLabel = new Label("From");
////        fromLabel.setStyle("font-weight: bold;"); 
////        startDateBox = new Datebox();
////        startDateBox.setValue(new Date(System.currentTimeMillis() - (7L * 24 * 60 * 60 * 1000))); // Default: last 60 days
////        startDateBox.setStyle("margin-bottom: 10px;"); // Add space below
////        fromBox.appendChild(fromLabel);
////        fromBox.appendChild(startDateBox);
////
////        // Create a vertical layout for "To" date
////        Vbox toBox = new Vbox();
////        Label toLabel = new Label("To");
////        toLabel.setStyle("font-weight: bold;"); 
////        endDateBox = new Datebox();
////        endDateBox.setValue(new Date()); // Default: today
////        toBox.appendChild(toLabel);
////        toBox.appendChild(endDateBox);
////
////        // Refresh button
////        okButton = new Button("Refresh");
////        okButton.addEventListener("onClick", this);
////        okButton.setStyle("margin-top: 23px; font-weight: bold;  background-color: green; color: white;");
////
////        // Use Hbox for horizontal layout with spacing
////        Hbox dateBoxContainer = new Hbox();
////        dateBoxContainer.setSpacing("10px"); // Space between date fields
////        dateBoxContainer.appendChild(fromBox);
////        dateBoxContainer.appendChild(toBox);
////        dateBoxContainer.appendChild(okButton);
////
////        // Add components to the main div
////        dateRangeDiv.appendChild(dateBoxContainer);
////        return dateRangeDiv;
//    }
//
//    private Div createActivitiesPanel() {
//        Div mainDiv = new Div();
//        mainDiv.setSclass(ITheme.WARE_HOUSE_DATA_WIDGET);
//        for (Map.Entry<String, Map<String, Integer>> roomEntry : roomCategoryQuantities.entrySet()) {
//            String roomType = roomEntry.getKey();
//            Map<String, Integer> categoryQuantities = roomEntry.getValue();
//
//            // Label for room type
//            Div roomBox = new Div();
//            roomBox.setSclass(ITheme.DASHBOARD_WIDGET_LABELS_BORDER_PURPLE);
//            Label roomLabel = new Label();
//            roomLabel.setSclass(ITheme.DASHBOARD_WIDGET_LABELS);
//            roomLabel.setValue(roomType);
//            roomLabel.setStyle("font-weight: bold; font-size: 25px;");
//            roomBox.appendChild(roomLabel);
//
//            // Create separate boxes for each category within the room type
//            for (String category : categories) {
//                Div categoryBox = new Div();
//                categoryBox.setSclass(ITheme.DASHBOARD_WIDGET_Text_COUNT_CONT);
//
//                // Label for category name
//                Label categoryLabel = new Label();
//                categoryLabel.setSclass(ITheme.DASHBOARD_WIDGET_LABELS);
//                categoryLabel.setValue(category);
//                categoryBox.appendChild(categoryLabel);
//
//                // Label for category quantity
//                Label countLabel = new Label();
//                countLabel.setSclass(ITheme.DASHBOARD_WIDGET_COUNT);
//                countLabel.setValue(" " + categoryQuantities.getOrDefault(category, 0));
//                categoryBox.appendChild(countLabel);
//
//                roomBox.appendChild(categoryBox);
//            }
//            mainDiv.appendChild(roomBox);
//        }
//        return mainDiv;
//    }
//
//    private void initOptions() {
//        Properties ctx = Env.getCtx();
//        int clientId = Env.getAD_Client_ID(ctx);
//        PreparedStatement pstmt = null;
//        ResultSet RS = null;
//        try {
//        	Calendar calendar = Calendar.getInstance();
//        	calendar.add(Calendar.DAY_OF_YEAR, -60); // Subtract 30 days from the current date
//        	
//        	Date startDate = calendar.getTime();
//        	if(startDateBox != null)
//        		startDate = startDateBox.getValue();
//        	
//        	Calendar calendar2 = Calendar.getInstance();
//        	Date today = calendar2.getTime();
//        	Date endDate = today;
//        	if (endDateBox != null && endDateBox.getValue() != null) {
//        	    endDate = endDateBox.getValue();
//        	}
//            
//            String sql = "WITH categories AS (\n"
//                + "    SELECT 'Initiation' AS Category\n"
//                + "    UNION ALL SELECT 'Callusing'\n"
//                + "    UNION ALL SELECT 'Multiplication'\n"
//                + "    UNION ALL SELECT 'Elongation'\n"
//                + "    UNION ALL SELECT 'Rooting'\n"
//                + "),\n"
//                + "category_counts AS (\n"
//                + "    SELECT\n"
//                + "        lt.name AS RoomType,\n"
//                + "        CASE\n"
//                + "            WHEN pr.name LIKE 'BI%' OR pr.name LIKE 'N%' THEN 'Initiation'\n"
//                + "            WHEN pr.name LIKE 'BC%' THEN 'Callusing'\n"
//                + "            WHEN pr.name LIKE 'BM%' OR pr.name LIKE 'M%' THEN 'Multiplication'\n"
//                + "            WHEN pr.name LIKE 'BE%' OR pr.name LIKE 'E1%' THEN 'Elongation'\n"
//                + "            WHEN pr.name LIKE 'BR%' OR pr.name LIKE 'R%' THEN 'Rooting'\n"
//                + "            ELSE 'Other'\n"
//                + "        END AS Category,\n"
//                + "        SUM(o.qtyonhand)::int AS TotalQuantity\n"
//                + "    FROM \n"
//                + "        adempiere.m_storageonhand o\n"
//                + "    JOIN \n"
//                + "        adempiere.m_product pr ON pr.m_product_id = o.m_product_id\n"
//                + "    JOIN \n"
//                + "        adempiere.m_locator l ON l.m_locator_id = o.m_locator_id\n"
//                + "    JOIN \n"
//                + "        adempiere.m_locatortype lt ON lt.m_locatortype_id = l.m_locatortype_id\n"
//                + "    WHERE \n"
//                + "        o.ad_client_id = ? AND o.created::Date BETWEEN ? AND ?\n"
//                + "        AND lt.description LIKE 'Room'\n"
//                + "    GROUP BY \n"
//                + "        lt.name,\n"
//                + "        CASE\n"
//                + "            WHEN pr.name LIKE 'BI%' OR pr.name LIKE 'N%' THEN 'Initiation'\n"
//                + "            WHEN pr.name LIKE 'BC%' THEN 'Callusing'\n"
//                + "            WHEN pr.name LIKE 'BM%' OR pr.name LIKE 'M%' THEN 'Multiplication'\n"
//                + "            WHEN pr.name LIKE 'BE%' OR pr.name LIKE 'E1%' THEN 'Elongation'\n"
//                + "            WHEN pr.name LIKE 'BR%' OR pr.name LIKE 'R%' THEN 'Rooting'\n"
//                + "            ELSE 'Other'\n"
//                + "        END\n"
//                + "),\n"
//                + "room_types AS (\n"
//                + "    SELECT DISTINCT lt.name AS RoomType\n"
//                + "    FROM adempiere.m_locatortype lt\n"
//                + "    WHERE lt.description LIKE 'Room'\n"
//                + ")\n"
//                + "SELECT \n"
//                + "    rt.RoomType, \n"
//                + "    c.Category, \n"
//                + "    COALESCE(cc.TotalQuantity, 0) AS TotalQuantity\n"
//                + "FROM \n"
//                + "    room_types rt\n"
//                + "CROSS JOIN \n"
//                + "    categories c\n"
//                + "LEFT JOIN \n"
//                + "    category_counts cc ON rt.RoomType = cc.RoomType AND c.Category = cc.Category\n"
//                + "ORDER BY \n"
//                + "    CASE \n"
//                + "        WHEN rt.RoomType LIKE 'C%' THEN 1\n"
//                + "        ELSE 2\n"
//                + "    END,\n"
//                + "    rt.RoomType,\n"
//                + "    CASE c.Category\n"
//                + "        WHEN 'Initiation' THEN 1\n"
//                + "        WHEN 'Callusing' THEN 2\n"
//                + "        WHEN 'Multiplication' THEN 3\n"
//                + "        WHEN 'Elongation' THEN 4\n"
//                + "        WHEN 'Rooting' THEN 5\n"
//                + "        ELSE 6\n"
//                + "    END";
//            pstmt = DB.prepareStatement(sql, null);
//            pstmt.setInt(1, clientId);
//            pstmt.setDate(2, new java.sql.Date(startDate.getTime()));
//            pstmt.setDate(3, new java.sql.Date(endDate.getTime()));
//            RS = pstmt.executeQuery();
//            roomCategoryQuantities.clear();
//            while (RS.next()) {
//                String roomType = RS.getString("RoomType");
//                String category = RS.getString("Category");
//                int totalQuantity = RS.getInt("TotalQuantity");
//
//                roomCategoryQuantities
//                    .computeIfAbsent(roomType, k -> new HashMap<>())
//                    .put(category, totalQuantity);
//            }
//            DB.close(RS, pstmt);
//            RS = null;
//            pstmt = null;
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public void refresh(ServerPushTemplate template) {
//    	initOptions();
//        template.executeAsync(this);
//    }
//
//    @Override
//    public void updateUI() {
////    	this.removeChild(this.getLastChild()); // Remove previous activities panel
////        this.appendChild(createActivitiesPanel());
//    }
//
//    @Override
//    public void onEvent(Event event) throws Exception {
//    	if (event.getTarget() == okButton) {
//    		initOptions();
//        	this.removeChild(this.getLastChild()); // Remove previous activities panel
//        	this.appendChild(createActivitiesPanel());
//        }
//    }
//    private void updateDashboardData() {
//        initOptions(); // Refresh data based on date range
//        updateUI();    // Update UI to reflect new data
//    }
//}
//
class RoomTypeComparator implements java.util.Comparator<String> {
    @Override
    public int compare(String o1, String o2) {
        if (o1.startsWith("C") && o2.startsWith("C")) {
            // Compare C1, C2, C3, C4 numerically
            int num1 = Integer.parseInt(o1.substring(1));
            int num2 = Integer.parseInt(o2.substring(1));
            return Integer.compare(num1, num2);
        } else if (o1.startsWith("C")) {
            // C1, C2, C3, C4 should come before PH and SH
            return -1;
        } else if (o2.startsWith("C")) {
            return 1;
        } else {
            // Compare PH and SH
            return o1.compareTo(o2);
        }
    }
}