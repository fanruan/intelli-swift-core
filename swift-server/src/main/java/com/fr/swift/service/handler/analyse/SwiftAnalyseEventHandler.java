package com.fr.swift.service.handler.analyse;

import com.fr.swift.basics.AsyncRpcCallback;
import com.fr.swift.cluster.entity.ClusterEntity;
import com.fr.swift.cluster.service.ClusterSwiftServerService;
import com.fr.swift.cluster.service.SegmentLocationInfoContainer;
import com.fr.swift.event.base.AbstractAnalyseRpcEvent;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.SegmentLocationInfo;
import com.fr.swift.service.ServiceType;
import com.fr.swift.service.handler.base.AbstractHandler;
import com.fr.swift.structure.Pair;
import com.fr.third.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author yee
 * @date 2018/6/8
 */
@Service
public class SwiftAnalyseEventHandler extends AbstractHandler<AbstractAnalyseRpcEvent> {
    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(SwiftAnalyseEventHandler.class);

    @Override
    public <S extends Serializable> S handle(AbstractAnalyseRpcEvent event) {
        switch (event.subEvent()) {
            case SEGMENT_LOCATION:
                Pair<SegmentLocationInfo.UpdateType, SegmentLocationInfo> pair = (Pair<SegmentLocationInfo.UpdateType, SegmentLocationInfo>) event.getContent();
                SegmentLocationInfoContainer.getContainer().add(pair);
                Map<String, ClusterEntity> analyseServices = checkAnalyseServiceEmpty();
                for (Entry<String, ClusterEntity> stringClusterEntityEntry : analyseServices.entrySet()) {
                    final long start = System.currentTimeMillis();
                    String address = stringClusterEntityEntry.getKey();
                    Class clazz = stringClusterEntityEntry.getValue().getServiceClass();

                    try {
                        runAsyncRpc(address, clazz, "updateSegmentInfo", pair.getValue(), pair.getKey())
                                .addCallback(new AsyncRpcCallback() {
                                    @Override
                                    public void success(Object result) {
                                        LOGGER.info(String.format("Update segmentInfo cost: %d ms", System.currentTimeMillis() - start));
                                    }

                                    @Override
                                    public void fail(Exception e) {
                                        LOGGER.error(e.getMessage(), e);
                                    }
                                });
                    } catch (Exception e) {
                        SwiftLoggers.getLogger().error(e);
                    }
                }
                break;
            case REQUEST_SEG_LOCATION:
                String clusterId = (String) event.getContent();
                Map<String, ClusterEntity> analyseNodeMap = ClusterSwiftServerService.getInstance().getClusterEntityByService(ServiceType.ANALYSE);
                if (analyseNodeMap.containsKey(clusterId)) {
                    return (S) SegmentLocationInfoContainer.getContainer().getLocationInfo();
                }
                return (S) Collections.emptyList();
            default:
                break;
        }
        return null;
    }

    private Map<String, ClusterEntity> checkAnalyseServiceEmpty() {
        Map<String, ClusterEntity> analyseServices = ClusterSwiftServerService.getInstance().getClusterEntityByService(ServiceType.ANALYSE);
        if (null == analyseServices || analyseServices.isEmpty()) {
            throw new RuntimeException("Cannot find analyse service");
        }
        return analyseServices;
    }
}
