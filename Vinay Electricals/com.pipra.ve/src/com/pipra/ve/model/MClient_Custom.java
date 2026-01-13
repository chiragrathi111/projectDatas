package com.pipra.ve.model;

import java.sql.ResultSet;
import java.util.Properties;

import org.compiere.model.MClient;

@org.adempiere.base.Model(table = MClient.Table_Name)
public class MClient_Custom extends MClient {

	private static final long serialVersionUID = 1101071347949960064L;

	public MClient_Custom(Properties ctx, int M_CLient_ID, String trxName) {
		super(ctx, M_CLient_ID, trxName);
	}

	public MClient_Custom(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}

	@Override
	protected boolean beforeSave(boolean newRecord) {
		return super.beforeSave(newRecord);
	}

	@Override
	protected boolean beforeDelete() {
		return super.beforeDelete();
	}

	@Override
	protected boolean afterSave(boolean newRecord, boolean success) {
		return super.afterSave(newRecord, success);
	}

	@Override
	protected boolean afterDelete(boolean success) {
		return super.afterDelete(success);
	}

	public static final String COLUMNNAME_PACKINGMODULE = "packingModule";

	public void setPackingModule(boolean qcpassed) {
		set_ValueNoCheck(COLUMNNAME_PACKINGMODULE, Boolean.valueOf(qcpassed));
	}

	public boolean getPackingModule() {
		Object oo = get_Value(COLUMNNAME_PACKINGMODULE);
		if (oo != null) {
			if (oo instanceof Boolean)
				return ((Boolean) oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}

}
