package com.fr.swift.api.rpc.impl;

import com.fr.swift.annotation.RpcService;
import com.fr.swift.annotation.RpcServiceType;
import com.fr.swift.api.rpc.DetectService;
import com.fr.swift.config.SwiftConfigConstants;
import com.fr.swift.config.service.SwiftMetaDataService;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.event.global.GetAnalyseAndRealTimeAddrEvent;
import com.fr.swift.exception.SwiftProxyException;
import com.fr.swift.exception.meta.SwiftMetaDataAbsentException;
import com.fr.swift.property.SwiftProperty;
import com.fr.swift.service.ServiceType;
import com.fr.swift.service.listener.SwiftServiceListenerHandler;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.utils.ClusterProxyUtils;
import com.fr.third.org.hibernate.criterion.Restrictions;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yee
 * @date 2018/8/23
 */
@RpcService(value = DetectService.class, type = RpcServiceType.EXTERNAL)
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

    @Override
    public SwiftMetaData detectiveMetaData(String tableName) throws SwiftMetaDataAbsentException {
        SwiftMetaDataService swiftMetaDataService = SwiftContext.get().getBean(SwiftMetaDataService.class);
        List<SwiftMetaData> metaDataList = swiftMetaDataService.find(Restrictions.eq(SwiftConfigConstants.MetaDataConfig.COLUMN_TABLE_NAME, tableName));
        if (metaDataList.isEmpty()) {
            throw new SwiftMetaDataAbsentException(tableName);
        }
        return metaDataList.get(0);
    }
}
