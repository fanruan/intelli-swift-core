package com.fr.swift.api.rpc.impl;

import com.fr.swift.annotation.RpcService;
import com.fr.swift.api.rpc.DetectService;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.event.global.GetAnalyseAndRealTimeAddrEvent;
import com.fr.swift.exception.SwiftProxyException;
import com.fr.swift.property.SwiftProperty;
import com.fr.swift.service.ServiceType;
import com.fr.swift.service.listener.SwiftServiceListenerHandler;
import com.fr.swift.utils.ClusterProxyUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yee
 * @date 2018/8/23
 */
@RpcService(value = DetectService.class, type = RpcService.RpcServiceType.EXTERNAL)
class DetectServiceImpl implements DetectService {
    @Override
    public Map<ServiceType, List<String>> detectiveAnalyseAndRealTime(String defaultAddress) {
        try {
            if (SwiftContext.get().getBean("swiftProperty", SwiftProperty.class).isCluster()) {
                return (Map<ServiceType, List<String>>) ClusterProxyUtils.getMasterProxy(SwiftServiceListenerHandler.class).trigger(new GetAnalyseAndRealTimeAddrEvent());
            } else {
                Map<ServiceType, List<String>> result = new HashMap<ServiceType, List<String>>();
                result.put(ServiceType.ANALYSE, Collections.singletonList(defaultAddress));
                result.put(ServiceType.REAL_TIME, Collections.singletonList(defaultAddress));
                return result;
            }
        } catch (SwiftProxyException e) {
            return Collections.emptyMap();
        }
    }

}
