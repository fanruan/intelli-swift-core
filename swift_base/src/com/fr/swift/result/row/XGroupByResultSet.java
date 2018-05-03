package com.fr.swift.result.row;

import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.result.KeyValue;

import java.util.List;
import java.util.Map;

/**
 * Created by Lyon on 2018/4/1.
 */
public interface XGroupByResultSet<T> extends GroupByResultSet<T> {

    /**
     * 交叉表行的集合
     * @return
     */
    List<KeyValue<RowIndexKey<T>, List<KeyValue<RowIndexKey<T>, AggregatorValue[]>>>> getXResultList();

    /**
     * 表头维度的字典
     * @return
     */
    List<Map<Integer, Object>> getColGlobalDictionaries();

    int colDimensionSize();
}
