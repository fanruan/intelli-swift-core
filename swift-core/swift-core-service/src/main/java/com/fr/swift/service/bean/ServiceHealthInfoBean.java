package com.fr.swift.service.bean;

import com.fr.swift.service.SwiftService;

/**
 * @author Marvin
 * @version 1.1
 * Created by Marvin on 9/4/2019
 */
public class ServiceHealthInfoBean {
    private SwiftService service;
    private String serviceInfo;

    public void setService(SwiftService service) {
        this.service = service;
    }

    public void setServiceInfo(String serviceInfo) {
        this.serviceInfo = serviceInfo;
    }

    public String getInspectResult() {
        return service.getId() + " / " + serviceInfo;
    }
}
