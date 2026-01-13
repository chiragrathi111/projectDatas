package org.realmeds.tissue.model;

import java.io.File;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.List;
import java.util.Properties;
import org.compiere.model.MClient;
import org.compiere.model.MLocator;
import org.compiere.model.MProduct;
import org.compiere.model.MStorageOnHand;
import org.compiere.model.Query;
import org.compiere.process.DocAction;
import org.compiere.process.DocOptions;
import org.compiere.process.DocumentEngine;
import org.realmeds.tissue.custom.TCUtills;
import org.realmeds.tissue.moduller.I_TC_MediaOutLine;
import org.realmeds.tissue.moduller.I_TC_in;
import org.realmeds.tissue.moduller.I_TC_out;
import org.realmeds.tissue.moduller.X_TC_order;

public class TCOrder extends X_TC_order implements DocAction,DocOptions{

	private static final long serialVersionUID = 1L;

	public TCOrder(Properties ctx, int TC_order_ID, String trxName) {
		super(ctx, TC_order_ID, trxName);
	}
	
	public TCOrder(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}

	@Override
	public int customizeValidActions(String docStatus, Object processing, String orderType, String isSOTrx,
			int AD_Table_ID, String[] docAction, String[] options, int index) {
		if (options == null)
			throw new IllegalArgumentException("Option array parameter is null");
		if (docAction == null)
			throw new IllegalArgumentException("Doc action array parameter is null");

		if (docStatus.equals(DocumentEngine.STATUS_Drafted) || docStatus.equals(DocumentEngine.STATUS_Invalid)) {
			options[index++] = DocumentEngine.ACTION_Complete;
			options[index++] = DocumentEngine.ACTION_Prepare;
			options[index++] = DocumentEngine.ACTION_Void;

		} else if (docStatus.equals(DocumentEngine.STATUS_Completed)) {
			options[index++] = DocumentEngine.ACTION_Void;
			options[index++] = DocumentEngine.ACTION_ReActivate;
		}

		return index;

	}

	@Override
	public boolean processIt(String action) throws Exception {
		DocumentEngine engine = new DocumentEngine(this, getDocStatus());
		return engine.processIt(action, getDocAction());
	}

	@Override
	public boolean unlockIt() {
		return true;
	}

	@Override
	public boolean invalidateIt() {
		return true;
	}

	@Override
	public String prepareIt() {
		setC_DocType_ID(getC_DocTypeTarget_ID());
		return DocAction.STATUS_InProgress;
	}

	@Override
	public boolean approveIt() {
		return true;
	}

	@Override
	public boolean rejectIt() {
		return true;
	}
	
	public TCIn[] getTcInLines ()
    {
        List<TCIn> list = new Query(getCtx(), I_TC_in.Table_Name, "TC_order_ID=?", get_TrxName())
        .setParameters(getTC_order_ID())
        .setOrderBy(TCIn.COLUMNNAME_TC_in_ID)
        .list();
        
        TCIn[] tcInArray = new TCIn[list.size()];
        list.toArray(tcInArray);
        return tcInArray;
    }
	
	public TCOut[] getTcOutLines() {
	    List<TCOut> list = new Query(getCtx(), I_TC_out.Table_Name, "TC_order_ID=?", get_TrxName())
	            .setParameters(getTC_order_ID())
	            .setOrderBy(TCOut.COLUMNNAME_TC_out_ID)
	            .list();

	    TCOut[] tcOutArray = new TCOut[list.size()];
	    list.toArray(tcOutArray);
	    return tcOutArray;
	}
	
	public TCMediaOutLine[] getTCMediaOutLine() {
	    List<TCMediaOutLine> list = new Query(getCtx(), I_TC_MediaOutLine.Table_Name, "TC_order_ID=?", get_TrxName())
	            .setParameters(getTC_order_ID())
	            .setOrderBy(TCMediaOutLine.COLUMNNAME_TC_MediaOutLine_ID)
	            .list();

	    TCMediaOutLine[] tcOutArray = new TCMediaOutLine[list.size()];
	    list.toArray(tcOutArray);
	    return tcOutArray;
	}

	@Override
	public String completeIt() {

		TCIn[] tcInArray = getTcInLines();
		processTC(tcInArray, true); // true for "in" operation

		TCOut[] tcOutArray = getTcOutLines();
		processTC(tcOutArray, false);
		
		TCMediaOutLine[] tcMediaOutLine = getTCMediaOutLine();
		processTC(tcMediaOutLine, true);//true means reduce Qty
		
		setProcessed(true);
		return DocAction.STATUS_Completed;
	}
	
	public void processTC(TC[] tcArray, boolean isIn) {
	    for (TC tc : tcArray) {
	        BigDecimal Qty = isIn ? tc.getQuantity().negate() : tc.getQuantity();
	        MProduct product = (MProduct) tc.getM_Product();
	        MLocator locator = (MLocator) tc.getM_Locator();
	        Timestamp dateMPolicy = null;

	        if (MStorageOnHand.add(getCtx(),
	                locator.getM_Locator_ID(),
	                product.getM_Product_ID(),
	                product.getM_AttributeSetInstance_ID(),
	                Qty, getDateOrdered(),
	                get_TrxName())) {

	            MStorageOnHand[] storages = MStorageOnHand.getWarehouse(getCtx(), 0,
	                    product.getM_Product_ID(), product.getM_AttributeSetInstance_ID(), null,
	                    MClient.MMPOLICY_FiFo.equals(product.getMMPolicy()), false,
	                    locator.getM_Locator_ID(), get_TrxName());

	            for (MStorageOnHand storage : storages) {
	                if (Qty.signum() == 0)
	                    break;
	                if (storage.getQtyOnHand().compareTo(Qty.negate()) >= 0) {
	                    dateMPolicy = storage.getDateMaterialPolicy();
	                    break;
	                } else if (storage.getQtyOnHand().signum() > 0) {
	                    BigDecimal onHand = storage.getQtyOnHand();
	                    // this locator has less qty than required, ship all qtyonhand and iterate to next locator
	                    if (!MStorageOnHand.add(getCtx(),
	                            locator.getM_Locator_ID(),
	                            product.getM_Product_ID(),
	                            product.getM_AttributeSetInstance_ID(),
	                            onHand.negate(), storage.getDateMaterialPolicy(), get_TrxName()))

	                        Qty = Qty.add(onHand.negate());
	                }
	            }
	            if (dateMPolicy == null && storages.length > 0)
	                dateMPolicy = storages[0].getDateMaterialPolicy();
	        }
	    }
	}

	@Override
	public boolean voidIt() {
		return true;
	}

	@Override
	public boolean closeIt() {
		return true;
	}

	@Override
	public boolean reverseCorrectIt() {
		return true;
	}

	@Override
	public boolean reverseAccrualIt() {
		return true;
	}

	@Override
	public boolean reActivateIt() {
		return true;
	}

	@Override
	public String getSummary() {
		return null;
	}

	@Override
	public String getDocumentInfo() {
		return null;
	}

	@Override
	public File createPDF() {
		return null;
	}

	@Override
	public String getProcessMsg() {
		return null;
	}

	@Override
	public int getDoc_User_ID() {
		return 0;
	}

	@Override
	public int getC_Currency_ID() {
		return 0;
	}

	@Override
	public BigDecimal getApprovalAmt() {
		return BigDecimal.ONE;
	}

	@Override
	protected boolean beforeSave(boolean newRecord) {
		// TODO Auto-generated method stub
		if (getc_uuid() == null) {
			setc_uuid(TCUtills.generateUUID());
		}
		return super.beforeSave(newRecord);
	}

}
