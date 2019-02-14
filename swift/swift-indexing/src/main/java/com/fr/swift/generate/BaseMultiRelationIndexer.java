package com.fr.swift.generate;

import com.fr.swift.bitmap.BitMaps;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.traversal.TraversalAction;
import com.fr.swift.cube.io.Releasable;
import com.fr.swift.cube.nio.NIOConstant;
import com.fr.swift.generate.history.index.RelationIndexHelper;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.segment.column.BitmapIndexedColumn;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.segment.operator.column.SwiftMultiRelationIndexer;
import com.fr.swift.segment.relation.CubeMultiRelation;
import com.fr.swift.segment.relation.RelationIndex;
import com.fr.swift.segment.relation.impl.CubeLogicColumnKey;
import com.fr.swift.source.SourceKey;
import com.fr.swift.structure.array.LongArray;
import com.fr.swift.structure.array.LongListFactory;
import com.fr.swift.task.TaskResult.Type;
import com.fr.swift.task.impl.BaseWorker;
import com.fr.swift.task.impl.TaskResultImpl;

import java.util.Comparator;
import java.util.List;

/**
 * @author yee
 * @date 2018/1/29
 */
public abstract class BaseMultiRelationIndexer extends BaseWorker implements SwiftMultiRelationIndexer {
    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(BaseMultiRelationIndexer.class);

    private CubeMultiRelation relation;
    protected SwiftSegmentManager provider;

    public BaseMultiRelationIndexer(CubeMultiRelation relation, SwiftSegmentManager provider) {
        this.relation = relation;
        this.provider = provider;
    }


    @Override
    public void work() {
        try {
            buildRelationIndex();
            workOver(new TaskResultImpl(Type.SUCCEEDED));
            LOGGER.info("generate relation: " + relation.getKey() + " success");
        } catch (Exception e) {
            LOGGER.error("generate relation: " + relation.getKey() + " failed because [" + e.getMessage() + "]", e);
            workOver(new TaskResultImpl(Type.FAILED, e));
        }
    }

    @Override
    public void buildRelationIndex() {
        LOGGER.info("start generate relation: " + relation.getKey());
        List<Segment> primarySegments = getSegments(relation.getPrimaryTable());
        List<Segment> foreignSegments = getSegments(relation.getForeignTable());
        for (int j = 0; j < foreignSegments.size(); j++) {
            Segment foreign = foreignSegments.get(j);
            RelationIndex relationIndex = foreign.getRelation(relation);
            int currentPos = 1;
            int reversePos = 0;
            try {
                for (int i = 0; i < primarySegments.size(); i++) {
                    LOGGER.info("start build relation primary segment: " + i + " to foreign segment: " + j);
                    int[] result = buildRelationIndex(primarySegments.get(i), foreignSegments.get(j), relationIndex, i, currentPos, reversePos);
                    currentPos = result[0];
                    reversePos = result[1];
                    LOGGER.info("build relation primary segment: " + i + " to foreign segment: " + j + " success");
                }
                relationIndex.putReverseCount(reversePos);
            } finally {
                releaseIfNeed(relationIndex);
            }
        }
    }

    protected abstract List<Segment> getSegments(SourceKey key);

    protected abstract void releaseIfNeed(Releasable releasable);

    /**
     * 建关联索引
     *
     * @param primary
     * @param foreign
     */
    private int[] buildRelationIndex(Segment primary, Segment foreign, RelationIndex relationIndex, int primaryIndex, int currentPos, int reversePos) {
        ImmutableBitMap allShow = primary.getAllShowIndex();
        CubeLogicColumnKey primaryKey = (CubeLogicColumnKey) relation.getPrimaryKey();
        CubeLogicColumnKey foreignKey = (CubeLogicColumnKey) relation.getForeignKey();
        List<ColumnKey> keys = primaryKey.getKeyFields();
        List<ColumnKey> foreignKeys = foreignKey.getKeyFields();
        RelationIndexHelper holder = new RelationIndexHelper();
        for (int i = 0, len = keys.size(); i < len; i++) {
            byte[][] relationIndexBytes = new byte[primary.getRowCount()][];
            ColumnKey primaryColumnKey = keys.get(i);
            ColumnKey foreignColumnKey = foreignKeys.get(i);
            Column primaryColumn = primary.getColumn(primaryColumnKey);
            Column foreignColumn = foreign.getColumn(foreignColumnKey);
            LOGGER.info("start build " + primaryColumnKey.getName() + "->" + foreignColumnKey.getName());
            buildRelationIndexPerColumn(primaryColumn, foreignColumn, allShow, 0, relationIndexBytes, foreign.getRowCount(), holder, primaryIndex);
            LOGGER.info("build " + primaryColumnKey.getName() + "->" + foreignColumnKey.getName() + " success");
        }
        currentPos = buildTargetIndex(relationIndex, holder, currentPos, primaryIndex);
        reversePos = buildRevertIndex(relationIndex, holder, reversePos);
        relationIndex.putNullIndex(primaryIndex, holder.getNullIndex());
        return new int[]{currentPos, reversePos};
    }

