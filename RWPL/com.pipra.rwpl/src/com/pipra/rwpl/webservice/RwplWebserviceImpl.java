package com.pipra.rwpl.webservice;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.jws.WebService;

import org.compiere.model.IAttachmentStore;
import org.compiere.model.I_C_OrderLine;
import org.compiere.model.I_M_InOut;
import org.compiere.model.I_M_Product;
import org.compiere.model.MAcctSchema;
import org.compiere.model.MAttachment;
import org.compiere.model.MBPartner;
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
import org.compiere.model.MLocator;
import org.compiere.model.MMessage;
import org.compiere.model.MMovement;
import org.compiere.model.MMovementLine;
import org.compiere.model.MMovementLineMA;
import org.compiere.model.MNote;
import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.compiere.model.MPeriod;
import org.compiere.model.MProduct;
import org.compiere.model.MProductCategory;
import org.compiere.model.MRMA;
import org.compiere.model.MShipper;
import org.compiere.model.MStorageOnHand;
import org.compiere.model.MStorageProvider;
import org.compiere.model.MTable;
import org.compiere.model.MTransaction;
import org.compiere.model.MUser;
import org.compiere.model.MWarehouse;
import org.compiere.model.PO;
import org.compiere.model.Query;
import org.compiere.model.X_M_Product;
import org.compiere.process.DocAction;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.KeyNamePair;
import org.compiere.util.Login;
import org.compiere.util.Trx;
import org.idempiere.adinterface.CompiereService;
import org.idempiere.webservices.AbstractService;
import org.pipra.model.custom.PipraUtils;

import com.pipra.rwpl.model.AdRole_Custom;
import com.pipra.rwpl.model.COrder_Custom;
import com.pipra.rwpl.model.MInOutLineConfirm_Custom;
import com.pipra.rwpl.model.MInOutLine_Custom;
import com.pipra.rwpl.model.MInOut_Custom;
import com.pipra.rwpl.model.MLocatorType_Custom;
import com.pipra.rwpl.model.MProduct_Custom;
import com.pipra.rwpl.model.Packline;
import com.pipra.rwpl.model.PiProductLabel;
import com.pipra.rwpl.model.PiUserToken;
import com.pipra.rwpl.model.X_pi_productLabel;
import com.pipra.rwpl.utils.RwplUtils;
import com.pipra.rwpl.x10.*;
//import com.pipra.ve.x10.RoleAppAcess;


@WebService(endpointInterface = "com.pipra.rwpl.webservice.RwplWebservice", serviceName = "RwplWebservice", targetNamespace = "http://pipra.com/Rwpl/1_0")
public class RwplWebserviceImpl extends AbstractService implements RwplWebservice {

	public static final String ROLE_TYPES_WEBSERVICE = "NULL,WS,SS";
	private static String webServiceName = new String("RwplWebservice");

	private boolean manageTrx = true;

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
			int roleId = loginReq.getRoleID();
			String serviceType = roleRequest.getServiceType();

			org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = RwplUtils.convertAdLogin(loginReq);
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
			g.setAppName("ispickbyorder");
			g.setAppAcess(adRole_Custom.ispickbyorder());
			
			RoleAppAcess h = roleResponse.addNewAppAcess();
			h.setAppName("mergeApp");
			h.setAppAcess(adRole_Custom.ismergeapp());
			
			RoleAppAcess i = roleResponse.addNewAppAcess();
			i.setAppName("splitApp");
			i.setAppAcess(adRole_Custom.issplitapp());
			
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
			ad.setAppName("qcCheckApp");
			ad.setAppAcess(adRole_Custom.isQcCheckApp());
			
			RoleAppAcess ae = roleResponse.addNewAppAcess();
			ae.setAppName("supervisorPutaway");
			ae.setAppAcess(adRole_Custom.isMaterialReceipt());
			
