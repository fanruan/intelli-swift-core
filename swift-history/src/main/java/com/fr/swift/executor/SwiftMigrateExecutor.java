package com.fr.swift.executor;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.service.SwiftSegmentService;
import com.fr.swift.executor.task.impl.MigrateExecutorTask;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.property.MigrateProperty;
import com.fr.swift.property.SwiftProperty;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SegmentUtils;
import com.fr.swift.service.executor.MigrateExecutor;
import com.fr.swift.structure.Pair;
import com.fr.swift.util.concurrent.PoolThreadFactory;
import com.fr.swift.util.concurrent.SwiftExecutors;

import java.io.File;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Moira
 * @date 2020/7/21
 * @description
 * @since swift-1.2.0
 */
@SwiftBean
public class SwiftMigrateExecutor implements Runnable, MigrateExecutor {
    private static final String MIGRATE_TASK = "MIGRATE";

    private ScheduledExecutorService executorService;

    private SwiftSegmentService swiftSegmentService;

    private static final long ONE_DAY = 24 * 60 * 60 * 1000;

    @Override
    public void start() {
        if (Arrays.asList(SwiftProperty.get().getExecutorTaskType()).contains(MIGRATE_TASK)) {
            long initDelay = MigrateProperty.get().getStartTime() - System.currentTimeMillis();
            initDelay = initDelay > 0 ? initDelay : ONE_DAY + initDelay;
            executorService = SwiftExecutors.newScheduledThreadPool(1, new PoolThreadFactory(getClass()));
            executorService.scheduleAtFixedRate(this, initDelay, ONE_DAY, TimeUnit.MILLISECONDS);
            swiftSegmentService = SwiftContext.get().getBean(SwiftSegmentService.class);
        }
    }


    private void triggerMigrate() {
        MigrateProperty migrateProperty = MigrateProperty.get();
        List<SegmentKey> allSegments = swiftSegmentService.getSegKeyOnNode(SwiftProperty.get().getMachineId());
        //TODO: 2020/7/29 还需要根据块生成时间还是最近被访问排序筛选
        Map<File, SegmentKey> limit = allSegments.stream().limit(migrateProperty.getMaxNum())
                .map(r -> new Pair<>(new File(SegmentUtils.newSegment(r).getLocation().getAbsolutePath()), r))
                .collect(Collectors.toMap(Pair::getKey, Pair::getValue));
        try {
            TaskProducer.produceTask(new MigrateExecutorTask(
                    limit, migrateProperty.isRemote(), migrateProperty.getBackupPath(),
                    SwiftProperty.get().getCubesPath()));
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
        }
    }

    @Override
    public void stop() {
        executorService.shutdown();
    }

    @Override
    public void run() {
        Calendar cal = Calendar.getInstance();
        int currentDay = cal.get(Calendar.DATE);
        if (currentDay >= MigrateProperty.get().getDay()) {
            SwiftLoggers.getLogger().info("Start migrate executor at {}", MigrateProperty.get().getStartTime());
            triggerMigrate();
        } else {
            SwiftLoggers.getLogger().info("It's not time to migrate.");
        }
    }
}
