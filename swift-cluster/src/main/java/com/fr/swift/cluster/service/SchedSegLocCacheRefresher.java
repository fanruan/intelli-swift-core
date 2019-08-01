package com.fr.swift.cluster.service;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.entity.SwiftSegmentLocationEntity;
import com.fr.swift.config.service.SwiftSegmentLocationService;
import com.fr.swift.cube.io.Types.StoreType;
import com.fr.swift.segment.SegmentDestination;
import com.fr.swift.segment.SegmentLocationInfo.UpdateType;
import com.fr.swift.segment.SegmentLocationProvider;
import com.fr.swift.segment.bean.impl.SegmentLocationInfoImpl;
import com.fr.swift.segment.impl.RealTimeSegDestImpl;
import com.fr.swift.segment.impl.SegmentDestinationImpl;
import com.fr.swift.selector.ClusterSelector;
import com.fr.swift.service.ServiceType;
import com.fr.swift.service.SwiftAnalyseService;
import com.fr.swift.source.SourceKey;
import com.fr.swift.util.concurrent.PoolThreadFactory;
import com.fr.swift.util.concurrent.SwiftExecutors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author anchore
 * @date 2019/5/14
 */
@SwiftBean
public class SchedSegLocCacheRefresher implements Runnable {
    private final SwiftSegmentLocationService segLocSvc = SwiftContext.get().getBean(SwiftSegmentLocationService.class);

    private final SwiftAnalyseService analyseSvc = SwiftContext.get().getBean(SwiftAnalyseService.class);

    @Override
    public void run() {
        if (ClusterSelector.getInstance().getFactory().isCluster()) {
            refreshCache();
        }
    }

    private void refreshCache() {
        Map<SourceKey, List<SegmentDestination>> histSegDsts = new HashMap<SourceKey, List<SegmentDestination>>();
        Map<SourceKey, List<SegmentDestination>> realtSegDsts = new HashMap<SourceKey, List<SegmentDestination>>();
        HashMap<SourceKey, Set<String>> tableToNodes = new HashMap<SourceKey, Set<String>>();
        // table -> seg locations
        Map<String, List<SwiftSegmentLocationEntity>> segLocs = segLocSvc.findAll();
        for (Entry<String, List<SwiftSegmentLocationEntity>> entry : segLocs.entrySet()) {
            String tableKey = entry.getKey();
            if (!histSegDsts.containsKey(tableKey)) {
                histSegDsts.put(new SourceKey(tableKey), new ArrayList<SegmentDestination>());
            }
            if (!realtSegDsts.containsKey(tableKey)) {
                realtSegDsts.put(new SourceKey(tableKey), new ArrayList<SegmentDestination>());
            }
            SourceKey tableSrcKey = new SourceKey(tableKey);
            if (!tableToNodes.containsKey(tableSrcKey)) {
                tableToNodes.put(tableSrcKey, new HashSet<String>());
            }

            for (SwiftSegmentLocationEntity segLocEntity : entry.getValue()) {
                String clusterId = segLocEntity.getClusterId();

                tableToNodes.get(tableSrcKey).add(clusterId);

                String segId = segLocEntity.getSegmentId();
                String[] splitSegId = segId.split("@");
                int segOrder = Integer.parseInt(splitSegId[2]);
                switch (StoreType.valueOf(splitSegId[1])) {
                    case FINE_IO: {
                        SegmentDestinationImpl segDst = new SegmentDestinationImpl(clusterId, segId, segOrder);
                        histSegDsts.get(tableKey).add(segDst);
                        break;
                    }
                    case MEMORY: {
                        RealTimeSegDestImpl segDst = new RealTimeSegDestImpl(clusterId, segId, segOrder);
                        realtSegDsts.get(tableKey).add(segDst);
                        break;
                    }
                    default:
                        continue;
                }
            }
        }
        // 清除旧的分布
        for (Entry<SourceKey, Set<String>> entry : tableToNodes.entrySet()) {
            for (String nodeId : entry.getValue()) {
                SegmentLocationProvider.getInstance().removeTable(nodeId, entry.getKey());
            }
        }
        // 加入新的分布
        // TODO: 2019-05-14 anchore 暂不考虑全量的情况
        analyseSvc.updateSegmentInfo(new SegmentLocationInfoImpl(ServiceType.HISTORY, histSegDsts), UpdateType.PART);
        analyseSvc.updateSegmentInfo(new SegmentLocationInfoImpl(ServiceType.REAL_TIME, realtSegDsts), UpdateType.PART);
    }

    private SchedSegLocCacheRefresher() {
        SwiftExecutors.newSingleThreadScheduledExecutor(new PoolThreadFactory(getClass()))
                .scheduleWithFixedDelay(this, 1, 1, TimeUnit.HOURS);
    }
}