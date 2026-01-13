package org.adempiere.webui.dashboard;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.adempiere.webui.component.Label;
import org.adempiere.webui.theme.ITheme;
import org.adempiere.webui.util.ServerPushTemplate;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Div;

public class DPSurveillanceStats extends DashboardPanel implements EventListener<Event> {

	private Label labelBorder;
	private Label labelSubText;

	private Label labelUnAuthorized;
	private Label labelUnAuthorizedCount;

	private Label labelDoorOpened;
	private Label labelDoorOpenedCount;

	private Label labelDoorClosed;
	private Label labelDoorClosedCount;

	private Label labelItemIn;
	private Label labelItemInCount;

	private Label labelItemOut;
	private Label labelItemOutCount;

	private int unAuthorizedCount;
	private int doorOpenedCount;
	private int doorClosedCount;
	private int itemInCount;
	private int itemOutCount;

	private final static String UnAuthorizedText = "Unauthorized";
	private final static String doorOpenedText = "Door Opened";
	private final static String doorCLosedText = "Door Closed";
	private final static String itemInText = "Item In";
	private final static String itemOutText = "Item Out";

	private static final long serialVersionUID = 1L;

	public DPSurveillanceStats() {
		super();
		this.setSclass("activities-box");
		initOptions();
		this.appendChild(createActivitiesPanel());
	}

	private Div createActivitiesPanel() {
		Div div = new Div();
		div.setSclass(ITheme.WARE_HOUSE_DATA_WIDGET);

		Div childDivFirst = new Div();
		Div subChildFirst = new Div();

		labelUnAuthorized = new Label();
		labelUnAuthorized.setSclass(ITheme.DASHBOARD_WIDGET_LABELS);
		labelUnAuthorized.setValue(UnAuthorizedText);

		labelBorder = new Label();
		labelBorder.setSclass(ITheme.DASHBOARD_WIDGET_LABELS_BORDER_PURPLE);

		subChildFirst.appendChild(labelBorder);
		subChildFirst.appendChild(labelUnAuthorized);

		childDivFirst.appendChild(subChildFirst);
		labelUnAuthorizedCount = new Label();
		labelUnAuthorizedCount.setSclass(ITheme.DASHBOARD_WIDGET_COUNT);
		labelUnAuthorizedCount.setValue(" " + unAuthorizedCount);
		childDivFirst.appendChild(labelUnAuthorizedCount);
		childDivFirst.setSclass(ITheme.DASHBOARD_WIDGET_Text_COUNT_CONT_BASIC_STATS);
		div.appendChild(childDivFirst);

		Div childDivSecond = new Div();
		Div subChildSecond = new Div();

		labelDoorOpened = new Label();
		labelDoorOpened.setSclass(ITheme.DASHBOARD_WIDGET_LABELS);
		labelDoorOpened.setValue(doorOpenedText);

		labelBorder = new Label();
		labelBorder.setSclass(ITheme.DASHBOARD_WIDGET_LABELS_BORDER_LIGHT_BLUE);

		subChildSecond.appendChild(labelBorder);
		subChildSecond.appendChild(labelDoorOpened);
		childDivSecond.appendChild(subChildSecond);
		labelDoorOpenedCount = new Label();
		labelDoorOpenedCount.setSclass(ITheme.DASHBOARD_WIDGET_COUNT);
		labelDoorOpenedCount.setValue("  " + doorOpenedCount);
		childDivSecond.appendChild(labelDoorOpenedCount);
		childDivSecond.setSclass(ITheme.DASHBOARD_WIDGET_Text_COUNT_CONT_BASIC_STATS);
		div.appendChild(childDivSecond);

		Div childDivThird = new Div();
		Div subChildThird = new Div();

		labelDoorClosed = new Label();
		labelDoorClosed.setSclass(ITheme.DASHBOARD_WIDGET_LABELS);
		labelDoorClosed.setValue(doorCLosedText);

		labelBorder = new Label();
		labelBorder.setSclass(ITheme.DASHBOARD_WIDGET_LABELS_BORDER_LIGHT_BLUE);

		subChildThird.appendChild(labelBorder);
		subChildThird.appendChild(labelDoorClosed);
		childDivThird.appendChild(subChildThird);

		labelDoorClosedCount = new Label();
		labelDoorClosedCount.setSclass(ITheme.DASHBOARD_WIDGET_COUNT);
		childDivThird.appendChild(labelDoorClosedCount);
		labelDoorClosedCount.setValue(" " + doorClosedCount);
		childDivThird.setSclass(ITheme.DASHBOARD_WIDGET_Text_COUNT_CONT_BASIC_STATS);
		div.appendChild(childDivThird);

		Div childDivFour = new Div();
		Div subChildFour = new Div();

		labelItemIn = new Label();
		labelItemIn.setSclass(ITheme.DASHBOARD_WIDGET_LABELS);
		labelItemIn.setValue(itemInText);

		labelBorder = new Label();
		labelBorder.setSclass(ITheme.DASHBOARD_WIDGET_LABELS_BORDER_ORANGE);

		subChildFour.appendChild(labelBorder);
		subChildFour.appendChild(labelItemIn);

		childDivFour.appendChild(subChildFour);

		Div stockCheckDiv = new Div();
		stockCheckDiv.setSclass(ITheme.DASHBOARD_WIDGET_Text_COUNT_CONT_BASIC_STATS);
		labelItemInCount = new Label();
		labelItemInCount.setValue("  " + itemInCount);
		labelItemInCount.setSclass(ITheme.DASHBOARD_WIDGET_COUNT);
		stockCheckDiv.appendChild(labelItemInCount);
		labelSubText = new Label();
		labelSubText.setSclass(ITheme.DASHBOARD_WIDGET_LABELS);
		labelSubText.setSclass(ITheme.DASHBOARD_WIDGET_LABELS_COUNT_TEXT);
//		labelSubText.setValue(itemInText);
		stockCheckDiv.appendChild(labelSubText);
		childDivFour.appendChild(stockCheckDiv);

		childDivFour.setSclass(ITheme.DASHBOARD_WIDGET_Text_COUNT_CONT_BASIC_STATS);
		div.appendChild(childDivFour);

		Div childDivFive = new Div();
		Div subChildFive = new Div();

		labelItemOut = new Label();
		labelItemOut.setSclass(ITheme.DASHBOARD_WIDGET_LABELS);
		labelItemOut.setValue(itemOutText);

		labelBorder = new Label();
		labelBorder.setSclass(ITheme.DASHBOARD_WIDGET_LABELS_BORDER_PURPLE);

		subChildFive.appendChild(labelBorder);
		subChildFive.appendChild(labelItemOut);

		childDivFive.appendChild(subChildFive);

		Div itemOutDiv = new Div();
		itemOutDiv.setSclass(ITheme.DASHBOARD_WIDGET_Text_COUNT_CONT_BASIC_STATS);
		labelItemOutCount = new Label();
		labelItemOutCount.setValue("  " + itemOutCount);
		labelItemOutCount.setSclass(ITheme.DASHBOARD_WIDGET_COUNT);
		itemOutDiv.appendChild(labelItemOutCount);
		labelSubText = new Label();
		labelSubText.setSclass(ITheme.DASHBOARD_WIDGET_LABELS);
		labelSubText.setSclass(ITheme.DASHBOARD_WIDGET_LABELS_COUNT_TEXT);
//		labelSubText.setValue(itemOutText);
		itemOutDiv.appendChild(labelSubText);
		childDivFive.appendChild(itemOutDiv);

		childDivFive.setSclass(ITheme.DASHBOARD_WIDGET_Text_COUNT_CONT_BASIC_STATS);
		div.appendChild(childDivFive);

		return div;
	}

