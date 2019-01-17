package com.fr.swift.service;

import com.fr.swift.db.Where;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftResultSet;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author yee
 * @date 2018/6/5
 */
public interface HistoryService extends SwiftService {
    /**
     * 查询
     *
     * @param queryInfo 查询描述
     * @return 数据
     */
    SwiftResultSet query(String queryInfo) throws Exception;

    /**
     * 从共享存储加载
     *
     * @param remoteUris
     * @throws IOException
     */
    void load(Map<String, Set<String>> remoteUris, boolean replace) throws Exception;

    void removeHistory(List<SegmentKey> needRemoveList);

    boolean delete(SourceKey sourceKey, Where where) throws Exception;
}
