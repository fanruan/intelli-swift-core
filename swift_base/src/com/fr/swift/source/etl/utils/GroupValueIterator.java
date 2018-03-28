package com.fr.swift.source.etl.utils;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.impl.AllShowBitMap;
import com.fr.swift.query.group.Group;
import com.fr.swift.query.group.by.GroupBy;
import com.fr.swift.query.group.by.GroupByEntry;
import com.fr.swift.query.group.by.GroupByResult;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;

import java.util.Comparator;

/**
 * Created by Handsome on 2017/12/28 0028 12:00
 */
public class GroupValueIterator {
    private SwiftValuesAndGVI[] next;
    private ImmutableBitMap allShowIndex;
    private GroupByResult[][] iterators;
    private SwiftValuesAndGVI[][] valuesAndGVIs;
    private Segment[] segment; //分片
    private ColumnKey[] columnKey; //字段
    private Group[] groups;
    Comparator[] comparators;
    SwiftValuesAndGVI nextOne;
    private Column[][] groupColumns;

    public GroupValueIterator(Segment[] segments, ColumnKey[] columnKey) {
        this(segments, columnKey, null);
    }

    public GroupValueIterator(Segment[] segment, ColumnKey[] columnKey, Group[] groups) {
        this.segment = segment;
        this.columnKey = columnKey;
        iterators = new GroupByResult[this.segment.length][this.columnKey.length];
        valuesAndGVIs = new SwiftValuesAndGVI[this.segment.length][this.columnKey.length + 1];
        next = new SwiftValuesAndGVI[this.segment.length];
        this.groups = groups;
        initGroupColumns();
        //初始化
        for (int i = 0; i < this.segment.length; i++) {
            allShowIndex = this.segment[i].getAllShowIndex();
            valuesAndGVIs[i][0] = new SwiftValuesAndGVI(new Object[0], allShowIndex);
            iterators[i][0] = GroupBy.createGroupByResult(groupColumns[i][0], allShowIndex, true);
            if (iterators[i][0].hasNext()) {
                move(i, 0);
            } else {
                //TODO here, need to do
                next = null;
            }
        }
        comparators = new Comparator[this.columnKey.length];
        for (int i = 0; i < comparators.length; i++) {
            comparators[i] = this.segment[0].getColumn(columnKey[i]).getDictionaryEncodedColumn().getComparator();
        }
    }

    private void initGroupColumns() {
        if (groups == null || groups.length != columnKey.length) {
            this.groups = new Group[columnKey.length];
        }
        groupColumns = new Column[segment.length][columnKey.length];
        for (int i = 0; i < segment.length; i++) {
            for (int j = 0; j < columnKey.length; j++) {
                Column column = segment[i].getColumn(columnKey[j]);
                groupColumns[i][j] = groups[j] == null ? column : groups[j].getGroupOperator().group(column);
            }
        }
    }

    public boolean hasNext() {
        for (int i = 0; i < this.segment.length; i++) {
            if (next[i] != null) {
                return true;
            }
        }
        return false;
    }

    private void move(int numOfSegment, int index) {
        if (index < 0) {
            next[numOfSegment] = null;
            return;
        }
        for (int i = index; i < columnKey.length; i++) {
            if (i != index) {
                iterators[numOfSegment][i] = GroupBy.createGroupByResult(groupColumns[numOfSegment][i], valuesAndGVIs[numOfSegment][i].getGvi(), true);
            }
            Object[] values = new Object[i + 1];
            System.arraycopy(valuesAndGVIs[numOfSegment][i].getValues(), 0, values, 0, values.length - 1);
            if (iterators[numOfSegment][i].hasNext()) {
                GroupByEntry entry = iterators[numOfSegment][i].next();
                values[values.length - 1] = groupColumns[numOfSegment][i].getDictionaryEncodedColumn().getValue(entry.getIndex());
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

    private void getNextOne() {
        nextOne = new SwiftValuesAndGVI(new Object[0], AllShowBitMap.newInstance(100));
        boolean[] isMin = new boolean[this.segment.length];
        SwiftValuesAndGVI min = next[0];
        if (segment.length == 1) {
            isMin[0] = true;
        }
        for (int i = 1; i < this.segment.length; i++) {
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
        nextOne.setValues(min.getValues());
        for (int i = 0; i < this.segment.length; i++) {
            if (isMin[i]) {
                AggregatorValueCollection aggCollection = new AggregatorValueCollection();
                aggCollection.setBitMap(next[i].getGvi());
                aggCollection.setColumnKey(this.columnKey);
                aggCollection.setSegment(this.segment[i]);
                nextOne.getAggreator().add(aggCollection);
                moveNext(i);
            }
        }
    }

    public SwiftValuesAndGVI next() {
        if (hasNext()) {
            getNextOne();
            return nextOne;
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
}
