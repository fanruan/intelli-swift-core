package com.fr.swift.listener;

import com.fr.swift.SwiftContext;
import com.fr.swift.config.entity.SwiftNodeInfo;
import com.fr.swift.config.service.SwiftNodeInfoService;
import com.fr.swift.dao.NodeInfoService;
import com.fr.swift.db.MigrateType;
import com.fr.swift.event.SwiftEventDispatcher;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.quartz.config.ScheduleTaskType;
import com.fr.swift.quartz.entity.TaskDefine;
import com.fr.swift.quartz.execute.ScheduleJob;
import com.fr.swift.quartz.service.ScheduleTaskService;
import com.fr.swift.service.event.NodeEvent;
import com.fr.swift.trigger.SwiftPriorityInitTrigger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Heng.J
 * @date 2020/11/13
 * @description
 * @since swift-1.2.0
 */
public class RefreshMigrateTaskTrigger implements SwiftPriorityInitTrigger {

    private static final JobKey JOBKEY = JobKey.jobKey("refresh");

    private ScheduleTaskService scheduleTaskService;

    @Override
    public void init() throws Exception {
        SwiftLoggers.getLogger().info("starting schedule refresh migrate task...");
        scheduleTaskService = SwiftContext.get().getBean(ScheduleTaskService.class);
        RedistributeJob job = new RedistributeJob();
        TaskDefine task = TaskDefine.builder()
                .jobKey(job.getJobKey())
                .cronExpression(job.getCronExpression())
                .jobClass(job.getClass())
                .build();
        scheduleTaskService.addOrUpdateJob(task);
    }

    @Override
    public void destroy() throws Exception {
        SwiftLoggers.getLogger().info("stopping schedule refresh migrate task...");
        scheduleTaskService.deleteJob(JOBKEY);
    }

    @Override
    public int priority() {
        return Priority.LOWER.priority();
    }

    private class RedistributeJob implements ScheduleJob {

        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            SwiftNodeInfoService swiftNodeInfoService = SwiftContext.get().getBean(SwiftNodeInfoService.class);
            List<SwiftNodeInfo> nodeInfoList = swiftNodeInfoService.getAllNodeInfo();
            Set<String> refreshNodeId = new HashSet<>();
            for (SwiftNodeInfo nodeInfo : nodeInfoList) {
                if (!nodeInfo.getMigrateType().equals(MigrateType.WAITING) && !nodeInfo.getMigrateType().equals(MigrateType.BACKUP)) {
                    nodeInfo.setMigrateType(MigrateType.WAITING);
                    swiftNodeInfoService.update(nodeInfo);
                    refreshNodeId.add(nodeInfo.getNodeId());
                }
            }
            SwiftContext.get().getBean(NodeInfoService.class).flushCache();
            refreshNodeId.forEach(clusterId -> SwiftEventDispatcher.asyncFire(NodeEvent.RETRY_DISTRIBUTE, clusterId));
        }

        @Override
        public String getCronExpression() {
            return "0 0 0 1 * ? *";
        }

        @Override
        public ScheduleTaskType getExecutorType() {
            return ScheduleTaskType.PART;
        }

        @Override
        public JobKey getJobKey() {
            return JOBKEY;
        }
    }
}
