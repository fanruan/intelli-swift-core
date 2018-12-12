package com.fr.swift.result.row;

import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.result.KeyValue;
import com.fr.swift.result.SwiftResultSet;

import java.util.List;
import java.util.Map;

/**
 * Created by Lyon on 2018/2/27.
 */
public interface GroupByResultSet<T> extends SwiftResultSet {

    /**
     * 表示普通行或者汇总行集合
     * 这边的index是global index
     *
     * @return
     */
    List<KeyValue<RowIndexKey<T>, AggregatorValue[]>> getResultList();

    /**
     * 行结果各个维度用到的字典值
     *
     * @return
     */
    List<Map<Integer, Object>> getRowGlobalDictionaries();

    int rowDimensionSize();
}
