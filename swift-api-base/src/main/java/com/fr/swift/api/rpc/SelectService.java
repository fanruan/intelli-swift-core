package com.fr.swift.api.rpc;

import com.fr.swift.source.SwiftResultSet;

/**
 * @author yee
 * @date 2018/8/23
 */
public interface SelectService {
    /**
     * 查询接口
     *
     * @param queryJson 查询json字符串
     * @return
     */
    SwiftResultSet query(String queryJson);
}
