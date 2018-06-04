package com.fr.swift.query.info.group;

import com.fr.swift.query.info.element.dimension.Dimension;
import com.fr.swift.query.info.element.metric.Metric;
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
