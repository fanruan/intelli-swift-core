package com.fr.swift.source.etl.utils;

import com.fr.swift.query.filter.detail.impl.AllShowDetailFilter;
import com.fr.swift.query.group.Group;
import com.fr.swift.query.group.by.MergerGroupByValues;
import com.fr.swift.query.group.by.MultiGroupByValues;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Created by pony on 2018/3/30.
 */
public class MergerGroupByValuesFactory {

    public static MergerGroupByValues createMergerGroupBy(Segment[] segments, ColumnKey[] dimensions,  boolean[] asc){
        return createMergerGroupBy(segments, dimensions, new Group[dimensions.length], asc);
    }

    public static MergerGroupByValues createMergerGroupBy(Segment[] segments, ColumnKey[] dimensions, Group[] groups,  boolean[] asc){
        if (dimensions == null){
            dimensions = new ColumnKey[0];
        }
        Comparator[] comparators = null;
        MultiGroupByValues[] multiGroupByValues = new MultiGroupByValues[segments.length];
        for (int i = 0; i < segments.length; i++) {
            int[] cursor = new int[dimensions.length];
            Arrays.fill(cursor, 0);
            List<Column> columns = new ArrayList<Column>();
            for (ColumnKey columnKey : dimensions) {
                Column column = segments[i].getColumn(columnKey);
                if (groups != null && groups[i] != null){
                    column = groups[i].getGroupOperator().group(column);
                }
                columns.add(column);
            }
            if (comparators == null) {
                comparators = new Comparator[dimensions.length];
                for (int j = 0; j < columns.size(); j++) {
                    comparators[j] = columns.get(j).getDictionaryEncodedColumn().getComparator();
                }
            }
            multiGroupByValues[i] = new MultiGroupByValues(columns, new AllShowDetailFilter(segments[i]), cursor, asc);
        }
        return new MergerGroupByValues(multiGroupByValues, comparators, asc);
    }
}
