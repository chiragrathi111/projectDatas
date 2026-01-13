package org.realmeds.tissue.model;

import java.sql.ResultSet;
import java.util.Properties;

import org.realmeds.tissue.moduller.X_TC_CultureStage;

public class TCCultureStage extends X_TC_CultureStage{

private static final long serialVersionUID = 1L;
	
	public TCCultureStage(Properties ctx, int TC_cultureStgae_ID, String trxName) {
		super(ctx, TC_cultureStgae_ID, trxName);
	}
	
	public TCCultureStage(Properties ctx, ResultSet rs, String trxName) {
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
			return super.afterSave(newRecord, success);
		}
}
