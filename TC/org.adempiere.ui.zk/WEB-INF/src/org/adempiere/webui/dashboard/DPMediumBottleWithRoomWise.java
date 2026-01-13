package org.adempiere.webui.dashboard;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import org.adempiere.webui.component.Label;
import org.adempiere.webui.theme.ITheme;
import org.adempiere.webui.util.ServerPushTemplate;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Toolbarbutton;

public class DPMediumBottleWithRoomWise extends DashboardPanel implements EventListener<Event> {

    private static final long serialVersionUID = 1L;

    private Map<String, Map<String, Integer>> roomCategoryQuantities = new TreeMap<>(new RoomTypeComparator());
    private List<String> categories = Arrays.asList(
        "Initiation", "Callusing", "Multiplication", "Elongation", "Rooting"
    );
    private Datebox startDateBox;
    private Datebox endDateBox;
    private Toolbarbutton okButton;

    public DPMediumBottleWithRoomWise() {
        super();
      this.setSclass("activities-box");
      this.appendChild(createDatePanel());  // Add date panel
      initOptions();
      this.appendChild(createActivitiesPanel());
    }
    
    private Div createDatePanel() {
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

        okButton = new Toolbarbutton();
        okButton.setIconSclass("z-icon-refresh");
        okButton.setTooltiptext("Refresh");
        okButton.setStyle(
                "font-size:16px;" +
                "color:#2e7d32;" +
                "cursor:pointer;" +
                "margin-left:6px;"
            );
        okButton.addEventListener("onClick", this);

        box.appendChild(startDateBox);
        box.appendChild(endDateBox);
        box.appendChild(okButton);

        header.appendChild(box);
        return header;
    }

    private Div createActivitiesPanel() {
        Div mainDiv = new Div();
        mainDiv.setSclass(ITheme.WARE_HOUSE_DATA_WIDGET);

        // Create separate boxes for each room type
        for (Map.Entry<String, Map<String, Integer>> roomEntry : roomCategoryQuantities.entrySet()) {
            String roomType = roomEntry.getKey();
            Map<String, Integer> categoryQuantities = roomEntry.getValue();

            // Label for room type
            Div roomBox = new Div();
            roomBox.setSclass(ITheme.DASHBOARD_WIDGET_LABELS_BORDER_ORANGE);
            Label roomLabel = new Label();
            roomLabel.setSclass(ITheme.DASHBOARD_WIDGET_LABELS);
            roomLabel.setValue(roomType);
            roomLabel.setStyle("font-weight: bold; font-size: 25px;");
            roomBox.appendChild(roomLabel);

            // Create separate boxes for each category within the room type
            for (String category : categories) {
                Div categoryBox = new Div();
                categoryBox.setSclass(ITheme.DASHBOARD_WIDGET_Text_COUNT_CONT);

                // Label for category name
                Label categoryLabel = new Label();
                categoryLabel.setSclass(ITheme.DASHBOARD_WIDGET_LABELS);
                categoryLabel.setValue(category);
                categoryBox.appendChild(categoryLabel);

                // Label for category quantity
                Label countLabel = new Label();
                countLabel.setSclass(ITheme.DASHBOARD_WIDGET_COUNT);
                countLabel.setValue(" " + categoryQuantities.getOrDefault(category, 0));
                categoryBox.appendChild(countLabel);

                roomBox.appendChild(categoryBox);
            }
            mainDiv.appendChild(roomBox);
        }
        return mainDiv;
    }

