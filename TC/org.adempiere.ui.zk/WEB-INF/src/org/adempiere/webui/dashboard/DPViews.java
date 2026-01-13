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
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Box;
import org.zkoss.zul.Vbox;

public class DPViews extends DashboardPanel implements EventListener<Event> {

    private static final long serialVersionUID = 1L;

    private Button btnAlert;
    private String labelA;
    private int noofAlert;

    public DPViews() {
        super();
        this.setSclass("activities-box");
        this.appendChild(createPanel());
    }

    private Box createPanel() {

        Vbox vbox = new Vbox();

        btnAlert = new Button();
        vbox.appendChild(btnAlert);

        labelA = Util.cleanAmp(Msg.translate(Env.getCtx(), "AlertCount"));
        btnAlert.setLabel(labelA + " : 0");
        btnAlert.setTooltiptext("OverHeat Temperature Alerts");
        btnAlert.setImage(ThemeManager.getThemeResource("images/tem.png"));

        int menuId = DB.getSQLValue(
            null,
            "SELECT AD_Menu_ID FROM adempiere.AD_Menu WHERE Name = 'Temperature' AND IsSummary='N'"
        );
        btnAlert.setName(String.valueOf(menuId));

        btnAlert.addEventListener(Events.ON_CLICK, this);

        return vbox;
    }

    @Override
    public void refresh(ServerPushTemplate template) {

        int alert = DPActivitiesModel.getOverHeatCountToday();

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

        EventQueue<Event> queue =
            EventQueues.lookup(IDesktop.ACTIVITIES_EVENT_QUEUE, true);

        queue.publish(
            new Event(
                IDesktop.ON_ACTIVITIES_CHANGED_EVENT,
                null,
                noofAlert
            )
        );
    }
    
