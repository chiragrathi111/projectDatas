package org.adempiere.webui.dashboard;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.adempiere.webui.component.Label;
import org.adempiere.webui.desktop.IDesktop;
import org.adempiere.webui.theme.ITheme;
import org.adempiere.webui.util.ServerPushTemplate;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zul.Div;

public class DPTransctions extends DashboardPanel implements EventListener<Event> {

	private Label labelInoutCount;
	private String inoutCountText;
	private Label inoutCountLabelText;

	private Label labelInCount;
	private String inCountText;
	private Label inCountLabelText;

	private Label labelOutCount;
	private String outCountText;
	private Label outCountLabelText;

	private Label labelInternalMoveCount;
	private String internalMoveCountText;
	private Label internalMoveCountLabelText;

	private Label labelQaRejectionsCount;
	private String qaRejectionsText;
	private Label qaRejectionsLabelText;

	private static final long serialVersionUID = 1L;
	private String inoutCount;
	private String inCount;
	private String outCount;
	private String internalMoveCount;
	private String qaRejections;
	List<String> warehouseNames = new ArrayList<String>();

	public DPTransctions() {
		super();
		this.setSclass("activities-box");
		initOptions();
		this.appendChild(createActivitiesPanel());
	}

	private Div createActivitiesPanel() {
		Div div = new Div();
		div.setSclass(ITheme.WARE_HOUSE_DATA_WIDGET);

		Div childDivFirst = new Div();

		inoutCountLabelText = new Label();
		inoutCountLabelText.setSclass(ITheme.DASHBOARD_WIDGET_LABELS);
		inoutCountText = "Receipts";
		inoutCountLabelText.setValue(inoutCountText);
		childDivFirst.appendChild(inoutCountLabelText);

		labelInoutCount = new Label();
		labelInoutCount.setSclass(ITheme.DASHBOARD_WIDGET_COUNT);
		labelInoutCount.setValue(" " + inoutCount);
		childDivFirst.appendChild(labelInoutCount);

		childDivFirst.setSclass(ITheme.DASHBOARD_WIDGET_Text_COUNT_CONT);
		div.appendChild(childDivFirst);

		Div childDivSec = new Div();
		inCountLabelText = new Label();
		inCountLabelText.setSclass(ITheme.DASHBOARD_WIDGET_LABELS);
		inCountText = "Picks";
		inCountLabelText.setValue(inCountText);
		childDivSec.appendChild(inCountLabelText);

		labelInCount = new Label();
		labelInCount.setSclass(ITheme.DASHBOARD_WIDGET_COUNT);
		childDivSec.appendChild(labelInCount);
		labelInCount.setValue(" " + inCount);

		childDivSec.setSclass(ITheme.DASHBOARD_WIDGET_Text_COUNT_CONT);
		div.appendChild(childDivSec);

		Div childDivThird = new Div();

		outCountLabelText = new Label();
		outCountLabelText.setSclass(ITheme.DASHBOARD_WIDGET_LABELS);
		outCountText = "Dispatches";
		outCountLabelText.setValue(outCountText);
		childDivThird.appendChild(outCountLabelText);

		labelOutCount = new Label();
		labelOutCount.setSclass(ITheme.DASHBOARD_WIDGET_COUNT);
		labelOutCount.setValue("  " + outCount);
		childDivThird.appendChild(labelOutCount);

		childDivThird.setSclass(ITheme.DASHBOARD_WIDGET_Text_COUNT_CONT);
		div.appendChild(childDivThird);

		Div childDivFour = new Div();
		internalMoveCountLabelText = new Label();
		internalMoveCountLabelText.setSclass(ITheme.DASHBOARD_WIDGET_LABELS);
		internalMoveCountText = "Internal Moves";
		internalMoveCountLabelText.setValue(internalMoveCountText);
		childDivFour.appendChild(internalMoveCountLabelText);

		labelInternalMoveCount = new Label();
		labelInternalMoveCount.setSclass(ITheme.DASHBOARD_WIDGET_COUNT);
		childDivFour.appendChild(labelInternalMoveCount);
		labelInternalMoveCount.setValue(" " + internalMoveCount);

		childDivFour.setSclass(ITheme.DASHBOARD_WIDGET_Text_COUNT_CONT);
		div.appendChild(childDivFour);

		Div childDivFive = new Div();
		qaRejectionsLabelText = new Label();
		qaRejectionsLabelText.setSclass(ITheme.DASHBOARD_WIDGET_LABELS);
		qaRejectionsText = "QA Rejections";
		qaRejectionsLabelText.setValue(qaRejectionsText);
		childDivFive.appendChild(qaRejectionsLabelText);

		labelQaRejectionsCount = new Label();
		labelQaRejectionsCount.setValue("  " + qaRejections);
		labelQaRejectionsCount.setSclass(ITheme.DASHBOARD_WIDGET_COUNT);
		childDivFive.appendChild(labelQaRejectionsCount);

		childDivFive.setSclass(ITheme.DASHBOARD_WIDGET_Text_COUNT_CONT);
		div.appendChild(childDivFive);

		return div;
	}

