package org.realmeds.tissue.model;

import java.sql.ResultSet;
import java.util.Properties;
import org.compiere.model.MInvoiceLine;
import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.realmeds.tissue.custom.SalesData;

public class TCInvoiceLine extends MInvoiceLine{
	
	private static final long serialVersionUID = 1L;

    public TCInvoiceLine(Properties ctx, int C_InvoiceLine_ID, String trxName) {
        super(ctx, C_InvoiceLine_ID, trxName);
    }

    public TCInvoiceLine(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }

    @Override
    protected boolean beforeDelete() {
        return super.beforeDelete();
    }

    @Override
    protected boolean beforeSave(boolean newRecord) {
        return super.beforeSave(newRecord);
    }

    @Override
    protected boolean afterDelete(boolean success) {
        return super.afterDelete(success);
    }
    
    @Override
    protected boolean afterSave(boolean newRecord, boolean success) {
//      int recordId = get_ID();
//      if (recordId != 0) {
//          MInvoiceLine line = new MInvoiceLine(getCtx(), recordId, get_TrxName());
//          int orderlineId = line.getC_OrderLine_ID();
//          MOrderLine lines = new MOrderLine(getCtx(), orderlineId, get_TrxName());
//          int orderId = lines.getC_Order_ID();
//          MOrder order = new MOrder(getCtx(), orderId, get_TrxName());
//          String description = order.getDescription();
//          if (description != "") {
//              try {
//                  SalesData data = new SalesData();
//                  data.getData(lines,description);
//              } catch (Exception e) {
//                  e.printStackTrace();
//                  return false;
//              }
//          }
//      }
    	return super.afterSave(newRecord, success);
    }


}
