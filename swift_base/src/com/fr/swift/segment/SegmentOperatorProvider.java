package com.fr.swift.segment;

import com.fr.swift.source.DataSource;

/**
 * @author yee
 * @date 2018/1/4
 */
public interface SegmentOperatorProvider {
    SegmentOperator getIndexSegmentOperator(DataSource dataSource);

    SegmentOperator getRealtimeSegmentOperator(DataSource dataSource);

    SegmentOperator getDecreaseSegmentOperator(DataSource dataSource);
}
