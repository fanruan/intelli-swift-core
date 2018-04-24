package com.fr.swift.cal.info;

import com.fr.swift.cal.builder.QueryType;
import com.fr.swift.query.adapter.dimension.Cursor;
import com.fr.swift.query.adapter.dimension.Dimension;
import com.fr.swift.query.adapter.dimension.Expander;
import com.fr.swift.query.adapter.metric.Metric;
import com.fr.swift.query.adapter.target.GroupTarget;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.source.SourceKey;

/**
 * Created by Lyon on 2018/4/1.
 */
public class XGroupQueryInfo extends GroupQueryInfo {

    // TODO: 2018/4/1 占坑
    private Expander xExpander;
    private Dimension[] colDimensions;

    public XGroupQueryInfo(Cursor cursor, String queryID, SourceKey table, FilterInfo filterInfo, Dimension[] dimensions,
                           Dimension[] colDimensions, Metric[] metrics, GroupTarget[] targets, Expander expander) {
        super(queryID, table, null, null);
        this.colDimensions = colDimensions;
    }

    public Dimension[] getColDimensions() {
        return colDimensions;
    }

    @Override
    public QueryType getType() {
        return QueryType.CROSS_GROUP;
    }
}
