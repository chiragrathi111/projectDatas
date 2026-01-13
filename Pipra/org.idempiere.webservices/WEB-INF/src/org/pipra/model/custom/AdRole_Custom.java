package org.pipra.model.custom;

import java.util.Properties;

import org.compiere.model.X_AD_Role;

@org.adempiere.base.Model(table = X_AD_Role.Table_Name)
public class AdRole_Custom extends X_AD_Role {

	private static final long serialVersionUID = 1101071347949960064L;

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
	
	public static final String COLUMNNAME_ReceivingProcessedApp = "receiving_processed_app";

	  public void setReceivingProcessedApp(boolean receiving_processed_app) {
	    set_Value(COLUMNNAME_ReceivingProcessedApp, Boolean.valueOf(receiving_processed_app));
	  }

	  public boolean isReceivingProcessedApp() {
	    Object oo = get_Value(COLUMNNAME_ReceivingProcessedApp);
	    if (oo != null) {
	      if (oo instanceof Boolean)
	        return ((Boolean) oo).booleanValue();
	      return "Y".equals(oo);
	    }
	    return false;
	  }
	  
	  public static final String COLUMNNAME_UnauthorisedEvents = "unauthorised_events";

	  public void setIsUnauthorisedEvents(boolean unauthorised_events) {
	    set_Value(COLUMNNAME_UnauthorisedEvents, Boolean.valueOf(unauthorised_events));
	  }

	  public boolean isUnauthorisedEvents() {
	    Object oo = get_Value(COLUMNNAME_UnauthorisedEvents);
	    if (oo != null) {
	      if (oo instanceof Boolean)
	        return ((Boolean) oo).booleanValue();
	      return "Y".equals(oo);
	    }
	    return false;
	  }
	  
	  public static final String COLUMNNAME_UsersApp = "users_app";

	  public void setIsUsersApp(boolean users_app) {
	    set_Value(COLUMNNAME_UsersApp, Boolean.valueOf(users_app));
	  }

	  public boolean isUsersApp() {
	    Object oo = get_Value(COLUMNNAME_UsersApp);
	    if (oo != null) {
	      if (oo instanceof Boolean)
	        return ((Boolean) oo).booleanValue();
	      return "Y".equals(oo);
	    }
	    return false;
	  }
	  
	  public static final String COLUMNNAME_ItemInout = "item_inout";

	  public void setIsItemInout(boolean item_inout) {
	    set_Value(COLUMNNAME_ItemInout, Boolean.valueOf(item_inout));
	  }

	  public boolean isItemInout() {
	    Object oo = get_Value(COLUMNNAME_ItemInout);
	    if (oo != null) {
	      if (oo instanceof Boolean)
	        return ((Boolean) oo).booleanValue();
	      return "Y".equals(oo);
	    }
	    return false;
	  }
	  
	  public static final String COLUMNNAME_supervisorPutaway = "supervisorPutaway";
		public void setIsSupervisorPutawayApp(boolean supervisorPutaway) {
			set_Value(COLUMNNAME_supervisorPutaway, Boolean.valueOf(supervisorPutaway));
			}
		
		public boolean issupervisorputawayapp() {
			Object oo = get_Value(COLUMNNAME_supervisorPutaway);
			if (oo != null) {
				if (oo instanceof Boolean)
					return ((Boolean) oo).booleanValue();
				return "Y".equals(oo);
			}
			return false;
		}
		
		public static final String COLUMNNAME_labourPutaway = "labourPutaway";
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
