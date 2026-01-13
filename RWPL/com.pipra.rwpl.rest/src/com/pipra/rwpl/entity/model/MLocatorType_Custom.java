package com.pipra.rwpl.entity.model;

import java.sql.ResultSet;
import java.util.List;
import java.util.Properties;

import org.compiere.model.I_M_Locator;
import org.compiere.model.MLocator;
import org.compiere.model.MLocatorType;
import org.compiere.model.Query;

@org.adempiere.base.Model(table = MLocatorType.Table_Name)
public class MLocatorType_Custom extends MLocatorType {

	private static final long serialVersionUID = 1101071347949960064L;

	public MLocatorType_Custom(Properties ctx, int MLocatorType_Custom_ID, String trxName) {
		super(ctx, MLocatorType_Custom_ID, trxName);
	}

	public MLocatorType_Custom(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}

	public static final String COLUMNNAME_DISPATCH = "dispatch";

	public void setdispatch(boolean dispatch) {
		set_Value(COLUMNNAME_DISPATCH, Boolean.valueOf(dispatch));
	}

	public boolean isdispatch() {
		Object oo = get_Value(COLUMNNAME_DISPATCH);
		if (oo != null) {
			if (oo instanceof Boolean)
				return ((Boolean) oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}

	public static final String COLUMNNAME_RECEIVING = "receiving";

	public void setReceiving(boolean receiving) {
		set_Value(COLUMNNAME_RECEIVING, Boolean.valueOf(receiving));
	}

	public boolean isReceiving() {
		Object oo = get_Value(COLUMNNAME_RECEIVING);
		if (oo != null) {
			if (oo instanceof Boolean)
				return ((Boolean) oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}

	public static final String COLUMNNAME_RETURNS = "returns";

	public void setReturns(boolean returns) {
		set_Value(COLUMNNAME_RETURNS, Boolean.valueOf(returns));
	}

	public boolean isReturns() {
		Object oo = get_Value(COLUMNNAME_RETURNS);
		if (oo != null) {
			if (oo instanceof Boolean)
				return ((Boolean) oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}

	public static final String COLUMNNAME_PACKING = "packing";

	public void setPacking(boolean packing) {
		set_Value(COLUMNNAME_PACKING, Boolean.valueOf(packing));
	}

	public boolean isPacking() {
		Object oo = get_Value(COLUMNNAME_PACKING);
		if (oo != null) {
			if (oo instanceof Boolean)
				return ((Boolean) oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}

	public static final String COLUMNNAME_STORAGE = "storage";

	public void setStorage(boolean storage) {
		set_Value(COLUMNNAME_STORAGE, Boolean.valueOf(storage));
	}

	public boolean isStorage() {
		Object oo = get_Value(COLUMNNAME_STORAGE);
		if (oo != null) {
			if (oo instanceof Boolean)
				return ((Boolean) oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}

	public static List<MLocator> getLocatorsByType(Properties ctx, String trxName, int warehouseId, Object columnName, Object columnValue) {

		String tblName = I_M_Locator.Table_Name;
		String whereClause = ""+tblName+".M_Warehouse_ID=?";
		if(columnName != null && columnValue != null)
			whereClause += " AND ml."+columnName+" = '"+columnValue+"'";
		List<MLocator> list = new Query(ctx, tblName, whereClause, trxName).setParameters(warehouseId)
				.addJoinClause("JOIN M_locatorType ml on  ml.m_locatortype_ID = "+tblName+".m_locatortype_ID")
				.setOnlyActiveRecords(true).setOrderBy("X,Y,Z").list();
		if (list.size() > 0)
			list.stream().forEach(e -> e.markImmutable());
		return list;
	}
}