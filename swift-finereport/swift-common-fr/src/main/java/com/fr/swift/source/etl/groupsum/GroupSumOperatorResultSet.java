package com.fr.swift.source.etl.groupsum;

import com.fr.swift.query.group.Group;
import com.fr.swift.query.group.by.MergerGroupByValues;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.etl.utils.MergerGroupByValuesFactory;
import com.fr.swift.structure.Pair;
import com.fr.swift.structure.iterator.RowTraversal;
import com.fr.swift.util.function.Function;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Handsome on 2018/1/22 0022 11:38
 */
public class GroupSumOperatorResultSet implements SwiftResultSet {

    private GroupSumTarget[] targets;
    private GroupSumDimension[] dimensions;
    private Function[] convertors;
    private Segment[] segments;
    private MergerGroupByValues mergerGroupByValues;
    private SwiftMetaData metaData;

    public GroupSumOperatorResultSet(GroupSumTarget[] targets, GroupSumDimension[] dimensions, Segment[] segments, SwiftMetaData metaData) {
        this.targets = targets == null ? new GroupSumTarget[0] : targets;
        this.dimensions = dimensions == null ? new GroupSumDimension[0] : dimensions;
        this.segments = segments;
        this.metaData = metaData;
        init();
    }

    private void init() {
        boolean[] asc = new boolean[dimensions.length];
        Arrays.fill(asc, true);
        ColumnKey[] columnKeys = new ColumnKey[dimensions.length];
        Group[] groups = new Group[dimensions.length];
        convertors = new Function[dimensions.length];
        for (int i = 0; i < dimensions.length; i++) {
            columnKeys[i] = new ColumnKey(dimensions[i].getName());
            groups[i] = dimensions[i].getGroup();
            convertors[i] = dimensions[i].createConvertor();
        }
        mergerGroupByValues = MergerGroupByValuesFactory.createMergerGroupBy(segments, columnKeys, groups, asc);
    }


    @Override
    public void close() throws SQLException {
        mergerGroupByValues = null;
    }

    @Override
    public boolean hasNext() throws SQLException {
        return mergerGroupByValues.hasNext();
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
        Pair<Object[], List<RowTraversal[]>> kv = mergerGroupByValues.next();
        List<Object> values = new ArrayList<Object>();
        Object[] dimensionsValues = kv.getKey();
        for (int i = 0; i < dimensionsValues.length; i++) {
            values.add(convertors[i].apply(dimensionsValues[i]));
        }
        List<RowTraversal[]> traversals = kv.getValue();
        RowTraversal[] traversal = new RowTraversal[traversals.size()];
        for (int i = 0; i < traversal.length; i++) {
            if (traversals.get(i) != null) {
                traversal[i] = traversals.get(i)[dimensionsValues.length];
            }
        }
        for (GroupSumTarget target : targets) {
            values.add(target.getSumValue(segments, traversal));
        }
        return new ListBasedRow(values);
    }

}
