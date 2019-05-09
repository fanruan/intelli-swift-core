package com.fr.swift.executor.task.job.impl;

import com.fr.swift.executor.task.job.BaseJob;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.segment.insert.HistoryBlockImporter;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.alloter.RowInfo;
import com.fr.swift.source.alloter.SwiftSourceAlloter;

/**
 * This class created on 2019/2/19
 *
 * @author Lucifer
 * @description
 */
public class HistoryImportJob extends BaseJob<Boolean, DataSource> {

    private DataSource dataSource;
    private SwiftSourceAlloter<?, RowInfo> alloter;
    private SwiftResultSet swiftResultSet;

    public HistoryImportJob(DataSource dataSource, SwiftSourceAlloter<?, RowInfo> alloter, SwiftResultSet swiftResultSet) {
        this.dataSource = dataSource;
        this.alloter = alloter;
        this.swiftResultSet = swiftResultSet;
    }

    @Override
    public Boolean call() {
        try {
            HistoryBlockImporter<?> importer = new HistoryBlockImporter<SwiftSourceAlloter<?, RowInfo>>(dataSource, alloter);
            importer.importData(swiftResultSet);
            return true;
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
            return false;
        }
    }
}
