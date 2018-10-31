package com.fr.swift.basics.base;

import com.fr.swift.basics.ServiceRegistry;
import com.fr.swift.basics.annotation.ProxyService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yee
 * @date 2018/10/30
 */
public class ProxyServiceRegistry implements ServiceRegistry {

    public static final ServiceRegistry INSTANCE = new ProxyServiceRegistry();

    private Map<String, Object> handlerMap = new ConcurrentHashMap<String, Object>();
    private Map<String, Object> externalMap = new ConcurrentHashMap<String, Object>();

    @Override
    public void registerService(Object service) {
        if (service.getClass().isAnnotationPresent(ProxyService.class)) {
            ProxyService proxyService = service.getClass().getAnnotation(ProxyService.class);
            if (proxyService.type().isInternal()) {
                handlerMap.put(proxyService.value().getName(), service);
            } else {
                externalMap.put(proxyService.value().getName(), service);
            }
        }
    }

    @Override
    public <Service> Service getInternalService(Class<Service> serviceClass) {
        return (Service) handlerMap.get(serviceClass.getName());
    }

    @Override
    public <Service> Service getExternalService(Class<Service> serviceClass) {
        return (Service) externalMap.get(serviceClass.getName());
    }

    @Override
    public <Service> Service getService(Class<Service> proxyClass) {
        Service service = getExternalService(proxyClass);
        if (null == service) {
            return getInternalService(proxyClass);
        }
        return service;
    }

    @Override
    public Object getService(String proxyClass) {
        Object service = getExternalService(proxyClass);
        if (null == service) {
            return getInternalService(proxyClass);
        }
        return service;
    }

    public Object getInternalService(String serviceClass) {
        return handlerMap.get(serviceClass);
    }

    public Object getExternalService(String serviceClass) {
        return externalMap.get(serviceClass);
    }
}
