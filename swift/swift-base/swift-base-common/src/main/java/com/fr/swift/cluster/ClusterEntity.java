package com.fr.swift.cluster;

import com.fr.swift.basic.URL;
import com.fr.swift.service.ServiceType;

/**
 * This class created on 2018/6/12
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class ClusterEntity<T> {
    private URL url;

    private ServiceType serviceType;

    private Class<T> serviceClass;

    public ClusterEntity(URL url, ServiceType serviceType, Class<T> serviceClass) {
        this.url = url;
        this.serviceType = serviceType;
        this.serviceClass = serviceClass;
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    public URL getUrl() {
        return url;
    }

    public Class<T> getServiceClass() {
        return serviceClass;
    }
}
