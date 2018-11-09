package com.fr.swift.api.rpc.impl;

import com.fr.swift.annotation.SwiftApi;
import com.fr.swift.api.rpc.DetectService;
import com.fr.swift.basics.annotation.ProxyService;
import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.event.global.GetAnalyseAndRealTimeAddrEvent;
import com.fr.swift.selector.ClusterSelector;
import com.fr.swift.service.ServiceType;
import com.fr.swift.service.listener.RemoteSender;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yee
 * @date 2018/8/23
 */
@ProxyService(value = DetectService.class, type = ProxyService.ServiceType.EXTERNAL)
@SwiftApi
public class DetectServiceImpl implements DetectService {
    @Override
    @SwiftApi
    public Map<ServiceType, List<String>> detectiveAnalyseAndRealTime(String defaultAddress) {
        try {
            if (ClusterSelector.getInstance().getFactory().isCluster()) {
                return (Map<ServiceType, List<String>>) ProxySelector.getInstance().getFactory().getProxy(RemoteSender.class).trigger(new GetAnalyseAndRealTimeAddrEvent());
            } else {
                Map<ServiceType, List<String>> result = new HashMap<ServiceType, List<String>>();
                result.put(ServiceType.ANALYSE, Collections.singletonList(defaultAddress));
                result.put(ServiceType.REAL_TIME, Collections.singletonList(defaultAddress));
                return result;
            }
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }

}
