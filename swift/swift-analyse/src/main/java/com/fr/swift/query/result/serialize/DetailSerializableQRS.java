package com.fr.swift.query.result.serialize;

import com.fr.swift.result.DetailQueryResultSet;
import com.fr.swift.result.qrs.QueryResultSetMerger;
import com.fr.swift.source.Row;

import java.io.Serializable;
import java.util.List;

/**
 * Created by lyon on 2018/12/29.
 */
public class DetailSerializableQRS extends BaseSerializableQRS<List<Row>> implements DetailQueryResultSet, Serializable {
    private static final long serialVersionUID = -3828469467383680963L;

    private int rowCount;

    public DetailSerializableQRS(int fetchSize, int rowCount, QueryResultSetMerger merger,
                                 List<Row> page, boolean originHasNextPage) {
        super(fetchSize, merger, page, originHasNextPage);
        this.rowCount = rowCount;
    }

    @Override
    public int getRowCount() {
        return rowCount;
    }
}
