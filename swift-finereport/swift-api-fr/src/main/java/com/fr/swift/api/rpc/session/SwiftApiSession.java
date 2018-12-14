package com.fr.swift.api.rpc.session;

import com.fr.swift.api.result.SwiftApiResultSet;
import com.fr.swift.api.rpc.bean.Column;
import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.db.Where;
import com.fr.swift.exception.meta.SwiftMetaDataAbsentException;
import com.fr.swift.query.query.QueryBean;
import com.fr.swift.source.Row;

import java.util.List;

/**
 * @author yee
 * @date 2018/9/6
 */
public interface SwiftApiSession {
    /**
     * 根据QueryBean查询
     *
     * @param database
     * @param queryBean
     * @return
     * @throws SwiftMetaDataAbsentException
     */
    SwiftApiResultSet<String> query(SwiftDatabase database, QueryBean queryBean) throws Exception;

    SwiftApiResultSet<String> query(SwiftDatabase database, String queryJson) throws Exception;

    boolean createTable(SwiftDatabase schema, String tableName, List<Column> columns) throws Exception;

    void dropTable(SwiftDatabase schema, String tableName) throws Exception;

    void truncateTable(SwiftDatabase schema, String tableName) throws Exception;

    int delete(SwiftDatabase schema, String tableName, Where where) throws Exception;

    int insert(SwiftDatabase schema, String tableName, List<Row> rows) throws Exception;

    int insert(SwiftDatabase schema, String tableName, List<String> fields, List<Row> rows) throws Exception;

    void close();
}
