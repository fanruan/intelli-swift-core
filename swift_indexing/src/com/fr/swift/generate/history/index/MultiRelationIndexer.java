package com.fr.swift.generate.history.index;

import com.fr.swift.bitmap.BitMaps;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.traversal.BreakTraversalAction;
import com.fr.swift.cube.nio.NIOConstant;
import com.fr.swift.cube.task.Task;
import com.fr.swift.cube.task.impl.BaseWorker;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.relation.CubeLogicColumnKey;
import com.fr.swift.relation.CubeMultiRelation;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.segment.column.BitmapIndexedColumn;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.segment.relation.RelationIndex;
import com.fr.swift.structure.array.IntArray;
import com.fr.swift.structure.array.IntListFactory;

import java.util.Comparator;
import java.util.List;

/**
 * @author yee
 * @date 2018/1/29
 */
public class MultiRelationIndexer extends BaseWorker {

    private CubeMultiRelation relation;
    private SwiftSegmentManager provider;
    private SwiftLogger logger = SwiftLoggers.getLogger(MultiRelationIndexer.class);

    public MultiRelationIndexer(CubeMultiRelation relation, SwiftSegmentManager provider) {
        this.relation = relation;
        this.provider = provider;
    }


    @Override
    public void work() {
        buildRelationIndex();
    }

    private void buildRelationIndex() {
        logger.info("start generate relation: " + relation.getKey());
        List<Segment> primarySegments = provider.getSegment(relation.getPrimaryTable());
        List<Segment> foreignSegments = provider.getSegment(relation.getForeignTable());

        try {
            for (int i = 0; i < primarySegments.size(); i++) {
                for (int j = 0; j < foreignSegments.size(); j++) {
                    logger.info("start build relation primary segment: " + i + " to foreign segment: " + j);
                    buildRelationIndex(primarySegments.get(i), foreignSegments.get(j));
                    logger.info("build relation primary segment: " + i + " to foreign segment: " + j + " success");
                }
            }
            workOver(Task.Result.SUCCEEDED);
            logger.info("generate relation: " + relation.getKey() + " success");
        } catch (Exception e) {
            logger.error("generate relation: " + relation.getKey() + " failed because [" + e.getMessage() + "]", e);
            workOver(Task.Result.FAILED);
        }
    }

    /**
     * 建关联索引
     *
     * @param primary
     * @param foreign
     */
    private void buildRelationIndex(Segment primary, Segment foreign) {
        RelationIndex relationIndex = foreign.getRelation(relation);
        try {
            ImmutableBitMap allShow = primary.getAllShowIndex();
            CubeLogicColumnKey primaryKey = relation.getPrimaryKey();
            CubeLogicColumnKey foreignKey = relation.getForeignKey();
            List<ColumnKey> keys = primaryKey.getKeyFields();
            List<ColumnKey> foreignKeys = foreignKey.getKeyFields();
            RelationIndexHelper holder = new RelationIndexHelper();
            for (int i = 0, len = keys.size(); i < len; i++) {
                final byte[][] relationIndexBytes = new byte[primary.getRowCount()][];
                ColumnKey primaryColumnKey = keys.get(i);
                ColumnKey foreignColumnKey = foreignKeys.get(i);
                Column primaryColumn = primary.getColumn(primaryColumnKey);
                Column foreignColumn = foreign.getColumn(foreignColumnKey);
                logger.info("start build " + primaryColumnKey.getName() + "->" + foreignColumnKey.getName());
                buildRelationIndexPerColumn(primaryColumn, foreignColumn, allShow, 0, relationIndexBytes, foreign.getRowCount(), holder);
                logger.info("build " + primaryColumnKey.getName() + "->" + foreignColumnKey.getName() + " success");
            }
            buildTargetIndex(relationIndex, holder);
            buildRevertIndex(relationIndex, holder);
            relationIndex.putNullIndex(holder.getNullIndex());
        } finally {
            relationIndex.release();
        }
    }

