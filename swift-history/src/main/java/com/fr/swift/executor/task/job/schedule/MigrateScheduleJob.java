package com.fr.swift.executor.task.job.schedule;

import com.fr.swift.SwiftContext;
import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.config.entity.SwiftNodeInfo;
import com.fr.swift.config.service.SwiftNodeInfoService;
import com.fr.swift.db.MigrateType;
import com.fr.swift.executor.task.ExecutorTask;
import com.fr.swift.executor.task.TaskRouter;
import com.fr.swift.executor.task.bean.MigrateBean;
import com.fr.swift.executor.task.job.trigger.MigrateTriggerJob;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.property.SwiftProperty;
import com.fr.swift.quartz.config.ScheduleTaskType;
import com.fr.swift.quartz.execute.ScheduleJob;
import com.fr.swift.service.ServiceContext;
import com.fr.swift.service.event.NodeEvent;
import com.fr.swift.service.event.NodeMessage;
import com.fr.swift.util.Strings;
import com.github.rholder.retry.RetryException;
import com.github.rholder.retry.Retryer;
import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;

import java.util.Objects;
import java.util.concurrent.ExecutionException;
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
        MigrateType originType = nodeInfoService.getOwnNodeInfo().getMigrateType();
        try {
            // 主节点停止分migIndex任务
            boolean blockSuccess = retryer.call(() -> serviceContext.report(NodeEvent.BLOCK, NodeMessage.of(clusterId, migrateIndex)));
            // 重试失败后检查数据库
            SwiftNodeInfo nodeInfo = nodeInfoService.getNodeInfo(clusterId);
            if (!blockSuccess && nodeInfo.getBlockingIndex().equals(migrateIndex)) {
                blockSuccess = true;
            }
            if (blockSuccess) {
                // 等本机相关任务执行
                clearConflictTasks(migrateIndex);

                // 通知主节点本机准备成功
                nodeInfo.setReadyStatus(1);
                nodeInfoService.update(nodeInfo);
                MigrateTriggerJob.getInstance().init(migrateBean, originType);
                Boolean reportSuccess = retryer.call(() -> serviceContext.report(NodeEvent.START_WAITING, NodeMessage.of(clusterId, Strings.EMPTY)));
                if (reportSuccess) {
                    MigrateTriggerJob.getInstance().getLatch().await();
                    SwiftLoggers.getLogger().info("migrate job finished");
                } else {
                    nodeInfo.setReadyStatus(0);
                    nodeInfoService.update(nodeInfo);
                }
            }
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
        } finally {
            try {
                // 开启某个月任务分发, 即使失败主节点也会掉线重连后读取配置
                retryer.call(() -> serviceContext.report(NodeEvent.ACTIVATE, NodeMessage.of(clusterId, migrateIndex)));
            } catch (Exception e) {
                SwiftLoggers.getLogger().error(e);
            } finally {
                SwiftLoggers.getLogger().info("migrate success cost {} ms", System.currentTimeMillis() - start);
            }
        }
    }

    /**
     * 检查TaskRouter中是否有migIndex相关任务, 依次移动位置
     */
    private void clearConflictTasks(String migrateIndex) throws ExecutionException, RetryException {
        Retryer<Boolean> retryer = RetryerBuilder.<Boolean>newBuilder()
                .retryIfResult(result -> Objects.equals(result, true))
                .withStopStrategy(StopStrategies.neverStop())
                .withWaitStrategy(WaitStrategies.fixedWait(5, TimeUnit.MINUTES)) //H.J TODO : 2020/12/3 时间待确定
                .build();
        retryer.call(() -> {
            int position = 0;
            boolean hasConflict = false;
            for (ExecutorTask executorTask : TaskRouter.getInstance().getTaskView(true).getValue()) {
                if (executorTask.getTaskContent().contains(migrateIndex)) {
                    TaskRouter.getInstance().moveTask(executorTask, position++);
                    hasConflict = true;
                }
            }
            return hasConflict;
        });
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