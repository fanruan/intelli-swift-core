package com.fr.swift.executor.task;

import com.fr.swift.SwiftContext;
import com.fr.swift.config.service.SwiftSegmentService;
import com.fr.swift.executor.TaskProducer;
import com.fr.swift.executor.task.impl.MigrateExecutorTask;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.property.MigrateProperty;
import com.fr.swift.property.SwiftProperty;
import com.fr.swift.quartz.config.ScheduleTaskType;
import com.fr.swift.quartz.execute.BaseScheduleJob;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SegmentService;
import com.fr.swift.segment.SegmentVisitedInfo;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Moira
 * @date 2020/8/3
 * @description
 * @since swift-1.2.0
 */
@DisallowConcurrentExecution
public class SwiftMigrateJob implements BaseScheduleJob {
    private static final MigrateProperty MIGRATE_PROPERTY = MigrateProperty.get();

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        SwiftProperty swiftProperty = SwiftProperty.get();
        SwiftSegmentService swiftSegmentService = SwiftContext.get().getBean(SwiftSegmentService.class);
        SegmentService segmentService = SwiftContext.get().getBean(SegmentService.class);
        List<SegmentKey> segmentKeys = swiftSegmentService.getSegKeyOnNode(swiftProperty.getMachineId());
        //拿出cube路径下需要迁移的块
        List<SegmentVisitedInfo> segmentInfos = segmentService.getSegmentInfos(segmentKeys).stream()
                .filter(r -> r.getLocation().equals(swiftProperty.getCubesPath()))
                .collect(Collectors.toList());

        List<SegmentVisitedInfo> needMigrate = new ArrayList<>();

        List<SegmentVisitedInfo> nobodyVisits = segmentInfos.stream().filter(r -> r.getVisits() == 0 && rule(r)).collect(Collectors.toList());
        //如果没被访问过的过期块超过最大迁移数量
        if (nobodyVisits.size() >= MIGRATE_PROPERTY.getMaxNum()) {
            needMigrate.addAll(nobodyVisits.subList(0, MIGRATE_PROPERTY.getMaxNum()));
        } else {
            //先把没被访问过的过期块放进迁移队里
            needMigrate.addAll(nobodyVisits);
            //最新被访问的排在后面
            List<SegmentVisitedInfo> collect = segmentInfos.stream()
                    .filter(r -> r.getVisits() > 0)
                    .sorted(Comparator.comparing(SegmentVisitedInfo::getVisitedTime))
                    .filter(this::rule)
                    .collect(Collectors.toList());
            if (!collect.isEmpty()) {
                //剩余可迁移的块数
                int remainingNumber = MIGRATE_PROPERTY.getMaxNum() - nobodyVisits.size();
                if (collect.size() * MIGRATE_PROPERTY.getLruCoefficient() > remainingNumber) {
                    needMigrate.addAll(collect.subList(0, remainingNumber));
                } else {
                    needMigrate.addAll(collect.subList(0, (int) (collect.size() * MIGRATE_PROPERTY.getLruCoefficient())));
                }
            }
        }
        try {
            TaskProducer.produceTask(new MigrateExecutorTask(needMigrate.stream().map(SegmentVisitedInfo::getSegmentKey).collect(Collectors.toList()), MIGRATE_PROPERTY.getBackupPath()));
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
        }
    }

    @Override
    public String getCronExpression() {
        return MIGRATE_PROPERTY.getCornExpression();
    }

    @Override
    public ScheduleTaskType getExecutorType() {
        return ScheduleTaskType.MIGRATE;
    }

    /**
     * @param segmentVisitedInfo
     * @return 块创建时间大于LifeCircle个月的
     */
    private boolean rule(SegmentVisitedInfo segmentVisitedInfo) {
        return (new Date().getTime() - segmentVisitedInfo.getCreateTime().getTime()) > MigrateProperty.get().getLifeCycle() * TimeUnit.DAYS.toMillis(30);
    }
}
