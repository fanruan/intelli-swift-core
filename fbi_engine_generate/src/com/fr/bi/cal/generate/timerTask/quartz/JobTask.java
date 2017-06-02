package com.fr.bi.cal.generate.timerTask.quartz;

import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.CubeGenerationManager;
import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.base.BICore;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.general.ComparatorUtils;
import com.fr.third.org.quartz.Job;
import com.fr.third.org.quartz.JobDataMap;
import com.fr.third.org.quartz.JobExecutionContext;
import com.fr.third.org.quartz.JobExecutionException;

import java.util.Set;

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
        if (ComparatorUtils.equals(tableKey, DBConstant.CUBE_UPDATE_TYPE.GLOBAL_UPDATE)) {
            CubeGenerationManager.getCubeManager().addCubeGenerateTask2Queue(userId, null, null, true);
        } else {
            if (isTableUsed(userId, tableKey)) {
                CubeGenerationManager.getCubeManager().addCubeGenerateTask2Queue(userId, tableKey, updateType, true);
            } else {
                BILoggerFactory.getLogger(this.getClass()).warn("the table " + tableKey + " is not existed. Timer task canceled");
            }
        }
    }

    private boolean isTableUsed(long userId, String tableKey) {
        Set<BusinessTable> allTables = BICubeConfigureCenter.getPackageManager().getAllTables(userId);
        for (BusinessTable table : allTables) {
            for (BICore biCore : table.getTableSource().createSourceMap().keySet()) {
                if (table.getTableSource().createSourceMap().get(biCore).getSourceID().equals(tableKey)) {
                    return true;
                }
            }
        }
        return false;
    }
}