    /**
     * @param primaryColumn
     * @param foreignColumn
     * @param allShow
     * @param foreignGroupIndex
     */
    private void buildRelationIndexPerColumn(Column primaryColumn, Column foreignColumn,
                                             ImmutableBitMap allShow, int foreignGroupIndex, byte[][] relationIndexBytes,
                                             int foreignTableRowCount, RelationIndexHelper holder) {
        DictionaryEncodedColumn primaryDicColumn = primaryColumn.getDictionaryEncodedColumn();
        DictionaryEncodedColumn foreignDicColumn = foreignColumn.getDictionaryEncodedColumn();
        BitmapIndexedColumn foreignIndexColumn = foreignColumn.getBitmapIndex();
        ImmutableBitMap foreignIndex = foreignIndexColumn.getBitMapIndex(foreignGroupIndex);
        ImmutableBitMap nullIndex = BitMaps.newRoaringMutable();
        int foreignGroupSize = foreignDicColumn.size();
        Object primaryObject = null;
        Object foreignObject = getForeignValue(foreignGroupIndex, foreignGroupSize, foreignDicColumn);
        IntArray revert = IntListFactory.createIntArray(foreignTableRowCount, NIOConstant.INTEGER.NULL_VALUE);
        for (int i = 0, size = primaryDicColumn.size(); i < size; i++) {
            BitmapIndexedColumn primaryIndexColumn = primaryColumn.getBitmapIndex();
            ImmutableBitMap primaryIndex = primaryIndexColumn.getBitMapIndex(i);
            primaryIndex = primaryIndex.getAnd(allShow);
            primaryObject = primaryDicColumn.getValue(i);
            Comparator c = primaryDicColumn.getComparator();
            int result = c.compare(primaryObject, foreignObject);
            if (result < 0) {
                // 主表不存在子表存在
                notMatch(primaryIndex, relationIndexBytes);
            } else if (result == 0) {
                // 表示主表和子表的值存在
                match(primaryIndex, foreignIndex, relationIndexBytes, revert);
                foreignObject = getForeignValue(++foreignGroupIndex, foreignGroupSize, foreignDicColumn);
                foreignIndex = getForeignColumnIndex(foreignGroupIndex, foreignGroupSize, foreignColumn);
            } else {
                // 主表存在子表不存在
                while (foreignGroupIndex < foreignGroupSize && c.compare(primaryObject, foreignObject) > 0) {
                    nullIndex = nullIndex.getOr(foreignIndex);
                    foreignObject = getForeignValue(++foreignGroupIndex, foreignGroupSize, foreignDicColumn);
                    foreignIndex = getForeignColumnIndex(foreignGroupIndex, foreignGroupSize, foreignColumn);
                }
                if (c.compare(primaryObject, foreignObject) == 0) {
                    match(primaryIndex, foreignIndex, relationIndexBytes, revert);
                    foreignObject = getForeignValue(++foreignGroupIndex, foreignGroupSize, foreignDicColumn);
                    foreignIndex = getForeignColumnIndex(foreignGroupIndex, foreignGroupSize, foreignColumn);
                } else {
                    notMatch(primaryIndex, relationIndexBytes);
                }
            }
        }
        while (foreignGroupIndex < foreignGroupSize - 1) {
            nullIndex = nullIndex.getOr(foreignIndex);
            foreignIndex = foreignIndexColumn.getBitMapIndex(++foreignGroupIndex);
        }
        nullIndex.getOr(foreignIndex);
        holder.addNullIndex(nullIndex);
        holder.addRevert(revert);
        holder.addIndex(relationIndexBytes);
    }

    public void buildTargetIndex(RelationIndex index, RelationIndexHelper holder) {
        ImmutableBitMap[] target = holder.getIndex();
        for (int i = 0, len = target.length; i < len; i++) {
            index.putIndex(i, target[i]);
        }
    }

    private void buildRevertIndex(RelationIndex index, RelationIndexHelper holder) {
        IntArray target = holder.getRevert();
        for (int i = 0, size = target.size(); i < size; i++) {
            index.putReverseIndex(i, target.get(i));
        }
    }

    private ImmutableBitMap getForeignColumnIndex(int foreignIndex, int foreignGroupSize, Column foreignColumn) {
        if (foreignIndex == foreignGroupSize) {
            return BitMaps.newRoaringMutable();
        }
        return foreignColumn.getBitmapIndex().getBitMapIndex(foreignIndex);
    }

    private void notMatch(ImmutableBitMap indexBitMap, final byte[][] relationIndexBytes) {
        indexBitMap.breakableTraversal(new BreakTraversalAction() {
            @Override
            public boolean actionPerformed(int row) {
                relationIndexBytes[row] = BitMaps.newRoaringMutable().toBytes();
                return false;
            }
        });
    }

    private void match(ImmutableBitMap primary, final ImmutableBitMap foreign, final byte[][] relationIndexBytes, final IntArray revert) {
        primary.breakableTraversal(new BreakTraversalAction() {
            @Override
            public boolean actionPerformed(int row) {
                relationIndexBytes[row] = foreign.toBytes();
                initReverseIndex(revert, row, foreign);
                return false;
            }
        });
    }

    private void initReverseIndex(final IntArray index, final int row, ImmutableBitMap bitMap) {
        bitMap.breakableTraversal(new BreakTraversalAction() {
            @Override
            public boolean actionPerformed(int rowIndex) {
                index.put(rowIndex, row);
                return false;
            }
        });
    }

    private Object getForeignValue(int index, int size, DictionaryEncodedColumn column) {
        if (index == size || index > size) {
            return null;
        }
        return column.getValue(index);
    }
}
