package org.idempiere.webservices.process;

import org.adempiere.exceptions.AdempiereException;
import org.adempiere.model.GenericPO;
import org.compiere.model.PO;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Trx;

public class ButtonTesting extends SvrProcess {
	private static final String TABLE_PLANT = "tc_planttag";
	private Trx trx = null;
	@Override
	protected void prepare() {
	}

	@Override
	protected String doIt() throws Exception {
		String trxName = Trx.createTrxName(getClass().getName() + "_");
		trx = Trx.get(trxName, true);
		trx.start();

        try {
            PO po = getPO();

            if (po == null || po.get_ID() == 0) {
                po = new GenericPO(TABLE_PLANT, getCtx(), 0, get_TrxName()); // New record
            }

            if (!po.save()) {
                throw new AdempiereException("Failed to save the record: ");
            }
            int recordId = po.get_ID();

            trx.commit();
   			int retryCount = 0;
   			String PlantUUId = null;
   			while (PlantUUId == null && retryCount < 10) {
   				PlantUUId = DB.getSQLValueString(null,
   						"SELECT c_uuid FROM adempiere."+ TABLE_PLANT +" WHERE tc_planttag_id = ?", recordId);
   				if (PlantUUId == null) {
   					Thread.sleep(2000); // Wait for 500ms before retrying
   					retryCount++;
   				}
   			}
   			if (PlantUUId == null) {
   				throw new Exception("Failed to get UUID for Plant Tag: " + recordId);
   			}

            return "Record saved success";
        } catch (Exception e) {
            trx.rollback();
            throw new AdempiereException("Error saving record: " + e.getMessage());
        } finally {
            trx.close();
        }
	}
	
	private PO getPO() throws AdempiereException {
        int recordId = getRecord_ID();  // Retrieve current record ID from the context (can be 0 if new)
        
        if (recordId == 0) {
            return null;
        }

        return new GenericPO(TABLE_PLANT,getCtx(), recordId);
    }
}
