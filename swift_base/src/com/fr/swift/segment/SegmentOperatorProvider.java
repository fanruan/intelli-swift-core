package com.fr.swift.segment;

import com.fr.swift.source.DataSource;

/**
 * @author yee
 * @date 2018/1/4
 * <p>
 * fixme 变成OperatorProvider了 改改吧
 */
public interface SegmentOperatorProvider {
    ISegmentOperator getIndexSegmentOperator(DataSource dataSource);

    ISegmentOperator getRealtimeSegmentOperator(DataSource dataSource);

    ISegmentOperator getDecreaseSegmentOperator(DataSource dataSource);
}
