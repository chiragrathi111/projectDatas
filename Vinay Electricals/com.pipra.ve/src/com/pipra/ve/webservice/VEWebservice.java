package com.pipra.ve.webservice;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.pipra.ve.x10.*;

@Path("/")
@Consumes({ "application/xml", "application/json"})
@Produces({ "application/xml", "application/json"})
@WebService(targetNamespace = "http://pipra.com/VE/1_0")
@SOAPBinding(style = Style.RPC, use = Use.LITERAL, parameterStyle = ParameterStyle.WRAPPED)
public interface VEWebservice {
	
	@POST
	@Path("/generateLabel")
	public GenerateLabelResponseDocument generateLabel(GenerateLabelRequestDocument req);
	
	@POST
	@Path("/attachChildLabelToParent")
	public GenerateLabelResponseDocument attachChildLabelToParent(ParentLabelRequestDocument req);
	
	@POST
	@Path("/createPurchaseOrder")
	public StandardResponseDocument createPurchaseOrder(String req);
	
	@POST
	@Path("/createSalesOrder")
	public StandardResponseDocument createSalesOrder(String req);
	
	@POST
	@Path("/getDeptAcess")
	public GetDeptAcessResponseDocument getDeptAcess(GetDeptAcessRequestDocument req);
	
	@POST
	@Path("/markShipmentPacked")
	public StandardResponseDocument markShipmentPacked(MarkShipmentPackedRequestDocument req);
	
	@POST
	@Path("/getMOComponents")
	public GetMOComponentsResponseDocument getMOComponents(GetMOComponentsRequestDocument req);
	
	@POST
	@Path("/createMO")
	public CreateMOResponseDocument createMO(CreateMORequestDocument req);
	
	@POST
	@Path("/getMOList")
	public MOListResponseDocument getMOList(MOListRequestDocument req);
	
	@POST
	@Path("/getMODetail")
	public MODetailResponseDocument getMODetail(MODetailRequestDocument req);
	
	@POST
	@Path("/createPAOrder")
	public StandardResponseDocument createPAOrder(PAOrderRequestDocument req);
	
	@POST
	@Path("/getPAOrderList")
	public PAOrderListResponseDocument getPAOrderList(PAOrderListRequestDocument req);
	
	@POST
	@Path("/getPAOrderDetail")
	public PAOrderDetailResponseDocument getPAOrderDetail(PAOrderDetailRequestDocument req); //getPAOrderComponents
	
	@POST
	@Path("/getOrderReceiptList")
	public OrderReceiptListResponseDocument getOrderReceiptList(OrderReceiptListRequestDocument req);
	
	@POST
	@Path("/UpdateOrderReceipt")
	public StandardResponseDocument UpdateOrderReceipt (UpdateOrderReceiptDocument req);
	
	@POST
	@Path("/createMOOrderReceipt")
	public StandardResponseDocument createMOOrderReceipt(CompleteMOOrderRequestDocument req);
	
	@POST
	@Path("/login_api")
	public LoginApiResponseDocument loginApi(LoginApiRequestDocument req);

	@POST
	@Path("/role_configure")
	public RoleConfigureResponseDocument roleConfig(RoleConfigureRequestDocument req);

	@POST
	@Path("/po_list")
	public POListResponseDocument poList(POListRequestDocument req);

	@POST
	@Path("/po_data")
	public PODataResponseDocument poData(PODataRequestDocument req);

	@POST
	@Path("/create_mr")
	public CreateMRResponseDocument createMR(CreateMRRequestDocument req);
	
	@POST
	@Path("/getMInoutForCOrder")
	public GetMInoutForCOrderResponseDocument getMInoutForCOrder(GetMInoutForCOrderRequestDocument req);
	
	@POST
	@Path("/mr_list")
	public MRListResponseDocument getMrList(MRListRequestDocument req);
	
	@POST
	@Path("/mr_data")
	public MRDataResponseDocument getMrData(PODataRequestDocument req);
	
	@POST
	@Path("/mr_failed")
	public MRFailedResponceDocument createFailedQty(MRFailedRequestDocument req);
	
	@POST
	@Path("/shipmentReceiptConfirmByScan")
	public StandardResponseDocument createShipmentReceiptConfirmByScan(ShipmentReceiptByScanRequestDocument req);
	
	@POST
	@Path("/pi_list")
	public PIListResponseDocument getPhysicalInventoryList(PIListRequestDocument req);
	
	@POST
	@Path("/pi_details")
	public PIDeatilsResponseDocument getPhysicalInventoryDetailsById(PIDeatilsRequestDocument req);
	
	@POST
	@Path("/pi_qtyChange")
	public PIQtyChangeResponseDocument pIQtyChange(PIQtyChangeRequestDocument req);
		
	@POST
	@Path("/so_list")
	public SOListResponseDocument soList(SOListRequestDocument req);
	
	@POST
	@Path("/so_detail")
	public SODetailResponseDocument soDetails(SODetailRequestDocument req);
	
	@POST
	@Path("/create_shipment")
	public CreateSCResponseDocument createShipment(CreateSCRequestDocument req);
	
	@POST
	@Path("/generateProductLabel")
	public GenerateProductLabelResponseDocument generateProductLabel(GenerateProductLabelRequestDocument req);
	
