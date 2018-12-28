package com.fr.swift.config.entity;

import com.fr.swift.config.bean.SwiftServiceInfoBean;
import com.fr.swift.converter.ObjectConverter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * This class created on 2018/5/29
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
@Entity
@Table(name = "fine_swift_service_info")
public class SwiftServiceInfoEntity implements ObjectConverter<SwiftServiceInfoBean> {

    @Id
    private String id;

    @Column(name = "service")
    private String service;

    @Column(name = "cluster_id")
    private String clusterId;

    @Column(name = "service_info")
    private String serviceInfo;

    @Column(name = "is_singleton")
    private boolean isSingleton;

    public SwiftServiceInfoEntity() {
    }

    public SwiftServiceInfoEntity(SwiftServiceInfoBean value) {
        //单一的服务例如集群master，service就能保证id唯一
        //非单一的服务，需要clusterId+service来保证
        if (value.isSingleton()) {
            this.setId(value.getService());
        } else {
            this.setId(value.getClusterId() + value.getService());
        }
        this.setService(value.getService());
        this.setClusterId(value.getClusterId());
        this.setServiceInfo(value.getServiceInfo());
        this.setSingleton(value.isSingleton());
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SwiftServiceInfoEntity that = (SwiftServiceInfoEntity) o;

        if (isSingleton != that.isSingleton) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (service != null ? !service.equals(that.service) : that.service != null) return false;
        if (clusterId != null ? !clusterId.equals(that.clusterId) : that.clusterId != null) return false;
        return serviceInfo != null ? serviceInfo.equals(that.serviceInfo) : that.serviceInfo == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (service != null ? service.hashCode() : 0);
        result = 31 * result + (clusterId != null ? clusterId.hashCode() : 0);
        result = 31 * result + (serviceInfo != null ? serviceInfo.hashCode() : 0);
        result = 31 * result + (isSingleton ? 1 : 0);
        return result;
    }

    @Override
    public SwiftServiceInfoBean convert() {
        return new SwiftServiceInfoBean(service, clusterId, serviceInfo, isSingleton);
    }
}
