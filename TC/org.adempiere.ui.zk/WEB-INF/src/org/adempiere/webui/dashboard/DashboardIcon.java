package org.adempiere.webui.dashboard;

import org.adempiere.webui.component.Button;
import org.adempiere.webui.session.SessionManager;
import org.compiere.util.CLogger;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Box;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Vbox;

public class DashboardIcon extends DashboardPanel implements EventListener<Event> {

	private static final long serialVersionUID = 3787249181565314148L;

	@SuppressWarnings("unused")
	private static final CLogger logger = CLogger.getCLogger(DashboardIcon.class);

	private static Button btnNotice;

	public DashboardIcon() {
		super();
		this.setSclass("activities-box");
		this.appendChild(createActivitiesPanel());
	}

	private Box createActivitiesPanel() {
		Vbox vbox = new Vbox();
		vbox.appendChild(btnNotice);
		return vbox;
	}

	@Override
	public boolean isPooling() {
		return true;
	}

	public void onEvent(Event event) {
		String eventName = event.getName();

		if (eventName.equals(Events.ON_CLICK)) {
			SessionManager.getAppDesktop().updateHelpContext("T", 50010);// X_AD_CtxHelp.CTXTYPE_Home
		}
	}

	@Override
	public boolean isLazy() {
		return true;
	}

	public static Button getBtnNotice() {
		return btnNotice;
	}

	public static void setBtnNotice(Button btnNotice) {
		DashboardIcon.btnNotice = btnNotice;
	}
}
