package com.pipra.ve.model;

import java.sql.ResultSet;
import java.util.Properties;

import org.compiere.model.MLocatorType;
import org.compiere.model.MTable;
import org.compiere.model.PO;

@org.adempiere.base.Model(table = MLocatorType.Table_Name)
public class MLocatorType_Custom extends MLocatorType {

	private static final long serialVersionUID = 1101071347949960064L;

	public MLocatorType_Custom(Properties ctx, int MLocatorType_Custom_ID, String trxName) {
		super(ctx, MLocatorType_Custom_ID, trxName);
	}

	public MLocatorType_Custom(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}
	
	public static final String COLUMNNAME_PI_DEPARTMENT_ID = "pi_deptartment_ID";

	public void setPI_DEPARTMENT_ID (int pi_deptartment_ID)
	{
		if (pi_deptartment_ID < 1)
			set_ValueNoCheck (COLUMNNAME_PI_DEPARTMENT_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_PI_DEPARTMENT_ID, Integer.valueOf(pi_deptartment_ID));
	}

	public int getPI_DEPARTMENT_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_PI_DEPARTMENT_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public X_PI_Deptartment getPi_Department() throws RuntimeException
	{
		PO po = MTable.get(getCtx(), X_PI_Deptartment.Table_ID)
		.getPO(getPI_DEPARTMENT_ID(), get_TrxName());
		X_PI_Deptartment dept = new X_PI_Deptartment(getCtx(), po.get_ID(), get_TrxName());
		return dept;
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

}