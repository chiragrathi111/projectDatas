package com.pipra.rwpl.entity.model;

import java.util.List;
import java.util.Properties;

import org.compiere.model.MRole;
import org.compiere.model.Query;
import org.compiere.model.X_AD_Role;

@org.adempiere.base.Model(table = X_AD_Role.Table_Name)
public class AdRole_Custom extends X_AD_Role {

	private static final long serialVersionUID = 1101071347949960064L;

	public AdRole_Custom(Properties ctx, int AD_Role_ID, String trxName) {
		super(ctx, AD_Role_ID, trxName);
	}
	
	public AdRole_Custom(Properties ctx, int AD_Role_ID, String trxName, String[] virtualColumns) {
		super(ctx, AD_Role_ID, trxName, virtualColumns);
	}

	public static final String COLUMNNAME_purchaseOrder = "purchaseOrder";

	public void setPurchaseOrder(boolean purchaseOrder) {
		set_Value(COLUMNNAME_purchaseOrder, Boolean.valueOf(purchaseOrder));
	}

	public boolean isPurchaseOrder() {
		Object oo = get_Value(COLUMNNAME_purchaseOrder);
		if (oo != null) {
			if (oo instanceof Boolean)
				return ((Boolean) oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}

	public static final String COLUMNNAME_materialReceipt = "materialReceipt";

	public void setMaterialReceipt(boolean materialReceipt) {
		set_Value(COLUMNNAME_materialReceipt, Boolean.valueOf(materialReceipt));
	}

	public boolean isMaterialReceipt() {
		Object oo = get_Value(COLUMNNAME_materialReceipt);
		if (oo != null) {
			if (oo instanceof Boolean)
				return ((Boolean) oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}

	public static final String COLUMNNAME_physicalInventory = "physicalInventory";

	public void setPhysicalInventory(boolean physicalInventory) {
		set_Value(COLUMNNAME_physicalInventory, Boolean.valueOf(physicalInventory));
	}

	public boolean isPhysicalInventory() {
		Object oo = get_Value(COLUMNNAME_physicalInventory);
		if (oo != null) {
			if (oo instanceof Boolean)
				return ((Boolean) oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}

	public static final String COLUMNNAME_saleOrder = "saleOrder";

	public void setSaleOrder(boolean saleOrder) {
		set_Value(COLUMNNAME_saleOrder, Boolean.valueOf(saleOrder));
	}

	public boolean isSaleOrder() {
		Object oo = get_Value(COLUMNNAME_saleOrder);
		if (oo != null) {
			if (oo instanceof Boolean)
				return ((Boolean) oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}

	public static final String COLUMNNAME_shipmentCustomer = "shipmentCustomer";

	public void setShipmentCustomer(boolean shipmentCustomer) {
		set_Value(COLUMNNAME_shipmentCustomer, Boolean.valueOf(shipmentCustomer));
	}

	public boolean isShipmentCustomer() {
		Object oo = get_Value(COLUMNNAME_shipmentCustomer);
		if (oo != null) {
			if (oo instanceof Boolean)
				return ((Boolean) oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}

	public static final String COLUMNNAME_addInward = "addInward";

	public void setAddInward(boolean addInward) {
		set_Value(COLUMNNAME_addInward, Boolean.valueOf(addInward));
	}

	public boolean isAddInward() {
		Object oo = get_Value(COLUMNNAME_addInward);
		if (oo != null) {
			if (oo instanceof Boolean)
				return ((Boolean) oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}
	
	public static final String COLUMNNAME_ispickbyorder = "ispickbyorder";

	public void setIspickbyorder(boolean ispickbyorder) {
		set_Value(COLUMNNAME_ispickbyorder, Boolean.valueOf(ispickbyorder));
	}

	public boolean ispickbyorder() {
		Object oo = get_Value(COLUMNNAME_ispickbyorder);
		if (oo != null) {
			if (oo instanceof Boolean)
				return ((Boolean) oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}
	
	public static final String COLUMNNAME_mergeapp = "mergeapp";
	
	public void setmergeapp(boolean mergeapp) {
		set_Value(COLUMNNAME_mergeapp, Boolean.valueOf(mergeapp));
	}
	
	public boolean ismergeapp() {
		Object oo = get_Value(COLUMNNAME_mergeapp);
		if (oo != null) {
			if (oo instanceof Boolean)
				return ((Boolean) oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}
	
	public static final String COLUMNNAME_splitapp = "splitapp";
	
	public void setsplitapp(boolean splitapp) {
		set_Value(COLUMNNAME_splitapp, Boolean.valueOf(splitapp));
	}
	
	public boolean issplitapp() {
		Object oo = get_Value(COLUMNNAME_splitapp);
		if (oo != null) {
			if (oo instanceof Boolean)
				return ((Boolean) oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}
	
	
	public static final String COLUMNNAME_supervisor = "supervisor";

	public void setSupervisorr(boolean supervisor) {
		set_Value(COLUMNNAME_supervisor, Boolean.valueOf(supervisor));
	}

	public boolean isSupervisor() {
		Object oo = get_Value(COLUMNNAME_supervisor);
		if (oo != null) {
			if (oo instanceof Boolean)
				return ((Boolean) oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}
	
	public static final String COLUMNNAME_labour = "labour";

	public void setLabour(boolean labour) {
		set_Value(COLUMNNAME_labour, Boolean.valueOf(labour));
	}

	public boolean isLabour() {
		Object oo = get_Value(COLUMNNAME_labour);
		if (oo != null) {
			if (oo instanceof Boolean)
				return ((Boolean) oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}
	
	public static List<MRole> getRoleBySupervisor(Properties ctx, int clientId) {
		String sql = " AD_Client_ID = ? AND supervisor = 'Y'";
		return new Query(ctx, MRole.Table_Name, sql, null).setParameters(clientId).list();
	}
	
	public static List<MRole> getRoleBylabour(Properties ctx, int clientId) {
		String sql = " AD_Client_ID = ? AND labour = 'Y'";
		return new Query(ctx, MRole.Table_Name, sql, null).setParameters(clientId).list();
	}

	public static final String COLUMNNAME_labourPutaway = "labourPutaway";
	public static final String COLUMNNAME_labourPicklist = "labourPicklist";
	public static final String COLUMNNAME_labourInventorymove = "labourInventorymove";

	// --- labourPutaway Methods ---
	public void setLabourPutaway(boolean labourPutaway) {
	    set_Value(COLUMNNAME_labourPutaway, Boolean.valueOf(labourPutaway));
	}

	public boolean isLabourPutaway() {
	    Object oo = get_Value(COLUMNNAME_labourPutaway);
	    if (oo != null) {
	        if (oo instanceof Boolean)
	            return ((Boolean) oo).booleanValue();
	        return "Y".equals(oo);
	    }
	    return false;
	}

	// --- labourPicklist Methods ---
	public void setLabourPicklist(boolean labourPicklist) {
	    set_Value(COLUMNNAME_labourPicklist, Boolean.valueOf(labourPicklist));
	}

	public boolean isLabourPicklist() {
	    Object oo = get_Value(COLUMNNAME_labourPicklist);
	    if (oo != null) {
	        if (oo instanceof Boolean)
	            return ((Boolean) oo).booleanValue();
	        return "Y".equals(oo);
	    }
	    return false;
	}

	// --- labourInventorymove Methods ---
	public void setLabourInventorymove(boolean labourInventorymove) {
	    set_Value(COLUMNNAME_labourInventorymove, Boolean.valueOf(labourInventorymove));
	}

	public boolean isLabourInventorymove() {
	    Object oo = get_Value(COLUMNNAME_labourInventorymove);
	    if (oo != null) {
	        if (oo instanceof Boolean)
	            return ((Boolean) oo).booleanValue();
	        return "Y".equals(oo);
	    }
	    return false;
	}
	
	public static final String COLUMNNAME_qcCheckApp = "qcCheckApp";

	public void setIsQcCheckApp(boolean qcCheckApp) {
	    set_Value(COLUMNNAME_qcCheckApp, Boolean.valueOf(qcCheckApp));
	}

	public boolean isQcCheckApp() {
	    Object oo = get_Value(COLUMNNAME_qcCheckApp);
	    if (oo != null) {
	        if (oo instanceof Boolean) {
	            return ((Boolean) oo).booleanValue();
	        }
	        return "Y".equals(oo);
	    }
	    return false;
	}

}
