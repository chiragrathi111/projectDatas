package org.realmeds.tissue.model;

import java.sql.ResultSet;
import java.util.Properties;
import org.realmeds.tissue.custom.TCUtills;
import org.realmeds.tissue.moduller.X_TC_intermediatejoinplant;

public class TCIntermediateJoinPlantDetails extends X_TC_intermediatejoinplant{
	
	private static final long serialVersionUID = 1L;
	public TCIntermediateJoinPlantDetails(Properties ctx, int TC_IntermediateJoinPlantDetails_ID, String trxName) {
		super(ctx, TC_IntermediateJoinPlantDetails_ID, trxName);
		// TODO Auto-generated constructor stub
	}
	
	public TCIntermediateJoinPlantDetails(Properties ctx, ResultSet rs, String trxName) {
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
		if (getc_uuid() == null) {
			setc_uuid(TCUtills.generateUUID());
		}
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
