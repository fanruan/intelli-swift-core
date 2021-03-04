package com.fr.swift.cloud.result;

import com.fr.swift.cloud.result.qrs.QueryResultSet;
import com.fr.swift.cloud.source.Row;

import java.util.List;

/**
 * @author yee
 * @date 2018-12-17
 */
public interface DetailQueryResultSet extends QueryResultSet<List<Row>> {
    int getRowCount();
}
