package com.fr.swift.source.etl.groupsum;

import com.fr.swift.query.group.Group;
import com.fr.swift.query.group.by.MergerGroupByValues;
import com.fr.swift.result.KeyValue;
import com.fr.swift.result.RowIndexKey;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.source.etl.utils.MergerGroupByValuesFactory;
import com.fr.swift.structure.iterator.RowTraversal;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Handsome on 2018/1/22 0022 11:38
 */
public class SumByGroupOperatorResultSet implements SwiftResultSet {

    private SumByGroupTarget[] targets;
    private SumByGroupDimension[] dimensions;
    private Segment[] segments;
    private MergerGroupByValues mergerGroupByValues;
    private SwiftMetaData metaData;

    public SumByGroupOperatorResultSet(SumByGroupTarget[] targets, SumByGroupDimension[] dimensions, Segment[] segments, SwiftMetaData metaData) {
        this.targets = targets == null ? new SumByGroupTarget[0] : targets;
        this.dimensions = dimensions == null ? new SumByGroupDimension[0] : dimensions;
        this.segments = segments;
        this.metaData = metaData;
        init();
    }

    private void init() {
        boolean[] asc = new boolean[dimensions.length];
        Arrays.fill(asc, true);
        ColumnKey[] columnKeys = new ColumnKey[dimensions.length];
        Group[] groups = new Group[dimensions.length];
        for (int i = 0; i < dimensions.length; i++){
            columnKeys[i] = new ColumnKey(dimensions[i].getName());
            groups[i] = dimensions[i].getGroup();
        }
        mergerGroupByValues = MergerGroupByValuesFactory.createMergerGroupBy(segments, columnKeys, groups,  asc);
    }


    @Override
    public void close() throws SQLException {
        mergerGroupByValues = null;
    }

    @Override
    public boolean next() throws SQLException {
        return mergerGroupByValues.hasNext();
    }

    @Override
    public SwiftMetaData getMetaData() throws SQLException {
        return metaData;
    }

    @Override
    public Row getRowData() throws SQLException {
        KeyValue<RowIndexKey<Object>, List<RowTraversal[]>> kv = mergerGroupByValues.next();
        List valueList = new ArrayList();
        Object[] dimensionsValues = (Object[]) kv.getKey().getKey();
        for (int i = 0; i < dimensionsValues.length; i++) {
            valueList.add(dimensionsValues[i]);
        }
        List<RowTraversal[]> traversals =kv.getValue();
        RowTraversal[] traversal = new RowTraversal[traversals.size()];
        for (int i = 0; i < traversal.length; i++) {
            if (traversals.get(i) != null){
                traversal[i] = traversals.get(i)[dimensionsValues.length];
            }
        }
        for (int i = 0; i< targets.length; i++){
            valueList.add(targets[i].getSumValue(segments, traversal));
        }
        return new ListBasedRow(valueList);
    }

}
