package com.pipra.rwpl.mode.request;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties; 

@JsonIgnoreProperties(ignoreUnknown = true)
public class QcCheckRequest {
    private int mInoutId;
    private boolean qcChecked;

    public int getMInoutId() {
        return mInoutId;
    }

    public void setMInoutId(int mInoutId) {
        this.mInoutId = mInoutId;
    }

    public boolean isQcChecked() {
        return qcChecked;
    }

    public void setQcChecked(boolean qcChecked) {
        this.qcChecked = qcChecked;
    }
}
