package com.fr.bi.cal.generate.timerTask.quartz;

import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.CubeBuildStuff;
import com.finebi.cube.conf.CubeGenerationManager;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.impl.conf.CubeBuildStuffSingleTable;
import com.fr.bi.base.BICore;
import com.fr.bi.base.BIUser;
import com.fr.bi.cal.generate.BuildCubeTask;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.engine.CubeTask;
import com.fr.general.ComparatorUtils;
import com.fr.third.org.quartz.Job;
import com.fr.third.org.quartz.JobDataMap;
import com.fr.third.org.quartz.JobExecutionContext;
import com.fr.third.org.quartz.JobExecutionException;

import java.util.*;

/**
 * Created by kary on 16/6/29.
 */

public class JobTask implements Job {

    public JobTask() {
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobDataMap data = jobExecutionContext.getJobDetail().getJobDataMap();
        long userId = Long.valueOf(data.get("userId").toString());
        String tableKey = data.getString("tableKey");
        int updateType = data.getInt("updateType");
        String message = "timerTask started!Current time is:" + new Date();
        BILoggerFactory.getLogger().info(message);
        BusinessTable businessTable = tableCheck(userId, tableKey);
        if (null == businessTable) {
            BILoggerFactory.getLogger().info("the table " + tableKey + " is not existed. Timer Task canceled");
            return;
        } else {
            List<CubeBuildStuff> cubeTasks = generateCubeTasks(userId, updateType, businessTable);
            startAllTasks(userId, cubeTasks);
        }
    }

    private void startAllTasks(long userId, List<CubeBuildStuff> stuffList) {
        for (CubeBuildStuff cubeBuildStuff : stuffList) {
            if (cubeBuildStuff.preConditionsCheck()) {
                CubeTask task = new BuildCubeTask(new BIUser(userId), cubeBuildStuff);
                CubeGenerationManager.getCubeManager().addTask(task, userId);
            }else {
                String errorMessage = "preConditions check failed! Please check the available HD space and data connections";
                BILoggerFactory.getLogger().error(errorMessage);
            }
        }
    }

    private List<CubeBuildStuff> generateCubeTasks(long userId, int updateType, BusinessTable baseTable) {
        String baseTableKey = baseTable.getTableSource().getSourceID();
        List cubeBuildTasks = new ArrayList<CubeBuildStuff>();
        Set<BusinessTable> hostTables = getHostTables(userId, baseTableKey);
        //如果该表所属的ETL需要更新，那自身就没有必要再更新一遍了
        if (hostTables.size() == 0) {
            CubeBuildStuff cubeBuild = new CubeBuildStuffSingleTable(baseTable, baseTableKey, userId, updateType);
            cubeBuildTasks.add(cubeBuild);
        } else {
            for (BusinessTable hostTable : hostTables) {
                CubeBuildStuff cubeBuild = new CubeBuildStuffSingleTable(hostTable, baseTableKey, userId, updateType);
                cubeBuildTasks.add(cubeBuild);
            }
        }
        return cubeBuildTasks;
    }

    private BusinessTable tableCheck(long userId, String tableKey) {
        Set<BusinessTable> allTables = BICubeConfigureCenter.getPackageManager().getAllTables(userId);
        for (BusinessTable table : allTables) {
            for (BICore biCore : table.getTableSource().createSourceMap().keySet()) {
                if (table.getTableSource().createSourceMap().get(biCore).getSourceID().equals(tableKey)) {
                    return table;
                }
            }
        }
        return null;
    }

    private Set<BusinessTable> getHostTables(long userId, String keys) {
        Set<BusinessTable> hostTables = new HashSet<BusinessTable>();
        for (BusinessTable businessTable : BICubeConfigureCenter.getPackageManager().getAllTables(userId)) {
            Map<BICore, CubeTableSource> sourceMap = businessTable.getTableSource().createSourceMap();
            for (BICore biCore : sourceMap.keySet()) {
                if (ComparatorUtils.equals(sourceMap.get(biCore).getSourceID(), keys) && sourceMap.size() > 1) {
                    hostTables.add(businessTable);
                    break;
                }
            }
        }
        hostTables = filterETLs(hostTables);
        return hostTables;
    }

    /**
     * todo kary
     * 过滤重复的ETL
     * 例：若ETL-A join B存在，则过滤掉ETL-A
     *
     * @param hostTables
     * @return
     */
    private Set<BusinessTable> filterETLs(Set<BusinessTable> hostTables) {
        return hostTables;
    }
}
