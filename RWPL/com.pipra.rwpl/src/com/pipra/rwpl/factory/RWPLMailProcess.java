package com.pipra.rwpl.factory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.Timestamp;
import java.util.List;
import java.util.logging.Level;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.compiere.model.MClient;
import org.compiere.model.MQuery;
import org.compiere.model.PO;
import org.compiere.model.PrintInfo;
import org.compiere.model.Query;
import org.compiere.print.MPrintFormat;
import org.compiere.print.ReportEngine;
import org.compiere.process.SvrProcess;
import org.compiere.util.EMail;
import org.compiere.util.Env;

import com.pipra.rwpl.model.X_pi_email;

@org.adempiere.base.annotation.Process
public class RWPLMailProcess extends SvrProcess {
	private static final String REPORT_SALES_PLAN_REPORT = "Sales Plan Detail View";
	private static final String REPORT_TOTAL_TONS_IN_WAREHOUSE = "Total Tons In Warehouse";
	private static final String REPORT_TOTAL_TRANSACTION_MATERIALS = "Total Received/Dispatch Materials";
	private static final String REPORT_INVENTORY_VIEW_FOR_EMAIL = "Inventory View For Email";
	private static final String REPORT_EMPTY_LOCATOR = "Empty Locator View";
	private static final String REPORT_TOTAL_RECEIVED_MATERIALS = "Total Received Materials";
	private static final String REPORT_TOTAL_DISPATCH_MATERIALS = "Total Dispatch Materials";

	@Override
	protected void prepare() {
	}

	@Override
	protected String doIt() throws Exception {
		StringBuilder result = new StringBuilder();
		int successCount = 0;
		int failureCount = 0;

		MClient client = MClient.get(getCtx());
		if (client.getSMTPHost() == null || client.getSMTPHost().isEmpty()) {
			throw new Exception("SMTP Host not configured in Request Management");
		}

		String senderEmail = client.getRequestEMail();
		if (senderEmail == null || senderEmail.isEmpty()) {
			throw new Exception("Sender email not configured in Request Management");
		}
		Timestamp yesterday = new Timestamp(System.currentTimeMillis() - 86400000);
		java.sql.Date yesterdayDate = new java.sql.Date(yesterday.getTime());

		File[] pdfFiles = { generateReceivedDispatchReport(yesterdayDate, true),
				generateReceivedDispatchReport(yesterdayDate, false), generateWarehouseReport(),
				generateInventoryReport(), generateEmpytyLocatorReport(),
				generateSalesPlanDetailViewReport(yesterdayDate) };
		File mergedFile = File.createTempFile("Daily_Reports_Merged_", ".xlsx");
		try (XSSFWorkbook mergedWorkbook = new XSSFWorkbook()) {
			try {
				for (File reportFile : pdfFiles) {
					if (reportFile == null || !reportFile.exists()) {
						continue;
					}

					try (FileInputStream fis = new FileInputStream(reportFile);
							XSSFWorkbook sourceWorkbook = new XSSFWorkbook(fis)) {

						Sheet sourceSheet = sourceWorkbook.getSheetAt(0);
						String sheetName = getSheetName(reportFile.getName());
						Sheet newSheet = mergedWorkbook.createSheet(sheetName);

						copySheet(sourceSheet, newSheet);
					}
				}

				try (FileOutputStream fos = new FileOutputStream(mergedFile)) {
					mergedWorkbook.write(fos);
				}

				String subject = "Daily Operations Reports";
				String message = "Please find attached the daily operations reports package.";

				List<PO> recipientEmailList = new Query(getCtx(), X_pi_email.Table_Name, "AD_Client_ID=?",
						get_TrxName()).setParameters(Env.getAD_Client_ID(getCtx())).list();

				if (recipientEmailList.isEmpty()) {
					return "No recipients found in pi_multiusermail for current user";
				}

				for (PO userlist : recipientEmailList) {
					X_pi_email userList = new X_pi_email(getCtx(), userlist.get_ID(), get_TrxName());
					String recipientEmail = userList.getEMail() != null ? userList.getEMail().trim() : "";

					if (recipientEmail.isEmpty()) {
						result.append("Skipped: Empty email for record ID ").append(userlist.get_ID()).append("\n");
						failureCount++;
						continue;
					}

					if (!EMail.validate(recipientEmail)) {
						result.append("Skipped: Invalid email format '").append(recipientEmail).append("'\n");
						failureCount++;
						continue;
					}

					EMail email = null;
					try {
						email = client.createEMailFrom(senderEmail, recipientEmail, subject, message, false);

						if (email == null) {
							throw new Exception("Failed to create email object");
						}

						for (File pdfFile : pdfFiles) {
							if (pdfFile != null && pdfFile.exists()) {
								email.addAttachment(pdfFile);
							}
						}
						email.addAttachment(mergedFile);

						String status = email.send();
						if (EMail.SENT_OK.equals(status)) {
							result.append("Email with reports sent to '").append(recipientEmail).append("'\n");
							successCount++;
						} else {
							result.append("Failed: Delivery to '").append(recipientEmail).append("'\n");
							failureCount++;
						}
					} catch (Exception e) {
						result.append("Error sending to '").append(recipientEmail).append("' - ").append(e.getMessage())
								.append("\n");
						failureCount++;
						log.severe("Email Exception for " + recipientEmail + ": " + e.getMessage());
					}
				}
			} finally {
				for (File reportFile : pdfFiles) {
					try {
						if (reportFile != null && reportFile.exists()) {
							reportFile.delete();
						}
					} catch (Exception e) {
						log.warning("Error deleting temporary file: " + e.getMessage());
					}
				}
			}
		}
		String summary = String.format("Email sending completed. Success: %d, Failed: %d\nDetails:\n%s", successCount,
				failureCount, result.toString());

		return summary;
	}

