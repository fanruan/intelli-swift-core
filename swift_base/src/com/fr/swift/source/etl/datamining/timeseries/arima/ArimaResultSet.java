package com.fr.swift.source.etl.datamining.timeseries.arima;

import com.finebi.conf.internalimp.analysis.bean.operator.datamining.AlgorithmBean;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.impl.AllShowBitMap;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jonas on 2018/3/12 9:00
 */
public class ArimaResultSet implements SwiftResultSet {
    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(ArimaResultSet.class);
    private AlgorithmBean columnKeyList;
    private List<Segment> segmentList;
    private ImmutableBitMap bitMap = AllShowBitMap.newInstance(100);
    private ListBasedRow listBasedRow = null;
    private SwiftMetaData selfMetaData = null;
    private SwiftMetaData baseMetaData = null;
    private int rowCursor = 0;

    public ArimaResultSet(AlgorithmBean algorithmBean, SwiftMetaData selfMetaData, SwiftMetaData baseMetaData, List<Segment> segmentList) {
        this.columnKeyList = algorithmBean;
        this.segmentList = segmentList;
        this.selfMetaData = selfMetaData;
        this.baseMetaData = baseMetaData;
        init();
    }

    private void init() {
    }

    @Override
    public void close() {

    }

    @Override
    public boolean next() throws SQLException{
        List row = new ArrayList();
        if(rowCursor < Math.min(segmentList.get(0).getRowCount(),100)){
            for(int i = 0; i < baseMetaData.getColumnCount();i++){
                Column column = segmentList.get(0).getColumn(new ColumnKey(baseMetaData.getColumnName(i+1)));
                DictionaryEncodedColumn dicColumn = column.getDictionaryEncodedColumn();
                row.add(dicColumn.getValue(dicColumn.getIndexByRow(rowCursor)));
            }
            setRowValue(new ListBasedRow(row));
            rowCursor++;
            return true;
        }else {
            return false;
        }
    }

    private void setRowValue(ListBasedRow row) {
        this.listBasedRow = row;
    }

    @Override
    public SwiftMetaData getMetaData() {
        return selfMetaData;
    }

    @Override
    public Row getRowData() {
        return getRowValue();
    }

    private ListBasedRow getRowValue() {
        return this.listBasedRow;
    }
}
