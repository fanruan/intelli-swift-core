package com.fr.swift.query.group.by;

import com.fr.swift.structure.array.IntList;
import com.fr.swift.structure.iterator.IntListRowTraversal;

import java.util.Iterator;
import java.util.Map;

/**
 * @author 小灰灰
 * @date 2017/3/28
 */
public class TreeMapResortResult implements GroupByResult {
    private Iterator<Map.Entry<Integer, IntList>> iterator;

    protected TreeMapResortResult(Map<Integer, IntList> indexArrayMap) {
        this.iterator = indexArrayMap.entrySet().iterator();
    }

    @Override
    public void remove() {
        iterator.remove();
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public GroupByEntry next() {
        Map.Entry<Integer, IntList> entry = iterator.next();
        return new IntListGroupByEntry(entry.getKey(), new IntListRowTraversal(entry.getValue()));
    }
}