    private void initOptions() {
        Properties ctx = Env.getCtx();
        int clientId = Env.getAD_Client_ID(ctx);
        PreparedStatement pstmt = null;
        ResultSet RS = null;
        try {
        	Calendar calendar = Calendar.getInstance();
        	calendar.add(Calendar.DAY_OF_YEAR, -60); // Subtract 30 days from the current date
        	
        	Date startDate = calendar.getTime();
        	if(startDateBox != null)
        		startDate = startDateBox.getValue();
        	
        	Calendar calendar2 = Calendar.getInstance();
        	Date today = calendar2.getTime();
        	Date endDate = today;
        	if (endDateBox != null && endDateBox.getValue() != null) {
        	    endDate = endDateBox.getValue();
        	}
        	
            String sql = "WITH categories AS (\n"
            		+ "    SELECT 'Initiation' AS Category\n"
            		+ "    UNION ALL SELECT 'Callusing'\n"
            		+ "    UNION ALL SELECT 'Multiplication'\n"
            		+ "    UNION ALL SELECT 'Elongation'\n"
            		+ "    UNION ALL SELECT 'Rooting'\n"
            		+ "),\n"
            		+ "category_counts AS (\n"
            		+ "    SELECT\n"
            		+ "        lt.name AS RoomType,\n"
            		+ "        CASE\n"
            		+ "            WHEN pr.description LIKE 'BI' THEN 'Initiation'\n"
            		+ "            WHEN pr.description LIKE 'BI' THEN 'Callusing'\n"
            		+ "            WHEN pr.description LIKE 'BM1' OR pr.name LIKE 'BM2' THEN 'Multiplication'\n"
            		+ "            WHEN pr.description LIKE 'BE' THEN 'Elongation'\n"
            		+ "            WHEN pr.description LIKE 'CR' THEN 'Rooting'\n"
            		+ "            ELSE 'Other'\n"
            		+ "        END AS Category,\n"
            		+ "        SUM(o.qtyonhand)::int AS TotalQuantity\n"
            		+ "    FROM \n"
            		+ "        adempiere.m_storageonhand o\n"
            		+ "    JOIN \n"
            		+ "        adempiere.m_product pr ON pr.m_product_id = o.m_product_id\n"
            		+ "    JOIN \n"
            		+ "        adempiere.m_locator l ON l.m_locator_id = o.m_locator_id\n"
            		+ "    JOIN \n"
            		+ "        adempiere.m_locatortype lt ON lt.m_locatortype_id = l.m_locatortype_id\n"
            		+ "    WHERE \n"
            		+ "        o.ad_client_id = ? AND o.created::Date BETWEEN ? AND ?\n"
            		+ "        AND lt.description LIKE 'Room'\n"
            		+ "    GROUP BY \n"
            		+ "        lt.name,\n"
            		+ "        CASE\n"
            		+ "            WHEN pr.description LIKE 'BI' THEN 'Initiation'\n"
            		+ "            WHEN pr.description LIKE 'BI' THEN 'Callusing'\n"
            		+ "            WHEN pr.description LIKE 'BM1' OR pr.name LIKE 'BM2' THEN 'Multiplication'\n"
            		+ "            WHEN pr.description LIKE 'BE' THEN 'Elongation'\n"
            		+ "            WHEN pr.description LIKE 'CR' THEN 'Rooting'\n"
            		+ "            ELSE 'Other'\n"
            		+ "        END\n"
            		+ "),\n"
            		+ "room_types AS (\n"
            		+ "    SELECT DISTINCT lt.name AS RoomType\n"
            		+ "    FROM adempiere.m_locatortype lt\n"
            		+ "    WHERE lt.description LIKE 'Room'\n"
            		+ ")\n"
            		+ "SELECT \n"
            		+ "    rt.RoomType, \n"
            		+ "    c.Category, \n"
            		+ "    COALESCE(cc.TotalQuantity, 0) AS TotalQuantity\n"
            		+ "FROM \n"
            		+ "    room_types rt\n"
            		+ "CROSS JOIN \n"
            		+ "    categories c\n"
            		+ "LEFT JOIN \n"
            		+ "    category_counts cc ON rt.RoomType = cc.RoomType AND c.Category = cc.Category\n"
            		+ "ORDER BY \n"
            		+ "    CASE \n"
            		+ "        WHEN rt.RoomType LIKE 'C%' THEN 1\n"
            		+ "        ELSE 2\n"
            		+ "    END,\n"
            		+ "    rt.RoomType,\n"
            		+ "    CASE c.Category\n"
            		+ "        WHEN 'Initiation' THEN 1\n"
            		+ "        WHEN 'Callusing' THEN 2\n"
            		+ "        WHEN 'Multiplication' THEN 3\n"
            		+ "        WHEN 'Elongation' THEN 4\n"
            		+ "        WHEN 'Rooting' THEN 5\n"
            		+ "        ELSE 6\n"
            		+ "    END;";
            pstmt = DB.prepareStatement(sql, null);
            pstmt.setInt(1, clientId);
            pstmt.setDate(2, new java.sql.Date(startDate.getTime()));
            pstmt.setDate(3, new java.sql.Date(endDate.getTime()));
            RS = pstmt.executeQuery();
            roomCategoryQuantities.clear();
            while (RS.next()) {
                String roomType = RS.getString("RoomType");
                String category = RS.getString("Category");
                int totalQuantity = RS.getInt("TotalQuantity");

                roomCategoryQuantities
                    .computeIfAbsent(roomType, k -> new HashMap<>())
                    .put(category, totalQuantity);
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
//    	this.removeChild(this.getLastChild()); // Remove previous activities panel
//        this.appendChild(createActivitiesPanel());
    }

    @Override
    public void onEvent(Event event) throws Exception {
    	if (event.getTarget() == okButton) {
    		initOptions();
        	this.removeChild(this.getLastChild()); // Remove previous activities panel
        	this.appendChild(createActivitiesPanel());
        }
    }
//    private void updateDashboardData() {
//        initOptions(); // Refresh data based on date range
//        updateUI();    // Update UI to reflect new data
//    }
}