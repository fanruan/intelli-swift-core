package com.fr.bi.cal.analyze.cal.sssecret.mergeiter;

import com.fr.bi.cal.analyze.cal.sssecret.MetricMergeResult;
import com.fr.bi.cal.analyze.cal.sssecret.MetricMergeResultWithGroupIndex;
import com.fr.bi.cal.analyze.exception.TerminateExecutorException;
import com.fr.bi.stable.engine.cal.DimensionIterator;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.io.newio.NIOConstant;
import com.fr.stable.collections.array.IntArray;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by 小灰灰 on 2016/12/30.
 */
public class MergeIterator implements Iterator<MetricMergeResult> {
    protected MetricMergeResult next;
    private DimensionIterator[] iterators;
    protected GroupValueIndex[] gvis;
    private Comparator c;
    private int sumLength;
    private Map.Entry<Object, GroupValueIndex>[] entries;
    private boolean returnResultWithGroupIndex = false;

    public MergeIterator(DimensionIterator[] iterators, GroupValueIndex[] gvis, Comparator c, int sumLength) {
        this.iterators = iterators;
        this.gvis = gvis;
        this.c = c;
        this.sumLength = sumLength;
        initEntries();
    }


    @Override
    public boolean hasNext() {
        moveNext();
        return next != null;
    }

    @Override
    public MetricMergeResult next() {
        return next;
    }

    @Override
    public void remove() {
        //do nothing
    }

    protected void reSetGroupValueIndex(MetricMergeResult result) {
        int[] group = ((MetricMergeResultWithGroupIndex) result).getGroupIndex();
        GroupValueIndex[] groupValueIndices = new GroupValueIndex[group.length];
        for (int i = 0; i < group.length; i++) {
            if (group[i] != NIOConstant.INTEGER.NULL_VALUE) {
                groupValueIndices[i] = iterators[i].getGroupValueIndexByGroupIndex(group[i]);
                if (!iterators[i].isReturnFinalGroupValueIndex()) {
                    groupValueIndices[i] = groupValueIndices[i].and(this.gvis[i]);
                }
            }
        }
        result.setGvis(groupValueIndices);
    }

    protected void setReturnResultWithGroupIndex(boolean returnResultWithGroupIndex) {
        this.returnResultWithGroupIndex = returnResultWithGroupIndex;
    }

    protected boolean canRelease() {
        for (DimensionIterator iterator : iterators) {
            if (!iterator.canReGainGroupValueIndex()) {
                return false;
            }
        }
        return true;
    }

    private void initEntries() {
        entries = new Map.Entry[iterators.length];
        for (int i = 0; i < entries.length; i++) {
            if (iterators[i].hasNext()) {
                entries[i] = iterators[i].next();
            }
        }
    }

    private void moveEntries(IntArray array) {
        for (int i = 0; i < array.size; i++) {
            int index = array.get(i);
            do {
                if (iterators[index].hasNext()) {
                    entries[index] = iterators[index].next();
                } else {
                    entries[index] = null;
                }
            }
            while (entries[index] != null && entries[index].getValue().isAllEmpty());
        }
    }

    protected void moveNext() {
        do {
            IntArray array = new IntArray();
            gvis = new GroupValueIndex[iterators.length];
            Object minValue = getMinValuePositions(array, gvis);
            //设置mergenode
            if (minValue == null) {
                next = null;
            } else {
                int[] groupIndex = null;
                if (returnResultWithGroupIndex) {
                    groupIndex = new int[gvis.length];
                }
                for (int i = 0; i < gvis.length; i++) {
                    if (this.gvis[i] == null || gvis[i] == null) {
                        gvis[i] = null;
                        if (returnResultWithGroupIndex) {
                            groupIndex[i] = NIOConstant.INTEGER.NULL_VALUE;
                        }
                    } else {
                        if (!iterators[i].isReturnFinalGroupValueIndex()) {
                            andGVI(gvis, i);
                        }
                        if (returnResultWithGroupIndex) {
                            groupIndex[i] = iterators[i].getCurrentGroup();
                        }
                    }
                }
                next = returnResultWithGroupIndex ? new MetricMergeResultWithGroupIndex(c, minValue, sumLength, gvis, groupIndex) : new MetricMergeResult(c, minValue, sumLength, gvis);
            }
            moveEntries(array);
        }
        while (isAllEmpty(gvis) && next != null);
    }

    private void andGVI(GroupValueIndex[] gvis, int i) {
        if (iterators[i].canReGainGroupValueIndex()) {
            gvis[i] = gvis[i].and(this.gvis[i]);
        } else {
            gvis[i] = gvis[i].AND(this.gvis[i]);
        }
    }

    private boolean isAllEmpty(GroupValueIndex[] para) throws TerminateExecutorException {
        for (GroupValueIndex gvi : para) {
            if (!indexIsAllEmpty(gvi)) {
                return false;
            }
        }
        return true;
    }

    private boolean indexIsAllEmpty(GroupValueIndex gvi) {
        return gvi == null || gvi.isAllEmpty();
    }

    //获取最小值，对应的索引，以及迭代器的序号
    private Object getMinValuePositions(IntArray array, GroupValueIndex[] gvis) {
        Object minValue = null;
        for (int i = 0; i < entries.length; i++) {
            Map.Entry<Object, GroupValueIndex> entry = entries[i];
            if (entry != null) {
                Object currentValue = entry.getKey();
                if (minValue == null) {
                    minValue = currentValue;
                    gvis[i] = entry.getValue();
                    array.add(i);
                } else {
                    int result = c.compare(minValue, currentValue);
                    if (result == 0) {
                        gvis[i] = entry.getValue();
                        array.add(i);
                    }
                    if (result > 0) {
                        minValue = currentValue;
                        Arrays.fill(gvis, null);
                        array.clear();
                        array.add(i);
                        gvis[i] = entry.getValue();
                    }
                }
            }
        }
        return minValue;
    }

    public static DimensionIterator EMPTY = new DimensionIterator() {
        @Override
        public int getCurrentGroup() {
            return 0;
        }

        @Override
        public boolean canReGainGroupValueIndex() {
            return false;
        }

        @Override
        public boolean isReturnFinalGroupValueIndex() {
            return false;
        }

        @Override
        public GroupValueIndex getGroupValueIndexByGroupIndex(int groupIndex) {
            return null;
        }

        @Override
        public void remove() {
            //do nothing
        }

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public Map.Entry<Object, GroupValueIndex> next() {
            return null;
        }

    };
}
