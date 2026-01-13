package com.pipra.rwpl.factory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.MAttachment;
import org.compiere.model.MAttachmentEntry;
import org.compiere.model.MProduct;
import org.compiere.model.Query;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Util;

import com.pipra.rwpl.model.PiProductLabel;
import com.pipra.rwpl.model.X_pi_import;
import com.pipra.rwpl.model.X_pi_importline;
import com.pipra.rwpl.utils.RwplUtils;

@org.adempiere.base.annotation.Process
public class ImportStorage extends SvrProcess {

	private int Record_ID = 0;
	private int AD_Client_ID = 0;
	private int AD_Org_ID = 0;
	private Properties ctx = null;
	private String trxName = null;
	X_pi_import pi_import = null;

	@Override
	protected void prepare() {
		Record_ID = getRecord_ID();
		ctx = getCtx();
		trxName = get_TrxName();
	}

	@Override
	protected String doIt() throws Exception {

		pi_import = new X_pi_import(ctx, Record_ID, trxName);
		if (pi_import == null || pi_import.get_ID() == 0) {
			return "Invalid Request: Import Record not found.";
		}

		AD_Client_ID = pi_import.getAD_Client_ID();
		AD_Org_ID = pi_import.getAD_Org_ID();

		if (AD_Client_ID == 0 || AD_Org_ID == 0) {
			return "Invalid Request: Client or Organization ID is missing.";
		}

		System.out.println("pi_import" + pi_import.getStatus());
		if (pi_import.getStatus() != null && pi_import.getStatus().equals("CO")) {
			return "Record Already processed";
		}

		MAttachment attachment = pi_import.getAttachment();
		if (attachment == null || attachment.get_ID() == 0) {
			return "Invalid Request: No Attachment associated with the record.";
		}

		MAttachmentEntry[] entries = attachment.getEntries();
		if (entries == null || entries.length == 0) {
			return "No Attachment Found.";
		}

		if (entries.length > 1) { // Changed from > 0 to > 1 as it should only allow ONE attachment
			return "Multiple Attachments Found. Only one file is allowed.";
		}

		File file = entries[0].getFile();
		return importStorage(file);
	}

	/**
	 * Internal class to hold a validated, parsed line of data.
	 */
	private static class ImportLine {
		final MProduct product;
		final int quantity;
		final int labelSize;
		final int M_Locator_ID;

		public ImportLine(MProduct product, int quantity, int labelSize, int M_Locator_ID) {
			this.product = product;
			this.quantity = quantity;
			this.labelSize = labelSize;
			this.M_Locator_ID = M_Locator_ID;
		}
	}