	private String getSheetName(String fileName) {
		String name = fileName.replaceAll("^[^_]+_", "").replaceAll("_[^_]+$", "");
		name = name.replace("_", " ");
		return name.substring(0, Math.min(name.length(), 31));
	}

	private void copySheet(Sheet source, Sheet target) {

		for (int i = 0; i < source.getNumMergedRegions(); i++) {
			CellRangeAddress mergedRegion = source.getMergedRegion(i);
			target.addMergedRegion(mergedRegion);
		}

		for (int i = 0; i <= source.getLastRowNum(); i++) {
			Row sourceRow = source.getRow(i);
			if (sourceRow == null) {
				continue;
			}

			Row newRow = target.createRow(i);

			for (int j = 0; j < sourceRow.getLastCellNum(); j++) {
				Cell sourceCell = sourceRow.getCell(j);
				if (sourceCell == null) {
					continue;
				}

				Cell newCell = newRow.createCell(j);

				CellStyle newCellStyle = target.getWorkbook().createCellStyle();
				newCellStyle.cloneStyleFrom(sourceCell.getCellStyle());
				newCell.setCellStyle(newCellStyle);

				switch (sourceCell.getCellType()) {
				case STRING:
					newCell.setCellValue(sourceCell.getStringCellValue());
					break;
				case NUMERIC:
					if (DateUtil.isCellDateFormatted(sourceCell)) {
						newCell.setCellValue(sourceCell.getDateCellValue());
						CellStyle dateStyle = target.getWorkbook().createCellStyle();
						dateStyle.cloneStyleFrom(sourceCell.getCellStyle());
						dateStyle.setDataFormat(target.getWorkbook().createDataFormat().getFormat("dd-MMM-yyyy"));
						newCell.setCellStyle(dateStyle);
					} else {
						newCell.setCellValue(sourceCell.getNumericCellValue());
					}
					break;
				case BOOLEAN:
					newCell.setCellValue(sourceCell.getBooleanCellValue());
					break;
				case FORMULA:
					newCell.setCellFormula(sourceCell.getCellFormula());
					break;
				case BLANK:
					newCell.setBlank();
					break;
				default:
					newCell.setCellValue(sourceCell.toString());
				}
			}
		}
		for (int i = 0; i < source.getRow(0).getLastCellNum(); i++) {
			target.autoSizeColumn(i);
			target.setColumnWidth(i, target.getColumnWidth(i) + 1000);
		}
	}

