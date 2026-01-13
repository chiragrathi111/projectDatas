package com.pipra.rwpl.service;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.pipra.rwpl.mode.request.CreateShipmentByLabelRequest;
import com.pipra.rwpl.mode.request.CreateShipmentRequest;
import com.pipra.rwpl.mode.request.EditShipmentRequest;
import com.pipra.rwpl.mode.request.GenerateProductLabelRequest;
import com.pipra.rwpl.mode.request.GetLabelDataRequest;
import com.pipra.rwpl.mode.request.MRCreateRequest;
import com.pipra.rwpl.mode.request.MRFailedRequest;
import com.pipra.rwpl.mode.request.PIQtyChangeRequest;
import com.pipra.rwpl.mode.request.PutAwayRequest;
import com.pipra.rwpl.mode.request.QcCheckRequest;
import com.pipra.rwpl.mode.request.RemoveDamagedQtyRequest;
import com.pipra.rwpl.mode.request.SetDocActionRequest;
import com.pipra.rwpl.mode.request.SplitAndMergeLabelRequest;
import com.pipra.rwpl.mode.request.UpdateProductLabelRequest;
import com.pipra.rwpl.mode.request.UpdateShipmentCustomerRequest;

@Path("")
public interface RwplService {

	@GET
	@Path("purchaseOrder/list")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response poList(@QueryParam("searchKey") String searchKey);

	@GET
	@Path("purchaseOrder/detail/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response poData(@PathParam("id") int id);

	@POST
	@Path("getmrlist")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getMRList();

	@GET
	@Path("mrComponents")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getMRComponents();

	@POST
	@Path("roleconfig")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response roleConfig(@QueryParam("deviceToken") String deviceToken);

	@POST
	@Path("createmr")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createMR(MRCreateRequest request);

	@POST
	@Path("mr/failed")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createFailedQty(MRFailedRequest request);

	@POST
	@Path("mr/qcCheck")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response markMrQcChecked(QcCheckRequest request);

	@GET
	@Path("inout/list")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getMRList(@QueryParam("searchKey") String searchKey, @QueryParam("status") String status,
			@QueryParam("isSalesTransaction") String isSalesTransaction);

	@GET
	@Path("inout/detail/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getMrData(@PathParam("id") int id);

	@GET
	@Path("putAway/list")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPutAwayList(@QueryParam("searchKey") String searchKey);

	@GET
	@Path("putAway/detail/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPutAwayDetail(@PathParam("id") int id);

	@GET
	@Path("putAway/labour")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPutAwayLabour();

	@POST
	@Path("putAway")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response putAway(PutAwayRequest request);

	@POST
	@Path("generateLabel")
	@Produces(MediaType.APPLICATION_JSON)
	public Response generateProductLabel(GenerateProductLabelRequest request);

	@POST
	@Path("labelData")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getLabelData(GetLabelDataRequest request);

	@POST
	@Path("label/splitAndMerge")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response splitAndMergeMoveLabel(SplitAndMergeLabelRequest request);

	@POST
	@Path("label/removeDamaged")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response removeDamagedQnty(RemoveDamagedQtyRequest request);

	@GET
	@Path("pi/list")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPhysicalInventoryList(@QueryParam("searchKey") String searchKey);

	@GET
	@Path("pi/detail/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPhysicalInventoryDetailsById(@PathParam("id") int id);

	@POST
	@Path("pi/qtyChange")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response pIQtyChange(PIQtyChangeRequest request);

	@GET
	@Path("so/list")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSOList(@QueryParam("searchKey") String searchKey);

	@GET
	@Path("so/detail/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSODetail(@PathParam("id") int cOrderId);

	@POST
	@Path("shipment/create")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createShipment(CreateShipmentRequest request);

	@POST
	@Path("shipment/edit")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response editShipment(EditShipmentRequest request);

	@POST
	@Path("document/setAction")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response setDocAction(SetDocActionRequest request);

	@POST
	@Path("shipment/createByLabel")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createShipmentByLabel(CreateShipmentByLabelRequest request);

	@POST
	@Path("label/update")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateProductLabel(UpdateProductLabelRequest request);

	@GET
	@Path("client/config")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getClientConfig();

	@POST
	@Path("shipment/updateCustomer")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateShipmentCustomer(UpdateShipmentCustomerRequest request);

	@GET
	@Path("shipper/list")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getShipperForClient();

	@GET
	@Path("locator/detail/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getLocatorDetailsById(@PathParam("id") int locatorId, 
			@QueryParam("getAvailableLabels") boolean getAvailableLabels,
			@QueryParam("availableProducts") boolean availableProducts);

}
