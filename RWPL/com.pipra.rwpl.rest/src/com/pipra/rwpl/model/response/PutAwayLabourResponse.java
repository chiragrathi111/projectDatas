package com.pipra.rwpl.model.response;

import java.util.List;

public class PutAwayLabourResponse {
    private boolean isError;
    private String error;
    private int count;
    private List<PutAwayLabourComponent> putAwayLabour;

    public boolean isError() {
        return isError;
    }

    public void setError(boolean isError) {
        this.isError = isError;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<PutAwayLabourComponent> getPutAwayLabour() {
        return putAwayLabour;
    }

    public void setPutAwayLabour(List<PutAwayLabourComponent> putAwayLabour) {
        this.putAwayLabour = putAwayLabour;
    }
}