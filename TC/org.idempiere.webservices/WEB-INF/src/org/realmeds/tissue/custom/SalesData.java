package org.realmeds.tissue.custom;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.compiere.model.MOrderLine;
import org.compiere.util.DB;

public class SalesData {

	public SalesData(String records) {
	}

	public SalesData() {
	}

	private List<Record> fetchRelatedRecords(String uuid, String tableName) {
		String sql = "SELECT ps.codeno AS CropType, v.codeno AS Variety, ph.parentCultureLine AS ParentCultureLine, TO_CHAR(ph.operationDate, 'DDMMYY') AS Date "
				+ "FROM adempiere." + tableName + " ph "
				+ "JOIN adempiere.tc_plantspecies ps ON ps.tc_plantspecies_id = ph.tc_species_id "
				+ "JOIN adempiere.tc_variety v ON v.tc_variety_id = ph.tc_variety_id " + "WHERE ph.c_uuid = ?;";
		List<Record> records = new ArrayList<>();
		try (PreparedStatement pstmt = DB.prepareStatement(sql, null)) {
			pstmt.setString(1, uuid);
			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					Record record = new Record();
					record.setCropType(rs.getString("CropType"));
					record.setVariety(rs.getString("Variety"));
					record.setDate(rs.getString("Date"));
					record.setParentCultureLine(rs.getString("ParentCultureLine"));
					records.add(record);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return records;
	}
	
	private List<Record> fetchCultureRecords(String tableName, String cultureUUID) {
		String sql = "SELECT ps.codeno AS CropType, v.codeno AS Variety, ph.parentCultureLine AS ParentCultureLine, TO_CHAR(ph.cultureoperationDate, 'DDMMYY') AS Date "
				+ "FROM adempiere." + tableName + " ph "
				+ "JOIN adempiere.tc_plantspecies ps ON ps.tc_plantspecies_id = ph.tc_species_id "
				+ "JOIN adempiere.tc_variety v ON v.tc_variety_id = ph.tc_variety_id " + "WHERE ph.c_uuid = ?;";
		List<Record> records = new ArrayList<>();
		try (PreparedStatement pstmt = DB.prepareStatement(sql, null)) {
			pstmt.setString(1, cultureUUID);
			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					Record record = new Record();
					record.setCropType(rs.getString("CropType"));
					record.setVariety(rs.getString("Variety"));
					record.setDate(rs.getString("Date"));
					record.setParentCultureLine(rs.getString("ParentCultureLine"));
					records.add(record);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return records;
	}

	public void getData(MOrderLine lines, String description) {

		String productName = lines.getM_Product().getName();

		String tableName = getTableNameByProduct(productName);

		if (tableName == null) {
			System.out.println("No matching table for product: " + productName);
			return;
		}

		List<Record> relatedRecords = fetchRelatedRecords(description, tableName);
		for (Record record : relatedRecords) {
			lines.setDescription(record.getCropType() + " " + record.getVariety() + " " + record.getParentCultureLine()
					+ " " + record.getDate());
			lines.saveEx();
		}
	}

	public void getCultureData(String table, MOrderLine lines, String cultureUUId) {
		List<Record> cultureRecords = fetchCultureRecords(table, cultureUUId);
		for (Record record : cultureRecords) {
			lines.setDescription(record.getCropType() + " " + record.getVariety() + " " + record.getParentCultureLine()
					+ " " + record.getDate());
			lines.saveEx();
		}
	}

	private String getTableNameByProduct(String productName) {
		if ("H01".equalsIgnoreCase(productName)) {
			return "tc_primaryhardeninglabel";
		} else if ("H02".equalsIgnoreCase(productName)) {
			return "tc_secondaryhardeninglabel";
		}
		return null;
	}
}
