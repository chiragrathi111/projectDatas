package com.pipra.rwpl.dashboard;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Properties;

import org.adempiere.webui.component.Label;
import org.adempiere.webui.dashboard.DashboardPanel;
import org.adempiere.webui.desktop.IDesktop;
import org.adempiere.webui.theme.ITheme;
import org.adempiere.webui.util.ServerPushTemplate;
import org.compiere.model.MInOut;
import org.compiere.model.MMovement;
import org.compiere.model.PO;
import org.compiere.model.Query;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zul.Div;

public class TransactionCount extends DashboardPanel implements EventListener<Event> {

    private static final long serialVersionUID = 1L;

    // Dynamic count labels shown in UI (updated on refresh)
    private Label labelInoutCount;
    private Label labelInCount;
    private Label labelOutCount;
    private Label labelInternalMoveCount;
    private Label labelemptyLocatorCount;
    private Label labeltotalTonsCount;

    // Count values as integers
    private int inoutCount = 0;
    private int inCount = 0;
    private int outCount = 0;
    private int internalMoveCount = 0;
    private int emptyLocatorCount = 0;
    private int totalTonsCount = 0;

    public TransactionCount() {
        super();
        this.setSclass("activities-box");
        initOptions(); // Load initial values
        this.appendChild(createActivitiesPanel());
    }

    /**
     * Creates the main panel with all metric divs.
     * Static label texts are local variables as they do not change dynamically.
     */
    private Div createActivitiesPanel() {
        Div container = new Div();
        container.setSclass(ITheme.WARE_HOUSE_DATA_WIDGET);

        Label inoutCountLabelText = new Label();
        Label inCountLabelText = new Label();
        Label outCountLabelText = new Label();
        Label internalMoveCountLabelText = new Label();
        Label emptyLocatorCountLabelText = new Label();
        Label totalTonsCountLabelText = new Label();

        container.appendChild(createMetricDiv("Receipts", inoutCountLabelText,
                labelInoutCount = new Label(), inoutCount));
        container.appendChild(createMetricDiv("Inwards", inCountLabelText,
                labelInCount = new Label(), inCount));
        container.appendChild(createMetricDiv("Dispatches", outCountLabelText,
                labelOutCount = new Label(), outCount));
        container.appendChild(createMetricDiv("Internal Moves", internalMoveCountLabelText,
                labelInternalMoveCount = new Label(), internalMoveCount));
        container.appendChild(createMetricDiv("Empty Locators", emptyLocatorCountLabelText,
                labelemptyLocatorCount = new Label(), emptyLocatorCount));
        container.appendChild(createMetricDiv("Total Tons in Warehouse", totalTonsCountLabelText,
                labeltotalTonsCount = new Label(), totalTonsCount));

        return container;
    }

    /**
     * Helper method to create a metric display div with a static label and a dynamic count.
     */
    private Div createMetricDiv(String labelText, Label labelTextComponent, Label countLabel, int countValue) {
        Div div = new Div();

        labelTextComponent.setSclass(ITheme.DASHBOARD_WIDGET_LABELS);
        labelTextComponent.setValue(labelText);
        div.appendChild(labelTextComponent);

        countLabel.setSclass(ITheme.DASHBOARD_WIDGET_COUNT);
        countLabel.setValue("  " + countValue);
        div.appendChild(countLabel);

        div.setSclass(ITheme.DASHBOARD_WIDGET_Text_COUNT_CONT);

        return div;
    }

    /**
     * Loads counts from database and business logic.
     */
    private void initOptions() {
        Properties ctx = Env.getCtx();
        int clientId = Env.getAD_Client_ID(ctx);
        String trxName = null;

        // Reset counts before calculation
        inoutCount = 0;
        inCount = 0;
        outCount = 0;
        internalMoveCount = 0;
        emptyLocatorCount = 0;
        totalTonsCount = 0;

        // Load transactions counts (in/out) for today with completed status
        List<PO> list = new Query(ctx, MInOut.Table_Name,
                "ad_client_ID =? AND DATE(created) = DATE(NOW()) AND docstatus = 'CO'", trxName)
                .setParameters(clientId).list();

        for (PO po : list) {
            MInOut inOut = new MInOut(ctx, po.get_ID(), trxName);
            if (inOut.isSOTrx())
                outCount++;
            else
                inCount++;

            inoutCount++;
        }

        // Load internal move count for today
        internalMoveCount = new Query(ctx, MMovement.Table_Name,
                "ad_client_ID =? AND DATE(created) = DATE(NOW())", trxName)
                .setParameters(clientId).count();

        // Load count of empty locators
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT COUNT(*) AS total_count FROM ( " +
                    "SELECT l.m_locator_id " +
                    "FROM adempiere.m_locator l " +
                    "LEFT JOIN adempiere.pi_productlabel s ON l.m_locator_id = s.m_locator_id " +
                    "WHERE NOT EXISTS ( " +
                    "   SELECT 1 FROM adempiere.pi_productlabel pp_sales " +
                    "   WHERE pp_sales.labeluuid = s.labeluuid AND pp_sales.issotrx = 'Y') " +
                    "GROUP BY l.m_locator_id " +
                    "HAVING COALESCE(SUM(s.quantity),0) = 0 " +
                    ") AS subquery";

            pstmt = DB.prepareStatement(sql, null);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                emptyLocatorCount = rs.getInt("total_count");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DB.close(rs, pstmt);
        }

        // Load total weight tons in warehouse
        pstmt = null;
        rs = null;
        try {
            String sql = "SELECT ROUND(SUM(pr.weight * pp.quantity) / 1000::numeric, 3) AS total_weight_ton " +
                    "FROM adempiere.pi_productlabel pp " +
                    "JOIN adempiere.m_product pr ON pr.m_product_id = pp.m_product_id " +
                    "JOIN adempiere.m_locator l ON l.m_locator_id = pp.m_locator_id " +
                    "JOIN adempiere.m_warehouse w ON w.m_warehouse_id = l.m_warehouse_id " +
                    "WHERE NOT EXISTS ( " +
                    "   SELECT 1 FROM adempiere.pi_productlabel pp_sales " +
                    "   WHERE pp_sales.labeluuid::text = pp.labeluuid::text AND pp_sales.issotrx::text = 'Y'::text)";

            pstmt = DB.prepareStatement(sql, null);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                totalTonsCount = rs.getInt("total_weight_ton");
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
        labelInoutCount.setValue("  " + inoutCount);
        labelInCount.setValue("  " + inCount);
        labelOutCount.setValue("  " + outCount);
        labelInternalMoveCount.setValue("  " + internalMoveCount);
        labelemptyLocatorCount.setValue("  " + emptyLocatorCount);
        labeltotalTonsCount.setValue("  " + totalTonsCount);

        EventQueue<Event> queue = EventQueues.lookup(IDesktop.ACTIVITIES_EVENT_QUEUE, true);
        Event event = new Event(IDesktop.ON_ACTIVITIES_CHANGED_EVENT, null,
                inoutCount + inCount + outCount + internalMoveCount + emptyLocatorCount + totalTonsCount);
        queue.publish(event);
    }

    @Override
    public void onEvent(Event event) throws Exception {
        // Implement event handling logic if needed
    }
}
