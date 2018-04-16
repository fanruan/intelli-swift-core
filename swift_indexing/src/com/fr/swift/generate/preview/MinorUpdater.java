package com.fr.swift.generate.preview;

import com.fr.swift.cube.io.Types;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.generate.preview.operator.MinorColumnIndexer;
import com.fr.swift.generate.preview.operator.MinorInserter;
import com.fr.swift.generate.preview.operator.MinorSubDateColumnIndexer;
import com.fr.swift.generate.realtime.index.RealtimeMultiRelationIndexer;
import com.fr.swift.generate.realtime.index.RealtimeTablePathIndexer;
import com.fr.swift.query.group.GroupType;
import com.fr.swift.relation.utils.RelationPathHelper;
import com.fr.swift.segment.RealTimeSegmentImpl;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.impl.SubDateColumn;
import com.fr.swift.segment.operator.Inserter;
import com.fr.swift.source.ColumnTypeConstants.ClassType;
import com.fr.swift.source.ColumnTypeUtils;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.EtlDataSource;
import com.fr.swift.source.RelationSource;
import com.fr.swift.source.RelationSourceType;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.source.etl.ETLOperator;
import com.fr.swift.source.etl.OperatorType;
import com.fr.swift.source.etl.detail.DetailOperator;
import com.fr.swift.util.DataSourceUtils;

import java.util.Collections;
import java.util.List;

/**
 * @author anchore
 * @date 2018/2/1
 * <p>
 * 基础表每次
 */
public class MinorUpdater {
    private DataSource dataSource;

    private int previewRowCount = 100;

    public MinorUpdater(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * 是基础表的话，就清除数据，重新update
     * 否则，取存在的数据update。
     *
     * @throws Exception 异常
     */
    public void update() throws Exception {
        if (isEtl(dataSource)) {
            buildEtl((EtlDataSource) dataSource);
        } else {
            MinorSegmentManager.getInstance().remove(dataSource.getSourceKey());
            build(dataSource);
        }
    }

    private void buildEtl(EtlDataSource etl) throws Exception {
        List<DataSource> dataSources = etl.getBasedSources();
        for (DataSource dataSource : dataSources) {
            if (isEtl(dataSource)) {
                buildEtl((EtlDataSource) dataSource);
            } else {
                build(dataSource);
            }
        }

        indexRelationOnSelectField(etl);

        build(etl);
    }

    private void build(final DataSource dataSource) throws Exception {
        List<Segment> segmentList = MinorSegmentManager.getInstance().getSegment(dataSource.getSourceKey());
        if (segmentList != null && !segmentList.isEmpty()) {
            return;
        }
        SwiftResultSet swiftResultSet = SwiftDataPreviewer.createPreviewTransfer(dataSource, previewRowCount).createResultSet();

        Segment segment = createSegment(dataSource);
        Inserter inserter = getInserter(dataSource, segment);
        inserter.insertData(swiftResultSet);

        for (String indexField : inserter.getFields()) {
            ColumnKey columnKey = new ColumnKey(indexField);
            new MinorColumnIndexer(dataSource, columnKey, segment).work();
            indexSubColumnIfNeed(dataSource, columnKey, segment);
        }

        // seg基础数据生成好了才放进去
        MinorSegmentManager.getInstance().putSegment(dataSource.getSourceKey(), Collections.singletonList(segment));
    }

    private void indexSubColumnIfNeed(DataSource dataSource, ColumnKey columnKey, Segment seg) throws SwiftMetaDataException {
        SwiftMetaDataColumn columnMeta = dataSource.getMetadata().getColumn(columnKey.getName());
        if (ColumnTypeUtils.getClassType(columnMeta) != ClassType.DATE) {
            return;
        }
        for (GroupType type : SubDateColumn.TYPES_TO_GENERATE) {
            new MinorSubDateColumnIndexer(dataSource, columnKey, type, seg).work();
        }
    }

    private void indexRelationOnSelectField(EtlDataSource etl) {
        ETLOperator op = etl.getOperator();
        if (op.getOperatorType() != OperatorType.DETAIL) {
            return;
        }
        // 只有选字段才生成关联
        DetailOperator detailOp = (DetailOperator) op;

        for (ColumnKey[] keys : detailOp.getFields()) {
            for (ColumnKey key : keys) {
                RelationSource relation = key.getRelation();
                if (relation != null) {
                    if (relation.getRelationType() == RelationSourceType.RELATION) {
                        new RealtimeMultiRelationIndexer(RelationPathHelper.convert2CubeRelation(relation), MinorSegmentManager.getInstance()).work();
                    } else {
                        new RealtimeTablePathIndexer(RelationPathHelper.convert2CubeRelationPath(relation), MinorSegmentManager.getInstance()).work();
                    }
                    // 只生成一次
                    break;
                }
            }
        }
    }

    private Inserter getInserter(DataSource dataSource, Segment segment) throws Exception {
        if (DataSourceUtils.isAddColumn(dataSource)) {
            return new MinorInserter(segment, DataSourceUtils.getAddFields(dataSource));
        }
        return new MinorInserter(segment);
    }

    private Segment createSegment(DataSource dataSource) {
        String cubeSourceKey = DataSourceUtils.getSwiftSourceKey(dataSource).getId();
        String path = String.format("/%s/cubes/%s/minor_seg",
                System.getProperty("user.dir"),
                cubeSourceKey);
        return new RealTimeSegmentImpl(new ResourceLocation(path, Types.StoreType.MEMORY), dataSource.getMetadata());
    }

    private boolean isEtl(DataSource ds) {
        return ds instanceof EtlDataSource;
    }
}