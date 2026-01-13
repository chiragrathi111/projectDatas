package com.pipra.rwpl.mode.request;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties; 

@JsonIgnoreProperties(ignoreUnknown = true)
public class MRFailedRequest {
    private int mInoutId;
    private List<MRFailedLine> mrFailedLines;

	public int getMInoutId() {
		return mInoutId;
	}

	public void setMInoutId(int mInoutId) {
		this.mInoutId = mInoutId;
	}

    public List<MRFailedLine> getMrFailedLines() {
        return mrFailedLines;
    }

    public void setMrFailedLines(List<MRFailedLine> mrFailedLines) {
        this.mrFailedLines = mrFailedLines;
    }

    public static class MRFailedLine {
        private int mInOutLineId;
        private int failedQty;

        public int getmInOutLineId() {
            return mInOutLineId;
        }

        public void setmInOutLineId(int mInOutLineId) {
            this.mInOutLineId = mInOutLineId;
        }

        public int getFailedQty() {
            return failedQty;
        }

        public void setFailedQty(int failedQty) {
            this.failedQty = failedQty;
        }
    }
}
