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
import org.realmeds.tissue.moduller.I_TC_MediaLine;
import org.realmeds.tissue.moduller.X_TC_MediaOrder;

public class TCMediaOrder extends X_TC_MediaOrder implements DocAction,DocOptions{

	private static final long serialVersionUID = -7751500318226324547L;

	public TCMediaOrder(Properties ctx, int TC_MediaOrder_ID, String trxName) {
		super(ctx, TC_MediaOrder_ID, trxName);
		// TODO Auto-generated constructor stub
	}
	
	public TCMediaOrder(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
		// TODO Auto-generated constructor stub
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
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean invalidateIt() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public String prepareIt() {
		setC_DocType_ID(getC_DocTypeTarget_ID());
		return DocAction.STATUS_InProgress;
	}

	@Override
	public boolean approveIt() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean rejectIt() {
		// TODO Auto-generated method stub
		return true;
	}
	
	public TCMediaLine[] getTcMediaLines ()
    {
        List<TCMediaLine> list = new Query(getCtx(), I_TC_MediaLine.Table_Name, "TC_mediaorder_ID=?", get_TrxName())
        .setParameters(getTC_mediaorder_ID())//getTC_mediaorder_ID
        .setOrderBy(TCMediaLine.COLUMNNAME_TC_MediaLine_ID)
        .list();
        
        TCMediaLine[] tcMedialineArray = new TCMediaLine[list.size()];
        list.toArray(tcMedialineArray);
        return tcMedialineArray;
    }

	@Override
	public String completeIt() {
		// TODO Auto-generated method stub.
		
		TCMediaLine[] tcMediaLineArray  = getTcMediaLines();
		for(TCMediaLine tcMediaLine : tcMediaLineArray) {
			
			BigDecimal Qty = tcMediaLine.getQuantity();
			MProduct product = (MProduct) tcMediaLine.getM_Product();
			MLocator locator = (MLocator) tcMediaLine.getM_Locator();
			Timestamp dateMPolicy= null;
			
			if (MStorageOnHand.add(getCtx(),
					locator.getM_Locator_ID(),
					product.getM_Product_ID(),
					product.getM_AttributeSetInstance_ID(),
					Qty,getDateOrdered(),
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
							onHand.negate(),storage.getDateMaterialPolicy(),get_TrxName()))
					
					Qty = Qty.add(onHand);
				}
			}
			if (dateMPolicy == null && storages.length > 0)
				dateMPolicy = storages[0].getDateMaterialPolicy();
		}
			}
		setProcessed(true);
		return DocAction.STATUS_Completed;
	}

	@Override
	public boolean voidIt() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean closeIt() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean reverseCorrectIt() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean reverseAccrualIt() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean reActivateIt() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public String getSummary() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDocumentInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public File createPDF() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getProcessMsg() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getDoc_User_ID() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getC_Currency_ID() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public BigDecimal getApprovalAmt() {
		// TODO Auto-generated method stub
		return BigDecimal.ONE;
	}
	@Override
	protected boolean beforeSave(boolean newRecord) {
		if (getc_uuid() == null) {
			setc_uuid(TCUtills.generateUUID());
		}
		// TODO Auto-generated method stub
		return super.beforeSave(newRecord);
	}
}
