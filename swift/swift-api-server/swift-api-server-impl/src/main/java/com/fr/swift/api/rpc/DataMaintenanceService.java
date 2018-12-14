package com.fr.swift.api.rpc;

import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.db.Where;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.source.Row;

import java.util.List;

/**
 * @author yee
 * @date 2018/8/23
 */
public interface DataMaintenanceService extends ApiService {
    /**
     * 插入指定字段数据
     *
     * @param schema
     * @param tableName
     * @param fields
     * @param rows
     * @return
     * @throws Exception
     */
    int insert(SwiftDatabase schema, String tableName, List<String> fields, List<Row> rows) throws Exception;

    /**
     * 插入所有字段
     *
     * @param schema
     * @param tableName
     * @param rows
     * @return
     * @throws Exception
     */
    int insert(SwiftDatabase schema, String tableName, List<Row> rows) throws Exception;

    /**
     * 将某查询的结果插入表中
     *
     * @param schema
     * @param tableName
     * @param queryJson
     * @return
     * @throws Exception
     */
    int insert(SwiftDatabase schema, String tableName, String queryJson) throws Exception;

    /**
     * 删除接口
     *
     * @param schema
     * @param tableName
     * @param where
     * @return
     * @throws Exception
     */
    int delete(SwiftDatabase schema, String tableName, Where where) throws Exception;

    /**
     * 修改数据接口
     *
     * @param schema
     * @param tableName
     * @param resultSet
     * @param where
     * @return
     * @throws Exception
     */
    int update(SwiftDatabase schema, String tableName, SwiftResultSet resultSet, Where where) throws Exception;

}
