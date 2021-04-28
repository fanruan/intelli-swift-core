package com.fr.swift.cloud.query.aggregator.funnel;

import com.fr.swift.cloud.cube.io.location.IResourceLocation;
import com.fr.swift.cloud.query.column.ComplexColumn;
import com.fr.swift.cloud.segment.column.BitmapIndexedColumn;
import com.fr.swift.cloud.segment.column.Column;
import com.fr.swift.cloud.segment.column.ColumnKey;
import com.fr.swift.cloud.segment.column.DetailColumn;
import com.fr.swift.cloud.segment.column.DictionaryEncodedColumn;

import java.util.Map;

/**
 * @author yee
 * @date 2019-07-11
 */
public class FunnelComplexColumn implements ComplexColumn<String> {
    private Column<String> eventColumn;
    private Map<ColumnKey, Column<?>> columnMap;

    public FunnelComplexColumn(Column<String> eventColumn, Map<ColumnKey, Column<?>> columnMap) {
        this.eventColumn = eventColumn;
        this.columnMap = columnMap;
    }

    @Override
    public Map<ColumnKey, Column<?>> getColumns() {
        return columnMap;
    }

    @Override
    public Column<?> getColumn(ColumnKey columnKey) {
        return columnMap.get(columnKey);
    }

    @Override
    public DictionaryEncodedColumn<String> getDictionaryEncodedColumn() {
        return eventColumn.getDictionaryEncodedColumn();
    }

    @Override
    public BitmapIndexedColumn getBitmapIndex() {
        return eventColumn.getBitmapIndex();
    }

    @Override
    public DetailColumn<String> getDetailColumn() {
        return eventColumn.getDetailColumn();
    }

    @Override
    public IResourceLocation getLocation() {
        return eventColumn.getLocation();
    }
}
