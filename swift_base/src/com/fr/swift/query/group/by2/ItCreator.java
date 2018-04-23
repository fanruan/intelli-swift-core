package com.fr.swift.query.group.by2;

import com.fr.swift.query.group.by.GroupBy;
import com.fr.swift.query.group.by.GroupByEntry;
import com.fr.swift.segment.column.Column;
import com.fr.swift.structure.iterator.RowTraversal;

import java.util.Iterator;
import java.util.List;

/**
 * Created by Lyon on 2018/4/23.
 */
public class ItCreator implements IteratorCreator<GroupByEntry> {

    private boolean[] asc;
    private int[] cursor;
    private List<Column> dimensions;
    private RowTraversal filter;

    public ItCreator(boolean[] asc, int[] cursor, List<Column> dimensions, RowTraversal filter) {
        this.asc = asc;
        this.cursor = cursor;
        this.dimensions = dimensions;
        this.filter = filter;
    }

    @Override
    public Iterator<GroupByEntry> createIterator(int stackSize, GroupByEntry groupByEntry) {
        if (groupByEntry == null && stackSize == 0) {
            return GroupBy.createGroupByResult(dimensions.get(0), filter, cursor[0], asc[0]);
        }
        return GroupBy.createGroupByResult(dimensions.get(stackSize), groupByEntry.getTraversal(),
                cursor[stackSize], asc[stackSize]);
    }
}
