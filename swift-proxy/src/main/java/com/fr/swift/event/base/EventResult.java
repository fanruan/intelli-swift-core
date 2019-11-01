package com.fr.swift.event.base;

import java.io.Serializable;

/**
 * @author yee
 * @date 2018/9/12
 */
public class EventResult implements Serializable {
    private static final long serialVersionUID = 527031438021757852L;
    private String clusterId;
    private boolean success;
    private String error;

    public EventResult() {
    }

    public EventResult(String clusterId, boolean success) {
        this.clusterId = clusterId;
        this.success = success;
    }

    public static EventResult success(String clusterId) {
        return new EventResult(clusterId, true);
    }

    public static EventResult failed(String clusterId, String errorMsg) {
        EventResult result = new EventResult(clusterId, false);
        result.setError(errorMsg);
        return result;
    }

    public String getClusterId() {
        return clusterId;
    }

    public void setClusterId(String clusterId) {
        this.clusterId = clusterId;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
