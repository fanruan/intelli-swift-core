package com.fr.swift.query.result.serialize;

import com.fr.swift.result.DetailQueryResultSet;
import com.fr.swift.source.Row;

import java.util.List;

/**
 * @author lyon
 * @date 2018/12/29
 */
public class SerializedDetailQueryResultSet extends BaseSerializedQueryResultSet<List<Row>> implements DetailQueryResultSet {
    private static final long serialVersionUID = -3828469467383680963L;

    private int rowCount;

    public SerializedDetailQueryResultSet(int fetchSize, int rowCount, List<Row> page, boolean hasNextPage) {
        super(fetchSize, page, hasNextPage);
        this.rowCount = rowCount;
    }

    @Override
    public int getRowCount() {
        return rowCount;
    }
}
