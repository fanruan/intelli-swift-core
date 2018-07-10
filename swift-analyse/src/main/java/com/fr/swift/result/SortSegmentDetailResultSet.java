package com.fr.swift.result;

import com.fr.swift.bitmap.BitMaps;
import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.query.group.by.GroupByEntry;
import com.fr.swift.query.group.by2.row.MultiGroupByRowIterator;
import com.fr.swift.query.group.info.GroupByInfo;
import com.fr.swift.query.group.info.GroupByInfoImpl;
import com.fr.swift.query.group.info.cursor.ExpanderImpl;
import com.fr.swift.query.group.info.cursor.ExpanderType;
import com.fr.swift.query.sort.AscSort;
import com.fr.swift.query.sort.DescSort;
import com.fr.swift.query.sort.Sort;
import com.fr.swift.query.sort.SortType;
import com.fr.swift.result.row.RowIndexKey;
import com.fr.swift.segment.column.Column;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.structure.array.HeapIntArray;
import com.fr.swift.structure.array.IntArray;
import com.fr.swift.structure.iterator.MapperIterator;
import com.fr.swift.structure.iterator.RowTraversal;
import com.fr.swift.util.function.Function;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * 调用groupBy来进行排序
 *
 * Created by Xiaolei.Liu on 2018/1/24
 */

public class SortSegmentDetailResultSet implements DetailResultSet {

    private int rowCount;
    private List<Column> columnList;
    private SwiftMetaData metaData;
    private RowIterator rowNumberIterator;
    private Iterator<Row> rowIterator;

    public SortSegmentDetailResultSet(List<Column> columnList, DetailFilter filter, List<Sort> sorts, SwiftMetaData metaData) {
        // TODO: 2018/6/19 为了计算总行数过滤了两次，待优化
        this.rowCount = filter.createFilterIndex().getCardinality();
        this.columnList = columnList;
        this.metaData = metaData;
        init(filter, sorts);
    }

    private void init(DetailFilter filter, List<Sort> sorts) {
        final List<Column> groupByColumns = getGroupByColumns(sorts);
        GroupByInfo groupByInfo = new GroupByInfoImpl(groupByColumns, filter, convertSorts(sorts), new ExpanderImpl(ExpanderType.ALL_EXPANDER, new HashSet<RowIndexKey<String[]>>()), null);
        Iterator<GroupByEntry[]> it = new MultiGroupByRowIterator(groupByInfo);
        Iterator<RowTraversal> traversalIterator = new MapperIterator<GroupByEntry[], RowTraversal>(it, new Function<GroupByEntry[], RowTraversal>() {
            @Override
            public RowTraversal apply(GroupByEntry[] p) {
                return p[groupByColumns.size() - 1].getTraversal();
            }
        });
        rowNumberIterator = new RowIterator(traversalIterator);
    }

    private List<Sort> convertSorts(List<Sort> sorts) {
        // 重写排序属性
        List<Sort> sortList = new ArrayList<Sort>();
        for (int i = 0; i < sorts.size(); i++) {
            sortList.add(sorts.get(i).getSortType() == SortType.ASC ? new AscSort(i) : new DescSort(i));
        }
        return sortList;
    }

    private List<Column> getGroupByColumns(List<Sort> sorts) {
        List<Column> columns = new ArrayList<Column>();
        for (Sort sort : sorts) {
            columns.add(columnList.get(sort.getTargetIndex()));
        }
        return columns;
    }

    @Override
    public List<Row> getPage() {
        List<Row> rows = new ArrayList<Row>();
        int count = PAGE_SIZE;
        while (rowNumberIterator.hasNext() && count-- > 0) {
            rows.add(SegmentDetailResultSet.readRow(rowNumberIterator.next(), columnList));
        }
        return rows;
    }

    @Override
    public boolean hasNextPage() {
        return rowNumberIterator.hasNext();
    }

    @Override
    public int getRowCount() {
        return rowCount;
    }

    @Override
    public SwiftMetaData getMetaData() {
        return metaData;
    }

    @Override
    public boolean next() {
        if (rowIterator == null) {
            rowIterator = new SwiftRowIteratorImpl(this);
        }
        return rowIterator.hasNext();
    }

    @Override
    public Row getNextRow() {
        return rowIterator.next();
    }

    @Override
    public void close() {

    }

    // 为了避免装箱操作
    private static class RowIterator {

        private Iterator<RowTraversal> iterator;
        private int rowCursor;
        private IntArray rows;

        RowIterator(Iterator<RowTraversal> iterator) {
            this.iterator = iterator;
            init();
        }

        private void init() {
            rowCursor = 0;
            rows = iterator.hasNext() ? BitMaps.traversal2Array(iterator.next()) : new HeapIntArray(0);
        }

        public boolean hasNext() {
            return rowCursor < rows.size();
        }

        public int next() {
            int row = rows.get(rowCursor++);
            if (rowCursor >= rows.size() && iterator.hasNext()) {
                init();
            }
            return row;
        }
    }
}