package com.pipra.rwpl.model.response;

import java.util.ArrayList;
import java.util.List;

public class ClientConfigResponse {
    private boolean isError;
    private String error;
    private List<ClientConfigItem> clientConfig;

    public ClientConfigResponse() {
        this.clientConfig = new ArrayList<>();
    }

    public boolean isError() {
        return isError;
    }

    public void setError(boolean isError) {
        this.isError = isError;
    }

    public String getErrorMessage() {
        return error;
    }

    public void setErrorMessage(String errorMessage) {
        this.error = errorMessage;
    }

    public List<ClientConfigItem> getClientConfig() {
        return clientConfig;
    }

    public void setClientConfig(List<ClientConfigItem> clientConfig) {
        this.clientConfig = clientConfig;
    }

    public static class ClientConfigItem {
        private String key;
        private String value;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
