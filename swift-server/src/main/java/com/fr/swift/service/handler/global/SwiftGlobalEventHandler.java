package com.fr.swift.service.handler.global;

import com.fr.event.EventDispatcher;
import com.fr.swift.basics.AsyncRpcCallback;
import com.fr.swift.cluster.entity.ClusterEntity;
import com.fr.swift.cluster.service.ClusterSwiftServerService;
import com.fr.swift.cluster.service.SegmentLocationInfoContainer;
import com.fr.swift.config.service.SwiftClusterSegmentService;
import com.fr.swift.config.service.SwiftMetaDataService;
import com.fr.swift.event.analyse.SegmentLocationRpcEvent;
import com.fr.swift.event.base.AbstractGlobalRpcEvent;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.SegmentLocationInfo;
import com.fr.swift.service.ServiceType;
import com.fr.swift.service.handler.SwiftServiceHandlerManager;
import com.fr.swift.service.handler.base.AbstractHandler;
import com.fr.swift.service.handler.history.HistoryDataSyncManager;
import com.fr.swift.structure.Pair;
import com.fr.swift.task.TaskKey;
import com.fr.swift.task.TaskResult;
import com.fr.swift.task.impl.TaskEvent;
import com.fr.swift.utils.ClusterCommonUtils;
import com.fr.third.springframework.beans.factory.annotation.Autowired;
import com.fr.third.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author yee
 * @date 2018/6/27
 */
@Service
public class SwiftGlobalEventHandler extends AbstractHandler<AbstractGlobalRpcEvent> {
    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(SwiftGlobalEventHandler.class);
    @Autowired(required = false)
    private SwiftClusterSegmentService segmentService;
    @Autowired(required = false)
    private SwiftMetaDataService swiftMetaDataService;
    @Autowired(required = false)
    private HistoryDataSyncManager historyDataSyncManager;

    @Override
    public <S extends Serializable> S handle(AbstractGlobalRpcEvent event) throws Exception {
        switch (event.subEvent()) {
            case TASK_DONE:
                Pair<TaskKey, TaskResult> pair = (Pair<TaskKey, TaskResult>) event.getContent();
                EventDispatcher.fire(TaskEvent.DONE, pair);
                break;
            case CLEAN:
                Map<String, ClusterEntity> analyseMap = ClusterSwiftServerService.getInstance().getClusterEntityByService(ServiceType.ANALYSE);
                Map<String, ClusterEntity> realTimeMap = ClusterSwiftServerService.getInstance().getClusterEntityByService(ServiceType.REAL_TIME);
                Map<String, ClusterEntity> historyMap = ClusterSwiftServerService.getInstance().getClusterEntityByService(ServiceType.HISTORY);
                Map<String, ClusterEntity> indexingMap = ClusterSwiftServerService.getInstance().getClusterEntityByService(ServiceType.INDEXING);
                String[] sourceKeys = (String[]) event.getContent();
                try {
                    if (null != sourceKeys) {
                        clean(analyseMap, sourceKeys);
                        clean(realTimeMap, sourceKeys);
                        clean(historyMap, sourceKeys);
                        clean(indexingMap, sourceKeys);
                    }
                } catch (Exception e) {
                    LOGGER.error(e);
                }
                break;
            case PUSH_SEG:
                SegmentLocationInfo info = (SegmentLocationInfo) event.getContent();
                SwiftServiceHandlerManager.getManager().
                        handle(new SegmentLocationRpcEvent(SegmentLocationInfo.UpdateType.PART, info));
                break;
            case GET_ANALYSE_REAL_TIME:
                Map<String, ClusterEntity> realtime = ClusterSwiftServerService.getInstance().getClusterEntityByService(ServiceType.REAL_TIME);
                Map<String, ClusterEntity> analyse = ClusterSwiftServerService.getInstance().getClusterEntityByService(ServiceType.ANALYSE);
                Map<ServiceType, List<String>> result = new HashMap<ServiceType, List<String>>();
                makeResultMap(realtime, result, ServiceType.REAL_TIME);
                makeResultMap(analyse, result, ServiceType.ANALYSE);
                return (S) result;
            case NODE_STARTED:
                String clusterId = (String) event.getContent();
                Map<String, ClusterEntity> analyseNodeMap = ClusterSwiftServerService.getInstance().getClusterEntityByService(ServiceType.ANALYSE);
                if (analyseNodeMap.containsKey(clusterId)) {
                    triggerAfterAnalyseRegister(Pair.of(clusterId, analyseNodeMap.get(clusterId)));
                }
                break;
            default:
                break;
        }
        return null;
    }

    private void triggerAfterAnalyseRegister(Pair<String, ClusterEntity> service) {
        String id = service.getKey();
        try {
            Class clazz = service.getValue().getServiceClass();
            List<Pair<SegmentLocationInfo.UpdateType, SegmentLocationInfo>> pairs = SegmentLocationInfoContainer.getContainer().getLocationInfo();
            for (Pair<SegmentLocationInfo.UpdateType, SegmentLocationInfo> pair : pairs) {
                ClusterCommonUtils.runAsyncRpc(id, clazz,
                        clazz.getMethod("updateSegmentInfo", SegmentLocationInfo.class, SegmentLocationInfo.UpdateType.class),
                        pair.getValue(), pair.getKey());
            }
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
        }
    }

    private void makeResultMap(Map<String, ClusterEntity> realtime, Map<ServiceType, List<String>> result, ServiceType type) {
        for (String id : realtime.keySet()) {
            if (result.get(type) == null) {
                result.put(type, new ArrayList<String>());
            }
            result.get(type).add(id);
        }
    }

    private void clean(Map<String, ClusterEntity> map, String[] sourceKeys) throws Exception {
        Iterator<Map.Entry<String, ClusterEntity>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, ClusterEntity> entry = iterator.next();
            runAsyncRpc(entry.getKey(), entry.getValue().getServiceClass(), "cleanMetaCache", sourceKeys)
                    .addCallback(new AsyncRpcCallback() {
                        @Override
                        public void success(Object result) {

                        }

                        @Override
                        public void fail(Exception e) {

                        }
                    });
        }
    }
}
