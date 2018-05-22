package com.fr.swift.manager;

import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.generate.history.index.ColumnDictMerger;
import com.fr.swift.generate.history.index.ColumnIndexer;
import com.fr.swift.generate.segment.operator.inserter.BlockInserter;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SwiftDataOperatorProvider;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.operator.Deleter;
import com.fr.swift.segment.operator.Inserter;
import com.fr.swift.segment.operator.column.SwiftColumnDictMerger;
import com.fr.swift.segment.operator.column.SwiftColumnIndexer;
import com.fr.swift.segment.operator.delete.HistorySwiftDeleter;
import com.fr.swift.segment.operator.delete.RealtimeSwiftDeleter;
import com.fr.swift.segment.operator.insert.HistorySwiftInserter;
import com.fr.swift.segment.operator.insert.RealtimeBlockSwiftInserter;
import com.fr.swift.segment.operator.insert.RealtimeSwiftInserter;
import com.fr.swift.source.DataSource;
import com.fr.swift.util.DataSourceUtils;

import java.util.List;

public class LocalDataOperatorProvider implements SwiftDataOperatorProvider {

    private static LocalDataOperatorProvider INSTANCE = new LocalDataOperatorProvider();

    public static LocalDataOperatorProvider getInstance() {
        return INSTANCE;
    }

    private SwiftSegmentManager manager;

    private LocalDataOperatorProvider() {
        manager = new LineSegmentManager();
    }

    public void registerSwiftSegmentManager(SwiftSegmentManager manager) {
        this.manager = manager;
    }

    @Override
    public Inserter getHistorySwiftInserter(Segment segment, DataSource dataSource) throws Exception {
        if (DataSourceUtils.isAddColumn(dataSource)) {
            return new HistorySwiftInserter(segment, DataSourceUtils.getAddFields(dataSource));
        }
        return new HistorySwiftInserter(segment);
    }

    @Override
    public Inserter getRealtimeSwiftInserter(Segment segment, DataSource dataSource) throws Exception {
        if (DataSourceUtils.isAddColumn(dataSource)) {
            return new RealtimeSwiftInserter(segment, DataSourceUtils.getAddFields(dataSource));
        }
        return new RealtimeSwiftInserter(segment);
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
    public Deleter getSwiftDeleter(Segment segment) throws SwiftMetaDataException {
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
}
