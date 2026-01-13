package com.pipra.rwpl.factory;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import org.compiere.model.MBPartner;
import org.compiere.model.MDocType;
import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.compiere.model.MTable;
import org.compiere.model.PO;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Trx;

import com.pipra.rwpl.model.MProduct_Custom;
import com.pipra.rwpl.model.X_pi_planitem;
import com.pipra.rwpl.model.X_pi_salesplanline;

@org.adempiere.base.annotation.Process
public class SalesOrderProcess extends SvrProcess {
	private int[] m_childRecordIDs = null;
	private String[] m_quantities = null;
	private int parentID = 0;

	@Override
	protected void prepare() {
		ProcessInfoParameter[] para = getParameter();
		for (ProcessInfoParameter p : para) {
			if ("SelectedChildRecords".equals(p.getParameterName())) {
				String selectedIDs = (String) p.getParameter();
				if (selectedIDs != null && !selectedIDs.isEmpty()) {
					String[] ids = selectedIDs.split(",");
					m_childRecordIDs = new int[ids.length];
					for (int i = 0; i < ids.length; i++) {
						m_childRecordIDs[i] = Integer.parseInt(ids[i].trim());
					}
				}
			} else if ("Quantities".equals(p.getParameterName())) {
				String quantities = (String) p.getParameter();
				if (quantities != null && !quantities.isEmpty()) {
					m_quantities = quantities.split(",");
				}
			}
		}
	}

	@Override
	protected String doIt() throws Exception {
		if (m_childRecordIDs == null || m_childRecordIDs.length == 0) {
			return "No child records selected";
		}
		parentID = getRecord_ID();

		if (parentID <= 0) {
			return Msg.getMsg(Env.getCtx(), "NoParentRecord");
		}

		Trx trx = Trx.get(get_TrxName(), true);
		try {
			int clientID = Env.getAD_Client_ID(getCtx());
			int userID = Env.getAD_User_ID(getCtx());

			MTable mDocType = MTable.get(Env.getCtx(), "c_doctype");
			PO mDocTypePo = mDocType.getPO("name = 'Standard Order' and ad_client_id = " + clientID + "",
					trx.getTrxName());
			MDocType mDocTypee = (MDocType) mDocTypePo;
			int docTypeId = mDocTypee.get_ID();

			X_pi_salesplanline process = new X_pi_salesplanline(getCtx(), parentID, trx.getTrxName());
			MBPartner busi = new MBPartner(getCtx(), process.getC_BPartner_ID(), trx.getTrxName());
			int warehouseID = process.getM_Warehouse_ID();
			int orgID = process.getAD_Org_ID();

			MOrder order = new MOrder(Env.getCtx(), 0, trx.getTrxName());
			order.setAD_Org_ID(orgID);
			order.setC_DocTypeTarget_ID(docTypeId);
			order.setM_Warehouse_ID(warehouseID);
			order.setAD_User_ID(userID);
			order.setC_BPartner_ID(busi.getC_BPartner_ID());
			order.setIsSOTrx(true);
			order.setPaymentRule("B");

			if (order.save()) {

				Map<Integer, BigDecimal> mergedData = new LinkedHashMap<Integer, BigDecimal>();

				for (int i = 0; i < m_childRecordIDs.length; i++) {
					int id = m_childRecordIDs[i];
					X_pi_planitem planItem = new X_pi_planitem(getCtx(), id, trx.getTrxName());
					int productId = planItem.getM_Product_ID();

					BigDecimal quantity = new BigDecimal(m_quantities[i]);

					if (mergedData.containsKey(productId)) {
						mergedData.put(productId, mergedData.get(productId).add(quantity));
					} else {
						mergedData.put(productId, quantity);
					}

					BigDecimal completedQty = planItem.getcompletedqnty().add(quantity);

					planItem.setcompletedqnty(completedQty);
					planItem.saveEx();

				}

				for (int key : mergedData.keySet()) {
					MProduct_Custom product = new MProduct_Custom(getCtx(), key, trx.getTrxName());
					try {
						BigDecimal qty = mergedData.get(key);

						if (qty.compareTo(BigDecimal.ZERO) <= 0) {
							addLog(0, null, qty, "Invalid quantity for product " + product.getName());
							continue;
						}

						MOrderLine line = new MOrderLine(order);
						line.setM_Product_ID(product.getM_Product_ID());
						line.setQty(qty);
						line.saveEx();

					} catch (Exception e) {
						addLog(0, null, null, "Error processing product " + product.getName() + ": " + e.getMessage());
						log.severe("Process error: " + e.getMessage());
					}
				}
			}
			trx.commit();

			order.setDocAction(MOrder.DOCACTION_Complete);
			order.processIt(MOrder.DOCACTION_Complete);
			order.saveEx();
			trx.commit();
			return "Sales Order #" + " Created with " + m_childRecordIDs.length;
		} catch (Exception e) {
			if (trx != null)
				trx.rollback();
			log.severe("Error in Custom_Sales_Process: " + e.getMessage());
			throw e;
		} finally {
			if (trx != null)
				trx.close();
		}
	}

}
