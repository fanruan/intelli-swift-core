package com.fr.swift.result.node;

import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.query.aggregator.DoubleAmountAggregatorValue;
import com.fr.swift.result.GroupNode;
import com.fr.swift.result.KeyValue;
import com.fr.swift.result.row.GroupByResultSet;
import com.fr.swift.result.row.RowIndexKey;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.fr.swift.cube.io.IOConstant.NULL_DOUBLE;

/**
 * Created by Lyon on 2018/4/4.
 */
public class GroupNodeFactory {

    public static GroupNode createNode(GroupByResultSet resultSet, int targetLength) {
        if (resultSet instanceof GroupNode){
            return (GroupNode) resultSet;
        }
        List<KeyValue<RowIndexKey<int[]>, AggregatorValue[]>> resultList = resultSet.getResultList();
        List<Map<Integer, Object>> dictionaries = resultSet.getRowGlobalDictionaries();
        Creator<GroupNode> creator = new Creator<GroupNode>() {
            @Override
            public GroupNode create(int deep, Object data) {
                return new GroupNode(deep, data);
            }
        };
        ValueSetter<GroupNode, AggregatorValue[]> valueSetter = new ValueSetter<GroupNode, AggregatorValue[]>() {
            @Override
            public void setValue(GroupNode node, AggregatorValue[] values, int targetLength) {
                node.setAggregatorValue(createAggregatorValueArray(values, targetLength));
            }
        };
        GroupNode root = createFromSortedList(targetLength, resultSet.rowDimensionSize(), resultList, dictionaries, creator, valueSetter);
        return root;
    }

    /**
     * 将聚合指标数组转为配置类（根据结果计算的类型）计算需要的指标的长度
     *
     * @param values 聚合指标数组
     * @param targetLength 指标计算中间过程所用到的各类指标组成的数组的长度
     * @return
     */
    public static AggregatorValue[] createAggregatorValueArray(AggregatorValue[] values, int targetLength) {
        AggregatorValue[] result = new AggregatorValue[targetLength];
        for (int i = 0; i < targetLength; i++) {
            result[i] = i < values.length ? values[i] : new DoubleAmountAggregatorValue(NULL_DOUBLE);
        }
        return result;
    }

    public static Object getData(int dimensionIndex, int key, List<Map<Integer, Object>> dictionaries) {
        return dictionaries.get(dimensionIndex).get(key);
    }


    /**
     * 利用有序序列构造树，避免了构造过程中的排序操作，同时通过缓存节点避免构造过程对树进行查找，构造复杂度为O(n)
     *
     * @param targetLength 指标的长度
     * @param dimensionSize 维度
     * @param resultList 有序列表
     * @param dictionaries 维度值字典
     * @param creator node创建
     * @param valueSetter 给node设置值
     * @param <N> node类型
     * @param <VALUE> node对应的指标集合的类型
     * @return 根节点
     */
    public static <N extends GroupNode, VALUE> N createFromSortedList(int targetLength, int dimensionSize,
                                                            List<KeyValue<RowIndexKey<int[]>, VALUE>> resultList,
                                                            List<Map<Integer, Object>> dictionaries,
                                                            Creator<N> creator, ValueSetter<N, VALUE> valueSetter) {
        // GroupNode各层的节点缓存，第一层为根节点
        Object[] cachedNode = new Object[dimensionSize + 1];
        Arrays.fill(cachedNode, null);
        // 缓存上一次插入的一行数据对于的各个维度的索引
        int[] cachedIndex = new int[dimensionSize];
        Arrays.fill(cachedIndex, -1);
        N root = creator.create(-1, null);
        cachedNode[0] = root;
        Iterator<KeyValue<RowIndexKey<int[]>, VALUE>> iterator = resultList.iterator();
        while (iterator.hasNext()) {
            KeyValue<RowIndexKey<int[]>, VALUE> kv = iterator.next();
            int[] index = kv.getKey().getKey();
            int deep = 0;
            for (; deep < index.length; deep++) {
                if (index[deep] == -1) {
                    break;
                }
                if (cachedNode[deep + 1] == null || cachedIndex[deep] != index[deep]) {
                    // 刷新缓存索引，deep之后的索引都无效了
                    Arrays.fill(cachedIndex, deep, cachedIndex.length, -1);
                    // cachedNode和cachedIndex是同步更新的
                    cachedNode[deep + 1] = creator.create(deep, getData(deep, index[deep], dictionaries));
                    cachedIndex[deep] = index[deep];
                    N node = (N) cachedNode[deep];
                    node.addChild((N) cachedNode[deep + 1]);
                }
            }
            // 给当前kv所代表的行设置值
            valueSetter.setValue((N) cachedNode[deep], kv.getValue(), targetLength);
        }
        return root;
    }

    public interface Creator<N extends GroupNode> {
        N create(int deep, Object data);
    }

    public interface ValueSetter<N extends GroupNode, VALUE> {
        void setValue(N node, VALUE value, int targetLength);
    }
}
