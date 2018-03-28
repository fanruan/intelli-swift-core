package com.fr.swift.generate;

import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.generate.history.index.ColumnIndexer;
import com.fr.swift.generate.history.transport.TableTransporter;
import com.fr.swift.generate.realtime.RealtimeColumnIndexer;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.DataSource;

/**
 * This class created on 2018/3/27
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class TestIndexer {

    public static void realtimeIndex(DataSource dataSource) throws SwiftMetaDataException {
        for (String field : dataSource.getMetadata().getFieldNames()) {
            RealtimeColumnIndexer<?> indexer = new RealtimeColumnIndexer(dataSource, new ColumnKey(field));
            indexer.work();
        }
    }

    public static void historyIndex(DataSource dataSource, TableTransporter transporter) throws SwiftMetaDataException {
        for (String field : transporter.getIndexFieldsList()) {
            ColumnIndexer<?> indexer = new ColumnIndexer(dataSource, new ColumnKey(field));
            indexer.work();
        }
    }
}
