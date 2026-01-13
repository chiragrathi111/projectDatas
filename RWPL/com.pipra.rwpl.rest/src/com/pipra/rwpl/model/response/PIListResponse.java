package com.pipra.rwpl.model.response;

import java.util.List;

public class PIListResponse {
    private boolean isError;
    private String error;
    private int count;
    private List<PIComponent> piList;

    public boolean isError() {
        return isError;
    }

    public void setError(boolean isError) {
        this.isError = isError;
    }

    public String getError() {
        return error;
    }

    public void setError(String errorMessage) {
        this.error = errorMessage;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<PIComponent> getPiList() {
        return piList;
    }

    public void setPiList(List<PIComponent> piList) {
        this.piList = piList;
    }
}