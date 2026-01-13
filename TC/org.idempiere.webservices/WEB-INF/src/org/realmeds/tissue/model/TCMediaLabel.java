package org.realmeds.tissue.model;

import java.sql.ResultSet;
import java.util.Properties;

import org.realmeds.tissue.custom.TCUtills;
import org.realmeds.tissue.moduller.X_TC_MediaLabel;

public class TCMediaLabel extends X_TC_MediaLabel{

	private static final long serialVersionUID = 1L;

	public TCMediaLabel(Properties ctx, int TCMediaLabel_ID, String trxName) {
		super(ctx, TCMediaLabel_ID, trxName);
		// TODO Auto-generated constructor stub
	}
	
	public TCMediaLabel(Properties ctx, ResultSet rs, String trxName) {
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
