package com.fr.swift.service;

import com.fr.swift.db.Where;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftResultSet;

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
    void insert(SourceKey tableKey, SwiftResultSet resultSet) throws Exception;

    boolean delete(SourceKey tableKey, Where where) throws Exception;

    /**
     * 恢复增量数据
     *
     * @param segKeys seg key
     */
    void recover(List<SegmentKey> segKeys) throws Exception;

    /**
     * 查询
     *
     * @param queryInfo 查询描述
     * @return 数据
     */
    SwiftResultSet query(String queryInfo) throws Exception;
}