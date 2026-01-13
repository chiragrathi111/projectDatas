package org.realmeds.tissue.custom;

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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.idempiere.adInterface.x10.*;

@Path("/realmeds_customservice/")
@Consumes({ "application/xml", "application/json" })
//@Produces({ "application/xml", "application/json" })
@Produces({ "application/xml", "application/json", MediaType.APPLICATION_OCTET_STREAM})
@WebService(targetNamespace = "http://idempiere.org/ADInterface/1_0")
@SOAPBinding(style = Style.RPC, use = Use.LITERAL, parameterStyle = ParameterStyle.WRAPPED)
public interface RealMedsTissueWebservice {
	
	@POST
	@RateLimited
	@Path("/login_api")
	public LoginApiResponseDocument loginApi(LoginApiRequestDocument req);
	
	@Path("token/refresh")
	@POST
	public TokenRefreshDocument tokenRefresh(TokenRefreshDocument requst);
	
	@POST
	@Path("/get_list")
	public GetListResponseDocument getList(GetListRequestDocument req);

	@POST
	@Path("/get_listcode")
	public GetCodeListResponseDocument getCodeList(GetCodeListRequestDocument req);
	
	@POST
	@Path("/farmer_register")
	public FarmerRegisterResponseDocument addFarmer(FarmerRegisterRequestDocument req);
	
	@POST
	@Path("/list_visit")
	public GetVisitResponseDocument getVisit(GetVisitRequestDocument req);
	
	@POST
	@Path("/add_visit")
	public AddVisitResponseDocument addVisit(AddVisitRequestDocument req);
	
	@POST
	@Path("/update_firstvisit")
	public UpdateFirstVisitResponseDocument updateFirstVisit(UpdateFirstVisitRequestDocument req);
	
	@POST
	@Path("/add_plantdetail")
	public AddPlantDetailResponseDocument addPlantDetail(AddPlantDetailRequestDocument req);
	
	@POST
	@Path("/list_plantdetils")
	public GetPlantDetailResponseDocument getPlantDetail(GetPlantDetailRequestDocument req);
	
	@POST
	@Path("/add_cultureOperation")
	public AddCultureOperationResponseDocument addCultureOperation(AddCultureOperationRequestDocument req);
	
	@POST
	@Path("/add_orderlabel")
	public AddOrderLabelResponseDocument addOrderlabel(AddOrderLabelRequestDocument req);
	
	@POST
	@Path("/get_culturelabel")
	public GetCultureLabelResponseDocument getCultureLabel(GetCultureLabelRequestDocument req);

	@POST
	@Path("/internal_move")
	public InternalMoveQuantityResponseDocument internalMoveQuantity(InternalMoveQuantityRequestDocument req);
	
	@POST
	@Path("/add_medialabel")
	public AddMediaLabelResponseDocument addMediaLabel(AddMediaLabelRequestDocument req);
	
	@POST
	@Path("/get_medialabel")
	public GetMediaLabelResponseDocument getMediaLabel(GetMediaLabelRequestDocument req);
	
	@POST
	@Path("/add_explantOperation")
	public AddExplantOperationResponseDocument addExplantOperation(AddExplantOperationRequestDocument req);
	
	@POST
	@Path("/get_explantLabel")
	public GetExplantLabelResponseDocument getExplantLabel(GetExplantLabelRequestDocument req);
	
	@POST
	@Path("/get_locatorDetail")
	public GetLocatorDetailResponseDocument getLocatorDetail(GetLocatorDetailRequestDocument req);
	
	@POST
	@Path("/add_hardeningDetail")
	public AddPrimaryHardeningDetailResponseDocument addPrimaryHardeningDetail(AddPrimaryHardeningDetailRequestDocument req);
	
	@POST
	@Path("/get_primaryhardeningLabel")
	public GetPrimaryHardeningLabelResponseDocument getPrimaryHardeningLabel(GetPrimaryHardeningLabelRequestDocument req);
	
	@POST
	@Path("/add_secondaryhardeningDetail")
	public AddSecondaryHardeningDetailResponseDocument addSecondaryHardeningDetail(AddSecondaryHardeningDetailRequestDocument req);
	
	@POST
	@Path("/upcoming_visit")
	public GetUpcomingVisitResponseDocument getUpcomingVisit(GetUpcomingVisitRequestDocument req);
	
	@POST
	@Path("/change_visitdate")
	public ChangeVisitDateResponseDocument changeVisitDate(ChangeVisitDateRequestDocument req);
	
