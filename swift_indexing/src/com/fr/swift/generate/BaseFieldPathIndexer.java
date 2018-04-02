package com.fr.swift.generate;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.impl.BitMapOrHelper;
import com.fr.swift.cube.task.Task;
import com.fr.swift.generate.history.index.RelationIndexHelper;
import com.fr.swift.relation.CubeLogicColumnKey;
import com.fr.swift.relation.CubeMultiRelationPath;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.segment.relation.RelationIndex;

import java.util.List;

/**
 * @author yee
 * @date 2018/1/17
 */
public abstract class BaseFieldPathIndexer extends BaseTablePathIndexer {
    private CubeLogicColumnKey logicColumnKey;

    public BaseFieldPathIndexer(CubeMultiRelationPath relationPath, CubeLogicColumnKey logicColumnKey, SwiftSegmentManager provider) {
        super(relationPath, provider);
        this.logicColumnKey = logicColumnKey;
    }


    @Override
    public void work() {
        try {
            List<Segment> primarySegment = getPrimaryTableSegments();
            List<Segment> targetSegment = getTargetTableSegments();
            for (Segment primary : primarySegment) {
                for (Segment target : targetSegment) {
                    buildIndexPerSegment(primary, target);
                }
            }
            workOver(Task.Result.SUCCEEDED);
        } catch (Exception e) {
            LOGGER.error("Build field path index error", e);
            workOver(Task.Result.FAILED);
        }
    }

    private void buildIndexPerSegment(Segment primary, Segment segment) {
        RelationIndex targetReader = getTargetReadIndex(segment);
        RelationIndex targetWriter = getTargetWriteIndex(segment);
        try {
            int targetRowCount = segment.getRowCount();
            List<ColumnKey> columnKeys = logicColumnKey.getKeyFields();
            ImmutableBitMap allShow = primary.getAllShowIndex();
            RelationIndexHelper indexHelper = new RelationIndexHelper();
            for (ColumnKey columnKey : columnKeys) {
                Column primaryColumn = primary.getColumn(columnKey);
                DictionaryEncodedColumn dicColumn = primaryColumn.getDictionaryEncodedColumn();
                int size = dicColumn.size();
                ImmutableBitMap[] index = new ImmutableBitMap[size - 1];
                BitMapOrHelper helper = new BitMapOrHelper();
                for (int i = 1; i < size; i++) {
                    ImmutableBitMap primaryIndex = primaryColumn.getBitmapIndex().getBitMapIndex(i);
                    primaryIndex = primaryIndex.getAnd(allShow);
                    index[i - 1] = buildIndexPerColumn(targetReader, helper, primaryIndex);
                }
                indexHelper.addIndex(index);
                indexHelper.addNullIndex(helper.compute().getNot(targetRowCount));
            }
            writeTargetIndex(targetWriter, indexHelper);
            targetWriter.putNullIndex(indexHelper.getNullIndex());
        } finally {
            if (null != targetReader) {
                targetReader.release();
            }

            if (null != targetWriter) {
                targetWriter.release();
            }

            primary.release();
            segment.release();
        }
    }

    private ImmutableBitMap buildIndexPerColumn(RelationIndex targetReader, BitMapOrHelper helper, ImmutableBitMap index) {
        ImmutableBitMap result = getTableLinkedOrGVI(index, targetReader);
        helper.add(result);
        return result;
    }

    private RelationIndex getTargetReadIndex(Segment targetSegment) {
        return targetSegment.getRelation(relationPath);
    }

    private RelationIndex getTargetWriteIndex(Segment targetSegment) {
        return targetSegment.getRelation(logicColumnKey);
    }

    private void writeTargetIndex(RelationIndex targetWriter, RelationIndexHelper helper) {
        ImmutableBitMap[] targetIndex = helper.getIndex();
        for (int i = 0, len = targetIndex.length; i < len; i++) {
            targetWriter.putIndex(i + 1, targetIndex[i]);
        }
    }
}