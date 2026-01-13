package com.pipra.rwpl.mode.request;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties; 

@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateShipmentByLabelRequest {
    private int mInoutId;
    private int cOrderId;
    private List<ShipmentLabelLine> lines;
	private String description;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
    public int getMInoutId() {
        return mInoutId;
    }

    public void setMInoutId(int mInoutId) {
        this.mInoutId = mInoutId;
    }

    public List<ShipmentLabelLine> getLines() {
        return lines;
    }

    public void setLines(List<ShipmentLabelLine> lines) {
        this.lines = lines;
    }

	public int getcOrderId() {
		return cOrderId;
	}

	public void setcOrderId(int cOrderId) {
		this.cOrderId = cOrderId;
	}



	public static class ShipmentLabelLine {
        private String labelUUID;
        private int productId;
        private int usedQuantity;

        public String getLabelUUID() {
            return labelUUID;
        }

        public void setLabelUUID(String labelUUID) {
            this.labelUUID = labelUUID;
        }

        public int getProductId() {
            return productId;
        }

        public void setProductId(int productId) {
            this.productId = productId;
        }

        public int getUsedQuantity() {
            return usedQuantity;
        }

        public void setUsedQuantity(int usedQuantity) {
            this.usedQuantity = usedQuantity;
        }
    }
}