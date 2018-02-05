package com.fr.swift.source.etl.columnfilter;

import com.fr.swift.bitmap.MutableBitMap;
import com.fr.swift.bitmap.impl.BitSetMutableBitMap;
import com.fr.swift.bitmap.traversal.TraversalAction;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.filter.FilterBuilder;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.source.*;
import com.fr.swift.structure.iterator.RowTraversal;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Handsome on 2018/1/24 0024 15:34
 */
public class ColumnFilterOperatorResultSet implements SwiftResultSet {

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(ColumnFilterOperatorResultSet.class);
    private Segment[] segment;
    private SwiftMetaData metaData;
    private FilterInfo filterInfo;
    private int segCursor;
    private int rowCursor;
    private RowTraversal bitMap;
    private TempValue tempValue;
    private boolean isBitMapInit;
    private DictionaryEncodedColumn[] getters;
    private SwiftMetaDataColumn[] metaDataColumn;
    private List<Integer> rowList;


    public ColumnFilterOperatorResultSet(Segment[] segment, SwiftMetaData metaData, FilterInfo filterInfo) {
        this.segment = segment;
        this.metaData = metaData;
        this.filterInfo = filterInfo;
        init();
    }

    private void init() {
        this.segCursor = 0;
        this.rowCursor = 0;
        this.bitMap = this.segment[0].getAllShowIndex();
        this.isBitMapInit = true;
        tempValue = new TempValue();
        rowList = new ArrayList<Integer>();
        try {
            getters = new DictionaryEncodedColumn[this.metaData.getColumnCount()];
            metaDataColumn = new SwiftMetaDataColumn[this.metaData.getColumnCount()];
            for(int i = 0; i < this.metaData.getColumnCount(); i++) {
                metaDataColumn[i] = this.metaData.getColumn(i + 1);
                getters[i] = this.segment[segCursor].getColumn(new ColumnKey(metaDataColumn[i].getName())).getDictionaryEncodedColumn();
            }
        } catch(SwiftMetaDataException e) {
            LOGGER.error("getting meta's column information failed", e);
        }
    }

    @Override
    public void close() throws SQLException {

    }

    private RowTraversal createFilter(Segment segment, FilterInfo filterInfo) {
        return FilterBuilder.buildDetailFilter(segment, filterInfo).createFilterIndex();
    }

    @Override
    public boolean next() {
        if(segCursor < segment.length && rowCursor < bitMap.getCardinality()) {
            for(; segCursor < segment.length; segCursor++) {
                bitMap = createFilter(this.segment[segCursor], this.filterInfo);
                if(bitMap != null) {
                    break;
                }
            }
            if(segCursor == segment.length) {
                return false;
            }
            if(isBitMapInit) {
                this.isBitMapInit = false;
                this.bitMap.traversal(new TraversalAction() {
                    @Override
                    public void actionPerformed(int row) {
                        rowList.add(row);
                    }
                });
            }
            List list = new ArrayList();
            for(int i = 0; i < metaDataColumn.length; i++) {
                list.add(getters[i].getValue(getters[i].getIndexByRow(rowList.get(rowCursor))));
            }
            ListBasedRow basedRow = new ListBasedRow(list);
            tempValue.setRow(basedRow);
            if(rowCursor < rowList.size() - 1) {
                rowCursor ++;
            } else {
                if(segCursor < segment.length) {
                    segCursor ++;
                    rowCursor = 0;
                    rowList = new ArrayList<Integer>();
                    this.isBitMapInit = true;
                } else {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public SwiftMetaData getMetaData() throws SQLException {
        return null;
    }

    @Override
    public Row getRowData() throws SQLException {
        return this.tempValue.getRow();
    }

    private class TempValue {

        private ListBasedRow row;

        public ListBasedRow getRow() {
            return row;
        }

        public void setRow(ListBasedRow row) {
            this.row = row;
        }
    }

}
