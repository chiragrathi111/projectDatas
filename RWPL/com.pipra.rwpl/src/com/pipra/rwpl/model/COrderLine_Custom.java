package com.pipra.rwpl.model;

import java.sql.ResultSet;
import java.util.List;
import java.util.Properties;

import org.compiere.model.I_C_OrderLine;
import org.compiere.model.MOrderLine;
import org.compiere.model.PO;
import org.compiere.model.Query;
import org.zkoss.zul.Messagebox;

public class COrderLine_Custom extends MOrderLine {

	private static final long serialVersionUID = 1L;

	public COrderLine_Custom(Properties ctx, int C_OrderLine_ID, String trxName) {
		super(ctx, C_OrderLine_ID, trxName);
	}

	public COrderLine_Custom(Properties ctx, ResultSet resultSet, String trxName) {
		super(ctx, resultSet, trxName);
	}

	@Override
	protected boolean beforeSave(boolean newRecord) {

		if (getC_Order().isSOTrx()) {
			List<PO> list = getLinesForCurrentOrder();
			boolean productExists = false;

			if (list != null && list.size() > 0)
				for (PO line : list) {

					COrderLine_Custom line_Custom = new COrderLine_Custom(p_ctx, line.get_ID(), get_TrxName());
					if (getM_Product_ID() == line_Custom.getM_Product_ID()) {

						Messagebox.show("Line With Same Product Exists", "Error", Messagebox.OK, Messagebox.ERROR);

						productExists = true;
						break;
					}
				}

			if (!productExists)
				return super.beforeSave(newRecord);
			else
				return false;
		} else
			return super.beforeSave(newRecord);
	}

	public List<PO> getLinesForCurrentOrder() {
		StringBuilder whereClauseFinal = new StringBuilder(COrderLine_Custom.COLUMNNAME_C_Order_ID + "=? ");
		if(get_ID() != 0) {
			whereClauseFinal.append(" AND "+COrderLine_Custom.COLUMNNAME_C_OrderLine_ID +" != "+get_ID()+"");
		}

		List<PO> list = new Query(getCtx(), I_C_OrderLine.Table_Name, whereClauseFinal.toString(), null)
				.setParameters(getC_Order_ID()).list();

		return list;
	}

}