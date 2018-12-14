package com.fr.swift.source.etl.rowcal.alldata;

import com.fr.swift.bitmap.traversal.TraversalAction;
import com.fr.swift.query.group.by.MergerGroupByValues;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
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
 * Created by Handsome on 2018/2/24 0024 16:11
 */
public class AllDataRowCalculatorResultSet implements SwiftResultSet {

    private String columnName;
    private Segment[] segments;
    private ColumnKey[] dimensions;
    private SwiftMetaData metaData;
    private AllDataCalculator cal;
    private Iterator<Double> valueIterator;

    public AllDataRowCalculatorResultSet(String columnName, Segment[] segments,
                                         SwiftMetaData metaData, AllDataCalculator cal, ColumnKey[] dimensions) {
        this.columnName = columnName;
        this.segments = segments;
        this.metaData = metaData;
        this.cal = cal;
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
            final Double value = cal.get(segments, traversal, new ColumnKey(columnName));
            for (int i = 0; i < traversal.length; i++) {
                if (traversal[i] != null) {
                    final int segIndex = i;
                    traversal[i].traversal(new TraversalAction() {
                        @Override
                        public void actionPerformed(int row) {
                            values[shiftRow[segIndex] + row] = value;
                        }
                    });
                }
            }
        }
        valueIterator = Arrays.asList(values).iterator();
    }

    private void initAllSameIter() {
        RowTraversal[] traversals = new RowTraversal[segments.length];
        int row = 0;
        for (int i = 0; i < segments.length; i++) {
            row += segments[i].getRowCount();
            traversals[i] = segments[i].getAllShowIndex();
        }
        Double value = cal.get(segments, traversals, new ColumnKey(columnName));
        this.valueIterator = new SingleValueIterator(row, value);
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

    private class SingleValueIterator implements Iterator<Double> {
        private int row;
        private int index = 0;
        private Double value;

        public SingleValueIterator(int row, Double value) {
            this.row = row;
            this.value = value;
        }

        @Override
        public boolean hasNext() {
            return index++ < row;
        }

        @Override
        public Double next() {
            return value;
        }

        @Override
        public void remove() {

        }
    }

}
