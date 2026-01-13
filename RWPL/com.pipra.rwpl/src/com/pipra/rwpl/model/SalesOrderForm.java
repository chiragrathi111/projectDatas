package com.pipra.rwpl.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.adempiere.util.ProcessUtil;
import org.adempiere.webui.component.Grid;
import org.adempiere.webui.component.Rows;
import org.adempiere.webui.panel.ADForm;
import org.compiere.model.GridTab;
import org.compiere.model.MProcess;
import org.compiere.model.MStorageReservation;
import org.compiere.model.PO;
import org.compiere.model.Query;
import org.compiere.process.ProcessInfo;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.util.CLogger;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Trx;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vbox;

public class SalesOrderForm extends ADForm {

	private static final long serialVersionUID = 1L;
	private List<Checkbox> checkboxes = new ArrayList<>();
	private List<Integer> childIDs = new ArrayList<>();
	private List<Textbox> quantityFields = new ArrayList<>();
	private int m_parentID;
	private Button ok;
	private Button cancel;
	CLogger log = CLogger.getCLogger(SalesOrderForm.class);

	@Override
	protected void initForm() {
		try {
			GridTab gridTab = getGridTab();
			if (gridTab != null) {
				Object value = gridTab.getValue("pi_salesplanline_id");
				if (value != null && value instanceof Integer) {
					m_parentID = (Integer) value;
				}
			}

			if (m_parentID <= 0) {
				m_parentID = Env.getContextAsInt(Env.getCtx(), getWindowNo(), "Record_ID");
			}

			if (m_parentID <= 0) {
				Messagebox.show("No parent record selected!", "Error", Messagebox.OK, Messagebox.ERROR);
				detach();
				return;
			}

			Vbox vbox = new Vbox();
			this.appendChild(vbox);

			Label title = new Label(Msg.getMsg(Env.getCtx(), "SelectItemsToProcess"));
			vbox.appendChild(title);

			Grid grid = new Grid();
			grid.setWidth("100%");
			Rows rows = new Rows();
			grid.appendChild(rows);
			vbox.appendChild(grid);

			Row headerRow = new Row();
			headerRow.appendChild(new Label("SelectProduct"));
			headerRow.appendChild(new Label("ProductQty"));
			headerRow.appendChild(new Label("Quantity"));
			rows.appendChild(headerRow);

			Query query = new Query(Env.getCtx(), X_pi_planitem.Table_Name,
					"pi_salesplanline_id=? AND totalQnty != completedQnty", null);

			List<PO> children = query.setParameters(m_parentID).list();

			for (PO child : children) {
				Row row = new Row();
				X_pi_planitem planItem = new X_pi_planitem(Env.getCtx(), child.get_ID(), null);
				MProduct_Custom product = new MProduct_Custom(Env.getCtx(), planItem.getM_Product_ID(), null);
				Checkbox cb = new Checkbox(product.getName());
				checkboxes.add(cb);
				childIDs.add(child.get_ID());
				
				int totalQty = planItem.gettotalqnty().intValue();
				int completedQty = planItem.getcompletedqnty().intValue();
				BigDecimal pendingQty = BigDecimal.valueOf(totalQty - completedQty);

				X_pi_salesplanline salesLine = new X_pi_salesplanline(Env.getCtx(), m_parentID, null);
				int qnty = MStorageReservation.getQtyAvailable(salesLine.getM_Warehouse_ID(),
						product.getM_Product_ID(), 0, null).intValue();
				
				BigDecimal textBoxQnty = pendingQty;
				if(textBoxQnty.intValue() >= qnty)
					textBoxQnty = BigDecimal.valueOf(qnty);
				
				Label productQty = new Label(pendingQty.toString());

				String quantity = textBoxQnty.toString();
				Textbox qtyTextbox = new Textbox();
				qtyTextbox.setValue(quantity);
				qtyTextbox.setWidth("80px");
				qtyTextbox.addEventListener("onChange", event -> {
					try {
						int value = Integer.parseInt(qtyTextbox.getValue());
						
						if (value < 0 || value > pendingQty.intValue()) {
							qtyTextbox.setValue(quantity);
							Messagebox.show("Please enter a value between 0 and " + quantity + ".");
						}
						BigDecimal available = MStorageReservation.getQtyAvailable(salesLine.getM_Warehouse_ID(),
								product.getM_Product_ID(), 0, null);
						
						int availableQnty = available.intValue();
					 if(value > availableQnty) 
						 Messagebox.show("Insufficient Quantity, Available Quantity: " + availableQnty + ".");
						
					} catch (NumberFormatException e) {
						qtyTextbox.setValue("0");
						Messagebox.show("Please enter a valid integer.");
					}
				});

				quantityFields.add(qtyTextbox);

				row.appendChild(cb);
				row.appendChild(productQty);
				row.appendChild(qtyTextbox);
				rows.appendChild(row);
			}

			ok = new Button(Msg.getMsg(Env.getCtx(), "OK"));
			ok.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
				public void onEvent(Event event) {
					processSelection();
				}
			});