	private void initOptions() {

		Properties ctx = Env.getCtx();
		int clientId = Env.getAD_Client_ID(ctx);
		int wareHouseId = DPWarehouseSelection.getWareHouse_ID(ctx);
		int productId = DPWarehouseSelection.getProduct_ID(ctx);

		PreparedStatement pstmt = null;
		ResultSet RS = null;
		try {
			String sql = null;
			if (wareHouseId == 0 && productId == 0) {
				sql = "SELECT COUNT(DISTINCT mi) AS inoutCount,\n"
						+ "(SELECT COUNT(a) FROM adempiere.m_inout a WHERE a.movementtype = 'V+' AND a.ad_client_id = "+ clientId +" AND DATE(a.created) = DATE(NOW()) AND a.docstatus = 'CO') AS inCount,\n"
						+ "(SELECT COUNT(b) FROM adempiere.m_inout b WHERE b.movementtype = 'C-' AND b.ad_client_id = "+ clientId +" AND DATE(b.created) = DATE(NOW()) AND b.docstatus = 'CO') AS outCount,\n"
						+ "(SELECT COUNT(DISTINCT c) FROM adempiere.m_movement c WHERE DATE(c.created) = DATE(NOW()) AND c.ad_client_id = "+ clientId +" AND c.docstatus = 'CO') AS internalMoveCount, \n"
						+ "(SELECT COUNT(d) FROM adempiere.m_inoutlineconfirm d JOIN m_inoutconfirm mic on mic.m_inoutconfirm_id = d.m_inoutconfirm_id WHERE qcfailedqty != 0.00 AND d.ad_client_id = "+ clientId +" AND DATE(d.created) = DATE(NOW()) AND mic.docstatus = 'CO') AS qc\n"
						+ "FROM adempiere.m_inout mi WHERE DATE(mi.created) = DATE(NOW()) AND mi.ad_client_id = "+ clientId +" AND mi.docstatus = 'CO';";
			} else if (wareHouseId != 0 && productId == 0) {
				sql = "SELECT COUNT(DISTINCT mi) AS inoutCount,\n"
						+ "(SELECT COUNT(a) FROM adempiere.m_inout a WHERE a.movementtype = 'V+' AND a.ad_client_id = "+ clientId +" AND a.m_warehouse_id = "+ wareHouseId +" AND DATE(a.created) = DATE(NOW()) AND a.docstatus = 'CO') AS inCount,\n"
						+ "(SELECT COUNT(b) FROM adempiere.m_inout b WHERE b.movementtype = 'C-' AND b.ad_client_id = "+ clientId +" AND b.m_warehouse_id = "+ wareHouseId +" AND DATE(b.created) = DATE(NOW()) AND b.docstatus = 'CO') AS outCount,\n"
						+ "(SELECT COUNT(DISTINCT c) FROM adempiere.m_movement c WHERE DATE(c.created) = DATE(NOW()) AND c.ad_client_id = "+ clientId +" AND c.m_warehouse_id = "+ wareHouseId +" AND c.docstatus = 'CO') AS internalMoveCount,\n"
						+ "(SELECT COUNT(d) FROM adempiere.m_inoutlineconfirm d JOIN m_inoutconfirm mic on mic.m_inoutconfirm_id = d.m_inoutconfirm_id JOIN adempiere.m_inoutline ili ON ili.m_inoutline_id = d.m_inoutline_id \n"
						+ "JOIN adempiere.m_inout ii ON ii.m_inout_id = ili.m_inout_id JOIN adempiere.m_warehouse whh ON whh.m_warehouse_id = ii.m_warehouse_id \n"
						+ "WHERE d.qcfailedqty != 0.00 AND d.ad_client_id = "+ clientId +"  AND mic.docstatus = 'CO' AND whh.m_warehouse_id = "+ wareHouseId +" AND DATE(d.created) = DATE(NOW())) AS qc\n"
						+ "FROM adempiere.m_inout mi \n"
						+ "WHERE DATE(mi.created) = DATE(NOW()) \n"
						+ "AND mi.m_warehouse_id = "+ wareHouseId +" \n"
						+ "AND mi.ad_client_id = "+ clientId +" AND mi.docstatus = 'CO';";
			} else if (wareHouseId == 0 && productId != 0) {
				sql = "SELECT COUNT(DISTINCT mi) as inoutCount,\n"
						+ "(SELECT COUNT(a) FROM m_inout a JOIN m_inoutline aa ON aa.m_inout_id = a.m_inout_id WHERE a.movementtype = 'V+' AND a.ad_client_id = "+ clientId +" AND aa.M_product_id = "+productId+" AND DATE(a.created) = DATE(NOW()) AND a.docstatus = 'CO') as inCount,\n"
						+ "(SELECT COUNT(a) FROM m_inout a JOIN m_inoutline aa ON aa.m_inout_id = a.m_inout_id WHERE a.movementtype = 'C-' AND a.ad_client_id = "+ clientId +" AND aa.m_product_id = "+productId+" AND DATE(a.created) = DATE(NOW()) AND a.docstatus = 'CO') as outCount,\n"
						+ "(SELECT COUNT(DISTINCT c) FROM m_movement c JOIN m_movementline cc ON cc.m_movement_id = c.m_movement_id WHERE DATE(c.created) = DATE(NOW()) AND c.ad_client_id = "+ clientId +" AND cc.m_product_id = "+productId+" AND c.docstatus = 'CO') as internalMoveCount,\n"
						+ "(SELECT COUNT(d) FROM adempiere.m_inoutlineconfirm d JOIN m_inoutconfirm mic on mic.m_inoutconfirm_id = d.m_inoutconfirm_id JOIN adempiere.m_inoutline ili ON ili.m_inoutline_id = d.m_inoutline_id JOIN adempiere.m_inout ii ON ii.m_inout_id = ili.m_inout_id\n"
						+ "JOIN adempiere.m_product wh ON wh.m_product_id = ili.m_product_id WHERE d.qcfailedqty != 0.00 AND d.ad_client_id = "+ clientId +" AND wh.m_product_id = "+productId+" AND DATE(d.created) = DATE(NOW()) AND mic.docstatus = 'CO') AS qc\n"
						+ "FROM m_inout mi JOIN m_inoutline mil ON mil.m_inout_id = mi.m_inout_id WHERE DATE(mi.created) = DATE(NOW()) AND mi.ad_client_id = "+clientId+" AND mil.m_product_id = "+productId+" AND mi.docstatus = 'CO';";
			}
			pstmt = DB.prepareStatement(sql.toString(), null);
			RS = pstmt.executeQuery();
			while (RS.next()) {
				inoutCount = RS.getString("inoutCount");
				inCount = RS.getString("inCount");
				outCount = RS.getString("outCount");
				internalMoveCount = RS.getString("internalMoveCount");
				qaRejections = RS.getString("qc");
			}
			DB.close(RS, pstmt);
			RS = null;
			pstmt = null;

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void refresh(ServerPushTemplate template) {
		initOptions();
		template.executeAsync(this);
	}

	@Override
	public void updateUI() {
		labelInoutCount.setValue("  " + inoutCount);
		labelInCount.setValue("  " + inCount);
		labelOutCount.setValue(" " + outCount);
		labelInternalMoveCount.setValue(" " + internalMoveCount);
		labelQaRejectionsCount.setValue("  " + qaRejections);
		EventQueue<Event> queue = EventQueues.lookup(IDesktop.ACTIVITIES_EVENT_QUEUE, true);
		Event event = new Event(IDesktop.ON_ACTIVITIES_CHANGED_EVENT, null,
				inoutCount + inCount + outCount + internalMoveCount + qaRejections);
		queue.publish(event);

	}

	@Override
	public void onEvent(Event event) throws Exception {

	}
}
