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

public class DPIOTSensorTH extends DashboardPanel implements EventListener<Event> {

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

    public DPIOTSensorTH() {
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
        cols.appendChild(new Column("Device Name"));
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
            "       lt.name AS room, t.name AS deviceName," +
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
                row.appendChild(new Label(rs.getString("deviceName")));
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
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Properties;
//import org.adempiere.webui.component.Label;
//import org.adempiere.webui.theme.ITheme;
//import org.adempiere.webui.util.ServerPushTemplate;
//import org.compiere.util.DB;
//import org.compiere.util.Env;
//import org.zkoss.zk.ui.event.Event;
//import org.zkoss.zk.ui.event.EventListener;
//import org.zkoss.zul.Div;
//
//public class DPIOTSensorTH extends DashboardPanel implements EventListener<Event> {
//
//    private static final long serialVersionUID = 1L;
//
//    private List<RoomData> roomDataList = new ArrayList<>();
//    private Label labelFirstRecordDate; // New label for first record's date
//
//    public DPIOTSensorTH() {
//        super();
//        this.setSclass("activities-box");   
//        this.appendChild(createDatePanel()); // Add date panel at the bottom
//        initOptions();
//        this.appendChild(createActivitiesPanel());
//    }
//
//    private Div createActivitiesPanel() {
//        Div mainDiv = new Div();
//        mainDiv.setSclass(ITheme.WARE_HOUSE_DATA_WIDGET);
//
//        for (RoomData roomData : roomDataList) {
//            Div roomBox = new Div();
//            roomBox.setSclass(ITheme.DASHBOARD_WIDGET_Text_COUNT_CONT);
//
//            // Label for room type
//            Label roomTypeLabel = new Label();
//            roomTypeLabel.setSclass(ITheme.DASHBOARD_WIDGET_LABELS);
//            roomTypeLabel.setValue(roomData.getRoomType());
//            roomTypeLabel.setStyle("font-weight: bold; font-size: 25px;");
//            roomBox.appendChild(roomTypeLabel);
//
//            // Label for temperature
//            Label tempLabel = new Label();
//            tempLabel.setSclass(ITheme.DASHBOARD_WIDGET_COUNT);
//            tempLabel.setSclass("small-font"); // Use small-font for records
//            tempLabel.setValue("Temperature: " + roomData.getTemperature() + "°C");
//            roomBox.appendChild(tempLabel);
//
//            // Label for humidity
//            Label humidityLabel = new Label();
//            humidityLabel.setSclass(ITheme.DASHBOARD_WIDGET_COUNT);
//            humidityLabel.setSclass("small-font"); // Use small-font for records
//            humidityLabel.setValue("Humidity: " + roomData.getHumidity() + "%");
//            roomBox.appendChild(humidityLabel);
//
//            // Label for date
//            Label dateLabel = new Label();
//            dateLabel.setSclass(ITheme.DASHBOARD_WIDGET_COUNT);
//            dateLabel.setSclass("small-font"); // Use small-font for records
//            dateLabel.setValue("Date: " + roomData.getDate());
//            roomBox.appendChild(dateLabel);
//            
//            Label lightLabel = new Label();
//            lightLabel.setSclass(ITheme.DASHBOARD_WIDGET_COUNT);
//            lightLabel.setSclass("small-font"); // Use small-font for records
//            lightLabel.setValue("Light: " + roomData.getLight() + "H");
//            roomBox.appendChild(lightLabel);
//
//            mainDiv.appendChild(roomBox);
//        }
//
//        return mainDiv;
//    }
//    private Div createDatePanel() {
//        Div div = new Div();
//        div.setSclass(ITheme.WARE_HOUSE_DATA_WIDGET);
//
//        labelFirstRecordDate = new Label();
//        labelFirstRecordDate.setSclass(ITheme.DASHBOARD_WIDGET_COUNT);
//        labelFirstRecordDate.setStyle("font-weight: bold; font-size: 25px;");
////        labelFirstRecordDate.setSclass("small-font");
//        div.appendChild(labelFirstRecordDate);
//
//        return div;
//    }
//
//    private void initOptions() {
//        Properties ctx = Env.getCtx();
//        int clientId = Env.getAD_Client_ID(ctx);
//        PreparedStatement pstmt = null;
//        ResultSet rs = null;
//        boolean hasRecord = false;
//        try {
//            String sql = "SELECT \n"
//            		+ "    lt.name AS RoomType, \n"
//            		+ "    ts.temperature, \n"
//            		+ "    ts.humidity,\n"
//            		+ "    l.lighton AS time,\n"
//            		+ "    ls.name AS lightstatus,\n"
//            		+ "    ts.updated::DATE AS updated_date,\n"
//            		+ "    TO_CHAR(ts.updated, 'DD-MM-YYYY') AS date\n"
//            		+ "FROM (\n"
//            		+ "    SELECT DISTINCT ON (ts.m_locatortype_id) *\n"
//            		+ "    FROM adempiere.tc_temperaturestatus ts\n"
//            		+ "    WHERE ts.ad_client_id = "+clientId+"\n"
//            		+ "    ORDER BY ts.m_locatortype_id, ts.updated DESC\n"
//            		+ ") ts\n"
//            		+ "JOIN adempiere.m_locatortype lt ON lt.m_locatortype_id = ts.m_locatortype_id\n"
//            		+ "JOIN (\n"
//            		+ "    SELECT DISTINCT ON (l.m_locatortype_id) *\n"
//            		+ "    FROM adempiere.tc_light l\n"
//            		+ "    ORDER BY l.m_locatortype_id, l.updated DESC\n"
//            		+ ") l ON l.m_locatortype_id = lt.m_locatortype_id\n"
//            		+ "JOIN adempiere.tc_lightstatus ls ON ls.tc_lightstatus_id = l.tc_lightstatus_id;";
//            pstmt = DB.prepareStatement(sql, null);
//            rs = pstmt.executeQuery();
//            
//            while (rs.next()) {
//            	hasRecord = true;
//                String roomType = rs.getString("RoomType");
//                float temperature = rs.getFloat("temperature");
//                float humidity = rs.getFloat("humidity");
//                String date = rs.getString("date");
//                String lightStatus = rs.getString("lightstatus");
//                String lighton = rs.getString("time");
//                String lightData = (lightStatus + " " +lighton);
//                roomDataList.add(new RoomData(roomType, temperature, humidity, date,lightData));
//            }
//            DB.close(rs, pstmt);
//            rs = null;
//            pstmt = null;
//            
//            if (!hasRecord) {
//            	labelFirstRecordDate.setValue("No Record Found");
//                return; 
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        } finally {
//            DB.close(rs, pstmt);
//        }
//    }
//
//    @Override
//    public void refresh(ServerPushTemplate template) {
//        roomDataList.clear();
//        initOptions();
//        template.executeAsync(this);
//    }
//
//    @Override
//    public void updateUI() {
//    }
//
//    @Override
//    public void onEvent(Event event) throws Exception {
//    }
//
//    private class RoomData {
//        private String roomType;
//        private float temperature;
//        private float humidity;
//        private String date;
//        private String light;
//
//        public RoomData(String roomType, float temperature, float humidity, String date,String light) {
//            this.roomType = roomType;
//            this.temperature = temperature;
//            this.humidity = humidity;
//            this.date = date;
//            this.light = light;
//        }
//
//        public String getRoomType() {
//            return roomType;
//        }
//
//        public float getTemperature() {
//            return temperature;
//        }
//
//        public float getHumidity() {
//            return humidity;
//        }
//
//        public String getDate() {
//            return date;
//        }
//        
//        public String getLight() {
//        	return light;
//        }
//    }
//}
