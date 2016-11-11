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
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.stable.data.db.PersistentTable;
import com.fr.bi.stable.data.source.CubeTableSource;
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
            BILoggerFactory.getLogger().info("the table " + tableKey + " is not existed.");
            return;
        } else {
            List<CubeBuildStuff> stuffList = getCubeBuildStuffs(userId, tableKey, updateType, businessTable);
            generateCubeTasks(userId, stuffList);
        }
    }

    private void generateCubeTasks(long userId, List<CubeBuildStuff> stuffList) {
        for (CubeBuildStuff cubeBuildStuff : stuffList) {
            if (!cubeBuildStuff.preConditionsCheck()) {
                String errorMessage = "preConditions check failed! Please check the available HD space and data connections";
                BILoggerFactory.getLogger().error(errorMessage);
                BIConfigureManagerCenter.getLogManager().errorTable(new PersistentTable("", "", ""), errorMessage, userId);
                BIConfigureManagerCenter.getLogManager().logEnd(userId);
                continue;
            }
            CubeGenerationManager.getCubeManager().addTask(new BuildCubeTask(new BIUser(userId), cubeBuildStuff), userId);
        }
    }

    private List<CubeBuildStuff> getCubeBuildStuffs(long userId, String baseTableKey, int updateType, BusinessTable hostTable) {
        List<CubeBuildStuff> stuffList = getETLCubeBuild(userId, baseTableKey, updateType, hostTable);
        if (stuffList.size() == 0) {
            stuffList.add(getBaseTableCubeBuild(userId, updateType, hostTable));
        }
        return stuffList;
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

    private List<CubeBuildStuff> getETLCubeBuild(long userId, String keys, int updateType, BusinessTable table) {
        List<CubeBuildStuff> etlCubeBuildList = new ArrayList<CubeBuildStuff>();
        for (BusinessTable businessTable : BICubeConfigureCenter.getPackageManager().getAllTables(userId)) {
            Map<BICore, CubeTableSource> sourceMap = businessTable.getTableSource().createSourceMap();
            for (BICore biCore : sourceMap.keySet()) {
                if (ComparatorUtils.equals(sourceMap.get(biCore).getSourceID(), keys) && sourceMap.size() > 1) {
                    CubeBuildStuff EtlCubeBuild = new CubeBuildStuffSingleTable(businessTable, table.getTableSource().getSourceID(), userId, updateType);
                    etlCubeBuildList.add(EtlCubeBuild);
                    break;
                }
            }
        }
        return etlCubeBuildList;
    }

    private CubeBuildStuff getBaseTableCubeBuild(long userId, int updateType, BusinessTable table) {
        CubeBuildStuff cubeBuild = new CubeBuildStuffSingleTable(table, table.getTableSource().getSourceID(), userId, updateType);
        return cubeBuild;
    }
}
