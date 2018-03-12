package com.fr.swift.source.etl.datamining.timeseries.regression;

import com.finebi.conf.internalimp.analysis.bean.operator.datamining.AlgorithmBean;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.impl.AllShowBitMap;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.Column;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jonas on 2018/3/12 9:00
 */
public class RegressionResultSet implements SwiftResultSet {
    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(RegressionResultSet.class);
    private AlgorithmBean columnKeyList;
    private List<List<Segment>> segmentListList = new ArrayList<List<Segment>>();
    private int rowIndex = 0, segmentListListSize = 0, segmentListSize = 0, columnIndexSize = 0;
    private int segmentListListCursor = 0, segmentCursor = 0, bitMapCursor = 0;
    private List<Segment> segmentList;
    private Column[] columnIndex = null;
    private ImmutableBitMap bitMap = AllShowBitMap.newInstance(100);
    private List valueList = new ArrayList();
    private ListBasedRow listBasedRow = null;
    private SwiftMetaData swiftMetatable = null;

    public RegressionResultSet(AlgorithmBean ab, List<List<Segment>> tis, SwiftMetaData metaData) {
        this.columnKeyList = ab;
        this.segmentListList = tis;
        this.swiftMetatable = metaData;
        init();
    }

    private void init() {
    }

    @Override
    public void close() {

    }

    @Override
    public boolean next() {
        return false;
    }

    private void setRowValue(ListBasedRow row) {
        this.listBasedRow = row;
    }

    @Override
    public SwiftMetaData getMetaData() {
        return swiftMetatable;
    }

    @Override
    public Row getRowData() {
        return getRowValue();
    }

    private ListBasedRow getRowValue() {
        return this.listBasedRow;
    }
}
