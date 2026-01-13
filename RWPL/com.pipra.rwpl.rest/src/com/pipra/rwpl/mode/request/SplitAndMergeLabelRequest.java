package com.pipra.rwpl.mode.request;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties; 

@JsonIgnoreProperties(ignoreUnknown = true)
public class SplitAndMergeLabelRequest {
    private String labelUUID;
    private String labelUUID2;
    private int quantity;

    public String getLabelUUID() {
        return labelUUID;
    }

    public void setLabelUUID(String labelUUID) {
        this.labelUUID = labelUUID;
    }

    public String getLabelUUID2() {
        return labelUUID2;
    }

    public void setLabelUUID2(String labelUUID2) {
        this.labelUUID2 = labelUUID2;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}