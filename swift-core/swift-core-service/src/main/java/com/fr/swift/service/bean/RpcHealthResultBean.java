package com.fr.swift.service.bean;

import com.fr.swift.service.ServiceType;

import java.io.Serializable;

/**
 * @author Marvin
 * @version 1.1
 * Created by Marvin on 9/4/2019
 */
public class RpcHealthResultBean implements Serializable {
    private static final long serialVersionUID = -1065062665128145850L;

    /**
     * <serviceName, serviceId>
     */
    private String serviceId;
    private String serviceName;
    private ServiceType type;

    public RpcHealthResultBean(ServiceType type) {
        this.type = type;
    }

    public RpcHealthResultBean setServiceId(String id) {
        this.serviceId = id;
        return this;
    }

    public RpcHealthResultBean setServiceName(String serviceName) {
        this.serviceName = serviceName;
        return this;
    }

    public void setType(ServiceType type) {
        this.type = type;
    }

    public ServiceType getType() {
        return type;
    }
}
