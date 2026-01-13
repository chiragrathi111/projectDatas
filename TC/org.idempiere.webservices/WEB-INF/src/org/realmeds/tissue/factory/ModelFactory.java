package org.realmeds.tissue.factory;

import java.sql.ResultSet;
import org.adempiere.base.IModelFactory;
import org.compiere.model.PO;
import org.realmeds.tissue.model.COrder_Custom_New;
import org.realmeds.tissue.model.SalesRecord;
import org.realmeds.tissue.model.TCCollectionJoinPlantDetails;
import org.realmeds.tissue.model.TCCollectionVisit;
import org.realmeds.tissue.model.TCCultureDetails;
import org.realmeds.tissue.model.TCCultureLabel;
import org.realmeds.tissue.model.TCCultureOperation;
import org.realmeds.tissue.model.TCCultureStage;
import org.realmeds.tissue.model.TCExplantDetail;
import org.realmeds.tissue.model.TCExplantLabel;
import org.realmeds.tissue.model.TCExplantOperation;
import org.realmeds.tissue.model.TCFarmer;
import org.realmeds.tissue.model.TCFirstVisit;
import org.realmeds.tissue.model.TCFirstVisitJoinPlantDetails;
import org.realmeds.tissue.model.TCHardeningDetail;
import org.realmeds.tissue.model.TCHardeningTrayTag;
import org.realmeds.tissue.model.TCIOTdeviceData;
import org.realmeds.tissue.model.TCIn;
import org.realmeds.tissue.model.TCIntermediateJoinPlantDetails;
import org.realmeds.tissue.model.TCIntermediateVisit;
import org.realmeds.tissue.model.TCInvoiceLine;
import org.realmeds.tissue.model.TCMachineType;
import org.realmeds.tissue.model.TCMediaLabel;
import org.realmeds.tissue.model.TCMediaLabelQr;
import org.realmeds.tissue.model.TCMediaLine;
import org.realmeds.tissue.model.TCMediaOrder;
import org.realmeds.tissue.model.TCMediaOutLine;
import org.realmeds.tissue.model.TCMediaType;
import org.realmeds.tissue.model.TCOrder;
import org.realmeds.tissue.model.TCOut;
import org.realmeds.tissue.model.TCPlantDetail;
import org.realmeds.tissue.model.TCPlantTag;
import org.realmeds.tissue.model.TCPrimaryHardeningLabel;
import org.realmeds.tissue.model.TCQualityCheck;
import org.realmeds.tissue.model.TCSecondaryHardeningLabel;
import org.realmeds.tissue.model.TCStatus;
import org.realmeds.tissue.model.TCVisit;

public class ModelFactory implements IModelFactory{

