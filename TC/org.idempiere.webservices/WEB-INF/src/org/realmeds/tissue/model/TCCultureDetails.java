package org.realmeds.tissue.model;

import java.sql.ResultSet;
import java.util.Properties;
import javax.jms.JMSException;
import org.realmeds.tissue.activemq.ActiveMQProducer;
import org.realmeds.tissue.moduller.X_TC_CultureDetails;

public class TCCultureDetails extends X_TC_CultureDetails {

	private static final long serialVersionUID = 1L;

	public TCCultureDetails(Properties ctx, int TC_cultureDetails_ID, String trxName) {
		super(ctx, TC_cultureDetails_ID, trxName);
	}

	public TCCultureDetails(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}

	@Override
	protected boolean beforeDelete() {
		return super.beforeDelete();
	}

	@Override
	protected boolean beforeSave(boolean newRecord) {
//		if (getc_uuid() == null) {
//			setc_uuid(TCUtills.generateUUID());
//		}
		return super.beforeSave(newRecord);
	}

	@Override
	protected boolean afterDelete(boolean success) {
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
