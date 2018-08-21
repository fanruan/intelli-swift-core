package com.fr.swift.util;

import com.fr.swift.context.SwiftContext;
import com.fr.swift.service.ServerService;
import com.fr.swift.service.SwiftService;
import com.fr.third.springframework.beans.factory.NoSuchBeanDefinitionException;

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

    private final static Map<String, String> clusterServiceName2BeanName = new HashMap<String, String>();

    private final static Map<String, String> serverName2BeanName = new HashMap<String, String>();

    static {
        Map<String, Object> swiftServiceBeans = SwiftContext.get().getBeansWithAnnotation(com.fr.swift.annotation.SwiftService.class);
        for (Map.Entry<String, Object> entry : swiftServiceBeans.entrySet()) {
            com.fr.swift.annotation.SwiftService annotation =
                    entry.getValue().getClass().getAnnotation(com.fr.swift.annotation.SwiftService.class);
            if (annotation.cluster()) {
                clusterServiceName2BeanName.put(annotation.name(), entry.getKey());
            } else {
                serviceName2BeanName.put(annotation.name(), entry.getKey());
            }
        }
        Map<String, Object> serverServiceBeans = SwiftContext.get().getBeansWithAnnotation(com.fr.swift.annotation.ServerService.class);
        for (Map.Entry<String, Object> entry : serverServiceBeans.entrySet()) {
            com.fr.swift.annotation.ServerService annotation =
                    entry.getValue().getClass().getAnnotation(com.fr.swift.annotation.ServerService.class);
            serverName2BeanName.put(annotation.name(), entry.getKey());
        }
    }

    public static List<SwiftService> getSwiftServiceByNames(Set<String> swiftServiceNames) {
        // TODO: 2018/8/9 暂时先把collate跟随indexing启动
        if (swiftServiceNames.contains("indexing")) {
            swiftServiceNames.add("collate");
        }
        List<SwiftService> swiftServiceList = new ArrayList<SwiftService>();
        for (String serviceName : swiftServiceNames) {
            try {
                if (serviceName2BeanName.containsKey(serviceName)) {
                    SwiftService swiftService = (SwiftService) SwiftContext.get().getBean(serviceName2BeanName.get(serviceName));
                    if (swiftService == null) {
                        continue;
                    }
                    swiftServiceList.add(swiftService);
                }
            } catch (NoSuchBeanDefinitionException e) {
                continue;
            }
        }
        return swiftServiceList;
    }

    public static List<SwiftService> getClusterSwiftServiceByNames(Set<String> swiftServiceNames) {
        // TODO: 2018/8/9 暂时先把collate跟随indexing启动
        if (swiftServiceNames.contains("indexing")) {
            swiftServiceNames.add("collate");
        }
        List<SwiftService> swiftServiceList = new ArrayList<SwiftService>();
        for (String serviceName : swiftServiceNames) {
            try {
                if (serviceName2BeanName.containsKey(serviceName)) {
                    SwiftService swiftService = (SwiftService) SwiftContext.get().getBean(serviceName2BeanName.get(serviceName));
                    if (swiftService == null) {
                        continue;
                    }
                    swiftServiceList.add(swiftService);
                }
                if (clusterServiceName2BeanName.containsKey(serviceName)) {
                    SwiftService swiftService = (SwiftService) SwiftContext.get().getBean(clusterServiceName2BeanName.get(serviceName));
                    if (swiftService == null) {
                        continue;
                    }
                    swiftServiceList.add(swiftService);
                }
            } catch (NoSuchBeanDefinitionException e) {
                continue;
            }
        }
        return swiftServiceList;
    }

    public static List<ServerService> getServerServiceByNames(Set<String> serverServiceNames) {
        List<ServerService> serverServiceList = new ArrayList<ServerService>();
        for (String serverName : serverServiceNames) {
            try {
                if (serverName2BeanName.containsKey(serverName)) {
                    ServerService serverService = (ServerService) SwiftContext.get().getBean(serverName2BeanName.get(serverName));
                    if (serverService == null) {
                        continue;
                    }
                    serverServiceList.add(serverService);
                }
            } catch (NoSuchBeanDefinitionException e) {
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
