package com.fr.swift.source.etl.join;

import com.fr.swift.bitmap.traversal.TraversalAction;
import com.fr.swift.compare.Comparators;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.group.by.MergerGroupByValues;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.source.ColumnTypeConstants;
import com.fr.swift.source.ColumnTypeUtils;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.source.etl.utils.MergerGroupByValuesFactory;
import com.fr.swift.structure.Pair;
import com.fr.swift.structure.iterator.RowTraversal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Handsome on 2018/1/16 0016 14:23
 */
public class JoinOperatorResultSet implements SwiftResultSet {
    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(JoinOperatorResultSet.class);
    private Segment[] lSegments;
    private Segment[] rSegments;
    private List<JoinColumn> columns;
    //左右不匹配时，是否需要返回结果。
    private boolean writeDifferentValue;
    //右有左没有时，是否要返回结果
    private boolean writeRightLeftValue;
    private SwiftMetaData metaData;
    private MergerGroupByValues lValueIterator;
    private MergerGroupByValues rValueIterator;
    private Pair<Object[], List<RowTraversal[]>> lKeyValue;
    private Pair<Object[], List<RowTraversal[]>> rKeyValue;
    private Comparator[] comparators;
    private LinkedList<Row> leftRows;


    public JoinOperatorResultSet(List<JoinColumn> columns, ColumnKey[] lKey, SwiftMetaData metaData,
                                 ColumnKey[] rKey, Segment[] lSegments, Segment[] rSegments,
                                 boolean writeDifferentValue, boolean writeRightLeftValue) {
        this.columns = columns;
        this.metaData = metaData;
        this.writeDifferentValue = writeDifferentValue;
        this.writeRightLeftValue = writeRightLeftValue;
        this.lSegments = lSegments;
        this.rSegments = rSegments;
        init(lKey, rKey, lSegments, rSegments);
    }

    private void init(ColumnKey[] lKey, ColumnKey[] rKey, Segment[] lSegments, Segment[] rSegments) {
        boolean[] asc = new boolean[lKey.length];
        Arrays.fill(asc, true);
        this.lValueIterator = MergerGroupByValuesFactory.createMergerGroupBy(lSegments, lKey, asc);
        this.rValueIterator = MergerGroupByValuesFactory.createMergerGroupBy(rSegments, rKey, asc);
        comparators = new Comparator[lKey.length];
        try {
            for (int i = 0; i < comparators.length; i++) {
                SwiftMetaDataColumn column = lSegments[0].getMetaData().getColumn(lKey[i].getName());
                comparators[i] = ColumnTypeUtils.getClassType(column) == ColumnTypeConstants.ClassType.STRING ? Comparators.STRING_ASC : Comparators.numberAsc();
            }
        } catch (SwiftMetaDataException e) {
            LOGGER.error(e);
        }
        lKeyValue = lValueIterator.hasNext() ? lValueIterator.next() : null;
        rKeyValue = rValueIterator.hasNext() ? rValueIterator.next() : null;
        leftRows = new LinkedList<Row>();
        moveToNextRows();
    }

    private void moveToNextRows() {
        //如果左边的没了，就只移动右边
        if (lKeyValue == null || lKeyValue.getKey() == null) {
            moverIter();
        } else {
            move2Iter();
        }
    }

    //极端情况可能引起递归过多导致的堆栈问题，可能需要考虑改成while循环
    private void move2Iter() {
        int result = compareValues();
        //相同，就写几行，左右都往下移动
        if (result == 0) {
            createNewLeftRows(lKeyValue, rKeyValue);
            lKeyValue = lValueIterator.hasNext() ? lValueIterator.next() : null;
            rKeyValue = rValueIterator.hasNext() ? rValueIterator.next() : null;
        } else if (result > 0) {
            //左边的快了，就看看是否需要写右边剩下的，移动右边
            if (writeRightLeftValue) {
                createNewLeftRows(null, rKeyValue);
            }
            rKeyValue = rValueIterator.hasNext() ? rValueIterator.next() : null;
        } else {
            //右边的快了就看看要不要写左边有值右边是空值的情况，fulljoin这种不用写，其他的都要写
            if (writeDifferentValue) {
                createNewLeftRows(lKeyValue, null);
            }
            lKeyValue = lValueIterator.hasNext() ? lValueIterator.next() : null;
        }
    }

