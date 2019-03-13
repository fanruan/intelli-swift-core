package com.fr.swift.cloud.analysis;

import com.fr.swift.result.SwiftResultSet;

/**
 * Created by lyon on 2018/12/19.
 */
public interface MetricQuery {

    SwiftResultSet getResult() throws Exception;
}
