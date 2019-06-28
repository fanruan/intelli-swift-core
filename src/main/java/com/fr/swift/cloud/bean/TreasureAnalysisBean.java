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
    private String customerId;


    public TreasureAnalysisBean() {
    }

    public TreasureAnalysisBean(String clientId, String clientAppId, String yearMonth, String version, String type, String customerId) {
        this.clientId = clientId;
        this.clientAppId = clientAppId;
        this.yearMonth = yearMonth;
        this.version = version;
        this.timestamp = System.currentTimeMillis();
        this.type = type;
        this.customerId = customerId;
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

    public String getCustomerId() {
        return customerId;
    }

    @Override
    public String toString() {
        return "TreasureAnalysisBean{" +
                "clientId='" + clientId + '\'' +
                ", clientAppId='" + clientAppId + '\'' +
                ", yearMonth='" + yearMonth + '\'' +
                ", version='" + version + '\'' +
                ", timestamp=" + timestamp +
                ", type='" + type + '\'' +
                ", customerId='" + customerId + '\'' +
                '}';
    }
}
