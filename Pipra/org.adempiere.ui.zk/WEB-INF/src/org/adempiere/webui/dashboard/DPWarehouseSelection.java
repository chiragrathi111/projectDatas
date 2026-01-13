package org.adempiere.webui.dashboard;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Properties;
import java.util.Set;

import org.adempiere.webui.component.Combobox;
import org.adempiere.webui.theme.ITheme;
import org.adempiere.webui.util.ServerPushTemplate;
import org.adempiere.webui.util.ZKUpdateUtil;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Box;
import org.zkoss.zul.Div;
import org.zkoss.zul.Vbox;

public class DPWarehouseSelection extends DashboardPanel implements EventListener<Event> {

	private Combobox comboboxWarehouseNames;
	public Combobox comboboxProductsData;

	private static final long serialVersionUID = 1L;
	public LinkedHashMap<String, Integer> warehouseNames = new LinkedHashMap<>();
	public LinkedHashMap<String, Integer> productNames = new LinkedHashMap<>();
	public static final String M_PRODUCT_ID = "#M_Product_ID";

	public static LinkedHashMap<DashboardPanel, Desktop> dashboardPanels = new LinkedHashMap<>();

	public DPWarehouseSelection() {
		super();
		this.setSclass("activities-box");
		initOptions();
		this.appendChild(createActivitiesPanel());
	}

	private Box createActivitiesPanel() {
		Vbox vbox = new Vbox();
		vbox.setSclass(ITheme.WARE_HOUSE_SELECT_WIDGET);

		Div div = new Div();
		div.setSclass(ITheme.WARE_HOUSE_SELECT_WIDGET_CONT);
		vbox.appendChild(div);

		comboboxProductsData = new Combobox();
		div.appendChild(comboboxProductsData);
		comboboxProductsData.setAutocomplete(true);
		comboboxProductsData.setAutodrop(true);
		comboboxProductsData.setPlaceholder("All Products");
		comboboxProductsData.appendItem("All Products");
		comboboxProductsData.setSelectedIndex(0);
		comboboxProductsData.setId("comboboxProductsData");
		comboboxProductsData.addEventListener(Events.ON_SELECT, this);
		ZKUpdateUtil.setWidth(comboboxProductsData, "30%");
		for (String langName : productNames.keySet()) {
			comboboxProductsData.appendItem(langName);
		}

		comboboxWarehouseNames = new Combobox();
		div.appendChild(comboboxWarehouseNames);
		comboboxWarehouseNames.setAutocomplete(true);

		comboboxWarehouseNames.setAutodrop(true);
		comboboxWarehouseNames.setPlaceholder("Select Ware House");
		comboboxWarehouseNames.appendItem("All Warehouse");
		comboboxWarehouseNames.setSelectedIndex(0);
		comboboxWarehouseNames.addEventListener(Events.ON_SELECT, this);
		ZKUpdateUtil.setWidth(comboboxWarehouseNames, "30%");
		Collection<String> wNames = warehouseNames.keySet();
		for (String langName : wNames) {
			comboboxWarehouseNames.appendItem(langName);
		}
		return vbox;
	}

