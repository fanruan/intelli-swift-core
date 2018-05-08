package com.fr.swift.cal.info;

import com.fr.swift.query.adapter.dimension.Dimension;
import com.fr.swift.query.adapter.metric.Metric;
import com.fr.swift.source.SourceKey;

/**
 * Created by Lyon on 2018/4/1.
 */
public class XTableGroupQueryInfo extends TableGroupQueryInfo {

    private Dimension[] colDimensions;

    public XTableGroupQueryInfo(Dimension[] rowDimensions, Dimension[] colDimensions, Metric[] metrics, SourceKey table) {
        super(rowDimensions, metrics, table);
        this.colDimensions = colDimensions;
    }

    public Dimension[] getColDimensions() {
        return colDimensions;
    }
}
