package com.fr.swift.service.handler.realtime;

import com.fr.swift.basics.AsyncRpcCallback;
import com.fr.swift.cluster.entity.ClusterEntity;
import com.fr.swift.cluster.service.ClusterSwiftServerService;
import com.fr.swift.config.service.SwiftClusterSegmentService;
import com.fr.swift.event.base.AbstractRealTimeRpcEvent;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.service.ServiceType;
import com.fr.swift.service.handler.base.AbstractHandler;
import com.fr.swift.service.handler.history.SwiftHistoryEventHandler;
import com.fr.third.springframework.beans.factory.annotation.Autowired;
import com.fr.third.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author yee
 * @date 2018/6/8
 */
@Service
public class SwiftRealTimeEventHandler extends AbstractHandler<AbstractRealTimeRpcEvent> {

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(SwiftHistoryEventHandler.class);

    @Autowired(required = false)
    private SwiftClusterSegmentService clusterSegmentService;

    @Override
    public <S extends Serializable> S handle(AbstractRealTimeRpcEvent event) {
        Map<String, ClusterEntity> realTimeServices = ClusterSwiftServerService.getInstance().getClusterEntityByService(ServiceType.REAL_TIME);
        if (null == realTimeServices || realTimeServices.isEmpty()) {
            throw new RuntimeException("Cannot find realTime service");
        }
        try {
            switch (event.subEvent()) {
                case RECOVER:
                    final Map<String, List<SegmentKey>> map = clusterSegmentService.getClusterSegments();
                    for (Entry<String, ClusterEntity> entityEntry : realTimeServices.entrySet()) {
                        final String key = entityEntry.getKey();
                        ClusterEntity entity = entityEntry.getValue();
                        final List<SegmentKey> list = map.get(key);
                        if (null != list && !list.isEmpty()) {
                            final long start = System.currentTimeMillis();
                            runAsyncRpc(key, entity.getServiceClass(), "recover", list)
                                    .addCallback(new AsyncRpcCallback() {
                                        @Override
                                        public void success(Object result) {
                                            LOGGER.info(String.format("clusterId: %s, recover cost: %d ms", key, (System.currentTimeMillis() - start)));
                                        }

                                        @Override
                                        public void fail(Exception e) {
                                            LOGGER.error(String.format("clusterId: %s, recover error!", key), e);
                                        }
                                    });
                        }
                    }
                    return null;
                case MERGE:
                    final Map<String, List<SegmentKey>> mergeMap = clusterSegmentService.getClusterSegments();
                    for (Entry<String, ClusterEntity> entityEntry : realTimeServices.entrySet()) {
                        final String key = entityEntry.getKey();
                        ClusterEntity entity = entityEntry.getValue();
                        final List<SegmentKey> list = mergeMap.get(key);
                        if (null != list && !list.isEmpty()) {
                            final long start = System.currentTimeMillis();
                            runAsyncRpc(key, entity.getServiceClass(), "merge", list)
                                    .addCallback(new AsyncRpcCallback() {
                                        @Override
                                        public void success(Object result) {
                                            LOGGER.info(String.format("clusterId: %s, merge cost: %d ms", key, (System.currentTimeMillis() - start)));
                                        }

                                        @Override
                                        public void fail(Exception e) {
                                            LOGGER.error(String.format("clusterId: %s, merge error!", key), e);
                                        }
                                    });
                        }
                    }
                    return null;
                default:
            }
        } catch (Exception e) {
            LOGGER.error("handle error! ", e);
        }
        return null;
    }
}
