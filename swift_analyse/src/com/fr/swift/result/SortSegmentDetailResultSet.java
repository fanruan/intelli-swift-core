package com.fr.swift.result;

import com.fr.swift.bitmap.traversal.TraversalAction;
import com.fr.swift.compare.Comparators;
import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.query.sort.SortType;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.structure.array.IntList;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

/**
 * Created by Xiaolei.Liu on 2018/1/24
 */

public class SortSegmentDetailResultSet extends DetailResultSet {


    private List<Column> columnList;
    private DetailFilter filter;
    private IntList sortIndex;
    private List<SortType> sorts;


    private ArrayList<Row> sortedDetailList = new ArrayList<Row>();


    public SortSegmentDetailResultSet(List<Column> columnList, DetailFilter filter, IntList sortIndex, List<SortType> sorts) {
        this.columnList = columnList;
        this.filter = filter;
        this.sortIndex = sortIndex;
        this.sorts = sorts;
        init();
    }


    @Override
    public SwiftMetaData getMetaData() {

        return null;
    }

    @Override
    public Row getRowData() {

        Row row = sortedDetailList.get(rowCount);
        return row;
    }


    public int getMaxRow() {
        return maxRow;
    }


    public int getColumnCount() {
        return columnList.size();
    }

    public DetailSortComparator getDetailSortComparator() {
        return new DetailSortComparator();
    }

    private void init() {
        maxRow = filter.createFilterIndex().getCardinality();
        sortDetail();
    }

    private void sortDetail() {
        final TreeSet<Row> treeSet = new TreeSet<Row>(new DetailSortComparator());
        filter.createFilterIndex().traversal(new TraversalAction() {
            @Override
            public void actionPerformed(int row) {
                List<Object> values = new ArrayList<Object>();
                for (int i = 0; i < columnList.size(); i++) {
                    DictionaryEncodedColumn column = columnList.get(i).getDictionaryEncodedColumn();
                    Object val = column.getValue(column.getIndexByRow(row));
                    values.add(val);
                }
                Row rowData = new ListBasedRow(values);
                treeSet.add(rowData);
            }
        });
        for (Row row : treeSet) {
            sortedDetailList.add(row);
        }
    }

    protected class DetailSortComparator implements Comparator<Row> {

        @Override
        public int compare(Row o1, Row o2) {

            for (int i = 0; i < sortIndex.size(); i++) {
                int c = 0;
                //比较的列先后顺序
                int realColumn = sortIndex.get(i);
                if (sorts.get(realColumn) == SortType.ASC) {
                    c = columnList.get(realColumn).getDictionaryEncodedColumn().getComparator().compare(o1.getValue(realColumn), o2.getValue(realColumn));
                }
                if (sorts.get(realColumn) == SortType.DESC) {
                    c = Comparators.reverse(columnList.get(realColumn).getDictionaryEncodedColumn().getComparator()).compare(o1.getValue(realColumn), o2.getValue(realColumn));
                }
                if (c != 0) {
                    return c;
                }
            }
            return 0;
        }

        @Override
        public boolean equals(Object obj) {
            return false;
        }
    }
}
