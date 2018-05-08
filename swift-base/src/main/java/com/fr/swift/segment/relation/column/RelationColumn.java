package com.fr.swift.segment.relation.column;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.traversal.TraversalAction;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.cube.nio.NIOConstant;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.BitmapIndexedColumn;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.segment.relation.RelationIndex;
import com.fr.swift.source.RelationSource;
import com.fr.swift.util.Crasher;
import com.fr.swift.util.Util;

import java.lang.reflect.Constructor;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author yee
 * @date 2018/4/3
 */
public class RelationColumn {
    private RelationIndex relationIndex;
    private Segment[] segments;
    private ColumnKey columnKey;
    private DictionaryEncodedColumn[] columns;
    private RelationSource relationSource;
    private int reverseCount;

    /**
     * @param relationIndex 关联索引操作器
     * @param segments      主表的所有segment
     * @param columnKey     主表的column
     */
    public RelationColumn(RelationIndex relationIndex, Segment[] segments, ColumnKey columnKey) {
        this.relationIndex = relationIndex;
        this.segments = segments;
        this.columnKey = columnKey;
        this.columns = new DictionaryEncodedColumn[segments.length];
        this.reverseCount = relationIndex.getReverseCount();
        this.relationSource = columnKey.getRelation();
    }

    public RelationColumn(RelationIndex relationIndex, List<Segment> segments, ColumnKey columnKey) {
        this.segments = new Segment[segments.size()];
        this.segments = segments.toArray(this.segments);
        this.relationIndex = relationIndex;
        this.columnKey = columnKey;
        this.columns = new DictionaryEncodedColumn[segments.size()];
        this.reverseCount = relationIndex.getReverseCount();
        this.relationSource = columnKey.getRelation();
    }

    public RelationColumn(RelationIndex relationIndex, ColumnKey columnKey) {
        Util.requireNonNull(relationIndex, columnKey);
        this.relationSource = columnKey.getRelation();
        Util.requireNonNull(relationSource);
        List<Segment> segments = SwiftContext.getInstance().getSegmentProvider().getSegment(relationSource.getPrimarySource());
        Util.requireNonEmpty(segments);
        this.segments = new Segment[segments.size()];
        this.segments = segments.toArray(this.segments);
        this.relationIndex = relationIndex;
        this.columnKey = columnKey;
        this.columns = new DictionaryEncodedColumn[segments.size()];
        try {
            this.reverseCount = relationIndex.getReverseCount();
        } catch (Exception ignore) {
            this.reverseCount = 0;
        }
    }

    /**
     * 通过子表行号取值
     *
     * @param row
     * @return
     */
    public Object getPrimaryValue(int row) {
        int[] result = getPrimarySegAndRow(row);
        if (null != result) {
            if (columns[result[0]] == null) {
                columns[result[0]] = segments[result[0]].getColumn(columnKey).getDictionaryEncodedColumn();
            }
            return columns[result[0]].getValue(columns[result[0]].getIndexByRow(result[1]));
        }
        return null;
    }

    public Column buildRelationColumn() {
        IResourceLocation baseLocation = relationIndex.getBaseLocation().buildChildLocation("field").buildChildLocation(columnKey.getName());

        Column column = segments[0].getColumn(columnKey);
        Column targetColumn = createColumn(column.getClass(), baseLocation);
        DictionaryEncodedColumn targetDicColumn = targetColumn.getDictionaryEncodedColumn();
        BitmapIndexedColumn bitmapIndexedColumn = targetColumn.getBitmapIndex();
        try {
            targetDicColumn.size();
        } catch (Exception ignore) {
            SwiftLoggers.getLogger(RelationColumn.class).error("Column do not exists! start build this column");
            buildTargetColumn(targetDicColumn, bitmapIndexedColumn);
        }

        return targetColumn;
    }

    private void buildTargetColumn(DictionaryEncodedColumn targetDicColumn, BitmapIndexedColumn bitmapIndexedColumn) {
        int index = 1;
        Set<Integer> globalSet = new HashSet<Integer>();
        for (int i = 0; i < segments.length; i++) {
            if (columns[i] == null) {
                columns[i] = segments[i].getColumn(columnKey).getDictionaryEncodedColumn();
            }
            int size = columns[i].size();

            for (int j = 1; j < size; j++) {
                int global = columns[i].getGlobalIndexByIndex(j);
                if (!globalSet.contains(global)) {
                    try {
                        ImmutableBitMap bitMap = relationIndex.getIndex(i, j);
                        Object value = columns[i].getValue(j);
                        index = handleDicAndIndex(bitMap, index, targetDicColumn, bitmapIndexedColumn, value);
                    } catch (Exception ignore) {
                    }
                }
            }
        }
        targetDicColumn.putSize(index);
        targetDicColumn.release();
        bitmapIndexedColumn.release();
    }

    private int handleDicAndIndex(ImmutableBitMap bitMap, final int index, final DictionaryEncodedColumn dic, BitmapIndexedColumn indexedColumn, Object dicValue) {
        final AtomicBoolean traversal = new AtomicBoolean(false);
        bitMap.traversal(new TraversalAction() {
            @Override
            public void actionPerformed(int row) {
                traversal.set(true);
                dic.putIndex(row, index);
            }
        });
        if (traversal.get()) {
            dic.putValue(index, dicValue);
            indexedColumn.putBitMapIndex(index, bitMap);
            return index + 1;
        }
        return index;
    }

    private <T extends Column> T createColumn(Class<T> clazz, IResourceLocation location) {
        try {
            Constructor<T> constructor = clazz.getDeclaredConstructor(IResourceLocation.class);
            constructor.setAccessible(true);
            return constructor.newInstance(location);
        } catch (Exception e) {
            return Crasher.crash(e.getMessage(), e);
        }
    }

    public int[] getPrimarySegAndRow(int row) {
        if (row < reverseCount) {
            long reverse = relationIndex.getReverseIndex(row);
            if (reverse != NIOConstant.LONG.NULL_VALUE) {
                return reverse2SegAndRow(reverse);
            }
        }
        return null;
    }

    public void release() {
        if (null != columns) {
            for (DictionaryEncodedColumn column : columns) {
                column.release();
            }
        }
    }

    /**
     * 根据long来获取segment序号和segment行号
     *
     * @param reverse
     * @return
     */
    private int[] reverse2SegAndRow(long reverse) {
        int[] result = new int[2];
        result[0] = (int) ((reverse & 0xFFFFFFFF00000000L) >> 32);
        result[1] = (int) (0xFFFFFFFFL & reverse);
        return result;
    }

}
