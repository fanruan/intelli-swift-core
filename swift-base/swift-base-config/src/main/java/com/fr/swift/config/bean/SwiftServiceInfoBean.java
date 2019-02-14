package com.fr.swift.config.bean;

import com.fr.swift.converter.ObjectConverter;
import com.fr.swift.log.SwiftLoggers;

import java.io.Serializable;
import java.lang.reflect.Constructor;

/**
 * This class created on 2018/5/29
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class SwiftServiceInfoBean implements Serializable, ObjectConverter {
    private static final long serialVersionUID = 7075811028423358006L;

    public static final Class TYPE = entityType();
    private String service;
    private String clusterId;
    private String serviceInfo;
    private String id;
    private boolean singleton;

    public SwiftServiceInfoBean(String service, String clusterId, String serviceInfo, boolean singleton) {
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

    public SwiftServiceInfoBean(String service, String clusterId, String serviceInfo) {
        this(service, clusterId, serviceInfo, true);
    }

    private static Class entityType() {
        try {
            return Class.forName("com.fr.swift.config.entity.SwiftServiceInfoEntity");
        } catch (ClassNotFoundException e) {
            return null;
        }
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

    public String getId() {
        return id;
    }

    @Override
    public Object convert() {
        try {
            Constructor constructor = TYPE.getDeclaredConstructor(SwiftServiceInfoBean.class);
            return constructor.newInstance(this);
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
        }
        return null;
    }
}
