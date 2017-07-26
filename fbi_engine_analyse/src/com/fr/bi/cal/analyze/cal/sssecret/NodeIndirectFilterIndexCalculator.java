package com.fr.bi.cal.analyze.cal.sssecret;

import com.finebi.cube.api.ICubeValueEntryGetter;
import com.fr.bi.cal.analyze.cal.multithread.BIMultiThreadExecutor;
import com.fr.bi.cal.analyze.cal.multithread.BISingleThreadCal;
import com.fr.bi.cal.analyze.cal.sssecret.diminfo.MergeIteratorCreator;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.gvi.GroupValueIndexOrHelper;
import com.fr.bi.report.result.DimensionCalculator;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by 小灰灰 on 2017/3/15.
 */
public class NodeIndirectFilterIndexCalculator {
    private NoneDimensionGroup root;
    private ICubeValueEntryGetter[][] getters;
    private DimensionCalculator[][] columns;
    private MergeIteratorCreator[] mergeIteratorCreators;
    private boolean useRealData;
    //最后一个需要构建结构的维度
    private int lastIndirectFilterDimensionIndex;
    private BIMultiThreadExecutor executor;

    private GroupValueIndexOrHelper[] helpers;
    private ReentrantLock[] locks;

    public NodeIndirectFilterIndexCalculator(NoneDimensionGroup root, ICubeValueEntryGetter[][] getters, DimensionCalculator[][] columns,
                                             MergeIteratorCreator[] mergeIteratorCreators, boolean useRealData, int lastConstructedDimensionIndex, BIMultiThreadExecutor executor) {
        this.root = root;
        this.getters = getters;
        this.columns = columns;
        this.mergeIteratorCreators = mergeIteratorCreators;
        this.useRealData = useRealData;
        this.lastIndirectFilterDimensionIndex = lastConstructedDimensionIndex;
        this.executor = executor;
    }


    public GroupValueIndex[] cal() {
        if (columns.length == 0) {
            return null;
        }
        helpers = new GroupValueIndexOrHelper[root.getGvis().length];
        locks = new ReentrantLock[helpers.length];
        for (int i = 0; i < helpers.length; i++) {
            helpers[i] = new GroupValueIndexOrHelper();
            locks[i] = new ReentrantLock();
        }
        //在第一个维度过滤的情况下这边不用多线程，用了效率反而更低
        if (executor != null && lastIndirectFilterDimensionIndex > 0) {
            multiThreadBuild();
        } else {
            singleThreadBuild();
        }
        GroupValueIndex[] gvis = new GroupValueIndex[helpers.length];
        for (int i = 0; i < gvis.length; i++) {
            gvis[i] = helpers[i].compute();
        }
        return gvis;
    }

    private void singleThreadBuild() {
        cal(root, 0);
    }

    private void cal(NoneDimensionGroup childDimensionGroup, int level) {
        if (level > lastIndirectFilterDimensionIndex) {
            return;
        }
        SingleDimensionGroup rootGroup = childDimensionGroup.createSingleDimensionGroup(columns[level], getters[level], null, mergeIteratorCreators[level], useRealData);
        int index = 0;
        NoneDimensionGroup group = rootGroup.getChildDimensionGroup(index);
        while (group != NoneDimensionGroup.NULL) {
            if (level == lastIndirectFilterDimensionIndex) {
                sum(group);
            }
            if (level < lastIndirectFilterDimensionIndex) {
                cal(group, level + 1);
            }
            index++;
            group = rootGroup.getChildDimensionGroup(index);
        }
    }

    private void sum(NoneDimensionGroup childDimensionGroup) {
        GroupValueIndex[] gvis = childDimensionGroup.getGvis();
        for (int i = 0; i < helpers.length; i++) {
            helpers[i].add(gvis[i]);
        }
    }

    private void synchronizedSum(NoneDimensionGroup childDimensionGroup) {
        GroupValueIndex[] gvis = childDimensionGroup.getGvis();
        for (int i = 0; i < helpers.length; i++) {
            locks[i].lock();
            helpers[i].add(gvis[i]);
            locks[i].unlock();
        }
    }

    private void multiThreadBuild() {
        new MultiThreadBuilder().build();
    }


    private class MultiThreadBuilder {
        //每一层维度计算完成的数量
        private AtomicInteger[] count;
        //每一层维度被丢进线程池的数量
        private AtomicInteger[] size;

        public void build() {
            int dimensionSize = lastIndirectFilterDimensionIndex;
            count = new AtomicInteger[dimensionSize];
            size = new AtomicInteger[dimensionSize];
            for (int i = 0; i < dimensionSize; i++) {
                count[i] = new AtomicInteger(0);
                size[i] = new AtomicInteger(0);
            }
            SingleDimensionGroup rootGroup = root.createSingleDimensionGroup(columns[0], getters[0], null, mergeIteratorCreators[0], useRealData);
            int index = 0;
            NoneDimensionGroup group = rootGroup.getChildDimensionGroup(index);
            while (group != NoneDimensionGroup.NULL) {
                size[0].incrementAndGet();
                executor.add(new SingleChildCal(group, 0));
                index++;
                group = rootGroup.getChildDimensionGroup(index);
            }
            //如果多线程计算没有结束，就等结束
            if (!allCompleted()) {
                executor.wakeUp();
                synchronized (this) {
                    if (!allCompleted()) {
                        try {
                            this.wait();
                        } catch (InterruptedException e) {
                        }
                    }
                }
            }
        }

        private boolean allCompleted() {
            for (int i = 0; i < count.length; i++) {
                if (count[i].get() != size[i].get()) {
                    return false;
                }
            }
            return true;
        }

        private void checkComplete(int level) {
            if (currentLevelAllAdded(level)) {
                //完成了一个维度必须唤醒下线程，要不肯能会wait住死掉。
                executor.wakeUp();
                synchronized (this) {
                    //全部完成了就唤醒下wait的主线程
                    if (allCompleted()) {
                        this.notify();
                    }
                }
            }
        }

        //当前层的迭代器是否都执行完了
        private boolean currentLevelAllAdded(int level) {
            //最后一层没有迭代器
            if (level > lastIndirectFilterDimensionIndex) {
                return false;
            }
            //执行完的迭代器的数量不等于0，并且等于上一层的丢进线程池的计算数量。
            return count[level].get() != 0 && count[level].get() == size[level].get();
        }

        private class SingleChildCal implements BISingleThreadCal {
            private NoneDimensionGroup childDimensionGroup;
            private int level;

            public SingleChildCal(NoneDimensionGroup childDimensionGroup, int level) {
                this.childDimensionGroup = childDimensionGroup;
                this.level = level;
            }

            @Override
            public void cal() {
                calculate();
            }

            private void calculate() {
                try {
                    if (level < lastIndirectFilterDimensionIndex) {
                        SingleDimensionGroup rootGroup = childDimensionGroup.createSingleDimensionGroup(columns[level + 1], getters[level + 1], null, mergeIteratorCreators[level + 1], useRealData);
                        int index = 0;
                        NoneDimensionGroup group = rootGroup.getChildDimensionGroup(index);
                        while (group != NoneDimensionGroup.NULL) {
                            if (level + 1 == lastIndirectFilterDimensionIndex) {
                                synchronizedSum(group);
                            } else {
                                executor.add(new SingleChildCal(group, level + 1));
                                size[level + 1].incrementAndGet();
                            }
                            index++;
                            group = rootGroup.getChildDimensionGroup(index);
                        }
                    }
                } finally {
                    count[level].incrementAndGet();
                    checkComplete(level);
                }
            }
        }

    }

}
