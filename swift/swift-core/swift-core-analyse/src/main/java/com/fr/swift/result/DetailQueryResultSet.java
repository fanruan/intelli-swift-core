package com.fr.swift.result;

import com.fr.swift.result.qrs.QueryResultSet;
import com.fr.swift.source.Row;

import java.util.List;

/**
 * @author yee
 * @date 2018-12-17
 */
public interface DetailQueryResultSet extends QueryResultSet<List<Row>> {
    int getRowCount();
}
