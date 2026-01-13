package com.pipra.rwpl.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.compiere.model.I_C_BPartner;
import org.compiere.model.I_C_Order;
import org.compiere.model.I_C_OrderLine;
import org.compiere.model.I_M_InOut;
import org.compiere.model.I_M_InOutLine;
import org.compiere.model.MBPartner;
import org.compiere.model.MBPartnerLocation;
import org.compiere.model.MDocType;
import org.compiere.model.MInOut;
import org.compiere.model.MInOutConfirm;
import org.compiere.model.MInOutLine;
import org.compiere.model.MInOutLineConfirm;
import org.compiere.model.MInventory;
import org.compiere.model.MInventoryLine;
import org.compiere.model.MLocator;
import org.compiere.model.MMovement;
import org.compiere.model.MMovementLine;
import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.compiere.model.MProduct;
import org.compiere.model.MProductCategory;
import org.compiere.model.MTable;
import org.compiere.model.MWarehouse;
import org.compiere.model.PO;
import org.compiere.model.Query;
import org.compiere.process.DocAction;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.KeyNamePair;
import org.compiere.util.Login;
import org.compiere.util.Trx;
import org.pipra.model.custom.PipraUtils;
import org.springframework.util.StringUtils;

import com.pipra.rwpl.entity.model.AdRole_Custom;
import com.pipra.rwpl.entity.model.MInOutLineConfirm_Custom;
import com.pipra.rwpl.entity.model.MInOutLine_Custom;
import com.pipra.rwpl.entity.model.MInOut_Custom;
import com.pipra.rwpl.entity.model.MLocatorType_Custom;
import com.pipra.rwpl.entity.model.MProduct_Custom;
import com.pipra.rwpl.entity.model.Packline;
import com.pipra.rwpl.entity.model.PiProductLabel;
import com.pipra.rwpl.entity.model.PiUserToken;
import com.pipra.rwpl.mode.request.CreateShipmentByLabelRequest;
import com.pipra.rwpl.mode.request.CreateShipmentRequest;
import com.pipra.rwpl.mode.request.EditShipmentLine;
import com.pipra.rwpl.mode.request.EditShipmentRequest;
import com.pipra.rwpl.mode.request.GenerateProductLabelRequest;
import com.pipra.rwpl.mode.request.GetLabelDataRequest;
import com.pipra.rwpl.mode.request.MRCreateRequest;
import com.pipra.rwpl.mode.request.MRFailedRequest;
import com.pipra.rwpl.mode.request.MRLineRequest;
import com.pipra.rwpl.mode.request.PILineRequest;
import com.pipra.rwpl.mode.request.PIQtyChangeRequest;
import com.pipra.rwpl.mode.request.PackLineRequest;
import com.pipra.rwpl.mode.request.PutAwayLineRequest;
import com.pipra.rwpl.mode.request.PutAwayRequest;
import com.pipra.rwpl.mode.request.QcCheckRequest;
import com.pipra.rwpl.mode.request.RemoveDamagedQtyRequest;
import com.pipra.rwpl.mode.request.SetDocActionRequest;
import com.pipra.rwpl.mode.request.ShipmentLine;
import com.pipra.rwpl.mode.request.SplitAndMergeLabelRequest;
import com.pipra.rwpl.mode.request.UpdateProductLabelRequest;
import com.pipra.rwpl.mode.request.UpdateShipmentCustomerRequest;
import com.pipra.rwpl.model.response.BusinessPartnerComponent;
import com.pipra.rwpl.model.response.ClientConfigResponse;
import com.pipra.rwpl.model.response.CreateShipmentByLabelResponse;
import com.pipra.rwpl.model.response.CreateShipmentResponse;
import com.pipra.rwpl.model.response.EditShipmentResponse;
import com.pipra.rwpl.model.response.GenerateProductLabelResponse;
import com.pipra.rwpl.model.response.GetLabelDataResponse;
import com.pipra.rwpl.model.response.LocatorDetailResponse;
import com.pipra.rwpl.model.response.MRComponent;
import com.pipra.rwpl.model.response.MRComponentsResponse;
import com.pipra.rwpl.model.response.MRCreateResponse;
import com.pipra.rwpl.model.response.MRDataResponse;
import com.pipra.rwpl.model.response.MRLineData;
import com.pipra.rwpl.model.response.MRLineResponse;
import com.pipra.rwpl.model.response.MRListResponse;
import com.pipra.rwpl.model.response.PIComponent;
import com.pipra.rwpl.model.response.PIDetailsLine;
import com.pipra.rwpl.model.response.PIDetailsResponse;
import com.pipra.rwpl.model.response.PIListResponse;
import com.pipra.rwpl.model.response.PIQtyChangeResponse;
import com.pipra.rwpl.model.response.PODataResponse;
import com.pipra.rwpl.model.response.POListAccess;
import com.pipra.rwpl.model.response.POListResponse;
import com.pipra.rwpl.model.response.ProductCategoryComponent;
import com.pipra.rwpl.model.response.ProductComponent;
import com.pipra.rwpl.model.response.ProductData;
import com.pipra.rwpl.model.response.ProductLabelLine;
import com.pipra.rwpl.model.response.PutAwayDetailComponent;
import com.pipra.rwpl.model.response.PutAwayDetailResponse;
import com.pipra.rwpl.model.response.PutAwayLabourComponent;
import com.pipra.rwpl.model.response.PutAwayLabourResponse;
import com.pipra.rwpl.model.response.PutAwayListComponent;
import com.pipra.rwpl.model.response.PutAwayListResponse;
import com.pipra.rwpl.model.response.PutAwayResponse;
import com.pipra.rwpl.model.response.QcCheckResponse;
import com.pipra.rwpl.model.response.RemoveDamagedQtyResponse;
import com.pipra.rwpl.model.response.SODetailLocator;
import com.pipra.rwpl.model.response.SODetailProductData;
import com.pipra.rwpl.model.response.SODetailResponse;
import com.pipra.rwpl.model.response.SOListComponent;
import com.pipra.rwpl.model.response.SOListResponse;
import com.pipra.rwpl.model.response.SetDocActionResponse;
import com.pipra.rwpl.model.response.ShipperListResponse;
import com.pipra.rwpl.model.response.SplitAndMergeLabelResponse;
import com.pipra.rwpl.model.response.UpdateProductLabelResponse;
import com.pipra.rwpl.model.response.UpdateShipmentCustomerResponse;
import com.pipra.rwpl.model.response.WarehouseComponent;
import com.pipra.rwpl.service.RwplService;
import com.pipra.rwpl.util.ErrorBuilder;
import com.pipra.rwpl.util.RwplUtils;

public class RwplServiceImpl implements RwplService {

	public static final String ROLE_TYPES_WEBSERVICE = "NULL,WS";

	private @Context HttpServletRequest request = null;

	public RwplServiceImpl() {
	}

	@Override
	public Response poList(String searchKey) {
		POListResponse listResponse = new POListResponse();
		List<POListAccess> poLists = new ArrayList<>();

		PreparedStatement pstm = null;
		ResultSet rs = null;

		try {
			Properties ctx = Env.getCtx();

			int clientId = Env.getAD_Client_ID(ctx);
			int roleId = Env.getAD_Role_ID(ctx);

			List<Integer> orgList = new ArrayList<>();
			Login login = new Login(ctx);
			KeyNamePair[] orgs = login.getOrgs(new KeyNamePair(roleId, ""));
			if (orgs != null) {
				for (KeyNamePair org : orgs) {
					orgList.add(Integer.valueOf(org.getID()));
				}
			}
			String orgIds = orgList.stream().map(Object::toString).collect(Collectors.joining(", "));

			String query = "SELECT DISTINCT po.documentno AS purchase_order, " + "po.c_order_id AS cOrderId, "
					+ "bp.name AS Supplier, " + "po.created, " + "TO_CHAR(po.dateordered, 'DD/MM/YYYY') AS Order_Date, "
					+ "wh.name AS Warehouse_Name, " + "po.description, " + "po.ad_org_id, " + "CASE "
					+ "    WHEN po.docstatus = 'CO' AND mr.m_inout_id IS NULL THEN FALSE "
					+ "    WHEN po.docstatus = 'CO' AND mr.m_inout_id IS NOT NULL THEN TRUE " + "    ELSE FALSE "
					+ "END AS status " + "FROM C_Order po "
					+ "JOIN C_BPartner bp ON po.C_BPartner_ID = bp.C_BPartner_ID "
					+ "JOIN M_Warehouse wh ON po.M_Warehouse_ID = wh.M_Warehouse_ID "
					+ "JOIN C_OrderLine pol ON po.C_Order_ID = pol.C_Order_ID "
					+ "LEFT JOIN M_InOut mr ON po.C_Order_ID = mr.C_Order_ID " + "WHERE po.AD_Client_ID = " + clientId
					+ " " + "AND po.DocStatus = 'CO' " + "AND po.IsSOTrx = 'N' " + "AND pol.QtyOrdered > ( "
					+ "    SELECT COALESCE(SUM(iol.QtyEntered), 0) " + "    FROM M_InOutLine iol "
					+ "    WHERE iol.C_OrderLine_ID = pol.C_OrderLine_ID " + ") " + "AND po.AD_Org_ID IN (" + orgIds
					+ ") " + "AND ( " + "    po.DocumentNo ILIKE '%' || COALESCE(?, po.DocumentNo) || '%' "
					+ "    OR bp.Name ILIKE '%' || COALESCE(?, bp.Name) || '%' "
					+ "    OR wh.Name ILIKE '%' || COALESCE(?, wh.Name) || '%' "
					+ "    OR po.Description ILIKE '%' || COALESCE(?, po.Description) || '%' " + ") "
					+ "ORDER BY po.created DESC";

			pstm = DB.prepareStatement(query.toString(), null);
			pstm.setString(1, searchKey);
			pstm.setString(2, searchKey);
			pstm.setString(3, searchKey);
			pstm.setString(4, searchKey);
			rs = pstm.executeQuery();

			while (rs.next()) {
				POListAccess poItem = new POListAccess();

				poItem.setDocumentNumber(rs.getString("purchase_order"));
				poItem.setcOrderId(rs.getString("cOrderId"));
				poItem.setSupplierName(rs.getString("Supplier"));
				poItem.setOrderDate(rs.getString("Order_Date"));
				poItem.setWarehouseName(rs.getString("Warehouse_Name"));
				poItem.setDescription(rs.getString("description"));

				poLists.add(poItem);
			}

			listResponse.setListAccess(poLists);
			listResponse.setIsError(false);

		} catch (Exception e) {
			listResponse.setError(e.getMessage());
			listResponse.setIsError(true);
			return Response.ok(listResponse).build();

		} finally {

			DB.close(rs, pstm);
		}

		return Response.ok(listResponse).build();
	}

	@Override
	public Response poData(int orderId) {
		PODataResponse listResponse = new PODataResponse();
		List<ProductData> poProducts = new ArrayList<>();

		String trxName = null;

		try {
			Properties ctx = Env.getCtx();

			MOrder mOrder = new MOrder(ctx, orderId, trxName);
			if (mOrder == null || mOrder.get_ID() == 0) {
				return Response.status(Status.NOT_FOUND)
						.entity(new ErrorBuilder().status(Status.NOT_FOUND).title("Order not found").build().toString())
						.build();
			}

			MOrderLine[] lines = mOrder.getLines();
			int overallQnty = 0;

			for (MOrderLine line : lines) {
				if (line.getQtyOrdered().signum() <= 0)
					continue;

				ProductData productData = new ProductData();

				int receivedQty = RwplUtils.getReceivedQty(line, trxName).intValue();
				int outstandingQty = line.getQtyOrdered().intValue() - receivedQty;

				if (outstandingQty > 0) {

					productData.setProductId(line.getM_Product_ID());
					productData.setProductName(line.getProduct().getName());
					productData.setcOrderlineId(line.getC_OrderLine_ID());
					productData.setUomId(line.getC_UOM_ID());
					productData.setOutstandingQnty(outstandingQty);
					productData.setTotalQuantity(line.getQtyOrdered().intValue());

					productData.setSuggestedLocator(0);
					productData.setLocatorName(null);

					overallQnty += outstandingQty;
					poProducts.add(productData);
				}
			}

			listResponse.setOrderDate(RwplUtils.convertTimestampToDate(mOrder.getDateOrdered()));

			listResponse.setcOrderId(mOrder.getC_Order_ID());
			listResponse.setDocumentNo(mOrder.getDocumentNo());
			listResponse.setSupplier(mOrder.getC_BPartner().getName());
			listResponse.setDocstatus(mOrder.getDocStatus());
			listResponse.setWarehouseName(mOrder.getM_Warehouse().getName());

			String description = mOrder.getDescription();
			listResponse.setDescription(description == null ? "" : description);

			listResponse.setOverallQnty(overallQnty);
			listResponse.setProductData(poProducts);

			List<MLocator> locators = MLocatorType_Custom.getLocatorsByType(ctx, trxName, mOrder.getM_Warehouse_ID(),
					"receiving", "Y");
			if (locators != null && locators.size() != 0)
				listResponse.setDefaultLocatorId(locators.get(0).get_ID());

			boolean hasShipment = new Query(ctx, "M_InOut", "C_Order_ID=?", trxName)
					.setParameters(mOrder.getC_Order_ID()).match();

			boolean orderStatus = mOrder.isProcessed() && mOrder.getDocStatus().equals(MOrder.DOCSTATUS_Completed)
					&& hasShipment;

			listResponse.setOrderStatus(orderStatus);
			listResponse.setError(false);

			return Response.ok(listResponse).build();

		} catch (Exception e) {
			listResponse.setError("Error processing order data: " + e.getMessage());
			listResponse.setError(true);
			return Response.serverError().entity(listResponse).build();

		}
	}

	@Override
	public Response getMRList() {
		List<Map<String, Object>> resultList = new ArrayList<>();

		String whereClause = "Processed='Y'"; // example condition
		MInOut[] mrs = new Query(Env.getCtx(), MInOut.Table_Name, whereClause, null).setOrderBy("MovementDate DESC")
				.list().toArray(new MInOut[0]);

		for (MInOut mr : mrs) {
			Map<String, Object> data = new HashMap<>();
			data.put("DocumentNo", mr.getDocumentNo());
			data.put("MR_ID", mr.get_ID());
			data.put("Date", mr.getMovementDate());
			resultList.add(data);
		}

		return Response.ok(resultList).build();
	}

