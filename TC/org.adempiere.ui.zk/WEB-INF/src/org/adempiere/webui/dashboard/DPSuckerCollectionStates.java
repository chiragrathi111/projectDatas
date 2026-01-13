package org.adempiere.webui.dashboard;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

public class DPSuckerCollectionStates extends DashboardPanel implements EventListener<Event> {

    private static final long serialVersionUID = 1L;

    private Label labelTSC;
    private Label labelTSCCount;
    private Label labelBorder;

    private Label labelAllSC;
    private Label labelAllSCCount;
    
    private int CompletedCount;
    private int cancelCount;
    
    private Datebox startDateBox;
    private Datebox endDateBox;
    private Toolbarbutton okButton;

    public DPSuckerCollectionStates() {
        super();
        this.setSclass("activities-box");
        initOptions();
        this.appendChild(createMainPanel());
    }
    
    private Div createDatePanel(boolean visible) {

        Div header = new Div();

        Hbox box = new Hbox();
        box.setSpacing("6px");

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

        if (!visible) {
            header.setStyle(
                "visibility:hidden;" +   // hides content only
                "width:110px;" +
                "min-width:110px;" +
                "flex-shrink:0;"
            );
        }

        return header;
    }
    
    private Hbox createMainPanel() {

        Hbox mainBox = new Hbox();
        mainBox.setWidth("100%");
        mainBox.setSpacing("20px");
        mainBox.setStyle(
            "display:flex;" +
            "align-items:flex-start;"
        );

        Div leftPanel = new Div();
        leftPanel.setStyle(
            "flex:1;" +
            "min-width:260px;"
        );

        Div rightPanel = new Div();
        rightPanel.setStyle(
            "flex:1;" +
            "min-width:320px;"
        );

        leftPanel.appendChild(createDatePanel(false));
        leftPanel.appendChild(createTodayPanel());

        rightPanel.appendChild(createDatePanel(true));
        rightPanel.appendChild(createTotalPanel());

        mainBox.appendChild(leftPanel);
        mainBox.appendChild(rightPanel);

        return mainBox;
    }

    
    private Div createTodayPanel() {

        Div div = new Div();
        div.setStyle("width:100%; margin-top:10px;");

        Div child = new Div();
        child.setSclass(ITheme.DASHBOARD_WIDGET_Text_COUNT_CONT_BASIC_STATS);
        child.setStyle(
            "display:flex;" +
            "flex-direction:column;" +
            "align-items:flex-start;"
        );

        Div header = new Div();
        header.setStyle(
            "display:flex;" +
            "align-items:center;" +
            "gap:6px;"
        );

        labelBorder = new Label();
        labelBorder.setSclass(ITheme.DASHBOARD_WIDGET_LABELS_BORDER_PURPLE);

        labelTSC = new Label("Today Sucker Collection");
        labelTSC.setSclass(ITheme.DASHBOARD_WIDGET_LABELS);
        labelTSC.setStyle("white-space:nowrap;");

        header.appendChild(labelBorder);
        header.appendChild(labelTSC);

        labelTSCCount = new Label(" " + CompletedCount);
        labelTSCCount.setSclass(ITheme.DASHBOARD_WIDGET_COUNT);

        child.appendChild(header);
        child.appendChild(labelTSCCount);
        div.appendChild(child);

        return div;
    }
    
    private Div createTotalPanel() {

        Div div = new Div();
        div.setStyle("width:100%; margin-top:10px;");

        Div child = new Div();
        child.setSclass(ITheme.DASHBOARD_WIDGET_Text_COUNT_CONT_BASIC_STATS);
        child.setStyle(
            "display:flex;" +
            "flex-direction:column;" +
            "align-items:flex-start;"
        );

        Div header = new Div();
        header.setStyle(
            "display:flex;" +
            "align-items:center;" +
            "gap:6px;"
        );

        labelBorder = new Label();
        labelBorder.setSclass(ITheme.DASHBOARD_WIDGET_LABELS_BORDER_ORANGE);

        labelAllSC = new Label("Total Sucker Collection");
        labelAllSC.setSclass(ITheme.DASHBOARD_WIDGET_LABELS);
        labelAllSC.setStyle("white-space:nowrap;");

        header.appendChild(labelBorder);
        header.appendChild(labelAllSC);

        labelAllSCCount = new Label(" " + cancelCount);
        labelAllSCCount.setSclass(ITheme.DASHBOARD_WIDGET_COUNT);

        child.appendChild(header);
        child.appendChild(labelAllSCCount);
        div.appendChild(child);

        return div;
    }
    
    private void initOptions() {

        Properties ctx = Env.getCtx();
        int clientId = Env.getAD_Client_ID(ctx);

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            Date startDate = startDateBox != null ? startDateBox.getValue() : new Date();
            Date endDate = endDateBox != null ? endDateBox.getValue() : new Date();

            String sql =
                "SELECT " +
                " SUM(CASE WHEN created::DATE = CURRENT_DATE THEN suckerno ELSE 0 END) AS today_count, " +
                " SUM(CASE WHEN created::DATE BETWEEN ? AND ? THEN suckerno ELSE 0 END) AS total_count " +
                "FROM adempiere.tc_collectionjoinplant " +
                "WHERE ad_client_id=? AND tc_plantstatus_id IS NULL";

            pstmt = DB.prepareStatement(sql, null);
            pstmt.setDate(1, new java.sql.Date(startDate.getTime()));
            pstmt.setDate(2, new java.sql.Date(endDate.getTime()));
            pstmt.setInt(3, clientId);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                CompletedCount = rs.getInt("today_count");
                cancelCount = rs.getInt("total_count");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DB.close(rs, pstmt);
        }
    }

    @Override
    public void refresh(ServerPushTemplate template) {
    	initOptions();
        template.executeAsync(this);
    }

    @Override
    public void updateUI() {
        labelTSCCount.setValue(" " + CompletedCount);
        labelAllSCCount.setValue("  " + cancelCount);
    }

    @Override
    public void onEvent(Event event) throws Exception {
    	if (event.getTarget() == okButton) {
            updateDashboardData();
        }
    }
    private void updateDashboardData() {
        initOptions(); // Refresh data based on date range
        updateUI();    // Update UI to reflect new data
    }
}