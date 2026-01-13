//package org.adempiere.webui.dashboard;
//
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.util.*;
//
//import org.adempiere.webui.component.Button;
//import org.adempiere.webui.util.ServerPushTemplate;
//import org.compiere.util.DB;
//import org.compiere.util.Env;
//import org.zkoss.zk.ui.Component;
//import org.zkoss.zk.ui.event.Event;
//import org.zkoss.zk.ui.event.EventListener;
//import org.zkoss.zk.ui.event.Events;
//import org.zkoss.zul.*;
//import org.zkoss.zul.event.PagingEvent;
//
///**
// * Dashboard gadget: Temperature Over Heat Alert (Paged)
// * @author Chirag
// */
//public class DPMenuTree extends DashboardPanel implements EventListener<Event> {
//
//    private static final long serialVersionUID = 1L;
//
//    private Combobox periodBox;
//    private Button btnAcknowledge;
//
//    private Grid alertGrid;
//    private Rows alertRows;
//    private Paging paging;
//
//    private Label noRecordLabel;
//    private Div noRecordDiv;
//
//    private static final int PAGE_SIZE = 5;
//
//    private Properties ctx = Env.getCtx();
//    private int clientId = Env.getAD_Client_ID(ctx);
//
//    // Checkbox → tc_temperaturestatus_id
//    private Map<Checkbox, Integer> checkboxMap = new HashMap<>();
//
//    public DPMenuTree() {
//        setSclass("activities-box");
//        appendChild(createUI());
//        loadAlertData(0);
//    }
//
//    /* ================= UI ================= */
//
//    private Vbox createUI() {
//        Vbox root = new Vbox();
//        root.setSpacing("8px");
//
//        root.appendChild(createHeader());
//        root.appendChild(createGrid());
//
//        return root;
//    }
//
//    private Hbox createHeader() {
//        Hbox hbox = new Hbox();
//        hbox.setSpacing("10px");
//        hbox.setAlign("center");
//
//        periodBox = new Combobox();
//        periodBox.setWidth("100px");
//        periodBox.appendChild(new Comboitem("DAY"));
//        periodBox.appendChild(new Comboitem("WEEK"));
//        periodBox.appendChild(new Comboitem("MONTH"));
//        periodBox.setSelectedIndex(0);
//
//        periodBox.addEventListener(Events.ON_CHANGE, e -> {
//            paging.setActivePage(0);
//            loadAlertData(0);
//        });
//
//        btnAcknowledge = new Button("Acknowledge");
//        btnAcknowledge.setIconSclass("z-icon-check");
//        btnAcknowledge.addEventListener(Events.ON_CLICK, this);
//
//        hbox.appendChild(periodBox);
//        hbox.appendChild(btnAcknowledge);
//
//        return hbox;
//    }
//
//    private Component createGrid() {
//
//        Vbox wrapper = new Vbox();
//        wrapper.setWidth("100%");
//
//        alertGrid = new Grid();
//        alertGrid.setWidth("100%");
//
//        Columns cols = new Columns();
//        cols.appendChild(new Column("Created Time"));
//        cols.appendChild(new Column("Room"));
//        cols.appendChild(new Column("Type"));
//        cols.appendChild(new Column("Select"));
//        alertGrid.appendChild(cols);
//
//        alertRows = new Rows();
//        alertGrid.appendChild(alertRows);
//
//        paging = new Paging();
//        paging.setPageSize(PAGE_SIZE);
//        paging.addEventListener("onPaging", e -> {
//            PagingEvent pe = (PagingEvent) e;
//            loadAlertData(pe.getActivePage());
//        });
//
//        noRecordLabel = new Label("No OverHeat Alerts");
//        noRecordLabel.setStyle("color:red;font-weight:bold;");
//
//        noRecordDiv = new Div();
//        noRecordDiv.setVisible(false);
//        noRecordDiv.appendChild(noRecordLabel);
//
//        wrapper.appendChild(alertGrid);
//        wrapper.appendChild(paging);
//        wrapper.appendChild(noRecordDiv);
//
//        return wrapper;
//    }
//
//    /* ================= DATA ================= */
//
//    private void loadAlertData(int pageNo) {
//
//        alertRows.getChildren().clear();
//        checkboxMap.clear();
//
//        String period = periodBox.getSelectedItem().getLabel();
//        int offset = pageNo * PAGE_SIZE;
//
//        int total = getTotalCount(period);
//        paging.setTotalSize(total);
//
//        if (total == 0) {
//            alertGrid.setVisible(false);
//            paging.setVisible(false);
//            noRecordDiv.setVisible(true);
//            return;
//        }
//
//        paging.setVisible(true);
//        alertGrid.setVisible(true);
//        noRecordDiv.setVisible(false);
//
//        String sql =
//            "SELECT t.tc_temperaturestatus_id, lt.name room, ts.name type, " +
//            "TO_CHAR(t.custom_timestamp,'DD/MM/YYYY HH12:MI AM') datetime " +
//            "FROM tc_temperaturestatus t " +
//            "JOIN m_locatortype lt ON lt.m_locatortype_id=t.m_locatortype_id " +
//            "JOIN tc_tempstatus ts ON ts.tc_tempstatus_id=t.tc_tempstatus_id " +
//            "WHERE t.ad_client_id=? " +
//            "AND ts.name='OverHeat' AND t.isacknowledge='N' " +
//            "AND ( (?='DAY' AND t.custom_timestamp::date=CURRENT_DATE) " +
//            "OR (?='WEEK' AND t.custom_timestamp>=date_trunc('week',CURRENT_DATE)) " +
//            "OR (?='MONTH' AND t.custom_timestamp>=date_trunc('month',CURRENT_DATE)) ) " +
//            "ORDER BY t.custom_timestamp DESC " +
//            "LIMIT ? OFFSET ?";
//
//        try (PreparedStatement ps = DB.prepareStatement(sql, null)) {
//
//            ps.setInt(1, clientId);
//            ps.setString(2, period);
//            ps.setString(3, period);
//            ps.setString(4, period);
//            ps.setInt(5, PAGE_SIZE);
//            ps.setInt(6, offset);
//
//            ResultSet rs = ps.executeQuery();
//
//            while (rs.next()) {
//                Row row = new Row();
//                row.appendChild(new Label(rs.getString("datetime")));
//                row.appendChild(new Label(rs.getString("room")));
//                row.appendChild(new Label(rs.getString("type")));
//
//                Checkbox cb = new Checkbox();
//                checkboxMap.put(cb, rs.getInt(1));
//                row.appendChild(cb);
//
//                alertRows.appendChild(row);
//            }
//
//        } catch (Exception e) {
//            Messagebox.show(e.getMessage());
//        }
//    }
//
//    private int getTotalCount(String period) {
//        String sql =
//            "SELECT COUNT(*) FROM tc_temperaturestatus t " +
//            "JOIN tc_tempstatus ts ON ts.tc_tempstatus_id=t.tc_tempstatus_id " +
//            "WHERE t.ad_client_id=? AND ts.name='OverHeat' AND t.isacknowledge='N'";
//
//        return DB.getSQLValue(null, sql, clientId);
//    }
//
//    /* ================= ACTION ================= */
//
//    @Override
//    public void onEvent(Event event) {
//        if (Events.ON_CLICK.equals(event.getName())) {
//            acknowledgeSelected();
//        }
//    }
//
//    private void acknowledgeSelected() {
//
//        List<Integer> ids = new ArrayList<>();
//        checkboxMap.forEach((cb, id) -> {
//            if (cb.isChecked()) ids.add(id);
//        });
//
//        if (ids.isEmpty()) {
//            Messagebox.show("Please select at least one record.");
//            return;
//        }
//
//        String inSql = String.join(",", Collections.nCopies(ids.size(), "?"));
//        String sql =
//            "UPDATE tc_temperaturestatus SET isacknowledge='Y', updated=now(), updatedby=? " +
//            "WHERE tc_temperaturestatus_id IN (" + inSql + ")";
//
//        try (PreparedStatement ps = DB.prepareStatement(sql, null)) {
//            int idx = 1;
//            ps.setInt(idx++, Env.getAD_User_ID(ctx));
//            for (Integer id : ids) ps.setInt(idx++, id);
//            ps.executeUpdate();
//        } catch (Exception e) {
//            Messagebox.show(e.getMessage());
//        }
//
//        int page = paging.getActivePage();
//        if (page > 0 && (page * PAGE_SIZE) >= getTotalCount(periodBox.getValue())) {
//            paging.setActivePage(page - 1);
//        }
//        loadAlertData(paging.getActivePage());
//    }
//
//    /* ================= DASHBOARD ================= */
//
//    @Override
//    public void refresh(ServerPushTemplate template) {
//        template.executeAsync(() -> loadAlertData(paging.getActivePage()));
//    }
//
//    @Override
//    public boolean isPooling() {
//        return true;
//    }
//
//    @Override
//    public boolean isLazy() {
//        return true;
//    }
//}