	@Override
	public Response getMRComponents() {
		MRComponentsResponse responseModel = new MRComponentsResponse();

		try {
			Properties ctx = Env.getCtx();
			int clientId = Env.getAD_Client_ID(ctx);

			List<MWarehouse> warehouses = new Query(ctx, MWarehouse.Table_Name, "IsActive='Y' AND ad_client_id=?", null)
					.setParameters(clientId).setOrderBy("Name ASC").list();
			List<WarehouseComponent> warehouseList = new ArrayList<>();

			for (MWarehouse wh : warehouses) {
				WarehouseComponent whComp = new WarehouseComponent();
				whComp.setWarehouseId(wh.get_ID());
				whComp.setWarehouse(wh.getName());

				int defaultLocatorId = 0;
				List<MLocator> locators = MLocatorType_Custom.getLocatorsByType(ctx, null, wh.get_ID(), "receiving",
						"Y");
				if (locators != null && !locators.isEmpty()) {
					defaultLocatorId = locators.get(0).get_ID();
				}
				whComp.setDefaultLocatorId(defaultLocatorId);

				warehouseList.add(whComp);
			}
			responseModel.setWarehouse(warehouseList);

			// --- 2. Fetch and Map Product Categories ---
			List<MProductCategory> categories = new Query(ctx, MProductCategory.Table_Name,
					"IsActive='Y' AND ad_client_id=?", null).setParameters(clientId).setOrderBy("Name ASC").list();
			List<ProductCategoryComponent> categoryList = new ArrayList<>();

			for (MProductCategory cat : categories) {
				ProductCategoryComponent catComp = new ProductCategoryComponent();
				catComp.setProductCategoryId(cat.getM_Product_Category_ID());
				catComp.setProductCategoryName(cat.getName());
				categoryList.add(catComp);
			}
			responseModel.setProductCategory(categoryList);

			// --- 3. Fetch and Map Products ---
			// NOTE: MProduct_Custom is a custom model, ensure it extends MProduct or
			// implements necessary getters
			List<PO> products = new Query(ctx, MProduct_Custom.Table_Name, "IsActive='Y' AND ad_client_id=?", null)
					.setParameters(clientId).setOrderBy("Name ASC").list();
			List<ProductComponent> productList = new ArrayList<>();

			for (PO prds : products) {
				// Instantiate the custom model again to access methods
				MProduct_Custom prd = new MProduct_Custom(ctx, prds.get_ID(), null);
				ProductComponent prdComp = new ProductComponent();

				prdComp.setProductId(prd.get_ID());
				prdComp.setProductName(prd.getName());
				prdComp.setUomId(prd.getC_UOM_ID());
				prdComp.setUomName(prd.getC_UOM() != null ? prd.getC_UOM().getName() : "");

				// Assuming getLabelQnty() returns BigDecimal
				prdComp.setLabelQnty(prd.getLabelQnty() != null ? prd.getLabelQnty().intValue() : 0);

				prdComp.setProductCategoryId(prd.getM_Product_Category_ID());
				prdComp.setProductCategoryName(
						prd.getM_Product_Category() != null ? prd.getM_Product_Category().getName() : "");
				productList.add(prdComp);
			}
			responseModel.setProduct(productList);

			// --- 4. Fetch and Map Business Partners ---
			List<MBPartner> bpartners = new Query(ctx, MBPartner.Table_Name, "IsActive='Y' AND ad_client_id=?", null)
					.setParameters(clientId).setOrderBy("Name ASC").list();
			List<BusinessPartnerComponent> bpList = new ArrayList<>();

			for (MBPartner bp : bpartners) {
				BusinessPartnerComponent bpComp = new BusinessPartnerComponent();
				bpComp.setBusinessPartnerID(bp.get_ID());
				bpComp.setBusinessPartnerName(bp.getName());

				// Get Primary Location Address Name
				bpComp.setAddress(
						bp.getPrimaryC_BPartner_Location() != null ? bp.getPrimaryC_BPartner_Location().getName() : "");
				bpList.add(bpComp);
			}
			responseModel.setBusinessPartner(bpList);

			// Success: set IsError to false (already default, but explicit is fine)
			responseModel.setError(false);

			// Return 200 OK with the clean model
			return Response.ok(responseModel).build();

		} catch (Exception e) {
			// Error: set IsError to true and populate the message
			responseModel.setError(true);
			responseModel.setErrorMessage(e.getMessage());

			// Return 500 Internal Server Error with the error response model
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();
		}
	}

	@Override
	public Response roleConfig(String deviceToken) {
		Map<String, Object> responseMap = new HashMap<>();
		List<Map<String, Object>> appAccessList = new ArrayList<>();

		Trx trx = null;

		try {
			Properties ctx = Env.getCtx();
			int userId = Env.getAD_User_ID(ctx);
			int roleId = Env.getAD_Role_ID(ctx);

			String trxName = null;
			trx = Trx.get(trxName, true);
			trx.start();

			AdRole_Custom adRole = new AdRole_Custom(ctx, roleId, trxName, null);

			appAccessList.add(Map.of("appName", "recieveApp", "appAcess", adRole.isPurchaseOrder()));
			appAccessList.add(Map.of("appName", "materialReceipt", "appAcess", adRole.isMaterialReceipt()));
			appAccessList.add(Map.of("appName", "stockCheckApp", "appAcess", adRole.isPhysicalInventory()));
			appAccessList.add(Map.of("appName", "pickList", "appAcess", adRole.isSaleOrder()));
			appAccessList.add(Map.of("appName", "dispatchApp", "appAcess", adRole.isShipmentCustomer()));
			appAccessList.add(Map.of("appName", "addInward", "appAcess", adRole.isAddInward()));
			appAccessList.add(Map.of("appName", "ispickbyorder", "appAcess", adRole.ispickbyorder()));
			appAccessList.add(Map.of("appName", "mergeApp", "appAcess", adRole.ismergeapp()));
			appAccessList.add(Map.of("appName", "splitApp", "appAcess", adRole.issplitapp()));
			appAccessList.add(Map.of("appName", "labourPutaway", "appAcess", adRole.isLabourPutaway()));
			appAccessList.add(Map.of("appName", "labourPicklist", "appAcess", adRole.isLabourPicklist()));
			appAccessList.add(Map.of("appName", "labourInventorymove", "appAcess", adRole.isLabourInventorymove()));
			appAccessList.add(Map.of("appName", "qcCheckApp", "appAcess", adRole.isQcCheckApp()));
			appAccessList.add(Map.of("appName", "supervisorPutaway", "appAcess", adRole.isMaterialReceipt()));

			responseMap.put("appAcess", appAccessList);

			// Save device token if not exists
			boolean flag = true;
			if (deviceToken != null && !deviceToken.isEmpty()) {
				flag = PiUserToken.checkTokenExistForuser(deviceToken, userId, ctx, trxName);
			}
			if (!flag) {
				PiUserToken piUserToken = new PiUserToken(ctx, 0, trxName);
				piUserToken.setAD_User_ID(userId);
				piUserToken.setdevicetoken(deviceToken);
				piUserToken.saveEx();
			}

			trx.commit();
			return Response.ok(Collections.singletonMap("RoleConfigureResponse", responseMap)).build();

		} catch (Exception e) {
			if (trx != null)
				trx.rollback();
			Map<String, Object> errorResp = new HashMap<>();
			errorResp.put("IsError", true);
			errorResp.put("ErrorMessage", e.getMessage());
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(Collections.singletonMap("RoleConfigureResponse", errorResp)).build();
		} finally {
			if (trx != null)
				trx.close();
		}
	}

	@Override
	public Response createMR(MRCreateRequest request) {

		MRCreateResponse responseModel = new MRCreateResponse();
		responseModel.setError(false);

		Properties ctx = Env.getCtx();
		MInOut inout = null;
		String trxName = null;

		try {
			String movementDateStr = request.getMovementDate();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
			Date parsedDate = dateFormat.parse(movementDateStr);
			Timestamp movementDate = new Timestamp(parsedDate.getTime());

			int clientId = Env.getAD_Client_ID(ctx);
			int orgId = Env.getAD_Org_ID(ctx);
			int userId = Env.getAD_User_ID(ctx);

			MDocType mDocType = new Query(ctx, MDocType.Table_Name, "name = 'MM Receipt' AND ad_client_id = ?", trxName)
					.setParameters(clientId).first();

			if (mDocType == null) {
				throw new Exception("Document Type 'MM Receipt' not found");
			}

			List<MRLineResponse> responseLines = new ArrayList<>();

			int cOrderId = request.getcOrderId();

			if (cOrderId == 0) {
				inout = new MInOut_Custom(ctx, trxName, clientId, orgId, userId, request.getbPartnerId(),
						request.getWarehouseId(), mDocType, movementDate, request.getDescription());
				inout.saveEx();

				for (MRLineRequest lineData : request.getMrLines()) {
					BigDecimal qtyEntered = BigDecimal.valueOf(lineData.getQnty());

					MInOutLine iol = new MInOutLine(ctx, 0, trxName);
					iol.setM_InOut_ID(inout.getM_InOut_ID());
					iol.setM_Product_ID(lineData.getProductId(), lineData.getUomId());
					iol.setQty(qtyEntered);
					iol.setMovementQty(qtyEntered);
					iol.setC_UOM_ID(lineData.getUomId());
					iol.setM_Warehouse_ID(request.getWarehouseId());
					iol.setM_Locator_ID(lineData.getLocator());
					iol.saveEx();

					if (lineData.getPackLine() != null && !lineData.getPackLine().isEmpty()) {
						createPackLineforInoutLine(ctx, trxName, lineData.getPackLine(), lineData.getQnty(),
								iol.get_ID());
					}

					MRLineResponse responseLine = new MRLineResponse();
					responseLine.setMrLineId(iol.get_ID());
					responseLine.setProductId(lineData.getProductId());
					responseLine.setUomId(lineData.getUomId());
					responseLine.setQnty(lineData.getQnty());
					responseLine.setLocator(lineData.getLocator());
					responseLines.add(responseLine);
				}
			} else {
				MOrder order = new MOrder(ctx, cOrderId, trxName);

				if (order.get_ID() == 0) {
					throw new Exception("Order not found with ID: " + cOrderId);
				}

				MInOut receipt = new MInOut(order, mDocType.get_ID(), order.getDateOrdered());
				receipt.setDocStatus(DocAction.STATUS_Drafted);
				receipt.saveEx();

				inout = receipt;

				for (MRLineRequest lineData : request.getMrLines()) {
					BigDecimal qtyEntered = BigDecimal.valueOf(lineData.getQnty());
					int cOrderlineId = lineData.getcOrderlineId();

					inout.createLineFrom(cOrderlineId, 0, 0, lineData.getProductId(), lineData.getUomId(), qtyEntered,
							lineData.getLocator());
					MInOutLine[] lineArray = MInOutLine.get(ctx, cOrderlineId, trxName);
					MInOutLine line = lineArray[lineArray.length - 1];

					Packline.deletepackLine(ctx, trxName, line.get_ID());

					if (lineData.getPackLine() != null && !lineData.getPackLine().isEmpty()) {
						createPackLineforInoutLine(ctx, trxName, lineData.getPackLine(), lineData.getQnty(),
								line.get_ID());
					}

					MRLineResponse responseLine = new MRLineResponse();
					responseLine.setMrLineId(line.get_ID());
					responseLine.setProductId(lineData.getProductId());
					responseLine.setUomId(lineData.getUomId());
					responseLine.setQnty(lineData.getQnty());
					responseLine.setLocator(lineData.getLocator());
					responseLines.add(responseLine);
				}

				inout.updateFrom(order, null, null);
			}

			responseModel.setMrDocumentNumber(inout.getDocumentNo());
			responseModel.setMrId(inout.get_ID());
			responseModel.setMrLines(responseLines);
			responseModel.setError(false);

			RwplUtils.sendNotificationAsync(true, false, inout.get_Table_ID(), inout.getM_InOut_ID(), ctx, trxName,
					"New Inward: " + inout.getDocumentNo() + "",
					" Inward - " + inout.getDocumentNo() + " added to process", inout.get_TableName(), null, clientId,
					"MaterialReciptCreated");

			return Response.ok(responseModel).build();

		} catch (Exception e) {

			responseModel.setError(true);
			responseModel.setErrorMessage("Transaction rolled back: " + e.getMessage());

			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();
		}
	}

