package com.fr.swift.service.handler.global;

import com.fr.event.EventDispatcher;
import com.fr.swift.config.service.SwiftClusterSegmentService;
import com.fr.swift.config.service.SwiftMetaDataService;
import com.fr.swift.event.analyse.SegmentLocationRpcEvent;
import com.fr.swift.event.base.AbstractGlobalRpcEvent;
import com.fr.swift.event.history.HistoryLoadSegmentRpcEvent;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.rpc.client.AsyncRpcCallback;
import com.fr.swift.segment.SegmentDestination;
import com.fr.swift.segment.SegmentLocationInfo;
import com.fr.swift.segment.impl.SegmentDestinationImpl;
import com.fr.swift.segment.impl.SegmentLocationInfoImpl;
import com.fr.swift.service.ClusterSwiftServerService;
import com.fr.swift.service.ServiceType;
import com.fr.swift.service.entity.ClusterEntity;
import com.fr.swift.service.handler.SwiftServiceHandlerManager;
import com.fr.swift.service.handler.base.AbstractHandler;
import com.fr.swift.service.handler.history.HistoryDataSyncManager;
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
    public <S extends Serializable> S handle(AbstractGlobalRpcEvent event) {
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
                Pair<ServiceType, List<String>> sources = (Pair<ServiceType, List<String>>) event.getContent();
                ServiceType type = sources.getKey();
                switch (type) {
                    case REAL_TIME:
                        calculateRealTimeDestination(sources);
                        break;
                    default:
                        if (null != sources.getValue() && !sources.getValue().isEmpty()) {
                            for (String s : sources.getValue()) {
                                historyDataSyncManager.handle(new HistoryLoadSegmentRpcEvent(s));
                            }
                        } else {
                            historyDataSyncManager.handle(new HistoryLoadSegmentRpcEvent());
                        }

                }


            default:
        }
        return null;
    }

    private void calculateRealTimeDestination(Pair<ServiceType, List<String>> sources) {
        Map<String, ClusterEntity> serviceMap = ClusterSwiftServerService.getInstance().getClusterEntityByService(sources.getKey());
        if (serviceMap.isEmpty()) {
            Crasher.crash("Cannot find any " + sources.getKey() + " service");
        }
        List<String> tables = sources.getValue();
        Iterator<Map.Entry<String, ClusterEntity>> iterator = serviceMap.entrySet().iterator();
        Map<String, Pair<Integer, List<SegmentDestination>>> destinations = new HashMap<String, Pair<Integer, List<SegmentDestination>>>();
        SegmentLocationInfo.UpdateType type = SegmentLocationInfo.UpdateType.PART;
        if (null == tables || tables.isEmpty()) {
            tables = new ArrayList<String>(swiftMetaDataService.getAllMetaData().keySet());
            type = SegmentLocationInfo.UpdateType.ALL;
        }
        while (iterator.hasNext()) {
            Map.Entry<String, ClusterEntity> entry = iterator.next();
            for (String table : tables) {
                if (null == destinations.get(table)) {
                    destinations.put(table, Pair.<Integer, List<SegmentDestination>>of(-1, new ArrayList<SegmentDestination>()));
                }
                destinations.get(table).getValue().add(new SegmentDestinationImpl(entry.getKey(), null, -1, entry.getValue().getServiceClass(), "realTimeQuery"));
            }
        }
        SwiftServiceHandlerManager.getManager().
                handle(new SegmentLocationRpcEvent(type, new SegmentLocationInfoImpl(ServiceType.REAL_TIME, destinations)));
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
