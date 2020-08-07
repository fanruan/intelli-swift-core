package com.fr.swift.executor;

import com.fr.swift.SwiftContext;
import com.fr.swift.config.service.SwiftSegmentService;
import com.fr.swift.executor.task.impl.CollateExecutorTask;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.property.SwiftProperty;
import com.fr.swift.quartz.config.ScheduleTaskType;
import com.fr.swift.quartz.execute.BaseScheduleJob;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SegmentService;
import com.fr.swift.segment.collate.SwiftFragmentFilter;
import com.fr.swift.source.SourceKey;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Moira
 * @date 2020/8/3
 * @description
 * @since swift-1.2.0
 */
@DisallowConcurrentExecution
public class SwiftCollateJob implements BaseScheduleJob {
    private static final int LINE_VIRTUAL_INDEX = -1;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        SwiftSegmentService swiftSegmentService = SwiftContext.get().getBean(SwiftSegmentService.class);
        try {
            Map<SourceKey, List<SegmentKey>> allSegments = swiftSegmentService.getOwnSegments(SwiftProperty.get().getMachineId());
            for (Map.Entry<SourceKey, List<SegmentKey>> tableEntry : allSegments.entrySet()) {
                SourceKey tableKey = tableEntry.getKey();
                List<SegmentKey> keys = new ArrayList<>();
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

                    Map<Integer, List<SegmentKey>> segKeysByBucketMap = new HashMap<>();
                    if (!bucketIndexMap.isEmpty()) {
                        bucketIndexMap.entrySet()
                                .stream()
                                .filter((entry) -> segmentKeysSet.contains(entry.getKey()))
                                .forEach((entry) -> segKeysByBucketMap.computeIfAbsent(entry.getValue(), k -> new ArrayList<>()).add(entry.getKey()));
                    } else {
                        segKeysByBucketMap.putAll(Collections.singletonMap(LINE_VIRTUAL_INDEX, keys));
                    }


                    Map<Integer, List<SegmentKey>> collateMap = new HashMap<>();
                    segKeysByBucketMap.entrySet()
                            .stream()
                            .filter((entry) -> entry.getValue().size() >= SwiftFragmentFilter.FRAGMENT_NUMBER)
                            .forEach((entry) -> collateMap.computeIfAbsent(entry.getKey(), k -> new ArrayList<>()).addAll(entry.getValue()));

                    for (Map.Entry<Integer, List<SegmentKey>> bucketListEntry : collateMap.entrySet()) {
                        try {
                            //分批collate，一批 5 - 100 块
                            List<SegmentKey> allSegmentKeyList = bucketListEntry.getValue();
                            if (allSegmentKeyList.size() < SwiftFragmentFilter.MAX_FRAGMENT_NUMBER) {
                                TaskProducer.produceTask(new CollateExecutorTask(tableKey, allSegmentKeyList));
                            } else {
                                int batch = allSegmentKeyList.size() / SwiftFragmentFilter.MAX_FRAGMENT_NUMBER;
                                int count = 0;
                                for (; count < batch; count++) {
                                    TaskProducer.produceTask(new CollateExecutorTask(tableKey, allSegmentKeyList.subList(count * SwiftFragmentFilter.MAX_FRAGMENT_NUMBER, (count + 1) * SwiftFragmentFilter.MAX_FRAGMENT_NUMBER)));
                                }
                                TaskProducer.produceTask(new CollateExecutorTask(tableKey, allSegmentKeyList.subList(count * SwiftFragmentFilter.MAX_FRAGMENT_NUMBER, allSegmentKeyList.size())));
                            }
                        } catch (Exception e) {
                            SwiftLoggers.getLogger().error(e);
                        }
                    }
                }
            }

        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
        }
    }

    @Override
    public String getCronExpression() {
        return SwiftProperty.get().getCollateTime();
    }

    @Override
    public ScheduleTaskType getExecutorType() {
        return ScheduleTaskType.COLLATE;
    }
}
