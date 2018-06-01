package com.fr.swift.service;

import com.fr.swift.query.QueryInfo;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftResultSet;

import java.sql.SQLException;
import java.util.List;

/**
 * @author anchore
 * @date 2018/5/28
 */
public interface RealtimeService extends SwiftService {
    /**
     * 增量导入
     *
     * @param tableKey  表
     * @param resultSet 数据
     */
    void insert(SourceKey tableKey, SwiftResultSet resultSet);

    /**
     * 合并增量块
     *
     * @param tableKeys 表
     */
    void merge(List<SourceKey> tableKeys);

    /**
     * 恢复增量数据
     *
     * @param tableKeys 表
     */
    void recover(List<SourceKey> tableKeys);

    /**
     * 查询
     *
     * @param queryInfo 查询描述
     * @param <T>       数据
     * @return 数据
     */
    <T extends SwiftResultSet> T query(QueryInfo<T> queryInfo) throws SQLException;
}