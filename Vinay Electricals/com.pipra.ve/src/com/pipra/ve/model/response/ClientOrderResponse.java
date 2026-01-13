package com.pipra.ve.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ClientOrderResponse {

    @JsonProperty("CreateOrderResponse")
    private CreateOrderResponse createOrderResponse;

    public CreateOrderResponse getCreateOrderResponse() {
        return createOrderResponse;
    }

    public void setCreateOrderResponse(CreateOrderResponse createOrderResponse) {
        this.createOrderResponse = createOrderResponse;
    }
}