	@POST
	@Path("/cancel_visit")
	public CancelVisitResponseDocument cancelVisit(CancelVisitRequestDocument req);
	
	@POST
	@Path("/addnext_visit")
	public AddNextVisitDatetForFVResponseDocument addNextVisitDatetForFV(AddNextVisitDatetForFVRequestDocument req);
	
	@POST
	@Path("/get_firstvisit")
	public GetFirstVisitByIdResponseDocument getFirstVisitById(GetFirstVisitByIdRequestDocument req);
	
	@POST
	@Path("/reject_plant")
	public RejectPlantResponseDocument rejectPlant(RejectPlantRequestDocument req);
	
	@POST
	@Path("/update_intermediatevisit")
	public UpdateIntermediateVisitResponseDocument updateIntermediateVisit(UpdateIntermediateVisitRequestDocument req);
	
	@POST
	@Path("/get_planttag")
	public GetPlantTagResponseDocument getPlantTag(GetPlantTagRequestDocument req);
	
	@POST
	@Path("/addIV_nextvisitdate")
	public AddNextVisitDatetForIVResponseDocument addNextVisitDatetForIV(AddNextVisitDatetForIVRequestDocument req);
	
	@POST
	@Path("/update_collectionvisit")
	public UpdateCollectionVisitResponseDocument updateCollectionVisit(UpdateCollectionVisitRequestDocument req);
	
	@POST
	@Path("/discard_medialabel")
	public DiscardMediaLabelResponseDocument discardMediaLabel(DiscardMediaLabelRequestDocument req);
	
	@POST
	@Path("/discard_culturelabel")
	public DiscardCultureLabelResponseDocument discardCultureLabel(DiscardCultureLabelRequestDocument req);
	
	@POST
	@Path("/get_intermediatevisit")
	public GetIntermediateVisitResponseDocument getIntermediateVisit(GetIntermediateVisitRequestDocument req);
	
	@POST
	@Path("/get_collectionvisit")
	public GetCollectionVisitResponseDocument getCollectionVisit(GetCollectionVisitRequestDocument req);
	
	@POST
	@Path("/get_firstvisitsbyvisitId")
	public GetFirstVisitByVisitIdResponseDocument getFirstVisitbyvisitId(GetFirstVisitByVisitIdRequestDocument req);
	
	@POST
	@Path("/get_allfarmerlist")
	public GetAllFarmerListResponseDocument getAllFarmerList(GetAllFarmerListRequestDocument req);
	
	@POST
	@Path("/get_farmerlistbyid")
	public GetFarmerListByIdResponseDocument getFarmerListById(GetFarmerListByIdRequestDocument req);
	
	@POST
	@Path("/get_visitlistbyfarmerid")
	public GetVisitListByFarmerIdResponseDocument getVisitListByFarmerId(GetVisitListByFarmerIdRequestDocument req);
	
	@POST
	@Path("/get_plantdetailslistbyfarmerid")
	public GetPlantDetailsListByFarmerIdResponseDocument getPlantDetailsListByFarmerId(GetPlantDetailsListByFarmerIdRequestDocument req);
	
	@POST
	@Path("/add_iotrecord")
	public AddIotRecordResponseDocument addIotRecord(AddIotRecordRequestDocument req);
	
	@POST
	@Path("/getfiledofficerrolereport")
	public GetFORoleReportResponseDocument getFiledOfficerRoleReport(GetFORoleReportRequestDocument req);
	
	@POST
	@Path("/getforreportsuckercount")
	public GetFORoleReportSuckerCountResponseDocument getFORoleReportSuckerCount(GetFORoleReportSuckerCountRequestDocument req);
	
	@POST
	@Path("/getltrolereportlabelcount")
	public GetLTRoleReportCultureLabelCountResponseDocument getLTRoleReportCultureLabelCount(GetLTRoleReportCultureLabelCountRequestDocument req);
	
	@POST
	@Path("/getmtrolereportlabelcount")
	public GetMTRoleReportMediaLabelCountResponseDocument getMTRoleReportMediaLabelCount(GetMTRoleReportMediaLabelCountRequestDocument req);
	
	@POST
	@Path("/getmtrolereportdiscardlabelcount")
	public GetMTRoleReportDiscardLabelCountResponseDocument getMTRoleReportDiscardLabelCount(GetMTRoleReportDiscardLabelCountRequestDocument req);
	
