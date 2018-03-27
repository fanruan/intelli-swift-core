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
import com.fr.swift.structure.iterator.RowTraversal;

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
    private Comparator[] comparators;
    private SwiftValuesAndGVI nextOne;
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
        groupColumns = new Column[this.segment.length][this.columnKey.length];
        if(groups == null || groups.length != columnKey.length){
            this.groups = new Group[columnKey.length];
        } else {
            this.groups = groups;
        }
        //初始化
        for (int i = 0; i < this.segment.length; i++) {
            allShowIndex = this.segment[i].getAllShowIndex();
            valuesAndGVIs[i][0] = new SwiftValuesAndGVI(new Object[0], allShowIndex);
            for(int j = 0; j < this.columnKey.length; j++) {
                groupColumns[i][j] = getGroupColumn(this.segment[i].getColumn(columnKey[j]), j);
            }
            iterators[i][0] = getIter(groupColumns[i][0], allShowIndex, 0);
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
            Column singleColumn = groupColumns[numOfSegment][i];
            if(i != index) {
                iterators[numOfSegment][i] = getIter(singleColumn, valuesAndGVIs[numOfSegment][i].getGvi(), i);
            }
            Object[] values = new Object[i + 1];
            System.arraycopy(valuesAndGVIs[numOfSegment][i].getValues(), 0, values, 0, values.length - 1);
            if (iterators[numOfSegment][i].hasNext()) {
                GroupByEntry entry = iterators[numOfSegment][i].next();
                values[values.length - 1] = singleColumn.getDictionaryEncodedColumn().getValue(entry.getIndex());
                valuesAndGVIs[numOfSegment][i + 1] = new SwiftValuesAndGVI(values, valuesAndGVIs[numOfSegment][i].getGvi().getAnd(entry.getTraversal().toBitMap()));
            } else {
                move(numOfSegment,i - 1);
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
        if (segment.length == 1){
            isMin[0] = true;
        }
        for (int i = 1; i < this.segment.length; i++) {
            int result = min.compareTo(next[i], comparators);
            if (result == 0 ) {
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
        if(hasNext()) {
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

    private GroupByResult getIter(Column column, RowTraversal gvi, int index) {
        return GroupBy.createGroupByResult(column, gvi, true);
        /*TempValue tempValue = new TempValue();
        if (groups[index] != null) {
            Column groupColumn = groups[index].getGroupOperator().group(column);
            tempValue.setColumn(groupColumn);
            tempValue.setResultSet(GroupBy.createGroupByResult(groupColumn, gvi, true));
        } else {
            tempValue.setColumn(column);
            tempValue.setResultSet(GroupBy.createGroupByResult(column, gvi, true));
        }
        return tempValue;*/
    }

    private Column getGroupColumn(Column column, int index) {
        Column groupColumn = null;
        if(groups[index] != null) {
            groupColumn = groups[index].getGroupOperator().group(column);
        } else {
            groupColumn = column;
        }
        return groupColumn;
    }
}
