package com.pipra.rwpl.factory;

import java.sql.ResultSet;

import org.adempiere.base.IModelFactory;
import org.compiere.model.PO;
import org.compiere.util.Env;

import com.pipra.rwpl.model.COrderLine_Custom;
import com.pipra.rwpl.model.COrder_Custom;
import com.pipra.rwpl.model.Custom_MR;
import com.pipra.rwpl.model.MInOutLine_Custom;
import com.pipra.rwpl.model.Packline;

public class RWPLModelFactory implements IModelFactory {

	@Override
	public Class<?> getClass(String tableName) {
		if (tableName.equalsIgnoreCase(COrder_Custom.Table_Name))
			return COrder_Custom.class;
		else if (tableName.equalsIgnoreCase(MInOutLine_Custom.Table_Name))
			return MInOutLine_Custom.class;
		else if (tableName.equalsIgnoreCase("m_packline"))
			return Packline.class;
		else if (tableName.equalsIgnoreCase(Custom_MR.Table_Name))
			return Custom_MR.class;
		else if (tableName.equalsIgnoreCase(COrderLine_Custom.Table_Name))
			return COrderLine_Custom.class;
		return null;
	}

	@Override
	public PO getPO(String tableName, int Record_ID, String trxName) {
		if (tableName.equalsIgnoreCase(COrder_Custom.Table_Name))
			return new COrder_Custom(Env.getCtx(), Record_ID, trxName);
		else if (tableName.equalsIgnoreCase(MInOutLine_Custom.Table_Name))
			return new MInOutLine_Custom(Env.getCtx(), Record_ID, trxName);
		else if (tableName.equalsIgnoreCase("m_packline"))
			return new Packline(Env.getCtx(), Record_ID, trxName);
		else if (tableName.equalsIgnoreCase(Custom_MR.Table_Name))
			return new Custom_MR(Env.getCtx(), Record_ID, trxName);
		else if (tableName.equalsIgnoreCase(COrderLine_Custom.Table_Name))
			return new COrderLine_Custom(Env.getCtx(), Record_ID, trxName);
		return null;
	}

	@Override
	public PO getPO(String tableName, ResultSet rs, String trxName) {
		if(tableName.equalsIgnoreCase("m_packline"))
			return new Packline(null,rs,trxName);
		else if(tableName.equalsIgnoreCase(Custom_MR.Table_Name))
			return new Custom_MR(null,rs,trxName);
		else if(tableName.equalsIgnoreCase(COrderLine_Custom.Table_Name))
			return new COrderLine_Custom(null,rs,trxName);
		return null;
	}
}
