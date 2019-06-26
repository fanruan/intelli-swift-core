package com.fr.swift.query.aggregator;

import com.fr.swift.source.Row;

import java.util.Iterator;

/**
 * @author yee
 * @date 2019-06-24
 */
public interface AggregatorValueSet extends Iterable<AggregatorValueRow> {
    /**
     * 大小
     *
     * @return
     */
    int size();

    /**
     * 清除
     */
    void clear();

    /**
     * set转row
     * @return
     */
    Iterator<Row> data();

    /**
     * 判空
     * @return
     */
    boolean isEmpty();

}
