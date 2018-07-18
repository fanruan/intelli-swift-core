package com.fr.swift.segment;

import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.operator.Inserter;
import com.fr.swift.segment.operator.column.SwiftColumnDictMerger;
import com.fr.swift.segment.operator.column.SwiftColumnIndexer;
import com.fr.swift.source.DataSource;

import java.util.List;

/**
 * @author yee
 * @date 2018/1/4
 */
public interface SwiftDataOperatorProvider {
    Inserter getInserter(DataSource dataSource, Segment seg);

    @Deprecated
    Inserter getHistoryBlockSwiftInserter(DataSource dataSource);

    @Deprecated
    Inserter getRealtimeBlockSwiftInserter(DataSource dataSource);

    SwiftColumnIndexer getColumnIndexer(DataSource ds, ColumnKey columnKey, List<Segment> segments);

    SwiftColumnDictMerger getColumnDictMerger(DataSource ds, ColumnKey columnKey, List<Segment> segments);
}
