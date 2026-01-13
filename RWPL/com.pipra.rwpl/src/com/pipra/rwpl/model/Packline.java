package com.pipra.rwpl.model;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.compiere.model.PO;
import org.compiere.model.Query;
import org.compiere.util.DB;
import org.compiere.util.Env;

//@SuppressWarnings("serial")
public class Packline extends X_m_packline{

	private static final long serialVersionUID = 1L;
	
	public Packline(Properties ctx, int m_packline_ID, String trxName) {
		super(ctx, m_packline_ID, trxName);
		// TODO Auto-generated constructor stub
	}
	
	public Packline(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
		// TODO Auto-generated constructor stub
	}
	
	public Packline(Properties ctx, int m_packline_ID, String trxName, String[] virtualColumns) {
		super(ctx, m_packline_ID, trxName, virtualColumns);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected boolean beforeDelete() {
		// TODO Auto-generated method stub
		return super.beforeDelete();
	}
	
	@Override
	protected boolean beforeSave(boolean newRecord) {
		int parentLineId = get_ValueAsInt("M_InOutLine_ID");

	    if (parentLineId > 0) {
	        BigDecimal parentQty = DB.getSQLValueBD(
	            get_TrxName(),
	            "SELECT qtyentered FROM M_InOutLine WHERE M_InOutLine_ID = ?",
	            parentLineId
	        );

	        if (parentQty == null)
	            parentQty = Env.ZERO;

	        BigDecimal existingQty = Env.ZERO;

	        String whereClause = "M_InOutLine_ID = ?";
	        List<PO> existingLines = new Query(getCtx(), get_TableName(), whereClause, get_TrxName())
	            .setParameters(parentLineId)
	            .list();

	        for (PO line : existingLines) {
	        	Packline pLine = new Packline(p_ctx, line.get_ID(), whereClause);
	            if (!newRecord && pLine.get_ID() == get_ID()) {
	                continue;
	            }
	            BigDecimal qty = (BigDecimal) line.get_Value("quantity");
	            if (qty != null)
	                existingQty = existingQty.add(qty);
	        }

	        BigDecimal currentQty = (BigDecimal) get_Value("quantity");
	        if (currentQty.intValue() == 0) {
	        	log.saveError("Error", "Entered quantity 0 not possible");
	            return false;
	        }
	            existingQty = existingQty.add(currentQty);

	        if (existingQty.compareTo(parentQty) > 0) {
	            log.saveError("Error", "Entered quantity exceeds the available quantity (" + parentQty + ")");
	            return false;
	        }

	        if (newRecord) {
	            int count = existingLines.size();
	            set_ValueOfColumn("label", "Pack " + (count + 1));
	        }
	    }
	    return true;
	}
	
	@Override
	protected boolean afterDelete(boolean success) {
		if (!success) return false;

	    int parentLineId = get_ValueAsInt("M_InOutLine_ID");

	    List<PO> packLines = new Query(getCtx(), get_TableName(), 
	        "M_InOutLine_ID = ?", get_TrxName())
	        .setParameters(parentLineId)
	        .setOrderBy("Created")
	        .list();

	    int counter = 1;
	    for (PO line : packLines) {
	        line.set_ValueOfColumn("label", "Pack " + counter++);
	        line.saveEx();
	    }

	    return true;
	}
	
	@Override
	protected boolean afterSave(boolean newRecord, boolean success) {
		
		return super.afterSave(newRecord, success);
	}
	
	public static List<Packline> getPackLines(Properties ctx, String trxName, int minoutLineId) {

		List<Packline> packlineList = new ArrayList<Packline>();
		List<PO> list = new Query(ctx, "m_packline", "M_InOutLine_ID = ?", trxName).setParameters(minoutLineId)
				.setOnlyActiveRecords(true).list();
		if (list != null && list.size() > 0) {
			for (PO po : list) {
				packlineList.add(new Packline(ctx, po.get_ID(), trxName));
			}
		}
		return packlineList;
	}

	public static void deletepackLine(Properties ctx, String trxName, int minoutLineId) {
		List<Packline> packlineList = getPackLines(ctx, trxName, minoutLineId);
		for (Packline line : packlineList) {
			line.delete(false);
		}
	}

}
