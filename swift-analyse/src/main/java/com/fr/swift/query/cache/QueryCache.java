package com.fr.swift.query.cache;

import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.util.Clearable;

/**
 * @Author: lucifer
 * @Description:
 * @Date: Created in 2020/12/16
 */
public interface QueryCache extends Clearable {

    long getIdle();

    void update();

    SwiftResultSet getSwiftResultSet();
}
