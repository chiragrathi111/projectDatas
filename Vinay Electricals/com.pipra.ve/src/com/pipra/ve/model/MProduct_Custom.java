package com.pipra.ve.model;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.compiere.model.MProduct;
import org.compiere.model.PO;
import org.compiere.model.Query;

@org.adempiere.base.Model(table = MProduct.Table_Name)
public class MProduct_Custom extends MProduct {

	private static final long serialVersionUID = 1101071347949960064L;

	public MProduct_Custom(Properties ctx, int M_Product_ID, String trxName) {
		super(ctx, M_Product_ID, trxName);
	}

	public MProduct_Custom(Properties ctx, int M_Product_ID, String trxName, String... virtualColumns) {
		super(ctx, M_Product_ID, trxName, virtualColumns);
	}

	public MProduct_Custom(Properties ctx, ResultSet rs, String trxName) {
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
		int recordId = get_ID();
		MProduct_Custom pro = new MProduct_Custom(p_ctx, recordId, get_TrxName());
		
	    Object boxQtyObj = pro.get_Value("BoxQnty");
	    int boxQtys = 0;
	    if (boxQtyObj instanceof BigDecimal) {
	        boxQtys = ((BigDecimal) boxQtyObj).intValue();
	    } else if (boxQtyObj instanceof Integer) {
	        boxQtys = (Integer) boxQtyObj;
	    }
		if (boxQtys < 0) {
			boxQtys = 0;
		}
	    pro.set_ValueOfColumn("BoxQnty", boxQtys);
	    
	    Object prodQtyObj = pro.get_Value("ProductCount");
	    int prodQtys = 0;
	    if (boxQtyObj instanceof BigDecimal) {
	    	prodQtys = ((BigDecimal) prodQtyObj).intValue();
	    } else if (boxQtyObj instanceof Integer) {
	    	prodQtys = (Integer) prodQtyObj;
	    }
		if (prodQtys < 0) {
			prodQtys = 0;
		}
	    pro.set_ValueOfColumn("ProductCount", prodQtys);
	    
		pro.saveEx();
		return super.afterSave(newRecord, success);
	}

	@Override
	protected boolean afterDelete(boolean success) {
		return super.afterDelete(success);
	}

	public static final String COLUMNNAME_PRODUCTCOUNT = "productCount";

	public void setProductCount(int productCount) {
		set_Value(COLUMNNAME_PRODUCTCOUNT, productCount);
	}

	public int getProductCount() {
		int bd = (int) get_Value(COLUMNNAME_PRODUCTCOUNT);
		return bd;
	}
	
	public static final String COLUMNNAME_BOXQNTY = "boxQnty";

	public void setBoxQnty(int boxQnty) {
		set_Value(COLUMNNAME_BOXQNTY, boxQnty);
	}

	public Object getBoxQnty() {
		Object bd =  get_Value(COLUMNNAME_BOXQNTY);
		return bd;
	}

	public static final String COLUMNNAME_SCAN_LABEL = "scanLabel";

	public void setScanLabel(boolean scanLabel) {
		set_Value(COLUMNNAME_SCAN_LABEL, Boolean.valueOf(scanLabel));
	}

	public boolean isScanLabel() {
		Object oo = get_Value(COLUMNNAME_SCAN_LABEL);
		if (oo != null) {
			if (oo instanceof Boolean)
				return ((Boolean) oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}

	public static List<PO> getProductsWithBom(int clientId, int orgId, Properties ctx, String trxName, int deptId) {
		List<PO> list = new Query(ctx, Table_Name,
				"ad_client_ID =? AND ad_org_ID = ? AND isBom = 'Y' AND pi_deptartment_ID = ?", trxName)
				.setParameters(clientId, orgId, deptId).setOrderBy(COLUMNNAME_M_Product_ID + " desc").list();
		return list;
	}
	
	public static List<PO> getProductsWithBomNew(
	        int clientId,
	        Properties ctx,
	        String trxName,
	        int deptId) {

	    String whereClause =
	            "AD_Client_ID = ? " +
	            "AND IsBOM = 'Y' " +
	            "AND PI_Deptartment_ID = ? " +
	            "AND EXISTS ( " +
	            "   SELECT 1 FROM PP_Product_BOM pb " +
	            "   JOIN PP_Product_BOMLine pbl " +
	            "     ON pbl.PP_Product_BOM_ID = pb.PP_Product_BOM_ID " +
	            "   WHERE pb.M_Product_ID = M_Product.M_Product_ID " +
	            "   AND pb.IsActive='Y' " +
	            "   AND pbl.IsActive='Y' " +
	            ")";

	    return new Query(ctx, Table_Name, whereClause, trxName)
	            .setParameters(clientId, deptId)
	            .setOrderBy(COLUMNNAME_M_Product_ID + " DESC")
	            .list();
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
	
	public static final String COLUMNNAME_SECOND_DEPARTMENT = "second_department";

	public void setSecondDepartment(boolean second_department) {
		set_Value(COLUMNNAME_SECOND_DEPARTMENT, Boolean.valueOf(second_department));
	}

	public boolean isSecondDepartment() {
		Object oo = get_Value(COLUMNNAME_SECOND_DEPARTMENT);
		if (oo != null) {
			if (oo instanceof Boolean)
				return ((Boolean) oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}
	
	public static List<PO> getProducts(int clientId, int orgId, Properties ctx, String trxName, int deptId) {
		List<PO> list = new Query(ctx, Table_Name,
				"ad_client_ID =? AND ad_org_ID = ? AND pi_deptartment_ID = ? AND second_department = 'N'", trxName)
				.setParameters(clientId, orgId, deptId).setOrderBy(COLUMNNAME_M_Product_ID + " desc").list();
		return list;
	}
	
	public static List<MProduct_Custom> getProductsForFGUser(int clientId, int orgId, Properties ctx, String trxName, int fgLightDeptId) {

	    List<MProduct_Custom> finalList = new ArrayList<>();

	    List<PO> fgProducts = new Query(ctx, Table_Name,
	            "ad_client_ID = ? AND ad_org_ID = ? AND pi_deptartment_ID = ?", trxName)
	            .setParameters(clientId, orgId, fgLightDeptId)
	            .setOrderBy(COLUMNNAME_M_Product_ID + " desc")
	            .list();

	    for (PO po : fgProducts) {
	        finalList.add(new MProduct_Custom(ctx, po.get_ID(), trxName));
	    }

	    List<PO> secondDeptProducts = new Query(ctx, Table_Name,
	            "ad_client_ID = ? AND ad_org_ID = ? AND pi_deptartment_ID != ? AND second_department = 'Y'", trxName)
	            .setParameters(clientId, orgId, fgLightDeptId)
	            .list();

	    for (PO po : secondDeptProducts) {
	        MProduct_Custom product = new MProduct_Custom(ctx, po.get_ID(), trxName);

	        product.set_ValueOfColumn("pi_deptartment_ID", fgLightDeptId);

	        finalList.add(product);
	    }

	    return finalList;
	}

}