    private int compareValues() {
        Object[] leftValues = lKeyValue.getKey();
        if (rKeyValue == null || rKeyValue.getKey() == null) {
            return -1;
        }
        Object[] rightValues = rKeyValue.getKey();
        for (int i = 0; i < leftValues.length; i++) {
            int result = comparators[i].compare(leftValues[i], rightValues[i]);
            if (result != 0) {
                return result;
            }
        }
        return 0;
    }

    private void moverIter() {
        //如果不需要写右边剩下的，就结束
        if (writeRightLeftValue && rKeyValue != null) {
            createNewLeftRows(null, rKeyValue);
            rKeyValue = rValueIterator.hasNext() ? rValueIterator.next() : null;
        } else {
            leftRows = null;
        }
    }

    @Override
    public void close() {

    }

    @Override
    public boolean hasNext() {
        while (leftRows != null && leftRows.isEmpty()) {
            moveToNextRows();
        }
        return leftRows != null && !leftRows.isEmpty();
    }

    @Override
    public int getFetchSize() {
        return 0;
    }

    @Override
    public SwiftMetaData getMetaData() {
        return metaData;
    }

    @Override
    public Row getNextRow() {
        return leftRows.poll();
    }

    private void createNewLeftRows(Pair<Object[], List<RowTraversal[]>> lKeyValue,
                                   final Pair<Object[], List<RowTraversal[]>> rKeyValue) {
        leftRows = new LinkedList<Row>();
        if (lKeyValue != null) {
            Object[] lValues = lKeyValue.getKey();
            for (int i = 0; i < lKeyValue.getValue().size(); i++) {
                RowTraversal[] traversals = lKeyValue.getValue().get(i);
                if (traversals != null && traversals[lValues.length] != null) {
                    final int finalI = i;
                    traversals[lValues.length].traversal(new TraversalAction() {
                        @Override
                        public void actionPerformed(int row) {
                            dealWithRightSegments(finalI, row, rKeyValue);
                        }
                    });
                }
            }
        } else {
            dealWithRightSegments(-1, -1, rKeyValue);
        }
    }

    private void dealWithRightSegments(final int leftSegIndex, final int leftRow,
                                       final Pair<Object[], List<RowTraversal[]>> rKeyValue) {
        if (rKeyValue != null) {
            Object[] lValues = rKeyValue.getKey();
            for (int i = 0; i < rKeyValue.getValue().size(); i++) {
                RowTraversal[] traversals = rKeyValue.getValue().get(i);
                if (traversals != null && traversals[lValues.length] != null) {
                    final int finalI = i;
                    traversals[lValues.length].traversal(new TraversalAction() {
                        @Override
                        public void actionPerformed(int row) {
                            insertRow(leftSegIndex, leftRow, finalI, row);
                        }
                    });
                }
            }
        } else {
            insertRow(leftSegIndex, leftRow, -1, -1);
        }
    }

    private void insertRow(int leftSegIndex, int leftRow, int rightSegIndex, int rightRow) {
        List valueList = new ArrayList();
        for (JoinColumn c : columns) {
            if (c.isLeft()) {
                if (leftSegIndex >= 0) {
                    DictionaryEncodedColumn dic = lSegments[leftSegIndex].getColumn(new ColumnKey(c.getColumnName())).getDictionaryEncodedColumn();
                    valueList.add(dic.getValue(dic.getIndexByRow(leftRow)));
                } else {
                    valueList.add(null);
                }
            } else {
                if (rightSegIndex >= 0) {
                    DictionaryEncodedColumn dic = rSegments[rightSegIndex].getColumn(new ColumnKey(c.getColumnName())).getDictionaryEncodedColumn();
                    valueList.add(dic.getValue(dic.getIndexByRow(rightRow)));
                } else {
                    valueList.add(null);
                }
            }
        }
        ListBasedRow row = new ListBasedRow(valueList);
        leftRows.add(row);
    }
}

