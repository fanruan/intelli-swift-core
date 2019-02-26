package com.fr.swift.executor.task.job.impl;

import com.fr.swift.db.Where;
import com.fr.swift.executor.task.job.Job;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.source.SourceKey;

/**
 * This class created on 2019/2/19
 *
 * @author Lucifer
 * @description
 */
public class DeleteJob implements Job<Boolean> {

    private SourceKey sourceKey;
    private Where where;

    public DeleteJob(SourceKey sourceKey, Where where) {
        this.sourceKey = sourceKey;
        this.where = where;
    }

    @Override
    public Boolean call() throws Exception {
        try {
//            HistoryService historyService = SwiftContext.get().getBean(HistoryService.class);
//            RealtimeService realtimeService = SwiftContext.get().getBean(RealtimeService.class);
//            historyService.delete(sourceKey, where, null);
//            realtimeService.delete(sourceKey, where, null);
            return true;
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
            return false;
        }
    }
}
