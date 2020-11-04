package com.fr.swift.executor.task.job.schedule;

import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.executor.task.bean.MigrateBean;
import com.fr.swift.executor.task.impl.MigrateExecutorTask;
import com.fr.swift.property.SwiftProperty;
import com.fr.swift.quartz.config.ScheduleTaskType;
import com.fr.swift.quartz.execute.ScheduleJob;
import com.fr.swift.service.ServiceContext;
import com.fr.swift.service.event.NodeEvent;
import com.fr.swift.service.event.NodeMessage;
import com.fr.swift.util.Strings;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;

/**
 * @author Heng.J
 * @date 2020/11/4
 * @description 迁移任务定时类
 * @since swift-1.2.0
 */
public class MigrateScheduleJob implements ScheduleJob {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        ServiceContext serviceContext = ProxySelector.getProxy(ServiceContext.class);
        MigrateBean migrateBean = (MigrateBean) context.getJobDetail().getJobDataMap().get("migrateBean");
        String yearMonth = migrateBean.getYearMonth();
        String machineId = SwiftProperty.get().getMachineId();

        try {
            serviceContext.report(NodeEvent.BLOCK, NodeMessage.of(machineId, yearMonth));
            //H.J TODO : 2020/11/2 改一下 collate 策略, 对month的块合并

            MigrateExecutorTask.of(migrateBean).getJob().call();
        } catch (Exception e) {
            throw new JobExecutionException(e);
        } finally {
            //H.J TODO : 2020/11/3 发消息给主节点更新配置 & 发送失败？
            serviceContext.report(NodeEvent.ACTIVATE, NodeMessage.of(machineId, yearMonth));
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
        return JobKey.jobKey("migrate");
    }
}