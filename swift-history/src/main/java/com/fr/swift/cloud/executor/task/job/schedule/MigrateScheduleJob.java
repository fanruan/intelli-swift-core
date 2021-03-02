package com.fr.swift.cloud.executor.task.job.schedule;

import com.fr.swift.cloud.SwiftContext;
import com.fr.swift.cloud.basics.base.selector.ProxySelector;
import com.fr.swift.cloud.config.entity.SwiftNodeInfo;
import com.fr.swift.cloud.config.service.SwiftNodeInfoService;
import com.fr.swift.cloud.db.MigrateType;
import com.fr.swift.cloud.executor.task.bean.MigrateBean;
import com.fr.swift.cloud.executor.task.job.trigger.MigrateTriggerJob;
import com.fr.swift.cloud.executor.utils.TaskQueueUtils;
import com.fr.swift.cloud.log.SwiftLoggers;
import com.fr.swift.cloud.property.SwiftProperty;
import com.fr.swift.cloud.quartz.config.ScheduleTaskType;
import com.fr.swift.cloud.quartz.execute.ScheduleJob;
import com.fr.swift.cloud.service.ServiceContext;
import com.fr.swift.cloud.service.event.NodeEvent;
import com.fr.swift.cloud.service.event.NodeMessage;
import com.fr.swift.cloud.util.Strings;
import com.github.rholder.retry.Retryer;
import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author Heng.J
 * @date 2020/11/4
 * @description 迁移任务定时准备类, 启停任务分发、本机任务处理
 * @since swift-1.2.0
 */
public class MigrateScheduleJob implements ScheduleJob {

    @Override
    public void execute(JobExecutionContext context) {
        long start = System.currentTimeMillis();
        SwiftLoggers.getLogger().info("start execute job {}", MigrateScheduleJob.class);
        ServiceContext serviceContext = ProxySelector.getProxy(ServiceContext.class);
        SwiftNodeInfoService nodeInfoService = SwiftContext.get().getBean(SwiftNodeInfoService.class);
        MigrateBean migrateBean = (MigrateBean) context.getJobDetail().getJobDataMap().get(MigrateBean.KEY);
        String migrateIndex = migrateBean.getMigrateIndex();
        String clusterId = SwiftProperty.get().getMachineId();
        // 失败尝试3次, 1分钟、3分钟、6分钟各一次
        Retryer<Boolean> retryer = RetryerBuilder.<Boolean>newBuilder()
                .retryIfResult(result -> Objects.equals(result, false))
                .retryIfExceptionOfType(Throwable.class)
                .withStopStrategy(StopStrategies.stopAfterAttempt(3))
                .withWaitStrategy(WaitStrategies.incrementingWait(1, TimeUnit.MINUTES, 1, TimeUnit.MINUTES))
                .build();
        SwiftNodeInfo originNodeInfo = nodeInfoService.getOwnNodeInfo();
        MigrateType originType = originNodeInfo.getMigrateType();
        boolean blockSuccess = false;
        try {
            // 主节点停止分migIndex任务
            blockSuccess = retryer.call(() -> serviceContext.report(NodeEvent.BLOCK, NodeMessage.of(clusterId, migrateIndex)));
            // 重试失败后检查数据库
            if (!blockSuccess && nodeInfoService.getNodeInfo(clusterId).getBlockingIndex().equals(migrateIndex)) {
                blockSuccess = true;
            }
            if (blockSuccess) {
                // 等本机相关任务执行
                TaskQueueUtils.clearConflictTasks(migrateIndex);

                // 通知主节点本机准备成功
                MigrateTriggerJob.getInstance().init(migrateBean, originType);
                Boolean reportSuccess = retryer.call(() -> serviceContext.report(NodeEvent.START_WAITING, NodeMessage.of(clusterId, migrateIndex)));
                // 重试失败后检查数据库
                if (!reportSuccess && nodeInfoService.getNodeInfo(clusterId).getReadyStatus() == 1) {
                    reportSuccess = true;
                }
                if (reportSuccess) {
                    SwiftLoggers.getLogger().info("migrate job await");
                    MigrateTriggerJob.getInstance().getLatch().await();
                    SwiftLoggers.getLogger().info("migrate job finished");
                }
            }
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
        } finally {
            try {
                if (!blockSuccess) {
                    originNodeInfo.setMigrateType(originType.levelUp());
                    nodeInfoService.update(originNodeInfo);
                }
                // 开启某个月任务分发, 即使失败主节点也会掉线重连后读取配置
                retryer.call(() -> serviceContext.report(NodeEvent.ACTIVATE, NodeMessage.of(clusterId, migrateIndex)));
            } catch (Exception e) {
                SwiftLoggers.getLogger().error(e);
            } finally {
                SwiftLoggers.getLogger().info("migrate process finished cost {} ms", System.currentTimeMillis() - start);
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