package com.fr.swift.adaptor.struct.node;

import com.finebi.conf.structure.result.table.BIGroupNode;
import com.fr.swift.adaptor.struct.node.trie.Trie;
import com.fr.swift.adaptor.struct.node.trie.GroupNodeTrie;
import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.query.sort.Sort;
import com.fr.swift.result.GroupByResultSet;
import com.fr.swift.result.KeyValue;
import com.fr.swift.result.RowIndexKey;
import com.fr.swift.result.node.FIFOQueue;
import com.fr.swift.result.node.LinkedListFIFOQueue;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Lyon on 2018/4/1.
 */
public class BIGroupNodeFactory {

    /**
     * 利用有序序列构造树，避免了构造过程中的排序操作，同时通过缓存节点避免构造过程对树进行查找，构造复杂度为O(n)
     *
     * @param resultSet
     * @return
     */
    public static BIGroupNode createFromSortedList(GroupByResultSet resultSet) {
        List<KeyValue<RowIndexKey<int[]>, AggregatorValue[]>> resultList = resultSet.getResultList();
        List<Map<Integer, Object>> dictionaries = resultSet.getGlobalDictionaries();
        int dimensionSize = resultList.isEmpty() ? 0 : resultList.get(0).getKey().getKey().length;
        // BIGroupNode各层的节点缓存，第一层为根节点
        SwiftBIGroupNode[] cachedNode = new SwiftBIGroupNode[dimensionSize + 1];
        Arrays.fill(cachedNode, null);
        // 缓存上一次插入的一行数据对于的各个维度的索引
        int[] cachedIndex = new int[dimensionSize];
        Arrays.fill(cachedIndex, -1);
        SwiftBIGroupNode root = new SwiftBIGroupNode(-1, null, null);
        cachedNode[0] = root;
        Iterator<KeyValue<RowIndexKey<int[]>, AggregatorValue[]>> iterator = resultList.iterator();
        while (iterator.hasNext()) {
            KeyValue<RowIndexKey<int[]>, AggregatorValue[]> kv = iterator.next();
            int[] index = kv.getKey().getKey();
            int deep = 0;
            for (; deep < index.length; deep++) {
                if (index[deep] == -1) {
                    break;
                } else if (cachedNode[deep + 1] == null || cachedIndex[deep] != index[deep]) {
                    // 刷新缓存索引，deep之后的索引都无效了
                    Arrays.fill(cachedIndex, deep, cachedIndex.length, -1);
                    // cachedNode和cachedIndex是同步更新的
                    cachedNode[deep + 1] = createNode(deep, index[deep], dictionaries);
                    cachedIndex[deep] = index[deep];
                    cachedNode[deep].addChild(cachedNode[deep + 1]);
                } else {
                    continue;
                }
            }
            // TODO: 2018/4/4 这边没有考虑到根据汇总值计算计算指标的情况。
            // TODO: 2018/4/4 需要使用AggregatorValueContainer保存和设置汇总值
            // 给当前kv所代表的行设置值
            cachedNode[deep].setSummaryValue(getValues(kv.getValue()));
        }
        return root;
    }

    private static SwiftBIGroupNode createNode(int dimensionIndex, int key, List<Map<Integer, Object>> dictionaries) {
        Object data = getData(dimensionIndex, key, dictionaries);
        return new SwiftBIGroupNode(dimensionIndex, data, new Number[0]);
    }

    private static Object getData(int dimensionIndex, int key, List<Map<Integer, Object>> dictionaries) {
        return dictionaries.get(dimensionIndex).get(key);
    }

    /**
     * 初阶阶段的尝试。。
     * 构建没有指标排序的node结构
     * @param resultSet
     * @return
     */
    public static BIGroupNode create(GroupByResultSet resultSet) {
        Iterator<KeyValue<RowIndexKey<int[]>, AggregatorValue[]>> iterator = resultSet.getResultList().iterator();
        List<Map<Integer, Object>> dictionaries = resultSet.getGlobalDictionaries();
        List<Sort> sorts = resultSet.getIndexSorts();
        Trie<int[], Integer, Number[]> trie = new GroupNodeTrie(-1, null, null, null, sorts);
        while (iterator.hasNext()) {
            KeyValue<RowIndexKey<int[]>, AggregatorValue[]> keyValue = iterator.next();
            Number[] value = getValues(keyValue.getValue());
            int[] key = keyValue.getKey().getKey();
            trie.insert(key, value);
        }
        // convert trie to BIGroupNode
        FIFOQueue<SwiftBIGroupNode> nodeFIFOQueue = new LinkedListFIFOQueue<SwiftBIGroupNode>();
        FIFOQueue<Trie<int[], Integer, Number[]>> trieFIFOQueue = new LinkedListFIFOQueue<Trie<int[], Integer, Number[]>>();
        SwiftBIGroupNode rootNode = new SwiftBIGroupNode(-1, getData(trie, dictionaries), trie.getValue());
        nodeFIFOQueue.add(rootNode);
        trieFIFOQueue.add(trie);
        while (!trieFIFOQueue.isEmpty()) {
            // 广度优先遍历
            Trie<int[], Integer, Number[]> t = trieFIFOQueue.remove();
            SwiftBIGroupNode n = nodeFIFOQueue.remove();
            Iterator<Map.Entry<Integer, Trie<int[], Integer, Number[]>>> entryIterator = t.iterator();
            while (entryIterator.hasNext()) {
                Trie<int[], Integer, Number[]> child = entryIterator.next().getValue();
                SwiftBIGroupNode childNode = new SwiftBIGroupNode(child.deep(), getData(child, dictionaries), child.getValue());
                childNode.setSummaryValue(child.getValue());
                n.addChild(childNode);
                nodeFIFOQueue.add(childNode);
                trieFIFOQueue.add(child);
            }
        }
        return rootNode;
    }

    private static Object getData(Trie<int[], Integer, Number[]> trie, List<Map<Integer, Object>> dictionaries) {
        return trie.deep() == -1 ? null : dictionaries.get(trie.deep()).get(trie.getKey());
    }

    private static Number[] getValues(AggregatorValue[] aggregatorValues) {
        Number[] values = new Number[aggregatorValues.length];
        for (int i = 0; i < values.length; i++) {
            values[i] = aggregatorValues[i].calculate();
        }
        return values;
    }
}