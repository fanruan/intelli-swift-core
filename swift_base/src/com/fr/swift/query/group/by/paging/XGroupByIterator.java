package com.fr.swift.query.group.by.paging;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.result.KeyValue;
import com.fr.swift.result.RowIndexKey;
import com.fr.swift.structure.iterator.RowTraversal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Lyon on 2018/3/21.
 */
public class XGroupByIterator implements Iterator<KeyValue<RowIndexKey, List<KeyValue<RowIndexKey, RowTraversal>>>> {

    // iterable用来reset迭代器（重新new一个iterator）
    private Iterable<KeyValue<RowIndexKey, RowTraversal>> colItCreator;

    private Iterator<KeyValue<RowIndexKey, RowTraversal>> rowIt;
    private Iterator<KeyValue<RowIndexKey, RowTraversal>> colIt;

    public XGroupByIterator(Iterable<KeyValue<RowIndexKey, RowTraversal>> rowItCreator,
                            Iterable<KeyValue<RowIndexKey, RowTraversal>> colItCreator) {
        this.colItCreator = colItCreator;
        this.rowIt = rowItCreator.iterator();
        this.colIt = colItCreator.iterator();
    }

    @Override
    public boolean hasNext() {
        return rowIt.hasNext();
    }

    /**
     * 每次计算交叉表的一行结果
     * 表头的分页在表头索引迭代器colIt里面处理
     * 考虑到多个segment结果合并，先根据行表头合并，再合并列表头
     * @return
     */
    @Override
    public KeyValue<RowIndexKey, List<KeyValue<RowIndexKey, RowTraversal>>> next() {
        KeyValue<RowIndexKey, RowTraversal> rowKV = rowIt.next();
        ImmutableBitMap bitMap = rowKV.getValue().toBitMap();
        List<KeyValue<RowIndexKey, RowTraversal>> keyValues = new ArrayList<KeyValue<RowIndexKey, RowTraversal>>();
        while (colIt.hasNext()) {
            KeyValue<RowIndexKey, RowTraversal> colKV = colIt.next();
            keyValues.add(new KeyValue<RowIndexKey, RowTraversal>(colKV.getKey(),
                    // 这边是否选择bitmap计算，可以另外做优化
                    bitMap.getAnd(colKV.getValue().toBitMap())));
        }
        // 重置表头索引迭代器colIt。是否缓存表头的索引可以在colItCreator里面优化
        colIt = colItCreator.iterator();
        return new KeyValue<RowIndexKey, List<KeyValue<RowIndexKey, RowTraversal>>>(rowKV.getKey(), keyValues);
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
