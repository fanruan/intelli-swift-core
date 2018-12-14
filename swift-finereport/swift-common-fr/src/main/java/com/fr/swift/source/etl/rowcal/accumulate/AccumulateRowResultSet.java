package com.fr.swift.source.etl.rowcal.accumulate;

import com.fr.swift.bitmap.traversal.TraversalAction;
import com.fr.swift.query.group.by.MergerGroupByValues;
import com.fr.swift.query.sort.SortType;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.etl.utils.MergerGroupByValuesFactory;
import com.fr.swift.structure.iterator.RowTraversal;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Handsome on 2018/2/28 0028 15:47
 */
public class AccumulateRowResultSet implements SwiftResultSet {

    private ColumnKey columnKey;
    private Segment[] segments;
    private SwiftMetaData metaData;
    private ColumnKey[] dimensions;
    private Iterator<Double> valueIterator;
    private SortType[] sorts;


    public AccumulateRowResultSet(ColumnKey columnKey, Segment[] segments, SwiftMetaData metaData, ColumnKey[] dimensions) {
        this.columnKey = columnKey;
        this.segments = segments;
        this.metaData = metaData;
        this.dimensions = dimensions == null ? new ColumnKey[0] : dimensions;
        init();
    }

    private void init() {
        boolean[] asc = new boolean[dimensions.length];
        Arrays.fill(asc, true);
        //每个segment再最终结果的偏移量
        final int[] shiftRow = new int[segments.length];
        //总行数
        int row = 0;
        //构造MergerGroupBy
        for (int i = 0; i < segments.length; i++) {
            shiftRow[i] = row;
            row += segments[i].getRowCount();
        }
        MergerGroupByValues mergerGroupByValues = MergerGroupByValuesFactory.createMergerGroupBy(segments, dimensions, asc);
        //组内的结果默认不多，都放到内存
        final Double[] values = new Double[row];
        while (mergerGroupByValues.hasNext()) {
            List<RowTraversal[]> traversals = mergerGroupByValues.next().getValue();
            RowTraversal[] traversal = new RowTraversal[traversals.size()];
            //调用mergetGroupBy遍历，取到最后一个维度的travels算结果，并且根据travels，把结果再填到对应的行
            for (int i = 0; i < traversal.length; i++) {
                if (traversals.get(i) != null) {
                    traversal[i] = traversals.get(i)[dimensions.length];
                }
            }
            for (int i = 0; i < traversal.length; i++) {
                final Value v = new Value();
                if (traversal[i] != null) {
                    final int segIndex = i;
                    traversal[i].traversal(new TraversalAction() {
                        @Override
                        public void actionPerformed(int row) {
                            DictionaryEncodedColumn dic = segments[segIndex].getColumn(columnKey).getDictionaryEncodedColumn();
                            v.add((Number) dic.getValue(dic.getIndexByRow(row)));
                            values[shiftRow[segIndex] + row] = v.getValue();
                        }
                    });
                }
            }
        }
        valueIterator = Arrays.asList(values).iterator();
    }


    @Override
    public void close() throws SQLException {
        valueIterator = null;
    }

    @Override
    public boolean hasNext() throws SQLException {
        return valueIterator.hasNext();
    }

    @Override
    public int getFetchSize() {
        return 0;
    }

    @Override
    public SwiftMetaData getMetaData() throws SQLException {
        return metaData;
    }

    @Override
    public Row getNextRow() throws SQLException {
        List list = new ArrayList();
        list.add(valueIterator.next());
        return new ListBasedRow(list);
    }

    private class Value {
        private Double value;

        public void add(Number o) {
            if (value == null) {
                value = o == null ? null : o.doubleValue();
            } else {
                value = value + (o == null ? 0 : o.doubleValue());
            }
        }

        public Double getValue() {
            return value;
        }

    }
}
