package com.fr.swift.boot.controller;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 直接用@RequestParam的方式不能传json
 * 所以还是把这个类加回来 方便传json
 *
 * @author yee
 * @date 2019-03-01
 */
public class UploadInfo {
    @JsonProperty("client_user_id")
    private String clientUserId;
    @JsonProperty("client_app_id")
    private String clientAppId;
    /**
     * 传过来的参数就是String 用String方便传输
     */
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
