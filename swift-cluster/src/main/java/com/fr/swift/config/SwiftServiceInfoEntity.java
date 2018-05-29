package com.fr.swift.config;

import com.fr.swift.config.bean.Convert;
import com.fr.third.javax.persistence.Column;
import com.fr.third.javax.persistence.Entity;
import com.fr.third.javax.persistence.Table;

/**
 * This class created on 2018/5/29
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
@Entity
@Table(name = "swift_service_info")
public class SwiftServiceInfoEntity extends com.fr.config.entity.Entity implements Convert<SwiftServiceInfoBean> {

    @Column(name = "service")
    private String service;

    @Column(name = "cluster_id")
    private String clusterId;

    @Column(name = "service_info")
    private String serviceInfo;

    @Column(name = "is_singleton")
    private boolean isSingleton;

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
    public SwiftServiceInfoBean convert() {
        SwiftServiceInfoBean bean = new SwiftServiceInfoBean(service, clusterId, serviceInfo, isSingleton);
        return bean;
    }
}
