package com.fr.swift.cloud.bean;

/**
 * This class created on 2019/5/13
 *
 * @author Lucifer
 * @description
 */
public class TreasureAnalysisBean {
    private String clientId;
    private String clientAppId;
    private String yearMonth;
    private String version;
    private long timestamp;
    private String type;


    public TreasureAnalysisBean() {
    }

    public TreasureAnalysisBean(String clientId, String clientAppId, String yearMonth, String version, String type) {
        this.clientId = clientId;
        this.clientAppId = clientAppId;
        this.yearMonth = yearMonth;
        this.version = version;
        this.timestamp = System.currentTimeMillis();
        this.type = type;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientAppId() {
        return clientAppId;
    }

    public String getYearMonth() {
        return yearMonth;
    }

    public String getVersion() {
        return version;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getType() {
        return type;
    }
}
