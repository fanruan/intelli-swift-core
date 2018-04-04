package com.fr.swift.result.node;

import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.result.GroupByResultSet;
import com.fr.swift.result.KeyValue;
import com.fr.swift.result.RowIndexKey;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Lyon on 2018/4/4.
 */
public class GroupNodeFactory {

    /**
     * 利用有序序列构造树，避免了构造过程中的排序操作，同时通过缓存节点避免构造过程对树进行查找，构造复杂度为O(n)
     *
     * @param resultSet
     * @return
     */
    public static GroupNode createFromSortedList(GroupByResultSet resultSet) {
        List<KeyValue<RowIndexKey<int[]>, AggregatorValue[]>> resultList = resultSet.getResultList();
        List<Map<Integer, Object>> dictionaries = resultSet.getGlobalDictionaries();
        int dimensionSize = resultList.isEmpty() ? 0 : resultList.get(0).getKey().getKey().length;
        // GroupNode各层的节点缓存，第一层为根节点
        GroupNode[] cachedNode = new GroupNode[dimensionSize + 1];
        Arrays.fill(cachedNode, null);
        // 缓存上一次插入的一行数据对于的各个维度的索引
        int[] cachedIndex = new int[dimensionSize];
        Arrays.fill(cachedIndex, -1);
        GroupNode root = new GroupNode(0, -1, null, null);
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
            cachedNode[deep].setValues(getValues(kv.getValue()));
        }
        return root;
    }

    private static GroupNode createNode(int dimensionIndex, int key, List<Map<Integer, Object>> dictionaries) {
        Object data = getData(dimensionIndex, key, dictionaries);
        return new GroupNode(0, dimensionIndex, data, new Number[0]);
    }

    private static Object getData(int dimensionIndex, int key, List<Map<Integer, Object>> dictionaries) {
        return dictionaries.get(dimensionIndex).get(key);
    }

    private static Number[] getValues(AggregatorValue[] aggregatorValues) {
        Number[] values = new Number[aggregatorValues.length];
        for (int i = 0; i < values.length; i++) {
            values[i] = aggregatorValues[i].calculate();
        }
        return values;
    }
}
