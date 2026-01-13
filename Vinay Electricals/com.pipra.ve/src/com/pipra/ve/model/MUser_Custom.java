package com.pipra.ve.model;

import java.sql.ResultSet;
import java.util.Properties;

import org.compiere.model.MUser;

@org.adempiere.base.Model(table = MUser.Table_Name)
public class MUser_Custom extends MUser {

	private static final long serialVersionUID = 1101071347949960064L;

	public MUser_Custom(Properties ctx, int M_User_ID, String trxName) {
		super(ctx, M_User_ID, trxName);
	}

	public MUser_Custom(Properties ctx, ResultSet rs, String trxName) {
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

	public static final String COLUMNNAME_DEPTACESS = "deptAcess";

	public void setDeptAcess(int deptAcess) {
		set_Value(COLUMNNAME_DEPTACESS, deptAcess);
	}

	public String getDeptAcess() {
		String bd = (String) get_Value(COLUMNNAME_DEPTACESS);
		return bd;
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
	
	public static final String COLUMNNAME_DEPTHEAD = "deptHead";

	
	public void setDeptHead(boolean deptHead) {
		set_Value (COLUMNNAME_DEPTHEAD, Boolean.valueOf(deptHead));
	}

	public boolean isDeptHead() {
		Object oo = get_Value(COLUMNNAME_DEPTHEAD);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}
	
	public static final String COLUMNNAME_DEPTAGENT = "deptAgent";

	
	public void setDeptAgent(boolean deptAgent) {
		set_Value (COLUMNNAME_DEPTAGENT, Boolean.valueOf(deptAgent));
	}

	public boolean isDeptAgent() {
		Object oo = get_Value(COLUMNNAME_DEPTAGENT);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}
}
