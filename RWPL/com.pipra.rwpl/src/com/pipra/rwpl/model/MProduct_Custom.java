package com.pipra.rwpl.model;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Properties;

import org.compiere.model.MProduct;

@org.adempiere.base.Model(table = MProduct.Table_Name)
public class MProduct_Custom extends MProduct {

	private static final long serialVersionUID = 1101071347949960064L;

	public MProduct_Custom(Properties ctx, int M_Product_ID, String trxName) {
		super(ctx, M_Product_ID, trxName);
	}

	public MProduct_Custom(Properties ctx, int M_Product_ID, String trxName, String... virtualColumns) {
		super(ctx, M_Product_ID, trxName, virtualColumns);
	}

	public MProduct_Custom(Properties ctx, ResultSet rs, String trxName) {
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

	public static final String COLUMNNAME_LABELQNTY = "labelQnty";

	public void setLabelQnty(int labelQnty) {
		set_Value(COLUMNNAME_LABELQNTY, labelQnty);
	}

	public BigDecimal getLabelQnty() {
		BigDecimal bd = (BigDecimal) get_Value(COLUMNNAME_LABELQNTY);
		return bd;
	}
}
