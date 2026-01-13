package org.realmeds.tissue.model;

import java.sql.ResultSet;
import java.util.Properties;
import org.realmeds.tissue.activemq.ActiveMQProducer;
import org.realmeds.tissue.custom.TCUtills;
import org.realmeds.tissue.moduller.X_TC_QualityCheck;
import org.realmeds.tissue.moduller.X_TC_Visit;

public class TCQualityCheck extends X_TC_QualityCheck{
	
	private static final long serialVersionUID = 1L;
	public TCQualityCheck(Properties ctx, int TC_qualityCheck_ID, String trxName) {
		super(ctx, TC_qualityCheck_ID, trxName);
		// TODO Auto-generated constructor stub
	}
	
	public TCQualityCheck(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
		// TODO Auto-generated constructor stub
	}
	
	@Override
		protected boolean beforeDelete() {
			// TODO Auto-generated method stub
			return super.beforeDelete();
		}
	@Override
		protected boolean beforeSave(boolean newRecord) {
			// TODO Auto-generated method stub
//		if (getc_uuid() == null) {
//			setc_uuid(TCUtills.generateUUID());
//		}
			return super.beforeSave(newRecord);
		}
	@Override
		protected boolean afterDelete(boolean success) {
			// TODO Auto-generated method stub
			return super.afterDelete(success);
		}
	@Override
		protected boolean afterSave(boolean newRecord, boolean success) {
		boolean result = super.afterSave(newRecord, success);

        if (newRecord && success) {
            int recordId = get_ID();
            String tableName = get_TableName();
            if (recordId != 0) {
                try {
                    ActiveMQProducer producer = new ActiveMQProducer();
                    producer.sendMessage(tableName, recordId);
                } catch (Exception e) {
                    log.severe("Failed to send message to ActiveMQ: " + e.getMessage());
                    return false;
                }
            } else {
                log.warning("Record ID is 0 after saving.");
            }
        }
        return result;	
		}

}
