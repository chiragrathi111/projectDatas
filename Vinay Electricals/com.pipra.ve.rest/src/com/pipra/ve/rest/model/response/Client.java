package com.pipra.ve.rest.model.response;

import java.util.List;

/**
 * 
 * @author Mahendhar Reddy
 *
 */
public class Client {
    private String clientId;
    private String client;
    private List<Role> roleList; // Assuming this is a list based on minOccurs="0" maxOccurs="unbounded"

    // Getters and Setters
    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public List<Role> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<Role> roleList) {
        this.roleList = roleList;
    }
}
