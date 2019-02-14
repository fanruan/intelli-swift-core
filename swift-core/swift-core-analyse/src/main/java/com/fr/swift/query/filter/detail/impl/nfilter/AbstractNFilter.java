package com.fr.swift.query.filter.detail.impl.nfilter;

import com.fr.swift.query.filter.detail.impl.AbstractDetailFilter;
import com.fr.swift.result.SwiftNode;
import com.fr.swift.segment.column.DictionaryEncodedColumn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author pony
 * @date 2018/5/11
 */
public abstract class AbstractNFilter extends AbstractDetailFilter {
    private Map<List, Double> lineCacheMap;

    /**
     * 由全局字典序找到局部字典序
     *
     * @param dict        字典
     * @param from        from
     * @param to          to
     * @param globalIndex 全局字典序
     * @return 局部字典序
     */
    static int getLocalIndex(DictionaryEncodedColumn dict, int from, int to, int globalIndex) {
        if (from > to) {
            return -1;
        }
        int mid = (from + to) / 2;
        int midGlobalIndex = dict.getGlobalIndexByIndex(mid);
        if (midGlobalIndex == globalIndex) {
            return mid;
        }
        return globalIndex < midGlobalIndex ?
                getLocalIndex(dict, from, mid - 1, globalIndex) :
                getLocalIndex(dict, mid + 1, to, globalIndex);
    }

    protected Double getValue(SwiftNode node, int targetIndex) {
        if (lineCacheMap == null) {
            lineCacheMap = new HashMap<List, Double>();
        }
        List valueList = new ArrayList();
        SwiftNode parent = node.getParent();
        while (parent != null) {
            valueList.add(parent.getData());
            parent = parent.getParent();
        }
        if (!lineCacheMap.containsKey(valueList)) {
            lineCacheMap.put(valueList, calculateValue(node, targetIndex));
        }
        return lineCacheMap.get(valueList);
    }

    private Double calculateValue(SwiftNode node, int targetIndex) {
        NTree<Double> nTree = getNTree();
        List<SwiftNode> children = node.getParent().getChildren();
        for (SwiftNode n : children) {
            Object value = n.getAggregatorValue(targetIndex).calculateValue();
            nTree.add(value == null ? null : ((Number) value).doubleValue());
        }
        return nTree.getLineValue();
    }

    public abstract NTree<Double> getNTree();
}
