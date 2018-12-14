package com.fr.swift.query;

import com.fr.swift.result.SwiftResultSet;

/**
 * @author yee
 * @date 2018/10/25
 */
public interface Queryable {
    SwiftResultSet query(String queryJson) throws Exception;
}
