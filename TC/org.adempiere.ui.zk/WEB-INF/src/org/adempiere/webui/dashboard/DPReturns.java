package org.adempiere.webui.dashboard;

import org.adempiere.webui.component.Button;
import org.adempiere.webui.desktop.IDesktop;
import org.adempiere.webui.session.SessionManager;
import org.adempiere.webui.theme.ThemeManager;
import org.adempiere.webui.util.ServerPushTemplate;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.*;

public class DPReturns extends DashboardPanel implements EventListener<Event> {

    private static final long serialVersionUID = 1L;

    private Button btnAlert;
    private Combobox periodBox;
    private String labelA;
    private int noofAlert;

    public DPReturns() {
        super();
        this.setSclass("activities-box");
        this.setStyle("overflow: visible;"); // 🔥 IMPORTANT
        this.appendChild(createPanel());
    }

    // ------------------------------------------------------
    // UI
    // ------------------------------------------------------
    private Box createPanel() {

        Vbox root = new Vbox();
        root.setSpacing("6px");
        root.setStyle("overflow: visible;");

        Hbox hbox = new Hbox();
        hbox.setSpacing("8px");
        hbox.setAlign("center");
        hbox.setStyle("overflow: visible;");

        // 🔽 Period Dropdown
        periodBox = new Combobox();
        periodBox.setWidth("90px");
        periodBox.setReadonly(false);
        periodBox.setDisabled(false);
        periodBox.setButtonVisible(true);
        periodBox.setStyle("display:inline-block; overflow:visible;");

        periodBox.appendChild(createItem("DAY", "day"));
        periodBox.appendChild(createItem("MONTH", "month"));
        periodBox.appendChild(createItem("YEAR", "year"));
        periodBox.setSelectedIndex(0);

        hbox.appendChild(periodBox);

        // 🔔 Alert Button
        btnAlert = new Button();
        labelA = Util.cleanAmp(Msg.translate(Env.getCtx(), "AlertActivity"));
        btnAlert.setLabel(labelA + " : 0");
        btnAlert.setTooltiptext("OverHeat Temperature Alerts");
        btnAlert.setImage(ThemeManager.getThemeResource("images/tem.png"));

        int menuId = DB.getSQLValue(
            null,
            "SELECT AD_Menu_ID FROM AD_Menu WHERE Name='Temperature' AND IsSummary='N'"
        );
        btnAlert.setName(String.valueOf(menuId));
        btnAlert.addEventListener(Events.ON_CLICK, this);

        hbox.appendChild(btnAlert);

        root.appendChild(hbox);
        return root;
    }

    private Comboitem createItem(String label, String value) {
        Comboitem item = new Comboitem(label);
        item.setValue(value);
        return item;
    }

    // ------------------------------------------------------
    // Dashboard refresh
    // ------------------------------------------------------
    @Override
    public void refresh(ServerPushTemplate template) {

        int alert = getOverHeatCount(getSelectedPeriod());

        if (noofAlert != alert) {
            noofAlert = alert;
            template.executeAsync(this);
        }
    }

    @Override
    public void updateUI() {

        btnAlert.setLabel(labelA + " : " + noofAlert);

        if (noofAlert > 0) {
            btnAlert.setSclass("z-button temp-alert-red");
        } else {
            btnAlert.setSclass(null);
        }

        EventQueues.lookup(
            IDesktop.ACTIVITIES_EVENT_QUEUE, true
        ).publish(new Event(
            IDesktop.ON_ACTIVITIES_CHANGED_EVENT,
            null,
            noofAlert
        ));
    }

    // ------------------------------------------------------
    // Click handling
    // ------------------------------------------------------
    @Override
    public void onEvent(Event event) {

        if (Events.ON_CLICK.equals(event.getName())) {

            String period = getSelectedPeriod();

            // Pass period to window
            Env.setContext(Env.getCtx(), "#TEMP_PERIOD", period);

            int menuId = Integer.parseInt(btnAlert.getName());
            if (menuId > 0) {
                SessionManager.getAppDesktop().onMenuSelected(menuId);
            }
        }
    }

    private String getSelectedPeriod() {
        Comboitem item = periodBox.getSelectedItem();
        return item != null ? item.getValue().toString() : "day";
    }

