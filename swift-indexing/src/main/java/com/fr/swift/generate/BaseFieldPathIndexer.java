package com.fr.swift.generate;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.MutableBitMap;
import com.fr.swift.bitmap.impl.BitMapOrHelper;
import com.fr.swift.cube.task.Task;
import com.fr.swift.generate.history.index.RelationIndexHelper;
import com.fr.swift.relation.CubeMultiRelationPath;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.segment.relation.RelationIndex;
import com.fr.swift.util.Crasher;

import java.util.List;

/**
 * @author yee
 * @date 2018/1/17
 */
public abstract class BaseFieldPathIndexer extends BaseTablePathIndexer {
    private ColumnKey logicColumnKey;

    public BaseFieldPathIndexer(CubeMultiRelationPath relationPath, ColumnKey logicColumnKey, SwiftSegmentManager provider) {
        super(relationPath, provider);
        this.logicColumnKey = logicColumnKey;
    }

    @Override
    public void work() {
        try {
            LOGGER.info(String.format("start build FieldRelationIndex: %s -> %s", logicColumnKey.getName(), relationPath.getKey()));
            List<Segment> primarySegment = getPrimaryTableSegments();
            List<Segment> targetSegment = getTargetTableSegments();
            for (Segment target : targetSegment) {
                RelationIndex targetReader = getTargetReadIndex(target);
                RelationIndex targetWriter = getTargetWriteIndex(target);
                try {
                    for (int i = 0; i < primarySegment.size(); i++) {
                        buildIndexPerSegment(targetReader, targetWriter, primarySegment.get(i), i, target.getRowCount());
                    }
                } catch (Exception e) {
                    Crasher.crash(e);
                } finally {
                    releaseIfNeed(targetReader);
                    releaseIfNeed(targetWriter);
                    releaseIfNeed(target);
                }
            }
            workOver(Task.Result.SUCCEEDED);
            LOGGER.info(String.format("build FieldRelationIndex: %s -> %s finished", logicColumnKey.getName(), relationPath.getKey()));
        } catch (Exception e) {
            LOGGER.error("Build field path index error", e);
            workOver(Task.Result.FAILED);
        }

    }

    /**
     * @param targetReader
     * @param targetWriter
     * @param primary
     * @param primarySegIndex
     * @param targetRowCount
     */
    private void buildIndexPerSegment(RelationIndex targetReader, RelationIndex targetWriter, Segment primary, int primarySegIndex, int targetRowCount) {
        try {
            ImmutableBitMap allShow = primary.getAllShowIndex();
            RelationIndexHelper indexHelper = new RelationIndexHelper();
            Column primaryColumn = primary.getColumn(logicColumnKey);
            DictionaryEncodedColumn dicColumn = primaryColumn.getDictionaryEncodedColumn();
            int size = dicColumn.size();
            MutableBitMap[] index = new MutableBitMap[size - 1];
            BitMapOrHelper helper = new BitMapOrHelper();
            for (int i = 1; i < size; i++) {
                MutableBitMap primaryIndex = (MutableBitMap) primaryColumn.getBitmapIndex().getBitMapIndex(i);
                primaryIndex.and(allShow);
                index[i - 1] = buildIndexPerColumn(targetReader, helper, primaryIndex, primarySegIndex);
                indexHelper.addNullIndex(helper.compute().getNot(targetRowCount));
            }
            indexHelper.addIndex(index);
            writeTargetIndex(targetWriter, indexHelper, primarySegIndex);
            targetWriter.putNullIndex(0, indexHelper.getNullIndex());
        } finally {
            releaseIfNeed(primary);
        }
    }

    private MutableBitMap buildIndexPerColumn(RelationIndex targetReader, BitMapOrHelper helper, ImmutableBitMap index, int primarySegIndex) {
        MutableBitMap result = getTableLinkedOrGVI(index, targetReader, primarySegIndex);
        helper.add(result);
        return result;
    }

    private RelationIndex getTargetReadIndex(Segment targetSegment) {
        return targetSegment.getRelation(relationPath);
    }

    private RelationIndex getTargetWriteIndex(Segment targetSegment) {
        return targetSegment.getRelation(logicColumnKey, relationPath);
    }

    private void writeTargetIndex(RelationIndex targetWriter, RelationIndexHelper helper, int primaryIndex) {
        ImmutableBitMap[] targetIndex = helper.getIndex();
        for (int i = 0, len = targetIndex.length; i < len; i++) {
            targetWriter.putIndex(primaryIndex, i + 1, targetIndex[i]);
        }
    }
}