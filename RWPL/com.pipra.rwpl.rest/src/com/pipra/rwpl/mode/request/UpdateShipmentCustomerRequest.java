package com.pipra.rwpl.mode.request;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties; 

@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateShipmentCustomerRequest {
    private int mInoutId;
    private String deliveryType;
    private int shipperId;

    public int getMInoutId() {
        return mInoutId;
    }

    public void setMInoutId(int mInoutId) {
        this.mInoutId = mInoutId;
    }

    public String getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(String deliveryType) {
        this.deliveryType = deliveryType;
    }

    public int getShipperId() {
        return shipperId;
    }

    public void setShipperId(int shipperId) {
        this.shipperId = shipperId;
    }
}
