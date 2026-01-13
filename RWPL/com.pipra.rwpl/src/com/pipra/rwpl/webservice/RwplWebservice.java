package com.pipra.rwpl.webservice;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

//import com.pipra.rwpl.x10.CleanUpDataRequestDocument;
//import com.pipra.rwpl.x10.CleanUpDataResponseDocument;
import com.pipra.rwpl.x10.CreateMRRequestDocument;
import com.pipra.rwpl.x10.CreateMRResponseDocument;
import com.pipra.rwpl.x10.CreateNoticeRequestDocument;
import com.pipra.rwpl.x10.CreateSCByLabelRequestDocument;
import com.pipra.rwpl.x10.CreateSCRequestDocument;
import com.pipra.rwpl.x10.CreateSCResponseDocument;
import com.pipra.rwpl.x10.GenerateProductLabelRequestDocument;
import com.pipra.rwpl.x10.GenerateProductLabelResponseDocument;
import com.pipra.rwpl.x10.GetClientConfigRequestDocument;
import com.pipra.rwpl.x10.GetClientConfigResponseDocument;
import com.pipra.rwpl.x10.GetIsUserActiveRequestDocument;
import com.pipra.rwpl.x10.GetLabelDataRequestDocument;
import com.pipra.rwpl.x10.GetLabelDataResponseDocument;
import com.pipra.rwpl.x10.GetMRComponentsRequestDocument;
import com.pipra.rwpl.x10.GetMRComponentsResponseDocument;
import com.pipra.rwpl.x10.LocatorDeatilRequestDocument;
import com.pipra.rwpl.x10.LocatorDeatilResponseDocument;
import com.pipra.rwpl.x10.LoginApiRequestDocument;
import com.pipra.rwpl.x10.LoginApiResponseDocument;
import com.pipra.rwpl.x10.MRDataResponseDocument;
import com.pipra.rwpl.x10.MRFailedRequestDocument;
import com.pipra.rwpl.x10.MRFailedResponceDocument;
import com.pipra.rwpl.x10.MRListRequestDocument;
import com.pipra.rwpl.x10.MRListResponseDocument;
import com.pipra.rwpl.x10.MarkMRQcCheckedRequestDocument;
import com.pipra.rwpl.x10.MarkSOToPickRequestDocument;
import com.pipra.rwpl.x10.ModelSetDocActionRequestDocument;
import com.pipra.rwpl.x10.PIDeatilsRequestDocument;
import com.pipra.rwpl.x10.PIDeatilsResponseDocument;
import com.pipra.rwpl.x10.PIListRequestDocument;
import com.pipra.rwpl.x10.PIListResponseDocument;
import com.pipra.rwpl.x10.PIQtyChangeRequestDocument;
import com.pipra.rwpl.x10.PIQtyChangeResponseDocument;
import com.pipra.rwpl.x10.PODataRequestDocument;
import com.pipra.rwpl.x10.PODataResponseDocument;
import com.pipra.rwpl.x10.POListRequestDocument;
import com.pipra.rwpl.x10.POListResponseDocument;
import com.pipra.rwpl.x10.PickDetailLabourRequestDocument;
import com.pipra.rwpl.x10.PickDetailLabourResponseDocument;
import com.pipra.rwpl.x10.PickListLabourRequestDocument;
import com.pipra.rwpl.x10.PickListLabourResponseDocument;
import com.pipra.rwpl.x10.PutAwayDetailRequestDocument;
import com.pipra.rwpl.x10.PutAwayDetailResponseDocument;
import com.pipra.rwpl.x10.PutAwayLabourRequestDocument;
import com.pipra.rwpl.x10.PutAwayLabourResponseDocument;
import com.pipra.rwpl.x10.PutAwayListRequestDocument;
import com.pipra.rwpl.x10.PutAwayListResponseDocument;
import com.pipra.rwpl.x10.PutAwayRequestDocument;
import com.pipra.rwpl.x10.RoleConfigureRequestDocument;
import com.pipra.rwpl.x10.RoleConfigureResponseDocument;
import com.pipra.rwpl.x10.SODetailRequestDocument;
import com.pipra.rwpl.x10.SODetailResponseDocument;
import com.pipra.rwpl.x10.SOListRequestDocument;
import com.pipra.rwpl.x10.SOListResponseDocument;
import com.pipra.rwpl.x10.ShipmentReceiptByScanRequestDocument;
import com.pipra.rwpl.x10.ShipperListRequestDocument;
import com.pipra.rwpl.x10.ShipperListResponseDocument;
import com.pipra.rwpl.x10.SplitAndMergeMoveLabelRequestDocument;
import com.pipra.rwpl.x10.SplitAndMergeMoveLabelResponseDocument;
import com.pipra.rwpl.x10.StandardResponseDocument;
import com.pipra.rwpl.x10.UpdateProductLabelRequestDocument;
import com.pipra.rwpl.x10.UpdateProductLabelResponseDocument;
import com.pipra.rwpl.x10.UpdateShipmentRequestDocument;

@Path("/")
@Consumes({ "application/xml", "application/json" })
@Produces({ "application/xml", "application/json" })
@WebService(targetNamespace = "http://pipra.com/Rwpl/1_0")
@SOAPBinding(style = Style.RPC, use = Use.LITERAL, parameterStyle = ParameterStyle.WRAPPED)
public interface RwplWebservice {

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
	@Path("/getMrComponents")
	public GetMRComponentsResponseDocument getMrComponents(GetMRComponentsRequestDocument req);

	@POST
	@Path("/create_mr")
	public CreateMRResponseDocument createMR(CreateMRRequestDocument req);

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
	public GetLabelDataResponseDocument getLabelData(GetLabelDataRequestDocument req);

	@POST
	@Path("/qc_check")
	public StandardResponseDocument markMrQcChecked(MarkMRQcCheckedRequestDocument req);

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

	@POST
	@Path("/createNotice")
	public StandardResponseDocument createNotice(CreateNoticeRequestDocument req);

	@POST
	@Path("/set_docaction")
	public StandardResponseDocument setDocAction(ModelSetDocActionRequestDocument req);

	@POST
	@Path("/getClientConfig")
	public GetClientConfigResponseDocument getClientConfig(GetClientConfigRequestDocument req);

	@POST
	@Path("/updateProductLabel")
	public UpdateProductLabelResponseDocument updateProductLabel(UpdateProductLabelRequestDocument req);

	@POST
	@Path("/createSCByLabel")
	public StandardResponseDocument createShipmentByLabel(CreateSCByLabelRequestDocument createSCRequestDocument);
	
	@POST
	@Path("/editShipment")
	public StandardResponseDocument editShipment(CreateSCByLabelRequestDocument createSCRequestDocument);
	
	@POST
	@Path("/splitAndMergeMoveLabel")
	public SplitAndMergeMoveLabelResponseDocument splitAndMergeMoveLabel(SplitAndMergeMoveLabelRequestDocument req);
	
//	@POST
//	@Path("/cleanupdata")
//	public CleanUpDataResponseDocument cleanupData(CleanUpDataRequestDocument req);
	
	@POST
	@Path("/removeDamagedQnty")
	public StandardResponseDocument removeDamagedQntyFromLabel(UpdateProductLabelRequestDocument req);
}
