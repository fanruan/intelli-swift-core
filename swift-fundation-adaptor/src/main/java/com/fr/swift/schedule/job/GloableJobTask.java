package com.fr.swift.schedule.job;

import com.finebi.conf.internalimp.update.TableUpdateInfo;
import com.fr.third.org.quartz.JobExecutionContext;
import com.fr.third.org.quartz.JobExecutionException;

/**
 * This class created on 2018/5/14
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class GloableJobTask extends JobTask {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        try {
            updateManager.triggerAllUpdate(new TableUpdateInfo());
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }
}
