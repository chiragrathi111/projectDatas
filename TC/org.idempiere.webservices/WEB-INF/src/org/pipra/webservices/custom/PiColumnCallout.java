package org.pipra.webservices.custom;

import java.util.ArrayList;
import java.util.List;

import org.adempiere.base.IColumnCallout;
import org.adempiere.base.IColumnCalloutFactory;
import org.compiere.model.MInOut;
import org.compiere.model.MOrder;

public class PiColumnCallout implements IColumnCalloutFactory {

	@Override
	public IColumnCallout[] getColumnCallouts(String tableName, String columnName) {
		List<IColumnCallout> list = new ArrayList<>();

		if ((tableName.equalsIgnoreCase(MOrder.Table_Name)
				&& columnName.equalsIgnoreCase(COrder_Custom.COLUMNNAME_PUTSTATUS))
				|| (tableName.equalsIgnoreCase(MOrder.Table_Name)
						&& columnName.equalsIgnoreCase(MOrder.DOC_COLUMNNAME_DocStatus))
				|| (tableName.equalsIgnoreCase(MOrder.Table_Name)
						&& columnName.equalsIgnoreCase(MOrder.COLUMNNAME_DocAction))
				|| (tableName.equalsIgnoreCase(MInOut.Table_Name)
						&& columnName.equalsIgnoreCase(MInOut.COLUMNNAME_DocAction))) {
			list.add(new PiColumnCalloutFactory());
		}
		return list.toArray(new IColumnCallout[0]);
	}
}
