package com.pipra.ve.model;

import java.math.BigDecimal;
import java.util.Properties;

import org.eevolution.model.X_PP_Order;

@org.adempiere.base.Model(table = X_PP_Order.Table_Name)
public class MPPOrderCustom extends X_PP_Order {

	public MPPOrderCustom(Properties ctx, int PP_Order_ID, String trxName) {
		super(ctx, PP_Order_ID, trxName);
	}

	private static final long serialVersionUID = 1101071347949960064L;

	public static final String COLUMNNAME_MACHINENO = "machineNo";

	public void setMachineNo(int machineNo) {
		set_Value(COLUMNNAME_MACHINENO, machineNo);
	}

	public int getMachineNo() {
		Integer ii = (Integer)get_Value(COLUMNNAME_MACHINENO);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}
	
	public static final String COLUMNNAME_WIRELENGTH = "wireLength";

	public void setWireLength(int wireLength) {
		set_Value(COLUMNNAME_WIRELENGTH, wireLength);
	}

	public int getWireLength() {
		Integer ii = (Integer)get_Value(COLUMNNAME_WIRELENGTH);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

//	public static final String COLUMNNAME_ORDERGROUP = "orderGroup";
//
//	public void setOrderGroup(String orderGroup) {
//		set_Value(COLUMNNAME_ORDERGROUP, orderGroup);
//	}
//
//	public String getOrderGroup() {
//		String bd = (String) get_Value(COLUMNNAME_ORDERGROUP);
//		return bd;
//	}
	
	public BigDecimal getQtyToDeliver()
	{
		return getQtyOrdered().subtract(getQtyDelivered());
	}
	
	
//	public static final String Assembly = "Assembly";
//	/** High = 3 */
//	public static final String Manufacturing = "Manufacturing";
//	
//	public static String getOrderGroup(String rule) {
//		if (rule.equalsIgnoreCase(Assembly))
//			return Assembly;
//		else if (rule.equalsIgnoreCase(Manufacturing))
//			return Manufacturing;
//		return null;
//	}
	
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
	
	public static final String COLUMNNAME_status = "status";

	public void setStatus(String status) {
		set_Value(COLUMNNAME_status, status);
	}

	public String getStatus() {
		String bd = (String) get_Value(COLUMNNAME_status);
		return bd;
	}
	
    public static final String COLUMNNAME_ismanufacturing = "ismanufacturing";
    
	public void setIsmanufacturing(boolean ismanufacturing) {
		set_Value (COLUMNNAME_ismanufacturing, Boolean.valueOf(ismanufacturing));
	}

	public boolean isManufacturing() {
		Object oo = get_Value(COLUMNNAME_ismanufacturing);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}
	
}
