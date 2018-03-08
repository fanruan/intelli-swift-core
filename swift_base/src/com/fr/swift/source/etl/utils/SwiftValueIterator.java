package com.fr.swift.source.etl.utils;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.traversal.TraversalAction;
import com.fr.swift.query.group.Group;
import com.fr.swift.query.group.by.GroupBy;
import com.fr.swift.query.group.by.GroupByEntry;
import com.fr.swift.query.group.by.GroupByResult;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DetailColumn;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.structure.iterator.RowTraversal;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Handsome on 2017/12/5 0005 11:57
 */
public class SwiftValueIterator {
    private DetailColumn[] getters;
    private DictionaryEncodedColumn[] mapGetters;
    private SwiftValuesAndGVI[] next;
    //private RowTraversal allShowIndex;
    private ImmutableBitMap allShowIndex;
    private GroupByResult[][] iterators;
    private SwiftValuesAndGVI[][] valuesAndGVIs;
    private Segment[] segment; //分片
    private ColumnKey[] columnKey; //字段
    private Group[] groups;
    private int numOfSegment = 0;
    Comparator[] comparators;
    //SwiftValuesAndGVI nextOne = new SwiftValuesAndGVI(new Object[0], AllShowBitMap.newInstance(100));
    Iterator<GVIAndSegment> resultIter;

    public SwiftValueIterator(Segment[] segments, ColumnKey[] columnKey) {
        this(segments, columnKey, null);
    }

    public SwiftValueIterator(Segment[] segment, ColumnKey[] columnKey, Group[] groups) {
        this.segment = segment;
        this.columnKey = columnKey;
        iterators = new GroupByResult[this.segment.length][this.columnKey.length];
        valuesAndGVIs = new SwiftValuesAndGVI[this.segment.length][this.columnKey.length + 1];
        next = new SwiftValuesAndGVI[this.segment.length];
        if (groups == null || groups.length != columnKey.length) {
            this.groups = new Group[columnKey.length];
        } else {
            this.groups = groups;
        }
        //初始化
        for (int i = 0; i < this.segment.length; i++) {
            allShowIndex = this.segment[i].getAllShowIndex();
            valuesAndGVIs[i][0] = new SwiftValuesAndGVI(new Object[0], allShowIndex);
            iterators[i][0] = getIter(this.segment[i].getColumn(columnKey[0]), allShowIndex, 0);
            if (iterators[i][0].hasNext()) {
                move(i, 0);
            } else {
                //TODO
                next = null;
            }
        }
        comparators = new Comparator[this.columnKey.length];
        for (int i = 0; i < comparators.length; i++) {
            // TODO need to do
            comparators[i] = this.segment[0].getColumn(columnKey[i]).getDictionaryEncodedColumn().getComparator();
        }
        getNextOne();//初始化迭代器
    }

    public boolean hasNext() {
        for (int i = 0; i < this.segment.length; i++) {
            if (next[i] != null) {
                return true;
            }
        }
        return this.resultIter.hasNext();
    }

    private void move(int numOfSegment, int index) {
        if (index < 0) {
            next[numOfSegment] = null;
            return;
        }
        for (int i = index; i < columnKey.length; i++) {
            Column singleColumn = this.segment[numOfSegment].getColumn(columnKey[i]);
            if (i != index) {
                iterators[numOfSegment][i] = getIter(singleColumn, valuesAndGVIs[numOfSegment][i].getGvi(), i);
            }
            Object[] values = new Object[i + 1];
            System.arraycopy(valuesAndGVIs[numOfSegment][i].getValues(), 0, values, 0, values.length - 1);
            if (iterators[numOfSegment][i].hasNext()) {
                GroupByEntry entry = iterators[numOfSegment][i].next();
                values[values.length - 1] = singleColumn.getDictionaryEncodedColumn().getValue(entry.getIndex());
                valuesAndGVIs[numOfSegment][i + 1] = new SwiftValuesAndGVI(values, valuesAndGVIs[numOfSegment][i].getGvi().getAnd(entry.getTraversal().toBitMap()));
            } else {
                move(numOfSegment, i - 1);
                if (next[numOfSegment] == null) {
                    return;
                }
            }
        }
        next[numOfSegment] = valuesAndGVIs[numOfSegment][valuesAndGVIs[numOfSegment].length - 1];
    }

    public void getNextOne() {
        boolean[] isMin = new boolean[this.segment.length];
        // TODO if next[i] is null here, how to solve this problem ?
        SwiftValuesAndGVI min = next[0];
        for (int i = 0; i < this.segment.length; i++) {
            int result = min.compareTo(next[i], comparators);
            if (result == 0) {
                isMin[i] = true;
            } else if (result < 0) {
                isMin[i] = false;
            } else {
                isMin = new boolean[this.segment.length];
                isMin[i] = true;
                min = next[i];
            }
        }
        final List<GVIAndSegment> resultList = new ArrayList<GVIAndSegment>();
        for (int i = 0; i < this.segment.length; i++) {
            if (isMin[i]) {
                final Segment tempSegment = this.segment[i];
                final Object[] values = this.next[i].getValues();
                next[i].getGvi().traversal(new TraversalAction() {
                    @Override
                    public void actionPerformed(int row) {
                        resultList.add(new GVIAndSegment(row, tempSegment, values));
                    }
                });
                moveNext(i);
            }
        }
        resultIter = resultList.iterator();
    }

    public GVIAndSegment next() {
        if (this.resultIter.hasNext()) {
            return this.resultIter.next();
        } else if (hasNext()) {
            getNextOne();
            if (this.resultIter.hasNext()) {
                return this.resultIter.next();
            }
        }
        return null;
    }


    private void moveNext(int numOfSegment) {
        for (int j = this.columnKey.length - 1; j >= 0; j--) {
            if (this.iterators[numOfSegment][j].hasNext()) {
                move(numOfSegment, j);
                return;
                // TODO
            }
        }
        next[numOfSegment] = null;
    }

    private GroupByResult getIter(Column column, RowTraversal gvi, int index) {
        if (groups[index] != null) {
            return GroupBy.createGroupByResult(groups[index].getGroupOperator().group(column), gvi, true);
        }
        return GroupBy.createGroupByResult(column, gvi, true);
    }

}
