package com.pipra.rwpl.mode.request;

public class PILineRequest {
    private int mInventoryLineId;
    private int countQty;

    public int getmInventoryLineId() {
        return mInventoryLineId;
    }

    public void setmInventoryLineId(int mInventoryLineId) {
        this.mInventoryLineId = mInventoryLineId;
    }

    public int getCountQty() {
        return countQty;
    }

    public void setCountQty(int countQty) {
        this.countQty = countQty;
    }
}