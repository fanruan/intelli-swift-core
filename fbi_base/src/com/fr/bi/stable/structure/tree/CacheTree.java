package com.fr.bi.stable.structure.tree;

import com.fr.bi.stable.structure.Node;
import com.fr.bi.common.inter.Release;

import java.util.HashMap;
import java.util.Map;

/**
 * 把每个节点信息缓存下来
 * Created by GUY on 2015/4/27.
 */
public class CacheTree extends Tree implements Release {
    Map<Object, Node> cache = new HashMap<Object, Node>();

    @Override
    public <K extends Node<?>> K addNode(K node, K newNode) {
        K t = super.addNode(node, newNode);
        cache.put(newNode.getValue(), t);
        return t;
    }

    @Override
    public <K extends Node<?>> K getNode(Object node) {
        K t = (K) cache.get(node);
        if (t == null) {
            t = super.getNode(node);
        }
        return t;
    }

    @Override
    public void clear() {
        super.clear();
        cache.clear();
    }
}