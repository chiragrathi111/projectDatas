package org.realmeds.tissue.model;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Properties;

import org.compiere.model.MUser;

public class ADUser_Custom extends MUser{
	
	private static final long serialVersionUID = 1101071347949960064L;

	public ADUser_Custom(Properties ctx, int C_Order_ID, String trxName) {
		super(ctx, C_Order_ID, trxName);
	}

//	public ADUser_Custom(Properties ctx, int C_Order_ID, String trxName, String... virtualColumns) {
//		super(ctx, C_Order_ID, trxName, virtualColumns);
//	}

	public ADUser_Custom(Properties ctx, ResultSet rs, String trxName) {
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

	public static final String COLUMNNAME_personalcode = "personalcode";
	
	public void setpersonalcode(String personalCode) {
		set_Value(COLUMNNAME_personalcode, personalCode);
	}
	
	public String getpersonalcode() {
		String personalCode = (String) get_Value(COLUMNNAME_personalcode);
		return personalCode;
	}
	
	 /** Column name token */
    public static final String COLUMNNAME_token = "token";
	/** Set token	  */
	public void settoken (String token) {
		set_Value(COLUMNNAME_token, token);
	}
	/** Get token	  */
	public String gettoken() {
		String token = (String) get_Value(COLUMNNAME_token);
		return token;
	}
	
    /** Column name tokentime */
    public static final String COLUMNNAME_tokentime = "tokentime";
	/** Set tokentime	  */
	public void settokentime (String tokentime) {
		set_Value(COLUMNNAME_tokentime, tokentime);
	}

	/** Get tokentime	  */
	public String gettokentime() {
		String tokenTime = (String) get_Value(COLUMNNAME_tokentime);
		return tokenTime;
	}
	
	public static final String COLUMNNAME_blockedupto = "blockedupto";

	public void setBlockedupto(Timestamp blockedupto) {
		set_Value(COLUMNNAME_blockedupto, blockedupto);
	}

	public Timestamp getBlockedupto() {
		return (Timestamp) get_Value(COLUMNNAME_blockedupto);
	}

}
