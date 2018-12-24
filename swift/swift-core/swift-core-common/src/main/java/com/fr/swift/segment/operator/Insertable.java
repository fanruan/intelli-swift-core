package com.fr.swift.segment.operator;

import com.fr.swift.result.SwiftResultSet;

/**
 * @author anchore
 * @date 2018/12/21
 */
public interface Insertable {

    void insertData(SwiftResultSet swiftResultSet) throws Exception;
}