	@POST
	@Path("/getLabelData")
	public Response getLabelData(GetLabelDataRequestDocument req);
//	
//	@POST
//	@Path("/get_qRData")
//	public GetQRDataResponseDocument getQRData(GetQRDataRequestDocument req);
	
	@POST
	@Path("/locator_suggestion")
	public LocatorSuggestionResponseDocument getLocatorSuggestionForProduct(LocatorSuggestionRequestDocument req);
	
	@POST
	@Path("/qc_check")
	public StandardResponseDocument markMrQcChecked(MarkMRQcCheckedRequestDocument req);

	@POST
	@Path("/update_mr")
	public StandardResponseDocument updateMr(UpdateMrRequestDocument req);
	
	@POST
	@Path("/putAway")
	public StandardResponseDocument putAway(PutAwayRequestDocument req);
	
	@POST
	@Path("/putAwayList")
	public PutAwayListResponseDocument putAwayList(PutAwayListRequestDocument req);
	
	@POST
	@Path("/putAwayLabour")
	public PutAwayLabourResponseDocument putAwayLabour(PutAwayLabourRequestDocument req);
	
	@POST
	@Path("/putAwayDetail")
	public PutAwayDetailResponseDocument putAwayDetail(PutAwayDetailRequestDocument req);
	
	@POST
	@Path("/warehouselist")
	public WarehouseLocatorListResponseDocument wareList(WarehouseLocatorListRequestDocument req);

	@POST
	@Path("/getLocatorDeatilsById")
	public LocatorDeatilResponseDocument getLocatorDeatilsById(LocatorDeatilRequestDocument req);
	
	@POST
	@Path("/pickListLabour")
	public PickListLabourResponseDocument pickListLabour(PickListLabourRequestDocument req);
	
	@POST
	@Path("/pickDetailLabour")
	public PickDetailLabourResponseDocument pickDetailLabour(PickDetailLabourRequestDocument req);
	
	@POST
	@Path("/markSOReadyToPick")
	public StandardResponseDocument markSOReadyToPick(MarkSOToPickRequestDocument req);
	
	@POST
	@Path("/getShipperForClient")
	public ShipperListResponseDocument getShipperForClient(ShipperListRequestDocument req);
	
	@POST
	@Path("/updateShipmentCustomer")
	public StandardResponseDocument updateShipmentCustomer(UpdateShipmentRequestDocument req);
	
	@POST
	@Path("/updateShipment")
	public StandardResponseDocument updateShipment(UpdateShipmentRequestDocument req);
	
	@POST
	@Path("/getIsUserActive")
	public StandardResponseDocument getIsUserActive(GetIsUserActiveRequestDocument req);
	
	@POST
	@Path("/getClientConfig")
	public GetClientConfigResponseDocument getClientConfig(GetClientConfigRequestDocument req);
	
	@POST
	@Path("/set_docaction")
	public StandardResponseDocument setDocAction(ModelSetDocActionRequestDocument req);
	
	@POST
	@Path("/updateproductlabel")
	public UpdateProductLabelResponseDocument updateProductLabel(UpdateProductLabelRequestDocument req);
	
	@POST
	@Path("/getProductList")
	public GetProductListResponseDocument getProductList(GetProductListRequestDocument req);
	
	@POST
	@Path("/createReturnOrder")
	public StandardResponseDocument createReturnOrder(ReturnOrderRequestDocument req);
	
	@POST
	@Path("/returnOrderList")
	public ReturnOrderListResponseDocument returnOrderList(ReturnOrderListRequestDocument req);
	
	@POST
	@Path("/returnOrderDetails")
	public ReturnOrderDetailsResponseDocument returnOrderDetails(ReturnOrderDetailsRequestDocument req);
	
	@POST
	@Path("/getReturnComponents")
	public GetReturnComponentsResponseDocument getReturnComponents(GetReturnComponentsRequestDocument req);
	
	@POST
	@Path("/editShipment")
	public StandardResponseDocument editShipment(CreateSCByLabelRequestDocument createSCRequestDocument);
	
	@POST
	@Path("/cleanupdata")
	public CleanUpDataResponseDocument cleanupData(CleanUpDataRequestDocument req);
	
	@POST
	@Path("/customer/list")
	public BPartnerListResponseDocument customerList(BPartnerListRequestDocument req);
	
	@POST
	@Path("/create/salesorder")
	public CreateSalesOrderResponseDocument createSalesOrderFromMobile(CreateSalesOrderRequestDocument req);
	
	@POST
	@Path("/update/salesorder")
	public UpdateSalesOrderResponseDocument updateSalesOrder(UpdateSalesOrderRequestDocument req);
	
	@POST
	@Path("/discard/productlabel")
	public StandardResponseDocument discardProductLabel(DiscardProductLabelRequestDocument req);
	
	@POST
	@Path("/createpurchaseorder")
	public CreatePurchaseOrderResponseDocument createPurchaseOrderFromMobile(CreatePurchaseOrderRequestDocument req);
	
	@POST
	@Path("/getMOComponentsNew")
	public GetMOComponentsNewResponseDocument getMOComponentsNew(GetMOComponentsNewRequestDocument req);
}
