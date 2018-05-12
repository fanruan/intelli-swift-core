package com.fr.swift.query.filter.detail.impl.nfilter;

import com.fr.swift.query.filter.detail.impl.AbstractDetailFilter;
import com.fr.swift.result.SwiftNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pony on 2018/5/11.
 */
public abstract class AbstractNFilter extends AbstractDetailFilter{
    private Map<List, Double> lineCacheMap;

    protected Double getValue(SwiftNode node, int targetIndex) {
        if (lineCacheMap == null){
            lineCacheMap = new HashMap<List, Double>();
        }
        List valueList = new ArrayList();
        SwiftNode parent = node.getParent();
        while (parent != null){
            valueList.add(parent.getData());
            parent = parent.getParent();
        }
        if (!lineCacheMap.containsKey(valueList)){
            lineCacheMap.put(valueList, calculateValue(node, targetIndex));
        }
        return lineCacheMap.get(valueList);
    }

    private Double calculateValue(SwiftNode node, int targetIndex) {
        NTree<Double> nTree = getNTree();
        List<SwiftNode> children = node.getParent().getChildren();
        for (SwiftNode n : children){
            Object value = n.getAggregatorValue(targetIndex).calculateValue();
            nTree.add(value == null ? null : ((Number)value).doubleValue());
        }
        return nTree.getLineValue();
    }

    public abstract NTree<Double> getNTree();
}
