package com.fr.swift.generate.preview;

import com.fr.swift.generate.realtime.RealtimeColumnDictMerger;
import com.fr.swift.generate.realtime.RealtimeColumnIndexer;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentOperator;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.ETLDataSource;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.utils.DataSourceUtils;

import java.util.List;

/**
 * @author anchore
 * @date 2018/2/1
 * <p>
 * todo 每次update都是重新存的数据，后期这块应该能优化下
 */
public class MinorUpdater {
    public static void update(DataSource dataSource) throws Exception {
        // 更新前，把之前的segment清除
        MinorSegmentManager.getInstance().clear();

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
        SwiftResultSet swiftResultSet = SwiftDataPreviewer.createPreviewTransfer(dataSource, 10000).createResultSet();

        SegmentOperator operator = getSegmentOperator(dataSource, swiftResultSet);
        operator.transport();
        operator.finishTransport();

        for (String indexField : operator.getIndexFields()) {
            new RealtimeColumnIndexer(dataSource, new ColumnKey(indexField)) {
                @Override
                protected List<Segment> getSegments() {
                    return MinorSegmentManager.getInstance().getSegment(dataSource.getSourceKey());
                }

                @Override
                protected void mergeDict() {
                    new RealtimeColumnDictMerger(dataSource, key) {
                        @Override
                        protected List<Segment> getSegments() {
                            return MinorSegmentManager.getInstance().getSegment(dataSource.getSourceKey());
                        }
                    }.work();
                }
            }.work();
        }

    }

    private static SegmentOperator getSegmentOperator(DataSource dataSource, SwiftResultSet swiftResultSet) throws Exception {

        if (DataSourceUtils.isAddColumn(dataSource)) {
            return new MinorFieldsSegmentOperator(dataSource.getSourceKey(),
                    null, DataSourceUtils.getSwiftSourceKey(dataSource),
                    swiftResultSet, DataSourceUtils.getAddFields(dataSource));
        }
        return new MinorSegmentOperator(dataSource.getSourceKey(),
                null, DataSourceUtils.getSwiftSourceKey(dataSource), swiftResultSet);
    }

    private static boolean isEtl(DataSource ds) {
        return ds instanceof ETLDataSource;
    }
}