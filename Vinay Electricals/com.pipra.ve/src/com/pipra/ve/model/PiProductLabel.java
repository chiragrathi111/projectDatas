package com.pipra.ve.model;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import org.compiere.model.PO;
import org.compiere.model.Query;
import org.compiere.util.DB;

import com.pipra.ve.utils.VeUtils;
import com.pipra.ve.x10.ADLoginRequest;
import com.pipra.ve.x10.GenerateLabelRequest;
import com.pipra.ve.x10.ProductLabelLine;

public class PiProductLabel extends X_pi_productLabel {

	private static final long serialVersionUID = 1L;

	public PiProductLabel(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}

	public PiProductLabel(Properties ctx, int productLabelId, String trxName) {
		this(ctx, productLabelId, trxName, (String[]) null);
	}

	public PiProductLabel(Properties ctx, int piQrId, String trxName, String... virtualColumns) {
		super(ctx, piQrId, trxName, virtualColumns);
	}

	public static final String COLUMNNAME_isLabelDiscarded = "isLabelDiscarded";
	public void setIsLabelDiscarded(boolean isLabelDiscarded) {
		set_Value(COLUMNNAME_isLabelDiscarded, Boolean.valueOf(isLabelDiscarded));
		}
	
	public boolean isLabelDiscarded() {
		Object oo = get_Value(COLUMNNAME_isLabelDiscarded);
		if (oo != null) {
			if (oo instanceof Boolean)
				return ((Boolean) oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}
	
	public PiProductLabel(Properties ctx, String trxName, ADLoginRequest loginReq, GenerateLabelRequest generateLabelRequest,
			boolean qcPassed) {
		this(ctx, 0, trxName);
		setAD_Client_ID(loginReq.getClientID());
		setAD_Org_ID(loginReq.getOrgID());
		setQcpassed(qcPassed);
		setM_Product_ID(generateLabelRequest.getProductId());
		
//		setM_Locator_ID(generateLabelRequest.getLocatorId());
//		setC_OrderLine_ID(generateLabelRequest.getCOrderlineId());
//		setM_InOutLine_ID(generateLabelRequest.getMInoutlineId());
//		setIsSOTrx(generateLabelRequest.getIsSalesTransaction());
//		setLabelUUID(generateLabelRequest.getProductLabelUUId());
		
		setIsActive(true);
		
		int parentLabelId = 0;
		if (generateLabelRequest.getParentLabel() != 0) {
			parentLabelId = generateLabelRequest.getParentLabel();
			setparentlabel(parentLabelId);
		}
		
		if (generateLabelRequest.getProductId() != 0) {
			MProduct_Custom product = new MProduct_Custom(ctx, generateLabelRequest.getProductId(), trxName);
			setM_Product_ID(product.getM_Product_ID());
			int productCount = product.getProductCount();
			setQuantity(BigDecimal.valueOf(productCount));
			if(parentLabelId != 0) {
				PiProductLabel parentLabel = new PiProductLabel(ctx, parentLabelId, trxName);
				List<PO> poList = getChildsForParentLabelById(parentLabelId, ctx, trxName, loginReq.getClientID(), loginReq.getOrgID());
				if(poList != null && poList.size() !=0) {
					for(PO po : poList) {
						PiProductLabel label = new PiProductLabel(ctx, po.get_ID(), trxName);
						MProduct_Custom childProduct = new MProduct_Custom(ctx, label.getM_Product().getM_Product_ID(), trxName);
						productCount += childProduct.getProductCount();
					}
				}
				parentLabel.setQuantity(BigDecimal.valueOf(productCount));
				parentLabel.saveEx();
			}
			setQuantity(BigDecimal.valueOf(productCount));
		}
		setpi_labeltype_ID(generateLabelRequest.getLabelTypeId());
	}

	
	public static List<PO> getChildsForParentLabelById(int id, Properties ctx, String trxName, int adClientId, int orgId) {
		List<PO> labelList = new Query(ctx, PiProductLabel.Table_Name, "parentlabel = ? and ad_client_id = ? and ad_org_id = ?", trxName)
				.setParameters(id, adClientId, orgId).list();
		return labelList;
	}
	
	public static PO getVELabelByUUID(String uuId, Properties ctx, String trxName, int adClientId, int orgId) {
		PO po =  new Query(ctx, PiProductLabel.Table_Name, "LabelUUID = ? and ad_client_id = ? and ad_org_id = ?", trxName)
				.setParameters(uuId, adClientId).firstOnly();
		return po;
	}
	
	public static PO getPiProductLabelByUUID(String columnName, Object labelUUID, Properties ctx, String trxName, boolean issotrx, int orgId) {
		PO po = new Query(ctx, Table_Name, "" + columnName + "=? AND issotrx = ? and ad_org_id = ?", trxName).setParameters(labelUUID, issotrx, orgId).first();
		return po;
	}

	public static List<PO> getPiProductLabel(String columnName, Object columnValue, Properties ctx, String trxName,
			Object issotrx, int clientId, int orgId) {
		List<PO> list = null;
		if (issotrx == null)
			list = new Query(ctx, Table_Name, "" + columnName + "=? and ad_client_id = ? and ad_org_id = ?", trxName).setParameters(columnValue, clientId, orgId)
					.setOrderBy(PiProductLabel.COLUMNNAME_pi_productLabel_ID).list();
		else
			list = new Query(ctx, Table_Name, "" + columnName + "=? AND issotrx = ? and ad_client_id = ? and ad_org_id = ?", trxName)
					.setParameters(columnValue, issotrx, clientId, orgId).setOrderBy(PiProductLabel.COLUMNNAME_pi_productLabel_ID + " desc").list();
		return list;
	}
	
	public static List<PO> getProductLabelForInoutLineAndLocator(int mLocatorId, int mInoutLineId, Properties ctx, String trxName, int clientId, int orgId) {
		List<PO> list  = new Query(ctx, Table_Name, "m_inoutline_ID =? AND m_locator_ID = ? AND issotrx = 'N' and ad_client_id = ? and ad_org_id = ?", trxName)
					.setParameters(mInoutLineId, mLocatorId, clientId, orgId).setOrderBy(PiProductLabel.COLUMNNAME_pi_productLabel_ID + " desc").list();
		return list;
	}
	
	public static LinkedHashMap<Integer, Integer> getAvailableLabelsByLocator(int clientId, int locatorId) {
		LinkedHashMap<Integer, Integer> productCount = new LinkedHashMap<Integer, Integer>();
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {

			String sql = "SELECT \n"
					+ "    pp.m_product_id,\n"
					+ "    SUM(CASE \n"
					+ "            WHEN pp.issotrx = 'N' THEN pp.quantity \n"
					+ "            ELSE 0 \n"
					+ "        END) AS remaining_count\n"
					+ "FROM \n"
					+ "    pi_productlabel pp\n"
					+ "WHERE \n"
					+ "    pp.ad_client_id = "+clientId+" \n"
					+ "    AND NOT EXISTS (\n"
					+ "        SELECT 1 \n"
					+ "        FROM pi_productlabel pp_sales\n"
					+ "        WHERE pp_sales.labeluuid = pp.labeluuid\n"
					+ "        AND pp_sales.issotrx = 'Y'\n"
					+ "    )\n"
					+ "    AND pp.m_locator_id = "+locatorId+"\n"
					+ "GROUP BY \n"
					+ "    pp.m_product_id;";
			pstm = DB.prepareStatement(sql.toString(), null);
			rs = pstm.executeQuery();
			while (rs.next()) {
				int remainingCount = rs.getInt("remaining_count");
				int ProductId = rs.getInt("m_product_id");
				productCount.put(ProductId, remainingCount);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DB.close(rs, pstm);
		}
		return productCount;
	}
	
	public PiProductLabel(Properties ctx, String trxName, ADLoginRequest loginReq, ProductLabelLine line,
			boolean qcPassed) {
		this(ctx, 0, trxName);
		setAD_Client_ID(loginReq.getClientID());
		setAD_Org_ID(loginReq.getOrgID());
		setQcpassed(qcPassed);
		setQuantity(BigDecimal.valueOf(line.getQuantity()));
		setM_Product_ID(line.getProductId());
		setM_Locator_ID(line.getLocatorId());
		setC_OrderLine_ID(line.getCOrderlineId());
		setM_InOutLine_ID(line.getMInoutlineId());
		setIsSOTrx(line.getIsSalesTransaction());
		setStatus(line.getStatus());
		setreason(line.getReason());
		String uuid = line.getProductLabelUUId();
		if(line.getProductLabelUUId() == null || line.getProductLabelUUId().isEmpty())
			uuid = UUID.randomUUID().toString();
		setLabelUUID(uuid);
		setIsActive(true);
	}
	
	public PiProductLabel(Properties ctx, String trxName, ADLoginRequest loginReq, ProductLabelLine line,
			boolean qcPassed, boolean isManufacturing, boolean isAssembly, boolean isLightProduction) {
		this(ctx, 0, trxName);
		setAD_Client_ID(loginReq.getClientID());
		setAD_Org_ID(loginReq.getOrgID());
		setQcpassed(qcPassed);
		setQuantity(BigDecimal.valueOf(line.getQuantity()));
		setM_Product_ID(line.getProductId());
		setM_Locator_ID(line.getLocatorId());
		setreason(line.getReason());
		setStatus(line.getStatus());
		if (isManufacturing) {
			setPP_Order_ID(line.getPpOrderId());
			setismanufacturing(isManufacturing);
			setpi_orderreceipt_ID(line.getPpOrderReceiptId());
		}
		if (isAssembly) {
			setPi_paorder_ID(line.getPAOrderId());
			if(line.getIsSalesTransaction())
				VeUtils.addOrReduceInventory(new MProduct_Custom(ctx, line.getProductId(), trxName), line.getQuantity(), line.getLocatorId(), loginReq.getOrgID(), ctx, trxName, true);
		}
		if(isLightProduction || isAssembly) {
			setPi_paorder_ID(line.getPAOrderId());
			if(!line.getIsSalesTransaction())
				VeUtils.addOrReduceInventory(new MProduct_Custom(ctx, line.getProductId(), trxName), line.getQuantity(), line.getLocatorId(), loginReq.getOrgID(), ctx, trxName, false);
		
		}
		setIsSOTrx(line.getIsSalesTransaction());
		String uuid = line.getProductLabelUUId();
		if (line.getProductLabelUUId() == null || line.getProductLabelUUId().isEmpty())
			uuid = UUID.randomUUID().toString();
		setLabelUUID(uuid);
		setIsActive(true);
	}
	
	public static List<PO> getProductLabelForOrderReceipt(int orderReceiptId, Properties ctx, String trxName) {
		List<PO> list  = new Query(ctx, Table_Name, "pi_orderreceipt_ID =?", trxName)
					.setParameters(orderReceiptId).setOrderBy(PiProductLabel.COLUMNNAME_pi_productLabel_ID + " desc").list();
		return list;
	}
	
	public static List<PO> getPiProductLabelById(String labelUUID, Properties ctx, String trxName, int adClientId, int orgId) {
		List<PO> poList = new Query(ctx, Table_Name, "labeluuid =? and ad_client_id = ? and ad_org_id = ?", trxName).setParameters(labelUUID, adClientId, orgId).list();
		return poList;
	}
	
	public static int getSumOfQntyByLocator(int clientId, int locatorId) {
		PreparedStatement pstm = null;
		ResultSet rs = null;
		int qnty = 0;
		try {
		
			String sql = "SELECT \n"
					+ "    SUM(CASE \n"
					+ "            WHEN pp.issotrx = 'N' THEN pp.quantity \n"
					+ "            ELSE 0 \n"
					+ "        END) AS remaining_count\n"
					+ "FROM \n"
					+ "    adempiere.pi_productlabel pp\n"
					+ "WHERE \n"
					+ "    pp.ad_client_id = "+clientId+"\n"
					+ "    AND NOT EXISTS (\n"
					+ "        SELECT 1 \n"
					+ "        FROM adempiere.pi_productlabel pp_sales\n"
					+ "        WHERE pp_sales.labeluuid = pp.labeluuid\n"
					+ "        AND pp_sales.issotrx = 'Y'\n"
					+ "    )\n"
					+ "    AND pp.m_locator_id = "+locatorId+";";
			pstm = DB.prepareStatement(sql.toString(), null);
			rs = pstm.executeQuery();
			while (rs.next()) {
				qnty += rs.getInt("remaining_count");
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DB.close(rs, pstm);
		}
		return qnty;
	}
}
