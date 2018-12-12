package com.fr.swift.source.etl.selfrelation;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.traversal.BreakTraversalAction;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Handsome on 2018/1/19 0019 10:03
 */
public class TwoUnionRelationOperatorResultSet implements SwiftResultSet {

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(TwoUnionRelationOperatorResultSet.class);
    private LinkedHashMap<String, Integer> columns = new LinkedHashMap<String, Integer>();
    private String idColumnName;
    private List<String> showColumns = new ArrayList<String>();
    private String[] addedColumns;
    private String parentIdColumnName;
    private RowValue row;
    private Column idColumn;
    private Column pidColumn;
    private int columnLength = 0;
    private SwiftMetaData metaData;
    private int segCursor = 0, rowCursor = 0, rowCount = 0;
    private Map<Object, IndexCollection> valueIndexMap = new HashMap<Object, IndexCollection>();
    private Segment[] segments;

    public TwoUnionRelationOperatorResultSet(LinkedHashMap<String, Integer> columns, String idColumnName, List<String> showColumns,
                                             String[] addedColumns, String parentIdColumnName, Segment[] segments, SwiftMetaData metaData) {
        this.columns = columns;
        this.idColumnName = idColumnName;
        this.showColumns = showColumns;
        this.addedColumns = addedColumns;
        this.parentIdColumnName = parentIdColumnName;
        this.segments = segments;
        this.metaData = metaData;
        init();
    }

    private void init() {
        row = new RowValue();
        idColumn = segments[0].getColumn(new ColumnKey(idColumnName));
        pidColumn = segments[0].getColumn(new ColumnKey(parentIdColumnName));
        columnLength = columns.size();
        rowCount = segments[segCursor].getRowCount();
        try {
            int idIndex = 0, pidIndex = 0;
            for (int i = 0; i < metaData.getColumnCount(); i++) {
                if (metaData.getColumnName(i + 1).equals(idColumnName)) {
                    idIndex = i + 1;
                }
                if (metaData.getColumnName(i + 1).equals(parentIdColumnName)) {
                    pidIndex = i + 1;
                }
            }
            if (idColumn != null && pidColumn != null && metaData.getColumnType(idIndex) == metaData.getColumnType(pidIndex)) {
                for (int i = 0; i < segments.length; i++) {
                    DictionaryEncodedColumn getter = segments[i].getColumn(new ColumnKey(idColumnName)).getDictionaryEncodedColumn();
                    for (int j = 0; j < segments[i].getRowCount(); j++) {
                        int indexByRow = getter.getIndexByRow(j);
                        Object ob = getter.getValue(indexByRow);
                        if (null == ob) {
                            continue;
                        }
                        IndexCollection indexCollection = new IndexCollection(i, j);
                        valueIndexMap.put(ob, indexCollection);
                    }
                }
            }
        } catch (SwiftMetaDataException e) {
            LOGGER.error("getting meta's column information failed", e);
        }
    }

    @Override
    public void close() throws SQLException {

    }

    @Override
    public boolean hasNext() throws SQLException {
        if (segCursor < segments.length && rowCursor < rowCount) {
            rowCount = segments[segCursor].getRowCount();
            int index = 0;
            Object[] res = new Object[columnLength * showColumns.size()];
            Object[] tags = new Object[columnLength];
            List<Number> list = new ArrayList<Number>();
            dealWithID(columnLength, rowCursor, list, segments[segCursor], new ColumnKey(idColumnName), new ColumnKey(parentIdColumnName), segments);
            for (int k = list.size(), cnt = 0; k > 0 && cnt < list.size(); k--, cnt++) {
                tags[cnt] = list.get(k - 1);
            }
            for (String s : showColumns) {
                for (int i = 0; i < columnLength; i++) {
                    if (tags[i] != null) {
                        IndexCollection indexCollection = valueIndexMap.get(tags[i]);
                        DictionaryEncodedColumn getter = segments[indexCollection.getNumOfSegment()].getColumn(new ColumnKey(s)).getDictionaryEncodedColumn();
                        Object showOb = getter.getValue(getter.getIndexByRow(indexCollection.getIndexOfRow()));
                        if (showOb != null) {
                            res[index] = showOb;
                        }
                    }
                    index++;
                }
            }
            List listRow = Arrays.asList(res);
            ListBasedRow listBasedRow = new ListBasedRow(listRow);
            row.setRow(listBasedRow);
            if (rowCursor < rowCount - 1) {
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

    private void dealWithID(final int cl, final int i, final List list,
                            final Segment segment, final ColumnKey idCIndex,
                            final ColumnKey pidCIndex, final Segment[] segments) {

        DictionaryEncodedColumn get1 = segment.getColumn(idCIndex).getDictionaryEncodedColumn();
        Object id = get1.getValue(get1.getIndexByRow(i));
        if (id != null && list.size() < cl) {
            list.add(id);
            DictionaryEncodedColumn get2 = segment.getColumn(pidCIndex).getDictionaryEncodedColumn();
            Object pid = get2.getValue(get2.getIndexByRow(i));
            if (pid != null) {
                for (int k = 0; k < segments.length; k++) {
                    DictionaryEncodedColumn gts = segments[k].getColumn(new ColumnKey(this.idColumnName)).getDictionaryEncodedColumn();
                    int index = gts.getIndex(pid);
                    if (index > -1) {
                        ImmutableBitMap bitMap = segments[k].getColumn(new ColumnKey(this.idColumnName)).getBitmapIndex().getBitMapIndex(index);
                        final int indexOfSeg = k;
                        if (bitMap != null) {
                            bitMap.breakableTraversal(new BreakTraversalAction() {
                                @Override
                                public boolean actionPerformed(int row) {
                                    dealWithID(cl, row, list, segments[indexOfSeg], idCIndex, pidCIndex, segments);
                                    return true;
                                }
                            });
                        }
                    }
                }
            }
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
}
