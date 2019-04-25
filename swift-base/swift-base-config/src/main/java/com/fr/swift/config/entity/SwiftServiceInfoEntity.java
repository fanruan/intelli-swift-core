package com.fr.swift.config.entity;

import com.fr.swift.annotation.persistence.Column;
import com.fr.swift.annotation.persistence.Entity;
import com.fr.swift.annotation.persistence.Id;

import java.io.Serializable;


/**
 * This class created on 2018/5/29
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
@Entity(name = "fine_swift_service_info")
public class SwiftServiceInfoEntity implements Serializable {

    @Id
    private String id;

    @Column(name = "service")
    private String service;

    @Column(name = "cluster_id")
    private String clusterId;

    @Column(name = "service_info")
    private String serviceInfo;

    @Column(name = "is_singleton")
    private boolean singleton;

    public SwiftServiceInfoEntity(String service, String clusterId, String serviceInfo, boolean singleton) {
        this.service = service;
        this.clusterId = clusterId;
        this.serviceInfo = serviceInfo;
        this.singleton = singleton;
        if (singleton) {
            id = service;
        } else {
            id = clusterId + service;
        }
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
        return singleton;
    }

    public void setSingleton(boolean singleton) {
        this.singleton = singleton;
    }

    public String getServiceInfo() {
        return serviceInfo;
    }

    public void setServiceInfo(String serviceInfo) {
        this.serviceInfo = serviceInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SwiftServiceInfoEntity that = (SwiftServiceInfoEntity) o;

        if (singleton != that.singleton) {
            return false;
        }
        if (id != null ? !id.equals(that.id) : that.id != null) {
            return false;
        }
        if (service != null ? !service.equals(that.service) : that.service != null) {
            return false;
        }
        if (clusterId != null ? !clusterId.equals(that.clusterId) : that.clusterId != null) {
            return false;
        }
        return serviceInfo != null ? serviceInfo.equals(that.serviceInfo) : that.serviceInfo == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (service != null ? service.hashCode() : 0);
        result = 31 * result + (clusterId != null ? clusterId.hashCode() : 0);
        result = 31 * result + (serviceInfo != null ? serviceInfo.hashCode() : 0);
        result = 31 * result + (singleton ? 1 : 0);
        return result;
    }

}
