package com.fr.bi.cal.generate.timerTask;


import com.fr.bi.cluster.ClusterAdapter;
import com.fr.bi.cluster.utils.ClusterEnv;
import com.fr.bi.conf.manager.update.source.UpdateSettingSource;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.fs.control.UserControl;
import com.fr.third.org.quartz.JobDataMap;
import com.fr.third.org.quartz.JobDetail;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Redesigned by kary on 2015/6/21
 */
public class CubeTimerTaskUtils {

    public static void reGenerateTimeTasks() {
        clear();
        resetTimeTasks();
    }

    private static void clear() {
//        service.removeTimeScheduler(DBConstant.timerTask.cube_task);
        BICubeTimeSchedulerService service = new BICubeTimeSchedulerServiceImpl();
        service.removeAllTimeSchedulers();
    }

    public static void resetTimeTasks() {
        synchronized (CubeTimerTaskUtils.class) {
            BICubeTimeSchedulerService service = new BICubeTimeSchedulerServiceImpl();
            List<JobDetail> scheduleList = getTimerTaskSchedules();
            //        LOGGER.info("***************timer task settings start*****************");
            for (JobDetail detail : scheduleList) {
                JobDataMap jobDataMap = detail.getJobDataMap();
                service.addTimeScheduler(jobDataMap.getString("cronExpression"), jobDataMap.getString("tableKey"), UserControl.getInstance().getSuperManagerID(), jobDataMap.getInt("updateType"), new Date(jobDataMap.getLongValue("startTime")), new Date(jobDataMap.getLongValue("endTime")));
            }
            service.persistData();
//        LOGGER.info("***************timer task settings end:*****************");


        }
    }

    private static List<JobDetail> getTimerTaskSchedules() {
        //子节点不添加定时任务。
        if (ClusterEnv.isCluster() && !ClusterAdapter.getManager().getHostManager().isBuildCube()) {
            return new ArrayList<JobDetail>();
        } else {
            ArrayList<JobDetail> jobDetails = new ArrayList<JobDetail>();
            Map<String, UpdateSettingSource> allTimeTaskMap = BIConfigureManagerCenter.getUpdateFrequencyManager().getUpdateSettings(UserControl.getInstance().getSuperManagerID());
            for (String sourceId : allTimeTaskMap.keySet()) {
                UpdateSettingSource updateSettingSource = allTimeTaskMap.get(sourceId);
                jobDetails.addAll(updateSettingSource.createJobDetails(sourceId));
            }
            return jobDetails;
        }

    }
}
