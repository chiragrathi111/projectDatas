package org.adempiere.webui.dashboard;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

import org.compiere.util.DB;
import org.compiere.util.Env;

import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.*;

public class DPRunningJobs extends DashboardPanel {

    private static final long serialVersionUID = 1L;

    private Combobox roomCombo;
    private Div thContainer;
    private Div lightContainer;

    private Properties ctx = Env.getCtx();
    private int clientId = Env.getAD_Client_ID(ctx);

    public DPRunningJobs() {
    	
    	 setSclass("iot-dashboard-card");
    	    setWidth("100%");

    	    // 🔹 Container for label + dropdown
    	    Hbox roomBox = new Hbox();
    	    roomBox.setSpacing("8px");
    	    roomBox.setAlign("center");
    	    roomBox.setStyle("margin-bottom:10px;");

    	    // 🔹 Label
    	    Label roomLabel = new Label("Growth Room:");
    	    roomLabel.setStyle("font-weight:bold; font-size:16px;");

    	    // 🔹 Combobox
    	    roomCombo = new Combobox();
    	    roomCombo.setPlaceholder("Select Room");
    	    roomCombo.setWidth("300px");
    	    roomCombo.setAutocomplete(false);

    	    loadRooms();
    	    roomCombo.addEventListener(Events.ON_CHANGE, e -> loadData());

    	    // ✅ IMPORTANT: add label first, then dropdown
    	    roomBox.appendChild(roomLabel);
    	    roomBox.appendChild(roomCombo);

    	    // ✅ IMPORTANT: add roomBox, not roomCombo
    	    appendChild(roomBox);

    	    thContainer = new Div();
    	    thContainer.setSclass("dashboard-section");

    	    lightContainer = new Div();
    	    lightContainer.setSclass("dashboard-section");

    	    appendChild(thContainer);
    	    appendChild(lightContainer);

    	    loadData();

//        setSclass("iot-dashboard-card");
//        setWidth("100%");
//        
//        Hbox roomBox = new Hbox();
//        roomBox.setSpacing("8px");
//        roomBox.setAlign("center");
//
//        Label roomLabel = new Label("Growth Room: ");
//        roomLabel.setStyle("font-weight:bold; font-size:13px;");
//
//        roomCombo = new Combobox();
//        roomCombo.setPlaceholder("Select Room");
//        roomCombo.setWidth("300px");
////        roomCombo.setReadonly(true);
//        roomCombo.setAutocomplete(false);
//
//        loadRooms();
//        roomCombo.addEventListener(Events.ON_CHANGE, e -> loadData());
//
//        appendChild(roomCombo);
//
//        thContainer = new Div();
//        thContainer.setSclass("dashboard-section");
//
//        lightContainer = new Div();
//        lightContainer.setSclass("dashboard-section");
//
//        appendChild(thContainer);
//        appendChild(lightContainer);
//
//        loadData();
    }

    private void loadRooms() {
    	PreparedStatement pstmt = null;
    	ResultSet rs = null;
        try {
            String sql =
                "SELECT m_locatortype_id, name " +
                "FROM m_locatortype " +
                "WHERE ad_client_id=? AND isactive='Y' AND description='Room' " +
                "ORDER BY name";

            pstmt = DB.prepareStatement(sql, null);
            pstmt.setInt(1, clientId);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                Comboitem item = new Comboitem(rs.getString("name"));
                item.setValue(rs.getInt("m_locatortype_id"));
                roomCombo.appendChild(item);
            }

            if (roomCombo.getItemCount() > 0) {
                roomCombo.setSelectedIndex(0);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            DB.close(rs, pstmt);
        }
    }

    private void loadData() {
    	 Comboitem selected = roomCombo.getSelectedItem();
    	    if (selected == null)
    	        return;

    	    int roomId = (Integer) selected.getValue();

        loadTemperatureTable(roomId);
        loadLightTable(roomId);
    }

