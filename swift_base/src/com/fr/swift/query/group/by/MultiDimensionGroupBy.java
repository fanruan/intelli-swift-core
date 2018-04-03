package com.fr.swift.query.group.by;

import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.result.KeyValue;
import com.fr.swift.result.RowIndexKey;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.structure.iterator.RowTraversal;
import com.fr.swift.structure.stack.ArrayStack;
import com.fr.swift.structure.stack.Stack;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Lyon on 2018/2/27.
 */
class MultiDimensionGroupBy implements Iterator<KeyValue<RowIndexKey<int[]>, RowTraversal>> {

    private static final int START_INDEX = DictionaryEncodedColumn.NOT_NULL_START_INDEX;

    private List<Column> dimensions;
    private DetailFilter detailFilter;
    private int[] cursor;
    private boolean[] asc;
    private Stack<GroupByResult> iterators;
    private KeyValue<RowIndexKey<int[]>, RowTraversal> next = null;
    private int[] groupIndexes;

    public MultiDimensionGroupBy(List<Column> dimensions, DetailFilter detailFilter, int[] cursor, boolean[] asc) {
        this.dimensions = dimensions;
        this.detailFilter = detailFilter;
        this.cursor = cursor;
        this.asc = asc;
        this.iterators = new ArrayStack<GroupByResult>(dimensions.size());
        this.groupIndexes = new int[dimensions.size()];
        Arrays.fill(groupIndexes, -1);
        init();
    }

    private void init() {
        RowTraversal traversal = detailFilter.createFilterIndex();
        next = new KeyValue<RowIndexKey<int[]>, RowTraversal>(new RowIndexKey(createRowIndexKey()), traversal);
        // 没有维度的情况下直接返回，当前迭代器只有一个汇总行，iterators为空
        if (dimensions.size() == 0) {
            return;
        }
        GroupByResult groupByResult = GroupBy.createGroupByResult(dimensions.get(0), traversal,
                getStartIndex(0), asc[0]);
        iterators.push(groupByResult);
    }

    private int getStartIndex(int dimensionIndex) {
        int index = cursor[dimensionIndex];
        if (index != START_INDEX) {
            // 分组定位的游标只能用一次
            cursor[dimensionIndex] = START_INDEX;
        }
        return index;
    }

    private void updateGroupIndex(int dimensionIndex, int groupIndex) {
        groupIndexes[dimensionIndex] = groupIndex;
    }

    private int[] createRowIndexKey() {
        return Arrays.copyOf(groupIndexes, groupIndexes.length);
    }

    @Override
    public boolean hasNext() {
        return next != null;
    }

    /**
     * 这边通过stack结构避免递归写法，其他地方涉及树结构的遍历也可以借鉴这边的处理方式
     * @return
     */
    @Override
    public KeyValue<RowIndexKey<int[]>, RowTraversal> next() {
        KeyValue<RowIndexKey<int[]>, RowTraversal> old = next;
        while (!iterators.isEmpty()) {
            GroupByResult it = iterators.peek();
            if (it.hasNext()) {
                GroupByEntry entry = it.next();
                RowTraversal traversal = entry.getTraversal();
                // 更新当前维度的groupIndex
                updateGroupIndex(iterators.size() - 1, entry.getIndex());
                next = new KeyValue<RowIndexKey<int[]>, RowTraversal>(new RowIndexKey(createRowIndexKey()), traversal);
                if (iterators.size() != iterators.limit()) {
                    // 要继续group by下一个维度
                    GroupByResult result = GroupBy.createGroupByResult(dimensions.get(iterators.size()), traversal,
                            getStartIndex(iterators.size()), asc[iterators.size()]);
                    iterators.push(result);
                }
                break;
            } else {
                // 更新当前维度的索引迭代器
                // 首先重置一下当前维度的groupIndex
                updateGroupIndex(iterators.size() - 1, -1);
                // 这边只要把当前维度的迭代器丢弃，继续执行循环体就行了
                iterators.pop();
            }
        }
        // iterators可能一开始就为空（没有维度的情况），也可能是group by完了
        if (iterators.isEmpty()) {
            next = null;
        }
        return old;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