    // ------------------------------------------------------
    // SQL (INSIDE SAME CLASS)
    // ------------------------------------------------------
    private int getOverHeatCount(String period) {

        String sql =
            "SELECT COUNT(*) " +
            "FROM tc_temperaturestatus t " +
            "JOIN tc_tempstatus ts ON ts.tc_tempstatus_id=t.tc_tempstatus_id " +
            "WHERE ts.name='OverHeat' " +
            "AND t.ad_client_id=? " +
            "AND t.created >= CASE lower(?) " +
            " WHEN 'day' THEN CURRENT_DATE " +
            " WHEN 'month' THEN date_trunc('month', CURRENT_DATE) " +
            " WHEN 'year' THEN date_trunc('year', CURRENT_DATE) END " +
            "AND t.created < CASE lower(?) " +
            " WHEN 'day' THEN CURRENT_DATE + INTERVAL '1 day' " +
            " WHEN 'month' THEN date_trunc('month', CURRENT_DATE) + INTERVAL '1 month' " +
            " WHEN 'year' THEN date_trunc('year', CURRENT_DATE) + INTERVAL '1 year' END";

        return DB.getSQLValue(
            null,
            sql,
            Env.getAD_Client_ID(Env.getCtx()),
            period,
            period
        );
    }

    @Override
    public boolean isPooling() {
        return true;
    }

    @Override
    public boolean isLazy() {
        return true;
    }
}