    private void loadTemperatureTable(int roomId) {

        thContainer.getChildren().clear();
        Label title = new Label("🌡️ 💧 Temperature & Humidity");
        title.setStyle("font-size:15px; font-weight:bold;");
        thContainer.appendChild(title);
//        thContainer.setStyle("font-weight:bold; font-size:24px;");
        thContainer.appendChild(new Separator());

        Grid grid = createGrid(
            "Device", "Temperature", "Humidity",
            "Temp Status", "Humidity Status", "Battery", "Time"
        );

        Rows rows = new Rows();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            String sql =
                "SELECT lt.name AS room_name,ts.tc_devicedata_id,ts.name AS device_name,tts.name AS temp_status,ts.humiditystatus AS humidity_status,\r\n"
                + "ts.temperature,ts.humidity,ts.battery_percentage,TO_CHAR(ts.custom_timestamp, 'DD/MM/YYYY HH12:MI AM') AS custom_timestamp\r\n"
                + "FROM (SELECT DISTINCT ON (ts.tc_devicedata_id) ts.* FROM   adempiere.tc_temperaturestatus ts\r\n"
                + "WHERE ts.m_locatortype_id = ? AND ts.ad_client_id = ? ORDER BY ts.tc_devicedata_id,ts.updated DESC,\r\n"
                + "ts.custom_timestamp DESC) ts\r\n"
                + "JOIN adempiere.m_locatortype lt ON lt.m_locatortype_id = ts.m_locatortype_id\r\n"
                + "JOIN adempiere.tc_tempstatus tts ON tts.tc_tempstatus_id = ts.tc_tempstatus_id\r\n"
                + "ORDER BY ts.tc_devicedata_id;";

            pstmt = DB.prepareStatement(sql, null);
            pstmt.setInt(1, roomId);
            pstmt.setInt(2, clientId);

            rs = pstmt.executeQuery();

            boolean hasData = false;

            while (rs.next()) {
                hasData = true;
                Row row = new Row();

                row.appendChild(new Label(rs.getString("device_name")));
                row.appendChild(new Label(rs.getString("temperature")));
                row.appendChild(new Label(rs.getString("humidity")));
                row.appendChild(coloredStatus(rs.getString("temp_status")));
                row.appendChild(new Label(rs.getString("humidity_status")));
                row.appendChild(new Label(rs.getString("battery_percentage")));
                row.appendChild(new Label(rs.getString("custom_timestamp")));

                rows.appendChild(row);
            }

            if (!hasData) {
            	Hbox hbox = new Hbox();
            	hbox.setPack("center");
            	hbox.setWidth("100%");

            	Label temRecords = new Label("No Records Found");
            	temRecords.setStyle(
            	    "font-size:14px;" +
            	    "font-weight:bold;" +
            	    "color:red;"
            	);

            	hbox.appendChild(temRecords);
            	thContainer.appendChild(hbox);
                return;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            DB.close(rs, pstmt);
        }

        grid.appendChild(rows);
        thContainer.appendChild(grid);
    }

    private void loadLightTable(int roomId) {

        lightContainer.getChildren().clear();
        Label lightTitle = new Label("💡 Light Status");
        lightTitle.setStyle("font-size:15px; font-weight:bold;");
        lightContainer.appendChild(lightTitle);
        lightContainer.appendChild(new Separator());

        Grid grid = createGrid(
            "Device", "Light Status", "Duration", "Ampere", "Time"
        );

        Rows rows = new Rows();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            String sql =
                "SELECT lt.name AS room_name,l.tc_devicedata_id,l.name AS device_name,ls.name AS light_status,l.lighton AS time,l.appearance AS ampere,\r\n"
                + "TO_CHAR(l.custom_timestamp,'DD/MM/YYYY HH12:MI AM') AS custom_timestamp FROM (SELECT DISTINCT ON (l.tc_devicedata_id) l.*\r\n"
                + "FROM adempiere.tc_light l WHERE l.m_locatortype_id = ? AND l.ad_client_id  = ? \r\n"
                + "ORDER BY l.tc_devicedata_id,l.updated DESC ) l\r\n"
                + "JOIN adempiere.m_locatortype lt ON lt.m_locatortype_id = l.m_locatortype_id\r\n"
                + "JOIN adempiere.tc_lightstatus ls ON ls.tc_lightstatus_id = l.tc_lightstatus_id\r\n"
                + "ORDER BY l.tc_devicedata_id;";

            pstmt = DB.prepareStatement(sql, null);
            pstmt.setInt(1, roomId);
            pstmt.setInt(2, clientId);

            rs = pstmt.executeQuery();

            boolean hasData = false;

            while (rs.next()) {
                hasData = true;
                Row row = new Row();

                row.appendChild(new Label(rs.getString("device_name")));
                row.appendChild(coloredLight(rs.getString("light_status")));
                row.appendChild(new Label(rs.getString("time")));
                row.appendChild(new Label(rs.getString("ampere")));
                row.appendChild(new Label(rs.getString("custom_timestamp")));

                rows.appendChild(row);
            }

            if (!hasData) {
            	Hbox hbox1 = new Hbox();
            	hbox1.setPack("center");
            	hbox1.setWidth("100%");

            	Label temRecords1 = new Label("No Records Found");
            	temRecords1.setStyle(
            	    "font-size:14px;" +
            	    "font-weight:bold;" +
            	    "color:red;"
            	);

            	hbox1.appendChild(temRecords1);
            	lightContainer.appendChild(hbox1);
                return;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            DB.close(rs, pstmt);
        }

        grid.appendChild(rows);
        lightContainer.appendChild(grid);
    }

    private Grid createGrid(String... headers) {
        Grid grid = new Grid();
        grid.setWidth("100%");
        grid.setSclass("modern-grid");

        Columns cols = new Columns();
        for (String h : headers) {
            Column c = new Column(h);
            c.setStyle("font-weight:bold;");
            cols.appendChild(c);
        }

        grid.appendChild(cols);
        return grid;
    }
    
    private Label coloredStatus(String status) {
        Label l = new Label(status);

        if ("OverHeat".equalsIgnoreCase(status)) {
            l.setStyle("color:#D32F2F; font-weight:bold;");   // Red
        } 
        else if ("OverCool".equalsIgnoreCase(status)) {
            l.setStyle("color:#1976D2; font-weight:bold;");   // Blue
        } 
        else {
            l.setStyle("color:#2E7D32;");                     // Green (Normal)
        }
        return l;
    }

    private Label coloredLight(String status) {
        Label l = new Label(status);
        if ("ON".equalsIgnoreCase(status)) {
            l.setStyle("color:green;font-weight:bold;");
        } else {
            l.setStyle("color:gray;");
        }
        return l;
    }
}