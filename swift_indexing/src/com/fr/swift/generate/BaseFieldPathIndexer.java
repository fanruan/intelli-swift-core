package com.fr.swift.generate;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.impl.BitMapOrHelper;
import com.fr.swift.bitmap.traversal.BreakTraversalAction;
import com.fr.swift.cube.nio.NIOConstant;
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
import com.fr.swift.structure.array.LongArray;
import com.fr.swift.structure.array.LongListFactory;
import com.fr.swift.util.Crasher;

import java.util.List;

/**
 * TODO NullIndex
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
            for (Segment target : targetSegment) {
                int pos = 1;
                RelationIndex targetReader = getTargetReadIndex(target);
                RelationIndex targetWriter = getTargetWriteIndex(target);
                try {
                    for (int i = 0; i < primarySegment.size(); i++) {
                        buildIndexPerSegment(targetReader, targetWriter, primarySegment.get(i), i, pos);
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
        } catch (Exception e) {
            LOGGER.error("Build field path index error", e);
            workOver(Task.Result.FAILED);
        }

    }

    /**
     * TODO NullIndex暂时没想好
     * @param targetReader
     * @param targetWriter
     * @param primary
     * @param primarySegIndex
     * @param pos
     */
    private void buildIndexPerSegment(RelationIndex targetReader, RelationIndex targetWriter, Segment primary, int primarySegIndex, int pos) {
        try {
            List<ColumnKey> columnKeys = logicColumnKey.getKeyFields();
            ImmutableBitMap allShow = primary.getAllShowIndex();
            RelationIndexHelper indexHelper = new RelationIndexHelper();
            for (ColumnKey columnKey : columnKeys) {
                Column primaryColumn = primary.getColumn(columnKey);
                DictionaryEncodedColumn dicColumn = primaryColumn.getDictionaryEncodedColumn();
                int size = dicColumn.size();
                LongArray[] index = new LongArray[size - 1];
//                BitMapOrHelper helper = new BitMapOrHelper();
                for (int i = 1; i < size; i++) {
                    ImmutableBitMap primaryIndex = primaryColumn.getBitmapIndex().getBitMapIndex(i);
                    primaryIndex = primaryIndex.getAnd(allShow);
                    index[i - 1] = buildIndexPerColumn(targetReader, primaryIndex, primarySegIndex, primary.getRowCount());
                }
                indexHelper.addIndex(index);
//                indexHelper.addNullIndex(helper.compute().getNot(targetRowCount));
            }
            writeTargetIndex(targetWriter, indexHelper, pos);
            targetWriter.putNullIndex(0, indexHelper.getNullIndex());
        } finally {
            releaseIfNeed(primary);
        }
    }

    private LongArray buildIndexPerColumn(RelationIndex targetReader, ImmutableBitMap index, final int primarySegIndex, int primaryRowCount) {
        final LongArray primaryArray = LongListFactory.createLongArray(primaryRowCount, NIOConstant.LONG.NULL_VALUE);
        index.breakableTraversal(new BreakTraversalAction() {
            @Override
            public boolean actionPerformed(int row) {
                primaryArray.put(row, RelationIndexHelper.merge2Long(primarySegIndex, row));
                return false;
            }
        });
        LongArray result = getTableLinkedOrGVI(primaryArray, targetReader);
        return result;
    }

    private RelationIndex getTargetReadIndex(Segment targetSegment) {
        return targetSegment.getRelation(relationPath);
    }

    private RelationIndex getTargetWriteIndex(Segment targetSegment) {
        return targetSegment.getRelation(logicColumnKey);
    }

    private void writeTargetIndex(RelationIndex targetWriter, RelationIndexHelper helper, int pos) {
        LongArray[] targetIndex = helper.getIndex();
        for (int i = 0, len = targetIndex.length; i < len; i++, pos++) {
            targetWriter.putIndex(pos, targetIndex[i]);
        }
    }
}