	private File generateWarehouseReport() throws Exception {
		PO po = new Query(getCtx(), MPrintFormat.Table_Name, "name = ?", get_TrxName())
				.setParameters(REPORT_TOTAL_TONS_IN_WAREHOUSE).firstOnly();
		if (po == null) {
			log.warning("Print format not found: " + REPORT_TOTAL_TONS_IN_WAREHOUSE);
			return null;
		}

		MPrintFormat pf = new MPrintFormat(getCtx(), po.get_ID(), get_TrxName());
		MQuery query = new MQuery();
		PrintInfo info = new PrintInfo(REPORT_TOTAL_TONS_IN_WAREHOUSE, pf.getAD_Table_ID(),
				po != null ? po.get_ID() : 0);

		ReportEngine re = new ReportEngine(getCtx(), pf, query, info, get_TrxName());

		File excelFile = File.createTempFile(REPORT_TOTAL_TONS_IN_WAREHOUSE.replace(" ", "_") + "_", ".xlsx");

		re.createXLSX(excelFile, Env.getLanguage(getCtx()));

		File finalFile = File.createTempFile(REPORT_TOTAL_TONS_IN_WAREHOUSE.replace(" ", "_") + "_", ".xlsx");
		FileInputStream fis = null;
		FileOutputStream fos = null;
		XSSFWorkbook workbook = null;

		try {
			fis = new FileInputStream(excelFile);
			workbook = new XSSFWorkbook(fis);
			Sheet sheet = workbook.getSheetAt(0);
			if (sheet.getLastRowNum() >= 0) {
				sheet.shiftRows(0, sheet.getLastRowNum(), 3, true, false);
			}
			sheet.createRow(0);

			CellStyle headerStyle = workbook.createCellStyle();
			headerStyle.setAlignment(HorizontalAlignment.CENTER);
			headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			Font headerFont = workbook.createFont();
			headerFont.setBold(true);
			headerFont.setFontHeightInPoints((short) 16);
			headerFont.setColor(IndexedColors.WHITE.getIndex());
			headerStyle.setFont(headerFont);
			headerStyle.setFillForegroundColor(IndexedColors.BLUE.getIndex());
			headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

			int lastCol = 0;
			if (sheet.getRow(3) != null) {
				lastCol = sheet.getRow(3).getLastCellNum() - 1;
				if (lastCol < 0)
					lastCol = 0;
			}
			if (lastCol > 0) {
				Row titleRow = sheet.createRow(1);
				Cell titleCell = titleRow.createCell(0);
				titleCell.setCellValue(REPORT_TOTAL_TONS_IN_WAREHOUSE.toUpperCase());
				titleCell.setCellStyle(headerStyle);
				if (lastCol > 0) {
					sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, lastCol));
				}
				sheet.createRow(2);
			}
			for (int i = 0; i <= lastCol; i++) {
				sheet.autoSizeColumn(i);
			}
			fos = new FileOutputStream(finalFile);
			workbook.write(fos);

