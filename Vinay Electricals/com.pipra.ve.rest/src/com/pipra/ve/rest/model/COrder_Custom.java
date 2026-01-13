package com.pipra.ve.rest.model;

import java.sql.ResultSet;
import java.util.Properties;

import org.compiere.model.MOrder;

@org.adempiere.base.Model(table = MOrder.Table_Name)
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

	public static final String COLUMNNAME_PI_DEPARTMENT_ID = "pi_deptartment_ID";

	
	public void setpidepartmentID(int pi_deptartment_ID) {
		set_Value(COLUMNNAME_PI_DEPARTMENT_ID, pi_deptartment_ID);
		
		if (pi_deptartment_ID < 1)
			set_ValueNoCheck (COLUMNNAME_PI_DEPARTMENT_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_PI_DEPARTMENT_ID, Integer.valueOf(pi_deptartment_ID));
		
	}

	public int getpidepartmentID() {
		Integer ii = (Integer)get_Value(COLUMNNAME_PI_DEPARTMENT_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}
	
	public static final String COLUMNNAME_PUTSTATUS = "putStatus";

	public void setPutStatus(String putStatus) {
		set_Value(COLUMNNAME_PUTSTATUS, putStatus);
	}

	public String getPutStatus() {
		String bd = (String) get_Value(COLUMNNAME_PUTSTATUS);
		return bd;
	}
	
	public static final String COLUMNNAME_iscourier = "iscourier";
	public void setIsCourier(boolean iscourier) {
		set_Value(COLUMNNAME_iscourier, Boolean.valueOf(iscourier));
		}
	
	public boolean iscourier() {
		Object oo = get_Value(COLUMNNAME_iscourier);
		if (oo != null) {
			if (oo instanceof Boolean)
				return ((Boolean) oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}
	
	public static final String COLUMNNAME_isMobileOrder = "isMobileOrder";
	public void setIsMobileOrder(boolean isMobileOrder) {
		set_Value(COLUMNNAME_isMobileOrder, Boolean.valueOf(isMobileOrder));
		}
	
	public boolean isMobileOrder() {
		Object oo = get_Value(COLUMNNAME_isMobileOrder);
		if (oo != null) {
			if (oo instanceof Boolean)
				return ((Boolean) oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}

//	@Override
//	public String completeIt() {
//		String result = super.completeIt();
//
//		String title = null;
//		String body = null;
//		String userType = null;
//		String messageTitle = null;
//		Map<String, String> data = new HashMap<>();
//
//		data.put("recordId", String.valueOf(getC_Order_ID()));
//		data.put("documentNo", getDocumentNo());
//
//		if (isSOTrx()) {
//			title = "New Sales Order: " + getDocumentNo() + "";
//			body = "Sales Order - " + getDocumentNo() + " added to process";
//			userType = "ispickbyorder";
//			messageTitle = "salesOrderCreated";
//
//			data.put("path1", "/pick_screen");
//			data.put("path2", "/pick_detail_screen");
//			
//		} else if (!isSOTrx()) {
//			title = "New Purchase Order: " + getDocumentNo() + "";
//			body = "Purchase order - " + getDocumentNo() + " added to process";
//			userType = "purchaseOrder";
//			messageTitle = "purchaseOrderCreated";
//
//			data.put("path1", "/po_screen");
//			data.put("path2", "/po_detail_screen");
//		}
//
//		VeUtils.sendNotificationAsync(userType, get_Table_ID(), getC_Order_ID(), getCtx(), get_TrxName(), title,
//				body, get_TableName(), data, getAD_Client_ID(), messageTitle, getpidepartmentID());
//
//		return result;
//	}

}
