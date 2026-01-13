package org.pipra.webservices.custom;

import java.io.File;
import java.util.List;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.idempiere.adInterface.x10.*;


@Path("/pipra_customservice/")
@Consumes({ "application/xml", "application/json", MediaType.MULTIPART_FORM_DATA})
@Produces({ "application/xml", "application/json" })
@WebService(targetNamespace = "http://idempiere.org/ADInterface/1_0")
@SOAPBinding(style = Style.RPC, use = Use.LITERAL, parameterStyle = ParameterStyle.WRAPPED)
public interface PipraCustomWebservice {

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
	@Path("/generate_palletDetails")
	public GeneratePalletDetailsResponseDocument generatePalletDetails(GeneratePalletDetailsRequestDocument req);
	
	@POST
	@Path("/generateProductLabel")
	public GenerateProductLabelResponseDocument generateProductLabel(GenerateProductLabelRequestDocument req);
	
	@POST
	@Path("/getLabelData")
	public GetLabelDataResponseDocument getLabelData(GetLabelDataRequestDocument req);
	
	@POST
	@Path("/get_qRData")
	public GetQRDataResponseDocument getQRData(GetQRDataRequestDocument req);
	
	@POST
	@Path("/linkOr_unlinkPallet")
	public StandardResponseDocument linkOrUnlinkPallet(LinkOrUnlinkPalletRequestDocument req);
	
	@POST
	@Path("/locator_suggestion")
	public LocatorSuggestionResponseDocument getLocatorSuggestionForProduct(LocatorSuggestionRequestDocument req);
	
	@POST
	@Path("/qc_check")
	public StandardResponseDocument markMrQcChecked(MarkMRQcCheckedRequestDocument req);
	
	@POST
	@Path("/qc_list")
	public QcCOListResponseDocument getQcCOMrList(QCListRequestDocument req);
	
	@POST
	@Path("/qc_data")
	public QcDataResponseDocument getQcCOMrData(PODataRequestDocument req);
	
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
	@Path("/d2c")
	public DToCResponseDocument d2cOrder(DToCRequestDocument req);
	
	@POST
	@Path("/warehouselist")
	public WarehouseLocatorListResponseDocument wareList(WarehouseLocatorListRequestDocument req);
	
	@POST
	@Path("/so_orderStatus")
	public SOOrderStatusResponseDocument soOrderStatus(SOOrderStatusRequestDocument req);
	
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
	@Path("/getIsUserActive")
	public StandardResponseDocument getIsUserActive(GetIsUserActiveRequestDocument req);
	
//	@POST
//	@Path("/createNotice")
//	public StandardResponseDocument createNotice(CreateNoticeRequestDocument req);
	
	@POST
	@Path("/createRoi")
	public StandardResponseDocument createRoi(CreateRoiDocument req);
	
	@POST
	@Path("/createNotice")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response createNotice(@Multipart("file") List<File> files, @Multipart("user") String user,
			@Multipart("pass") String pass, @Multipart("clientId") int clientId, @Multipart("orgId") int orgId,
			@Multipart("roleId") int roleId, @Multipart("warehouseId") int warehouseId,
			@Multipart("message") String message, @Multipart("textMessage") String textMessage,
			@Multipart("description") String description, @Multipart("fileName") String fileName);

	@POST
	@Path("/getMrComponents")
	public GetMRComponentsResponseDocument getMrComponents(GetMRComponentsRequestDocument req);
	
	@POST
	@Path("/set_docaction")
	public StandardResponseDocument setDocAction(ModelSetDocActionRequestDocument req);
	
	@POST
	@Path("/getUsersList")
	public GetUsersListResponseDocument getUsersList(GetUsersListRequestDocument req);
	
	@POST
	@Path("/livestream")
	public String livestream();
	
	@POST
    @Path("/uploadfile")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public StandardResponseDocument uploadfile(@Multipart("files") List<File> files,@Multipart("user") String user,@Multipart("pass") String pass, @Multipart("clientId") int clientId,@Multipart("orgId") int orgId,@Multipart("roleId") int roleId,@Multipart("warehouseId") int warehouseId,@Multipart("tableId") int tableId,@Multipart("recordId") int recordId);
	
	@POST
	@Path("/switchstate")
	public SwitchStateResponseDocument switchstate(SwitchStateRequestDocument req);
	
	@POST
	@Path("/createiteminout")
	public CreateItemInoutResponseDocument createItemInout(CreateItemInoutRequestDocument req);

	@POST
	@Path("/getiteminout")
	public GetItemInoutResponseDocument getItemInout(GetItemInoutRequestDocument req);

	@POST
	@Path("/getunauthorisedlist")
	public GetUnauthorisedListResponseDocument getUnauthorisedList(GetUnauthorisedListRequestDocument req);

	@POST
	@Path("/labelinventorylist")
	public LabelInventoryListResponseDocument getLabelInventoryList(LabelInventoryListRequestDocument req);

	@POST
	@Path("/labelinventorydetail")
	public LabelInventoryDetailResponseDocument getLabelInventoryDetail(LabelInventoryDetailRequestDocument req);

	@POST
	@Path("/updateinventorycount")
	public UpdateInventoryCountResponseDocument updateInventoryCount(UpdateInventoryCountRequestDocument req);

	@POST
	@Path("/rmalist")
	public RMAListResponseDocument getRMAList(RMAListRequestDocument req);

	@POST
	@Path("/rmadetail")
	public RMADetailResponseDocument getRMADetail(RMADetailRequestDocument req);

	@POST
	@Path("/getrmacomponents")
	public GetRMAComponentsResponseDocument getRMAComponents(GetRMAComponentsRequestDocument req);

	@POST
	@Path("/createrma")
	public StandardResponseDocument createRMA(CreateRMARequestDocument req);

	@POST
	@Path("/customerreturnlist")
	public CustomerReturnListResponseDocument getCustomerReturnList(CustomerReturnListRequestDocument req);

	@POST
	@Path("/customerreturndetail")
	public CustomerReturnDetailResponseDocument getCustomerReturnDetail(CustomerReturnDetailRequestDocument req);

	@POST
	@Path("/createcustomerreturn")
	public CreateCustomerReturnResponseDocument createCustomerReturn(CreateCustomerReturnRequestDocument req);

	@GET
    @Path("/img/tableId/{tableId}/id/{id}/index/{index}")
    @Produces("image/png") 
    public Response getImage( @PathParam("tableId") int tableId, @PathParam("id") int id, @PathParam("index") int index);
}