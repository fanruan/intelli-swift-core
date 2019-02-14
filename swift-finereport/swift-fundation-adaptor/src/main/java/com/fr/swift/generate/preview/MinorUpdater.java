package com.fr.swift.generate.preview;

import com.fr.swift.cube.io.ResourceDiscovery;
import com.fr.swift.cube.io.Types;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.generate.history.index.SubDateColumnDictMerger;
import com.fr.swift.generate.history.index.SubDateColumnIndexer;
import com.fr.swift.generate.realtime.index.RealtimeMultiRelationIndexer;
import com.fr.swift.generate.realtime.index.RealtimeTablePathIndexer;
import com.fr.swift.query.group.GroupType;
import com.fr.swift.relation.utils.RelationPathHelper;
import com.fr.swift.segment.RealTimeSegmentImpl;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.impl.SubDateColumn;
import com.fr.swift.segment.operator.Inserter;
import com.fr.swift.segment.operator.insert.SwiftInserter;
import com.fr.swift.source.ColumnTypeConstants.ClassType;
import com.fr.swift.source.ColumnTypeUtils;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.EtlDataSource;
import com.fr.swift.source.RelationSource;
import com.fr.swift.source.RelationSourceType;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SourcePath;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.source.etl.ETLOperator;
import com.fr.swift.source.etl.OperatorType;
import com.fr.swift.source.etl.detail.DetailOperator;
import com.fr.swift.source.relation.RelationPathSourceImpl;
import com.fr.swift.util.DataSourceUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author anchore
 * @date 2018/2/1
 * <p>
 * 基础表每次
 */
public class MinorUpdater {
    /**
     * 预览数据过期时间
     */
    private static Map<SourceKey, Long> segmentsExpireMap = new ConcurrentHashMap<SourceKey, Long>();

    private static long expireTime = 60000L;

    private DataSource dataSource;

    private int previewRowCount = 200;

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
            build(dataSource);
        }
    }

    public void clearData() {
        synchronized (MinorSegmentManager.getInstance()) {
            segmentsExpireMap.clear();
            MinorSegmentManager.getInstance().clear();
            ResourceDiscovery.getInstance().clear();
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
        synchronized (MinorSegmentManager.getInstance()) {
            Long putTime = segmentsExpireMap.get(dataSource.getSourceKey());
            if (putTime != null) {
                long nowTime = System.currentTimeMillis();
                if ((nowTime - putTime) > expireTime) {
                    segmentsExpireMap.remove(dataSource.getSourceKey());
                    MinorSegmentManager.getInstance().remove(dataSource.getSourceKey());
                }
            }

            List<Segment> segmentList = MinorSegmentManager.getInstance().getSegment(dataSource.getSourceKey());
            if (segmentList != null && !segmentList.isEmpty()) {
                return;
            }
            SwiftResultSet swiftResultSet = SwiftDataPreviewer.createPreviewTransfer(dataSource, previewRowCount).createResultSet();

            Segment segment = createSegment(dataSource);
            Inserter inserter = getInserter(dataSource, segment);
            inserter.insertData(swiftResultSet);

            if (!segmentsExpireMap.containsKey(dataSource.getSourceKey())) {
                segmentsExpireMap.put(dataSource.getSourceKey(), System.currentTimeMillis());
            }
            // seg基础数据生成好了才放进去
            MinorSegmentManager.getInstance().putSegment(dataSource.getSourceKey(), Collections.singletonList(segment));
        }
    }

    private void indexSubColumnIfNeed(DataSource dataSource, ColumnKey columnKey, Segment seg) throws Exception {
        SwiftMetaDataColumn columnMeta = dataSource.getMetadata().getColumn(columnKey.getName());
        if (ColumnTypeUtils.getClassType(columnMeta) != ClassType.DATE) {
            return;
        }
        for (GroupType type : SubDateColumn.TYPES_TO_GENERATE) {
            new SubDateColumnIndexer(dataSource, columnKey, type, Collections.singletonList(seg)).buildIndex();
            new SubDateColumnDictMerger(dataSource, columnKey, type, Collections.singletonList(seg)).mergeDict();
        }
    }

    private void indexRelationOnSelectField(EtlDataSource etl) {
        ETLOperator op = etl.getOperator();
        if (op.getOperatorType() != OperatorType.DETAIL) {
            return;
        }
        List<SourceKey> hadBuild = new ArrayList<SourceKey>();
        // 只有选字段才生成关联
        DetailOperator detailOp = (DetailOperator) op;

        for (ColumnKey[] keys : detailOp.getFields()) {
            for (ColumnKey key : keys) {
                RelationSource relation = key.getRelation();

                if (relation != null) {
                    if (!hadBuild.contains(relation.getSourceKey())) {
                        if (relation.getRelationType() == RelationSourceType.RELATION) {
                            buildRelationIndex(relation);
                        } else {
                            RelationPathSourceImpl pathSource = (RelationPathSourceImpl) relation;
                            buildPathRelationIndex(pathSource, hadBuild);
                        }
                        hadBuild.add(relation.getSourceKey());
                    }
                    // 只生成一次
                    break;
                }
            }
        }
        hadBuild.clear();
        hadBuild = null;
    }

    private void buildRelationIndex(RelationSource relation) {
        new RealtimeMultiRelationIndexer(RelationPathHelper.convert2CubeRelation(relation), MinorSegmentManager.getInstance()).buildRelationIndex();
    }

    private void buildPathRelationIndex(SourcePath sourcePath, List<SourceKey> hadBuild) {
        List<RelationSource> relationSources = sourcePath.getRelations();
        for (RelationSource relation : relationSources) {
            if (!hadBuild.contains(relation.getSourceKey())) {
                if (relation.getRelationType() == RelationSourceType.RELATION) {
                    buildRelationIndex(relation);
                } else {
                    buildPathRelationIndex((SourcePath) relation, hadBuild);
                }
                hadBuild.add(relation.getSourceKey());
            }
        }
        new RealtimeTablePathIndexer(RelationPathHelper.convert2CubeRelationPath(sourcePath), MinorSegmentManager.getInstance()).buildTablePath();
    }

    private Inserter getInserter(DataSource dataSource, Segment segment) {
        if (DataSourceUtils.isAddColumn(dataSource)) {
            return new SwiftInserter(segment, DataSourceUtils.getAddFields(dataSource));
        }
        return new SwiftInserter(segment);
    }

    private Segment createSegment(DataSource dataSource) {
        String cubeSourceKey = DataSourceUtils.getSwiftSourceKey(dataSource).getId();
        String path = String.format("%s/%s/seg0", SwiftDatabase.MINOR_CUBE.getDir(), cubeSourceKey);
        return new RealTimeSegmentImpl(new ResourceLocation(path, Types.StoreType.MEMORY), dataSource.getMetadata());
    }

    private boolean isEtl(DataSource ds) {
        return ds instanceof EtlDataSource;
    }
}