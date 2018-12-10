package com.fr.swift.service.handler.realtime;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.cluster.ClusterEntity;
import com.fr.swift.cluster.service.ClusterSwiftServerService;
import com.fr.swift.config.service.SwiftClusterSegmentService;
import com.fr.swift.event.base.AbstractRealTimeRpcEvent;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.service.ServiceType;
import com.fr.swift.service.handler.base.AbstractHandler;
import com.fr.swift.service.handler.history.SwiftHistoryEventHandler;

import java.io.Serializable;
import java.util.Map;

/**
 * @author yee
 * @date 2018/6/8
 */
@SwiftBean
public class SwiftRealTimeEventHandler extends AbstractHandler<AbstractRealTimeRpcEvent> {

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(SwiftHistoryEventHandler.class);

    private SwiftClusterSegmentService clusterSegmentService = SwiftContext.get().getBean(SwiftClusterSegmentService.class);

    @Override
    public <S extends Serializable> S handle(AbstractRealTimeRpcEvent event) {
        Map<String, ClusterEntity> realTimeServices = ClusterSwiftServerService.getInstance().getClusterEntityByService(ServiceType.REAL_TIME);
        if (null == realTimeServices || realTimeServices.isEmpty()) {
            throw new RuntimeException("Cannot find realTime service");
        }
        // Recover 不在这里做了
//        try {
//            switch (event.subEvent()) {
//                case RECOVER:
//                    final Map<String, List<SegmentKey>> map = clusterSegmentService.getClusterSegments();
//                    Iterator<Map.Entry<String, ClusterEntity>> iterator = realTimeServices.entrySet().iterator();
//                    while (iterator.hasNext()) {
//                        Map.Entry<String, ClusterEntity> entityEntry = iterator.next();
//                        final String key = entityEntry.getKey();
//                        ClusterEntity entity = entityEntry.getValue();
//                        final List<SegmentKey> list = map.get(key);
//                        if (null != list && !list.isEmpty()) {
//                            final long start = System.currentTimeMillis();
//                            runAsyncRpc(key, entity.getServiceClass(), "recover", list)
//                                    .addCallback(new AsyncRpcCallback() {
//                                        @Override
//                                        public void success(Object result) {
//                                            LOGGER.info(String.format("clusterId: %s, recover cost: %d ms", key, (System.currentTimeMillis() - start)));
//                                        }
//
//                                        @Override
//                                        public void fail(Exception e) {
//                                            LOGGER.error(String.format("clusterId: %s, recover error!", key), e);
//                                        }
//                                    });
//                        }
//                    }
//                    return null;
//            }
//        } catch (Exception e) {
//            LOGGER.error("handle error! ", e);
//        }
        return null;
    }
}
