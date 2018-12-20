package com.fr.swift.service.handler.global;

import com.fr.general.ComparatorUtils;
import com.fr.swift.ClusterNodeService;
import com.fr.swift.basics.AsyncRpcCallback;
import com.fr.swift.cluster.entity.ClusterEntity;
import com.fr.swift.cluster.service.ClusterSwiftServerService;
import com.fr.swift.config.bean.SwiftServiceInfoBean;
import com.fr.swift.config.service.SwiftClusterSegmentService;
import com.fr.swift.config.service.SwiftMetaDataService;
import com.fr.swift.config.service.SwiftServiceInfoService;
import com.fr.swift.core.cluster.FRClusterNodeService;
import com.fr.swift.db.Where;
import com.fr.swift.event.ClusterEvent;
import com.fr.swift.event.ClusterEventType;
import com.fr.swift.event.ClusterListenerHandler;
import com.fr.swift.event.ClusterType;
import com.fr.swift.event.SwiftEventDispatcher;
import com.fr.swift.event.analyse.SegmentLocationRpcEvent;
import com.fr.swift.event.base.AbstractGlobalRpcEvent;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SegmentLocationInfo;
import com.fr.swift.selector.ClusterSelector;
import com.fr.swift.service.ServiceType;
import com.fr.swift.service.handler.SwiftServiceHandlerManager;
import com.fr.swift.service.handler.base.AbstractHandler;
import com.fr.swift.service.handler.history.HistoryDataSyncManager;
import com.fr.swift.source.SourceKey;
import com.fr.swift.structure.Pair;
import com.fr.swift.task.TaskKey;
import com.fr.swift.task.TaskResult;
import com.fr.swift.task.impl.TaskEvent;
import com.fr.swift.util.Crasher;
import com.fr.third.springframework.beans.factory.annotation.Autowired;
import com.fr.third.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
    @Autowired(required = false)
    private SwiftServiceInfoService serviceInfoService;

    @Override
    public <S extends Serializable> S handle(AbstractGlobalRpcEvent event) throws Exception {
        switch (event.subEvent()) {
            case CHECK_MASTER:
                List<SwiftServiceInfoBean> masterServiceInfoBeanList = serviceInfoService.getServiceInfoByService(ClusterNodeService.SERVICE);
                if (masterServiceInfoBeanList.isEmpty()) {
                    Crasher.crash("Master is null!");
                }
                SwiftServiceInfoBean masterBean = masterServiceInfoBeanList.get(0);
                if (!ComparatorUtils.equals(ClusterSelector.getInstance().getFactory().getMasterId(), masterBean.getClusterId())) {
                    LOGGER.info("Master is not synchronized!");
                    FRClusterNodeService.getInstance().competeMaster();
                    ClusterListenerHandler.handlerEvent(new ClusterEvent(ClusterEventType.JOIN_CLUSTER, ClusterType.FR));
                }
                break;
            case TASK_DONE:
                Pair<TaskKey, TaskResult> pair = (Pair<TaskKey, TaskResult>) event.getContent();
                SwiftEventDispatcher.fire(TaskEvent.DONE, pair);
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
            case DELETE:
                Pair<SourceKey, Where> content = (Pair<SourceKey, Where>) event.getContent();
                SourceKey sourceKey = content.getKey();
                Where where = content.getValue();
                Map<String, ClusterEntity> realTimeServices = ClusterSwiftServerService.getInstance().getClusterEntityByService(ServiceType.REAL_TIME);
                dealDelete(sourceKey, where, realTimeServices, "realtimeDelete");
                Map<String, ClusterEntity> historyServices = ClusterSwiftServerService.getInstance().getClusterEntityByService(ServiceType.HISTORY);
                Iterator<Map.Entry<String, ClusterEntity>> iterator = historyServices.entrySet().iterator();
                while (iterator.hasNext()) {
                    if (realTimeServices.containsKey(iterator.next().getKey())) {
                        iterator.remove();
                    }
                }
                dealDelete(sourceKey, where, historyServices, "historyDelete");
                break;
            case TRUNCATE:
                String truncateSourceKey = (String) event.getContent();
                Map<String, ClusterEntity> histories = ClusterSwiftServerService.getInstance().getClusterEntityByService(ServiceType.HISTORY);
                if (null == histories || histories.isEmpty()) {
                    throw new RuntimeException("Cannot find any services");
                }
                for (Map.Entry<String, ClusterEntity> entry : histories.entrySet()) {
                    String clusterId = entry.getKey();
                    runAsyncRpc(clusterId, entry.getValue().getServiceClass(), "truncate", truncateSourceKey).addCallback(new AsyncRpcCallback() {
                        @Override
                        public void success(Object result) {

                        }

                        @Override
                        public void fail(Exception e) {
                            SwiftLoggers.getLogger().error(e);
                        }
                    });
                }
            default:
                break;
        }
        return null;
    }

    private void dealDelete(SourceKey sourceKey, Where where, Map<String, ClusterEntity> services, String method) throws Exception {
        if (null == services || services.isEmpty()) {
            SwiftLoggers.getLogger().warn("Cannot find services");
            return;
        }
        List<String> uploadedSegments = new ArrayList<String>();
        for (Map.Entry<String, ClusterEntity> entry : services.entrySet()) {
            String clusterId = entry.getKey();
            List<SegmentKey> segmentKeys = segmentService.getOwnSegments(clusterId).get(sourceKey.getId());
            List<String> needUploadSegs = new ArrayList<String>();
            if (null != segmentKeys) {
                for (SegmentKey segmentKey : segmentKeys) {
                    if (segmentKey.getStoreType().isPersistent()) {
                        String segKey = segmentKey.toString();
                        // 如果不包含就放到需要上传的list
                        if (!uploadedSegments.contains(segKey)) {
                            needUploadSegs.add(segKey);
                            uploadedSegments.add(segKey);
                        }
                    }
                }
                runAsyncRpc(clusterId, entry.getValue().getServiceClass(), method, sourceKey, where, needUploadSegs);
            }
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
        for (Entry<String, ClusterEntity> entry : map.entrySet()) {
            try {
                runAsyncRpc(entry.getKey(), entry.getValue().getServiceClass(), "cleanMetaCache", sourceKeys);
            } catch (Exception e) {
                SwiftLoggers.getLogger().error(e);
            }
        }
    }
}
