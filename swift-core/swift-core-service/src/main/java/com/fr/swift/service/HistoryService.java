package com.fr.swift.service;

import com.fr.swift.basics.annotation.InvokeMethod;
import com.fr.swift.basics.annotation.Target;
import com.fr.swift.basics.handler.CommonLoadProcessHandler;
import com.fr.swift.basics.handler.CommonProcessHandler;
import com.fr.swift.basics.handler.QueryableProcessHandler;
import com.fr.swift.basics.handler.SyncDataProcessHandler;
import com.fr.swift.db.Where;
import com.fr.swift.query.Queryable;
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
public interface HistoryService extends SwiftService, Queryable {
    /**
     * 查询
     *
     * @param queryInfo 查询描述
     * @return 数据
     */
    @Override
    @InvokeMethod(QueryableProcessHandler.class)
    SwiftResultSet query(String queryInfo) throws Exception;

    /**
     * 从共享存储加载
     *
     * @param remoteUris
     * @throws IOException
     */
    @InvokeMethod(value = SyncDataProcessHandler.class, target = Target.HISTORY)
    void load(Map<String, Set<String>> remoteUris, boolean replace) throws Exception;

    @InvokeMethod(value = CommonLoadProcessHandler.class, target = Target.HISTORY)
    void commonLoad(String sourceKey, Map<String, List<String>> needLoad) throws Exception;

    @InvokeMethod(value = CommonProcessHandler.class, target = Target.HISTORY)
    boolean delete(SourceKey sourceKey, Where where, List<String> segKeys) throws Exception;

    @InvokeMethod(value = CommonProcessHandler.class, target = Target.HISTORY)
    void truncate(String sourceKey) throws Exception;
}
