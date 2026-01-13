package org.realmeds.tissue.model;

import java.sql.ResultSet;
import java.util.Properties;
import org.realmeds.tissue.activemq.ActiveMQProducer;
import org.realmeds.tissue.custom.TCUtills;
import org.realmeds.tissue.moduller.X_TC_devicedata;

public class TCIOTdeviceData extends X_TC_devicedata {

	private static final long serialVersionUID = 1L;

	public TCIOTdeviceData(Properties ctx, int TC_iotDevice_ID, String trxName) {
		super(ctx, TC_iotDevice_ID, trxName);
	}

	public TCIOTdeviceData(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}

	@Override
	protected boolean beforeDelete() {
		return super.beforeDelete();
	}

	@Override
	protected boolean beforeSave(boolean newRecord) {
//		if (getdeviceid() == null) {
//			setdeviceid(TCUtills.generateUUID());
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
            	String deviceId = (String) get_Value("deviceid");

                if (deviceId == null || deviceId.isEmpty()) {
                    set_Value("deviceid", String.valueOf(recordId));
                    set_Value("name", String.valueOf(recordId));
                    saveEx();
                }
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
