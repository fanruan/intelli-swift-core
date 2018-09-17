package com.fr.swift.generate;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.MutableBitMap;
import com.fr.swift.bitmap.impl.FasterAggregation;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.segment.operator.column.SwiftFieldPathIndexer;
import com.fr.swift.segment.relation.CubeMultiRelationPath;
import com.fr.swift.segment.relation.RelationIndex;
import com.fr.swift.task.TaskResult.Type;
import com.fr.swift.task.impl.TaskResultImpl;
import com.fr.swift.util.Crasher;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yee
 * @date 2018/1/17
 */
public abstract class BaseFieldPathIndexer extends BaseTablePathIndexer implements SwiftFieldPathIndexer {
    private ColumnKey logicColumnKey;

    public BaseFieldPathIndexer(CubeMultiRelationPath relationPath, ColumnKey logicColumnKey, SwiftSegmentManager provider) {
        super(relationPath, provider);
        this.logicColumnKey = logicColumnKey;
    }

    @Override
    public void work() {
        try {
            buildFieldPath();
            workOver(new TaskResultImpl(Type.SUCCEEDED));
        } catch (Exception e) {
            LOGGER.error("Build field path index error", e);
            workOver(new TaskResultImpl(Type.FAILED, e));
        }

    }

    @Override
    public void buildFieldPath() {
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
        LOGGER.info(String.format("build FieldRelationIndex: %s -> %s finished", logicColumnKey.getName(), relationPath.getKey()));
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
            Column primaryColumn = primary.getColumn(logicColumnKey);
            DictionaryEncodedColumn dicColumn = primaryColumn.getDictionaryEncodedColumn();
            int size = dicColumn.size();
            MutableBitMap[] index = new MutableBitMap[size - 1];
            List<ImmutableBitMap> bitmaps = new ArrayList<ImmutableBitMap>();
            for (int i = 1; i < size; i++) {
                MutableBitMap primaryIndex = (MutableBitMap) primaryColumn.getBitmapIndex().getBitMapIndex(i);
                primaryIndex.and(allShow);
                index[i - 1] = getTableLinkedOrGVI(primaryIndex, targetReader, primarySegIndex);
                bitmaps.add(index[i - 1]);
            }
            writeTargetIndex(targetWriter, index, primarySegIndex);
            targetWriter.putNullIndex(0, FasterAggregation.or(bitmaps).getNot(targetRowCount));
        } finally {
            releaseIfNeed(primary);
        }
    }

    private RelationIndex getTargetReadIndex(Segment targetSegment) {
        return targetSegment.getRelation(relationPath);
    }

    private RelationIndex getTargetWriteIndex(Segment targetSegment) {
        return targetSegment.getRelation(logicColumnKey, relationPath);
    }

    private void writeTargetIndex(RelationIndex targetWriter, ImmutableBitMap[] targetIndex, int primaryIndex) {
        for (int i = 0, len = targetIndex.length; i < len; i++) {
            targetWriter.putIndex(primaryIndex, i + 1, targetIndex[i]);
        }
    }
}