package com.fr.swift.cloud.service;

import com.fr.swift.cloud.SwiftContext;
import com.fr.swift.cloud.basics.base.selector.ProxySelector;
import com.fr.swift.cloud.beans.annotation.SwiftBean;
import com.fr.swift.cloud.config.service.SwiftSegmentService;
import com.fr.swift.cloud.executor.TaskProducer;
import com.fr.swift.cloud.executor.task.bean.CollateBean;
import com.fr.swift.cloud.executor.task.impl.CollateExecutorTask;
import com.fr.swift.cloud.log.SwiftLoggers;
import com.fr.swift.cloud.property.SwiftProperty;
import com.fr.swift.cloud.segment.SegmentKey;
import com.fr.swift.cloud.segment.SegmentService;
import com.fr.swift.cloud.segment.collate.SwiftFragmentFilter;
import com.fr.swift.cloud.service.executor.CollateExecutor;
import com.fr.swift.cloud.source.SourceKey;
import com.fr.swift.cloud.util.concurrent.PoolThreadFactory;
import com.fr.swift.cloud.util.concurrent.SwiftExecutors;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
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
@SwiftBean
public final class SwiftCollateExecutor implements Runnable, CollateExecutor {

    private static final String COLLATE_TASK = "COLLATE";

    private static final int LINE_VIRTUAL_INDEX = -1;

    private static final String DEFAULT_URI = "0";

    private ScheduledExecutorService executorService;

    private SwiftSegmentService swiftSegmentService;

    private static DateFormat DATE_FORMAT = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
    private static DateFormat DAY_FORMAT = new SimpleDateFormat("yy-MM-dd");
    private static long ONE_DAY = 24 * 60 * 60 * 1000;

    private SwiftCollateExecutor() {
    }

    @Override
    public void start() {
        if (Arrays.asList(SwiftProperty.get().getExecutorTaskType()).contains(COLLATE_TASK)) {
            long initDelay = getTimeMillis(SwiftProperty.get().getCollateTime()) - System.currentTimeMillis();
            initDelay = initDelay > 0 ? initDelay : ONE_DAY + initDelay;
            executorService = SwiftExecutors.newScheduledThreadPool(1, new PoolThreadFactory(getClass()));
            executorService.scheduleAtFixedRate(this, initDelay, ONE_DAY, TimeUnit.MILLISECONDS);
            SwiftLoggers.getLogger().info("Start collate executor at {}", SwiftProperty.get().getCollateTime());
//            executorService.scheduleWithFixedDelay(this, 20, 100000, TimeUnit.SECONDS);
            swiftSegmentService = SwiftContext.get().getBean(SwiftSegmentService.class);
        }
    }

    @Override
    public void stop() {
        executorService.shutdown();
    }

    private static long getTimeMillis(String time) {
        try {
            Date currentDate = DATE_FORMAT.parse(DAY_FORMAT.format(new Date()) + " " + time);
            return currentDate.getTime();
        } catch (ParseException e) {
            return 0;
        }
    }

    @Override
    public void run() {
        triggerCollate();
    }

    private void triggerCollate() {
        try {
            Map<SourceKey, List<SegmentKey>> allSegments = swiftSegmentService.getOwnSegments(SwiftProperty.get().getMachineId());
            for (Map.Entry<SourceKey, List<SegmentKey>> tableEntry : allSegments.entrySet()) {
                SourceKey tableKey = tableEntry.getKey();
                List<SegmentKey> keys = new ArrayList<SegmentKey>();
                for (SegmentKey key : tableEntry.getValue()) {
                    if (key.getStoreType().isTransient()) {
                        continue;
                    }
                    keys.add(key);
                }
                if (!keys.isEmpty()) {
                    // TODO: 2019/9/12 先改成凌晨2点触发，单线程同步跑，防止宕机先
                    Set<SegmentKey> segmentKeysSet = new HashSet<>(keys);

                    SegmentService segmentService = SwiftContext.get().getBean(SegmentService.class);
                    Map<SegmentKey, Integer> bucketIndexMap = segmentService.getBucketByTable(tableEntry.getKey()).getBucketIndexMap();

                    //H.J TODO : 2020/12/15 加了一层路径, 旧cubes都删掉后舍弃
                    Map<Integer, Map<String, List<SegmentKey>>> segKeysByBucketMap = new HashMap<>();
                    if (!bucketIndexMap.isEmpty()) {
                        bucketIndexMap.entrySet().stream()
                                .filter(entry -> segmentKeysSet.contains(entry.getKey()))
                                .forEach(entry -> segKeysByBucketMap.computeIfAbsent(entry.getValue(), k -> new HashMap<>())
                                        .computeIfAbsent(entry.getKey().getSegmentUri(), k -> new ArrayList<>()).add(entry.getKey()));
                    } else {
                        segKeysByBucketMap.putAll(Collections.singletonMap(LINE_VIRTUAL_INDEX, Collections.singletonMap(DEFAULT_URI, keys)));
                    }

                    Map<Integer, Map<String, List<SegmentKey>>> collateMap = new HashMap<>();
                    segKeysByBucketMap.forEach((key1, value1) -> value1.entrySet()
                            .stream()
                            .filter(listEntry -> listEntry.getValue().size() >= SwiftFragmentFilter.FRAGMENT_NUMBER)
                            .forEach(listEntry -> collateMap.computeIfAbsent(key1, k -> new HashMap<>())
                                    .computeIfAbsent(listEntry.getKey(), v -> new ArrayList<>())
                                    .addAll(listEntry.getValue())));

                    collateMap.forEach((key1, value) -> value.forEach((key, value1) -> batchProduceCollate(tableKey, value1)));
                }
            }

        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
        }
    }

    private void batchProduceCollate(SourceKey tableKey, List<SegmentKey> segmentKeys) {
        ServiceContext serviceContext = ProxySelector.getProxy(ServiceContext.class);
        try {
            //分批collate，一批 5 - 100 块
            List<SegmentKey> allSegmentKeyList = segmentKeys;
            if (allSegmentKeyList.size() < SwiftFragmentFilter.MAX_FRAGMENT_NUMBER) {
                produce(tableKey, allSegmentKeyList);
            } else {
                int batch = allSegmentKeyList.size() / SwiftFragmentFilter.MAX_FRAGMENT_NUMBER;
                int count = 0;
                for (; count < batch; count++) {
                    produce(tableKey, allSegmentKeyList.subList(count * SwiftFragmentFilter.MAX_FRAGMENT_NUMBER, (count + 1) * SwiftFragmentFilter.MAX_FRAGMENT_NUMBER));
                }
                produce(tableKey, allSegmentKeyList.subList(count * SwiftFragmentFilter.MAX_FRAGMENT_NUMBER, allSegmentKeyList.size()));
            }
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
        }
    }

    private void produce(SourceKey tableKey, List<SegmentKey> segmentKeyList) throws Exception {
        TaskProducer.produceTask(new CollateExecutorTask(CollateBean.of(tableKey, segmentKeyList)));
    }
}
