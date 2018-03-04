package com.fr.swift.adaptor.struct.node.trie;

import com.fr.swift.query.sort.Sort;
import com.fr.swift.result.KeyValue;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Lyon on 2018/3/4.
 */
public class TrieImpl implements Trie<KeyValue<int[], Number[]>, Number[]> {

    private int deep;
    private Number[] value;
    private Trie<KeyValue<int[], Number[]>, Number[]> parent;
    private Map<KeyValue<Integer, Number[]>, Trie<KeyValue<int[], Number[]>, Number[]>> childrenMap;
    List<Sort> sorts;

    public TrieImpl(int deep, List<Sort> sorts, Trie<KeyValue<int[], Number[]>, Number[]> parent, Number[] value) {
        this.deep = deep;
        this.sorts = sorts;
        childrenMap = new TreeMap<KeyValue<Integer, Number[]>, Trie<KeyValue<int[], Number[]>, Number[]>>(
                new KeyComparator(sorts));
        this.parent = parent;
        this.value = value;
    }

    /**
     * 插入
     * @param key
     * @param value
     * @return 返回根节点还是返回更新的节点好呢？
     */
    @Override
    public Trie<KeyValue<int[], Number[]>, Number[]> insert(KeyValue<int[], Number[]> key, Number[] value) {
        int[] index = key.getKey();
        if (childrenMap.containsKey(index[0])) {
            if (index.length == 1) {
                // 更新已经存在的节点的值，修改或者设置汇总值
                this.value = value;
                return this;
            }
            int[] copy = Arrays.copyOfRange(index, 1, index.length);
            return childrenMap.get(index[0]).insert(new KeyValue<int[], Number[]>(copy, key.getValue()), value);
        }
        Trie<KeyValue<int[], Number[]>, Number[]> trie = new TrieImpl(deep + 1, sorts, this, null);
        childrenMap.put(new KeyValue<Integer, Number[]>(index[0], value), trie);
        if (index.length == 1) {
            ((TrieImpl) trie).value = value;
            return trie;
        }
        int[] copy = Arrays.copyOfRange(index, 1, index.length);
        return trie.insert(new KeyValue<int[], Number[]>(copy, key.getValue()), value);
    }

    @Override
    public Trie<KeyValue<int[], Number[]>, Number[]> delete(KeyValue<int[], Number[]> key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Trie<KeyValue<int[], Number[]>, Number[]> getParent() {
        return parent;
    }

    @Override
    public Trie<KeyValue<int[], Number[]>, Number[]> get(KeyValue<int[], Number[]> key) {
        int[] index = key.getKey();
        if (index.length == 0) {
            return null;
        }
        if (childrenMap.containsKey(index[0])) {
            if (index.length == 1) {
                return childrenMap.get(index[0]);
            }
            int[] copy = Arrays.copyOfRange(index, 1, index.length);
            return childrenMap.get(index[0]).get(new KeyValue<int[], Number[]>(copy, key.getValue()));
        }
        return null;
    }

    @Override
    public Number[] getData() {
        return value;
    }

    @Override
    public boolean containsKey(KeyValue<int[], Number[]> key) {
        int[] index = key.getKey();
        if (index.length == 0) {
            return false;
        }
        if (childrenMap.containsKey(index[0])) {
            if (index.length == 1) {
                return true;
            }
            int[] copy = Arrays.copyOfRange(index, 1, index.length);
            return childrenMap.get(index[0]).containsKey(new KeyValue<int[], Number[]>(copy, key.getValue()));
        }
        return false;
    }

    private static class KeyComparator implements Comparator<KeyValue<Integer, Number[]>> {

        private List<Sort> sorts;

        public KeyComparator(List<Sort> sorts) {
            this.sorts = sorts;
        }

        @Override
        public int compare(KeyValue<Integer, Number[]> o1, KeyValue<Integer, Number[]> o2) {
            return 0;
        }
    }
}
