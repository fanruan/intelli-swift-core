package com.fr.swift.manager;

import com.fr.swift.generate.history.index.ColumnDictMerger;
import com.fr.swift.generate.history.index.ColumnIndexer;
import com.fr.swift.generate.segment.operator.inserter.BlockInserter;
import com.fr.swift.segment.Incrementer;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SwiftDataOperatorProvider;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.operator.Inserter;
import com.fr.swift.segment.operator.column.SwiftColumnDictMerger;
import com.fr.swift.segment.operator.column.SwiftColumnIndexer;
import com.fr.swift.segment.operator.delete.HistorySwiftDeleter;
import com.fr.swift.segment.operator.delete.RealtimeSwiftDeleter;
import com.fr.swift.segment.operator.delete.WhereDeleter;
import com.fr.swift.segment.operator.insert.RealtimeBlockSwiftInserter;
import com.fr.swift.segment.operator.insert.SwiftInserter;
import com.fr.swift.segment.operator.insert.SwiftRealtimeInserter;
import com.fr.swift.source.DataSource;
import com.fr.swift.util.DataSourceUtils;
import com.fr.third.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocalDataOperatorProvider implements SwiftDataOperatorProvider {

    @Override
    public Inserter getInserter(DataSource dataSource, Segment seg) {
        if (DataSourceUtils.isAddColumn(dataSource)) {
            return new SwiftInserter(seg, DataSourceUtils.getAddFields(dataSource));
        }
        return new SwiftInserter(seg);
    }

    @Override
    public Inserter getIncrementer(DataSource dataSource) {
        return new Incrementer(dataSource);
    }

    @Override
    public Inserter getRealtimeSwiftInserter(Segment segment, DataSource dataSource) {
        if (DataSourceUtils.isAddColumn(dataSource)) {
            return new SwiftRealtimeInserter(segment, DataSourceUtils.getAddFields(dataSource));
        }
        return new SwiftRealtimeInserter(segment);
    }

    @Override
    public Inserter getHistoryBlockSwiftInserter(DataSource dataSource) {
        if (DataSourceUtils.isAddColumn(dataSource)) {
            return new BlockInserter(dataSource.getSourceKey(), DataSourceUtils.getSwiftSourceKey(dataSource).getId(),
                    dataSource.getMetadata(), DataSourceUtils.getAddFields(dataSource));
        }
        return new BlockInserter(dataSource.getSourceKey(), DataSourceUtils.getSwiftSourceKey(dataSource).getId(),
                dataSource.getMetadata());
    }

    @Override
    public Inserter getRealtimeBlockSwiftInserter(DataSource dataSource) {
        return new RealtimeBlockSwiftInserter(new LineSegmentManager().getSegment(dataSource.getSourceKey()),
                dataSource.getSourceKey(), DataSourceUtils.getSwiftSourceKey(dataSource).getId(),
                dataSource.getMetadata());
    }

    @Override
    public WhereDeleter getRowDeleter(Segment segment) {
        if (segment.isHistory()) {
            return new HistorySwiftDeleter(segment);
        } else {
            return new RealtimeSwiftDeleter(segment);
        }
    }

    @Override
    public SwiftColumnIndexer getColumnIndexer(DataSource ds, ColumnKey columnKey, List<Segment> segments) {
        return new ColumnIndexer(ds, columnKey, segments);
    }

    @Override
    public SwiftColumnDictMerger getColumnDictMerger(DataSource ds, ColumnKey columnKey, List<Segment> segments) {
        return new ColumnDictMerger(ds, columnKey, segments);
    }

    private LocalDataOperatorProvider() {
    }
}
