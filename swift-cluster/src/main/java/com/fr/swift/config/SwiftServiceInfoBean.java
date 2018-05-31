package com.fr.swift.config;

import com.fr.swift.config.bean.Convert;

import java.io.Serializable;

/**
 * This class created on 2018/5/29
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class SwiftServiceInfoBean implements Serializable, Convert<SwiftServiceInfoEntity> {
    private static final long serialVersionUID = 7075811028423358006L;

    private String service;
    private String clusterId;
    private String serviceInfo;
    private boolean isSingleton;

    public SwiftServiceInfoBean(String service, String clusterId, String serviceInfo) {
        this(service, clusterId, serviceInfo, true);
    }

    public SwiftServiceInfoBean(String service, String clusterId, String serviceInfo, boolean isSingleton) {
        this.service = service;
        this.clusterId = clusterId;
        this.serviceInfo = serviceInfo;
        this.isSingleton = isSingleton;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getClusterId() {
        return clusterId;
    }

    public void setClusterId(String clusterId) {
        this.clusterId = clusterId;
    }

    public boolean isSingleton() {
        return isSingleton;
    }

    public void setSingleton(boolean singleton) {
        isSingleton = singleton;
    }

    public String getServiceInfo() {
        return serviceInfo;
    }

    public void setServiceInfo(String serviceInfo) {
        this.serviceInfo = serviceInfo;
    }

    @Override
    public SwiftServiceInfoEntity convert() {
        SwiftServiceInfoEntity entity = new SwiftServiceInfoEntity();
        //单一的服务例如集群master，service就能保证id唯一
        //非单一的服务，需要clusterId+service来保证
        if (isSingleton) {
            entity.setId(service);
        } else {
            entity.setId(clusterId + service);
        }
        entity.setService(service);
        entity.setClusterId(clusterId);
        entity.setServiceInfo(serviceInfo);
        entity.setSingleton(isSingleton);
        return entity;
    }
}
