package org.realmeds.tissue.model;

import java.sql.ResultSet;
import java.util.Properties;
import org.realmeds.tissue.moduller.X_TC_Farmer;

public class TCFarmer extends X_TC_Farmer{

private static final long serialVersionUID = 1L;
	
	public TCFarmer(Properties ctx, int TC_farmer_ID, String trxName) {
		super(ctx, TC_farmer_ID, trxName);
		// TODO Auto-generated constructor stub
	}
	
	public TCFarmer(Properties ctx, ResultSet rs, String trxName) {
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
