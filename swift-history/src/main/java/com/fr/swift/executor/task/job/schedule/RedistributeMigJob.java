package com.fr.swift.executor.task.job.schedule;

import com.fr.swift.SwiftContext;
import com.fr.swift.config.entity.SwiftNodeInfo;
import com.fr.swift.config.service.SwiftNodeInfoService;
import com.fr.swift.dao.NodeInfoService;
import com.fr.swift.db.MigrateType;
import com.fr.swift.event.SwiftEventDispatcher;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.quartz.config.ScheduleTaskType;
import com.fr.swift.quartz.execute.ScheduleJob;
import com.fr.swift.service.event.NodeEvent;
import com.fr.swift.service.event.NodeMessage;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Heng.J
 * @date 2020/12/1
 * @description
 * @since swift-1.2.0
 */
public class RedistributeMigJob implements ScheduleJob {

    public static final JobKey JOBKEY = JobKey.jobKey("RefreshMigratePlan");

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        SwiftLoggers.getLogger().info("start execute job {}", RedistributeMigJob.class);
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
        refreshNodeId.forEach(clusterId -> SwiftEventDispatcher.asyncFire(NodeEvent.RETRY_DISTRIBUTE, NodeMessage.of(clusterId)));
        SwiftLoggers.getLogger().info("finish execute job {}", RedistributeMigJob.class);
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
