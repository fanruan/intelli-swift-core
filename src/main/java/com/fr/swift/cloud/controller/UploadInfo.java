package com.fr.swift.cloud.controller;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author yee
 * @date 2019-02-25
 */
public class UploadInfo {
    @JsonProperty("client_user_id")
    private String clientUserId;
    @JsonProperty("client_app_id")
    private String clientAppId;
    @JsonProperty("treas_date")
    private String treasDate;

    public String getClientUserId() {
        return clientUserId;
    }

    public void setClientUserId(String clientUserId) {
        this.clientUserId = clientUserId;
    }

    public String getClientAppId() {
        return clientAppId;
    }

    public void setClientAppId(String clientAppId) {
        this.clientAppId = clientAppId;
    }

    public String getTreasDate() {
        return treasDate;
    }

    public void setTreasDate(String treasDate) {
        this.treasDate = treasDate;
    }
}
