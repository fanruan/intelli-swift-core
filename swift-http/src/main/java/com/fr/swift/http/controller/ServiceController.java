package com.fr.swift.http.controller;

import com.fr.swift.context.SwiftContext;
import com.fr.swift.controller.BaseController;
import com.fr.swift.property.SwiftProperty;
import com.fr.swift.service.manager.ClusterServiceManager;
import com.fr.swift.service.manager.LocalServiceManager;
import com.fr.swift.service.manager.ServerServiceManager;
import com.fr.swift.service.manager.ServiceManager;
import com.fr.swift.util.ServiceBeanUtils;
import com.fr.third.springframework.stereotype.Controller;
import com.fr.third.springframework.web.bind.annotation.RequestBody;
import com.fr.third.springframework.web.bind.annotation.RequestMapping;
import com.fr.third.springframework.web.bind.annotation.RequestMethod;
import com.fr.third.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * This class created on 2018/8/8
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
@Controller()
public class ServiceController extends BaseController {

    // TODO: 2018/8/8 集群和单机优化
    private ServiceManager swiftLocalServiceManager = SwiftContext.get().getBean(LocalServiceManager.class);
    private ServiceManager swiftClusterServiceManager = SwiftContext.get().getBean(ClusterServiceManager.class);
    private ServiceManager serverServiceManager = SwiftContext.get().getBean(ServerServiceManager.class);
    private SwiftProperty swiftProperty = SwiftContext.get().getBean(SwiftProperty.class);

    @ResponseBody
    @RequestMapping(value = SWIFT_SERVICE_START, method = RequestMethod.POST)
    public void swiftServiceStart(HttpServletResponse response, HttpServletRequest request,
                                  @RequestBody(required = false) List<String> requestList) throws Exception {
        if (requestList != null) {
            if (swiftProperty.isCluster()) {
                swiftClusterServiceManager.registerService(ServiceBeanUtils.getSwiftServiceByNames(requestList.toArray(new String[requestList.size()])));
            } else {
                swiftLocalServiceManager.registerService(ServiceBeanUtils.getSwiftServiceByNames(requestList.toArray(new String[requestList.size()])));
            }
        }
    }

    @ResponseBody
    @RequestMapping(value = SWIFT_SERVICE_STOP, method = RequestMethod.POST)
    public void swiftServiceStop(HttpServletResponse response, HttpServletRequest request,
                                 @RequestBody(required = false) List<String> requestList) throws Exception {
        if (requestList != null) {
            if (swiftProperty.isCluster()) {
                swiftClusterServiceManager.unregisterService(ServiceBeanUtils.getSwiftServiceByNames(requestList.toArray(new String[requestList.size()])));
            } else {
                swiftLocalServiceManager.unregisterService(ServiceBeanUtils.getSwiftServiceByNames(requestList.toArray(new String[requestList.size()])));
            }
        }
    }

    @ResponseBody
    @RequestMapping(value = SERVER_SERVICE_START, method = RequestMethod.POST)
    public void serverServiceStart(HttpServletResponse response, HttpServletRequest request,
                                   @RequestBody(required = false) List<String> requestList) throws Exception {
        if (requestList != null) {
            serverServiceManager.registerService(ServiceBeanUtils.getServerServiceByNames(requestList.toArray(new String[requestList.size()])));
        }
    }

    @ResponseBody
    @RequestMapping(value = SERVER_SERVICE_STOP, method = RequestMethod.POST)
    public void serverServiceStop(HttpServletResponse response, HttpServletRequest request,
                                  @RequestBody(required = false) List<String> requestList) throws Exception {
        if (requestList != null) {
            serverServiceManager.unregisterService(ServiceBeanUtils.getServerServiceByNames(requestList.toArray(new String[requestList.size()])));
        }
    }
}