package org.adempiere.webui.dashboard;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

import org.adempiere.webui.component.Button;
import org.adempiere.webui.util.ServerPushTemplate;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.*;

/**
* Dashboard gadget: Temperature Over Heat Alert
* @author Chirag Rathi
* @date Dec 22, 2025
*/

public class DPMenuTree extends DashboardPanel implements EventListener<Event> {

    private static final long serialVersionUID = 1L;

    private Combobox periodBox;
    private Button btnAcknowledge;

    private Grid alertGrid;
    private Rows alertRows;
    
    private Label noRecordLabel;
    private Div noRecordDiv;

    private Properties ctx = Env.getCtx();
    private int clientId = Env.getAD_Client_ID(ctx);

    // Checkbox → tc_temperaturestatus_id
    private Map<Checkbox, Integer> checkboxMap = new HashMap<>();

    public DPMenuTree() {
        setSclass("activities-box");
        appendChild(createUI());
        loadAlertData();
    }

    private Vbox createUI() {

        Vbox root = new Vbox();
        root.setSpacing("8px");

        root.appendChild(createHeader());
        root.appendChild(createGrid());

        return root;
    }

    private Hbox createHeader() {

        Hbox hbox = new Hbox();
        hbox.setSpacing("10px");
        hbox.setAlign("center");

        periodBox = new Combobox();
        periodBox.setWidth("100px");

        periodBox.appendChild(new Comboitem("DAY"));
        periodBox.appendChild(new Comboitem("WEEK"));
        periodBox.appendChild(new Comboitem("MONTH"));
        periodBox.setSelectedIndex(0);

        periodBox.addEventListener(Events.ON_CHANGE, e -> loadAlertData());

        btnAcknowledge = new Button("Acknowledge");
        btnAcknowledge.setIconSclass("z-icon-check");
        btnAcknowledge.addEventListener(Events.ON_CLICK, this);

        hbox.appendChild(periodBox);
        hbox.appendChild(btnAcknowledge);

        return hbox;
    }