	/**
	 * Reads, validates, and imports the CSV file data. All lines must pass
	 * validation for the import to proceed.
	 */
	public String importStorage(File file) {

		final String delimiter = ",";
		int completedCount = 0;
		List<ImportLine> validLines = new ArrayList<>();

		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

			String line;
			int lineNumber = 0;

			if ((line = reader.readLine()) != null) {
				lineNumber++;
			} else {
				throw new AdempiereException("The imported file is empty.");
			}

			while ((line = reader.readLine()) != null) {
				lineNumber++;

				String trimmedLine = line.trim();
				if (trimmedLine.isEmpty()) {
					continue;
				}

				String[] values = trimmedLine.split(delimiter);
				if (values.length < 3) {
					throw new AdempiereException(
							"Line " + lineNumber + ": Insufficient columns (Expected 3+). Found " + values.length);
				}

				String productName = values[0].trim();
				String quantityStr = values[1].trim();
				String locatorName = values[2].trim();

				String labelSizeStr = (values.length > 3) ? values[3].trim() : "";
				if (Util.isEmpty(labelSizeStr)) {

					if (values.length < 4) {
						throw new AdempiereException("Line " + lineNumber
								+ ": Insufficient columns (Expected 4: Product, Quantity, Locator Name, Label Size). Found "
								+ values.length);
					}
					locatorName = values[2].trim();
					labelSizeStr = values[3].trim();
				}

				if (Util.isEmpty(productName)) {
					throw new AdempiereException("Line " + lineNumber + ": Product Name cannot be null.");
				}
				if (Util.isEmpty(locatorName)) {
					throw new AdempiereException("Line " + lineNumber + ": Locator Name cannot be null.");
				}
				if (Util.isEmpty(quantityStr)) {
					throw new AdempiereException("Line " + lineNumber + ": Quantity cannot be null.");
				}

				MProduct product = getProductByName(productName.trim());
				if (product == null || product.getM_Product_ID() <= 0) {
					throw new AdempiereException("Line " + lineNumber + ": Product not found for name: " + productName);
				}

				int M_Locator_ID = getLocatorIdByName(locatorName);
				if (M_Locator_ID <= 0) {
					throw new AdempiereException("Line " + lineNumber + ": Locator not found for name: " + locatorName
							+ " in Organization: " + AD_Org_ID);
				}

				int quantity;
				try {
					double doubleQuantity = Double.parseDouble(quantityStr);

					if (doubleQuantity != Math.floor(doubleQuantity)) {
						throw new AdempiereException(
								"Line " + lineNumber + ": Quantity must be a whole number, not " + quantityStr);
					}

					quantity = (int) doubleQuantity;

					if (quantity <= 0) {
						throw new AdempiereException("Line " + lineNumber + ": Quantity must be a positive value.");
					}
				} catch (NumberFormatException e) {
					throw new AdempiereException("Line " + lineNumber + ": Invalid Quantity format: " + quantityStr);
				}

				int labelSize = 1;
				if (!Util.isEmpty(labelSizeStr)) {
					try {
						int parsedSize = Integer.parseInt(labelSizeStr);
						labelSize = (parsedSize <= 0) ? 1 : parsedSize;
					} catch (NumberFormatException e) {
						labelSize = 1;
					}
				}

				validLines.add(new ImportLine(product, quantity, labelSize, M_Locator_ID));

			}

			if (validLines.isEmpty()) {
				return "File is valid but contains no data lines to import.";
			}

			System.out.println("Validation complete. Starting data import...");

			for (ImportLine data : validLines) {

				createLabel(data.quantity, data.labelSize, AD_Client_ID, AD_Org_ID, data.product.getM_Product_ID(),
						data.M_Locator_ID);

				RwplUtils.addOrReduceInventory(data.product, data.quantity, data.M_Locator_ID, AD_Org_ID, ctx, trxName,
						false);

				completedCount++;
			}

			String summary = String.format("CSV Import finished successfully. Total lines imported: %d.",
					completedCount);

			pi_import.setStatus("CO");
			pi_import.saveEx();

			return summary;

		} catch (AdempiereException e) {
			String errorMsg = "Invalid File: Import aborted due to data error. " + e.getMessage();

			return errorMsg;
		} catch (Exception e) {
			return "An unexpected error occurred during file processing: " + e.getLocalizedMessage();
		}
	}

	/**
	 * Creates the necessary product labels for the imported quantity. Changed
	 * labelSizeStr (String) to labelSize (int) for cleaner logic.
	 */
	private void createLabel(int quantity, int labelSize, int AD_Client_ID, int AD_Org_ID, int M_Product_ID,
			int M_Locator_ID) {

		int effectiveLabelSize = (labelSize <= 0) ? 1 : labelSize;

		int totalLabelsToCreate = (quantity + effectiveLabelSize - 1) / effectiveLabelSize;
		int remainingQuantity = quantity;

		for (int i = 0; i < totalLabelsToCreate; i++) {
			int labelQuantity = Math.min(effectiveLabelSize, remainingQuantity);

			PiProductLabel label = new PiProductLabel(ctx, trxName, AD_Client_ID, AD_Org_ID, M_Product_ID, M_Locator_ID,
					0, 0, false, BigDecimal.valueOf(labelQuantity), null);

			label.saveEx();

			X_pi_importline importline = new X_pi_importline(ctx, 0, trxName);
			importline.setAD_Org_ID(AD_Org_ID);
			importline.setpi_import_ID(Record_ID);
			importline.setpi_productLabel_ID(label.get_ID());
			importline.saveEx();

			remainingQuantity -= labelQuantity;
			if (remainingQuantity <= 0)
				break;
		}

	}

	/**
	 * Look up MProduct by Name for the current Client.
	 */
	private MProduct getProductByName(String name) {
		String where = "AD_Client_ID = ? AND TRIM(Name) = ?";
		MProduct product = new Query(ctx, MProduct.Table_Name, where, trxName).setParameters(AD_Client_ID, name)
				.first();
		return product;
	}

	/**
	 * Look up M_Locator_ID by Name (Value) for the current Client and Organization.
	 */
	private int getLocatorIdByName(String name) {

		String sql = "SELECT M_Locator_ID FROM M_Locator WHERE AD_Client_ID=? AND AD_Org_ID=? AND Value=? AND IsActive='Y'";
		return DB.getSQLValueEx(trxName, sql, AD_Client_ID, AD_Org_ID, name);
	}

}