			cancel = new Button(Msg.getMsg(Env.getCtx(), "Cancel"));
			cancel.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
				public void onEvent(Event event) {
					detach();
				}
			});

			vbox.appendChild(ok);
			vbox.appendChild(cancel);

		} catch (Exception e) {
			log.severe("error: " + e.getMessage());
			Messagebox.show("Error: " + e.getMessage(), "Error", Messagebox.OK, Messagebox.ERROR);
			detach();
		}
	}

	private void processSelection() {
		Trx localTrx = null;
		try {
			localTrx = Trx.get(Trx.createTrxName("SalesOrderProcess"), true);
			Env.setContext(Env.getCtx(), "#AD_TrxName", localTrx.getTrxName());

			StringBuilder selectedIDs = new StringBuilder();
			StringBuilder quantities = new StringBuilder();

			for (int i = 0; i < checkboxes.size(); i++) {
				if (checkboxes.get(i).isChecked()) {
					if (selectedIDs.length() > 0) {
						selectedIDs.append(",");
						quantities.append(",");
					}
					selectedIDs.append(childIDs.get(i));
					quantities.append(quantityFields.get(i).getValue());
				}
			}

			if (selectedIDs.length() == 0) {
				Messagebox.show("Please select at least one item", "Warning", Messagebox.OK, Messagebox.EXCLAMATION);
				return;
			}

			ProcessInfo pi = getProcessInfo();
			pi.setClassName("com.pipra.rwpl.factory.SalesOrderProcess");
			pi.setRecord_ID(m_parentID);
			pi.setParameter(new ProcessInfoParameter[] {
					new ProcessInfoParameter("SelectedChildRecords", selectedIDs.toString(), null, null, null),
					new ProcessInfoParameter("Quantities", quantities.toString(), null, null, null) });

			MProcess process = MProcess.get(Env.getCtx(), pi.getAD_Process_ID());
			if (process == null || process.getAD_Process_ID() == 0) {
				Messagebox.show("Process not found", "Error", Messagebox.OK, Messagebox.ERROR);
				return;
			}

			log.info("Executing Process - ID: " + process.getAD_Process_ID() + ", Name: " + process.getName()
					+ ", Procedure: " + process.getProcedureName());

			if (process.isDatabaseProcedure()) {
				if (process.getProcedureName() == null || process.getProcedureName().trim().isEmpty()) {
					Messagebox.show("Process procedure is not defined", "Error", Messagebox.OK, Messagebox.ERROR);
					return;
				}
			}

			boolean success = false;
			if (process.isDatabaseProcedure()) {
				if (process.getProcedureName() == null || process.getProcedureName().trim().isEmpty()) {
					Messagebox.show("Process procedure is not defined", "Error", Messagebox.OK, Messagebox.ERROR);
					return;
				}
				success = ProcessExecuter.executeProcess(process, pi);
			} else {
				success = ProcessUtil.startJavaProcess(Env.getCtx(), pi, localTrx, true);
			}
			if (success) {
				localTrx.commit();
				Messagebox.show("Process completed successfully", "Success", Messagebox.OK, Messagebox.INFORMATION);
			} else {
				localTrx.rollback();
				String errorMsg = pi.getSummary() != null ? pi.getSummary() : "Unknown error occurred";
				Messagebox.show("Process failed: " + errorMsg, "Error", Messagebox.OK, Messagebox.ERROR);
				log.severe("Process failed: " + errorMsg);
				System.out.println(pi.getSummary());
			}
			detach();
		} catch (Exception e) {
			if (localTrx != null)
				localTrx.rollback();
			e.printStackTrace();
			log.severe("Process error: " + e.getMessage());
			Messagebox.show("Processing failed: " + e.getMessage(), "Error", Messagebox.OK, Messagebox.ERROR);
		} finally {
			if (localTrx != null)
				localTrx.close();
		}
	}
}