	@POST
	@Path("/getqarolereportdiscardlabelcount")
	public GetQARoleReportDiscardLabelCountResponseDocument getQARoleReportDiscardLabelCount(GetQARoleReportDiscardLabelCountRequestDocument req);
	
	@POST
	@Path("/getiotdevicerecordbyid")
	public GetIOTDeviceRecordByIdResponseDocument getIOTDeviceRecordById(GetIOTDeviceRecordByIdRequestDocument req);
	
	@POST
	@Path("/gettctroletosubculturereport")
	public GetTCTRoleToSubCultureReportResponseDocument getTCTRoleToSubCultureReport(GetTCTRoleToSubCultureReportRequestDocument req);
	
	@POST
	@Path("/getqaroleallocatestoragereport")
	public GetQARoleAllocateStorageReportResponseDocument getQARoleAllocateStorageReport(GetQARoleAllocateStorageReportRequestDocument req);
	
	@POST
	@Path("/downloadexcel")
	public DownloadExcelApiForVisitCountResponseDocument downloadExcelApiForVisitCount(DownloadExcelApiForVisitCountRequestDocument req);
	
	@POST
	@Path("/getvillagelist")
	public GetVillageListResponseDocument getVillageList(GetVillageListRequestDocument req);
	
	@POST
	@Path("/getqaroleexpiryreport")
	public GetQARoleExpiryReportResponseDocument getQARoleExpiryReport(GetQARoleExpiryReportRequestDocument req);
	
	@POST
	@Path("/getqaroleworklogapi")
	public GetQARoleWorkLogResponseDocument getQARoleWorkLog(GetQARoleWorkLogRequestDocument req);
	
	@POST
	@Path("/getallworklogrecords")
	public GetAllWorkLogRecordsResponseDocument getAllWorkLogRecords(GetAllWorkLogRecordsRequestDocument req);
	
	@POST
	@Path("/getallpendingrecords")
	public GetAllPendingRecordsResponseDocument getAllPendingRecords(GetAllPendingRecordsRequestDocument req);
	
	@POST
	@Path("/getdiscardlistrecords")
	public GetDiscardListResponseDocument getDiscardList(GetDiscardListRequestDocument req);
	
	@POST
	@Path("/addcultureordertoallrecords")
	public AddCultureOrderToAllRecordsResponseDocument addCultureOrderToAllRecords(AddCultureOrderToAllRecordsRequestDocument req);
	
	@POST
	@Path("/gettctroleworklogapi")
	public GetTCTRoleWorkLogResponseDocument getTCTRoleWorkLog(GetTCTRoleWorkLogRequestDocument req);
	
	@POST
	@Path("/gettctroleworklodetailsgapi")
	public GetTCTRoleWorkLogDetailsRecordsResponseDocument getTCTRoleWorkLogDetailsRecords(GetTCTRoleWorkLogDetailsRecordsRequestDocument req);
	
	@POST
	@Path("/getmtroleworklogapi")
	public GetMTRoleWorkLogResponseDocument getMTRoleWorkLog(GetMTRoleWorkLogRequestDocument req);
	
	@POST
	@Path("/getmtroleworklogdetailsapi")
	public GetMTRoleWorkLogDetailsResponseDocument getMTRoleWorkLogDetails(GetMTRoleWorkLogDetailsRequestDocument req);
	
	@GET
	@Path("/gettokenvalidation")
	public GetTokenValidationResponseDocument getTokenValidation(@QueryParam("userId") int userId, @QueryParam("token") String token);
	
	@POST
	@Path("/updateuserpassword")
	public UpdateUserPasswordResponseDocument updateUserPassword(UpdateUserPasswordRequestDocument req);
	
	@POST
	@Path("/getvillagenamelist")
	public GetVillageNameListResponseDocument getVillageNameList(GetVillageNameListRequestDocument req);
	
	@POST
	@Path("/getcitynamelist")
	public Response getCityNameList(GetCityNameListRequestDocument req);
	
	@POST
	@Path("/gettcmediaorderlist")
	public GetTCMediaOrderListResponseDocument getTCMediaOrderList(GetTCMediaOrderListRequestDocument req);
	
	@POST
	@Path("/createmediaorder")
	public CreateMediaOrderResponseDocument createMediaOrder(CreateMediaOrderRequestDocument req);
	
	@POST
	@Path("/gettcpf")
	public GetTCPFResponseDocument getTCPF(GetTCPFRequestDocument req);
	
	@POST
	@Path("/getpersonalcode")
	public GetPersonalCodeResponseDocument getPersonalCode(GetPersonalCodeRequestDocument req);
	
