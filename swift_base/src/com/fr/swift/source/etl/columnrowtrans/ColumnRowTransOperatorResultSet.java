package com.fr.swift.source.etl.columnrowtrans;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.traversal.TraversalAction;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.source.etl.utils.AggregatorValueCollection;
import com.fr.swift.source.etl.utils.GroupValueIterator;
import com.fr.swift.source.etl.utils.SwiftValuesAndGVI;

import java.sql.SQLException;
import java.util.*;

/**
 * Created by Handsome on 2018/1/23 0023 14:16
 */
public class ColumnRowTransOperatorResultSet implements SwiftResultSet {

    private String groupName;
    private String lcName;
    private List<NameText> lc_value;
    private List<NameText> columns;
    private List<String> otherColumnNames;
    private SwiftMetaData basicTable;
    private Segment[] segment;
    private TempValue tempRow;
    private int startIndex;
    private int lcCount;
    private Map<Object, Integer> lcNameMap;
    private GroupValueIterator valueIter;

    public ColumnRowTransOperatorResultSet(String groupName, String lcName, List<NameText> columns, Segment[] segment,
                                           List<NameText> lc_value, List<String> otherColumnNames, SwiftMetaData basicTable) {
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
        tempRow = new TempValue();
        startIndex = otherColumnNames.size() + 1;
        lcCount = lc_value.size();
        lcNameMap = getDistinctLcNameIndexMap(this.segment);
        valueIter = new GroupValueIterator(this.segment, new ColumnKey[]{new ColumnKey(this.groupName)});
    }


    @Override
    public void close() throws SQLException {

    }

    @Override
    public boolean next() throws SQLException {
        if (valueIter.hasNext()) {
            SwiftValuesAndGVI valuesAndGVI = valueIter.next();
            AggregatorValueCollection valueCollection = valuesAndGVI.getAggreator().get(0);
            Object[] values = new Object[basicTable.getColumnCount()];
            values[0] = valuesAndGVI.getValues()[0];
            ImmutableBitMap bitMap = valueCollection.getBitMap();
            bitMap.traversal(new PrivateSingleRowTraversalAction(startIndex, values, valueCollection.getSegment(), lcNameMap, lcCount));
            List list = new ArrayList();
            Collections.addAll(list, values);
            ListBasedRow row = new ListBasedRow(list);
            tempRow.setRow(row);
            return true;
        }
        return false;
    }

        @Override
        public SwiftMetaData getMetaData () throws SQLException {
            return basicTable;
        }

        @Override
        public Row getRowData () throws SQLException {
            return this.tempRow.getRow();
        }

        private Map<Object, Integer> getDistinctLcNameIndexMap (Segment[]segments){
            ColumnKey columnKey = new ColumnKey(this.lcName);
            DictionaryEncodedColumn[] getter = new DictionaryEncodedColumn[segments.length];
            Map<Object, Integer> map = new HashMap<Object, Integer>();
            for (int i = 0; i < segments.length; i++) {
                getter[i] = segments[i].getColumn(columnKey).getDictionaryEncodedColumn();
            }
            for (int i = 0; i < this.lc_value.size(); i++) {
                String str = this.lc_value.get(i).origin;
                for (int j = 0; j < segments.length; j++) {
                    int index = getter[j].getIndex(this.lc_value.get(i).origin);
                    if (index > -1) {
                        map.put(str, i);
                        break;
                    }
                }
            }
            return map;
        }


    private class TempValue {

        public TempValue() {}

        public ListBasedRow getRow() {
            return row;
        }

        public void setRow(ListBasedRow row) {
            this.row = row;
        }

        private ListBasedRow row ;

    }

    private class PrivateSingleRowTraversalAction implements TraversalAction {

        public boolean firstLineWrited = false;

        private int startIndex;

        private Object[] values;

        private Segment segment;

        private Map<Object, Integer> lcNameMap;

        private int lcCount;

        public PrivateSingleRowTraversalAction(int startIndex, Object[] values, Segment segment, Map<Object, Integer> lcNameMap, int lcCount) {
            this.startIndex = startIndex;
            this.values = values;
            this.segment = segment;
            this.lcNameMap = lcNameMap;
            this.lcCount = lcCount;
        }

        @Override
        public void actionPerformed(int rowIndices) {
            if (!firstLineWrited) {
                for (int i = 1; i < startIndex; i++) {
                    DictionaryEncodedColumn getter = this.segment.getColumn(new ColumnKey(otherColumnNames.get(i - 1))).getDictionaryEncodedColumn();
                    int indexByRow = getter.getIndexByRow(rowIndices);
                    values[i] = getter.getValue(indexByRow);
                }
                firstLineWrited = true;
            }
            Object indexOfMap = new Object();
            DictionaryEncodedColumn getter = this.segment.getColumn(new ColumnKey(lcName)).getDictionaryEncodedColumn();
            int indexByRow = getter.getIndexByRow(rowIndices);
            indexOfMap = getter.getValue(indexByRow);
            Integer lcNameIndex = this.lcNameMap.get(indexOfMap);
            if(lcNameIndex != null) {
                for(int i = 0; i < columns.size(); i ++) {
                    int cIndex = lcNameIndex + i * this.lcCount + this.startIndex;
                    DictionaryEncodedColumn get = this.segment.getColumn(new ColumnKey(columns.get(i).origin)).getDictionaryEncodedColumn();
                    int indexOfRow = get.getIndexByRow(rowIndices);
                    values[cIndex] = get.getValue(indexOfRow);
                }
            }
        }
    }
}
