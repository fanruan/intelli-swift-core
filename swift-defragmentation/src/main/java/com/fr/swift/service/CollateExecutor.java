package com.fr.swift.service;

import com.fr.swift.config.service.SwiftSegmentService;
import com.fr.swift.config.service.impl.SwiftSegmentServiceProvider;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.netty.rpc.server.ServiceMethodRegistry;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.collate.SwiftFragmentCollectRule;
import com.fr.swift.source.SourceKey;
import com.fr.swift.util.concurrent.PoolThreadFactory;
import com.fr.swift.util.concurrent.SwiftExecutors;
import com.fr.swift.utils.ClusterCommonUtils;
import com.fr.third.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * This class created on 2018/9/3
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
@Service
public final class CollateExecutor implements Runnable {

    private ScheduledExecutorService executorService;

    private SwiftSegmentService swiftSegmentService;

    private CollateExecutor() {

    }

    public void start() {
        executorService = SwiftExecutors.newScheduledThreadPool(1, new PoolThreadFactory(getClass()));
        executorService.scheduleWithFixedDelay(this, 60, 60, TimeUnit.MINUTES);
        swiftSegmentService = SwiftContext.get().getBean(SwiftSegmentServiceProvider.class);
    }

    public void stop() {
        executorService.shutdown();
        swiftSegmentService = null;
    }

    @Override
    public void run() {
        triggerCollate();
    }

    private void triggerCollate() {
        try {
            //所有节点的segmentKey
            Map<String, Map<String, List<SegmentKey>>> allSegmentKeys = swiftSegmentService.getAllSegLocations();
            //计算后需要collate的节点和segmentkey
            Map<String, Map<String, List<SegmentKey>>> collateMap = new HashMap<String, Map<String, List<SegmentKey>>>();
            //计算过程中已经算过的segKeys
            Set<SegmentKey> collatedSegKeys = new HashSet<SegmentKey>();

            for (Map.Entry<String, Map<String, List<SegmentKey>>> allEntry : allSegmentKeys.entrySet()) {
                String clusterId = allEntry.getKey();
                collateMap.put(clusterId, new HashMap<String, List<SegmentKey>>());

                for (Map.Entry<String, List<SegmentKey>> sourcekeyEntry : allEntry.getValue().entrySet()) {
                    String sourceKey = sourcekeyEntry.getKey();
                    List<SegmentKey> segmentKeys = new ArrayList<SegmentKey>(sourcekeyEntry.getValue());
                    segmentKeys.removeAll(collatedSegKeys);
                    if (segmentKeys.size() >= SwiftFragmentCollectRule.FRAGMENT_NUMBER) {
                        collateMap.get(clusterId).put(sourceKey, segmentKeys);
                        collatedSegKeys.addAll(segmentKeys);
                    }
                }
                if (!collateMap.get(clusterId).isEmpty()) {
                    for (Map.Entry<String, List<SegmentKey>> sourcekeyEntry : collateMap.get(clusterId).entrySet()) {
                        ClusterCommonUtils.runAsyncRpc(clusterId, CollateService.class,
                                ServiceMethodRegistry.INSTANCE.getMethodByName("appointCollate")
                                , new SourceKey(sourcekeyEntry.getKey()), sourcekeyEntry.getValue());
                    }
                }
            }

        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
        }
    }
}
