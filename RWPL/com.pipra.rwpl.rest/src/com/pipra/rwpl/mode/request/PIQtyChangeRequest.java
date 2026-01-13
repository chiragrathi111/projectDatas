package com.pipra.rwpl.mode.request;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties; 

@JsonIgnoreProperties(ignoreUnknown = true)
public class PIQtyChangeRequest {
    private int piId;
    private List<PILineRequest> piLines;

    public int getPiId() {
        return piId;
    }

    public void setPiId(int piId) {
        this.piId = piId;
    }

    public List<PILineRequest> getPiLines() {
        return piLines;
    }

    public void setPiLines(List<PILineRequest> piLines) {
        this.piLines = piLines;
    }
}