package com.fr.swift.result;

import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.query.sort.Sort;
import com.fr.swift.source.SwiftResultSet;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Lyon on 2018/2/27.
 */
public interface GroupByResultSet<T> extends SwiftResultSet {

    /**
     * 表示普通行或者汇总行集合
     * 这边的index是global index
     * @return
     */
    List<KeyValue<RowIndexKey<T>, AggregatorValue[]>> getResultList();

    /**
     * 行结果各个维度用到的字典值
     * @return
     */
    List<Map<Integer, Object>> getGlobalDictionaries();

    /**
     * 维度排序
     * @return
     */
    List<Sort> getIndexSorts();
}
