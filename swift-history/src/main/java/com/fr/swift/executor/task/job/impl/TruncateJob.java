package com.fr.swift.executor.task.job.impl;

import com.fr.swift.SwiftContext;
import com.fr.swift.executor.task.job.BaseJob;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.service.HistoryService;
import com.fr.swift.service.RealtimeService;
import com.fr.swift.source.SourceKey;

/**
 * @author anchore
 * @date 2019/2/27
 */
public class TruncateJob extends BaseJob<Void, SourceKey> {

    private SourceKey tableKey;

    public TruncateJob(SourceKey tableKey) {
        this.tableKey = tableKey;
    }

    @Override
    public Void call() {
        try {
            SwiftContext.get().getBean(RealtimeService.class).truncate(tableKey);
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
        }

        try {
            SwiftContext.get().getBean(HistoryService.class).truncate(tableKey);
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
        }
        return null;
    }

    @Override
    public SourceKey serializedTag() {
        return tableKey;
    }
}