	private void createPackLineforInoutLine(Properties ctx, String trxName, List<PackLineRequest> packlineArray,
			int qnty, int inoutLineId) {

		int packCount = 1;
		int remainingQty = qnty;

		for (PackLineRequest packline : packlineArray) {
			int packQty = packline.getPackCount();

			createPackLine(ctx, trxName, inoutLineId, packCount, packQty);
			remainingQty -= packQty;
			packCount++;
		}

		if (remainingQty > 0) {
			createPackLine(ctx, trxName, inoutLineId, packCount, remainingQty);
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
	public Response getMRList(String searchKey, String status, String isSalesTransaction) {

		MRListResponse responseModel = new MRListResponse();
		responseModel.setError(false);
		List<MRComponent> mrComponents = new ArrayList<>();

		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			Properties ctx = Env.getCtx();
			int clientId = Env.getAD_Client_ID(ctx);

			String query = "SELECT po.m_inout_id, po.documentno, bp.name AS supplier, wh.name AS warehouseName, po.pickStatus "
					+ "FROM m_inout po " + "JOIN c_bpartner bp ON po.c_bpartner_id = bp.c_bpartner_id "
					+ "JOIN m_warehouse wh ON po.m_warehouse_id = wh.m_warehouse_id " + "WHERE po.ad_client_id = ? "
					+ "AND (? IS NULL OR po.pickStatus = ?) " + "AND (? IS NULL OR po.issotrx = ?) AND po.docstatus = 'DR' "
					+ "AND (? IS NULL OR po.documentno ILIKE '%' || ? || '%') ORDER BY po.created DESC";

			pstmt = DB.prepareStatement(query, null);

			int index = 1;
			pstmt.setInt(index++, clientId);

			pstmt.setString(index++, status);
			pstmt.setString(index++, status);

			pstmt.setString(index++, isSalesTransaction);
			pstmt.setString(index++, isSalesTransaction);

			pstmt.setString(index++, searchKey);
			pstmt.setString(index++, searchKey);

			rs = pstmt.executeQuery();

			while (rs.next()) {
				MRComponent mr = new MRComponent();

				int mInoutId = rs.getInt("m_inout_id");
				MInOut inOut = new MInOut(ctx, mInoutId, null);

				mr.setmInoutID(mInoutId);
				mr.setDocumentNo(rs.getString("documentno"));
				mr.setSupplier(rs.getString("supplier"));
				mr.setWarehouseName(rs.getString("warehouseName"));
				mr.setPickStatus(rs.getString("pickStatus"));
				mr.setOrderDate(inOut.getCreated());
				mr.setDescription(inOut.getDescription());
				
				String orderDocumentNo = inOut.getC_Order() != null ? inOut.getC_Order().getDocumentNo() : null;
				mr.setOrderDocumentno(orderDocumentNo);
				
				mrComponents.add(mr);
			}

			responseModel.setMrList(mrComponents);
			responseModel.setCount(mrComponents.size());

			return Response.ok(responseModel).build();

		} catch (Exception e) {
			responseModel.setError(true);
			responseModel.setErrorMessage(e.getMessage());

			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();

		} finally {
			DB.close(rs, pstmt);
		}
	}

	@Override
	public Response getMrData(int id) {

		MRDataResponse responseModel = new MRDataResponse();
		responseModel.setError(false);
		List<MRLineData> lineList = new ArrayList<>();

		String trxName = null;
		int totalQty = 0;

		try {
			Properties ctx = Env.getCtx();

			MInOut mr = new MInOut(ctx, id, trxName);

			if (mr == null || mr.get_ID() == 0) {
				responseModel.setError(true);
				responseModel.setErrorMessage("Material Receipt (M_InOut_ID: " + id + ") not found.");
				return Response.status(Status.NOT_FOUND).entity(responseModel).build();
			}

			responseModel.setmInoutID(mr.getM_InOut_ID());
			responseModel.setDocumentNo(mr.getDocumentNo());
			responseModel.setDescription(mr.getDescription() != null ? mr.getDescription() : "");
			responseModel.setDocStatus(mr.getDocStatus());
			responseModel.setMovementDate(mr.getMovementDate());
			
			String orderDocumentNo = mr.getC_Order() != null ? mr.getC_Order().getDocumentNo() : null;
			responseModel.setOrderDocumentno(orderDocumentNo);
			
			if (mr.getC_BPartner_ID() > 0) {
				MBPartner bp = new MBPartner(ctx, mr.getC_BPartner_ID(), trxName);
				responseModel.setSupplier(bp.getName());
				MBPartnerLocation[] locations = bp.getLocations(false);
				if (locations != null && locations.length > 0) {
					MBPartnerLocation l = locations[0];
					responseModel.setSupplierLocationName(l.getName());
				}
			}

			if (mr.getM_Warehouse_ID() > 0) {
				MWarehouse wh = new MWarehouse(ctx, mr.getM_Warehouse_ID(), trxName);
				responseModel.setWarehouseName(wh.getName());
			}

			MInOutLine[] lines = mr.getLines();

			for (MInOutLine line : lines) {
				MRLineData lineData = new MRLineData();

				MProduct product = MProduct.get(ctx, line.getM_Product_ID());
				lineData.setProductID(line.getM_Product_ID());
				lineData.setProductName(product.getName());

				MLocator locator = new MLocator(ctx, line.getM_Locator_ID(), trxName);
				lineData.setLocatorID(line.getM_Locator_ID());
				lineData.setLocatorName(locator.getValue());

				lineData.setmInoutLineID(line.getM_InOutLine_ID());
				lineData.setMovementQty(line.getMovementQty());
				lineData.setUomID(line.getC_UOM_ID());
				lineData.setcOrderLineID(line.getC_OrderLine_ID());

				totalQty += line.getMovementQty().intValue();

				lineList.add(lineData);
			}

			responseModel.setTotalQty(totalQty);
			responseModel.setLines(lineList);
			responseModel.setLineCount(lineList.size());

			responseModel.setError(false);

			return Response.ok(responseModel).build();

		} catch (Exception e) {
			responseModel.setError(true);
			responseModel.setErrorMessage("Error processing MR data: " + e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();

		}
	}

	private boolean matchesSearchKey(I_M_InOut inout, String searchKey) {
		if (StringUtils.isEmpty(searchKey))
			return true;
		String keyLower = searchKey.toLowerCase();

		if (!StringUtils.isEmpty(inout.getDocumentNo()) && inout.getDocumentNo().toLowerCase().contains(keyLower))
			return true;
		if (inout.getC_BPartner() != null && !StringUtils.isEmpty(inout.getC_BPartner().getName())
				&& inout.getC_BPartner().getName().toLowerCase().contains(keyLower))
			return true;
		if (inout.getM_Warehouse() != null && !StringUtils.isEmpty(inout.getM_Warehouse().getName())
				&& inout.getM_Warehouse().getName().toLowerCase().contains(keyLower))
			return true;
		if (!StringUtils.isEmpty(inout.getDescription()) && inout.getDescription().toLowerCase().contains(keyLower))
			return true;

		return false;
	}

	public Response getPutAwayList(String searchKey) {
		PutAwayListResponse responseModel = new PutAwayListResponse();
		LinkedHashMap<Integer, PutAwayListComponent> processedMInouts = new LinkedHashMap<>();
		String trxName = null;

		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			Properties ctx = Env.getCtx();
			int clientId = Env.getAD_Client_ID(ctx);
			int warehouseId = Env.getContextAsInt(ctx, Env.M_WAREHOUSE_ID);
			int roleId = Env.getAD_Role_ID(ctx);

			List<Integer> orgList = new ArrayList<>();
			Login login = new Login(ctx);
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
					+ "    po.description, po.M_Warehouse_ID,  po.created, po.m_inout_id,\n"
					+ "	co.documentno as orderDocumentno\n" + "FROM m_inout po\n"
					+ "JOIN c_bpartner bp ON po.c_bpartner_id = bp.c_bpartner_id \n"
					+ "LEFT JOIN c_order co on co.c_order_id = po.c_order_id\n"
					+ "JOIN m_warehouse wh ON po.m_warehouse_id = wh.m_warehouse_id\n" + "WHERE po.ad_client_id = "
					+ clientId + " AND po.issotrx = 'N' AND po.docstatus = 'DR' AND po.ad_org_id IN (" + orgIds
					+ ") AND po.ad_orgtrx_id is null " + "AND (\n"
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

			while (rs.next()) {
				PutAwayListComponent item = new PutAwayListComponent();
				int mInoutID = rs.getInt("mInoutID");

				item.setDocumentNo(rs.getString("documentNo"));
				item.setMInoutID(mInoutID);
				item.setSupplier(rs.getString("Supplier"));
				item.setOrderDate(rs.getString("Order_Date"));
				item.setWarehouseId(rs.getInt("M_Warehouse_ID"));
				item.setWarehouseName(rs.getString("Warehouse_Name"));
				item.setOrderDocumentno(
						StringUtils.isEmpty(rs.getString("orderDocumentno")) ? "" : rs.getString("orderDocumentno"));
				item.setToMarkForPutAway(true);
				item.setDescription(rs.getString("description"));

				int totalQuantity = MInOut_Custom.getTotalQuantityForMInout(ctx, trxName, mInoutID);
				item.setTotalQuantity(totalQuantity);
				item.setQuantityToPick(totalQuantity); // For Draft, all is pending

				processedMInouts.put(mInoutID, item);
			}

			List<MLocator> locators = MLocatorType_Custom.getLocatorsByType(ctx, trxName, warehouseId, "receiving",
					"Y");

			if (locators != null && !locators.isEmpty()) {

				String locatorIds = locators.stream().map(locator -> String.valueOf(locator.get_ID()))
						.collect(Collectors.joining(","));
				String piWhereClause = "M_Locator_ID IN (" + locatorIds + ") AND AD_Client_ID=?";

				List<PO> piLabels = new Query(ctx, PiProductLabel.Table_Name, piWhereClause, trxName)
						.setParameters(clientId).setOrderBy(PiProductLabel.COLUMNNAME_pi_productLabel_ID).list();

				for (PO po : piLabels) {
					PiProductLabel piProductLabel = new PiProductLabel(ctx, po.get_ID(), trxName);
					I_M_InOutLine inoutLine = piProductLabel.getM_InOutLine();
					if (inoutLine == null || inoutLine.getM_InOutLine_ID() == 0)
						continue;

					I_M_InOut mInout = inoutLine.getM_InOut();

					if (mInout != null && DocAction.STATUS_Completed.equalsIgnoreCase(mInout.getDocStatus())) {

						if (!matchesSearchKey(mInout, searchKey))
							continue;

						int mInoutID = mInout.getM_InOut_ID();
						int quantity = piProductLabel.getquantity().intValue();

						PutAwayListComponent item = processedMInouts.get(mInoutID);

						if (item != null) {
							item.setQuantityToPick(item.getQuantityToPick() + quantity);
							item.setToMarkForPutAway(false);
						} else {
							item = new PutAwayListComponent();
							item.setDocumentNo(mInout.getDocumentNo());
							item.setMInoutID(mInoutID);
							item.setSupplier(mInout.getC_BPartner().getName());
							item.setOrderDate(RwplUtils.formatTimestamp(mInout.getDateOrdered()));
							item.setWarehouseId(mInout.getM_Warehouse_ID());
							item.setWarehouseName(mInout.getM_Warehouse().getName());
							item.setOrderDocumentno(
									mInout.getC_Order() != null ? mInout.getC_Order().getDocumentNo() : "");
							item.setToMarkForPutAway(false);
							item.setQuantityToPick(quantity);

							int totalQuantity = MInOut_Custom.getTotalQuantityForMInout(ctx, trxName, mInoutID);
							item.setTotalQuantity(totalQuantity);
							item.setDescription(mInout.getDescription());

						}
						processedMInouts.put(mInoutID, item);
					}
				}
			} else if (processedMInouts.isEmpty()) {
				responseModel.setError(true);
				responseModel.setErrorMessage("Receiving Locator Not Found");
				return Response.ok(responseModel).build();
			}

			responseModel.setPutAwayList(new ArrayList<>(processedMInouts.values()));
			responseModel.setCount(processedMInouts.size());

			return Response.ok(responseModel).build();

		} catch (Exception e) {

			responseModel.setError(true);
			e.printStackTrace();
			responseModel.setErrorMessage(e.getMessage() != null ? e.getMessage() : "An unexpected error occurred.");
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();

		} finally {
			DB.close(rs, pstm);
		}
	}

	@Override
	public Response generateProductLabel(GenerateProductLabelRequest request) {
		GenerateProductLabelResponse responseModel = new GenerateProductLabelResponse();
		Trx trx = null;

		try {
			Properties ctx = Env.getCtx();
			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();

			List<ProductLabelLine> productLabelLines = request.getProductLabelLine();

			if (request.isFinalDispatch()) {
				int scannedQnty = productLabelLines.stream().mapToInt(ProductLabelLine::getQuantity).sum();
				int orderQnty = 0;

				MInOut_Custom inOut = new MInOut_Custom(ctx, request.getmInoutId(), trxName);
				MInOutLine[] inoutLines = inOut.getLines();

				for (MInOutLine inoutLine : inoutLines) {
					orderQnty += inoutLine.getQtyEntered().intValue();
				}

				if (orderQnty != scannedQnty) {
					responseModel.setError(true);
					responseModel.setError("Scanned Quantity is not matching with shipment quantity");
					return Response.status(Status.BAD_REQUEST).entity(responseModel).build();
				}
			}

			for (ProductLabelLine line : productLabelLines) {
				if (request.isFinalDispatch()) {
					createSalesLabel(line, ctx, trxName, request.getmInoutId());

				} else {

					PiProductLabel piProductLabel = new PiProductLabel(ctx, 0, trxName);
					piProductLabel.setM_Product_ID(line.getProductId());
					piProductLabel.setquantity(BigDecimal.valueOf(line.getQuantity()));
					piProductLabel.setM_Locator_ID(line.getLocatorId());
					piProductLabel.setIsSOTrx(line.isSalesTransaction());

					piProductLabel.saveEx();

					line.setLabelId(piProductLabel.get_ID());
					line.setProductName(piProductLabel.getM_Product().getName());
					line.setProductLabelUUId(piProductLabel.getlabeluuid());
				}
			}

			trx.commit();
			responseModel.setError(false);
			responseModel.setProductLabelLine(productLabelLines);
			return Response.ok(responseModel).build();

		} catch (Exception e) {
			if (trx != null) {
				trx.rollback();
			}
			responseModel.setError(true);
			responseModel.setError("Error generating product label: " + e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();

		} finally {
			if (trx != null) {
				trx.close();
			}
		}
	}

	private void createSalesLabel(ProductLabelLine line, Properties ctx, String trxName, int mInoutId) {

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
	public Response getLabelData(GetLabelDataRequest request) {

		GetLabelDataResponse responseModel = new GetLabelDataResponse();

		String labelUUID = request.getLabelUUID();
		boolean isPutawy = request.isPutAway();
		boolean internalMove = request.isInternalMove();
		boolean receiving = request.isReceiving();
		boolean pickList = request.isPickList();
		boolean finalDispatch = request.isFinalDispatch();
		boolean labelAvailableInWarehouse = request.isLabelAvailableInWarehouse();

		Properties ctx = Env.getCtx();
		String trxName = null;

		try {

			String columnName = "labelUUId";
			List<PO> poList = PiProductLabel.getPiProductLabel(columnName, labelUUID, ctx, trxName, null);

			if (poList.isEmpty()) {
				responseModel.setError(true);
				responseModel.setError("Invalid label");
				return Response.status(Status.OK).entity(responseModel).build();
			}

			List<ProductLabelLine> responseLines = new ArrayList<>();
			int labelCount = 0;

			for (PO po : poList) {
				PiProductLabel label = new PiProductLabel(ctx, po.get_ID(), trxName);
				I_M_InOut mInout = label.getM_InOutLine().getM_InOut();

				MLocatorType_Custom locatorType_Custom = new MLocatorType_Custom(ctx,
						label.getM_Locator().getM_LocatorType_ID(), trxName);

				if (isPutawy && !mInout.getDocStatus().equalsIgnoreCase("CO")) {
					responseModel.setError(true);
					responseModel.setError("Material receipt is not completed, can't do putaway");
					return Response.status(Status.OK).entity(responseModel).build();
				}

				if (receiving && !locatorType_Custom.isReceiving()) {
					responseModel.setError(true);
					responseModel.setError("Label is Not in Receiving Area");
					return Response.status(Status.OK).entity(responseModel).build();
				}

				if ((receiving || pickList || internalMove) && label.isSOTrx()) {
					responseModel.setError(true);
					responseModel.setError("Label is Already Dispatched");
					return Response.status(Status.OK).entity(responseModel).build();
				}

				if (pickList && (locatorType_Custom.isdispatch() || locatorType_Custom.isReturns())) {
					responseModel.setError(true);
					responseModel.setError("Label is in Dispatch/ Returns Area");
					return Response.status(Status.OK).entity(responseModel).build();
				}

				if (finalDispatch && !label.isfinaldispatch()) {
					responseModel.setError(true);
					responseModel.setError("Label is Not ready for Final Dispatch");
					return Response.status(Status.OK).entity(responseModel).build();
				}

				if (labelAvailableInWarehouse && label.isSOTrx()) {
					responseModel.setError(true);
					responseModel.setError("Label is Already Dispatched");
					return Response.status(Status.OK).entity(responseModel).build();
				}

				ProductLabelLine labelLine = new ProductLabelLine();
				labelLine.setLabelId(label.getpi_productLabel_ID());
				labelLine.setProductLabelUUId(label.getlabeluuid());
				labelLine.setcOrderlineId(label.getC_OrderLine_ID());
				labelLine.setmInoutlineId(label.getM_InOutLine_ID());
				labelLine.setProductId(label.getM_Product_ID());
				labelLine.setProductName(label.getM_Product().getName());
				labelLine.setLocatorId(label.getM_Locator_ID());
				labelLine.setLocatorName(label.getM_Locator().getValue());
				labelLine.setQuantity(label.getquantity().intValue());
				labelLine.setSalesTransaction(label.isSOTrx());
				labelLine.setQcPassed(label.qcpassed());
				labelLine.setWarehouseId(label.getM_Locator().getM_Warehouse_ID());
				labelLine.setWarehouseName(label.getM_Locator().getM_Warehouse().getName());
				labelLine.setFinalDispatch(label.isfinaldispatch());

				responseLines.add(labelLine);
				labelCount++;
			}

			responseModel.setProductCount(labelCount);
			responseModel.setLabelUUID(labelUUID);
			responseModel.setLabelLine(responseLines);
			responseModel.setError(false);

			return Response.ok(responseModel).build();

		} catch (Exception e) {

			responseModel.setError("Error retrieving label data: " + e.getMessage());
			responseModel.setError(true);
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();
		}
	}

	@Override
	public Response getPhysicalInventoryList(String searchKey) {
		PIListResponse responseModel = new PIListResponse();
		List<PIComponent> piComponents = new ArrayList<>();

		try {
			Properties ctx = Env.getCtx();
			int clientId = Env.getAD_Client_ID(ctx);
			int roleId = Env.getAD_Role_ID(ctx);

			List<Integer> orgList = new ArrayList<>();
			Login login = new Login(ctx);
			KeyNamePair[] orgs = login.getOrgs(new KeyNamePair(roleId, ""));
			if (orgs != null) {
				for (KeyNamePair org : orgs) {
					orgList.add(Integer.valueOf(org.getID()));
				}
			}

			String whereClause = "AD_Client_ID=? AND AD_Org_ID IN ("
					+ orgList.stream().map(Object::toString).collect(Collectors.joining(", ")) + ")";
			List<Object> params = new ArrayList<>();
			params.add(clientId);

			if (searchKey != null && !searchKey.trim().isEmpty()) {
				whereClause += " AND DocumentNo ILIKE '%' || ? || '%'";
				params.add(searchKey);
			}

			List<MInventory> inventories = new Query(ctx, MInventory.Table_Name, whereClause, null)
					.setParameters(params.toArray()).setOrderBy("Created DESC").list();

			for (MInventory inventory : inventories) {
				PIComponent pi = new PIComponent();
				pi.setmInventoryId(inventory.getM_Inventory_ID());
				pi.setDocumentNo(inventory.getDocumentNo());
				pi.setDescription(inventory.getDescription());
				pi.setWarehouseName(inventory.getM_Warehouse().getName());
				pi.setOrderDate(RwplUtils.convertTimestampToDate(inventory.getCreated()));
				piComponents.add(pi);
			}

			responseModel.setPiList(piComponents);
			responseModel.setCount(piComponents.size());
			responseModel.setError(false);
			return Response.ok(responseModel).build();

		} catch (Exception e) {
			responseModel.setError(true);
			responseModel.setError(e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();
		}
	}

	@Override
	public Response getPhysicalInventoryDetailsById(int id) {
		PIDetailsResponse responseModel = new PIDetailsResponse();
		List<PIDetailsLine> lineList = new ArrayList<>();

		try {
			Properties ctx = Env.getCtx();
			MInventory inventory = new MInventory(ctx, id, null);

			if (inventory.get_ID() == 0) {
				responseModel.setError(true);
				responseModel.setError("Physical Inventory not found");
				return Response.status(Status.NOT_FOUND).entity(responseModel).build();
			}

			responseModel.setmInventoryId(inventory.getM_Inventory_ID());
			responseModel.setDocumentNo(inventory.getDocumentNo());
			responseModel.setDescription(inventory.getDescription() != null ? inventory.getDescription() : "");
			responseModel.setWarehouseName(inventory.getM_Warehouse().getName());
			responseModel.setOrderDate(RwplUtils.convertTimestampToDate(inventory.getCreated()));
			responseModel.setPiId(id);

			MInventoryLine[] lines = inventory.getLines(false);
			for (MInventoryLine line : lines) {
				PIDetailsLine lineData = new PIDetailsLine();
				MProduct product = MProduct.get(ctx, line.getM_Product_ID());
				lineData.setProductId(line.getM_Product_ID());
				lineData.setProductName(product.getName());
				MLocator locator = new MLocator(ctx, line.getM_Locator_ID(), null);
				lineData.setLocatorId(line.getM_Locator_ID());
				lineData.setLocatorName(locator.getValue());
				lineData.setPiLineId(line.getM_InventoryLine_ID());
				lineData.setQntyBook(line.getQtyBook().intValue());
				lineData.setQtyCount(line.getQtyCount().intValue());
				lineList.add(lineData);
			}

			responseModel.setPiDetailsLines(lineList);
			responseModel.setError(false);
			return Response.ok(responseModel).build();

		} catch (Exception e) {
			responseModel.setError(true);
			responseModel.setError("Error processing PI details: " + e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();
		}
	}

	@Override
	public Response pIQtyChange(PIQtyChangeRequest request) {
		PIQtyChangeResponse responseModel = new PIQtyChangeResponse();

		try {
			Properties ctx = Env.getCtx();

			MInventory inventory = new MInventory(ctx, request.getPiId(), null);

			if (inventory.get_ID() == 0) {
				responseModel.setError(true);
				responseModel.setError("Physical Inventory not found");
				return Response.status(Status.NOT_FOUND).entity(responseModel).build();
			}

			for (PILineRequest piLine : request.getPiLines()) {
				MInventoryLine inventoryLine = new MInventoryLine(ctx, piLine.getmInventoryLineId(), null);

				if (inventoryLine.get_ID() == 0)
					continue;

				inventoryLine.setQtyCount(BigDecimal.valueOf(piLine.getCountQty()));
				inventoryLine.saveEx();
			}

			responseModel.setError(false);
			return Response.ok(responseModel).build();

		} catch (Exception e) {
			responseModel.setError(true);
			responseModel.setError("Error updating quantity: " + e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();
		}
	}

	@Override
	public Response getSOList(String searchKey) {

		SOListResponse responseModel = new SOListResponse();
		responseModel.setError(false);
		List<SOListComponent> soComponents = new ArrayList<>();

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String trxName = null;

		try {
			Properties ctx = Env.getCtx();
			int clientId = Env.getAD_Client_ID(ctx);
			int roleId = Env.getAD_Role_ID(ctx);

			List<Integer> orgList = new ArrayList<>();
			Login login = new Login(ctx);
			KeyNamePair[] orgs = login.getOrgs(new KeyNamePair(roleId, ""));
			if (orgs != null) {
				for (KeyNamePair org : orgs) {
					orgList.add(Integer.valueOf(org.getID()));
				}
			}
			String orgIds = orgList.stream().map(Object::toString).collect(Collectors.joining(", "));

			int warehouseId = Env.getContextAsInt(ctx, Env.M_WAREHOUSE_ID);

			List<MLocator> locators = MLocatorType_Custom.getLocatorsByType(ctx, trxName, warehouseId, "dispatch", "Y");
			if (locators == null || locators.size() == 0) {
				responseModel.setError(true);
				responseModel.setErrorMessage("Dispatch Locator Not Found for Warehouse ID: " + warehouseId);
				return Response.ok(responseModel).build();
			}

			StringBuilder query = new StringBuilder("SELECT DISTINCT\n" + " so.documentno AS Sales_Order,\n"
					+ " so.c_order_id AS cOrderId, so.putStatus,\n"
					+ " TO_CHAR(so.dateordered, 'DD/MM/YYYY') AS Order_Date,\n"
					+ " wh.name AS Warehouse_Name, wh.m_warehouse_id,\n" + " bp.name AS Customer, so.created,\n"
					+ " so.description,\n" + " CASE\n"
					+ "     WHEN so.docstatus = 'CO' AND mr.m_inout_id IS NULL THEN 'N'\n"
					+ "     WHEN so.docstatus = 'CO' AND mr.m_inout_id IS NOT NULL THEN 'Y' \n"
					+ " END AS status_shipped\n" + "FROM c_order so\n"
					+ "JOIN c_orderline sol ON so.c_order_id = sol.c_order_id\n"
					+ "JOIN c_bpartner bp ON so.c_bpartner_id = bp.c_bpartner_id \n"
					+ "JOIN m_warehouse wh ON so.m_warehouse_id = wh.m_warehouse_id\n"
					+ "LEFT JOIN m_inout mr ON so.c_order_id = mr.c_order_id\n" + "WHERE sol.m_product_id != 0 \n"
					+ " AND sol.qtyordered > (\n" + "     SELECT COALESCE(SUM(iol.qtyentered), 0)\n"
					+ "     FROM m_inoutline iol\n" + "     WHERE iol.c_orderline_id = sol.c_orderline_id\n" + " )\n"
					+ " AND so.ad_client_id = ? \n" + " AND so.issotrx = 'Y'\n" + " AND so.docstatus = 'CO' \n"
					+ " AND (\n" + "     so.documentno ILIKE '%' || ? || '%'\n"
					+ "     OR bp.name ILIKE '%' || ? || '%'\n" + "     OR wh.name ILIKE '%' || ? || '%'\n"
					+ "     OR so.description ILIKE '%' || ? || '%'\n" + " ) \n" + " AND so.ad_org_id IN (" + orgIds
					+ ") \n" + " ORDER BY so.created DESC");

			pstmt = DB.prepareStatement(query.toString(), trxName);

			int index = 1;
			pstmt.setInt(index++, clientId);

			pstmt.setString(index++, searchKey != null ? searchKey : "");
			pstmt.setString(index++, searchKey != null ? searchKey : "");
			pstmt.setString(index++, searchKey != null ? searchKey : "");
			pstmt.setString(index++, searchKey != null ? searchKey : "");

			rs = pstmt.executeQuery();

			while (rs.next()) {
				SOListComponent so = new SOListComponent();

				int cOrderId = rs.getInt("cOrderId");
				so.setcOrderId(cOrderId);
				so.setDocumentNumber(rs.getString("Sales_Order"));
				so.setOrderDate(rs.getString("Order_Date"));
				so.setCustomerName(rs.getString("Customer"));
				so.setWarehouseName(rs.getString("Warehouse_Name"));
				so.setWarehouseId(rs.getInt("m_warehouse_id"));

				String putStatus = rs.getString("putStatus");
				so.setStatus(putStatus != null ? putStatus : "");

				String description = rs.getString("description");
				so.setDescription(description != null ? description : "");

				boolean isShipped = "Y".equals(rs.getString("status_shipped"));
				so.setSalesOrderStatus(isShipped);

				MOrder mOrder = new MOrder(ctx, cOrderId, trxName);
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
					int productTotalQnty = totalOrderQnty.get(key);
					totalQnty += productTotalQnty;

					int productPickedQnty = 0;
					if (labelList.containsKey(key))
						productPickedQnty = labelList.get(key);

					pickedQnty += productPickedQnty;

				}

				so.setQuantityPicked(pickedQnty);
				so.setQuantityTotal(totalQnty);
				so.setRemainingQuantityToPick(totalQnty - pickedQnty);

				soComponents.add(so);
			}

			responseModel.setSoList(soComponents);
			responseModel.setCount(soComponents.size());

			return Response.ok(responseModel).build();

		} catch (Exception e) {
			responseModel.setError(true);
			e.printStackTrace();
			responseModel.setErrorMessage(e.getMessage() != null ? e.getMessage() : "An unexpected error occurred.");
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();

		} finally {
			DB.close(rs, pstmt);
		}
	}

	private LinkedHashMap<Integer, Integer> getLabelsPickedForSO(Properties ctx, String trxName, MInOut[] mInouts) {
		LinkedHashMap<Integer, Integer> labelList = new LinkedHashMap<Integer, Integer>();

		if (mInouts != null && mInouts.length != 0) {
			for (MInOut inout : mInouts) {
				MInOutLine[] inOutLines = inout.getLines();
				if (inOutLines != null && inOutLines.length != 0) {
					for (MInOutLine line : inOutLines) {
						// Use BigDecimal.intValue() if QtyEntered is BigDecimal
						int qnty = line.getQtyEntered().intValue();
						int productId = line.getM_Product_ID();

						if (labelList.containsKey(productId))
							qnty += labelList.get(productId);

						labelList.put(productId, qnty);
					}
				}
			}
		}
		return labelList;
	}

	@Override
	public Response getSODetail(int cOrderId) {
		SODetailResponse responseModel = new SODetailResponse();
		responseModel.setError(false);
		List<SODetailProductData> productDataList = new ArrayList<>();
		String trxName = null;

		try {
			Properties ctx = Env.getCtx();
			int clientId = Env.getAD_Client_ID(ctx);
			int warehouseId = Env.getContextAsInt(ctx, Env.M_WAREHOUSE_ID);

			MOrder mOrder = new MOrder(ctx, cOrderId, trxName);
			if (mOrder == null || mOrder.get_ID() == 0 || mOrder.getAD_Client_ID() != clientId) {
				responseModel.setError(true);
				responseModel.setErrorMessage("Sales Order (C_Order_ID: " + cOrderId + ") not found or access denied.");
				return Response.status(Status.NOT_FOUND).entity(responseModel).build();
			}

			List<MLocator> locators = MLocatorType_Custom.getLocatorsByType(ctx, trxName, warehouseId, "dispatch", "Y");
			if (locators == null || locators.isEmpty()) {
				responseModel.setError(true);
				responseModel.setErrorMessage("Dispatch Locator Not Found for Warehouse ID: " + warehouseId);
				return Response.ok(responseModel).build();
			}
			MLocator dispatchLocator = locators.get(0);
			responseModel.setDispatchLocatorId(dispatchLocator.get_ID());
			responseModel.setDispatchLocatorName(dispatchLocator.getValue());

			responseModel.setcOrderId(cOrderId);
			responseModel.setDocumentNo(mOrder.getDocumentNo());
			responseModel.setDocStatus(mOrder.getDocStatus());
			responseModel.setDescription(mOrder.getDescription() != null ? mOrder.getDescription() : "");
			responseModel.setOrderStatus(mOrder.isDelivered());

			LocalDateTime createdDateTime = mOrder.getCreated().toLocalDateTime();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
			responseModel.setOrderDate(createdDateTime.format(formatter));

			responseModel.setCustomer(mOrder.getC_BPartner().getName());
			responseModel.setWarehouseName(mOrder.getM_Warehouse().getName());

			MBPartnerLocation bpl = new MBPartnerLocation(ctx, mOrder.getC_BPartner_Location_ID(), trxName);
			responseModel.setLocationName(bpl.getName());

			int quantityPicked = 0;
			int quantityTotal = 0;

			MOrderLine[] orderLines = mOrder.getLines();
			for (MOrderLine line : orderLines) {
				if (line.getM_Product_ID() == 0)
					continue;

				MProduct product = MProduct.get(ctx, line.getM_Product_ID());
				BigDecimal totalQntyBD = line.getQtyEntered();
				BigDecimal outstandingQntyBD = totalQntyBD.subtract(getQtyInDraftShipments(line.get_ID(), trxName));

				int totalQnty = totalQntyBD.intValue();
				int outstanding_qty = outstandingQntyBD.intValue();
				int alreadyPickedQnty = totalQnty - outstanding_qty;
				if (outstanding_qty <= 0)
					continue;

				SODetailProductData productData = new SODetailProductData();
				productData.setProductId(product.getM_Product_ID());
				productData.setProductName(product.getName());
				productData.setcOrderlineId(line.getC_OrderLine_ID());
				productData.setUomId(line.getC_UOM_ID());

				productData.setQuantityTotal(totalQntyBD.intValue());
				productData.setQuantityPicked(alreadyPickedQnty);
				productData.setRemainingQuantityToPick(outstanding_qty);

				if (outstanding_qty > 0) {
					getSuggestedLocatorForProduct(productData, ctx, trxName, warehouseId, clientId);

					if (productData.getQntyAvailableInLocator() == null
							|| productData.getQntyAvailableInLocator().isEmpty()) {
						productData.setQntyAvailableInLocator(new ArrayList<>());
						productData.getQntyAvailableInLocator().add(new SODetailLocator());
					}
				} else {
					productData.setQntyAvailableInLocator(new ArrayList<>());
				}

				quantityPicked += alreadyPickedQnty;
				quantityTotal += totalQnty;
				productDataList.add(productData);
			}

			responseModel.setProductData(productDataList);
			responseModel.setQuantityPicked(quantityPicked);
			responseModel.setQuantityTotal(quantityTotal);

			return Response.ok(responseModel).build();

		} catch (Exception e) {
			responseModel.setError(true);
			e.printStackTrace();
			responseModel.setErrorMessage(e.getMessage() != null ? e.getMessage() : "An unexpected error occurred.");
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();

		}
	}

	private BigDecimal getQtyInDraftShipments(int C_OrderLine_ID, String trxName) {
		if (C_OrderLine_ID == 0) {
			return Env.ZERO;
		}

		String sql = "SELECT SUM(iol.QtyEntered) " + "FROM M_InOutLine iol " + "WHERE iol.C_OrderLine_ID = ?";

		BigDecimal qtyInShipments = DB.getSQLValueBD(trxName, sql, C_OrderLine_ID);

		return qtyInShipments != null ? qtyInShipments : Env.ZERO;
	}

	private void getSuggestedLocatorForProduct(SODetailProductData productData, Properties ctx, String trxName,
			int warehouseId, int clientId) {

		List<SODetailLocator> suggestedLocators = productData.getQntyAvailableInLocator() != null
				? productData.getQntyAvailableInLocator()
				: new ArrayList<>();

		productData.setQntyAvailableInLocator(suggestedLocators);

		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {

			boolean pickFlag = false;
			int productId = productData.getProductId();
			int quantityRemaining = productData.getRemainingQuantityToPick();

			String sql = "SELECT pp.m_locator_id, pp.m_product_id, " + "SUM(CASE \n"
					+ "WHEN pp.issotrx = 'N' THEN pp.quantity \n" + "ELSE 0 \n" + "END) AS remaining_count\n"
					+ "FROM pi_productlabel pp\n" + "JOIN m_locator ml ON ml.m_locator_id = pp.m_locator_id\n"
					+ "JOIN m_locatortype mt ON mt.m_locatortype_id = ml.m_locatortype_id\n" + "WHERE \n"
					+ "pp.ad_client_id = ? AND \n" + "NOT EXISTS (\n" + "SELECT 1 \n"
					+ "FROM pi_productlabel pp_sales\n" + "WHERE pp_sales.labeluuid = pp.labeluuid\n"
					+ "AND pp_sales.issotrx = 'Y'\n" + ")\n" + "AND mt.storage = 'Y'\n" + "AND pp.m_product_id = ? \n"
					+ "GROUP BY pp.m_locator_id, pp.m_product_id";

			pstm = DB.prepareStatement(sql, trxName);
			pstm.setInt(1, clientId);
			pstm.setInt(2, productId);
			rs = pstm.executeQuery();

			while (rs.next()) {
				int remainingCount = rs.getInt("remaining_count");
				int locatorId = rs.getInt("m_locator_id");

				MLocator locator = new MLocator(ctx, locatorId, trxName);
				MLocatorType_Custom type = new MLocatorType_Custom(ctx, locator.getM_LocatorType_ID(), trxName);

				boolean locatorFoundInList = false;

				for (SODetailLocator line : suggestedLocators) {
					if (line.getLocatorId() == locatorId) {
						int availableQty = Math.min(remainingCount, quantityRemaining);
						line.setQuantityAvailable(line.getQuantityAvailable() + availableQty);
						quantityRemaining -= availableQty;
						locatorFoundInList = true;
						if (quantityRemaining <= 0) {
							pickFlag = true;
							break;
						}
						break;
					}
				}

				if (!locatorFoundInList && !type.isdispatch() && !type.isPacking() && !type.isReturns()) {
					SODetailLocator newLine = new SODetailLocator();
					int availableQty = Math.min(remainingCount, quantityRemaining);

					newLine.setQuantityAvailable(availableQty);
					quantityRemaining -= availableQty;
					newLine.setLocatorId(locatorId);
					newLine.setLocatorName(locator.getValue());
					suggestedLocators.add(newLine);

					if (quantityRemaining <= 0) {
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
	public Response createShipment(CreateShipmentRequest request) {

		CreateShipmentResponse responseModel = new CreateShipmentResponse();
		responseModel.setError(false);

		Properties ctx = Env.getCtx();

		String trxName = null;
		int clientID = Env.getAD_Client_ID(ctx);
		int cOrderId = request.getcOrderId();
		String status = request.getStatus();
		String description = request.getDescription();
		List<ShipmentLine> shipmentLines = request.getShipmentLines();

		int mInoutId = 0;
		try {

			MOrder order = new MOrder(ctx, cOrderId, trxName);
			if (order.get_ID() == 0 || order.getAD_Client_ID() != clientID) {
				responseModel.setError(true);
				responseModel.setErrorMessage("Order not found for ID: " + cOrderId);
				return Response.ok(responseModel).build();
			}

			boolean existingInout = false;
			MInOut dispatch = null;
			MInOut[] mInOuts = order.getShipments();
			if (mInOuts != null && mInOuts.length != 0) {
				for (MInOut in : mInOuts) {
					if (in.getDocStatus().equalsIgnoreCase(DocAction.STATUS_Drafted)
							|| in.getDocStatus().equalsIgnoreCase(DocAction.STATUS_InProgress)) {
						existingInout = true;
						dispatch = in;
					}
				}
			}

			if (dispatch == null) {
				MTable docTypeTable = MTable.get(ctx, "c_doctype");
				PO docTypePO = docTypeTable.getPO("name = 'MM Shipment' and ad_client_id = " + clientID + "", trxName);
				MDocType mDocType = (MDocType) docTypePO;

				dispatch = new MInOut(order, mDocType.get_ID(), order.getDateOrdered());
				dispatch.setDocStatus(DocAction.STATUS_Drafted);
				dispatch.saveEx();

				mInoutId = dispatch.getM_InOut_ID();

			}

			MInOut_Custom inout = new MInOut_Custom(ctx, dispatch.get_ID(), trxName);
			if (status != null && status != "") {
				inout.setPickStatus(status);
				inout.saveEx();
			}
			
			if (description != null && description != "") {
				inout.setPickStatus(status);
				inout.saveEx();
			}

			if (existingInout) {
				MInOutLine[] inouttLines = dispatch.getLines();
				for (ShipmentLine line : shipmentLines) {
					int C_InvoiceLine_ID = 0;
					int M_RMALine_ID = 0;
					int M_Product_ID = line.getProductId();
					int C_UOM_ID = line.getUomId();
					int C_OrderLine_ID = line.getcOrderlineId();
					BigDecimal QtyEntered = BigDecimal.valueOf(line.getQnty());
					int M_Locator_ID = line.getLocator();

					int mrLineId = 0;

					boolean create = true;
					if (inouttLines != null && inouttLines.length != 0) {
						for (MInOutLine inoutLine : inouttLines) {
							if (line.getcOrderlineId() == inoutLine.getC_OrderLine_ID()) {
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

					line.setMrLineId(mrLineId);

				}
			} else {
				for (ShipmentLine line : shipmentLines) {
					int C_InvoiceLine_ID = 0;
					int M_RMALine_ID = 0;
					int M_Product_ID = line.getProductId();
					int C_UOM_ID = line.getUomId();
					int C_OrderLine_ID = line.getcOrderlineId();
					BigDecimal QtyEntered = BigDecimal.valueOf(line.getQnty());
					int M_Locator_ID = line.getLocator();

					inout.createLineFrom(C_OrderLine_ID, C_InvoiceLine_ID, M_RMALine_ID, M_Product_ID, C_UOM_ID,
							QtyEntered, M_Locator_ID);
					MInOutLine[] mInoutLines = inout.getLines();
					int lineId = mInoutLines[mInoutLines.length - 1].get_ID();
					line.setMrLineId(lineId);

				}
				inout.updateFrom(order, null, null);
			}

			responseModel.setShipmentDocumentNumber(inout.getDocumentNo());
			responseModel.setShipmentId(mInoutId);
			responseModel.setShipmentLines(shipmentLines);

			Map<String, String> data = new HashMap<>();
			data.put("recordId", String.valueOf(inout.getM_InOut_ID()));
			data.put("documentNo", String.valueOf(inout.getDocumentNo()));
			data.put("path1", "/dispatch_screen");
			data.put("path2", "/dispatch_detail_screen");

			RwplUtils.sendNotificationAsync(true, false, inout.get_Table_ID(), inout.getM_InOut_ID(), ctx, trxName,
					"Products ready for dispatch - " + inout.getDocumentNo() + "",
					"New products marked ready for dispatch with Shipment No: " + inout.getDocumentNo()
							+ " for Order - " + inout.getC_Order().getDocumentNo() + "",
					inout.get_TableName(), data, clientID, "CustomerShipmentCreted");

		} catch (Exception e) {
			MTable table = MTable.get(ctx, "m_inout");
			PO po = table.getPO(mInoutId, trxName);
			po.delete(true);
			responseModel.setErrorMessage(e.getMessage());
			responseModel.setError(true);
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();
		}
		return Response.ok(responseModel).build();
	}

	@Override
	public Response getPutAwayDetail(int id) {
		PutAwayDetailResponse responseModel = new PutAwayDetailResponse();
		responseModel.setIsError(false);
		String trxName = null;

		try {
			Properties ctx = Env.getCtx();
			int clientId = Env.getAD_Client_ID(ctx);

			MInOut inOut = new MInOut(ctx, id, trxName);

			if (inOut == null || inOut.get_ID() == 0) {
				responseModel.setIsError(true);
				responseModel.setError("Material Receipt (M_InOut_ID: " + id + ") not found.");
				return Response.status(Status.NOT_FOUND).entity(responseModel).build();
			}

			I_C_BPartner supplier = inOut.getC_BPartner();
			I_C_Order order = inOut.getC_Order();

			responseModel.setDocumentNo(inOut.getDocumentNo());
			responseModel.setMInoutID(inOut.getM_InOut_ID());
			responseModel.setSupplier(supplier != null ? supplier.getName() : "");

			responseModel.setOrderDate(RwplUtils.formatTimestamp(inOut.getCreated()));

			responseModel.setOrderDocumentno(order != null ? order.getDocumentNo() : "");
			responseModel.setWarehouseId(inOut.getM_Warehouse_ID());
			responseModel.setWarehouseName(inOut.getM_Warehouse().getName());
			responseModel.setDescription(inOut.getDescription());

			List<PutAwayDetailComponent> detailList = new ArrayList<>();
			Map<Integer, PutAwayDetailComponent> lineMap = new HashMap<>();

			MInOutLine[] lines = inOut.getLines();

			for (MInOutLine line : lines) {
				PutAwayDetailComponent detail = new PutAwayDetailComponent();

				detail.setProductId(line.getM_Product_ID());
				detail.setProductName(line.getM_Product().getValue());
				detail.setCOrderlineId(line.getC_OrderLine_ID());
				detail.setMInoutLineId(line.getM_InOutLine_ID());

				MInOutLine_Custom lineCustom = new MInOutLine_Custom(ctx, line.getM_InOutLine_ID(), trxName);
				int qcFailedQty = lineCustom.getQCFailedQty().intValue();

				detail.setTotalQuantity(line.getQtyEntered().intValue() - qcFailedQty);

				if (!inOut.getDocStatus().equals(DocAction.ACTION_Complete)) {
					detail.setQuantityInRecevingLocator(line.getQtyEntered().intValue());
				} else {
					detail.setQuantityInRecevingLocator(0);
				}

				lineMap.put(line.getM_InOutLine_ID(), detail);
			}

			if (inOut.getDocStatus().equals(DocAction.ACTION_Complete)) {

				List<MLocator> locators = MLocatorType_Custom.getLocatorsByType(ctx, trxName, inOut.getM_Warehouse_ID(),
						"receiving", "Y");

				if (locators == null || locators.isEmpty()) {
					responseModel.setIsError(true);
					responseModel.setError("Receiving Locators Not Found For Department");
					return Response.ok(responseModel).build();
				}

				String locatorIds = locators.stream().mapToInt(MLocator::get_ID).mapToObj(String::valueOf)
						.collect(Collectors.joining(","));

				String piWhereClause = "M_Locator_ID IN (" + locatorIds + ") AND M_InOutLine_ID IN ("
						+ lineMap.keySet().stream().map(String::valueOf).collect(Collectors.joining(","))
						+ ") AND AD_Client_ID=?";

				List<PO> piLabels = new Query(ctx, PiProductLabel.Table_Name, piWhereClause, trxName)
						.setParameters(clientId).list();

				for (PO po : piLabels) {
					PiProductLabel piProductLabel = new PiProductLabel(ctx, po.get_ID(), trxName);
					int inoutLineId = piProductLabel.getM_InOutLine_ID();
					int quantity = piProductLabel.getquantity().intValue();

					PutAwayDetailComponent detail = lineMap.get(inoutLineId);

					if (detail != null) {
						detail.setQuantityInRecevingLocator(detail.getQuantityInRecevingLocator() + quantity);
					}
				}
			}

			detailList.addAll(lineMap.values());
			responseModel.setPutAwayDetail(detailList);

			return Response.ok(responseModel).build();

		} catch (Exception e) {

			responseModel.setIsError(true);
			e.printStackTrace();
			responseModel.setError(e.getMessage() != null ? e.getMessage() : "An unexpected error occurred.");
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();

		}
	}

	@Override
	public Response getPutAwayLabour() {
		PutAwayLabourResponse responseModel = new PutAwayLabourResponse();
		responseModel.setError(false);
		List<PutAwayLabourComponent> putAwayLabourList = new ArrayList<>();

		String trxName = null;

		try {
			Properties ctx = Env.getCtx();
			int clientId = Env.getAD_Client_ID(ctx);
			int warehouseId = Env.getContextAsInt(ctx, Env.M_WAREHOUSE_ID);

			// Get receiving locators
			List<MLocator> receivingLocators = MLocatorType_Custom.getLocatorsByType(ctx, trxName, warehouseId,
					"receiving", "Y");
			if (receivingLocators == null || receivingLocators.isEmpty()) {
				responseModel.setError(true);
				responseModel.setError("Receiving Locator Not Found");
				return Response.ok(responseModel).build();
			}

			// Get storage locators for suggestions
			List<MLocator> storageLocators = MLocatorType_Custom.getLocatorsByType(ctx, trxName, warehouseId, "storage",
					"Y");
			if (storageLocators == null || storageLocators.isEmpty()) {
				responseModel.setError(true);
				responseModel.setError("Storage Locator Not Found");
				return Response.ok(responseModel).build();
			}

			// Get all product labels in receiving locators from completed material receipts
			String receivingLocatorIds = receivingLocators.stream().map(locator -> String.valueOf(locator.get_ID()))
					.collect(Collectors.joining(","));

			String piWhereClause = "M_Locator_ID IN (" + receivingLocatorIds + ") AND AD_Client_ID=? AND IsSOTrx='N'";
			List<PO> piLabels = new Query(ctx, PiProductLabel.Table_Name, piWhereClause, trxName)
					.setParameters(clientId).list();

			Map<Integer, PutAwayLabourComponent> productMap = new HashMap<>();
			List<MLocator> availableStorageLocators = new ArrayList<>(storageLocators);

			for (PO po : piLabels) {
				PiProductLabel piProductLabel = new PiProductLabel(ctx, po.get_ID(), trxName);

				// Check if the material receipt is completed
				I_M_InOut mInout = piProductLabel.getM_InOutLine().getM_InOut();
				if (!DocAction.STATUS_Completed.equalsIgnoreCase(mInout.getDocStatus())) {
					continue;
				}

				int productId = piProductLabel.getM_Product_ID();
				int quantity = piProductLabel.getquantity().intValue();

				PutAwayLabourComponent component = productMap.get(productId);
				if (component != null) {
					// Add to existing product quantity
					component.setQuantity(component.getQuantity() + quantity);
				} else {
					// Create new component
					component = new PutAwayLabourComponent();
					component.setProductId(productId);
					component.setProductName(piProductLabel.getM_Product().getName());
					component.setQuantity(quantity);
					component.setWarehouseId(piProductLabel.getM_Locator().getM_Warehouse_ID());
					component.setWarehouseName(piProductLabel.getM_Locator().getM_Warehouse().getName());

					// Find suggested storage locator
					MLocator suggestedLocator = findAvailableStorageLocator(availableStorageLocators, clientId);
					if (suggestedLocator != null) {
						component.setLocatorId(suggestedLocator.get_ID());
						component.setLocatorName(suggestedLocator.getValue());
					} else {
						// Use first storage locator as fallback
						MLocator fallbackLocator = storageLocators.get(0);
						component.setLocatorId(fallbackLocator.get_ID());
						component.setLocatorName(fallbackLocator.getValue());
					}

					productMap.put(productId, component);
				}
			}

			putAwayLabourList.addAll(productMap.values());
			responseModel.setPutAwayLabour(putAwayLabourList);
			responseModel.setCount(putAwayLabourList.size());

			return Response.ok(responseModel).build();

		} catch (Exception e) {
			responseModel.setError(true);
			e.printStackTrace();
			responseModel.setError(e.getMessage() != null ? e.getMessage() : "An unexpected error occurred.");
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();
		}
	}

	private MLocator findAvailableStorageLocator(List<MLocator> storageLocators, int clientId) {
		// Find a storage locator with minimal existing inventory
		for (MLocator locator : storageLocators) {
			MLocatorType_Custom locatorType = new MLocatorType_Custom(Env.getCtx(), locator.getM_LocatorType_ID(),
					null);

			// Skip dispatch, receiving, and returns locators
			if (locatorType.isdispatch() || locatorType.isReceiving() || locatorType.isReturns()) {
				continue;
			}

			// Check quantity on hand for this locator
			Integer qntyOnHand = PiProductLabel.getSumOfQntyByLocator(clientId, locator.getM_Locator_ID());
			if (qntyOnHand == null || qntyOnHand <= 0) {
				// Found empty locator, remove from available list and return
				storageLocators.remove(locator);
				return locator;
			}
		}

		// If no empty locator found, return the first available one
		return storageLocators.isEmpty() ? null : storageLocators.get(0);
	}

	@Override
	public Response putAway(PutAwayRequest request) {
		PutAwayResponse responseModel = new PutAwayResponse();
		Trx trx = null;

		try {
			Properties ctx = Env.getCtx();
			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			
			int clientId = Env.getAD_Client_ID(ctx);
			int orgId = Env.getAD_Org_ID(ctx);
			boolean finalDispatch = request.isFinalDispatch();
			List<PutAwayLineRequest> putAwayLines = request.getPutAwayLines();

			if (putAwayLines == null || putAwayLines.isEmpty()) {
				responseModel.setIsError(true);
				responseModel.setError("No putaway lines provided");
				return Response.status(Status.BAD_REQUEST).entity(responseModel).build();
			}

			// Create movement for inventory transfer
			MMovement mMovement = new MMovement(ctx, 0, trxName);
			mMovement.setIsActive(true);
			mMovement.setDescription(null);
			mMovement.setApprovalAmt(BigDecimal.valueOf(0));
			mMovement.setChargeAmt(BigDecimal.valueOf(0));
			mMovement.setFreightAmt(BigDecimal.valueOf(0));
			mMovement.setAD_Org_ID(orgId);

			int mWarehouseId = 0;
			boolean movementCreated = false;

			for (PutAwayLineRequest line : putAwayLines) {
				String labelUUID = line.getProductLabelUUId();
				int newLocatorId = line.getLocatorId();
				int newLabelQnty = line.getNewLabelQnty();

				// Get the product label
				List<PO> poList = PiProductLabel.getPiProductLabel("labelUUId", labelUUID, ctx, trxName, false);
				if (poList.isEmpty()) {
					responseModel.setIsError(true);
					responseModel.setError("Product label not found: " + labelUUID);
					return Response.status(Status.NOT_FOUND).entity(responseModel).build();
				}

				PiProductLabel piProductLabel = new PiProductLabel(ctx, poList.get(0).get_ID(), trxName);

				// Set warehouse for movement if not set
				if (mWarehouseId == 0) {
					mWarehouseId = piProductLabel.getM_InOutLine().getM_Locator().getM_Warehouse_ID();
					mMovement.setM_Warehouse_ID(mWarehouseId);
					mMovement.setM_WarehouseTo_ID(mWarehouseId);
					mMovement.saveEx();
					movementCreated = true;
				}

				int currentLocatorId = piProductLabel.getM_Locator_ID();

				if (newLabelQnty <= 0) {
					// Move entire label to new location
					if (currentLocatorId != newLocatorId) {
						moveInventory(ctx, trxName, mMovement, piProductLabel.getC_OrderLine_ID(),
								piProductLabel.getquantity(), piProductLabel.getM_Product_ID(), currentLocatorId,
								newLocatorId, clientId, orgId);
					}

					if (finalDispatch) {
						piProductLabel.setfinaldispatch(true);
					}

					piProductLabel.setM_Locator_ID(newLocatorId);
					piProductLabel.saveEx();

				} else {

					PiProductLabel newLabel = new PiProductLabel(ctx, trxName, clientId, orgId, piProductLabel.getM_Product_ID(),
							newLocatorId, piProductLabel.getC_OrderLine_ID(), piProductLabel.getM_InOutLine_ID(), false, BigDecimal.valueOf(newLabelQnty), null);
					
					if (finalDispatch) {
						newLabel.setfinaldispatch(true);
					}

					newLabel.saveEx();

					// Update original label quantity
					BigDecimal remainingQty = piProductLabel.getquantity().subtract(BigDecimal.valueOf(newLabelQnty));
					piProductLabel.setquantity(remainingQty);
					piProductLabel.saveEx();

					// Move inventory for the split quantity
					moveInventory(ctx, trxName, mMovement, piProductLabel.getC_OrderLine_ID(),
							BigDecimal.valueOf(newLabelQnty), piProductLabel.getM_Product_ID(), currentLocatorId,
							newLocatorId, clientId, orgId);
				}
			}

			// Complete the movement if created
			if (movementCreated) {
				mMovement.setDocStatus(DocAction.ACTION_Complete);
				mMovement.setDocAction(DocAction.ACTION_Close);
				mMovement.setPosted(true);
				mMovement.setProcessed(true);
				mMovement.setIsApproved(true);
				mMovement.completeIt();
				mMovement.saveEx();
			}

			trx.commit();
			responseModel.setIsError(false);
			return Response.ok(responseModel).build();

		} catch (Exception e) {
			if (trx != null) {
				trx.rollback();
			}
			responseModel.setIsError(true);
			responseModel.setError("Error processing putaway: " + e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();

		} finally {
			if (trx != null) {
				trx.close();
			}
		}
	}

	private void moveInventory(Properties ctx, String trxName, MMovement mMovement, int cOrderLineID,
			BigDecimal quantity, int productId, int fromLocatorId, int toLocatorId, int clientId, int orgId) {

		// Create movement line
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
	}

	@Override
	public Response setDocAction(SetDocActionRequest request) {
		SetDocActionResponse response = new SetDocActionResponse();

		String trxName = null;
		Trx trx = null;

		try {
			Properties ctx = Env.getCtx();

			trxName = Trx.createTrxName(getClass().getName() + "_setDocAction");
			trx = Trx.get(trxName, true);
			trx.start();

			String tableName = request.getTableName();
			int recordId = request.getRecordId();
			String docAction = request.getDocAction();

			response.setRecordId(recordId);

			// Get the table
			MTable table = MTable.get(ctx, tableName);
			if (table == null) {
				response.setError(true);
				response.setErrorMessage("No Table Found");
				return Response.status(Status.BAD_REQUEST).entity(response).build();
			}

			// Get the record
			PO po = table.getPO(recordId, trxName);
			if (po == null) {
				response.setError(true);
				response.setErrorMessage("No Record Found");
				return Response.status(Status.NOT_FOUND).entity(response).build();
			}

			// Set DocAction column to avoid automatic process of default option
			po.set_ValueOfColumn("DocAction", docAction);
			if (!po.save()) {
				response.setError(true);
				response.setErrorMessage("Cannot save before set docAction");
				return Response.status(Status.INTERNAL_SERVER_ERROR).entity(response).build();
			}

			// Process the document action
			try {
				if (!((DocAction) po).processIt(docAction)) {
					response.setError(true);
					response.setErrorMessage("Cannot process docAction");
					return Response.status(Status.INTERNAL_SERVER_ERROR).entity(response).build();
				}
			} catch (Exception e) {
				response.setError(true);
				response.setErrorMessage("Cannot process docAction: " + e.getMessage());
				return Response.status(Status.INTERNAL_SERVER_ERROR).entity(response).build();
			}

			// Save the processed document
			if (!po.save()) {
				response.setError(true);
				response.setErrorMessage("Cannot save after processing docAction");
				return Response.status(Status.INTERNAL_SERVER_ERROR).entity(response).build();
			}

			trx.commit();
			response.setError(false);

		} catch (Exception e) {
			if (trx != null) {
				trx.rollback();
			}
			response.setError(true);
			response.setErrorMessage(e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(response).build();

		} finally {
			if (trx != null) {
				trx.close();
			}
		}

		return Response.ok(response).build();
	}

	@Override
	public Response splitAndMergeMoveLabel(SplitAndMergeLabelRequest request) {
		SplitAndMergeLabelResponse response = new SplitAndMergeLabelResponse();

		String trxName = null;

		try {
			Properties ctx = Env.getCtx();

			String labelA = request.getLabelUUID();
			String labelB = request.getLabelUUID2();
			int quantity = request.getQuantity();

			// Validate inputs
			if (labelA == null || labelB == null || labelA.trim().equalsIgnoreCase(labelB.trim())) {
				response.setError(true);
				response.setErrorMessage("Both labels are the same or invalid");
				return Response.status(Status.BAD_REQUEST).entity(response).build();
			}

			if (quantity <= 0) {
				response.setError(true);
				response.setErrorMessage("Quantity cannot be zero or negative");
				return Response.status(Status.BAD_REQUEST).entity(response).build();
			}

			// Get first label
			List<PO> poList = PiProductLabel.getPiProductLabel("labelUUId", labelA, ctx, trxName, false);
			if (poList == null || poList.size() != 1) {
				response.setError(true);
				response.setErrorMessage("Invalid First Product Label");
				return Response.status(Status.NOT_FOUND).entity(response).build();
			}

			PiProductLabel productLabel1 = new PiProductLabel(ctx, poList.get(0).get_ID(), trxName);
			int productQty1 = productLabel1.getquantity().intValue();
			int productId1 = productLabel1.getM_Product_ID();

			if (productId1 == 0) {
				response.setError(true);
				response.setErrorMessage("First Label does not have a product");
				return Response.status(Status.BAD_REQUEST).entity(response).build();
			}

			if (productQty1 < quantity) {
				response.setError(true);
				response.setErrorMessage("Quantity exceeds available quantity on first label");
				return Response.status(Status.BAD_REQUEST).entity(response).build();
			}

			// Get second label
			List<PO> poList2 = PiProductLabel.getPiProductLabel("labelUUId", labelB, ctx, trxName, false);
			if (poList2 == null || poList2.size() != 1) {
				response.setError(true);
				response.setErrorMessage("Invalid Second Product Label");
				return Response.status(Status.NOT_FOUND).entity(response).build();
			}

			PiProductLabel productLabel2 = new PiProductLabel(ctx, poList2.get(0).get_ID(), trxName);
			int productQty2 = productLabel2.getquantity().intValue();
			int productId2 = productLabel2.getM_Product_ID();

			if (productId2 == 0) {
				response.setError(true);
				response.setErrorMessage("Second Label does not have a product");
				return Response.status(Status.BAD_REQUEST).entity(response).build();
			}

			if (productId1 != productId2) {
				response.setError(true);
				response.setErrorMessage("Product labels have different products");
				return Response.status(Status.BAD_REQUEST).entity(response).build();
			}

			if (productQty2 <= 0) {
				response.setError(true);
				response.setErrorMessage("Second Product quantity cannot be zero or negative");
				return Response.status(Status.BAD_REQUEST).entity(response).build();
			}

			// Merge quantity to second label
			int totalQty = productQty2 + quantity;
			productLabel2.setquantity(BigDecimal.valueOf(totalQty));
			productLabel2.saveEx();

			// Update first label quantity
			int remainingQty = productQty1 - quantity;
			if (remainingQty == 0) {
				// Delete first label if no quantity remains
				productLabel1.deleteEx(true);
			} else {
				productLabel1.setquantity(BigDecimal.valueOf(remainingQty));
				productLabel1.saveEx();
			}

			// Set response data
			MProduct product = new MProduct(ctx, productId2, trxName);
			response.setProductLabelId(productLabel2.getpi_productLabel_ID());
			response.setProductLabelUUId(productLabel2.getlabeluuid());
			response.setProductName(product.getName());
			response.setQuantity(totalQty);
			response.setError(false);

		} catch (Exception e) {
			response.setError(true);
			response.setErrorMessage(e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(response).build();

		}

		return Response.ok(response).build();
	}

	@Override
	public Response removeDamagedQnty(RemoveDamagedQtyRequest request) {
		RemoveDamagedQtyResponse response = new RemoveDamagedQtyResponse();

		String trxName = null;
		try {
			Properties ctx = Env.getCtx();
			int orgId = Env.getAD_Org_ID(ctx);

			String labelUUID = request.getLabelUUID();
			int damagedQuantity = request.getQuantity();

			// Validate inputs
			if (damagedQuantity <= 0) {
				response.setError(true);
				response.setErrorMessage("Quantity cannot be zero or negative");
				return Response.status(Status.BAD_REQUEST).entity(response).build();
			}

			// Get product label
			List<PO> poList = PiProductLabel.getPiProductLabel("labelUUId", labelUUID, ctx, trxName, false);
			if (poList == null || poList.size() != 1) {
				response.setError(true);
				response.setErrorMessage("Invalid Product Label");
				return Response.status(Status.NOT_FOUND).entity(response).build();
			}

			PiProductLabel productLabel = new PiProductLabel(ctx, poList.get(0).get_ID(), trxName);
			int labelQty = productLabel.getquantity().intValue();
			int productId = productLabel.getM_Product_ID();

			if (productId == 0) {
				response.setError(true);
				response.setErrorMessage("Label does not have a product");
				return Response.status(Status.BAD_REQUEST).entity(response).build();
			}

			if (labelQty < damagedQuantity) {
				response.setError(true);
				response.setErrorMessage("Quantity exceeds available quantity on label");
				return Response.status(Status.BAD_REQUEST).entity(response).build();
			}

			// Get returns locator
			List<MLocator> locators = MLocatorType_Custom.getLocatorsByType(ctx, trxName,
					productLabel.getM_Locator().getM_Warehouse_ID(), "returns", "Y");
			if (locators == null || locators.isEmpty()) {
				response.setError(true);
				response.setErrorMessage("Returns locator not found");
				return Response.status(Status.BAD_REQUEST).entity(response).build();
			}

			int returnLocatorId = locators.get(0).getM_Locator_ID();

			// Create new label for damaged quantity in returns locator
			PiProductLabel damagedLabel = new PiProductLabel(ctx, 0, trxName);
			damagedLabel.setAD_Org_ID(orgId);
			damagedLabel.setM_Product_ID(productLabel.getM_Product_ID());
			damagedLabel.setM_Locator_ID(returnLocatorId);
			damagedLabel.setC_OrderLine_ID(productLabel.getC_OrderLine_ID());
			damagedLabel.setM_InOutLine_ID(productLabel.getM_InOutLine_ID());
			damagedLabel.setIsSOTrx(false);
			damagedLabel.setquantity(BigDecimal.valueOf(damagedQuantity));
			damagedLabel.setQcpassed(false);
			damagedLabel.saveEx();

			// Update original label quantity
			BigDecimal updatedQty = productLabel.getquantity().subtract(BigDecimal.valueOf(damagedQuantity));
			if (updatedQty.intValue() == 0) {
				// Delete original label if no quantity remains
				productLabel.deleteEx(true);
			} else {
				productLabel.setquantity(updatedQty);
				productLabel.saveEx();
			}

			response.setError(false);

		} catch (Exception e) {
			response.setError(true);
			response.setErrorMessage(e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(response).build();

		}

		return Response.ok(response).build();
	}

	@Override
	public Response editShipment(EditShipmentRequest request) {
		EditShipmentResponse response = new EditShipmentResponse();

		String trxName = null;
		try {
			Properties ctx = Env.getCtx();

			int mInoutId = request.getMInoutId();

			// Get shipment
			MInOut inout = new MInOut(ctx, mInoutId, trxName);
			if (inout == null || inout.get_ID() == 0) {
				response.setError(true);
				response.setErrorMessage("Shipment not found for ID: " + mInoutId);
				return Response.status(Status.NOT_FOUND).entity(response).build();
			}

			if (request.getLines() == null || request.getLines().isEmpty()) {
				response.setError(true);
				response.setErrorMessage("No lines found");
				return Response.status(Status.BAD_REQUEST).entity(response).build();
			}

			// Get receiving locator
			List<MLocator> locators = MLocatorType_Custom.getLocatorsByType(ctx, trxName, inout.getM_Warehouse_ID(),
					"receiving", "Y");
			if (locators == null || locators.isEmpty()) {
				response.setError(true);
				response.setErrorMessage("Receiving locator not found");
				return Response.status(Status.BAD_REQUEST).entity(response).build();
			}

			int receivingLocatorId = locators.get(0).getM_Locator_ID();
			MInOutLine[] existingLines = inout.getLines();

			// Process each label line
			List<EditShipmentLine> lines = request.getLines();
			if (lines == null || lines.isEmpty()) {
				response.setError(true);
				response.setErrorMessage("No Lines found");
				return Response.status(Status.BAD_REQUEST).entity(response).build();
			}

			for (EditShipmentLine line : lines) {
				int qnty = line.getUsedQuantity();
				String labelUUID = line.getLabelUUID();
				int productId = line.getProductId();

				// Get product label
				List<PO> poList = PiProductLabel.getPiProductLabel("labelUUId", labelUUID, ctx, trxName, false);
				if (poList == null || poList.size() != 1) {
					response.setError(true);
					response.setErrorMessage("Invalid Product Label: " + labelUUID);
					return Response.status(Status.NOT_FOUND).entity(response).build();
				}

				PiProductLabel label = new PiProductLabel(ctx, poList.get(0).get_ID(), trxName);

				// Update shipment lines
				if (existingLines != null && existingLines.length > 0) {
					for (MInOutLine inoutLine : existingLines) {
						if (inoutLine.getM_Product_ID() == productId) {
							int lineQnty = inoutLine.getQtyEntered().intValue();

							if (qnty == 0) {
								break;
							}

							if (qnty == lineQnty) {
								inoutLine.deleteEx(true);
								qnty = 0;
								break;
							} else if (qnty > lineQnty) {
								qnty = qnty - lineQnty;
								inoutLine.deleteEx(true);
								break;
							} else if (qnty < lineQnty) {
								inoutLine.setQtyEntered(BigDecimal.valueOf(lineQnty - qnty));
								inoutLine.setMovementQty(BigDecimal.valueOf(lineQnty - qnty));
								inoutLine.saveEx();
								qnty = 0;
								break;
							}
						}
					}
				}

				// Update label - move back to receiving and mark as not final dispatch
				label.setfinaldispatch(false);
				label.setM_Locator_ID(receivingLocatorId);
				label.saveEx();
			}

			// Check if shipment has any remaining lines, delete if empty
			inout = new MInOut(ctx, mInoutId, trxName);
			MInOutLine[] remainingLines = inout.getLines();
			if (remainingLines == null || remainingLines.length == 0) {
				inout.deleteEx(true);
			}

			response.setError(false);

		} catch (Exception e) {
			response.setError(true);
			response.setErrorMessage(e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(response).build();

		}

		return Response.ok(response).build();
	}

	@Override
	public Response createShipmentByLabel(CreateShipmentByLabelRequest request) {
		CreateShipmentByLabelResponse response = new CreateShipmentByLabelResponse();

		String trxName = null;
		Trx trx = null;

		try {
			Properties ctx = Env.getCtx();
			int clientId = Env.getAD_Client_ID(ctx);
			int orgId = Env.getAD_Org_ID(ctx);

			trxName = Trx.createTrxName(getClass().getName() + "_createSCByLabel");
			trx = Trx.get(trxName, true);
			trx.start();

			if (request.getLines() == null || request.getLines().isEmpty()) {
				response.setError(true);
				response.setErrorMessage("No lines found");
				return Response.status(Status.BAD_REQUEST).entity(response).build();
			}

			// Get old shipment
			MInOut oldInout = new MInOut(ctx, request.getMInoutId(), trxName);
			if (oldInout == null || oldInout.get_ID() == 0) {
				response.setError(true);
				response.setErrorMessage("Shipment not found");
				return Response.status(Status.NOT_FOUND).entity(response).build();
			}

			// Group quantities by product
			Map<Integer, Integer> productQtyMap = new HashMap<>();
			for (CreateShipmentByLabelRequest.ShipmentLabelLine line : request.getLines()) {
				int productId = line.getProductId();
				int qty = line.getUsedQuantity();
				productQtyMap.put(productId, productQtyMap.getOrDefault(productId, 0) + qty);
			}
			
			Map<Integer, Integer> inoutQtyMap = new HashMap<>();
			MInOutLine[] oldLines = oldInout.getLines();
			for (MInOutLine line : oldLines) {
				int productId = line.getM_Product_ID();
				int qty = line.getQtyEntered().intValue();
				inoutQtyMap.put(productId, inoutQtyMap.getOrDefault(productId, 0) + qty);
			}
			
			boolean notMatched = false;
			boolean inavlidInout = false;

			if (!productQtyMap.keySet().equals(inoutQtyMap.keySet())) {
			    notMatched = true;
			} else {
			    for (Map.Entry<Integer, Integer> entry : productQtyMap.entrySet()) {
			        Integer productId = entry.getKey();
			        Integer productQty = entry.getValue();
			        Integer inoutQty = inoutQtyMap.get(productId); 

			        if (!productQty.equals(inoutQty)) {
			            notMatched = true;
			            
			            if(productQty > inoutQty){
			            	inavlidInout = true;
			            }
			            
			            break; 
			        }
			    }
			}
			
			if (inavlidInout) {
				response.setError(true);
				response.setErrorMessage("Cannot scan more than required quantity");
				return Response.status(Status.NOT_FOUND).entity(response).build();
			}
			
			// Get dispatch locator
			List<MLocator> locators = MLocatorType_Custom.getLocatorsByType(ctx, trxName, oldInout.getM_Warehouse_ID(),
					"dispatch", "Y");
			if (locators == null || locators.isEmpty()) {
				response.setError(true);
				response.setErrorMessage("Dispatch locator not found");
				return Response.status(Status.BAD_REQUEST).entity(response).build();
			}

			int dispatchLocatorId = locators.get(0).getM_Locator_ID();
			
			MInOut currentShipment = oldInout;
			MOrder order = new MOrder(ctx, oldInout.getC_Order_ID(), trxName);
			if(notMatched) {
				MTable docTypeTable = MTable.get(ctx, "c_doctype");
				PO docTypePO = docTypeTable.getPO("name = 'MM Shipment' and ad_client_id = " + clientId + "",
						trx.getTrxName());
				MDocType docType = (MDocType) docTypePO;

				MInOut newInout = new MInOut(order, docType.get_ID(), order.getDateOrdered());
				newInout.setDocStatus(DocAction.STATUS_Drafted);
				newInout.saveEx();
				
				// Create shipment lines
				for (Map.Entry<Integer, Integer> entry : productQtyMap.entrySet()) {
					int productId = entry.getKey();
					int totalQty = entry.getValue();

					MProduct product = new MProduct(ctx, productId, trxName);
					int orderLineId = 0;

					// Find order line ID from old shipment
					for (MInOutLine oldLine : oldLines) {
						if (oldLine.getM_Product_ID() == productId) {
							MInOutLine inOutLine = new MInOutLine(ctx, oldLine.get_ID(), trxName);
							BigDecimal qty = oldLine.getQtyEntered().subtract(BigDecimal.valueOf(totalQty));
							if (qty.intValue() <= 0) {
								inOutLine.deleteEx(true);
							} else {
								inOutLine.setQty(qty);
								inOutLine.saveEx();
							}
							
							orderLineId = oldLine.getC_OrderLine_ID();
							break;
						}
					}

					int C_InvoiceLine_ID = 0;
					int M_RMALine_ID = 0;

					newInout.createLineFrom(orderLineId, C_InvoiceLine_ID, M_RMALine_ID, productId, product.getC_UOM_ID(),
							BigDecimal.valueOf(totalQty), dispatchLocatorId);
				}
				
				currentShipment = newInout;
			}
			
			// Complete the shipment
			currentShipment.setDocStatus(DocAction.ACTION_Complete);
			currentShipment.setDocAction(DocAction.ACTION_Close);
			currentShipment.setPosted(true);
			currentShipment.setProcessed(true);
			currentShipment.setIsApproved(true);
			currentShipment.completeIt();
			currentShipment.saveEx();

			// Create sales labels and handle label splitting
			Map<Integer, Integer> productOrderLineMap = new HashMap<>();
			Map<Integer, Integer> productInoutLineMap = new HashMap<>();
			MInOutLine[] newLines = currentShipment.getLines();
			for (MInOutLine line : newLines) {
				productOrderLineMap.put(line.getM_Product_ID(), line.getC_OrderLine_ID());
				productInoutLineMap.put(line.getM_Product_ID(), line.getM_InOutLine_ID());
			}

			for (CreateShipmentByLabelRequest.ShipmentLabelLine line : request.getLines()) {
				String labelUUID = line.getLabelUUID();
				int usedQty = line.getUsedQuantity();
				int productId = line.getProductId();

				// Get original label
				List<PO> poList = PiProductLabel.getPiProductLabel("labelUUId", labelUUID, ctx, trxName, false);
				if (poList.isEmpty())
					continue;

				PiProductLabel originalLabel = new PiProductLabel(ctx, poList.get(0).get_ID(), trxName);
				int labelQty = originalLabel.getquantity().intValue();

				// Split label if needed
				if (usedQty != labelQty) {
					PiProductLabel splitLabel = new PiProductLabel(ctx, trxName, clientId, orgId, productId,
							dispatchLocatorId, 0, originalLabel.getM_InOutLine_ID(), false, BigDecimal.valueOf(usedQty), null);

					splitLabel.saveEx();

					labelUUID = splitLabel.getlabeluuid();
					originalLabel.setquantity(BigDecimal.valueOf(labelQty - usedQty));
					originalLabel.saveEx();
				}

				// Create sales label
				PiProductLabel salesLabel = new PiProductLabel(ctx, trxName, clientId, orgId, productId,
						dispatchLocatorId, productOrderLineMap.get(productId), productInoutLineMap.get(productId), true, BigDecimal.valueOf(usedQty), labelUUID);
				salesLabel.saveEx();
				
			}

			trx.commit();
			response.setError(false);

		} catch (Exception e) {
			if (trx != null) {
				trx.rollback();
			}
			response.setError(true);
			response.setErrorMessage(e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(response).build();

		} finally {
			if (trx != null) {
				trx.close();
			}
		}

		return Response.ok(response).build();
	}

	@Override
	public Response updateProductLabel(UpdateProductLabelRequest request) {
		UpdateProductLabelResponse response = new UpdateProductLabelResponse();

		String trxName = null;
		Trx trx = null;

		try {
			Properties ctx = Env.getCtx();
			int orgId = Env.getAD_Org_ID(ctx);

			trxName = Trx.createTrxName(getClass().getName() + "_updateLabel");
			trx = Trx.get(trxName, true);
			trx.start();

			String labelUUID = request.getLabelUUID();
			int quantity = request.getQuantity();
			boolean mergeStatus = request.isMergeStatus();

			// Get first label
			List<PO> poList = PiProductLabel.getPiProductLabel("labelUUId", labelUUID, ctx, trxName, false);
			if (poList == null || poList.size() != 1) {
				response.setError(true);
				response.setErrorMessage("Invalid Product Label");
				return Response.status(Status.NOT_FOUND).entity(response).build();
			}

			PiProductLabel productLabel = new PiProductLabel(ctx, poList.get(0).get_ID(), trxName);
			int productQty = productLabel.getquantity().intValue();
			int productId = productLabel.getM_Product_ID();

			if (productId == 0) {
				response.setError(true);
				response.setErrorMessage("Label does not have a product");
				return Response.status(Status.BAD_REQUEST).entity(response).build();
			}

			if (!mergeStatus) {
				// Split operation
				if (quantity <= 0 || productQty <= quantity) {
					response.setError(true);
					response.setErrorMessage("Invalid quantity for split operation");
					return Response.status(Status.BAD_REQUEST).entity(response).build();
				}

				// Update original label
				int remainingQty = productQty - quantity;
				productLabel.setquantity(BigDecimal.valueOf(remainingQty));
				productLabel.saveEx();

				// Create new split label
				PiProductLabel newLabel = new PiProductLabel(ctx, 0, trxName);
				newLabel.setAD_Org_ID(orgId);
				newLabel.setQcpassed(productLabel.qcpassed());
				newLabel.setM_Product_ID(productLabel.getM_Product_ID());
				newLabel.setM_Locator_ID(productLabel.getM_Locator_ID());
				newLabel.setquantity(BigDecimal.valueOf(quantity));
				newLabel.setC_OrderLine_ID(productLabel.getC_OrderLine_ID());
				newLabel.setM_InOutLine_ID(productLabel.getM_InOutLine_ID());
				newLabel.setIsSOTrx(productLabel.isSOTrx());
				newLabel.saveEx();

				MProduct product = new MProduct(ctx, productId, trxName);
				response.setProductLabelId(newLabel.getpi_productLabel_ID());
				response.setProductLabelUUId(newLabel.getlabeluuid());
				response.setProductName(product.getName());
				response.setQuantity(quantity);

			} else {
				// Merge operation
				String labelUUID2 = request.getLabelUUID2();
				if (labelUUID.trim().equalsIgnoreCase(labelUUID2.trim())) {
					response.setError(true);
					response.setErrorMessage("Both labels are the same");
					return Response.status(Status.BAD_REQUEST).entity(response).build();
				}

				// Get second label
				List<PO> poList2 = PiProductLabel.getPiProductLabel("labelUUId", labelUUID2, ctx, trxName, false);
				if (poList2 == null || poList2.size() != 1) {
					response.setError(true);
					response.setErrorMessage("Invalid Second Product Label");
					return Response.status(Status.NOT_FOUND).entity(response).build();
				}

				PiProductLabel productLabel2 = new PiProductLabel(ctx, poList2.get(0).get_ID(), trxName);
				int productQty2 = productLabel2.getquantity().intValue();
				int productId2 = productLabel2.getM_Product_ID();

				if (productId2 == 0 || productId != productId2) {
					response.setError(true);
					response.setErrorMessage("Labels have different products");
					return Response.status(Status.BAD_REQUEST).entity(response).build();
				}

				if (productQty2 <= 0) {
					response.setError(true);
					response.setErrorMessage("Second label quantity invalid");
					return Response.status(Status.BAD_REQUEST).entity(response).build();
				}

				// Merge quantities into first label
				int totalQty = productQty + productQty2;
				productLabel.setquantity(BigDecimal.valueOf(totalQty));
				productLabel.saveEx();

				// Delete second label
				productLabel2.deleteEx(true);

				MProduct product = new MProduct(ctx, productId, trxName);
				response.setProductLabelId(productLabel.getpi_productLabel_ID());
				response.setProductLabelUUId(productLabel.getlabeluuid());
				response.setProductName(product.getName());
				response.setQuantity(totalQty);
			}

			trx.commit();
			response.setError(false);

		} catch (Exception e) {
			if (trx != null) {
				trx.rollback();
			}
			response.setError(true);
			response.setErrorMessage(e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(response).build();

		} finally {
			if (trx != null) {
				trx.close();
			}
		}

		return Response.ok(response).build();
	}

	@Override
	public Response getClientConfig() {
		ClientConfigResponse response = new ClientConfigResponse();

		try {
			ClientConfigResponse.ClientConfigItem configItem = new ClientConfigResponse.ClientConfigItem();
			configItem.setKey("clientName");
			configItem.setValue("RWPL");

			response.getClientConfig().add(configItem);
			response.setError(false);

			return Response.ok(response).build();

		} catch (Exception e) {
			response.setError(true);
			response.setErrorMessage(e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(response).build();
		}
	}

	@Override
	public Response updateShipmentCustomer(UpdateShipmentCustomerRequest request) {
		UpdateShipmentCustomerResponse response = new UpdateShipmentCustomerResponse();

		String trxName = null;
		Trx trx = null;

		try {
			Properties ctx = Env.getCtx();

			trxName = Trx.createTrxName(getClass().getName() + "_updateShipment");
			trx = Trx.get(trxName, true);
			trx.start();

			int mInoutId = request.getMInoutId();
			String deliveryType = request.getDeliveryType();
			int shipperId = request.getShipperId();

			MInOut_Custom mInout = new MInOut_Custom(ctx, mInoutId, trxName);
			if (mInout.getM_InOut_ID() == 0) {
				response.setError(true);
				response.setErrorMessage("Invalid shipment ID: " + mInoutId);
				return Response.status(Status.NOT_FOUND).entity(response).build();
			}

			if ("shipper".equalsIgnoreCase(deliveryType)) {
				mInout.setDeliveryViaRule("S");
				mInout.setM_Shipper_ID(shipperId);
			} else if ("pickup".equalsIgnoreCase(deliveryType)) {
				mInout.setDeliveryViaRule("P");
			} else if ("delivery".equalsIgnoreCase(deliveryType)) {
				mInout.setDeliveryViaRule("D");
			}

			mInout.saveEx();
			trx.commit();
			response.setError(false);

			return Response.ok(response).build();

		} catch (Exception e) {
			if (trx != null) {
				trx.rollback();
			}
			response.setError(true);
			response.setErrorMessage(e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(response).build();

		} finally {
			if (trx != null) {
				trx.close();
			}
		}
	}

	@Override
	public Response getShipperForClient() {
		ShipperListResponse response = new ShipperListResponse();

		try {
			Properties ctx = Env.getCtx();
			int clientId = Env.getAD_Client_ID(ctx);

			// Add delivery types
			ShipperListResponse.DeliveryType shipper = new ShipperListResponse.DeliveryType();
			shipper.setDeliveryType("shipper");
			shipper.setShowDetails(true);
			response.getDeliveryTypes().add(shipper);

			ShipperListResponse.DeliveryType pickup = new ShipperListResponse.DeliveryType();
			pickup.setDeliveryType("pickup");
			pickup.setShowDetails(false);
			response.getDeliveryTypes().add(pickup);

			ShipperListResponse.DeliveryType delivery = new ShipperListResponse.DeliveryType();
			delivery.setDeliveryType("delivery");
			delivery.setShowDetails(false);
			response.getDeliveryTypes().add(delivery);

			// Get shipper list
			List<PO> poList = PipraUtils.getShipperListForClient(ctx, clientId);
			if (poList != null) {
				for (PO po : poList) {
					org.compiere.model.MShipper mShipper = (org.compiere.model.MShipper) po;
					ShipperListResponse.ShipperItem shipperItem = new ShipperListResponse.ShipperItem();
					shipperItem.setShipperId(mShipper.getM_Shipper_ID());
					shipperItem.setShipperName(mShipper.getName());
					response.getShipperList().add(shipperItem);
				}
			}

			response.setError(false);
			return Response.ok(response).build();

		} catch (Exception e) {
			response.setError(true);
			response.setErrorMessage(e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(response).build();
		}
	}

	@Override
	public Response createFailedQty(MRFailedRequest request) {
		com.pipra.rwpl.model.response.MRFailedResponse response = new com.pipra.rwpl.model.response.MRFailedResponse();
		Trx trx = null;

		try {
			Properties ctx = Env.getCtx();
			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();

			int mInOutID = request.getMInoutId();
			MTable table = MTable.get(ctx, "m_inout");
			PO po = table.getPO(mInOutID, trx.getTrxName());
			if (po == null) {
				response.setError(true);
				response.setErrorMessage("order not found for " + mInOutID);
				return Response.status(Status.NOT_FOUND).entity(response).build();
			}

			MInOut mInOut = (MInOut) po;
			MInOutConfirm mInOutConfirm = new MInOutConfirm(mInOut, "PC");
			mInOutConfirm.setDocStatus(DocAction.STATUS_Drafted);
			mInOutConfirm.saveEx();

			MInOutLine[] lines = mInOut.getLines(false);
			for (MInOutLine line : lines) {
				MInOutLineConfirm lineConfirm = new MInOutLineConfirm(mInOutConfirm);
				lineConfirm.setM_InOutLine_ID(line.get_ID());
				lineConfirm.setTargetQty(line.getMovementQty());
				lineConfirm.setConfirmedQty(line.getMovementQty());
				lineConfirm.saveEx();

				for (com.pipra.rwpl.mode.request.MRFailedRequest.MRFailedLine failedLine : request.getMrFailedLines()) {
					int data = failedLine.getmInOutLineId();
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

			response.setError(false);
			response.setCreateConfirmationDocumentNumber(mInOutConfirm.getDocumentNo());
			response.setCreateConfirmationId(mInOutConfirm.get_ID());

			return Response.ok(response).build();

		} catch (Exception e) {
			if (trx != null) {
				trx.rollback();
			}
			response.setError(true);
			response.setErrorMessage(e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(response).build();

		} finally {
			if (trx != null) {
				trx.close();
			}
		}
	}

	@Override
	public Response markMrQcChecked(QcCheckRequest request) {
		QcCheckResponse response = new QcCheckResponse();
		Trx trx = null;

		try {
			Properties ctx = Env.getCtx();
			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();

			if (request.isQcChecked()) {
				MInOut_Custom mInOut_Custom = new MInOut_Custom(ctx, request.getMInoutId(), trxName);
				mInOut_Custom.setPickStatus("QC");
				mInOut_Custom.saveEx();
			}

			response.setError(false);
			trx.commit();
			return Response.ok(response).build();

		} catch (Exception e) {
			if (trx != null) {
				trx.rollback();
			}
			response.setError(true);
			response.setErrorMessage(e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(response).build();

		} finally {
			if (trx != null) {
				trx.close();
			}
		}
	}

	@Override
	public Response getLocatorDetailsById(int locatorId, boolean getAvailableLabels, boolean availableProducts) {
		LocatorDetailResponse response = new LocatorDetailResponse();
		String trxName = null;
		
		try {
			Properties ctx = Env.getCtx();
			int clientId = Env.getAD_Client_ID(ctx);
			
			MLocator mLocator = new MLocator(ctx, locatorId, trxName);
			if (mLocator.getM_Locator_ID() == 0) {
				response.setError(true);
				response.setErrorMessage("Invalid Locator ID " + locatorId);
				return Response.status(Status.NOT_FOUND).entity(response).build();
			}
			
			response.setLocatorId(mLocator.getM_Locator_ID());
			response.setLocatorName(mLocator.getValue());
			response.setAisle(mLocator.getX());
			response.setLevel(mLocator.getY());
			response.setBin(mLocator.getZ());
			response.setLocatorTypeId(mLocator.getM_LocatorType_ID());
			response.setLocatorType(mLocator.getM_LocatorType().getName());
			response.setWarehouseId(mLocator.getM_Warehouse_ID());
			response.setWarehouseName(mLocator.getWarehouseName());
			
			if (getAvailableLabels) {
				List<Integer> productLabelIds = PiProductLabel.getPiProductLabelsByLocator(ctx, trxName, clientId, locatorId);
				if (productLabelIds != null && !productLabelIds.isEmpty()) {
					for (Integer id : productLabelIds) {
						PiProductLabel label = new PiProductLabel(ctx, id, trxName);
						ProductLabelLine line = new ProductLabelLine();
						line.setProductLabelUUId(label.getlabeluuid());
						line.setProductId(label.getM_Product_ID());
						line.setQuantity(label.getquantity().intValue());
						line.setLabelId(label.get_ID());
						line.setProductName(label.getM_Product().getName());
						response.getLabelLines().add(line);
					}
				}
			}
			
			if (availableProducts) {
				List<Integer> productLabelIds = PiProductLabel.getPiProductLabelsByLocator(ctx, trxName, clientId, locatorId);
				if (productLabelIds != null && !productLabelIds.isEmpty()) {
					for (Integer id : productLabelIds) {
						PiProductLabel label = new PiProductLabel(ctx, id, trxName);
						boolean flag = false;
						for (LocatorDetailResponse.ProductLine line : response.getProductLines()) {
							if (line.getProductId() == label.getM_Product_ID()) {
								line.setQuantity(line.getQuantity() + label.getquantity().intValue());
								flag = true;
								break;
							}
						}
						if (!flag) {
							LocatorDetailResponse.ProductLine line = new LocatorDetailResponse.ProductLine();
							line.setProductId(label.getM_Product_ID());
							line.setQuantity(label.getquantity().intValue());
							line.setProductName(label.getM_Product().getName());
							response.getProductLines().add(line);
						}
					}
				}
			}
			
			response.setError(false);
			return Response.ok(response).build();
			
		} catch (Exception e) {
			response.setError(true);
			response.setErrorMessage(e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(response).build();
		}
	}

}
