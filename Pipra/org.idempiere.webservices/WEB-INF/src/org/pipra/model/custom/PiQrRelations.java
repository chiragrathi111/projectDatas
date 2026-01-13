package org.pipra.model.custom;

import java.io.File;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.List;
import java.util.Properties;

import org.compiere.model.PO;
import org.compiere.model.Query;
import org.compiere.process.DocAction;
import org.compiere.process.IDocsPostProcess;
import org.idempiere.adInterface.x10.ADLoginRequest;
import org.idempiere.adInterface.x10.PalletLines;

public class PiQrRelations extends X_pi_qrRelations implements DocAction, IDocsPostProcess {

	private static final long serialVersionUID = 1L;

	public PiQrRelations(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}

	public PiQrRelations(Properties ctx, int pi_qr_ID, String trxName) {
		this(ctx, pi_qr_ID, trxName, (String[]) null);
	}

	public PiQrRelations(Properties ctx, int piQrId, String trxName, String... virtualColumns) {
		super(ctx, piQrId, trxName, virtualColumns);
	}

	public PiQrRelations(Properties ctx, String trxName, ADLoginRequest loginReq, PalletLines line) {
		this(ctx, 0, trxName);
		setAD_Client_ID(loginReq.getClientID());
		setAD_Org_ID(loginReq.getOrgID());
		setproductid(line.getProductId());
		setpalletuuid(line.getPalletUUId());
		setquantity(BigDecimal.valueOf(line.getQnty()));
		setpstatus(line.getStatus());
		setlocatorid(line.getLocatorId());
		setcorderlineid(line.getCOrderlineId());
		setminoutlineid(line.getMrlineId());
		if(line.getLocatorId() == 0)
			setisinlocator (0);
		else
			setisinlocator (1);
		if(line.getIsShippedOut())
			setisshippedout(1);
		else
			setisshippedout(0);
	}

	public static List<PO> getPiQrRelationsData(String columnName, String qrId, Properties ctx, String trxName) {
		Object obj = null;
		if (columnName == "loctorId" || columnName == "minoutlineid")
			obj = Integer.valueOf(qrId);
		else
			obj = qrId;

		List<PO> list = new Query(ctx, PiQrRelations.Table_Name, "" + columnName + "=?", trxName).setParameters(obj)
				.setOrderBy(PiQrRelations.COLUMNNAME_pi_qrRelations_ID).list();
		return list;
	}

	@Override
	public List<PO> getDocsPostProcess() {
		return null;
	}

	@Override
	public void setDocStatus(String newStatus) {

	}

	@Override
	public String getDocStatus() {
		return null;
	}

	@Override
	public boolean processIt(String action) throws Exception {
		return false;
	}

	@Override
	public boolean unlockIt() {
		return false;
	}

	@Override
	public boolean invalidateIt() {
		return false;
	}

	@Override
	public String prepareIt() {
		return null;
	}

	@Override
	public boolean approveIt() {
		return false;
	}

	@Override
	public boolean rejectIt() {
		return false;
	}

	@Override
	public String completeIt() {
		return null;
	}

	@Override
	public boolean voidIt() {
		return false;
	}

	@Override
	public boolean closeIt() {
		return false;
	}

	@Override
	public boolean reverseCorrectIt() {
		return false;
	}

	@Override
	public boolean reverseAccrualIt() {
		return false;
	}

	@Override
	public boolean reActivateIt() {
		return false;
	}

	@Override
	public String getSummary() {
		return null;
	}

	@Override
	public String getDocumentNo() {
		return null;
	}

	@Override
	public String getDocumentInfo() {
		return null;
	}

	@Override
	public File createPDF() {
		return null;
	}

	@Override
	public String getProcessMsg() {
		return null;
	}

	@Override
	public int getDoc_User_ID() {
		return 0;
	}

	@Override
	public int getC_Currency_ID() {
		return 0;
	}

	@Override
	public BigDecimal getApprovalAmt() {
		return null;
	}

	@Override
	public String getDocAction() {
		return null;
	}

}
