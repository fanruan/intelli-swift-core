package com.fr.swift.source.etl.datamining;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.impl.AllShowBitMap;
import com.fr.swift.bitmap.traversal.TraversalAction;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.ColumnTypeConstants.ColumnType;
import com.fr.swift.source.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Handsome on 2018/1/17 0017 09:54
 */
public class DataMiningOperatorResultSet implements SwiftResultSet {
    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(DataMiningOperatorResultSet.class);
    private List<List<ColumnKey>> lists = new ArrayList<List<ColumnKey>>();
    private List<List<Segment>> tis = new ArrayList<List<Segment>>();
    private int rowIndex = 0, tisSize = 0, tiSize = 0, cIndexSize = 0;
    private int tisCursor = 0, tiCursor = 0, bitMapCursor = 0;
    private List<Segment> ti;
    private Column[] cIndex = null;
    private ImmutableBitMap bitMap = AllShowBitMap.newInstance(100);
    private List valueList = new ArrayList();
    private ListBasedRow listBasedRow = null;
    private SwiftMetaData table = null;

    public DataMiningOperatorResultSet(List<List<ColumnKey>> lists, List<List<Segment>> tis, SwiftMetaData metaData) {
        this.lists = lists;
        this.tis = tis;
        this.table = metaData;
        init();
    }

    private void init() {
        cIndex = new Column[this.lists.size()];
        rowIndex = 0;
        tisSize = tis.size();
        tiSize = tis.size();
        cIndexSize = this.lists.size();
    }

    @Override
    public void close() {

    }

    @Override
    public boolean next() {
        if (tisCursor < tisSize && tiCursor < tiSize && bitMapCursor < bitMap.getCardinality()) {
            try {
                ti = tis.get(tisCursor);
                tiSize = ti.size();

                for (int i = 0; i < cIndexSize; i++) {
                    try {
                        cIndex[i] = ti.get(tiCursor).getColumn(this.lists.get(i).get(tisCursor + 1));
                    } catch (Exception e) {
                        cIndex[i] = null;
                    }
                }

                bitMap = ti.get(tiCursor).getAllShowIndex();
                final List<Integer> intList = new ArrayList<Integer>();
                bitMap.traversal(new TraversalAction() {
                    @Override
                    public void actionPerformed(int row) {
                        intList.add(row);
                    }
                });
                for (int cIndexCursor = 0; cIndexCursor < cIndexSize; cIndexCursor++) {
                    if (cIndex[cIndexCursor] == null) {
                        valueList.add(null);
                    } else {
                        Object ob = cIndex[cIndexCursor].getDictionaryEncodedColumn().getValue(cIndex[cIndexCursor].getDictionaryEncodedColumn().getIndexByRow(intList.get(bitMapCursor)));
                        Object res = "";
                        //TODO
                        if (ob == null) {
                            res = null;
                        } else {
                            res = table.getColumnType(cIndexCursor + 1) == ColumnTypeUtils.columnTypeToSqlType(ColumnType.NUMBER) ?
                                    ((Number) ob).doubleValue() : ob;
                        }
                        valueList.add(res);
                        //TODO  value
                    }
                }
                setRowValue(new ListBasedRow(valueList));
                valueList = new ArrayList();
                if (bitMapCursor < intList.size() - 1) {
                    bitMapCursor++;
                } else {
                    if (tiCursor < tiSize - 1) {
                        tiCursor++;
                        bitMapCursor = 0;
                    } else {
                        if (tisCursor < tisSize) {
                            tisCursor++;
                            tiCursor = 0;
                            bitMapCursor = 0;
                        } else {
                            return false;
                        }
                    }
                }
                return true;
            } catch (SwiftMetaDataException e) {
                LOGGER.error("when getting the meta's information, the error occurred", e);
                return false;
            }
        }
        return false;
    }

    private void setRowValue(ListBasedRow row) {
        this.listBasedRow = row;
    }

    @Override
    public SwiftMetaData getMetaData() {
        return table;
    }

    @Override
    public Row getRowData() {
        return getRowValue();
    }

    private ListBasedRow getRowValue() {
        return this.listBasedRow;
    }
}
