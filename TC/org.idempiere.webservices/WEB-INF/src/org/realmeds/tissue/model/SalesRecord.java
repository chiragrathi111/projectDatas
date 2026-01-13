package org.realmeds.tissue.model;

import java.sql.ResultSet;
import java.util.Properties;
import org.realmeds.tissue.custom.SalesData;
import org.realmeds.tissue.custom.TCUtills;
import org.realmeds.tissue.moduller.C_OrderLine_custom;

public class SalesRecord extends C_OrderLine_custom {

    private static final long serialVersionUID = 1L;
    private static final String TABLE_CULTURELABEL = "tc_cultureLabel";

    public SalesRecord(Properties ctx, int C_OrderLine_ID, String trxName) {
        super(ctx, C_OrderLine_ID, trxName);
    }

    public SalesRecord(Properties ctx, ResultSet rs, String trxName) {
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
    	int recordId = get_ID();
        if (recordId != 0) {
        	C_OrderLine_custom lines = new C_OrderLine_custom(getCtx(), recordId, get_TrxName());
            int orderId = lines.getC_Order_ID();
            String cultureUUid = lines.getCultureUUId();
            COrder_Custom_New order = new COrder_Custom_New(getCtx(), orderId, get_TrxName());
            String description = order.getsecondaryhardeninguuid();
            int AD_Client_ID = order.getAD_Client_ID();
            boolean isRooting = order.isRooting();
            if (description != null && !description.isEmpty()) {
                try {
                    SalesData data = new SalesData();
                    data.getData(lines,description);
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
            
            if(isRooting) {
            	if(cultureUUid != null && !cultureUUid.isEmpty()) {
            		try {
            			int cultureId = TCUtills.getValidCultureId(cultureUUid, AD_Client_ID);
            			if(cultureId == 0) {
            				log.saveError("InvalidCulture", 
            	                    "Culture UUID " + cultureUUid + " is either already Sold, Discarded, or not in Rooting stage.");
            				return false;	
            			}
            			SalesData data = new SalesData();
            			data.getCultureData(TABLE_CULTURELABEL, lines, cultureUUid);	
            		}catch (Exception e) {
            			 log.saveError("Error", "Unexpected error while fetching culture: " + e.getMessage());
                        return false;
					}
            	}
            }
            
            if(!isRooting) {
            	if(cultureUUid != null && !cultureUUid.isEmpty()) {
            		log.saveError("Error", "Please check 'Is Rooting' before entering a Rooting Label UUID.");
            		return false;
            	}
            }
        }
        return super.afterSave(newRecord, success);
    }
    
    
}