			return finalFile;
		} catch (Exception e) {
			log.log(Level.SEVERE, "Error adding headers", e);
			return excelFile;
		} finally {
			try {
				if (fos != null)
					fos.close();
			} catch (Exception e) {
			}
			try {
				if (workbook != null)
					workbook.close();
			} catch (Exception e) {
			}
			try {
				if (fis != null)
					fis.close();
			} catch (Exception e) {
			}
			excelFile.delete();
		}

	}

	private File generateEmpytyLocatorReport() throws Exception {
		PO po = new Query(getCtx(), MPrintFormat.Table_Name, "name = ?", get_TrxName())
				.setParameters(REPORT_EMPTY_LOCATOR).firstOnly();
		if (po == null) {
			log.warning("Print format not found: " + REPORT_EMPTY_LOCATOR);
			return null;
		}

		MPrintFormat pf = new MPrintFormat(getCtx(), po.get_ID(), get_TrxName());
		MQuery query = new MQuery();
		PrintInfo info = new PrintInfo(REPORT_EMPTY_LOCATOR, pf.getAD_Table_ID(), po != null ? po.get_ID() : 0);

		ReportEngine re = new ReportEngine(getCtx(), pf, query, info, get_TrxName());

		File excelFile = File.createTempFile(REPORT_EMPTY_LOCATOR.replace(" ", "_") + "_", ".xlsx");

		re.createXLSX(excelFile, Env.getLanguage(getCtx()));

		File finalFile = File.createTempFile(REPORT_EMPTY_LOCATOR.replace(" ", "_") + "_", ".xlsx");
		FileInputStream fis = null;
		FileOutputStream fos = null;
		XSSFWorkbook workbook = null;

		try {
			fis = new FileInputStream(excelFile);
			workbook = new XSSFWorkbook(fis);
			Sheet sheet = workbook.getSheetAt(0);
			if (sheet.getLastRowNum() >= 0) {
				sheet.shiftRows(0, sheet.getLastRowNum(), 3, true, false);
			}
			sheet.createRow(0);

			CellStyle headerStyle = workbook.createCellStyle();
			headerStyle.setAlignment(HorizontalAlignment.CENTER);
			headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			Font headerFont = workbook.createFont();
			headerFont.setBold(true);
			headerFont.setFontHeightInPoints((short) 16);
			headerFont.setColor(IndexedColors.WHITE.getIndex());
			headerStyle.setFont(headerFont);
			headerStyle.setFillForegroundColor(IndexedColors.BLUE.getIndex());
			headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

			int lastCol = 0;
			if (sheet.getRow(3) != null) {
				lastCol = sheet.getRow(3).getLastCellNum() - 1;
				if (lastCol < 0)
					lastCol = 0;
			}
			if (lastCol > 0) {
				Row titleRow = sheet.createRow(1);
				Cell titleCell = titleRow.createCell(0);
				titleCell.setCellValue(REPORT_EMPTY_LOCATOR.toUpperCase());
				titleCell.setCellStyle(headerStyle);
				if (lastCol > 0) {
					sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, lastCol));
				}
				sheet.createRow(2);
			}
			for (int i = 0; i <= lastCol; i++) {
				sheet.autoSizeColumn(i);
			}
			fos = new FileOutputStream(finalFile);
			workbook.write(fos);

			return finalFile;
		} catch (Exception e) {
			log.log(Level.SEVERE, "Error adding headers", e);
			return excelFile;
		} finally {
			try {
				if (fos != null)
					fos.close();
			} catch (Exception e) {
			}
			try {
				if (workbook != null)
					workbook.close();
			} catch (Exception e) {
			}
			try {
				if (fis != null)
					fis.close();
			} catch (Exception e) {
			}
			excelFile.delete();
		}
	}

	private File generateInventoryReport() throws Exception {
		PO po = new Query(getCtx(), MPrintFormat.Table_Name, "name = ?", get_TrxName())
				.setParameters(REPORT_INVENTORY_VIEW_FOR_EMAIL).firstOnly();
		if (po == null) {
			log.warning("Print format not found: " + REPORT_INVENTORY_VIEW_FOR_EMAIL);
			return null;
		}

		MPrintFormat pf = new MPrintFormat(getCtx(), po.get_ID(), get_TrxName());
		MQuery query = new MQuery();
		PrintInfo info = new PrintInfo(REPORT_INVENTORY_VIEW_FOR_EMAIL, pf.getAD_Table_ID(),
				po != null ? po.get_ID() : 0);

		ReportEngine re = new ReportEngine(getCtx(), pf, query, info, get_TrxName());

		File excelFile = File.createTempFile(REPORT_INVENTORY_VIEW_FOR_EMAIL.replace(" ", "_") + "_", ".xlsx");

		re.createXLSX(excelFile, Env.getLanguage(getCtx()));

		File finalFile = File.createTempFile(REPORT_INVENTORY_VIEW_FOR_EMAIL.replace(" ", "_") + "_", ".xlsx");
		FileInputStream fis = null;
		FileOutputStream fos = null;
		XSSFWorkbook workbook = null;

		try {
			fis = new FileInputStream(excelFile);
			workbook = new XSSFWorkbook(fis);
			Sheet sheet = workbook.getSheetAt(0);
			if (sheet.getLastRowNum() >= 0) {
				sheet.shiftRows(0, sheet.getLastRowNum(), 3, true, false);
			}
			sheet.createRow(0);

			CellStyle headerStyle = workbook.createCellStyle();
			headerStyle.setAlignment(HorizontalAlignment.CENTER);
			headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			Font headerFont = workbook.createFont();
			headerFont.setBold(true);
			headerFont.setFontHeightInPoints((short) 16);
			headerFont.setColor(IndexedColors.WHITE.getIndex());
			headerStyle.setFont(headerFont);
			headerStyle.setFillForegroundColor(IndexedColors.BLUE.getIndex());
			headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

			int lastCol = 0;
			if (sheet.getRow(3) != null) {
				lastCol = sheet.getRow(3).getLastCellNum() - 1;
				if (lastCol < 0)
					lastCol = 0;
			}
			if (lastCol > 0) {
				Row titleRow = sheet.createRow(1);
				Cell titleCell = titleRow.createCell(0);
				titleCell.setCellValue(REPORT_INVENTORY_VIEW_FOR_EMAIL.toUpperCase());
				titleCell.setCellStyle(headerStyle);
				if (lastCol > 0) {
					sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, lastCol));
				}
				sheet.createRow(2);
			}
			for (int i = 0; i <= lastCol; i++) {
				sheet.autoSizeColumn(i);
			}
			fos = new FileOutputStream(finalFile);
			workbook.write(fos);

			return finalFile;
		} catch (Exception e) {
			log.log(Level.SEVERE, "Error adding headers", e);
			return excelFile;
		} finally {
			try {
				if (fos != null)
					fos.close();
			} catch (Exception e) {
			}
			try {
				if (workbook != null)
					workbook.close();
			} catch (Exception e) {
			}
			try {
				if (fis != null)
					fis.close();
			} catch (Exception e) {
			}
			excelFile.delete();
		}
	}

	private File generateReceivedDispatchReport(java.sql.Date reportDate, boolean isSOTrx) throws Exception {
		PO po = new Query(getCtx(), MPrintFormat.Table_Name, "name = ?", get_TrxName())
				.setParameters(REPORT_TOTAL_TRANSACTION_MATERIALS).firstOnly();
		if (po == null) {
			log.warning("Print format not found: " + REPORT_TOTAL_TRANSACTION_MATERIALS);
			return null;
		}

		MPrintFormat pf = new MPrintFormat(getCtx(), po.get_ID(), get_TrxName());
		MQuery query = new MQuery();
		query.addRestriction("report_date = TO_DATE('" + reportDate + "','YYYY-MM-DD')");
		query.addRestriction("IsSOTrx = '" + (isSOTrx ? "Y" : "N") + "'");

		PrintInfo info = new PrintInfo(REPORT_TOTAL_TRANSACTION_MATERIALS, pf.getAD_Table_ID(),
				po != null ? po.get_ID() : 0);

		ReportEngine re = new ReportEngine(getCtx(), pf, query, info, get_TrxName());

		String filePrefix = isSOTrx ? REPORT_TOTAL_DISPATCH_MATERIALS : REPORT_TOTAL_RECEIVED_MATERIALS;

		File excelFile = File.createTempFile(filePrefix.replace(" ", "_") + "_", ".xlsx");

		re.createXLSX(excelFile, Env.getLanguage(getCtx()));

		File finalFile = File.createTempFile(filePrefix.replace(" ", "_") + "_", ".xlsx");
		FileInputStream fis = null;
		FileOutputStream fos = null;
		XSSFWorkbook workbook = null;

		try {
			fis = new FileInputStream(excelFile);
			workbook = new XSSFWorkbook(fis);
			Sheet sheet = workbook.getSheetAt(0);
			if (sheet.getLastRowNum() >= 0) {
				sheet.shiftRows(0, sheet.getLastRowNum(), 3, true, false);
			}
			sheet.createRow(0);

			CellStyle headerStyle = workbook.createCellStyle();
			headerStyle.setAlignment(HorizontalAlignment.CENTER);
			headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			Font headerFont = workbook.createFont();
			headerFont.setBold(true);
			headerFont.setFontHeightInPoints((short) 16);
			headerFont.setColor(IndexedColors.WHITE.getIndex());
			headerStyle.setFont(headerFont);
			headerStyle.setFillForegroundColor(IndexedColors.BLUE.getIndex());
			headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

			int lastCol = 0;
			if (sheet.getRow(5) != null) {
				lastCol = sheet.getRow(5).getLastCellNum() - 1;
				if (lastCol < 0)
					lastCol = 0;
			}
			if (lastCol > 0) {
				Row titleRow = sheet.createRow(1);
				Cell titleCell = titleRow.createCell(0);
				titleCell.setCellValue(filePrefix.toUpperCase());
				titleCell.setCellStyle(headerStyle);
				if (lastCol > 0) {
					sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, lastCol));
				}
				sheet.createRow(2);
			}
			for (int i = 0; i <= lastCol; i++) {
				sheet.autoSizeColumn(i);
			}
			fos = new FileOutputStream(finalFile);
			workbook.write(fos);

			return finalFile;
		} catch (Exception e) {
			log.log(Level.SEVERE, "Error adding headers", e);
			return excelFile;
		} finally {
			try {
				if (fos != null)
					fos.close();
			} catch (Exception e) {
			}
			try {
				if (workbook != null)
					workbook.close();
			} catch (Exception e) {
			}
			try {
				if (fis != null)
					fis.close();
			} catch (Exception e) {
			}
			excelFile.delete();
		}
	}

	private File generateSalesPlanDetailViewReport(java.sql.Date reportDate) throws Exception {
		PO po = new Query(getCtx(), MPrintFormat.Table_Name, "name = ?", get_TrxName())
				.setParameters(REPORT_SALES_PLAN_REPORT).firstOnly();
		if (po == null) {
			log.warning("Print format not found: " + REPORT_SALES_PLAN_REPORT);
			return null;
		}

		MPrintFormat pf = new MPrintFormat(getCtx(), po.get_ID(), get_TrxName());
		MQuery query = new MQuery();
		query.addRestriction("salesplandate = TO_DATE('" + reportDate + "','YYYY-MM-DD')");

		PrintInfo info = new PrintInfo(REPORT_SALES_PLAN_REPORT, pf.getAD_Table_ID(), po != null ? po.get_ID() : 0);

		ReportEngine re = new ReportEngine(getCtx(), pf, query, info, get_TrxName());

		File excelFile = File.createTempFile(REPORT_SALES_PLAN_REPORT.replace(" ", "_") + "_", ".xlsx");

		if (re.getRowCount() == 0) {
			log.warning("No data found for date: " + reportDate);
			return null;
		}

		re.createXLSX(excelFile, Env.getLanguage(getCtx()));

		File finalFile = File.createTempFile(REPORT_SALES_PLAN_REPORT.replace(" ", "_") + "_", ".xlsx");
		FileInputStream fis = null;
		FileOutputStream fos = null;
		XSSFWorkbook workbook = null;

		try {
			fis = new FileInputStream(excelFile);
			workbook = new XSSFWorkbook(fis);
			Sheet sheet = workbook.getSheetAt(0);
			if (sheet.getLastRowNum() >= 0) {
				sheet.shiftRows(0, sheet.getLastRowNum(), 3, true, false);
			}
			sheet.createRow(0);

			CellStyle headerStyle = workbook.createCellStyle();
			headerStyle.setAlignment(HorizontalAlignment.CENTER);
			headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			Font headerFont = workbook.createFont();
			headerFont.setBold(true);
			headerFont.setFontHeightInPoints((short) 16);
			headerFont.setColor(IndexedColors.WHITE.getIndex());
			headerStyle.setFont(headerFont);
			headerStyle.setFillForegroundColor(IndexedColors.BLUE.getIndex());
			headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

			int lastCol = 0;
			if (sheet.getRow(6) != null) {
				lastCol = sheet.getRow(6).getLastCellNum() - 1;
				if (lastCol < 0)
					lastCol = 0;
			}
			if (lastCol > 0) {
				Row titleRow = sheet.createRow(1);
				Cell titleCell = titleRow.createCell(0);
				titleCell.setCellValue(REPORT_SALES_PLAN_REPORT.toUpperCase());
				titleCell.setCellStyle(headerStyle);
				if (lastCol > 0) {
					sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, lastCol));
				}
				sheet.createRow(2);
			}
			for (int i = 0; i <= lastCol; i++) {
				sheet.autoSizeColumn(i);
			}
			fos = new FileOutputStream(finalFile);
			workbook.write(fos);

			return finalFile;
		} catch (Exception e) {
			log.log(Level.SEVERE, "Error adding headers", e);
			return excelFile;
		} finally {
			try {
				if (fos != null)
					fos.close();
			} catch (Exception e) {
			}
			try {
				if (workbook != null)
					workbook.close();
			} catch (Exception e) {
			}
			try {
				if (fis != null)
					fis.close();
			} catch (Exception e) {
			}
			excelFile.delete();
		}
	}

}
