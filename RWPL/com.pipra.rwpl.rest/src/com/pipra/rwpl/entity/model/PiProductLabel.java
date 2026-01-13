package com.pipra.rwpl.entity.model;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import org.compiere.model.PO;
import org.compiere.model.Query;
import org.compiere.util.DB;
import org.compiere.util.Env;

import com.pipra.rwpl.model.response.ProductLabelLine;

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

	public PiProductLabel(Properties ctx, String trxName,  ProductLabelLine line,
			boolean qcPassed) {
		this(ctx, 0, trxName);
		setAD_Client_ID(Env.getAD_Client_ID(ctx));
		setAD_Org_ID(Env.getAD_Org_ID(ctx));
		setQcpassed(qcPassed);
		setquantity(BigDecimal.valueOf(line.getQuantity()));
		setM_Product_ID(line.getProductId());
		setM_Locator_ID(line.getLocatorId());
		setC_OrderLine_ID(line.getcOrderlineId());
		setM_InOutLine_ID(line.getmInoutlineId());
		setIsSOTrx(line.isSalesTransaction());

		String uuid = line.getProductLabelUUId();
		if (line.getProductLabelUUId() == null || line.getProductLabelUUId().isEmpty())
			uuid = UUID.randomUUID().toString();
		setlabeluuid(uuid);
		setIsActive(true);
	}

	public PiProductLabel(Properties ctx, String trxName, int clientId, int orgId, int productId, int locatorId,
			int cOrderLineid, int minoutLineid, boolean salesTrx, BigDecimal quantity, String labelUUID) {
		this(ctx, 0, trxName);
		setAD_Client_ID(clientId);
		setAD_Org_ID(orgId);
		setQcpassed(true);
		setquantity(quantity);
		setM_Product_ID(productId);
		setM_Locator_ID(locatorId);
		setC_OrderLine_ID(cOrderLineid);
		setM_InOutLine_ID(minoutLineid);
		setIsSOTrx(salesTrx);

		if (labelUUID == null || labelUUID.isEmpty())
			labelUUID = UUID.randomUUID().toString();
		setlabeluuid(labelUUID);
		setIsActive(true);
	}

	public static PO getPiProductLabelByUUID(String columnName, Object labelUUID, Properties ctx, String trxName,
			boolean issotrx) {
		PO po = new Query(ctx, Table_Name, "" + columnName + "=? AND issotrx = ?", trxName)
				.setParameters(labelUUID, issotrx).first();
		return po;
	}

	public static PO getPiProductLabelById(String columnName, Object labelUUID, Properties ctx, String trxName,
			boolean issotrx) {
		PO po = new Query(ctx, Table_Name, "" + columnName + "=? AND issotrx = ?", trxName)
				.setParameters(labelUUID, issotrx).firstOnly();
		return po;
	}

	public static List<PO> getPiProductLabel(String columnName, Object columnValue, Properties ctx, String trxName,
			Object issotrx) {
		List<PO> list = null;
		if (issotrx == null)
			list = new Query(ctx, Table_Name, "" + columnName + "=?", trxName).setParameters(columnValue)
					.setOrderBy(PiProductLabel.COLUMNNAME_pi_productLabel_ID).list();
		else
			list = new Query(ctx, Table_Name, "" + columnName + "=? AND issotrx = ?", trxName)
					.setParameters(columnValue, issotrx)
					.setOrderBy(PiProductLabel.COLUMNNAME_pi_productLabel_ID + " desc").list();
		return list;
	}

	public static List<PO> getProductLabelForInoutLineAndLocator(int mLocatorId, int mInoutLineId, Properties ctx,
			String trxName) {
		List<PO> list = new Query(ctx, Table_Name, "m_inoutline_ID =? AND m_locator_ID = ? AND issotrx = 'N'", trxName)
				.setParameters(mInoutLineId, mLocatorId)
				.setOrderBy(PiProductLabel.COLUMNNAME_pi_productLabel_ID + " desc").list();
		return list;
	}

	public static List<PO> getProductLabelForInoutLine(int mInoutLineId, Properties ctx, String trxName) {
		List<PO> list = new Query(ctx, Table_Name, "m_inoutline_ID =? ", trxName).setParameters(mInoutLineId)
				.setOrderBy(PiProductLabel.COLUMNNAME_pi_productLabel_ID + " desc").list();
		return list;
	}

	public static LinkedHashMap<Integer, Integer> getAvailableLabelsByLocator(int clientId, int locatorId) {
		LinkedHashMap<Integer, Integer> productCount = new LinkedHashMap<Integer, Integer>();
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {

			String sql = "SELECT \n" + "    pp.m_product_id,\n" + "    SUM(CASE \n"
					+ "            WHEN pp.issotrx = 'N' THEN pp.quantity \n" + "            ELSE 0 \n"
					+ "        END) AS remaining_count\n" + "FROM \n" + "    pi_productlabel pp\n" + "WHERE \n"
					+ "    pp.ad_client_id = " + clientId + " \n" + "    AND NOT EXISTS (\n" + "        SELECT 1 \n"
					+ "        FROM pi_productlabel pp_sales\n" + "        WHERE pp_sales.labeluuid = pp.labeluuid\n"
					+ "        AND pp_sales.issotrx = 'Y'\n" + "    )\n" + "    AND pp.m_locator_id = " + locatorId
					+ "\n" + "GROUP BY \n" + "    pp.m_product_id;";
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

	public static int getSumOfQntyByLocator(int clientId, int locatorId) {
		PreparedStatement pstm = null;
		ResultSet rs = null;
		int qnty = 0;
		try {

			String sql = "SELECT \n" + "    SUM(CASE \n" + "            WHEN pp.issotrx = 'N' THEN pp.quantity \n"
					+ "            ELSE 0 \n" + "        END) AS remaining_count\n" + "FROM \n"
					+ "    adempiere.pi_productlabel pp\n" + "WHERE \n" + "    pp.ad_client_id = " + clientId + "\n"
					+ "    AND NOT EXISTS (\n" + "        SELECT 1 \n"
					+ "        FROM adempiere.pi_productlabel pp_sales\n"
					+ "        WHERE pp_sales.labeluuid = pp.labeluuid\n" + "        AND pp_sales.issotrx = 'Y'\n"
					+ "    )\n" + "    AND pp.m_locator_id = " + locatorId + ";";
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

	public static List<Integer> getPiProductLabelsByLocator(Properties ctx, String trxName, int clientId,
			int locatorId) {

		List<Integer> productLabelIds = new ArrayList<Integer>();

		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {

			String sql = "SELECT\n" + "    pp.pi_productlabel_ID\n" + "FROM\n" + "    pi_productlabel pp\n" + "WHERE\n"
					+ "    pp.ad_client_id = " + clientId + " \n" + "    AND NOT EXISTS (\n" + "        SELECT 1\n"
					+ "        FROM adempiere.pi_productlabel pp_sales\n"
					+ "        WHERE pp_sales.labeluuid = pp.labeluuid\n" + "        AND pp_sales.issotrx = 'Y'\n"
					+ "    )\n" + "    AND pp.m_locator_id = " + locatorId + " ;";

			pstm = DB.prepareStatement(sql.toString(), null);
			rs = pstm.executeQuery();
			while (rs.next()) {

				int pi_productlabel_ID = rs.getInt("pi_productlabel_ID");

				productLabelIds.add(pi_productlabel_ID);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DB.close(rs, pstm);
		}
		return productLabelIds;
	}
	
	@Override
	protected boolean beforeDelete() {
		List<PO> poList = getProductLabelForImportline(get_ID(), p_ctx, null);
		if (poList != null && !poList.isEmpty()) {
			for (PO po : poList)
				new X_pi_importline(p_ctx, po.get_ID(), null).delete(true);
		}
		return super.beforeDelete();
	}

	public static List<PO> getProductLabelForImportline(int pi_productLabel_ID, Properties ctx, String trxName) {
		List<PO> list = new Query(ctx, X_pi_importline.Table_Name, "pi_productLabel_ID =? ", trxName)
				.setParameters(pi_productLabel_ID).list();
		return list;
	}
}