	@POST
	@Path("/getmachinecode")
	public GetMachineCodeResponseDocument getMachineCode(GetMachineCodeRequestDocument req);
	
	@POST
	@Path("/getmediadiscardlabeldetails")
	public GetMediaDiscardLabelDetailsResponseDocument getMediaDiscardLabelDetails(GetMediaDiscardLabelDetailsRequestDocument req);
	
	@POST
	@Path("/getmtrolecompletedcountDetails")
	public GetMTRoleCompletedCountDetailsResponseDocument getMTRoleCompletedCountDetails(GetMTRoleCompletedCountDetailsRequestDocument req);
	
	@POST
	@Path("/gettraceability")
	public GetTraceabilityResponseDocument getTraceability(GetTraceabilityRequestDocument req);
	
	@POST
	@Path("/createorderid")
	public CreateOrderIdAndInIdResponseDocument createOrderIdAndInId(CreateOrderIdAndInIdRequestDocument req);
	
	@POST
	@Path("/createorderidforexplant")
	public CreateOrderIdForExplantResponseDocument createOrderIdForExplant(CreateOrderIdForExplantRequestDocument req);
	
	@POST
	@Path("/createorderidforculture")
	public CreateOrderIdForCultureResponseDocument createOrderIdForCulture(CreateOrderIdForCultureRequestDocument req);
	
	@POST
	@Path("/getLabelDetails")
	public GetLabelDetailsResponseDocument getLabelDetails(GetLabelDetailsRequestDocument req);
	
	@POST
	@Path("/createorderidforprimaryhardening")
	public CreateOrderIdForPrimaryHResponseDocument createOrderIdForPrimaryH(CreateOrderIdForPrimaryHRequestDocument req);
	
	@POST
	@Path("/getqaroleexpirycounts")
	public GetQARoleExpiryCountsResponseDocument getQARoleExpiryCounts(GetQARoleExpiryCountsRequestDocument req);
	
	@POST
	@Path("/deleteattachmentrecord")
	public DeleteAttachmentRecordUsingIndexResponseDocument deleteAttachmentRecordUsingIndex(DeleteAttachmentRecordUsingIndexRequestDocument req);
	
	@POST
	@Path("/updatefarmerdetail")
	public UpdateFarmerDetailResponseDocument updateFarmerDetail(UpdateFarmerDetailRequestDocument req);
	
	@POST
	@Path("/getmedialabelexpirytotalcount")
	public GetMediaLabelExpiryCountResponseDocument getMediaLabelExpiryCount(GetMediaLabelExpiryCountRequestDocument req);
	
	@POST
	@Path("/getmedialabelexpirylist")
	public GetMediaLabelExpiryListResponseDocument getMediaLabelExpiryList(GetMediaLabelExpiryListRequestDocument req);
	
	@GET
    @Path("/img/tableId/{tableId}/id/{id}/index/{index}")
    @Produces("image/png") 
    public Response getImage( @PathParam("tableId") int tableId, @PathParam("id") int id, @PathParam("index") int index);
	
	@POST
    @Path("/uploadfile")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public StandardResponseDocument uploadfile(@Multipart("files") List<File> files, @Multipart("tableId") int tableId,@Multipart("recordId") int recordId);
	
	 
	
	@POST
	@Path("/getforreportsuckercountdownload")
	@Produces("file/xlsx")
	public Response getFORoleReportSuckerCountDownload(GetFORoleReportSuckerCountDownloadRequestDocument req);
	
	@POST
	@Path("/getforreportvisitcountdownload")
	@Produces("file/xlsx")
	public Response getFORoleReportVisitCountDownload(GetFORoleReportVisitCountDownloadRequestDocument req);
	
	@POST
	@Path("/add_lightrecord")
	public AddLightRecordResponseDocument addLightRecord(AddLightRecordRequestDocument req);
	
	@POST
	@RateLimited
	@Path("/generatetokenandsendmailnew")
	public GenerateTokenAndSendMailNewResponseDocument generateTokenAndSendMailNew(GenerateTokenAndSendMailNewRequestDocument req);
	
	@POST
	@RateLimited
	 @Path("/gettracedataforqr")
	 public GetTraceabilityDataForQRResponseDocument getTraceabilityDataForQR(GetTraceabilityDataForQRRequestDocument req);
	
	@POST
	@Path("/set_docaction")
	public StandardResponseDocument setDocAction(ModelSetDocActionRequestDocument req);
}

