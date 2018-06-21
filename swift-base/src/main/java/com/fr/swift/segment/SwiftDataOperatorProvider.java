package com.fr.swift.segment;

import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.operator.Inserter;
import com.fr.swift.segment.operator.column.SwiftColumnDictMerger;
import com.fr.swift.segment.operator.column.SwiftColumnIndexer;
import com.fr.swift.segment.operator.delete.RowDeleter;
import com.fr.swift.source.DataSource;

import java.util.List;

/**
 * @author yee
 * @date 2018/1/4
 */
public interface SwiftDataOperatorProvider {
    Inserter getInserter(DataSource dataSource, Segment seg);

    Inserter getIncrementer(DataSource dataSource);

    Inserter getRealtimeSwiftInserter(Segment segment, DataSource dataSource) throws Exception;

    @Deprecated
    Inserter getHistoryBlockSwiftInserter(DataSource dataSource);

    @Deprecated
    Inserter getRealtimeBlockSwiftInserter(DataSource dataSource);

    RowDeleter getRowDeleter(Segment segment) throws SwiftMetaDataException;

    SwiftColumnIndexer getColumnIndexer(DataSource ds, ColumnKey columnKey, List<Segment> segments);

    SwiftColumnDictMerger getColumnDictMerger(DataSource ds, ColumnKey columnKey, List<Segment> segments);
}
