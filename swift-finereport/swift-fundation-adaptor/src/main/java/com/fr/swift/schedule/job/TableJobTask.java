package com.fr.swift.schedule.job;

import com.finebi.conf.internalimp.update.TableUpdateInfo;
import com.finebi.conf.structure.bean.table.FineBusinessTable;
import com.fr.swift.constants.UpdateConstants;
import com.fr.third.org.quartz.JobDataMap;
import com.fr.third.org.quartz.JobExecutionContext;

/**
 * This class created on 2018/5/14
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class TableJobTask extends JobTask {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        try {
            JobDataMap data = jobExecutionContext.getJobDetail().getJobDataMap();
            String tableName = (String) data.get("tableName");
            FineBusinessTable fineBusinessTable = tableManager.getSingleTable(tableName);
            int updateType = Integer.valueOf((String) data.get("updateType"));

            TableUpdateInfo tableUpdateInfo = updateInfoConfigService.getTableUpdateInfo(tableName);

            if (updateType != UpdateConstants.TableUpdateType.INCREMENT) {
                tableUpdateInfo = new TableUpdateInfo();
                tableUpdateInfo.setUpdateType(UpdateConstants.TableUpdateType.ALL);
            }
            if (fineBusinessTable == null || tableUpdateInfo == null) {
                LOGGER.error("BusinessTable " + tableName + " is not exist or tableUpdateInfo is not exist!!");
            } else {
                updateManager.triggerTableUpdate(tableUpdateInfo, fineBusinessTable);
            }
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }
}
