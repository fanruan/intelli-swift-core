package com.fr.bi.cal.analyze.cal.sssecret;

import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.cal.analyze.cal.index.loader.TargetAndKey;
import com.fr.bi.cal.analyze.cal.multithread.BIMultiThreadExecutor;
import com.fr.bi.cal.analyze.cal.multithread.BISingleThreadCal;
import com.fr.bi.cal.analyze.cal.multithread.SummaryCall;
import com.fr.bi.cal.analyze.cal.result.Node;
import com.fr.bi.cal.analyze.cal.result.NodeAndPageInfo;
import com.fr.bi.cal.analyze.cal.result.NodeUtils;
import com.fr.bi.cal.analyze.cal.result.operator.Operator;
import com.fr.bi.stable.gvi.GroupValueIndex;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class GroupUtils {


    public static NodeAndPageInfo createNextPageMergeNode(NodeDimensionIterator iterator, Operator op, boolean showSum, boolean shouldSetIndex, int sumLength, BIMultiThreadExecutor executor) {

        return createMergePageNode(iterator, op, showSum, shouldSetIndex, sumLength, executor);
    }

    private static NodeAndPageInfo createMergePageNode(NodeDimensionIterator iterator, Operator op, boolean showSum, boolean shouldSetIndex, int sumLength, BIMultiThreadExecutor executor) {

        Node node = new Node(sumLength);
        GroupConnectionValue gc = iterator.next();
        if (gc == null) {
            return new NodeAndPageInfo(node, iterator);
        }
        AtomicInteger count = new AtomicInteger(0);
        AtomicInteger size = new AtomicInteger(0);
        addSummaryValue(node, gc, showSum, shouldSetIndex, executor, count, size);
        List<GroupConnectionValue> display = gc.getDisplayGroupConnectionValue();
        Iterator<GroupConnectionValue> displayIter = display.iterator();
        if (displayIter.hasNext()) {
            gc = displayIter.next();
        }
        addSummaryValue(node, gc, showSum, shouldSetIndex, executor, count, size);
        while (!op.isPageEnd(gc) && gc != null && gc.getChild() != null) {
            GroupConnectionValue gcvChild = gc.getChild();
            Node parent = node;
            while (gcvChild != null) {
                Object data = gcvChild.getData();
                Node child = parent.getChild(data);
                if (child == null) {
                    child = new Node(data, sumLength);
                    parent.addChild(child);
                }
                parent = child;
                addSummaryValue(child, gcvChild, showSum, shouldSetIndex, executor, count, size);
                gcvChild = gcvChild.getChild();
            }
            op.addRow();
            // 先从一行产生的迭代器中进行获取下一行的值,如果一行产生的迭代器产生的行值已经获取了完成,则再产生下一行以及其迭代器
            if (displayIter.hasNext()) {
                gc = displayIter.next();
            } else {
                iterator.moveNext();
                gc = iterator.next();
                while (gc != null) {
                    displayIter = gc.getDisplayGroupConnectionValue().iterator();
                    if (displayIter.hasNext()) {
                        gc = displayIter.next();
                        break;
                    } else {
                        gc = iterator.next();
                    }
                }
            }
        }
        boolean completed = executor == null || count.get() == size.get();
        if (!completed) {
            //没完成的话要唤醒下executor，以防有加到executor里wait住的。
            executor.wakeUp();
            synchronized (count) {
                if (!completed) {
                    try {
                        count.wait();
                    } catch (InterruptedException e) {
                    }
                }
            }
        }
        iterator.pageEnd();
        NodeUtils.setSiblingBetweenFirstAndLastChild(node);
        return new NodeAndPageInfo(node, iterator);
    }

    /**
     * 获取维度对应指标的汇总值
     *
     * @param node
     * @param shouldSetIndex
     */
    private static void addSummaryValue(Node node, GroupConnectionValue gcv, boolean showSum, boolean shouldSetIndex, BIMultiThreadExecutor executor,  AtomicInteger count, AtomicInteger size) {

        if (showSum || gcv.getChild() == null) {
            NoneDimensionGroup group = gcv.getCurrentValue();
            if (group != null) {
                List<TargetAndKey>[] summaryLists = group.getSummaryLists();
                node.setSummaryValue(group.getSummaryValue());
                GroupValueIndex[] gvis = group.getGvis();
                if (gvis == null) {
                    return;
                }
                ICubeTableService[] tis = group.getTis();
                for (int i = 0; i < summaryLists.length; i++) {
                    List<TargetAndKey> targetAndKeys = summaryLists[i];
                    for (TargetAndKey targetAndKey : targetAndKeys) {
                        if (node.getSummaryValue(targetAndKey.getTargetGettingKey()) == null) {
                            BISingleThreadCal singleThreadCal = createSingleThreadCal(tis[i], node, targetAndKey, gvis[i], group.getLoader(), shouldSetIndex, count, size);
                            if (executor != null) {
                                executor.add(singleThreadCal);
                                size.incrementAndGet();
                            } else {
                                singleThreadCal.cal();
                            }
                        }
                        if (shouldSetIndex && node.getTargetIndex(targetAndKey.getTargetGettingKey()) == null) {
                            //会出现node copy了summaryValue但是没有index的情况，这时候交叉表就出问题了，需要设置下gvi
                            node.setTargetIndex(targetAndKey.getTargetGettingKey(), gvis[i]);
                        }
                    }
                }
            }
        }
    }

    private static BISingleThreadCal createSingleThreadCal(ICubeTableService ti, Node node, TargetAndKey targetAndKey, GroupValueIndex gvi, ICubeDataLoader loader, boolean shouldSetIndex, AtomicInteger count, AtomicInteger size) {

        if (shouldSetIndex) {
            return new SummaryIndexCal(ti, node, targetAndKey, gvi, loader, count, size);
        } else {
            return new SummaryCountCall(ti, node, targetAndKey, gvi, loader, count, size);
        }
    }


    static class SummaryCountCall  extends SummaryCall {
        AtomicInteger count;
        AtomicInteger size;
        public SummaryCountCall(ICubeTableService ti, Node node, TargetAndKey targetAndKey, GroupValueIndex gvi, ICubeDataLoader loader, AtomicInteger count, AtomicInteger size) {
            super(ti, node, targetAndKey, gvi, loader);
            this.count = count;
            this.size = size;
        }

        @Override
        public void cal() {
            try{
                super.cal();
            } finally {
                checkComplete();
            }
        }

        void checkComplete() {
            if (count.incrementAndGet() == size.get()) {
                synchronized (count) {
                    count.notify();
                }
            }
        }
    }

    static class SummaryIndexCal  extends SummaryCountCall {
        public SummaryIndexCal(ICubeTableService ti, Node node, TargetAndKey targetAndKey, GroupValueIndex gvi, ICubeDataLoader loader, AtomicInteger count, AtomicInteger size) {
            super(ti, node, targetAndKey, gvi, loader, count, size);
        }

        @Override
        public void cal() {
            node.setTargetIndex(targetAndKey.getTargetGettingKey(), gvi);
            super.cal();
        }
    }
}