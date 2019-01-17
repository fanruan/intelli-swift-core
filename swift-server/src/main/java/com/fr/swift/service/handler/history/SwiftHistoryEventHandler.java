package com.fr.swift.service.handler.history;

import com.fr.swift.basics.AsyncRpcCallback;
import com.fr.swift.cluster.entity.ClusterEntity;
import com.fr.swift.cluster.service.ClusterSwiftServerService;
import com.fr.swift.config.service.SwiftClusterSegmentService;
import com.fr.swift.event.base.AbstractHistoryRpcEvent;
import com.fr.swift.event.base.EventResult;
import com.fr.swift.event.history.HistoryRemoveEvent;
import com.fr.swift.event.history.SegmentLoadRpcEvent;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.netty.rpc.server.ServiceMethodRegistry;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.service.ServiceType;
import com.fr.swift.service.handler.base.AbstractHandler;
import com.fr.swift.structure.Pair;
import com.fr.swift.utils.ClusterCommonUtils;
import com.fr.third.springframework.beans.factory.annotation.Autowired;
import com.fr.third.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author yee
 * @date 2018/6/8
 */
@Service
public class SwiftHistoryEventHandler extends AbstractHandler<AbstractHistoryRpcEvent> {

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(SwiftHistoryEventHandler.class);
    @Autowired
    private HistoryDataSyncManager historyDataSyncManager;
    @Autowired(required = false)
    private SwiftClusterSegmentService clusterSegmentService;

    @Override
    public <S extends Serializable> S handle(AbstractHistoryRpcEvent event) {
        try {
            switch (event.subEvent()) {
                case LOAD_SEGMENT:
                case TRANS_COLLATE_LOAD:
                    return historyDataSyncManager.handle((SegmentLoadRpcEvent) event);
                case COMMON_LOAD:
                    String source = event.getSourceClusterId();
                    handleCommonLoad(event, 0);
                    return (S) EventResult.success(source);
                case MODIFY_LOAD:
                    String sourceId = event.getSourceClusterId();
                    if (handleCommonLoad(event, 1)) {
                        return (S) EventResult.success(sourceId);
                    } else {
                        return (S) EventResult.failed(sourceId, "load failed");
                    }
                case CHECK_LOAD:
                    checkLoad(event.getSourceClusterId());
                    break;
                case HISTORY_REMOVE:
                    HistoryRemoveEvent removeEvent = (HistoryRemoveEvent) event;
                    Map<String, ClusterEntity> historyServices = ClusterSwiftServerService.getInstance().getClusterEntityByService(ServiceType.HISTORY);
                    Map<String, ClusterEntity> analyseServices = ClusterSwiftServerService.getInstance().getClusterEntityByService(ServiceType.ANALYSE);
                    //找所有history节点删seg文件
                    for (Map.Entry<String, ClusterEntity> serviceEntry : historyServices.entrySet()) {
                        try {
                            if (!removeEvent.getSourceClusterId().equals(serviceEntry.getKey())) {
                                ClusterCommonUtils.runAsyncRpc(serviceEntry.getKey(), serviceEntry.getValue().getServiceClass()
                                        , ServiceMethodRegistry.INSTANCE.getMethodByName("removeHistory"), removeEvent.getContent().getValue());
                            }
                        } catch (Exception e) {
                            SwiftLoggers.getLogger().error(e);
                        }
                    }
                    //找所有analyse节点删内存中segkey和location配置
                    for (Map.Entry<String, ClusterEntity> serviceEntry : analyseServices.entrySet()) {
                        try {
                            if (!removeEvent.getSourceClusterId().equals(serviceEntry.getKey())) {
                                List<SegmentKey> segmentKeyList = removeEvent.getContent().getValue();
                                List<String> segmentIdList = new ArrayList<String>();
                                for (SegmentKey segmentKey : segmentKeyList) {
                                    segmentIdList.add(segmentKey.getId());
                                }
                                ClusterCommonUtils.runAsyncRpc(serviceEntry.getKey(), serviceEntry.getValue().getServiceClass()
                                        , ServiceMethodRegistry.INSTANCE.getMethodByName("removeSegments"),
                                        serviceEntry.getKey(),
                                        removeEvent.getContent().getKey().getId(), segmentIdList);
                            }
                        } catch (Exception e) {
                            SwiftLoggers.getLogger().error(e);
                        }
                    }
                    break;
                default:
                    return null;
            }
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return null;
    }


    private void checkLoad(final String clusterId) {
        // TODO 新节点加入处理
    }

    private boolean handleCommonLoad(AbstractHistoryRpcEvent event, int wait) throws Exception {
        Map<String, ClusterEntity> services = ClusterSwiftServerService.getInstance().getClusterEntityByService(ServiceType.HISTORY);
        if (null == services || services.isEmpty()) {
            throw new RuntimeException("Cannot find history service");
        }
        Pair<String, Map<String, List<String>>> pair = (Pair<String, Map<String, List<String>>>) event.getContent();
        Iterator<Map.Entry<String, ClusterEntity>> iterator = services.entrySet().iterator();
        Map<String, List<String>> uris = pair.getValue();
        final CountDownLatch latch = wait > 0 ? new CountDownLatch(wait) : null;
        final AtomicBoolean success = new AtomicBoolean(true);
        while (iterator.hasNext()) {
            Map.Entry<String, ClusterEntity> entry = iterator.next();
            Map<String, List<SegmentKey>> map = clusterSegmentService.getOwnSegments(entry.getKey());
            List<SegmentKey> list = map.get(pair.getKey());
            Set<String> needLoad = new HashSet<String>();
            if (!list.isEmpty()) {
                for (SegmentKey segmentKey : list) {
                    String segKey = segmentKey.toString();
                    if (uris.containsKey(segKey)) {
                        needLoad.addAll(uris.get(segKey));
                    }
                }
            }
            if (!needLoad.isEmpty()) {
                Map<String, Set<String>> load = new HashMap<String, Set<String>>();
                load.put(pair.getKey(), needLoad);
                runAsyncRpc(entry.getKey(), entry.getValue().getServiceClass(), "load", load, false)
                        .addCallback(new AsyncRpcCallback() {
                            @Override
                            public void success(Object result) {
                                try {
                                    LOGGER.info("load success");
                                    success.set(true);
                                } finally {
                                    if (null != latch) {
                                        latch.countDown();
                                    }
                                }
                            }

                            @Override
                            public void fail(Exception e) {
                                if (null != latch) {
                                    latch.countDown();
                                }
                                LOGGER.error("load error! ", e);
                            }
                        });
            }
        }
        if (null != latch) {
            latch.await(30, TimeUnit.SECONDS);
        }
        return success.get();
    }
}
