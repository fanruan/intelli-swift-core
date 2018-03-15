package com.fr.swift.adaptor.struct.node;

import com.finebi.conf.structure.result.table.BIGroupNode;
import com.fr.swift.adaptor.struct.node.trie.Trie;
import com.fr.swift.adaptor.struct.node.trie.GroupNodeTrie;
import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.query.sort.Sort;
import com.fr.swift.result.GroupByResultSet;
import com.fr.swift.result.KeyValue;
import com.fr.swift.result.RowIndexKey;
import com.fr.swift.structure.queue.FIFOQueue;
import com.fr.swift.structure.queue.LinkedListFIFOQueue;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class BIGroupNodeFactory {

    // 构建没有指标排序的node结构
    public static BIGroupNode create(GroupByResultSet resultSet) {
        Iterator<KeyValue<RowIndexKey, AggregatorValue[]>> iterator = resultSet.getRowResultIterator();
        List<Map<Integer, Object>> dictionaries = resultSet.getGlobalDictionaries();
        List<Sort> sorts = resultSet.getIndexSorts();
        Trie<int[], Integer, Number[]> trie = new GroupNodeTrie(-1, null, null, null, sorts);
        while (iterator.hasNext()) {
            KeyValue<RowIndexKey, AggregatorValue[]> keyValue = iterator.next();
            Number[] value = getValues(keyValue.getValue());
            int[] key = keyValue.getKey().getKey();
            trie.insert(key, value);
        }
        // convert trie to BIGroupNode
        FIFOQueue<SwiftBIGroupNode> nodeFIFOQueue = new LinkedListFIFOQueue<SwiftBIGroupNode>();
        FIFOQueue<Trie<int[], Integer, Number[]>> trieFIFOQueue = new LinkedListFIFOQueue<Trie<int[], Integer, Number[]>>();
        SwiftBIGroupNode rootNode = new SwiftBIGroupNode(getData(trie, dictionaries), trie.getValue());
        nodeFIFOQueue.add(rootNode);
        trieFIFOQueue.add(trie);
        while (!trieFIFOQueue.isEmpty()) {
            // 广度优先遍历
            Trie<int[], Integer, Number[]> t = trieFIFOQueue.remove();
            SwiftBIGroupNode n = nodeFIFOQueue.remove();
            Iterator<Map.Entry<Integer, Trie<int[], Integer, Number[]>>> entryIterator = t.iterator();
            if (!entryIterator.hasNext()) {
                trieFIFOQueue.remove();
                nodeFIFOQueue.remove();
            }
            while (entryIterator.hasNext()) {
                Trie<int[], Integer, Number[]> child = entryIterator.next().getValue();
                SwiftBIGroupNode childNode = new SwiftBIGroupNode(getData(child, dictionaries), child.getValue());
                n.addChild(childNode);
                nodeFIFOQueue.add(childNode);
                trieFIFOQueue.add(child);
            }
        }
        return rootNode;
    }

    private static Object getData(Trie<int[], Integer, Number[]> trie, List<Map<Integer, Object>> dictionaries) {
        return dictionaries.get(trie.deep()).get(trie.getKey());
    }

    private static Number[] getValues(AggregatorValue[] aggregatorValues) {
        Number[] values = new Number[aggregatorValues.length];
        for (int i = 0; i < values.length; i++) {
            values[i] = aggregatorValues[i].calculate();
        }
        return values;
    }
}