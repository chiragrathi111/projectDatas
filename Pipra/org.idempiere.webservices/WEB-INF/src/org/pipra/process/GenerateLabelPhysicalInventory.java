package org.pipra.process;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.compiere.process.DocAction;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.pipra.model.custom.PiProductLabel;
import org.pipra.model.custom.X_pi_InventoryDetail;
import org.pipra.model.custom.X_pi_InventoryLine;
import org.pipra.model.custom.X_pi_LabelInventory;

public class GenerateLabelPhysicalInventory extends SvrProcess {

    private int p_pi_LabelInventory_ID = 0;

    @Override
    protected void prepare() {
        p_pi_LabelInventory_ID = getRecord_ID();
    }

    @Override
    protected String doIt() throws Exception {
        if (p_pi_LabelInventory_ID <= 0)
            throw new IllegalArgumentException("No Label Inventory selected");

        X_pi_LabelInventory header = new X_pi_LabelInventory(getCtx(), p_pi_LabelInventory_ID, get_TrxName());
        int warehouseId = header.getM_Warehouse_ID();

        if(header.getDocStatus().equals(DocAction.ACTION_Complete)) {
            throw new IllegalArgumentException("Document Action Completed");
        }
        
        // Delete existing lines
        DB.executeUpdateEx("DELETE FROM adempiere.pi_InventoryDetail WHERE pi_InventoryLine_ID IN " +
                "(SELECT pi_InventoryLine_ID FROM adempiere.pi_InventoryLine WHERE pi_LabelInventory_ID=?)",
                new Object[]{p_pi_LabelInventory_ID}, get_TrxName());
        DB.executeUpdateEx("DELETE FROM adempiere.pi_InventoryLine WHERE pi_LabelInventory_ID=?",
                new Object[]{p_pi_LabelInventory_ID}, get_TrxName());

        // Fetch labels
        List<PiProductLabel> labels = PiProductLabel.getAvailableProductLabels(warehouseId, getCtx(), get_TrxName());

        // Group by locator + product
        Map<String, X_pi_InventoryLine> lineMap = new LinkedHashMap<>();
        Map<String, BigDecimal> qtyMap = new HashMap<>();
        
        int lineNo = 10;
        for (PiProductLabel label : labels) {
//            PiProductLabel label = (PiProductLabel) po;
            String key = label.getM_Locator_ID() + "_" + label.getM_Product_ID();
            
            X_pi_InventoryLine line = lineMap.get(key);
            if (line == null) {
                line = new X_pi_InventoryLine(getCtx(), 0, get_TrxName());
                line.setpi_LabelInventory_ID(p_pi_LabelInventory_ID);
                line.setM_Locator_ID(label.getM_Locator_ID());
                line.setM_Product_ID(label.getM_Product_ID());
                line.setLine(lineNo);
                line.setQtyBook(BigDecimal.ZERO);
                line.setQtyCount(BigDecimal.ZERO);
                line.saveEx();
                lineMap.put(key, line);
                qtyMap.put(key, BigDecimal.ZERO);
                lineNo += 10;
            }
            
            // Create detail
            X_pi_InventoryDetail detail = new X_pi_InventoryDetail(getCtx(), 0, get_TrxName());
            detail.setpi_InventoryLine_ID(line.getpi_InventoryLine_ID());
            detail.setpi_productLabel_ID(label.getpi_productLabel_ID());
            detail.setLabelUUID(label.getlabeluuid());
            detail.setQtyBook(label.getquantity());
            detail.setQtyCount(BigDecimal.ZERO);
            detail.saveEx();
            
            // Update line qty
            BigDecimal totalQty = qtyMap.get(key).add(label.getquantity());
            qtyMap.put(key, totalQty);
            line.setQtyBook(totalQty);
            line.saveEx();
        }

        return "Generated " + lineMap.size() + " lines with " + labels.size() + " labels";
    }
}
