package com.fr.swift.result;

import com.fr.swift.query.aggregator.Aggregator;

import java.util.List;
import java.util.Map;

/**
 * Created by Lyon on 2018/4/27.
 */
public interface NodeMergeResultSet<T extends SwiftNode> extends NodeResultSet<T> {

    /**
     * 各个维度的node节点用到的字典值
     *
     * @return
     */
    List<Map<Integer, Object>> getRowGlobalDictionaries();

    /**
     * 明细的聚合器，结果过滤在用明细聚合器汇总之后，再做结果过滤
     *
     * @return
     */
    List<Aggregator> getAggregators();
}
