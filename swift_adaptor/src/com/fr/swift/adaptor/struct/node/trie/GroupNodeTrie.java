package com.fr.swift.adaptor.struct.node.trie;

import com.fr.swift.query.sort.Sort;
import com.fr.swift.query.sort.SortType;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 类似字符trie
 * 处理没有指标排序的场景
 * Created by Lyon on 2018/3/4.
 */
public class GroupNodeTrie implements Trie<int[], Integer, Number[]> {

    private static final int EMPTY_KEY = -1;

    private int deep;
    private Integer key;
    private Number[] value;
    private Trie<int[], Integer, Number[]> parent;
    private Map<Integer, Trie<int[], Integer, Number[]>> childrenMap;
    private List<Sort> sorts;

    public GroupNodeTrie(int deep, Integer key, Number[] value, Trie<int[], Integer, Number[]> parent, List<Sort> sorts) {
        this.deep = deep;
        this.key = key;
        this.value = value;
        this.parent = parent;
        this.childrenMap = new TreeMap<Integer, Trie<int[], Integer, Number[]>>(new TrieComparator(sorts, deep + 1));
        this.sorts = sorts;
    }

    @Override
    public void insert(int[] key, Number[] value) {
        insert(key, 0, value);
    }

    private void insert(int[] key, int index, Number[] value) {
        if (index >= key.length || key[index] == EMPTY_KEY) {
            // 设置节点的值
            this.value = value;
            return;
        }
        if (childrenMap.containsKey(key[index])) {
            Trie<int[], Integer, Number[]> trie = childrenMap.get(key[index]);
            ((GroupNodeTrie) trie).insert(key, index + 1, value);
            return;
        }
        Trie<int[], Integer, Number[]> trie = new GroupNodeTrie(deep + 1, key[index], null, this, sorts);
        childrenMap.put(key[index], trie);
        ((GroupNodeTrie) trie).insert(key, index + 1, value);
    }

    @Override
    public void delete(int[] key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Trie<int[], Integer, Number[]> parent() {
        return parent;
    }

    @Override
    public Trie<int[], Integer, Number[]> get(int[] key) {
        if (key.length == 0 || key[0] == EMPTY_KEY) {
            return this;
        }
        if (childrenMap.containsKey(key[0])) {
            int[] copy = Arrays.copyOfRange(key, 1, key.length);
            return childrenMap.get(key[0]).get(copy);
        }
        return null;
    }

    @Override
    public Number[] getValue() {
        return value;
    }

    @Override
    public void setValue(Number[] value) {
        this.value = value;
    }

    @Override
    public Integer getKey() {
        return key;
    }

    @Override
    public boolean containsKey(int[] key) {
        if (key.length == 0 || key[0] == EMPTY_KEY) {
            return true;
        }
        if (childrenMap.containsKey(key[0])) {
            int[] copy = Arrays.copyOfRange(key, 1, key.length);
            return childrenMap.get(key[0]).containsKey(copy);
        }
        return false;
    }

    @Override
    public int deep() {
        return deep;
    }

    @Override
    public Iterator<Map.Entry<Integer, Trie<int[], Integer, Number[]>>> iterator() {
        return childrenMap.entrySet().iterator();
    }

    private static class TrieComparator implements Comparator<Integer> {
        private List<Sort> sorts;
        private int deep;

        public TrieComparator(List<Sort> sorts, int deep) {
            this.sorts = sorts;
            this.deep = deep;
        }

        @Override
        public int compare(Integer o1, Integer o2) {
            int result = compareInt(o1, o2);
            if (sorts != null && sorts.size() > deep) {
                return sorts.get(deep).getSortType() == SortType.ASC ? result : -result;
            }
            return result;
        }

        private static int compareInt(int x, int y) {
            return (x < y) ? -1 : ((x == y) ? 0 : 1);
        }
    }
}
