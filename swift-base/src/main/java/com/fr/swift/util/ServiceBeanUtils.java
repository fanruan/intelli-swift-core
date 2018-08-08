package com.fr.swift.util;

import com.fr.swift.context.SwiftContext;
import com.fr.swift.service.ServerService;
import com.fr.swift.service.SwiftService;
import com.fr.third.springframework.beans.factory.NoSuchBeanDefinitionException;

import java.util.ArrayList;
import java.util.List;

/**
 * This class created on 2018/8/8
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class ServiceBeanUtils {
    public static List<SwiftService> getSwiftServiceByNames(String[] swiftServiceNames) {
        List<SwiftService> swiftServiceList = new ArrayList<SwiftService>();
        for (String serviceName : swiftServiceNames) {
            try {
                SwiftService swiftService = (SwiftService) SwiftContext.get().getBean(serviceName);
                if (swiftService == null) {
                    continue;
                }
                swiftServiceList.add(swiftService);
            } catch (NoSuchBeanDefinitionException e) {
                continue;
            }
        }
        return swiftServiceList;
    }

    public static List<ServerService> getServerServiceByNames(String[] serverServiceNames) {
        List<ServerService> serverServiceList = new ArrayList<ServerService>();
        for (String serverName : serverServiceNames) {
            try {
                ServerService serverService = (ServerService) SwiftContext.get().getBean(serverName);
                if (serverService == null) {
                    continue;
                }
                serverServiceList.add(serverService);
            } catch (NoSuchBeanDefinitionException e) {
                continue;
            }
        }
        return serverServiceList;
    }
}
