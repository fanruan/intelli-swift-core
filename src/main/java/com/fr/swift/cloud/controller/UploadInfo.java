package com.fr.swift.cloud.controller;

/**
 * @author yee
 * @date 2019-02-25
 */
public class UploadInfo {
    private String clientUserId;
    private String clientAppId;
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
