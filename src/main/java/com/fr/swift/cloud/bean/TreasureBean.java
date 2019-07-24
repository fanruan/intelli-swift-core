package com.fr.swift.cloud.bean;

/**
 * This class created on 2019/5/9
 *
 * @author Lucifer
 * @description
 */
public class TreasureBean {
    private String topic;
    private String key;
    private String url;
    private String clientId;
    private String clientAppId;
    private String yearMonth;
    private String version;
    private long timestamp;
    private String type;
    private String customerId;

    public TreasureBean() {
    }

    public TreasureBean(String topic, String key, String url, String clientId, String clientAppId, String yearMonth, String version, long timestamp, String type, String customerId) {
        this.topic = topic;
        this.key = key;
        this.url = url;
        this.clientId = clientId;
        this.clientAppId = clientAppId;
        this.yearMonth = yearMonth;
        this.version = version;
        this.timestamp = timestamp;
        this.type = type;
        this.customerId = customerId;
    }

    public String getTopic() {
        return topic;
    }

    public String getKey() {
        return key;
    }

    public String getUrl() {
        return url;
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
        return "TreasureBean{" +
                "topic='" + topic + '\'' +
                ", key='" + key + '\'' +
                ", url='" + url + '\'' +
                ", clientId='" + clientId + '\'' +
                ", clientAppId='" + clientAppId + '\'' +
                ", yearMonth='" + yearMonth + '\'' +
                ", version='" + version + '\'' +
                ", timestamp=" + timestamp +
                ", type='" + type + '\'' +
                ", customerId='" + customerId + '\'' +
                '}';
    }
}