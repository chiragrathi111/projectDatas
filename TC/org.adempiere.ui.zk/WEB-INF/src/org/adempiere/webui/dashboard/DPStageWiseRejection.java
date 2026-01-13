package org.adempiere.webui.dashboard;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
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

public class DPStageWiseRejection extends DashboardPanel implements EventListener<Event> {

    private static final long serialVersionUID = 1L;

    private List<String> categories = new ArrayList<>();
    private Map<String, Integer> categoryQuantities = new HashMap<>();
    private Datebox startDateBox;
    private Datebox endDateBox;
    private Toolbarbutton okButton;

    public DPStageWiseRejection() {
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

        // Create separate boxes for each category
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

            mainDiv.appendChild(categoryBox);
        }
        return mainDiv;
    }

    private void initOptions() {
        Properties ctx = Env.getCtx();
        int clientId = Env.getAD_Client_ID(ctx);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
        	Calendar calendar = Calendar.getInstance();
        	calendar.add(Calendar.DAY_OF_YEAR, -60); // Subtract 60 days from the current date
        	
        	Date startDate = calendar.getTime();
        	if(startDateBox != null)
        		startDate = startDateBox.getValue();
        	
        	Calendar calendar2 = Calendar.getInstance();
        	Date today = calendar2.getTime();
        	Date endDate = today;
        	if (endDateBox != null && endDateBox.getValue() != null) {
        	    endDate = endDateBox.getValue();
        	}
            String sql = "WITH categories AS (SELECT 'Initiation' AS Category UNION ALL SELECT 'Callusing' UNION ALL SELECT 'Multiplication'\n"
            		+ "UNION ALL SELECT 'Elongation' UNION ALL SELECT 'Rooting' UNION ALL SELECT 'Hardening'),\n"
            		+ "category_counts AS (SELECT lt.name AS RoomType,\n"
            		+ "        CASE\n"
            		+ "            WHEN pr.name LIKE 'BI%' OR pr.name LIKE 'N%' THEN 'Initiation'\n"
            		+ "            WHEN pr.name LIKE 'BC%' THEN 'Callusing'\n"
            		+ "            WHEN pr.name LIKE 'BM%' OR pr.name LIKE 'M%' THEN 'Multiplication'\n"
            		+ "            WHEN pr.name LIKE 'BE%' OR pr.name LIKE 'E%' THEN 'Elongation'\n"
            		+ "            WHEN pr.name LIKE 'BR%' OR pr.name LIKE 'R%' THEN 'Rooting'\n"
            		+ "            WHEN pr.name LIKE 'BH%' OR pr.name LIKE 'H%' THEN 'Hardening'\n"
            		+ "            ELSE 'Other'\n"
            		+ "        END AS Category, SUM(o.qtyonhand)::int AS TotalQuantity FROM adempiere.m_storageonhand o\n"
            		+ "JOIN adempiere.m_product pr ON pr.m_product_id = o.m_product_id JOIN adempiere.m_locator l ON l.m_locator_id = o.m_locator_id\n"
            		+ "JOIN adempiere.m_locatortype lt ON lt.m_locatortype_id = l.m_locatortype_id WHERE o.ad_client_id = ? AND o.updated::Date BETWEEN ? AND ?\n"
            		+ "AND lt.description LIKE 'Discard' GROUP BY lt.name, CASE WHEN pr.name LIKE 'BI%' OR pr.name LIKE 'N%' THEN 'Initiation'\n"
            		+ "WHEN pr.name LIKE 'BC%' THEN 'Callusing' WHEN pr.name LIKE 'BM%' OR pr.name LIKE 'M%' THEN 'Multiplication'\n"
            		+ "WHEN pr.name LIKE 'BE%' OR pr.name LIKE 'E%' THEN 'Elongation' WHEN pr.name LIKE 'BR%' OR pr.name LIKE 'R%' THEN 'Rooting'\n"
            		+ "WHEN pr.name LIKE 'BH%' OR pr.name LIKE 'H%' THEN 'Hardening' ELSE 'Other' END)\n"
            		+ "SELECT r.RoomType, c.Category AS Category, COALESCE(cc.TotalQuantity, 0) AS TotalQuantity\n"
            		+ "FROM (SELECT DISTINCT lt.name AS RoomType FROM adempiere.m_locatortype lt WHERE lt.description LIKE 'Discard') r\n"
            		+ "CROSS JOIN categories c LEFT JOIN category_counts cc ON r.RoomType = cc.RoomType AND c.Category = cc.Category\n"
            		+ "ORDER BY CASE c.Category WHEN 'Initiation' THEN 1 WHEN 'Callusing' THEN 2 WHEN 'Multiplication' THEN 3\n"
            		+ "WHEN 'Elongation' THEN 4 WHEN 'Rooting' THEN 5 WHEN 'Hardening' THEN 6 ELSE 7 END;";

            pstmt = DB.prepareStatement(sql, null);
            pstmt.setInt(1, clientId);
            pstmt.setDate(2, new java.sql.Date(startDate.getTime()));
            pstmt.setDate(3, new java.sql.Date(endDate.getTime()));

            rs = pstmt.executeQuery();
            categories.clear();
            categoryQuantities.clear();
            while (rs.next()) {
                String category = rs.getString("Category");
                int totalQuantity = rs.getInt("TotalQuantity");
                categories.add(category);
                categoryQuantities.put(category, totalQuantity);
            }
            DB.close(rs, pstmt);
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
//        this.removeChild(this.getLastChild()); // Remove previous activities panel
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