    /**
     * @param primaryColumn
     * @param foreignColumn
     * @param allShow
     * @param foreignGroupIndex
     */
    private void buildRelationIndexPerColumn(Column primaryColumn, Column foreignColumn,
                                             ImmutableBitMap allShow, int foreignGroupIndex, byte[][] relationIndexBytes,
                                             int foreignTableRowCount, RelationIndexHelper holder, int primarySegmentIndex) {
        DictionaryEncodedColumn primaryDicColumn = primaryColumn.getDictionaryEncodedColumn();
        DictionaryEncodedColumn foreignDicColumn = foreignColumn.getDictionaryEncodedColumn();
        BitmapIndexedColumn foreignIndexColumn = foreignColumn.getBitmapIndex();
        ImmutableBitMap foreignIndex = foreignIndexColumn.getBitMapIndex(foreignGroupIndex);
        ImmutableBitMap nullIndex = BitMaps.newRoaringMutable();
        int foreignGroupSize = foreignDicColumn.size();
        Object primaryObject;
        Object foreignObject = getForeignValue(foreignGroupIndex, foreignGroupSize, foreignDicColumn);
        LongArray revert = LongListFactory.createLongArray(foreignTableRowCount, NIOConstant.LONG.NULL_VALUE);
        for (int i = 1, size = primaryDicColumn.size(); i < size; i++) {
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
                match(primaryIndex, foreignIndex, relationIndexBytes, revert, primarySegmentIndex);
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
                    match(primaryIndex, foreignIndex, relationIndexBytes, revert, primarySegmentIndex);
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

    private int buildTargetIndex(RelationIndex index, RelationIndexHelper holder, int pos, int primaryIndex) {
        ImmutableBitMap[] target = holder.getIndex();
        for (int i = 0, len = target.length; i < len; i++, pos++) {
            index.putIndex(primaryIndex, pos, target[i]);
        }
        return pos;
    }

    private int buildRevertIndex(RelationIndex index, RelationIndexHelper holder, int pos) {
        LongArray target = holder.getRevert();
        for (int i = 0, size = target.size(); i < size; i++, pos++) {
            index.putReverseIndex(pos, target.get(i));
        }
        return pos;
    }

    private ImmutableBitMap getForeignColumnIndex(int foreignIndex, int foreignGroupSize, Column foreignColumn) {
        if (foreignIndex == foreignGroupSize) {
            return BitMaps.newRoaringMutable();
        }
        return foreignColumn.getBitmapIndex().getBitMapIndex(foreignIndex);
    }

    private void notMatch(ImmutableBitMap indexBitMap, final byte[][] relationIndexBytes) {
        indexBitMap.traversal(new TraversalAction() {
            @Override
            public void actionPerformed(int row) {
                relationIndexBytes[row] = BitMaps.newRoaringMutable().toBytes();
            }
        });
    }

    private void match(ImmutableBitMap primary, final ImmutableBitMap foreign, final byte[][] relationIndexBytes, final LongArray revert, final int primaryIndex) {
        primary.traversal(new TraversalAction() {
            @Override
            public void actionPerformed(int row) {
                relationIndexBytes[row] = foreign.toBytes();
                initReverseIndex(revert, RelationIndexHelper.merge2Long(primaryIndex, row), foreign);
            }
        });
    }

    private void initReverseIndex(final LongArray index, final long row, ImmutableBitMap bitMap) {
        bitMap.traversal(new TraversalAction() {
            @Override
            public void actionPerformed(int rowIndex) {
                index.put(rowIndex, row);
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
