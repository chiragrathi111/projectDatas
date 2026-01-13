package com.pipra.rwpl.mode.request;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties; 

@JsonIgnoreProperties(ignoreUnknown = true)
public class SetDocActionRequest {
    private String tableName;
    private int recordId;
    private String docAction;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public int getRecordId() {
        return recordId;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

    public String getDocAction() {
        return docAction;
    }

    public void setDocAction(String docAction) {
        this.docAction = docAction;
    }
}