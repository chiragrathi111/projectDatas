package com.pipra.rwpl.mode.request;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties; 

@JsonIgnoreProperties(ignoreUnknown = true)
public class RemoveDamagedQtyRequest {
    private String labelUUID;
    private int quantity;

    public String getLabelUUID() {
        return labelUUID;
    }

    public void setLabelUUID(String labelUUID) {
        this.labelUUID = labelUUID;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}