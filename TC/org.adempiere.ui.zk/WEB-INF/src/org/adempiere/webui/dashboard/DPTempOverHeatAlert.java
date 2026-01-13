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

public class DPTempOverHeatAlert extends DashboardPanel implements EventListener<Event> {
	
	private static final long serialVersionUID = 1L;
	
	    private Button btnAlert;
	    private Combobox periodBox;
	    private String labelA;
	    private int noofAlert;
	
	    public DPTempOverHeatAlert() {
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
	        
	        periodBox.addEventListener(Events.ON_CHANGE, e -> {
	            
	        	String period = getSelectedPeriod();
	            noofAlert = getOverHeatCount(period);
	
	            updateUI();
	        });
	
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
	
	    	String period = getSelectedPeriod();
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
