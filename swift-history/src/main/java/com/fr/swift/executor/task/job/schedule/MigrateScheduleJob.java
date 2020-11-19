package com.fr.swift.executor.task.job.schedule;

import com.fr.swift.SwiftContext;
import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.config.entity.SwiftNodeInfo;
import com.fr.swift.config.service.SwiftNodeInfoService;
import com.fr.swift.config.service.SwiftSegmentLocationService;
import com.fr.swift.config.service.SwiftSegmentService;
import com.fr.swift.db.MigrateType;
import com.fr.swift.executor.task.bean.MigrateBean;
import com.fr.swift.executor.task.impl.MigrateExecutorTask;
import com.fr.swift.executor.task.info.MigInterval;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.property.SwiftProperty;
import com.fr.swift.quartz.config.ScheduleTaskType;
import com.fr.swift.quartz.execute.ScheduleJob;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SegmentService;
import com.fr.swift.service.CollateService;
import com.fr.swift.service.ServiceContext;
import com.fr.swift.service.event.NodeEvent;
import com.fr.swift.service.event.NodeMessage;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.alloter.impl.hash.HashIndexRange;
import com.fr.swift.util.Strings;
import com.fr.swift.util.exception.LambdaWrapper;
import com.github.rholder.retry.Retryer;
import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import com.google.common.collect.Lists;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Heng.J
 * @date 2020/11/4
 * @description 迁移任务定时类
 * @since swift-1.2.0
 */
public class MigrateScheduleJob implements ScheduleJob {

    private static final int COLLATE_BATCH_SIZE = 100;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        ServiceContext serviceContext = ProxySelector.getProxy(ServiceContext.class);
        SwiftNodeInfoService nodeInfoService = SwiftContext.get().getBean(SwiftNodeInfoService.class);
        MigrateBean migrateBean = (MigrateBean) context.getJobDetail().getJobDataMap().get(MigrateBean.KEY);
        String migrateIndex = migrateBean.getMigrateIndex();
        String clusterId = SwiftProperty.get().getMachineId();

        // 失败尝试3次, 1分钟、3分钟、6分钟各一次
        Retryer<Boolean> retryer = RetryerBuilder.<Boolean>newBuilder()
                .retryIfResult(aBoolean -> false)
                .retryIfExceptionOfType(Throwable.class)
                .withStopStrategy(StopStrategies.stopAfterAttempt(3))
                .withWaitStrategy(WaitStrategies.incrementingWait(1, TimeUnit.MINUTES, 1, TimeUnit.MINUTES))
                .build();
        boolean migrateSuccess = false;
        SwiftNodeInfo originNodeInfo = nodeInfoService.getNodeInfo(clusterId);
        MigrateType originMigrateType = originNodeInfo.getMigrateType();
        try {
            boolean blockSuccess = retryer.call(() -> serviceContext.report(NodeEvent.BLOCK, NodeMessage.of(clusterId, migrateIndex)));
            if (blockSuccess) {
                HashIndexRange indexRange = HashIndexRange.getRange(originNodeInfo.getRelatedHashType()).ofKey(migrateIndex);
                // 执行块合并
                SwiftSegmentService swiftSegmentService = SwiftContext.get().getBean(SwiftSegmentService.class);
                Map<SourceKey, List<SegmentKey>> ownSegmentsByIndex = swiftSegmentService.getOwnSegmentsByIndex(indexRange);
                CollateService collateService = SwiftContext.get().getBean(CollateService.class);
                for (Map.Entry<SourceKey, List<SegmentKey>> entry : ownSegmentsByIndex.entrySet()) {
                    Lists.partition(entry.getValue(), COLLATE_BATCH_SIZE).forEach(LambdaWrapper.rethrowConsumer(segKeys -> collateService.appointCollate(entry.getKey(), segKeys)));
                }

                // 执行迁移
                migrateSuccess = MigrateExecutorTask.of(migrateBean).getJob().call();
                if (migrateSuccess) {
                    Set<SegmentKey> segmentKeySet = ownSegmentsByIndex.values().stream().flatMap(Collection::stream).collect(Collectors.toSet());
                    // 迁移成功更新segment配置  缓存和配置远程新加、本地删除
                    final SwiftSegmentLocationService locationService = SwiftContext.get().getBean(SwiftSegmentLocationService.class);
                    locationService.updateBelongs(migrateBean.getMigrateTarget(), segmentKeySet);

                    List<SegmentKey> segmentKeys = new ArrayList<>(segmentKeySet);
                    serviceContext.updateConfigs(segmentKeys, migrateBean.getMigrateTarget());

                    final SegmentService segmentService = SwiftContext.get().getBean(SegmentService.class);
                    segmentService.removeSegments(segmentKeys);
                }
            }
        } catch (Exception e) {
            throw new JobExecutionException(e);
        } finally {
            try {
                // 迁移后由节点自身更新数据库配置
                SwiftNodeInfo nodeInfo = nodeInfoService.getNodeInfo(clusterId);
                if (migrateSuccess) {
                    nodeInfo.setMigrateType(MigrateType.SUCCESS);
                    MigInterval migInterval = MigInterval.getMigrateInterval(nodeInfo);
                    migInterval.addOnePeriod();
                    nodeInfo.setBeginIndex(migInterval.getBeginIndex());
                    nodeInfo.setEndIndex(migInterval.getEndIndex());

                    SwiftNodeInfo targetNodeInfo = nodeInfoService.getNodeInfo(nodeInfo.getMigrateTarget());
                    if (targetNodeInfo.getEndIndex().compareTo(migrateIndex) < 0) {
                        targetNodeInfo.setEndIndex(migrateIndex);
                        nodeInfoService.update(targetNodeInfo);
                    }
                } else {
                    nodeInfo.setMigrateType(originMigrateType.levelUp());
                }
                nodeInfo.setBlockingIndex(Strings.EMPTY);
                nodeInfoService.update(nodeInfo);
                // 开启某个月任务分发, 即使失败主节点也会掉线重连后读取配置
                retryer.call(() -> serviceContext.report(NodeEvent.ACTIVATE, NodeMessage.of(clusterId, migrateIndex)));
            } catch (Exception e) {
                SwiftLoggers.getLogger().error(e);
            }
        }
    }

    @Override
    public String getCronExpression() {
        return Strings.EMPTY;
    }

    @Override
    public ScheduleTaskType getExecutorType() {
        return ScheduleTaskType.PART;
    }

    @Override
    public JobKey getJobKey() {
        return JobKey.jobKey(Strings.EMPTY);
    }
}