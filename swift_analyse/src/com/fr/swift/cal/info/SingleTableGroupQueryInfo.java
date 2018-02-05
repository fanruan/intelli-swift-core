package com.fr.swift.cal.info;

import com.fr.swift.cal.result.group.Cursor;
import com.fr.swift.query.adapter.dimension.Dimension;
import com.fr.swift.query.adapter.metric.Metric;
import com.fr.swift.query.adapter.target.GroupTarget;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.source.SourceKey;

/**
 * Created by pony on 2017/12/15.
 */
public class SingleTableGroupQueryInfo extends GroupQueryInfo {
    public SingleTableGroupQueryInfo(Cursor cursor, String queryID, Dimension[] dimensions, Metric[] metrics, GroupTarget[] targets, FilterInfo filter, Expander expander) {
        super(cursor, queryID, filter, null, dimensions, metrics, targets, expander);
        SourceKey table = getTable(dimensions, metrics);
        tablesGroups = new TableGroupQueryInfo[]{new TableGroupQueryInfo(dimensions, metrics, table)};
    }

    private SourceKey getTable(Dimension[] dimensions, Metric[] metrics) {
        if (dimensions == null || dimensions.length == 0) {
            return metrics[0].getSourceKey();
        } else {
            return dimensions[0].getSourceKey();
        }
    }
}
