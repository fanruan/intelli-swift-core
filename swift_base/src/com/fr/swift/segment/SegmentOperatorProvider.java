package com.fr.swift.segment;

import com.fr.swift.source.DataSource;
import com.fr.swift.source.SwiftResultSet;

/**
 * @author yee
 * @date 2018/1/4
 */
public interface SegmentOperatorProvider {
    SegmentOperator getHistorySegmentOperator(DataSource dataSource, SwiftResultSet resultSet);

    SegmentOperator getIncreaseSegmentOperator(DataSource dataSource, SwiftResultSet resultSet);

    SegmentOperator getDecreaseSegmentOperator(DataSource dataSource, SwiftResultSet resultSet);
}