			boolean flag = true;
			if (deviceToken != null && !deviceToken.isEmpty())
				flag = PiUserToken.checkTokenExistForuser(deviceToken, Env.getAD_User_ID(ctx), ctx, trxName);
			if (!flag) {
				PiUserToken piUserToken = new PiUserToken(ctx, 0, trxName);
				piUserToken.setAD_User_ID(Env.getAD_User_ID(ctx));
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
		try {
			getCompiereService().connect();
			CompiereService m_cs = getCompiereService();
			int client_id = loginReq.getClientID();
			int roleId = loginReq.getRoleID();
			
			org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = RwplUtils.convertAdLogin(loginReq);
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

			List<Integer> orgList = new ArrayList<>();
			Login login = new Login(m_cs.getCtx());
			KeyNamePair[] orgs = login.getOrgs(new KeyNamePair(roleId, ""));
			if (orgs != null) {
				for (KeyNamePair org : orgs) {
					orgList.add(Integer.valueOf(org.getID()));
				}
			}
			String orgIds = orgList.stream().map(Object::toString).collect(Collectors.joining(", "));

			String query = "SELECT\n" + "    DISTINCT(po.documentno) AS purchase_order,\n"
					+ "    po.c_order_id AS cOrderId,\n" + "    bp.name AS Supplier, po.created,\n"
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
					+ "and po.ad_org_id IN (" + orgIds + ") ORDER BY po.created desc;\n" + "";

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
			
			org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = RwplUtils.convertAdLogin(loginReq);
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

			pstm = DB.prepareStatement(query.toString(), null);
			rs = pstm.executeQuery();

			int qnty = 0;
			int cOrderId = 0;
			while (rs.next()) {
				ProductData productData = pODataResponse.addNewProductData();
				int outstandingQty = rs.getInt("outstanding_qty");
				int totalQnty = rs.getInt("totalQnty");
				String productName = rs.getString("product_name");
				int productId = rs.getInt("productId");
				cOrderId = rs.getInt("c_order_id");
				int cOrderlineId = rs.getInt("c_orderline_id");
				int uomId = rs.getInt("c_uom_id");
				productData.setProductId(productId);
				productData.setProductName(productName);
				productData.setCOrderlineId(cOrderlineId);
				productData.setUomId(uomId);
				productData.setOutstandingQnty(outstandingQty);
				productData.setTotalQuantity(totalQnty);
				qnty += Integer.valueOf(outstandingQty);

			}
			pODataResponse.setCOrderId(cOrderId);
			pODataResponse.setOverallQnty(qnty);
			pstm.close();
			rs.close();

			query = "SELECT\n" + "    TO_CHAR(po.dateordered, 'DD/MM/YYYY') AS Order_Date,\n"
					+ "    bp.name AS Supplier,\n" + "    wh.name AS Warehouse_Name,\n" + "    po.description,\n"
					+ "    po.docstatus,\n" + "    ml.m_locator_id,\n" + "	CASE\n"
					+ "   	 WHEN po.docstatus = 'CO' AND mr.m_inout_id IS NULL THEN false\n"
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
				int mLocatorId = rs.getInt("m_locator_id");
				pODataResponse.setOrderDate(orderDate);
				pODataResponse.setDocstatus(docStatus);
				pODataResponse.setSupplier(supplier);
				pODataResponse.setWarehouseName(warehouseName);
				if (description == null)
					pODataResponse.setDescription("");
				else
					pODataResponse.setDescription(description);
				pODataResponse.setDefaultLocatorId(mLocatorId);
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
	public GetMRComponentsResponseDocument getMrComponents(GetMRComponentsRequestDocument req) {

		GetMRComponentsResponseDocument response = GetMRComponentsResponseDocument.Factory.newInstance();
		GetMRComponentsResponse resp = response.addNewGetMRComponentsResponse();
		GetMRComponentsRequest putAwayDetailRequest = req.getGetMRComponentsRequest();
		ADLoginRequest loginReq = putAwayDetailRequest.getADLoginRequest();
		String serviceType = putAwayDetailRequest.getServiceType();
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

			org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = RwplUtils.convertAdLogin(loginReq);
			String err = login(adLoginReq, webServiceName, "getMrComponents", serviceType);
			if (err != null && err.length() > 0) {
				resp.setError(err);
				resp.setIsError(true);
				return response;
			}

			if (!serviceType.equalsIgnoreCase("getMrComponents")) {
				resp.setIsError(true);
				resp.setError("Service type " + serviceType + " not configured");
				return response;
			}
			int clientId = loginReq.getClientID();
//			int orgId = loginReq.getOrgID();

			List<Integer> orgList = new ArrayList<>();
			Login login = new Login(m_cs.getCtx());
			KeyNamePair[] orgs = login.getOrgs(new KeyNamePair(adLoginReq.getRoleID(), ""));
			if (orgs != null) {
				for (KeyNamePair org : orgs) {
					orgList.add(Integer.valueOf(org.getID()));
				}
			}
			String orgIds = orgList.stream().map(Object::toString).collect(Collectors.joining(", "));

			List<PO> poList = RwplUtils.getWarehousesByClientOrg(ctx, trxName, clientId, orgIds);
			for (PO po : poList) {
				MWarehouse wh = new MWarehouse(ctx, po.get_ID(), trxName);
				Warehouse warehouse = resp.addNewWarehouse();
				warehouse.setWarehouseId(String.valueOf(wh.get_ID()));
				warehouse.setWarehouse(wh.getName());

				int M_Locator_ID = 0;
				List<MLocator> locators = MLocatorType_Custom.getLocatorsByType(ctx, trxName, loginReq.getWarehouseID(),
						"receiving", "Y");
				if (locators != null && locators.size() != 0) {
					M_Locator_ID = locators.get(0).get_ID();
				} else {
					resp.setIsError(true);
					resp.setError("Receiving Locator Not Found");
					return response;
				}
				warehouse.setDefaultLocatorId(M_Locator_ID);
			}

			poList = RwplUtils.getProductsByClientOrg(ctx, trxName, clientId, orgIds);
			for (PO po : poList) {
				MProduct_Custom prd = new MProduct_Custom(ctx, po.get_ID(), trxName);
				Product product = resp.addNewProduct();
				product.setProductId(prd.get_ID());
				product.setProductName(prd.getName());
				product.setUomId(prd.getC_UOM_ID());
				product.setUomName(prd.getC_UOM().getName());
				int qnty = 0;
				if (prd.getLabelQnty() != null)
					qnty = prd.getLabelQnty().intValue();
				product.setLabelQnty(qnty);

				int categoryId = 0;
				String categoryName = "";
				if (prd.getM_Product_Category_ID() != 0) {
					categoryId = prd.getM_Product_Category_ID();
					categoryName = prd.getM_Product_Category().getName();
				}
				product.setProductCategoryId(categoryId);
				product.setProductCategoryName(categoryName);
			}

			poList = RwplUtils.getBPartnersByClientOrg(ctx, trxName, clientId, orgIds);
			for (PO po : poList) {
				MBPartner bp = new MBPartner(ctx, po.get_ID(), trxName);
				BusinessPartner businessPartner = resp.addNewBusinessPartner();
				businessPartner.setBusinessPartnerID(bp.get_ID());
				businessPartner.setBusinessPartnerName(bp.getName());
				businessPartner.setAddress(bp.getPrimaryC_BPartner_Location().getName());
			}

			poList = RwplUtils.getProductCategoryClientOrg(ctx, trxName, clientId, orgIds);
			for (PO po : poList) {
				MProductCategory mpc = new MProductCategory(ctx, po.get_ID(), trxName);
				ProductCategory productCategory = resp.addNewProductCategory();
				productCategory.setProductCategoryId(mpc.getM_Product_Category_ID());
				productCategory.setProductCategoryName(mpc.getName());
			}

			resp.setIsError(false);
			trx.commit();
		} catch (Exception e) {
			resp.setIsError(true);
			resp.setError(e.getMessage());
			return response;
		} finally {
			closeDbCon(pstm, rs);
			getCompiereService().disconnect();
			trx.close();
		}
		return response;
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
			
			org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = RwplUtils.convertAdLogin(loginReq);
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

			MRLines[] MRLinesArray = createMRRequest.getMRLinesArray();

			MTable docTypeTable = MTable.get(ctx, "c_doctype");
			PO docTypePO = docTypeTable.getPO("name = 'MM Receipt' and ad_client_id = " + clientID + "",
					trx.getTrxName());
			MDocType mDocType = (MDocType) docTypePO;

			MTable table = MTable.get(ctx, "c_order");
			PO po = table.getPO(cOrderId, trx.getTrxName());

			MInOut inout = null;
			int clientId = loginReq.getClientID();
			int orgId = loginReq.getOrgID();
			int userId = Env.getAD_User_ID(ctx);
			if (po.get_ID() == 0) {
				int warehouseId = createMRRequest.getWarehouseId();
				int bPartnerId = createMRRequest.getBPartnerId();
				String description = createMRRequest.getDescription();
				Timestamp movementDate = new Timestamp(createMRRequest.getMovementdate().getTimeInMillis());

				inout = new MInOut_Custom(ctx, trxName, clientId, orgId, userId, bPartnerId,
						warehouseId, mDocType, movementDate, description);
				inout.saveEx();

				mInoutId = inout.getM_InOut_ID();

				for (MRLines data : MRLinesArray) {
					int qnty = data.getQnty();
					int M_Product_ID = data.getProductId();
					int C_UOM_ID = data.getUomId();
					BigDecimal QtyEntered = BigDecimal.valueOf(qnty);
					int M_Locator_ID = data.getLocator();

					MInOutLine iol = new MInOutLine(ctx, 0, trxName);
					iol.setM_InOut_ID(mInoutId);
					iol.setM_Product_ID(M_Product_ID, C_UOM_ID); // Line UOM
					iol.setQty(QtyEntered);

					iol.setMovementQty(QtyEntered);
					iol.setC_UOM_ID(C_UOM_ID);
					iol.setM_Warehouse_ID(warehouseId);
					iol.setM_Locator_ID(M_Locator_ID);
					iol.saveEx();

					data.setMrLineId(iol.get_ID());
					
					createPackLineforInoutLine(ctx, trxName, data.getPackLineArray(), qnty, iol.get_ID());
				}

			} else {
				MOrder order = (MOrder) po;

				MInOut receipt = new MInOut(order, mDocType.get_ID(), order.getDateOrdered());
				receipt.setDocStatus(DocAction.STATUS_Drafted);
				receipt.saveEx();

				mInoutId = receipt.getM_InOut_ID();
				inout = new MInOut(ctx, mInoutId, trx.getTrxName());
				for (MRLines data : MRLinesArray) {
					int C_InvoiceLine_ID = 0;
					int M_RMALine_ID = 0;
					int M_Product_ID = data.getProductId();
					int C_UOM_ID = data.getUomId();
					int C_OrderLine_ID = data.getCOrderlineId();
					BigDecimal QtyEntered = BigDecimal.valueOf(data.getQnty());
					int M_Locator_ID = data.getLocator();

					inout.createLineFrom(C_OrderLine_ID, C_InvoiceLine_ID, M_RMALine_ID, M_Product_ID, C_UOM_ID,
							QtyEntered, M_Locator_ID);
					MInOutLine lineArray[] = MInOutLine.get(ctx, C_OrderLine_ID, trxName);
					MInOutLine line = lineArray[lineArray.length - 1];
					data.setMrLineId(line.get_ID());
					
					Packline.deletepackLine(ctx, trxName, line.get_ID());
					createPackLineforInoutLine(ctx, trxName, data.getPackLineArray(), data.getQnty(), line.get_ID());
				}
				inout.updateFrom(order, m_invoice, m_rma);
			}
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

			RwplUtils.sendNotificationAsync(true, false, inout.get_Table_ID(), inout.getM_InOut_ID(), ctx, trxName,
					"New Inward: " + inout.getDocumentNo() + "",
					" Inward - " + inout.getDocumentNo() + " added to process", inout.get_TableName(), data,
					loginReq.getClientID(), "MaterialReciptCreated");

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

	private void createPackLineforInoutLine(Properties ctx, String trxName, PackLine[] packlineArray, int qnty,
int inoutLineId) {

int packCount = 1;
for (PackLine packline : packlineArray) {
createPackLine(ctx, trxName, inoutLineId, packCount, packline.getPackCount());
qnty = qnty - packline.getPackCount();
packCount++;
}

if (qnty > 0) {
createPackLine(ctx, trxName, inoutLineId, packCount, qnty);
}
}
	
	private void createPackLine(Properties ctx, String trxName, int minoutLineId, int packCount, int qnty) {
		Packline lineNew = new Packline(ctx, 0, trxName);
		lineNew.setM_InOutLine_ID(minoutLineId);
		lineNew.setAD_Org_ID(Env.getAD_Org_ID(ctx));
		lineNew.setlabel("Pack " + packCount);
		lineNew.setquantity(BigDecimal.valueOf(qnty));
		lineNew.saveEx();
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
		try {
			CompiereService m_cs = getCompiereService();
			int clientId = loginReq.getClientID();
			int roleId = loginReq.getRoleID();
			getCompiereService().connect();

			org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = RwplUtils.convertAdLogin(loginReq);
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
			List<Integer> orgList = new ArrayList<>();
			Login login = new Login(m_cs.getCtx());
			KeyNamePair[] orgs = login.getOrgs(new KeyNamePair(roleId, ""));
			if (orgs != null) {
				for (KeyNamePair org : orgs) {
					orgList.add(Integer.valueOf(org.getID()));
				}
			}
			String orgIds = orgList.stream().map(Object::toString).collect(Collectors.joining(", "));

			String query = "SELECT\n" + "    DISTINCT(po.documentno) AS documentNo,\n" + "    bp.name AS Supplier,\n"
					+ "    po.m_inout_id AS mInoutID,\n" + "    TO_CHAR(po.dateordered, 'DD/MM/YYYY') AS Order_Date,\n"
					+ "    wh.name AS Warehouse_Name,\n"
					+ "    po.description, po.m_inout_id, TO_CHAR(po.created, 'DD/MM/YYYY') AS mrDate,\n"
					+ "po.pickStatus,	co.documentno as orderDocumentno, po.created \n" + "FROM m_inout po\n"
					+ "JOIN c_bpartner bp ON po.c_bpartner_id = bp.c_bpartner_id \n"
					+ "LEFT JOIN c_order co on co.c_order_id = po.c_order_id\n"
					+ "JOIN m_warehouse wh ON po.m_warehouse_id = wh.m_warehouse_id\n" + "WHERE po.ad_client_id = "
					+ clientId + " AND po.issotrx = '" + isSalesTransaction
					+ "' AND po.docstatus = 'DR' AND po.ad_org_id IN (" + orgIds + ") AND po.ad_orgtrx_id is null "
					+ "AND (\n" + "    po.documentno ILIKE '%' || COALESCE(?, po.documentno) || '%'\n"
					+ "    OR bp.name ILIKE '%' || COALESCE(?, bp.name) || '%'\n"
					+ "    OR wh.name ILIKE '%' || COALESCE(?, wh.name) || '%'\n"
					+ "    OR po.description ILIKE '%' || COALESCE(?, po.description) || '%'\n"
					+ "    OR co.documentno ILIKE '%' || COALESCE(?, co.documentno) || '%'\n" + ")\n"
					+ " ORDER BY po.created desc;";

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
				String mrDate = rs.getString("mrDate");
				String orderDocumentno = rs.getString("orderDocumentno");
				String warehouseName = rs.getString("Warehouse_Name");
				String description = rs.getString("description");
				String pickStatus = rs.getString("pickStatus");

				boolean isPickStatusValid = pickStatus != null && pickStatus.equalsIgnoreCase("QC");
				boolean isStatusValid = (status == null && pickStatus == null)
						|| (status != null && status.equals(pickStatus));

				if (!isPickStatusValid && isStatusValid) {
					MRList mRList = listResponse.addNewMRList();
					mRList.setDocumentNo(documentNo);
					mRList.setMInoutID(mInoutID);
					mRList.setSupplier(supplier);
					mRList.setOrderDate(date == null ? mrDate : date);
					mRList.setWarehouseName(warehouseName);
					mRList.setOrderDocumentno(orderDocumentno == null ? "" : orderDocumentno);
					if (description == null)
						mRList.setDescription("");
					else
						mRList.setDescription(description);
					count++;
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
		getCompiereService().connect();
		
		org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = RwplUtils.convertAdLogin(loginReq);
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
			String query = "SELECT\n" + "    bp.name AS Supplier,mi.c_order_id,\n" + "    mi.docstatus,TO_CHAR(mi.created, 'DD/MM/YYYY') AS mrDate,\n"
					+ "    bpl.name AS bpLocation,\n" + "    TO_CHAR(mi.dateordered, 'DD/MM/YYYY') AS Order_Date,\n"
					+ "    ml.movementqty,\n" + "    wh.name AS Warehouse_Name,\n" + "    mi.description,\n"
					+ "    ml.m_inoutline_id,\n" + "    ml.m_locator_id,\n" + "    mloctr.value AS locatorName,\n"
					+ "    ml.M_inout_id,  -- Added this field\n" + "    co.documentno as orderDocumentno,\n"
					+ "    mp.name, mp.m_product_id,ml.c_uom_id, ml.c_orderline_id\n" + "FROM m_inoutline ml\n"
					+ "JOIN m_inout mi ON mi.m_inout_id = ml.m_inout_id\n"
					+ "JOIN c_bpartner bp ON mi.c_bpartner_id = bp.c_bpartner_id\n"
					+ "JOIN c_bpartner_location bpl ON bpl.c_bpartner_id = bp.c_bpartner_id\n"
					+ "JOIN m_warehouse wh ON mi.m_warehouse_id = wh.m_warehouse_id\n"
					+ "JOIN m_product mp on mp.m_product_id = ml.m_product_id\n"
					+ "JOIN m_locator mloctr ON mloctr.m_locator_id = ml.m_locator_id\n"
					+ "LEFT JOIN c_order co on mi.c_order_id = co.c_order_id\n" + "WHERE mi.documentNo = '" + documentNo
					+ "' AND mi.ad_client_id = " + clientId + " ORDER BY ml.m_inoutline_id;";

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
			int lineCount = 0;
			int mInoutId = 0;
			int cOrderId = 0;
			while (rs.next()) {
				MRLine mRLine = listResponse.addNewMRLines();
				supplier = rs.getString("Supplier");
				bpLocation = rs.getString("bpLocation");
				docStatus = rs.getString("docStatus");
				date = rs.getString("Order_Date");
				String mrDate = rs.getString("mrDate");
				date = date == null ? mrDate : date;
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
				orderDocumentno = rs.getString("orderDocumentno");
				orderDocumentno = orderDocumentno == null ? "" : orderDocumentno;
				cOrderId = rs.getInt("c_order_id");
				mRLine.setProductName(productName);
				mRLine.setProductID(mProductID);
				mRLine.setMRLineId(mInoutlineId);
				mRLine.setCOrderlineId(cOrderlineID);
				mRLine.setUomID(uomID);
				mRLine.setRecievedQnty(receivedQnty);
				mRLine.setLocatorId(locatorId);
				mRLine.setLocatorName(locatorName);
				count += receivedQnty;
				lineCount++;
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
			if (description == null)
				listResponse.setDescription("");
			else
				listResponse.setDescription(description);
			listResponse.setOverallQnty(count);

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

			org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = RwplUtils.convertAdLogin(loginReq);
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

		try {
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();

			int clientId = loginReq.getClientID();
			getCompiereService().connect();

			org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = RwplUtils.convertAdLogin(loginReq);
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

			String query = "select a.m_inventory_id,\n" + "TO_CHAR(a.movementdate, 'DD/MM/YYYY') AS Date,\n"
					+ "b.name as Warehouse_Name,\n" + "c.name as Org_Name,\n" + "a.description,\n" + "a.documentno\n"
					+ "from m_inventory a\n" + "join m_warehouse b on a.m_warehouse_id = b.m_warehouse_id\n"
					+ "join ad_org c on a.ad_org_id = c.ad_org_id\n" + "where a.ad_client_id = " + clientId
					+ " and a.docstatus = 'DR' AND a.ad_org_id IN (" + orgIds + ") AND (\n"
					+ "    a.m_inventory_id::VARCHAR ILIKE '%' || COALESCE(?, a.m_inventory_id::VARCHAR) || '%'\n"
					+ "    OR c.name ILIKE '%' || COALESCE(?, c.name) || '%'\n"
					+ "    OR b.name ILIKE '%' || COALESCE(?, b.name) || '%'\n"
					+ "    OR a.description ILIKE '%' || COALESCE(?, a.description) || '%'\n"
					+ "    OR a.documentno ILIKE '%' || COALESCE(?,a.documentno) || '%'\n" + ")\n"
					+ " ORDER BY a.created desc";

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

			org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = RwplUtils.convertAdLogin(loginReq);
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

			org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = RwplUtils.convertAdLogin(loginReq);
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
	
	
   // pick by orders code, shipment will be generated based on scanned products and sales label will be generated in final dispatch app
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

			org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = RwplUtils.convertAdLogin(loginReq);
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

			List<Integer> orgList = new ArrayList<>();
			Login login = new Login(ctx);
			KeyNamePair[] orgs = login.getOrgs(new KeyNamePair(loginReq.getRoleID(), ""));
			if (orgs != null) {
				for (KeyNamePair org : orgs) {
					orgList.add(Integer.valueOf(org.getID()));
				}
			}
			
			List<MLocator> locators = MLocatorType_Custom.getLocatorsByType(ctx, trxName, loginReq.getWarehouseID(), "dispatch", "Y");
			if (locators == null || locators.size() == 0) {
				sOListResponse.setIsError(true);
				sOListResponse.setError("Dispatch Locator Not Found");
				return sOListResponseDocument;
			}

			String orgIds = orgList.stream().map(Object::toString).collect(Collectors.joining(", "));

			StringBuilder query = new StringBuilder("SELECT DISTINCT\n" + "    so.documentno as Sales_Order,\n"
					+ "    so.c_order_id AS cOrderId, so.putStatus,\n"
					+ "TO_CHAR(so.dateordered, 'DD/MM/YYYY') AS Order_Date,\n"
					+ "    wh.name AS Warehouse_Name, wh.m_warehouse_id,\n" + "bp.name AS Customer, so.created,\n"
					+ " so.description,\n" + "    CASE\n"
					+ "        WHEN so.docstatus = 'CO' AND mr.m_inout_id IS NULL THEN false\n"
					+ "        WHEN so.docstatus = 'CO' AND mr.m_inout_id IS NOT NULL THEN true \n"
					+ "    END AS status\n" + "FROM\n" + "    adempiere.c_order so\n" + "JOIN\n"
					+ "    adempiere.c_orderline sol ON so.c_order_id = sol.c_order_id\n" + "JOIN\n"
					+ "    adempiere.c_bpartner bp ON so.c_bpartner_id = bp.c_bpartner_id \n" + "JOIN\n"
					+ "    adempiere.m_warehouse wh ON so.m_warehouse_id = wh.m_warehouse_id\n" + "LEFT JOIN\n"
					+ "    adempiere.m_inout mr ON so.c_order_id = mr.c_order_id\n" + "WHERE sol.m_product_id != 0 and \n"
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

			int count = 0;
			while (rs.next()) {
				String documentNo = rs.getString("Sales_Order");
				String cOrderId = rs.getString("cOrderId");
				String customer = rs.getString("Customer");
				String date = rs.getString("Order_Date");
				String warehouseName = rs.getString("Warehouse_Name");
				String description = rs.getString("description");
				boolean Status = rs.getBoolean("status");
				String putStatus = rs.getString("putStatus");
				int mWarehouseId = rs.getInt("m_warehouse_id");

				SOListAccess sOListAccess = sOListResponse.addNewListAccess();
				sOListAccess.setDocumentNumber(documentNo);
				sOListAccess.setCOrderId(cOrderId);
				sOListAccess.setOrderDate(date);
				sOListAccess.setCustomerName(customer);
				sOListAccess.setWarehouseName(warehouseName);
				sOListAccess.setWarehouseId(mWarehouseId);

				if (putStatus == null)
					putStatus = "";
				if (description == null)
					description = "";
				sOListAccess.setDescription(description);
				sOListAccess.setStatus(putStatus);
				sOListAccess.setSalesOrderStatus(Status);

				MOrder mOrder = new MOrder(ctx, Integer.valueOf(cOrderId), trxName);
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

//				LinkedHashMap<Integer, Integer> totalOrderQnty = new LinkedHashMap<Integer, Integer>();
//				if (orderLines != null && orderLines.length != 0)
//					for (MOrderLine line : orderLines) {
//						int productId = line.getM_Product_ID();
//						int qnty = line.getQtyEntered().intValue();
//						totalQnty += qnty;
//						if (totalOrderQnty.containsKey(productId))
//							qnty += totalOrderQnty.get(productId);
//
//						totalOrderQnty.put(productId, qnty);
//					}
//
//				for (Integer key : totalOrderQnty.keySet()) {
//					int keyQnty = 0;
//					if(labelList.containsKey(key))
//						keyQnty = labelList.get(key);
//					if (labelList.containsKey(key))
//						totalOrderQnty.put(key, totalOrderQnty.get(key) - keyQnty);
//
//				}
//				for (Integer key : totalOrderQnty.keySet()) {
//					pickedQnty += totalOrderQnty.get(key);
//				}

				sOListAccess.setQuantityPicked(pickedQnty);
				sOListAccess.setQuantityTotal(totalQnty);
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

			org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = RwplUtils.convertAdLogin(loginReq);
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
			
			List<MLocator> locators = MLocatorType_Custom.getLocatorsByType(ctx, trxName, loginReq.getWarehouseID(), "dispatch", "Y");
			if (locators == null || locators.size() == 0) {
				sODetailResponse.setIsError(true);
				sODetailResponse.setError("Dispatch Locator Not Found");
				return sODetailResponseDocument;
			}
			
			MLocator dispatchLocator = locators.get(0);
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
			MOrder mOrder = null;
			
			while (rs.next()) {

				String productName = rs.getString("product_name");
				int productId = rs.getInt("productId");
				cOrderId = rs.getInt("c_order_id");
				int cOrderlineId = rs.getInt("c_orderline_id");
				int uomId = rs.getInt("c_uom_id");
				int totalQnty = rs.getInt("totalQnty");
				int outstanding_qty = rs.getInt("outstanding_qty");

				if (mOrder == null) {
					mOrder = new MOrder(ctx, cOrderId, trxName);
//					MInOut[] mInouts = mOrder.getShipments();
//					labelList = getLabelsPickedForSO(ctx, trxName, mInouts);
				}
//
//				MOrderLine orderLine = new MOrderLine(ctx, cOrderlineId, trxName);
//				int qntyTotal = orderLine.getQtyEntered().intValue();
//				int qntyPicked = 0;
//				if (labelList.containsKey(productId))
//					qntyPicked = labelList.get(productId);
//				int qntyToPick = 0;
//
//				if (qntyTotal >= qntyPicked) {
//					qntyToPick = qntyTotal - qntyPicked;
//					labelList.put(productId, 0);
//				} else
//					labelList.put(productId, qntyPicked - qntyTotal);

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
			}
			sODetailResponse.setCOrderId(cOrderId);
			sODetailResponse.setQuantityPicked(quantityPicked);
			sODetailResponse.setQuantityTotal(quantityTotal);

			LocalDateTime createdDateTime = mOrder.getCreated().toLocalDateTime();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
			String formattedDate = createdDateTime.format(formatter);

			sODetailResponse.setOrderDate(formattedDate);
			sODetailResponse.setDocStatus(mOrder.getDocStatus());
			sODetailResponse.setCustomer(mOrder.getC_BPartner().getName());
			sODetailResponse.setWarehouseName(mOrder.getM_Warehouse().getName());
			sODetailResponse.setDescription(mOrder.getDescription() != null ? mOrder.getDescription() : "");
			sODetailResponse.setOrderStatus(false);
			sODetailResponse.setLocationName(mOrder.getC_BPartner_Location().getName());
			sODetailResponse.setDocumentNo(documentNo);
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
	
	//code ended pick by orders code, shipment will be generated based on scanned products and sales label will be generated in final dispatch app


//direct pick by order code, shipment and sales label will be created at a time, in putaway commeneded code related to this which generates sales label
//	public SOListResponseDocument soList(SOListRequestDocument sOListRequestDocument) {
//		SOListResponseDocument sOListResponseDocument = SOListResponseDocument.Factory.newInstance();
//		SOListResponse sOListResponse = sOListResponseDocument.addNewSOListResponse();
//		SOListRequest sOListRequest = sOListRequestDocument.getSOListRequest();
//		ADLoginRequest loginReq = sOListRequest.getADLoginRequest();
//		String serviceType = sOListRequest.getServiceType();
//		String searchkey = sOListRequest.getSearchKey();
//		PreparedStatement pstm = null;
//		ResultSet rs = null;
//		Trx trx = null;
//		try {
//			int clientId = loginReq.getClientID();
//			getCompiereService().connect();
//			CompiereService m_cs = getCompiereService();
//			Properties ctx = m_cs.getCtx();
//			String trxName = Trx.createTrxName(getClass().getName() + "_");
//			trx = Trx.get(trxName, true);
//			trx.start();
//
//			org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = RwplUtils.convertAdLogin(loginReq);
//			String err = login(adLoginReq, webServiceName, "sOList", serviceType);
//			if (err != null && err.length() > 0) {
//				sOListResponse.setError(err);
//				sOListResponse.setIsError(true);
//				return sOListResponseDocument;
//			}
//
//			if (!serviceType.equalsIgnoreCase("sOList")) {
//				sOListResponse.setIsError(true);
//				sOListResponse.setError("Service type " + serviceType + " not configured");
//				return sOListResponseDocument;
//			}
//
//			List<Integer> orgList = new ArrayList<>();
//			Login login = new Login(ctx);
//			KeyNamePair[] orgs = login.getOrgs(new KeyNamePair(loginReq.getRoleID(), ""));
//			if (orgs != null) {
//				for (KeyNamePair org : orgs) {
//					orgList.add(Integer.valueOf(org.getID()));
//				}
//			}
//			
//			List<MLocator> locators = MLocatorType_Custom.getLocatorsByType(ctx, trxName, loginReq.getWarehouseID(), "dispatch", "Y");
//			if (locators == null || locators.size() == 0) {
//				sOListResponse.setIsError(true);
//				sOListResponse.setError("Dispatch Locator Not Found");
//				return sOListResponseDocument;
//			}
//
//			String orgIds = orgList.stream().map(Object::toString).collect(Collectors.joining(", "));
//
//			StringBuilder query = new StringBuilder("SELECT DISTINCT\n" + "    so.documentno as Sales_Order,\n"
//					+ "    so.c_order_id AS cOrderId, so.putStatus,\n"
//					+ "TO_CHAR(so.dateordered, 'DD/MM/YYYY') AS Order_Date,\n"
//					+ "    wh.name AS Warehouse_Name, wh.m_warehouse_id,\n" + "bp.name AS Customer, so.created,\n"
//					+ " so.description,\n" + "    CASE\n"
//					+ "        WHEN so.docstatus = 'CO' AND mr.m_inout_id IS NULL THEN false\n"
//					+ "        WHEN so.docstatus = 'CO' AND mr.m_inout_id IS NOT NULL THEN true \n"
//					+ "    END AS status\n" + "FROM\n" + "    adempiere.c_order so\n" + "JOIN\n"
//					+ "    adempiere.c_orderline sol ON so.c_order_id = sol.c_order_id\n" + "JOIN\n"
//					+ "    adempiere.c_bpartner bp ON so.c_bpartner_id = bp.c_bpartner_id \n" + "JOIN\n"
//					+ "    adempiere.m_warehouse wh ON so.m_warehouse_id = wh.m_warehouse_id\n" + "LEFT JOIN\n"
//					+ "    adempiere.m_inout mr ON so.c_order_id = mr.c_order_id\n" + "WHERE sol.m_product_id != 0 and \n"
//					+ "    sol.qtyordered > (\n" + "        SELECT COALESCE(SUM(iol.qtyentered), 0)\n"
//					+ "        FROM adempiere.m_inoutline iol\n"
//					+ "        WHERE iol.c_orderline_id = sol.c_orderline_id\n" + "    )\n" + "AND\n"
//					+ "    so.ad_client_id = '" + clientId + "'\n" + "AND\n" + "    so.issotrx = 'Y'\n" + "AND\n"
//					+ "    so.docstatus = 'CO' " + "AND (\n"
//					+ "    so.documentno ILIKE '%' || COALESCE(?, so.documentno) || '%'\n"
//					+ "    OR bp.name ILIKE '%' || COALESCE(?, bp.name) || '%'\n"
//					+ "    OR wh.name ILIKE '%' || COALESCE(?, wh.name) || '%'\n"
//					+ "    OR so.description ILIKE '%' || COALESCE(?, so.description) || '%'\n"
//					+ ") AND so.ad_org_id IN (" + orgIds + ") \n" + " ORDER BY so.created desc");
//
//			pstm = DB.prepareStatement(query.toString(), null);
//			pstm.setString(1, searchkey);
//			pstm.setString(2, searchkey);
//			pstm.setString(3, searchkey);
//			pstm.setString(4, searchkey);
//			rs = pstm.executeQuery();
//
//			int count = 0;
//			while (rs.next()) {
//				String documentNo = rs.getString("Sales_Order");
//				String cOrderId = rs.getString("cOrderId");
//				String customer = rs.getString("Customer");
//				String date = rs.getString("Order_Date");
//				String warehouseName = rs.getString("Warehouse_Name");
//				String description = rs.getString("description");
//				boolean Status = rs.getBoolean("status");
//				String putStatus = rs.getString("putStatus");
//				int mWarehouseId = rs.getInt("m_warehouse_id");
//
//				SOListAccess sOListAccess = sOListResponse.addNewListAccess();
//				sOListAccess.setDocumentNumber(documentNo);
//				sOListAccess.setCOrderId(cOrderId);
//				sOListAccess.setOrderDate(date);
//				sOListAccess.setCustomerName(customer);
//				sOListAccess.setWarehouseName(warehouseName);
//				sOListAccess.setWarehouseId(mWarehouseId);
//
//				if (putStatus == null)
//					putStatus = "";
//				if (description == null)
//					description = "";
//				sOListAccess.setDescription(description);
//				sOListAccess.setStatus(putStatus);
//				sOListAccess.setSalesOrderStatus(Status);
//
//				MOrder mOrder = new MOrder(ctx, Integer.valueOf(cOrderId), trxName);
//				MInOut[] mInouts = mOrder.getShipments();
//				LinkedHashMap<Integer, Integer> labelList = getLabelsPendingtoPickForSO(ctx, trxName, mInouts);
//				MOrderLine[] orderLines = mOrder.getLines();
//
//				int pickedQnty = 0;
//				int totalQnty = 0;
//				LinkedHashMap<Integer, Integer> totalOrderQnty = new LinkedHashMap<Integer, Integer>();
//				if (orderLines != null && orderLines.length != 0)
//					for (MOrderLine line : orderLines) {
//						int productId = line.getM_Product_ID();
//						int qnty = line.getQtyEntered().intValue();
//						totalQnty += qnty;
//						if (totalOrderQnty.containsKey(productId))
//							qnty += totalOrderQnty.get(productId);
//
//						totalOrderQnty.put(productId, qnty);
//					}
//
//				for (Integer key : totalOrderQnty.keySet()) {
//					int keyQnty = 0;
//					if(labelList.containsKey(key))
//						keyQnty = labelList.get(key);
//					if (labelList.containsKey(key))
//						totalOrderQnty.put(key, totalOrderQnty.get(key) - keyQnty);
//
//				}
//				for (Integer key : totalOrderQnty.keySet()) {
//					pickedQnty += totalOrderQnty.get(key);
//				}
//				sOListAccess.setQuantityPicked(pickedQnty);
//				sOListAccess.setQuantityTotal(totalQnty);
//				count++;
//			}
//			
//			sOListResponse.setCount(count);
//			trx.commit();
//		} catch (Exception e) {
//			sOListResponse.setIsError(true);
//			e.printStackTrace();
//			sOListResponse.setError(e.getMessage());
//			return sOListResponseDocument;
//		} finally {
//			trx.close();
//			closeDbCon(pstm, rs);
//			getCompiereService().disconnect();
//		}
//		return sOListResponseDocument;
//	}
//	
//	private LinkedHashMap<Integer, Integer> getLabelsPendingtoPickForSO(Properties ctx, String trxName, MInOut[] mInouts) {
//		LinkedHashMap<Integer, Integer> labelList = new LinkedHashMap<Integer, Integer>();
//
//		if (mInouts != null && mInouts.length != 0)
//			for (MInOut inout : mInouts) {
//				MInOutLine[] inOutLines = inout.getLines();
//				if (inOutLines != null && inOutLines.length != 0)
//					for (MInOutLine line : inOutLines) {
//
//						List<PO> poList = PiProductLabel.getProductLabelForInoutLine(line.get_ID(), ctx, trxName);
//						if (poList != null && poList.size() != 0)
//							for (PO po : poList) {
//
//								PiProductLabel label = new PiProductLabel(ctx, po.get_ID(), trxName);
//								int qnty = label.getquantity().intValue();
//								int productId = label.getM_Product_ID();
//								if (labelList.containsKey(productId))
//									qnty += labelList.get(productId);
//								labelList.put(productId, qnty);
//
//							}
//					}
//			}
//		return labelList;
//	}
//
//	public SODetailResponseDocument soDetails(SODetailRequestDocument sODetailRequestDocument) {
//		SODetailResponseDocument sODetailResponseDocument = SODetailResponseDocument.Factory.newInstance();
//		SODetailResponse sODetailResponse = sODetailResponseDocument.addNewSODetailResponse();
//		SODetailRequest sODetailRequest = sODetailRequestDocument.getSODetailRequest();
//		ADLoginRequest loginReq = sODetailRequest.getADLoginRequest();
//		String serviceType = sODetailRequest.getServiceType();
//		String documentNo = sODetailRequest.getDocumentNo();
//		int client_ID = loginReq.getClientID();
//		Trx trx = null;
//		PreparedStatement pstm = null;
//		ResultSet rs = null;
//		try {
//			CompiereService m_cs = getCompiereService();
//			Properties ctx = m_cs.getCtx();
//			String trxName = Trx.createTrxName(getClass().getName() + "_");
//			trx = Trx.get(trxName, true);
//			trx.start();
//
//			org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = RwplUtils.convertAdLogin(loginReq);
//			String err = login(adLoginReq, webServiceName, "sODetail", serviceType);
//			if (err != null && err.length() > 0) {
//				sODetailResponse.setError(err);
//				sODetailResponse.setIsError(true);
//				return sODetailResponseDocument;
//			}
//			if (!serviceType.equalsIgnoreCase("sODetail")) {
//				sODetailResponse.setIsError(true);
//				sODetailResponse.setError("Service type " + serviceType + " not configured");
//				return sODetailResponseDocument;
//			}
//			
//			List<MLocator> locators = MLocatorType_Custom.getLocatorsByType(ctx, trxName, loginReq.getWarehouseID(), "dispatch", "Y");
//			if (locators == null || locators.size() == 0) {
//				sODetailResponse.setIsError(true);
//				sODetailResponse.setError("Dispatch Locator Not Found");
//				return sODetailResponseDocument;
//			}
//			
//			MLocator dispatchLocator = locators.get(0);
//			sODetailResponse.setDispatchLocatorId(dispatchLocator.get_ID());
//			sODetailResponse.setDispatchLocatorName(dispatchLocator.getValue());
//			String query = null;
//			query = "SELECT a.qtyordered as totalQnty, (a.qtyordered - COALESCE(SUM(c.qtyentered), 0)) AS outstanding_qty, e.m_product_id as productId, a.c_order_id, a.c_uom_id, a.c_orderline_id, e.name AS product_name\n"
//					+ "FROM c_orderline a \n" + "JOIN c_order d ON d.c_order_id = a.c_order_id \n"
//					+ "LEFT JOIN m_inout b ON a.c_order_id = b.c_order_id \n"
//					+ "LEFT JOIN m_inoutline c ON c.m_inout_id = b.m_inout_id AND c.c_orderline_id = a.c_orderline_id\n"
//					+ "JOIN m_product e ON e.m_product_id = a.m_product_id \n" + "WHERE d.documentno = '" + documentNo
//					+ "' AND d.ad_client_id = '" + client_ID + "' AND a.c_order_id = (\n"
//					+ "  SELECT c_order_id FROM c_order WHERE documentno = '" + documentNo + "' AND ad_client_id = '"
//					+ client_ID + "'\n" + ")\n"
//					+ "GROUP BY e.m_product_id, e.name, a.qtyordered, a.c_orderline_id, a.c_uom_id, a.c_order_id ORDER BY a.c_orderline_id;\n"
//					+ "";
//
//			pstm = DB.prepareStatement(query.toString(), trxName);
//			rs = pstm.executeQuery();
//
//			int quantityPicked = 0;
//			int quantityTotal = 0;
//			int cOrderId = 0;
//			MOrder mOrder = null;
//			
//			LinkedHashMap<Integer, Integer> labelList = null;
//
//			while (rs.next()) {
//
//				String productName = rs.getString("product_name");
//				int productId = rs.getInt("productId");
//				cOrderId = rs.getInt("c_order_id");
//				int cOrderlineId = rs.getInt("c_orderline_id");
//				int uomId = rs.getInt("c_uom_id");
//
//				if (mOrder == null) {
//					mOrder = new MOrder(ctx, cOrderId, trxName);
//					MInOut[] mInouts = mOrder.getShipments();
//					labelList = getLabelsPendingtoPickForSO(ctx, trxName, mInouts);
//				}
//
//				MOrderLine orderLine = new MOrderLine(ctx, cOrderlineId, trxName);
//				int qntyTotal = orderLine.getQtyEntered().intValue();
//				int qntyPicked = 0;
//				if (labelList.containsKey(productId))
//					qntyPicked = labelList.get(productId);
//				int qntyToPick = 0;
//
//				if (qntyTotal >= qntyPicked) {
//					qntyToPick = qntyTotal - qntyPicked;
//					labelList.put(productId, 0);
//				} else
//					labelList.put(productId, qntyPicked - qntyTotal);
//
//				SoDetailProductData productData = sODetailResponse.addNewProductData();
//				productData.setProductId(productId);
//				productData.setProductName(productName);
//				productData.setCOrderlineId(cOrderlineId);
//				productData.setUomId(uomId);
//				productData.setOutstandingQnty(qntyTotal - qntyToPick);
//				productData.setTotalQuantity(qntyTotal);
//				productData.setRemainingQntyToPick(qntyToPick);
//
//				if (qntyToPick > 0) {
//					getSuggestedLocatorForProduct(productData, ctx, trxName, loginReq.getWarehouseID(), loginReq.getClientID());
//					QntyAvailableInLocator[] pickLabourLinesArray = productData.getQntyAvailableInLocatorArray();
//					if (pickLabourLinesArray.length == 0 || pickLabourLinesArray == null) {
//						productData.addNewQntyAvailableInLocator();
//					}
//				}
//
//				quantityPicked += qntyTotal - qntyToPick;
//				quantityTotal += qntyTotal;
//			}
//			sODetailResponse.setCOrderId(cOrderId);
//			sODetailResponse.setQuantityPicked(quantityPicked);
//			sODetailResponse.setQuantityTotal(quantityTotal);
//
//			LocalDateTime createdDateTime = mOrder.getCreated().toLocalDateTime();
//			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
//			String formattedDate = createdDateTime.format(formatter);
//
//			sODetailResponse.setOrderDate(formattedDate);
//			sODetailResponse.setDocStatus(mOrder.getDocStatus());
//			sODetailResponse.setCustomer(mOrder.getC_BPartner().getName());
//			sODetailResponse.setWarehouseName(mOrder.getM_Warehouse().getName());
//			sODetailResponse.setDescription(mOrder.getDescription() != null ? mOrder.getDescription() : "");
//			sODetailResponse.setOrderStatus(false);
//			sODetailResponse.setLocationName(mOrder.getC_BPartner_Location().getName());
//			sODetailResponse.setDocumentNo(documentNo);
//			trx.commit();
//		} catch (Exception e) {
//			e.printStackTrace();
//			sODetailResponse.setError(e.getMessage());
//			sODetailResponse.setIsError(true);
//			return sODetailResponseDocument;
//
//		} finally {
//			closeDbCon(pstm, rs);
//			trx.close();
//			getCompiereService().disconnect();
//		}
//		return sODetailResponseDocument;
//	}
//
	/*direct picklist code eneded*/
	
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

//	private LinkedHashMap<Integer, Integer> getQntyPickedBySoList(int clientId, int wrehouseId, Properties ctx,
//			String trxName, int orderId, String orderDocumentNo, int roleId, List<MLocator> locatorList) {
//		PreparedStatement pstm = null;
//		ResultSet rs = null;
//		LinkedHashMap<Integer, Integer> availableQnty = new LinkedHashMap<Integer, Integer>();
//		try {
//
//			List<Integer> orgList = new ArrayList<>();
//			Login login = new Login(ctx);
//			KeyNamePair[] orgs = login.getOrgs(new KeyNamePair(roleId, ""));
//			if (orgs != null) {
//				for (KeyNamePair org : orgs) {
//					orgList.add(Integer.valueOf(org.getID()));
//				}
//			}
//			String orgIds = orgList.stream().map(Object::toString).collect(Collectors.joining(", "));
//
//			
//			String query = "SELECT DISTINCT\n" + "    so.documentno as Sales_Order,\n"
//					+ "    so.c_order_id AS cOrderId, so.putStatus,\n"
//					+ "TO_CHAR(so.dateordered, 'DD/MM/YYYY') AS Order_Date,\n"
//					+ "    wh.name AS Warehouse_Name, wh.m_warehouse_id,\n" + "bp.name AS Customer, so.created,\n"
//					+ " so.description,\n" + "    CASE\n"
//					+ "        WHEN so.docstatus = 'CO' AND mr.m_inout_id IS NULL THEN false\n"
//					+ "        WHEN so.docstatus = 'CO' AND mr.m_inout_id IS NOT NULL THEN true \n"
//					+ "    END AS status\n" + "FROM\n" + "    adempiere.c_order so\n" + "JOIN\n"
//					+ "    adempiere.c_orderline sol ON so.c_order_id = sol.c_order_id\n" + "JOIN\n"
//					+ "    adempiere.c_bpartner bp ON so.c_bpartner_id = bp.c_bpartner_id \n" + "JOIN\n"
//					+ "    adempiere.m_warehouse wh ON so.m_warehouse_id = wh.m_warehouse_id\n" + "LEFT JOIN\n"
//					+ "    adempiere.m_inout mr ON so.c_order_id = mr.c_order_id\n" + "WHERE\n"
//					+ "    sol.qtyordered > (\n" + "        SELECT COALESCE(SUM(iol.qtyentered), 0)\n"
//					+ "        FROM adempiere.m_inoutline iol\n"
//					+ "        WHERE iol.c_orderline_id = sol.c_orderline_id\n" + "    )\n" + "AND\n"
//					+ "    so.ad_client_id = '" + clientId + "'\n" + "AND\n" + "    so.issotrx = 'Y'\n" + "AND\n"
//					+ "    so.docstatus = 'CO' " + "AND (\n"
//					+ "    so.documentno ILIKE '%' || COALESCE(?, so.documentno) || '%'\n"
//					+ "    OR bp.name ILIKE '%' || COALESCE(?, bp.name) || '%'\n"
//					+ "    OR wh.name ILIKE '%' || COALESCE(?, wh.name) || '%'\n"
//					+ "    OR so.description ILIKE '%' || COALESCE(?, so.description) || '%'\n"
//					+ ") AND so.ad_org_id IN (" + orgIds + ") \n" + " ORDER BY so.created Asc;";
//
//			pstm = DB.prepareStatement(query.toString(), null);
//
//			pstm.setString(1, null);
//
//			pstm.setString(2, null);
//			pstm.setString(3, null);
//			pstm.setString(4, null);
//			rs = pstm.executeQuery();
//			
//			for (MLocator mLocator : locatorList) {
//
//				LinkedHashMap<Integer, Integer> pickedLabels = PiProductLabel.getAvailableLabelsByLocator(clientId,
//						mLocator.getM_Locator_ID());
//
//				if (pickedLabels != null) {
//					for (Map.Entry<Integer, Integer> entry : pickedLabels.entrySet()) {
////						availableQnty.put(entry.getKey(), entry.getValue());
//
//						int value = entry.getValue();
//						if (availableQnty.containsKey(entry.getKey()))
//							value = entry.getValue() + availableQnty.get(entry.getKey());
//						availableQnty.put(entry.getKey(), value);
//
//					}
//				} else
//					availableQnty = pickedLabels;
//
//			}
//
//			while (rs.next()) {
//
//				MOrder order = new MOrder(ctx, Integer.valueOf(orderId), trxName);
//
//				if (order.getDocumentNo().equalsIgnoreCase(orderDocumentNo)) {
//					break;
//				}
//
//				MInOut[] mInout = order.getShipments();
//
//				LinkedHashMap<Integer, Integer> orderQntyForProducts = new LinkedHashMap<Integer, Integer>();
//
//				for (MOrderLine mOrderLine : order.getLines()) {
//
//					int qnty = 0;
//					if (!orderQntyForProducts.containsKey(mOrderLine.getM_Product_ID()))
//						qnty = mOrderLine.getQtyEntered().intValue();
//					else
//						qnty = orderQntyForProducts.get(mOrderLine.getM_Product_ID())
//								+ mOrderLine.getQtyEntered().intValue();
//					orderQntyForProducts.put(mOrderLine.getM_Product_ID(), qnty);
//				}
//
//				for (MInOut inout : mInout) {
//
//					for (MInOutLine line : inout.getLines()) {
//						if (!inout.getDocStatus().equalsIgnoreCase("CO")) {
//
//							if (availableQnty.containsKey(line.getM_Product_ID()))
//								availableQnty.put(line.getM_Product_ID(),
//										availableQnty.get(line.getM_Product_ID()) - line.getQtyEntered().intValue());
//						}
//						orderQntyForProducts.put(line.getM_Product_ID(),
//								orderQntyForProducts.get(line.getM_Product_ID()) - line.getQtyEntered().intValue());
//
//					}
//				}
//
//				for (Integer key : orderQntyForProducts.keySet()) {
//
//					int orderQnty = orderQntyForProducts.get(key);
//					int qntyAvailable = 0;
//					if (availableQnty.containsKey(key))
//						qntyAvailable = availableQnty.get(key);
//
//					if (orderQnty > qntyAvailable)
//						availableQnty.put(key, 0);
//					else if (orderQnty < qntyAvailable)
//						availableQnty.put(key, qntyAvailable - orderQnty);
//					else if (orderQnty == qntyAvailable)
//						availableQnty.put(key, 0);
//
//				}
//
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//		} finally {
//			closeDbCon(pstm, rs);
//		}
//		return availableQnty;
//	}


//	public SOListResponseDocument soList(SOListRequestDocument sOListRequestDocument) {
//		SOListResponseDocument sOListResponseDocument = SOListResponseDocument.Factory.newInstance();
//		SOListResponse sOListResponse = sOListResponseDocument.addNewSOListResponse();
//		SOListRequest sOListRequest = sOListRequestDocument.getSOListRequest();
//		ADLoginRequest loginReq = sOListRequest.getADLoginRequest();
//		String serviceType = sOListRequest.getServiceType();
//		String searchkey = sOListRequest.getSearchKey();
//		PreparedStatement pstm = null;
//		ResultSet rs = null;
//		Trx trx = null;
//		try {
//			int clientId = loginReq.getClientID();
//			getCompiereService().connect();
//			CompiereService m_cs = getCompiereService();
//			Properties ctx = m_cs.getCtx();
//			String trxName = Trx.createTrxName(getClass().getName() + "_");
//			trx = Trx.get(trxName, true);
//			trx.start();
//
//			org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = RwplUtils.convertAdLogin(loginReq);
//			String err = login(adLoginReq, webServiceName, "sOList", serviceType);
//			if (err != null && err.length() > 0) {
//				sOListResponse.setError(err);
//				sOListResponse.setIsError(true);
//				return sOListResponseDocument;
//			}
//
//			if (!serviceType.equalsIgnoreCase("sOList")) {
//				sOListResponse.setIsError(true);
//				sOListResponse.setError("Service type " + serviceType + " not configured");
//				return sOListResponseDocument;
//			}
//
//			List<Integer> orgList = new ArrayList<>();
//			Login login = new Login(ctx);
//			KeyNamePair[] orgs = login.getOrgs(new KeyNamePair(loginReq.getRoleID(), ""));
//			if (orgs != null) {
//				for (KeyNamePair org : orgs) {
//					orgList.add(Integer.valueOf(org.getID()));
//				}
//			}
//			
//			List<MLocator> locators = MLocatorType_Custom.getLocatorsByType(ctx, trxName, loginReq.getWarehouseID(), "dispatch", "Y");
//			if (locators == null || locators.size() == 0) {
//				sOListResponse.setIsError(true);
//				sOListResponse.setError("Dispatch Locator Not Found");
//				return sOListResponseDocument;
//			}
//
//			String orgIds = orgList.stream().map(Object::toString).collect(Collectors.joining(", "));
//
//			StringBuilder query = new StringBuilder("SELECT DISTINCT\n" + "    so.documentno as Sales_Order,\n"
//					+ "    so.c_order_id AS cOrderId, so.putStatus,\n"
//					+ "TO_CHAR(so.dateordered, 'DD/MM/YYYY') AS Order_Date,\n"
//					+ "    wh.name AS Warehouse_Name, wh.m_warehouse_id,\n" + "bp.name AS Customer, so.created,\n"
//					+ " so.description,\n" + "    CASE\n"
//					+ "        WHEN so.docstatus = 'CO' AND mr.m_inout_id IS NULL THEN false\n"
//					+ "        WHEN so.docstatus = 'CO' AND mr.m_inout_id IS NOT NULL THEN true \n"
//					+ "    END AS status\n" + "FROM\n" + "    adempiere.c_order so\n" + "JOIN\n"
//					+ "    adempiere.c_orderline sol ON so.c_order_id = sol.c_order_id\n" + "JOIN\n"
//					+ "    adempiere.c_bpartner bp ON so.c_bpartner_id = bp.c_bpartner_id \n" + "JOIN\n"
//					+ "    adempiere.m_warehouse wh ON so.m_warehouse_id = wh.m_warehouse_id\n" + "LEFT JOIN\n"
//					+ "    adempiere.m_inout mr ON so.c_order_id = mr.c_order_id\n" + "WHERE\n"
//					+ "    sol.qtyordered > (\n" + "        SELECT COALESCE(SUM(iol.qtyentered), 0)\n"
//					+ "        FROM adempiere.m_inoutline iol\n"
//					+ "        WHERE iol.c_orderline_id = sol.c_orderline_id\n" + "    )\n" + "AND\n"
//					+ "    so.ad_client_id = '" + clientId + "'\n" + "AND\n" + "    so.issotrx = 'Y'\n" + "AND\n"
//					+ "    so.docstatus = 'CO' " + "AND (\n"
//					+ "    so.documentno ILIKE '%' || COALESCE(?, so.documentno) || '%'\n"
//					+ "    OR bp.name ILIKE '%' || COALESCE(?, bp.name) || '%'\n"
//					+ "    OR wh.name ILIKE '%' || COALESCE(?, wh.name) || '%'\n"
//					+ "    OR so.description ILIKE '%' || COALESCE(?, so.description) || '%'\n"
//					+ ") AND so.ad_org_id IN (" + orgIds + ") \n" + " ORDER BY so.created desc");
//
//			pstm = DB.prepareStatement(query.toString(), null);
//			pstm.setString(1, searchkey);
//			pstm.setString(2, searchkey);
//			pstm.setString(3, searchkey);
//			pstm.setString(4, searchkey);
//			rs = pstm.executeQuery();
//
//			int count = 0;
//			while (rs.next()) {
//				String documentNo = rs.getString("Sales_Order");
//				String cOrderId = rs.getString("cOrderId");
//				String customer = rs.getString("Customer");
//				String date = rs.getString("Order_Date");
//				String warehouseName = rs.getString("Warehouse_Name");
//				String description = rs.getString("description");
//				boolean Status = rs.getBoolean("status");
//				String putStatus = rs.getString("putStatus");
//				int mWarehouseId = rs.getInt("m_warehouse_id");
//
//					SOListAccess sOListAccess = sOListResponse.addNewListAccess();
//					sOListAccess.setDocumentNumber(documentNo);
//					sOListAccess.setCOrderId(cOrderId);
//					sOListAccess.setOrderDate(date);
//					sOListAccess.setCustomerName(customer);
//					sOListAccess.setWarehouseName(warehouseName);
//					sOListAccess.setWarehouseId(mWarehouseId);
//
//					if (putStatus == null)
//						putStatus = "";
//					if (description == null)
//						description = "";
//					sOListAccess.setDescription(description);
//					sOListAccess.setStatus(putStatus);
//					sOListAccess.setSalesOrderStatus(Status);
//
//					count++;
//			}
//
//			LinkedHashMap<Integer, LinkedHashMap<Integer, Integer>> pickedQuantity = new LinkedHashMap<Integer, LinkedHashMap<Integer, Integer>>();
//
//			if (!pickedQuantity.containsKey(loginReq.getWarehouseID())) {
//				for (MLocator mLocator : locators) {
//
//					LinkedHashMap<Integer, Integer> pickedLabels = PiProductLabel.getAvailableLabelsByLocator(clientId,
//							mLocator.getM_Locator_ID());
//
//					LinkedHashMap<Integer, Integer> existing = null;
//					if (pickedQuantity.containsKey(loginReq.getWarehouseID())) {
//
//						existing = pickedQuantity.get(loginReq.getWarehouseID());
//						if (pickedLabels != null)
//							for (Map.Entry<Integer, Integer> entry : pickedLabels.entrySet()) {
//								int value = entry.getValue();
//								if (existing.containsKey(entry.getKey()))
//									value = entry.getValue() + existing.get(entry.getKey());
//								existing.put(entry.getKey(), value);
//							}
//					} else
//						existing = pickedLabels;
//
//					pickedQuantity.put(loginReq.getWarehouseID(), existing);
//				}
//			}
//
//			SOListAccess[] sOListAccessArray = sOListResponse.getListAccessArray();
//
//			for (int i = sOListAccessArray.length - 1; i >= 0; i--) {
//
//				SOListAccess listAcess = sOListAccessArray[i];
//				if (listAcess.getStatus().equalsIgnoreCase("pick")) {
//
//					JSONObject jsonObject = getRemainQuantityForOrder(ctx, trxName, listAcess.getCOrderId(),
//							listAcess.getDocumentNumber(), clientId, pickedQuantity, listAcess.getWarehouseId());
//
//					int pickedQnty = jsonObject.getInt("pickedQnty");
//					int totalQnty = jsonObject.getInt("totalQnty");
//					int remainQntyToPick = totalQnty - pickedQnty;
//					listAcess.setQuantityPicked(pickedQnty);
//					listAcess.setQuantityTotal(totalQnty);
//					listAcess.setRemainingQuantityToPick(remainQntyToPick);
//				}
//			}
//			sOListResponse.setCount(count);
//			trx.commit();
//		} catch (Exception e) {
//			sOListResponse.setIsError(true);
//			e.printStackTrace();
//			sOListResponse.setError(e.getMessage());
//			return sOListResponseDocument;
//		} finally {
//			trx.close();
//			closeDbCon(pstm, rs);
//			getCompiereService().disconnect();
//		}
//		return sOListResponseDocument;
//	}
//
//	public SODetailResponseDocument soDetails(SODetailRequestDocument sODetailRequestDocument) {
//		SODetailResponseDocument sODetailResponseDocument = SODetailResponseDocument.Factory.newInstance();
//		SODetailResponse sODetailResponse = sODetailResponseDocument.addNewSODetailResponse();
//		SODetailRequest sODetailRequest = sODetailRequestDocument.getSODetailRequest();
//		ADLoginRequest loginReq = sODetailRequest.getADLoginRequest();
//		String serviceType = sODetailRequest.getServiceType();
//		String documentNo = sODetailRequest.getDocumentNo();
//		int client_ID = loginReq.getClientID();
//		Trx trx = null;
//		PreparedStatement pstm = null;
//		ResultSet rs = null;
//		try {
//			CompiereService m_cs = getCompiereService();
//			Properties ctx = m_cs.getCtx();
//			String trxName = Trx.createTrxName(getClass().getName() + "_");
//			trx = Trx.get(trxName, true);
//			trx.start();
//
//			org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = RwplUtils.convertAdLogin(loginReq);
//			String err = login(adLoginReq, webServiceName, "sODetail", serviceType);
//			if (err != null && err.length() > 0) {
//				sODetailResponse.setError(err);
//				sODetailResponse.setIsError(true);
//				return sODetailResponseDocument;
//			}
//			if (!serviceType.equalsIgnoreCase("sODetail")) {
//				sODetailResponse.setIsError(true);
//				sODetailResponse.setError("Service type " + serviceType + " not configured");
//				return sODetailResponseDocument;
//			}
//			
//			List<MLocator> locators = MLocatorType_Custom.getLocatorsByType(ctx, trxName, loginReq.getWarehouseID(), "dispatch", "Y");
//			if (locators == null || locators.size() == 0) {
//				sODetailResponse.setIsError(true);
//				sODetailResponse.setError("Dispatch Locator Not Found");
//				return sODetailResponseDocument;
//			}
//			
//			MLocator dispatchLocator = locators.get(0);
//			sODetailResponse.setDispatchLocatorId(dispatchLocator.get_ID());
//			sODetailResponse.setDispatchLocatorName(dispatchLocator.getValue());
//			String query = null;
//			query = "SELECT a.qtyordered as totalQnty, (a.qtyordered - COALESCE(SUM(c.qtyentered), 0)) AS outstanding_qty, e.m_product_id as productId, a.c_order_id, a.c_uom_id, a.c_orderline_id, e.name AS product_name\n"
//					+ "FROM c_orderline a \n" + "JOIN c_order d ON d.c_order_id = a.c_order_id \n"
//					+ "LEFT JOIN m_inout b ON a.c_order_id = b.c_order_id \n"
//					+ "LEFT JOIN m_inoutline c ON c.m_inout_id = b.m_inout_id AND c.c_orderline_id = a.c_orderline_id\n"
//					+ "JOIN m_product e ON e.m_product_id = a.m_product_id \n" + "WHERE d.documentno = '" + documentNo
//					+ "' AND d.ad_client_id = '" + client_ID + "' AND a.c_order_id = (\n"
//					+ "  SELECT c_order_id FROM c_order WHERE documentno = '" + documentNo + "' AND ad_client_id = '"
//					+ client_ID + "'\n" + ")\n"
//					+ "GROUP BY e.m_product_id, e.name, a.qtyordered, a.c_orderline_id, a.c_uom_id, a.c_order_id ORDER BY a.c_orderline_id;\n"
//					+ "";
//
//			pstm = DB.prepareStatement(query.toString(), trxName);
//			rs = pstm.executeQuery();
//
//			int quantityPicked = 0;
//			int quantityTotal = 0;
//			int cOrderId = 0;
//			MOrder mOrder = null;
//
//			LinkedHashMap<Integer, Integer> availableQnty = null;
//			LinkedHashMap<Integer, Integer> orderQntyForProducts = new LinkedHashMap<Integer, Integer>();
//
//			while (rs.next()) {
//
//				String productName = rs.getString("product_name");
//				int productId = rs.getInt("productId");
//				cOrderId = rs.getInt("c_order_id");
//				int cOrderlineId = rs.getInt("c_orderline_id");
//				int uomId = rs.getInt("c_uom_id");
//
//				if (mOrder == null) {
//					mOrder = new MOrder(ctx, cOrderId, trxName);
//					availableQnty = getQntyPickedBySoList(client_ID, loginReq.getWarehouseID(), ctx, trxName,
//							mOrder.get_ID(), documentNo, loginReq.getRoleID(), locators);
//
//					MInOut[] mInout = mOrder.getShipments();
//
//					for (MOrderLine mOrderLine : mOrder.getLines()) {
//
//						int qnty = 0;
//						if (!orderQntyForProducts.containsKey(mOrderLine.getC_OrderLine_ID()))
//							qnty = mOrderLine.getQtyEntered().intValue();
//						else
//							qnty = orderQntyForProducts.get(mOrderLine.getC_OrderLine_ID())
//									+ mOrderLine.getQtyEntered().intValue();
//						orderQntyForProducts.put(mOrderLine.getC_OrderLine_ID(), qnty);
//					}
//
//					for (MInOut inout : mInout) {
//
//						for (MInOutLine line : inout.getLines()) {
//							if (!inout.getDocStatus().equalsIgnoreCase("CO")) {
//
//								if (availableQnty.containsKey(line.getM_Product_ID()))
//									availableQnty.put(line.getM_Product_ID(), availableQnty.get(line.getM_Product_ID())
//											- line.getQtyEntered().intValue());
//							}
//							orderQntyForProducts.put(line.getC_OrderLine_ID(),
//									orderQntyForProducts.get(line.getC_OrderLine_ID())
//											- line.getQtyEntered().intValue());
//
//						}
//					}
//				}
//
//				int qntyTotal = orderQntyForProducts.get(cOrderlineId);
//				int qntyOutstanding = 0; // qntyPicked
//
//				int qntyAvailable = 0;
//				if (availableQnty.containsKey(productId))
//					qntyAvailable = availableQnty.get(productId);
//
//				if (qntyTotal > qntyAvailable) {
//					qntyOutstanding = qntyAvailable;
//					availableQnty.put(productId, 0);
//				} else if (qntyTotal < qntyAvailable) {
//					qntyOutstanding = qntyTotal;
//					availableQnty.put(productId, qntyAvailable - qntyTotal);
//				} else if (qntyTotal == qntyAvailable) {
//					qntyOutstanding = qntyTotal;
//					availableQnty.put(productId, 0);
//				}
//
//				if (qntyTotal != 0) {
//					int remainingQntyToPick = qntyTotal - qntyOutstanding;
//					SoDetailProductData productData = sODetailResponse.addNewProductData();
//					productData.setProductId(productId);
//					productData.setProductName(productName);
//					productData.setCOrderlineId(cOrderlineId);
//					productData.setUomId(uomId);
//					productData.setOutstandingQnty(qntyOutstanding);
//					productData.setTotalQuantity(qntyTotal);
//					productData.setRemainingQntyToPick(remainingQntyToPick);
//					
//					if(remainingQntyToPick > 0) {
//						getSuggestedLocatorForProduct(productData, ctx, trxName, loginReq.getWarehouseID());
//						QntyAvailableInLocator[] pickLabourLinesArray = productData.getQntyAvailableInLocatorArray();
//						if(pickLabourLinesArray.length == 0 || pickLabourLinesArray == null) {
//							productData.addNewQntyAvailableInLocator();
//						}
//					}
//				}
//
//				quantityPicked += qntyOutstanding;
//				quantityTotal += qntyTotal;
//			}
//			sODetailResponse.setCOrderId(cOrderId);
//			sODetailResponse.setQuantityPicked(quantityPicked);
//			sODetailResponse.setQuantityTotal(quantityTotal);
//
//			LocalDateTime createdDateTime = mOrder.getCreated().toLocalDateTime();
//			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
//			String formattedDate = createdDateTime.format(formatter);
//
//			sODetailResponse.setOrderDate(formattedDate);
//			sODetailResponse.setDocStatus(mOrder.getDocStatus());
//			sODetailResponse.setCustomer(mOrder.getC_BPartner().getName());
//			sODetailResponse.setWarehouseName(mOrder.getM_Warehouse().getName());
//			sODetailResponse.setDescription(mOrder.getDescription() != null ? mOrder.getDescription() : "");
//			sODetailResponse.setOrderStatus(false);
//			sODetailResponse.setLocationName(mOrder.getC_BPartner_Location().getName());
//			sODetailResponse.setDocumentNo(documentNo);
//			trx.commit();
//		} catch (Exception e) {
//			e.printStackTrace();
//			sODetailResponse.setError(e.getMessage());
//			sODetailResponse.setIsError(true);
//			return sODetailResponseDocument;
//
//		} finally {
//			closeDbCon(pstm, rs);
//			trx.close();
//			getCompiereService().disconnect();
//		}
//		return sODetailResponseDocument;
//	}
//
//	private void getSuggestedLocatorForProduct(SoDetailProductData productData, Properties ctx, String trxName,
//			int warehouseId) {
//
//		try {
//			int productId = productData.getProductId();
//			int quantity = productData.getRemainingQntyToPick();
//
//			MStorageOnHand[] mStorageOnHandArray = MStorageOnHand.getWarehouse(ctx, warehouseId, productId, 0, null,
//					true, true, 0, trxName, false, 0);
//			boolean pickFlag = false;
//			for (MStorageOnHand mStorageOnHand : mStorageOnHandArray) {
//				QntyAvailableInLocator[] pickLabourLinesArray = productData.getQntyAvailableInLocatorArray();
//				boolean flag = false;
//
//				for (QntyAvailableInLocator line : pickLabourLinesArray) {
//					if (line.getLocatorId() == mStorageOnHand.getM_Locator_ID()) {
//						int availableQty = Math.min(mStorageOnHand.getQtyOnHand().intValue(), quantity);
//						line.setQuantityAvailable(line.getQuantityAvailable() + availableQty);
//						quantity -= availableQty;
//						flag = true;
//						if (quantity <= 0) {
//							pickFlag = true;
//							break;
//						}
//						break;
//					}
//				}
//				MLocatorType_Custom type = new MLocatorType_Custom(ctx,
//						mStorageOnHand.getM_Locator().getM_LocatorType_ID(), trxName);
//				if (!flag && !type.isdispatch() && !type.isPacking() && !type.isReturns()) {
//					QntyAvailableInLocator line = productData.addNewQntyAvailableInLocator();
//					int availableQty = Math.min(mStorageOnHand.getQtyOnHand().intValue(), quantity);
//					line.setQuantityAvailable(availableQty);
//					quantity -= availableQty;
//					line.setLocatorId(mStorageOnHand.getM_Locator_ID());
//					line.setLocatorName(mStorageOnHand.getM_Locator().getValue());
//					if (quantity <= 0) {
//						pickFlag = true;
//						break;
//					}
//
//				}
//
//				if (pickFlag) {
//					break;
//				}
//			}
//
//		} catch (Exception e) {
//		}
//
//	}
//
//	private LinkedHashMap<Integer, Integer> getQntyPickedBySoList(int clientId, int wrehouseId, Properties ctx,
//			String trxName, int orderId, String orderDocumentNo, int roleId, List<MLocator> locatorList) {
//		PreparedStatement pstm = null;
//		ResultSet rs = null;
//		LinkedHashMap<Integer, Integer> availableQnty = new LinkedHashMap<Integer, Integer>();
//		try {
//
//			List<Integer> orgList = new ArrayList<>();
//			Login login = new Login(ctx);
//			KeyNamePair[] orgs = login.getOrgs(new KeyNamePair(roleId, ""));
//			if (orgs != null) {
//				for (KeyNamePair org : orgs) {
//					orgList.add(Integer.valueOf(org.getID()));
//				}
//			}
//			String orgIds = orgList.stream().map(Object::toString).collect(Collectors.joining(", "));
//
//			
//			String query = "SELECT DISTINCT\n" + "    so.documentno as Sales_Order,\n"
//					+ "    so.c_order_id AS cOrderId, so.putStatus,\n"
//					+ "TO_CHAR(so.dateordered, 'DD/MM/YYYY') AS Order_Date,\n"
//					+ "    wh.name AS Warehouse_Name, wh.m_warehouse_id,\n" + "bp.name AS Customer, so.created,\n"
//					+ " so.description,\n" + "    CASE\n"
//					+ "        WHEN so.docstatus = 'CO' AND mr.m_inout_id IS NULL THEN false\n"
//					+ "        WHEN so.docstatus = 'CO' AND mr.m_inout_id IS NOT NULL THEN true \n"
//					+ "    END AS status\n" + "FROM\n" + "    adempiere.c_order so\n" + "JOIN\n"
//					+ "    adempiere.c_orderline sol ON so.c_order_id = sol.c_order_id\n" + "JOIN\n"
//					+ "    adempiere.c_bpartner bp ON so.c_bpartner_id = bp.c_bpartner_id \n" + "JOIN\n"
//					+ "    adempiere.m_warehouse wh ON so.m_warehouse_id = wh.m_warehouse_id\n" + "LEFT JOIN\n"
//					+ "    adempiere.m_inout mr ON so.c_order_id = mr.c_order_id\n" + "WHERE\n"
//					+ "    sol.qtyordered > (\n" + "        SELECT COALESCE(SUM(iol.qtyentered), 0)\n"
//					+ "        FROM adempiere.m_inoutline iol\n"
//					+ "        WHERE iol.c_orderline_id = sol.c_orderline_id\n" + "    )\n" + "AND\n"
//					+ "    so.ad_client_id = '" + clientId + "'\n" + "AND\n" + "    so.issotrx = 'Y'\n" + "AND\n"
//					+ "    so.docstatus = 'CO' " + "AND (\n"
//					+ "    so.documentno ILIKE '%' || COALESCE(?, so.documentno) || '%'\n"
//					+ "    OR bp.name ILIKE '%' || COALESCE(?, bp.name) || '%'\n"
//					+ "    OR wh.name ILIKE '%' || COALESCE(?, wh.name) || '%'\n"
//					+ "    OR so.description ILIKE '%' || COALESCE(?, so.description) || '%'\n"
//					+ ") AND so.ad_org_id IN (" + orgIds + ") \n" + " ORDER BY so.created Asc;";
//
//			pstm = DB.prepareStatement(query.toString(), null);
//
//			pstm.setString(1, null);
//
//			pstm.setString(2, null);
//			pstm.setString(3, null);
//			pstm.setString(4, null);
//			rs = pstm.executeQuery();
//			
//			for (MLocator mLocator : locatorList) {
//
//				LinkedHashMap<Integer, Integer> pickedLabels = PiProductLabel.getAvailableLabelsByLocator(clientId,
//						mLocator.getM_Locator_ID());
//
//				if (pickedLabels != null) {
//					for (Map.Entry<Integer, Integer> entry : pickedLabels.entrySet()) {
////						availableQnty.put(entry.getKey(), entry.getValue());
//
//						int value = entry.getValue();
//						if (availableQnty.containsKey(entry.getKey()))
//							value = entry.getValue() + availableQnty.get(entry.getKey());
//						availableQnty.put(entry.getKey(), value);
//
//					}
//				} else
//					availableQnty = pickedLabels;
//
//			}
//
//			while (rs.next()) {
//
//				MOrder order = new MOrder(ctx, Integer.valueOf(orderId), trxName);
//
//				if (order.getDocumentNo().equalsIgnoreCase(orderDocumentNo)) {
//					break;
//				}
//
//				MInOut[] mInout = order.getShipments();
//
//				LinkedHashMap<Integer, Integer> orderQntyForProducts = new LinkedHashMap<Integer, Integer>();
//
//				for (MOrderLine mOrderLine : order.getLines()) {
//
//					int qnty = 0;
//					if (!orderQntyForProducts.containsKey(mOrderLine.getM_Product_ID()))
//						qnty = mOrderLine.getQtyEntered().intValue();
//					else
//						qnty = orderQntyForProducts.get(mOrderLine.getM_Product_ID())
//								+ mOrderLine.getQtyEntered().intValue();
//					orderQntyForProducts.put(mOrderLine.getM_Product_ID(), qnty);
//				}
//
//				for (MInOut inout : mInout) {
//
//					for (MInOutLine line : inout.getLines()) {
//						if (!inout.getDocStatus().equalsIgnoreCase("CO")) {
//
//							if (availableQnty.containsKey(line.getM_Product_ID()))
//								availableQnty.put(line.getM_Product_ID(),
//										availableQnty.get(line.getM_Product_ID()) - line.getQtyEntered().intValue());
//						}
//						orderQntyForProducts.put(line.getM_Product_ID(),
//								orderQntyForProducts.get(line.getM_Product_ID()) - line.getQtyEntered().intValue());
//
//					}
//				}
//
//				for (Integer key : orderQntyForProducts.keySet()) {
//
//					int orderQnty = orderQntyForProducts.get(key);
//					int qntyAvailable = 0;
//					if (availableQnty.containsKey(key))
//						qntyAvailable = availableQnty.get(key);
//
//					if (orderQnty > qntyAvailable)
//						availableQnty.put(key, 0);
//					else if (orderQnty < qntyAvailable)
//						availableQnty.put(key, qntyAvailable - orderQnty);
//					else if (orderQnty == qntyAvailable)
//						availableQnty.put(key, 0);
//
//				}
//
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//		} finally {
//			closeDbCon(pstm, rs);
//		}
//		return availableQnty;
//	}
//
	
	//if there is existing shipment availble wirh doc status not co, it will uses it
	public CreateSCResponseDocument createShipment(CreateSCRequestDocument createSCRequestDocument) {
		CreateSCResponseDocument createSCResponseDocument = CreateSCResponseDocument.Factory.newInstance();
		CreateSCResponse createSCResponse = createSCResponseDocument.addNewCreateSCResponse();
		CreateSCRequest createSCRequest = createSCRequestDocument.getCreateSCRequest();
		ADLoginRequest loginReq = createSCRequest.getADLoginRequest();
		String serviceType = createSCRequest.getServiceType();
		int cOrderId = createSCRequest.getCOrderId();
		MInvoice m_invoice = null;
		MRMA s_rma = null;
		Trx trx = null;
		int clientID = loginReq.getClientID();
		CompiereService m_cs = getCompiereService();
		Properties ctx = m_cs.getCtx();
		int mInoutId = 0;
		String status = createSCRequest.getStatus();
		try {
			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			
			org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = RwplUtils.convertAdLogin(loginReq);
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
			
			boolean existingInout = false;
			MInOut dispatch = null;
			MInOut[] mInOuts = order.getShipments();
			if(mInOuts != null && mInOuts.length !=0) {
				for(MInOut in : mInOuts) {
					if(in.getDocStatus().equalsIgnoreCase(DocAction.STATUS_Drafted) || in.getDocStatus().equalsIgnoreCase(DocAction.STATUS_InProgress)) {
						existingInout = true;
					    dispatch = in;
					}
				}
			}
			
			if (dispatch == null) {
				MTable docTypeTable = MTable.get(ctx, "c_doctype");
				PO docTypePO = docTypeTable.getPO("name = 'MM Shipment' and ad_client_id = " + clientID + "",
						trx.getTrxName());
				MDocType mDocType = (MDocType) docTypePO;

				dispatch = new MInOut(order, mDocType.get_ID(), order.getDateOrdered());
				dispatch.setDocStatus(DocAction.STATUS_Drafted);
				dispatch.saveEx();

				mInoutId = dispatch.getM_InOut_ID();
				
			}
			
			MInOut_Custom inout = new MInOut_Custom(ctx, dispatch.get_ID(), trx.getTrxName());
			if (status != null && status != "") {
				inout.setPickStatus(status);
				inout.saveEx();
			}
			
			ShipmentLines[] shipmentLinesArray = createSCRequest.getShipmentLinesArray();

			if (existingInout) {
				MInOutLine[] inouttLines = dispatch.getLines();
				for (ShipmentLines lines : shipmentLinesArray) {
					int C_InvoiceLine_ID = 0;
					int M_RMALine_ID = 0;
					int M_Product_ID = lines.getProductId();
					int C_UOM_ID = lines.getUomId();
					int C_OrderLine_ID = lines.getCOrderlineId();
					BigDecimal QtyEntered = BigDecimal.valueOf(lines.getQnty());
					int M_Locator_ID = lines.getLocator();

					int mrLineId = 0;

					boolean create = true;
					if (inouttLines != null && inouttLines.length != 0) {
						for (MInOutLine inoutLine : inouttLines) {
							if (lines.getCOrderlineId() == inoutLine.getC_OrderLine_ID()) {
								I_C_OrderLine C_OrderLine = inoutLine.getC_OrderLine();
								BigDecimal existingQnty = inoutLine.getQtyEntered();
								BigDecimal totalQnty = existingQnty.add(QtyEntered);
								inoutLine.setQty(totalQnty);

								inoutLine.setMovementQty(totalQnty.multiply(C_OrderLine.getQtyOrdered())
										.divide(C_OrderLine.getQtyEntered(), 12, RoundingMode.HALF_UP));
								inoutLine.saveEx();
								mrLineId = inoutLine.get_ID();
								create = false;
								break;
							}
						}
					}

					if (create) {
						inout.createLineFrom(C_OrderLine_ID, C_InvoiceLine_ID, M_RMALine_ID, M_Product_ID, C_UOM_ID,
								QtyEntered, M_Locator_ID);
						MInOutLine[] mInoutLines = inout.getLines();
						mrLineId = mInoutLines[mInoutLines.length - 1].get_ID();
					}

					lines.setMRLineId(mrLineId);

				}
			}
			else {
				for (ShipmentLines lines : shipmentLinesArray) {
					int C_InvoiceLine_ID = 0;
					int M_RMALine_ID = 0;
					int M_Product_ID = lines.getProductId();
					int C_UOM_ID = lines.getUomId();
					int C_OrderLine_ID = lines.getCOrderlineId();
					BigDecimal QtyEntered = BigDecimal.valueOf(lines.getQnty());
					int M_Locator_ID = lines.getLocator();

					inout.createLineFrom(C_OrderLine_ID, C_InvoiceLine_ID, M_RMALine_ID, M_Product_ID, C_UOM_ID,
							QtyEntered, M_Locator_ID);
					MInOutLine[] mInoutLines = inout.getLines();
					int lineId = mInoutLines[mInoutLines.length - 1].get_ID();
					lines.setMRLineId(lineId);

				}
				inout.updateFrom(order, m_invoice, s_rma);
			}

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

			RwplUtils.sendNotificationAsync(true,false, inout.get_Table_ID(), inout.getM_InOut_ID(), ctx, trxName,
					"Products ready for dispatch - " + inout.getDocumentNo() + "",
					"New products marked ready for dispatch with Shipment No: " + inout.getDocumentNo()
							+ " for Order - " + inout.getC_Order().getDocumentNo() + "",
					inout.get_TableName(), data, loginReq.getClientID(), "CustomerShipmentCreted");

		} catch (Exception e) {
			MTable table = MTable.get(ctx, "m_inout");
			PO po = table.getPO(mInoutId, trx.getTrxName());
			po.delete(true);
			createSCResponse.setError(e.getMessage());
			createSCResponse.setIsError(true);
			return createSCResponseDocument;
		} finally {
			getCompiereService().disconnect();
			trx.close();
		}
		return createSCResponseDocument;
	}

	//It creates the new shipment and closes it, if label is splited creates new one
		@Override
		public StandardResponseDocument createShipmentByLabel(CreateSCByLabelRequestDocument requestDocument) {
			StandardResponseDocument responseDocument = StandardResponseDocument.Factory.newInstance();
			StandardResponse response = responseDocument.addNewStandardResponse();
			CreateSCByLabelRequest createSCRequest = requestDocument.getCreateSCByLabelRequest();
			ADLoginRequest loginReq = createSCRequest.getADLoginRequest();
			String serviceType = createSCRequest.getServiceType();
			int mInoutID = createSCRequest.getMInoutID();
			MInvoice m_invoice = null;
			MRMA s_rma = null;
			Trx trx = null;
			int clientID = loginReq.getClientID();
			int orgID = loginReq.getOrgID();

			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			int mInoutId = 0;
			try {
				String trxName = Trx.createTrxName(getClass().getName() + "_");
				trx = Trx.get(trxName, true);
				trx.start();

				org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = RwplUtils.convertAdLogin(loginReq);
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

				LinkedHashMap<Integer, Integer> productAndQnty = new LinkedHashMap<Integer, Integer>();

				for (SCLabelLine line : scLabelLines) {

					int labelQnty = line.getUsedQuantity();
					int productId = line.getProductId();

					if (productAndQnty.containsKey(productId))
						labelQnty += productAndQnty.get(productId);

					productAndQnty.put(productId, labelQnty);
				}

				MTable table = MTable.get(ctx, "m_inout");
				PO tablePo = table.getPO(mInoutID, trx.getTrxName());
				if (tablePo == null) {
					response.setError("shipment not found for " + mInoutID + "");
					response.setIsError(true);
					return responseDocument;
				}

				MInOut_Custom oldInout = new MInOut_Custom(ctx, tablePo.get_ID(), trxName);

				MOrder order = new MOrder(ctx, oldInout.getC_Order_ID(), trxName);

				MTable docTypeTable = MTable.get(ctx, "c_doctype");
				PO docTypePO = docTypeTable.getPO("name = 'MM Shipment' and ad_client_id = " + clientID + "",
						trx.getTrxName());
				MDocType mDocType = (MDocType) docTypePO;

				MInOut dispatch = new MInOut(order, mDocType.get_ID(), order.getDateOrdered());
				dispatch.setDocStatus(DocAction.STATUS_Drafted);
				dispatch.saveEx();

				mInoutId = dispatch.getM_InOut_ID();
				MInOut_Custom inout = new MInOut_Custom(ctx, mInoutId, trx.getTrxName());

				inout.setPickStatus(oldInout.getPickStatus());
				inout.saveEx();

				MInOutLine[] existingLines = oldInout.getLines();

				int M_Locator_ID = RwplUtils.getLocatorIdByType(ctx, trxName, clientID, "dispatch");

				for (Integer key : productAndQnty.keySet()) {

					int qnty = productAndQnty.get(key);

					MProduct product = new MProduct(ctx, key, trxName);
					int M_Product_ID = product.getM_Product_ID();
					int C_OrderLine_ID = 0;

					if (existingLines != null && existingLines.length != 0) {
						for (MInOutLine line : existingLines) {
							if (line.getM_Product_ID() == M_Product_ID) {

								int lineQnty = line.getQtyEntered().intValue();

								if (C_OrderLine_ID == 0)
									C_OrderLine_ID = line.getC_OrderLine_ID();

								if (qnty == 0)
									break;

								if (qnty == lineQnty) {
									line.deleteEx(false);
									qnty = 0;
									break;
								}

								if (qnty > lineQnty) {
									qnty = qnty - lineQnty;
									line.deleteEx(false);
									break;
								}

								if (qnty < lineQnty) {
									line.setQtyEntered(BigDecimal.valueOf(lineQnty - qnty));
									line.setMovementQty(BigDecimal.valueOf(lineQnty - qnty));
									line.save();
									qnty = 0;

									break;
								}

							}
						}
					}

					int C_InvoiceLine_ID = 0;
					int M_RMALine_ID = 0;
					int C_UOM_ID = product.getC_UOM_ID();
					
					BigDecimal QtyEntered = BigDecimal.valueOf(productAndQnty.get(key));

					inout.createLineFrom(C_OrderLine_ID, C_InvoiceLine_ID, M_RMALine_ID, M_Product_ID, C_UOM_ID,
							QtyEntered, M_Locator_ID);

				}
				inout.updateFrom(order, m_invoice, s_rma);
				
				inout.setDocStatus(DocAction.ACTION_Complete);
				inout.setDocAction(DocAction.ACTION_Close);
				inout.setPosted(true);
				inout.setProcessed(true);
				inout.setIsApproved(true);
				inout.completeIt();
				inout.saveEx();
				

				LinkedHashMap<Integer, Integer> productOrderLineIds = new LinkedHashMap<Integer, Integer>();
				LinkedHashMap<Integer, Integer> productInoutLineIds = new LinkedHashMap<Integer, Integer>();
				MInOutLine[] newLines = inout.getLines();
				if (newLines != null && newLines.length != 0) {
					for (MInOutLine line : newLines) {
						productOrderLineIds.put(line.getM_Product_ID(), line.getC_OrderLine_ID());
						productInoutLineIds.put(line.getM_Product_ID(), line.getM_InOutLine_ID());
					}
				}

				for (SCLabelLine line : scLabelLines) {

					int qnty = line.getUsedQuantity();
					String labelUUID = line.getLabelUUID();

					PO po = PiProductLabel.getPiProductLabelById("labelUUId", line.getLabelUUID(), ctx, trxName, false);
					PiProductLabel label = new PiProductLabel(ctx, po.get_ID(), trxName);

					int productId = label.getM_Product_ID();
					int cOrderLineID = productOrderLineIds.get(productId);
					int mInoutLineID = productInoutLineIds.get(productId);
					int labelQnty = label.getquantity().intValue();

					if (qnty != labelQnty) {

						int newQnty = labelQnty - qnty;
						PiProductLabel newLabel = new PiProductLabel(ctx, trxName, clientID, orgID, productId,
								M_Locator_ID, 0, label.getM_InOutLine_ID(), false, BigDecimal.valueOf(qnty), null);

						newLabel.saveEx();
						labelUUID = newLabel.getlabeluuid();

						label.setquantity(BigDecimal.valueOf(newQnty));
						label.saveEx();
					}

					PiProductLabel salesLabel = new PiProductLabel(ctx, trxName, clientID, orgID, productId,
							M_Locator_ID, cOrderLineID, mInoutLineID, true, BigDecimal.valueOf(qnty), labelUUID);
					salesLabel.saveEx();

				}

				trx.commit();
				response.setIsError(false);

				Map<String, String> data = new HashMap<>();
				data.put("recordId", String.valueOf(inout.getM_InOut_ID()));
				data.put("documentNo", String.valueOf(inout.getDocumentNo()));
				data.put("path1", "/dispatch_screen");
				data.put("path2", "/dispatch_detail_screen");

				RwplUtils.sendNotificationAsync(true, false, inout.get_Table_ID(), inout.getM_InOut_ID(), ctx, trxName,
						"Products ready for dispatch - " + inout.getDocumentNo() + "",
						"New products marked ready for dispatch with Shipment No: " + inout.getDocumentNo()
								+ " for Order - " + inout.getC_Order().getDocumentNo() + "",
						inout.get_TableName(), data, loginReq.getClientID(), "CustomerShipmentCreted");

			} catch (Exception e) {
				MTable table = MTable.get(ctx, "m_inout");
				PO po = table.getPO(mInoutId, trx.getTrxName());
				po.delete(true);
				response.setError(e.getMessage());
				response.setIsError(true);
				return responseDocument;
			} finally {
				getCompiereService().disconnect();
				trx.close();
			}
			return responseDocument;
		}
		
		//It creates the new shipment and closes it, if label is splited creates new one
		@Override
		public StandardResponseDocument editShipment(CreateSCByLabelRequestDocument requestDocument) {
			StandardResponseDocument responseDocument = StandardResponseDocument.Factory.newInstance();
			StandardResponse response = responseDocument.addNewStandardResponse();
			CreateSCByLabelRequest createSCRequest = requestDocument.getCreateSCByLabelRequest();
			ADLoginRequest loginReq = createSCRequest.getADLoginRequest();
			String serviceType = createSCRequest.getServiceType();
			int mInoutID = createSCRequest.getMInoutID();
			Trx trx = null;
			int clientID = loginReq.getClientID();

			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			try {
				String trxName = Trx.createTrxName(getClass().getName() + "_");
				trx = Trx.get(trxName, true);
				trx.start();

				org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = RwplUtils.convertAdLogin(loginReq);
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

				int M_Locator_ID = RwplUtils.getLocatorIdByType(ctx, trxName, clientID, "receiving");

				for (SCLabelLine line : scLabelLines) {

					int qnty = line.getUsedQuantity();
					String labelUUID = line.getLabelUUID();

					PO po = PiProductLabel.getPiProductLabelById("labelUUId", labelUUID, ctx, trxName, false);
					PiProductLabel label = new PiProductLabel(ctx, po.get_ID(), trxName);

					MProduct product = new MProduct(ctx, line.getProductId(), trxName);
					int M_Product_ID = product.getM_Product_ID();

					if (existingLines != null && existingLines.length != 0) {
						for (MInOutLine inoutLine : existingLines) {

							if (inoutLine.getM_Product_ID() == M_Product_ID) {

								int lineQnty = inoutLine.getQtyEntered().intValue();

								if (qnty == 0)
									break;

								if (qnty == lineQnty) {
									inoutLine.deleteEx(true);
									qnty = 0;
									break;
								}

								if (qnty > lineQnty) {
									qnty = qnty - lineQnty;
									inoutLine.deleteEx(true);
									break;
								}

								if (qnty < lineQnty) {
									inoutLine.setQtyEntered(BigDecimal.valueOf(lineQnty - qnty));
									inoutLine.setMovementQty(BigDecimal.valueOf(lineQnty - qnty));
									inoutLine.save();
									qnty = 0;

									break;
								}

							}
						}
					}

					label.setfinaldispatch(false);
					label.setM_Locator_ID(M_Locator_ID);
					label.saveEx();

				}

				inout = new MInOut(ctx, mInoutID, trxName);
				existingLines = inout.getLines();
				if (existingLines == null || existingLines.length == 0)
					inout.delete(true);

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

	// code for which creates always new shipment 
//	@Override
//	public CreateSCResponseDocument createShipment(CreateSCRequestDocument createSCRequestDocument) {
//		CreateSCResponseDocument createSCResponseDocument = CreateSCResponseDocument.Factory.newInstance();
//		CreateSCResponse createSCResponse = createSCResponseDocument.addNewCreateSCResponse();
//		CreateSCRequest createSCRequest = createSCRequestDocument.getCreateSCRequest();
//		ADLoginRequest loginReq = createSCRequest.getADLoginRequest();
//		String serviceType = createSCRequest.getServiceType();
//		int cOrderId = createSCRequest.getCOrderId();
//		MInvoice m_invoice = null;
//		MRMA s_rma = null;
//		Trx trx = null;
//		int clientID = loginReq.getClientID();
//		CompiereService m_cs = getCompiereService();
//		Properties ctx = m_cs.getCtx();
//		int mInoutId = 0;
//		String status = createSCRequest.getStatus();
//		try {
//			String trxName = Trx.createTrxName(getClass().getName() + "_");
//			trx = Trx.get(trxName, true);
//			trx.start();
//			
//			org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = RwplUtils.convertAdLogin(loginReq);
//			String err = login(adLoginReq, webServiceName, "createSC", serviceType);
//			if (err != null && err.length() > 0) {
//				createSCResponse.setError(err);
//				createSCResponse.setIsError(true);
//				return createSCResponseDocument;
//			}
//			if (!serviceType.equalsIgnoreCase("createSC")) {
//				createSCResponse.setIsError(true);
//				createSCResponse.setError("Service type " + serviceType + " not configured");
//				return createSCResponseDocument;
//			}
//
//			MTable table = MTable.get(ctx, "c_order");
//			PO po = table.getPO(cOrderId, trx.getTrxName());
//			if (po == null) {
//				createSCResponse.setError("order not found for " + cOrderId + "");
//				createSCResponse.setIsError(true);
//				return createSCResponseDocument;
//			}
//			MOrder order = (MOrder) po;
//			MTable docTypeTable = MTable.get(ctx, "c_doctype");
//			PO docTypePO = docTypeTable.getPO("name = 'MM Shipment' and ad_client_id = " + clientID + "",
//					trx.getTrxName());
//			MDocType mDocType = (MDocType) docTypePO;
//
//			MInOut dispatch = new MInOut(order, mDocType.get_ID(), order.getDateOrdered());
//			dispatch.setDocStatus(DocAction.STATUS_Drafted);
//			dispatch.saveEx();
//
//			mInoutId = dispatch.getM_InOut_ID();
//			MInOut_Custom inout = new MInOut_Custom(ctx, mInoutId, trx.getTrxName());
//
//			if (status != null && status != "") {
//				inout.setPickStatus(status);
//				inout.saveEx();
//			}
//
//			ShipmentLines[] shipmentLinesArray = createSCRequest.getShipmentLinesArray();
//			for (ShipmentLines lines : shipmentLinesArray) {
//				int C_InvoiceLine_ID = 0;
//				int M_RMALine_ID = 0;
//				int M_Product_ID = lines.getProductId();
//				int C_UOM_ID = lines.getUomId();
//				int C_OrderLine_ID = lines.getCOrderlineId();
//				BigDecimal QtyEntered = BigDecimal.valueOf(lines.getQnty());
//				int M_Locator_ID = lines.getLocator();
//				
//				inout.createLineFrom(C_OrderLine_ID, C_InvoiceLine_ID, M_RMALine_ID, M_Product_ID, C_UOM_ID, QtyEntered,
//						M_Locator_ID);
//				MInOutLine[] mInoutLines = inout.getLines();
//				int lineId = mInoutLines[mInoutLines.length - 1].get_ID();
//				lines.setMRLineId(lineId);
//
//			}
//			inout.updateFrom(order, m_invoice, s_rma);
//
//			trx.commit();
//			createSCResponse.setIsError(false);
//			createSCResponse.setShipmentDocumentNumber(inout.getDocumentNo());
//			createSCResponse.setShipmentId(mInoutId);
//			createSCResponse.setShipmentLinesArray(shipmentLinesArray);
//
//			Map<String, String> data = new HashMap<>();
//			data.put("recordId", String.valueOf(inout.getM_InOut_ID()));
//			data.put("documentNo", String.valueOf(inout.getDocumentNo()));
//			data.put("path1", "/dispatch_screen");
//			data.put("path2", "/dispatch_detail_screen");
//
//			RwplUtils.sendNotificationAsync(true,false, inout.get_Table_ID(), inout.getM_InOut_ID(), ctx, trxName,
//					"Products ready for dispatch - " + inout.getDocumentNo() + "",
//					"New products marked ready for dispatch with Shipment No: " + inout.getDocumentNo()
//							+ " for Order - " + inout.getC_Order().getDocumentNo() + "",
//					inout.get_TableName(), data, loginReq.getClientID(), "CustomerShipmentCreted");
//
//		} catch (Exception e) {
//			MTable table = MTable.get(ctx, "m_inout");
//			PO po = table.getPO(mInoutId, trx.getTrxName());
//			po.delete(true);
//			createSCResponse.setError(e.getMessage());
//			createSCResponse.setIsError(true);
//			return createSCResponseDocument;
//		} finally {
//			getCompiereService().disconnect();
//			trx.close();
//		}
//		return createSCResponseDocument;
//	}

	private void moveInventory(Properties ctx, String trxName, MMovement mMovement, ADLoginRequest loginReq,
			int cCOrderLineID, BigDecimal quantity, int productId, int fromLocatorId, int toLocatorId) {
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

		MAcctSchema[] MAcctSchemaArray = MAcctSchema.getClientAcctSchema(ctx, loginReq.getClientID());
		MAcctSchema AcctSchema = MAcctSchemaArray[0];

		List<PO> list = new Query(ctx, MElementValue.Table_Name, "name=? AND ad_client_id=?", trxName)
				.setParameters("Product asset", loginReq.getClientID())
				.setOrderBy(MElementValue.COLUMNNAME_C_ElementValue_ID).list();
		MElementValue mElementValue = (MElementValue) list.get(0);
		int mElementValueId = mElementValue.get_ID();

		MPeriod mPeriod = MPeriod.get(ctx, mMovement.getMovementDate(), loginReq.getOrgID(), trxName);

		MGLCategory mGLCategory = MGLCategory.getDefault(ctx, "M");
		MOrderLine mOrderLine = new MOrderLine(ctx, cCOrderLineID, trxName);

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
		mFactAcct.setAmtSourceDr(mOrderLine.getPriceActual().multiply(quantity));
		mFactAcct.setAmtSourceCr(BigDecimal.valueOf(0));
		mFactAcct.setAmtAcctCr(BigDecimal.valueOf(0));
		mFactAcct.setAmtAcctDr(mOrderLine.getPriceActual().multiply(quantity));
		mFactAcct.setC_UOM_ID(mOrderLine.getC_UOM_ID());
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
		mFactAcctIn.setAmtSourceCr(mOrderLine.getPriceActual().multiply(quantity));
		mFactAcctIn.setAmtSourceDr(BigDecimal.valueOf(0));
		mFactAcctIn.setAmtAcctDr(BigDecimal.valueOf(0));
		mFactAcctIn.setAmtAcctCr(mOrderLine.getPriceActual().multiply(quantity));
		mFactAcctIn.setC_UOM_ID(mOrderLine.getC_UOM_ID());
		mFactAcctIn.setQty(quantity);
		mFactAcctIn.setM_Product_ID(productId);
		mFactAcctIn.setDescription(mMovement.getDocumentNo() + "#" + mMovementLine.getLine());
		mFactAcctIn.saveEx();
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
			
			org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = RwplUtils.convertAdLogin(loginReq);
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
				// MInOut inout = new MInOut(ctx, mInoutId, trx.getTrxName());
				// inout.setDescription("QC");
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
	public GenerateProductLabelResponseDocument generateProductLabel(GenerateProductLabelRequestDocument request) {
		GenerateProductLabelRequest generateProductLabelRequest = request.getGenerateProductLabelRequest();
		GenerateProductLabelResponseDocument response = GenerateProductLabelResponseDocument.Factory.newInstance();
		GenerateProductLabelResponse generateProductLabelResponse = response.addNewGenerateProductLabelResponse();

		ADLoginRequest loginReq = generateProductLabelRequest.getADLoginRequest();
		String serviceType = generateProductLabelRequest.getServiceType();
		Trx trx = null;
		try {
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();

			org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = RwplUtils.convertAdLogin(loginReq);
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
			
			if (generateProductLabelRequest.getFinalDispatch()) {
				int orderQnty = 0;
				int scannedQnty = 0;

				for (ProductLabelLine line : productLabelLine) {
					scannedQnty += line.getQuantity();
				}

				MInOut_Custom inOut = new MInOut_Custom(ctx, generateProductLabelRequest.getMInoutId(), trxName);
				MInOutLine[] inoutLines = inOut.getLines();
				for (MInOutLine inoutLine : inoutLines) {
					orderQnty += inoutLine.getQtyEntered().intValue();
				}

				if (orderQnty != scannedQnty) {
					generateProductLabelResponse.setIsError(true);
					generateProductLabelResponse.setError("Scanned Quantity is not matching with shipment quantity");
					return response;
				}
			}
			
			for (ProductLabelLine line : productLabelLine) {
				if (generateProductLabelRequest.getFinalDispatch()) {
					createSalesLabel(loginReq, line, ctx, trxName, generateProductLabelRequest.getMInoutId());
				} else {

					PiProductLabel piProductLabel = new PiProductLabel(ctx, trxName, loginReq, line, true);
					piProductLabel.saveEx();

					line.setLabelId(piProductLabel.get_ID());
					line.setProductName(piProductLabel.getM_Product().getName());
					line.setProductLabelUUId(piProductLabel.getlabeluuid());

				}
			}
			trx.commit();
			generateProductLabelResponse.setIsError(false);
			generateProductLabelResponse.setProductLabelLineArray(productLabelLine);

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

		MInOut_Custom inOut = new MInOut_Custom(ctx, mInoutId, trxName);
		MInOutLine[] inoutLines = inOut.getLines();
		PiProductLabel piProductLabel = new PiProductLabel(ctx, 0, trxName);
		
		piProductLabel.setQcpassed(true);
		if (line.getProductId() != 0) {
			piProductLabel.setquantity(BigDecimal.valueOf(line.getQuantity()));
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
		piProductLabel.setlabeluuid(line.getProductLabelUUId());
		piProductLabel.setIsActive(true);
		piProductLabel.saveEx();

	}
	
	@Override
	public GetLabelDataResponseDocument getLabelData(GetLabelDataRequestDocument request) {
		GetLabelDataRequest getLabelDataRequest = request.getGetLabelDataRequest();
		GetLabelDataResponseDocument getLabelDataResponseDocument = GetLabelDataResponseDocument.Factory.newInstance();
		GetLabelDataResponse getLabelDataResponse = getLabelDataResponseDocument.addNewGetLabelDataResponse();
		String labelUUID = getLabelDataRequest.getLabelUUID();
		// String labelType = getLabelDataRequest.getLabelType();
		boolean isPutawy = getLabelDataRequest.getIsPutAway();
		boolean internalMove = getLabelDataRequest.getInternalMove();
		boolean receiving = getLabelDataRequest.getReceiving();
		boolean pickList = getLabelDataRequest.getPickList();
		boolean finalDispatch = getLabelDataRequest.getFinalDispatch();
		boolean labelAvailableInWarehouse = getLabelDataRequest.getLabelAvailableInWarehouse();
		
		ADLoginRequest loginReq = getLabelDataRequest.getADLoginRequest();
		String serviceType = getLabelDataRequest.getServiceType();
		Trx trx = null;
		try {
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();

			org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = RwplUtils.convertAdLogin(loginReq);
			String err = login(adLoginReq, webServiceName, "getLabelData", serviceType);
			if (err != null && err.length() > 0) {
				getLabelDataResponse.setError(err);
				getLabelDataResponse.setIsError(true);
				return getLabelDataResponseDocument;
			}
			if (!serviceType.equalsIgnoreCase("getLabelData")) {
				getLabelDataResponse.setIsError(true);
				getLabelDataResponse.setError("Service type " + serviceType + " not configured");
				return getLabelDataResponseDocument;
			}

			String columnName = "labelUUId";
			List<PO> poList = PiProductLabel.getPiProductLabel(columnName, labelUUID, ctx, trxName, null);
			if (poList.isEmpty()) {
				getLabelDataResponse.setIsError(true);
				getLabelDataResponse.setError("Invalid label");
				return getLabelDataResponseDocument;
			}
			
			int labelCount = 0;

			for (PO po : poList) {
				ProductLabelLine labelLine = getLabelDataResponse.addNewLabelLine();
				PiProductLabel label = new PiProductLabel(ctx, po.get_ID(), trxName);
				I_M_InOut mInout = label.getM_InOutLine().getM_InOut();

				if (isPutawy && !mInout.getDocStatus().equalsIgnoreCase("CO")) {
					getLabelDataResponse.setIsError(true);
					getLabelDataResponse.setError("Material recipt is not completed, cant do putaway");
					return getLabelDataResponseDocument;
				}
				MLocatorType_Custom locatorType_Custom = new MLocatorType_Custom(ctx,
						label.getM_Locator().getM_LocatorType_ID(), trxName);
				if (receiving && !locatorType_Custom.isReceiving()) {
					getLabelDataResponse.setIsError(true);
					getLabelDataResponse.setError("Label is Not in Receving Area");
					return getLabelDataResponseDocument;
				}

				if ((receiving || pickList || internalMove) && label.isSOTrx()) {
					getLabelDataResponse.setIsError(true);
					getLabelDataResponse.setError("Label is Already Dispatched");
					return getLabelDataResponseDocument;
				}

				if (pickList && (locatorType_Custom.isdispatch() || locatorType_Custom.isReturns())) {
					getLabelDataResponse.setIsError(true);
					getLabelDataResponse.setError("Label is in Dispath/ Returns Area");
					return getLabelDataResponseDocument;
				}
				
				if(finalDispatch && !label.isfinaldispatch()) {
					getLabelDataResponse.setIsError(true);
					getLabelDataResponse.setError("Label is Not in Dispath Area");
					return getLabelDataResponseDocument;
				}
				
				if (labelAvailableInWarehouse && label.isSOTrx()) {
					getLabelDataResponse.setIsError(true);
					getLabelDataResponse.setError("Label is Already Dispatched");
					return getLabelDataResponseDocument;
				}
				
				labelLine.setLabelId(label.getpi_productLabel_ID());
				labelLine.setProductLabelUUId(label.getlabeluuid());
				labelLine.setCOrderlineId(label.getC_OrderLine_ID());
				labelLine.setMInoutlineId(label.getM_InOutLine_ID());
				labelLine.setProductId(label.getM_Product_ID());
				labelLine.setProductName(label.getM_Product().getName());
				labelLine.setLocatorId(label.getM_Locator_ID());
				labelLine.setLocatorName(label.getM_Locator().getValue());
				labelLine.setQuantity(label.getquantity().intValue());
				labelLine.setIsSalesTransaction(label.isSOTrx());
				labelLine.setQcPassed(label.qcpassed());
				labelLine.setWarehouseId(label.getM_Locator().getM_Warehouse_ID());
				labelLine.setWarehouseName(label.getM_Locator().getM_Warehouse().getName());
				labelLine.setFinalDispatch(label.isfinaldispatch());
				labelCount++;
			}

			getLabelDataResponse.setProductCount(labelCount);
			getLabelDataResponse.setLabelUUID(labelUUID);
			trx.commit();

		} catch (Exception e) {
			getLabelDataResponse.setError(e.getMessage());
			getLabelDataResponse.setIsError(true);
			return getLabelDataResponseDocument;
		} finally {
			trx.close();
			getCompiereService().disconnect();
		}

		return getLabelDataResponseDocument;
	}

	@Override
	public StandardResponseDocument putAway(PutAwayRequestDocument request) {

		StandardResponseDocument standardResponseDocument = StandardResponseDocument.Factory.newInstance();
		StandardResponse standardResponse = standardResponseDocument.addNewStandardResponse();
		PutAwayRequest putAwayRequest = request.getPutAwayRequest();
		ADLoginRequest loginReq = putAwayRequest.getADLoginRequest();
		String serviceType = putAwayRequest.getServiceType();
		boolean finalDispatch = putAwayRequest.getFinalDispatch();
//		int mInout = putAwayRequest.getMInoutId();
		Trx trx = null;
		try {
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			getCompiereService().connect();

			org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = RwplUtils.convertAdLogin(loginReq);
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

			MMovement mMovement = new MMovement(ctx, 0, trxName);
			mMovement.setIsActive(true);
			mMovement.setDescription(null);
			mMovement.setApprovalAmt(BigDecimal.valueOf(0));
			mMovement.setChargeAmt(BigDecimal.valueOf(0));
			mMovement.setFreightAmt(BigDecimal.valueOf(0));
//			mMovement.setAD_Org_ID(loginReq.getOrgID());

			PutAwayLine putAwayLineArray[] = putAwayRequest.getPutAwayLineArray();
			String columnName = "labelUUId";
			int mWarehouseId = 0;
			for (PutAwayLine line : putAwayLineArray) {
				List<PO> poList = PiProductLabel.getPiProductLabel(columnName, line.getProductLabelUUId(), ctx, trxName,
						false);
				if (!poList.isEmpty()) {
					PiProductLabel piProductLabel = new PiProductLabel(ctx, poList.get(0).get_ID(), trxName);

					if (mWarehouseId == 0) {
						mWarehouseId = piProductLabel.getM_InOutLine().getM_Locator().getM_Warehouse_ID();
						mMovement.setM_Warehouse_ID(mWarehouseId);
						mMovement.setM_WarehouseTo_ID(mWarehouseId);
						mMovement.saveEx();
					}
					int currentLocatorId = piProductLabel.getM_Locator_ID();
					int newLocatorId = line.getLocatorId();

					int newLabelQnty = line.getNewLabelQnty();

					if (newLabelQnty <= 0) {
						if (currentLocatorId != newLocatorId) {
							moveInventory(ctx, trxName, mMovement, loginReq, piProductLabel.getC_OrderLine_ID(),
									piProductLabel.getquantity(), piProductLabel.getM_Product_ID(), currentLocatorId,
									newLocatorId);
						}

						if (finalDispatch)
							piProductLabel.setfinaldispatch(true);

						piProductLabel.setM_Locator_ID(line.getLocatorId());
						piProductLabel.saveEx();
					} else {
						PiProductLabel newLabel = new PiProductLabel(ctx, trxName, loginReq.getClientID(),
								loginReq.getOrgID(), piProductLabel.getM_Product_ID(), newLocatorId,
								piProductLabel.getC_OrderLine_ID(), piProductLabel.getM_InOutLine_ID(), false,
								BigDecimal.valueOf(newLabelQnty), null);
						if (finalDispatch)
							newLabel.setfinaldispatch(true);
						newLabel.saveEx();

						piProductLabel
								.setquantity(piProductLabel.getquantity().subtract(BigDecimal.valueOf(newLabelQnty)));
						piProductLabel.saveEx();

						Map<String, String> data = new HashMap<>();
						data.put("recordId", String.valueOf(piProductLabel.getlabeluuid()));

						RwplUtils.sendNotificationAsync(true, false, newLabel.get_Table_ID(), newLabel.get_ID(), ctx,
								trxName, "New Label is created : " + piProductLabel.getlabeluuid() + "",
								"New Label is created : " + piProductLabel.getlabeluuid() + "",
								piProductLabel.get_TableName(), data, loginReq.getClientID(),
								"MarkedSalesOrderReadyToPick");

					}
				}

			}

			mMovement.setDocStatus(DocAction.ACTION_Complete);
			mMovement.setDocAction(DocAction.ACTION_Close);
			mMovement.setPosted(true);
			mMovement.setProcessed(true);
			mMovement.setIsApproved(true);
			mMovement.completeIt();
			mMovement.saveEx();

//			if (finalDispatch) {a
//				MInOut_Custom inOut = new MInOut_Custom(ctx, mInout, trxName);
//				MInOutLine[] lines = inOut.getLines();
//				if (lines != null && lines.length != 0)
//					for (PutAwayLine line : putAwayLineArray) {
//
//						PO po = PiProductLabel.getPiProductLabelById("labelUUId", line.getProductLabelUUId(), ctx,
//								trxName, false);
//
//						if (po != null && po.get_ID() != 0) {
//							PiProductLabel cLabel = new PiProductLabel(ctx, 0, trxName);
//							PiProductLabel piProductLabel = new PiProductLabel(ctx, po.get_ID(), trxName);
//							cLabel.setQcpassed(true);
//							if (piProductLabel.getM_Product_ID() != 0) {
//								cLabel.setquantity(piProductLabel.getquantity());
//								cLabel.setM_Product_ID(piProductLabel.getM_Product_ID());
//								for (MInOutLine inoutLine : lines) {
//									if (inoutLine.getM_Product_ID() == piProductLabel.getM_Product_ID()) {
//										cLabel.setM_InOutLine_ID(inoutLine.get_ID());
//										cLabel.setC_OrderLine_ID(inoutLine.getC_OrderLine_ID());
//										break;
//									}
//								}
//							}
//							cLabel.setM_Locator_ID(piProductLabel.getM_Locator_ID());
//							cLabel.setIsSOTrx(true);
//							cLabel.setlabeluuid(piProductLabel.getlabeluuid());
//							cLabel.setIsActive(true);
//							cLabel.saveEx();
//						}
//					}
//			}

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
			
			org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = RwplUtils.convertAdLogin(loginReq);
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
			locatorDeatilResponse.setLocatorType(mLocator.getM_LocatorType().getName());
			locatorDeatilResponse.setWarehouseId(mLocator.getM_Warehouse_ID());
			locatorDeatilResponse.setWarehouseName(mLocator.getWarehouseName());
			
			if(locatorDeatilRequest.getGetAvailableLabels()) {
				List<Integer> productLabelIds = PiProductLabel.getPiProductLabelsByLocator(ctx, trxName, loginReq.getClientID(), locatorId);
				
				if(productLabelIds .size() != 0) {
					
					for(Integer id : productLabelIds) {
						ProductLabelLine line = locatorDeatilResponse.addNewLabelLine();
						PiProductLabel label = new PiProductLabel(ctx, id, trxName);
						line.setProductLabelUUId(label.getlabeluuid());
						line.setProductId(label.getM_Product_ID());
						line.setQuantity(label.getquantity().intValue());
						line.setLabelId(label.get_ID());
						line.setProductName(label.getM_Product().getName());
					}
				}
			}else
				locatorDeatilResponse.addNewLabelLine();
			
			if (locatorDeatilRequest.getAvailableProducts()) {
				List<Integer> productLabelIds = PiProductLabel.getPiProductLabelsByLocator(ctx, trxName,
						loginReq.getClientID(), locatorId);

				if (productLabelIds.size() != 0) {

					for (Integer id : productLabelIds) {

						PiProductLabel label = new PiProductLabel(ctx, id, trxName);

						boolean flag = false;
						for (ProductLine line : locatorDeatilResponse.getProductLineArray()) {
							if (line.getProductId() == label.getM_Product_ID()) {
								line.setQuantity(line.getQuantity() + label.getquantity().intValue());
								flag = true;
								break;
							}
						}
						if (!flag) {
							ProductLine line = locatorDeatilResponse.addNewProductLine();
							line.setProductId(label.getM_Product_ID());
							line.setQuantity(label.getquantity().intValue());
							line.setProductName(label.getM_Product().getName());

						}

					}
				}
			}else
				locatorDeatilResponse.addNewProductLine();
			
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
		try {
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			getCompiereService().connect();
			
			org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = RwplUtils.convertAdLogin(loginReq);
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

			List<Integer> orgList = new ArrayList<>();
			Login login = new Login(m_cs.getCtx());
			KeyNamePair[] orgs = login.getOrgs(new KeyNamePair(loginReq.getRoleID(), ""));
			if (orgs != null) {
				for (KeyNamePair org : orgs) {
					orgList.add(Integer.valueOf(org.getID()));
				}
			}
			String orgIds = orgList.stream().map(Object::toString).collect(Collectors.joining(", "));

			String query = "SELECT\n" + "    DISTINCT(po.documentno) AS documentNo,\n" + "    bp.name AS Supplier,\n"
					+ "    po.m_inout_id AS mInoutID,\n" + "    TO_CHAR(po.dateordered, 'DD/MM/YYYY') AS Order_Date,\n"
					+ "    wh.name AS Warehouse_Name,\n" + "    po.description, po.created, po.m_inout_id,\n"
					+ "	co.documentno as orderDocumentno\n" + "FROM m_inout po\n"
					+ "JOIN c_bpartner bp ON po.c_bpartner_id = bp.c_bpartner_id \n"
					+ "LEFT JOIN c_order co on co.c_order_id = po.c_order_id\n"
					+ "JOIN m_warehouse wh ON po.m_warehouse_id = wh.m_warehouse_id\n" + "WHERE po.ad_client_id = "
					+ loginReq.getClientID() + " AND po.issotrx = 'N' AND po.docstatus = 'DR' AND po.ad_org_id IN ("
					+ orgIds + ") AND po.ad_orgtrx_id is null " + "AND (\n"
					+ "    po.documentno ILIKE '%' || COALESCE(?, po.documentno) || '%'\n"
					+ "    OR bp.name ILIKE '%' || COALESCE(?, bp.name) || '%'\n"
					+ "    OR wh.name ILIKE '%' || COALESCE(?, wh.name) || '%'\n"
					+ "    OR po.description ILIKE '%' || COALESCE(?, po.description) || '%'\n"
					+ "    OR co.documentno ILIKE '%' || COALESCE(?, co.documentno) || '%'\n" + ")\n"
					+ " ORDER BY po.created desc;";

			pstm = DB.prepareStatement(query.toString(), null);
			pstm.setString(1, searchKey);
			pstm.setString(2, searchKey);
			pstm.setString(3, searchKey);
			pstm.setString(4, searchKey);
			pstm.setString(5, searchKey);
			rs = pstm.executeQuery();

			int count = 0;
			while (rs.next()) {
				PutAwayList putAwayList = putAwayListResponse.addNewPutAwayList();
				String documentNo = rs.getString("documentNo");
				int mInoutID = rs.getInt("mInoutID");
				String supplier = rs.getString("Supplier");
				String date = rs.getString("Order_Date") == null ? "" : rs.getString("Order_Date");
				String orderDocumentno = rs.getString("orderDocumentno") == null ? "" : rs.getString("orderDocumentno");
				String warehouseName = rs.getString("Warehouse_Name");
				String description = rs.getString("description");
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

			List<PO> poList = new ArrayList<PO>();
//			= MInOut_Custom.getLocatorByName("Receiving Locator", null, null,
//					loginReq.getWarehouseID());
			List<MLocator> locators = MLocatorType_Custom.getLocatorsByType(ctx, trxName, loginReq.getWarehouseID(), "receiving", "Y");
			if (locators != null && locators.size() != 0) {
				for (MLocator locator : locators) {
					poList.addAll(PiProductLabel.getPiProductLabel("m_locator_ID", locator.get_ID(), ctx, trxName, false));
				}
			} else {
				putAwayListResponse.setIsError(true);
				putAwayListResponse.setError("Receiving Locator Not Found");
				return response;
			}
			for (PO po : poList) {
				PiProductLabel piProductLabel = new PiProductLabel(ctx, po.get_ID(), trxName);
				I_M_InOut mInout = piProductLabel.getM_InOutLine().getM_InOut();
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
							pal.setQuantityToPick(pal.getQuantityToPick() + piProductLabel.getquantity().intValue());
							flag = true;
							count++;
							break;
						}
					}
					if (!flag) {
						PutAwayList putAwayList = putAwayListResponse.addNewPutAwayList();
						putAwayList.setDocumentNo(mInout.getDocumentNo());
						putAwayList.setMInoutID(mInout.getM_InOut_ID());
						putAwayList.setSupplier(mInout.getC_BPartner().getName());
						putAwayList.setOrderDate(mInout.getDateOrdered() == null ? "" : mInout.getDateOrdered().toString());
						putAwayList.setWarehouseId(mInout.getM_Warehouse_ID());
						putAwayList.setWarehouseName(mInout.getM_Warehouse().getValue());
						putAwayList.setOrderDocumentno(mInout.getC_Order().getDocumentNo() == null ? "" : mInout.getC_Order().getDocumentNo());
						putAwayList.setToMarkForPutAway(false);
						putAwayList.setQuantityToPick(piProductLabel.getquantity().intValue());
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
			
			org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = RwplUtils.convertAdLogin(loginReq);
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

			List<PO> poList = new ArrayList<PO>();
			
			List<MLocator> locators = MLocatorType_Custom.getLocatorsByType(ctx, trxName, loginReq.getWarehouseID(), "receiving", "Y");
			if (locators != null && locators.size() != 0) {
				for (MLocator mLocator : locators) {
					poList.addAll(PiProductLabel.getPiProductLabel("m_locator_ID", mLocator.get_ID(), ctx, trxName, false));
				}
			} else {
				putAwayLabourResponse.setIsError(true);
				putAwayLabourResponse.setError("Receiving Locator Not Found");
				return response;
			}
			List<MLocator> mLocatorArray = null;
			
			int locatoId = 0;
			String locatorName = "";
			for (PO po : poList) {
				
				PiProductLabel piProductLabel = new PiProductLabel(ctx, po.get_ID(), trxName);
				if (piProductLabel.getM_InOutLine().getM_InOut().getDocStatus().equalsIgnoreCase("CO")) {
					PutAwayLabour[] putAwayLabourArray = putAwayLabourResponse.getPutAwayLabourArray();

					boolean flag = false;
					for (PutAwayLabour detail : putAwayLabourArray) {
						if (detail.getProductId() == piProductLabel.getM_Product_ID()) {
							detail.setQuantity(detail.getQuantity() + piProductLabel.getquantity().intValue());
							flag = true;
							count++;
							break;
						}
					}
					if (!flag) {
						PutAwayLabour putAwayLabour = putAwayLabourResponse.addNewPutAwayLabour();
						putAwayLabour.setProductId(piProductLabel.getM_Product_ID());
						putAwayLabour.setProductName(piProductLabel.getM_Product().getName());
						putAwayLabour.setQuantity(piProductLabel.getquantity().intValue());
						
						if (mLocatorArray == null) {
							mLocatorArray = MLocatorType_Custom.getLocatorsByType(ctx, trxName,
									loginReq.getWarehouseID(), "storage", "Y");
							if (mLocatorArray == null || mLocatorArray.size() == 0) {
								putAwayLabourResponse.setIsError(true);
								putAwayLabourResponse.setError("storage Locator Not Found");
								return response;
							}
						}
						
						int i = -1;
						boolean removeLocator = false;
						for (MLocator mLocator : mLocatorArray) {

							i++;
							
							MLocatorType_Custom locatorType = new MLocatorType_Custom(ctx,
									mLocator.getM_LocatorType_ID(), trxName);

							if (!locatorType.isdispatch() && !locatorType.isReceiving() && !locatorType.isReturns()) {
								Integer qntyOnHand = PiProductLabel.getSumOfQntyByLocator(clientId, mLocator.getM_Locator_ID());
								
								if (locatoId == 0) {
									locatoId = mLocator.getM_Locator_ID();
									locatorName = mLocator.getValue();
								}
								if (qntyOnHand <= 0) {
									locatoId = mLocator.getM_Locator_ID();
									locatorName = mLocator.getValue();
									removeLocator = true;
									break;
								}
							}
						}
						
						if(removeLocator)
							mLocatorArray.remove(i);
						
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

			org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = RwplUtils.convertAdLogin(loginReq);
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

			SReceiptLine[] sReceiptLineArray = requestData.getSReceiptLineArray();

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

				for (SReceiptLine sReceiptLine : sReceiptLineArray) {
					PO po = PiProductLabel.getPiProductLabelById("labelUUId", sReceiptLine.getProductLabelUUId(), ctx,
							trxName, false);
					PiProductLabel piProductLabel = new PiProductLabel(ctx, po.get_ID(), trxName);

					List<MLocator> locators = MLocatorType_Custom.getLocatorsByType(ctx, trxName, loginReq.getWarehouseID(), "returns", "Y");
									
					if (locators == null || locators.size() == 0) {
						standardResponse.setIsError(true);
						standardResponse.setError("Returns Locator not found for Warehouse : "
								+ piProductLabel.getM_Locator().getM_Warehouse_ID() + "");
						return response;
					}
					MLocator mLocator =locators.get(0);

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
											+ piProductLabel.getquantity().intValue());
									innerFlag = true;
									break;
								}
							}
							if (!innerFlag) {
								lineIdAndQntyMap.put(piProductLabel.getM_InOutLine_ID(),
										piProductLabel.getquantity().intValue());
								mInoutMap.put(minoutId, lineIdAndQntyMap);
							}
							flag = true;
							break;
						}
					}
					if (!flag) {
						LinkedHashMap<Integer, Integer> lineIdAndQntyMap = new LinkedHashMap<Integer, Integer>();
						lineIdAndQntyMap.put(piProductLabel.getM_InOutLine_ID(),
								piProductLabel.getquantity().intValue());
						mInoutMap.put(piProductLabel.getM_InOutLine().getM_InOut_ID(), lineIdAndQntyMap);
					}

					moveInventory(ctx, trxName, mMovement, loginReq, piProductLabel.getC_OrderLine_ID(),
							piProductLabel.getquantity(), piProductLabel.getM_Product_ID(),
							piProductLabel.getM_Locator_ID(), mLocator.getM_Locator_ID());

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
			
			org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = RwplUtils.convertAdLogin(loginReq);
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

			List<MLocator> locators = MLocatorType_Custom.getLocatorsByType(ctx, trxName, loginReq.getWarehouseID(), "dispatch", "Y");

			MLocator mLocator =locators.get(0);

			String sql = "SELECT \n" + "    SUM(qtyordered) as totalQnty, \n"
					+ "    SUM(outstanding_qty) AS total_outstanding_qty, \n" + "    productId, \n" + "    value\n"
					+ "FROM (\n" + "    SELECT \n" + "        SUM(a.qtyordered) AS qtyordered,\n"
					+ "        SUM(COALESCE((\n" + "            SELECT SUM(c.qtyentered)\n"
					+ "            FROM m_inoutline c\n" + "            JOIN m_inout d ON d.m_inout_id = c.m_inout_id\n"
					+ "            WHERE c.c_orderline_id = a.c_orderline_id\n"
					+ "                AND d.issotrx = 'Y'\n" + "        ), 0)) AS total_qtyentered,\n"
					+ "        SUM(a.qtyordered) - SUM(COALESCE((\n" + "            SELECT SUM(c.qtyentered)\n"
					+ "            FROM m_inoutline c\n" + "            JOIN m_inout d ON d.m_inout_id = c.m_inout_id\n"
					+ "            WHERE c.c_orderline_id = a.c_orderline_id\n"
					+ "                AND d.DocStatus = 'CO' AND d.issotrx = 'Y'\n"
					+ "        ), 0)) AS outstanding_qty,\n" + "        e.m_product_id AS productId, \n"
					+ "        e.value\n" + "    FROM \n" + "        c_orderline a \n" + "    JOIN \n"
					+ "        c_order d ON d.c_order_id = a.c_order_id \n" + "    JOIN \n"
					+ "        m_product e ON e.m_product_id = a.m_product_id \n" + "    WHERE \n"
					+ "        d.ad_client_id = " + loginReq.getClientID() + "\n" + "        AND d.m_warehouse_id = "
					+ loginReq.getWarehouseID() + " \n" + "        AND d.putStatus = 'pick' \n"
					+ "        AND d.issotrx = 'Y' \n" + "        AND d.docstatus = 'CO'\n"
					+ "    GROUP BY e.m_product_id, e.value\n" + ") AS subquery\n" + "GROUP BY \n"
					+ "    productId, value\n" + "HAVING \n" + "    SUM(outstanding_qty) > 0\n" + "ORDER BY \n"
					+ "    productId;\n" + "";

			pstm = DB.prepareStatement(sql.toString(), null);
			rs = pstm.executeQuery();

			int count = 0;
			LinkedHashMap<Integer, Integer> qntyOnLocator = PiProductLabel
					.getAvailableLabelsByLocator(loginReq.getClientID(), mLocator.getM_Locator_ID());
			while (rs.next()) {
				int mProductId = rs.getInt("productId");

				String productName = rs.getString("value");
//					int totalQtyordered = rs.getInt("totalQnty");
				int remainQuantity = rs.getInt("total_outstanding_qty");

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

					pickDetailLabour(line, ctx, trxName, mLocator.getM_Warehouse_ID(), mProductId, remainQuantity);

					qntyOnLocator.put(mProductId, qntyToPutBack);
					count++;
				}

			}
			pickListLabourResponse.setWarehouseId(mLocator.getM_Warehouse_ID());
			pickListLabourResponse.setWarehouseName(mLocator.getM_Warehouse().getValue());
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

			MLocatorType_Custom locatorType = new MLocatorType_Custom(ctx,
					mStorageOnHand.getM_Locator().getM_LocatorType_ID(), trxName);
			if (!flag && !locatorType.isdispatch() && !locatorType.isReceiving()) {
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
			
			org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = RwplUtils.convertAdLogin(loginReq);
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


				MLocatorType_Custom locatorType = new MLocatorType_Custom(ctx,
						mStorageOnHand.getM_Locator().getM_LocatorType_ID(), trxName);
				if (!flag && !locatorType.isdispatch() && !locatorType.isReceiving()) {
					PickLabourLines line = response.addNewPickLabourLines();
					line.setProductId(mStorageOnHand.getM_Product_ID());
					line.setProductName(mStorageOnHand.getM_Product().getValue());
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

//	private JSONObject getRemainQuantityForOrder(Properties ctx, String trxName, String cOrderId, String documentNo,
//			int clientId, LinkedHashMap<Integer, LinkedHashMap<Integer, Integer>> pickedQuantity, int warehouseId) {
//		JSONObject jsonObject = null;
//		PreparedStatement pstm = null;
//		ResultSet rs = null;
//		try {
//			int quantityPicked = 0;
//			int quantityTotal = 0;
//			jsonObject = new JSONObject();
//
//			String sql = "SELECT a.qtyordered as totalQnty, (a.qtyordered - COALESCE(SUM(c.qtyentered), 0)) AS outstanding_qty, e.m_product_id as productId, a.c_order_id, a.c_uom_id, a.c_orderline_id, e.name AS product_name\n"
//					+ "FROM c_orderline a \n" + "JOIN c_order d ON d.c_order_id = a.c_order_id \n"
//					+ "LEFT JOIN m_inout b ON a.c_order_id = b.c_order_id \n"
//					+ "LEFT JOIN m_inoutline c ON c.m_inout_id = b.m_inout_id AND c.c_orderline_id = a.c_orderline_id\n"
//					+ "JOIN m_product e ON e.m_product_id = a.m_product_id \n" + "WHERE d.documentno = '" + documentNo
//					+ "' AND d.ad_client_id = '" + clientId + "' AND a.c_order_id = (\n"
//					+ "  SELECT c_order_id FROM c_order WHERE documentno = '" + documentNo + "' AND ad_client_id = '"
//					+ clientId + "'\n" + ")\n"
//					+ "GROUP BY e.m_product_id, e.name, a.qtyordered, a.c_orderline_id, a.c_uom_id, a.c_order_id ORDER BY a.c_orderline_id;\n"
//					+ "";
//
//			pstm = DB.prepareStatement(sql.toString(), null);
//			rs = pstm.executeQuery();
//			while (rs.next()) {
//				int totalQnty = rs.getInt("totalQnty");
//				int outstandingQty = rs.getInt("outstanding_qty");
//				int productId = rs.getInt("productId");
//
//				LinkedHashMap<Integer, Integer> availableQnty = pickedQuantity.get(warehouseId);
//				MOrder order = new MOrder(ctx, Integer.valueOf(cOrderId), trxName);
//				MInOut[] mInout = order.getShipments();
//				for (MInOut inout : mInout) {
//					for (MInOutLine line : inout.getLines()) {
//						if (line.getM_Product_ID() == productId) {
//							if (!inout.getDocStatus().equalsIgnoreCase("CO")) 
//								for (Integer key : availableQnty.keySet()) {
//									if (productId == key) {
//										int qnty = availableQnty.get(key);
//										availableQnty.put(key, qnty - line.getQtyEntered().intValue());
//										break;
//									}
//									
//							}
//							totalQnty = totalQnty - line.getQtyEntered().intValue();
//						}
//					}
//				}
//
//				
//				for (Integer key : availableQnty.keySet()) {
//					if (productId == key) {
//						int qnty = availableQnty.get(key);
//
//						if (qnty >= outstandingQty) {
//							quantityPicked += outstandingQty;
//							availableQnty.put(key, qnty - outstandingQty);
//						} else if (outstandingQty != 0 && qnty != 0) {
//							quantityPicked += qnty;
//							availableQnty.put(key, 0);
//						}
//						break;
//					}
//				}
//				quantityTotal += totalQnty;
//
//			}
//			jsonObject.put("totalQnty", quantityTotal);
//			jsonObject.put("pickedQnty", quantityPicked);
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			closeDbCon(pstm, rs);
//		}
//		return jsonObject;
//	}

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
			
			org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = RwplUtils.convertAdLogin(loginReq);
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

//			data.put("path1", "/labour_pick_screen");

//			PipraUtils.sendNotificationAsync("Supervisor", cOrder.get_Table_ID(), cOrder.getC_Order_ID(), ctx, trxName,
//					"PickList Updated", "Sales Order " + cOrder.getDocumentNo() + " Marked To PickList",
//					cOrder.get_TableName(), data, loginReq.getClientID(), "MarkedSalesOrderReadyToPick");

			data.put("path1", "/labour_pick_screen");

			RwplUtils.sendNotificationAsync(false, true, cOrder.get_Table_ID(), cOrder.getC_Order_ID(), ctx, trxName,
					" Products added for Picking", "New items marked for Picking", cOrder.get_TableName(), data,
					loginReq.getClientID(), "MarkedSalesOrderReadyToPick");

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

			org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = RwplUtils.convertAdLogin(loginReq);
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
			
			org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = RwplUtils.convertAdLogin(loginReq);
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
	public StandardResponseDocument createNotice(CreateNoticeRequestDocument request) {
		StandardResponseDocument response = StandardResponseDocument.Factory.newInstance();
		StandardResponse standardResponse = response.addNewStandardResponse();
		CreateNoticeRequest createNoticeRequest = request.getCreateNoticeRequest();
		ADLoginRequest loginReq = createNoticeRequest.getADLoginRequest();
		String serviceType = createNoticeRequest.getServiceType();
		Trx trx = null;
		try {
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			getCompiereService().connect();
			
			org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = RwplUtils.convertAdLogin(loginReq);
			String err = login(adLoginReq, webServiceName, "createNotice", serviceType);
			if (err != null && err.length() > 0) {
				standardResponse.setError(err);
				standardResponse.setIsError(true);
				return response;
			}

			if (!serviceType.equalsIgnoreCase("createNotice")) {
				standardResponse.setIsError(true);
				standardResponse.setError("Service type " + serviceType + " not configured");
				return response;
			}

			MNote mNoteS = new MNote(ctx, 0, trxName);
			mNoteS.setAD_Message_ID(createNoticeRequest.getMessage());
			mNoteS.setTextMsg(createNoticeRequest.getTextMessage());
			mNoteS.setDescription(createNoticeRequest.getDescription());
			mNoteS.setReference(createNoticeRequest.getReference());
			mNoteS.setAD_User_ID(Env.getAD_User_ID(ctx));

			if (mNoteS.getAD_Message_ID() == 240) {
				MMessage mMessage = new MMessage(ctx, 0, trxName);
				mMessage.setValue(createNoticeRequest.getMessage());
				mMessage.setMsgText(createNoticeRequest.getMessage());
				mMessage.setMsgType("M");
				mMessage.saveEx();

				mNoteS.setAD_Message_ID(mMessage.getAD_Message_ID());
			}

			mNoteS.saveEx();

			MAttachment mAttachment = new MAttachment(ctx, 0, trxName);
			mAttachment.setAD_Table_ID(mNoteS.get_Table_ID());
			mAttachment.setRecord_ID(mNoteS.getAD_Note_ID());
			mAttachment.addEntry(createNoticeRequest.getFileName(), createNoticeRequest.getFileData());

			MStorageProvider newProvider = MStorageProvider.get(ctx, 0);

			IAttachmentStore prov = newProvider.getAttachmentStore();
			mAttachment.saveEx();
			if (prov != null)
				prov.save(mAttachment, newProvider);

			standardResponse.setIsError(false);
			trx.commit();
		} catch (Exception e) {
			e.printStackTrace();
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

			org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = RwplUtils.convertAdLogin(loginReq);
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
					+ "    TO_CHAR(po.created, 'DD/MM/YYYY') AS created,\n" + "    po.m_inout_id,\n"
					+ "	co.documentno as orderDocumentno\n" + "FROM m_inout po\n"
					+ "JOIN c_bpartner bp ON po.c_bpartner_id = bp.c_bpartner_id \n"
					+ "LEFT JOIN c_order co on co.c_order_id = po.c_order_id\n"
					+ "JOIN m_warehouse wh ON po.m_warehouse_id = wh.m_warehouse_id\n" + "WHERE po.ad_client_id = "
					+ loginReq.getClientID() + " AND po.issotrx = 'N' AND po.documentno ='" + documentNumber + "';";

			pstm = DB.prepareStatement(query.toString(), null);
			rs = pstm.executeQuery();

			while (rs.next()) {
				String documentNo = rs.getString("documentNo");
				int mInoutID = rs.getInt("mInoutID");
				String supplier = rs.getString("Supplier");
				String date = rs.getString("Order_Date");
				if (date == null)
					date = rs.getString("created");
				String orderDocumentno = rs.getString("orderDocumentno") == null ? "" : rs.getString("orderDocumentno");
				String warehouseName = rs.getString("Warehouse_Name");
				int mWarehouseId = rs.getInt("m_warehouse_id");
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

			MInOut inOut = new MInOut(ctx, putAwayDetailResponse.getMInoutID(), trxName);
			if (inOut.getDocStatus() == DocAction.ACTION_Complete) {
				List<PO> poList = new ArrayList<PO>();

				List<MLocator> locators = MLocatorType_Custom.getLocatorsByType(ctx, trxName, loginReq.getWarehouseID(),
						"receiving", "Y");

				if (locators == null || locators.size() == 0) {
					putAwayDetailResponse.setIsError(true);
					putAwayDetailResponse.setError("Receiving Locators Not Found For Department");
					return response;
				}

				MInOut_Custom mInout = new MInOut_Custom(ctx, putAwayDetailResponse.getMInoutID(), trxName);
				for (MInOutLine line : mInout.getLines()) {

					for (MLocator locator : locators) {
						poList.addAll(PiProductLabel.getProductLabelForInoutLineAndLocator(locator.getM_Locator_ID(),
								line.getM_InOutLine_ID(), ctx, trxName));
					}

				}

				for (PO po : poList) {
					PiProductLabel piProductLabel = new PiProductLabel(ctx, po.get_ID(), trxName);

					PutAwayDetail[] list = putAwayDetailResponse.getPutAwayDetailArray();
					boolean flag = false;
					for (PutAwayDetail detail : list) {
						if (detail.getMInoutLineId() == piProductLabel.getM_InOutLine_ID()) {
							detail.setQuantityInRecevingLocator(
									detail.getQuantityInRecevingLocator() + piProductLabel.getquantity().intValue());
							flag = true;
							break;
						}
					}
					if (!flag) {
						PutAwayDetail putAwayDetail = putAwayDetailResponse.addNewPutAwayDetail();
						putAwayDetail.setProductId(piProductLabel.getM_Product_ID());
						putAwayDetail.setProductName(piProductLabel.getM_Product().getValue());
						putAwayDetail.setCOrderlineId(piProductLabel.getC_OrderLine_ID());
						putAwayDetail.setMInoutLineId(piProductLabel.getM_InOutLine_ID());
						putAwayDetail.setQuantityInRecevingLocator(piProductLabel.getquantity().intValue());
						MInOutLine_Custom lineCustom = new MInOutLine_Custom(ctx, piProductLabel.getM_InOutLine_ID(),
								trxName);
						putAwayDetail.setTotalQuantity(piProductLabel.getM_InOutLine().getQtyEntered().intValue()
								- lineCustom.getQCFailedQty().intValue());
					}
				}
			} else {
				MInOutLine[] lines = inOut.getLines(false);
				for (MInOutLine line : lines) {
					PutAwayDetail putAwayDetail = putAwayDetailResponse.addNewPutAwayDetail();
					putAwayDetail.setProductId(line.getM_Product_ID());
					putAwayDetail.setProductName(line.getM_Product().getValue());
					putAwayDetail.setCOrderlineId(line.getC_OrderLine_ID());
					putAwayDetail.setMInoutLineId(line.getM_InOutLine_ID());
					putAwayDetail.setQuantityInRecevingLocator(line.getQtyEntered().intValue());
					MInOutLine_Custom lineCustom = new MInOutLine_Custom(ctx, line.getM_InOutLine_ID(), trxName);
					putAwayDetail
							.setTotalQuantity(line.getQtyEntered().intValue() - lineCustom.getQCFailedQty().intValue());

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

			org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = RwplUtils.convertAdLogin(loginReq);
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
				Map<String, String> data = new HashMap<>();
				data.put("recordId", String.valueOf(recordID));
				data.put("documentNo", inout.getDocumentNo());
				if (!inout.isSOTrx()) {
					data.put("path1", "/put_away_screen");
					data.put("path2", "/put_away_detail_screen");
			
					RwplUtils.sendNotificationAsync(true,false, inout.get_Table_ID(), recordID, ctx, trxName,
							"Products added for Put away", "New products ready for Put away " + recordID + "",
							inout.get_TableName(), data, clientId, "productsAddedForPutaway");

					data.put("path1", "/labour_put_away_screen");
					data.remove("path2");

					RwplUtils.sendNotificationAsync(false,true, inout.get_Table_ID(), recordID, ctx, trxName,
							"Products added for Put away", "New products ready for Put away", inout.get_TableName(),
							data, clientId, "productsAddedForPutaway");
				} else {

					data.put("path1", "/dispatch_screen");
					data.put("path2", "/dispatch_detail_screen");

					RwplUtils.sendNotificationAsync(true, false, inout.get_Table_ID(), recordID, ctx, trxName,
							"Products dispatched with shipment- " + inout.getDocumentNo() + "",
							"Products dispatched with Shipment No: " + inout.getDocumentNo() + " for Order - "
									+ inout.getC_Order().getDocumentNo() + "",
							inout.get_TableName(), data, clientId, "MarkedSalesOrderReadyToPick");
				}

			}
			return res;
		} finally {
			if (manageTrx && trx != null)
				trx.close();

			getCompiereService().disconnect();
		}
	}
	
	public GetClientConfigResponseDocument getClientConfig(GetClientConfigRequestDocument req) {
		GetClientConfigResponseDocument res = GetClientConfigResponseDocument.Factory.newInstance();
		GetClientConfigResponse response = res.addNewGetClientConfigResponse();
		GetClientConfigRequest request = req.getGetClientConfigRequest();
		ADLoginRequest loginReq = request.getADLoginRequest();
		String serviceType = request.getServiceType();
		Trx trx = null;
		try {
//			CompiereService m_cs = getCompiereService();
//			Properties ctx = m_cs.getCtx();
			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			getCompiereService().connect();

			org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = RwplUtils.convertAdLogin(loginReq);
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

			ClientConfig clientName = response.addNewClientConfig();
			clientName.setKey("clientName");
			clientName.setValue("RWPL");

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
		String labelB = request.getLabelUUID2();
		int quantity = request.getQuantity();
		int productQty = 0, productQty2 = 0,totalQty = 0,actualQty = 0;
		int productId1 = 0,productId2 = 0;
		Trx trx = null;
		boolean mergeStatus = request.getMergeStatus();
		try {
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			getCompiereService().connect();

			org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = RwplUtils.convertAdLogin(loginReq);
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
			
			List<PO> poList = RwplUtils.getPiProductLabelById(labelUUid, ctx, trxName);
			if (poList == null || poList.size() == 0 || poList.size() > 1) {
				response.setIsError(true);
				response.setError("Invalid Product Label");
				return res;
			}

			PO po = poList.get(0);

			X_pi_productLabel productlabel = new X_pi_productLabel(ctx, po.get_ID(), trxName);
			productQty = productlabel.getquantity().intValue();
			productId1 = productlabel.getM_Product_ID();
			if (productId1 == 0) {
				response.setIsError(true);
				response.setError("This label does not have a product");
				return res;
			}
			if (mergeStatus == false) {

				if (quantity <= 0) {
					response.setIsError(true);
					response.setError("Enter Qty not Zero and negetive value");
					return res;
				}

				if (productQty < quantity) {
					response.setIsError(true);
					response.setError(
							"The entered quantity must not be greater than the quantity on the product label");
					return res;
				}

				actualQty = productQty - quantity;
				if (actualQty == 0) {
					response.setIsError(true);
					response.setError("The entered quantity must not be Equal than the quantity on the product label");
					return res;
				}
				productlabel.setquantity(BigDecimal.valueOf(actualQty));
				productlabel.saveEx();
				trx.commit();

				PiProductLabel label = new PiProductLabel(ctx, 0, trxName);
				label.setAD_Org_ID(adLoginReq.getOrgID());
				label.setQcpassed(productlabel.qcpassed());
				label.setM_Product_ID(productlabel.getM_Product_ID());
				label.setM_Locator_ID(productlabel.getM_Locator_ID());
				label.setquantity(BigDecimal.valueOf(quantity));
				label.setC_OrderLine_ID(productlabel.getC_OrderLine_ID());
				label.setM_InOutLine_ID(productlabel.getM_InOutLine_ID());
				label.setIsSOTrx(productlabel.isSOTrx());
				label.setlabeluuid(UUID.randomUUID().toString());
				label.setIsActive(true);
				label.saveEx();

				trx.commit();

				I_M_Product product = new X_M_Product(ctx, label.getM_Product_ID(), trxName);

				response.setProductLabelId(label.getpi_productLabel_ID());
				response.setProductLabelUUId(label.getlabeluuid());
				response.setProductName(product.getName());
				response.setQuantity(quantity);
				response.setIsError(false);
			} else {
				List<PO> poList2 = RwplUtils.getPiProductLabelById(labelB, ctx, trxName);
				if (poList2 == null || poList2.size() == 0 || poList2.size() > 1) {
					response.setIsError(true);
					response.setError("Invalid Label");
					return res;
				}

				PO po2 = poList2.get(0);

				X_pi_productLabel productlabel2 = new X_pi_productLabel(ctx, po2.get_ID(), trxName);
				productQty2 = productlabel2.getquantity().intValue();
				productId2 = productlabel2.getM_Product_ID();
				if (productId2 == 0) {
					response.setIsError(true);
					response.setError("This Second label does not have a product");
					return res;
				}
				
				if(productId1 != productId2) {
					response.setIsError(true);
					response.setError("The product labels for both products are different");
					return res;
				}
				
				if (productQty2 <= 0) {
					response.setIsError(true);
					response.setError("Enter Qty not Zero and negetive value");
					return res;
				}
				totalQty = productQty + productQty2;
				productlabel.setquantity(BigDecimal.valueOf(totalQty));
				productlabel.saveEx();
				trx.commit();
				
				productlabel2.deleteEx(true);
				trx.commit();
				
				I_M_Product product = new X_M_Product(ctx, productlabel.getM_Product_ID(), trxName);
				
				response.setProductLabelId(productlabel.getpi_productLabel_ID());
				response.setProductLabelUUId(productlabel.getlabeluuid());
				response.setProductName(product.getName());
				response.setQuantity(totalQty);
				response.setIsError(false);
			}
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
	public SplitAndMergeMoveLabelResponseDocument splitAndMergeMoveLabel(SplitAndMergeMoveLabelRequestDocument req) {
		SplitAndMergeMoveLabelResponseDocument res = SplitAndMergeMoveLabelResponseDocument.Factory.newInstance();
		SplitAndMergeMoveLabelResponse response = res.addNewSplitAndMergeMoveLabelResponse();
		SplitAndMergeMoveLabelRequest request = req.getSplitAndMergeMoveLabelRequest();
		ADLoginRequest loginReq = request.getADLoginRequest();
		String serviceType = request.getServiceType();
		String labelA = request.getLabelUUID();
		String labelB = request.getLabelUUID2();
		int quantity = request.getQuantity();
		int productQty = 0, productQty2 = 0,totalQty = 0,actualQty = 0;
		int productId1 = 0,productId2 = 0;
		Trx trx = null;
		try {
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			getCompiereService().connect();

			org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = RwplUtils.convertAdLogin(loginReq);
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
			
			if (labelA.trim().equalsIgnoreCase(labelB.trim())) {
				response.setIsError(true);
				response.setError("Both labels are the same");
				return res;
			}
			
			List<PO> poList = RwplUtils.getPiProductLabelById(labelA, ctx, trxName);
			if (poList == null || poList.size() == 0 || poList.size() > 1) {
				response.setIsError(true);
				response.setError("Invalid First Product Label");
				return res;
			}

			PO po = poList.get(0);

			X_pi_productLabel productlabel = new X_pi_productLabel(ctx, po.get_ID(), trxName);
			productQty = productlabel.getquantity().intValue();
			productId1 = productlabel.getM_Product_ID();
			if (productId1 == 0) {
				response.setIsError(true);
				response.setError("This First Label does not have a product");
				return res;
			}
				if (quantity <= 0) {
					response.setIsError(true);
					response.setError("Quantity not be Zero and negetive value");
					return res;
				}

				if (productQty < quantity) {
					response.setIsError(true);
					response.setError(
							"The entered quantity must not be greater than the quantity on the product label");
					return res;
				}

				List<PO> poList2 = RwplUtils.getPiProductLabelById(labelB, ctx, trxName);
				if (poList2 == null || poList2.size() == 0 || poList2.size() > 1) {
					response.setIsError(true);
					response.setError("Invalid Second Product Label");
					return res;
				}

				PO po2 = poList2.get(0);

				X_pi_productLabel productlabel2 = new X_pi_productLabel(ctx, po2.get_ID(), trxName);
				productQty2 = productlabel2.getquantity().intValue();
				productId2 = productlabel2.getM_Product_ID();
				if (productId2 == 0) {
					response.setIsError(true);
					response.setError("This Second label does not have a product");
					return res;
				}
				
				if(productId1 != productId2) {
					response.setIsError(true);
					response.setError("The product labels for both products are different");
					return res;
				}
				
				if (productQty2 <= 0) {
					response.setIsError(true);
					response.setError("Second Product Quantity not be Zero and negetive value");
					return res;
				}
				totalQty = productQty2 + quantity;
				productlabel2.setquantity(BigDecimal.valueOf(totalQty));
				productlabel2.saveEx();
				trx.commit();
				
				actualQty = productQty - quantity;
				productlabel.setquantity(BigDecimal.valueOf(actualQty));
				productlabel.saveEx();
				trx.commit();
				
				if(actualQty == 0) {
					productlabel.deleteEx(true);
					trx.commit();
				}
				
				I_M_Product product = new X_M_Product(ctx, productlabel2.getM_Product_ID(), trxName);
				
				response.setProductLabelId(productlabel2.getpi_productLabel_ID());
				response.setProductLabelUUId(productlabel2.getlabeluuid());
				response.setProductName(product.getName());
				response.setQuantity(totalQty);
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

//	@Override
//	public CleanUpDataResponseDocument cleanupData(CleanUpDataRequestDocument req) {
//		CleanUpDataResponseDocument res = CleanUpDataResponseDocument.Factory.newInstance();
//		CleanUpDataResponse response = res.addNewCleanUpDataResponse();
//		CleanUpDataRequest loginRequest = req.getCleanUpDataRequest();
//		ADLoginRequest login = loginRequest.getADLoginRequest();
//		String serviceType = loginRequest.getServiceType().trim();
//		Trx trx = null;
////		int client_id = login.getClientID();
//		try {
//			getCompiereService().connect();
//			String trxName = Trx.createTrxName(getClass().getName() + "_");
//			trx = Trx.get(trxName, true);
//			trx.start();
//
//			org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = RwplUtils.convertAdLogin(login);
//			String err = login(adLoginReq, webServiceName, "cleanup", serviceType);
//			if (err != null && err.length() > 0) {
//				response.setError(err);
//				response.setIsError(true);
//				return res;
//			}
//
//			if (!serviceType.equalsIgnoreCase("cleanup")) {
//				response.setIsError(true);
//				response.setError("Service type " + serviceType + " not configured");
//				return res;
//			}
//			
////			String sqlUpdateInvoices = "UPDATE adempiere.c_invoice SET docstatus = 'DR', docaction = 'CO', processed = 'N' WHERE AD_Client_ID = ?";
////			String sqlDeleteInvoiceLines = "DELETE FROM adempiere.c_invoiceline WHERE c_invoice_id IN (SELECT c_invoice_id FROM adempiere.c_invoice WHERE AD_Client_ID = ?)";
////			String sqlDeleteAllocationLines = "DELETE FROM adempiere.c_allocationline WHERE c_invoice_id IN (SELECT c_invoice_id FROM adempiere.c_invoice WHERE AD_Client_ID = ?)";
////			String sqlDeleteInvoiceTaxes = "DELETE FROM adempiere.c_invoicetax WHERE c_invoice_id IN (SELECT c_invoice_id FROM adempiere.c_invoice WHERE AD_Client_ID = ?)";
////			String sqlUpdatePayments = "UPDATE adempiere.c_invoice SET c_payment_id = NULL WHERE AD_Client_ID = ?";
////			String sqlDeletePayments = "DELETE FROM adempiere.c_payment WHERE c_invoice_id IN (SELECT c_invoice_id FROM adempiere.c_invoice WHERE AD_Client_ID = ?)";
////			String sqlDeleteInvoices = "DELETE FROM adempiere.c_invoice WHERE AD_Client_ID = ?";
////
////			DB.executeUpdateEx(sqlUpdateInvoices, new Object[] { client_id }, trxName);
////			DB.executeUpdateEx(sqlDeleteInvoiceLines, new Object[] { client_id }, trxName);
////			DB.executeUpdateEx(sqlDeleteAllocationLines, new Object[] { client_id }, trxName);
////			DB.executeUpdateEx(sqlDeleteInvoiceTaxes, new Object[] { client_id }, trxName);
////			DB.executeUpdateEx(sqlUpdatePayments, new Object[] { client_id }, trxName);
////			DB.executeUpdateEx(sqlDeletePayments, new Object[] { client_id }, trxName);
////			DB.executeUpdateEx(sqlDeleteInvoices, new Object[] { client_id }, trxName);
////			trx.commit();
////			
////			String sqlUpdateInOut = "UPDATE adempiere.m_inout SET docstatus = 'DR', docaction = 'CO', processed = 'N' WHERE AD_Client_ID = ?";
////			String sqlDeleteMatchPO = "DELETE FROM adempiere.m_matchpo WHERE m_inoutline_id IN (SELECT m_inoutline_id FROM adempiere.m_inoutline WHERE m_inout_id IN (SELECT m_inout_id FROM adempiere.m_inout WHERE AD_Client_ID = ?))";
////			String sqlDeleteMatchInv = "DELETE FROM adempiere.m_matchinv WHERE m_inoutline_id IN (SELECT m_inoutline_id FROM adempiere.m_inoutline WHERE m_inout_id IN (SELECT m_inout_id FROM adempiere.m_inout WHERE AD_Client_ID = ?))";
////			String sqlDeleteTransactions = "DELETE FROM adempiere.m_transaction WHERE m_inoutline_id IN (SELECT m_inoutline_id FROM adempiere.m_inoutline WHERE m_inout_id IN (SELECT m_inout_id FROM adempiere.m_inout WHERE AD_Client_ID = ?))";
////			String sqlDeleteInOutLineMA = "DELETE FROM adempiere.m_inoutlinema WHERE m_inoutline_id IN (SELECT m_inoutline_id FROM adempiere.m_inoutline WHERE m_inout_id IN (SELECT m_inout_id FROM adempiere.m_inout WHERE AD_Client_ID = ?))";
////			String sqlDeletePackline = "DELETE FROM adempiere.m_packline WHERE m_inoutline_id IN (SELECT m_inoutline_id FROM adempiere.m_inoutline WHERE m_inout_id IN (SELECT m_inout_id FROM adempiere.m_inout WHERE AD_Client_ID = ?))";
////			String sqlDeleteProductlabel = "DELETE FROM adempiere.pi_productlabel WHERE m_inoutline_id IN (SELECT m_inoutline_id FROM adempiere.m_inoutline WHERE m_inout_id IN (SELECT m_inout_id FROM adempiere.m_inout WHERE AD_Client_ID = ?))";
////			String sqlDeleteInOutLine = "DELETE FROM adempiere.m_inoutline WHERE m_inout_id IN (SELECT m_inout_id FROM adempiere.m_inout WHERE AD_Client_ID = ?)";
////			String sqlDeleteInOut = "DELETE FROM adempiere.m_inout WHERE AD_Client_ID = ?";
////
////			DB.executeUpdateEx(sqlUpdateInOut, new Object[] { client_id }, trxName);
////			DB.executeUpdateEx(sqlDeleteMatchPO, new Object[] { client_id }, trxName);
////			DB.executeUpdateEx(sqlDeleteMatchInv, new Object[] { client_id }, trxName);
////			DB.executeUpdateEx(sqlDeleteTransactions, new Object[] { client_id }, trxName);
////			DB.executeUpdateEx(sqlDeleteInOutLineMA, new Object[] { client_id }, trxName);
////			DB.executeUpdateEx(sqlDeletePackline, new Object[] { client_id }, trxName);
////			DB.executeUpdateEx(sqlDeleteProductlabel, new Object[] { client_id }, trxName);
////			DB.executeUpdateEx(sqlDeleteInOutLine, new Object[] { client_id }, trxName);
////			DB.executeUpdateEx(sqlDeleteInOut, new Object[] { client_id }, trxName);
////			trx.commit();
////			
////			String sqlUpdateOrders = "UPDATE adempiere.c_order SET docstatus = 'DR', docaction = 'CO', processed = 'N' WHERE AD_Client_ID = ?";
////			String sqlDeleteCostHistory = "DELETE FROM adempiere.m_costhistory WHERE m_costdetail_id IN (" +
////			        "SELECT m_costdetail_id FROM adempiere.m_costdetail " +
////			        "WHERE c_orderline_id IN (" +
////			        "SELECT c_orderline_id FROM adempiere.c_orderline " +
////			        "WHERE c_order_id IN (" +
////			        "SELECT c_order_id FROM adempiere.c_order WHERE AD_Client_ID = ?)))";
////			String sqlDeleteCostDetail = "DELETE FROM adempiere.m_costdetail WHERE c_orderline_id IN (SELECT c_orderline_id FROM adempiere.c_orderline WHERE c_order_id IN (SELECT c_order_id FROM adempiere.c_order WHERE AD_Client_ID = ?))";
////			String sqlDeleteOrderTax = "DELETE FROM adempiere.c_ordertax WHERE c_order_id IN (SELECT c_order_id FROM adempiere.c_order WHERE AD_Client_ID = ?)";
////			String sqlDeleteOrderLines = "DELETE FROM adempiere.c_orderline WHERE c_order_id IN (SELECT c_order_id FROM adempiere.c_order WHERE AD_Client_ID = ?)";
////			String sqlDeleteOrders = "DELETE FROM adempiere.c_order WHERE AD_Client_ID = ?";
////
////			DB.executeUpdateEx(sqlUpdateOrders, new Object[] { client_id }, trxName);
////			DB.executeUpdateEx(sqlDeleteCostHistory, new Object[] { client_id }, trxName);
////			DB.executeUpdateEx(sqlDeleteCostDetail, new Object[] { client_id }, trxName);
////			DB.executeUpdateEx(sqlDeleteOrderTax, new Object[] { client_id }, trxName);
////			DB.executeUpdateEx(sqlDeleteOrderLines, new Object[] { client_id }, trxName);
////			DB.executeUpdateEx(sqlDeleteOrders, new Object[] { client_id }, trxName);
////			trx.commit();
////			
////			String sqlDeletePlanItem = "DELETE FROM adempiere.pi_planitem WHERE pi_salesplanline_id IN (SELECT pi_salesplanline_id FROM adempiere.pi_salesplanline WHERE pi_salesplan_id IN (SELECT pi_salesplan_id FROM adempiere.pi_salesplan WHERE AD_Client_ID = ?))";
////			String sqlDeleteSalesPlanLine = "DELETE FROM adempiere.pi_salesplanline WHERE pi_salesplan_id IN (SELECT pi_salesplan_id FROM adempiere.pi_salesplan WHERE AD_Client_ID = ?)";
////			String sqlDeleteSalesPlan = "DELETE FROM adempiere.pi_salesplan WHERE AD_Client_ID = ?";
////			String sqlDeleteEmail = "DELETE FROM adempiere.pi_email WHERE AD_Client_ID = ?";
////			
////			DB.executeUpdateEx(sqlDeletePlanItem, new Object[] { client_id }, trxName);
////			DB.executeUpdateEx(sqlDeleteSalesPlanLine, new Object[] { client_id }, trxName);
////			DB.executeUpdateEx(sqlDeleteSalesPlan, new Object[] { client_id }, trxName);
////			DB.executeUpdateEx(sqlDeleteEmail, new Object[] { client_id }, trxName);
////			
////			String sqlDeleteUserToken = "DELETE FROM adempiere.pi_userToken WHERE AD_Client_ID = ?";
////			String sqlDeleteProductLabel = "DELETE FROM adempiere.pi_productLabel WHERE AD_Client_ID = ?";
////			DB.executeUpdateEx(sqlDeleteUserToken, new Object[] { client_id }, trxName);
////			DB.executeUpdateEx(sqlDeleteProductLabel, new Object[] { client_id }, trxName);
////			trx.commit();
////
////			String deleteCostHistory = "DELETE FROM adempiere.M_CostHistory " + "WHERE M_CostDetail_ID IN ("
////					+ "SELECT M_CostDetail_ID FROM adempiere.M_CostDetail WHERE AD_Client_ID = ?" + ")";
////			String deleteCostDetail = "DELETE FROM adempiere.M_CostDetail WHERE AD_Client_ID = ?";
////			String deleteAllocations = "DELETE FROM adempiere.M_TransactionAllocation WHERE AD_Client_ID = ?";
////			String sqlDeleteTransaction = "DELETE FROM adempiere.M_Transaction WHERE AD_Client_ID = ?";
////			String sqlDeleteStorage = "DELETE FROM adempiere.M_StorageOnHand WHERE AD_Client_ID = ?";
////			String sqlDeleteStorageReservation = "DELETE FROM adempiere.m_storagereservation WHERE AD_Client_ID = ?";
////
////			DB.executeUpdateEx(deleteCostHistory, new Object[] { client_id }, trxName);
////			DB.executeUpdateEx(deleteCostDetail, new Object[] { client_id }, trxName);
////			DB.executeUpdateEx(deleteAllocations, new Object[] { client_id }, trxName);
////			DB.executeUpdateEx(sqlDeleteTransaction, new Object[] { client_id }, trxName);
////			DB.executeUpdateEx(sqlDeleteStorage, new Object[] { client_id }, trxName);
////			DB.executeUpdateEx(sqlDeleteStorageReservation, new Object[] { client_id }, trxName);
////			trx.commit();
////
////			String sqlDeleteMovementLine = "DELETE FROM adempiere.M_MovementLine WHERE M_Movement_ID IN (SELECT M_Movement_ID FROM adempiere.M_Movement WHERE AD_Client_ID = ?)";
////			String sqlDeleteMovement = "DELETE FROM adempiere.M_Movement WHERE AD_Client_ID = ?";
////
////			DB.executeUpdateEx(sqlDeleteMovementLine, new Object[] { client_id }, trxName);
////			DB.executeUpdateEx(sqlDeleteMovement, new Object[] { client_id }, trxName);
////			trx.commit();
//
//			response.setIsError(false);
//			response.setError("Data Clean Up Successfully");
//		} catch (Exception e) {
//			e.printStackTrace();
//			response.setError(e.getMessage());
//			response.setIsError(true);
//		} finally {
//			if (trx != null) {
//				trx.close();
//			}
//			getCompiereService().disconnect();
//		}
//		return res;
//	}

	@Override
	public StandardResponseDocument removeDamagedQntyFromLabel(UpdateProductLabelRequestDocument req) {
		StandardResponseDocument res = StandardResponseDocument.Factory.newInstance();
		StandardResponse response = res.addNewStandardResponse();
		UpdateProductLabelRequest request = req.getUpdateProductLabelRequest();
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

			org.idempiere.adInterface.x10.ADLoginRequest adLoginReq = RwplUtils.convertAdLogin(loginReq);
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

			String labelUUid = request.getLabelUUID();
			int damagedQuantity = request.getQuantity();

			List<PO> poList = RwplUtils.getPiProductLabelById(labelUUid, ctx, trxName);
			if (poList == null || poList.size() == 0 || poList.size() > 1) {
				response.setIsError(true);
				response.setError("Invalid Product Label");
				return res;
			}

			PO po = poList.get(0);

			X_pi_productLabel productlabel = new X_pi_productLabel(ctx, po.get_ID(), trxName);
			int labelQty = productlabel.getquantity().intValue();
			int productId = productlabel.getM_Product_ID();

			if (productId == 0) {
				response.setIsError(true);
				response.setError("label does not have a product");
				return res;
			}

			if (damagedQuantity <= 0) {
				response.setIsError(true);
				response.setError("Enter Qty not Zero and negetive value");
				return res;
			}

			if (labelQty < damagedQuantity) {
				response.setIsError(true);
				response.setError("The entered quantity must not be greater than the quantity on the product label");
				return res;
			}

			int returnLocatorId = RwplUtils.getLocatorIdByType(ctx, trxName, loginReq.getClientID(), "returns");

			if (returnLocatorId == 0) {
				response.setIsError(true);
				response.setError("return locator not found");
				return res;
			}

			BigDecimal updatedLabelQnty = productlabel.getquantity().subtract(BigDecimal.valueOf(damagedQuantity));

			PiProductLabel newLabel = new PiProductLabel(ctx, trxName, loginReq.getClientID(), loginReq.getOrgID(),
					productlabel.getM_Product_ID(), returnLocatorId, productlabel.getC_OrderLine_ID(),
					productlabel.getM_InOutLine_ID(), false, BigDecimal.valueOf(damagedQuantity), null);

			newLabel.setQcpassed(false);
			newLabel.saveEx();

			if (updatedLabelQnty.intValue() == 0)
				productlabel.delete(true);
			else {
				productlabel.setquantity(updatedLabelQnty);
				productlabel.saveEx();
			}

			trx.commit();

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
}
