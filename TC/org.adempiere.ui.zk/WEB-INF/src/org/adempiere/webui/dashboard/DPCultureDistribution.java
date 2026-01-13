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

public class DPCultureDistribution extends DashboardPanel implements EventListener<Event> {

    private static final long serialVersionUID = 1L;

    private List<String> categories = new ArrayList<>();
    private Map<String, Integer> categoryQuantities = new HashMap<>();
    private Datebox startDateBox;
    private Datebox endDateBox;
    private Toolbarbutton okButton;

    public DPCultureDistribution() {
        super();
        this.setSclass("activities-box");       
//        this.appendChild(createDatePanel());  // Add date panel
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
        ResultSet RS = null;
        try {
        	
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
            String sql = "WITH categories AS (SELECT 'Initiation' AS Category UNION ALL SELECT 'Callusing' UNION ALL SELECT 'Multiplication'\n"
            		+ "UNION ALL SELECT 'Elongation' UNION ALL SELECT 'Rooting' UNION ALL SELECT 'Hardening'),\n"
            		+ "category_counts AS (SELECT\n"
            		+ "        CASE\n"
            		+ "            WHEN pr.name LIKE 'BI%' OR pr.name LIKE 'N%' THEN 'Initiation'\n"
            		+ "            WHEN pr.name LIKE 'BC%' THEN 'Callusing'\n"
            		+ "            WHEN pr.name LIKE 'BM%' OR pr.name LIKE 'M%' THEN 'Multiplication'\n"
            		+ "            WHEN pr.name LIKE 'BE%' OR pr.name LIKE 'E1%' THEN 'Elongation'\n"
            		+ "            WHEN pr.name LIKE 'BR%' OR pr.name LIKE 'R%' THEN 'Rooting'\n"
            		+ "            WHEN pr.name LIKE 'BH%' OR pr.name LIKE 'H%' THEN 'Hardening'\n"
            		+ "            ELSE 'Other'\n"
            		+ "        END AS Category,SUM(o.qtyonhand)::int AS TotalQuantity\n"
            		+ "FROM adempiere.m_storageonhand o JOIN adempiere.m_product pr ON pr.m_product_id = o.m_product_id\n"
            		+ "JOIN adempiere.m_locator l ON l.m_locator_id = o.m_locator_id JOIN adempiere.m_locatortype lt ON lt.m_locatortype_id = l.m_locatortype_id \n"
            		+ "WHERE o.ad_client_id = ? \n"
//            		+ " AND o.created::Date BETWEEN ? AND ? "
            		+ "AND lt.description <> 'Discard'\n"
            		+ "GROUP BY CASE WHEN pr.name LIKE 'BI%' OR pr.name LIKE 'N%' THEN 'Initiation' WHEN pr.name LIKE 'BC%' THEN 'Callusing'\n"
            		+ "WHEN pr.name LIKE 'BM%' OR pr.name LIKE 'M%' THEN 'Multiplication' WHEN pr.name LIKE 'BE%' OR pr.name LIKE 'E1%' THEN 'Elongation'\n"
            		+ "WHEN pr.name LIKE 'BR%' OR pr.name LIKE 'R%' THEN 'Rooting' WHEN pr.name LIKE 'BH%' OR pr.name LIKE 'H%' THEN 'Hardening'\n"
            		+ "ELSE 'Other' END)\n"
            		+ "SELECT c.Category,COALESCE(cc.TotalQuantity, 0) AS TotalQuantity FROM categories c\n"
            		+ "LEFT JOIN category_counts cc ON c.Category = cc.Category\n"
            		+ "ORDER BY CASE c.Category WHEN 'Initiation' THEN 1 WHEN 'Callusing' THEN 2\n"
            		+ "WHEN 'Multiplication' THEN 3 WHEN 'Elongation' THEN 4\n"
            		+ "WHEN 'Rooting' THEN 5 WHEN 'Hardening' THEN 6 ELSE 7 END;";
            pstmt = DB.prepareStatement(sql, null);
            pstmt.setInt(1, clientId);
//            pstmt.setDate(2, new java.sql.Date(startDate.getTime()));
//            pstmt.setDate(3, new java.sql.Date(endDate.getTime()));
            RS = pstmt.executeQuery();
            categories.clear();
            categoryQuantities.clear();
            while (RS.next()) {
                String category = RS.getString("Category");
                int totalQuantity = RS.getInt("TotalQuantity");
                categories.add(category);
                categoryQuantities.put(category, totalQuantity);
            }
            DB.close(RS, pstmt);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void refresh(ServerPushTemplate template) {
    	initOptions();
        template.executeAsync(this);
    }
//    private void updateDashboardData() {
//        initOptions(); // Refresh data based on date range
//        updateUI();    // Update UI to reflect new data
//    }

    @Override
    public void updateUI() {
//    	this.removeChild(this.getLastChild()); // Remove previous activities panel
//        this.appendChild(createActivitiesPanel());
    }

    @Override
    public void onEvent(Event event) throws Exception {
    	if (event.getTarget() == okButton) {
    		  initOptions(); // Refresh data based on date range
    		  this.removeChild(this.getLastChild()); // Remove previous activities panel
    	      this.appendChild(createActivitiesPanel());
        }
    }
}
