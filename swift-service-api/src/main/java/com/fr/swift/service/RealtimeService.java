package com.fr.swift.service;

import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.source.SourceKey;

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
    void insert(SourceKey tableKey, SwiftResultSet resultSet) throws Exception;

    /**
     * truncate
     *
     * @param tableKey table key
     */
    void truncate(SourceKey tableKey);
}