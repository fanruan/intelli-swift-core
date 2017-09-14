package com.fr.bi.cal.generate.timerTask;


import com.finebi.cube.common.log.BILogger;
import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.bi.cal.generate.timerTask.quartz.JobTask;
import com.fr.bi.cluster.ClusterAdapter;
import com.fr.bi.cluster.utils.ClusterEnv;
import com.fr.bi.conf.base.scheduler.ScheduleEntity;
import com.fr.bi.conf.manager.update.source.UpdateSettingSource;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.stable.utils.program.BIStringUtils;
import com.fr.fs.control.UserControl;
import com.fr.third.org.quartz.CronExpression;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Redesigned by kary on 2015/6/21
 */
public class CubeTimerTaskHelper {
    private static BILogger LOGGER = BILoggerFactory.getLogger(CubeTimerTaskHelper.class);

    public static void reGenerateTimeTasks() {
        clear();
        resetTimeTasks();
    }

    private static void clear() {
        BICubeTimeSchedulerService service = new BICubeTimeSchedulerServiceImpl();
        service.removeAllTimeSchedulers();
    }

    public static void resetTimeTasks() {
        LOGGER.info("***************timer task settings start*****************");
        List<ScheduleEntity> list = new ArrayList<ScheduleEntity>();
        BICubeTimeSchedulerService service = new BICubeTimeSchedulerServiceImpl();
        for (ScheduleEntity scheduleEntity : getTimerTaskSchedules()) {
            list.add(scheduleEntity);
        }
        service.addTimeSchedulerList(list);
        LOGGER.info("***************timer task settings end:*****************");
    }

    private static List<ScheduleEntity> getTimerTaskSchedules() {
        //子节点不添加定时任务。
        if (ClusterEnv.isCluster() && !ClusterAdapter.getManager().getHostManager().isBuildCube()) {
            return new ArrayList<ScheduleEntity>();
        } else {
            List<ScheduleEntity> entities = new ArrayList<ScheduleEntity>();
            Map<String, UpdateSettingSource> allTimeTaskMap = BIConfigureManagerCenter.getUpdateFrequencyManager().getUpdateSettings(UserControl.getInstance().getSuperManagerID());
            for (String sourceId : allTimeTaskMap.keySet()) {
                UpdateSettingSource updateSettingSource = allTimeTaskMap.get(sourceId);
                entities.addAll(updateSettingSource.createScheduleEntity(sourceId, JobTask.class));
            }
            return entities;
        }
    }
}
