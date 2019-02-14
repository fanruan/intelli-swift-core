package com.fr.swift.source.etl.selfrelation;

import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.source.ColumnTypeConstants;
import com.fr.swift.source.ColumnTypeUtils;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Handsome on 2018/1/22 0022 10:19
 */
public class OneUnionRelationOperatorResultSet implements SwiftResultSet {

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(OneUnionRelationOperatorResultSet.class);
    int columnLength = 0;
    private LinkedHashMap<String, Integer> columns = new LinkedHashMap<String, Integer>();
    private String idColumnName;
    private List<String> showColumns = new ArrayList<String>();
    private int columnType;
    private String columnName;
    private Segment[] segments;
    private SwiftMetaData metaData;
    private RowValue row;
    private Column column;
    private int[] groupLength;
    private Map<String, IndexCollection> valueIndexMap = new HashMap<String, IndexCollection>();
    private DictionaryEncodedColumn[][] gts = null;
    private int segCursor, rowCursor, rowCount;

    public OneUnionRelationOperatorResultSet(String columnName, List<String> showColumns, String idColumnName,
                                             int columnType, LinkedHashMap<String, Integer> columns,
                                             Segment[] segments, SwiftMetaData metaData) {
        this.columnName = columnName;
        this.showColumns = showColumns;
        this.idColumnName = idColumnName;
        this.columnType = columnType;
        this.columns = columns;
        this.segments = segments;
        this.metaData = metaData;
        row = new RowValue();
        init();
    }

    private void init() {
        segCursor = 0;
        rowCursor = 0;
        columnLength = columns.size();
        groupLength = new int[columnLength];
        column = segments[0].getColumn(new ColumnKey(idColumnName));
        rowCount = segments[0].getRowCount();
        if (column != null) {
            try {
                gts = new DictionaryEncodedColumn[segments.length][metaData.getColumnCount()];
                //TODO  String
                ColumnTypeConstants.ColumnType type = ColumnTypeUtils.sqlTypeToColumnType(metaData.getColumn(idColumnName).getType(), 1, 1);
                if (type == ColumnTypeConstants.ColumnType.STRING) {
                    Iterator<Integer> it = columns.values().iterator();
                    int k = 0;
                    while (it.hasNext()) {
                        groupLength[k] = it.next();
                        k++;
                    }
                    for (int i = 0; i < segments.length; i++) {
                        DictionaryEncodedColumn getter = segments[i].getColumn(new ColumnKey(idColumnName)).getDictionaryEncodedColumn();
                        for (int j = 0; j < segments[i].getRowCount(); j++) {
                            int indexByRow = getter.getIndexByRow(j);
                            Object ob = getter.getValue(indexByRow);
                            if (null == ob) {
                                continue;
                            }
                            String v = ob.toString();
                            IndexCollection indexCollection = new IndexCollection(i, j);
                            valueIndexMap.put(v, indexCollection);
                        }
                    }
                    for (int i = 0; i < segments.length; i++) {
                        for (int j = 0; j < metaData.getColumnCount(); j++) {
                            try {
                                gts[i][j] = segments[i].getColumn(new ColumnKey(metaData.getColumnName(j + 1))).getDictionaryEncodedColumn();
                            } catch (Exception e) {
                                gts[i][j] = null;
                            }
                        }
                    }
                }
            } catch (SwiftMetaDataException e) {
                LOGGER.error("getting meta's column information failed", e);
            }
        }
    }

    @Override
    public void close() throws SQLException {

    }

    /**
     * 这边直接返回新增的列就好了，不用把表原有的列也算进去
     *
     * @return
     * @throws SQLException
     */
    @Override
    public boolean hasNext() throws SQLException {
        if (segCursor < segments.length && rowCursor < rowCount) {
            rowCount = segments[segCursor].getRowCount();
            int index = 0;
            Object[] res = new Object[showColumns.size() * columns.size()];
            DictionaryEncodedColumn getter = segments[segCursor].getColumn(new ColumnKey(idColumnName)).getDictionaryEncodedColumn();
            int indexByRow = getter.getIndexByRow(rowCursor);
            Object ob = getter.getValue(indexByRow);
            if (ob != null) {
                String v = ob.toString();
                v = dealWithLayerValue(v, groupLength);
                for (String s : showColumns) {
                    for (int m = 0; m < columnLength; m++) {
                        if (v.length() >= groupLength[m]) {
                            String result = v.substring(0, groupLength[m]);
                            IndexCollection indexCollection = valueIndexMap.get(result);
                            if (indexCollection != null) {
                                DictionaryEncodedColumn get = segments[indexCollection.getNumOfSegment()].getColumn(new ColumnKey(s)).getDictionaryEncodedColumn();
                                int indexOfRow = get.getIndexByRow(indexCollection.getIndexOfRow());
                                Object showOb = get.getValue(indexOfRow);
                                if (showOb != null) {
                                    res[index] = showOb;
                                }
                            }
                        }
                        index++;
                    }
                }
            }
            List list = Arrays.asList(res);
            ListBasedRow listBasedRow = new ListBasedRow(list);
            row.setRow(listBasedRow);
            if (rowCursor < segments[segCursor].getRowCount() - 1) {
                rowCursor++;
            } else {
                if (segCursor < segments.length) {
                    segCursor++;
                    rowCursor = 0;
                } else {
                    return false;
                }
            }
            return true;
        }
        return false;
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
        return row.getRow();
    }

    public String dealWithLayerValue(String v, int[] cz) {
        return v;
    }

    private class IndexCollection {

        private int numOfSegment;
        private int indexOfRow;

        public IndexCollection(int numOfSegment, int indexOfRow) {
            this.numOfSegment = numOfSegment;
            this.indexOfRow = indexOfRow;
        }

        public int getNumOfSegment() {
            return numOfSegment;
        }

        public void setNumOfSegment(int numOfSegment) {
            this.numOfSegment = numOfSegment;
        }

        public int getIndexOfRow() {
            return indexOfRow;
        }

        public void setIndexOfRow(int indexOfRow) {
            this.indexOfRow = indexOfRow;
        }

    }

    private class RowValue {
        private ListBasedRow row;

        public ListBasedRow getRow() {
            return row;
        }

        public void setRow(ListBasedRow row) {
            this.row = row;
        }

    }
}