	@Override
	public Class<?> getClass(String tableName) {
		if(tableName.equalsIgnoreCase(TCOut.Table_Name))
			return TCOut.class;
		else if (tableName.equalsIgnoreCase(TCOrder.Table_Name))
			return TCOrder.class;
		else if (tableName.equalsIgnoreCase(TCIn.Table_Name))
			return TCIn.class;
		else if (tableName.equalsIgnoreCase(TCMediaOutLine.Table_Name))
			return TCMediaOutLine.class;
		else if (tableName.equalsIgnoreCase(TCMediaOrder.Table_Name))
			return TCMediaOrder.class;
		else if (tableName.equalsIgnoreCase(TCMediaLine.Table_Name))
			return TCMediaLine.class;
		else if (tableName.equalsIgnoreCase(TCFarmer.Table_Name))
			return TCFarmer.class;
		else if (tableName.equalsIgnoreCase(TCVisit.Table_Name))
			return TCVisit.class;
		else if (tableName.equalsIgnoreCase(TCFirstVisit.Table_Name))
			return TCFirstVisit.class;
		else if (tableName.equalsIgnoreCase(TCPlantDetail.Table_Name))
			return TCPlantDetail.class;
		else if (tableName.equalsIgnoreCase(TCCultureDetails.Table_Name))
			return TCCultureDetails.class;
		else if (tableName.equalsIgnoreCase(TCCultureOperation.Table_Name))
			return TCCultureOperation.class;
		else if (tableName.equalsIgnoreCase(TCCultureStage.Table_Name))
			return TCCultureStage.class;
		else if (tableName.equalsIgnoreCase(TCCultureLabel.Table_Name))
			return TCCultureLabel.class;
		else if (tableName.equalsIgnoreCase(TCMediaLabel.Table_Name))
			return TCMediaLabel.class;
		else if (tableName.equalsIgnoreCase(TCMediaType.Table_Name))
			return TCMediaType.class;
		else if (tableName.equalsIgnoreCase(TCMediaLabelQr.Table_Name))
			return TCMediaLabelQr.class;
		else if (tableName.equalsIgnoreCase(TCExplantDetail.Table_Name))
			return TCExplantDetail.class;
		else if (tableName.equalsIgnoreCase(TCExplantOperation.Table_Name))
			return TCExplantOperation.class;
		else if (tableName.equalsIgnoreCase(TCExplantLabel.Table_Name))
			return TCExplantLabel.class;
		else if (tableName.equalsIgnoreCase(TCQualityCheck.Table_Name))
			return TCQualityCheck.class;
		else if (tableName.equalsIgnoreCase(TCPlantTag.Table_Name))
			return TCPlantTag.class;
		else if (tableName.equalsIgnoreCase(TCMachineType.Table_Name))
			return TCMachineType.class;
		else if (tableName.equalsIgnoreCase(TCIntermediateVisit.Table_Name))
			return TCIntermediateVisit.class;
		else if (tableName.equalsIgnoreCase(TCHardeningTrayTag.Table_Name))
			return TCHardeningTrayTag.class;
		else if (tableName.equalsIgnoreCase(TCHardeningDetail.Table_Name))
			return TCHardeningDetail.class;
		else if (tableName.equalsIgnoreCase(TCCollectionVisit.Table_Name))
			return TCCollectionVisit.class;
		else if (tableName.equalsIgnoreCase(TCPrimaryHardeningLabel.Table_Name))
			return TCPrimaryHardeningLabel.class;
		else if (tableName.equalsIgnoreCase(TCSecondaryHardeningLabel.Table_Name))
			return TCSecondaryHardeningLabel.class;
		else if (tableName.equalsIgnoreCase(TCStatus.Table_Name))
			return TCStatus.class;
		else if (tableName.equalsIgnoreCase(TCIntermediateJoinPlantDetails.Table_Name))
			return TCIntermediateJoinPlantDetails.class;
		else if (tableName.equalsIgnoreCase(TCCollectionJoinPlantDetails.Table_Name))
			return TCCollectionJoinPlantDetails.class;
		else if (tableName.equalsIgnoreCase(TCFirstVisitJoinPlantDetails.Table_Name))
			return TCFirstVisitJoinPlantDetails.class;
		else if (tableName.equalsIgnoreCase(TCIOTdeviceData.Table_Name))
			return TCIOTdeviceData.class;
		else if (tableName.equalsIgnoreCase(TCInvoiceLine.Table_Name))
			return TCInvoiceLine.class;
		else if (tableName.equalsIgnoreCase(SalesRecord.Table_Name))
			return SalesRecord.class;
		else if (tableName.equalsIgnoreCase(COrder_Custom_New.Table_Name))
			return COrder_Custom_New.class;
		return null;
	}