//package org.adempiere.webui.dashboard;
//
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.Properties;
//
//import org.adempiere.webui.component.Label;
//import org.adempiere.webui.theme.ITheme;
//import org.adempiere.webui.util.ServerPushTemplate;
//import org.compiere.util.DB;
//import org.compiere.util.Env;
//import org.zkoss.zk.ui.event.Event;
//import org.zkoss.zk.ui.event.EventListener;
//import org.zkoss.zk.ui.select.annotation.Wire;
//import org.zkoss.zul.Chart;
//import org.zkoss.zul.Div;
//import org.zkoss.zul.Hlayout;
//
//public class DPReturns extends DashboardPanel implements EventListener<Event> {
//
//	private static final long serialVersionUID = 1L;
//
//	private Label labelProcessedCount;
//	private String processedCountText;
//	private Label processedCountLabelText;
//
//	private Label labelReturnPercentage;
//	private String ReturnPercentText;
//	private Label returnPercentLabelText;
//
//	private int pendingReturnCount;
//	private int returnCountPercenatage;
//
//	@Wire
//	private Chart chart;
//
//	public DPReturns() {
//		super();
//		this.setSclass("activities-box");
//		initLineModel();
//		this.appendChild(createActivitiesPanel());
//	}
//
//	private Hlayout createActivitiesPanel() {
//		Div div = new Div();
//		div.setSclass(ITheme.WARE_HOUSE_DATA_WIDGET);
//
//		Div childDivFirst = new Div();
//
//		processedCountLabelText = new Label();
//		processedCountLabelText.setSclass(ITheme.DASHBOARD_WIDGET_LABELS);
//		processedCountText = "Returns Pending";
//		processedCountLabelText.setValue(processedCountText);
//		childDivFirst.appendChild(processedCountLabelText);
//
//		labelProcessedCount = new Label();
//		labelProcessedCount.setSclass(ITheme.DASHBOARD_WIDGET_COUNT);
//		labelProcessedCount.setValue(" " + pendingReturnCount);
//		childDivFirst.appendChild(labelProcessedCount);
//
//		childDivFirst.setSclass(ITheme.DASHBOARD_WIDGET_Text_COUNT_CONT);
//		div.appendChild(childDivFirst);
//
//		Div childDivSec = new Div();
//
//		returnPercentLabelText = new Label();
//		returnPercentLabelText.setSclass(ITheme.DASHBOARD_WIDGET_LABELS);
//		ReturnPercentText = "Returns Rate";
//		returnPercentLabelText.setValue(ReturnPercentText);
//		childDivSec.appendChild(returnPercentLabelText);
//
//		labelReturnPercentage = new Label();
//		labelReturnPercentage.setSclass(ITheme.DASHBOARD_WIDGET_COUNT);
//		labelReturnPercentage.setValue(" " + returnCountPercenatage + "%");
//
//		childDivSec.appendChild(labelReturnPercentage);
//
//		childDivSec.setSclass(ITheme.DASHBOARD_WIDGET_Text_COUNT_CONT);
//		div.appendChild(childDivSec);
//
//		Div div2 = new Div();
//		div2.setHflex("1");
//
//		Div div1 = new Div();
//		div1.appendChild(div);
//		Hlayout layout = new Hlayout();
//		layout.setWidth("100%");
//		layout.appendChild(div1);
//		layout.appendChild(div2);
//
//		return layout;
//	}
//
//	private void initLineModel() {
//
//		Properties ctx = Env.getCtx();
//		int wareHouseId = DPWarehouseSelection.getWareHouse_ID(ctx);
//		int productId = DPWarehouseSelection.getProduct_ID(ctx);
//		int clientId = Env.getAD_Client_ID(ctx);
//
//		try {
//			PreparedStatement pstmt = null;
//			ResultSet RS = null;
//
//			String sql = null;
//			String returnPendingSql = null;
//			if (wareHouseId == 0 && productId == 0) {
//				sql = "SELECT SUM(DISTINCT(movementqty))AS salesQty,SUM(DISTINCT(qtydelivered)) AS returnQty FROM adempiere.m_rma rm\n"
//						+ "JOIN adempiere.m_inout ii ON ii.m_inout_id = rm.inout_id\n"
//						+ "JOIN adempiere.m_inoutline ili ON ili.m_inout_id = rm.inout_id\n"
//						+ "JOIN adempiere.m_rmaline rmli ON rmli.m_rma_id = rm.m_rma_id\n"
//						+ "WHERE rm.ad_client_id = " + clientId + ";";
//				
//				returnPendingSql = "SELECT count(docStatus) from m_inout mi \n"
//						+ "join c_docType cd on cd.c_docType_Id = mi.c_docType_Id\n"
//						+ "where cd.name = 'MM Customer Return' and mi.docstatus = 'DR' and mi.ad_client_id=" + clientId
//						+ ";";
//			} else if (wareHouseId != 0 && productId == 0) {
//				sql = "SELECT SUM(DISTINCT(movementqty))AS salesQty,SUM(DISTINCT(qtydelivered)) AS returnQty FROM adempiere.m_rma rm\n"
//						+ "JOIN adempiere.m_inout ii ON ii.m_inout_id = rm.inout_id\n"
//						+ "JOIN adempiere.m_inoutline ili ON ili.m_inout_id = rm.inout_id\n"
//						+ "JOIN adempiere.m_rmaline rmli ON rmli.m_rma_id = rm.m_rma_id\n"
//						+ "JOIN adempiere.m_warehouse wh ON wh.m_warehouse_id = ii.m_warehouse_id\n"
//						+ "WHERE ii.m_warehouse_id = "+ wareHouseId +" AND rm.ad_client_id = "+ clientId +";";
//
//				returnPendingSql = "SELECT count(docStatus) from m_inout mi \n"
//						+ "join c_docType cd on cd.c_docType_Id = mi.c_docType_Id\n"
//						+ "where cd.name = 'MM Customer Return' and mi.docstatus = 'DR' and mi.ad_client_id=" + clientId
//						+ " and mi.m_warehouse_id =" + wareHouseId + ";";
//			} else if (wareHouseId == 0 && productId != 0) {
//				sql = "SELECT SUM(DISTINCT(movementqty))AS salesQty,SUM(DISTINCT rmli.qtydelivered) AS returnQty FROM adempiere.m_rma rm\n"
//						+ "JOIN adempiere.m_inout ii ON ii.m_inout_id = rm.inout_id\n"
//						+ "JOIN adempiere.m_inoutline ili ON ili.m_inout_id = rm.inout_id\n"
//						+ "JOIN adempiere.m_rmaline rmli ON rmli.m_rma_id = rm.m_rma_id\n"
//						+ "JOIN adempiere.m_product pd ON pd.m_product_id = rmli.m_product_id\n"
//						+ "WHERE rm.ad_client_id = "+ clientId +" and rmli.m_product_id = "+ productId +"";
//
//				returnPendingSql = "SELECT count(docStatus) from m_inout mi \n"
//						+ "join c_docType cd on cd.c_docType_Id = mi.c_docType_Id\n"
//						+ "join m_inoutline ml on ml.m_inout_id = mi.m_inout_id\n"
//						+ "where cd.name = 'MM Customer Return' and mi.docstatus = 'DR' and mi.ad_client_id=" + clientId
//						+ " and ml.m_product_id =" + productId + ";";
//			}
//
//			pstmt = DB.prepareStatement(sql.toString(), null);
//			RS = pstmt.executeQuery();
//			while (RS.next()) {
//				int returnCount = RS.getInt("returnQty");
//				int outCount = RS.getInt("salesQty");
//				if (outCount != 0) {
//					returnCountPercenatage = ((returnCount * 100) / outCount);
//				}
//				else
//					returnCountPercenatage = 0;
//			}
//			DB.close(RS, pstmt);
//			RS = null;
//			pstmt = null;
//
//			pstmt = DB.prepareStatement(returnPendingSql.toString(), null);
//			RS = pstmt.executeQuery();
//			while (RS.next()) {
//				pendingReturnCount = RS.getInt("count");
//			}
//			DB.close(RS, pstmt);
//			RS = null;
//			pstmt = null;
//
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//
//	}
//
//	@Override
//	public void refresh(ServerPushTemplate template) {
//		initLineModel();
//		template.executeAsync(this);
//	}
//
//	@Override
//	public void updateUI() {
//		labelProcessedCount.setValue(" " + pendingReturnCount);
//		labelReturnPercentage.setValue(" " + returnCountPercenatage + "%");
//	}
//
//	@Override
//	public void onEvent(Event event) throws Exception {
//	}
//}
