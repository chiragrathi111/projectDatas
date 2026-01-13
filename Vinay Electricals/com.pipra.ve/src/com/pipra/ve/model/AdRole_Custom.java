package com.pipra.ve.model;

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
	
	public static final String COLUMNNAME_productionsupervisor = "productionsupervisor";
	public void setproductionsupervisor(boolean productionsupervisor) {
		set_Value(COLUMNNAME_productionsupervisor, Boolean.valueOf(productionsupervisor));
	}
	
	public boolean isProductionsupervisor() {
		Object oo = get_Value(COLUMNNAME_productionsupervisor);
		if (oo != null) {
			if (oo instanceof Boolean)
				return ((Boolean) oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}
	
	public static final String COLUMNNAME_productionSwitchSocketAgent = "productionswitchagent";
	public void setProductionSwitchSocketAgent(boolean productionSwitchSocketAgent) {
		set_Value(COLUMNNAME_productionSwitchSocketAgent, Boolean.valueOf(productionSwitchSocketAgent));
	}
	
	public boolean isProductionSwitchSocketAgent() {
		Object oo = get_Value(COLUMNNAME_productionSwitchSocketAgent);
		if (oo != null) {
			if (oo instanceof Boolean)
				return ((Boolean) oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}
	
	public static final String COLUMNNAME_mouldingSwitchSocketReceiptReceiving = "mouldingswitchreceiptreceiving";
	public void setMouldingSwitchSocketReceiptReceiving(boolean mouldingSwitchSocketReceiptReceiving) {
		set_Value(COLUMNNAME_mouldingSwitchSocketReceiptReceiving, Boolean.valueOf(mouldingSwitchSocketReceiptReceiving));
	}
	
	public boolean isMouldingSwitchSocketReceiptReceiving() {
		Object oo = get_Value(COLUMNNAME_mouldingSwitchSocketReceiptReceiving);
		if (oo != null) {
			if (oo instanceof Boolean)
				return ((Boolean) oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}
	
	public static final String COLUMNNAME_assemblysupervisor = "assemblysupervisor";
	public void setAssemblysupervisor(boolean assemblysupervisor) {
		set_Value(COLUMNNAME_assemblysupervisor, Boolean.valueOf(assemblysupervisor));
	}
	
	public boolean isAssemblysupervisor() {
		Object oo = get_Value(COLUMNNAME_assemblysupervisor);
		if (oo != null) {
			if (oo instanceof Boolean)
				return ((Boolean) oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}
	
	public static final String COLUMNNAME_assemblySwitchandsocketAgent = "assemblyswitchagent";
	public void setAssemblySwitchandsocketAgent(boolean assemblySwitchandsocketAgent) {
		set_Value(COLUMNNAME_assemblySwitchandsocketAgent, Boolean.valueOf(assemblySwitchandsocketAgent));
	}
	
	public boolean isAssemblySwitchandsocketAgent() {
		Object oo = get_Value(COLUMNNAME_assemblySwitchandsocketAgent);
		if (oo != null) {
			if (oo instanceof Boolean)
				return ((Boolean) oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}
	
	public static final String COLUMNNAME_fgSwitchSocketAssemblyReceiving = "fgswitchassemblyreceiving";
	public void setFgSwitchSocketAssemblyReceiving(boolean fgSwitchSocketAssemblyReceiving) {
		set_Value(COLUMNNAME_fgSwitchSocketAssemblyReceiving, Boolean.valueOf(fgSwitchSocketAssemblyReceiving));
	}
	
	public boolean isFgSwitchSocketAssemblyReceiving() {
		Object oo = get_Value(COLUMNNAME_fgSwitchSocketAssemblyReceiving);
		if (oo != null) {
			if (oo instanceof Boolean)
				return ((Boolean) oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}
	
	public static final String COLUMNNAME_lightSupervisor = "lightSupervisor";
	public void setLightSupervisor(boolean lightSupervisor) {
		set_Value(COLUMNNAME_lightSupervisor, Boolean.valueOf(lightSupervisor));
	}
	
	public boolean isLightSupervisor() {
		Object oo = get_Value(COLUMNNAME_lightSupervisor);
		if (oo != null) {
			if (oo instanceof Boolean)
				return ((Boolean) oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}
	
	public static final String COLUMNNAME_lightAgent = "lightAgent";
	public void setLightAgent(boolean lightAgent) {
		set_Value(COLUMNNAME_lightAgent, Boolean.valueOf(lightAgent));
	}
	
	public boolean isLightAgent() {
		Object oo = get_Value(COLUMNNAME_lightAgent);
		if (oo != null) {
			if (oo instanceof Boolean)
				return ((Boolean) oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}
	
	public static final String COLUMNNAME_automationSupervisor = "automationSupervisor";
	public void setAutomationSupervisor(boolean automationSupervisor) {
		set_Value(COLUMNNAME_automationSupervisor, Boolean.valueOf(automationSupervisor));
	}
	
	public boolean isAutomationSupervisor() {
		Object oo = get_Value(COLUMNNAME_automationSupervisor);
		if (oo != null) {
			if (oo instanceof Boolean)
				return ((Boolean) oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}
	
	public static final String COLUMNNAME_automationAgent = "automationAgent";
	public void setAutomationAgent(boolean automationAgent) {
		set_Value(COLUMNNAME_automationAgent, Boolean.valueOf(automationAgent));
	}
	
	public boolean isAutomationAgent() {
		Object oo = get_Value(COLUMNNAME_automationAgent);
		if (oo != null) {
			if (oo instanceof Boolean)
				return ((Boolean) oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}
	
	public static final String COLUMNNAME_rmWireSupervisor = "rmWireSupervisor";
	public void setArmWireSupervisor(boolean rmWireSupervisor) {
		set_Value(COLUMNNAME_rmWireSupervisor, Boolean.valueOf(rmWireSupervisor));
	}
	
	public boolean isrmWireSupervisor() {
		Object oo = get_Value(COLUMNNAME_rmWireSupervisor);
		if (oo != null) {
			if (oo instanceof Boolean)
				return ((Boolean) oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}
	
	public static final String COLUMNNAME_wireproduction = "wireproduction";
	
	public void setRmWireDrawing(boolean wireproduction) {
		set_Value(COLUMNNAME_wireproduction, Boolean.valueOf(wireproduction));
	}
	
	public boolean isWireProduction() {
		Object oo = get_Value(COLUMNNAME_wireproduction);
		if (oo != null) {
			if (oo instanceof Boolean)
				return ((Boolean) oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
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
	
//	public static final String COLUMNNAME_rmWireTwisting = "rmWireTwisting";
//	public void setRmWireTwisting(boolean rmWireTwisting) {
//		set_Value(COLUMNNAME_rmWireTwisting, Boolean.valueOf(rmWireTwisting));
//	}
//	
//	public boolean isrmWireTwisting() {
//		Object oo = get_Value(COLUMNNAME_rmWireTwisting);
//		if (oo != null) {
//			if (oo instanceof Boolean)
//				return ((Boolean) oo).booleanValue();
//			return "Y".equals(oo);
//		}
//		return false;
//	}
//	
//	public static final String COLUMNNAME_rmWireInsulation = "rmWireInsulation";
//	public void setrmWireInsulation(boolean rmWireInsulation) {
//		set_Value(COLUMNNAME_rmWireInsulation, Boolean.valueOf(rmWireInsulation));
//	}
//	
//	public boolean isRmWireInsulation() {
//		Object oo = get_Value(COLUMNNAME_rmWireInsulation);
//		if (oo != null) {
//			if (oo instanceof Boolean)
//				return ((Boolean) oo).booleanValue();
//			return "Y".equals(oo);
//		}
//		return false;
//	}
//	
//	
//	public static final String COLUMNNAME_rmWireCoiling = "rmWireCoiling";
//	public void setRmWireCoiling(boolean rmWireCoiling) {
//		set_Value(COLUMNNAME_rmWireCoiling, Boolean.valueOf(rmWireCoiling));
//	}
//	
//	public boolean isRmWireCoiling() {
//		Object oo = get_Value(COLUMNNAME_rmWireCoiling);
//		if (oo != null) {
//			if (oo instanceof Boolean)
//				return ((Boolean) oo).booleanValue();
//			return "Y".equals(oo);
//		}
//		return false;
//	}
	
	public static final String COLUMNNAME_fgWireSupervisor = "fgWireSupervisor";
	public void setFgWireSupervisor(boolean fgWireSupervisor) {
		set_Value(COLUMNNAME_fgWireSupervisor, Boolean.valueOf(fgWireSupervisor));
	}
	
	public boolean isFgWireSupervisor() {
		Object oo = get_Value(COLUMNNAME_fgWireSupervisor);
		if (oo != null) {
			if (oo instanceof Boolean)
				return ((Boolean) oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}
	
	public static final String COLUMNNAME_brassAssembly = "brassAssembly";
	public void setBrassAssembly(boolean brassAssembly) {
		set_Value(COLUMNNAME_brassAssembly, Boolean.valueOf(brassAssembly));
	}
	
	public boolean isBrassAssembly() {
		Object oo = get_Value(COLUMNNAME_brassAssembly);
		if (oo != null) {
			if (oo instanceof Boolean)
				return ((Boolean) oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}
	
	public static final String COLUMNNAME_packagingAssembly = "packagingAssembly";
	public void setPackagingAssembly(boolean packagingAssembly) {
		set_Value(COLUMNNAME_packagingAssembly, Boolean.valueOf(packagingAssembly));
	}
	
	public boolean isPackagingAssembly() {
		Object oo = get_Value(COLUMNNAME_packagingAssembly);
		if (oo != null) {
			if (oo instanceof Boolean)
				return ((Boolean) oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}
	
	public static final String COLUMNNAME_mouldingAssembly = "mouldingAssembly";
	public void setmMuldingAssembly(boolean mouldingAssembly) {
		set_Value(COLUMNNAME_mouldingAssembly, Boolean.valueOf(mouldingAssembly));
		}
	
	public boolean isMouldingAssembly() {
		Object oo = get_Value(COLUMNNAME_mouldingAssembly);
		if (oo != null) {
			if (oo instanceof Boolean)
				return ((Boolean) oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}
	
	public static final String COLUMNNAME_packagingAgentAssembly = "packagingAgentAssembly";
	public void setPackagingAgentAssembly(boolean packagingAgentAssembly) {
		set_Value(COLUMNNAME_packagingAgentAssembly, Boolean.valueOf(packagingAgentAssembly));
		}
	
	public boolean isPackagingAgentAssembly() {
		Object oo = get_Value(COLUMNNAME_packagingAgentAssembly);
		if (oo != null) {
			if (oo instanceof Boolean)
				return ((Boolean) oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}
	
	public static final String COLUMNNAME_rmswitch = "rmswitch";
	public void setrmswitch(boolean rmswitch) {
		set_Value(COLUMNNAME_rmswitch, Boolean.valueOf(rmswitch));
		}
	
	public boolean isrmswitch() {
		Object oo = get_Value(COLUMNNAME_rmswitch);
		if (oo != null) {
			if (oo instanceof Boolean)
				return ((Boolean) oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}
	
	public static final String COLUMNNAME_IsPickByOrder = "ispickbyorder";
	public void setIspickbyorder(boolean ispickbyorder) {
		set_Value(COLUMNNAME_IsPickByOrder, Boolean.valueOf(ispickbyorder));
		}
	
	public boolean ispickbyorder() {
		Object oo = get_Value(COLUMNNAME_IsPickByOrder);
		if (oo != null) {
			if (oo instanceof Boolean)
				return ((Boolean) oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}
	
	public static final String COLUMNNAME_ReturnApp = "returnsapp";
	public void setIsReturn(boolean returnsapp) {
		set_Value(COLUMNNAME_ReturnApp, Boolean.valueOf(returnsapp));
		}
	
	public boolean isreturn() {
		Object oo = get_Value(COLUMNNAME_ReturnApp);
		if (oo != null) {
			if (oo instanceof Boolean)
				return ((Boolean) oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}
	
	public static final String COLUMNNAME_courierapp = "courierapp";
	public void setIsCourierApp(boolean courierapp) {
		set_Value(COLUMNNAME_courierapp, Boolean.valueOf(courierapp));
		}
	
	public boolean iscourierapp() {
		Object oo = get_Value(COLUMNNAME_courierapp);
		if (oo != null) {
			if (oo instanceof Boolean)
				return ((Boolean) oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}
	
	public static final String COLUMNNAME_finaldispatchapp = "finaldispatchapp";
	public void setIsFinalDispatchApp(boolean finaldispatchapp) {
		set_Value(COLUMNNAME_finaldispatchapp, Boolean.valueOf(finaldispatchapp));
		}
	
	public boolean isfinaldispatchapp() {
		Object oo = get_Value(COLUMNNAME_finaldispatchapp);
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
	
	public static final String COLUMNNAME_rePackingApp = "rePacking";

	public void setRePackingApp(boolean rePacking) {
	    set_Value(COLUMNNAME_rePackingApp, Boolean.valueOf(rePacking));
	}

	public boolean rePackingApp() {
	    Object oo = get_Value(COLUMNNAME_rePackingApp);
	    if (oo != null) {
	        if (oo instanceof Boolean) {
	            return ((Boolean) oo).booleanValue();
	        }
	        return "Y".equals(oo);
	    }
	    return false;
	}
	
	public static final String COLUMNNAME_poReceivingApp = "poReceivingApp";

	public void setPoReceivingApp(boolean poReceivingApp) {
	    set_Value(COLUMNNAME_poReceivingApp, Boolean.valueOf(poReceivingApp));
	}

	public boolean isPoReceivingApp() {
	    Object oo = get_Value(COLUMNNAME_poReceivingApp);
	    if (oo != null) {
	        if (oo instanceof Boolean) {
	            return ((Boolean) oo).booleanValue();
	        }
	        return "Y".equals(oo);
	    }
	    return false;
	}


}
