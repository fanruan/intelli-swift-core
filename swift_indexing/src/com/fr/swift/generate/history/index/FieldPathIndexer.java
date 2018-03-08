package com.fr.swift.generate.history.index;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.impl.BitMapOrHelper;
import com.fr.swift.cube.task.Task;
import com.fr.swift.relation.CubeLogicColumnKey;
import com.fr.swift.relation.CubeMultiRelationPath;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.relation.RelationIndex;

import java.util.List;

/**
 * @author yee
 * @date 2018/1/17
 */
public class FieldPathIndexer extends TablePathIndexer {

    private CubeLogicColumnKey logicColumnKey;

    public FieldPathIndexer(CubeMultiRelationPath relationPath, CubeLogicColumnKey logicColumnKey, SwiftSegmentManager provider) {
        super(relationPath, provider);
        this.logicColumnKey = logicColumnKey;
    }


    @Override
    public void work() {
        try {
            List<Segment> primarySegment = getPrimaryTableSegments();
            List<Segment> targetSegment = getTargetTableSegments();
            for (int i = 0, primaryLen = primarySegment.size(); i < primaryLen; i++) {
                Segment primary = primarySegment.get(i);
                for (int j = 0, targetLen = targetSegment.size(); j < targetLen; j++) {
                    buildIndexPerSegment(primary, targetSegment.get(j));
                }
            }
            workOver(Task.Result.SUCCEEDED);
        } catch (Exception e) {
            logger.error("Build field path index error", e);
            workOver(Task.Result.FAILED);
        }
    }

    private void buildIndexPerSegment(Segment primary, Segment segment) {
        RelationIndex targetReader = getTargetReadIndex(segment);
        RelationIndex targetWriter = getTargetWriteIndex(segment);
        try {
            int rowCount = primary.getRowCount();
            int targetRowCount = segment.getRowCount();
            List<ColumnKey> columnKeys = logicColumnKey.getKeyFields();
            ImmutableBitMap allShow = primary.getAllShowIndex();
            RelationIndexHelper indexHelper = new RelationIndexHelper();
            for (int j = 0, columnLen = columnKeys.size(); j < columnLen; j++) {
                Column primaryColumn = primary.getColumn(columnKeys.get(j));
                ImmutableBitMap[] index = new ImmutableBitMap[rowCount];
                BitMapOrHelper helper = new BitMapOrHelper();
                for (int i = 0; i < rowCount; i++) {
                    ImmutableBitMap primaryIndex = primaryColumn.getBitmapIndex().getBitMapIndex(i);
                    primaryIndex = primaryIndex.getAnd(allShow);
                    index[i] = buildIndexPerColumn(targetReader, helper, primaryIndex);
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

    public void writeTargetIndex(RelationIndex targetWriter, RelationIndexHelper helper) {
        ImmutableBitMap[] targetIndex = helper.getIndex();
        for (int i = 0, len = targetIndex.length; i < len; i++) {
            targetWriter.putIndex(i, targetIndex[i]);
        }
    }
}
