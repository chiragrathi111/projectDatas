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

public class DPAverageTime extends DashboardPanel implements EventListener<Event> {

	private Label labelOrdersProcessedCount;
	private Label days;
	private String averageTime;

	private static final long serialVersionUID = 1L;

	public DPAverageTime() {
		super();
		this.setSclass("activities-box");
		this.appendChild(createActivitiesPanel());
		initOptions();
//		updateDashboardData();
	}

	private Div createActivitiesPanel() {
		Div div = new Div();
		div.setSclass(ITheme.WARE_HOUSE_DATA_WIDGET);
		Div ordersDiv = new Div();
		Label ordersLabelText = new Label("Average Time");
		ordersLabelText.setSclass(ITheme.DASHBOARD_WIDGET_LABELS);
		ordersDiv.appendChild(ordersLabelText);

		labelOrdersProcessedCount = new Label();
		labelOrdersProcessedCount.setSclass(ITheme.DASHBOARD_WIDGET_COUNT);
		ordersDiv.appendChild(labelOrdersProcessedCount);

		days = new Label("Days");
		ordersDiv.appendChild(days);
		ordersDiv.setSclass(ITheme.DASHBOARD_WIDGET_Text_COUNT_CONT);
		div.appendChild(ordersDiv);
		return div;
	}

	private void initOptions() {
		updateDashboardData();
		updateUI();
	}

	@Override
	public void onEvent(Event event) throws Exception {
	}

	private void updateDashboardData() {
		Properties ctx = Env.getCtx();
		int clientId = Env.getAD_Client_ID(ctx);

		String query = "WITH \n" + "RootingDate AS (\n"
				+ "SELECT clr.c_uuid AS rootuuid, clr.parentuuid AS rootingparentuuid, clr.updated AS rooting_date \n"
				+ "FROM adempiere.tc_culturelabel clr WHERE clr.ad_client_id = " + clientId + "),\n"
				+ "ElongationDate AS (\n"
				+ "SELECT cle.c_uuid AS elongationuuid, cle.parentuuid AS elongationparentuuid, cle.updated AS elongation_date FROM adempiere.tc_culturelabel cle\n"
				+ "JOIN adempiere.tc_culturelabel cl ON cle.c_uuid = cl.parentuuid WHERE cle.ad_client_id = " + clientId
				+ "),\n" + "multi2Date AS (\n"
				+ "SELECT clm2.c_uuid AS multi2uuid, clm2.parentuuid AS multi2parentuuid, clm2.updated AS multi2_date FROM adempiere.tc_culturelabel clm2\n"
				+ "JOIN adempiere.tc_culturelabel cl ON clm2.c_uuid = cl.parentuuid WHERE clm2.ad_client_id = "
				+ clientId + "),\n" + "multi1Date AS (\n"
				+ "SELECT clm1.c_uuid AS multi1uuid, clm1.parentuuid AS multi1parentuuid, clm1.updated AS multi1_date FROM adempiere.tc_culturelabel clm1\n"
				+ "JOIN adempiere.tc_culturelabel cl ON clm1.c_uuid = cl.parentuuid WHERE clm1.ad_client_id = "
				+ clientId + "),\n" + "initial2cultureDate AS (\n"
				+ "SELECT cl2.c_uuid AS initial2cultureuuid, cl2.parentuuid AS ini2cultureparentuuid, cl2.updated AS ini2culture_date FROM adempiere.tc_culturelabel cl2\n"
				+ "JOIN adempiere.tc_culturelabel cl ON cl2.c_uuid = cl.parentuuid WHERE cl2.ad_client_id = " + clientId
				+ "),\n" + "initialcultureDates AS (\n"
				+ "SELECT cl.c_uuid AS initialcultureuuid, cl.parentuuid AS inicultureparentuuid, cl.updated AS iniculture_date FROM adempiere.tc_culturelabel cl\n"
				+ "JOIN adempiere.tc_culturelabel cll ON cl.c_uuid = cll.parentuuid WHERE cl.ad_client_id = " + clientId
				+ "),\n" + "explantDates AS (\n"
				+ "SELECT el.c_uuid AS explantuuid, el.parentuuid AS parentuuid, el.updated AS explant_date FROM adempiere.tc_explantlabel el\n"
				+ "JOIN adempiere.tc_culturelabel cl ON el.c_uuid = cl.parentuuid WHERE el.ad_client_id = " + clientId
				+ "),\n" + "SuckerCollectionDates AS (\n"
				+ "SELECT pt.planttaguuid AS planttaguuid, sc.tc_plantdetails_id, sc.suckerNo, sc.updated AS sucker_collection_date \n"
				+ "FROM adempiere.tc_collectionjoinplant sc JOIN adempiere.tc_plantdetails pt ON pt.tc_plantdetails_id = sc.tc_plantdetails_id\n"
				+ "JOIN adempiere.tc_explantlabel el ON el.parentuuid = pt.planttaguuid WHERE sc.ad_client_id = "
				+ clientId + ") \n"
				+ "SELECT DISTINCT r.rootuuid,sc.planttaguuid,r.rooting_date,ed.elongation_date,m2.multi2_date,m1.multi1_date,\n"
				+ "i2.ini2culture_date,i1.iniculture_date,el.explant_date,sc.sucker_collection_date,\n"
				+ "ROUND(EXTRACT(EPOCH FROM (r.rooting_date - sc.sucker_collection_date)) / 86400.0) AS duration_days,\n"
				+ "ROUND(AVG(ROUND(EXTRACT(EPOCH FROM (r.rooting_date - sc.sucker_collection_date)) / 86400.0)) OVER ()) AS avg_duration_days\n"
				+ "FROM RootingDate r\n"
				+ "left JOIN ElongationDate ed ON ed.elongationuuid = r.rootingparentuuid    \n"
				+ "left JOIN multi2Date m2 ON m2.multi2uuid = COALESCE(ed.elongationparentuuid, r.rootingparentuuid)\n"
				+ "left JOIN multi1Date m1 ON m1.multi1uuid = COALESCE(m2.multi2parentuuid,ed.elongationparentuuid, r.rootingparentuuid)\n"
				+ "left JOIN initial2cultureDate i2 ON i2.initial2cultureuuid = COALESCE(m1.multi1parentuuid,m2.multi2parentuuid,ed.elongationparentuuid, r.rootingparentuuid)	\n"
				+ "JOIN initialcultureDates i1 ON i1.initialcultureuuid = COALESCE(i2.ini2cultureparentuuid, m1.multi1parentuuid, m2.multi2parentuuid, r.rootingparentuuid)	\n"
				+ "JOIN explantDates el ON el.explantuuid = i1.inicultureparentuuid   \n"
				+ "JOIN SuckerCollectionDates sc ON sc.planttaguuid = el.parentuuid limit 1;";

		try (PreparedStatement pstmt = DB.prepareStatement(query, null)) {

			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				averageTime = String.valueOf(rs.getInt("avg_duration_days"));
			} else {
				averageTime = "No Record Found";
				days.setVisible(false); // Hide the "Days" label
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void refresh(ServerPushTemplate template) {
		updateDashboardData();
		template.executeAsync(this);
	}

	@Override
	public void updateUI() {
		if (labelOrdersProcessedCount != null) {
			labelOrdersProcessedCount.setValue(" " + averageTime);
		}
	}
}