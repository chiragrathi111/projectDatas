/******************************************************************************
 * Copyright (C) 2009 Low Heng Sin                                            *
 * Copyright (C) 2009 Idalica Corporation                                     *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program; if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 *****************************************************************************/
package org.adempiere.webui.theme;

import org.compiere.model.MSysConfig;

/**
 * Interface to hold global theme constant
 * @author hengsin
 *
 */
public interface ITheme {
	//default theme
	public static final String ZK_THEME_DEFAULT = "default";
	//theme resource url prefix. ~./ is the zk url prefix for resources loaded from classpath (typically at src/web folder)
	public static final String THEME_PATH_PREFIX_V8 = "~./theme/";
	public static final String THEME_PATH_PREFIX_V7 = "/theme/"; // for backward compatibility

	//css for login window and box
	public static final String LOGIN_WINDOW_CLASS = "login-body";
	public static final String LOGIN_BOX_HEADER_CLASS = "login-box-header";
	public static final String LOGIN_BOX_HEADER_TXT_CLASS = "login-box-header-txt";
	public static final String LOGIN_BOX_HEADER_LOGO_CLASS = "login-box-header-logo";
	public static final String LOGIN_BOX_BODY_CLASS = "login-box-body";
	public static final String LOGIN_BOX_FOOTER_CLASS = "login-box-footer";
	public static final String LOGIN_BOX_FOOTER_PANEL_CLASS = "login-box-footer-pnl";
	public static final String Role_BOX_FOOTER="role-box-footer";

	//css for login control
	public static final String LOGIN_BUTTON_CLASS = "login-btn";
	public static final String LOGIN_LABEL_CLASS = "login-label";
	public static final String LOGIN_FIELD_CLASS = "login-field";
	public static final String LOGIN_WELCOME_NOTE_CONT = "login-welcome-note-cont";
	public static final String LOGIN_WELCOME_NOTE ="login-welcome-note";
	public static final String LOGIN_WELCOME_NOTE_SUBTITLE="login-welcome-note-subtitle";
	public static final String LOGIN_NOTE="login-note";
	public static final String LOGIN_INPUT_FIELD="login-input-field";
	public static final String LOGIN_FIELDS_CONT="login-fields-cont";
	public static final String LOGIN_INPUT_DROPDOWN="login-input-dropdown";
	public static final String LOGIN_REMEMBER_ME="login-remember-me";
	public static final String LOGIN_FORGOT_PWD = "forgot-pwd";
	public static final String LOGIN_VERSION_COPYRIGHT_CONT="login-version-copyright-cont";
	public static final String LOGIN_BOX_ROLE_SELECTION_HEADER_TXT_CLASS="login-box-role-selection-header-txt";
	public static final String LOGIN_BUTTON_CLASS_ROLE_CANCEL="login-button-role-cancel";
	
	//css for widgets
	public static final String WARE_HOUSE_DATA_WIDGET="ware-house-data-widget";
	public static final String WARE_HOUSE_SELECT_WIDGET="ware-house-select-widget";
	public static final String WARE_HOUSE_SELECT_WIDGET_CONT="ware-house-select-widget-cont";
	public static final String DASHBOARD_WIDGET_LABELS="dashboard-widget-labels";
	
	public static final String DASHBOARD_WIDGET_LABELS_BORDER_PURPLE="dashboard-widget-labels-border-purple";
	public static final String DASHBOARD_WIDGET_LABELS_BORDER_LIGHT_BLUE="dashboard-widget-labels-border-light-blue";
	public static final String DASHBOARD_WIDGET_LABELS_BORDER_ORANGE="dashboard-widget-labels-border-orange";
	public static final String DASHBOARD_WIDGET_COUNT="dashboard-widget-count";
	public static final String DASHBOARD_WIDGET_OCCUPANY="dashboard-widget-occupancy";
	public static final String DASHBOARD_WIDGET_LABELS_COUNT_CONT="dashboard-widget-lables-count-cont";
	public static final String DASHBOARD_WIDGET_LABELS_COUNT_TEXT = "dashboard-widget-labels-count-text";
	public static final String DASHBOARD_WIDGET_LABELS_COUNT="dashboard-widget-lables-count";
	public static final String DASHBOARD_WIDGET_Text_COUNT_CONT="dashboard-widget-text-count-cont";
	
	public static final String DASHBOARD_WIDGET_Text_COUNT_CONT_BASIC_STATS="dashboard-widget-text-count-cont-basic-stats";
	public static final String DASHBOARD_WIDGET_CURRENCY_TYPE="dashboard-widget-currency-type";
	public static final String DASHBOARD_WIDGET_LABELS_STOCK_CHECK_CONT="dashboard-widget-lables-stock-check-cont";

	//logo
//	public static final String LOGIN_LOGO_IMAGE = "/images/login-logo.png";
//	public static final String LOGIN_LOGO_IMAGE = "/images/warepro-logo.svg";
	public static final String LOGIN_LOGO_IMAGE = MSysConfig.getValue("ZK_LOGIN_ICON");
	public static final String HEADER_LOGO_IMAGE = "/images/header-logo.png";
//	public static final String BROWSER_ICON_IMAGE= "/images/icon.png";
	public static final String BROWSER_ICON_IMAGE= MSysConfig.getValue(MSysConfig.ZK_BROWSER_ICON);
	public static final String WMS_USER_IMG = "/images/wms_user.svg";

	//stylesheet url
	public static final String THEME_STYLESHEET = "/css/theme.css.dsp";
	//http://books.zkoss.org/wiki/ZK_Developer's_Reference/Internationalization/Locale-Dependent_Resources#Specifying_Locale-_and_browser-dependent_URL
	public static final String THEME_STYLESHEET_BY_BROWSER = "/css/theme*.css.dsp*";
	
	//theme preference
	public static final String THEME_PREFERENCE = "/preference.zul";
	
	public static final String USE_CSS_FOR_WINDOW_SIZE = "#THEME_USE_CSS_FOR_WINDOW_SIZE";
	
	public static final String USE_FONT_ICON_FOR_IMAGE = "#THEME_USE_FONT_ICON_FOR_IMAGE";
	
	public static final String ZK_TOOLBAR_BUTTON_SIZE = "#ZK_Toolbar_Button_Size";
	public static final String DASHBOARD_WIDGET_REMOVE_HEADER = "dashboard-widget-remove-header";
}
