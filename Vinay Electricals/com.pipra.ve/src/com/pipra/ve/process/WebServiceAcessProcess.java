package com.pipra.ve.process;

import org.compiere.model.PO;
import org.compiere.model.Query;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.idempiere.webservices.model.X_WS_WebServiceTypeAccess;

@org.adempiere.base.annotation.Process
public class WebServiceAcessProcess extends SvrProcess {

	private int WS_WebServiceType_ID = 0;
	private int[] AD_Role_ID;

	@Override
	protected void prepare() {
		for (ProcessInfoParameter para : getParameter()) {
			String name = para.getParameterName();
			switch (name) {
			case "WS_WebServiceType_ID":
				WS_WebServiceType_ID = para.getParameterAsInt();
				break;
			case "AD_Role_ID":
				AD_Role_ID = para.getParameterAsIntArray();
				break;
			}
		}
	}

	@Override
	protected String doIt() throws Exception {

		if (AD_Role_ID != null && AD_Role_ID.length > 0)
			for (int id : AD_Role_ID) {
				System.out.println(id);

				PO po = new Query(getCtx(), X_WS_WebServiceTypeAccess.Table_Name,
						"ad_role_ID = ? AND ws_webservicetype_id = ?", get_TrxName())
						.setParameters(id, WS_WebServiceType_ID).firstOnly();

				if (po == null || po.get_ID() == 0) {
					X_WS_WebServiceTypeAccess access = new X_WS_WebServiceTypeAccess(getCtx(), 0, get_TrxName());
					access.setWS_WebServiceType_ID(WS_WebServiceType_ID);
					access.setAD_Role_ID(id);
					access.saveEx();
				}
			}
		return "OK";
	}

}