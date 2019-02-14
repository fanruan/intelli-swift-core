package com.fr.swift.query.group.by2.node;

import com.fr.swift.query.group.by.GroupByEntry;
import com.fr.swift.result.GroupNode;
import com.fr.swift.structure.Pair;
import com.fr.swift.util.function.BinaryFunction;
import com.fr.swift.util.function.Function;

import java.util.Iterator;

/**
 * 当前实现去掉了多余的逻辑，兼顾逻辑拆分和封装
 * <p>
 * Created by Lyon on 2018/4/20.
 */
class GroupNodeIterator implements Iterator<GroupNode> {

    private int dimensionSize;
    private int pageSize;
    private Iterator<Pair<Integer, GroupByEntry>> iterator;
    private Function<Pair<Integer, GroupByEntry>, GroupNode> itemMapper;
    private BinaryFunction<GroupByEntry, GroupNode, GroupNode> rowMapper;
    private GroupNode[] cachedNodes;

    /**
     * 节点的页迭代器
     *
     * @param dimensionSize 维度个数
     * @param pageSize      分页行数
     * @param iterator      底层dft迭代器
     * @param itemMapper    创建Node的函数
     * @param rowMapper     聚合明细的函数
     */
    public GroupNodeIterator(int dimensionSize, int pageSize, Iterator<Pair<Integer, GroupByEntry>> iterator,
                             Function<Pair<Integer, GroupByEntry>, GroupNode> itemMapper,
                             BinaryFunction<GroupByEntry, GroupNode, GroupNode> rowMapper) {
        this.dimensionSize = dimensionSize;
        this.pageSize = pageSize;
        this.iterator = iterator;
        this.itemMapper = itemMapper;
        this.rowMapper = rowMapper;
        this.cachedNodes = new GroupNode[dimensionSize == 0 ? 1 : dimensionSize];
    }

    private void initCachedNodes(int index) {
        GroupNode[] newCachedNodes = new GroupNode[cachedNodes.length];
        newCachedNodes[0] = new GroupNode(-1, null);
        for (int i = 0; i < index; i++) {
            GroupNode child = new GroupNode(i, cachedNodes[i + 1].getDictionaryIndex());
            child.setData(cachedNodes[i + 1].getData());
            newCachedNodes[i + 1] = child;
            newCachedNodes[i].addChild(child);
        }
        cachedNodes = newCachedNodes;
    }

    /**
     * 利用深度优先迭代器提供的维度深度，实现无查找构建树
     */
    private GroupNode getPage() {
        boolean uninitialized = true;
        int rowCount = 0;
        while (rowCount < pageSize && iterator.hasNext()) {
            Pair<Integer, GroupByEntry> pair = iterator.next();
            if (uninitialized) {
                initCachedNodes(pair.getKey());
                uninitialized = false;
            }
            GroupNode child = itemMapper.apply(pair);
            int depth = pair.getKey();
            cachedNodes[depth].addChild(child);
            if (depth == dimensionSize - 1) {
                rowMapper.apply(pair.getValue(), child);
                rowCount++;
                continue;
            }
            cachedNodes[depth + 1] = child;
        }
        return cachedNodes[0];
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public GroupNode next() {
        return getPage();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