    @Override
    public void onEvent(Event event) {

        if (Events.ON_CLICK.equals(event.getName())) {

            Component comp = event.getTarget();
            if (comp instanceof Button) {

                // 🔹 set context filter
                Env.setContext(
                    Env.getCtx(),
                    "TEMP_ALERT_FILTER",
                    "AND ts.name='OverHeat' " +
                    "AND t.created = CURRENT_DATE"
                );

                int menuId = Integer.parseInt(((Button) comp).getName());
                if (menuId > 0) {
                    SessionManager.getAppDesktop().onMenuSelected(menuId);
                }
            }
        }
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

//Working
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
//public class DPSales extends DashboardPanel implements EventListener<Event> {
//
//    private static final long serialVersionUID = 1L;
//
//    private Button btnAlert;
//    private Combobox periodBox;
//    private String labelA;
//    private int noofAlert;
//
//    public DPSales() {
//        super();
//        this.setSclass("activities-box");
//        this.setStyle("overflow: visible;"); // 🔥 IMPORTANT
//        this.appendChild(createPanel());
//    }
//
//    // ------------------------------------------------------
//    // UI
//    // ------------------------------------------------------
//    private Box createPanel() {
//
//        Vbox root = new Vbox();
//        root.setSpacing("6px");
//        root.setStyle("overflow: visible;");
//
//        Hbox hbox = new Hbox();
//        hbox.setSpacing("8px");
//        hbox.setAlign("center");
//        hbox.setStyle("overflow: visible;");
//
//        // 🔽 Period Dropdown
//        periodBox = new Combobox();
//        periodBox.setWidth("90px");
//        periodBox.setReadonly(false);
//        periodBox.setDisabled(false);
//        periodBox.setButtonVisible(true);
//        periodBox.setStyle("display:inline-block; overflow:visible;");
//
//        periodBox.appendChild(createItem("DAY", "day"));
//        periodBox.appendChild(createItem("MONTH", "month"));
//        periodBox.appendChild(createItem("YEAR", "year"));
//        periodBox.setSelectedIndex(0);
//        
//        periodBox.addEventListener(Events.ON_CHANGE, e -> {
//            
//        	String period = getSelectedPeriod();
//            noofAlert = getOverHeatCount(period);
//
//            updateUI();
//        });
//
//        hbox.appendChild(periodBox);
//
//        // 🔔 Alert Button
//        btnAlert = new Button();
//        labelA = Util.cleanAmp(Msg.translate(Env.getCtx(), "AlertActivity"));
//        btnAlert.setLabel(labelA + " : 0");
//        btnAlert.setTooltiptext("OverHeat Temperature Alerts");
//        btnAlert.setImage(ThemeManager.getThemeResource("images/tem.png"));
//
//        int menuId = DB.getSQLValue(
//            null,
//            "SELECT AD_Menu_ID FROM AD_Menu WHERE Name='Temperature' AND IsSummary='N'"
//        );
//        btnAlert.setName(String.valueOf(menuId));
//        btnAlert.addEventListener(Events.ON_CLICK, this);
//
//        hbox.appendChild(btnAlert);
//
//        root.appendChild(hbox);
//        return root;
//    }
//
//    private Comboitem createItem(String label, String value) {
//        Comboitem item = new Comboitem(label);
//        item.setValue(value);
//        return item;
//    }
//
//    // ------------------------------------------------------
//    // Dashboard refresh
//    // ------------------------------------------------------
//    @Override
//    public void refresh(ServerPushTemplate template) {
//
//    	String period = getSelectedPeriod();
//        int alert = getOverHeatCount(getSelectedPeriod());
//
//        if (noofAlert != alert) {
//            noofAlert = alert;
//            template.executeAsync(this);
//        }
//    }
//
//    @Override
//    public void updateUI() {
//
//        btnAlert.setLabel(labelA + " : " + noofAlert);
//
//        if (noofAlert > 0) {
//            btnAlert.setSclass("z-button temp-alert-red");
//        } else {
//            btnAlert.setSclass(null);
//        }
//
//        EventQueues.lookup(
//            IDesktop.ACTIVITIES_EVENT_QUEUE, true
//        ).publish(new Event(
//            IDesktop.ON_ACTIVITIES_CHANGED_EVENT,
//            null,
//            noofAlert
//        ));
//    }
//
//    // ------------------------------------------------------
//    // Click handling
//    // ------------------------------------------------------
//    @Override
//    public void onEvent(Event event) {
//
//        if (Events.ON_CLICK.equals(event.getName())) {
//
//            String period = getSelectedPeriod();
//
//            // Pass period to window
//            Env.setContext(Env.getCtx(), "#TEMP_PERIOD", period);
//
//            int menuId = Integer.parseInt(btnAlert.getName());
//            if (menuId > 0) {
//                SessionManager.getAppDesktop().onMenuSelected(menuId);
//            }
//        }
//    }
//
//    private String getSelectedPeriod() {
//        Comboitem item = periodBox.getSelectedItem();
//        return item != null ? item.getValue().toString() : "day";
//    }
//
//    // ------------------------------------------------------
//    // SQL (INSIDE SAME CLASS)
//    // ------------------------------------------------------
//    private int getOverHeatCount(String period) {
//
//        String sql =
//            "SELECT COUNT(*) " +
//            "FROM tc_temperaturestatus t " +
//            "JOIN tc_tempstatus ts ON ts.tc_tempstatus_id=t.tc_tempstatus_id " +
//            "WHERE ts.name='OverHeat' " +
//            "AND t.ad_client_id=? " +
//            "AND t.created >= CASE lower(?) " +
//            " WHEN 'day' THEN CURRENT_DATE " +
//            " WHEN 'month' THEN date_trunc('month', CURRENT_DATE) " +
//            " WHEN 'year' THEN date_trunc('year', CURRENT_DATE) END " +
//            "AND t.created < CASE lower(?) " +
//            " WHEN 'day' THEN CURRENT_DATE + INTERVAL '1 day' " +
//            " WHEN 'month' THEN date_trunc('month', CURRENT_DATE) + INTERVAL '1 month' " +
//            " WHEN 'year' THEN date_trunc('year', CURRENT_DATE) + INTERVAL '1 year' END";
//
//        return DB.getSQLValue(
//            null,
//            sql,
//            Env.getAD_Client_ID(Env.getCtx()),
//            period,
//            period
//        );
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

///******************************************************************************
// * Copyright (C) 2008 Elaine Tan                                              *
// * Copyright (C) 2008 Idalica Corporation
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
//package org.adempiere.webui.dashboard;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.List;
//
//import org.adempiere.webui.LayoutUtils;
//import org.adempiere.webui.apps.AEnv;
//import org.adempiere.webui.component.ToolBarButton;
//import org.adempiere.webui.component.Window;
//import org.adempiere.webui.session.SessionManager;
//import org.adempiere.webui.theme.ThemeManager;
//import org.adempiere.webui.window.InfoSchedule;
//import org.compiere.model.MInfoWindow;
//import org.compiere.model.MRole;
//import org.compiere.model.MSysConfig;
//import org.compiere.model.MUserDefInfo;
//import org.compiere.model.Query;
//import org.compiere.util.Env;
//import org.compiere.util.Msg;
//import org.compiere.util.Util;
//import org.zkoss.zk.ui.Component;
//import org.zkoss.zk.ui.event.Event;
//import org.zkoss.zk.ui.event.EventListener;
//import org.zkoss.zk.ui.event.Events;
//import org.zkoss.zul.Box;
//import org.zkoss.zul.Vbox;
//
///**
// * Dashboard gadget: List of Info views
// * @author Elaine
// * @date November 20, 2008
// */
//public class DPViews extends DashboardPanel implements EventListener<Event> {
//	/**
//	 * generated serial id
//	 */
//	private static final long serialVersionUID = 8375414665766937581L;
//
//	/**
//	 * Default constructor
//	 */
//	public DPViews()
//	{
//		super();
//		setSclass("views-box");
//		this.appendChild(createViewPanel());
//	}
//
//	/**
//	 * Layout panel
//	 * @return {@link Box}
//	 */
//	private Box createViewPanel()
//	{
//		Vbox vbox = new Vbox();
//
//		if (MSysConfig.getBooleanValue(MSysConfig.DPViews_ShowInfoAccount, true, Env.getAD_Client_ID(Env.getCtx()))
//				&& MRole.getDefault().isShowAcct() && MRole.getDefault().isAllow_Info_Account())
//		{
//			ToolBarButton btnViewItem = new ToolBarButton("InfoAccount");
//			btnViewItem.setSclass("link");
//			btnViewItem.setLabel(Util.cleanAmp(Msg.getMsg(Env.getCtx(), "InfoAccount")));
//			if (ThemeManager.isUseFontIconForImage())
//				btnViewItem.setIconSclass("z-icon-InfoAccount");
//			else
//				btnViewItem.setImage(ThemeManager.getThemeResource("images/InfoAccount16.png"));
//			btnViewItem.addEventListener(Events.ON_CLICK, this);
//			vbox.appendChild(btnViewItem);
//			if (ThemeManager.isUseFontIconForImage())
//				LayoutUtils.addSclass("medium-toolbarbutton toolbarbutton-with-text", btnViewItem);
//		}
//		if (MSysConfig.getBooleanValue(MSysConfig.DPViews_ShowInfoSchedule, true, Env.getAD_Client_ID(Env.getCtx()))
//				&& MRole.getDefault().isAllow_Info_Schedule())
//		{
//			ToolBarButton btnViewItem = new ToolBarButton("InfoSchedule");
//			btnViewItem.setSclass("link");
//			btnViewItem.setLabel(Util.cleanAmp(Msg.getMsg(Env.getCtx(), "InfoSchedule")));
//			if (ThemeManager.isUseFontIconForImage())
//				btnViewItem.setIconSclass("z-icon-InfoSchedule");
//			else
//				btnViewItem.setImage(ThemeManager.getThemeResource("images/InfoSchedule16.png"));
//			btnViewItem.addEventListener(Events.ON_CLICK, this);
//			vbox.appendChild(btnViewItem);
//			if (ThemeManager.isUseFontIconForImage())
//				LayoutUtils.addSclass("medium-toolbarbutton toolbarbutton-with-text", btnViewItem);
//		}
//
//		List<MInfoWindow> listAll = new Query(Env.getCtx(), MInfoWindow.Table_Name, "IsValid='Y'", null)
//				.setOnlyActiveRecords(true)
//				.list();
//
//		MInfoWindow[] infosAll = listAll.toArray(new MInfoWindow[listAll.size()]);
//
//		List<ListInfoWindow> selectedInfoWindows = new ArrayList<ListInfoWindow>();
//
//		for (int i = 0; i < infosAll.length; i++) 
//		{
//			MInfoWindow info = infosAll[i];
//			if (MInfoWindow.get(info.getAD_InfoWindow_ID(), null) != null)
//			{
//				MUserDefInfo userDef = MUserDefInfo.getBestMatch(Env.getCtx(), info.getAD_InfoWindow_ID());
//
//				if (userDef != null) {
//
//					if ((info.isShowInDashboard() && Util.isEmpty(userDef.getIsShowInDashboard())) || (!Util.isEmpty(userDef.getIsShowInDashboard()) && userDef.getIsShowInDashboard().equals(MUserDefInfo.ISSHOWINDASHBOARD_Yes))) {
//						int seqNo = userDef.getSeqNo() > 0 ? userDef.getSeqNo() : info.getSeqNo();
//						selectedInfoWindows.add(new ListInfoWindow(info, seqNo));
//					}
//				}
//				else if (info.isShowInDashboard())
//					selectedInfoWindows.add(new ListInfoWindow(info, info.getSeqNo()));
//			}
//		}
//
//		Collections.sort(selectedInfoWindows, new SeqNoComparator());
//
//		for (ListInfoWindow so : selectedInfoWindows) {
//			MInfoWindow info = so.getInfoWindow();
//			if (MInfoWindow.get(info.getAD_InfoWindow_ID(), null) != null)
//			{
//				// Load User Def
//				String name = info.get_Translation("Name");
//				String image = (Util.isEmpty(info.getImageURL()) ? "Info16.png" : info.getImageURL());
//
//				MUserDefInfo userDef = MUserDefInfo.getBestMatch(Env.getCtx(), info.getAD_InfoWindow_ID());
//				if(userDef != null) {
//
//					if (!Util.isEmpty(userDef.getName()))
//						name = userDef.getName();
//					if (!Util.isEmpty(userDef.getImageURL()))
//						image = userDef.getImageURL();
//				} 
//
//				ToolBarButton btnViewItem = new ToolBarButton(info.getName());
//				btnViewItem.setSclass("link");
//				btnViewItem.setLabel(name);
//
//				if (ThemeManager.isUseFontIconForImage()) 
//				{
//					image = image.replace("16.png", "");
//					btnViewItem.setIconSclass("z-icon-"+image);
//				}
//				else
//				{
//					btnViewItem.setImage(ThemeManager.getThemeResource("images/" + image));
//				}
//				btnViewItem.addEventListener(Events.ON_CLICK, this);
//				vbox.appendChild(btnViewItem);
//				if (ThemeManager.isUseFontIconForImage())
//					LayoutUtils.addSclass("medium-toolbarbutton toolbarbutton-with-text", btnViewItem);
//			}
//		}
//
//		return vbox;
//	}
//
//	@Override
//	public void onEvent(Event event)
//	{
//		Component comp = event.getTarget();
//		String eventName = event.getName();
//
//		if(eventName.equals(Events.ON_CLICK))
//		{
//			if(comp instanceof ToolBarButton)
//			{
//				ToolBarButton btn = (ToolBarButton) comp;
//				String actionCommand = btn.getName();
//
//				if (actionCommand.equals("InfoAccount"))
//				{
//					new org.adempiere.webui.acct.WAcctViewer();
//				}
//				else if (actionCommand.equals("InfoSchedule"))
//				{
//					InfoSchedule is = new InfoSchedule(null, false);
//					is.setAttribute(Window.MODE_KEY, Mode.EMBEDDED);
//					AEnv.showWindow(is);
//				}
//				else
//				{
//					int infoWindowID = new Query(Env.getCtx(), MInfoWindow.Table_Name, "Name = ?", null)
//					.setParameters(actionCommand)
//					.setOnlyActiveRecords(true)
//					.firstIdOnly();
//
//					if (infoWindowID<=0)
//						return;
//					
//					SessionManager.getAppDesktop().openInfo(infoWindowID);
//				}
//			}
//		}
//	}
//
//	/**
//	 * Info Window to be displayed in the panel
//	 * @author nmicoud
//	 */ 
//	private class ListInfoWindow {
//
//		MInfoWindow iw = null;
//		int seqNo = 0;
//
//		/**
//		 * @param infoWindow
//		 * @param seqNo
//		 */
//		public ListInfoWindow(MInfoWindow infoWindow, int seqNo) {
//			iw = infoWindow;
//			this.seqNo = seqNo;
//		}
//
//		/**
//		 * @return Sequence Number
//		 */
//		public int getSeqNo() {
//			return seqNo;
//		}
//
//		/**
//		 * @return MInfoWindow
//		 */
//		public MInfoWindow getInfoWindow() {
//			return iw;
//		}
//	}
//
//	/**
//	 * @author nmicoud
//	 * IDEMPIERE-4946 Implement InfoWindow SeqNo customization
//	 */
//	public static class SeqNoComparator implements Comparator<ListInfoWindow> {
//		@Override
//		public int compare(ListInfoWindow iw1, ListInfoWindow iw2) {
//			return (Integer.valueOf(iw1.getSeqNo())).compareTo(Integer.valueOf(iw2.getSeqNo()));
//		}
//	}
//}
