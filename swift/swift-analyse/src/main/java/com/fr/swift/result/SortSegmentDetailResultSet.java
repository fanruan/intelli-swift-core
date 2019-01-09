package com.fr.swift.result;

import com.fr.swift.bitmap.BitMaps;
import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.query.group.by.GroupByEntry;
import com.fr.swift.query.group.by2.row.MultiGroupByRowIterator;
import com.fr.swift.query.group.info.GroupByInfo;
import com.fr.swift.query.group.info.GroupByInfoImpl;
import com.fr.swift.query.group.info.IndexInfo;
import com.fr.swift.query.sort.AscSort;
import com.fr.swift.query.sort.DescSort;
import com.fr.swift.query.sort.Sort;
import com.fr.swift.query.sort.SortType;
import com.fr.swift.segment.column.Column;
import com.fr.swift.source.Row;
import com.fr.swift.structure.Pair;
import com.fr.swift.structure.array.HeapIntArray;
import com.fr.swift.structure.array.IntArray;
import com.fr.swift.structure.iterator.MapperIterator;
import com.fr.swift.structure.iterator.RowTraversal;
import com.fr.swift.util.function.Function;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 调用groupBy来进行排序
 *
 * Created by Xiaolei.Liu on 2018/1/24
 * @author yee
 */

public class SortSegmentDetailResultSet extends BaseDetailQueryResultSet {

    private int rowCount;
    private int fetchSize;
    private List<Pair<Column, IndexInfo>> columnList;
    private RowIterator rowNumberIterator;

    public SortSegmentDetailResultSet(int fetchSize, List<Pair<Column, IndexInfo>> columnList, DetailFilter filter,
                                      List<Sort> sorts) {
        super(fetchSize);
        this.rowCount = filter.createFilterIndex().getCardinality();
        this.fetchSize = fetchSize;
        this.columnList = columnList;
        init(filter, sorts);
    }

    private void init(DetailFilter filter, List<Sort> sorts) {
        final List<Pair<Column, IndexInfo>> groupByColumns = getGroupByColumns(sorts);
        GroupByInfo groupByInfo = new GroupByInfoImpl(fetchSize, groupByColumns, filter, convertSorts(sorts), null);
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

    private List<Pair<Column, IndexInfo>> getGroupByColumns(List<Sort> sorts) {
        List<Pair<Column, IndexInfo>> columns = new ArrayList<Pair<Column, IndexInfo>>();
        for (Sort sort : sorts) {
            columns.add(columnList.get(sort.getTargetIndex()));
        }
        return columns;
    }

    @Override
    public List<Row> getPage() {
        List<Row> rows = new ArrayList<Row>();
        int count = fetchSize;
        List<Column> columns = getColumnList(columnList);
        while (rowNumberIterator.hasNext() && count-- > 0) {
            rows.add(SegmentDetailResultSet.readRow(rowNumberIterator.next(), columns));
        }
        return rows;
    }

    static List<Column> getColumnList(List<Pair<Column, IndexInfo>> columnList) {
        List<Column> columns = new ArrayList<Column>();
        for (Pair<Column, IndexInfo> pair : columnList) {
            columns.add(pair.getKey());
        }
        return columns;
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