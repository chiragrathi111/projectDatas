package org.realmeds.tissue.model;

import java.sql.ResultSet;
import java.util.Properties;

import org.realmeds.tissue.custom.TCUtills;
import org.realmeds.tissue.moduller.X_TC_in;

public class TCIn extends X_TC_in implements TC{

	private static final long serialVersionUID = 1L;

	public TCIn(Properties ctx, int TC_in_ID, String trxName) {
		super(ctx, TC_in_ID, trxName);
		// TODO Auto-generated constructor stub
	}
	
	public TCIn(Properties ctx, ResultSet rs, String trxName) {
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
		if (getc_uuid() == null) {
			setc_uuid(TCUtills.generateUUID());
		}
		// TODO Auto-generated method stub
		return super.beforeSave(newRecord);
	}
	@Override
	protected boolean afterDelete(boolean success) {
		// TODO Auto-generated method stub
		return super.afterDelete(success);
	}
	@Override
	protected boolean afterSave(boolean newRecord, boolean success) {
		// TODO Auto-generated method stub
		return super.afterSave(newRecord, success);
	}

	

}
