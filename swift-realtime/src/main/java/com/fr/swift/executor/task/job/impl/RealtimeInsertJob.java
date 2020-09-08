package com.fr.swift.executor.task.job.impl;

import com.fr.swift.SwiftContext;
import com.fr.swift.db.Table;
import com.fr.swift.db.impl.SwiftDatabase;
import com.fr.swift.executor.task.job.BaseJob;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.segment.backup.BackupBlockImporter;
import com.fr.swift.segment.backup.ReusableResultSet;
import com.fr.swift.service.RealtimeService;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.alloter.RowInfo;
import com.fr.swift.source.alloter.SwiftSourceAlloter;
import com.fr.swift.source.alloter.impl.BaseAllotRule;
import com.fr.swift.source.alloter.impl.line.BackupLineSourceAlloter;
import com.fr.swift.source.alloter.impl.line.LineAllotRule;

/**
 * This class created on 2019/2/19
 *
 * @author Lucifer
 * @description
 */
public class RealtimeInsertJob extends BaseJob<Boolean, SwiftResultSet> {

    private SourceKey tableKey;
    private SwiftResultSet resultSet;

    public RealtimeInsertJob(SourceKey tableKey, SwiftResultSet resultSet) {
        this.tableKey = tableKey;
        this.resultSet = resultSet;
    }

    @Override
    public Boolean call() {
        try {
            // 先备份，后insert
            ReusableResultSet reusableResultSet = backup();
            // 暂定备份出问题就直接pass
            insert(reusableResultSet);
            return true;
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
            return false;
        }
    }

    private ReusableResultSet backup() throws Exception {
        SwiftSourceAlloter alloter = new BackupLineSourceAlloter(tableKey, new LineAllotRule(BaseAllotRule.MEM_CAPACITY));
        Table table = SwiftDatabase.getInstance().getTable(tableKey);
        ReusableResultSet reusableResultSet = new ReusableResultSet(resultSet);
        new BackupBlockImporter<SwiftSourceAlloter<?, RowInfo>>(table, alloter).importResultSet(reusableResultSet);

        return reusableResultSet;
    }

    private void insert(ReusableResultSet reusableResultSet) throws Exception {
        RealtimeService realtimeService = SwiftContext.get().getBean(RealtimeService.class);
        realtimeService.insert(tableKey, reusableResultSet.reuse());
    }
}
