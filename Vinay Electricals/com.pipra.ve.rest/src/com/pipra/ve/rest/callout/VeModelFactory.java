package com.pipra.ve.rest.callout;

import java.sql.ResultSet;
import org.adempiere.base.IModelFactory;
import org.compiere.model.PO;
import org.compiere.util.Env;
import com.pipra.ve.rest.model.COrder_Custom;

public class VeModelFactory implements IModelFactory {

	@Override
	public Class<?> getClass(String tableName) {
		if (tableName.equalsIgnoreCase(COrder_Custom.Table_Name))
			return COrder_Custom.class;
		return null;
	}

	@Override
	public PO getPO(String tableName, int Record_ID, String trxName) {
		if (tableName.equalsIgnoreCase(COrder_Custom.Table_Name))
			return new COrder_Custom(Env.getCtx(), Record_ID, trxName);
		return null;
	}

	@Override
	public PO getPO(String tableName, ResultSet rs, String trxName) {
		return null;
	}
}
