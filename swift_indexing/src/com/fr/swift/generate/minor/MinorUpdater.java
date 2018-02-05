package com.fr.swift.generate.minor;

import com.fr.swift.generate.realtime.RealtimeColumnIndexer;
import com.fr.swift.segment.ISegmentOperator;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.ETLDataSource;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.source.SwiftSourceTransfer;
import com.fr.swift.source.SwiftSourceTransferFactory;

import java.util.List;

/**
 * @author anchore
 * @date 2018/2/1
 */
public class MinorUpdater {
    public static void update(DataSource dataSource) throws Exception {
        if (isEtl(dataSource)) {
            buildEtl((ETLDataSource) dataSource);
        } else {
            build(dataSource);
        }
    }

    private static void buildEtl(ETLDataSource etl) throws Exception {
        List<DataSource> dataSources = etl.getBasedSources();
        for (DataSource dataSource : dataSources) {
            if (isEtl(dataSource)) {
                buildEtl((ETLDataSource) dataSource);
            } else {
                build(dataSource);
            }
        }
        build(etl);
    }

    private static void build(final DataSource dataSource) throws Exception {
        ISegmentOperator operator = new MinorSegmentOperator(dataSource.getSourceKey(), dataSource.getMetadata(), null);
        SwiftSourceTransfer transfer = SwiftSourceTransferFactory.createSourcePreviewTransfer(dataSource, 100);

        operator.transport(transfer.createResultSet());
        operator.finishTransport();

        for (SwiftMetaDataColumn metaColumn : dataSource.getMetadata()) {
            new RealtimeColumnIndexer(dataSource, new ColumnKey(metaColumn.getName())) {
                @Override
                protected List<Segment> getSegments() {
                    return MinorSegmentManager.getInstance().getSegment(dataSource.getSourceKey());
                }

                @Override
                protected void mergeDict() {
                }
            }.work();
        }

    }

    private static boolean isEtl(DataSource ds) {
        return ds instanceof ETLDataSource;
    }
}