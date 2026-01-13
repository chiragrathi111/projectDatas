package org.idempiere.webservices.process;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.compiere.model.MProcessPara;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.realmeds.tissue.model.TCPlantTag;

public class TissueCulturePlantProcess extends SvrProcess {

	private static final String TABLE_PLANT = "tc_planttag";
	private int count = 0;
	private int plantId = 0;

	@Override
	protected void prepare() {
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++) {
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null)
				;
			else if (name.equals("aint"))
				count = ((BigDecimal) para[i].getParameter()).intValue();
			else
				MProcessPara.validateUnknownParameter(getProcessInfo().getAD_Process_ID(), para[i]);
		}
	}

	@Override
	protected String doIt() throws Exception {
		LocalDateTime currentDate = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy:HH:mm:ss");
		String formattedDate = currentDate.format(formatter);
		List<PlantTagData> plantTagList = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			TCPlantTag plant = new TCPlantTag(getCtx(), 0, get_TrxName());
			plant.setName("PlantTag_" + i + "_" + formattedDate);
			plant.saveEx();
			commitEx();
			plantId = plant.get_ID();
			int retryCount = 0;
			String PlantUUId = null;
			while (PlantUUId == null && retryCount < 10) {
				PlantUUId = DB.getSQLValueString(null,
						"SELECT c_uuid FROM adempiere."+ TABLE_PLANT +" WHERE tc_planttag_id = ?", plantId);
				if (PlantUUId == null) {
					Thread.sleep(2000); // Wait for 500ms before retrying
					retryCount++;
				}
			}
			if (PlantUUId == null) {
				throw new Exception("Failed to get UUID for Plant Tag: " + plantId);
			}
			plantTagList.add(new PlantTagData(plantId, PlantUUId)); 
		}
		return "Report generated successfully!";
	}

	public class PlantTagData {
		private int plantId;
		private String plantUUId;

		public PlantTagData(int plantId, String plantUUId) {
			this.plantId = plantId;
			this.plantUUId = plantUUId;
		}

		public int getPlantId() {
			return plantId;
		}

		public String getPlantUUId() {
			return plantUUId;
		}
	}
}
