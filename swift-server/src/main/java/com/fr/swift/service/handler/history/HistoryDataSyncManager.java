package com.fr.swift.service.handler.history;

import com.fr.stable.StringUtils;
import com.fr.swift.config.service.SwiftClusterSegmentService;
import com.fr.swift.event.analyse.SegmentLocationRpcEvent;
import com.fr.swift.event.history.HistoryLoadSegmentRpcEvent;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.netty.rpc.client.AsyncRpcCallback;
import com.fr.swift.segment.SegmentDestination;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SegmentLocationInfo;
import com.fr.swift.segment.impl.SegmentLocationInfoImpl;
import com.fr.swift.service.ClusterSwiftServerService;
import com.fr.swift.service.ServiceType;
import com.fr.swift.service.entity.ClusterEntity;
import com.fr.swift.service.handler.SwiftServiceHandlerManager;
import com.fr.swift.service.handler.base.AbstractHandler;
import com.fr.swift.service.handler.history.rule.DataSyncRule;
import com.fr.swift.structure.Pair;
import com.fr.third.springframework.beans.factory.annotation.Autowired;
import com.fr.third.springframework.stereotype.Service;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author yee
 * @date 2018/6/8
 */
@Service
public class HistoryDataSyncManager extends AbstractHandler<HistoryLoadSegmentRpcEvent> {

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(HistoryDataSyncManager.class);

    @Autowired(required = false)
    private SwiftClusterSegmentService clusterSegmentService;
    private DataSyncRule rule = DataSyncRule.DEFAULT;

    public void setRule(DataSyncRule rule) {
        this.rule = rule;
    }

    public <S extends Serializable> S handle(HistoryLoadSegmentRpcEvent event) {
        Map<String, ClusterEntity> services = ClusterSwiftServerService.getInstance().getClusterEntityByService(ServiceType.HISTORY);
        if (null == services || services.isEmpty()) {
            throw new RuntimeException("Cannot find history service");
        }

        String needLoadSourceKey = event.getContent();

        Map<String, List<SegmentKey>> allSegments = clusterSegmentService.getAllSegments();

        if (StringUtils.isNotEmpty(needLoadSourceKey)) {
            List<SegmentKey> keys = allSegments.get(needLoadSourceKey);
            allSegments.clear();
            allSegments.put(needLoadSourceKey, keys);
        }

        Map<String, List<SegmentKey>> needLoadSegment = new HashMap<String, List<SegmentKey>>(allSegments);
        Iterator<String> keyIterator = services.keySet().iterator();
        Map<String, List<SegmentKey>> exists = new HashMap<String, List<SegmentKey>>();
        while (keyIterator.hasNext()) {
            String key = keyIterator.next();
            if (null == exists.get(key)) {
                exists.put(key, new ArrayList<SegmentKey>());
            }
            Map<String, List<SegmentKey>> segments = clusterSegmentService.getOwnSegments(key);
            Iterator<Map.Entry<String, List<SegmentKey>>> existsIter = segments.entrySet().iterator();
            while (existsIter.hasNext()) {
                Map.Entry<String, List<SegmentKey>> entry = existsIter.next();
                String sourceKey = entry.getKey();
//                for (SegmentKey segmentKey : entry.getValue()) {
//                    if (null != needLoadSegment.get(sourceKey)) {
//                        needLoadSegment.get(sourceKey).remove(segmentKey);
//                    }

//                }
                exists.get(key).addAll(entry.getValue());
            }
        }
        final Map<String, Pair<Integer, List<SegmentDestination>>> destinations = new HashMap<String, Pair<Integer, List<SegmentDestination>>>();
        Map<String, Set<SegmentKey>> result = rule.calculate(exists, needLoadSegment, destinations);
        keyIterator = result.keySet().iterator();
        try {
            while (keyIterator.hasNext()) {
                final String key = keyIterator.next();
                ClusterEntity entity = services.get(key);
                Iterator<SegmentKey> valueIterator = result.get(key).iterator();
                Set<URI> uriSet = new HashSet<URI>();
                final List<Pair<String, String>> idList = new ArrayList<Pair<String, String>>();
                while (valueIterator.hasNext()) {
                    SegmentKey segmentKey = valueIterator.next();
                    uriSet.add(segmentKey.getUri());
                    idList.add(Pair.of(segmentKey.getTable().getId(), segmentKey.toString()));
                }
                runAsyncRpc(key, entity.getServiceClass(), "load", uriSet)
                        .addCallback(new AsyncRpcCallback() {
                            @Override
                            public void success(Object result) {
                                Map<String, List<Pair<String, String>>> segmentTable = new HashMap<String, List<Pair<String, String>>>();
                                segmentTable.put(key, idList);
                                clusterSegmentService.updateSegmentTable(segmentTable);
//                                updateDestination(destinations);
                                SwiftServiceHandlerManager.getManager().
                                        handle(new SegmentLocationRpcEvent(SegmentLocationInfo.UpdateType.ALL, new SegmentLocationInfoImpl(ServiceType.HISTORY, destinations)));
                            }

                            @Override
                            public void fail(Exception e) {
                                LOGGER.error("Load failed! ", e);
                            }
                        });

            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }
}
