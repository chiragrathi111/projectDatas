package com.pipra.ve.webservice;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Collectors;

import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.adempiere.exceptions.AdempiereException;
//import org.codehaus.jettison.json.JSONException;
//import org.codehaus.jettison.json.JSONObject;
import org.compiere.model.I_M_InOut;
import org.compiere.model.I_M_Warehouse;
import org.compiere.model.MAcctSchema;
import org.compiere.model.MBPartner;
import org.compiere.model.MBPartnerLocation;
import org.compiere.model.MDocType;
import org.compiere.model.MElementValue;
import org.compiere.model.MFactAcct;
import org.compiere.model.MGLCategory;
import org.compiere.model.MInOut;
import org.compiere.model.MInOutConfirm;
import org.compiere.model.MInOutLine;
import org.compiere.model.MInOutLineConfirm;
import org.compiere.model.MInventoryLine;
import org.compiere.model.MInvoice;
import org.compiere.model.MLocation;
import org.compiere.model.MLocator;
import org.compiere.model.MMovement;
import org.compiere.model.MMovementLine;
import org.compiere.model.MMovementLineMA;
import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.compiere.model.MPeriod;
import org.compiere.model.MPriceListVersion;
import org.compiere.model.MProduct;
import org.compiere.model.MProductCategory;
import org.compiere.model.MProductPrice;
import org.compiere.model.MRMA;
import org.compiere.model.MShipper;
import org.compiere.model.MStorageOnHand;
import org.compiere.model.MTable;
import org.compiere.model.MTaxCategory;
import org.compiere.model.MTransaction;
import org.compiere.model.MUOM;
import org.compiere.model.MUser;
import org.compiere.model.MUserRoles;
import org.compiere.model.MWarehouse;
import org.compiere.model.PO;
import org.compiere.model.Query;
import org.compiere.model.X_AD_Workflow;
import org.compiere.model.X_S_Resource;
import org.compiere.process.DocAction;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.KeyNamePair;
import org.compiere.util.Login;
import org.compiere.util.Trx;
import org.eevolution.model.I_PP_Product_BOM;
import org.eevolution.model.MPPProductBOM;
import org.eevolution.model.MPPProductBOMLine;
import org.eevolution.model.X_PP_Product_BOM;
import org.eevolution.model.X_PP_Product_BOMLine;
//import org.idempiere.adInterface.x10.ADLoginRequest;
import org.idempiere.adinterface.CompiereService;
import org.idempiere.webservices.AbstractService;
//import org.libero.model.MPPOrder;
//import org.libero.model.MPPOrderBOM;
//import org.libero.model.MPPOrderBOMLine;
//import org.libero.tables.X_PP_Order_BOMLine;
import org.pipra.model.custom.PiQrRelations;
import org.pipra.model.custom.PipraUtils;
import org.zkoss.json.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pipra.ve.model.AdRole_Custom;
import com.pipra.ve.model.COrder_Custom;
import com.pipra.ve.model.MInOutLineConfirm_Custom;
import com.pipra.ve.model.MInOutLine_Custom;
import com.pipra.ve.model.MInOut_Custom;
import com.pipra.ve.model.MLocatorType_Custom;
import com.pipra.ve.model.MPPOrderCustom;
import com.pipra.ve.model.MProduct_Custom;
import com.pipra.ve.model.MUser_Custom;
import com.pipra.ve.model.PiProductLabel;
import com.pipra.ve.model.PiUserToken;
import com.pipra.ve.model.X_PI_Deptartment;
import com.pipra.ve.model.X_pi_machineconf;
import com.pipra.ve.model.X_pi_orderreceipt;
import com.pipra.ve.model.X_pi_paorder;
import com.pipra.ve.model.X_pi_paorderpackingqty;
import com.pipra.ve.model.X_pi_paorderreceiveqty;
import com.pipra.ve.model.X_pi_return;
import com.pipra.ve.model.X_pi_returnline;
import com.pipra.ve.model.request.PurchaseOrderRequest.DTItem;
import com.pipra.ve.utils.VEClientConfig;
import com.pipra.ve.utils.VeUtils;
import com.pipra.ve.x10.*;

@WebService(endpointInterface = "com.pipra.ve.webservice.VEWebservice", serviceName = "VEWebservice", targetNamespace = "http://pipra.com/VE/1_0")
public class VEWebserviceImpl extends AbstractService implements VEWebservice {

	public static final String ROLE_TYPES_WEBSERVICE = "NULL,WS,SS";
	private static String webServiceName = new String("VEWebservice");

	private boolean manageTrx = true;

	private @Context HttpServletRequest httpServletRequest = null;

	private static CLogger s_log = CLogger.getCLogger(VEWebserviceImpl.class);

	public boolean isManageTrx() {
		return manageTrx;
	}

	public void setManageTrx(boolean manageTrx) {
		this.manageTrx = manageTrx;
	}

	@Override
	public LoginApiResponseDocument loginApi(LoginApiRequestDocument loginRequestDocument) {
		Trx trx = null;
		LoginApiResponseDocument loginApiResponseDocument = LoginApiResponseDocument.Factory.newInstance();
		LoginApiResponse loginApiResponse = loginApiResponseDocument.addNewLoginApiResponse();
		LoginApiRequest loginRequest = loginRequestDocument.getLoginApiRequest();
		try {

			getCompiereService().connect();
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			Login login = new Login(m_cs.getCtx());
			String serviceType = loginRequest.getServiceType();
			if (!serviceType.equalsIgnoreCase("openLoginApi")) {
				loginApiResponse.setIsError(true);
				loginApiResponse.setError("Service type " + serviceType + " not configured");
				loginApiResponseDocument.setLoginApiResponse(loginApiResponse);
				return loginApiResponseDocument;
			}

			KeyNamePair[] clients = login.getClients(loginRequest.getUser(), loginRequest.getPassword(),
					ROLE_TYPES_WEBSERVICE);
			if (clients == null) {
				loginApiResponse.setIsError(true);
				loginApiResponse.setError("Invalid User ID or Password");
				loginApiResponseDocument.setLoginApiResponse(loginApiResponse);
				return loginApiResponseDocument;
			}

			String userType = null;
			List<PO> poList = MInOut_Custom.getUserByName(loginRequest.getUser(), ctx, trxName);
			if (poList.size() != 0) {
				MUser mUser = new MUser(ctx, poList.get(0).get_ID(), trxName);
				userType = mUser.getC_Job().getName();
			}
			if (userType == null)
				loginApiResponse.setUserType("");
			else
				loginApiResponse.setUserType(userType);

			for (KeyNamePair client : clients) {

				Client clientName = loginApiResponse.addNewClient();
				KeyNamePair[] roles = login.getRoles(loginRequest.getUser(), client, ROLE_TYPES_WEBSERVICE);

				if (roles != null) {
					for (KeyNamePair role : roles) {
						Role roleName = clientName.addNewRoleList();
						roleName.setRoleId(role.getID());
						roleName.setRole(role.getName());

						KeyNamePair[] orgs = login.getOrgs(new KeyNamePair(role.getKey(), ""));
						if (orgs != null) {
							for (KeyNamePair org : orgs) {
								Organization orgName = roleName.addNewOrgList();
								orgName.setOrgId(org.getID());
								orgName.setOrg(org.getName());

								KeyNamePair[] warehouses = login.getWarehouses(new KeyNamePair(org.getKey(), ""));
								if (warehouses != null) {
									for (KeyNamePair warehouse : warehouses) {
										Warehouse wName = orgName.addNewWarehouse();
										wName.setWarehouseId(warehouse.getID());
										wName.setWarehouse(warehouse.getName());
									}
								}
							}
						}
					}
				}
				clientName.setClientId(client.getID());
				clientName.setClient(client.getName());
			}
			trx.commit();
		} finally {
			if (manageTrx && trx != null)
				trx.close();
			getCompiereService().disconnect();
		}
		return loginApiResponseDocument;
	}

	@Override
	public RoleConfigureResponseDocument roleConfig(RoleConfigureRequestDocument roleConfigureRequestDocument) {

		RoleConfigureResponseDocument roleConfigureResponseDocument = RoleConfigureResponseDocument.Factory
				.newInstance();
		RoleConfigureResponse roleResponse = roleConfigureResponseDocument.addNewRoleConfigureResponse();

		RoleConfigureRequest roleRequest = roleConfigureRequestDocument.getRoleConfigureRequest();
		ADLoginRequest loginReq = roleRequest.getADLoginRequest();
		String deviceToken = roleRequest.getDeviceToken();
		PreparedStatement pstm = null;
		ResultSet rs = null;
		Trx trx = null;
		try {
			getCompiereService().connect();
			CompiereService m_cs = getCompiereService();
			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			Properties ctx = m_cs.getCtx();
			String serviceType = roleRequest.getServiceType();

			org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = VeUtils.convertAdLogin(loginReq);
			String err = login(adLoginReq, webServiceName, "roleConfig", serviceType);
			if (err != null && err.length() > 0) {
				roleResponse.setError(err);
				roleResponse.setIsError(true);
				return roleConfigureResponseDocument;
			}

			if (!serviceType.equalsIgnoreCase("roleConfig")) {
				roleResponse.setIsError(true);
				roleResponse.setError("Service type " + serviceType + " not configured");
				return roleConfigureResponseDocument;
			}

			int roleId = loginReq.getRoleID();

			AdRole_Custom adRole_Custom = new AdRole_Custom(ctx, roleId, trxName, null);
			RoleAppAcess a = roleResponse.addNewAppAcess();
			a.setAppName("recieveApp");
			a.setAppAcess(adRole_Custom.isPurchaseOrder());

			RoleAppAcess b = roleResponse.addNewAppAcess();
			b.setAppName("materialReceipt");
			b.setAppAcess(adRole_Custom.isMaterialReceipt());

			RoleAppAcess c = roleResponse.addNewAppAcess();
			c.setAppName("stockCheckApp");
			c.setAppAcess(adRole_Custom.isPhysicalInventory());

			RoleAppAcess d = roleResponse.addNewAppAcess();
			d.setAppName("pickList");
			d.setAppAcess(adRole_Custom.isSaleOrder());

			RoleAppAcess e = roleResponse.addNewAppAcess();
			e.setAppName("dispatchApp");
			e.setAppAcess(adRole_Custom.isShipmentCustomer());

			RoleAppAcess f = roleResponse.addNewAppAcess();
			f.setAppName("addInward");
			f.setAppAcess(adRole_Custom.isAddInward());

			RoleAppAcess g = roleResponse.addNewAppAcess();
			g.setAppName("productionsupervisor");
			g.setAppAcess(adRole_Custom.isProductionsupervisor());

//			RoleAppAcess h = roleResponse.addNewAppAcess();
//			h.setAppName("productionSwitchSocketAgent");
//			h.setAppAcess(adRole_Custom.isProductionSwitchSocketAgent());

			RoleAppAcess i = roleResponse.addNewAppAcess();
			i.setAppName("mouldingSwitchSocketReceiptReceiving");
			i.setAppAcess(adRole_Custom.isMouldingSwitchSocketReceiptReceiving());

			RoleAppAcess j = roleResponse.addNewAppAcess();
			j.setAppName("assemblysupervisor");
			j.setAppAcess(adRole_Custom.isAssemblysupervisor());

//			RoleAppAcess k = roleResponse.addNewAppAcess();
//			k.setAppName("assemblySwitchandsocketAgent");
//			k.setAppAcess(adRole_Custom.isAssemblySwitchandsocketAgent());

			RoleAppAcess l = roleResponse.addNewAppAcess();
			l.setAppName("fgSwitchSocketAssemblyReceiving");
			l.setAppAcess(adRole_Custom.isFgSwitchSocketAssemblyReceiving());

			RoleAppAcess m = roleResponse.addNewAppAcess();
			m.setAppName("lightSupervisor");
			m.setAppAcess(adRole_Custom.isLightSupervisor());

//			RoleAppAcess n = roleResponse.addNewAppAcess();
//			n.setAppName("lightAgent");
//			n.setAppAcess(adRole_Custom.isLightAgent());

			RoleAppAcess o = roleResponse.addNewAppAcess();
			o.setAppName("automationSupervisor");
			o.setAppAcess(adRole_Custom.isAutomationSupervisor());

//			RoleAppAcess p = roleResponse.addNewAppAcess();
//			p.setAppName("automationAgent");
//			p.setAppAcess(adRole_Custom.isAutomationAgent());
						
			RoleAppAcess q = roleResponse.addNewAppAcess();
			q.setAppName("rmWireSupervisor");
			q.setAppAcess(adRole_Custom.isrmWireSupervisor());

			RoleAppAcess r = roleResponse.addNewAppAcess();
			r.setAppName("wireproduction");
			r.setAppAcess(adRole_Custom.isWireProduction());

//			RoleAppAcess s = roleResponse.addNewAppAcess();
//			s.setAppName("rmWireTwisting");
//			s.setAppAcess(adRole_Custom.isrmWireTwisting());
//			
//			
//			RoleAppAcess t = roleResponse.addNewAppAcess();
//			t.setAppName("rmWireInsulation");
//			t.setAppAcess(adRole_Custom.isRmWireInsulation());
//
//			RoleAppAcess u = roleResponse.addNewAppAcess();
//			u.setAppName("rmWireCoiling");
//			u.setAppAcess(adRole_Custom.isRmWireCoiling());

			RoleAppAcess v = roleResponse.addNewAppAcess();
			v.setAppName("fgWireSupervisor");
			v.setAppAcess(adRole_Custom.isFgWireSupervisor());
			
			RoleAppAcess w = roleResponse.addNewAppAcess();
			w.setAppName("brassAssembly");
			w.setAppAcess(adRole_Custom.isBrassAssembly());
			
			RoleAppAcess x = roleResponse.addNewAppAcess();
			x.setAppName("packagingAssembly");
			x.setAppAcess(adRole_Custom.isPackagingAssembly());
			
			RoleAppAcess y = roleResponse.addNewAppAcess();
			y.setAppName("mouldingAssembly");
			y.setAppAcess(adRole_Custom.isMouldingAssembly());
			
			RoleAppAcess z = roleResponse.addNewAppAcess();
			z.setAppName("packagingAgentAssembly");
			z.setAppAcess(adRole_Custom.isPackagingAgentAssembly());

			RoleAppAcess z1 = roleResponse.addNewAppAcess();
			z1.setAppName("rmswitch");
			z1.setAppAcess(adRole_Custom.isrmswitch());
			
			RoleAppAcess z2 = roleResponse.addNewAppAcess();
			z2.setAppName("ispickbyorder");
			z2.setAppAcess(adRole_Custom.ispickbyorder());
			
			RoleAppAcess z3 = roleResponse.addNewAppAcess();
			z3.setAppName("returnapp");
			z3.setAppAcess(adRole_Custom.isreturn());
			
			RoleAppAcess z4 = roleResponse.addNewAppAcess();
			z4.setAppName("courierapp");
			z4.setAppAcess(adRole_Custom.iscourierapp());
			
			RoleAppAcess aa = roleResponse.addNewAppAcess();
			aa.setAppName("labourPutaway");
			aa.setAppAcess(adRole_Custom.isLabourPutaway());
			
			RoleAppAcess ab = roleResponse.addNewAppAcess();
			ab.setAppName("labourPicklist");
			ab.setAppAcess(adRole_Custom.isLabourPicklist());
			
			RoleAppAcess ac = roleResponse.addNewAppAcess();
			ac.setAppName("labourInventorymove");
			ac.setAppAcess(adRole_Custom.isLabourInventorymove());
			
			RoleAppAcess ad = roleResponse.addNewAppAcess();
	        ad.setAppName("finaldispatchapp");
	        ad.setAppAcess(adRole_Custom.isfinaldispatchapp());
	         
	        RoleAppAcess ae = roleResponse.addNewAppAcess();
	        ae.setAppName("supervisorPutaway");
	        ae.setAppAcess(adRole_Custom.issupervisorputawayapp());
	        
	        RoleAppAcess af = roleResponse.addNewAppAcess();
	        af.setAppName("qcCheckApp");
	        af.setAppAcess(adRole_Custom.isQcCheckApp());
	        
	        RoleAppAcess ag = roleResponse.addNewAppAcess();
	        ag.setAppName("rePackingApp");
	        ag.setAppAcess(adRole_Custom.rePackingApp());
	        
	        RoleAppAcess ah = roleResponse.addNewAppAcess();
	        ah.setAppName("poReceivingApp");
	        ah.setAppAcess(adRole_Custom.isPoReceivingApp());

			PiUserToken.deleteTokensForUserAndRole(Env.getAD_User_ID(ctx), roleId, ctx, trxName);
			if (deviceToken != null) {
				PiUserToken piUserToken = new PiUserToken(ctx, 0, trxName);
				piUserToken.setAD_User_ID(Env.getAD_User_ID(ctx));
				piUserToken.setAD_Role_ID(roleId);
				piUserToken.setdevicetoken(deviceToken);
				piUserToken.saveEx();
			}

			trx.commit();

		} catch (Exception e) {
			roleResponse.setError(e.getMessage());
			roleResponse.setIsError(true);
			return roleConfigureResponseDocument;
		} finally {
			closeDbCon(pstm, rs);
			getCompiereService().disconnect();
			trx.close();
		}
		return roleConfigureResponseDocument;
	}

	@Override
	public POListResponseDocument poList(POListRequestDocument pOListRequestDocument) {
		Trx trx = null;
		List<POListAccess> poLists = new ArrayList<>();
		POListResponseDocument pOListResponseDocument = POListResponseDocument.Factory.newInstance();
		POListResponse listResponse = pOListResponseDocument.addNewPOListResponse();

		POListRequest pOListRequest = pOListRequestDocument.getPOListRequest();
		ADLoginRequest loginReq = pOListRequest.getADLoginRequest();
		String serviceType = pOListRequest.getServiceType();
		String searchKey = pOListRequest.getSearchKey();
		PreparedStatement pstm = null;
		ResultSet rs = null;

		// Get pagination details from request
		int pageSize = pOListRequest.getPageSize(); // Number of records per page
		int pageNumber = pOListRequest.getPageNumber(); // Current page number

		try {
			getCompiereService().connect();
			CompiereService m_cs = getCompiereService();
			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			Properties ctx = m_cs.getCtx();

			int client_id = loginReq.getClientID();
			int roleId = loginReq.getRoleID();
			org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = VeUtils.convertAdLogin(loginReq);
			String err = login(adLoginReq, webServiceName, "poList", serviceType);
			if (err != null && err.length() > 0) {
				listResponse.setError(err);
				listResponse.setIsError(true);
				return pOListResponseDocument;
			}

			if (!serviceType.equalsIgnoreCase("poli")) {
				listResponse.setIsError(true);
				listResponse.setError("Service type " + serviceType + " not configured");
				return pOListResponseDocument;
			}

			int userId = Env.getAD_User_ID(ctx);
			MUser_Custom user = new MUser_Custom(ctx, userId, trxName);
			X_PI_Deptartment dept = new X_PI_Deptartment(ctx, user.getPI_DEPARTMENT_ID(), trxName);

			List<Integer> orgList = new ArrayList<>();
			Login login = new Login(m_cs.getCtx());
			KeyNamePair[] orgs = login.getOrgs(new KeyNamePair(roleId, ""));
			if (orgs != null) {
				for (KeyNamePair org : orgs) {
					orgList.add(Integer.valueOf(org.getID()));
				}
			}
			String orgIds = orgList.stream().map(Object::toString).collect(Collectors.joining(", "));

			StringBuilder query = new StringBuilder("SELECT\n" + "    DISTINCT(po.documentno) AS purchase_order,\n"
					+ "    po.c_order_id AS cOrderId,\n"
					+ "    bp.name AS Supplier, po.created, po.pi_deptartment_ID,\n"
					+ "    TO_CHAR(po.dateordered, 'DD/MM/YYYY') AS Order_Date,\n" + "    wh.name AS Warehouse_Name,\n"
					+ "    po.description,\n" + "    po.ad_org_id,\n" + "    CASE\n"
					+ "        WHEN po.docstatus = 'CO' AND mr.m_inout_id IS NULL THEN false\n"
					+ "        WHEN po.docstatus = 'CO' AND mr.m_inout_id IS NOT NULL THEN true \n"
					+ "    END AS status\n" + "FROM c_order po\n"
					+ "JOIN c_orderline pol ON po.c_order_id = pol.c_order_id\n"
					+ "LEFT JOIN m_inout mr ON po.c_order_id = mr.c_order_id\n"
					+ "JOIN c_bpartner bp ON po.c_bpartner_id = bp.c_bpartner_id \n"
					+ "JOIN m_warehouse wh ON po.m_warehouse_id = wh.m_warehouse_id\n" + "WHERE pol.qtyordered > (\n"
					+ "        SELECT COALESCE(SUM(iol.qtyentered), 0)\n" + "        FROM m_inoutline iol\n"
					+ "        WHERE iol.c_orderline_id = pol.c_orderline_id\n" + "    ) and po.ad_client_id = "
					+ client_id + " and po.docstatus = 'CO' and po.issotrx = 'N' " + "AND (\n"
					+ "    po.documentno ILIKE '%' || COALESCE(?, po.documentno) || '%'\n"
					+ "    OR bp.name ILIKE '%' || COALESCE(?, bp.name) || '%'\n"
					+ "    OR wh.name ILIKE '%' || COALESCE(?, wh.name) || '%'\n"
					+ "    OR po.description ILIKE '%' || COALESCE(?, po.description) || '%'\n" + ")"
					+ "and po.ad_org_id IN (" + orgIds + ") ORDER BY po.created desc \n" + "");

			// Check if pageSize or pageNumber are not set (default to return all records)
			if (pageSize <= 0 || pageNumber <= 0) {
				
			} else {
			    int offset = (pageNumber - 1) * pageSize;
//			    query.append(" LIMIT "+pageSize+" OFFSET "+offset+"");
			    query.append(" LIMIT ").append(pageSize).append(" OFFSET ").append(offset);
			}

			pstm = DB.prepareStatement(query.toString(), null);
			pstm.setString(1, searchKey);
			pstm.setString(2, searchKey);
			pstm.setString(3, searchKey);
			pstm.setString(4, searchKey);
			rs = pstm.executeQuery();

			while (rs.next()) {
				String documentNo = rs.getString("Purchase_Order");
				String cOrderId = rs.getString("cOrderId");
				String supplier = rs.getString("Supplier");
				String date = rs.getString("Order_Date");
				String warehouseName = rs.getString("Warehouse_Name");
				String description = rs.getString("description");
				boolean Status = rs.getBoolean("status");
				String deptId = rs.getString("pi_deptartment_ID");

				if (deptId == null || (deptId != null && dept.getPI_Deptartment_ID() == Integer.valueOf(deptId)))
					poLists.add(createPOList(documentNo, cOrderId, supplier, date, warehouseName, description, Status));
			}
			POListAccess[] polistArray = poLists.toArray(new POListAccess[poLists.size()]);
			listResponse.setListAccessArray(polistArray);

		} catch (Exception e) {
			listResponse.setError(e.getMessage());
			return pOListResponseDocument;

		} finally {
			closeDbCon(pstm, rs);
			if (manageTrx && trx != null) {
				trx.close();
				getCompiereService().disconnect();
			}
		}
		pOListResponseDocument.setPOListResponse(listResponse);
		return pOListResponseDocument;
	}

	private POListAccess createPOList(String docNo, String cOrderId, String supplier, String date, String warehouseName,
			String description, boolean status) {
		POListAccess poListAccess = POListAccess.Factory.newInstance();
		poListAccess.setDocumentNumber(docNo);
		poListAccess.setCOrderId(cOrderId);
		poListAccess.setSupplierName(supplier);
		poListAccess.setOrderDate(date);
		poListAccess.setWarehouseName(warehouseName);
		if (description == null)
			poListAccess.setDescription("");
		else
			poListAccess.setDescription(description);
		poListAccess.setOrderStatus(status);
		return poListAccess;
	}

	@Override
	public PODataResponseDocument poData(PODataRequestDocument pODataRequestDocument) {
		PODataResponseDocument pODataResponseDocument = PODataResponseDocument.Factory.newInstance();
		PODataResponse pODataResponse = pODataResponseDocument.addNewPODataResponse();
		PODataRequest pODataRequest = pODataRequestDocument.getPODataRequest();
		ADLoginRequest loginReq = pODataRequest.getADLoginRequest();
		String serviceType = pODataRequest.getServiceType();
		String documentNo = pODataRequest.getDocumentNo();
		int client_ID = loginReq.getClientID();
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			getCompiereService().connect();
			org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = VeUtils.convertAdLogin(loginReq);
			String err = login(adLoginReq, webServiceName, "poData", serviceType);
			if (err != null && err.length() > 0) {
				pODataResponse.setError(err);
				pODataResponse.setIsError(true);
				return pODataResponseDocument;
			}
			if (!serviceType.equalsIgnoreCase("poData")) {
				pODataResponse.setIsError(true);
				pODataResponse.setError("Service type " + serviceType + " not configured");
				return pODataResponseDocument;
			}
			String query = null;
			query = "SELECT a.qtyentered as totalQnty, (a.qtyentered - COALESCE(SUM(c.qtyentered), 0)) AS outstanding_qty, e.m_product_id as productId, a.c_order_id, a.c_uom_id, a.c_orderline_id, e.name AS product_name\n"
					+ "FROM c_orderline a \n" + "JOIN c_order d ON d.c_order_id = a.c_order_id \n"
					+ "LEFT JOIN m_inout b ON a.c_order_id = b.c_order_id \n"
					+ "LEFT JOIN m_inoutline c ON c.m_inout_id = b.m_inout_id AND c.c_orderline_id = a.c_orderline_id\n"
					+ "JOIN m_product e ON e.m_product_id = a.m_product_id \n" + "WHERE d.documentno = '" + documentNo
					+ "' AND d.ad_client_id = '" + client_ID + "' AND a.c_order_id = (\n"
					+ "  SELECT c_order_id FROM c_order WHERE documentno = '" + documentNo + "' AND ad_client_id = '"
					+ client_ID + "'\n" + ")\n"
					+ "GROUP BY e.m_product_id, e.name, a.qtyordered, a.c_orderline_id, a.c_uom_id, a.c_order_id ORDER BY a.c_orderline_id;\n"
					+ "";

			pstm = DB.prepareStatement(query.toString(), null);
			rs = pstm.executeQuery();

			int qnty = 0;
			int cOrderId = 0;
			while (rs.next()) {
				int outstandingQty = rs.getInt("outstanding_qty");
				if(outstandingQty <= 0)
					continue;
				int totalQnty = rs.getInt("totalQnty");
				String productName = rs.getString("product_name");
				int productId = rs.getInt("productId");
				cOrderId = rs.getInt("c_order_id");
				int cOrderlineId = rs.getInt("c_orderline_id");
				int uomId = rs.getInt("c_uom_id");
				ProductData productData = pODataResponse.addNewProductData();
				productData.setProductId(productId);
				productData.setProductName(productName);
				productData.setCOrderlineId(cOrderlineId);
				productData.setUomId(uomId);
				productData.setOutstandingQnty(outstandingQty);
				productData.setTotalQuantity(totalQnty);
				
				MProduct_Custom product = new MProduct_Custom(Env.getCtx(), productId, null);
				productData.setScanLabel(product.isScanLabel());
				productData.setProductCount(product.getProductCount());
				qnty += Integer.valueOf(outstandingQty);
			}
			pODataResponse.setCOrderId(cOrderId);
			pODataResponse.setOverallQnty(qnty);
			pstm.close();
			rs.close();

			query = "SELECT\n" + "    TO_CHAR(po.dateordered, 'DD/MM/YYYY') AS Order_Date, po.pi_deptartment_ID, \n"
					+ "    bp.name AS Supplier,\n" + "    wh.name AS Warehouse_Name,\n"
					+ "    po.description, po.m_warehouse_id,\n" + "    po.docstatus,\n" + "    ml.m_locator_id,\n"
					+ "	CASE\n" + "   	 WHEN po.docstatus = 'CO' AND mr.m_inout_id IS NULL THEN false\n"
					+ "   	 WHEN po.docstatus = 'CO' AND mr.m_inout_id IS NOT NULL THEN true \n" + "  	END AS status\n"
					+ "FROM c_order po\n" + "JOIN c_bpartner bp ON po.c_bpartner_id = bp.c_bpartner_id\n"
					+ "LEFT JOIN m_inout mr ON po.c_order_id = mr.c_order_id\n"
					+ "JOIN m_warehouse wh ON po.m_warehouse_id = wh.m_warehouse_id\n"
					+ "JOIN m_locator ml ON ml.m_warehouse_id = wh.m_warehouse_id\n" + "WHERE\n"
					+ "    po.documentno = '" + documentNo + "'\n" + "    AND isDefault = 'Y' AND po.ad_client_id = "
					+ client_ID + ";";
			pstm = DB.prepareStatement(query.toString(), null);
			rs = pstm.executeQuery();

			while (rs.next()) {
				String orderDate = rs.getString("Order_Date");
				String supplier = rs.getString("Supplier");
				String docStatus = rs.getString("docstatus");
				String warehouseName = rs.getString("Warehouse_Name");
				String description = rs.getString("description");
				boolean orderStatus = rs.getBoolean("status");
//				int mLocatorId = rs.getInt("m_locator_id");
				int mwarehouseId = rs.getInt("m_warehouse_id");
				int departmentId = rs.getInt("pi_deptartment_ID");

				List<MLocator> poList = VeUtils.getLocatorsByDepartment(null, null, mwarehouseId, departmentId,
						"receiving", "Y");
				if (poList == null || poList.size() == 0) {
//					pODataResponseDocument.setNil();
					pODataResponse = pODataResponseDocument.addNewPODataResponse();
					pODataResponse.setError("No Locators found for department");
					pODataResponse.setIsError(true);
					return pODataResponseDocument;
				}

				pODataResponse.setOrderDate(orderDate);
				pODataResponse.setDocstatus(docStatus);
				pODataResponse.setSupplier(supplier);
				pODataResponse.setWarehouseName(warehouseName);
				if (description == null)
					pODataResponse.setDescription("");
				else
					pODataResponse.setDescription(description);
				pODataResponse.setDefaultLocatorId(poList.get(0).get_ID());
				pODataResponse.setOrderStatus(orderStatus);
			}
			pODataResponse.setDocumentNo(documentNo);
		} catch (Exception e) {
			pODataResponse.setError(e.getMessage());
			pODataResponse.setIsError(true);
			return pODataResponseDocument;

		} finally {
			closeDbCon(pstm, rs);
			getCompiereService().disconnect();
		}
		return pODataResponseDocument;
	}

	@Override
	public CreateMRResponseDocument createMR(CreateMRRequestDocument createMRRequestDocument) {
		CreateMRResponseDocument createMRResponseDocument = CreateMRResponseDocument.Factory.newInstance();
		CreateMRResponse createMRResponse = createMRResponseDocument.addNewCreateMRResponse();
		CreateMRRequest createMRRequest = createMRRequestDocument.getCreateMRRequest();
		ADLoginRequest loginReq = createMRRequest.getADLoginRequest();
		String serviceType = createMRRequest.getServiceType();
		int cOrderId = createMRRequest.getCOrderId();
		MInvoice m_invoice = null;
		MRMA m_rma = null;
		Trx trx = null;
		int clientID = loginReq.getClientID();
		CompiereService m_cs = getCompiereService();
		Properties ctx = m_cs.getCtx();
		int mInoutId = 0;
		try {
			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();

			org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = VeUtils.convertAdLogin(loginReq);
			String err = login(adLoginReq, webServiceName, "createMR", serviceType);
			if (err != null && err.length() > 0) {
				createMRResponse.setError(err);
				createMRResponse.setIsError(true);
				return createMRResponseDocument;
			}
			if (!serviceType.equalsIgnoreCase("createMR")) {
				createMRResponse.setIsError(true);
				createMRResponse.setError("Service type " + serviceType + " not configured");
				return createMRResponseDocument;
			}

			MTable table = MTable.get(ctx, "c_order");
			PO po = table.getPO(cOrderId, trx.getTrxName());
			if (po == null) {
				createMRResponse.setError("order not found for " + cOrderId + "");
				createMRResponse.setIsError(true);
				return createMRResponseDocument;
			}
			MOrder order = (MOrder) po;
			
			LinkedHashMap<Integer, Integer>  qntyList = new LinkedHashMap<Integer, Integer>();
			for(MOrderLine line : order.getLines()) {
				
				int qnty = line.getQtyEntered().intValue();
				if(qntyList.containsKey(line.getM_Product_ID()))
					qnty += qntyList.get(line.getM_Product_ID());
				
				qntyList.put(line.getM_Product_ID(), qnty);
			}
			
			MInOut[] mInoutArray = order.getShipments();
			for (MInOut inout : mInoutArray) {
				MInOutLine[] linesArray = inout.getLines();
				for (MInOutLine line : linesArray) {
					if (qntyList.containsKey(line.getM_Product_ID())) {
						int qntyEntered = line.getQtyEntered().intValue();
						int productQnty = qntyList.get(line.getM_Product_ID());
						qntyList.put(line.getM_Product_ID(), productQnty - qntyEntered);
					}
				}
			}

			MRLines[] MRLinesArray = createMRRequest.getMRLinesArray();
			for (MRLines data : MRLinesArray) {

				int qntyEntered = data.getQnty();
				if (qntyList.containsKey(data.getProductId())) {
					int productQnty = qntyList.get(data.getProductId());
					qntyList.put(data.getProductId(), productQnty - qntyEntered);
				}
			}

			for (Integer key : qntyList.keySet()) {
				if (qntyList.get(key) < 0) {
					createMRResponse.setError("Material Recipt Qnty cannot be more that Purchase Order Qnty");
					createMRResponse.setIsError(true);
					return createMRResponseDocument;
				}
			}
			
			
			
			MTable docTypeTable = MTable.get(ctx, "c_doctype");
			PO docTypePO = docTypeTable.getPO("name = 'MM Receipt' and ad_client_id = " + clientID + "",
					trx.getTrxName());
			MDocType mDocType = (MDocType) docTypePO;

			MInOut receipt = new MInOut(order, mDocType.get_ID(), order.getDateOrdered());
			receipt.setDocStatus(DocAction.STATUS_Drafted);
			receipt.saveEx();

			mInoutId = receipt.getM_InOut_ID();
			MInOut inout = new MInOut(ctx, mInoutId, trx.getTrxName());
			for (MRLines data : MRLinesArray) {
				int C_InvoiceLine_ID = 0;
				int M_RMALine_ID = 0;
				int M_Product_ID = data.getProductId();
				int C_UOM_ID = data.getUomId();
				int C_OrderLine_ID = data.getCOrderlineId();
				BigDecimal QtyEntered = BigDecimal.valueOf(data.getQnty());
				int M_Locator_ID = data.getLocator();

				inout.createLineFrom(C_OrderLine_ID, C_InvoiceLine_ID, M_RMALine_ID, M_Product_ID, C_UOM_ID, QtyEntered,
						M_Locator_ID);
				MInOutLine lineArray[] = MInOutLine.get(ctx, C_OrderLine_ID, trxName);
				MInOutLine line = lineArray[lineArray.length - 1];
				data.setMrLineId(line.get_ID());
			}
			inout.updateFrom(order, m_invoice, m_rma);
			trx.commit();
			createMRResponse.setMRLinesArray(MRLinesArray);
			createMRResponse.setIsError(false);
			createMRResponse.setMrDocumentNumber(inout.getDocumentNo());
			createMRResponse.setMrId(inout.get_ID());

			Map<String, String> data = new HashMap<>();
			data.put("recordId", String.valueOf(inout.getM_InOut_ID()));
			data.put("documentNo", String.valueOf(inout.getDocumentNo()));
			data.put("path1", "/put_away_screen");
			data.put("path2", "/put_away_detail_screen");

			COrder_Custom orderCustom = new COrder_Custom(ctx, cOrderId, trxName);

			
			VeUtils.sendNotificationAsync("materialReceipt", inout.get_Table_ID(), inout.getM_InOut_ID(), ctx, trxName,
					"New Inward: " + inout.getDocumentNo() + "",
					" Inward - " + inout.getDocumentNo() + " added to process", inout.get_TableName(), data,
					loginReq.getClientID(), "MaterialReciptCreated", orderCustom.getpidepartmentID());

		} catch (Exception e) {
			MTable table = MTable.get(ctx, "m_inout");
			PO po = table.getPO(mInoutId, trx.getTrxName());
			po.delete(true);
			createMRResponse.setError(e.getMessage());
			createMRResponse.setIsError(true);
			return createMRResponseDocument;
		} finally {
			getCompiereService().disconnect();
			trx.close();
		}

		return createMRResponseDocument;
	}

	@Override
	public MRListResponseDocument getMrList(MRListRequestDocument pOListRequestDocument) {

		MRListResponseDocument mRListResponseDocument = MRListResponseDocument.Factory.newInstance();
		MRListResponse listResponse = mRListResponseDocument.addNewMRListResponse();
		MRListRequest mRListRequest = pOListRequestDocument.getMRListRequest();
		ADLoginRequest loginReq = mRListRequest.getADLoginRequest();
		String serviceType = mRListRequest.getServiceType();
		String searchKey = mRListRequest.getSearchKey();
		String isSalesTransaction = mRListRequest.getIsSalesTransaction();
		String status = mRListRequest.getStatus();
		
		PreparedStatement pstm = null;
		ResultSet rs = null;
		Trx trx = null;

		// Get pagination details from request
		int pageSize = mRListRequest.getPageSize(); // Number of records per page
		int pageNumber = mRListRequest.getPageNumber(); // Current page number
		try {
			CompiereService m_cs = getCompiereService();

			Properties ctx = m_cs.getCtx();

			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();

			int clientId = loginReq.getClientID();
			int roleId = loginReq.getRoleID();
			getCompiereService().connect();
			org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = VeUtils.convertAdLogin(loginReq);
			String err = login(adLoginReq, webServiceName, "mrList", serviceType);
			if (err != null && err.length() > 0) {
				listResponse.setError(err);
				listResponse.setIsError(true);
				return mRListResponseDocument;
			}

			if (!serviceType.equalsIgnoreCase("mrList")) {
				listResponse.setIsError(true);
				listResponse.setError("Service type " + serviceType + " not configured");
				return mRListResponseDocument;
			}

			if (status != null && status.isEmpty())
				status = null;

			int userId = Env.getAD_User_ID(ctx);
			MUser_Custom user = new MUser_Custom(ctx, userId, trxName);
			X_PI_Deptartment dept = new X_PI_Deptartment(ctx, user.getPI_DEPARTMENT_ID(), trxName);

			List<Integer> orgList = new ArrayList<>();
			Login login = new Login(m_cs.getCtx());
			KeyNamePair[] orgs = login.getOrgs(new KeyNamePair(roleId, ""));
			if (orgs != null) {
				for (KeyNamePair org : orgs) {
					orgList.add(Integer.valueOf(org.getID()));
				}
			}
			
			String isCourier = "N";
			if(mRListRequest.getIsCourier())
				isCourier = "Y";
				
			String orgIds = orgList.stream().map(Object::toString).collect(Collectors.joining(", "));

			StringBuilder query = new StringBuilder("SELECT\n" + "    DISTINCT(po.documentno) AS documentNo,\n"
					+ "    bp.name AS Supplier, co.pi_deptartment_ID,\n" + "    po.m_inout_id AS mInoutID,\n"
					+ "    TO_CHAR(po.dateordered, 'DD/MM/YYYY') AS Order_Date,\n" + "    wh.name AS Warehouse_Name,\n"
					+ "    po.description, po.m_inout_id,co.iscourier AS iscourier,\n"
					+ "po.pickStatus,	co.documentno as orderDocumentno, po.created \n" + "FROM m_inout po\n"
					+ "JOIN c_bpartner bp ON po.c_bpartner_id = bp.c_bpartner_id \n"
					+ "JOIN c_order co on co.c_order_id = po.c_order_id\n"
					+ "JOIN m_warehouse wh ON po.m_warehouse_id = wh.m_warehouse_id\n" + "WHERE po.ad_client_id = "
					+ clientId + " AND po.issotrx = '" + isSalesTransaction
					+ "' AND po.docstatus = 'DR' AND co.iscourier = '"+isCourier+"' AND po.ad_org_id IN (" + orgIds + ") AND po.ad_orgtrx_id is null "
					+ "AND (\n" + "    po.documentno ILIKE '%' || COALESCE(?, po.documentno) || '%'\n"
					+ "    OR bp.name ILIKE '%' || COALESCE(?, bp.name) || '%'\n"
					+ "    OR wh.name ILIKE '%' || COALESCE(?, wh.name) || '%'\n"
					+ "    OR po.description ILIKE '%' || COALESCE(?, po.description) || '%'\n"
					+ "    OR co.documentno ILIKE '%' || COALESCE(?, co.documentno) || '%'\n" + ")\n"
					+ " ORDER BY po.created desc");

			// Check if pageSize or pageNumber are not set (default to return all records)
			if (pageSize <= 0 || pageNumber <= 0) {
				
			} else {
				int offset = (pageNumber - 1) * pageSize;
				query.append(" LIMIT " + pageSize + " OFFSET " + offset + "");
			}

			pstm = DB.prepareStatement(query.toString(), null);
			pstm.setString(1, searchKey);
			pstm.setString(2, searchKey);
			pstm.setString(3, searchKey);
			pstm.setString(4, searchKey);
			pstm.setString(5, searchKey);
			rs = pstm.executeQuery();

			int count = 0;
			while (rs.next()) {
				String documentNo = rs.getString("documentNo");
				String mInoutID = rs.getString("mInoutID");
				String supplier = rs.getString("Supplier");
				String date = rs.getString("Order_Date");
				String orderDocumentno = rs.getString("orderDocumentno");
				String warehouseName = rs.getString("Warehouse_Name");
				String description = rs.getString("description");
				String pickStatus = rs.getString("pickStatus");
				String deptId = rs.getString("pi_deptartment_ID");
				Boolean isCourierOrder = rs.getBoolean("iscourier");

				if ((deptId == null || (deptId != null && dept.getPI_Deptartment_ID() == Integer.valueOf(deptId)))
						|| (dept.isdispatch() && pickStatus != null && pickStatus.equals("FINAL-DISPATCH"))) {

					boolean isPickStatusValid = pickStatus != null && pickStatus.equalsIgnoreCase("QC");
					boolean isStatusValid = (status == null && pickStatus == null)
							|| (status != null && status.equals(pickStatus));

					if (!isPickStatusValid && isStatusValid) {
						MRList mRList = listResponse.addNewMRList();
						mRList.setDocumentNo(documentNo);
						mRList.setMInoutID(mInoutID);
						mRList.setSupplier(supplier);
						mRList.setOrderDate(date);
						mRList.setWarehouseName(warehouseName);
						mRList.setOrderDocumentno(orderDocumentno);
						mRList.setIsCourier(isCourierOrder);
						if (description == null)
							mRList.setDescription("");
						else
							mRList.setDescription(description);
						count++;
					}
				}
			}
			listResponse.setCount(count);
		} catch (SQLException e) {
			listResponse.setIsError(true);
			listResponse.setError(e.getMessage());
			return mRListResponseDocument;
		} finally {
			closeDbCon(pstm, rs);
			getCompiereService().disconnect();
			trx.close();
		}
		return mRListResponseDocument;
	}

	@Override
	public MRDataResponseDocument getMrData(PODataRequestDocument pODataRequestDocument) {

		MRDataResponseDocument mRDataResponseDocument = MRDataResponseDocument.Factory.newInstance();
		MRDataResponse listResponse = mRDataResponseDocument.addNewMRDataResponse();
		PODataRequest pODataRequest = pODataRequestDocument.getPODataRequest();
		ADLoginRequest loginReq = pODataRequest.getADLoginRequest();
		String serviceType = pODataRequest.getServiceType();
		String documentNo = pODataRequest.getDocumentNo();
		int clientId = loginReq.getClientID();
		
		org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = VeUtils.convertAdLogin(loginReq);
		String err = login(adLoginReq, webServiceName, "mrData", serviceType);
		if (err != null && err.length() > 0) {
			listResponse.setError(err);
			listResponse.setIsError(true);
			return mRDataResponseDocument;
		}

		if (!serviceType.equalsIgnoreCase("mrData")) {
			listResponse.setIsError(true);
			listResponse.setError("Service type " + serviceType + " not configured");
			return mRDataResponseDocument;
		}
		listResponse = getMInoutDataByDocumentNumber(listResponse, documentNo, clientId);
		getCompiereService().disconnect();

		return mRDataResponseDocument;
	}

	private MRDataResponse getMInoutDataByDocumentNumber(MRDataResponse listResponse, String documentNo, int clientId) {
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			String query = "SELECT bp.name AS Supplier,mi.c_order_id,mi.docstatus,\n"
					+ "(SELECT bpl.name FROM adempiere.c_bpartner_location bpl WHERE bpl.c_bpartner_id = bp.c_bpartner_id \n"
					+ "ORDER BY bpl.c_bpartner_location_id LIMIT 1) AS bpLocation,co.iscourier AS iscourier,TO_CHAR(mi.dateordered, 'DD/MM/YYYY') AS Order_Date,\n"
					+ "ml.movementqty,wh.name AS Warehouse_Name,co.pi_deptartment_ID,mi.description,ml.m_inoutline_id,\n"
					+ "ml.m_locator_id,mloctr.value AS locatorName,ml.M_inout_id,co.documentno AS orderDocumentno,mp.name,\n"
					+ "mp.m_product_id,ml.c_uom_id,ml.c_orderline_id,ml.qcfailedqty AS failedQty,COALESCE(ml.dispatcedQty, 0) AS dispatcedQty \n"
					+ " FROM adempiere.m_inoutline ml\n"
					+ "JOIN adempiere.m_inout mi ON mi.m_inout_id = ml.m_inout_id\n"
					+ "JOIN adempiere.c_bpartner bp ON mi.c_bpartner_id = bp.c_bpartner_id\n"
					+ "JOIN adempiere.m_warehouse wh ON mi.m_warehouse_id = wh.m_warehouse_id\n"
					+ "JOIN adempiere.m_product mp ON mp.m_product_id = ml.m_product_id\n"
					+ "JOIN adempiere.m_locator mloctr ON mloctr.m_locator_id = ml.m_locator_id\n"
					+ "JOIN adempiere.c_order co ON mi.c_order_id = co.c_order_id\n"
					+ "WHERE mi.documentNo = '"+documentNo+"' AND mi.ad_client_id = "+clientId+" ORDER BY ml.m_inoutline_id;";

			pstm = DB.prepareStatement(query.toString(), null);
			rs = pstm.executeQuery();

			int count = 0;
			String supplier = null;
			String bpLocation = null;
			String description = null;
			String warehouseName = null;
			String docStatus = null;
			String orderDocumentno = null;
			String date = null;
			boolean isCourier = false;
			int lineCount = 0;
			int mInoutId = 0;
			int cOrderId = 0;
			while (rs.next()) {
				supplier = rs.getString("Supplier");
				bpLocation = rs.getString("bpLocation");
				docStatus = rs.getString("docStatus");
				date = rs.getString("Order_Date");
				warehouseName = rs.getString("Warehouse_Name");
				description = rs.getString("description");
				int receivedQnty = rs.getInt("movementqty");
				int mProductID = rs.getInt("m_product_id");
				int uomID = rs.getInt("c_uom_id");
				int cOrderlineID = rs.getInt("c_orderline_id");
				int mInoutlineId = rs.getInt("m_inoutline_id");
				mInoutId = rs.getInt("m_inout_id");
				String productName = rs.getString("name");
				int locatorId = rs.getInt("m_locator_id");
				String locatorName = rs.getString("locatorName");
				int failedQty = rs.getInt("failedQty");
				int actualQty = receivedQnty - failedQty;
				orderDocumentno = rs.getString("orderDocumentno");
				cOrderId = rs.getInt("c_order_id");
				isCourier = rs.getBoolean("iscourier");
				int dispatcedQty = rs.getInt("dispatcedQty");
				
				if (actualQty > 0) {
					MRLine mRLine = listResponse.addNewMRLines();
					mRLine.setProductName(productName);
					mRLine.setProductID(mProductID);
					mRLine.setMRLineId(mInoutlineId);
					mRLine.setCOrderlineId(cOrderlineID);
					mRLine.setUomID(uomID);
					mRLine.setRecievedQnty(actualQty);
					mRLine.setLocatorId(locatorId);
					mRLine.setLocatorName(locatorName);
					mRLine.setDispatchedQty(dispatcedQty);
					count += actualQty;
					lineCount++;
				}
			}
			listResponse.setDocumentNo(documentNo);
			listResponse.setCOrderId(cOrderId);
			listResponse.setMInoutID(mInoutId);
			listResponse.setOrderDocumentno(orderDocumentno);
			listResponse.setDocStatus(docStatus);
			listResponse.setSupplier(supplier);
			listResponse.setSupplierLocationName(bpLocation);
			listResponse.setOrderDate(date);
			listResponse.setWarehouseName(warehouseName);
			listResponse.setIsCourier(isCourier);
			if (description == null)
				listResponse.setDescription("");
			else
				listResponse.setDescription(description);
			listResponse.setOverallQnty(count);
			
			MInOut_Custom inOut_Custom = new MInOut_Custom(null, mInoutId,null);
			listResponse.setAreaMarking(inOut_Custom.getMarking() != null && inOut_Custom.getMarking() != "" ? inOut_Custom.getMarking() : "");

			if (lineCount == 0) {
				MRDataResponseDocument mRDataResponse = MRDataResponseDocument.Factory.newInstance();
				listResponse = mRDataResponse.addNewMRDataResponse();
				listResponse.setIsError(true);
				listResponse.setError("MR with : " + documentNo + " have no lines");
				return listResponse;
			}

		} catch (SQLException e) {
			listResponse.setIsError(true);
			listResponse.setError(e.getMessage());
			return listResponse;
		} finally {
			closeDbCon(pstm, rs);
		}
		return listResponse;
	}

	@Override
	public MRFailedResponceDocument createFailedQty(MRFailedRequestDocument mRFailedRequestDocument) {
		MRFailedResponceDocument mRFailedResponceDocument = MRFailedResponceDocument.Factory.newInstance();
		MRFailedResponce mRFailedResponce = mRFailedResponceDocument.addNewMRFailedResponce();
		MRFailedRequest mRFailedRequest = mRFailedRequestDocument.getMRFailedRequest();
		ADLoginRequest loginReq = mRFailedRequest.getADLoginRequest();
		String serviceType = mRFailedRequest.getServiceType();
		int mInOutID = mRFailedRequest.getMInOutID();
		Trx trx = null;
		try {
			CompiereService m_cs = getCompiereService();
			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();

			org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = VeUtils.convertAdLogin(loginReq);
			String err = login(adLoginReq, webServiceName, "mrFailed", serviceType);
			if (err != null && err.length() > 0) {
				mRFailedResponce.setError(err);
				mRFailedResponce.setIsError(true);
				return mRFailedResponceDocument;
			}

			if (!serviceType.equalsIgnoreCase("mrFailed")) {
				mRFailedResponce.setIsError(true);
				mRFailedResponce.setError("Service type " + serviceType + " not configured");
				return mRFailedResponceDocument;
			}

			Properties ctx = m_cs.getCtx();
			MTable table = MTable.get(ctx, "m_inout");
			PO po = table.getPO(mInOutID, trx.getTrxName());
			if (po == null) {
				mRFailedResponce.setError("order not found for " + mInOutID + "");
				mRFailedResponce.setIsError(true);
				return mRFailedResponceDocument;
			}
			MInOut mInOut = (MInOut) po;
			MInOutConfirm mInOutConfirm = new MInOutConfirm(mInOut, "PC");
			mInOutConfirm.setDocStatus(DocAction.STATUS_Drafted);
			mInOutConfirm.saveEx();

			MRFailedLines[] mRFailedLinesArray = mRFailedRequest.getMRFailedLinesArray();
			MInOutLine[] lines = mInOut.getLines(false);
			for (MInOutLine line : lines) {
				MInOutLineConfirm lineConfirm = new MInOutLineConfirm(mInOutConfirm);
				lineConfirm.setM_InOutLine_ID(line.get_ID());
				lineConfirm.setTargetQty(line.getMovementQty());
				lineConfirm.setConfirmedQty(line.getMovementQty());
				lineConfirm.saveEx();

				for (MRFailedLines failedLine : mRFailedLinesArray) {
					int data = failedLine.getMInOutLineId();
					if (data == line.get_ID()) {
						int failedQty = failedLine.getFailedQty();
						int confirmID = lineConfirm.get_ID();
						MInOutLineConfirm_Custom custom = new MInOutLineConfirm_Custom(ctx, confirmID,
								trx.getTrxName());
						BigDecimal failedB = new BigDecimal(failedQty);
						custom.processLine(false, "PC");
						custom.setQCFailedQty(failedB);
						custom.saveEx();
						lineConfirm.saveEx();
					}
				}
			}
			mInOutConfirm.setDescription(MInOutConfirm.COLUMNNAME_Description);
			mInOutConfirm.setProcessed(true);
			mInOutConfirm.setIsApproved(true);
			mInOutConfirm.setDocStatus(MInOutConfirm.DOCSTATUS_Completed);
			mInOutConfirm.setDocAction(MInOutConfirm.DOCACTION_Close);
			mInOutConfirm.saveEx();
			trx.commit();

			mRFailedResponce.setIsError(false);
			mRFailedResponce.setCreateConfirmationDocumentNumber(mInOutConfirm.getDocumentNo());
			mRFailedResponce.setCreateConfirmationId(mInOutConfirm.get_ID());

		} catch (Exception e) {
			mRFailedResponce.setIsError(true);
			mRFailedResponce.setError(e.getMessage());
			return mRFailedResponceDocument;
		} finally {
			getCompiereService().disconnect();
			trx.close();
		}
		return mRFailedResponceDocument;
	}

	@Override
	public PIListResponseDocument getPhysicalInventoryList(PIListRequestDocument pIListRequestDocument) {
		PIListResponseDocument pIListResponseDocument = PIListResponseDocument.Factory.newInstance();
		PIListResponse pIListResponse = pIListResponseDocument.addNewPIListResponse();
		PIListRequest pIListRequest = pIListRequestDocument.getPIListRequest();
		ADLoginRequest loginReq = pIListRequest.getADLoginRequest();
		String serviceType = pIListRequest.getServiceType();
		String searchKey = pIListRequest.getSearchKey();
		PreparedStatement pstm = null;
		ResultSet rs = null;
		Trx trx = null;
		
		// Get pagination details from request
		int pageSize = pIListRequest.getPageSize(); // Number of records per page
		int pageNumber = pIListRequest.getPageNumber(); // Current page number


		try {
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();

			int clientId = loginReq.getClientID();
			getCompiereService().connect();

			org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = VeUtils.convertAdLogin(loginReq);
			String err = login(adLoginReq, webServiceName, "pIList", serviceType);
			if (err != null && err.length() > 0) {
				pIListResponse.setError(err);
				pIListResponse.setIsError(true);
				return pIListResponseDocument;
			}

			if (!serviceType.equalsIgnoreCase("pIList")) {
				pIListResponse.setIsError(true);
				pIListResponse.setError("Service type " + serviceType + " not configured");
				return pIListResponseDocument;
			}

			List<Integer> orgList = new ArrayList<>();
			Login login = new Login(ctx);
			KeyNamePair[] orgs = login.getOrgs(new KeyNamePair(loginReq.getRoleID(), ""));
			if (orgs != null) {
				for (KeyNamePair org : orgs) {
					orgList.add(Integer.valueOf(org.getID()));
				}
			}
			String orgIds = orgList.stream().map(Object::toString).collect(Collectors.joining(", "));

			StringBuilder query = new StringBuilder("select a.m_inventory_id,\n" + "TO_CHAR(a.movementdate, 'DD/MM/YYYY') AS Date,\n"
					+ "b.name as Warehouse_Name,\n" + "c.name as Org_Name,\n" + "a.description,\n" + "a.documentno\n"
					+ "from m_inventory a\n" + "join m_warehouse b on a.m_warehouse_id = b.m_warehouse_id\n"
					+ "join ad_org c on a.ad_org_id = c.ad_org_id\n" + "where a.ad_client_id = " + clientId
					+ " and a.docstatus = 'DR' AND a.ad_org_id IN (" + orgIds + ") AND (\n"
					+ "    a.m_inventory_id::VARCHAR ILIKE '%' || COALESCE(?, a.m_inventory_id::VARCHAR) || '%'\n"
					+ "    OR c.name ILIKE '%' || COALESCE(?, c.name) || '%'\n"
					+ "    OR b.name ILIKE '%' || COALESCE(?, b.name) || '%'\n"
					+ "    OR a.description ILIKE '%' || COALESCE(?, a.description) || '%'\n"
					+ "    OR a.documentno ILIKE '%' || COALESCE(?,a.documentno) || '%'\n" + ")\n"
					+ " ORDER BY a.created desc");
			
			// Check if pageSize or pageNumber are not set (default to return all records)
			if (pageSize <= 0 || pageNumber <= 0) {

			} else {
				int offset = (pageNumber - 1) * pageSize;
				query.append(" LIMIT " + pageSize + " OFFSET " + offset + "");
			}			
			
			pstm = DB.prepareStatement(query.toString(), null);
			pstm.setString(1, searchKey);
			pstm.setString(2, searchKey);
			pstm.setString(3, searchKey);
			pstm.setString(4, searchKey);
			pstm.setString(5, searchKey);
			rs = pstm.executeQuery();

			int count = 0;
			while (rs.next()) {

				PIList pIList = pIListResponse.addNewPIList();
				String m_inventory_id = rs.getString("m_inventory_id");
				String date = rs.getString("Date");
				String orgName = rs.getString("Org_Name");
				String warehouseName = rs.getString("Warehouse_Name");
				String description = rs.getString("description");
				String documentno = rs.getString("documentno");
				pIList.setDocumentNo(documentno);
				pIList.setMInventoryId(m_inventory_id);
				pIList.setOrderDate(date);
				pIList.setWarehouseName(warehouseName);
				pIList.setOrgName(orgName);
				if (description == null)
					pIList.setDescription("");
				else
					pIList.setDescription(description);
				count++;
			}
			pIListResponse.setCount(count);
			trx.commit();

		} catch (Exception e) {
			pIListResponse.setIsError(true);
			pIListResponse.setError(e.getMessage());
			return pIListResponseDocument;
		} finally {
			closeDbCon(pstm, rs);
			trx.close();
			getCompiereService().disconnect();
		}
		return pIListResponseDocument;
	}

	@Override
	public PIDeatilsResponseDocument getPhysicalInventoryDetailsById(
			PIDeatilsRequestDocument pIDeatilsRequestDocument) {
		PIDeatilsResponseDocument pIDeatilsResponseDocument = PIDeatilsResponseDocument.Factory.newInstance();
		PIDeatilsResponse pIDeatilsResponse = pIDeatilsResponseDocument.addNewPIDeatilsResponse();
		PIDeatilsRequest pIDeatilsRequest = pIDeatilsRequestDocument.getPIDeatilsRequest();
		ADLoginRequest loginReq = pIDeatilsRequest.getADLoginRequest();
		String serviceType = pIDeatilsRequest.getServiceType();
		String mInventoryId = pIDeatilsRequest.getMInventoryId();
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			int clientId = loginReq.getClientID();
			getCompiereService().connect();

			org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = VeUtils.convertAdLogin(loginReq);
			String err = login(adLoginReq, webServiceName, "pIDetails", serviceType);
			if (err != null && err.length() > 0) {
				pIDeatilsResponse.setError(err);
				pIDeatilsResponse.setIsError(true);
				return pIDeatilsResponseDocument;
			}

			if (!serviceType.equalsIgnoreCase("pIDetails")) {
				pIDeatilsResponse.setIsError(true);
				pIDeatilsResponse.setError("Service type " + serviceType + " not configured");
				return pIDeatilsResponseDocument;
			}
			String query = null;
			query = "SELECT a.documentno,\n" + "       TO_CHAR(a.movementdate, 'DD/MM/YYYY') AS Date,\n"
					+ "       b.name AS Warehouse_Name,\n" + "       c.name AS Org_Name,\n" + "       a.description,\n"
					+ "       a.m_inventory_id\n" + "FROM m_inventory a\n"
					+ "JOIN m_warehouse b ON a.m_warehouse_id = b.m_warehouse_id\n"
					+ "JOIN ad_org c ON a.ad_org_id = c.ad_org_id\n" + "WHERE a.ad_client_id = " + clientId
					+ " AND a.m_inventory_id = " + mInventoryId + ";";

			pstm = DB.prepareStatement(query.toString(), null);
			rs = pstm.executeQuery();
			while (rs.next()) {
				String date = rs.getString("Date");
				String orgName = rs.getString("Org_Name");
				String warehouseName = rs.getString("Warehouse_Name");
				String description = rs.getString("description");
				String documentNo = rs.getString("documentno");
				pIDeatilsResponse.setDocumentNo(documentNo);
				pIDeatilsResponse.setMInventoryId(mInventoryId);
				pIDeatilsResponse.setOrderDate(date);
				pIDeatilsResponse.setWarehouseName(warehouseName);
				pIDeatilsResponse.setOrgName(orgName);
				if (description == null)
					pIDeatilsResponse.setDescription("");
				else
					pIDeatilsResponse.setDescription(description);
			}
			pstm.close();
			rs.close();

			query = "SELECT mil.m_inventoryline_id,\n" + "       mil.qtycount,mil.qtybook,\n"
					+ "       l.m_locator_id,\n" + "       l.value as locatorName,\n" + "       p.m_product_id,\n"
					+ "       p.name as productName\n" + "FROM adempiere.m_inventoryline mil\n"
					+ "JOIN adempiere.m_locator l ON l.m_locator_id = mil.m_locator_id\n"
					+ "JOIN adempiere.m_product p ON p.m_product_id = mil.m_product_id\n"
					+ "WHERE mil.m_inventory_id = " + mInventoryId + "ORDER BY mil.m_inventoryline_id;";
			pstm = DB.prepareStatement(query.toString(), null);
			rs = pstm.executeQuery();
			while (rs.next()) {
				PIDetailsLine pIDetailsLine = pIDeatilsResponse.addNewPIDetailsLines();

				String productName = rs.getString("productName");
				String locatorName = rs.getString("locatorName");
				int m_inventoryline_id = rs.getInt("m_inventoryline_id");
				int qtycount = rs.getInt("qtycount");
				int qtybook = rs.getInt("qtybook");
				int m_locator_id = rs.getInt("m_locator_id");
				int m_product_id = rs.getInt("m_product_id");
				pIDetailsLine.setLocatorId(m_locator_id);
				pIDetailsLine.setLocatorName(locatorName);
				pIDetailsLine.setQntyBook(qtybook);
				pIDetailsLine.setQtyCount(qtycount);
				pIDetailsLine.setPiLineId(m_inventoryline_id);
				pIDetailsLine.setProductId(m_product_id);
				pIDetailsLine.setProductName(productName);

			}

		} catch (Exception e) {
			pIDeatilsResponse.setIsError(true);
			pIDeatilsResponse.setError(e.getMessage());
			return pIDeatilsResponseDocument;
		} finally {
			closeDbCon(pstm, rs);
			getCompiereService().disconnect();
		}
		return pIDeatilsResponseDocument;
	}

	@Override
	public PIQtyChangeResponseDocument pIQtyChange(PIQtyChangeRequestDocument pICountQtyRequestDocument) {
		PIQtyChangeResponseDocument pICountQtyResponseDocument = PIQtyChangeResponseDocument.Factory.newInstance();
		PIQtyChangeResponse pICountQtyResponse = pICountQtyResponseDocument.addNewPIQtyChangeResponse();
		PIQtyChangeRequest pICountQtyRequest = pICountQtyRequestDocument.getPIQtyChangeRequest();
		ADLoginRequest loginReq = pICountQtyRequest.getADLoginRequest();
		String serviceType = pICountQtyRequest.getServiceType();
		Trx trx = null;

		try {
			CompiereService m_cs = getCompiereService();
			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();

			org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = VeUtils.convertAdLogin(loginReq);
			String err = login(adLoginReq, webServiceName, "pIQtyChange", serviceType);
			if (err != null && err.length() > 0) {
				pICountQtyResponse.setError(err);
				pICountQtyResponse.setIsError(true);
				return pICountQtyResponseDocument;
			}

			if (!serviceType.equalsIgnoreCase("pIQtyChange")) {
				pICountQtyResponse.setIsError(true);
				pICountQtyResponse.setError("Service type " + serviceType + " not configured");
				return pICountQtyResponseDocument;
			}
			Properties ctx = m_cs.getCtx();
			PILines[] ILinesArray = pICountQtyRequest.getPILinesArray();
			for (PILines lines : ILinesArray) {
				int pILineID = lines.getMInventoryLineId();
				int countQty = lines.getCountQty();
				BigDecimal countQtyB = new BigDecimal(countQty);
				MInventoryLine line = new MInventoryLine(ctx, pILineID, trx.getTrxName());
				line.setQtyCount(countQtyB);
				line.saveEx();
			}
			trx.commit();
			pICountQtyResponse.setIsError(false);

		} catch (Exception e) {
			pICountQtyResponse.setIsError(true);
			pICountQtyResponse.setError(e.getMessage());
			return pICountQtyResponseDocument;
		} finally {
			trx.close();
			getCompiereService().disconnect();
		}
		return pICountQtyResponseDocument;
	}

	@Override
	public SOListResponseDocument soList(SOListRequestDocument sOListRequestDocument) {
		SOListResponseDocument sOListResponseDocument = SOListResponseDocument.Factory.newInstance();
		SOListResponse sOListResponse = sOListResponseDocument.addNewSOListResponse();
		SOListRequest sOListRequest = sOListRequestDocument.getSOListRequest();
		ADLoginRequest loginReq = sOListRequest.getADLoginRequest();
		String serviceType = sOListRequest.getServiceType();
		String searchkey = sOListRequest.getSearchKey();
		PreparedStatement pstm = null;
		ResultSet rs = null;
		Trx trx = null;
		try {
			int clientId = loginReq.getClientID();
			getCompiereService().connect();
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();

			org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = VeUtils.convertAdLogin(loginReq);
			String err = login(adLoginReq, webServiceName, "sOList", serviceType);
			if (err != null && err.length() > 0) {
				sOListResponse.setError(err);
				sOListResponse.setIsError(true);
				return sOListResponseDocument;
			}

			if (!serviceType.equalsIgnoreCase("sOList")) {
				sOListResponse.setIsError(true);
				sOListResponse.setError("Service type " + serviceType + " not configured");
				return sOListResponseDocument;
			}

			int userId = Env.getAD_User_ID(ctx);
			MUser_Custom user = new MUser_Custom(ctx, userId, trxName);
			X_PI_Deptartment dept = new X_PI_Deptartment(ctx, user.getPI_DEPARTMENT_ID(), trxName);

			List<Integer> orgList = new ArrayList<>();
			Login login = new Login(ctx);
			KeyNamePair[] orgs = login.getOrgs(new KeyNamePair(loginReq.getRoleID(), ""));
			if (orgs != null) {
				for (KeyNamePair org : orgs) {
					orgList.add(Integer.valueOf(org.getID()));
				}
			}
			
			String orgIds = orgList.stream().map(Object::toString).collect(Collectors.joining(", "));

			StringBuilder query = new StringBuilder("SELECT DISTINCT\n" + "    so.documentno as Sales_Order,\n"
					+ "    so.c_order_id AS cOrderId, so.putStatus,so.iscourier AS iscourier,\n"
					+ "TO_CHAR(so.created, 'DD/MM/YYYY') AS Order_Date, so.pi_deptartment_ID,\n"
					+ "    wh.name AS Warehouse_Name, wh.m_warehouse_id,\n" + "bp.name AS Customer, so.created,\n"
					+ " so.description,\n" + "    CASE\n"
					+ "        WHEN so.docstatus = 'CO' AND mr.m_inout_id IS NULL THEN false\n"
					+ "        WHEN so.docstatus = 'CO' AND mr.m_inout_id IS NOT NULL THEN true \n"
					+ "    END AS status\n" + "FROM\n" + "    adempiere.c_order so\n" + "JOIN\n"
					+ "    adempiere.c_orderline sol ON so.c_order_id = sol.c_order_id\n" + "JOIN\n"
					+ "    adempiere.c_bpartner bp ON so.c_bpartner_id = bp.c_bpartner_id \n" + "JOIN\n"
					+ "    adempiere.m_warehouse wh ON so.m_warehouse_id = wh.m_warehouse_id\n" + "LEFT JOIN\n"
					+ "    adempiere.m_inout mr ON so.c_order_id = mr.c_order_id\n" + "WHERE\n"
					+ "    sol.qtyordered > (\n" + "        SELECT COALESCE(SUM(iol.qtyentered), 0)\n"
					+ "        FROM adempiere.m_inoutline iol\n"
					+ "        WHERE iol.c_orderline_id = sol.c_orderline_id\n" + "    )\n" + "AND\n"
					+ "    so.ad_client_id = '" + clientId + "'\n" + "AND\n" + "    so.issotrx = 'Y'\n" + "AND\n"
					+ "    so.docstatus = 'CO' " + "AND (\n"
					+ "    so.documentno ILIKE '%' || COALESCE(?, so.documentno) || '%'\n"
					+ "    OR bp.name ILIKE '%' || COALESCE(?, bp.name) || '%'\n"
					+ "    OR wh.name ILIKE '%' || COALESCE(?, wh.name) || '%'\n"
					+ "    OR so.description ILIKE '%' || COALESCE(?, so.description) || '%'\n"
					+ ") AND so.ad_org_id IN (" + orgIds + ") \n" + " ORDER BY so.created desc");

			pstm = DB.prepareStatement(query.toString(), null);
			pstm.setString(1, searchkey);
			pstm.setString(2, searchkey);
			pstm.setString(3, searchkey);
			pstm.setString(4, searchkey);
			rs = pstm.executeQuery();

			List<Map<String, Object>> allOrders = new ArrayList<>();

			while (rs.next()) {
				String deptId = rs.getString("pi_deptartment_ID");
				if (deptId == null || (deptId != null && dept.getPI_Deptartment_ID() == Integer.valueOf(deptId))) {
					Map<String, Object> orderData = new HashMap<>();
					orderData.put("documentNo", rs.getString("Sales_Order"));
					orderData.put("cOrderId", rs.getString("cOrderId"));
					orderData.put("customer", rs.getString("Customer"));
					orderData.put("date", rs.getString("Order_Date"));
					orderData.put("warehouseName", rs.getString("Warehouse_Name"));
					orderData.put("description", rs.getString("description"));
					orderData.put("status", rs.getBoolean("status"));
					orderData.put("putStatus", rs.getString("putStatus"));
					orderData.put("mWarehouseId", rs.getInt("m_warehouse_id"));
					orderData.put("created", rs.getTimestamp("created"));
					orderData.put("iscourier", rs.getBoolean("iscourier"));
					orderData.put("isMobileOrder", false);
					allOrders.add(orderData);
				}
			}

			List<PO> mobileOrders = new Query(ctx, MOrder.Table_Name, 
					"AD_Client_ID=? AND IsSOTrx='Y' AND IsMobileOrder='Y' AND DocStatus='DR' AND AD_Org_ID IN (" + orgIds + ")", trxName)
					.setParameters(clientId)
					.list();

			for (PO po : mobileOrders) {
				COrder_Custom mOrder = new COrder_Custom(ctx, po.get_ID(), trxName);
				Map<String, Object> orderData = new HashMap<>();
				orderData.put("documentNo", mOrder.getDocumentNo());
				orderData.put("cOrderId", String.valueOf(mOrder.getC_Order_ID()));
				orderData.put("customer", mOrder.getC_BPartner().getName());
				orderData.put("date", new SimpleDateFormat("dd/MM/yyyy").format(mOrder.getDateOrdered()));
				orderData.put("warehouseName", mOrder.getM_Warehouse().getName());
				orderData.put("description", mOrder.getDescription() != null ? mOrder.getDescription() : "");
				orderData.put("status", false);
				orderData.put("putStatus", "DRAFT");
				orderData.put("mWarehouseId", mOrder.getM_Warehouse_ID());
				orderData.put("created", mOrder.getCreated());
				orderData.put("iscourier", mOrder.iscourier());
				orderData.put("isMobileOrder", true);
				orderData.put("mOrder", mOrder);
				allOrders.add(orderData);
			}

			allOrders.sort((o1, o2) -> ((Timestamp)o2.get("created")).compareTo((Timestamp)o1.get("created")));

			int count = 0;
			for (Map<String, Object> orderData : allOrders) {
				SOListAccess sOListAccess = sOListResponse.addNewListAccess();
				sOListAccess.setDocumentNumber((String)orderData.get("documentNo"));
				sOListAccess.setCOrderId((String)orderData.get("cOrderId"));
				sOListAccess.setOrderDate((String)orderData.get("date"));
				sOListAccess.setCustomerName((String)orderData.get("customer"));
				sOListAccess.setWarehouseName((String)orderData.get("warehouseName"));
				sOListAccess.setWarehouseId((Integer)orderData.get("mWarehouseId"));
				sOListAccess.setDescription((String)orderData.get("description") != null ? (String)orderData.get("description") : "");
				sOListAccess.setStatus((String)orderData.get("putStatus") != null ? (String)orderData.get("putStatus") : "");
				sOListAccess.setSalesOrderStatus((Boolean)orderData.get("status"));
				sOListAccess.setIsCourier((Boolean)orderData.get("iscourier"));
				sOListAccess.setIsMobileOrder((Boolean)orderData.get("isMobileOrder"));

				if ((Boolean)orderData.get("isMobileOrder")) {
					sOListAccess.setIsCourier(false);
					sOListAccess.setQuantityPicked(0);
					sOListAccess.setQuantityTotal(0);
				} else {
					MOrder mOrder = new MOrder(ctx, Integer.valueOf((String)orderData.get("cOrderId")), trxName);
					MInOut[] mInouts = mOrder.getShipments();
					LinkedHashMap<Integer, Integer> labelList = getLabelsPickedForSO(ctx, trxName, mInouts);
					MOrderLine[] orderLines = mOrder.getLines();

					int pickedQnty = 0;
					int totalQnty = 0;
					LinkedHashMap<Integer, Integer> totalOrderQnty = new LinkedHashMap<Integer, Integer>();
					for (MOrderLine line : orderLines) {
						int productId = line.getM_Product_ID();
						int qnty = line.getQtyEntered().intValue();
						if (totalOrderQnty.containsKey(productId))
							qnty += totalOrderQnty.get(productId);
						totalOrderQnty.put(productId, qnty);
					}
					for (Integer key : totalOrderQnty.keySet()) {
						totalQnty += totalOrderQnty.get(key);
						if (labelList.containsKey(key))
							pickedQnty += labelList.get(key);
					}
					sOListAccess.setQuantityPicked(pickedQnty);
					sOListAccess.setQuantityTotal(totalQnty);
				}
				count++;
			}

			sOListResponse.setCount(count);
			trx.commit();
		} catch (Exception e) {
			sOListResponse.setIsError(true);
			e.printStackTrace();
			sOListResponse.setError(e.getMessage());
			return sOListResponseDocument;
		} finally {
			trx.close();
			closeDbCon(pstm, rs);
			getCompiereService().disconnect();
		}
		return sOListResponseDocument;
	}

	@Override
	public SODetailResponseDocument soDetails(SODetailRequestDocument sODetailRequestDocument) {
		SODetailResponseDocument sODetailResponseDocument = SODetailResponseDocument.Factory.newInstance();
		SODetailResponse sODetailResponse = sODetailResponseDocument.addNewSODetailResponse();
		SODetailRequest sODetailRequest = sODetailRequestDocument.getSODetailRequest();
		ADLoginRequest loginReq = sODetailRequest.getADLoginRequest();
		String serviceType = sODetailRequest.getServiceType();
		String documentNo = sODetailRequest.getDocumentNo();
		int client_ID = loginReq.getClientID();
		Trx trx = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();

			org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = VeUtils.convertAdLogin(loginReq);
			String err = login(adLoginReq, webServiceName, "sODetail", serviceType);
			if (err != null && err.length() > 0) {
				sODetailResponse.setError(err);
				sODetailResponse.setIsError(true);
				return sODetailResponseDocument;
			}
			if (!serviceType.equalsIgnoreCase("sODetail")) {
				sODetailResponse.setIsError(true);
				sODetailResponse.setError("Service type " + serviceType + " not configured");
				return sODetailResponseDocument;
			}		
	
			List<PO> dispatchDepartment = VeUtils.getDispatchDepartment(client_ID, loginReq.getOrgID(), ctx, trxName);
			if(dispatchDepartment == null || dispatchDepartment.size() == 0) {
				sODetailResponse.setIsError(true);
				sODetailResponse.setError("Dispatch Department Not Found");
				return sODetailResponseDocument;
			}
			PO po = dispatchDepartment.get(0);
			X_PI_Deptartment deptartment = new X_PI_Deptartment(ctx, po.get_ID(), trxName);
			List<MLocator> dispatchLocatorList = VeUtils.getLocatorsByDepartment(ctx, trxName, loginReq.getWarehouseID(),
					po.get_ID(), "dispatch", "Y");
			if(dispatchLocatorList == null || dispatchLocatorList.size() == 0) {
				sODetailResponse.setIsError(true);
				sODetailResponse.setError("Dispatch  Locator Not Found for Department : "+deptartment.getdeptname()+"");
				return sODetailResponseDocument;
			}
			MLocator dispatchLocator = new MLocator(ctx, dispatchLocatorList.get(0).get_ID(), trxName);
			sODetailResponse.setDispatchLocatorId(dispatchLocator.get_ID());
			sODetailResponse.setDispatchLocatorName(dispatchLocator.getValue());
			
			String query = null;
			query = "SELECT a.qtyordered as totalQnty, (a.qtyordered - COALESCE(SUM(c.qtyentered), 0)) AS outstanding_qty, e.m_product_id as productId, a.c_order_id, a.c_uom_id, a.c_orderline_id, e.name AS product_name\n"
					+ "FROM c_orderline a \n" + "JOIN c_order d ON d.c_order_id = a.c_order_id \n"
					+ "LEFT JOIN m_inout b ON a.c_order_id = b.c_order_id \n"
					+ "LEFT JOIN m_inoutline c ON c.m_inout_id = b.m_inout_id AND c.c_orderline_id = a.c_orderline_id\n"
					+ "JOIN m_product e ON e.m_product_id = a.m_product_id \n" + "WHERE d.documentno = '" + documentNo
					+ "' AND d.ad_client_id = '" + client_ID + "' AND a.c_order_id = (\n"
					+ "  SELECT c_order_id FROM c_order WHERE documentno = '" + documentNo + "' AND ad_client_id = '"
					+ client_ID + "'\n" + ")\n"
					+ "GROUP BY e.m_product_id, e.name, a.qtyordered, a.c_orderline_id, a.c_uom_id, a.c_order_id ORDER BY a.c_orderline_id;\n"
					+ "";

			pstm = DB.prepareStatement(query.toString(), trxName);
			rs = pstm.executeQuery();

			int quantityPicked = 0;
			int quantityTotal = 0;
			int cOrderId = 0;
			COrder_Custom mOrder = null;
			
			boolean linesExist = false;
			
			while (rs.next()) {

				String productName = rs.getString("product_name");
				int productId = rs.getInt("productId");
				cOrderId = rs.getInt("c_order_id");
				int cOrderlineId = rs.getInt("c_orderline_id");
				int uomId = rs.getInt("c_uom_id");
				int totalQnty = rs.getInt("totalQnty");
				int outstanding_qty = rs.getInt("outstanding_qty");

				if (mOrder == null) 
					mOrder = new COrder_Custom(ctx, cOrderId, trxName);

				SoDetailProductData productData = sODetailResponse.addNewProductData();
				productData.setProductId(productId);
				productData.setProductName(productName);
				productData.setCOrderlineId(cOrderlineId);
				productData.setUomId(uomId);
				productData.setOutstandingQnty(totalQnty - outstanding_qty); // key mistake, but it tells the quantity how much already picked / dispatched for the sales order
				productData.setTotalQuantity(totalQnty);
				productData.setRemainingQntyToPick(outstanding_qty);

				if (outstanding_qty > 0) {
					getSuggestedLocatorForProduct(productData, ctx, trxName, loginReq.getWarehouseID(), loginReq.getClientID());
					QntyAvailableInLocator[] pickLabourLinesArray = productData.getQntyAvailableInLocatorArray();
					if (pickLabourLinesArray.length == 0 || pickLabourLinesArray == null) {
						productData.addNewQntyAvailableInLocator();
					}
				}

				quantityPicked += totalQnty - outstanding_qty;
				quantityTotal += totalQnty;
				
				if(!linesExist)
					linesExist = true;
				
			}
			
			if(!linesExist)
				sODetailResponse.addNewProductData();
			
			sODetailResponse.setQuantityPicked(quantityPicked);
			sODetailResponse.setQuantityTotal(quantityTotal);

			if (mOrder == null) {
				List<PO> orderList = new Query(ctx, MOrder.Table_Name, "DocumentNo=? AND AD_Client_ID=?", trxName)
						.setParameters(documentNo, client_ID).list();
				if (orderList != null && orderList.size() > 0) {
					mOrder = new COrder_Custom(ctx, orderList.get(0).get_ID(), trxName);
				}else {
					sODetailResponse.setIsError(true);
					sODetailResponse.setError("Inavlid Order");
					return sODetailResponseDocument;
				}
			}

			sODetailResponse.setCOrderId(mOrder.get_ID());
			LocalDateTime createdDateTime = mOrder.getCreated().toLocalDateTime();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String formattedDate = createdDateTime.format(formatter);

			sODetailResponse.setOrderDate(formattedDate);
			sODetailResponse.setDocStatus(mOrder.getDocStatus());
			sODetailResponse.setCustomer(mOrder.getC_BPartner().getName());
			sODetailResponse.setWarehouseName(mOrder.getM_Warehouse().getName());
			sODetailResponse.setDescription(mOrder.getDescription() != null ? mOrder.getDescription() : "");
			sODetailResponse.setOrderStatus(false);
			sODetailResponse.setLocationName(mOrder.getC_BPartner_Location().getName());
			sODetailResponse.setDocumentNo(documentNo);
			sODetailResponse.setIsCourier(mOrder.iscourier());
			sODetailResponse.setIsMobileOrder(mOrder.isMobileOrder());
			trx.commit();
		} catch (Exception e) {
			e.printStackTrace();
			sODetailResponse.setError(e.getMessage());
			sODetailResponse.setIsError(true);
			return sODetailResponseDocument;

		} finally {
			closeDbCon(pstm, rs);
			trx.close();
			getCompiereService().disconnect();
		}
		return sODetailResponseDocument;
	}

	private LinkedHashMap<Integer, Integer> getLabelsPickedForSO(Properties ctx, String trxName, MInOut[] mInouts) {
		LinkedHashMap<Integer, Integer> labelList = new LinkedHashMap<Integer, Integer>();

		if (mInouts != null && mInouts.length != 0)
			for (MInOut inout : mInouts) {
				MInOutLine[] inOutLines = inout.getLines();
				if (inOutLines != null && inOutLines.length != 0)
					for (MInOutLine line : inOutLines) {

						int qnty = line.getQtyEntered().intValue();
						int productId = line.getM_Product_ID();
						if (labelList.containsKey(productId))
							qnty += labelList.get(productId);
						labelList.put(productId, qnty);

					}
			}
		return labelList;
	}
	
	private void getSuggestedLocatorForProduct(SoDetailProductData productData, Properties ctx, String trxName,
			int warehouseId, int clientId) {

		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {

			boolean pickFlag = false;
			int productId = productData.getProductId();
			int quantity = productData.getRemainingQntyToPick();

			String sql = "SELECT pp.m_locator_id, pp.m_product_id,\n"
					+ "    SUM(CASE \n"
					+ "            WHEN pp.issotrx = 'N' THEN pp.quantity \n"
					+ "            ELSE 0 \n"
					+ "        END) AS remaining_count\n"
					+ "FROM \n"
					+ "    adempiere.pi_productlabel pp\n"
					+ "	JOin adempiere.m_locator ml on ml.m_locator_id = pp.m_locator_id\n"
					+ "	JOin adempiere.m_locatortype mt on mt.m_locatortype_id = ml.m_locatortype_id\n"
					+ "WHERE \n"
					+ "    -- pp.ad_client_id = "+clientId+" AND\n"
					+ "	NOT EXISTS (\n"
					+ "        SELECT 1 \n"
					+ "        FROM adempiere.pi_productlabel pp_sales\n"
					+ "        WHERE pp_sales.labeluuid = pp.labeluuid\n"
					+ "        AND pp_sales.issotrx = 'Y'\n"
					+ "    )\n"
					+ "    AND mt.storage = 'Y'\n"
					+ "	And pp.m_product_id = "+productId+"\n"
					+ "	Group by pp.m_locator_id, pp.m_product_id;";

			pstm = DB.prepareStatement(sql.toString(), null);
			rs = pstm.executeQuery();
			while (rs.next()) {
				int remainingCount = rs.getInt("remaining_count");
//				int ProductId = rs.getInt("m_product_id");
				int locatorId = rs.getInt("m_locator_id");

				MLocator locator = new MLocator(ctx, locatorId, trxName);

				QntyAvailableInLocator[] pickLabourLinesArray = productData.getQntyAvailableInLocatorArray();
				boolean flag = false;

				for (QntyAvailableInLocator line : pickLabourLinesArray) {
					if (line.getLocatorId() == locatorId) {
						int availableQty = Math.min(remainingCount, quantity);
						line.setQuantityAvailable(line.getQuantityAvailable() + availableQty);
						quantity -= availableQty;
						flag = true;
						if (quantity <= 0) {
							pickFlag = true;
							break;
						}
						break;
					}
				}
				MLocatorType_Custom type = new MLocatorType_Custom(ctx, locator.getM_LocatorType_ID(), trxName);
				if (!flag && !type.isdispatch() && !type.isPacking() && !type.isReturns()) {
					QntyAvailableInLocator line = productData.addNewQntyAvailableInLocator();
					int availableQty = Math.min(remainingCount, quantity);
					line.setQuantityAvailable(availableQty);
					quantity -= availableQty;
					line.setLocatorId(locatorId);
					line.setLocatorName(locator.getValue());
					if (quantity <= 0) {
						pickFlag = true;
						break;
					}

				}
				if (pickFlag) {
					break;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DB.close(rs, pstm);
		}

	}

	@Override
	public CreateSCResponseDocument createShipment(CreateSCRequestDocument createSCRequestDocument) {
		CreateSCResponseDocument createSCResponseDocument = CreateSCResponseDocument.Factory.newInstance();
		CreateSCResponse createSCResponse = createSCResponseDocument.addNewCreateSCResponse();
		CreateSCRequest createSCRequest = createSCRequestDocument.getCreateSCRequest();
		ADLoginRequest loginReq = createSCRequest.getADLoginRequest();
		String serviceType = createSCRequest.getServiceType();
		int cOrderId = createSCRequest.getCOrderId();
		String marking = createSCRequest.getAreaMarking();
		String status = createSCRequest.getStatus();
		MInvoice m_invoice = null;
		MRMA s_rma = null;
		Trx trx = null;
		int clientID = loginReq.getClientID();
		CompiereService m_cs = getCompiereService();
		Properties ctx = m_cs.getCtx();
		int mInoutId = 0;
		try {
			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = VeUtils.convertAdLogin(loginReq);
			String err = login(adLoginReq, webServiceName, "createSC", serviceType);
			if (err != null && err.length() > 0) {
				createSCResponse.setError(err);
				createSCResponse.setIsError(true);
				return createSCResponseDocument;
			}
			if (!serviceType.equalsIgnoreCase("createSC")) {
				createSCResponse.setIsError(true);
				createSCResponse.setError("Service type " + serviceType + " not configured");
				return createSCResponseDocument;
			}

			MTable table = MTable.get(ctx, "c_order");
			PO po = table.getPO(cOrderId, trx.getTrxName());
			if (po == null) {
				createSCResponse.setError("order not found for " + cOrderId + "");
				createSCResponse.setIsError(true);
				return createSCResponseDocument;
			}
			MOrder order = (MOrder) po;
			MTable docTypeTable = MTable.get(ctx, "c_doctype");
			PO docTypePO = docTypeTable.getPO("name = 'MM Shipment' and ad_client_id = " + clientID + "",
					trx.getTrxName());
			MDocType mDocType = (MDocType) docTypePO;

			MInOut dispatch = new MInOut(order, mDocType.get_ID(), order.getDateOrdered());
			dispatch.setDocStatus(DocAction.STATUS_Drafted);
			dispatch.saveEx();

			mInoutId = dispatch.getM_InOut_ID();
			MInOut_Custom inout = new MInOut_Custom(ctx, mInoutId, trx.getTrxName());
			if (marking != null && marking != "") {
				inout.setMarking(marking);
				inout.saveEx();
			}
			if (status != null && status != "") {
				inout.setPickStatus(status);
				inout.saveEx();
			}

			ShipmentLines[] shipmentLinesArray = createSCRequest.getShipmentLinesArray();
			for (ShipmentLines lines : shipmentLinesArray) {
				int C_InvoiceLine_ID = 0;
				int M_RMALine_ID = 0;
				int M_Product_ID = lines.getProductId();
				int C_UOM_ID = lines.getUomId();
				int C_OrderLine_ID = lines.getCOrderlineId();
				BigDecimal QtyEntered = BigDecimal.valueOf(lines.getQnty());
				int M_Locator_ID = lines.getLocator();

				inout.createLineFrom(C_OrderLine_ID, C_InvoiceLine_ID, M_RMALine_ID, M_Product_ID, C_UOM_ID, QtyEntered,
						M_Locator_ID);
				MInOutLine[] mInoutLines = inout.getLines();
				int lineId = mInoutLines[mInoutLines.length - 1].get_ID();
				lines.setMRLineId(lineId);

			}
			inout.updateFrom(order, m_invoice, s_rma);

			trx.commit();
			createSCResponse.setIsError(false);
			createSCResponse.setShipmentDocumentNumber(inout.getDocumentNo());
			createSCResponse.setShipmentId(mInoutId);
			createSCResponse.setShipmentLinesArray(shipmentLinesArray);

			Map<String, String> data = new HashMap<>();
			data.put("recordId", String.valueOf(inout.getM_InOut_ID()));
			data.put("documentNo", String.valueOf(inout.getDocumentNo()));
			data.put("path1", "/dispatch_screen");
			data.put("path2", "/dispatch_detail_screen");

			COrder_Custom orderCustom = new COrder_Custom(ctx, cOrderId, trxName);
			
			VeUtils.sendNotificationAsync("finaldispatchapp", inout.get_Table_ID(), inout.getM_InOut_ID(), ctx, trxName,
					"Products ready for dispatch - " + inout.getDocumentNo() + "",
					"New products marked ready for dispatch with Shipment No: " + inout.getDocumentNo()
							+ " for Order - " + inout.getC_Order().getDocumentNo() + "",
					inout.get_TableName(), data, loginReq.getClientID(), "CustomerShipmentCreted", orderCustom.getpidepartmentID());
			
			if(createSCRequest.getMoveInventory()) {
				putAwayLabels(createSCRequest.getPutAwayLineArray(), trxName, ctx, loginReq.getClientID(),
						loginReq.getOrgID(), false, false, true, loginReq.getWarehouseID());
			}

		} catch (Exception e) {
			MTable table = MTable.get(ctx, "m_inout");
			PO po = table.getPO(mInoutId, trx.getTrxName());
			po.delete(true);
			if (trx != null)
				trx.rollback();
			createSCResponse.setError(e.getMessage());
			createSCResponse.setIsError(true);
			return createSCResponseDocument;
		} finally {
			getCompiereService().disconnect();
			trx.close();
		}
		return createSCResponseDocument;
	}

	private void moveInventory(Properties ctx, String trxName, MMovement mMovement, int cCOrderLineID,
			BigDecimal quantity, int productId, int fromLocatorId, int toLocatorId, int clientId, int orgId) {
		MMovementLine mMovementLine = new MMovementLine(ctx, 0, trxName);
		mMovementLine.setM_Locator_ID(fromLocatorId);
		mMovementLine.setM_LocatorTo_ID(toLocatorId);
		mMovementLine.setM_Movement_ID(mMovement.get_ID());
		mMovementLine.setM_Product_ID(productId);
		mMovementLine.setLine(10);
		mMovementLine.setMovementQty(quantity);
		mMovementLine.setDescription(null);
		mMovementLine.setProcessed(true);
		mMovementLine.saveEx();

		MMovementLineMA mMovementLineMA = new MMovementLineMA(ctx, 0, trxName);
		mMovementLineMA.setM_AttributeSetInstance_ID(0);
		mMovementLineMA.setIsActive(true);
		mMovementLineMA.setMovementQty(mMovementLine.getMovementQty());
		mMovementLineMA.setIsAutoGenerated(true);
		mMovementLineMA.setM_MovementLine_ID(mMovementLine.get_ID());
		mMovementLineMA.setDateMaterialPolicy(new Timestamp(new Date().getTime()));
		mMovementLineMA.saveEx();

		MTransaction mTransactionOut = new MTransaction(ctx, 0, trxName);
		mTransactionOut.setIsActive(true);
		mTransactionOut.setMovementType("M-");
		mTransactionOut.setM_Locator_ID(mMovementLine.getM_Locator_ID());
		mTransactionOut.setM_Product_ID(mMovementLine.getM_Product_ID());
		mTransactionOut.setMovementQty(mMovementLine.getMovementQty().negate());
		mTransactionOut.setM_MovementLine_ID(mMovementLine.get_ID());
		mTransactionOut.setM_AttributeSetInstance_ID(mMovementLine.getM_AttributeSetInstance_ID());
		mTransactionOut.saveEx();

		MTransaction mTransactionIn = new MTransaction(ctx, 0, trxName);
		mTransactionIn.setIsActive(true);
		mTransactionIn.setMovementType("M+");
		mTransactionIn.setM_Locator_ID(mMovementLine.getM_LocatorTo_ID());
		mTransactionIn.setM_Product_ID(mMovementLine.getM_Product_ID());
		mTransactionIn.setMovementQty(mMovementLine.getMovementQty());
		mTransactionIn.setM_MovementLine_ID(mMovementLine.get_ID());
		mTransactionIn.setM_AttributeSetInstance_ID(mMovementLine.getM_AttributeSetInstance_ID());
		mTransactionIn.saveEx();

		MAcctSchema[] MAcctSchemaArray = MAcctSchema.getClientAcctSchema(ctx, clientId);
		MAcctSchema AcctSchema = MAcctSchemaArray[0];

		List<PO> list = new Query(ctx, MElementValue.Table_Name, "name=? AND ad_client_id=?", trxName)
				.setParameters("Product asset", clientId).setOrderBy(MElementValue.COLUMNNAME_C_ElementValue_ID).list();
		MElementValue mElementValue = (MElementValue) list.get(0);
		int mElementValueId = mElementValue.get_ID();

		MPeriod mPeriod = MPeriod.get(ctx, mMovement.getMovementDate(), orgId, trxName);

		MGLCategory mGLCategory = MGLCategory.getDefault(ctx, "M");
		BigDecimal price = new BigDecimal(0);
		if (cCOrderLineID != 0) {
			MOrderLine mOrderLine = new MOrderLine(ctx, cCOrderLineID, trxName);
			price = mOrderLine.getPriceActual();
		}

		int uomId = new MProduct(ctx, productId, trxName).getC_UOM_ID();
		MFactAcct mFactAcct = new MFactAcct(ctx, 0, trxName);
		mFactAcct.setIsActive(true);
		mFactAcct.setC_AcctSchema_ID(AcctSchema.get_ID());
		mFactAcct.setAccount_ID(mElementValueId);
		mFactAcct.setDateTrx(mMovement.getMovementDate());
		mFactAcct.setDateAcct(mMovement.getMovementDate());
		mFactAcct.setC_Period_ID(mPeriod.get_ID());
		mFactAcct.setAD_Table_ID(mMovement.get_Table_ID());
		mFactAcct.setRecord_ID(mMovement.get_ID());
		mFactAcct.setLine_ID(mMovementLine.get_ID());
		mFactAcct.setGL_Category_ID(mGLCategory.get_ID());
		mFactAcct.setM_Locator_ID(fromLocatorId);
		mFactAcct.setPostingType("A");
		mFactAcct.setC_Currency_ID(AcctSchema.getC_Currency_ID());
		mFactAcct.setAmtSourceDr(price.multiply(quantity));
		mFactAcct.setAmtSourceCr(BigDecimal.valueOf(0));
		mFactAcct.setAmtAcctCr(BigDecimal.valueOf(0));
		mFactAcct.setAmtAcctDr(price.multiply(quantity));
		mFactAcct.setC_UOM_ID(uomId);
		mFactAcct.setQty(quantity);
		mFactAcct.setM_Product_ID(productId);
		mFactAcct.setDescription(mMovement.getDocumentNo() + "#" + mMovementLine.getLine());
		mFactAcct.saveEx();

		MFactAcct mFactAcctIn = new MFactAcct(ctx, 0, trxName);
		mFactAcctIn.setIsActive(true);
		mFactAcctIn.setC_AcctSchema_ID(AcctSchema.get_ID());
		mFactAcctIn.setAccount_ID(mElementValueId);
		mFactAcctIn.setDateTrx(mMovement.getMovementDate());
		mFactAcctIn.setDateAcct(mMovement.getMovementDate());
		mFactAcctIn.setC_Period_ID(mPeriod.get_ID());
		mFactAcctIn.setAD_Table_ID(mMovement.get_Table_ID());
		mFactAcctIn.setRecord_ID(mMovement.get_ID());
		mFactAcctIn.setLine_ID(mMovementLine.get_ID());
		mFactAcctIn.setGL_Category_ID(mGLCategory.get_ID());
		mFactAcctIn.setM_Locator_ID(toLocatorId);
		mFactAcctIn.setPostingType("A");
		mFactAcctIn.setC_Currency_ID(AcctSchema.getC_Currency_ID());
		mFactAcctIn.setAmtSourceCr(price.multiply(quantity));
		mFactAcctIn.setAmtSourceDr(BigDecimal.valueOf(0));
		mFactAcctIn.setAmtAcctDr(BigDecimal.valueOf(0));
		mFactAcctIn.setAmtAcctCr(price.multiply(quantity));
		mFactAcctIn.setC_UOM_ID(uomId);
		mFactAcctIn.setQty(quantity);
		mFactAcctIn.setM_Product_ID(productId);
		mFactAcctIn.setDescription(mMovement.getDocumentNo() + "#" + mMovementLine.getLine());
		mFactAcctIn.saveEx();
	}

	@Override
	public LocatorSuggestionResponseDocument getLocatorSuggestionForProduct(
			LocatorSuggestionRequestDocument locatorSuggestionRequestDocument) {
		LocatorSuggestionResponseDocument locatorSuggestionResponseDocument = LocatorSuggestionResponseDocument.Factory
				.newInstance();
		LocatorSuggestionResponse locatorSuggestionResponse = locatorSuggestionResponseDocument
				.addNewLocatorSuggestionResponse();
		LocatorSuggestionRequest locatorSuggestionRequest = locatorSuggestionRequestDocument
				.getLocatorSuggestionRequest();
		ADLoginRequest loginReq = locatorSuggestionRequest.getADLoginRequest();
		String serviceType = locatorSuggestionRequest.getServiceType();
		Trx trx = null;
		CompiereService m_cs = getCompiereService();
		Properties ctx = m_cs.getCtx();
		try {
			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = VeUtils.convertAdLogin(loginReq);
			String err = login(adLoginReq, webServiceName, "locatorSuggestion", serviceType);
			if (err != null && err.length() > 0) {
				locatorSuggestionResponse.setError(err);
				locatorSuggestionResponse.setIsError(true);
				return locatorSuggestionResponseDocument;
			}
			if (!serviceType.equalsIgnoreCase("locatorSuggestion")) {
				locatorSuggestionResponse.setIsError(true);
				locatorSuggestionResponse.setError("Service type " + serviceType + " not configured");
				return locatorSuggestionResponseDocument;
			}

			int warehouseId = locatorSuggestionRequest.getWarehouseID();
			MWarehouse mWarehouse = new MWarehouse(ctx, warehouseId, trxName);
			MLocator mLocatorArray[] = mWarehouse.getLocators(true);

			LocatorSLine[] locatorSLineArray = locatorSuggestionRequest.getLocatorSLineArray();
			for (LocatorSLine data : locatorSLineArray) {
				LocatorSLine line = locatorSuggestionResponse.addNewLocatorSLine();
				int M_Product_ID = data.getProductId();
				int locatorSuggested = 0;
				for (MLocator mLocator : mLocatorArray) {
					BigDecimal qnty = MStorageOnHand.getQtyOnHandForLocator(M_Product_ID, mLocator.get_ID(), 0,
							trxName);
					MLocatorType_Custom type = new MLocatorType_Custom(ctx, mLocator.getM_LocatorType_ID(), trxName);
					if (!type.isdispatch() && qnty.intValue() <= 0) {
						locatorSuggested = mLocator.get_ID();
						break;
					}
				}
				line.setProductId(M_Product_ID);
				line.setLocatorSuggested(locatorSuggested);
			}
			trx.commit();
			locatorSuggestionResponse.setIsError(false);
		} catch (Exception e) {
			locatorSuggestionResponse.setError(e.getMessage());
			locatorSuggestionResponse.setIsError(true);
			return locatorSuggestionResponseDocument;

		} finally {
			getCompiereService().disconnect();
			trx.close();
		}

		return locatorSuggestionResponseDocument;
	}

	@Override
	public StandardResponseDocument markMrQcChecked(MarkMRQcCheckedRequestDocument req) {

		StandardResponseDocument standardResponseDocument = StandardResponseDocument.Factory.newInstance();
		StandardResponse standardResponse = standardResponseDocument.addNewStandardResponse();
		MarkMRQcCheckedRequest markMRQcCheckedRequest = req.getMarkMRQcCheckedRequest();
		ADLoginRequest loginReq = markMRQcCheckedRequest.getADLoginRequest();
		String serviceType = markMRQcCheckedRequest.getServiceType();
		boolean isQcChecked = markMRQcCheckedRequest.getQchChecked();
		int mInoutId = markMRQcCheckedRequest.getMInoutId();
		Trx trx = null;
		try {
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			getCompiereService().connect();
			org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = VeUtils.convertAdLogin(loginReq);
			String err = login(adLoginReq, webServiceName, "markMRQcChecked", serviceType);
			if (err != null && err.length() > 0) {
				standardResponse.setError(err);
				standardResponse.setIsError(true);
				return standardResponseDocument;
			}

			if (!serviceType.equalsIgnoreCase("markMRQcChecked")) {
				standardResponse.setIsError(true);
				standardResponse.setError("Service type " + serviceType + " not configured");
				return standardResponseDocument;
			}

			if (isQcChecked == true) {
				MInOut_Custom mInOut_Custom = new MInOut_Custom(ctx, mInoutId, trxName);
				mInOut_Custom.setPickStatus("QC");
				mInOut_Custom.saveEx();
			}
			standardResponse.setIsError(false);
			trx.commit();
		} catch (Exception e) {
			standardResponse.setIsError(true);
			standardResponse.setError(e.getMessage());
			return standardResponseDocument;
		} finally {
			getCompiereService().disconnect();
			trx.close();
		}
		return standardResponseDocument;
	}

	@Override
	public StandardResponseDocument updateMr(UpdateMrRequestDocument req) {

		StandardResponseDocument standardResponseDocument = StandardResponseDocument.Factory.newInstance();
		StandardResponse standardResponse = standardResponseDocument.addNewStandardResponse();
		UpdateMrRequest updateMrRequest = req.getUpdateMrRequest();
		ADLoginRequest loginReq = updateMrRequest.getADLoginRequest();
		String serviceType = updateMrRequest.getServiceType();
		Trx trx = null;
		try {
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			getCompiereService().connect();
			org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = VeUtils.convertAdLogin(loginReq);
			String err = login(adLoginReq, webServiceName, "updateMr", serviceType);
			if (err != null && err.length() > 0) {
				standardResponse.setError(err);
				standardResponse.setIsError(true);
				return standardResponseDocument;
			}

			if (!serviceType.equalsIgnoreCase("updateMr")) {
				standardResponse.setIsError(true);
				standardResponse.setError("Service type " + serviceType + " not configured");
				return standardResponseDocument;
			}

			QcLine qcLineArray[] = updateMrRequest.getQcLineArray();
			for (QcLine line : qcLineArray) {
				int mInoutLineId = line.getMRLineId();
				MInOutLine inoutLine = new MInOutLine(ctx, mInoutLineId, trx.getTrxName());
				inoutLine.setM_Locator_ID(line.getSuggestedLocator());
				inoutLine.saveEx();

				List<PO> piQrRelationsList = PiQrRelations.getPiQrRelationsData("minoutlineid",
						Integer.toString(mInoutLineId), ctx, trxName);
				PiQrRelations pr = new PiQrRelations(ctx, piQrRelationsList.get(0).get_ID(), trxName);
				PiQrRelations piQrRelations = new PiQrRelations(ctx, pr.get_ID(), trxName);
				piQrRelations.setlocatorid(line.getSuggestedLocator());
				piQrRelations.setisinlocator(1);
				piQrRelations.saveEx();

			}
			standardResponse.setIsError(false);
			trx.commit();
		} catch (Exception e) {
			standardResponse.setIsError(true);
			standardResponse.setError(e.getMessage());
			return standardResponseDocument;
		} finally {
			getCompiereService().disconnect();
			trx.close();
		}
		return standardResponseDocument;
	}

	@Override
	public WarehouseLocatorListResponseDocument wareList(
			WarehouseLocatorListRequestDocument warehouseLocatorListRequestDocument) {
		WarehouseLocatorListResponseDocument warehouseLocatorListResponseDocument = WarehouseLocatorListResponseDocument.Factory
				.newInstance();
		WarehouseLocatorListResponse warehouseLocatorListResponse = warehouseLocatorListResponseDocument
				.addNewWarehouseLocatorListResponse();
		WarehouseLocatorListRequest warehouseLocatorListRequest = warehouseLocatorListRequestDocument
				.getWarehouseLocatorListRequest();
		ADLoginRequest loginReq = warehouseLocatorListRequest.getADLoginRequest();
		int ad_client_id = loginReq.getClientID();
		String serviceType = warehouseLocatorListRequest.getServiceType();
		PreparedStatement pstm = null;
		ResultSet rs = null;

		Trx trx = null;
		try {

			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			getCompiereService().connect();

			org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = VeUtils.convertAdLogin(loginReq);
			String err = login(adLoginReq, webServiceName, "wareList", serviceType);
			if (err != null && err.length() > 0) {
				warehouseLocatorListResponse.setError(err);
				warehouseLocatorListResponse.setIsError(true);
				return warehouseLocatorListResponseDocument;
			}
			if (!serviceType.equalsIgnoreCase("wareList")) {
				warehouseLocatorListResponse.setIsError(true);
				warehouseLocatorListResponse.setError("Service type " + serviceType + " not configured");
				return warehouseLocatorListResponseDocument;
			}

			int userId = Env.getAD_User_ID(ctx);
			MUser_Custom user = new MUser_Custom(ctx, userId, trxName);
			X_PI_Deptartment userDepartment = new X_PI_Deptartment(ctx, user.getPI_DEPARTMENT_ID(), trxName);
			int userDepartmentId = userDepartment.get_ID();

			String sql = "SELECT \n" + "    w.m_warehouse_id as warehouseID,\n" + "    w.name as warehouseName,\n"
					+ "	ml.isdefault, lt.m_locatortype_id,\n" + "    (SELECT COUNT(*) FROM adempiere.M_Locator l\n"
					+ "     LEFT JOIN (\n" + "         SELECT M_Locator_ID ,COALESCE(SUM(QtyOnHand), 0) AS TotalQty\n"
					+ "         FROM adempiere.M_StorageOnHand\n" + "         GROUP BY M_Locator_ID\n"
					+ "     ) ms ON l.M_Locator_ID = ms.M_Locator_ID\n"
					+ "     WHERE l.m_warehouse_id = w.m_warehouse_id\n"
					+ "     AND COALESCE(ms.TotalQty, 0) = 0) AS emptyCount,\n"
					+ "    (SELECT COUNT(*) FROM adempiere.m_locator WHERE m_warehouse_id = w.m_warehouse_id) AS total_count,\n"
					+ "    lt.name AS locator_type,\n" + "    ml.value AS location_values,\n"
					+ "    ((SELECT COUNT(*) FROM adempiere.m_locator WHERE m_warehouse_id = w.m_warehouse_id) - \n"
					+ "     (SELECT COUNT(*) FROM adempiere.M_Locator l\n" + "      LEFT JOIN (\n"
					+ "          SELECT M_Locator_ID, COALESCE(SUM(QtyOnHand), 0) AS TotalQty\n"
					+ "          FROM adempiere.M_StorageOnHand\n" + "          GROUP BY M_Locator_ID\n"
					+ "      ) ms ON l.M_Locator_ID = ms.M_Locator_ID\n"
					+ "      WHERE l.m_warehouse_id = w.m_warehouse_id\n" + "      AND COALESCE(ms.TotalQty, 0) = 0) \n"
					+ "    ) * 100 / (SELECT COUNT(*) FROM adempiere.m_locator WHERE m_warehouse_id = w.m_warehouse_id) AS occupancy_percentage\n"
					+ "FROM \n" + "    adempiere.m_warehouse w\n" + "JOIN \n"
					+ "    adempiere.m_locator ml ON ml.m_warehouse_id = w.m_warehouse_id\n" + "JOIN \n"
					+ "    adempiere.m_locatortype lt ON ml.m_locatortype_id = lt.m_locatortype_id\n" + "WHERE \n"
					+ "    ml.ad_client_id = " + ad_client_id + "\n" + "GROUP BY \n"
					+ "    w.m_warehouse_id, w.name, lt.name,lt.m_locatortype_id, ml.value,ml.isdefault;";
			pstm = DB.prepareStatement(sql.toString(), null);
			rs = pstm.executeQuery();

			List<Integer> warehouseIds = new ArrayList<>();

			while (rs.next()) {
				int warehouseId = rs.getInt("warehouseID");
				String warehouseName = rs.getString("warehouseName");
				int occupancyPercents = rs.getInt("occupancy_percentage");
				String locatorType = rs.getString("locator_type");
				String locatorName = rs.getString("location_values");
				int locatorTypeId = rs.getInt("m_locatortype_id");

				MLocatorType_Custom mLocatorType = new MLocatorType_Custom(ctx, locatorTypeId, trxName);
				X_PI_Deptartment deptartment = mLocatorType.getPi_Department();
				int departmentId = deptartment.get_ID();

				if (departmentId != 0 && mLocatorType.isStorage() && userDepartmentId == departmentId) {
					if (!warehouseIds.contains(warehouseId)) {
						WarehouseListAccess warehouseListAccess = warehouseLocatorListResponse
								.addNewWarehouseListAccess();
						warehouseListAccess.setWarehouseId(warehouseId);
						warehouseListAccess.setWarehouseName(warehouseName);
						warehouseListAccess.setOccupancyPercentage(occupancyPercents);

						LocationType locationType = warehouseListAccess.addNewLocations();
						locationType.setLocatorTypeName(locatorType);

						Locators locators = locationType.addNewLocators();
						locators.setLocatorName(locatorName);
						locators.setIsOccupied(false);
						warehouseIds.add(warehouseId);
					} else {
						WarehouseListAccess[] warehouseListAccessArray = warehouseLocatorListResponse
								.getWarehouseListAccessArray();
						for (WarehouseListAccess wLAcess : warehouseListAccessArray) {
							if (wLAcess.getWarehouseId() == warehouseId) {
								boolean flag = false;
								LocationType[] LocationsArray = wLAcess.getLocationsArray();
								for (LocationType lType : LocationsArray) {
									if (lType.getLocatorTypeName().equals(locatorType)) {
										Locators locators = lType.addNewLocators();
										locators.setLocatorName(locatorName);
										locators.setIsOccupied(false);
										flag = true;
										break;
									}
								}
								if (flag == false) {
									LocationType locationType = wLAcess.addNewLocations();
									locationType.setLocatorTypeName(locatorType);
									Locators locators = locationType.addNewLocators();
									locators.setLocatorName(locatorName);
									locators.setIsOccupied(false);
								}
								break;
							}
						}
					}
				}
			}
			trx.commit();
		} catch (Exception e) {
			warehouseLocatorListResponse.setError(e.getLocalizedMessage());
			warehouseLocatorListResponse.setIsError(true);
			return warehouseLocatorListResponseDocument;
		} finally {
			closeDbCon(pstm, rs);
			getCompiereService().disconnect();
			trx.close();
		}
		return warehouseLocatorListResponseDocument;
	}

	@Override
	public GetMInoutForCOrderResponseDocument getMInoutForCOrder(GetMInoutForCOrderRequestDocument req) {
		GetMInoutForCOrderResponseDocument getMInoutForCOrderResponseDocument = GetMInoutForCOrderResponseDocument.Factory
				.newInstance();
		GetMInoutForCOrderResponse getMInoutForCOrderResponse = getMInoutForCOrderResponseDocument
				.addNewGetMInoutForCOrderResponse();
		GetMInoutForCOrderRequest getMInoutForCOrderRequest = req.getGetMInoutForCOrderRequest();
		ADLoginRequest loginReq = getMInoutForCOrderRequest.getADLoginRequest();
		String serviceType = getMInoutForCOrderRequest.getServiceType();
		int cOrderId = getMInoutForCOrderRequest.getCOrderId();
		int clientId = loginReq.getClientID();
		getCompiereService().connect();
		CompiereService m_cs = getCompiereService();
		Properties ctx = m_cs.getCtx();
		String trxName = Trx.createTrxName(getClass().getName() + "_");
		Trx trx = Trx.get(trxName, true);
		trx.start();
		org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = VeUtils.convertAdLogin(loginReq);
		String err = login(adLoginReq, webServiceName, "getMInoutForCOrder", serviceType);
		if (err != null && err.length() > 0) {
			getMInoutForCOrderResponse.setError(err);
			getMInoutForCOrderResponse.setIsError(true);
			return getMInoutForCOrderResponseDocument;
		}

		if (!serviceType.equalsIgnoreCase("getMInoutForCOrder")) {
			getMInoutForCOrderResponse.setIsError(true);
			getMInoutForCOrderResponse.setError("Service type " + serviceType + " not configured");
			return getMInoutForCOrderResponseDocument;
		}
		try {
			List<PO> poList = MInOut_Custom.getMInoutsByCOrderId(cOrderId, ctx, trxName);
			if (poList.isEmpty()) {
				getMInoutForCOrderResponse.setIsError(true);
				getMInoutForCOrderResponse.setError("Invalid cOrderId " + cOrderId + "");
				return getMInoutForCOrderResponseDocument;
			}
			for (PO po : poList) {
				MInOut mInOut = new MInOut(ctx, po.get_ID(), trxName);
				MRDataResponse listResponse = getMInoutForCOrderResponse.addNewMRDataResponse();
				listResponse = getMInoutDataByDocumentNumber(listResponse, mInOut.getDocumentNo(), clientId);
			}
			trx.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			trx.close();
			getCompiereService().disconnect();
		}

		return getMInoutForCOrderResponseDocument;
	}

	@Override
	public GenerateProductLabelResponseDocument generateProductLabel(GenerateProductLabelRequestDocument request) {
		GenerateProductLabelRequest generateProductLabelRequest = request.getGenerateProductLabelRequest();
		GenerateProductLabelResponseDocument response = GenerateProductLabelResponseDocument.Factory.newInstance();
		GenerateProductLabelResponse generateProductLabelResponse = response.addNewGenerateProductLabelResponse();
		boolean isManufacturing = generateProductLabelRequest.getIsManufacturing();
		boolean isAssembly = generateProductLabelRequest.getIsAssembly();
		ADLoginRequest loginReq = generateProductLabelRequest.getADLoginRequest();
		boolean isLightProduction = generateProductLabelRequest.getIsLightProduction();
		String serviceType = generateProductLabelRequest.getServiceType();
		Trx trx = null;
		int locatorId = 0;
		String palletUUId = null;
		try {
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = VeUtils.convertAdLogin(loginReq);
			String err = login(adLoginReq, webServiceName, "generateProductLabel", serviceType);
			if (err != null && err.length() > 0) {
				generateProductLabelResponse.setError(err);
				generateProductLabelResponse.setIsError(true);
				return response;
			}
			if (!serviceType.equalsIgnoreCase("generateProductLabel")) {
				generateProductLabelResponse.setIsError(true);
				generateProductLabelResponse.setError("Service type " + serviceType + " not configured");
				return response;
			}
			ProductLabelLine[] productLabelLine = generateProductLabelRequest.getProductLabelLineArray();
			for (ProductLabelLine line : productLabelLine) {

				if (generateProductLabelRequest.getFinalDispatch()) {
					createSalesLabel(loginReq, line, ctx, trxName, generateProductLabelRequest.getMInoutId());
				} else {
					PiProductLabel piProductLabel = null;
					if (isManufacturing || isAssembly || isLightProduction) {
						piProductLabel = new PiProductLabel(ctx, trxName, loginReq, line, true, isManufacturing,
								isAssembly, isLightProduction);
						piProductLabel.saveEx();
					} else {
						piProductLabel = new PiProductLabel(ctx, trxName, loginReq, line, true);
						piProductLabel.saveEx();
						if (locatorId == 0)
							locatorId = piProductLabel.getM_Locator_ID();
						if (palletUUId == null)
							palletUUId = piProductLabel.getLabelUUID();
					}
					line.setLabelId(piProductLabel.get_ID());
					line.setProductName(piProductLabel.getM_Product().getName());
					line.setProductLabelUUId(piProductLabel.getLabelUUID());
				}
			}
			generateProductLabelResponse.setProductLabelLineArray(productLabelLine);
			trx.commit();
			generateProductLabelResponse.setIsError(false);

		} catch (Exception e) {
			generateProductLabelResponse.setError(e.getMessage());
			generateProductLabelResponse.setIsError(true);
			return response;
		} finally {
			trx.close();
			getCompiereService().disconnect();
		}
		return response;
	}

	private void createSalesLabel(ADLoginRequest loginReq, ProductLabelLine line, Properties ctx, String trxName,
			int mInoutId) {

		PO po =PiProductLabel.getPiProductLabelByUUID("LabelUUID", line.getProductLabelUUId(), ctx, trxName, false, loginReq.getOrgID());
		MInOut_Custom inOut = new MInOut_Custom(ctx, mInoutId, trxName);
		MInOutLine[] inoutLines = inOut.getLines();
		PiProductLabel piProductLabel = new PiProductLabel(ctx, 0, trxName);
		piProductLabel.setQcpassed(true);
		if (line.getProductId() != 0) {
			piProductLabel.setQuantity(BigDecimal.valueOf(line.getQuantity()));
			piProductLabel.setM_Product_ID(line.getProductId());
			for (MInOutLine inoutLine : inoutLines) {
				if (inoutLine.getM_Product_ID() == line.getProductId()) {
					piProductLabel.setM_InOutLine_ID(inoutLine.get_ID());
					piProductLabel.setC_OrderLine_ID(inoutLine.getC_OrderLine_ID());
					break;
				}
			}
		}
		piProductLabel.setM_Locator_ID(line.getLocatorId());
		piProductLabel.setIsSOTrx(true);
		piProductLabel.setLabelUUID(line.getProductLabelUUId());
		piProductLabel.setIsActive(true);
		piProductLabel.saveEx();

		List<PO> childList = PiProductLabel.getPiProductLabel("parentLabel", po.get_ID(), ctx, trxName,
				false, loginReq.getClientID(), loginReq.getOrgID());
		if (childList != null && childList.size() != 0)
			for (PO child : childList) {
				PiProductLabel cExistingLabel = new PiProductLabel(ctx, child.get_ID(), trxName);
				PiProductLabel cLabel = new PiProductLabel(ctx, 0, trxName);
				cLabel.setQcpassed(true);
				if (cExistingLabel.getM_Product_ID() != 0) {
					cLabel.setQuantity(cExistingLabel.getQuantity());
					cLabel.setM_Product_ID(cExistingLabel.getM_Product_ID());
					for (MInOutLine inoutLine : inoutLines) {
						if (inoutLine.getM_Product_ID() == cExistingLabel.getM_Product_ID()) {
							piProductLabel.setM_InOutLine_ID(inoutLine.get_ID());
							piProductLabel.setC_OrderLine_ID(inoutLine.getC_OrderLine_ID());
							break;
						}
					}
				}
				cLabel.setM_Locator_ID(cExistingLabel.getM_Locator_ID());
				cLabel.setIsSOTrx(true);
				cLabel.setLabelUUID(cExistingLabel.getLabelUUID());
				cLabel.setIsActive(true);
				cLabel.saveEx();

				List<PO> superChildList = PiProductLabel.getPiProductLabel("parentLabel", cExistingLabel.get_ID(), ctx,
						trxName, false,  loginReq.getClientID(), loginReq.getOrgID());
				if (superChildList != null && superChildList.size() != 0)
					for (PO sChild : superChildList) {
						PiProductLabel sExistingLabel = new PiProductLabel(ctx, sChild.get_ID(), trxName);
						PiProductLabel sLabel = new PiProductLabel(ctx, 0, trxName);
						sLabel.setQcpassed(true);
						sLabel.setQuantity(sExistingLabel.getQuantity());
						sLabel.setM_Product_ID(sExistingLabel.getM_Product_ID());
						sLabel.setM_Locator_ID(sExistingLabel.getM_Locator_ID());
						sLabel.setIsSOTrx(true);
						sLabel.setLabelUUID(sExistingLabel.getLabelUUID());
						sLabel.setIsActive(true);
						for (MInOutLine inoutLine : inoutLines) {
							if (inoutLine.getM_Product_ID() ==sExistingLabel.getM_Product_ID()) {
								piProductLabel.setM_InOutLine_ID(inoutLine.get_ID());
								piProductLabel.setC_OrderLine_ID(inoutLine.getC_OrderLine_ID());
								break;
							}
						}
						sLabel.saveEx();
					}
			}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Response getLabelData(GetLabelDataRequestDocument request) {
		GetLabelDataRequest getLabelDataRequest = request.getGetLabelDataRequest();
		String labelUUID = getLabelDataRequest.getLabelUUID();
		boolean isPutawy = getLabelDataRequest.getIsPutAway();
		boolean receiving = getLabelDataRequest.getReceiving();
		boolean pickList = getLabelDataRequest.getPickList();
		boolean finalDispatch = getLabelDataRequest.getFinalDispatch();
		boolean internalMove = getLabelDataRequest.getInternalMove();
		boolean isFgReceiving = getLabelDataRequest.getIsFgReceiving();
		boolean isLinkedToParent = getLabelDataRequest.getIsLinkedToParent();

		JSONObject resp = new JSONObject();
		JSONObject jsonObject = new JSONObject();
		
		ADLoginRequest loginReq = getLabelDataRequest.getADLoginRequest();
		String serviceType = getLabelDataRequest.getServiceType();
		Trx trx = null;
		try {
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();

			org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = VeUtils.convertAdLogin(loginReq);
			String err = login(adLoginReq, webServiceName, "getLabelData", serviceType);
			if (err != null && err.length() > 0) {
				jsonObject.put("Error", "Invalid Login Credentials");
				jsonObject.put("@IsError", true);
				resp.put("GetLabelDataResponse", jsonObject);
				return Response.ok(resp.toJSONString()).type(MediaType.APPLICATION_JSON).build();
			}
			if (!serviceType.equalsIgnoreCase("getLabelData")) {
				jsonObject.put("Error", "Service type " + serviceType + " not configured");
				jsonObject.put("@IsError", true);
				resp.put("GetLabelDataResponse", jsonObject);
				return Response.ok(resp.toJSONString()).type(MediaType.APPLICATION_JSON).build();
			}

			String columnName = "labelUUId";
			List<PO> poList = PiProductLabel.getPiProductLabel(columnName, labelUUID, ctx, trxName, null,  loginReq.getClientID(), loginReq.getOrgID());
			if (poList.isEmpty()) {
				jsonObject.put("Error", "Invalid label");
				jsonObject.put("@IsError", true);
				resp.put("GetLabelDataResponse", jsonObject);
				return Response.ok(resp.toJSONString()).type(MediaType.APPLICATION_JSON).build();
			}
			int labelCount = 0;

			int userId = Env.getAD_User_ID(ctx);
			MUser_Custom user = new MUser_Custom(ctx, userId, trxName);
			X_PI_Deptartment userDepartment = new X_PI_Deptartment(ctx, user.getPI_DEPARTMENT_ID(), trxName);
			int userDepartmentId = userDepartment.get_ID();
			
			for (PO po : poList) {
				PiProductLabel label = new PiProductLabel(ctx, po.get_ID(), trxName);
				
				if (label.isLabelDiscarded()) {
					jsonObject.put("Error", "Label is Discarded");
					jsonObject.put("@IsError", true);
					resp.put("GetLabelDataResponse", jsonObject);
					return Response.ok(resp.toJSONString()).type(MediaType.APPLICATION_JSON).build();
				}
				
				I_M_InOut mInout = label.getM_InOutLine().getM_InOut();
				if (isPutawy && !mInout.getDocStatus().equalsIgnoreCase("CO")) {
					jsonObject.put("Error", "Material recipt is not completed, cant do putaway");
					jsonObject.put("@IsError", true);
					resp.put("GetLabelDataResponse", jsonObject);
					return Response.ok(resp.toJSONString()).type(MediaType.APPLICATION_JSON).build();
				}

				MLocatorType_Custom locatorType_Custom = new MLocatorType_Custom(ctx,
						label.getM_Locator().getM_LocatorType_ID(), trxName);
				if (receiving && !locatorType_Custom.isReceiving()) {
					jsonObject.put("Error", "Label is Not in Receving Area");
					jsonObject.put("@IsError", true);
					resp.put("GetLabelDataResponse", jsonObject);
					return Response.ok(resp.toJSONString()).type(MediaType.APPLICATION_JSON).build();
				}

				if ((receiving || pickList || internalMove) && label.isSOTrx()) {
					jsonObject.put("Error", "Label is Already Dispatched");
					jsonObject.put("@IsError", true);
					resp.put("GetLabelDataResponse", jsonObject);
					return Response.ok(resp.toJSONString()).type(MediaType.APPLICATION_JSON).build();
				}

				if (pickList && (locatorType_Custom.isdispatch() || locatorType_Custom.isReturns())) {
					jsonObject.put("Error", "Label is in Dispatch/ Returns Area");
					jsonObject.put("@IsError", true);
					resp.put("GetLabelDataResponse", jsonObject);
					return Response.ok(resp.toJSONString()).type(MediaType.APPLICATION_JSON).build();
				}

				if (finalDispatch && label.isSOTrx()) {
					jsonObject.put("Error", "Invalid Label");
					jsonObject.put("@IsError", true);
					resp.put("GetLabelDataResponse", jsonObject);
					return Response.ok(resp.toJSONString()).type(MediaType.APPLICATION_JSON).build();
				}
				
				if (finalDispatch && !label.isfinaldispatch()) {
					jsonObject.put("Error", "Label is not in Dispatch Area");
					jsonObject.put("@IsError", true);
					resp.put("GetLabelDataResponse", jsonObject);
					return Response.ok(resp.toJSONString()).type(MediaType.APPLICATION_JSON).build();
				}
				
				if (isFgReceiving && locatorType_Custom.getPI_DEPARTMENT_ID() == userDepartmentId) {
					jsonObject.put("Error", "Label is Already in FG Receiving");
					jsonObject.put("@IsError", true);
					resp.put("GetLabelDataResponse", jsonObject);
					return Response.ok(resp.toJSONString()).type(MediaType.APPLICATION_JSON).build();
				}
				
				if ((isPutawy || receiving || pickList || internalMove || finalDispatch || isFgReceiving || isLinkedToParent) && label.getparentlabel() != 0) {
					jsonObject.put("Error", "Label is Linked to Parent");
					jsonObject.put("@IsError", true);
					resp.put("GetLabelDataResponse", jsonObject);
					return Response.ok(resp.toJSONString()).type(MediaType.APPLICATION_JSON).build();
				}
			}
			
			int qnty = 0;
			JSONObject respList = setProductLabel(poList, ctx, trxName,  loginReq.getClientID(), loginReq.getOrgID());
			List<JSONObject> child = (List<JSONObject>) respList.get("labelLine");
			qnty += (int)respList.get("quantity");
			
			jsonObject.put("labelLine", child);
			
			jsonObject.put("@IsError", false);

			jsonObject.put("productCount", labelCount);
			jsonObject.put("quantity", qnty);
			jsonObject.put("labelUUID", labelUUID);
			trx.commit();

		} catch (Exception e) {
				jsonObject = new JSONObject();
				jsonObject.put("Error", e.getMessage());
				jsonObject.put("@IsError", true);
				resp.put("GetLabelDataResponse", jsonObject);

			return Response.ok(resp.toJSONString()).type(MediaType.APPLICATION_JSON).build();

		} finally {
			trx.close();
			getCompiereService().disconnect();
		}

		resp.put("GetLabelDataResponse", jsonObject);
		return Response.ok(resp.toJSONString()).type(MediaType.APPLICATION_JSON).build();
	}

	@SuppressWarnings("unchecked")
	private JSONObject setProductLabel(List<PO> poList, Properties ctx, String trxName, int clientId, int orgId) {

		int quantity = 0;
		JSONObject resp = new JSONObject();
		List<JSONObject> respList = new ArrayList<JSONObject>();
		for (PO po : poList) {
			try {
				JSONObject jsonObject = new JSONObject();
				PiProductLabel label = new PiProductLabel(ctx, po.get_ID(), trxName);
				jsonObject.put("labelId", label.getpi_productLabel_ID() == 0 ? 0 : label.getpi_productLabel_ID());

				jsonObject.put("productLabelUUId", label.getLabelUUID() == null ? "" : label.getLabelUUID() + "");
				jsonObject.put("cOrderlineId", label.getC_OrderLine_ID() == 0 ? 0 : label.getC_OrderLine_ID());
				jsonObject.put("mInoutlineId", label.getM_InOutLine_ID() == 0 ? 0 : label.getM_InOutLine_ID());
				jsonObject.put("productId", label.getM_Product_ID() == 0 ? 0 : label.getM_Product_ID());
				jsonObject.put("productName",
						label.getM_Product().getName() == null ? "" : label.getM_Product().getName());
				jsonObject.put("locatorId", label.getM_Locator_ID() == 0 ? 0 : label.getM_Locator_ID());

				jsonObject.put("locatorName",
						label.getM_Locator().getValue() == null ? "" : label.getM_Locator().getValue());

				jsonObject.put("quantity", label.getQuantity().intValue() == 0 ? 0 : label.getQuantity().intValue());
				jsonObject.put("isSalesTransaction", label.isSOTrx());
				jsonObject.put("qcPassed", label.qcpassed());
				jsonObject.put("warehouseId",
						label.getM_Locator().getM_Warehouse_ID() == 0 ? 0 : label.getM_Locator().getM_Warehouse_ID());
				jsonObject.put("warehouseName", label.getM_Locator().getM_Warehouse().getName() == null ? ""
						: label.getM_Locator().getM_Warehouse().getName());
				jsonObject.put("labelTypeId", label.getpi_labeltype_ID() == 0 ? 0 : label.getpi_labeltype_ID());
				jsonObject.put("labelType", "product");
				jsonObject.put("finalDispatch", label.isfinaldispatch());
				jsonObject.put("parentLabel", label.getparentlabel() == 0 ? 0 : label.getparentlabel());

//				quantity +=  label.getQuantity().intValue();
//				If we want show only actual product quantity show added below condition ...
				if (label.getM_Product_ID() != 0) {
				    quantity += label.getQuantity().intValue();
				}
				
				List<PO> childList = PiProductLabel.getPiProductLabel("parentLabel", label.getpi_productLabel_ID(), ctx,
						trxName, null,  clientId, orgId);
				if (childList != null && !childList.isEmpty()) {
					JSONObject childObject = setProductLabel(childList, ctx, trxName, clientId, orgId);
					List<JSONObject> child = (List<JSONObject>) childObject.get("labelLine");
					quantity += (int)childObject.get("quantity");
					jsonObject.put("labelLine", child);
				}
				respList.add(jsonObject);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		resp.put("labelLine", respList);
		resp.put("quantity", quantity);
		return resp;
	}

	@Override
	public StandardResponseDocument putAway(PutAwayRequestDocument request) {

		StandardResponseDocument standardResponseDocument = StandardResponseDocument.Factory.newInstance();
		StandardResponse standardResponse = standardResponseDocument.addNewStandardResponse();
		PutAwayRequest putAwayRequest = request.getPutAwayRequest();
		ADLoginRequest loginReq = putAwayRequest.getADLoginRequest();
		String serviceType = putAwayRequest.getServiceType();
		Trx trx = null;
		try {
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			getCompiereService().connect();
			org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = VeUtils.convertAdLogin(loginReq);
			String err = login(adLoginReq, webServiceName, "putAway", serviceType);
			if (err != null && err.length() > 0) {
				standardResponse.setError(err);
				standardResponse.setIsError(true);
				return standardResponseDocument;
			}

			if (!serviceType.equalsIgnoreCase("putAway")) {
				standardResponse.setIsError(true);
				standardResponse.setError("Service type " + serviceType + " not configured");
				return standardResponseDocument;
			}

			if(putAwayRequest.getPutAwayLineArray() == null || putAwayRequest.getPutAwayLineArray().length == 0) {
				standardResponse.setIsError(true);
				standardResponse.setError("No lines Available");
				return standardResponseDocument;
				
			}
			
			putAwayLabels(putAwayRequest.getPutAwayLineArray(), trxName, ctx, loginReq.getClientID(),
					loginReq.getOrgID(), putAwayRequest.getIsFgReceiving(), putAwayRequest.getMouldingAssemblyReceving(), putAwayRequest.getFinalDispatch(), loginReq.getWarehouseID());
			standardResponse.setIsError(false);
			trx.commit();
		} catch (Exception e) {
			if (trx != null)
				trx.rollback();
			standardResponse.setIsError(true);
			standardResponse.setError(e.getMessage());
			return standardResponseDocument;
		} finally {
			getCompiereService().disconnect();
			trx.close();
		}
		return standardResponseDocument;
	}

	private void putAwayLabels(PutAwayLine[] putAwayLineArray, String trxName, Properties ctx, int clientId, int orgId,
			boolean isFgReceiving, boolean mouldingAssemblyReceving, boolean finalDispatch, int warehouseId) throws Exception{

		MMovement mMovement = new MMovement(ctx, 0, trxName);
		mMovement.setIsActive(true);
		mMovement.setDescription(null);
		mMovement.setApprovalAmt(BigDecimal.valueOf(0));
		mMovement.setChargeAmt(BigDecimal.valueOf(0));
		mMovement.setFreightAmt(BigDecimal.valueOf(0));

		int userId = Env.getAD_User_ID(ctx);
		MUser_Custom user = new MUser_Custom(ctx, userId, trxName);
		X_PI_Deptartment userDepartment = new X_PI_Deptartment(ctx, user.getPI_DEPARTMENT_ID(), trxName);
		int userDepartmentId = userDepartment.get_ID();

		String columnName = "labelUUId";
		int mWarehouseId = 0;
		int locatorId = 0;
		for (PutAwayLine line : putAwayLineArray) {
			int toLocatorId = line.getLocatorId();
			if (toLocatorId == 0)
				toLocatorId = locatorId;
			if (toLocatorId == 0 && (mouldingAssemblyReceving || isFgReceiving)) {

				List<MLocator> mLocatorArray = VeUtils.getLocatorsByDepartment(ctx, trxName, warehouseId,
						userDepartmentId, "receiving", "Y");
				for (MLocator locatroList : mLocatorArray) {
					toLocatorId = locatroList.getM_Locator_ID();
					locatorId = locatroList.getM_Locator_ID();
					break;
				}
			}

			List<PO> poList = PiProductLabel.getPiProductLabel(columnName, line.getProductLabelUUId(), ctx, trxName,
					false, clientId, orgId);
			if (!poList.isEmpty()) {
				PiProductLabel piProductLabel = new PiProductLabel(ctx, poList.get(0).get_ID(), trxName);
				
				if (piProductLabel.isLabelDiscarded()) {
					throw new Exception("Label is Discarded");
				}
				
				if (mWarehouseId == 0) {

					if (piProductLabel.getM_InOutLine_ID() != 0)
						mWarehouseId = piProductLabel.getM_InOutLine().getM_Locator().getM_Warehouse_ID();
					else
						mWarehouseId = piProductLabel.getPP_Order().getM_Warehouse_ID();
					mMovement.setM_Warehouse_ID(mWarehouseId);
					mMovement.setM_WarehouseTo_ID(mWarehouseId);
					mMovement.saveEx();
				}
	
				moveLabels(trxName, ctx, clientId, orgId, piProductLabel.get_ID(), isFgReceiving, finalDispatch, mMovement,
						toLocatorId);
			}

		}

		mMovement.setDocStatus(DocAction.ACTION_Complete);
		mMovement.setDocAction(DocAction.ACTION_Close);
		mMovement.setPosted(true);
		mMovement.setProcessed(true);
		mMovement.setIsApproved(true);
		mMovement.completeIt();
		mMovement.saveEx();
	}

	private void moveLabels(String trxName, Properties ctx, int clientId, int orgId, int labelId, boolean isFgReceiving,
			boolean finalDispatch, MMovement mMovement, int toLocatorId) {
		PiProductLabel piProductLabel = new PiProductLabel(ctx, labelId, trxName);
		if (piProductLabel.getM_Product_ID() != 0) {

			if (isFgReceiving)
				VeUtils.addOrReduceInventory(new MProduct_Custom(ctx, piProductLabel.getM_Product_ID(), trxName),
						piProductLabel.getQuantity().intValue(), piProductLabel.getM_Locator_ID(),
						piProductLabel.getAD_Org_ID(), ctx, trxName, false);
			else if (!finalDispatch || (finalDispatch && piProductLabel.getM_Locator_ID() != toLocatorId))
				moveInventory(ctx, trxName, mMovement, piProductLabel.getC_OrderLine_ID(), piProductLabel.getQuantity(),
						piProductLabel.getM_Product_ID(), piProductLabel.getM_Locator_ID(), toLocatorId, clientId,
						orgId);
		}

		piProductLabel.setfinaldispatch(finalDispatch);
		piProductLabel.setM_Locator_ID(toLocatorId);
		piProductLabel.saveEx();

		List<PO> childList = PiProductLabel.getPiProductLabel("parentLabel", piProductLabel.getpi_productLabel_ID(),
				ctx, trxName, false, clientId, orgId);
		if (childList != null && !childList.isEmpty() && piProductLabel.getM_Product_ID() == 0)
			for (PO po : childList) {
				PiProductLabel clabel = new PiProductLabel(ctx, po.get_ID(), trxName);
				moveLabels(trxName, ctx, clientId, orgId, clabel.get_ID(), isFgReceiving, finalDispatch, mMovement, toLocatorId);
			}
	}

	@Override
	public LocatorDeatilResponseDocument getLocatorDeatilsById(LocatorDeatilRequestDocument request) {

		LocatorDeatilResponseDocument locatorDeatilResponseDocument = LocatorDeatilResponseDocument.Factory
				.newInstance();
		LocatorDeatilResponse locatorDeatilResponse = locatorDeatilResponseDocument.addNewLocatorDeatilResponse();
		LocatorDeatilRequest locatorDeatilRequest = request.getLocatorDeatilRequest();
		ADLoginRequest loginReq = locatorDeatilRequest.getADLoginRequest();
		String serviceType = locatorDeatilRequest.getServiceType();
		int locatorId = locatorDeatilRequest.getLocatorId();
		Trx trx = null;
		try {
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			getCompiereService().connect();
			org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = VeUtils.convertAdLogin(loginReq);
			String err = login(adLoginReq, webServiceName, "getLocatorDeatilsById", serviceType);
			if (err != null && err.length() > 0) {
				locatorDeatilResponse.setError(err);
				locatorDeatilResponse.setIsError(true);
				return locatorDeatilResponseDocument;
			}

			if (!serviceType.equalsIgnoreCase("getLocatorDeatilsById")) {
				locatorDeatilResponse.setIsError(true);
				locatorDeatilResponse.setError("Service type " + serviceType + " not configured");
				return locatorDeatilResponseDocument;
			}
			MLocator mLocator = new MLocator(ctx, locatorDeatilRequest.getLocatorId(), trxName);
			if (mLocator.getM_Locator_ID() == 0) {
				locatorDeatilResponse.setIsError(true);
				locatorDeatilResponse.setError("Invalid Locator ID " + locatorId + "");
				return locatorDeatilResponseDocument;
			}
			locatorDeatilResponse.setLocatorId(mLocator.getM_Locator_ID());
			locatorDeatilResponse.setLocatorName(mLocator.getValue());
			locatorDeatilResponse.setAisle(mLocator.getX());
			locatorDeatilResponse.setLevel(mLocator.getY());
			locatorDeatilResponse.setBin(mLocator.getZ());
			locatorDeatilResponse.setLocatorTypeId(mLocator.getM_LocatorType_ID());
			locatorDeatilResponse.setLocatorType(
					mLocator.getM_LocatorType() == null || mLocator.getM_LocatorType().getM_LocatorType_ID() == 0 ? ""
							: VeUtils.getLocatorTypeName(ctx, trxName, mLocator.getM_LocatorType_ID()));
			locatorDeatilResponse.setWarehouseId(mLocator.getM_Warehouse_ID());
			locatorDeatilResponse.setWarehouseName(mLocator.getWarehouseName());
			MLocatorType_Custom ltype = new MLocatorType_Custom(ctx, mLocator.getM_LocatorType_ID(), trxName);
			locatorDeatilResponse.setDeptartmentId(ltype.getPI_DEPARTMENT_ID());
			if (ltype.getPI_DEPARTMENT_ID() == 0)
				locatorDeatilResponse.setDeptartmentName("");
			else
				locatorDeatilResponse.setDeptartmentName(ltype.getPi_Department().getdeptname());
			locatorDeatilResponse.setIsError(false);
			trx.commit();
		} catch (Exception e) {
			locatorDeatilResponse.setIsError(true);
			locatorDeatilResponse.setError(e.getMessage());
			return locatorDeatilResponseDocument;
		} finally {
			getCompiereService().disconnect();
			trx.close();
		}
		return locatorDeatilResponseDocument;
	}

	@Override
	public PutAwayListResponseDocument putAwayList(PutAwayListRequestDocument request) {

		PutAwayListResponseDocument response = PutAwayListResponseDocument.Factory.newInstance();
		PutAwayListResponse putAwayListResponse = response.addNewPutAwayListResponse();
		PutAwayListRequest putAwayListRequest = request.getPutAwayListRequest();
		ADLoginRequest loginReq = putAwayListRequest.getADLoginRequest();
		String serviceType = putAwayListRequest.getServiceType();
		String searchKey = putAwayListRequest.getSearchKey();
		Trx trx = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		
		int pageSize = putAwayListRequest.getPageSize(); // Number of records per page
		int pageNumber = putAwayListRequest.getPageNumber(); // Current page number
		
		try {
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			getCompiereService().connect();
			org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = VeUtils.convertAdLogin(loginReq);
			String err = login(adLoginReq, webServiceName, "putAwayList", serviceType);
			if (err != null && err.length() > 0) {
				putAwayListResponse.setError(err);
				putAwayListResponse.setIsError(true);
				return response;
			}

			if (!serviceType.equalsIgnoreCase("putAwayList")) {
				putAwayListResponse.setIsError(true);
				putAwayListResponse.setError("Service type " + serviceType + " not configured");
				return response;
			}

			int userId = Env.getAD_User_ID(ctx);
			MUser_Custom user = new MUser_Custom(ctx, userId, trxName);
			X_PI_Deptartment dept = new X_PI_Deptartment(ctx, user.getPI_DEPARTMENT_ID(), trxName);

			List<Integer> orgList = new ArrayList<>();
			Login login = new Login(m_cs.getCtx());
			KeyNamePair[] orgs = login.getOrgs(new KeyNamePair(loginReq.getRoleID(), ""));
			if (orgs != null) {
				for (KeyNamePair org : orgs) {
					orgList.add(Integer.valueOf(org.getID()));
				}
			}
			String orgIds = orgList.stream().map(Object::toString).collect(Collectors.joining(", "));

			StringBuilder query = new StringBuilder("SELECT\n" + "    DISTINCT(po.documentno) AS documentNo,\n" + "    bp.name AS Supplier,\n"
					+ "    po.m_inout_id AS mInoutID,\n"
					+ "    TO_CHAR(po.dateordered, 'DD/MM/YYYY') AS Order_Date, co.pi_deptartment_ID,\n"
					+ "    wh.name AS Warehouse_Name,\n" + "    po.description, po.created, po.m_inout_id,\n"
					+ "	co.documentno as orderDocumentno\n" + "FROM m_inout po\n"
					+ "JOIN c_bpartner bp ON po.c_bpartner_id = bp.c_bpartner_id \n"
					+ "JOIN c_order co on co.c_order_id = po.c_order_id\n"
					+ "JOIN m_warehouse wh ON po.m_warehouse_id = wh.m_warehouse_id\n" + "WHERE po.ad_client_id = "
					+ loginReq.getClientID() + " AND po.issotrx = 'N' AND po.docstatus = 'DR' AND po.ad_org_id IN ("
					+ orgIds + ") AND po.ad_orgtrx_id is null " + "AND (\n"
					+ "    po.documentno ILIKE '%' || COALESCE(?, po.documentno) || '%'\n"
					+ "    OR bp.name ILIKE '%' || COALESCE(?, bp.name) || '%'\n"
					+ "    OR wh.name ILIKE '%' || COALESCE(?, wh.name) || '%'\n"
					+ "    OR po.description ILIKE '%' || COALESCE(?, po.description) || '%'\n"
					+ "    OR co.documentno ILIKE '%' || COALESCE(?, co.documentno) || '%'\n" + ")\n"
					+ " ORDER BY po.created desc");
			
						if (pageSize <= 0 || pageNumber <= 0) {

						} else {
							int offset = (pageNumber - 1) * pageSize;
							query.append(" LIMIT " + pageSize + " OFFSET " + offset + "");
						}

			pstm = DB.prepareStatement(query.toString(), null);
			pstm.setString(1, searchKey);
			pstm.setString(2, searchKey);
			pstm.setString(3, searchKey);
			pstm.setString(4, searchKey);
			pstm.setString(5, searchKey);
			rs = pstm.executeQuery();

			int count = 0;
			while (rs.next()) {
				String documentNo = rs.getString("documentNo");
				int mInoutID = rs.getInt("mInoutID");
				String supplier = rs.getString("Supplier");
				String date = rs.getString("Order_Date");
				String orderDocumentno = rs.getString("orderDocumentno");
				String warehouseName = rs.getString("Warehouse_Name");
				String description = rs.getString("description");
				String deptId = rs.getString("pi_deptartment_ID");

				System.out.println(dept.getPI_Deptartment_ID());
				if (deptId == null || (deptId != null && dept.getPI_Deptartment_ID() == Integer.valueOf(deptId))) {
					PutAwayList putAwayList = putAwayListResponse.addNewPutAwayList();
					putAwayList.setDocumentNo(documentNo);
					putAwayList.setMInoutID(mInoutID);
					putAwayList.setSupplier(supplier);
					putAwayList.setOrderDate(date);
					putAwayList.setWarehouseName(warehouseName);
					putAwayList.setOrderDocumentno(orderDocumentno);
					putAwayList.setToMarkForPutAway(true);
					if (description == null)
						putAwayList.setDescription("");
					else
						putAwayList.setDescription(description);
					count++;
				}
			}

			List<PO> poList = new ArrayList<PO>();
			List<MLocator> mLocatorArray = VeUtils.getLocatorsByDepartment(ctx, trxName, loginReq.getWarehouseID(),
					dept.getPI_Deptartment_ID(), "receiving", "Y");

			if (mLocatorArray.size() != 0) {
				for (MLocator locator : mLocatorArray) {
					poList.addAll(PiProductLabel.getPiProductLabel("m_locator_ID", locator.getM_Locator_ID(), ctx,
							trxName, false,  loginReq.getClientID(), loginReq.getOrgID()));
				}
			} else {
				putAwayListResponse.setIsError(true);
				putAwayListResponse.setError("Receiving Locator Type Not Found");
				return response;
			}
			for (PO po : poList) {
				PiProductLabel piProductLabel = new PiProductLabel(ctx, po.get_ID(), trxName);
				I_M_InOut mInout = piProductLabel.getM_InOutLine().getM_InOut();
				COrder_Custom cOrder = new COrder_Custom(ctx, mInout.getC_Order_ID(), trxName);
				int deptId = cOrder.getpidepartmentID();
				if (deptId != 0 && (deptId != 0 && dept.getPI_Deptartment_ID() != Integer.valueOf(deptId)))
					continue;

				if (mInout.getDocStatus().equalsIgnoreCase("CO")) {

					if (searchKey != null) {
						if (!mInout.getDocumentNo().toLowerCase().contains(searchKey.toLowerCase())
								&& !mInout.getC_BPartner().getName().toLowerCase().contains(searchKey.toLowerCase())
								&& !mInout.getM_Warehouse().getValue().toLowerCase().contains(searchKey.toLowerCase())
								&& !(mInout.getDescription() != null && mInout.getDescription() != ""
										&& mInout.getDescription().toLowerCase().contains(searchKey.toLowerCase()))
								&& !mInout.getC_Order().getDocumentNo().toLowerCase()
										.contains(searchKey.toLowerCase())) {
							continue;
						}
					}

					PutAwayList[] list = putAwayListResponse.getPutAwayListArray();
					boolean flag = false;
					for (PutAwayList pal : list) {
						if (pal.getMInoutID() == mInout.getM_InOut_ID()) {
							pal.setQuantityToPick(pal.getQuantityToPick() + piProductLabel.getQuantity().intValue());
							flag = true;
//							count++;
							break;
						}
					}
					if (!flag) {
						PutAwayList putAwayList = putAwayListResponse.addNewPutAwayList();
						putAwayList.setDocumentNo(mInout.getDocumentNo());
						putAwayList.setMInoutID(mInout.getM_InOut_ID());
						putAwayList.setSupplier(mInout.getC_BPartner().getName());
						putAwayList.setOrderDate(mInout.getDateOrdered().toString());
						putAwayList.setWarehouseId(mInout.getM_Warehouse_ID());
						putAwayList.setWarehouseName(mInout.getM_Warehouse().getValue());
						putAwayList.setOrderDocumentno(mInout.getC_Order().getDocumentNo());
						putAwayList.setToMarkForPutAway(false);
						putAwayList.setQuantityToPick(piProductLabel.getQuantity().intValue());
						int totalQuantity = MInOut_Custom.getTotalQuantityForMInout(ctx, trxName,
								mInout.getM_InOut_ID());
						putAwayList.setTotalQuantity(totalQuantity);
						if (mInout.getDescription() == null)
							putAwayList.setDescription("");
						else
							putAwayList.setDescription(mInout.getDescription());
						count++;
					}
				}

			}
			putAwayListResponse.setCount(count);
			putAwayListResponse.setIsError(false);
			trx.commit();
		} catch (Exception e) {
			putAwayListResponse.setIsError(true);
			putAwayListResponse.setError(e.getMessage());
			return response;
		} finally {
			closeDbCon(pstm, rs);
			getCompiereService().disconnect();
			trx.close();
		}
		return response;
	}

	@Override
	public PutAwayLabourResponseDocument putAwayLabour(PutAwayLabourRequestDocument request) {

		PutAwayLabourResponseDocument response = PutAwayLabourResponseDocument.Factory.newInstance();
		PutAwayLabourResponse putAwayLabourResponse = response.addNewPutAwayLabourResponse();
		PutAwayLabourRequest putAwayLabourRequest = request.getPutAwayLabourRequest();
		ADLoginRequest loginReq = putAwayLabourRequest.getADLoginRequest();
		String serviceType = putAwayLabourRequest.getServiceType();
		Trx trx = null;
		try {
			int count = 0;
			int clientId = loginReq.getClientID();
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			getCompiereService().connect();
			org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = VeUtils.convertAdLogin(loginReq);
			String err = login(adLoginReq, webServiceName, "putAwayLabour", serviceType);
			if (err != null && err.length() > 0) {
				putAwayLabourResponse.setError(err);
				putAwayLabourResponse.setIsError(true);
				return response;
			}

			if (!serviceType.equalsIgnoreCase("putAwayLabour")) {
				putAwayLabourResponse.setIsError(true);
				putAwayLabourResponse.setError("Service type " + serviceType + " not configured");
				return response;
			}

			int userId = Env.getAD_User_ID(ctx);
			MUser_Custom user = new MUser_Custom(ctx, userId, trxName);
			X_PI_Deptartment dept = new X_PI_Deptartment(ctx, user.getPI_DEPARTMENT_ID(), trxName);
			int deptId = dept.getPI_Deptartment_ID();

			List<PO> poList = new ArrayList<PO>();
			List<MLocator> mLocatorArray = VeUtils.getLocatorsByDepartment(ctx, trxName, loginReq.getWarehouseID(),
					deptId, "receiving", "Y");
			if (mLocatorArray.size() != 0) {
				for (MLocator id : mLocatorArray) {
					poList.addAll(PiProductLabel.getPiProductLabel("m_locator_ID", id.getM_Locator_ID(), ctx, trxName,
							false, clientId, loginReq.getOrgID()));
				}
			} else {
				putAwayLabourResponse.setIsError(true);
				putAwayLabourResponse.setError("Receiving Locator Type Not Found");
				return response;
			}
			mLocatorArray = null;

//			if(isManufacturing == false) {
			for (PO po : poList) {
				PiProductLabel piProductLabel = new PiProductLabel(ctx, po.get_ID(), trxName);
				if ((piProductLabel.getM_InOutLine_ID() != 0
						&& piProductLabel.getM_InOutLine().getM_InOut().getDocStatus().equalsIgnoreCase("CO"))
						|| (piProductLabel.getPP_Order().getPP_Order_ID() != 0
								&& piProductLabel.getPP_Order().getDocStatus().equalsIgnoreCase("CO")) || piProductLabel.getPi_paorder_ID() != 0 ||
								(piProductLabel.getM_InOutLine_ID() == 0 && piProductLabel.getPP_Order_ID() == 0 && piProductLabel.getPi_paorder_ID() == 0 )) {
//					
					PutAwayLabour[] putAwayLabourArray = putAwayLabourResponse.getPutAwayLabourArray();

					boolean flag = false;
					for (PutAwayLabour detail : putAwayLabourArray) {
						if (detail.getProductId() == piProductLabel.getM_Product_ID()) {
							detail.setQuantity(detail.getQuantity() + piProductLabel.getQuantity().intValue());
							flag = true;
							count++;
							break;
						}
					}
					if (!flag && piProductLabel.getM_Product_ID() != 0) {
						PutAwayLabour putAwayLabour = putAwayLabourResponse.addNewPutAwayLabour();
						putAwayLabour.setProductId(piProductLabel.getM_Product_ID());
						putAwayLabour.setProductName(piProductLabel.getM_Product().getName());
						putAwayLabour.setQuantity(piProductLabel.getQuantity().intValue());

						if (mLocatorArray == null) {
							MWarehouse mWarehouse = new MWarehouse(ctx,
									piProductLabel.getM_Locator().getM_Warehouse().getM_Warehouse_ID(), trxName);
//							mLocatorArray = mWarehouse.getLocators(true);
							mLocatorArray = VeUtils.getLocatorsByDepartment(ctx, trxName,
									mWarehouse.getM_Warehouse_ID(), deptId, "storage", "Y");
						}
						int locatoId = 0;
						String locatorName = "";
						for (MLocator mLocator : mLocatorArray) {
							
							Integer qntyOnHand = PiProductLabel.getSumOfQntyByLocator(clientId, mLocator.getM_Locator_ID());

							if (locatoId == 0) {
								locatoId = mLocator.getM_Locator_ID();
								locatorName = mLocator.getValue();
							}
							if (qntyOnHand <= 0) {
								locatoId = mLocator.getM_Locator_ID();
								locatorName = mLocator.getValue();
								break;
							}
						}
						putAwayLabour.setLocatorId(locatoId);
						putAwayLabour.setLocatorName(locatorName);
						putAwayLabour.setWarehouseId(piProductLabel.getM_Locator().getM_Warehouse_ID());
						putAwayLabour.setWarehouseName(piProductLabel.getM_Locator().getM_Warehouse().getValue());

						count++;
					}
				}
			}

			putAwayLabourResponse.setCount(count);
			putAwayLabourResponse.setIsError(false);
			trx.commit();
		} catch (Exception e) {
			putAwayLabourResponse.setIsError(true);
			putAwayLabourResponse.setError(e.getMessage());
			return response;
		} finally {
			getCompiereService().disconnect();
			trx.close();
		}
		return response;
	}

	@Override
	public StandardResponseDocument createShipmentReceiptConfirmByScan(ShipmentReceiptByScanRequestDocument request) {
		StandardResponseDocument response = StandardResponseDocument.Factory.newInstance();
		StandardResponse standardResponse = response.addNewStandardResponse();
		ShipmentReceiptByScanRequest requestData = request.getShipmentReceiptByScanRequest();
		ADLoginRequest loginReq = requestData.getADLoginRequest();
		String serviceType = requestData.getServiceType();
		Trx trx = null;
		try {
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();

			org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = VeUtils.convertAdLogin(loginReq);
			String err = login(adLoginReq, webServiceName, "mrFailed", serviceType);
			if (err != null && err.length() > 0) {
				standardResponse.setError(err);
				standardResponse.setIsError(true);
				return response;
			}

			if (!serviceType.equalsIgnoreCase("mrFailed")) {
				standardResponse.setIsError(true);
				standardResponse.setError("Service type " + serviceType + " not configured");
				return response;
			}

			int userId = Env.getAD_User_ID(ctx);
			MUser_Custom user = new MUser_Custom(ctx, userId, trxName);
			X_PI_Deptartment dept = new X_PI_Deptartment(ctx, user.getPI_DEPARTMENT_ID(), trxName);
			int deptId = dept.getPI_Deptartment_ID();

			SReceiptLine[] sReceiptLineArray = requestData.getSReceiptLineArray();
			
			for (SReceiptLine sReceiptLine : sReceiptLineArray) {
				List<PO> poList = PiProductLabel.getPiProductLabelById(sReceiptLine.getProductLabelUUId(), ctx, trxName, loginReq.getClientID(), loginReq.getOrgID());
				if(poList == null || poList.size() ==0 || poList.size() >1) {
					standardResponse.setIsError(true);
					standardResponse.setError("Invalid Product Label");
					return response;
				}
			}

			LinkedHashMap<Integer, LinkedHashMap<Integer, Integer>> mInoutMap = new LinkedHashMap<Integer, LinkedHashMap<Integer, Integer>>();
			if (sReceiptLineArray.length != 0) {

				/* Internal move to returns Locators */
				MMovement mMovement = new MMovement(ctx, 0, trxName);
				mMovement.setIsActive(true);
				mMovement.setDescription(null);
				mMovement.setApprovalAmt(BigDecimal.valueOf(0));
				mMovement.setChargeAmt(BigDecimal.valueOf(0));
				mMovement.setFreightAmt(BigDecimal.valueOf(0));
				mMovement.setAD_Org_ID(loginReq.getOrgID());
				mMovement.saveEx();

				/* check atleast one product moved */
				boolean inventoryMoved = false;

				List<MLocator> mLocatorArray = VeUtils.getLocatorsByDepartment(ctx, trxName, loginReq.getWarehouseID(),
						deptId, "returns", "Y");

				for (SReceiptLine sReceiptLine : sReceiptLineArray) {
					List<PO> poList = PiProductLabel.getPiProductLabelById(sReceiptLine.getProductLabelUUId(), ctx, trxName, loginReq.getClientID(), loginReq.getOrgID());
					if(poList == null || poList.size() ==0 || poList.size() >1) {
						standardResponse.setIsError(true);
						standardResponse.setError("Invalid Product Label");
						return response;
					}
					
					PO po = poList.get(0);
					
					PiProductLabel piProductLabel = new PiProductLabel(ctx, po.get_ID(), trxName);

					if (mLocatorArray == null || mLocatorArray.size() == 0) {
						standardResponse.setIsError(true);
						standardResponse
								.setError("Returns Locator Type not found for Department : " + dept.getdeptname() + "");
						return response;
					}

					MLocator mLocator = mLocatorArray.get(0);

					if (piProductLabel.qcpassed() == false)
						break;

					Set<Integer> mInoutMapKeys = mInoutMap.keySet();
					boolean flag = false;
					for (Integer minoutId : mInoutMapKeys) {
						if (minoutId == piProductLabel.getM_InOutLine().getM_InOut_ID()) {
							LinkedHashMap<Integer, Integer> lineIdAndQntyMap = mInoutMap.get(minoutId);
							Set<Integer> lineIdAndQntyMapKeys = lineIdAndQntyMap.keySet();
							boolean innerFlag = false;
							for (Integer mInoutlineId : lineIdAndQntyMapKeys) {
								if (mInoutlineId == piProductLabel.getM_InOutLine_ID()) {
									lineIdAndQntyMap.put(mInoutlineId, lineIdAndQntyMap.get(mInoutlineId)
											+ piProductLabel.getQuantity().intValue());
									innerFlag = true;
									break;
								}
							}
							if (!innerFlag) {
								lineIdAndQntyMap.put(piProductLabel.getM_InOutLine_ID(),
										piProductLabel.getQuantity().intValue());
								mInoutMap.put(minoutId, lineIdAndQntyMap);
							}
							flag = true;
							break;
						}
					}
					if (!flag) {
						LinkedHashMap<Integer, Integer> lineIdAndQntyMap = new LinkedHashMap<Integer, Integer>();
						lineIdAndQntyMap.put(piProductLabel.getM_InOutLine_ID(),
								piProductLabel.getQuantity().intValue());
						mInoutMap.put(piProductLabel.getM_InOutLine().getM_InOut_ID(), lineIdAndQntyMap);
					}

					moveInventory(ctx, trxName, mMovement, piProductLabel.getC_OrderLine_ID(),
							piProductLabel.getQuantity(), piProductLabel.getM_Product_ID(),
							piProductLabel.getM_Locator_ID(), mLocator.getM_Locator_ID(), loginReq.getClientID(),
							loginReq.getOrgID());

					piProductLabel.setQcpassed(false);
					piProductLabel.setM_Locator_ID(mLocator.getM_Locator_ID());
					piProductLabel.saveEx();

					inventoryMoved = true;
				}

				if (inventoryMoved) {
					mMovement.setDocStatus(DocAction.ACTION_Complete);
					mMovement.setDocAction(DocAction.ACTION_Close);
					mMovement.setPosted(true);
					mMovement.setProcessed(true);
					mMovement.setIsApproved(true);
					mMovement.completeIt();
					mMovement.saveEx();
				} else
					mMovement.deleteEx(true);

			}

			Set<Integer> mInoutMapKeys = mInoutMap.keySet();
			for (Integer mInoutId : mInoutMapKeys) {
				MTable table = MTable.get(ctx, "m_inout");
				PO po = table.getPO(mInoutId, trx.getTrxName());
				if (po == null) {
					break;
				}
				MInOut mInOut = (MInOut) po;
				MInOutConfirm mInOutConfirm = new MInOutConfirm(mInOut, "PC");
				mInOutConfirm.setDocStatus(DocAction.STATUS_Drafted);
				mInOutConfirm.saveEx();

				LinkedHashMap<Integer, Integer> lineIdAndQntyMap = mInoutMap.get(mInoutId);
				MInOutLine[] lines = mInOut.getLines(false);
				for (MInOutLine line : lines) {
					MInOutLineConfirm lineConfirm = new MInOutLineConfirm(mInOutConfirm);
					lineConfirm.setM_InOutLine_ID(line.get_ID());
					lineConfirm.setTargetQty(line.getMovementQty());
					lineConfirm.setConfirmedQty(line.getMovementQty());
					lineConfirm.saveEx();

					Set<Integer> lineIdAndQntyMapKeys = lineIdAndQntyMap.keySet();
					for (Integer mInoutlineId : lineIdAndQntyMapKeys) {
						if (mInoutlineId == line.get_ID()) {
							MInOutLine_Custom lineCustom = new MInOutLine_Custom(ctx, mInoutlineId, trxName);
							int failedQty = lineIdAndQntyMap.get(mInoutlineId) + lineCustom.getQCFailedQty().intValue();
							int confirmID = lineConfirm.get_ID();
							MInOutLineConfirm_Custom custom = new MInOutLineConfirm_Custom(ctx, confirmID,
									trx.getTrxName());
							BigDecimal failedB = new BigDecimal(failedQty);
							custom.processLine(false, "PC");
							custom.setQCFailedQty(failedB);
							custom.saveEx();
							lineConfirm.saveEx();
						}
					}
				}
				mInOutConfirm.setDescription(MInOutConfirm.COLUMNNAME_Description);
				mInOutConfirm.setProcessed(true);
				mInOutConfirm.setIsApproved(true);
				mInOutConfirm.setDocStatus(MInOutConfirm.DOCSTATUS_Completed);
				mInOutConfirm.setDocAction(MInOutConfirm.DOCACTION_Close);
				mInOutConfirm.saveEx();
				trx.commit();

			}
			
			MDocType mDocTypee = (MDocType) new Query(ctx, MDocType.Table_Name,
					"name = 'MM Receipt' and ad_client_id = ?", trxName).setParameters(loginReq.getClientID()).firstOnly();
			
			for (Integer mInoutId :  mInoutMap.keySet()) {
				MInOut mInOut = new MInOut(ctx, mInoutId, trxName);
				boolean completeDocAction = false;
				MInOutLine[] lines = mInOut.getLines(false);
				
				for (MInOutLine line : lines) {
					MInOutLine_Custom lineCustom = new MInOutLine_Custom(ctx, line.get_ID(), trxName);
					BigDecimal qntyEntered = lineCustom.getQtyEntered();
					BigDecimal qCFailedQty = lineCustom.getQCFailedQty();
					if (qntyEntered.compareTo(qCFailedQty) == 0)
						completeDocAction = true;
					else {
						completeDocAction = false;
						break;
					}
				}
				
				if(completeDocAction) {
					
					mInOut.setC_DocType_ID(mDocTypee.get_ID());
					mInOut.setDocStatus(MOrder.DOCACTION_Complete);
					mInOut.setDocAction(MOrder.DOCACTION_Complete);
					mInOut.setIsApproved(true);
					mInOut.setProcessed(true);
					mInOut.saveEx();
				}
			}
			
			standardResponse.setIsError(false);

		} catch (Exception e) {
			standardResponse.setIsError(true);
			standardResponse.setError(e.getMessage());
			return response;
		} finally {
			getCompiereService().disconnect();
			trx.close();
		}
		return response;
	}

	@Override
	public PickListLabourResponseDocument pickListLabour(PickListLabourRequestDocument pickListLabourRequest) {
		PickListLabourResponseDocument response = PickListLabourResponseDocument.Factory.newInstance();
		PickListLabourResponse pickListLabourResponse = response.addNewPickListLabourResponse();
		PickListLabourRequest request = pickListLabourRequest.getPickListLabourRequest();
		ADLoginRequest loginReq = request.getADLoginRequest();
		String serviceType = request.getServiceType();
//		String searchKey = request.getSearchKey();
		Trx trx = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			getCompiereService().connect();
			org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = VeUtils.convertAdLogin(loginReq);
			String err = login(adLoginReq, webServiceName, "pickListLabour", serviceType);
			if (err != null && err.length() > 0) {
				pickListLabourResponse.setError(err);
				pickListLabourResponse.setIsError(true);
				return response;
			}

			if (!serviceType.equalsIgnoreCase("pickListLabour")) {
				pickListLabourResponse.setIsError(true);
				pickListLabourResponse.setError("Service type " + serviceType + " not configured");
				return response;
			}

			int userId = Env.getAD_User_ID(ctx);
			MUser_Custom user = new MUser_Custom(ctx, userId, trxName);
			X_PI_Deptartment dept = new X_PI_Deptartment(ctx, user.getPI_DEPARTMENT_ID(), trxName);

			String locatorTypeName = "dispatch";
			if (dept.ispackingmodule())
				locatorTypeName = "packing";

//			List<PO> poList = new Query(ctx, MLocator.Table_Name, "m_warehouse_id =? AND value = '"+locatorName+"'",
//					trxName).setParameters(loginReq.getWarehouseID()).setOrderBy(MLocator.COLUMNNAME_M_Locator_ID)
//					.list();
			List<MLocator> mLocatorArray = VeUtils.getLocatorsByDepartment(ctx, trxName, loginReq.getWarehouseID(),
					dept.getPI_Deptartment_ID(), locatorTypeName, "Y");

//			MLocator mLocator = (MLocator) poList.get(0);

			String sql = "SELECT \n" + "    SUM(qtyordered) as totalQnty, \n"
					+ "    SUM(outstanding_qty) AS total_outstanding_qty, \n" + "    productId, pi_deptartment_ID, \n"
					+ "    name\n" + "FROM (\n" + "    SELECT \n" + "        SUM(a.qtyordered) AS qtyordered,\n"
					+ "        SUM(COALESCE((\n" + "            SELECT SUM(c.qtyentered)\n"
					+ "            FROM m_inoutline c\n" + "            JOIN m_inout d ON d.m_inout_id = c.m_inout_id\n"
					+ "            WHERE c.c_orderline_id = a.c_orderline_id\n"
					+ "                AND d.issotrx = 'Y'\n" + "        ), 0)) AS total_qtyentered,\n"
					+ "        SUM(a.qtyordered) - SUM(COALESCE((\n" + "            SELECT SUM(c.qtyentered)\n"
					+ "            FROM m_inoutline c\n" + "            JOIN m_inout d ON d.m_inout_id = c.m_inout_id\n"
					+ "            WHERE c.c_orderline_id = a.c_orderline_id\n"
					+ "                AND d.DocStatus = 'CO' AND d.issotrx = 'Y'\n"
					+ "        ), 0)) AS outstanding_qty,\n"
					+ "        e.m_product_id AS productId, d.pi_deptartment_ID, \n" + "        e.name\n"
					+ "    FROM \n" + "        c_orderline a \n" + "    JOIN \n"
					+ "        c_order d ON d.c_order_id = a.c_order_id \n" + "    JOIN \n"
					+ "        m_product e ON e.m_product_id = a.m_product_id \n" + "    WHERE \n"
					+ "        d.ad_client_id = " + loginReq.getClientID() + "\n" + "        AND d.m_warehouse_id = "
					+ loginReq.getWarehouseID() + " \n" + "        AND d.putStatus = 'pick' \n"
					+ "        AND d.issotrx = 'Y' \n" + "        AND d.docstatus = 'CO'\n"
					+ "    GROUP BY e.m_product_id, d.pi_deptartment_ID, e.name\n" + ") AS subquery\n" + "GROUP BY \n"
					+ "    productId, pi_deptartment_ID, name\n" + "HAVING \n" + "    SUM(outstanding_qty) > 0\n"
					+ "ORDER BY \n" + "    productId;\n" + "";

			pstm = DB.prepareStatement(sql.toString(), null);
			rs = pstm.executeQuery();

			int count = 0;
			if (mLocatorArray.size() == 0) {
				pickListLabourResponse.setError(locatorTypeName + " LocatorType not found");
				pickListLabourResponse.setIsError(true);
				return response;
			}

			LinkedHashMap<Integer, Integer> qntyOnLocator = new LinkedHashMap<Integer, Integer>();

			I_M_Warehouse warehouse = null;
			for (MLocator locator : mLocatorArray) {

				if (warehouse == null)
					warehouse = locator.getM_Warehouse();
				LinkedHashMap<Integer, Integer> labels = PiProductLabel
						.getAvailableLabelsByLocator(loginReq.getClientID(), locator.getM_Locator_ID());
				if (labels != null && !labels.isEmpty())
					for (Map.Entry<Integer, Integer> entry : labels.entrySet()) {
						qntyOnLocator.put(entry.getKey(), entry.getValue());
					}
//				qntyOnLocator += PiProductLabel
//						.getAvailableLabelsByLocator(loginReq.getClientID(), locator.getM_Locator_ID());
			}
//			LinkedHashMap<Integer, Integer> qntyOnLocator = PiProductLabel
//					.getAvailableLabelsByLocator(loginReq.getClientID(), mLocator.getM_Locator_ID());
			while (rs.next()) {
				int mProductId = rs.getInt("productId");

				String productName = rs.getString("name");
				String deptId = rs.getString("pi_deptartment_ID");
//					int totalQtyordered = rs.getInt("totalQnty");
				int remainQuantity = rs.getInt("total_outstanding_qty");

				if (deptId == null || (deptId != null && dept.getPI_Deptartment_ID() == Integer.valueOf(deptId))) {
					int qntyOnHand = 0;
					if (qntyOnLocator.containsKey(mProductId))
						qntyOnHand = qntyOnLocator.get(mProductId);
					int qntyToPutBack = 0;
					if (qntyOnHand != 0 && remainQuantity > qntyOnHand)
						remainQuantity = remainQuantity - qntyOnHand;
					else if ((qntyOnHand != 0 && qntyOnHand > remainQuantity) || qntyOnHand == remainQuantity) {
						remainQuantity = 0;
						qntyToPutBack = remainQuantity;
					}

					if (remainQuantity > 0) {
						PickLabourLines line = pickListLabourResponse.addNewPickLabourLines();
						line.setProductId(mProductId);
						line.setProductName(productName);
						line.setQuantity(remainQuantity);

						pickDetailLabour(line, ctx, trxName, warehouse.getM_Warehouse_ID(), mProductId, remainQuantity);

						qntyOnLocator.put(mProductId, qntyToPutBack);
						count++;
					}
				}

			}
			pickListLabourResponse.setWarehouseId(warehouse.getM_Warehouse_ID());
			pickListLabourResponse.setWarehouseName(warehouse.getValue());
			pickListLabourResponse.setLineCount(count);
			pickListLabourResponse.setIsError(false);
			trx.commit();
		} catch (Exception e) {
			pickListLabourResponse.setIsError(true);
			pickListLabourResponse.setError(e.getMessage());
			return response;
		} finally {
			closeDbCon(pstm, rs);
			getCompiereService().disconnect();
			trx.close();
		}
		return response;
	}

	public void pickDetailLabour(PickLabourLines pickLabourLines, Properties ctx, String trxName, int warehouseId,
			int productId, int quantity) {

		MStorageOnHand[] mStorageOnHandArray = MStorageOnHand.getWarehouse(ctx, warehouseId, productId, 0, null, true,
				true, 0, trxName, false, 0);
		boolean pickFlag = false;
		for (MStorageOnHand mStorageOnHand : mStorageOnHandArray) {
			PickLabourLines[] pickLabourLinesArray = pickLabourLines.getPickLabourLinesArray();
			boolean flag = false;

			for (PickLabourLines line : pickLabourLinesArray) {
				if (line.getLocatorId() == mStorageOnHand.getM_Locator_ID()) {
					int availableQty = Math.min(mStorageOnHand.getQtyOnHand().intValue(), quantity);
					line.setQuantity(line.getQuantity() + availableQty);
					quantity -= availableQty;
					flag = true;
					if (quantity <= 0) {
						pickFlag = true;
						break;
					}
					break;
				}
			}

//			if (!flag && !mStorageOnHand.getM_Locator().getValue().equalsIgnoreCase("Dispatch Locator")
//					&& !mStorageOnHand.getM_Locator().getValue().equalsIgnoreCase("Receiving Locator")
//					&& !mStorageOnHand.getM_Locator().getValue().equalsIgnoreCase("Packing Locator")) {
			MLocatorType_Custom type = new MLocatorType_Custom(ctx, mStorageOnHand.getM_Locator().getM_LocatorType_ID(),
					trxName);
			if (!flag && !type.isdispatch() && !type.isPacking() && !type.isReceiving() && !type.isReturns()) {
				PickLabourLines line = pickLabourLines.addNewPickLabourLines();
//				line.setProductId(mStorageOnHand.getM_Product_ID());
//				line.setProductName(mStorageOnHand.getM_Product().getValue());
				int availableQty = Math.min(mStorageOnHand.getQtyOnHand().intValue(), quantity);
				line.setQuantity(availableQty);
				quantity -= availableQty;
				line.setLocatorId(mStorageOnHand.getM_Locator_ID());
				line.setLocatorName(mStorageOnHand.getM_Locator().getValue());
//				line.setWarehouseId(mStorageOnHand.getM_Warehouse_ID());
//				line.setWarehouseName(mStorageOnHand.getM_Locator().getM_Warehouse().getValue());
				if (quantity <= 0) {
					pickFlag = true;
					break;
				}

			}

			if (pickFlag) {
				break;
			}
		}

	}

	@Override
	public PickDetailLabourResponseDocument pickDetailLabour(PickDetailLabourRequestDocument pickDetailLabourRequest) {
		PickDetailLabourResponseDocument pickDetailLabourResponse = PickDetailLabourResponseDocument.Factory
				.newInstance();
		PickDetailLabourResponse response = pickDetailLabourResponse.addNewPickDetailLabourResponse();
		PickDetailLabourRequest request = pickDetailLabourRequest.getPickDetailLabourRequest();
		ADLoginRequest loginReq = request.getADLoginRequest();
		String serviceType = request.getServiceType();
		int productId = request.getProductId();
		int quantity = request.getQuantity();
		Trx trx = null;
		try {
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			getCompiereService().connect();
			org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = VeUtils.convertAdLogin(loginReq);
			String err = login(adLoginReq, webServiceName, "pickDetailLabour", serviceType);
			if (err != null && err.length() > 0) {
				response.setError(err);
				response.setIsError(true);
				return pickDetailLabourResponse;
			}

			if (!serviceType.equalsIgnoreCase("pickDetailLabour")) {
				response.setIsError(true);
				response.setError("Service type " + serviceType + " not configured");
				return pickDetailLabourResponse;
			}
			MProduct mProduct = new MProduct(ctx, productId, trxName);
			if (mProduct.getM_Product_ID() == 0) {
				response.setIsError(true);
				response.setError("Invalid Product ID " + productId + "");
				return pickDetailLabourResponse;
			}

			MStorageOnHand[] mStorageOnHandArray = MStorageOnHand.getWarehouse(ctx, loginReq.getWarehouseID(),
					productId, 0, null, true, true, 0, trxName, false, 0);
			boolean pickFlag = false;
			for (MStorageOnHand mStorageOnHand : mStorageOnHandArray) {
				PickLabourLines[] pickLabourLinesArray = response.getPickLabourLinesArray();
				boolean flag = false;

				MLocatorType_Custom type = new MLocatorType_Custom(ctx,
						mStorageOnHand.getM_Locator().getM_LocatorType_ID(), trxName);

				for (PickLabourLines line : pickLabourLinesArray) {
					if (line.getLocatorId() == mStorageOnHand.getM_Locator_ID()) {
						int availableQty = Math.min(mStorageOnHand.getQtyOnHand().intValue(), quantity);
						line.setQuantity(line.getQuantity() + availableQty);
						quantity -= availableQty;
						flag = true;
						if (quantity <= 0) {
							pickFlag = true;
							break;
						}
						break;
					}
				}

//				if (!flag && !mStorageOnHand.getM_Locator().getValue().equalsIgnoreCase("Dispatch Locator")
//						&& !mStorageOnHand.getM_Locator().getValue().equalsIgnoreCase("Receiving Locator")) {
				if (!flag && !type.isdispatch() && !type.isPacking() && !type.isReceiving() && !type.isReturns()) {
					PickLabourLines line = response.addNewPickLabourLines();
					line.setProductId(mStorageOnHand.getM_Product_ID());
					line.setProductName(mStorageOnHand.getM_Product().getName());
					int availableQty = Math.min(mStorageOnHand.getQtyOnHand().intValue(), quantity);
					line.setQuantity(availableQty);
					quantity -= availableQty;
					line.setLocatorId(mStorageOnHand.getM_Locator_ID());
					line.setLocatorName(mStorageOnHand.getM_Locator().getValue());
					line.setWarehouseId(mStorageOnHand.getM_Warehouse_ID());
					line.setWarehouseName(mStorageOnHand.getM_Locator().getM_Warehouse().getValue());
					if (quantity <= 0) {
						pickFlag = true;
						break;
					}

				}

				if (pickFlag) {
					break;
				}
			}

			response.setIsError(false);
			trx.commit();
		} catch (Exception e) {
			response.setIsError(true);
			response.setError(e.getMessage());
			return pickDetailLabourResponse;
		} finally {
			getCompiereService().disconnect();
			trx.close();
		}
		return pickDetailLabourResponse;
	}

	@Override
	public StandardResponseDocument markSOReadyToPick(MarkSOToPickRequestDocument request) {
		StandardResponseDocument response = StandardResponseDocument.Factory.newInstance();
		StandardResponse standardResponse = response.addNewStandardResponse();
		MarkSOToPickRequest markSOToPickRequest = request.getMarkSOToPickRequest();
		ADLoginRequest loginReq = markSOToPickRequest.getADLoginRequest();
		String serviceType = markSOToPickRequest.getServiceType();
		int cOrderId = markSOToPickRequest.getCOrderId();
		Trx trx = null;
		try {
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			getCompiereService().connect();
			org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = VeUtils.convertAdLogin(loginReq);
			String err = login(adLoginReq, webServiceName, "markSOReadyToPick", serviceType);
			if (err != null && err.length() > 0) {
				standardResponse.setError(err);
				standardResponse.setIsError(true);
				return response;
			}

			if (!serviceType.equalsIgnoreCase("markSOReadyToPick")) {
				standardResponse.setIsError(true);
				standardResponse.setError("Service type " + serviceType + " not configured");
				return response;
			}
			COrder_Custom cOrder = new COrder_Custom(ctx, cOrderId, trxName);
			if (cOrder.getC_Order_ID() == 0) {
				standardResponse.setIsError(true);
				standardResponse.setError("Invalid Order ID " + cOrderId + "");
				return response;
			}
			cOrder.setPutStatus("pick");
			cOrder.saveEx();
			standardResponse.setIsError(false);

			Map<String, String> data = new HashMap<>();
			data.put("recordId", String.valueOf(cOrder.getC_Order_ID()));
			data.put("documentNo", String.valueOf(cOrder.getDocumentNo()));

			data.put("path1", "/labour_pick_screen");

			VeUtils.sendNotificationAsync("ispickbyorder", cOrder.get_Table_ID(), cOrder.getC_Order_ID(), ctx, trxName,
					" Products added for Picking", "New items marked for Picking", cOrder.get_TableName(), data,
					loginReq.getClientID(), "MarkedSalesOrderReadyToPick", cOrder.getpidepartmentID());

			trx.commit();
		} catch (Exception e) {
			standardResponse.setIsError(true);
			standardResponse.setError(e.getMessage());
			return response;
		} finally {
			getCompiereService().disconnect();
			trx.close();
		}
		return response;
	}

	@Override
	public ShipperListResponseDocument getShipperForClient(ShipperListRequestDocument shipperListRequestDocument) {
		ShipperListResponseDocument shipperListResponseDocument = ShipperListResponseDocument.Factory.newInstance();
		ShipperListResponse response = shipperListResponseDocument.addNewShipperListResponse();
		ShipperListRequest request = shipperListRequestDocument.getShipperListRequest();
		ADLoginRequest loginReq = request.getADLoginRequest();
		String serviceType = request.getServiceType();
		int clientId = loginReq.getClientID();
		Trx trx = null;
		try {
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			getCompiereService().connect();

			org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = VeUtils.convertAdLogin(loginReq);
			String err = login(adLoginReq, webServiceName, "getShipperForClient", serviceType);
			if (err != null && err.length() > 0) {
				response.setError(err);
				response.setIsError(true);
				return shipperListResponseDocument;
			}

			if (!serviceType.equalsIgnoreCase("getShipperForClient")) {
				response.setIsError(true);
				response.setError("Service type " + serviceType + " not configured");
				return shipperListResponseDocument;
			}

			response = addDeliveryType(response, "shipper", true);
			response = addDeliveryType(response, "pickup", false);
			response = addDeliveryType(response, "delivery", false);

			List<PO> poList = PipraUtils.getShipperListForClient(ctx, clientId);
			if (poList != null) {
				for (PO po : poList) {
					MShipper mShipper = (MShipper) po;
					ShipperList shipperList = response.addNewShipperList();
					shipperList.setShipperName(mShipper.getName());
					shipperList.setShipperId(mShipper.getM_Shipper_ID());
				}
			}

			trx.commit();
		} catch (Exception e) {
			response.setError(e.getMessage());
			response.setIsError(true);
			return shipperListResponseDocument;
		} finally {
			getCompiereService().disconnect();
			trx.close();
		}
		return shipperListResponseDocument;
	}

	private ShipperListResponse addDeliveryType(ShipperListResponse response, String deliveryType,
			boolean showDetails) {
		DeliveryTypes deliveryTypes = response.addNewDeliveryTypes();
		deliveryTypes.setDeliveryType(deliveryType);
		deliveryTypes.setShowDetails(showDetails);
		return response;
	}

	@Override
	public StandardResponseDocument updateShipmentCustomer(UpdateShipmentRequestDocument request) {
		StandardResponseDocument response = StandardResponseDocument.Factory.newInstance();
		StandardResponse standardResponse = response.addNewStandardResponse();
		UpdateShipmentRequest updateShipmentRequest = request.getUpdateShipmentRequest();
		ADLoginRequest loginReq = updateShipmentRequest.getADLoginRequest();
		String serviceType = updateShipmentRequest.getServiceType();
		int mInoutId = updateShipmentRequest.getMInoutId();
		String deliveryType = updateShipmentRequest.getDeliveryType();
		int shipperId = updateShipmentRequest.getShipperId();
		Trx trx = null;
		try {
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			getCompiereService().connect();
			org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = VeUtils.convertAdLogin(loginReq);
			String err = login(adLoginReq, webServiceName, "updateShipmentCustomer", serviceType);
			if (err != null && err.length() > 0) {
				standardResponse.setError(err);
				standardResponse.setIsError(true);
				return response;
			}

			if (!serviceType.equalsIgnoreCase("updateShipmentCustomer")) {
				standardResponse.setIsError(true);
				standardResponse.setError("Service type " + serviceType + " not configured");
				return response;
			}
			MInOut_Custom mInout = new MInOut_Custom(ctx, mInoutId, trxName);
			if (mInout.getM_InOut_ID() == 0) {
				standardResponse.setIsError(true);
				standardResponse.setError("Invalid mInout ID " + mInoutId + "");
				return response;
			}

			if (deliveryType.equalsIgnoreCase("shipper")) {
				mInout.setDeliveryViaRule("S");
				mInout.setM_Shipper_ID(shipperId);
			} else if (deliveryType.equalsIgnoreCase("pickup"))
				mInout.setDeliveryViaRule("P");
			else if (deliveryType.equalsIgnoreCase("delivery"))
				mInout.setDeliveryViaRule("D");
			mInout.saveEx();
			standardResponse.setIsError(false);
			trx.commit();
		} catch (Exception e) {
			standardResponse.setIsError(true);
			standardResponse.setError(e.getMessage());
			return response;
		} finally {
			getCompiereService().disconnect();
			trx.close();
		}
		return response;
	}

	@Override
	public StandardResponseDocument updateShipment(UpdateShipmentRequestDocument request) {
		StandardResponseDocument response = StandardResponseDocument.Factory.newInstance();
		StandardResponse standardResponse = response.addNewStandardResponse();
		UpdateShipmentRequest updateShipmentRequest = request.getUpdateShipmentRequest();
		ADLoginRequest loginReq = updateShipmentRequest.getADLoginRequest();
		String serviceType = updateShipmentRequest.getServiceType();
		int mInoutId = updateShipmentRequest.getMInoutId();
		String status = updateShipmentRequest.getStatus();
		String marking = updateShipmentRequest.getMarking();
		Trx trx = null;
		DispatcedLines[] dispatchedLines =
	            updateShipmentRequest.getDispatcedLinesArray();
		try {
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			getCompiereService().connect();
			org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = VeUtils.convertAdLogin(loginReq);
			String err = login(adLoginReq, webServiceName, "createSC", serviceType);
			if (err != null && err.length() > 0) {
				standardResponse.setError(err);
				standardResponse.setIsError(true);
				return response;
			}

			if (!serviceType.equalsIgnoreCase("createSC")) {
				standardResponse.setIsError(true);
				standardResponse.setError("Service type " + serviceType + " not configured");
				return response;
			}
			MInOut_Custom mInout = new MInOut_Custom(ctx, mInoutId, trxName);
			if (mInout.getM_InOut_ID() == 0) {
				standardResponse.setIsError(true);
				standardResponse.setError("Invalid mInout ID " + mInoutId + "");
				return response;
			}

			if (status != null && !status.isEmpty()) {
				mInout.setPickStatus(status);
			}
			if (marking != null && !marking.isEmpty()) {
				mInout.setMarking(marking);
			}
			mInout.saveEx();
			
			if (dispatchedLines != null && dispatchedLines.length > 0) {

	            for (DispatcedLines line : dispatchedLines) {

	                if (line == null) {
	                    continue;
	                }

	                int mRLineId = line.getMRLineId();
	                int qty = line.getDispatchedQty();
	                BigDecimal dispatchedQty = BigDecimal.valueOf(qty);

	                if (mRLineId <= 0 || dispatchedQty == null) {
	                    continue;
	                }

	                MInOutLine_Custom mLine =
	                        new MInOutLine_Custom(ctx, mRLineId, trxName);

	                if (mLine.getM_InOutLine_ID() == 0
	                        || mLine.getM_InOut_ID() != mInoutId) {
	                    continue;
	                }
	                
	                BigDecimal oldQty = mLine.getDispatcedQty();
	                if (oldQty == null) {
	                    oldQty = BigDecimal.ZERO;
	                }

	                BigDecimal finalQty = oldQty.add(dispatchedQty);

	                mLine.setDispatcedQty(finalQty);
	                
	                mLine.saveEx();
	            }
	        }
			
			standardResponse.setIsError(false);
			trx.commit();
		} catch (Exception e) {
			standardResponse.setIsError(true);
			standardResponse.setError(e.getMessage());
			return response;
		} finally {
			getCompiereService().disconnect();
			trx.close();
		}
		return response;
	}

	private void closeDbCon(PreparedStatement pstm, ResultSet rs) {
		try {
			if (pstm != null)
				pstm.close();
			if (rs != null)
				rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public StandardResponseDocument getIsUserActive(GetIsUserActiveRequestDocument request) {
		StandardResponseDocument response = StandardResponseDocument.Factory.newInstance();
		StandardResponse standardResponse = response.addNewStandardResponse();
		GetIsUserActiveRequest getIsUserActiveRequest = request.getGetIsUserActiveRequest();
		String userName = getIsUserActiveRequest.getUserName();
		Trx trx = null;
		try {
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			getCompiereService().connect();

			PO po = new Query(ctx, MUser.Table_Name, "name =?", trxName).setParameters(userName)
					.setOrderBy(MUser.COLUMNNAME_AD_User_ID).firstOnly();

			if (po == null || po.get_ID() == 0) {
				standardResponse.setIsError(true);
				standardResponse.setError("User with name " + userName + " does not exist");
				return response;
			}
			MUser mUser = (MUser) po;
			if (mUser.isActive())
				standardResponse.setIsError(false);
			else
				standardResponse.setIsError(true);

			standardResponse.setIsError(false);
			trx.commit();
		} catch (Exception e) {
			standardResponse.setIsError(true);
			standardResponse.setError(e.getMessage());
			return response;
		} finally {
			getCompiereService().disconnect();
			trx.close();
		}
		return response;
	}

	@Override
	public PutAwayDetailResponseDocument putAwayDetail(PutAwayDetailRequestDocument request) {

		PutAwayDetailResponseDocument response = PutAwayDetailResponseDocument.Factory.newInstance();
		PutAwayDetailResponse putAwayDetailResponse = response.addNewPutAwayDetailResponse();
		PutAwayDetailRequest putAwayDetailRequest = request.getPutAwayDetailRequest();
		ADLoginRequest loginReq = putAwayDetailRequest.getADLoginRequest();
		String serviceType = putAwayDetailRequest.getServiceType();
		String documentNumber = putAwayDetailRequest.getDocumentNo();
		Trx trx = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			getCompiereService().connect();
			org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = VeUtils.convertAdLogin(loginReq);
			String err = login(adLoginReq, webServiceName, "putAwayDetail", serviceType);
			if (err != null && err.length() > 0) {
				putAwayDetailResponse.setError(err);
				putAwayDetailResponse.setIsError(true);
				return response;
			}

			if (!serviceType.equalsIgnoreCase("putAwayDetail")) {
				putAwayDetailResponse.setIsError(true);
				putAwayDetailResponse.setError("Service type " + serviceType + " not configured");
				return response;
			}

			String query = "SELECT\n" + "    DISTINCT(po.documentno) AS documentNo,\n" + "    bp.name AS Supplier,\n"
					+ "    po.m_inout_id AS mInoutID,\n" + "    TO_CHAR(po.dateordered, 'DD/MM/YYYY') AS Order_Date,\n"
					+ "    wh.name AS Warehouse_Name, wh.m_warehouse_id,\n" + "    po.description,\n"
					+ "    po.created,\n" + "    po.m_inout_id,\n"
					+ "	co.documentno as orderDocumentno, co.pi_deptartment_ID\n" + "FROM m_inout po\n"
					+ "JOIN c_bpartner bp ON po.c_bpartner_id = bp.c_bpartner_id \n"
					+ "JOIN c_order co on co.c_order_id = po.c_order_id\n"
					+ "JOIN m_warehouse wh ON po.m_warehouse_id = wh.m_warehouse_id\n" + "WHERE po.ad_client_id = "
					+ loginReq.getClientID() + " AND po.issotrx = 'N' AND po.documentno ='" + documentNumber + "';";

			pstm = DB.prepareStatement(query.toString(), null);
			rs = pstm.executeQuery();

			int deptId = 0;
			while (rs.next()) {
				String documentNo = rs.getString("documentNo");
				int mInoutID = rs.getInt("mInoutID");
				String supplier = rs.getString("Supplier");
				String date = rs.getString("Order_Date");
				String orderDocumentno = rs.getString("orderDocumentno");
				String warehouseName = rs.getString("Warehouse_Name");
				int mWarehouseId = rs.getInt("m_warehouse_id");
				deptId = rs.getInt("pi_deptartment_ID");
				String description = rs.getString("description");
				putAwayDetailResponse.setDocumentNo(documentNo);
				putAwayDetailResponse.setMInoutID(mInoutID);
				putAwayDetailResponse.setSupplier(supplier);
				putAwayDetailResponse.setOrderDate(date);
				putAwayDetailResponse.setWarehouseName(warehouseName);
				putAwayDetailResponse.setWarehouseId(mWarehouseId);
				putAwayDetailResponse.setOrderDocumentno(orderDocumentno);
				if (description == null)
					putAwayDetailResponse.setDescription("");
				else
					putAwayDetailResponse.setDescription(description);
			}

			List<PO> poList = new ArrayList<PO>();

			X_PI_Deptartment deptartment = null;
			if (deptId != 0)
				deptartment = new X_PI_Deptartment(ctx, deptId, trxName);
			else
				deptartment = VeUtils.getDefaultLocatorType(ctx, trxName);

			if (deptartment == null || deptartment.get_ID() == 0) {
				putAwayDetailResponse.setIsError(true);
				putAwayDetailResponse.setError("Receiving or Default Locator Type Not Found For Department");
				return response;
			}

			List<MLocator> mLocatorArray = VeUtils.getLocatorsByDepartment(ctx, trxName, loginReq.getWarehouseID(),
					deptartment.getPI_Deptartment_ID(), "receiving", "Y");

			if (mLocatorArray.size() == 0) {
				putAwayDetailResponse.setIsError(true);
				putAwayDetailResponse.setError("Receiving Locators Not Found For Department");
				return response;
			}

			MInOut_Custom mInout = new MInOut_Custom(ctx, putAwayDetailResponse.getMInoutID(), trxName);
			for (MInOutLine line : mInout.getLines()) {

				for (MLocator locator : mLocatorArray) {
					poList.addAll(PiProductLabel.getProductLabelForInoutLineAndLocator(locator.getM_Locator_ID(),
							line.getM_InOutLine_ID(), ctx, trxName,  loginReq.getClientID(), loginReq.getOrgID()));
				}

			}

			for (PO po : poList) {
				PiProductLabel piProductLabel = new PiProductLabel(ctx, po.get_ID(), trxName);

				PutAwayDetail[] list = putAwayDetailResponse.getPutAwayDetailArray();
				boolean flag = false;
				for (PutAwayDetail detail : list) {
					if (detail.getCOrderlineId() == piProductLabel.getC_OrderLine_ID()) {
						detail.setTotalQuantity(detail.getTotalQuantity() + piProductLabel.getQuantity().intValue())	;
						detail.setQuantityInRecevingLocator(
								detail.getQuantityInRecevingLocator() + piProductLabel.getQuantity().intValue());
						flag = true;
						break;
					}
				}
				if (!flag) {
					PutAwayDetail putAwayDetail = putAwayDetailResponse.addNewPutAwayDetail();
					putAwayDetail.setProductId(piProductLabel.getM_Product_ID());
					putAwayDetail.setProductName(piProductLabel.getM_Product().getName());
					putAwayDetail.setCOrderlineId(piProductLabel.getC_OrderLine_ID());
					putAwayDetail.setQuantityInRecevingLocator(piProductLabel.getQuantity().intValue());
//					MInOutLine_Custom lineCustom = new MInOutLine_Custom(ctx, piProductLabel.getM_InOutLine_ID(),
//							trxName);
					putAwayDetail.setTotalQuantity(piProductLabel.getQuantity().intValue());
				}

			}

			putAwayDetailResponse.setIsError(false);
			trx.commit();
		} catch (Exception e) {
			putAwayDetailResponse.setIsError(true);
			putAwayDetailResponse.setError(e.getMessage());
			return response;
		} finally {
			closeDbCon(pstm, rs);
			getCompiereService().disconnect();
			trx.close();
		}
		return response;
	}

	@Override
	public GenerateLabelResponseDocument generateLabel(GenerateLabelRequestDocument request) {
		GenerateLabelResponseDocument response = GenerateLabelResponseDocument.Factory.newInstance();
		GenerateLabelResponse generateLabelResponse = response.addNewGenerateLabelResponse();
		GenerateLabelRequest generateLabelRequest = request.getGenerateLabelRequest();
		ADLoginRequest loginReq = generateLabelRequest.getADLoginRequest();
		String serviceType = generateLabelRequest.getServiceType();
		Trx trx = null;
		try {
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			getCompiereService().connect();

			org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = VeUtils.convertAdLogin(loginReq);
			String err = login(adLoginReq, webServiceName, "generateLabel", serviceType);
			if (err != null && err.length() > 0) {
				generateLabelResponse.setError(err);
				generateLabelResponse.setIsError(true);
				return response;
			}

			if (!serviceType.equalsIgnoreCase("generateLabel")) {
				generateLabelResponse.setIsError(true);
				generateLabelResponse.setError("Service type " + serviceType + " not configured");
				return response;
			}

			UUID uuid = UUID.randomUUID();
			String uuidString = uuid.toString();

			PiProductLabel veLabel = new PiProductLabel(ctx, trxName, loginReq, generateLabelRequest, true);
			veLabel.setLabelUUID(uuidString);
			veLabel.saveEx();

			generateLabelResponse.setLabelId(veLabel.getpi_productLabel_ID());
			generateLabelResponse.setLabelUUID(uuidString);
			generateLabelResponse.setIsError(false);
			trx.commit();
		} catch (Exception e) {
			e.printStackTrace();
			generateLabelResponse.setIsError(true);
			generateLabelResponse.setError(e.getMessage());
			return response;
		} finally {
			getCompiereService().disconnect();
			trx.close();
		}
		return response;
	}

	@Override
	public GenerateLabelResponseDocument attachChildLabelToParent(ParentLabelRequestDocument request) {
		GenerateLabelResponseDocument response = GenerateLabelResponseDocument.Factory.newInstance();
		GenerateLabelResponse generateLabelResponse = response.addNewGenerateLabelResponse();
		ParentLabelRequest parentLabelRequest = request.getParentLabelRequest();
		ADLoginRequest loginReq = parentLabelRequest.getADLoginRequest();
		String serviceType = parentLabelRequest.getServiceType();
		Trx trx = null;
		try {
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			getCompiereService().connect();

			org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = VeUtils.convertAdLogin(loginReq);
			String err = login(adLoginReq, webServiceName, "attachChildLabelToParent", serviceType);
			if (err != null && err.length() > 0) {
				generateLabelResponse.setError(err);
				generateLabelResponse.setIsError(true);
				return response;
			}

			if (!serviceType.equalsIgnoreCase("attachChildLabelToParent")) {
				generateLabelResponse.setIsError(true);
				generateLabelResponse.setError("Service type " + serviceType + " not configured");
				return response;
			}

			int quanity = 0;
			int userId = Env.getAD_User_ID(ctx);
			MUser_Custom user = new MUser_Custom(ctx, userId, trxName);
			X_PI_Deptartment dept = new X_PI_Deptartment(ctx, user.getPI_DEPARTMENT_ID(), trxName);
			int deptId = dept.getPI_Deptartment_ID();

			int parentLabelId = parentLabelRequest.getParentLabelId();
			boolean disintegrate = parentLabelRequest.getDisintegrate();
			PiProductLabel parentLabel = new PiProductLabel(ctx, parentLabelId, trxName);
			if (disintegrate) {

				MLocatorType_Custom locatorTypeCustom = new MLocatorType_Custom(ctx,
						parentLabel.getM_Locator().getM_LocatorType_ID(), trxName);
				if (!locatorTypeCustom.isStorage() && !locatorTypeCustom.isReceiving()) {
					generateLabelResponse.setIsError(true);
					generateLabelResponse.setError("Disintegrate allowed for Storage/ Receiving area Items");
					return response;
				}
				if (locatorTypeCustom.isStorage()) {

					List<MLocator> mLocatorArray = VeUtils.getLocatorsByDepartment(ctx, trxName,
							loginReq.getWarehouseID(), deptId, "receiving", "Y");
					if (mLocatorArray == null || mLocatorArray.size() == 0) {
						generateLabelResponse.setIsError(true);
						generateLabelResponse.setError("Receiving Locator not found for : " + dept.getdeptname() + "");
						return response;
					}
					PutAwayRequest putAwayRequest = PutAwayRequest.Factory.newInstance();
					PutAwayLine line = putAwayRequest.addNewPutAwayLine();
					line.setLocatorId(mLocatorArray.get(0).get_ID());
					line.setProductLabelUUId(parentLabel.getLabelUUID());

					putAwayLabels(putAwayRequest.getPutAwayLineArray(), trxName, ctx, loginReq.getClientID(),
							loginReq.getOrgID(), false, false, false, loginReq.getWarehouseID());
				}

				List<PO> pI_productLabel_id = new Query(ctx, PiProductLabel.Table_Name, "p.pi_productLabel_ID = ?", trxName)
						.addJoinClause("JOIN adempiere.pi_productlabel p ON (" + PiProductLabel.Table_Name
								+ ".parentlabel = p.pi_productLabel_ID)")
						.setParameters(parentLabelId)
						.setOrderBy(PiProductLabel.Table_Name + ".PI_ProductLabel_ID ASC").list();

				for (PO ids : pI_productLabel_id) {
					PiProductLabel idss = new PiProductLabel(ctx, ids.get_ID(), trxName);
					idss.setparentlabel(0);
					idss.saveEx();
				}
			}

			if (parentLabel.getpi_productLabel_ID() == 0) {
				generateLabelResponse.setIsError(true);
				generateLabelResponse.setError("Inavalid parentLabel Id " + parentLabelId + "");
				return response;
			}

			ProductLabelLine[] labelLineArray = parentLabelRequest.getProductLabelLineArray();
			if (labelLineArray != null && labelLineArray.length != 0)
				for (ProductLabelLine line : labelLineArray) {

					PiProductLabel childLabel = new PiProductLabel(ctx, line.getLabelId(), trxName);
					childLabel.setparentlabel(parentLabelId);
					childLabel.saveEx();
					
					if (childLabel.getQuantity() != null
							&& childLabel.getQuantity().compareTo(BigDecimal.valueOf(0)) > 0)
						quanity += childLabel.getQuantity().intValue();
				}
			
			parentLabel.setQuantity(BigDecimal.valueOf(quanity));
			parentLabel.saveEx();
			trx.commit();
			
			generateLabelResponse.setQuantity(quanity);
			generateLabelResponse.setLabelId(parentLabel.getpi_productLabel_ID());
			generateLabelResponse.setLabelUUID(parentLabel.getLabelUUID() != null ? parentLabel.getLabelUUID() : "");
			generateLabelResponse.setIsError(false);
			trx.commit();
		} catch (Exception e) {
			if (trx != null)
				trx.rollback();
			generateLabelResponse.setIsError(true);
			generateLabelResponse.setError(e.getMessage());
			return response;
		} finally {
			getCompiereService().disconnect();
			
			if (trx != null)
				trx.close();
		}
		return response;
	}

	@Override
	public StandardResponseDocument createPurchaseOrder(String req) {
		StandardResponseDocument resp = StandardResponseDocument.Factory.newInstance();
		StandardResponse response = resp.addNewStandardResponse();
		ObjectMapper objectMapper = new ObjectMapper();
		Trx trx = null;

		try {

			String getOrgin = httpServletRequest.getHeader("Origin");
			s_log.log(Level.SEVERE, "getOrgin : " + getOrgin);

			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			getCompiereService().connect();
			trx.start();

			String serviceType = "createPurchaseOrder";
			String user = "Vinay";
			String password = "Vinay";

			com.pipra.ve.model.request.PurchaseOrderRequest request = objectMapper.readValue(req,
					com.pipra.ve.model.request.PurchaseOrderRequest.class);

			String bPartnerName = request.getAcHeadName();

			MUser mUser = MUser.get(ctx, user, password);
			if (mUser == null || mUser.get_ID() == 0) {
				response.setIsError(true);
				response.setError("Inavid Login Credentials");
				return resp;
			}

			int adClientId = mUser.getAD_Client_ID();
			int adOrgId = mUser.getAD_Org_ID();
			org.idempiere.adInterface.x10.ADLoginRequest aDLoginRequest = org.idempiere.adInterface.x10.ADLoginRequest.Factory
					.newInstance();
			aDLoginRequest.setUser(user);
			aDLoginRequest.setPass(password);
			aDLoginRequest.setClientID(adClientId);
			aDLoginRequest.setOrgID(adOrgId);

			PO po1 = new Query(ctx, MUserRoles.Table_Name, "ad_user_id = ? AND ad_client_id = ?", trxName)
					.setParameters(mUser.get_ID(), adClientId).list().get(0);
			MUserRoles roles = new MUserRoles(ctx, mUser.get_ID(), po1.get_ID(), trxName);
			aDLoginRequest.setRoleID(roles.getAD_Role_ID());
			PO po = new Query(ctx, MWarehouse.Table_Name, "ad_client_ID = ? AND ad_org_ID = ?", trxName)
					.setParameters(adClientId, adOrgId).list().get(0);
			aDLoginRequest.setWarehouseID(po.get_ID());
			aDLoginRequest.setLang("112");
			aDLoginRequest.setStage(0);

			String err = login(aDLoginRequest, webServiceName, "createPurchaseOrder", serviceType);
			if (err != null && err.length() > 0) {
				response.setError(err);
				response.setIsError(true);
				return resp;
			}
			if (!serviceType.equalsIgnoreCase("createPurchaseOrder")) {
				response.setIsError(true);
				response.setError("Service type " + serviceType + " not configured");
				return resp;
			}

			MWarehouse warehouse = new MWarehouse(ctx, po.get_ID(), trxName);

			MDocType mDocTypee = (MDocType) new Query(ctx, MDocType.Table_Name,
					"name = 'Purchase Order' and ad_client_id = ?", trxName).setParameters(adClientId).firstOnly();
			MBPartner mBPartner = (MBPartner) new Query(ctx, MBPartner.Table_Name,
					"name = ? and ad_client_id = ? and isVendor = 'Y'", trxName).setParameters(bPartnerName, adClientId)
					.firstOnly();

			if (mBPartner == null) {

				boolean sameAddress = true;
				MLocation billAddress = null;

				billAddress = new MLocation(ctx, 0, trxName);

				String address1 = request.getBillTo();
				String address2 = null;
				String address3 = null;
				String cutString = null;
				String cutString2 = null;
				if (request.getBillTo().length() > 60) {
					address1 = request.getBillTo().substring(0, 60);
					cutString = request.getBillTo().substring(60, request.getBillTo().length());
				}
				if (cutString != null) {

					if (cutString.length() > 60) {
						cutString2 = cutString.substring(60, cutString.length());
						address2 = cutString.substring(0, 60);
					} else
						address2 = cutString.substring(0, cutString.length());
				}
				if (cutString2 != null)
					address3 = cutString2.substring(0, 60);

				billAddress.setAddress1(address1);
				billAddress.setAddress2(address2);
				billAddress.setAddress3(address3);
				billAddress.setC_Country_ID(208);
				billAddress.saveEx(trxName);

				MLocation shipAddress = null;
				if (!request.getBillTo().equals(request.getShipTo()) && request.getShipTo() != ""
						&& request.getShipTo() != null) {
					shipAddress = new MLocation(ctx, 0, trxName);

					String a1 = request.getShipTo();
					String a2 = null;
					String a3 = null;
					String c1 = null;
					String c2 = null;
					if (request.getShipTo().length() > 60) {
						a1 = request.getShipTo().substring(0, 60);
						c1 = request.getShipTo().substring(60, request.getShipTo().length());
					}
					if (c1 != null) {
						if (c1.length() > 60) {
							c2 = c1.substring(60, c1.length());
							a2 = c1.substring(0, 60);
						} else
							a2 = c1.substring(0, c1.length());
					}
					if (c2 != null)
						a3 = c2.substring(0, 60);

					shipAddress.setAddress1(a1);
					shipAddress.setAddress2(a2);
					shipAddress.setAddress3(a3);

					shipAddress.setC_Country_ID(208);
					shipAddress.saveEx(trxName);

					sameAddress = false;
				}

				mBPartner = new MBPartner(ctx, 0, trxName);
				mBPartner.setClientOrg(adClientId, adOrgId);
				mBPartner.setName(bPartnerName);
				mBPartner.setIsVendor(true);
				mBPartner.saveEx(trxName);

				MBPartnerLocation shipBpLocation = new MBPartnerLocation(mBPartner);
				shipBpLocation.setC_Location_ID(billAddress.getC_Location_ID());

				if (!sameAddress) {
					MBPartnerLocation billBpLocation = new MBPartnerLocation(mBPartner);
					billBpLocation.setC_Location_ID(shipAddress.getC_Location_ID());
					billBpLocation.setIsShipTo(true);
					billBpLocation.setIsBillTo(false);
					billBpLocation.saveEx(trxName);

					shipBpLocation.setIsShipTo(false);
				}

				shipBpLocation.saveEx(trxName);
				System.out.println(shipBpLocation.isShipTo());
				System.out.println(shipBpLocation.isBillTo());

			}

			trx.commit();
			COrder_Custom mOrder = createPOOrder(adClientId, adOrgId, warehouse.getM_Warehouse_ID(), mBPartner.get_ID(), ctx,
					mDocTypee.get_ID(), mUser.getAD_User_ID(), trxName, request, trx,request.getDeptName());
			trx.commit();
			response.setIsError(false);
			response.setRecordID(mOrder.get_ID());

		} catch (Exception e) {
			if (trx != null) {
				trx.rollback();
			}
			response.setError(e.getLocalizedMessage());
			response.setIsError(true);
		} finally {
			getCompiereService().disconnect();
			if (trx != null) {
				trx.close();
			}
		}
		return resp;
	}

	private COrder_Custom createPOOrder(int ad_client_id, int org_id, int warehouseId, int mBPartnerId, Properties ctx,
			int docTypeId, int userId, String trxName, com.pipra.ve.model.request.PurchaseOrderRequest request, Trx trx,
			String departmentName )
			throws Exception {

		MBPartner mBPartner = new MBPartner(ctx, mBPartnerId, trxName);

		int orderId = 0;
		int erpId = request.getId();
		if (erpId != 0) {
			String id = String.valueOf(erpId);
			PO po = new Query(ctx, MOrder.Table_Name, "ad_client_id = ? AND ad_org_id = ? AND POReference = ?", trxName)
					.setParameters(ad_client_id, org_id, id).firstOnly();
			if (po != null && po.get_ID() != 0)
				orderId = po.get_ID();
		}

		COrder_Custom po = new COrder_Custom(ctx, orderId, trxName);
		if (orderId == 0) {
			po.setC_DocTypeTarget_ID(docTypeId);
			po.setClientOrg(ad_client_id, org_id);
			po.setDescription(request.getNarration());
			po.setPOReference(String.valueOf(request.getId()));

			po.setDateOrdered(request.getvDate());
			po.setDatePromised(request.getvDate());
			po.setC_BPartner_ID(mBPartner.getC_BPartner_ID());
			po.setBill_BPartner_ID(mBPartner.getC_BPartner_ID());
			po.setBill_User_ID(userId);
			po.setM_Warehouse_ID(warehouseId);
			po.setIsSOTrx(false);
			po.setSalesRep_ID(userId);
			po.setAD_User_ID(userId);
			po.setPaymentRule("B");
			
			PO po1 = new Query(ctx, X_PI_Deptartment.Table_Name,"ad_client_id=? AND value=?", trxName)
					.setParameters(ad_client_id,departmentName).firstOnly();
			if(po1 != null && po1.get_ID() != 0) {
				po.setpidepartmentID(po1.get_ID());
			}
			po.saveEx(trxName);
		}

		List<Integer> lineIdList = new ArrayList<Integer>();
		for (DTItem line : request.getDtItems()) {
			int cOrderLineId = 0;
			int lineId = line.getId();
			lineIdList.add(lineId);
			if (orderId != 0)
				for (MOrderLine mol : po.getLines()) {
					if (mol.getLine() == lineId) {
						cOrderLineId = mol.getC_OrderLine_ID();
						break;
					}
				}

			int productQTY = line.getQty();
			Double discount = line.getDiscAmt();
			String productName = line.getMaterialName();
			if (productQTY == 0)
				throw new AdempiereException("Please Enter Product Quantity > 0");

			MOrderLine mOrderLine = null;
			if (cOrderLineId == 0) {
				mOrderLine = new MOrderLine(po);
			} else
				mOrderLine = new MOrderLine(ctx, cOrderLineId, trxName);

			MProduct mProduct = (MProduct) new Query(ctx, MProduct.Table_Name, "name = ? and ad_client_id = ?", trxName)
					.setParameters(productName, ad_client_id).firstOnly();
			if (mProduct == null) {
				mProduct = new MProduct_Custom(ctx, 0, trxName);
				mProduct.setAD_Org_ID(org_id);
				mProduct.setValue(productName);
				mProduct.setName(productName);

				MProductCategory mProductCategory = VeUtils.getDefaultProductCategory(ctx, trxName, ad_client_id);
				if (mProductCategory.get_ID() == 0)
					throw new AdempiereException("No Default Product Category");
				mProduct.setM_Product_Category_ID(mProductCategory.get_ID());

				MTaxCategory mTaxCategory = VeUtils.getDefaultTaxCategory(ctx, trxName, ad_client_id);
				if (mTaxCategory.get_ID() == 0)
					throw new AdempiereException("No Default Tax Category");
				mProduct.setC_TaxCategory_ID(mTaxCategory.get_ID());

				mProduct.setC_UOM_ID(MUOM.getDefault_UOM_ID(ctx));

				mProduct.saveEx(trxName);

				MProductPrice mProductPrice = new MProductPrice(ctx, 0, trxName);
				mProductPrice.setM_Product_ID(mProduct.get_ID());
				mProductPrice.setPriceLimit(BigDecimal.valueOf(line.getRate()));
				mProductPrice.setPriceList(BigDecimal.valueOf(line.getRate()));
				mProductPrice.setPriceStd(BigDecimal.valueOf(line.getRate()));

				MPriceListVersion mPriceListVersion = VeUtils.getDefaultPriceListVersion(ctx, trxName, ad_client_id,
						"N");
				mProductPrice.setM_PriceList_Version_ID(mPriceListVersion.get_ID());

				mProductPrice.saveEx(trxName);

				trx.commit();
			}

			mOrderLine.setM_Product_ID(mProduct.getM_Product_ID());
			mOrderLine.setDescription(line.getNarration());
			mOrderLine.setQty(BigDecimal.valueOf(productQTY));
//				mOrderLine.setC_Tax_ID(tax.getC_Tax_ID());
			mOrderLine.setDiscount(BigDecimal.valueOf(discount));
			mOrderLine.setC_UOM_ID(mProduct.getC_UOM_ID());
			mOrderLine.setLine(line.getId());
			mOrderLine.saveEx(trxName);
		}

		for (MOrderLine orderline : po.getLines()) {
			if (!lineIdList.contains(orderline.getLine()))
				orderline.delete(true);
		}
//			po.saveEx();
//			po.setC_DocType_ID(docTypeId);
//			po.setDocStatus(MOrder.DOCACTION_Complete);
//			po.setDocAction(MOrder.DOCACTION_Complete);
//			po.setIsApproved(true);
//			po.setProcessed(true);
//			po.saveEx();

		return po;
	}

	@Override
	public StandardResponseDocument createSalesOrder(String req) {
		StandardResponseDocument resp = StandardResponseDocument.Factory.newInstance();
		StandardResponse response = resp.addNewStandardResponse();
		ObjectMapper objectMapper = new ObjectMapper();
		Trx trx = null;

		try {
			String getOrgin = httpServletRequest.getHeader("Origin");
			s_log.log(Level.SEVERE, "getOrgin : " + getOrgin);

			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			getCompiereService().connect();
			trx.start();

			String serviceType = "createSalesOrder";
			String user = "Vinay";
			String password = "Vinay";

			com.pipra.ve.model.request.PurchaseOrderRequest request = objectMapper.readValue(req,
					com.pipra.ve.model.request.PurchaseOrderRequest.class);

			String bPartnerName = request.getAcHeadName();

			MUser mUser = MUser.get(ctx, user, password);
			if (mUser == null || mUser.get_ID() == 0) {
				response.setIsError(true);
				response.setError("Inavid Login Credentials");
				return resp;
			}

			int adClientId = mUser.getAD_Client_ID();
			int adOrgId = mUser.getAD_Org_ID();
			org.idempiere.adInterface.x10.ADLoginRequest aDLoginRequest = org.idempiere.adInterface.x10.ADLoginRequest.Factory
					.newInstance();
			aDLoginRequest.setUser(user);
			aDLoginRequest.setPass(password);
			aDLoginRequest.setClientID(adClientId);
			aDLoginRequest.setOrgID(adOrgId);

			PO po1 = new Query(ctx, MUserRoles.Table_Name, "ad_user_id = ? AND ad_client_id = ?", trxName)
					.setParameters(mUser.get_ID(), adClientId).list().get(0);
			MUserRoles roles = new MUserRoles(ctx, mUser.get_ID(), po1.get_ID(), trxName);
			aDLoginRequest.setRoleID(roles.getAD_Role_ID());
			PO po = new Query(ctx, MWarehouse.Table_Name, "ad_client_ID = ? AND ad_org_ID = ?", trxName)
					.setParameters(adClientId, adOrgId).list().get(0);
			aDLoginRequest.setWarehouseID(po.get_ID());
			aDLoginRequest.setLang("112");
			aDLoginRequest.setStage(0);

			String err = login(aDLoginRequest, webServiceName, "createSalesOrder", serviceType);
			if (err != null && err.length() > 0) {
				response.setError(err);
				response.setIsError(true);
				return resp;
			}
			if (!serviceType.equalsIgnoreCase("createSalesOrder")) {
				response.setIsError(true);
				response.setError("Service type " + serviceType + " not configured");
				return resp;
			}

			MWarehouse warehouse = new MWarehouse(ctx, po.get_ID(), trxName);

			MDocType mDocTypee = (MDocType) new Query(ctx, MDocType.Table_Name,
					"name = 'Standard Order' and ad_client_id = ?", trxName).setParameters(adClientId).firstOnly();
			MBPartner mBPartner = (MBPartner) new Query(ctx, MBPartner.Table_Name,
					"name = ? and ad_client_id = ? and isCustomer = 'Y'", trxName)
					.setParameters(bPartnerName, adClientId).firstOnly();

			if (mBPartner == null) {

				boolean sameAddress = true;
				MLocation billAddress = null;

				billAddress = new MLocation(ctx, 0, trxName);

				String address1 = request.getBillTo();
				String address2 = null;
				String address3 = null;
				String cutString = null;
				String cutString2 = null;
				if (request.getBillTo().length() > 60) {
					address1 = request.getBillTo().substring(0, 60);
					cutString = request.getBillTo().substring(60, request.getBillTo().length());
				}
				if (cutString != null) {

					if (cutString.length() > 60) {
						cutString2 = cutString.substring(60, cutString.length());
						address2 = cutString.substring(0, 60);
					} else
						address2 = cutString.substring(0, cutString.length());
				}
				if (cutString2 != null)
					address3 = cutString2.substring(0, 60);

				billAddress.setAddress1(address1);
				billAddress.setAddress2(address2);
				billAddress.setAddress3(address3);
				billAddress.setC_Country_ID(208);
				billAddress.saveEx(trxName);

				MLocation shipAddress = null;
				if (!request.getBillTo().equals(request.getShipTo()) && request.getShipTo() != ""
						&& request.getShipTo() != null) {
					shipAddress = new MLocation(ctx, 0, trxName);

					String a1 = request.getShipTo();
					String a2 = null;
					String a3 = null;
					String c1 = null;
					String c2 = null;
					if (request.getShipTo().length() > 60) {
						a1 = request.getShipTo().substring(0, 60);
						c1 = request.getShipTo().substring(60, request.getShipTo().length());
					}
					if (c1 != null) {
						if (c1.length() > 60) {
							c2 = c1.substring(60, c1.length());
							a2 = c1.substring(0, 60);
						} else
							a2 = c1.substring(0, c1.length());
					}
					if (c2 != null)
						a3 = c2.substring(0, 60);

					shipAddress.setAddress1(a1);
					shipAddress.setAddress2(a2);
					shipAddress.setAddress3(a3);

					shipAddress.setC_Country_ID(208);
					shipAddress.saveEx(trxName);

					sameAddress = false;
				}

				mBPartner = new MBPartner(ctx, 0, trxName);
				mBPartner.setClientOrg(adClientId, adOrgId);
				mBPartner.setName(bPartnerName);
				mBPartner.setIsCustomer(true);
				mBPartner.saveEx(trxName);

				MBPartnerLocation shipBpLocation = new MBPartnerLocation(mBPartner);
				shipBpLocation.setC_Location_ID(billAddress.getC_Location_ID());

				if (!sameAddress) {
					MBPartnerLocation billBpLocation = new MBPartnerLocation(mBPartner);
					billBpLocation.setC_Location_ID(shipAddress.getC_Location_ID());
					billBpLocation.setIsShipTo(true);
					billBpLocation.setIsBillTo(false);
					billBpLocation.saveEx(trxName);

					shipBpLocation.setIsShipTo(false);
				}
				shipBpLocation.saveEx(trxName);
			}
			trx.commit();
			int orderId = 0;
			int erpId = request.getId();
			if (erpId != 0) {
				String id = String.valueOf(erpId);
				PO po2 = new Query(ctx, MOrder.Table_Name, "ad_client_id = ? AND ad_org_id = ? AND POReference = ?",
						trxName).setParameters(adClientId, adOrgId, id).firstOnly();
				if (po2 != null && po2.get_ID() != 0)
					orderId = po2.get_ID();
			}

			COrder_Custom mOrder = new COrder_Custom(ctx, orderId, trxName);
			if (orderId == 0) {
				mOrder.setC_DocTypeTarget_ID(mDocTypee.get_ID());
				mOrder.setClientOrg(adClientId, adOrgId);
				mOrder.setDescription(request.getNarration());
				mOrder.setPOReference(String.valueOf(request.getId()));

				mOrder.setDateOrdered(request.getvDate());
				mOrder.setDatePromised(request.getvDate());
				mOrder.setC_BPartner_ID(mBPartner.getC_BPartner_ID());
				mOrder.setBill_BPartner_ID(mBPartner.getC_BPartner_ID());
				mOrder.setBill_User_ID(mUser.get_ID());
				mOrder.setM_Warehouse_ID(warehouse.get_ID());
				mOrder.setIsSOTrx(true);
				mOrder.setSalesRep_ID(mUser.get_ID());
				mOrder.setAD_User_ID(mUser.get_ID());
				mOrder.setPaymentRule("B");
				
				PO po2 = new Query(ctx, X_PI_Deptartment.Table_Name,"ad_client_id=? AND value=?", trxName)
						.setParameters(adClientId,request.getDeptName()).firstOnly();
				if(po1 != null && po1.get_ID() != 0) {
					mOrder.setpidepartmentID(po2.get_ID());
				}

				int ii = DB.getSQLValueEx(null, "SELECT M_PriceList_ID FROM M_PriceList "
						+ "WHERE AD_Client_ID=? AND IsSOPriceList=? AND IsActive=? " + "ORDER BY IsDefault DESC",
						adClientId, true, true);
				if (ii != 0)
					mOrder.setM_PriceList_ID (ii);
				
				mOrder.saveEx(trxName);
			}

			List<Integer> lineIdList = new ArrayList<Integer>();
			for (DTItem line : request.getDtItems()) {
				int cOrderLineId = 0;
				int lineId = line.getId();
				lineIdList.add(lineId);
				if (orderId != 0)
					for (MOrderLine mol : mOrder.getLines()) {
						if (mol.getLine() == lineId) {
							cOrderLineId = mol.getC_OrderLine_ID();
							break;
						}
					}

				int productQTY = line.getQty();
				Double discount = line.getDiscAmt();
				String productName = line.getMaterialName();
				if (productQTY == 0)
					throw new AdempiereException("Please Enter Product Quantity > 0");

				MOrderLine mOrderLine = null;
				if (cOrderLineId == 0) {
					mOrderLine = new MOrderLine(mOrder);
				} else
					mOrderLine = new MOrderLine(ctx, cOrderLineId, trxName);

				MProduct mProduct = (MProduct) new Query(ctx, MProduct.Table_Name, "name = ? and ad_client_id = ?",
						trxName).setParameters(productName, adClientId).firstOnly();
				if (mProduct == null) {
					mProduct = new MProduct_Custom(ctx, 0, trxName);
					mProduct.setAD_Org_ID(adOrgId);
					mProduct.setValue(productName);
					mProduct.setName(productName);

					MProductCategory mProductCategory = VeUtils.getDefaultProductCategory(ctx, trxName, adClientId);
					if (mProductCategory.get_ID() == 0)
						throw new AdempiereException("No Default Product Category");
					mProduct.setM_Product_Category_ID(mProductCategory.get_ID());

					MTaxCategory mTaxCategory = VeUtils.getDefaultTaxCategory(ctx, trxName, adClientId);
					if (mTaxCategory.get_ID() == 0)
						throw new AdempiereException("No Default Tax Category");
					mProduct.setC_TaxCategory_ID(mTaxCategory.get_ID());

					mProduct.setC_UOM_ID(MUOM.getDefault_UOM_ID(ctx));

					mProduct.saveEx(trxName);

					MProductPrice mProductPrice = new MProductPrice(ctx, 0, trxName);
					mProductPrice.setM_Product_ID(mProduct.get_ID());
					mProductPrice.setPriceLimit(BigDecimal.valueOf(line.getRate()));
					mProductPrice.setPriceList(BigDecimal.valueOf(line.getRate()));
					mProductPrice.setPriceStd(BigDecimal.valueOf(line.getRate()));

					MPriceListVersion mPriceListVersion = VeUtils.getDefaultPriceListVersion(ctx, trxName, adClientId,
							"Y");
					mProductPrice.setM_PriceList_Version_ID(mPriceListVersion.get_ID());

					mProductPrice.saveEx(trxName);

					trx.commit();
				}

				mOrderLine.setM_Product_ID(mProduct.getM_Product_ID());
				mOrderLine.setDescription(line.getNarration());
				mOrderLine.setQty(BigDecimal.valueOf(productQTY));
//					mOrderLine.setC_Tax_ID(tax.getC_Tax_ID());
				mOrderLine.setDiscount(BigDecimal.valueOf(discount));
				mOrderLine.setC_UOM_ID(mProduct.getC_UOM_ID());
				mOrderLine.setLine(line.getId());
				mOrderLine.saveEx(trxName);
			}

			for (MOrderLine orderline : mOrder.getLines()) {
				if (!lineIdList.contains(orderline.getLine()))
					orderline.delete(true);
			}

			mOrder.saveEx();
			trx.commit();
			response.setIsError(false);
			response.setRecordID(mOrder.get_ID());
		} catch (Exception e) {
			response.setError(e.getLocalizedMessage());
			response.setIsError(true);
			return resp;
		} finally {
			trx.commit();
			getCompiereService().disconnect();
			trx.close();
		}
		return resp;
	}

	@Override
	public GetDeptAcessResponseDocument getDeptAcess(GetDeptAcessRequestDocument req) {
		GetDeptAcessResponseDocument res = GetDeptAcessResponseDocument.Factory.newInstance();
		GetDeptAcessResponse response = res.addNewGetDeptAcessResponse();
		GetDeptAcessRequest request = req.getGetDeptAcessRequest();
		ADLoginRequest loginReq = request.getADLoginRequest();
		String serviceType = request.getServiceType();
		Trx trx = null;
		try {
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			getCompiereService().connect();

			org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = VeUtils.convertAdLogin(loginReq);
			String err = login(adLoginReq, webServiceName, "getDeptAcess", serviceType);
			if (err != null && err.length() > 0) {
				response.setError(err);
				response.setIsError(true);
				return res;
			}

			if (!serviceType.equalsIgnoreCase("getDeptAcess")) {
				response.setIsError(true);
				response.setError("Service type " + serviceType + " not configured");
				return res;
			}

			int userId = request.getUserId();
			MUser_Custom mUser = new MUser_Custom(ctx, userId, trxName);
			if (mUser == null || mUser.getAD_User_ID() == 0) {
				response.setError("Invalid User ID " + userId + "");
				response.setIsError(true);
				return res;
			}

			String deptAcess = mUser.getDeptAcess();
			if (deptAcess != null || deptAcess != "") {
				String[] array = deptAcess.split("\\|");
				for (String element : array) {
					DeptAcess acess = response.addNewDeptAcess();
					acess.setDeptName(element);
				}
			}

			response.setIsError(false);

			trx.commit();
		} catch (Exception e) {
			response.setIsError(true);
			response.setError(e.getMessage());
			return res;
		} finally {
			getCompiereService().disconnect();
			trx.close();
		}
		return res;
	}

	@Override
	public StandardResponseDocument markShipmentPacked(MarkShipmentPackedRequestDocument req) {
		StandardResponseDocument res = StandardResponseDocument.Factory.newInstance();
		StandardResponse response = res.addNewStandardResponse();
		MarkShipmentPackedRequest request = req.getMarkShipmentPackedRequest();
		ADLoginRequest loginReq = request.getADLoginRequest();
		String serviceType = request.getServiceType();
		Trx trx = null;
		try {
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			getCompiereService().connect();

			org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = VeUtils.convertAdLogin(loginReq);
			String err = login(adLoginReq, webServiceName, "markShipmentPacked", serviceType);
			if (err != null && err.length() > 0) {
				response.setError(err);
				response.setIsError(true);
				return res;
			}

			if (!serviceType.equalsIgnoreCase("markShipmentPacked")) {
				response.setIsError(true);
				response.setError("Service type " + serviceType + " not configured");
				return res;
			}

			int mInoutLineId = request.getMInoutId();
			MInOut_Custom line = new MInOut_Custom(ctx, mInoutLineId, trxName);
			line.setPickStatus(request.getStatus());
			line.saveEx();

			response.setIsError(false);

			trx.commit();
		} catch (Exception e) {
			response.setIsError(true);
			response.setError(e.getMessage());
			return res;
		} finally {
			getCompiereService().disconnect();
			trx.close();
		}
		return res;
	}

	@Override
	public GetMOComponentsResponseDocument getMOComponents(GetMOComponentsRequestDocument req) {
		GetMOComponentsResponseDocument res = GetMOComponentsResponseDocument.Factory.newInstance();
		GetMOComponentsResponse response = res.addNewGetMOComponentsResponse();
		GetMOComponentsRequest request = req.getGetMOComponentsRequest();
		ADLoginRequest loginReq = request.getADLoginRequest();
		String serviceType = request.getServiceType();
		boolean isPackingAndAssembly = request.getIsPackingAndAssembly();
		
		Trx trx = null;
		try {
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			getCompiereService().connect();

			org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = VeUtils.convertAdLogin(loginReq);
			String err = login(adLoginReq, webServiceName, "getMOComponents", serviceType);
			if (err != null && err.length() > 0) {
				response.setError(err);
				response.setIsError(true);
				return res;
			}

			if (!serviceType.equalsIgnoreCase("getMOComponents")) {
				response.setIsError(true);
				response.setError("Service type " + serviceType + " not configured");
				return res;
			}

			int orgId = loginReq.getOrgID();
			int clientId = loginReq.getClientID();

			int userId = Env.getAD_User_ID(ctx);
			MUser_Custom user = new MUser_Custom(ctx, userId, trxName);
			X_PI_Deptartment dept = null;
			
			if (isPackingAndAssembly) 
				dept = VeUtils.getFGSwitchAndSocketDepartment(clientId, ctx, trxName);
			else
				dept = new X_PI_Deptartment(ctx, user.getPI_DEPARTMENT_ID(), trxName);
			
			if(dept == null){
				StringBuilder builder = new StringBuilder();
				if(isPackingAndAssembly)
					builder.append("FG-Switch & Acc.");
				
				builder.append("Department Not Found");
				response.setError(builder.toString());
				response.setIsError(true);
				return res;
			}

			List<PO> poList = MProduct_Custom.getProductsWithBom(clientId, orgId, ctx, trxName, dept.get_ID());
			if (poList != null && poList.size() != 0) {
				for (PO po : poList) {
					MProduct_Custom product = new MProduct_Custom(ctx, po.get_ID(), trxName);
					ProductBom productBom = response.addNewProductBom();
					productBom.setProductId(product.getM_Product_ID());
					productBom.setProductName(product.getName());

					PO bom = VeUtils.getProductBom(product.getM_Product_ID(), ctx, trxName);
					if (bom != null && bom.get_ID() != 0) {
						BomFormula bomFormula = productBom.addNewBomFormula();
						X_PP_Product_BOM x_PP_Product_BOM = new X_PP_Product_BOM(ctx, bom.get_ID(), trxName);
						bomFormula.setBomFormulaId(x_PP_Product_BOM.getPP_Product_BOM_ID());
						bomFormula.setBomFormulaName(x_PP_Product_BOM.getName());
						List<PO> lineList = VeUtils.getProductBomLine(x_PP_Product_BOM.getPP_Product_BOM_ID(), ctx,
								trxName);
						if (lineList != null && lineList.size() != 0) {
							for (PO poLine : lineList) {
								X_PP_Product_BOMLine line = new X_PP_Product_BOMLine(ctx, poLine.get_ID(), trxName);
								BOMComponents component = bomFormula.addNewBomComponents();
								component.setComponentId(line.getPP_Product_BOMLine_ID());
								component.setProductId(line.getM_Product_ID());
								component.setProductName(line.getM_Product().getName());
								component.setQuantity(line.getQtyBOM().floatValue());
							}
						}
					}else
						productBom.addNewBomFormula();
				}
			}

			poList = VeUtils.getWarehouseList(clientId, orgId, ctx, trxName);
			if (poList != null && poList.size() != 0) {
				for (PO po : poList) {
					MWarehouse warehouse = new MWarehouse(ctx, po.get_ID(), trxName);
					WarehouseBom warehouseBom = response.addNewWarehouseBom();
					warehouseBom.setWarehouseId(warehouse.getM_Warehouse_ID());
					warehouseBom.setWarehouseName(warehouse.getName());

					List<PO> resourceList = VeUtils.getResourceForWarehouse(warehouse.getM_Warehouse_ID(), ctx,
							trxName);
					if (resourceList != null && resourceList.size() != 0) {
						for (PO resourcePo : resourceList) {
							X_S_Resource resource = new X_S_Resource(ctx, resourcePo.get_ID(), trxName);
							ResourceBom resourceBom = warehouseBom.addNewResourceBom();
							resourceBom.setResourceId(resource.getS_Resource_ID());
							resourceBom.setResourceName(resource.getName());
						}
					}
				}
			}
			List<PO> contractorList = VeUtils.getContractorUser(clientId, ctx, trxName);
			if(contractorList != null && contractorList.size() != 0) {
				for(PO list : contractorList) {
					MUser user2 = new MUser(ctx, list.get_ID(), trxName);
					Agent agent = response.addNewAgent();
					agent.setAgentId(user2.getAD_User_ID());
					agent.setAgentName(user2.getName());
				}
			}else {
				response.addNewAgent();
			}


			poList = VeUtils.getWorkflowList(clientId, orgId, ctx, trxName);
			if (poList != null && poList.size() != 0) {
				for (PO po : poList) {
					X_AD_Workflow workflow = new X_AD_Workflow(ctx, po.get_ID(), trxName);
					Workflow workflowBom = response.addNewWorkflow();
					workflowBom.setWorkflowId(workflow.getAD_Workflow_ID());
					workflowBom.setWorkflowName(workflow.getName());
				}
			}

			List<String> priorityList = new ArrayList<>(Arrays.asList("Urgent", "High", "Medium", "Low"));
			priorityList.forEach(it -> {
				Priority priority = response.addNewPriority();
				priority.setPriorityName(it);
			});

			List<PO> machineList = VeUtils.getMachineConfList(clientId, orgId, ctx, trxName);
			if (machineList != null && machineList.size() != 0) {
				for (PO list : machineList) {
					X_pi_machineconf machineConf = new X_pi_machineconf(ctx, list.get_ID(), trxName);
					Machinelist machinelist = response.addNewMachinelist();
					machinelist.setMachineConfigId(machineConf.getpi_machineconf_ID());
					machinelist.setMachineName(machineConf.getName());
					machinelist.setSerialNumber(machineConf.getserialnumber());
				}
			} else
				response.addNewMachinelist();

			response.setIsError(false);
			trx.commit();
		} catch (Exception e) {
			response.setIsError(true);
			response.setError(e.getMessage());
			return res;
		} finally {
			getCompiereService().disconnect();
			trx.close();
		}
		return res;
	}

	@Override
	public CreateMOResponseDocument createMO(CreateMORequestDocument req) {
		CreateMOResponseDocument res = CreateMOResponseDocument.Factory.newInstance();
//		CreateMOResponse response = res.addNewCreateMOResponse();
//		CreateMORequest request = req.getCreateMORequest();
//		ADLoginRequest loginReq = request.getADLoginRequest();
//		String serviceType = request.getServiceType();
//		Trx trx = null;
//		try {
//			CompiereService m_cs = getCompiereService();
//			Properties ctx = m_cs.getCtx();
//			String trxName = Trx.createTrxName(getClass().getName() + "_");
//			trx = Trx.get(trxName, true);
//			trx.start();
//			getCompiereService().connect();
//
//			org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = VeUtils.convertAdLogin(loginReq);
//			String err = login(adLoginReq, webServiceName, "createMO", serviceType);
//			if (err != null && err.length() > 0) {
//				response.setError(err);
//				response.setIsError(true);
//				return res;
//			}
//
//			if (!serviceType.equalsIgnoreCase("createMO")) {
//				response.setIsError(true);
//				response.setError("Service type " + serviceType + " not configured");
//				return res;
//			}
//
//			int userId = Env.getAD_User_ID(ctx);
//			MUser_Custom user = new MUser_Custom(ctx, userId, trxName);
//			X_PI_Deptartment dept = new X_PI_Deptartment(ctx, user.getPI_DEPARTMENT_ID(), trxName);
//
//			String operation = request.getOperation();
//			MPPOrder mOrder = null;
//			if (operation.equalsIgnoreCase("CREATE")) {
//
//				int clientId = loginReq.getClientID();
//				Env.setContext(Env.getCtx(), Env.AD_CLIENT_ID, clientId);
//
//				mOrder = new MPPOrder(ctx, 0, trxName);
//				mOrder.setAD_Org_ID(loginReq.getOrgID());
//
//				int docTypeId = VeUtils.getDocTypeId(clientId, ctx, trxName);
//				mOrder.setC_DocType_ID(docTypeId);
//				mOrder.setC_DocTypeTarget_ID(docTypeId);
//
//				MProduct_Custom product = new MProduct_Custom(ctx, request.getProductId(), trxName);
//				mOrder.setM_Product_ID(product.getM_Product_ID());
//				mOrder.setC_UOM_ID(product.getC_UOM_ID());
//
//				X_PP_Product_BOM productBom = new X_PP_Product_BOM(ctx, request.getBomFormulaId(), trxName);
//
//				mOrder.setDescription(request.getDescription());
//				mOrder.setPP_Product_BOM_ID(productBom.getPP_Product_BOM_ID());
//				mOrder.setM_Warehouse_ID(loginReq.getWarehouseID());
//				mOrder.setS_Resource_ID(request.getResourceOrPlantId());
//				mOrder.setAD_Workflow_ID(request.getWorkflowId());
//				mOrder.setPlanner_ID(request.getPlannerId());
//
//				if (request.getDateOrdered() != null)
//					mOrder.setDateOrdered(new Timestamp(request.getDateOrdered().getTimeInMillis()));
//				if (request.getDatePromised() != null)
//					mOrder.setDatePromised(new Timestamp(request.getDatePromised().getTimeInMillis()));
//				if (!request.getDateStartSchedule().toString().equals(""))
//					mOrder.setDateStartSchedule(new Timestamp(request.getDateStartSchedule().getTimeInMillis()));
//				if (request.getDateFinished() != null)
//					mOrder.setDateFinish(new Timestamp(request.getDateFinished().getTimeInMillis()));
//
//				mOrder.setDocAction(DocAction.ACTION_Prepare);
//				mOrder.setDocStatus(DocAction.STATUS_Drafted);
//				mOrder.setIsApproved(false);
//				mOrder.setIsPrinted(false);
//				mOrder.setIsSelected(false);
//				mOrder.setProcessed(false);
//				mOrder.setIsSOTrx(false);
//				mOrder.setProcessing(false);
//				mOrder.setPosted(false);
//				mOrder.setLine(10);
//				mOrder.setM_AttributeSetInstance_ID(0);
//				mOrder.setFloatAfter(BigDecimal.valueOf(0));
//				mOrder.setFloatBefored(BigDecimal.valueOf(0));
//
//				mOrder.setQtyEntered(BigDecimal.valueOf(request.getQtyToDeliver()));
//				mOrder.setQtyBatchs(BigDecimal.valueOf(request.getQtyToDeliver()));
//				mOrder.setQtyBatchSize(Env.ONE);
//				mOrder.setQtyOrdered(BigDecimal.valueOf(request.getQtyToDeliver()));
//				mOrder.setQtyReserved(BigDecimal.valueOf(request.getQtyToDeliver()));
//
////			mOrder.setPriorityRule(VeUtils.getpriorityRule(request.getPriority()));
//				mOrder.setPriorityRule(X_PP_Order.PRIORITYRULE_Medium);
//				mOrder.setQtyDelivered(Env.ZERO);
//				mOrder.setQtyReject(BigDecimal.valueOf(request.getQtyRejected()));
//				mOrder.setQtyScrap(Env.ZERO);
//				mOrder.setYield(Env.ONEHUNDRED);
//
//				mOrder.saveEx();
//
//				MPPOrderCustom mOrderCustom = new MPPOrderCustom(ctx, mOrder.get_ID(), trxName);
//				mOrderCustom.setPI_DEPARTMENT_ID(dept.get_ID());
//				mOrderCustom.setStatus(request.getStatus());
//				mOrderCustom.setIsmanufacturing(request.getIsManufacturing());
//				mOrderCustom.saveEx();
//
//				if (request.getMachineNo() != 0) {
//					MPPOrderCustom mPPOrderCustom = new MPPOrderCustom(ctx, mOrder.get_ID(), trxName);
//					mPPOrderCustom.setMachineNo(request.getMachineNo());
//					mPPOrderCustom.saveEx();
//				}
//
//				MWarehouse warehouse = new MWarehouse(ctx, loginReq.getWarehouseID(), trxName);
//				MLocator locator = warehouse.getDefaultLocator();
//				MStorageOnHand storage = MStorageOnHand.get(ctx, locator.getM_Locator_ID(), product.getM_Product_ID(),
//						0, new Timestamp(request.getDateOrdered().getTimeInMillis()), trxName);
//
//				if (storage == null) {
//					storage = MStorageOnHand.getCreate(ctx, locator.getM_Locator_ID(), product.getM_Product_ID(), 0,
//							new Timestamp(request.getDateOrdered().getTimeInMillis()), trxName);
//				}
//
//				BigDecimal qtyOnHand = storage.getQtyOnHand().add(mOrder.getQtyEntered());
//				storage.setQtyOnHand(qtyOnHand);
//				storage.saveEx(trxName);
//
//			} else if (operation.equalsIgnoreCase("UPDATE")) {
//
//				mOrder = new MPPOrder(ctx, request.getMOrderId(), trxName);
//				MPPOrderCustom mPPOrderCustom = new MPPOrderCustom(ctx, mOrder.get_ID(), trxName);
//				if (request.getMachineNo() != 0) {
//					mPPOrderCustom.setMachineNo(request.getMachineNo());
//					mPPOrderCustom.saveEx();
//				}
//
//				if (request.getQtyToDeliver() != 0
//						&& (mOrder.getQtyEntered().intValue() != request.getQtyToDeliver())) {
//					mOrder.setQtyEntered(BigDecimal.valueOf(request.getQtyToDeliver()));
//					mOrder.setQtyBatchs(BigDecimal.valueOf(request.getQtyToDeliver()));
//					mOrder.setQtyOrdered(BigDecimal.valueOf(request.getQtyToDeliver()));
//				}
//				if (request.getDescription() != null && !request.getDescription().isBlank()
//						&& (!request.getDescription().equalsIgnoreCase(mOrder.getDescription())))
//					mOrder.setDescription(request.getDescription());
//				if (request.getStatus() != null && !request.getStatus().isBlank()) {
//					mPPOrderCustom.setStatus(request.getStatus());
//					mPPOrderCustom.saveEx();
//				}
//
//				if (request.getPlannerId() != 0 && request.getPlannerId() != mOrder.getPlanner_ID()) {
//					mOrder.setPlanner_ID(request.getPlannerId());
//				}
//				
//				if (request.getWireLength() != 0 && request.getWireLength() != mPPOrderCustom.getWireLength()) {
//					mPPOrderCustom.setWireLength(request.getWireLength());
//					mPPOrderCustom.saveEx();
//				}
//
//				mOrder.saveEx();
//			} else if (operation.equalsIgnoreCase("DELETE")) {
//
//				mOrder = new MPPOrder(ctx, request.getMOrderId(), trxName);
//				mOrder.deleteEx(true);
//			}
//
//			if (mOrder.get_ID() != 0) {
//				response.setMOrderId(mOrder.get_ID());
//				response.setDocumentNo(mOrder.getDocumentNo());
//			}
//
//			response.setIsError(false);
//			trx.commit();
//
//			trx.commit();
//
//		} catch (Exception e) {
//			response.setIsError(true);
//			response.setError(e.getMessage());
//			return res;
//		} finally {
//			getCompiereService().disconnect();
//			trx.close();
//		}
		return res;
	}

	@Override
	public MOListResponseDocument getMOList(MOListRequestDocument req) {
		MOListResponseDocument res = MOListResponseDocument.Factory.newInstance();
		MOListResponse response = res.addNewMOListResponse();
		MOListRequest request = req.getMOListRequest();
		ADLoginRequest loginReq = request.getADLoginRequest();
		String serviceType = request.getServiceType();
		String searchKey = request.getSearchKey();
		boolean isManufacturing = request.getIsManufacturing();
		Trx trx = null;
		
		// Get pagination details from request
        int pageSize = request.getPageSize(); // Number of records per page
        int pageNumber = request.getPageNumber(); // Current page number
		try {
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			getCompiereService().connect();

			org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = VeUtils.convertAdLogin(loginReq);
			String err = login(adLoginReq, webServiceName, "getMOList", serviceType);
			if (err != null && err.length() > 0) {
				response.setError(err);
				response.setIsError(true);
				return res;
			}

			if (!serviceType.equalsIgnoreCase("getMOList")) {
				response.setIsError(true);
				response.setError("Service type " + serviceType + " not configured");
				return res;
			}

			int userId = Env.getAD_User_ID(ctx);
			MUser_Custom user = new MUser_Custom(ctx, userId, trxName);
			X_PI_Deptartment dept = new X_PI_Deptartment(ctx, user.getPI_DEPARTMENT_ID(), trxName);

			String status = request.getStatus();
			if (status != null && status.isEmpty())
				status = null;

			int clientId = loginReq.getClientID();
			int orgId = loginReq.getOrgID();
			List<PO> mOList = VeUtils.getMOList(clientId, orgId, ctx, trxName, searchKey, isManufacturing);
			
			// Calculate offset and end index for pagination
			int offset = (pageNumber > 0 && pageSize > 0) ? (pageNumber - 1) * pageSize : 0;
			int end = (pageNumber > 0 && pageSize > 0) ? Math.min(offset + pageSize, mOList.size()) : mOList.size();

			if (mOList != null && mOList.size() != 0) {
//				for (PO po : mOList) {
				for (int i = offset; i < end; i++) {  // Iterate over the paginated subset
			        PO po = mOList.get(i);
					MPPOrderCustom mOrder = new MPPOrderCustom(ctx, po.get_ID(), trxName);
					String orderStatus = mOrder.getStatus();
					boolean isStatusValid = status == null || (status != null && status.equals(orderStatus));

					int deptId = mOrder.getPI_DEPARTMENT_ID();
					if (isStatusValid && (deptId == 0 || (dept.getPI_Deptartment_ID() == deptId))) {
						MOList mo = response.addNewMOList();
						mo.setMOrderId(mOrder.getPP_Order_ID());
						mo.setDocumentNumber(mOrder.getDocumentNo());
						mo.setDescription(mOrder.getDescription() == null ? "" : mOrder.getDescription());
						mo.setProductId(mOrder.getM_Product_ID());
						mo.setWarehouseId(mOrder.getM_Warehouse_ID());
						mo.setPriority(mOrder.getPriorityRule());
						mo.setQtyEntered(mOrder.getQtyEntered().intValue());
						mo.setQtyDelivered(mOrder.getQtyDelivered().intValue());
						mo.setQtyToDeliver(mOrder.getQtyToDeliver().intValue());
						mo.setProductName(mOrder.getM_Product().getName());
						mo.setWorkflowId(mOrder.getAD_Workflow_ID());
						mo.setWorkflowName(mOrder.getAD_Workflow().getName());
						mo.setIsManufacturing(isManufacturing);

						boolean compleAction = false;
						boolean createRecipt = false;
						if (user.isDeptHead())
							compleAction = true;
						else
							createRecipt = true;
						mo.setCompleAction(compleAction);
						mo.setCreateReceipt(createRecipt);

						if (mOrder.getDocStatus().equals(DocAction.ACTION_Complete))
							mo.setToMarkForComplete(false);
						else
							mo.setToMarkForComplete(true);

						Calendar calendar = Calendar.getInstance();
						if (mOrder.getDatePromised() != null)
							calendar.setTimeInMillis(mOrder.getDatePromised().getTime());
						mo.setDatePromised(calendar);
						calendar = Calendar.getInstance();
						if (mOrder.getDateFinish() != null)
							calendar.setTimeInMillis(mOrder.getDateFinish().getTime());
						mo.setDateFinished(calendar);
						calendar = Calendar.getInstance();
						if (mOrder.getDateOrdered() != null)
							calendar.setTimeInMillis(mOrder.getDateOrdered().getTime());
						mo.setDateOrdered(calendar);
						calendar = Calendar.getInstance();
						if (mOrder.getDateStartSchedule() != null)
							calendar.setTimeInMillis(mOrder.getDateStartSchedule().getTime());
						mo.setDateStartSchedule(calendar);
					}
				}
			} else {
				response.setIsError(false);
				response.setError("No Orders Found");
				return res;
			}

			response.setIsError(false);

			trx.commit();
		} catch (Exception e) {
			response.setIsError(true);
			response.setError(e.getMessage());
			return res;
		} finally {
			getCompiereService().disconnect();
			trx.close();
		}
		return res;
	}

	@Override
	public GetClientConfigResponseDocument getClientConfig(GetClientConfigRequestDocument req) {
		GetClientConfigResponseDocument res = GetClientConfigResponseDocument.Factory.newInstance();
		GetClientConfigResponse response = res.addNewGetClientConfigResponse();
		GetClientConfigRequest request = req.getGetClientConfigRequest();
		ADLoginRequest loginReq = request.getADLoginRequest();
		String serviceType = request.getServiceType();
		Trx trx = null;
		try {
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			getCompiereService().connect();

			org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = VeUtils.convertAdLogin(loginReq);
			String err = login(adLoginReq, webServiceName, "getClientConfig", serviceType);
			if (err != null && err.length() > 0) {
				response.setError(err);
				response.setIsError(true);
				return res;
			}

			if (!serviceType.equalsIgnoreCase("getClientConfig")) {
				response.setIsError(true);
				response.setError("Service type " + serviceType + " not configured");
				return res;
			}

			int userId = Env.getAD_User_ID(ctx);
			MUser_Custom user = new MUser_Custom(ctx, userId, trxName);
			X_PI_Deptartment dept = new X_PI_Deptartment(ctx, user.getPI_DEPARTMENT_ID(), trxName);

			ClientConfig userConfig = response.addNewClientConfig();
			userConfig.setKey("department");
			userConfig.setValue(dept.getdeptname());

			ClientConfig userDept = response.addNewClientConfig();
			userDept.setKey("departmentId");
			userDept.setValue(dept.getPI_Deptartment_ID());

			ClientConfig shipmentStatus = response.addNewClientConfig();
			shipmentStatus.setKey("shipmentStatusKey");
			shipmentStatus.setValue(true);

			ClientConfig deptConfig = response.addNewClientConfig();
			deptConfig.setKey("PackingModule");
			deptConfig.setValue(dept.ispackingmodule());

			ClientConfig mo = response.addNewClientConfig();
			mo.setKey("manufacturingModule");
			mo.setValue(dept.ismanufacturingModule());

			ClientConfig dispatch = response.addNewClientConfig();
			dispatch.setKey("dispatch");
			dispatch.setValue(dept.isdispatch());
			
			ClientConfig packingInReceiving = response.addNewClientConfig();
			packingInReceiving.setKey("pcknginrecivn");
			packingInReceiving.setValue(dept.ispcknginrecivn());
			
			ClientConfig isMCBReceiving = response.addNewClientConfig();
			isMCBReceiving.setKey("ismcbreceiving");
			isMCBReceiving.setValue(dept.isMcbReceiving());
			
			ClientConfig clientName = response.addNewClientConfig();
			clientName.setKey("clientName");
			clientName.setValue("VE");
			
			ClientConfig salesOrderMobile = response.addNewClientConfig();
			salesOrderMobile.setKey("salesOrderMobile");
			salesOrderMobile.setValue(true);

			AdRole_Custom adRole_Custom = new AdRole_Custom(ctx, adLoginReq.getRoleID(), trxName, null);

			ClientConfig purchaseOrderMobile = response.addNewClientConfig();
			purchaseOrderMobile.setKey("purchaseOrderMobile");
			purchaseOrderMobile.setValue(true);

			String deptName = dept.getdeptname();  

			if(deptName != null && deptName.equalsIgnoreCase("FG-Wire")) {
                List<MLocator> mLocatorArray = VeUtils.getLocatorsByDepartment(ctx, trxName, loginReq.getWarehouseID(),
                        dept.getPI_Deptartment_ID(), "storage", "Y");
                
                int storageLocatorId = 0;
                if (mLocatorArray != null && mLocatorArray.size() != 0)
                    storageLocatorId = mLocatorArray.get(mLocatorArray.size() - 1).get_ID();
                
                MLocator locator = new MLocator(ctx, storageLocatorId, trxName);
                
                ClientConfig cfg1 = response.addNewClientConfig();
                cfg1.setKey("searchKey");
                cfg1.setValue(locator.getValue());

                ClientConfig cfg2 = response.addNewClientConfig();
                cfg2.setKey("locatorID");
                cfg2.setValue(storageLocatorId);
            }

//			ClientConfig subDept = response.addNewClientConfig();
//			subDept.setKey("subDepartment");
//			subDept.setValue(dept.isSubDepartment());
//			
//			if (user.isDeptHead() || user.isDeptAgent()) {
//				ClientConfig deptHead = response.addNewClientConfig();
//				deptHead.setKey("departmenttHead");
//				deptHead.setValue(user.isDeptHead());
//
//				ClientConfig deptAgent = response.addNewClientConfig();
//				deptAgent.setKey("departmentAgent");
//				deptAgent.setValue(user.isDeptAgent());
//			}

			LinkedHashMap<Object, Object> config = VEClientConfig.config;
			config.keySet().forEach(it -> {
				ClientConfig clientConfig = response.addNewClientConfig();
				clientConfig.setKey(it.toString());
				clientConfig.setValue(config.get(it));
			});

			response.setIsError(false);

			trx.commit();
		} catch (Exception e) {
			response.setIsError(true);
			response.setError(e.getMessage());
			return res;
		} finally {
			getCompiereService().disconnect();
			trx.close();
		}
		return res;
	}

	@Override
	public MODetailResponseDocument getMODetail(MODetailRequestDocument req) {
		MODetailResponseDocument res = MODetailResponseDocument.Factory.newInstance();
		MODetailResponse response = res.addNewMODetailResponse();
		MODetailRequest request = req.getMODetailRequest();
		ADLoginRequest loginReq = request.getADLoginRequest();
		String serviceType = request.getServiceType();
		Trx trx = null;
		int locatorId = 0;
		try {
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			getCompiereService().connect();

			org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = VeUtils.convertAdLogin(loginReq);
			String err = login(adLoginReq, webServiceName, "getMODetail", serviceType);
			if (err != null && err.length() > 0) {
				response.setError(err);
				response.setIsError(true);
				return res;
			}

			if (!serviceType.equalsIgnoreCase("getMODetail")) {
				response.setIsError(true);
				response.setError("Service type " + serviceType + " not configured");
				return res;
			}

			int userId = Env.getAD_User_ID(ctx);
			MUser_Custom user = new MUser_Custom(ctx, userId, trxName);
			X_PI_Deptartment dept = new X_PI_Deptartment(ctx, user.getPI_DEPARTMENT_ID(), trxName);

			AdRole_Custom role_Custom = new AdRole_Custom(ctx, loginReq.getRoleID(), trxName, null);

			int deptId = dept.getPI_Deptartment_ID();
			if (dept.getreceiptTransferDepartmentId() != 0 && role_Custom.isProductionSwitchSocketAgent())
				deptId = dept.getreceiptTransferDepartmentId();
			List<MLocator> mLocatorArray = VeUtils.getLocatorsByDepartment(ctx, trxName, loginReq.getWarehouseID(),
					deptId, "receiving", "Y");
			for (MLocator locatroList : mLocatorArray) {
				locatorId = locatroList.getM_Locator_ID();
			}

			int mOrderId = request.getMOrderId();
			MPPOrderCustom order = new MPPOrderCustom(ctx, mOrderId, trxName);
			if (order.get_ID() == 0) {
				response.setIsError(true);
				response.setError("Invalid OrderId: " + mOrderId + "");
				return res;
			}
			response.setMOrderId(order.getPP_Order_ID());
			response.setDocumentNumber(order.getDocumentNo());

			response.setDescription(order.getDescription() == null ? "" : order.getDescription());
			response.setProductId(order.getM_Product_ID());
			response.setWarehouseId(order.getM_Warehouse_ID());
			response.setProductName(order.getM_Product().getName());
			response.setWorkflowId(order.getAD_Workflow_ID());
			response.setWorkflowName(order.getAD_Workflow().getName());
			response.setResourceId(order.getS_Resource_ID());
			response.setResourceName(order.getS_Resource().getName());
//			response.setBomId(order.getBo);
			response.setPriority(order.getPriorityRule());
			response.setQtyEntered((order.getQtyEntered().intValue()));
			response.setQtyDelivered(order.getQtyDelivered().intValue());
			response.setQtyToDeliver(order.getQtyEntered().intValue() - order.getQtyDelivered().intValue());
			response.setLocatorId(locatorId);
			response.setMachineNo(order.getMachineNo());
			response.setPlannerId(order.getPlanner_ID());
			response.setWireLength(order.getWireLength());
			//			response.setPlannerName(order.getPlanner().getName());
			response.setPlannerName(order.getPlanner().getName() != null ? order.getPlanner().getName() : "");
			boolean flag = true;
			if (user.isDeptHead())
				flag = false;
			response.setIsProductionAgent(flag);
			;

			Calendar calendar = Calendar.getInstance();
			if (order.getDatePromised() != null)
				calendar.setTimeInMillis(order.getDatePromised().getTime());
			response.setDatePromised(calendar);
			calendar = Calendar.getInstance();
			if (order.getDateFinish() != null)
				calendar.setTimeInMillis(order.getDateFinish().getTime());
			response.setDateFinished(calendar);
			calendar = Calendar.getInstance();
			if (order.getDateOrdered() != null)
				calendar.setTimeInMillis(order.getDateOrdered().getTime());
			response.setDateOrdered(calendar);
			calendar = Calendar.getInstance();
			if (order.getDateStartSchedule() != null)
				calendar.setTimeInMillis(order.getDateStartSchedule().getTime());
			response.setDateStartSchedule(calendar);

			I_PP_Product_BOM orderBom = order.getPP_Product_BOM();
			OrderBom bom = response.addNewOrderBom();
			bom.setOrderBomId(orderBom.getPP_Product_BOM_ID());
			bom.setSearchKey(orderBom.getName());
			bom.setName(orderBom.getName());

			bom.setDescription(orderBom.getDescription() == null ? "" : orderBom.getDescription());
			bom.setProductId(orderBom.getM_Product_ID());
			bom.setProductName(orderBom.getM_Product().getName());

			response.setIsError(false);
			trx.commit();
		} catch (Exception e) {
			response.setIsError(true);
			response.setError(e.getMessage());
			return res;
		} finally {
			getCompiereService().disconnect();
			trx.close();
		}
		return res;
	}

	@Override
	public StandardResponseDocument createPAOrder(PAOrderRequestDocument req) {
		StandardResponseDocument res = StandardResponseDocument.Factory.newInstance();
		StandardResponse response = res.addNewStandardResponse();
		PAOrderRequest request = req.getPAOrderRequest();
		ADLoginRequest loginReq = request.getADLoginRequest();
		String serviceType = request.getServiceType();
		int packingQty = request.getPackingQty();
		int receivedQty = request.getReceivedQty();
		Trx trx = null;
		try {
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			getCompiereService().connect();

			org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = VeUtils.convertAdLogin(loginReq);
			String err = login(adLoginReq, webServiceName, "createPAOrder", serviceType);
			if (err != null && err.length() > 0) {
				response.setError(err);
				response.setIsError(true);
				return res;
			}

			if (!serviceType.equalsIgnoreCase("createPAOrder")) {
				response.setIsError(true);
				response.setError("Service type " + serviceType + " not configured");
				return res;
			}

			int userId = Env.getAD_User_ID(ctx);
			MUser_Custom user = new MUser_Custom(ctx, userId, trxName);
			X_PI_Deptartment dept = new X_PI_Deptartment(ctx, user.getPI_DEPARTMENT_ID(), trxName);

			String operation = request.getOperation();
			X_pi_paorder paOrder = null;
			if (operation.equals("CREATE")) {
				paOrder = new X_pi_paorder(ctx, 0, trxName);
				paOrder.setM_Product_ID(request.getProductId());
				paOrder.setcontractorId(request.getContractorId());
				paOrder.setquantity(BigDecimal.valueOf(request.getQuantity()));
				paOrder.setStatus(request.getStatus());
				paOrder.setDescription(request.getDescription());
				paOrder.setPI_Deptartment_ID(dept.get_ID());
				paOrder.setpackingorder(request.getPackagingOrder());
				paOrder.setIsAutomation(request.getIsAutomation());	
				paOrder.saveEx();
			} else if (operation.equals("UPDATE")) {
				paOrder = new X_pi_paorder(ctx, request.getPAOrderId(), trxName);

				if (request.getQuantity() != 0 && (paOrder.getquantity().intValue() != request.getQuantity()))
					paOrder.setquantity(BigDecimal.valueOf(request.getQuantity()));

				if (request.getDescription() != null && !request.getDescription().isBlank()
						&& (!request.getDescription().equalsIgnoreCase(paOrder.getDescription())))
					paOrder.setDescription(request.getDescription());
				if (request.getStatus() != null && !request.getStatus().isBlank())
					paOrder.setStatus(request.getStatus());

				if (request.getContractorId() != 0 && request.getContractorId() != paOrder.getcontractorId())
					paOrder.setcontractorId(request.getContractorId());

				if (request.getProcessed() != paOrder.isProcessed())
					paOrder.setProcessed(request.getProcessed());
				
				if (request.getIsRMPicked() != paOrder.isrmpicked())
					paOrder.setisrmpicked(request.getIsRMPicked());
				
				if (request.getPackagingOrder() != paOrder.ispackingorder())
					paOrder.setpackingorder(request.getPackagingOrder());
				
				paOrder.saveEx();
				
				String Query = "SELECT COALESCE(SUM(packingqty), 0) FROM pi_paorderpackingqty WHERE pi_paorder_id = ?";
				BigDecimal sum = DB.getSQLValueBD(trxName, Query, paOrder.getpi_paorder_ID());
				int pendingQty = paOrder.getquantity().intValue() - sum.intValue();
				if(packingQty > pendingQty) {
					response.setIsError(true);
					response.setError("The packing quantity is not equal to or not less than the actual quantity");
				}
				
				PO po = new Query(ctx, X_pi_paorderpackingqty.Table_Name, "pi_paorder_id = ? AND received = 'N'", trxName)
						.setParameters(paOrder.getpi_paorder_ID()).firstOnly();
				if (po != null && po.get_ID() > 0) {
				X_pi_paorderpackingqty packQty = new X_pi_paorderpackingqty(ctx, po.get_ID(), trxName);
				if (request.getStatusLine() != null && !request.getStatusLine().isBlank())
					packQty.setstatusline(request.getStatusLine());
				
				if (request.getIsReceived() != packQty.isreceived())
					packQty.setreceived(request.getIsReceived());
				packQty.saveEx();
				}
				
				if (packingQty != 0) {
					X_pi_paorderpackingqty packingQuantity = new X_pi_paorderpackingqty(ctx, 0, trxName);
					packingQuantity.setAD_Org_ID(loginReq.getOrgID());
					packingQuantity.setpackingqty(BigDecimal.valueOf(packingQty));
					packingQuantity.setpi_paorder_ID(paOrder.getpi_paorder_ID());
					packingQuantity.saveEx();
				}
				
				if (receivedQty != 0) {
					String Query2 = "SELECT COALESCE(SUM(fgreceivedQty), 0) FROM pi_paorderreceiveqty WHERE pi_paorder_id = ?";
					BigDecimal sum2 = DB.getSQLValueBD(trxName, Query2, paOrder.getpi_paorder_ID());
					int pendingReceivingQty = sum.intValue() - sum2.intValue();
					if(receivedQty > pendingReceivingQty) {
						response.setIsError(true);
						response.setError("The Receiving quantity is not equal to or not less than the actual Pending Packing quantity");
					}
					
					X_pi_paorderreceiveqty receivedQunatity = new X_pi_paorderreceiveqty(ctx, 0, trxName);
					receivedQunatity.setAD_Org_ID(loginReq.getOrgID());
					receivedQunatity.setfgreceivedqty(BigDecimal.valueOf(receivedQty));
					receivedQunatity.setpi_paorder_ID(paOrder.getpi_paorder_ID());
					receivedQunatity.saveEx();
				}
				

			} else if (operation.equals("DELETE")) {
				paOrder = new X_pi_paorder(ctx, request.getPAOrderId(), trxName);
				paOrder.deleteEx(true);
			}

			response.setRecordID(paOrder.get_ID());
			response.setIsError(false);
			trx.commit();

			trx.commit();

		} catch (Exception e) {
			response.setIsError(true);
			response.setError(e.getMessage());
			return res;
		} finally {
			getCompiereService().disconnect();
			trx.close();
		}
		return res;
	}
	
	@Override
	public PAOrderListResponseDocument getPAOrderList(PAOrderListRequestDocument req) {
		PAOrderListResponseDocument res = PAOrderListResponseDocument.Factory.newInstance();
		PAOrderListResponse response = res.addNewPAOrderListResponse();
		PAOrderListRequest request = req.getPAOrderListRequest();
		ADLoginRequest loginReq = request.getADLoginRequest();
		String serviceType = request.getServiceType();
		String searchKey = request.getSearchKey();
		boolean isPackagingOrder = request.getIsPackagingOrder();
		Trx trx = null;
//		PreparedStatement patm = null;
//		ResultSet rs = null;

		// Get pagination details from request
		int pageSize = request.getPageSize(); // Number of records per page
		int pageNumber = request.getPageNumber(); // Current page number
		try {
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			getCompiereService().connect();

			org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = VeUtils.convertAdLogin(loginReq);
			String err = login(adLoginReq, webServiceName, "getPAOrderList", serviceType);
			if (err != null && err.length() > 0) {
				response.setError(err);
				response.setIsError(true);
				return res;
			}

			if (!serviceType.equalsIgnoreCase("getPAOrderList")) {
				response.setIsError(true);
				response.setError("Service type " + serviceType + " not configured");
				return res;
			}

//			int userId = Env.getAD_User_ID(ctx);
//			MUser_Custom user = new MUser_Custom(ctx, userId, trxName);
//			X_PI_Deptartment dept = new X_PI_Deptartment(ctx, user.getPI_DEPARTMENT_ID(), trxName);

			String status = request.getStatus();
			if (status != null && status.isEmpty())
				status = null;

			int clientId = loginReq.getClientID();
			int orgId = loginReq.getOrgID();
			List<PO> paOrderList = VeUtils.getPAOrderList(clientId, orgId, ctx, trxName, searchKey, isPackagingOrder);

			// Calculate offset and end index for pagination
			int offset = (pageNumber > 0 && pageSize > 0) ? (pageNumber - 1) * pageSize : 0;
			int end = (pageNumber > 0 && pageSize > 0) ? Math.min(offset + pageSize, paOrderList.size())
					: paOrderList.size();

			if (paOrderList != null && paOrderList.size() != 0) {
				for (int i = offset; i < end; i++) { // Iterate over the paginated subset
					PO po = paOrderList.get(i);
					X_pi_paorder paorder = new X_pi_paorder(ctx, po.get_ID(), trxName);
					String orderStatus = paorder.getStatus();
					boolean isStatusValid = status == null || (status != null && status.equals(orderStatus));

//					int deptId = paorder.getPI_Deptartment_ID();
//					if (isStatusValid && (deptId == 0 || (dept.getPI_Deptartment_ID() == deptId))) {
					if (isStatusValid) {
						PAOrderList pAOrderList = response.addNewPAOrderList();
						pAOrderList.setPAOrderId(paorder.get_ID());
						pAOrderList.setStatus(paorder.getStatus());
						pAOrderList.setDescription(paorder.getDescription() != null ? paorder.getDescription() : "");
						pAOrderList.setProductId(paorder.getM_Product_ID());
						pAOrderList.setProductname(paorder.getM_Product().getName());
						pAOrderList.setContractorId(paorder.getcontractorId());
						pAOrderList.setContractorName(paorder.getcontractor().getName());
						pAOrderList.setPackagingOrder(paorder.ispackingorder());
						pAOrderList.setProcessed(paorder.isProcessed());
						pAOrderList.setQuantity(paorder.getquantity().intValue());
						pAOrderList.setIsRMPicked(paorder.isrmpicked());
						Calendar calendar = Calendar.getInstance();
						calendar.setTimeInMillis(paorder.getCreated().getTime());
						pAOrderList.setDateOrdered(calendar);
						
						String Query = "SELECT COALESCE(SUM(packingqty), 0) FROM pi_paorderpackingqty WHERE pi_paorder_id = ?";
						BigDecimal sum = DB.getSQLValueBD(trxName, Query, paorder.getpi_paorder_ID());
//						pAOrderList.setPackingQty(sum.intValue());
						pAOrderList.setPendingQty(paorder.getquantity().intValue() - sum.intValue());
						
						String Query3 = "SELECT COALESCE(SUM(packingqty), 0) FROM pi_paorderpackingqty WHERE received = 'N' AND pi_paorder_id = ?";
						BigDecimal sum3 = DB.getSQLValueBD(trxName, Query3, paorder.getpi_paorder_ID());
						pAOrderList.setPackingQty(sum3.intValue());
						
						String Query2 = "SELECT COALESCE(SUM(fgreceivedQty), 0) FROM pi_paorderreceiveqty WHERE pi_paorder_id = ?";
						BigDecimal sum2 = DB.getSQLValueBD(trxName, Query2, paorder.getpi_paorder_ID());
						pAOrderList.setReceivedQty(sum2.intValue());
					}
				}
			} else {
				response.setIsError(false);
				response.setError("No Orders Found");
				return res;
			}

			response.setIsError(false);

			trx.commit();
		} catch (Exception e) {
			response.setIsError(true);
			response.setError(e.getMessage());
			return res;
		} finally {
			getCompiereService().disconnect();
			trx.close();
		}
		return res;
	}
	
	
	@Override
	public PAOrderDetailResponseDocument getPAOrderDetail(PAOrderDetailRequestDocument req) {
		PAOrderDetailResponseDocument res = PAOrderDetailResponseDocument.Factory.newInstance();
		PAOrderDetailResponse response = res.addNewPAOrderDetailResponse();
		PAOrderDetailRequest request = req.getPAOrderDetailRequest();
		ADLoginRequest loginReq = request.getADLoginRequest();
		String serviceType = request.getServiceType();
		Trx trx = null;
		try {
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			getCompiereService().connect();

			org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = VeUtils.convertAdLogin(loginReq);
			String err = login(adLoginReq, webServiceName, "getPAOrderDetail", serviceType);
			if (err != null && err.length() > 0) {
				response.setError(err);
				response.setIsError(true);
				return res;
			}

			if (!serviceType.equalsIgnoreCase("getPAOrderDetail")) {
				response.setIsError(true);
				response.setError("Service type " + serviceType + " not configured");
				return res;
			}

			int userId = Env.getAD_User_ID(ctx);
			MUser_Custom user = new MUser_Custom(ctx, userId, trxName);
			X_PI_Deptartment dept = new X_PI_Deptartment(ctx, user.getPI_DEPARTMENT_ID(), trxName);

			AdRole_Custom role_Custom = new AdRole_Custom(ctx, loginReq.getRoleID(), trxName, null);
			int deptId = dept.getPI_Deptartment_ID();
			if (dept.getreceiptTransferDepartmentId() != 0 && role_Custom.isProductionSwitchSocketAgent())
				deptId = dept.getreceiptTransferDepartmentId();

			int paOrderId = request.getPAOrderId();
			X_pi_paorder paorder = new X_pi_paorder(ctx, paOrderId, trxName);
			if (paorder.get_ID() == 0) {
				response.setIsError(true);
				response.setError("Invalid OrderId: " + paOrderId + "");
				return res;
			}

			boolean isPackingOrder = paorder.ispackingorder();

			MProduct_Custom product = new MProduct_Custom(ctx, paorder.getM_Product_ID(), trxName);

			List<MLocator> mLocatorArray = VeUtils.getLocatorsByDepartment(ctx, trxName, loginReq.getWarehouseID(),
					dept.getPI_Deptartment_ID(), "receiving", "Y");
			int receivingLocatorId = 0;
			if (mLocatorArray != null && mLocatorArray.size() != 0)
				receivingLocatorId = mLocatorArray.get(0).get_ID();

			response.setPAOrderId(paorder.get_ID());
			response.setStatus(paorder.getStatus());
			response.setDescription(paorder.getDescription() != null ? paorder.getDescription() : "");
			response.setProductId(paorder.getM_Product_ID());
			response.setProductname(paorder.getM_Product().getName());
			int boxQnty = 0;
			if (product.getBoxQnty() != null)
				boxQnty = (int) product.getBoxQnty();
			response.setBoxQty(boxQnty);
			response.setScanLabel(product.isScanLabel());
			response.setContractorId(paorder.getcontractorId());
			response.setContractorName(paorder.getcontractor().getName());
			response.setPackagingOrder(paorder.ispackingorder());
			response.setProcessed(paorder.isProcessed());
			response.setQuantity(paorder.getquantity().intValue());
			response.setReceivingLocatorId(receivingLocatorId);
			response.setIsAutomation(paorder.getIsAutomation());
			
			String Query = "SELECT COALESCE(SUM(packingqty), 0) FROM pi_paorderpackingqty WHERE pi_paorder_id = ?";
			BigDecimal sum = DB.getSQLValueBD(trxName, Query, paorder.getpi_paorder_ID());
//			response.setPackingQty(sum.intValue());
			response.setPendingQty(paorder.getquantity().intValue() - sum.intValue());
			
			String Query3 = "SELECT COALESCE(SUM(packingqty), 0) FROM pi_paorderpackingqty WHERE received = 'N' AND pi_paorder_id = ?";
			BigDecimal sum3 = DB.getSQLValueBD(trxName, Query3, paorder.getpi_paorder_ID());
			response.setPackingQty(sum3.intValue());
			
			String Query2 = "SELECT COALESCE(SUM(fgreceivedQty), 0) FROM pi_paorderreceiveqty WHERE pi_paorder_id = ?";
			BigDecimal sum2 = DB.getSQLValueBD(trxName, Query2, paorder.getpi_paorder_ID());
			response.setReceivedQty(sum2.intValue());

			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(paorder.getCreated().getTime());
			response.setDateOrdered(calendar);

			MPPProductBOM bom = MPPProductBOM.getDefault(product, trxName);
			if (bom != null && bom.get_ID() != 0) {
//				response.setIsError(true);
//				response.setError("Bom Lines Not Found");
//				return res;

				MPPProductBOMLine[] bomLines = bom.getLines();
				if (bomLines != null && bomLines.length != 0)
					for (MPPProductBOMLine line : bomLines) {
						MProduct_Custom mProduct = new MProduct_Custom(ctx, line.getM_Product_ID(), trxName);

						if (isPackingOrder || !isPackingOrder && mProduct.getPI_DEPARTMENT_ID() == deptId) {
							BOMComponents bomComponents = response.addNewBOMComponents();
							bomComponents.setProductId(line.getM_Product_ID());
							bomComponents.setProductName(line.getM_Product().getName());
							bomComponents.setQuantity(line.getQty().intValue() * paorder.getquantity().intValue());
							bomComponents.setDescription(line.getDescription() != null ? line.getDescription() : "");
							bomComponents.setComponentId(line.getPP_Product_BOMLine_ID());
						}
					}
				else
					response.addNewBOMComponents();
			} else
				response.addNewBOMComponents();

			response.setIsError(false);
			trx.commit();
		} catch (Exception e) {
			response.setIsError(true);
			response.setError(e.getMessage());
			return res;
		} finally {
			getCompiereService().disconnect();
			trx.close();
		}
		return res;
	}
	
	@Override
	public StandardResponseDocument createMOOrderReceipt(CompleteMOOrderRequestDocument req) {
		StandardResponseDocument res = StandardResponseDocument.Factory.newInstance();
		StandardResponse response = res.addNewStandardResponse();
		CompleteMOOrderRequest request = req.getCompleteMOOrderRequest();
		ADLoginRequest loginReq = request.getADLoginRequest();
		String serviceType = request.getServiceType();
		Trx trx = null;
		try {
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			getCompiereService().connect();

			org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = VeUtils.convertAdLogin(loginReq);
			String err = login(adLoginReq, webServiceName, "createMOOrderReceipt", serviceType);
			if (err != null && err.length() > 0) {
				response.setError(err);
				response.setIsError(true);
				return res;
			}

			if (!serviceType.equalsIgnoreCase("createMOOrderReceipt")) {
				response.setIsError(true);
				response.setError("Service type " + serviceType + " not configured");
				return res;
			}

			int mOrderId = request.getMOrderId();

			MPPOrderCustom orderCustom = new MPPOrderCustom(ctx, mOrderId, trxName);
			X_pi_orderreceipt orderreceipt = new X_pi_orderreceipt(ctx, 0, trxName);
			orderreceipt.setPP_Order_ID(mOrderId);
			X_PI_Deptartment deptartment = new X_PI_Deptartment(ctx, orderCustom.getPI_DEPARTMENT_ID(), trxName);
			int depId = deptartment.get_ID();
			if (deptartment.getreceiptTransferDepartmentId() != 0 && orderCustom.isManufacturing())
				depId = deptartment.getreceiptTransferDepartmentId();
			orderreceipt.setPI_Deptartment_ID(depId);
			orderreceipt.setReceiptStatus(request.getStatus());
			orderreceipt.setM_Locator_ID(request.getLocatorId());

			orderreceipt.saveEx();

			response.setIsError(false);
			response.setRecordID(orderreceipt.get_ID());

			trx.commit();
		} catch (Exception e) {
			e.printStackTrace();
			response.setIsError(true);
			response.setError(e.getMessage());
			return res;
		} finally {
			getCompiereService().disconnect();
			trx.close();
		}
		return res;
	}

	@Override
	public OrderReceiptListResponseDocument getOrderReceiptList(OrderReceiptListRequestDocument req) {
		OrderReceiptListResponseDocument res = OrderReceiptListResponseDocument.Factory.newInstance();
		OrderReceiptListResponse response = res.addNewOrderReceiptListResponse();
		OrderReceiptListRequest request = req.getOrderReceiptListRequest();
		ADLoginRequest loginReq = request.getADLoginRequest();
		String serviceType = request.getServiceType();
		Trx trx = null;
		try {
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			getCompiereService().connect();

			org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = VeUtils.convertAdLogin(loginReq);
			String err = login(adLoginReq, webServiceName, "getOrderReceiptList", serviceType);
			if (err != null && err.length() > 0) {
				response.setError(err);
				response.setIsError(true);
				return res;
			}

			if (!serviceType.equalsIgnoreCase("getOrderReceiptList")) {
				response.setIsError(true);
				response.setError("Service type " + serviceType + " not configured");
				return res;
			}

			int userId = Env.getAD_User_ID(ctx);
			MUser_Custom user = new MUser_Custom(ctx, userId, trxName);
			X_PI_Deptartment dept = new X_PI_Deptartment(ctx, user.getPI_DEPARTMENT_ID(), trxName);

			String status = request.getStatus();
			if (status != null && status.isEmpty())
				status = null;

			List<PO> poList = X_pi_orderreceipt.getOrderReceiptList(loginReq.getClientID(), loginReq.getOrgID(),
					dept.get_ID(), ctx, trxName);
			if (poList != null && poList.size() != 0) {
				for (PO po : poList) {
					X_pi_orderreceipt orderreceipt = new X_pi_orderreceipt(ctx, po.get_ID(), trxName);

					String receiptStatus = orderreceipt.getReceiptStatus();
					boolean isStatusValid = (status == null && receiptStatus == null)
							|| (status != null && status.equals(receiptStatus));
					if (isStatusValid) {
						ReceiptLines line = response.addNewReceiptLines();
						line.setOrderId(orderreceipt.getPP_Order_ID());
						line.setOrderDocumentNo(orderreceipt.getPP_Order().getDocumentNo());
						line.setOrderreceiptId(orderreceipt.getpi_orderreceipt_ID());
						line.setQuantity(orderreceipt.getquantity().intValue());
						line.setProductId(orderreceipt.getM_Product_ID());
						line.setProductName(orderreceipt.getM_Product().getName());
						line.setDescription(orderreceipt.getDescription() == null ? "" : orderreceipt.getDescription());
						line.setProcessed(orderreceipt.isProcessed());
						line.setWarehouseId(orderreceipt.getPP_Order().getM_Warehouse_ID());
						line.setWarehouseName(orderreceipt.getPP_Order().getM_Warehouse().getName());
						line.setLocatorId(orderreceipt.getM_Locator_ID());

						SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
						line.setDateOrdered(dateFormat.format(orderreceipt.getCreated()));

						boolean flag = false;
						int deptTransferId = 0;
						if (dept.getreceiptTransferDepartmentId() != 0) {
							flag = true;
							deptTransferId = dept.getreceiptTransferDepartmentId();
							line.setTransferDeptName(dept.getreceiptTransferDepartment().getdeptname());
						}
						line.setTransferDeptId(deptTransferId);
						line.setDeptTransfer(flag);
					}
				}
			}

			response.setIsError(false);

			trx.commit();
		} catch (Exception e) {
			response.setIsError(true);
			response.setError(e.getMessage());
			return res;
		} finally {
			getCompiereService().disconnect();
			trx.close();
		}
		return res;
	}

	@Override
	public StandardResponseDocument UpdateOrderReceipt(UpdateOrderReceiptDocument req) {
		StandardResponseDocument res = StandardResponseDocument.Factory.newInstance();
		StandardResponse response = res.addNewStandardResponse();
		UpdateOrderReceipt request = req.getUpdateOrderReceipt();
		ADLoginRequest loginReq = request.getADLoginRequest();
		String serviceType = request.getServiceType();
		Trx trx = null;
		try {
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			getCompiereService().connect();

			org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = VeUtils.convertAdLogin(loginReq);
			String err = login(adLoginReq, webServiceName, "UpdateOrderReceipt", serviceType);
			if (err != null && err.length() > 0) {
				response.setError(err);
				response.setIsError(true);
				return res;
			}

			if (!serviceType.equalsIgnoreCase("UpdateOrderReceipt")) {
				response.setIsError(true);
				response.setError("Service type " + serviceType + " not configured");
				return res;
			}

			int orderReceiptId = request.getOrderreceiptId();
			int transferDeptId = request.getTransferDeptId();
			boolean processed = request.getProcessed();
			String receiptStatus = request.getStatus();
			X_pi_orderreceipt orderreceipt = new X_pi_orderreceipt(ctx, orderReceiptId, trxName);
			if (transferDeptId != 0) {
				orderreceipt.setPI_Deptartment_ID(transferDeptId);
			} else if (processed)
				orderreceipt.setProcessed(processed);
			if (receiptStatus != null && !receiptStatus.isEmpty()
					&& !receiptStatus.equalsIgnoreCase(orderreceipt.getReceiptStatus()))
				orderreceipt.setReceiptStatus(receiptStatus);
			orderreceipt.saveEx();

			response.setIsError(false);

			trx.commit();
		} catch (Exception e) {
			response.setIsError(true);
			response.setError(e.getMessage());
			return res;
		} finally {
			getCompiereService().disconnect();
			trx.close();
		}
		return res;
	}

	@Override
	public UpdateProductLabelResponseDocument updateProductLabel(UpdateProductLabelRequestDocument req) {
		UpdateProductLabelResponseDocument res = UpdateProductLabelResponseDocument.Factory.newInstance();
		UpdateProductLabelResponse response = res.addNewUpdateProductLabelResponse();
		UpdateProductLabelRequest request = req.getUpdateProductLabelRequest();
		ADLoginRequest loginReq = request.getADLoginRequest();
		String serviceType = request.getServiceType();
		String labelUUid = request.getLabelUUID();
		int quantity = request.getQuantity();
		int productQty = 0;
		int actualQty = 0;
		String reason = request.getReason();
		String status = request.getStatus();
		Trx trx = null;
		boolean generateNewLabel = request.getGenerateNewLabel();
		try {
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			getCompiereService().connect();

			org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = VeUtils.convertAdLogin(loginReq);
			String err = login(adLoginReq, webServiceName, "UpdateLabel", serviceType);
			if (err != null && err.length() > 0) {
				response.setError(err);
				response.setIsError(true);
				return res;
			}

			if (!serviceType.equalsIgnoreCase("UpdateLabel")) {
				response.setIsError(true);
				response.setError("Service type " + serviceType + " not configured");
				return res;
			}
			List<PO> poList = PiProductLabel.getPiProductLabelById(labelUUid, ctx, trxName, loginReq.getClientID(), loginReq.getOrgID());
			if(poList == null || poList.size() ==0 || poList.size() >1) {
				response.setIsError(true);
				response.setError("Invalid Product Label");
				return res;
			}
			
			PO po = poList.get(0);
			
			PiProductLabel productlabel = new PiProductLabel(ctx, po.get_ID(), trxName);
			productQty = productlabel.getQuantity().intValue();
			if (productlabel.getM_Product_ID() == 0) {
				response.setIsError(true);
				response.setError("Invalid Label");
				return res;
			}
			
			if (productlabel.isLabelDiscarded()) {
				response.setIsError(true);
				response.setError("Label is Discarded");
				return res;
			}
			
			if(productQty<quantity) {
				response.setIsError(true);
				response.setError("Entered quantity must not be greater than the product label quantity");
				return res;
			}
			
			if(!generateNewLabel) {
				actualQty = productQty - quantity;
				productlabel.setQuantity(BigDecimal.valueOf(actualQty));
				productlabel.setreason(reason);
				productlabel.setStatus(status);
				productlabel.saveEx();
				trx.commit();
			}
			else if (generateNewLabel && productQty > 0) {
				for (int i = 0; i < productQty; i++) {
					PiProductLabel label = new PiProductLabel(ctx, 0, trxName);
					label.setAD_Org_ID(productlabel.getAD_Org_ID());
					label.setQcpassed(productlabel.qcpassed());
					label.setQuantity(BigDecimal.ONE);
					label.setM_Product_ID(productlabel.getM_Product_ID());
					label.setM_Locator_ID(productlabel.getM_Locator_ID());
					label.setC_OrderLine_ID(productlabel.getC_OrderLine_ID());
					label.setM_InOutLine_ID(productlabel.getM_InOutLine_ID());
					label.setPi_paorder_ID(productlabel.getPi_paorder_ID());
					label.setIsSOTrx(productlabel.isSOTrx());
					label.setLabelUUID(UUID.randomUUID().toString());
					label.setIsActive(true);
					label.saveEx();
					
					ProductLabelList list = response.addNewProductLabelList();
					list.setProductLabelId(label.getpi_productLabel_ID());
					list.setProductLabelUUId(label.getLabelUUID());
					list.setQuantity(1);
					MProduct_Custom product = new MProduct_Custom(ctx, label.getM_Product_ID(), trxName);
					list.setProductName(product.getName());
				}
				productlabel.deleteEx(true);
				trx.commit();
			}
			response.setIsError(false);
		} catch (Exception e) {
			response.setIsError(true);
			response.setError(e.getMessage());
			return res;
		} finally {
			getCompiereService().disconnect();
			trx.close();
		}
		return res;
	}
	
	@Override
	public StandardResponseDocument setDocAction(ModelSetDocActionRequestDocument req) {
		
		StandardResponseDocument res = StandardResponseDocument.Factory.newInstance();
		StandardResponse response = res.addNewStandardResponse();
		ModelSetDocActionRequest request = req.getModelSetDocActionRequest();
		ADLoginRequest loginReq = request.getADLoginRequest();
		ModelSetDocAction modelSetDocAction = req.getModelSetDocActionRequest().getModelSetDocAction();
		String serviceType = modelSetDocAction.getServiceType();
		Trx trx = null;
		try {
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			getCompiereService().connect();

			org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = VeUtils.convertAdLogin(loginReq);
			String err = login(adLoginReq, webServiceName, "CompleteOrder", serviceType);
			if (err != null && err.length() > 0) {
				response.setError(err);
				response.setIsError(true);
				return res;
			}

			if (!serviceType.equalsIgnoreCase("CompleteOrder")) {
				response.setIsError(true);
				response.setError("Service type " + serviceType + " not configured");
				return res;
			}
						
			int clientId = adLoginReq.getClientID();

			trx = Trx.get(trxName, true);
			if (manageTrx)
				trx.setDisplayName(getClass().getName() + "_" + webServiceName + "_setDocAction");

			String tableName = modelSetDocAction.getTableName();

			int recordID = modelSetDocAction.getRecordID();

			String docAction = modelSetDocAction.getDocAction();
			response.setRecordID(recordID);

			// get the PO for the tablename and record ID
			MTable table = MTable.get(ctx, tableName);
			if (table == null) {

				response.setIsError(true);
				response.setError("No Table Found");
				return res;

			}

			PO po = table.getPO(recordID, trxName);
			if (po == null) {
				response.setIsError(true);
				response.setError("No Record Found");
				return res;
			}

			// set explicitly the column DocAction to avoid automatic process of
			// default option
			po.set_ValueOfColumn("DocAction", docAction);
			if (!po.save()) {
				response.setIsError(true);
				response.setError("Cannot save before set docAction");
				return res;
			}

			// call process it
			try {
				if (!((org.compiere.process.DocAction) po).processIt(docAction)) {

					response.setIsError(true);
					response.setError("Cannot process docAction");
					return res;

				}
			} catch (Exception e) {
				response.setIsError(true);
				response.setError("Cannot process docAction");
				return res;
			}

			// close the trx
			if (!po.save()) {

				response.setIsError(true);
				response.setError("Cannot save before set docAction");
				return res;

			}

			if (manageTrx && !trx.commit()) {
				response.setIsError(true);
				response.setError("Cannot commit after docAction");
				return res;
			}

			// resp.setError("");
			response.setIsError(false);

			if (tableName.equalsIgnoreCase("m_inout")) {
				MInOut inout = new MInOut(ctx, recordID, trxName);

				COrder_Custom orderCustom = new COrder_Custom(ctx, inout.getC_Order_ID(), trxName);
				
				Map<String, String> data = new HashMap<>();
				data.put("recordId", String.valueOf(recordID));
				data.put("documentNo", inout.getDocumentNo());
				if (!inout.isSOTrx()) {
					data.put("path1", "/put_away_screen");
					data.put("path2", "/put_away_detail_screen");

					VeUtils.sendNotificationAsync("materialReceipt", inout.get_Table_ID(), recordID, ctx, trxName,
							"Products added for Put away", "New products ready for Put away " + recordID + "",
							inout.get_TableName(), data, clientId, "productsAddedForPutaway", orderCustom.getpidepartmentID());

					data.put("path1", "/labour_put_away_screen");
					data.remove("path2");

					VeUtils.sendNotificationAsync("labourPutaway", inout.get_Table_ID(), recordID, ctx, trxName,
							"Products added for Put away", "New products ready for Put away", inout.get_TableName(),
							data, clientId, "productsAddedForPutaway",  orderCustom.getpidepartmentID());
				} else {

					data.put("path1", "/dispatch_screen");
					data.put("path2", "/dispatch_detail_screen");

					VeUtils.sendNotificationAsync("finaldispatchapp", inout.get_Table_ID(), recordID, ctx, trxName,
							"Products dispatched with shipment- " + inout.getDocumentNo() + "",
							"Products dispatched with Shipment No: " + inout.getDocumentNo() + " for Order - "
									+ inout.getC_Order().getDocumentNo() + "",
							inout.get_TableName(), data, clientId, "MarkedSalesOrderReadyToPick",  orderCustom.getpidepartmentID());
				}

			}
			return res;
		} finally {
			if (manageTrx && trx != null)
				trx.close();

			getCompiereService().disconnect();
		}
	}

	@Override
	public GetProductListResponseDocument getProductList(GetProductListRequestDocument req) {
		GetProductListResponseDocument res = GetProductListResponseDocument.Factory.newInstance();
		GetProductListResponse response = res.addNewGetProductListResponse();
		GetProductListRequest request = req.getGetProductListRequest();
		ADLoginRequest loginReq = request.getADLoginRequest();
		String serviceType = request.getServiceType();
		Trx trx = null;
		int client_id = loginReq.getClientID();
		String user = loginReq.getUser();
		try {
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			getCompiereService().connect();

			org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = VeUtils.convertAdLogin(loginReq);
			String err = login(adLoginReq, webServiceName, "UpdateLabel", serviceType);
			if (err != null && err.length() > 0) {
				response.setError(err);
				response.setIsError(true);
				return res;
			}

			if (!serviceType.equalsIgnoreCase("UpdateLabel")) {
				response.setIsError(true);
				response.setError("Service type " + serviceType + " not configured");
				return res;
			}
			int userId = Env.getAD_User_ID(ctx);
			MUser_Custom users = new MUser_Custom(ctx, userId, trxName);
			X_PI_Deptartment dept = new X_PI_Deptartment(ctx, users.getPI_DEPARTMENT_ID(), trxName);

			List<MLocator> mLocatorArray = VeUtils.getLocatorsByDepartment(ctx, trxName, loginReq.getWarehouseID(),
					dept.getPI_Deptartment_ID(), "receiving", "Y");
			int receivingLocatorId = 0;
			if(mLocatorArray != null && mLocatorArray.size() != 0)
				receivingLocatorId = mLocatorArray.get(0).get_ID();
			
			List<PO> list = VeUtils.getProductList(client_id, user, ctx, trxName);
			for(PO prList : list) {
				ProductList productLists = response.addNewProductList();
				MProduct_Custom productList = new MProduct_Custom(ctx, prList.get_ID(), trxName);
				productLists.setProductId(productList.getM_Product_ID());
				productLists.setProductName(productList.getName());
				productLists.setLocatorId(receivingLocatorId);
			}
			response.setIsError(false);
		} catch (Exception e) {
			response.setIsError(true);
			response.setError(e.getMessage());
			return res;
		} finally {
			getCompiereService().disconnect();
			trx.close();
		}
		return res;
	}

	@Override
	public StandardResponseDocument createReturnOrder(ReturnOrderRequestDocument req) {
		StandardResponseDocument res = StandardResponseDocument.Factory.newInstance();
		StandardResponse response = res.addNewStandardResponse();
		ReturnOrderRequest request = req.getReturnOrderRequest();
		ADLoginRequest loginReq = request.getADLoginRequest();
		String serviceType = request.getServiceType();
		Trx trx = null;
		try {
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			getCompiereService().connect();

			org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = VeUtils.convertAdLogin(loginReq);
			String err = login(adLoginReq, webServiceName, "returnOrder", serviceType);
			if (err != null && err.length() > 0) {
				response.setError(err);
				response.setIsError(true);
				return res;
			}

			if (!serviceType.equalsIgnoreCase("returnOrder")) {
				response.setIsError(true);
				response.setError("Service type " + serviceType + " not configured");
				return res;
			}

			String operation = request.getOperation();
			X_pi_return returnOrder = new X_pi_return(ctx, request.getReturnOrderId(), trxName);
			if (operation.equals("CREATE")) {
				returnOrder.setAD_Org_ID(loginReq.getOrgID());
				returnOrder.setC_BPartner_ID(request.getBPartnerId());
				returnOrder.setStatus(request.getStatus());

			} else if (operation.equals("UPDATE")) {
				if (request.getDescription() != null && !request.getDescription().isBlank()
						&& (!request.getDescription().equalsIgnoreCase(returnOrder.getDescription())))
					returnOrder.setDescription(request.getDescription());

				if (request.getStatus() != null && !request.getStatus().isBlank())
					returnOrder.setStatus(request.getStatus());
			}

			returnOrder.saveEx();

			ReturnList[] list = request.getReturnListArray();
			for (ReturnList returnLine : list) {
				int productId = returnLine.getProductId();
				int quantity = returnLine.getQuantity();
				int quantityIssue = returnLine.getQuantityIssue();

				X_pi_returnline line = new X_pi_returnline(ctx, returnLine.getReturnLineId(), trxName);
				line.setAD_Org_ID(loginReq.getOrgID());
				line.setpi_return_ID(returnOrder.getpi_return_ID());
				line.setM_Product_ID(productId);
				line.setqtytotal(BigDecimal.valueOf(quantity));
				line.setqtyissued(BigDecimal.valueOf(quantityIssue));
				line.saveEx();
			}

			response.setRecordID(returnOrder.get_ID());
			response.setIsError(false);
			trx.commit();

		} catch (Exception e) {
			response.setIsError(true);
			response.setError(e.getMessage());
			return res;
		} finally {
			getCompiereService().disconnect();
			trx.close();
		}
		return res;
	}

	@Override
	public ReturnOrderListResponseDocument returnOrderList(ReturnOrderListRequestDocument req) {
		ReturnOrderListResponseDocument res = ReturnOrderListResponseDocument.Factory.newInstance();
		ReturnOrderListResponse response = res.addNewReturnOrderListResponse();
		ReturnOrderListRequest request = req.getReturnOrderListRequest();
		ADLoginRequest loginReq = request.getADLoginRequest();
		String serviceType = request.getServiceType();
		String searchKey = request.getSearchKey();
		Trx trx = null;
		int pageSize = request.getPageSize(); // Number of records per page
		int pageNumber = request.getPageNumber(); // Current page number
		try {
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			getCompiereService().connect();

			org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = VeUtils.convertAdLogin(loginReq);
			String err = login(adLoginReq, webServiceName, "returnOrderList", serviceType);
			if (err != null && err.length() > 0) {
				response.setError(err);
				response.setIsError(true);
				return res;
			}
			if (!serviceType.equalsIgnoreCase("returnOrderList")) {
				response.setIsError(true);
				response.setError("Service type " + serviceType + " not configured");
				return res;
			}

			String status = request.getStatus();
			if (status != null && status.isEmpty())
				status = null;

			int clientId = loginReq.getClientID();
			int orgId = loginReq.getOrgID();
			List<PO> returnOrderList = VeUtils.getReturnrderList(clientId, orgId, ctx, trxName, searchKey);

			int offset = (pageNumber > 0 && pageSize > 0) ? (pageNumber - 1) * pageSize : 0;
			int end = (pageNumber > 0 && pageSize > 0) ? Math.min(offset + pageSize, returnOrderList.size())
					: returnOrderList.size();

			if (returnOrderList != null && returnOrderList.size() != 0) {
				for (int i = offset; i < end; i++) { // Iterate over the paginated subset
					PO po = returnOrderList.get(i);
					X_pi_return paorder = new X_pi_return(ctx, po.get_ID(), trxName);
					String orderStatus = paorder.getStatus();
					boolean isStatusValid = status == null || (status != null && status.equals(orderStatus));

					if (isStatusValid) {
						ReturnOrderList returnOrderLists = response.addNewReturnOrderList();
						returnOrderLists.setReturnOrderId(paorder.get_ID());
						returnOrderLists.setStatus(paorder.getStatus());
						returnOrderLists.setDescription(paorder.getDescription() != null ? paorder.getDescription() : "");
						returnOrderLists.setBpartnerId(paorder.getC_BPartner_ID());
						returnOrderLists.setBpartnerName(paorder.getC_BPartner().getName());
						Calendar calendar = Calendar.getInstance();
						calendar.setTimeInMillis(paorder.getCreated().getTime());
						returnOrderLists.setDateOrdered(calendar);
					}
				}
			} else {
				response.setIsError(false);
				response.setError("No Orders Found");
				return res;
			}
			response.setIsError(false);

			trx.commit();
		} catch (Exception e) {
			response.setIsError(true);
			response.setError(e.getMessage());
			return res;
		} finally {
			getCompiereService().disconnect();
			trx.close();
		}
		return res;
	}

	@Override
	public ReturnOrderDetailsResponseDocument returnOrderDetails(ReturnOrderDetailsRequestDocument req) {
		ReturnOrderDetailsResponseDocument res = ReturnOrderDetailsResponseDocument.Factory.newInstance();
		ReturnOrderDetailsResponse response = res.addNewReturnOrderDetailsResponse();
		ReturnOrderDetailsRequest request = req.getReturnOrderDetailsRequest();
		ADLoginRequest loginReq = request.getADLoginRequest();
		String serviceType = request.getServiceType();
		Trx trx = null;
		try {
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			getCompiereService().connect();

			org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = VeUtils.convertAdLogin(loginReq);
			String err = login(adLoginReq, webServiceName, "returnOrderList", serviceType);
			if (err != null && err.length() > 0) {
				response.setError(err);
				response.setIsError(true);
				return res;
			}

			if (!serviceType.equalsIgnoreCase("returnOrderList")) {
				response.setIsError(true);
				response.setError("Service type " + serviceType + " not configured");
				return res;
			}

			int userId = Env.getAD_User_ID(ctx);
			MUser_Custom user = new MUser_Custom(ctx, userId, trxName);
			X_PI_Deptartment dept = new X_PI_Deptartment(ctx, user.getPI_DEPARTMENT_ID(), trxName);

			int returnOrderId = request.getReturnOrderId();
			X_pi_return returnorder = new X_pi_return(ctx, returnOrderId, trxName);
			if (returnorder.get_ID() == 0) {
				response.setIsError(true);
				response.setError("Invalid OrderId: " + returnOrderId + "");
				return res;
			}
			List<MLocator> mLocatorArray = VeUtils.getLocatorsByDepartment(ctx, trxName, loginReq.getWarehouseID(),
					dept.getPI_Deptartment_ID(), "returns", "Y");
			int returnceivingLocatorId = 0;
			if (mLocatorArray != null && mLocatorArray.size() != 0)
				returnceivingLocatorId = mLocatorArray.get(0).get_ID();

			response.setReturnOrderId(returnOrderId);
			response.setStatus(returnorder.getStatus());
			response.setDescription(returnorder.getDescription() != null ? returnorder.getDescription() : "");
			response.setBpartnerId(returnorder.getC_BPartner_ID());
			response.setBpartnerName(returnorder.getC_BPartner().getName());
			response.setLocatorId(returnceivingLocatorId);

			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(returnorder.getCreated().getTime());
			response.setDateOrdered(calendar);
			
			List<PO> returnList = VeUtils.getReturnLineList(loginReq.getClientID(),loginReq.getOrgID(),ctx,trxName,returnOrderId);
			for(PO lists : returnList) {
				X_pi_returnline line = new X_pi_returnline(ctx, lists.get_ID(), trxName);
				
				ReturnList datas = response.addNewReturnList();
				datas.setReturnLineId(line.getpi_returnline_ID());
				datas.setProductId(line.getM_Product_ID());
				datas.setProductName(line.getM_Product().getName());
				datas.setQuantity(line.getqtytotal().intValue());
				datas.setQuantityIssue(line.getqtyissued().intValue());
			}
			response.setIsError(false);
			trx.commit();
		} catch (Exception e) {
			response.setIsError(true);
			response.setError(e.getMessage());
			return res;
		} finally {
			getCompiereService().disconnect();
			trx.close();
		}
		return res;
	}

	@Override
	public GetReturnComponentsResponseDocument getReturnComponents(GetReturnComponentsRequestDocument req) {
		GetReturnComponentsResponseDocument res = GetReturnComponentsResponseDocument.Factory.newInstance();
		GetReturnComponentsResponse response = res.addNewGetReturnComponentsResponse();
		GetReturnComponentsRequest request = req.getGetReturnComponentsRequest();
		ADLoginRequest loginReq = request.getADLoginRequest();
		String serviceType = request.getServiceType();
		Trx trx = null;
		try {
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			getCompiereService().connect();

			org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = VeUtils.convertAdLogin(loginReq);
			String err = login(adLoginReq, webServiceName, "returnOrderList", serviceType);
			if (err != null && err.length() > 0) {
				response.setError(err);
				response.setIsError(true);
				return res;
			}

			if (!serviceType.equalsIgnoreCase("returnOrderList")) {
				response.setIsError(true);
				response.setError("Service type " + serviceType + " not configured");
				return res;
			}

			int orgId = loginReq.getOrgID();
			int clientId = loginReq.getClientID();

			int userId = Env.getAD_User_ID(ctx);
			MUser_Custom user = new MUser_Custom(ctx, userId, trxName);
			X_PI_Deptartment dept = null;
			
			dept = new X_PI_Deptartment(ctx, user.getPI_DEPARTMENT_ID(), trxName);
			
			List<MLocator> mLocatorArray = VeUtils.getLocatorsByDepartment(ctx, trxName, loginReq.getWarehouseID(),
					dept.getPI_Deptartment_ID(), "returns", "Y");
			int returnceivingLocatorId = 0;
			if (mLocatorArray != null && mLocatorArray.size() != 0)
				returnceivingLocatorId = mLocatorArray.get(0).get_ID();
			response.setLocatorId(returnceivingLocatorId);
			
			List<MProduct_Custom> products;
			if ("FG-Light".equalsIgnoreCase(dept.getdeptname())) {
			    products = MProduct_Custom.getProductsForFGUser(clientId, orgId, ctx, trxName, dept.get_ID());
			} else {
			    List<PO> poList = MProduct_Custom.getProducts(clientId, orgId, ctx, trxName, dept.get_ID());
			    products = new ArrayList<>();
			    for (PO po : poList) {
			        products.add(new MProduct_Custom(ctx, po.get_ID(), trxName));
			    }
			}
			
			for (MProduct_Custom product : products) {
			    Product productBom = response.addNewProduct();
			    productBom.setProductId(product.getM_Product_ID());
			    productBom.setProductName(product.getName());
			}

			List<PO> bPartnerList = VeUtils.getBusinessPartnerList(clientId, orgId, ctx, trxName);
			if (bPartnerList != null && bPartnerList.size() != 0) {
				for (PO po : bPartnerList) {
					MBPartner partner = new MBPartner(ctx, po.get_ID(), trxName);
					BusinessPartner bPartner = response.addNewBusinessPartner();
					bPartner.setBPartnerId(partner.getC_BPartner_ID());
					bPartner.setBPartnerName(partner.getName());
				}
			}
			response.setIsError(false);
			trx.commit();
		} catch (Exception e) {
			response.setIsError(true);
			response.setError(e.getMessage());
			return res;
		} finally {
			getCompiereService().disconnect();
			trx.close();
		}
		return res;
	}
	
	@Override
	public StandardResponseDocument editShipment(CreateSCByLabelRequestDocument requestDocument) {
		StandardResponseDocument responseDocument = StandardResponseDocument.Factory.newInstance();
		StandardResponse response = responseDocument.addNewStandardResponse();
		CreateSCByLabelRequest createSCRequest = requestDocument.getCreateSCByLabelRequest();
		ADLoginRequest loginReq = createSCRequest.getADLoginRequest();
		String serviceType = createSCRequest.getServiceType();
		int mInoutID = createSCRequest.getMInoutID();
		Trx trx = null;

		CompiereService m_cs = getCompiereService();
		Properties ctx = m_cs.getCtx();
		try {
			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();

			org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = VeUtils.convertAdLogin(loginReq);
			String err = login(adLoginReq, webServiceName, "createSC", serviceType);
			if (err != null && err.length() > 0) {
				response.setError(err);
				response.setIsError(true);
				return responseDocument;
			}
			if (!serviceType.equalsIgnoreCase("createSC")) {
				response.setIsError(true);
				response.setError("Service type " + serviceType + " not configured");
				return responseDocument;
			}

			SCLabelLine[] scLabelLines = createSCRequest.getLineArray();

			if (scLabelLines == null || scLabelLines.length == 0) {
				response.setIsError(true);
				response.setError("No lines Found");
				return responseDocument;
			}

			MInOut inout = new MInOut(ctx, mInoutID, trxName);
			if (inout == null || inout.get_ID() == 0) {
				response.setError("shipment not found for " + mInoutID + "");
				response.setIsError(true);
				return responseDocument;
			}

			MInOutLine[] existingLines = inout.getLines();

			LinkedHashMap<Integer, Integer> productQntyMap = new LinkedHashMap<Integer, Integer>();

			for (SCLabelLine line : scLabelLines) {
				try {
					validateLabels(ctx, trxName, line.getLabelUUID(), productQntyMap,  loginReq.getClientID(), loginReq.getOrgID());
				} catch (Exception e) {
					response.setIsError(true);
					response.setError(e.getMessage());
					return responseDocument;
				}
			}

			LinkedHashMap<Integer, Integer> existingProductQntyMap = new LinkedHashMap<Integer, Integer>();

			if (existingLines != null && existingLines.length != 0) {
				for (MInOutLine inoutLine : existingLines) {

					int qnty = inoutLine.getQtyEntered().intValue();
					if (existingProductQntyMap.containsKey(inoutLine.getM_Product_ID())) {
						qnty += existingProductQntyMap.get(inoutLine.getM_Product_ID());
					}
					existingProductQntyMap.put(inoutLine.getM_Product_ID(), qnty);

				}
			}

			for (Map.Entry<Integer, Integer> entry : productQntyMap.entrySet()) {
				Integer productId = entry.getKey();
				Integer requestedQty = entry.getValue();

				Integer existingQty = existingProductQntyMap.get(productId);
				if (existingQty == null || requestedQty > existingQty) {
					response.setIsError(true);
					response.setError("Requested quantity for product ID " + productId + " (" + requestedQty
							+ ") is greater than existing quantity (" + (existingQty != null ? existingQty : 0) + ").");
					return responseDocument;
				}
			}

			COrder_Custom cOrder = new COrder_Custom(ctx, inout.getC_Order_ID(), trxName);
			
			int userDepartmentId = cOrder.getpidepartmentID();

			X_PI_Deptartment userDepartment = new X_PI_Deptartment(ctx, userDepartmentId, trxName);

			List<MLocator> mLocatorArray = VeUtils.getLocatorsByDepartment(ctx, trxName, loginReq.getWarehouseID(),
					userDepartmentId, "receiving", "Y");

			if (mLocatorArray == null || mLocatorArray.size() == 0) {
				response.setIsError(true);
				response.setError("Receiving Locator not found for : " + userDepartment.getdeptname() + "");
				return responseDocument;
			}

			for (Map.Entry<Integer, Integer> entry : productQntyMap.entrySet()) {
				Integer M_Product_ID = entry.getKey();
				Integer qnty = entry.getValue();

				if (existingLines != null && existingLines.length != 0) {
					for (MInOutLine inoutLine : existingLines) {

						if (inoutLine.getM_Product_ID() == M_Product_ID) {

							int lineQnty = inoutLine.getQtyEntered().intValue();

							if (qnty == 0)
								break;

							else if (qnty == lineQnty) {
								inoutLine.deleteEx(true);
								qnty = 0;
							}

							else if (qnty > lineQnty) {
								qnty = qnty - lineQnty;
								inoutLine.deleteEx(true);
							}

							else if (qnty < lineQnty) {
								inoutLine.setQtyEntered(BigDecimal.valueOf(lineQnty - qnty));
								inoutLine.setMovementQty(BigDecimal.valueOf(lineQnty - qnty));
								inoutLine.save();
								qnty = 0;
							}
						}
					}
				}

			}

			inout = new MInOut(ctx, mInoutID, trxName);
			existingLines = inout.getLines();
			if (existingLines == null || existingLines.length == 0)
				inout.delete(true);

			int receivingLocatorId = mLocatorArray.get(0).get_ID();
			for (SCLabelLine line : scLabelLines) {

				PutAwayRequest putAwayRequest = PutAwayRequest.Factory.newInstance();

				PutAwayLine putAwayLine = putAwayRequest.addNewPutAwayLine();
				putAwayLine.setLocatorId(receivingLocatorId);
				putAwayLine.setProductLabelUUId(line.getLabelUUID());

				putAwayLabels(putAwayRequest.getPutAwayLineArray(), trxName, ctx, loginReq.getClientID(),
						loginReq.getOrgID(), false, false, false, loginReq.getWarehouseID());
			}

			trx.commit();
			response.setIsError(false);

		} catch (Exception e) {
			response.setError(e.getMessage());
			response.setIsError(true);
			return responseDocument;
		} finally {
			getCompiereService().disconnect();
			trx.close();
		}
		return responseDocument;
	}

	private void validateLabels(Properties ctx, String trxName, String labelUUId,
			LinkedHashMap<Integer, Integer> productQntyMap, int clientId, int orgId) throws Exception {

		List<PO> poList = PiProductLabel.getPiProductLabel("labelUUId", labelUUId, ctx, trxName, null, clientId, orgId);

		if (poList == null || poList.size() == 0 || poList.size() > 1)
			throw new Exception("Invalid Product Label");

		PiProductLabel piProductLabel = new PiProductLabel(ctx, poList.get(0).get_ID(), trxName);

		if (!piProductLabel.isfinaldispatch())
			throw new Exception("Invalid Product Label");

		if (piProductLabel.getM_Product_ID() != 0) {
			int qnty = piProductLabel.getQuantity().intValue();
			if( productQntyMap.containsKey(piProductLabel.getM_Product_ID()) )
				qnty +=  productQntyMap.get(piProductLabel.getM_Product_ID()) ;
			
			productQntyMap.put(piProductLabel.getM_Product_ID(),qnty);
		}

		if (piProductLabel.getM_Product_ID() == 0) {
			List<PO> childList = PiProductLabel.getPiProductLabel("parentLabel", piProductLabel.getpi_productLabel_ID(),
					ctx, trxName, null, clientId, orgId);

			if (childList != null && !childList.isEmpty())
				for (PO po : childList) {
					PiProductLabel clabel = new PiProductLabel(ctx, po.get_ID(), trxName);
					validateLabels(ctx, trxName, clabel.getLabelUUID(), productQntyMap, clientId, orgId);
				}
		}
	}

	@Override
	public CleanUpDataResponseDocument cleanupData(CleanUpDataRequestDocument req) {
		CleanUpDataResponseDocument res = CleanUpDataResponseDocument.Factory.newInstance();
		CleanUpDataResponse response = res.addNewCleanUpDataResponse();
		CleanUpDataRequest loginRequest = req.getCleanUpDataRequest();
		ADLoginRequest login = loginRequest.getADLoginRequest();
		String serviceType = loginRequest.getServiceType().trim();
		Trx trx = null;
//		int client_id = login.getClientID();
		try {
			getCompiereService().connect();
			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();

			org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = VeUtils.convertAdLogin(login);
			String err = login(adLoginReq, webServiceName, "cleanup", serviceType);
			if (err != null && err.length() > 0) {
				response.setError(err);
				response.setIsError(true);
				return res;
			}

			if (!serviceType.equalsIgnoreCase("cleanup")) {
				response.setIsError(true);
				response.setError("Service type " + serviceType + " not configured");
				return res;
			}
			
//			String sqlUpdateInvoices = "UPDATE adempiere.c_invoice SET docstatus = 'DR', docaction = 'CO', processed = 'N' WHERE AD_Client_ID = ?";
//			String sqlDeleteInvoiceLines = "DELETE FROM adempiere.c_invoiceline WHERE c_invoice_id IN (SELECT c_invoice_id FROM adempiere.c_invoice WHERE AD_Client_ID = ?)";
//			String sqlDeleteAllocationLines = "DELETE FROM adempiere.c_allocationline WHERE c_invoice_id IN (SELECT c_invoice_id FROM adempiere.c_invoice WHERE AD_Client_ID = ?)";
//			String sqlDeleteInvoiceTaxes = "DELETE FROM adempiere.c_invoicetax WHERE c_invoice_id IN (SELECT c_invoice_id FROM adempiere.c_invoice WHERE AD_Client_ID = ?)";
//			String sqlUpdatePayments = "UPDATE adempiere.c_invoice SET c_payment_id = NULL WHERE AD_Client_ID = ?";
//			String sqlDeletePayments = "DELETE FROM adempiere.c_payment WHERE c_invoice_id IN (SELECT c_invoice_id FROM adempiere.c_invoice WHERE AD_Client_ID = ?)";
//			String sqlDeleteInvoices = "DELETE FROM adempiere.c_invoice WHERE AD_Client_ID = ?";
//
//			DB.executeUpdateEx(sqlUpdateInvoices, new Object[] { client_id }, trxName);
//			DB.executeUpdateEx(sqlDeleteInvoiceLines, new Object[] { client_id }, trxName);
//			DB.executeUpdateEx(sqlDeleteAllocationLines, new Object[] { client_id }, trxName);
//			DB.executeUpdateEx(sqlDeleteInvoiceTaxes, new Object[] { client_id }, trxName);
//			DB.executeUpdateEx(sqlUpdatePayments, new Object[] { client_id }, trxName);
//			DB.executeUpdateEx(sqlDeletePayments, new Object[] { client_id }, trxName);
//			DB.executeUpdateEx(sqlDeleteInvoices, new Object[] { client_id }, trxName);
//			trx.commit();
//			
//			String sqlUpdatePAOrder = "UPDATE adempiere.pi_paorder SET processed = 'N' WHERE AD_Client_ID = ?";
//			String sqlDeleteReceiveQty = "DELETE FROM adempiere.pi_paorderreceiveqty WHERE AD_Client_ID = ?";
//			String sqlDeletePackingQty = "DELETE FROM adempiere.pi_paorderpackingqty WHERE AD_Client_ID = ?";
//			String sqlDeleteProductLabel2 = "DELETE FROM adempiere.pi_productlabel WHERE AD_Client_ID = ?";
//			String sqlDeletePAOrder = "DELETE FROM adempiere.pi_paorder WHERE AD_Client_ID = ?";
//
//			DB.executeUpdateEx(sqlUpdatePAOrder, new Object[] { client_id }, trxName);
//			DB.executeUpdateEx(sqlDeleteReceiveQty, new Object[] { client_id }, trxName);
//			DB.executeUpdateEx(sqlDeletePackingQty, new Object[] { client_id }, trxName);
//			DB.executeUpdateEx(sqlDeleteProductLabel2, new Object[] { client_id }, trxName);
//			DB.executeUpdateEx(sqlDeletePAOrder, new Object[] { client_id }, trxName);
//
//			trx.commit();
//			
//			// 1. Reset m_inout status for the client
//			DB.executeUpdateEx(
//			    "UPDATE adempiere.m_inout SET docstatus = 'DR', docaction = 'CO', processed = 'N' WHERE AD_Client_ID = ?",
//			    new Object[]{client_id},
//			    trxName
//			);
//
//			// 2. All delete / update statements
//			String[] deleteStatements = {
//			    "DELETE FROM adempiere.m_matchpo WHERE m_inoutline_id IN (SELECT m_inoutline_id FROM adempiere.m_inoutline WHERE m_inout_id IN (SELECT m_inout_id FROM adempiere.m_inout WHERE AD_Client_ID = ?))",
//			    "DELETE FROM adempiere.m_matchinv WHERE m_inoutline_id IN (SELECT m_inoutline_id FROM adempiere.m_inoutline WHERE m_inout_id IN (SELECT m_inout_id FROM adempiere.m_inout WHERE AD_Client_ID = ?))",
//			    "DELETE FROM adempiere.m_transaction WHERE m_inoutline_id IN (SELECT m_inoutline_id FROM adempiere.m_inoutline WHERE m_inout_id IN (SELECT m_inout_id FROM adempiere.m_inout WHERE AD_Client_ID = ?))",
//			    "DELETE FROM adempiere.m_inoutlinema WHERE m_inoutline_id IN (SELECT m_inoutline_id FROM adempiere.m_inoutline WHERE m_inout_id IN (SELECT m_inout_id FROM adempiere.m_inout WHERE AD_Client_ID = ?))",
//			    "DELETE FROM adempiere.pi_productlabel WHERE m_inoutline_id IN (SELECT m_inoutline_id FROM adempiere.m_inoutline WHERE m_inout_id IN (SELECT m_inout_id FROM adempiere.m_inout WHERE AD_Client_ID = ?))",
//			    "DELETE FROM adempiere.m_costhistory WHERE m_costdetail_id IN (SELECT cd.m_costdetail_id FROM adempiere.m_costdetail cd JOIN adempiere.m_inoutline iol ON cd.m_inoutline_id = iol.m_inoutline_id WHERE iol.AD_Client_ID = ?)",
//			    "DELETE FROM adempiere.m_costdetail WHERE m_inoutline_id IN (SELECT m_inoutline_id FROM adempiere.m_inoutline WHERE AD_Client_ID = ?)",
//			    // This one has 2 placeholders
//			    "UPDATE adempiere.m_inoutline SET m_rmaline_id = NULL WHERE m_rmaline_id IN (SELECT rma.m_rmaline_id FROM adempiere.m_rmaline rma JOIN adempiere.m_inoutline iol ON rma.m_inoutline_id = iol.m_inoutline_id WHERE iol.AD_Client_ID = ?) AND AD_Client_ID = ?",
//			    "DELETE FROM adempiere.m_rmaline WHERE m_inoutline_id IN (SELECT m_inoutline_id FROM adempiere.m_inoutline WHERE AD_Client_ID = ?)",
//			    "DELETE FROM adempiere.m_rmatax WHERE m_rma_id IN (SELECT m_rma_id FROM adempiere.m_rma WHERE AD_Client_ID = ?)",
//			    // This one has 2 placeholders
//			    "UPDATE adempiere.m_inout SET m_rma_id = NULL WHERE m_rma_id IN (SELECT m_rma_id FROM adempiere.m_rma WHERE AD_Client_ID = ?) AND AD_Client_ID = ?",
//			    "DELETE FROM adempiere.m_rma WHERE AD_Client_ID = ?",
//			    "DELETE FROM adempiere.m_inoutline WHERE m_inout_id IN (SELECT m_inout_id FROM adempiere.m_inout WHERE AD_Client_ID = ?)",
//			    "DELETE FROM adempiere.m_inout WHERE AD_Client_ID = ?"
//			};
//
//			// 3. Execute all statements
//			for (String sql : deleteStatements) {
//			    int count = sql.length() - sql.replace("?", "").length(); // count placeholders
//			    if (count == 1) {
//			        DB.executeUpdateEx(sql, new Object[]{client_id}, trxName);
//			    } else if (count == 2) {
//			        DB.executeUpdateEx(sql, new Object[]{client_id, client_id}, trxName);
//			    }
//			}
//			trx.commit();
//			
//			String sqlUpdateOrders = "UPDATE adempiere.c_order SET docstatus = 'DR', docaction = 'CO', processed = 'N' WHERE AD_Client_ID = ?";
//			String sqlDeleteCostHistory = "DELETE FROM adempiere.m_costhistory WHERE m_costdetail_id IN (" +
//			        "SELECT m_costdetail_id FROM adempiere.m_costdetail " +
//			        "WHERE c_orderline_id IN (" +
//			        "SELECT c_orderline_id FROM adempiere.c_orderline " +
//			        "WHERE c_order_id IN (" +
//			        "SELECT c_order_id FROM adempiere.c_order WHERE AD_Client_ID = ?)))";
//			String sqlDeleteCostDetail = "DELETE FROM adempiere.m_costdetail WHERE c_orderline_id IN (SELECT c_orderline_id FROM adempiere.c_orderline WHERE c_order_id IN (SELECT c_order_id FROM adempiere.c_order WHERE AD_Client_ID = ?))";
//			String sqlDeleteOrderTax = "DELETE FROM adempiere.c_ordertax WHERE c_order_id IN (SELECT c_order_id FROM adempiere.c_order WHERE AD_Client_ID = ?)";
//			String sqlDeleteProductLabelFromOrder = "DELETE FROM adempiere.pi_productlabel " +
//				    "WHERE c_orderline_id IN (SELECT c_orderline_id FROM adempiere.c_orderline WHERE ad_client_id = ?)";
//			String sqlDeletePPCostCollectorMA = "DELETE FROM pp_cost_collectorma " +
//			        "WHERE pp_cost_collector_id IN (" +
//			        "   SELECT pp_cost_collector_id FROM pp_cost_collector " +
//			        "   WHERE pp_order_id IN (" +
//			        "       SELECT pp_order_id FROM pp_order " +
//			        "       WHERE c_orderline_id IN (" +
//			        "           SELECT c_orderline_id FROM c_orderline " +
//			        "           WHERE c_order_id IN (SELECT c_order_id FROM c_order WHERE ad_client_id = ?)" +
//			        "       )" +
//			        "   )" +
//			        ")";
//			String sqlDeletePPCostCollector = "DELETE FROM pp_cost_collector " +
//			        "WHERE pp_order_id IN (" +
//			        "   SELECT pp_order_id FROM pp_order " +
//			        "   WHERE c_orderline_id IN (" +
//			        "       SELECT c_orderline_id FROM c_orderline " +
//			        "       WHERE c_order_id IN (SELECT c_order_id FROM c_order WHERE ad_client_id = ?)" +
//			        "   )" +
//			        ")";
//			String sqlDeleteProductLAbel = "DELETE FROM adempiere.pi_productlabel\n"
//					+ "WHERE pi_orderreceipt_id IN (SELECT pi_orderreceipt_id FROM adempiere.pi_orderreceipt WHERE ad_client_id = ?)";
//			
//			String sqlDeletePIOrderReceipt = "DELETE FROM pi_orderreceipt " +
//			        "WHERE pp_order_id IN (" +
//			        "   SELECT pp_order_id FROM pp_order " +
//			        "   WHERE c_orderline_id IN (" +
//			        "       SELECT c_orderline_id FROM c_orderline " +
//			        "       WHERE c_order_id IN (SELECT c_order_id FROM c_order WHERE ad_client_id = ?)" +
//			        "   )" +
//			        ")";
//			String sqlDeletePPMRP = "DELETE FROM pp_mrp " +
//			        "WHERE pp_order_bomline_id IN (" +
//			        "   SELECT pp_order_bomline_id FROM pp_order_bomline " +
//			        "   WHERE pp_order_bom_id IN (" +
//			        "       SELECT pp_order_bom_id FROM pp_order_bom " +
//			        "       WHERE pp_order_id IN (" +
//			        "           SELECT pp_order_id FROM pp_order " +
//			        "           WHERE c_orderline_id IN (" +
//			        "               SELECT c_orderline_id FROM c_orderline " +
//			        "               WHERE c_order_id IN (SELECT c_order_id FROM c_order WHERE ad_client_id = ?)" +
//			        "           )" +
//			        "       )" +
//			        "   )" +
//			        ")";
//			String sqlDeletePPOrderBOMLineTrl = "DELETE FROM pp_order_bomline_trl " +
//			        "WHERE pp_order_bomline_id IN (" +
//			        "   SELECT pp_order_bomline_id FROM pp_order_bomline " +
//			        "   WHERE pp_order_bom_id IN (" +
//			        "       SELECT pp_order_bom_id FROM pp_order_bom " +
//			        "       WHERE pp_order_id IN (" +
//			        "           SELECT pp_order_id FROM pp_order " +
//			        "           WHERE c_orderline_id IN (" +
//			        "               SELECT c_orderline_id FROM c_orderline " +
//			        "               WHERE c_order_id IN (SELECT c_order_id FROM c_order WHERE ad_client_id = ?)" +
//			        "           )" +
//			        "       )" +
//			        "   )" +
//			        ")";
//			String sqlDeletePPOrderBOMLine = "DELETE FROM pp_order_bomline " +
//			        "WHERE pp_order_bom_id IN (" +
//			        "   SELECT pp_order_bom_id FROM pp_order_bom " +
//			        "   WHERE pp_order_id IN (" +
//			        "       SELECT pp_order_id FROM pp_order " +
//			        "       WHERE c_orderline_id IN (" +
//			        "           SELECT c_orderline_id FROM c_orderline " +
//			        "           WHERE c_order_id IN (SELECT c_order_id FROM c_order WHERE ad_client_id = ?)" +
//			        "       )" +
//			        "   )" +
//			        ")";
//			String sqlDeletePPOrderBOMTrl = "DELETE FROM pp_order_bom_trl " +
//			        "WHERE pp_order_bom_id IN (" +
//			        "   SELECT pp_order_bom_id FROM pp_order_bom " +
//			        "   WHERE pp_order_id IN (" +
//			        "       SELECT pp_order_id FROM pp_order " +
//			        "       WHERE c_orderline_id IN (" +
//			        "           SELECT c_orderline_id FROM c_orderline " +
//			        "           WHERE c_order_id IN (SELECT c_order_id FROM c_order WHERE ad_client_id = ?)" +
//			        "       )" +
//			        "   )" +
//			        ")";
//			String sqlDeletePPOrderBOM = "DELETE FROM pp_order_bom " +
//			        "WHERE pp_order_id IN (" +
//			        "   SELECT pp_order_id FROM pp_order " +
//			        "   WHERE c_orderline_id IN (" +
//			        "       SELECT c_orderline_id FROM c_orderline " +
//			        "       WHERE c_order_id IN (SELECT c_order_id FROM c_order WHERE ad_client_id = ?)" +
//			        "   )" +
//			        ")";
//			String sqlDeletePPMRPFromOrder = "DELETE FROM pp_mrp " +
//			        "WHERE pp_order_id IN (" +
//			        "   SELECT pp_order_id FROM pp_order " +
//			        "   WHERE c_orderline_id IN (" +
//			        "       SELECT c_orderline_id FROM c_orderline " +
//			        "       WHERE c_order_id IN (SELECT c_order_id FROM c_order WHERE ad_client_id = ?)" +
//			        "   )" +
//			        ")";
//			String sqlDeletePPOrderCost = "DELETE FROM pp_order_cost " +
//			        "WHERE pp_order_id IN (" +
//			        "   SELECT pp_order_id FROM pp_order " +
//			        "   WHERE c_orderline_id IN (" +
//			        "       SELECT c_orderline_id FROM c_orderline " +
//			        "       WHERE c_order_id IN (SELECT c_order_id FROM c_order WHERE ad_client_id = ?)" +
//			        "   )" +
//			        ")";
//			String sqlDeletePPOrderNodeTrl = "DELETE FROM pp_order_node_trl " +
//			        "WHERE pp_order_node_id IN (" +
//			        "   SELECT pp_order_node_id FROM pp_order_node " +
//			        "   WHERE pp_order_id IN (" +
//			        "       SELECT pp_order_id FROM pp_order " +
//			        "       WHERE c_orderline_id IN (" +
//			        "           SELECT c_orderline_id FROM c_orderline " +
//			        "           WHERE c_order_id IN (SELECT c_order_id FROM c_order WHERE ad_client_id = ?)" +
//			        "       )" +
//			        "   )" +
//			        ")";
//			String sqlDeletePPOrderWorkflowTrl = "DELETE FROM pp_order_workflow_trl " +
//			        "WHERE pp_order_workflow_id IN (" +
//			        "   SELECT pp_order_workflow_id FROM pp_order_workflow " +
//			        "   WHERE pp_order_node_id IN (" +
//			        "       SELECT pp_order_node_id FROM pp_order_node " +
//			        "       WHERE pp_order_id IN (" +
//			        "           SELECT pp_order_id FROM pp_order " +
//			        "           WHERE c_orderline_id IN (" +
//			        "               SELECT c_orderline_id FROM c_orderline " +
//			        "               WHERE c_order_id IN (SELECT c_order_id FROM c_order WHERE ad_client_id = ?)" +
//			        "           )" +
//			        "       )" +
//			        "   )" +
//			        ")";
//			String sqlDeletePPOrderWorkflow = "DELETE FROM pp_order_workflow " +
//			        "WHERE pp_order_node_id IN (" +
//			        "   SELECT pp_order_node_id FROM pp_order_node " +
//			        "   WHERE pp_order_id IN (" +
//			        "       SELECT pp_order_id FROM pp_order " +
//			        "       WHERE c_orderline_id IN (" +
//			        "           SELECT c_orderline_id FROM c_orderline " +
//			        "           WHERE c_order_id IN (SELECT c_order_id FROM c_order WHERE ad_client_id = ?)" +
//			        "       )" +
//			        "   )" +
//			        ")";
//			String sqlDeletePPOrderNode = "DELETE FROM pp_order_node " +
//			        "WHERE pp_order_id IN (" +
//			        "   SELECT pp_order_id FROM pp_order " +
//			        "   WHERE c_orderline_id IN (" +
//			        "       SELECT c_orderline_id FROM c_orderline " +
//			        "       WHERE c_order_id IN (SELECT c_order_id FROM c_order WHERE ad_client_id = ?)" +
//			        "   )" +
//			        ")";
//			String sqlDeletePPOrder = "DELETE FROM pp_order " +
//			        "WHERE c_orderline_id IN (" +
//			        "   SELECT c_orderline_id FROM c_orderline " +
//			        "   WHERE c_order_id IN (SELECT c_order_id FROM c_order WHERE ad_client_id = ?)" +
//			        ")";
//			String sqlDeletePPMRPFromOrderLine = "DELETE FROM pp_mrp " +
//			        "WHERE c_orderline_id IN (" +
//			        "    SELECT c_orderline_id FROM c_orderline " +
//			        "    WHERE c_order_id IN (SELECT c_order_id FROM c_order WHERE ad_client_id = ?)" +
//			        ")";
//			String sqlDeletePPMRPs = "DELETE FROM adempiere.pp_mrp\n"
//					+ "WHERE c_orderline_id IN (\n"
//					+ "    SELECT c_orderline_id \n"
//					+ "    FROM adempiere.c_orderline \n"
//					+ "    WHERE AD_Client_ID = ? \n"
//					+ ");";
//			String sqlDeleteOrderLines = "DELETE FROM adempiere.c_orderline WHERE c_order_id IN (SELECT c_order_id FROM adempiere.c_order WHERE AD_Client_ID = ?)";
//			String sqlDeleteOrders = "DELETE FROM adempiere.c_order WHERE AD_Client_ID = ?";
//
//			DB.executeUpdateEx(sqlUpdateOrders, new Object[] { client_id }, trxName);
//			DB.executeUpdateEx(sqlDeleteCostHistory, new Object[] { client_id }, trxName);
//			DB.executeUpdateEx(sqlDeleteCostDetail, new Object[] { client_id }, trxName);
//			DB.executeUpdateEx(sqlDeleteOrderTax, new Object[] { client_id }, trxName);
//			DB.executeUpdateEx(sqlDeleteProductLabelFromOrder, new Object[] { client_id }, trxName);
//			DB.executeUpdateEx(sqlDeletePPCostCollectorMA, new Object[]{client_id}, trxName);
//
//			DB.executeUpdateEx(sqlDeletePPCostCollector, new Object[]{client_id}, trxName);
//			DB.executeUpdateEx(sqlDeleteProductLAbel, new Object[]{client_id}, trxName);
//			DB.executeUpdateEx(sqlDeletePIOrderReceipt, new Object[]{client_id}, trxName);
//			DB.executeUpdateEx(sqlDeletePPMRP, new Object[]{client_id}, trxName);
//
//			DB.executeUpdateEx(sqlDeletePPOrderBOMLineTrl, new Object[]{client_id}, trxName);
//
//			DB.executeUpdateEx(sqlDeletePPOrderBOMLine, new Object[]{client_id}, trxName);
//			DB.executeUpdateEx(sqlDeletePPOrderBOMTrl, new Object[]{client_id}, trxName);
//
//			DB.executeUpdateEx(sqlDeletePPOrderBOM, new Object[]{client_id}, trxName);
//			DB.executeUpdateEx(sqlDeletePPMRPFromOrder, new Object[]{client_id}, trxName);
//			DB.executeUpdateEx(sqlDeletePPOrderCost, new Object[]{client_id}, trxName);
//			DB.executeUpdateEx(sqlDeletePPOrderNodeTrl, new Object[]{client_id}, trxName);
//			DB.executeUpdateEx(sqlDeletePPOrderWorkflowTrl, new Object[]{client_id}, trxName);
//
//			DB.executeUpdateEx(sqlDeletePPOrderWorkflow, new Object[]{client_id}, trxName);
//
//			DB.executeUpdateEx(sqlDeletePPOrderNode, new Object[]{client_id}, trxName);
//
//			DB.executeUpdateEx(sqlDeletePPOrder, new Object[]{client_id}, trxName);
//			DB.executeUpdateEx(sqlDeletePPMRPFromOrderLine, new Object[]{client_id}, trxName);
//
//			DB.executeUpdateEx(sqlDeletePPMRPs, new Object[] { client_id }, trxName);
//			DB.executeUpdateEx(sqlDeleteOrderLines, new Object[] { client_id }, trxName);
//			DB.executeUpdateEx(sqlDeleteOrders, new Object[] { client_id }, trxName);
//			trx.commit();
//			
//			String sqlDeleteUserToken = "DELETE FROM adempiere.pi_userToken WHERE AD_Client_ID = ?";
//			String sqlDeleteProductLabel = "DELETE FROM adempiere.pi_productLabel WHERE AD_Client_ID = ?";
//			DB.executeUpdateEx(sqlDeleteUserToken, new Object[] { client_id }, trxName);
//			DB.executeUpdateEx(sqlDeleteProductLabel, new Object[] { client_id }, trxName);
//			trx.commit();
//
//			String deleteCostHistory = "DELETE FROM adempiere.M_CostHistory " + "WHERE M_CostDetail_ID IN ("
//					+ "SELECT M_CostDetail_ID FROM adempiere.M_CostDetail WHERE AD_Client_ID = ?" + ")";
//			String deleteCostDetail = "DELETE FROM adempiere.M_CostDetail WHERE AD_Client_ID = ?";
//			String deleteAllocations = "DELETE FROM adempiere.M_TransactionAllocation WHERE AD_Client_ID = ?";
//			String sqlDeleteTransaction = "DELETE FROM adempiere.M_Transaction WHERE AD_Client_ID = ?";
//			String sqlDeleteStorage = "DELETE FROM adempiere.M_StorageOnHand WHERE AD_Client_ID = ?";
//			String sqlDeleteStorageReservation = "DELETE FROM adempiere.m_storagereservation WHERE AD_Client_ID = ?";
//
//			DB.executeUpdateEx(deleteCostHistory, new Object[] { client_id }, trxName);
//			DB.executeUpdateEx(deleteCostDetail, new Object[] { client_id }, trxName);
//			DB.executeUpdateEx(deleteAllocations, new Object[] { client_id }, trxName);
//			DB.executeUpdateEx(sqlDeleteTransaction, new Object[] { client_id }, trxName);
//			DB.executeUpdateEx(sqlDeleteStorage, new Object[] { client_id }, trxName);
//			DB.executeUpdateEx(sqlDeleteStorageReservation, new Object[] { client_id }, trxName);
//			trx.commit();
//
//			String sqlDeleteMovementLine = "DELETE FROM adempiere.M_MovementLine WHERE M_Movement_ID IN (SELECT M_Movement_ID FROM adempiere.M_Movement WHERE AD_Client_ID = ?)";
//			String sqlDeleteMovement = "DELETE FROM adempiere.M_Movement WHERE AD_Client_ID = ?";
//
//			DB.executeUpdateEx(sqlDeleteMovementLine, new Object[] { client_id }, trxName);
//			DB.executeUpdateEx(sqlDeleteMovement, new Object[] { client_id }, trxName);
//			trx.commit();
//			
//			DB.executeUpdateEx(
//				    "DELETE FROM pi_orderreceipt WHERE ad_client_id=?",
//				    new Object[]{ client_id }, trxName
//				);
//			
//			DB.executeUpdateEx(
//				    "DELETE FROM pi_machineconf WHERE ad_client_id=?",
//				    new Object[]{ client_id }, trxName
//				);
//			
//			String[] ppOrderStatements = {
//					"UPDATE ad_clientinfo SET m_productfreight_id = NULL WHERE m_productfreight_id IN (SELECT m_product_id FROM m_product WHERE ad_client_id=?)",
//				    
//					"DELETE FROM fact_acct WHERE m_product_id IN (SELECT m_product_id FROM m_product WHERE ad_client_id=?)",
//					
//				    // Delete cost records
//				    "DELETE FROM m_cost WHERE m_product_id IN (SELECT m_product_id FROM m_product WHERE ad_client_id=?)",				    
//				    // De
//					"DELETE FROM m_storageonhand WHERE m_product_id IN (SELECT m_product_id FROM m_product WHERE ad_client_id=?)",
//				    "DELETE FROM m_storagereservation WHERE m_product_id IN (SELECT m_product_id FROM m_product WHERE ad_client_id=?)",
//				    "DELETE FROM m_transaction WHERE m_product_id IN (SELECT m_product_id FROM m_product WHERE ad_client_id=?)",
//
//				    // Delete Pricing / Purchase / Replenishment
//				    "DELETE FROM m_productprice WHERE m_product_id IN (SELECT m_product_id FROM m_product WHERE ad_client_id=?)",
//				    "DELETE FROM m_product_po WHERE m_product_id IN (SELECT m_product_id FROM m_product WHERE ad_client_id=?)",
//				    "DELETE FROM m_replenish WHERE m_product_id IN (SELECT m_product_id FROM m_product WHERE ad_client_id=?)",
//
//				    // Delete Order / Invoice Lines
//				    "DELETE FROM c_orderline WHERE m_product_id IN (SELECT m_product_id FROM m_product WHERE ad_client_id=?)",
//				    "DELETE FROM c_invoiceline WHERE m_product_id IN (SELECT m_product_id FROM m_product WHERE ad_client_id=?)",
//
//				    // Delete Material Transactions
//				    "DELETE FROM m_inoutline WHERE m_product_id IN (SELECT m_product_id FROM m_product WHERE ad_client_id=?)",
//				    "DELETE FROM m_matchpo WHERE m_product_id IN (SELECT m_product_id FROM m_product WHERE ad_client_id=?)",
//				    "DELETE FROM m_matchinv WHERE m_product_id IN (SELECT m_product_id FROM m_product WHERE ad_client_id=?)",
//
//				    // PI Return Lines
//				    "DELETE FROM pi_returnline WHERE m_product_id IN (SELECT m_product_id FROM m_product WHERE ad_client_id=?)",
//
//				    // Accounting Schema Elements
//				    "DELETE FROM c_acctschema_element WHERE m_product_id IN (SELECT m_product_id FROM m_product WHERE ad_client_id=?)",
//				    "DELETE FROM m_inventoryline WHERE m_product_id IN (SELECT m_product_id FROM m_product WHERE ad_client_id=?)",
//				    "DELETE FROM m_storagereservationlog WHERE m_product_id IN (SELECT m_product_id FROM m_product WHERE ad_client_id=?)",
//				    "DELETE FROM pp_product_planning WHERE m_product_id IN (SELECT m_product_id FROM m_product WHERE ad_client_id=?)",
//				    "DELETE FROM pp_mrp WHERE m_product_id IN (SELECT m_product_id FROM m_product WHERE ad_client_id=?)",
//				    "DELETE FROM pp_product_bomline_trl WHERE pp_product_bomline_id IN (SELECT pp_product_bomline_id FROM pp_product_bomline WHERE m_product_id IN (SELECT m_product_id FROM m_product WHERE ad_client_id=?))",
//
//				    "DELETE FROM pp_product_bomline WHERE m_product_id IN (SELECT m_product_id FROM m_product WHERE ad_client_id=?)",
//				    "DELETE FROM pp_product_bom_trl WHERE pp_product_bom_id IN (SELECT pp_product_bom_id FROM pp_product_bom WHERE m_product_id IN (SELECT m_product_id FROM m_product WHERE ad_client_id=?))",
//
//				    "DELETE FROM pp_product_bom WHERE m_product_id IN (SELECT m_product_id FROM m_product WHERE ad_client_id=?)",
//
//				    // Finally delete product
//				    "DELETE FROM m_product WHERE ad_client_id=?",
//
//				    // First delete translation records
//				    "DELETE FROM pp_cost_collectorma WHERE pp_cost_collector_id IN (SELECT pp_cost_collector_id FROM pp_cost_collector WHERE pp_order_workflow_id IN (SELECT pp_order_workflow_id FROM pp_order_workflow WHERE pp_order_id IN (SELECT pp_order_id FROM pp_order WHERE ad_client_id=?)))",
//
//				    "DELETE FROM pp_cost_collector WHERE pp_order_workflow_id IN (SELECT pp_order_workflow_id FROM pp_order_workflow WHERE pp_order_id IN (SELECT pp_order_id FROM pp_order WHERE ad_client_id=?))",
//				    
//				    "DELETE FROM pp_order_workflow_trl WHERE pp_order_workflow_id IN (SELECT pp_order_workflow_id FROM pp_order_workflow WHERE pp_order_id IN (SELECT pp_order_id FROM pp_order WHERE ad_client_id=?))",
//				    
//				    // Then delete workflow related records
//				    "DELETE FROM pp_order_workflow WHERE pp_order_id IN (SELECT pp_order_id FROM pp_order WHERE ad_client_id=?)",
//				    
//				    // Delete other dependent tables (removed pp_order_transaction as it doesn't exist)
//				    "DELETE FROM pp_order_cost WHERE pp_order_id IN (SELECT pp_order_id FROM pp_order WHERE ad_client_id=?)",
//				    "DELETE FROM pp_order_bomline_trl WHERE pp_order_bomline_id IN (SELECT pp_order_bomline_id FROM pp_order_bomline WHERE pp_order_id IN (SELECT pp_order_id FROM pp_order WHERE ad_client_id=?))",
//
//				    "DELETE FROM pp_order_bomline WHERE pp_order_id IN (SELECT pp_order_id FROM pp_order WHERE ad_client_id=?)",
//				    "DELETE FROM pp_order_bom_trl WHERE pp_order_bom_id IN (SELECT pp_order_bom_id FROM pp_order_bom WHERE pp_order_id IN (SELECT pp_order_id FROM pp_order WHERE ad_client_id=?))",
//
//				    "DELETE FROM pp_order_bom WHERE pp_order_id IN (SELECT pp_order_id FROM pp_order WHERE ad_client_id=?)",
//				    "DELETE FROM pp_order_node_trl\n"
//				    + "WHERE pp_order_node_id IN (\n"
//				    + "    SELECT pp_order_node_id\n"
//				    + "    FROM pp_order_node\n"
//				    + "    WHERE ad_client_id = ?)",
//				    "DELETE FROM pp_order_node WHERE pp_order_id IN (SELECT pp_order_id FROM pp_order WHERE ad_client_id=?)",
//				    
//				    // Finally delete the main order
//				    "DELETE FROM pp_order WHERE ad_client_id=?"
//				};
//			
//			for (String sql : ppOrderStatements) {
//		        DB.executeUpdateEx(sql, new Object[]{client_id}, trxName);
//		    }
//			
//			String[] sqlStatements = {
//				    // Delete storage, reservations, etc.
//				    "DELETE FROM m_storageonhand WHERE m_product_id IN (SELECT m_product_id FROM m_product WHERE ad_client_id=?)",
//				    "DELETE FROM m_storagereservation WHERE m_product_id IN (SELECT m_product_id FROM m_product WHERE ad_client_id=?)",
//				    "DELETE FROM m_transaction WHERE m_product_id IN (SELECT m_product_id FROM m_product WHERE ad_client_id=?)",
//
//				    // Delete Pricing / Purchase / Replenishment
//				    "DELETE FROM m_productprice WHERE m_product_id IN (SELECT m_product_id FROM m_product WHERE ad_client_id=?)",
//				    "DELETE FROM m_product_po WHERE m_product_id IN (SELECT m_product_id FROM m_product WHERE ad_client_id=?)",
//				    "DELETE FROM m_replenish WHERE m_product_id IN (SELECT m_product_id FROM m_product WHERE ad_client_id=?)",
//
//				    // Delete Order / Invoice Lines
//				    "DELETE FROM c_orderline WHERE m_product_id IN (SELECT m_product_id FROM m_product WHERE ad_client_id=?)",
//				    "DELETE FROM c_invoiceline WHERE m_product_id IN (SELECT m_product_id FROM m_product WHERE ad_client_id=?)",
//
//				    // Delete Material Transactions
//				    "DELETE FROM m_inoutline WHERE m_product_id IN (SELECT m_product_id FROM m_product WHERE ad_client_id=?)",
//				    "DELETE FROM m_matchpo WHERE m_product_id IN (SELECT m_product_id FROM m_product WHERE ad_client_id=?)",
//				    "DELETE FROM m_matchinv WHERE m_product_id IN (SELECT m_product_id FROM m_product WHERE ad_client_id=?)",
//
//				    // PI Return Lines
//				    "DELETE FROM pi_returnline WHERE m_product_id IN (SELECT m_product_id FROM m_product WHERE ad_client_id=?)",
//
//				    // *** NEW: Accounting Schema Elements ***
//				    "DELETE FROM c_acctschema_element WHERE m_product_id IN (SELECT m_product_id FROM m_product WHERE ad_client_id=?)",
//
//				    // Finally delete product
//				    "DELETE FROM m_product WHERE ad_client_id=?"
//				};
//			
//			for (String sql : sqlStatements) {
//		        DB.executeUpdateEx(sql, new Object[]{client_id}, trxName);
//		    }
//
//			trx.commit();

			response.setIsError(false);
			response.setError("Data Clean Up Successfully");
		} catch (Exception e) {
			e.printStackTrace();
			response.setError(e.getMessage());
			response.setIsError(true);
		} finally {
			if (trx != null) {
				trx.close();
			}
			getCompiereService().disconnect();
		}
		return res;
	}

	@Override
	public BPartnerListResponseDocument customerList(BPartnerListRequestDocument customerListRequestDocument) {
		Trx trx = null;
		BPartnerListResponseDocument bPartnerListResponseDocument = BPartnerListResponseDocument.Factory.newInstance();
		BPartnerListResponse listResponse = bPartnerListResponseDocument.addNewBPartnerListResponse();
		BPartnerListRequest customerListRequest = customerListRequestDocument.getBPartnerListRequest();
		ADLoginRequest loginReq = customerListRequest.getADLoginRequest();
		String serviceType = customerListRequest.getServiceType();

		try {
			getCompiereService().connect();
			CompiereService m_cs = getCompiereService();
			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			Properties ctx = m_cs.getCtx();

			int client_id = loginReq.getClientID();
			org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = VeUtils.convertAdLogin(loginReq);
			String err = login(adLoginReq, webServiceName, "sOList", serviceType);
			if (err != null && err.length() > 0) {
				listResponse.setError(err);
				listResponse.setIsError(true);
				return bPartnerListResponseDocument;
			}

			if (!serviceType.equalsIgnoreCase("sOList")) {
				listResponse.setIsError(true);
				listResponse.setError("Service type " + serviceType + " not configured");
				return bPartnerListResponseDocument;
			}
			int pageNo  = customerListRequest.getPageNo() > 0 ? customerListRequest.getPageNo() : 1;
	        int pageSize = customerListRequest.getPageSize() > 0 ? customerListRequest.getPageSize() : 20;
	        
	        String searchKey = customerListRequest.getSearchKey(); 
	        boolean hasSearch = (searchKey != null && searchKey.trim().length() > 0);
	        
	        StringBuilder sql = new StringBuilder("AD_Client_ID=? AND IsActive='Y'");

	        Boolean type = customerListRequest.getIsVendor();

	        if (type) {
	            sql.append(" AND IsVendor='Y'");
	        } else { 
	            sql.append(" AND IsCustomer='Y'");
	        }

	        if (hasSearch) {
	            sql.append(" AND (UPPER(Value) LIKE ? OR UPPER(Name) LIKE ?)");
	        }
	        	        
	        Query q = new Query(ctx, MBPartner.Table_Name, sql.toString(), trxName)
	                .setParameters(client_id);

	        if (hasSearch) {
	            String like = "%" + searchKey.trim().toUpperCase() + "%";
	            q.setParameters(client_id, like, like);
	        } else {
	            q.setParameters(client_id);
	        }
	        
	        List<PO> bpartners = q.list();

	        int fromIndex = (pageNo - 1) * pageSize;
	        if (fromIndex >= bpartners.size()) {
	        	listResponse.setIsError(false);
	            return bPartnerListResponseDocument;
	        }
	        int toIndex = Math.min(fromIndex + pageSize, bpartners.size());

	        List<PO> listOfBPartner = bpartners.subList(fromIndex, toIndex);

	        if (listOfBPartner.isEmpty()) {
	            listResponse.setIsError(true);
	            listResponse.setError("Business Partner not found");
	            return bPartnerListResponseDocument;
	        }

			for (PO po : listOfBPartner) {
				MBPartner bpartner = (MBPartner) po;
				BusinessPartners cust = listResponse.addNewBusinessPartners();
				cust.setBPartnerId(bpartner.getC_BPartner_ID());
				cust.setBusinessPartnerName(bpartner.getName());
			}

			trx.commit();
			listResponse.setIsError(false);
		} catch (Exception e) {
			e.printStackTrace();
			listResponse.setError(e.getMessage());
			listResponse.setIsError(true);
		} finally {
			if (manageTrx && trx != null)
				trx.close();
			getCompiereService().disconnect();
		}
		return bPartnerListResponseDocument;
	}

	@Override
	public CreateSalesOrderResponseDocument createSalesOrderFromMobile(CreateSalesOrderRequestDocument createSalesOrderRequestDocument) {
		Trx trx = null;
		CreateSalesOrderResponseDocument createSalesOrderResponseDocument = CreateSalesOrderResponseDocument.Factory.newInstance();
		CreateSalesOrderResponse response = createSalesOrderResponseDocument.addNewCreateSalesOrderResponse();
		CreateSalesOrderRequest request = createSalesOrderRequestDocument.getCreateSalesOrderRequest();
		ADLoginRequest loginReq = request.getADLoginRequest();
		String serviceType = request.getServiceType();

		try {
			getCompiereService().connect();
			CompiereService m_cs = getCompiereService();
			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			Properties ctx = m_cs.getCtx();

			org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = VeUtils.convertAdLogin(loginReq);
			String err = login(adLoginReq, webServiceName, "sOList", serviceType);
			if (err != null && err.length() > 0) {
				response.setError(err);
				response.setIsError(true);
				return createSalesOrderResponseDocument;
			}

			if (!serviceType.equalsIgnoreCase("sOList")) {
				response.setIsError(true);
				response.setError("Service type " + serviceType + " not configured");
				return createSalesOrderResponseDocument;
			}

			COrder_Custom order = new COrder_Custom(ctx, 0, trxName);
			order.setAD_Org_ID(loginReq.getOrgID());
			order.setC_BPartner_ID(request.getCustomerId());
			order.setM_Warehouse_ID(request.getWarehouseId());
			order.setDescription(request.getDescription());
			order.setIsSOTrx(true);
			order.setIsMobileOrder(true);
			order.setDocStatus(DocAction.STATUS_Drafted);
			order.setDocAction(DocAction.ACTION_Complete);

			int userId = Env.getAD_User_ID(ctx);
			MUser_Custom user = new MUser_Custom(ctx, userId, trxName);
			X_PI_Deptartment dept = new X_PI_Deptartment(ctx, user.getPI_DEPARTMENT_ID(), trxName);
			if(dept != null && dept.get_ID() != 0) {
				order.setpidepartmentID(dept.get_ID());
			}
			
			if (!order.save()) {
				response.setError("Failed to create sales order");
				response.setIsError(true);
				return createSalesOrderResponseDocument;
			}

			response.setOrderId(order.getC_Order_ID());
			response.setDocumentNo(order.getDocumentNo());
			response.setIsError(false);

			trx.commit();
		} catch (Exception e) {
			e.printStackTrace();
			response.setError(e.getMessage());
			response.setIsError(true);
		} finally {
			if (manageTrx && trx != null)
				trx.close();
			getCompiereService().disconnect();
		}
		return createSalesOrderResponseDocument;
	}

	@Override
	public UpdateSalesOrderResponseDocument updateSalesOrder(UpdateSalesOrderRequestDocument updateSalesOrderRequestDocument) {
		Trx trx = null;
		UpdateSalesOrderResponseDocument updateSalesOrderResponseDocument = UpdateSalesOrderResponseDocument.Factory.newInstance();
		UpdateSalesOrderResponse response = updateSalesOrderResponseDocument.addNewUpdateSalesOrderResponse();
		UpdateSalesOrderRequest request = updateSalesOrderRequestDocument.getUpdateSalesOrderRequest();
		ADLoginRequest loginReq = request.getADLoginRequest();
		String serviceType = request.getServiceType();

		try {
			getCompiereService().connect();
			CompiereService m_cs = getCompiereService();
			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			Properties ctx = m_cs.getCtx();

			int clientId = loginReq.getClientID();
			org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = VeUtils.convertAdLogin(loginReq);
			String err = login(adLoginReq, webServiceName, "createSC", serviceType);
			if (err != null && err.length() > 0) {
				response.setError(err);
				response.setIsError(true);
				return updateSalesOrderResponseDocument;
			}

			if (!serviceType.equalsIgnoreCase("createSC")) {
				response.setIsError(true);
				response.setError("Service type " + serviceType + " not configured");
				return updateSalesOrderResponseDocument;
			}

			COrder_Custom order = new COrder_Custom(ctx, request.getOrderId(), trxName);
			if (order == null || order.get_ID() == 0) {
				response.setError("Sales order not found");
				response.setIsError(true);
				return updateSalesOrderResponseDocument;
			}

			List<PiProductLabel> allLabels = new ArrayList<>();
			Map<Integer, BigDecimal> productQtyMap = new HashMap<>();

			int locatorId = 0;
			for (PutAwayLine line : request.getPutAwayLineArray()) {
				String labelUUID = line.getProductLabelUUId();
				
				if(locatorId == 0)
					locatorId = line.getLocatorId();
				
				String columnName = "labelUUId";
				List<PO> poList = PiProductLabel.getPiProductLabel(columnName, labelUUID, ctx, trxName, null, clientId, loginReq.getOrgID());
				if (poList == null || poList.size() != 1) {
					response.setError("Invalid label: " + labelUUID);
					response.setIsError(true);
					return updateSalesOrderResponseDocument;
				}

				PiProductLabel label = new PiProductLabel(ctx, poList.get(0).get_ID(), trxName);
				collectAllLabels(label, allLabels, ctx, trxName, clientId,loginReq.getOrgID());
			}

			for (PiProductLabel label : allLabels) {
				int productId = label.getM_Product_ID();
				if (productId > 0) {
					BigDecimal qty = label.getQuantity();
					productQtyMap.put(productId, productQtyMap.getOrDefault(productId, BigDecimal.ZERO).add(qty));
				}
			}

			for (Map.Entry<Integer, BigDecimal> entry : productQtyMap.entrySet()) {
				MOrderLine orderLine = new MOrderLine(order);
				orderLine.setM_Product_ID(entry.getKey());
				orderLine.setQty(entry.getValue());
				orderLine.setM_Warehouse_ID(order.getM_Warehouse_ID());
				orderLine.saveEx();
				
			}

			order.setDocAction(DocAction.ACTION_Complete);
			if (!order.processIt(DocAction.ACTION_Complete)) {
				response.setError("Failed to complete order: " + order.getProcessMsg());
				response.setIsError(true);
				return updateSalesOrderResponseDocument;
			}
			order.saveEx();

			MInOut shipment = new MInOut(order, 0, order.getDateOrdered());
			shipment.setDocStatus(DocAction.STATUS_Drafted);
			shipment.setDocAction(DocAction.ACTION_Complete);
			shipment.saveEx();
			
			MInOut_Custom custom = new MInOut_Custom(ctx, shipment.get_ID(), trxName);
			custom.setPickStatus("Tranfer-To-Dispatch");
			custom.saveEx();

			MOrderLine[] orderLines = order.getLines();
			for (MOrderLine orderLine : orderLines) {
				MInOutLine shipmentLine = new MInOutLine(shipment);
				shipmentLine.setOrderLine(orderLine, 0, orderLine.getQtyOrdered());
				shipmentLine.setQty(orderLine.getQtyOrdered());
				shipmentLine.setM_Locator_ID(locatorId);
				shipmentLine.saveEx();
			}

			putAwayLabels(request.getPutAwayLineArray(), trxName, ctx, loginReq.getClientID(),
					loginReq.getOrgID(), false, false, true, loginReq.getWarehouseID());

			response.setOrderId(order.getC_Order_ID());
			response.setDocumentNo(order.getDocumentNo());
			response.setShipmentId(shipment.getM_InOut_ID());
			response.setShipmentDocumentNo(shipment.getDocumentNo());
			response.setIsError(false);

			trx.commit();
		} catch (Exception e) {
			e.printStackTrace();
			response.setError(e.getMessage());
			response.setIsError(true);
		} finally {
			if (manageTrx && trx != null)
				trx.close();
			getCompiereService().disconnect();
		}
		return updateSalesOrderResponseDocument;
	}

	private void collectAllLabels(PiProductLabel label, List<PiProductLabel> allLabels, Properties ctx, String trxName, int clientId, int orgId) {
		allLabels.add(label);
		List<PO> children = PiProductLabel.getChildsForParentLabelById(label.get_ID(), ctx, trxName, clientId, orgId);
		if (children != null) {
			for (PO child : children) {
				PiProductLabel childLabel = new PiProductLabel(ctx, child.get_ID(), trxName);
				collectAllLabels(childLabel, allLabels, ctx, trxName, clientId, orgId);
			}
		}
	}

	@Override
	public StandardResponseDocument discardProductLabel(DiscardProductLabelRequestDocument discardProductLabelRequestDocument) {
		Trx trx = null;
		StandardResponseDocument standardResponseDocument = StandardResponseDocument.Factory.newInstance();
		StandardResponse response = standardResponseDocument.addNewStandardResponse();
		DiscardProductLabelRequest request = discardProductLabelRequestDocument.getDiscardProductLabelRequest();
		ADLoginRequest loginReq = request.getADLoginRequest();
		String serviceType = request.getServiceType();

		try {
			getCompiereService().connect();
			CompiereService m_cs = getCompiereService();
			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			Properties ctx = m_cs.getCtx();

			org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = VeUtils.convertAdLogin(loginReq);
			String err = login(adLoginReq, webServiceName, "UpdateLabel", serviceType);
			if (err != null && err.length() > 0) {
				response.setError(err);
				response.setIsError(true);
				return standardResponseDocument;
			}

			if (!serviceType.equalsIgnoreCase("UpdateLabel")) {
				response.setIsError(true);
				response.setError("Service type " + serviceType + " not configured");
				return standardResponseDocument;
			}

			String labelUUID = request.getLabelUUID();
			List<PO> poList = PiProductLabel.getPiProductLabel("labelUUId", labelUUID, ctx, trxName, null, loginReq.getClientID(), loginReq.getOrgID());
			if (poList == null || poList.size() != 1) {
				response.setError("Invalid label: " + labelUUID);
				response.setIsError(true);
				return standardResponseDocument;
			}

			PiProductLabel label = new PiProductLabel(ctx, poList.get(0).get_ID(), trxName);
			if (label.isLabelDiscarded()) {
				response.setError("Label is already discarded");
				response.setIsError(true);
				return standardResponseDocument;
			}

			label.setIsLabelDiscarded(true);
			label.saveEx();

			response.setIsError(false);
			trx.commit();
		} catch (Exception e) {
			e.printStackTrace();
			response.setError(e.getMessage());
			response.setIsError(true);
		} finally {
			if (manageTrx && trx != null)
				trx.close();
			getCompiereService().disconnect();
		}
		return standardResponseDocument;
	}

	@Override
	public CreatePurchaseOrderResponseDocument createPurchaseOrderFromMobile(CreatePurchaseOrderRequestDocument req) {
		CreatePurchaseOrderResponseDocument createPurchaseOrderResponseDocument = CreatePurchaseOrderResponseDocument.Factory.newInstance();
		CreatePurchaseOrderResponse response = createPurchaseOrderResponseDocument.addNewCreatePurchaseOrderResponse();
		CreatePurchaseOrderRequest request = req.getCreatePurchaseOrderRequest();
		ADLoginRequest loginReq = request.getADLoginRequest();
		String serviceType = request.getServiceType();
		Trx trx = null;
		
		try {
			getCompiereService().connect();
			CompiereService m_cs = getCompiereService();
			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			Properties ctx = m_cs.getCtx();

			org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = VeUtils.convertAdLogin(loginReq);
			String err = login(adLoginReq, webServiceName, "poData", serviceType);
			if (err != null && err.length() > 0) {
				response.setError(err);
				response.setIsError(true);
				return createPurchaseOrderResponseDocument;
			}

			if (!serviceType.equalsIgnoreCase("poData")) {
				response.setIsError(true);
				response.setError("Service type " + serviceType + " not configured");
				return createPurchaseOrderResponseDocument;
			}
			
			int orgId = loginReq.getOrgID();
	        int warehouseId = loginReq.getWarehouseID();
	        int vendorId = request.getVendorId();
	        int clientId = loginReq.getClientID();
	        String date = request.getDateOrdered();
	        Timestamp dateOrderedTS = null;

	        if (vendorId <= 0) {
	            response.setError("Vendor (C_BPartner_ID) is required");
	            response.setIsError(true);
	            trx.rollback();
	            return createPurchaseOrderResponseDocument;
	        }
	        if (orgId <= 0 || warehouseId <= 0) {
	            response.setError("Org and Warehouse are required");
	            response.setIsError(true);
	            trx.rollback();
	            return createPurchaseOrderResponseDocument;
	        }
	        
	        MTable mDocType = MTable.get(ctx, "c_doctype");
			PO mDocTypePo = mDocType.getPO("name = 'Purchase Order' and ad_client_id = " + clientId + "",
					trx.getTrxName());
			MDocType mDocTypee = (MDocType) mDocTypePo;
			int docTypeId = mDocTypee.get_ID();
			

			if (date != null && !date.trim().isEmpty()) {
			    try {
			        dateOrderedTS = Timestamp.valueOf(date);  
			    } catch (Exception ex1) {
			        try {
			            LocalDate localDate = LocalDate.parse(date);
			            dateOrderedTS = Timestamp.valueOf(localDate.atStartOfDay());
			        } catch (Exception ex2) {
			            response.setError("Invalid date format: " + date + ". Use yyyy-MM-dd");
			            response.setIsError(true);
			            trx.rollback();
			            return createPurchaseOrderResponseDocument;
			        }
			    }
			}

			
			COrder_Custom poOrder = new COrder_Custom(ctx, 0, trxName);
			poOrder.setAD_Org_ID(orgId);
			poOrder.setC_BPartner_ID(vendorId);
			poOrder.setM_Warehouse_ID(warehouseId);
			poOrder.setDescription(request.getDescription());
			poOrder.setIsSOTrx(false);
			poOrder.setIsMobileOrder(true);
			poOrder.setC_DocTypeTarget_ID(docTypeId);
			poOrder.setDateOrdered(dateOrderedTS);
			poOrder.setDatePromised(dateOrderedTS);
			
			int userId = Env.getAD_User_ID(ctx);
			MUser_Custom user = new MUser_Custom(ctx, userId, trxName);
			X_PI_Deptartment dept = new X_PI_Deptartment(ctx, user.getPI_DEPARTMENT_ID(), trxName);
			if(dept != null && dept.get_ID() != 0) {
				poOrder.setpidepartmentID(dept.get_ID());
			}
			
			poOrder.saveEx();
			
			POLines[] lines = request.getPOLinesArray();
			
			if (lines == null || lines.length == 0) {
	            response.setError("No POLines specified");
	            response.setIsError(true);
	            trx.rollback();
	            return createPurchaseOrderResponseDocument;
	        }
			
			for (POLines lineReq : lines) {

			    int productId = lineReq.getProductId();
			    BigDecimal qty = BigDecimal.valueOf(lineReq.getQuantity() <= 0 ? 0 : lineReq.getQuantity());
			    
	            if (productId <= 0) {
	                response.setError("Invalid product id in POLines");
	                response.setIsError(true);
	                trx.rollback();
	                return createPurchaseOrderResponseDocument;
	            }
	            MProduct product = new MProduct(ctx, productId, trxName);
	            if (product == null || product.get_ID() == 0) {
	                response.setError("Product not found: " + productId);
	                response.setIsError(true);
	                trx.rollback();
	                return createPurchaseOrderResponseDocument;
	            }

	            if (qty.compareTo(BigDecimal.ZERO) <= 0) {
	                response.setError("Quantity must be > 0 for product: " + productId);
	                response.setIsError(true);
	                trx.rollback();
	                return createPurchaseOrderResponseDocument;
	            }

			    MOrderLine line = new MOrderLine(poOrder);
			    line.setM_Product_ID(productId);
			    if (product.getC_UOM_ID() > 0) {
	                line.setC_UOM_ID(product.getC_UOM_ID());
	            }
			    line.setQty(qty);
			    line.saveEx();
			}		
				poOrder.setDocAction(MOrder.DOCACTION_Complete);
				boolean processed = poOrder.processIt(MOrder.DOCACTION_Complete);
		        if (!processed) {
		            String processMsg = poOrder.getProcessMsg();
		            response.setError("Failed to process PO: " + (processMsg == null ? "unknown" : processMsg));
		            response.setIsError(true);
		            trx.rollback();
		            return createPurchaseOrderResponseDocument;
		        }
				poOrder.saveEx();
				
				trx.commit();
				response.setIsError(false);
				response.setOrderId(poOrder.get_ID());
				response.setDocumentNo(poOrder.getDocumentNo());
			
		}catch (Exception e) {
			e.printStackTrace();
			response.setError(e.getMessage());
			response.setIsError(true);
		} finally {
			if (manageTrx && trx != null)
				trx.close();
			getCompiereService().disconnect();
		}
		return createPurchaseOrderResponseDocument;
	}

	@Override
	public GetMOComponentsNewResponseDocument getMOComponentsNew(GetMOComponentsNewRequestDocument req) {
		GetMOComponentsNewResponseDocument res = GetMOComponentsNewResponseDocument.Factory.newInstance();
		GetMOComponentsNewResponse response = res.addNewGetMOComponentsNewResponse();
		GetMOComponentsNewRequest request = req.getGetMOComponentsNewRequest();
		ADLoginRequest loginReq = request.getADLoginRequest();
		String serviceType = request.getServiceType();
		boolean isPackingAndAssembly = request.getIsPackingAndAssembly();
		
		Trx trx = null;
		try {
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			getCompiereService().connect();

			org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = VeUtils.convertAdLogin(loginReq);
			String err = login(adLoginReq, webServiceName, "getMOComponents", serviceType);
			if (err != null && err.length() > 0) {
				response.setError(err);
				response.setIsError(true);
				return res;
			}

			if (!serviceType.equalsIgnoreCase("getMOComponents")) {
				response.setIsError(true);
				response.setError("Service type " + serviceType + " not configured");
				return res;
			}

			int orgId = loginReq.getOrgID();
			int clientId = loginReq.getClientID();

			int userId = Env.getAD_User_ID(ctx);
			MUser_Custom user = new MUser_Custom(ctx, userId, trxName);
			X_PI_Deptartment dept = null;
			
			if (isPackingAndAssembly) 
				dept = VeUtils.getFGSwitchAndSocketDepartment(clientId, ctx, trxName);
			else
				dept = new X_PI_Deptartment(ctx, user.getPI_DEPARTMENT_ID(), trxName);
			
			if(dept == null){
				StringBuilder builder = new StringBuilder();
				if(isPackingAndAssembly)
					builder.append("FG-Switch & Acc.");
				
				builder.append("Department Not Found");
				response.setError(builder.toString());
				response.setIsError(true);
				return res;
			}

			List<PO> poList = MProduct_Custom.getProductsWithBomNew(clientId, ctx, trxName, dept.get_ID());
			if (poList != null && poList.size() != 0) {
				for (PO po : poList) {
					MProduct_Custom product = new MProduct_Custom(ctx, po.get_ID(), trxName);
					ProductBom productBom = response.addNewProductBom();
					productBom.setProductId(product.getM_Product_ID());
					productBom.setProductName(product.getName());

					PO bom = VeUtils.getProductBom(product.getM_Product_ID(), ctx, trxName);
					if (bom != null && bom.get_ID() != 0) {
						BomFormula bomFormula = productBom.addNewBomFormula();
						X_PP_Product_BOM x_PP_Product_BOM = new X_PP_Product_BOM(ctx, bom.get_ID(), trxName);
						bomFormula.setBomFormulaId(x_PP_Product_BOM.getPP_Product_BOM_ID());
						bomFormula.setBomFormulaName(x_PP_Product_BOM.getName());
						List<PO> lineList = VeUtils.getProductBomLine(x_PP_Product_BOM.getPP_Product_BOM_ID(), ctx,
								trxName);
						if (lineList != null && lineList.size() != 0) {
							for (PO poLine : lineList) {
								X_PP_Product_BOMLine line = new X_PP_Product_BOMLine(ctx, poLine.get_ID(), trxName);
								BOMComponents component = bomFormula.addNewBomComponents();
								component.setComponentId(line.getPP_Product_BOMLine_ID());
								component.setProductId(line.getM_Product_ID());
								component.setProductName(line.getM_Product().getName());
								component.setQuantity(line.getQtyBOM().floatValue());
							}
						}
					}else
						productBom.addNewBomFormula();
				}
			}

			poList = VeUtils.getWarehouseList(clientId, orgId, ctx, trxName);
			if (poList != null && poList.size() != 0) {
				for (PO po : poList) {
					MWarehouse warehouse = new MWarehouse(ctx, po.get_ID(), trxName);
					WarehouseBom warehouseBom = response.addNewWarehouseBom();
					warehouseBom.setWarehouseId(warehouse.getM_Warehouse_ID());
					warehouseBom.setWarehouseName(warehouse.getName());

					List<PO> resourceList = VeUtils.getResourceForWarehouse(warehouse.getM_Warehouse_ID(), ctx,
							trxName);
					if (resourceList != null && resourceList.size() != 0) {
						for (PO resourcePo : resourceList) {
							X_S_Resource resource = new X_S_Resource(ctx, resourcePo.get_ID(), trxName);
							ResourceBom resourceBom = warehouseBom.addNewResourceBom();
							resourceBom.setResourceId(resource.getS_Resource_ID());
							resourceBom.setResourceName(resource.getName());
						}
					}
				}
			}
			List<PO> contractorList = VeUtils.getContractorUser(clientId, ctx, trxName);
			if(contractorList != null && contractorList.size() != 0) {
				for(PO list : contractorList) {
					MUser user2 = new MUser(ctx, list.get_ID(), trxName);
					Agent agent = response.addNewAgent();
					agent.setAgentId(user2.getAD_User_ID());
					agent.setAgentName(user2.getName());
				}
			}else {
				response.addNewAgent();
			}


			poList = VeUtils.getWorkflowList(clientId, orgId, ctx, trxName);
			if (poList != null && poList.size() != 0) {
				for (PO po : poList) {
					X_AD_Workflow workflow = new X_AD_Workflow(ctx, po.get_ID(), trxName);
					Workflow workflowBom = response.addNewWorkflow();
					workflowBom.setWorkflowId(workflow.getAD_Workflow_ID());
					workflowBom.setWorkflowName(workflow.getName());
				}
			}

			List<String> priorityList = new ArrayList<>(Arrays.asList("Urgent", "High", "Medium", "Low"));
			priorityList.forEach(it -> {
				Priority priority = response.addNewPriority();
				priority.setPriorityName(it);
			});

			List<PO> machineList = VeUtils.getMachineConfList(clientId, orgId, ctx, trxName);
			if (machineList != null && machineList.size() != 0) {
				for (PO list : machineList) {
					X_pi_machineconf machineConf = new X_pi_machineconf(ctx, list.get_ID(), trxName);
					Machinelist machinelist = response.addNewMachinelist();
					machinelist.setMachineConfigId(machineConf.getpi_machineconf_ID());
					machinelist.setMachineName(machineConf.getName());
					machinelist.setSerialNumber(machineConf.getserialnumber());
				}
			} else
				response.addNewMachinelist();

			response.setIsError(false);
			trx.commit();
		} catch (Exception e) {
			response.setIsError(true);
			response.setError(e.getMessage());
			return res;
		} finally {
			getCompiereService().disconnect();
			trx.close();
		}
		return res;
	}

}
