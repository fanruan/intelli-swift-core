package com.fr.swift.segment.column.impl.multi;

import com.fr.swift.query.aggregator.Combiner;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.segment.column.impl.base.AbstractDictColumn;
import com.fr.swift.source.ColumnTypeConstants.ClassType;
import com.fr.swift.structure.queue.SortedListMergingUtils;
import com.fr.swift.util.IoUtil;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * @author anchore
 * @date 2019/7/11
 */
class ReadonlyMultiDictColumn<T> extends AbstractDictColumn<T> {
    private List<DictionaryEncodedColumn<T>> dicts;

    private int[] offsets;
    /**
     * (全局字典序 -> 第几块seg) -> 局部字典序
     */
    private List<int[]> globalToLocalIndex = new ArrayList<int[]>();
    /**
     * (第几块seg, 局部字典序) -> 全局字典序
     */
    private int[][] localToGlobalIndex;

    public ReadonlyMultiDictColumn(List<DictionaryEncodedColumn<T>> dicts, int[] offsets) {
        this.dicts = dicts;
        this.offsets = offsets;
        init();
    }

    private void init() {
        int segCount = dicts.size();

        localToGlobalIndex = new int[segCount][];
        List<Iterator<T>> dictItrs = new ArrayList<Iterator<T>>(segCount);
        for (int i = 0; i < segCount; i++) {
            DictionaryEncodedColumn<T> dict = dicts.get(i);
            localToGlobalIndex[i] = new int[dict.size()];
            dictItrs.add(new DictAdapter<T>(dict).iterator());
        }

        Iterator<T> globalDictValues = SortedListMergingUtils.mergeIterator(dictItrs, getComparator(), new EmptyCombiner<T>());
        int globalIndex = 0;
        globalToLocalIndex.add(new int[segCount]);
        while (globalDictValues.hasNext()) {
            globalIndex++;

            T val = globalDictValues.next();

            int[] localIndices = new int[segCount];
            globalToLocalIndex.add(localIndices);

            for (int i = 0; i < segCount; i++) {
                DictionaryEncodedColumn<T> dict = dicts.get(i);
                int localIndex = dict.getIndex(val);
                localIndices[i] = localIndex;
                if (localIndex != -1) {
                    localToGlobalIndex[i][localIndex] = globalIndex;
                }
            }
        }
    }

    int[] getLocalIndices(int globalIndex) {
        return globalToLocalIndex.get(globalIndex);
    }

    @Override
    public int size() {
        return globalToLocalIndex.size();
    }

    @Override
    public T getValue(int index) {
        if (index == 0) {
            return null;
        }
        int localIndex = getLocalIndices(index)[0];
        return dicts.get(0).getValue(localIndex);
    }

    @Override
    public int getIndex(Object value) {
        if (value == null) {
            return 0;
        }
        // 不常用，实时算吧
        for (int i = 0; i < dicts.size(); i++) {
            DictionaryEncodedColumn<T> dict = dicts.get(i);
            int localIndex = dict.getIndex(value);
            if (localIndex != -1) {
                return localToGlobalIndex[i][localIndex];
            }
        }
        return -1;
    }

    @Override
    public int getIndexByRow(int row) {
        for (int i = 0; i < offsets.length - 1; i++) {
            if (row >= offsets[i] && row < offsets[i + 1]) {
                int localIndex = dicts.get(i).getIndexByRow(row - offsets[i]);
                return localToGlobalIndex[i][localIndex];
            }
        }
        return -1;
    }

    @Override
    public Comparator<T> getComparator() {
        return dicts.get(0).getComparator();
    }

    @Override
    public ClassType getType() {
        return dicts.get(0).getType();
    }

    @Override
    public boolean isReadable() {
        return true;
    }

    @Override
    public void release() {
        IoUtil.release(dicts.toArray(new DictionaryEncodedColumn[0]));
    }

    private static class DictAdapter<T> extends AbstractList<T> {

        DictionaryEncodedColumn<T> dict;

        private DictAdapter(DictionaryEncodedColumn<T> dict) {
            this.dict = dict;
        }

        @Override
        public T get(int index) {
            return dict.getValue(index + 1);
        }

        @Override
        public int size() {
            return dict.size() - 1;
        }

    }

    private static class EmptyCombiner<T> implements Combiner<T> {
        @Override
        public void combine(T current, T other) {
        }
    }

    @Override
    public int globalSize() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getGlobalIndexByIndex(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getGlobalIndexByRow(int row) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Putter<T> putter() {
        throw new UnsupportedOperationException();
    }
}