package com.fr.swift.source.etl.utils;

import com.fr.swift.query.filter.detail.impl.AllShowDetailFilter;
import com.fr.swift.query.group.Group;
import com.fr.swift.query.group.by.MergerGroupByValues;
import com.fr.swift.query.group.by.MultiGroupByValues;
import com.fr.swift.query.group.info.GroupByInfo;
import com.fr.swift.query.group.info.GroupByInfoImpl;
import com.fr.swift.query.group.info.IndexInfo;
import com.fr.swift.query.group.info.IndexInfoImpl;
import com.fr.swift.query.group.info.cursor.AllCursor;
import com.fr.swift.query.sort.AscSort;
import com.fr.swift.query.sort.DescSort;
import com.fr.swift.query.sort.Sort;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.structure.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Created by pony on 2018/3/30.
 */
public class MergerGroupByValuesFactory {

    private static int fetchSize = 200;

    public static MergerGroupByValues createMergerGroupBy(Segment[] segments, ColumnKey[] dimensions, boolean[] asc) {
        return createMergerGroupBy(segments, dimensions, new Group[dimensions.length], asc);
    }

    public static MergerGroupByValues createMergerGroupBy(Segment[] segments, ColumnKey[] dimensions, Group[] groups, boolean[] asc) {
        if (dimensions == null) {
            dimensions = new ColumnKey[0];
        }
        Comparator[] comparators = null;
        MultiGroupByValues[] multiGroupByValues = new MultiGroupByValues[segments.length];
        for (int i = 0; i < segments.length; i++) {
            int[] cursor = new int[dimensions.length];
            Arrays.fill(cursor, 0);
            List<Column> columns = new ArrayList<Column>();
            for (int j = 0; j < dimensions.length; j++) {
                ColumnKey columnKey = dimensions[j];
                Column column = segments[i].getColumn(columnKey);
                if (groups != null && groups[j] != null) {
                    column = groups[j].getGroupOperator().group(column);
                }
                columns.add(column);
            }
            if (comparators == null) {
                comparators = new Comparator[dimensions.length];
                for (int j = 0; j < columns.size(); j++) {
                    comparators[j] = columns.get(j).getDictionaryEncodedColumn().getComparator();
                }
            }
            GroupByInfo groupByInfo = new GroupByInfoImpl(fetchSize, getPairs(columns),
                    new AllShowDetailFilter(segments[i]), toSortList(asc), new AllCursor());
            multiGroupByValues[i] = new MultiGroupByValues(groupByInfo);
        }
        return new MergerGroupByValues(multiGroupByValues, comparators, asc);
    }

    private static List<Sort> toSortList(boolean[] asc) {
        List<Sort> sorts = new ArrayList<Sort>();
        for (int i = 0; i < asc.length; i++) {
            sorts.add(asc[i] ? new AscSort(i) : new DescSort(i));
        }
        return sorts;
    }

    private static List<Pair<Column, IndexInfo>> getPairs(List<Column> columns) {
        List<Pair<Column, IndexInfo>> pairs = new ArrayList<Pair<Column, IndexInfo>>();
        for (Column column : columns) {
            // TODO: 2019/1/8 从配置取
            IndexInfo indexInfo = new IndexInfoImpl(true, false);
            pairs.add(Pair.of(column, indexInfo));
        }
        return pairs;
    }
}