    @SuppressWarnings("deprecation")
	private Component createGrid() {
    	
    	Vbox wrapper = new Vbox();
        wrapper.setWidth("100%");
        wrapper.setAlign("center");

        alertGrid = new Grid();
        alertGrid.setWidth("100%");
        alertGrid.setVisible(true);

        Columns cols = new Columns();
        cols.appendChild(new Column("Created Time"));
        cols.appendChild(new Column("Room"));
        cols.appendChild(new Column("Type"));
        cols.appendChild(new Column("Acknowledge option"));

        alertGrid.appendChild(cols);

        alertRows = new Rows();
        alertGrid.appendChild(alertRows);

        noRecordLabel = new Label("No OverHeat Alerts");
        noRecordLabel.setStyle(
            "color:red;" +
            "font-size:14px;" +
            "font-weight:bold;"
        );

        noRecordDiv = new Div();
        noRecordDiv.setWidth("100%");
        noRecordDiv.setAlign("center");
        noRecordDiv.setVisible(false);
        noRecordDiv.appendChild(noRecordLabel);

        wrapper.appendChild(alertGrid);
        wrapper.appendChild(noRecordDiv);

        return wrapper;
    }

    private void loadAlertData() {

        alertRows.getChildren().clear();
        checkboxMap.clear();

        String period = periodBox.getSelectedItem().getLabel();

        String sql =
            "SELECT t.tc_temperaturestatus_id, " +
            "       lt.name AS room, " +
            "       ts.name AS type, " +
            "       TO_CHAR(t.custom_timestamp,'DD/MM/YYYY HH12:MI AM') AS datetime " +
            "FROM tc_temperaturestatus t " +
            "JOIN m_locatortype lt ON lt.m_locatortype_id = t.m_locatortype_id " +
            "JOIN tc_tempstatus ts ON ts.tc_tempstatus_id = t.tc_tempstatus_id " +
            "WHERE t.ad_client_id=? " +
            "AND ts.name='OverHeat' " +
            "AND t.isacknowledge='N' " +
            "AND ( " +
            " (?='DAY' AND t.custom_timestamp::date = CURRENT_DATE) OR " +
            " (?='WEEK' AND t.custom_timestamp >= date_trunc('week', CURRENT_DATE) " +
            "               AND t.custom_timestamp < date_trunc('week', CURRENT_DATE) + INTERVAL '1 week') OR " +
            " (?='MONTH' AND t.custom_timestamp >= date_trunc('month', CURRENT_DATE) " +
            "                AND t.custom_timestamp < date_trunc('month', CURRENT_DATE) + INTERVAL '1 month') " +
            ") " +
            "ORDER BY t.custom_timestamp DESC";

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = DB.prepareStatement(sql, null);
            pstmt.setInt(1, clientId);
            pstmt.setString(2, period);
            pstmt.setString(3, period);
            pstmt.setString(4, period);

            rs = pstmt.executeQuery();
            
            boolean hasData = false;
            while (rs.next()) {
            	hasData = true;

                int id = rs.getInt("tc_temperaturestatus_id");

                Row row = new Row();
                row.appendChild(new Label(rs.getString("datetime")));
                row.appendChild(new Label(rs.getString("room")));
                row.appendChild(new Label(rs.getString("type")));

                Checkbox cb = new Checkbox();
                checkboxMap.put(cb, id);
                row.appendChild(cb);

                alertRows.appendChild(row);
            }
            
            alertGrid.setVisible(hasData);
            noRecordDiv.setVisible(!hasData);

        } catch (Exception e) {
            e.printStackTrace();
            Messagebox.show("Error loading temperature alerts:\n" + e.getMessage());
        } finally {
            DB.close(rs, pstmt);
        }
    }

    @Override
    public void onEvent(Event event) {
        if (Events.ON_CLICK.equals(event.getName())) {
            acknowledgeSelected();
        }
    }

    private void acknowledgeSelected() {

        List<Integer> ids = new ArrayList<>();

        for (Map.Entry<Checkbox, Integer> e : checkboxMap.entrySet()) {
            if (e.getKey().isChecked()) {
                ids.add(e.getValue());
            }
        }

        if (ids.isEmpty()) {
            Messagebox.show("Please select at least one alert.");
            return;
        }
        
        StringBuilder inClause = new StringBuilder();
        for (int i = 0; i < ids.size(); i++) {
            if (i > 0) inClause.append(",");
            inClause.append("?");
        }

        String sql =
                "UPDATE tc_temperaturestatus " +
                "SET isacknowledge='Y', updated=now(), updatedby=? " +
                "WHERE tc_temperaturestatus_id IN (" + inClause + ")";

            PreparedStatement pstmt = null;

            try {
                pstmt = DB.prepareStatement(sql, null);

                int index = 1;
                pstmt.setInt(index++, Env.getAD_User_ID(ctx));

                for (Integer id : ids) {
                    pstmt.setInt(index++, id);
                }

                pstmt.executeUpdate();

            } catch (Exception e) {
                e.printStackTrace();
                Messagebox.show("Failed to acknowledge alerts:\n" + e.getMessage());
            } finally {
                DB.close(pstmt);
            }

            // Reload grid
            loadAlertData();
        }

    @Override
    public void refresh(ServerPushTemplate template) {
        template.executeAsync(this::loadAlertData);
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
//import java.util.*;
//
//import org.adempiere.webui.component.Button;
//import org.adempiere.webui.desktop.IDesktop;
//import org.adempiere.webui.session.SessionManager;
//import org.adempiere.webui.util.ServerPushTemplate;
//import org.compiere.util.DB;
//import org.compiere.util.Env;
//import org.zkoss.zk.ui.event.Event;
//import org.zkoss.zk.ui.event.EventListener;
//import org.zkoss.zk.ui.event.Events;
//import org.zkoss.zul.*;
//
//public class DPMenuTree extends DashboardPanel
//        implements EventListener<Event> {
//
//    private static final long serialVersionUID = 1L;
//
//    private Combobox periodBox;
//    private Button btnAcknowledge;
//
//    private Grid alertGrid;
//    private Rows alertRows;
//    
//    private Properties ctx = Env.getCtx();
//    private int clientId = Env.getAD_Client_ID(ctx);
//
//    // Checkbox → tc_temperaturestatus_id
//    private Map<Checkbox, Integer> checkboxMap = new HashMap<>();
//
//    public DPMenuTree() {
//        this.setSclass("activities-box");
//        this.appendChild(createUI());
//        loadAlertData();
//    }
//
//    /* ================= UI ================= */
//
//    private Vbox createUI() {
//
//        Vbox root = new Vbox();
//        root.setSpacing("8px");
//
//        root.appendChild(createHeader());
//        root.appendChild(createGrid());
//
//        return root;
//    }
//
//    private Hbox createHeader() {
//
//        Hbox hbox = new Hbox();
//        hbox.setSpacing("10px");
//        hbox.setAlign("center");
//
//        periodBox = new Combobox();
//        periodBox.setReadonly(true);
//        periodBox.setWidth("100px");
//
//        periodBox.appendChild(new Comboitem("DAY"));
//        periodBox.appendChild(new Comboitem("WEEK"));
//        periodBox.appendChild(new Comboitem("MONTH"));
//        periodBox.setSelectedIndex(0);
//
//        periodBox.addEventListener(Events.ON_CHANGE, e -> loadAlertData());
//
//        btnAcknowledge = new Button("Acknowledge");
//        btnAcknowledge.setIconSclass("z-icon-check");
//        btnAcknowledge.addEventListener(Events.ON_CLICK, this);
//
//        hbox.appendChild(periodBox);
//        hbox.appendChild(btnAcknowledge);
//
//        return hbox;
//    }
//
//    private Grid createGrid() {
//
//        alertGrid = new Grid();
//        alertGrid.setWidth("100%");
//        alertGrid.setEmptyMessage("No OverHeat Alerts");
//
//        Columns cols = new Columns();
//        cols.appendChild(new Column("Created Time"));
//        cols.appendChild(new Column("Room"));
//        cols.appendChild(new Column("Type"));
//        cols.appendChild(new Column("Status"));
//        cols.appendChild(new Column("✔"));
//
//        alertGrid.appendChild(cols);
//
//        alertRows = new Rows();
//        alertGrid.appendChild(alertRows);
//
//        return alertGrid;
//    }
//
//    /* ================= DATA ================= */
//
//    private void loadAlertData() {
//
//        alertRows.getChildren().clear();
//        checkboxMap.clear();
//
//        String period = periodBox.getSelectedItem().getLabel();
//        PreparedStatement pstmt = null;
//    	ResultSet rs = null;
//        try {
//        String sql =
//            "SELECT t.tc_temperaturestatus_id, " +
//            "       lt.name AS room, " +
//            "       ts.name AS type, " +
//            "       TO_CHAR(t.custom_timestamp,'DD/MM/YYYY HH12:MI AM') AS datetime " +
//            "FROM tc_temperaturestatus t " +
//            "JOIN m_locatortype lt ON lt.m_locatortype_id = t.m_locatortype_id " +
//            "JOIN tc_tempstatus ts ON ts.tc_tempstatus_id = t.tc_tempstatus_id " +
//            "WHERE t.ad_client_id=? " +
//            "AND ts.name='OverHeat' " +
//            "AND t.isacknowledge='N' " +
//            "AND ( " +
//            " (?='DAY' AND t.custom_timestamp::date = CURRENT_DATE) OR " +
//            " (?='WEEK' AND t.custom_timestamp >= date_trunc('week', CURRENT_DATE) " +
//            "               AND t.custom_timestamp < date_trunc('week', CURRENT_DATE) + INTERVAL '1 week') OR " +
//            " (?='MONTH' AND t.custom_timestamp >= date_trunc('month', CURRENT_DATE) " +
//            "                AND t.custom_timestamp < date_trunc('month', CURRENT_DATE) + INTERVAL '1 month') " +
//            ") " +
//            "ORDER BY t.updated DESC, t.custom_timestamp DESC";
//        
//        pstmt = DB.prepareStatement(sql, null);
//        pstmt.setInt(1, clientId);
//        pstmt.setString(2, period);
//        pstmt.setString(3, period);
//        pstmt.setString(4, period);
//
//        rs = pstmt.executeQuery();
//
//        boolean hasData = false;
//        
//        while (rs.next()) {
//            hasData = true;
//            Row row = new Row();
//
//            row.appendChild(new Label(rs.getString("device_name")));
//            row.appendChild(new Label(rs.getString("temperature")));
//            row.appendChild(new Label(rs.getString("humidity")));
//            row.appendChild(new Label(rs.getString("humidity_status")));
//            row.appendChild(new Label(rs.getString("battery_percentage")));
//            row.appendChild(new Label(rs.getString("custom_timestamp")));
//
//            alertRows.appendChild(row);
//        }
//
//        if (!hasData) {
//        	Hbox hbox = new Hbox();
//        	hbox.setPack("center");
//        	hbox.setWidth("100%");
//
//        	Label temRecords = new Label("No Records Found");
//        	temRecords.setStyle(
//        	    "font-size:14px;" +
//        	    "font-weight:bold;" +
//        	    "color:red;"
//        	);
//
//        	hbox.appendChild(temRecords);
//        	thContainer.appendChild(hbox);
//            return;
//        }
//        
//    }catch (Exception e) {
//        e.printStackTrace();
//    }finally {
//        DB.close(rs, pstmt);
//    }
//    }
//
//    @Override
//    public void onEvent(Event event) {
//
//        if (Events.ON_CLICK.equals(event.getName())) {
//            acknowledgeSelected();
//        }
//    }
//
//    private void acknowledgeSelected() {
//
//        List<Integer> ids = new ArrayList<>();
//
//        for (Map.Entry<Checkbox, Integer> e : checkboxMap.entrySet()) {
//            if (e.getKey().isChecked()) {
//                ids.add(e.getValue());
//            }
//        }
//
//        if (ids.isEmpty()) {
//            Messagebox.show("Please select at least one alert.");
//            return;
//        }
//
//        String sql =
//            "UPDATE tc_temperaturestatus " +
//            "SET isacknowledge='Y', updated=now(), updatedby=? " +
//            "WHERE tc_temperaturestatus_id = ANY (?)";
//
//        DB.executeUpdateEx(
//            sql,
//            new Object[] {
//                Env.getAD_User_ID(Env.getCtx()),
//                ids.toArray(new Integer[0])
//            },
//            null
//        );
//
//        // Reload grid
//        loadAlertData();
//    }
//
//    /* ================= DASHBOARD ================= */
//
//    @Override
//    public void refresh(ServerPushTemplate template) {
//        template.executeAsync(this::loadAlertData);
//    }
//
//    @Override
//    public boolean isPooling() {
//        return true;
//    }
//
//    @Override
//    public boolean isLazy() {
//        return true;
//    }
//}



//package org.adempiere.webui.dashboard;
//
//import org.adempiere.webui.component.Button;
//import org.adempiere.webui.desktop.IDesktop;
//import org.adempiere.webui.session.SessionManager;
//import org.adempiere.webui.theme.ThemeManager;
//import org.adempiere.webui.util.ServerPushTemplate;
//import org.compiere.util.DB;
//import org.compiere.util.Env;
//import org.compiere.util.Msg;
//import org.compiere.util.Util;
//import org.zkoss.zk.ui.event.Event;
//import org.zkoss.zk.ui.event.EventListener;
//import org.zkoss.zk.ui.event.EventQueues;
//import org.zkoss.zk.ui.event.Events;
//import org.zkoss.zul.*;
//
/////**
////* Dashboard gadget: Temperature Over Heat Alert
////* @author Chirag Rathi
////* @date Dec 19, 2025
////*/
//
//public class DPMenuTree extends DashboardPanel implements EventListener<Event> {
//	
//	private static final long serialVersionUID = 1L;
//	
//	    private Button btnAlert;
//	    private Combobox periodBox;
//	    private String labelA;
//	    private int noofAlert;
//	
//	    public DPMenuTree() {
//	        super();
//	        this.setSclass("activities-box");
//	        this.setStyle("overflow: visible;"); // 🔥 IMPORTANT
//	        this.appendChild(createPanel());
//	    }
//	
//	    private Box createPanel() {
//	
//	        Vbox root = new Vbox();
//	        root.setSpacing("6px");
//	        root.setStyle("overflow: visible;");
//	
//	        Hbox hbox = new Hbox();
//	        hbox.setSpacing("8px");
//	        hbox.setAlign("center");
//	        hbox.setStyle("overflow: visible;");
//	
//	        periodBox = new Combobox();
//	        periodBox.setWidth("90px");
//	        periodBox.setReadonly(false);
//	        periodBox.setDisabled(false);
//	        periodBox.setButtonVisible(true);
//	        periodBox.setStyle("display:inline-block; overflow:visible;");
//	
//	        periodBox.appendChild(createItem("DAY", "day"));
//	        periodBox.appendChild(createItem("MONTH", "month"));
//	        periodBox.appendChild(createItem("YEAR", "year"));
//	        periodBox.setSelectedIndex(0);
//	        
//	        periodBox.addEventListener(Events.ON_CHANGE, e -> {
//	            
//	        	String period = getSelectedPeriod();
//	            noofAlert = getOverHeatCount(period);
//	
//	            updateUI();
//	        });
//	
//	        hbox.appendChild(periodBox);
//	
//	        btnAlert = new Button();
//	        labelA = Util.cleanAmp(Msg.translate(Env.getCtx(), "AlertCount"));
//	        btnAlert.setLabel(labelA + " : 0");
//	        btnAlert.setTooltiptext("OverHeat Temperature Alerts");
//	        btnAlert.setImage(ThemeManager.getThemeResource("images/tem.png"));
//	
//	        int menuId = DB.getSQLValue(
//	            null,
//	            "SELECT AD_Menu_ID FROM AD_Menu WHERE Name='Temperature' AND IsSummary='N'"
//	        );
//	        btnAlert.setName(String.valueOf(menuId));
//	        btnAlert.addEventListener(Events.ON_CLICK, this);
//	
//	        hbox.appendChild(btnAlert);
//	
//	        root.appendChild(hbox);
//	        return root;
//	    }
//	
//	    private Comboitem createItem(String label, String value) {
//	        Comboitem item = new Comboitem(label);
//	        item.setValue(value);
//	        return item;
//	    }
//	    
//	    @Override
//	    public void refresh(ServerPushTemplate template) {
//	
//	    	String period = getSelectedPeriod();
//	        int alert = getOverHeatCount(getSelectedPeriod());
//	
//	        if (noofAlert != alert) {
//	            noofAlert = alert;
//	            template.executeAsync(this);
//	        }
//	    }
//	
//	    @Override
//	    public void updateUI() {
//	
//	        btnAlert.setLabel(labelA + " : " + noofAlert);
//	
//	        if (noofAlert > 0) {
//	            btnAlert.setSclass("z-button temp-alert-red");
//	        } else {
//	            btnAlert.setSclass(null);
//	        }
//	
//	        EventQueues.lookup(
//	            IDesktop.ACTIVITIES_EVENT_QUEUE, true
//	        ).publish(new Event(
//	            IDesktop.ON_ACTIVITIES_CHANGED_EVENT,
//	            null,
//	            noofAlert
//	        ));
//	    }
//	
//	    @Override
//	    public void onEvent(Event event) {
//	
//	        if (Events.ON_CLICK.equals(event.getName())) {
//	
//	            String period = getSelectedPeriod();
//	
//	            // Pass period to window
//	            Env.setContext(Env.getCtx(), "#TEMP_PERIOD", period);
//	
//	            int menuId = Integer.parseInt(btnAlert.getName());
//	            if (menuId > 0) {
//	                SessionManager.getAppDesktop().onMenuSelected(menuId);
//	            }
//	        }
//	    }
//	
//	    private String getSelectedPeriod() {
//	        Comboitem item = periodBox.getSelectedItem();
//	        return item != null ? item.getValue().toString() : "day";
//	    }
//
//	    private int getOverHeatCount(String period) {
//	
//	        String sql =
//	            "SELECT COUNT(*) " +
//	            "FROM tc_temperaturestatus t " +
//	            "JOIN tc_tempstatus ts ON ts.tc_tempstatus_id=t.tc_tempstatus_id " +
//	            "WHERE ts.name='OverHeat' " +
//	            "AND t.ad_client_id=? " +
//	            "AND t.created >= CASE lower(?) " +
//	            " WHEN 'day' THEN CURRENT_DATE " +
//	            " WHEN 'month' THEN date_trunc('month', CURRENT_DATE) " +
//	            " WHEN 'year' THEN date_trunc('year', CURRENT_DATE) END " +
//	            "AND t.created < CASE lower(?) " +
//	            " WHEN 'day' THEN CURRENT_DATE + INTERVAL '1 day' " +
//	            " WHEN 'month' THEN date_trunc('month', CURRENT_DATE) + INTERVAL '1 month' " +
//	            " WHEN 'year' THEN date_trunc('year', CURRENT_DATE) + INTERVAL '1 year' END";
//	
//	        return DB.getSQLValue(
//	            null,
//	            sql,
//	            Env.getAD_Client_ID(Env.getCtx()),
//	            period,
//	            period
//	        );
//	    }
//	
//	    @Override
//	    public boolean isPooling() {
//	        return true;
//	    }
//	
//	    @Override
//	    public boolean isLazy() {
//	        return true;
//	    }
//}



///******************************************************************************
// * Copyright (C) 2012 Elaine Tan                                              *
// * Copyright (C) 2012 Trek Global
// * This program is free software; you can redistribute it and/or modify it    *
// * under the terms version 2 of the GNU General Public License as published   *
// * by the Free Software Foundation. This program is distributed in the hope   *
// * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
// * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
// * See the GNU General Public License for more details.                       *
// * You should have received a copy of the GNU General Public License along    *
// * with this program; if not, write to the Free Software Foundation, Inc.,    *
// * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
// *****************************************************************************/
//
//package org.adempiere.webui.dashboard;
//
//import org.adempiere.webui.panel.MenuTreePanel;
//
///**
// * Dashboard gadget: Menu Tree
// * @author Elaine
// * @date July 31, 2012
// */
//public class DPMenuTree extends DashboardPanel {
//	/**
//	 * generated serial id
//	 */
//	private static final long serialVersionUID = -3095921038206382907L;
//	
//	private MenuTreePanel menuTreePanel;
//
//	/**
//	 * Default constructor
//	 */
//	public DPMenuTree()
//	{
//		super();
//		
//		menuTreePanel = new MenuTreePanel(this);
//	}
//	
//	/**
//	 * @return {@link MenuTreePanel}
//	 */
//	public MenuTreePanel getMenuTreePanel()
//	{
//		return menuTreePanel;
//	}
//	
//}
