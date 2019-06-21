package com.fr.swift.result;

import com.fr.swift.result.qrs.QueryResultSet;
import com.fr.swift.result.qrs.QueryResultSetMerger;
import com.fr.swift.source.Row;

import java.util.List;

/**
 * @author lyon
 * @date 2018/12/24
 */
public interface IDetailQueryResultSetMerger extends QueryResultSetMerger<QueryResultSet<List<Row>>> {
}
