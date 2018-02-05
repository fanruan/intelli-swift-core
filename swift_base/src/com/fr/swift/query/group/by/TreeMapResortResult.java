package com.fr.swift.query.group.by;

import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.structure.array.IntList;
import com.fr.swift.structure.iterator.IntListRowTraversal;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by 小灰灰 on 2017/3/28.
 */
public class TreeMapResortResult implements GroupByResult{
    //保存分组序号与行号的map
    private DictionaryEncodedColumn dictionaryEncodedColumn;
    private Iterator<Map.Entry<Integer, IntList>> iterator;

    protected TreeMapResortResult(DictionaryEncodedColumn dictionaryEncodedColumn, Map<Integer, IntList> indexArrayMap) {
        this.dictionaryEncodedColumn = dictionaryEncodedColumn;
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
