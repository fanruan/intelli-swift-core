package com.fr.swift.cloud.util;

import com.fr.swift.cloud.SwiftContext;
import com.fr.swift.cloud.annotation.ServerService;
import com.fr.swift.cloud.annotation.SwiftService;
import com.fr.swift.cloud.beans.exception.NoSuchBeanException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class created on 2018/8/8
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class ServiceBeanFactory {

    private final static Map<String, String> serviceName2BeanName = new HashMap<String, String>();

    private final static Map<String, String> serverName2BeanName = new HashMap<String, String>();

    static {
        Map<String, Object> swiftServiceBeans = SwiftContext.get().getBeansByAnnotations(SwiftService.class);
        for (Map.Entry<String, Object> entry : swiftServiceBeans.entrySet()) {
            SwiftService annotation =
                    entry.getValue().getClass().getAnnotation(SwiftService.class);
            serviceName2BeanName.put(annotation.name(), entry.getKey());
        }
        Map<String, Object> serverServiceBeans = SwiftContext.get().getBeansByAnnotations(ServerService.class);
        for (Map.Entry<String, Object> entry : serverServiceBeans.entrySet()) {
            ServerService annotation =
                    entry.getValue().getClass().getAnnotation(ServerService.class);
            serverName2BeanName.put(annotation.name(), entry.getKey());
        }
    }

    public static List<com.fr.swift.cloud.service.SwiftService> getSwiftServiceByNames(Set<String> swiftServiceNames) {
        // TODO: 2018/8/9 暂时先把collate跟随indexing启动
        if (swiftServiceNames.contains("indexing")) {
            swiftServiceNames.add("collate");
        }
        if (swiftServiceNames.contains("realtime") || swiftServiceNames.contains("history")) {
            swiftServiceNames.add("swiftDeleteService");
            swiftServiceNames.add("swiftUploadService");
        }
        List<com.fr.swift.cloud.service.SwiftService> swiftServiceList = new ArrayList<com.fr.swift.cloud.service.SwiftService>();
        for (String serviceName : swiftServiceNames) {
            try {
                if (serviceName2BeanName.containsKey(serviceName)) {
                    com.fr.swift.cloud.service.SwiftService swiftService = (com.fr.swift.cloud.service.SwiftService) SwiftContext.get().getBean(serviceName2BeanName.get(serviceName));
                    if (swiftService == null) {
                        continue;
                    }
                    swiftServiceList.add(swiftService);
                }
            } catch (NoSuchBeanException e) {
                continue;
            }
        }
        return swiftServiceList;
    }

    public static List<com.fr.swift.cloud.service.ServerService> getServerServiceByNames(Set<String> serverServiceNames) {
        List<com.fr.swift.cloud.service.ServerService> serverServiceList = new ArrayList<com.fr.swift.cloud.service.ServerService>();
        for (String serverName : serverServiceNames) {
            try {
                if (serverName2BeanName.containsKey(serverName)) {
                    com.fr.swift.cloud.service.ServerService serverService = SwiftContext.get().getBean(serverName2BeanName.get(serverName), com.fr.swift.cloud.service.ServerService.class);
                    if (serverService == null) {
                        continue;
                    }
                    serverServiceList.add(serverService);
                }
            } catch (NoSuchBeanException e) {
                continue;
            }
        }
        return serverServiceList;
    }

    public static Collection<String> getAllSwiftServiceNames() {
        return serviceName2BeanName.keySet();
    }

    public static Collection<String> getAllServerServiceNames() {
        return serverName2BeanName.keySet();
    }
}
