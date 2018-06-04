package com.fr.swift.query.info.group;

import com.fr.swift.query.QueryType;
import com.fr.swift.query.info.element.dimension.DimensionInfo;
import com.fr.swift.query.info.element.target.TargetInfo;
import com.fr.swift.source.SourceKey;

/**
 * Created by Lyon on 2018/4/1.
 */
public class XGroupQueryInfo extends GroupQueryInfoImpl {

    private DimensionInfo colDimensionInfo;

    public XGroupQueryInfo(String queryId, SourceKey table, DimensionInfo rowDimensionInfo,
                           DimensionInfo colDimensionInfo, TargetInfo targetInfo) {
        super(queryId, table, rowDimensionInfo, targetInfo);
        this.colDimensionInfo = colDimensionInfo;
    }

    public DimensionInfo getColDimensionInfo() {
        return colDimensionInfo;
    }

    @Override
    public QueryType getType() {
        return QueryType.CROSS_GROUP;
    }
}
