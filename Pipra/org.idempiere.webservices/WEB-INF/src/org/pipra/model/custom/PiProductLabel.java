package org.pipra.model.custom;

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
import org.idempiere.adInterface.x10.ADLoginRequest;
import org.idempiere.adInterface.x10.ProductLabelLine;

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

	public PiProductLabel(Properties ctx, String trxName, ADLoginRequest loginReq, ProductLabelLine line,
			boolean qcPassed) {
		this(ctx, 0, trxName);
		setAD_Client_ID(loginReq.getClientID());
		setAD_Org_ID(loginReq.getOrgID());
		setQcpassed(qcPassed);
		setquantity(BigDecimal.valueOf(line.getQuantity()));
		setM_Product_ID(line.getProductId());
		setM_Locator_ID(line.getLocatorId());
		setC_OrderLine_ID(line.getCOrderlineId());
		setM_InOutLine_ID(line.getMInoutlineId());
		setIsSOTrx(line.getIsSalesTransaction());
//		setlabeluuid(line.getProductLabelUUId());
		
		String uuid = line.getProductLabelUUId();
		if (line.getProductLabelUUId() == null || line.getProductLabelUUId().isEmpty())
			uuid = UUID.randomUUID().toString();
		setlabeluuid(uuid);
		
		setIsActive(true);
	}

	public static PO getPiProductLabelById(String columnName, Object labelUUID, Properties ctx, String trxName, boolean issotrx) {
		PO po = new Query(ctx, Table_Name, "" + columnName + "=? AND issotrx = ?", trxName).setParameters(labelUUID, issotrx).firstOnly();
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
					.setParameters(columnValue, issotrx).setOrderBy(PiProductLabel.COLUMNNAME_pi_productLabel_ID + " desc").list();
		return list;
	}
	
	public static List<PO> getProductLabelForInoutLineAndLocator(int mLocatorId, int mInoutLineId, Properties ctx, String trxName) {
		List<PO> list  = new Query(ctx, Table_Name, "m_inoutline_ID =? AND m_locator_ID = ? AND issotrx = 'N'", trxName)
					.setParameters(mInoutLineId, mLocatorId).setOrderBy(PiProductLabel.COLUMNNAME_pi_productLabel_ID + " desc").list();
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
	
//	public static List<PO> getAvailableProductLabels(int M_Warehouse_ID, Properties ctx, String trxName) {
//	    
//	    String whereClause = 
//	          "M_Warehouse_ID = ? "
//	        + "AND IsActive = 'Y' AND qcPassed = 'Y' AND quantity > 0 "
//	        + "AND NOT EXISTS ("
//	        + "    SELECT 1 "
//	        + "    FROM pi_productlabel pp_sales"
//	        + "    WHERE pp_sales.labeluuid = " + Table_Name + ".labeluuid"
//	        + "    AND pp_sales.issotrx = 'Y'"
//	        + ")";
//	    
//	    List<PO> list = new Query(ctx, Table_Name, whereClause, trxName)
//	                .setParameters(M_Warehouse_ID)
//	                .setOrderBy(PiProductLabel.COLUMNNAME_pi_productLabel_ID + " desc")
//	                .list();
//	    
//	    return list;
//	}
	
	public static List<PiProductLabel> getAvailableProductLabels(int M_Warehouse_ID, Properties ctx, String trxName) {
        List<PiProductLabel> list = new ArrayList<>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        // 1. SELECT the PI_ProductLabel_ID and Locator/Product IDs.
        // 2. JOIN M_Locator to filter by M_Warehouse_ID.
        // 3. Use the NOT EXISTS check for consumed labels.
        String sql = 
            "SELECT pp.pi_productLabel_ID " + // We only need the ID to load the PO later
            "FROM adempiere.pi_productlabel pp " +
            "JOIN adempiere.M_Locator ml ON (pp.M_Locator_ID = ml.M_Locator_ID) " +
            "WHERE " +
            "    ml.M_Warehouse_ID = ? " + // Parameter 1: Warehouse ID
            "    AND pp.IsActive = 'Y' " +
            "    AND pp.qcPassed = 'Y' " +
            "    AND pp.quantity > 0 " +
            "    AND NOT EXISTS (" +
            "        SELECT 1 " +
            "        FROM pi_productlabel pp_sales " +
            "        WHERE pp_sales.labeluuid = pp.labeluuid " +
            "        AND pp_sales.issotrx = 'Y'" +
            "    ) " +
            "ORDER BY pp.M_Locator_ID, pp.M_Product_ID";

        try {
            pstmt = DB.prepareStatement(sql, trxName);
            pstmt.setInt(1, M_Warehouse_ID);
            rs = pstmt.executeQuery();

            // Load the POs based on the IDs retrieved
            while (rs.next()) {
                int piProductLabelID = rs.getInt(1);
                PiProductLabel label = new PiProductLabel(ctx, piProductLabelID, trxName);
                if (label.get_ID() > 0) {
                    list.add(label);
                }
            }

        } catch (Exception e) {
            // In a real iDempiere environment, use log.log(Level.SEVERE, ...)
            System.err.println("Error fetching available labels by warehouse using JOIN: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DB.close(rs, pstmt);
        }

        return list;
    }
}