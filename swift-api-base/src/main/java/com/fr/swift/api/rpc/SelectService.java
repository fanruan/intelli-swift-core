package com.fr.swift.api.rpc;

import com.fr.swift.source.SwiftResultSet;

/**
 * @author yee
 * @date 2018/8/23
 */
public interface SelectService {
    SwiftResultSet query(String queryJson);
}
