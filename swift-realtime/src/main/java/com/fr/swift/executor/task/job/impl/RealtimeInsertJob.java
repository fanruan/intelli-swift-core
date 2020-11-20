package com.fr.swift.executor.task.job.impl;

import com.fr.swift.db.Table;
import com.fr.swift.db.impl.SwiftDatabase;
import com.fr.swift.executor.task.job.BaseJob;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.segment.Incrementer;
import com.fr.swift.segment.backup.BackupBlockImporter;
import com.fr.swift.segment.backup.ReusableResultSet;
import com.fr.swift.segment.operator.Importer;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.alloter.RowInfo;
import com.fr.swift.source.alloter.SwiftSourceAlloter;
import com.fr.swift.source.alloter.impl.BaseAllotRule;
import com.fr.swift.source.alloter.impl.line.BackupLineSourceAlloter;
import com.fr.swift.source.alloter.impl.line.LineAllotRule;
import com.fr.swift.source.alloter.impl.line.RealtimeLineSourceAlloter;

/**
 * This class created on 2019/2/19
 *
 * @author Lucifer
 * @description
 */
public class RealtimeInsertJob extends BaseJob<Boolean, SwiftResultSet> {

    protected SourceKey tableKey;
    protected SwiftResultSet resultSet;

    public RealtimeInsertJob(SourceKey tableKey, SwiftResultSet resultSet) {
        this.tableKey = tableKey;
        this.resultSet = resultSet;
    }

    @Override
    public Boolean call() {
        try {
            //todo rowcount&allshowindex快照
            // 先备份，后insert
            Table table = SwiftDatabase.getInstance().getTable(tableKey);
            ReusableResultSet reusableResultSet = backup(table);
            // 暂定备份出问题就直接pass1去
            insert(table, reusableResultSet);
            return true;
        } catch (Exception e) {
            // TODO: 2020/11/18 backup & mem回滚
            SwiftLoggers.getLogger().error(e);
            return false;
        }
    }

    private ReusableResultSet backup(Table table) throws Exception {
        SwiftSourceAlloter alloter = new BackupLineSourceAlloter(tableKey, new LineAllotRule(BaseAllotRule.MEM_CAPACITY));
        ReusableResultSet reusableResultSet = new ReusableResultSet(resultSet);
        Importer importer = new BackupBlockImporter<SwiftSourceAlloter<?, RowInfo>>(table, alloter);
        importer.importResultSet(reusableResultSet);
        return reusableResultSet;
    }

    private void insert(Table table, ReusableResultSet reusableResultSet) throws Exception {
        SwiftSourceAlloter alloter = new RealtimeLineSourceAlloter(tableKey, new LineAllotRule(BaseAllotRule.MEM_CAPACITY));
        Importer importer = new Incrementer<SwiftSourceAlloter<?, RowInfo>>(table, alloter);
        importer.importResultSet(reusableResultSet.reuse());
    }
}
