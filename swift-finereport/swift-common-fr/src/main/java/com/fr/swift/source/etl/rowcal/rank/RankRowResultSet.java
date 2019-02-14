package com.fr.swift.source.etl.rowcal.rank;

import com.fr.general.ComparatorUtils;
import com.fr.swift.bitmap.traversal.TraversalAction;
import com.fr.swift.query.group.by.MergerGroupByValues;
import com.fr.swift.query.sort.SortType;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.etl.utils.MergerGroupByValuesFactory;
import com.fr.swift.structure.Pair;
import com.fr.swift.structure.iterator.RowTraversal;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Handsome on 2018/2/28 0028 11:06
 */
public class RankRowResultSet implements SwiftResultSet {

    private SortType type;
    private ColumnKey columnKey;
    private Segment[] segments;
    private ColumnKey[] dimensions;
    private SwiftMetaData metaData;
    private Iterator<Long> valueIterator;

    public RankRowResultSet(SortType type, ColumnKey columnKey, Segment[] segments, SwiftMetaData metaData, ColumnKey[] dimensions) {
        this.type = type;
        this.columnKey = columnKey;
        this.segments = segments;
        this.metaData = metaData;
        this.dimensions = dimensions == null ? new ColumnKey[0] : dimensions;
        init();
    }

    private void init() {
        //只要把排序的字段放在最后，再设置下排序即可
        ColumnKey[] dims = new ColumnKey[dimensions.length + 1];
        System.arraycopy(dimensions, 0, dims, 0, dimensions.length);
        dims[dims.length - 1] = columnKey;
        boolean[] asc = new boolean[dims.length];
        Arrays.fill(asc, true);
        asc[asc.length - 1] = type == SortType.ASC;
        //每个segment再最终结果的偏移量
        final int[] shiftRow = new int[segments.length];
        //总行数
        int row = 0;
        //构造MergerGroupBy
        for (int i = 0; i < segments.length; i++) {
            shiftRow[i] = row;
            row += segments[i].getRowCount();
        }
        MergerGroupByValues mergerGroupByValues = MergerGroupByValuesFactory.createMergerGroupBy(segments, dims, asc);
        //组内的结果默认不多，都放到内存
        final Long[] values = new Long[row];
        final AtomicLong value = new AtomicLong(1);
        Pair<Object[], List<RowTraversal[]>> lastKv = null;
        while (mergerGroupByValues.hasNext()) {
            long size = 0l;
            Pair<Object[], List<RowTraversal[]>> kv = mergerGroupByValues.next();
            if (groupChanged(lastKv, kv)) {
                value.set(1);
            }
            lastKv = kv;
            List<RowTraversal[]> traversals = kv.getValue();
            RowTraversal[] traversal = new RowTraversal[traversals.size()];
            //调用mergetGroupBy遍历，取到最后一个维度的travels算结果，并且根据travels，把结果再填到对应的行
            for (int i = 0; i < traversal.length; i++) {
                if (traversals.get(i) != null) {
                    traversal[i] = traversals.get(i)[dims.length];
                }
            }
            for (int i = 0; i < traversal.length; i++) {
                if (traversal[i] != null) {
                    final int segIndex = i;
                    traversal[i].traversal(new TraversalAction() {
                        @Override
                        public void actionPerformed(int row) {
                            values[shiftRow[segIndex] + row] = value.get();
                        }
                    });
                    size += traversal[i].getCardinality();
                }
            }
            value.set(value.get() + size);
        }
        valueIterator = Arrays.asList(values).iterator();
    }

    private boolean groupChanged(Pair<Object[], List<RowTraversal[]>> lastKv,
                                 Pair<Object[], List<RowTraversal[]>> kv) {
        if (lastKv == null || kv == null) {
            return true;
        }
        Object[] last = lastKv.getKey();
        Object[] current = kv.getKey();
        for (int i = 0; i < last.length - 1; i++) {
            if (!ComparatorUtils.equals(last[i], current[i])) {
                return true;
            }
        }
        return false;
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
}
