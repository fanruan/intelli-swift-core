package com.fr.swift.source.etl.columnrowtrans;

import com.fr.swift.bitmap.traversal.TraversalAction;
import com.fr.swift.query.group.by.MergerGroupByValues;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.etl.utils.MergerGroupByValuesFactory;
import com.fr.swift.structure.Pair;
import com.fr.swift.structure.iterator.RowTraversal;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Handsome on 2018/1/23 0023 14:16
 */
public class ColumnRowTransOperatorResultSet implements SwiftResultSet {

    private String groupName;
    private String lcName;
    private List<Pair<String, String>> lc_value;
    private List<Pair<String, String>> columns;
    private List<Pair<String, String>> otherColumnNames;
    private SwiftMetaData basicTable;
    private Segment[] segment;
    private MergerGroupByValues valueIter;
    private Map<String, Integer> lcIndexMap;

    public ColumnRowTransOperatorResultSet(String groupName, String lcName, List<Pair<String, String>> columns, Segment[] segment,
                                           List<Pair<String, String>> lc_value, List<Pair<String, String>> otherColumnNames, SwiftMetaData basicTable) {
        this.groupName = groupName;
        this.lcName = lcName;
        this.columns = columns;
        this.segment = segment;
        this.lc_value = lc_value;
        this.otherColumnNames = otherColumnNames;
        this.basicTable = basicTable;
        init();
    }

    private void init() {
        valueIter = MergerGroupByValuesFactory.createMergerGroupBy(segment, new ColumnKey[]{new ColumnKey(groupName)}, new boolean[]{true});
        lcIndexMap = new HashMap<String, Integer>();
        int index = 1;
        for (Pair<String, String> lc : lc_value) {
            lcIndexMap.put(lc.getKey(), index++);
        }
    }


    @Override
    public void close() throws SQLException {
        valueIter = null;
    }

    @Override
    public boolean hasNext() throws SQLException {
        return valueIter.hasNext();
    }

    @Override
    public int getFetchSize() {
        return 0;
    }

    @Override
    public SwiftMetaData getMetaData() throws SQLException {
        return basicTable;
    }

    @Override
    public Row getNextRow() throws SQLException {
        final List list = new ArrayList(Arrays.asList(new Object[getMetaData().getColumnCount()]));
        Pair<Object[], List<RowTraversal[]>> kv = valueIter.next();
        Object[] groupName = kv.getKey();
        list.set(0, groupName[0]);
        List<RowTraversal[]> rowTraversals = kv.getValue();
        for (int i = 0; i < rowTraversals.size(); i++) {
            RowTraversal[] rowTraversal = rowTraversals.get(i);
            if (rowTraversal != null && rowTraversal[groupName.length] != null) {
                final int finalI = i;
                rowTraversal[groupName.length].traversal(new TraversalAction() {
                    @Override
                    public void actionPerformed(int row) {
                        DictionaryEncodedColumn dic = segment[finalI].getColumn(new ColumnKey(lcName)).getDictionaryEncodedColumn();
                        Object value = dic.getValueByRow(row);
                        for (int j = 0; j < columns.size(); j++) {
                            DictionaryEncodedColumn columnDic = segment[finalI].getColumn(new ColumnKey(columns.get(j).getKey())).getDictionaryEncodedColumn();
                            Object columnValue = columnDic.getValueByRow(row);
                            Integer index = lcIndexMap.get(value.toString());
                            if (index != null) {
                                list.set(lcIndexMap.get(value.toString()) + lcIndexMap.size() * j, columnValue);
                            }
                        }
                        for (int j = 0; j < otherColumnNames.size(); j++) {
                            DictionaryEncodedColumn columnDic = segment[finalI].getColumn(new ColumnKey(otherColumnNames.get(j).getKey())).getDictionaryEncodedColumn();
                            Object columnValue = columnDic.getValueByRow(row);
                            if (columnValue != null) {
                                list.set(lcIndexMap.size() * columns.size() + j + 1, columnValue);
                            }
                        }
                    }
                });
            }
        }
        return new ListBasedRow(list);
    }
}
