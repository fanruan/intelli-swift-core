package com.fr.swift.result;

import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.query.sort.Sort;
import com.fr.swift.query.sort.SortType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Lyon on 18-1-1.
 */
public class NodeFactory {

    // TODO: 2018/2/26 把这边的递归全部改为循环，使用stack之类的数据结构来替换递归 
    public static Node createNode(GroupByResultSet collector) {
        // 这边先假设没有指标排序，同时不考虑分页截断多余数据
        List<Sort> indexSorts = collector.getIndexSorts();
        assert indexSorts != null;
        Map<RowIndexKey, AggregatorValue[]> rows =
                new TreeMap<RowIndexKey, AggregatorValue[]>(new RowIndexKeyComparator(indexSorts));
        Map<RowIndexKey, AggregatorValue[]> sumRows = new HashMap<RowIndexKey, AggregatorValue[]>();
        Iterator<KeyValue<RowIndexKey, AggregatorValue[]>> iterator = null;
        KeyValue<RowIndexKey, AggregatorValue[]> keyValue = null;
        while (iterator.hasNext()) {
            keyValue = iterator.next();
            if (keyValue.getKey().isSum()) {
                sumRows.put(keyValue.getKey(), keyValue.getValue());
                continue;
            }
            rows.put(keyValue.getKey(), keyValue.getValue());
        }
        int sumLength = keyValue.getValue().length;
        int maxDeep = keyValue.getKey().getValues().length - 1;
        RowIndexKey sumKey = new RowIndexKey(maxDeep + 1, true);
        Node root = new Node(sumLength, null);
        setSumValue(root, sumKey, sumRows);
        List<Map.Entry<RowIndexKey, AggregatorValue[]>> subRows = new ArrayList<Map.Entry<RowIndexKey, AggregatorValue[]>>(rows.entrySet());
        createChildrenNode(root, 0, maxDeep, sumLength, subRows, collector.getGlobalDictionaries(), sumRows, sumKey);
        return root;
    }

    private static void setSumValue(Node node, RowIndexKey key, Map<RowIndexKey, AggregatorValue[]> sumRows) {
        AggregatorValue[] sum = sumRows.get(key);
        if (sum != null) {
            node.setAggregatorValue(sum);
        }
    }

    private static void createChildrenNode(Node current, int dimensionIndex, int mapDeep, int sumLength,
                                           List<Map.Entry<RowIndexKey, AggregatorValue[]>> rows,
                                           List<Map<Integer, Object>> globalDictionaries,
                                           Map<RowIndexKey, AggregatorValue[]> sumRows,
                                           RowIndexKey sumKey) {
        Map<Integer, List<Map.Entry<RowIndexKey, AggregatorValue[]>>> groups = getGroupMap(rows, dimensionIndex);
        for (Integer index : groups.keySet()) {
            Node childNode = new Node(sumLength, globalDictionaries.get(dimensionIndex).get(index));
            current.addChild(childNode);
            if (dimensionIndex == mapDeep) {
                assert groups.get(index).size() == 1;
                Map.Entry<RowIndexKey, AggregatorValue[]> entry = groups.get(index).get(0);
                childNode.setAggregatorValue(entry.getValue());
                continue;
            }
            // 不是最后一个节点，设置汇总值
            sumKey.setValue(dimensionIndex, index);
            setSumValue(childNode, sumKey, sumRows);
            createChildrenNode(childNode, dimensionIndex + 1, mapDeep, sumLength, groups.get(index), globalDictionaries, sumRows, sumKey);
            // 当前节点的子节点添加完成，重置sumKey
            sumKey.setValue(dimensionIndex, -1);
        }
    }

    private static Map<Integer, List<Map.Entry<RowIndexKey, AggregatorValue[]>>> getGroupMap(
            List<Map.Entry<RowIndexKey, AggregatorValue[]>> rows, int dimensionIndex) {
        Map<Integer, List<Map.Entry<RowIndexKey, AggregatorValue[]>>> result = new LinkedHashMap<Integer, List<Map.Entry<RowIndexKey, AggregatorValue[]>>>();
        for (Map.Entry<RowIndexKey, AggregatorValue[]> entry : rows) {
            Integer key = entry.getKey().getValue(dimensionIndex);
            if (key == -1) {
                continue;
            }
            if (!result.containsKey(key)) {
                result.put(key, new ArrayList<Map.Entry<RowIndexKey, AggregatorValue[]>>());
            }
            result.get(key).add(entry);
        }
        return result;
    }

    static class RowIndexKeyComparator implements Comparator<RowIndexKey> {

        private List<Sort> indexSorts;

        public RowIndexKeyComparator(List<Sort> indexSorts) {
            this.indexSorts = indexSorts;
        }

        @Override
        public int compare(RowIndexKey o1, RowIndexKey o2) {
            assert indexSorts.size() != 0;
            // 这边暂时只考虑根据全局分组序号进行排序的情况
            for (int i = 0; i < indexSorts.size(); i++) {
                SortType type = indexSorts.get(i).getSortType();
                if (type == SortType.NONE) {
                    break;
                }
                int result = compareInt(o1.getValue(i), o2.getValue(i));
                result = type == SortType.ASC ? result : -result;
                if (result != 0) {
                    return result;
                }
            }
            return 1;
        }

        private static int compareInt(int x, int y) {
            return (x < y) ? -1 : ((x == y) ? 0 : 1);
        }
    }
}
