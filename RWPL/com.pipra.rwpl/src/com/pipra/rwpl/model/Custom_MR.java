package com.pipra.rwpl.model;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.List;
import java.util.Properties;

import org.compiere.model.MInOut;
import org.compiere.model.MInOutLine;
import org.compiere.model.MLocator;
import org.compiere.model.PO;
import org.compiere.model.Query;

@org.adempiere.base.Model(table = MInOut.Table_Name)
public class Custom_MR extends MInOut {

	private static final long serialVersionUID = 1L;

	public Custom_MR(Properties ctx, int m_inout_ID, String trxName) {
		super(ctx, m_inout_ID, trxName);
		// TODO Auto-generated constructor stub
	}

	public Custom_MR(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public String completeIt() {
		if (!isSOTrx()) {
			MInOutLine[] lines = getLines();
			for (MInOutLine line : lines) {
				int clientId = line.getAD_Client_ID();
				int OrgId = line.getAD_Org_ID();
				int productId = line.getM_Product_ID();

				int locatorId = line.getM_Locator_ID();

				MLocator_Custom locator = new MLocator_Custom(p_ctx, locatorId, get_TrxName());
				MLocatorType_Custom locatorType = new MLocatorType_Custom(p_ctx, locator.get_ID(), get_TrxName());

				if (!locatorType.isReceiving()) {
					List<MLocator> locators = MLocatorType_Custom.getLocatorsByType(getCtx(), get_TrxName(),
							getM_Warehouse_ID(), "receiving", "Y");
					if (locators != null && locators.size() != 0) {
						locatorId = locators.get(0).get_ID();

						line.setM_Locator_ID(locatorId);
						line.saveEx();
					}
				}

				BigDecimal Quantity = line.getQtyEntered();

				String whereClause = "M_InOutLine_ID = ?";
				List<PO> packLines = new Query(getCtx(), Packline.Table_Name, whereClause, get_TrxName())
						.setParameters(line.get_ID()).list();

				if (packLines.isEmpty()) {
					Packline newPackLine = new Packline(getCtx(), 0, get_TrxName());
					newPackLine.setAD_Org_ID(OrgId);
					newPackLine.setM_InOutLine_ID(line.get_ID());
					newPackLine.setlabel("Pack 1");
					newPackLine.setquantity(Quantity);
					newPackLine.saveEx();

					PiProductLabel label = new PiProductLabel(getCtx(), get_TrxName(), clientId, OrgId, productId,
							locatorId, 0, line.get_ID(), false, Quantity, null);
					label.saveEx();

					if (label.get_ID() == 0) {
						return "Material receipt line not created for Product ID: " + productId;
					}
				} else {

					BigDecimal remainingQnty = Quantity;

					int packCount = 0;

					for (PO pack : packLines) {
						Packline lineNew = new Packline(p_ctx, pack.get_ID(), get_TrxName());

						PiProductLabel label = new PiProductLabel(getCtx(), get_TrxName(), clientId, OrgId, productId,
								locatorId, 0, line.get_ID(), false, lineNew.getquantity(), null);
						label.saveEx();

						if (label.get_ID() == 0) {
							return "Material receipt line not created for Product ID: " + productId;
						}

						remainingQnty = remainingQnty.subtract(lineNew.getquantity());

						packCount++;
					}

					if (remainingQnty.compareTo(BigDecimal.valueOf(0)) > 0) {

						Packline lineNew = new Packline(p_ctx, 0, get_TrxName());
						lineNew.setM_InOutLine_ID(line.get_ID());
						lineNew.setAD_Org_ID(line.getAD_Org_ID());
						lineNew.setlabel("Pack " + packCount);
						lineNew.setquantity(remainingQnty);
						lineNew.saveEx();

						PiProductLabel label = new PiProductLabel(getCtx(), get_TrxName(), clientId, OrgId, productId,
								locatorId, 0, line.get_ID(), false, remainingQnty, null);
						label.saveEx();
					}
				}
			}
		}
		return super.completeIt();
	}

}