	private void initOptions() {

		Properties ctx = Env.getCtx();
		int clientId = Env.getAD_Client_ID(ctx);

		PreparedStatement pstmt = null;
		ResultSet RS = null;
		try {

			String sql = "SELECT\n"
					+ "    SUM(CASE WHEN msg.msgtext = 'Unauthorised person' THEN 1 ELSE 0 END) AS UnAuthorizedCount,\n"
					+ "    SUM(CASE WHEN msg.msgtext = 'Door Opened' THEN 1 ELSE 0 END) AS DoorOpenedCount,\n"
					+ "    SUM(CASE WHEN msg.msgtext = 'Door Closed' THEN 1 ELSE 0 END) AS DoorClosedCount,\n"
					+ "    SUM(CASE WHEN msg.msgtext = 'Item In' THEN 1 ELSE 0 END) AS ItemInCount,\n"
					+ "    SUM(CASE WHEN msg.msgtext = 'Item Out' THEN 1 ELSE 0 END) AS ItemOutCount\n"
					+ "FROM ad_message msg\n" + "JOIN ad_note note ON msg.ad_message_id = note.ad_message_id\n"
					+ "where note.ad_client_id = " + clientId + ";";

			pstmt = DB.prepareStatement(sql.toString(), null);
			RS = pstmt.executeQuery();
			while (RS.next()) {
				unAuthorizedCount = RS.getInt("UnAuthorizedCount");
				doorOpenedCount = RS.getInt("DoorOpenedCount");
				doorClosedCount = RS.getInt("DoorClosedCount");
				itemInCount = RS.getInt("ItemInCount");
				itemOutCount = RS.getInt("ItemOutCount");
			}

		} catch (SQLException e) {

			e.printStackTrace();
		} finally {
			DB.close(RS, pstmt);
		}
	}

	@Override
	public void refresh(ServerPushTemplate template) {
		initOptions();
		template.executeAsync(this);
	}

	@Override
	public void updateUI() {
		labelUnAuthorizedCount.setValue(" " + unAuthorizedCount);
		labelDoorOpenedCount.setValue("  " + doorOpenedCount);
		labelDoorClosedCount.setValue(" " + doorClosedCount);
		labelItemInCount.setValue("  " + itemInCount);
		labelItemOutCount.setValue("" + itemOutCount);

	}

	@Override
	public void onEvent(Event event) throws Exception {

	}
}
