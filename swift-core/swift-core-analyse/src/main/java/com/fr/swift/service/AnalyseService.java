package com.fr.swift.service;

import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.result.qrs.QueryResultSet;

/**
 * @author yee
 * @date 2018/6/13
 * todo 除了Query接口，其他的应该是通过事件触发的操作（更新seg信息啥的），放在接口上怪怪的
 */
public interface AnalyseService extends SwiftService {

    /**
     * 方法调用不区分本地还是远程，转发逻辑在QueryableProcessHandler的实现中处理
     *
     * @param queryJson 查询字符串
     * @return
     * @throws Exception
     */
    QueryResultSet getQueryResult(String queryJson) throws Exception;

    SwiftResultSet getResultResult(String queryJson) throws Exception;
}
