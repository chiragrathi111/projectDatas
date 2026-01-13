package org.realmeds.tissue.model;

import java.sql.ResultSet;
import java.util.Properties;

import org.realmeds.tissue.custom.TCUtills;
import org.realmeds.tissue.moduller.X_TC_IntermediateVisit;
import org.realmeds.tissue.moduller.X_TC_Visit;

public class TCIntermediateVisit extends X_TC_IntermediateVisit{
	
	private static final long serialVersionUID = 1L;
	public TCIntermediateVisit(Properties ctx, int TC_intermediateVisit_ID, String trxName) {
		super(ctx, TC_intermediateVisit_ID, trxName);
		// TODO Auto-generated constructor stub
	}
	
	public TCIntermediateVisit(Properties ctx, ResultSet rs, String trxName) {
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
