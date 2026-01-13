package com.pipra.rwpl.mode.request;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties; 

@JsonIgnoreProperties(ignoreUnknown = true)
public class PutAwayRequest {
    private boolean finalDispatch;
    private List<PutAwayLineRequest> putAwayLines;

    public boolean isFinalDispatch() {
        return finalDispatch;
    }

    public void setFinalDispatch(boolean finalDispatch) {
        this.finalDispatch = finalDispatch;
    }

    public List<PutAwayLineRequest> getPutAwayLines() {
        return putAwayLines;
    }

    public void setPutAwayLines(List<PutAwayLineRequest> putAwayLines) {
        this.putAwayLines = putAwayLines;
    }
}