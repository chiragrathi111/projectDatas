package com.pipra.rwpl.model.response;

import java.util.ArrayList;
import java.util.List;

public class ShipperListResponse {
    private boolean isError;
    private String errorMessage;
    private List<DeliveryType> deliveryTypes;
    private List<ShipperItem> shipperList;

    public ShipperListResponse() {
        this.deliveryTypes = new ArrayList<>();
        this.shipperList = new ArrayList<>();
    }

    public boolean isError() {
        return isError;
    }

    public void setError(boolean isError) {
        this.isError = isError;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public List<DeliveryType> getDeliveryTypes() {
        return deliveryTypes;
    }

    public void setDeliveryTypes(List<DeliveryType> deliveryTypes) {
        this.deliveryTypes = deliveryTypes;
    }

    public List<ShipperItem> getShipperList() {
        return shipperList;
    }

    public void setShipperList(List<ShipperItem> shipperList) {
        this.shipperList = shipperList;
    }

    public static class DeliveryType {
        private String deliveryType;
        private boolean showDetails;

        public String getDeliveryType() {
            return deliveryType;
        }

        public void setDeliveryType(String deliveryType) {
            this.deliveryType = deliveryType;
        }

        public boolean isShowDetails() {
            return showDetails;
        }

        public void setShowDetails(boolean showDetails) {
            this.showDetails = showDetails;
        }
    }

    public static class ShipperItem {
        private int shipperId;
        private String shipperName;

        public int getShipperId() {
            return shipperId;
        }

        public void setShipperId(int shipperId) {
            this.shipperId = shipperId;
        }

        public String getShipperName() {
            return shipperName;
        }

        public void setShipperName(String shipperName) {
            this.shipperName = shipperName;
        }
    }
}
