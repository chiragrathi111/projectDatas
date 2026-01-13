package org.realmeds.tissue.model;

import java.sql.ResultSet;
import java.util.Properties;

import org.realmeds.tissue.custom.TCUtills;
import org.realmeds.tissue.moduller.X_TC_ExplantDetails;

public class TCExplantDetail extends X_TC_ExplantDetails {

	private static final long serialVersionUID = 1L;

	public TCExplantDetail(Properties ctx, int TCExplantDetail_ID, String trxName) {
		super(ctx, TCExplantDetail_ID, trxName);
	}

	public TCExplantDetail(Properties ctx, ResultSet rs, String trxName) {
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
