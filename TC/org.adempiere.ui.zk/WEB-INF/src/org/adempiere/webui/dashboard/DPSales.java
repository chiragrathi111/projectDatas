package org.adempiere.webui.dashboard;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
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

public class DPSales extends DashboardPanel implements EventListener<Event> {

    private Datebox startDateBox;
    private Datebox endDateBox;
    private Toolbarbutton okButton;

    private Label labelOrdersProcessedCount;
    private String ordersProcessedCount;

    private Label labelLinesProcessedCount;
    private String linesProcessedCount;

    private Label labelUnitsProcessedCount;
    private int unitsProcessedCount;

    private Label labelAmount;
    private String amount;

    private static final long serialVersionUID = 1L;

    public DPSales() {
        super();
        this.setSclass("activities-box");
        this.appendChild(createDatePanel());  // Add date panel
        this.appendChild(createActivitiesPanel());
        initOptions();
        }

    private Div createActivitiesPanel() {
        Div div = new Div();
        div.setSclass(ITheme.WARE_HOUSE_DATA_WIDGET);

        // Orders Processed Section
        Div ordersDiv = new Div();
        Label ordersLabelText = new Label("Orders Processed");
        ordersLabelText.setSclass(ITheme.DASHBOARD_WIDGET_LABELS);
        ordersDiv.appendChild(ordersLabelText);

        labelOrdersProcessedCount = new Label();
        labelOrdersProcessedCount.setSclass(ITheme.DASHBOARD_WIDGET_COUNT);
        ordersDiv.appendChild(labelOrdersProcessedCount);

        ordersDiv.setSclass(ITheme.DASHBOARD_WIDGET_Text_COUNT_CONT);
        div.appendChild(ordersDiv);

        // Lines Processed Section
        Div linesDiv = new Div();
        Label linesLabelText = new Label("Lines Processed");
        linesLabelText.setSclass(ITheme.DASHBOARD_WIDGET_LABELS);
        linesDiv.appendChild(linesLabelText);

        labelLinesProcessedCount = new Label();
        labelLinesProcessedCount.setSclass(ITheme.DASHBOARD_WIDGET_COUNT);
        linesDiv.appendChild(labelLinesProcessedCount);

        linesDiv.setSclass(ITheme.DASHBOARD_WIDGET_Text_COUNT_CONT);
        div.appendChild(linesDiv);

        // Units Processed Section
        Div unitsDiv = new Div();
        Label unitsLabelText = new Label("Units Processed");
        unitsLabelText.setSclass(ITheme.DASHBOARD_WIDGET_LABELS);
        unitsDiv.appendChild(unitsLabelText);

        labelUnitsProcessedCount = new Label();
        labelUnitsProcessedCount.setSclass(ITheme.DASHBOARD_WIDGET_COUNT);
        unitsDiv.appendChild(labelUnitsProcessedCount);

        unitsDiv.setSclass(ITheme.DASHBOARD_WIDGET_Text_COUNT_CONT);
        div.appendChild(unitsDiv);

        // Amount Section
        Div amountDiv = new Div();
        Label amountLabelText = new Label("Amount");
        amountLabelText.setSclass(ITheme.DASHBOARD_WIDGET_LABELS);
        amountDiv.appendChild(amountLabelText);

        Div currencyDiv = new Div();
        Label currencyTypeText = new Label("\u20B9");
        currencyTypeText.setSclass(ITheme.DASHBOARD_WIDGET_CURRENCY_TYPE);
        currencyDiv.appendChild(currencyTypeText);

        labelAmount = new Label();
        labelAmount.setSclass(ITheme.DASHBOARD_WIDGET_COUNT);
        currencyDiv.appendChild(labelAmount);

        amountDiv.appendChild(currencyDiv);
        amountDiv.setSclass(ITheme.DASHBOARD_WIDGET_Text_COUNT_CONT);
        div.appendChild(amountDiv);

        return div;
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

    private void initOptions() {
    	updateDashboardData();
    	updateUI();
    }

    @Override
    public void onEvent(Event event) throws Exception {
        if (event.getTarget() == okButton) {
        	initOptions();
        }
    }

	private void updateDashboardData() {
		Date startDate = startDateBox.getValue();
		Date endDate = endDateBox.getValue();
		Properties ctx = Env.getCtx();
		int clientId = Env.getAD_Client_ID(ctx);
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		if (startDate == null || endDate == null) {
			// Log an error or set default values if dates are null
			System.err.println("Start date or end date is null.");
			return;
		}

		String query = "SELECT COUNT(DISTINCT o.C_Order_ID) AS ordercount,\n"
				+ "COUNT(ol.C_OrderLine_ID) AS orderLineCount,\n" + "COALESCE(SUM(ol.QtyOrdered),0) AS totalunits,\n"
				+ "COALESCE(SUM(ol.LineNetAmt),0) AS totalamount \n"
				+ "FROM adempiere.C_Order o JOIN adempiere.C_OrderLine ol ON o.C_Order_ID = ol.C_Order_ID\n"
				+ "WHERE o.AD_Client_ID = ? AND o.created::Date BETWEEN '" + startDate + "' AND '" + endDate + "';";

		try {
			pstmt = DB.prepareStatement(query, null);
			int index = 1;
			pstmt.setInt(index++, clientId);

			rs = pstmt.executeQuery();
			if (rs.next()) {
				ordersProcessedCount = String.valueOf(rs.getInt("ordercount"));
				linesProcessedCount = String.valueOf(rs.getInt("orderLineCount"));
				unitsProcessedCount = rs.getInt("totalunits");
				amount = rs.getBigDecimal("totalamount").toString();
			} else {
				ordersProcessedCount = "0";
				linesProcessedCount = "0";
				unitsProcessedCount = 0;
				amount = "0.00";
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DB.close(rs, pstmt);
		}
	}
    
    @Override
    public void refresh(ServerPushTemplate template) {
    	updateDashboardData();
        template.executeAsync(this);
    }

    @Override
    public void updateUI() {
    	// Check if labels are null before updating them
        if (labelOrdersProcessedCount != null) {
            labelOrdersProcessedCount.setValue(" " + ordersProcessedCount);
        }
        if (labelLinesProcessedCount != null) {
            labelLinesProcessedCount.setValue(" " + linesProcessedCount);
        }
        if (labelUnitsProcessedCount != null) {
            labelUnitsProcessedCount.setValue(" " + unitsProcessedCount);
        }
        if (labelAmount != null) {
            labelAmount.setValue(" " + amount);
        }
    }
}