	private void initOptions() {

		Properties ctx = Env.getCtx();
		int clientId = Env.getAD_Client_ID(ctx);
		int RoleId = Env.getAD_Role_ID(ctx);

		String wareHouseCountSql = "SELECT mw.name, mw.m_warehouse_id FROM m_warehouse mw \n"
				+ "join ad_role ar on ar.ad_client_id = mw.ad_client_id \n"
				+ "join ad_role_orgaccess aro on aro.ad_role_id = ar.ad_role_id \n"
				+ "and aro.ad_client_id= ar.ad_client_id and aro.ad_org_id= mw.ad_org_id \n" + "where ar.ad_client_id="
				+ clientId + " and aro.ad_role_id=" + RoleId + ";";

		String productCountSql = "select name, m_product_id from m_product where ad_client_id = " + clientId + ";";

		PreparedStatement pstmt = null;
		ResultSet RS = null;
		try {

			pstmt = DB.prepareStatement(wareHouseCountSql.toString(), null);
			RS = pstmt.executeQuery();
			while (RS.next()) {
				String name = RS.getString("name");
				int m_warehouse_id = RS.getInt("m_warehouse_id");
				if (name != null && !warehouseNames.containsValue(name))
					warehouseNames.put(name, m_warehouse_id);
			}
			DB.close(RS, pstmt);
			RS = null;
			pstmt = null;

			pstmt = DB.prepareStatement(productCountSql.toString(), null);
			RS = pstmt.executeQuery();
			while (RS.next()) {
				String name = RS.getString("name");
				int m_product_id = RS.getInt("m_product_id");
				if (name != null)
					productNames.put(name, m_product_id);
			}
			DB.close(RS, pstmt);
			RS = null;
			pstmt = null;

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onEvent(Event event) throws Exception {
		if (comboboxWarehouseNames.getSelectedItem() != null || comboboxProductsData.getSelectedItem() != null) {
			Combobox combobox = (Combobox) event.getTarget();
			if (combobox.getId() == comboboxProductsData.getId()) {
//				productName = comboboxProductsData.getSelectedItem().getLabel();
				int productId = getSelectedProductId();
//				if (productName.equalsIgnoreCase("All Products"))
//					productId = 0;
//				else
//					productId = productNames.get(productName);
				Env.setContext(Env.getCtx(), M_PRODUCT_ID, productId);

				Env.setContext(Env.getCtx(), Env.M_WAREHOUSE_ID, 0);
				comboboxWarehouseNames.setSelectedIndex(0);
			} else if (combobox.getId() == comboboxWarehouseNames.getId()) {
//				warehouseDefaultName = comboboxWarehouseNames.getSelectedItem().getLabel();
				int m_warehouse_id = getSelectedWarehouseId();
//				if (warehouseDefaultName.equalsIgnoreCase("All Warehouse"))// All Products
//					m_warehouse_id = 0;
//				else
//					m_warehouse_id = warehouseNames.get(warehouseDefaultName);
				Env.setContext(Env.getCtx(), Env.M_WAREHOUSE_ID, m_warehouse_id);

				Env.setContext(Env.getCtx(), M_PRODUCT_ID, 0);
				comboboxProductsData.setSelectedIndex(0);
			}
			DashboardRunnable dashboardRunnable = refreshDashboardPanels(getDesktop());
			dashboardRunnable.refreshDashboard(false);
		}
	}

	public static DashboardRunnable refreshDashboardPanels(Desktop desktop) {
		DashboardRunnable dashboardRunnable = new DashboardRunnable(desktop);
		for (DashboardPanel dp : dashboardPanels.keySet()) {

			try {
				if (desktop == dp.getDesktop()) {
					String dpPanelName = getDpName(dp);
					if (!dpPanelName.equalsIgnoreCase("DPFavourites") && !dpPanelName.equalsIgnoreCase("DPRecentItems"))
						dashboardRunnable.add(dp);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return dashboardRunnable;
	}

	public static String getDpName(DashboardPanel dp) {
		String[] dpPanelSet = dp.toString().split("\\s+");
		String dpPanelName = dpPanelSet[0].substring(1);
		return dpPanelName;
	}

	public static void addPanel(DashboardPanel dashboardPanel, Desktop currentDesktop) {
		Set<DashboardPanel> keys = dashboardPanels.keySet();
		boolean remove = false;
		DashboardPanel name = null;
		if (keys != null) {

			String[] parts = dashboardPanel.toString().split(" ");
			String panel = parts[0].replace("<", "");

			for (DashboardPanel dp : keys) {
				String[] listParts = dp.toString().split(" ");
				String listPanel = listParts[0].replace("<", "");
				if (listPanel.equalsIgnoreCase(panel)) {
					if (dp.getDesktop() == currentDesktop)
						remove = true;
					name = dp;
				}
			}
			if (remove) {
				dashboardPanels.remove(name);

			}
			dashboardPanels.put(dashboardPanel, currentDesktop);
		}
	}

	@Override
	public void refresh(ServerPushTemplate template) {
		template.executeAsync(this);
	}

	private int getSelectedWarehouseId() {
		String selectedWarehouseName = comboboxWarehouseNames.getSelectedItem().getLabel();
		int selectedWarehouseId;
		if (selectedWarehouseName.equalsIgnoreCase("All Warehouse"))
			selectedWarehouseId = 0;
		else
			selectedWarehouseId = warehouseNames.get(selectedWarehouseName);
		return selectedWarehouseId;
	}

	private int getSelectedProductId() {
		String selectedProductName = comboboxProductsData.getSelectedItem().getLabel();
		int selectedProductId;
		if (selectedProductName.equalsIgnoreCase("All Products"))
			selectedProductId = 0;
		else
			selectedProductId = productNames.get(selectedProductName);
		return selectedProductId;
	}

	@Override
	public void updateUI() {
		int warehouseId = getWareHouse_ID(Env.getCtx());
		if (warehouseId != getSelectedWarehouseId()) {
			int index = -1;
			int currentIndex = 1;
			for (Integer key : warehouseNames.values()) {
				if (key == warehouseId) {
					index = currentIndex;
					break;
				}
				currentIndex++;
			}
			if (index < 0)
				index = 0;
			comboboxWarehouseNames.setSelectedIndex(index);
		}

		int productId = getProduct_ID(Env.getCtx());
		if (productId != getSelectedProductId()) {

			int index = -1;
			int currentIndex = 1;
			for (Integer key : productNames.values()) {
				if (key == productId) {
					index = currentIndex;
					break;
				}
				currentIndex++;
			}
			if (index < 0)
				index = 0;
			comboboxProductsData.setSelectedIndex(index);
		}
	}

	public static int getWareHouse_ID(Properties ctx) {
		return Env.getContextAsInt(ctx, Env.M_WAREHOUSE_ID);
	}

	public static int getProduct_ID(Properties ctx) {
		return Env.getContextAsInt(ctx, M_PRODUCT_ID);
	}
}
