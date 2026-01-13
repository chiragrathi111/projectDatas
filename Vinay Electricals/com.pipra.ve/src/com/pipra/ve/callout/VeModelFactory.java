package com.pipra.ve.callout;

import java.sql.ResultSet;

import org.adempiere.base.IModelFactory;
import org.compiere.model.PO;
import org.compiere.util.Env;

import com.pipra.ve.model.COrder_Custom;
import com.pipra.ve.model.MClient_Custom;
import com.pipra.ve.model.MProduct_Custom;
import com.pipra.ve.model.MUser_Custom;
import com.pipra.ve.model.PiProductLabel;

public class VeModelFactory implements IModelFactory {

	@Override
	public Class<?> getClass(String tableName) {
		if (tableName.equalsIgnoreCase(PiProductLabel.Table_Name))
			return PiProductLabel.class;
		else if (tableName.equalsIgnoreCase(MProduct_Custom.Table_Name))
			return MProduct_Custom.class;
		else if (tableName.equalsIgnoreCase(MClient_Custom.Table_Name))
			return MClient_Custom.class;
		else if (tableName.equalsIgnoreCase(MUser_Custom.Table_Name))
			return MUser_Custom.class;
		else if (tableName.equalsIgnoreCase(COrder_Custom.Table_Name))
			return COrder_Custom.class;
		return null;
	}

	@Override
	public PO getPO(String tableName, int Record_ID, String trxName) {
		if (tableName.equalsIgnoreCase(PiProductLabel.Table_Name))
			return new PiProductLabel(Env.getCtx(), Record_ID, trxName);
		else if (tableName.equalsIgnoreCase(MProduct_Custom.Table_Name))
			return new MProduct_Custom(Env.getCtx(), Record_ID, trxName);
		else if (tableName.equalsIgnoreCase(MClient_Custom.Table_Name))
			return new MClient_Custom(Env.getCtx(), Record_ID, trxName);
		else if (tableName.equalsIgnoreCase(MUser_Custom.Table_Name))
			return new MUser_Custom(Env.getCtx(), Record_ID, trxName);
		else if (tableName.equalsIgnoreCase(COrder_Custom.Table_Name))
			return new COrder_Custom(Env.getCtx(), Record_ID, trxName);
		return null;
	}

	@Override
	public PO getPO(String tableName, ResultSet rs, String trxName) {
		return null;
	}
}
