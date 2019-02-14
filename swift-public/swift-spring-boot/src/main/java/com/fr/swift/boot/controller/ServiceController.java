package com.fr.swift.boot.controller;

import com.fr.swift.SwiftContext;
import com.fr.swift.property.SwiftProperty;
import com.fr.swift.service.manager.ClusterServiceManager;
import com.fr.swift.service.manager.LocalServiceManager;
import com.fr.swift.service.manager.ServerServiceManager;
import com.fr.swift.service.manager.ServiceManager;
import com.fr.swift.util.ServiceBeanFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Set;

/**
 * This class created on 2018/8/8
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
@RestController()
public class ServiceController extends BaseController {

    private ServiceManager swiftLocalServiceManager = SwiftContext.get().getBean(LocalServiceManager.class);
    private ServiceManager swiftClusterServiceManager = SwiftContext.get().getBean(ClusterServiceManager.class);
    private ServiceManager serverServiceManager = SwiftContext.get().getBean(ServerServiceManager.class);
    private SwiftProperty swiftProperty = SwiftProperty.getProperty();

    @ResponseBody
    @RequestMapping(value = SWIFT_SERVICE, method = RequestMethod.POST)
    public void swiftServiceStart(HttpServletResponse response, HttpServletRequest request,
                                  @RequestBody(required = false) Set<String> services) throws Exception {
        if (services != null) {
            if (swiftProperty.isCluster()) {
                swiftClusterServiceManager.registerService(ServiceBeanFactory.getSwiftServiceByNames(services));
            } else {
                swiftLocalServiceManager.registerService(ServiceBeanFactory.getSwiftServiceByNames(services));
            }
        }
    }

    @ResponseBody
    @RequestMapping(value = SWIFT_SERVICE, method = RequestMethod.DELETE)
    public void swiftServiceStop(HttpServletResponse response, HttpServletRequest request,
                                 @RequestBody(required = false) Set<String> services) throws Exception {
        if (services != null) {
            if (swiftProperty.isCluster()) {
                swiftClusterServiceManager.unregisterService(ServiceBeanFactory.getSwiftServiceByNames(services));
            } else {
                swiftLocalServiceManager.unregisterService(ServiceBeanFactory.getSwiftServiceByNames(services));
            }
        }
    }

    @ResponseBody
    @RequestMapping(value = SERVER_SERVICE, method = RequestMethod.POST)
    public void serverServiceStart(HttpServletResponse response, HttpServletRequest request,
                                   @RequestBody(required = false) Set<String> services) throws Exception {
        if (services != null) {
            serverServiceManager.registerService(ServiceBeanFactory.getServerServiceByNames(services));
        }
    }

    @ResponseBody
    @RequestMapping(value = SERVER_SERVICE, method = RequestMethod.DELETE)
    public void serverServiceStop(HttpServletResponse response, HttpServletRequest request,
                                  @RequestBody(required = false) Set<String> services) throws Exception {
        if (services != null) {
            serverServiceManager.unregisterService(ServiceBeanFactory.getServerServiceByNames(services));
        }
    }
}