	@Override
	public PO getPO(String tableName, int Record_ID, String trxName) {
		if(tableName.equalsIgnoreCase(TCOut.Table_Name))
			return new TCOut(null, Record_ID, trxName);
		else if(tableName.equalsIgnoreCase(TCOrder.Table_Name))
			return new TCOrder(null,Record_ID,trxName);
		else if(tableName.equalsIgnoreCase(TCIn.Table_Name))
			return new TCIn(null,Record_ID,trxName);
		else if(tableName.equalsIgnoreCase(TCMediaOutLine.Table_Name))
			return new TCMediaOutLine(null,Record_ID,trxName);
		else if(tableName.equalsIgnoreCase(TCMediaOrder.Table_Name))
			return new TCMediaOrder(null,Record_ID,trxName);
		else if(tableName.equalsIgnoreCase(TCMediaLine.Table_Name))
			return new TCMediaLine(null,Record_ID,trxName);
		else if(tableName.equalsIgnoreCase(TCFarmer.Table_Name))
			return new TCFarmer(null,Record_ID,trxName);
		else if(tableName.equalsIgnoreCase(TCVisit.Table_Name))
			return new TCVisit(null,Record_ID,trxName);
		else if(tableName.equalsIgnoreCase(TCFirstVisit.Table_Name))
			return new TCFirstVisit(null,Record_ID,trxName);
		else if(tableName.equalsIgnoreCase(TCPlantDetail.Table_Name))
			return new TCPlantDetail(null,Record_ID,trxName);
		else if(tableName.equalsIgnoreCase(TCCultureDetails.Table_Name))
			return new TCCultureDetails(null,Record_ID,trxName);
		else if(tableName.equalsIgnoreCase(TCCultureOperation.Table_Name))
			return new TCCultureOperation(null,Record_ID,trxName);
		else if(tableName.equalsIgnoreCase(TCCultureStage.Table_Name))
			return new TCCultureStage(null,Record_ID,trxName);
		else if(tableName.equalsIgnoreCase(TCCultureLabel.Table_Name))
			return new TCCultureLabel(null,Record_ID,trxName);
		else if(tableName.equalsIgnoreCase(TCMediaLabel.Table_Name))
			return new TCMediaLabel(null,Record_ID,trxName);
		else if(tableName.equalsIgnoreCase(TCMediaType.Table_Name))
			return new TCMediaType(null,Record_ID,trxName);
		else if(tableName.equalsIgnoreCase(TCMediaLabelQr.Table_Name))
			return new TCMediaLabelQr(null,Record_ID,trxName);
		else if(tableName.equalsIgnoreCase(TCExplantDetail.Table_Name))
			return new TCExplantDetail(null,Record_ID,trxName);
		else if(tableName.equalsIgnoreCase(TCExplantOperation.Table_Name))
			return new TCExplantOperation(null,Record_ID,trxName);
		else if(tableName.equalsIgnoreCase(TCExplantLabel.Table_Name))
			return new TCExplantLabel(null,Record_ID,trxName);
		else if(tableName.equalsIgnoreCase(TCQualityCheck.Table_Name))
			return new TCQualityCheck(null,Record_ID,trxName);
		else if(tableName.equalsIgnoreCase(TCPlantTag.Table_Name))
			return new TCPlantTag(null,Record_ID,trxName);
		else if(tableName.equalsIgnoreCase(TCMachineType.Table_Name))
			return new TCMachineType(null,Record_ID,trxName);
		else if(tableName.equalsIgnoreCase(TCIntermediateVisit.Table_Name))
			return new TCIntermediateVisit(null,Record_ID,trxName);
		else if(tableName.equalsIgnoreCase(TCHardeningTrayTag.Table_Name))
			return new TCHardeningTrayTag(null,Record_ID,trxName);
		else if(tableName.equalsIgnoreCase(TCHardeningDetail.Table_Name))
			return new TCHardeningDetail(null,Record_ID,trxName);
		else if(tableName.equalsIgnoreCase(TCCollectionVisit.Table_Name))
			return new TCCollectionVisit(null,Record_ID,trxName);
		else if(tableName.equalsIgnoreCase(TCPrimaryHardeningLabel.Table_Name))
			return new TCPrimaryHardeningLabel(null,Record_ID,trxName);
		else if(tableName.equalsIgnoreCase(TCSecondaryHardeningLabel.Table_Name))
			return new TCSecondaryHardeningLabel(null,Record_ID,trxName);
		else if(tableName.equalsIgnoreCase(TCStatus.Table_Name))
			return new TCStatus(null,Record_ID,trxName);
		else if(tableName.equalsIgnoreCase(TCIntermediateJoinPlantDetails.Table_Name))
			return new TCIntermediateJoinPlantDetails(null,Record_ID,trxName);
		else if(tableName.equalsIgnoreCase(TCCollectionJoinPlantDetails.Table_Name))
			return new TCCollectionJoinPlantDetails(null,Record_ID,trxName);
		else if(tableName.equalsIgnoreCase(TCFirstVisitJoinPlantDetails.Table_Name))
			return new TCFirstVisitJoinPlantDetails(null,Record_ID,trxName);
		else if(tableName.equalsIgnoreCase(TCIOTdeviceData.Table_Name))
			return new TCIOTdeviceData(null,Record_ID,trxName);
		else if(tableName.equalsIgnoreCase(TCInvoiceLine.Table_Name))
			return new TCInvoiceLine(null,Record_ID,trxName);
		else if(tableName.equalsIgnoreCase(SalesRecord.Table_Name))
			return new SalesRecord(null,Record_ID,trxName);
		else if(tableName.equalsIgnoreCase(COrder_Custom_New.Table_Name))
			return new COrder_Custom_New(null,Record_ID,trxName);
		return null;
	}

