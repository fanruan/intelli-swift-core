package com.fr.swift.generate;

import com.fr.swift.context.SwiftContext;
import com.fr.swift.generate.history.index.ColumnDictMerger;
import com.fr.swift.generate.history.index.ColumnIndexer;
import com.fr.swift.generate.history.transport.TableTransporter;
import com.fr.swift.manager.LocalSegmentProvider;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.DataSource;

import java.util.ArrayList;
import java.util.List;

/**
 * This class created on 2018/3/27
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class TestIndexer {

    public static void realtimeIndex(DataSource dataSource) {

        List<Segment> allSegments = SwiftContext.get().getBean(LocalSegmentProvider.class).getSegment(dataSource.getSourceKey());
        List<Segment> indexSegments = new ArrayList<Segment>();
        for (Segment segment : allSegments) {
            if (!segment.isHistory()) {
                indexSegments.add(segment);
            }
        }

        for (String field : dataSource.getMetadata().getFieldNames()) {
            ColumnIndexer<?> indexer = new ColumnIndexer(dataSource, new ColumnKey(field), indexSegments);
            indexer.work();
            ColumnDictMerger<?> merger = new ColumnDictMerger(dataSource, new ColumnKey(field), allSegments);
            merger.work();
        }
    }

    public static void historyIndex(DataSource dataSource, TableTransporter transporter) {
        List<Segment> segments = SwiftContext.get().getBean(LocalSegmentProvider.class).getSegment(dataSource.getSourceKey());
        for (String field : transporter.getIndexFieldsList()) {
            ColumnIndexer<?> indexer = new ColumnIndexer(dataSource, new ColumnKey(field), segments);
            indexer.work();
            ColumnDictMerger<?> merger = new ColumnDictMerger(dataSource, new ColumnKey(field), segments);
            merger.work();
        }
    }
}
