package com.fr.swift.schedule.job;

import com.finebi.conf.internalimp.update.TableUpdateInfo;
import com.finebi.conf.structure.bean.pack.FineBusinessPackage;
import com.fr.third.org.quartz.JobDataMap;
import com.fr.third.org.quartz.JobExecutionContext;

/**
 * This class created on 2018/5/14
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class PackageJobTask extends JobTask {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        try {
            JobDataMap data = jobExecutionContext.getJobDetail().getJobDataMap();
            String packageId = (String) data.get("packageId");
            FineBusinessPackage fineBusinessPackage = packageManager.getSinglePackage(packageId);

            TableUpdateInfo tableUpdateInfo = updateInfoConfigService.getPackageUpdateInfo(packageId);
            if (fineBusinessPackage == null || tableUpdateInfo == null) {
                LOGGER.error("BusinessPackage " + packageId + " is not exist or tableUpdateInfo is not exist!!");
            } else {
                updateManager.triggerPackageUpdate(packageId);
            }
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }
}