	@Override
	public PO getPO(String tableName, ResultSet rs, String trxName) {
		if(tableName.equalsIgnoreCase(TCOut.Table_Name))
			return new TCOut(null,rs,trxName);
		else if(tableName.equalsIgnoreCase(TCOrder.Table_Name))
			return new TCOrder(null,rs,trxName);
		else if(tableName.equalsIgnoreCase(TCIn.Table_Name))
			return new TCIn(null,rs,trxName);
		else if(tableName.equalsIgnoreCase(TCMediaOutLine.Table_Name))
			return new TCMediaOutLine(null,rs,trxName);
		else if(tableName.equalsIgnoreCase(TCMediaOrder.Table_Name))
			return new TCMediaOrder(null,rs,trxName);
		else if(tableName.equalsIgnoreCase(TCMediaLine.Table_Name))
			return new TCMediaLine(null,rs,trxName);
		else if(tableName.equalsIgnoreCase(TCFarmer.Table_Name))
			return new TCFarmer(null,rs,trxName);
		else if(tableName.equalsIgnoreCase(TCVisit.Table_Name))
			return new TCVisit(null,rs,trxName);
		else if(tableName.equalsIgnoreCase(TCFirstVisit.Table_Name))
			return new TCFirstVisit(null,rs,trxName);
		else if(tableName.equalsIgnoreCase(TCPlantDetail.Table_Name))
			return new TCPlantDetail(null,rs,trxName);
		else if(tableName.equalsIgnoreCase(TCCultureDetails.Table_Name))
			return new TCCultureDetails(null,rs,trxName);
		else if(tableName.equalsIgnoreCase(TCCultureOperation.Table_Name))
			return new TCCultureOperation(null,rs,trxName);
		else if(tableName.equalsIgnoreCase(TCCultureStage.Table_Name))
			return new TCCultureStage(null,rs,trxName);
		else if(tableName.equalsIgnoreCase(TCCultureLabel.Table_Name))
			return new TCCultureLabel(null,rs,trxName);
		else if(tableName.equalsIgnoreCase(TCMediaLabel.Table_Name))
			return new TCMediaLabel(null,rs,trxName);
		else if(tableName.equalsIgnoreCase(TCMediaType.Table_Name))
			return new TCMediaType(null,rs,trxName);
		else if(tableName.equalsIgnoreCase(TCMediaLabelQr.Table_Name))
			return new TCMediaLabelQr(null,rs,trxName);
		else if(tableName.equalsIgnoreCase(TCExplantDetail.Table_Name))
			return new TCExplantDetail(null,rs,trxName);
		else if(tableName.equalsIgnoreCase(TCExplantOperation.Table_Name))
			return new TCExplantOperation(null,rs,trxName);
		else if(tableName.equalsIgnoreCase(TCExplantLabel.Table_Name))
			return new TCExplantLabel(null,rs,trxName);
		else if(tableName.equalsIgnoreCase(TCQualityCheck.Table_Name))
			return new TCQualityCheck(null,rs,trxName);
		else if(tableName.equalsIgnoreCase(TCPlantTag.Table_Name))
			return new TCPlantTag(null,rs,trxName);
		else if(tableName.equalsIgnoreCase(TCMachineType.Table_Name))
			return new TCMachineType(null,rs,trxName);
		else if(tableName.equalsIgnoreCase(TCIntermediateVisit.Table_Name))
			return new TCIntermediateVisit(null,rs,trxName);
		else if(tableName.equalsIgnoreCase(TCHardeningTrayTag.Table_Name))
			return new TCHardeningTrayTag(null,rs,trxName);
		else if(tableName.equalsIgnoreCase(TCHardeningDetail.Table_Name))
			return new TCHardeningDetail(null,rs,trxName);
		else if(tableName.equalsIgnoreCase(TCCollectionVisit.Table_Name))
			return new TCCollectionVisit(null,rs,trxName);
		else if(tableName.equalsIgnoreCase(TCPrimaryHardeningLabel.Table_Name))
			return new TCPrimaryHardeningLabel(null,rs,trxName);
		else if(tableName.equalsIgnoreCase(TCSecondaryHardeningLabel.Table_Name))
			return new TCSecondaryHardeningLabel(null,rs,trxName);
		else if(tableName.equalsIgnoreCase(TCStatus.Table_Name))
			return new TCStatus(null,rs,trxName);
		else if(tableName.equalsIgnoreCase(TCIntermediateJoinPlantDetails.Table_Name))
			return new TCIntermediateJoinPlantDetails(null,rs,trxName);
		else if(tableName.equalsIgnoreCase(TCCollectionJoinPlantDetails.Table_Name))
			return new TCCollectionJoinPlantDetails(null,rs,trxName);
		else if(tableName.equalsIgnoreCase(TCFirstVisitJoinPlantDetails.Table_Name))
			return new TCFirstVisitJoinPlantDetails(null,rs,trxName);
		else if(tableName.equalsIgnoreCase(TCIOTdeviceData.Table_Name))
			return new TCIOTdeviceData(null,rs,trxName);
		else if(tableName.equalsIgnoreCase(TCInvoiceLine.Table_Name))
			return new TCInvoiceLine(null,rs,trxName);
		else if(tableName.equalsIgnoreCase(SalesRecord.Table_Name))
			return new SalesRecord(null,rs,trxName);
		else if(tableName.equalsIgnoreCase(COrder_Custom_New.Table_Name))
			return new COrder_Custom_New(null,rs,trxName);
		return null;
	}

}
