package com.fr.swift.api.result;

import com.fr.swift.result.DetailResultSet;
import com.fr.swift.result.SerializableResultSet;
import com.fr.swift.source.Row;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * @author yee
 * @date 2018-12-12
 */
public interface SwiftApiResultSet<T> extends SerializableResultSet, DetailResultSet {
    Map<String, Integer> getLabel2Index() throws SQLException;

    SwiftApiResultSet queryNextPage(T queryInfo) throws SQLException;

    List<Row> getRows();

    boolean isOriginHasNextPage();

}
