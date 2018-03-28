package com.fr.swift.generate.preview;

import com.fr.swift.cube.io.Types;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.generate.Util;
import com.fr.swift.generate.preview.operator.MinorInserter;
import com.fr.swift.generate.realtime.index.RealtimeColumnDictMerger;
import com.fr.swift.generate.realtime.index.RealtimeColumnIndexer;
import com.fr.swift.generate.realtime.index.RealtimeSubDateColumnIndexer;
import com.fr.swift.query.group.GroupType;
import com.fr.swift.segment.RealTimeSegmentImpl;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.impl.SubDateColumn;
import com.fr.swift.segment.operator.Inserter;
import com.fr.swift.source.ColumnTypeConstants.ClassType;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.ETLDataSource;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.utils.DataSourceUtils;

import java.util.Collections;
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
        SwiftResultSet swiftResultSet = SwiftDataPreviewer.createPreviewTransfer(dataSource, 100).createResultSet();

        Segment segment = createSegment(dataSource);
        Inserter inserter = getInserter(dataSource, segment);
        inserter.insertData(swiftResultSet);

        for (
                String indexField : inserter.getFields())

        {
            ColumnKey columnKey = new ColumnKey(indexField);
            indexColumn(dataSource, columnKey);
            indexSubColumnIfNeed(dataSource, columnKey);
        }

    }

    private static void indexColumn(final DataSource dataSource, final ColumnKey indexField) {
        new RealtimeColumnIndexer(dataSource, indexField) {
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

    private static void indexSubColumnIfNeed(final DataSource dataSource, final ColumnKey columnKey) {
        if (Util.getClassType(dataSource, columnKey) != ClassType.DATE) {
            return;
        }
        for (GroupType type : SubDateColumn.TYPES_TO_GENERATE) {
            new RealtimeSubDateColumnIndexer(dataSource, columnKey, type) {
                @Override

                protected List<Segment> getSegments() {
                    return MinorSegmentManager.getInstance().getSegment(dataSource.getSourceKey());
                }
            }.work();
        }
    }

    private static Inserter getInserter(DataSource dataSource, Segment segment) throws Exception {
        if (DataSourceUtils.isAddColumn(dataSource)) {
            return new MinorInserter(segment, DataSourceUtils.getAddFields(dataSource));
        }
        return new MinorInserter(segment);
    }

    private static Segment createSegment(DataSource dataSource) {
        String cubeSourceKey = DataSourceUtils.getSwiftSourceKey(dataSource);
        String path = String.format("/%s/cubes/%s/minor_seg",
                System.getProperty("user.dir"),
                cubeSourceKey);
        Segment seg = new RealTimeSegmentImpl(new ResourceLocation(path, Types.StoreType.MEMORY), dataSource.getMetadata());
        MinorSegmentManager.getInstance().putSegment(dataSource.getSourceKey(), Collections.singletonList(seg));
        return seg;
    }

    private static boolean isEtl(DataSource ds) {
        return ds instanceof ETLDataSource;
    }

    private static GroupType[] SUB_DATE_TYPES = {
            GroupType.YEAR, GroupType.QUARTER, GroupType.MONTH,
            GroupType.WEEK, GroupType.WEEK_OF_YEAR, GroupType.DAY,
            GroupType.HOUR, GroupType.MINUTE, GroupType.SECOND,
            GroupType.Y_M_D_H_M_S, GroupType.Y_M_D_H_M, GroupType.Y_M_D_H,
            GroupType.Y_M_D, GroupType.Y_M, GroupType.Y_Q, GroupType.Y_W, GroupType.Y_D
    };
}