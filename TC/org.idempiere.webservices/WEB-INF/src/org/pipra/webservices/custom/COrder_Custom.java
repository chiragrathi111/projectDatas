package org.pipra.webservices.custom;

import java.sql.ResultSet;
import java.util.Properties;

import org.compiere.model.MOrder;

public class COrder_Custom extends MOrder {

	private static final long serialVersionUID = 1101071347949960064L;

	public COrder_Custom(Properties ctx, int C_Order_ID, String trxName) {
		super(ctx, C_Order_ID, trxName);
	}

	public COrder_Custom(Properties ctx, int C_Order_ID, String trxName, String... virtualColumns) {
		super(ctx, C_Order_ID, trxName, virtualColumns);
	}

	public COrder_Custom(Properties ctx, ResultSet rs, String trxName) {
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

	public static final String COLUMNNAME_PUTSTATUS = "putStatus";

	public void setPutStatus(String putStatus) {
		set_Value(COLUMNNAME_PUTSTATUS, putStatus);
	}

	public String getPutStatus() {
		String bd = (String) get_Value(COLUMNNAME_PUTSTATUS);
		return bd;
	}
	
	/** Column name secondaryhardeninguuid */
    public static final String COLUMNNAME_secondaryhardeninguuid = "secondaryhardeninguuid";

	/** Set secondaryhardeninguuid	  */
	public void setsecondaryhardeninguuid (String secondaryhardeninguuid) {
		set_Value(COLUMNNAME_secondaryhardeninguuid, secondaryhardeninguuid);
	}

	/** Get secondaryhardeninguuid	  */
	public String getsecondaryhardeninguuid() {
		String bd = (String) get_Value(COLUMNNAME_secondaryhardeninguuid);
		return bd;
	}

}
