package com.fr.swift.executor.task.job.impl;

import com.fr.swift.SwiftContext;
import com.fr.swift.executor.task.job.Job;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.service.RealtimeService;
import com.fr.swift.source.SourceKey;

/**
 * This class created on 2019/2/19
 *
 * @author Lucifer
 * @description
 */
public class RealtimeInsertJob implements Job<Boolean> {

    private SourceKey tableKey;
    private SwiftResultSet resultSet;

    public RealtimeInsertJob(SourceKey tableKey, SwiftResultSet resultSet) {
        this.tableKey = tableKey;
        this.resultSet = resultSet;
    }

    @Override
    public Boolean call() throws Exception {
        try {
            RealtimeService realtimeService = SwiftContext.get().getBean(RealtimeService.class);
            realtimeService.insert(tableKey, resultSet);
            return true;
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
            return false;
        }
    }
}
