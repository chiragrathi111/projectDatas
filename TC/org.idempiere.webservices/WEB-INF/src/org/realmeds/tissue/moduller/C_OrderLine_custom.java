package org.realmeds.tissue.moduller;

import java.sql.ResultSet;
import java.util.Properties;

import org.compiere.model.MOrderLine;

public class C_OrderLine_custom extends MOrderLine{
	
	private static final long serialVersionUID = 1101071347949960064L;

	public C_OrderLine_custom(Properties ctx, int C_Orderline_ID, String trxName) {
		super(ctx, C_Orderline_ID, trxName);
	}

	public C_OrderLine_custom(Properties ctx, int C_Order_ID, String trxName, String... virtualColumns) {
		super(ctx, C_Order_ID, trxName, virtualColumns);
	}

	public C_OrderLine_custom(Properties ctx, ResultSet rs, String trxName) {
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
	
	/** Column name secondaryhardeninguuid */
    public static final String COLUMNNAME_RootingUUId = "cultureuuid";

	/** Set secondaryhardeninguuid	  */
	public void setCultureUUId (String cultureuuid) {
		set_Value(COLUMNNAME_RootingUUId, cultureuuid);
	}

	/** Get secondaryhardeninguuid	  */
	public String getCultureUUId() {
		String bd = (String) get_Value(COLUMNNAME_RootingUUId);
		return bd;
	}

}
