package org.realmeds.tissue.model;

import java.sql.ResultSet;
import java.util.Properties;
import org.realmeds.tissue.custom.TCUtills;
import org.realmeds.tissue.moduller.X_tc_temperatureStatus;

public class TCIOTRecord extends X_tc_temperatureStatus {

	private static final long serialVersionUID = 1L;

	public TCIOTRecord(Properties ctx, int TC_temperatureStatus_ID, String trxName) {
		super(ctx, TC_temperatureStatus_ID, trxName);
	}

	public TCIOTRecord(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}

	@Override
	protected boolean beforeDelete() {
		return super.beforeDelete();
	}

	@Override
	protected boolean beforeSave(boolean newRecord) {
		if (getc_uuid() == null) {
			setc_uuid(TCUtills.generateUUID());
		}		
		return super.beforeSave(newRecord);
	}

	@Override
	protected boolean afterDelete(boolean success) {
		return super.afterDelete(success);
	}

	@Override
	protected boolean afterSave(boolean newRecord, boolean success) {
		return super.afterSave(newRecord, success);
	}
}
