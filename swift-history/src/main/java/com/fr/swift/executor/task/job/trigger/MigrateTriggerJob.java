package com.fr.swift.executor.task.job.trigger;

import com.fr.swift.SwiftContext;
import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.config.entity.SwiftNodeInfo;
import com.fr.swift.config.service.SwiftNodeInfoService;
import com.fr.swift.config.service.SwiftSegmentLocationService;
import com.fr.swift.config.service.SwiftSegmentService;
import com.fr.swift.db.MigrateType;
import com.fr.swift.executor.task.bean.MigrateBean;
import com.fr.swift.executor.task.constants.PathConstants;
import com.fr.swift.executor.task.impl.MigrateExecutorTask;
import com.fr.swift.executor.task.info.interval.MigInterval;
import com.fr.swift.executor.task.utils.MigrationZipUtils;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SegmentService;
import com.fr.swift.service.CollateService;
import com.fr.swift.service.ServiceContext;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.alloter.impl.hash.HashIndexRange;
import com.fr.swift.source.alloter.impl.hash.function.HashType;
import com.fr.swift.util.Strings;
import com.fr.swift.util.exception.LambdaWrapper;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Heng.J
 * @date 2020/12/3
 * @description 迁移任务工作类, 合并、迁移、配置更新
 * @since swift-1.2.0
 */
public class MigrateTriggerJob {

    private static final int COLLATE_BATCH_SIZE = 100;

    private final Object lock = new Object();

    private MigrateBean migrateBean;

    private MigrateType originType;

    private final SwiftNodeInfoService nodeInfoService = SwiftContext.get().getBean(SwiftNodeInfoService.class);

    private static final MigrateTriggerJob INSTANCE = new MigrateTriggerJob();

    public static MigrateTriggerJob getInstance() {
        return INSTANCE;
    }

    public Object getLock() {
        return lock;
    }

    public void setMigrateBean(MigrateBean migrateBean) {
        this.migrateBean = migrateBean;
    }

    public void setOriginType(MigrateType originType) {
        this.originType = originType;
    }

    public void triggerMigrate() {
        boolean migrateSuccess = false;
        SwiftNodeInfo nodeInfo = nodeInfoService.getOwnNodeInfo();
        try {
            // 等待时间过长, 本次失败
            int curHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
            if (curHour > nodeInfo.getLimitStartHour()) {
                SwiftLoggers.getLogger().warn("The end hour {} of preparing for migration exceeded the expected time", curHour);
                return;
            }
            // 获取migIndex相关seg
            Map<SourceKey, List<SegmentKey>> migSegments = getMigSegmentsByHashType(nodeInfo.getRelatedHashType());

            // 执行块合并
            collateMigSegments(migSegments);

            // 执行迁移
            migrateSuccess = MigrateExecutorTask.of(migrateBean).getJob().call();

            if (migrateSuccess) {
                // 更新seg配置
                updateSegmentsConfig(migSegments);

                // 删除本地cubes
                MigrationZipUtils.delDir(nodeInfo.getCubePath() + PathConstants.PATH_CUBES + migrateBean.getMigrateIndex());
            }
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
        } finally {
            // 更新迁移配置
            updateNodeInfoConfig(migrateSuccess, nodeInfo);

            // 通知迁移状态MigrateScheduleJob
            Object lock = MigrateTriggerJob.getInstance().getLock();
            synchronized (lock) {
                lock.notify();
            }
        }
    }

    /**
     * 根据迁移对应hash函数, 范围筛选相关相关块
     */
    private Map<SourceKey, List<SegmentKey>> getMigSegmentsByHashType(HashType relatedHashType) {
        SwiftSegmentService swiftSegmentService = SwiftContext.get().getBean(SwiftSegmentService.class);
        HashIndexRange indexRange = HashIndexRange.getRange(relatedHashType).ofKey(migrateBean.getMigrateIndex());
        return swiftSegmentService.getOwnSegmentsByRange(indexRange);
    }

    /**
     * 合并迁移涉及的seg
     */
    private void collateMigSegments(Map<SourceKey, List<SegmentKey>> migSegments) {
        CollateService collateService = SwiftContext.get().getBean(CollateService.class);
        try {
            for (Map.Entry<SourceKey, List<SegmentKey>> entry : migSegments.entrySet()) {
                Lists.partition(entry.getValue(), COLLATE_BATCH_SIZE)
                        .forEach(LambdaWrapper.rethrowConsumer(segKeys -> collateService.appointCollate(entry.getKey(), segKeys)));
            }
        } catch (Exception ignored) {
        }
    }


    /**
     * 更新迁移涉及的seg配置
     */
    private void updateSegmentsConfig(Map<SourceKey, List<SegmentKey>> migSegments) {
        try {
            Set<SegmentKey> segmentKeySet = migSegments.values().stream().flatMap(Collection::stream).collect(Collectors.toSet());
            // 缓存本地更新
            final SwiftSegmentLocationService locationService = SwiftContext.get().getBean(SwiftSegmentLocationService.class);
            locationService.updateBelongs(migrateBean.getMigrateTarget(), segmentKeySet);

            // 配置db删除
            List<SegmentKey> segmentKeys = new ArrayList<>(segmentKeySet);
            final SegmentService segmentService = SwiftContext.get().getBean(SegmentService.class);
            segmentService.removeSegments(segmentKeys);

            // 远程换存新增
            ServiceContext serviceContext = ProxySelector.getProxy(ServiceContext.class);
            serviceContext.updateConfigs(segmentKeys, migrateBean.getMigrateTarget());
        } catch (Exception ignore) {
        }
    }

    /**
     * 根据迁移成功状态更新数据库配置
     */
    private void updateNodeInfoConfig(boolean migrateSuccess, SwiftNodeInfo nodeInfo) {
        // 迁移后由节点自身更新数据库配置
        if (migrateSuccess) {
            nodeInfo.setMigrateType(MigrateType.SUCCESS);
            MigInterval migInterval = MigInterval.getMigrateInterval(nodeInfo);
            migInterval.addOnePeriod();
            nodeInfo.setBeginIndex(migInterval.getBeginIndex());
            nodeInfo.setEndIndex(migInterval.getEndIndex());
            String migrateIndex = migrateBean.getMigrateIndex();
            SwiftNodeInfo targetNodeInfo = nodeInfoService.getNodeInfo(nodeInfo.getMigrateTarget());
            if (targetNodeInfo.getEndIndex().compareTo(migrateIndex) < 0) {
                targetNodeInfo.setEndIndex(migrateIndex);
                nodeInfoService.update(targetNodeInfo);
            }
        } else {
            nodeInfo.setMigrateType(originType.levelUp());
        }
        nodeInfo.setBlockingIndex(Strings.EMPTY);
        nodeInfo.setReadyStatus(0);
        nodeInfoService.update(nodeInfo);
    }
}
