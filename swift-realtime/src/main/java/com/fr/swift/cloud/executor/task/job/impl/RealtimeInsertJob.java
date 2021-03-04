package com.fr.swift.cloud.executor.task.job.impl;

import com.fr.swift.cloud.config.entity.SwiftSegmentEntity;
import com.fr.swift.cloud.cube.io.Types;
import com.fr.swift.cloud.db.Table;
import com.fr.swift.cloud.db.impl.SwiftDatabase;
import com.fr.swift.cloud.executor.task.job.BaseJob;
import com.fr.swift.cloud.lock.SegLocks;
import com.fr.swift.cloud.log.SwiftLoggers;
import com.fr.swift.cloud.result.SwiftResultSet;
import com.fr.swift.cloud.segment.Incrementer;
import com.fr.swift.cloud.segment.SegmentKey;
import com.fr.swift.cloud.segment.backup.BackupBlockImporter;
import com.fr.swift.cloud.segment.backup.ReusableResultSet;
import com.fr.swift.cloud.segment.operator.Importer;
import com.fr.swift.cloud.source.SourceKey;
import com.fr.swift.cloud.source.alloter.RowInfo;
import com.fr.swift.cloud.source.alloter.SwiftSourceAlloter;
import com.fr.swift.cloud.source.alloter.impl.BaseAllotRule;
import com.fr.swift.cloud.source.alloter.impl.line.BackupLineSourceAlloter;
import com.fr.swift.cloud.source.alloter.impl.line.LineAllotRule;
import com.fr.swift.cloud.source.alloter.impl.line.RealtimeLineSourceAlloter;

import java.util.List;

/**
 * This class created on 2019/2/19
 *
 * @author Lucifer
 * @description
 */
public class RealtimeInsertJob extends BaseJob<SegmentKey, SwiftResultSet> {

    protected SourceKey tableKey;
    protected SwiftResultSet resultSet;

    public RealtimeInsertJob(SourceKey tableKey, SwiftResultSet resultSet) {
        this.tableKey = tableKey;
        this.resultSet = resultSet;
    }

    @Override
    public SegmentKey call() {
        try {
            // TODO: 2020/11/25 rowcount&allshowindex快照
            //先备份，后insert
            //加表锁，避免同一个表被同时增量写入
            Table table = SwiftDatabase.getInstance().getTable(tableKey);
            SwiftSegmentEntity segKey = new SwiftSegmentEntity(tableKey, 0, Types.StoreType.MEMORY, table.getMeta().getSwiftDatabase());
            synchronized (SegLocks.SEG_LOCK.computeIfAbsent(segKey, s -> s)) {
                ReusableResultSet reusableResultSet = backup(table);
                // 暂定备份出问题就直接pass1去
                return insert(table, reusableResultSet);
            }
        } catch (Exception e) {
            // TODO: 2020/11/18 backup & mem回滚
            SwiftLoggers.getLogger().error(e);
            return null;
        }
    }

    private ReusableResultSet backup(Table table) throws Exception {
        SwiftSourceAlloter alloter = new BackupLineSourceAlloter(tableKey, new LineAllotRule(BaseAllotRule.MEM_CAPACITY));
        ReusableResultSet reusableResultSet = new ReusableResultSet(resultSet);
        Importer importer = new BackupBlockImporter<SwiftSourceAlloter<?, RowInfo>>(table, alloter);
        importer.importResultSet(reusableResultSet);
        return reusableResultSet;
    }

    /**
     * 增量块调整、优化后最多只有一块，默认return get(0)
     * 若为空，则return null;
     *
     * @param table
     * @param reusableResultSet
     * @return
     * @throws Exception
     */
    private SegmentKey insert(Table table, ReusableResultSet reusableResultSet) throws Exception {
        SwiftSourceAlloter alloter = new RealtimeLineSourceAlloter(tableKey, new LineAllotRule(BaseAllotRule.MEM_CAPACITY));
        Importer importer = new Incrementer<SwiftSourceAlloter<?, RowInfo>>(table, alloter);
        importer.importResultSet(reusableResultSet.reuse());
        List<SegmentKey> importSegments = importer.getImportSegments();
        return importSegments.isEmpty() ? null : importSegments.get(0);
    }
}
