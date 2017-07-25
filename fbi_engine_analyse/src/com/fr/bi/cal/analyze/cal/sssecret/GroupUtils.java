package com.fr.bi.cal.analyze.cal.sssecret;

import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.cal.analyze.cal.index.loader.TargetAndKey;
import com.fr.bi.cal.analyze.cal.multithread.BIMultiThreadExecutor;
import com.fr.bi.cal.analyze.cal.multithread.BISingleThreadCal;
import com.fr.bi.cal.analyze.cal.multithread.SummaryCall;
import com.fr.bi.cal.analyze.cal.result.NodeCreator;
import com.fr.bi.cal.analyze.cal.result.Node;
import com.fr.bi.cal.analyze.cal.result.NodeAndPageInfo;
import com.fr.bi.cal.analyze.cal.result.NodeUtils;
import com.fr.bi.cal.analyze.cal.result.operator.Operator;
import com.fr.bi.stable.gvi.GroupValueIndex;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class GroupUtils {


    /**
     *
     * @param iterator
     * @param op
     * @param showSum 是否显示汇总（只控制是否计算索引）
     * @param shouldSetIndex
     * @param shouldSum 是否需要计算汇总值（控制是否算指标的值）
     * @param nodeCreator
     * @param sumLength
     * @param executor
     * @return
     */
    public static NodeAndPageInfo createNextPageMergeNode(NodeDimensionIterator iterator, Operator op, boolean showSum, boolean shouldSetIndex, boolean shouldSum, NodeCreator nodeCreator, int sumLength, BIMultiThreadExecutor executor) {

        return createMergePageNode(iterator, op, showSum, shouldSetIndex, shouldSum, nodeCreator, sumLength, executor);
    }

    private static NodeAndPageInfo createMergePageNode(NodeDimensionIterator iterator, Operator op, boolean showSum, boolean shouldSetIndex, boolean shouldSum, NodeCreator nodeCreator, int sumLength, BIMultiThreadExecutor executor) {

        Node node = nodeCreator.createNode(sumLength);
        GroupConnectionValue gc = iterator.next();
        if (gc == null) {
            return new NodeAndPageInfo(node, iterator);
        }
        Status calculated = new Status();
        AtomicInteger count = new AtomicInteger(0);
        AtomicInteger size = new AtomicInteger(0);
        Status allAdded = new Status();
        addSummaryValue(node, gc, nodeCreator, showSum, shouldSetIndex, shouldSum, executor, count, size, calculated, allAdded);
        // BI-6991 根节点才需要依靠showSum属性进行显示汇总,子节点需要一直进行显示
        // pony 这边问了交互了，说一开始不好做，就定了都展示了，现在能做出来不展示了，就不展示了
        addChild(iterator, op, showSum, shouldSetIndex, shouldSum, nodeCreator, sumLength, executor, node, gc, count, size, calculated, allAdded);
        allAdded.setCompleted();
        if (executor == null || count.get() == size.get()){
            calculated.setCompleted();
        }
        if (!calculated.isCompleted() && shouldSum) {
            //没完成的话要唤醒下executor，以防有加到executor里wait住的。
            executor.wakeUp();
            synchronized (size) {
                //这边需要再判断一次，全部完成会把状态设置成true，之前内部变量不能申明volitile，就把这个判断取消了- -！
                if (!calculated.isCompleted()) {
                    try {
                        size.wait();
                    } catch (InterruptedException e) {
                    }
                }
            }
        }
        iterator.pageEnd();
        NodeUtils.setSiblingBetweenFirstAndLastChild(node);
        return new NodeAndPageInfo(node, iterator);
    }

    private static void addChild(NodeDimensionIterator iterator, Operator op, boolean showSum, boolean shouldSetIndex, boolean shouldSum, NodeCreator nodeCreator, int sumLength, BIMultiThreadExecutor executor, Node node, GroupConnectionValue gc, AtomicInteger count, AtomicInteger size, Status status, Status allAdded) {
        List<GroupConnectionValue> display = gc.getDisplayGroupConnectionValue();
        Iterator<GroupConnectionValue> displayIter = display.iterator();
        if (displayIter.hasNext()) {
            gc = displayIter.next();
        }
        while (!op.isPageEnd(gc) && gc != null && gc.getChild() != null) {
            GroupConnectionValue gcvChild = gc.getChild();
            Node parent = node;
            while (gcvChild != null) {
                Object data = gcvChild.getData();
                Node child = parent.getChild(data);
                if (child == null) {
                    child = nodeCreator.createNode(data, sumLength);
                    parent.addChild(child);
                }
                parent = child;
                addSummaryValue(child, gcvChild, nodeCreator, showSum, shouldSetIndex, shouldSum, executor, count, size, status, allAdded);
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
    }

    /**
     * 获取维度对应指标的汇总值
     *
     * @param node
     * @param shouldSetIndex
     */
    private static void addSummaryValue(Node node, GroupConnectionValue gcv, NodeCreator creator, boolean showSum, boolean shouldSetIndex, boolean shouldSum, BIMultiThreadExecutor executor,  AtomicInteger count, AtomicInteger size,  Status calculated, Status allAdded) {

        if (showSum || gcv.getChild() == null) {
            NoneDimensionGroup group = gcv.getCurrentValue();
            if (group != null) {
                List<TargetAndKey>[] summaryLists = group.getSummaryLists();
                creator.copySumValue(node, group.getMergeResult());
                GroupValueIndex[] gvis = group.getGvis();
                if (gvis == null) {
                    return;
                }
                ICubeTableService[] tis = group.getTis();
                for (int i = 0; i < summaryLists.length; i++) {
                    List<TargetAndKey> targetAndKeys = summaryLists[i];
                    for (TargetAndKey targetAndKey : targetAndKeys) {
                        if (node.getSummaryValue(targetAndKey.getTargetGettingKey()) == null && shouldSum) {
                            for (TargetAndKey tk : creator.createTargetAndKeyList(targetAndKey)){
                                BISingleThreadCal singleThreadCal = createSingleThreadCal(tis[i], node, tk, gvis[i], group.getLoader(), shouldSetIndex, count, size, calculated, allAdded);
                                if (executor != null) {
                                    executor.add(singleThreadCal);
                                    size.incrementAndGet();
                                } else {
                                    singleThreadCal.cal();
                                }
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

    private static BISingleThreadCal createSingleThreadCal(ICubeTableService ti, Node node, TargetAndKey targetAndKey, GroupValueIndex gvi, ICubeDataLoader loader, boolean shouldSetIndex, AtomicInteger count, AtomicInteger size,  Status calculated, Status allAdded) {

        if (shouldSetIndex) {
            return new SummaryIndexCal(ti, node, targetAndKey, gvi, loader, count, size, calculated, allAdded);
        } else {
            return new SummaryCountCall(ti, node, targetAndKey, gvi, loader, count, size, calculated, allAdded);
        }
    }

    static class Status{
        private volatile boolean completed = false;


        public Status() {
        }

        public boolean isCompleted() {
            return completed;
        }

        public void setCompleted() {
            completed = true;
        }
    }

    static class SummaryCountCall  extends SummaryCall {
        AtomicInteger count;
        AtomicInteger size;
        Status calculated;
        Status allAdded;
        public SummaryCountCall(ICubeTableService ti, Node node, TargetAndKey targetAndKey, GroupValueIndex gvi, ICubeDataLoader loader, AtomicInteger count, AtomicInteger size, Status calculated, Status allAdded) {
            super(ti, node, targetAndKey, gvi, loader);
            this.count = count;
            this.size = size;
            this.calculated = calculated;
            this.allAdded = allAdded;
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
            if (count.incrementAndGet() == size.get() && allAdded.isCompleted()) {
                calculated.setCompleted();
                synchronized (size) {
                    size.notify();
                }
            }
        }
    }

    static class SummaryIndexCal  extends SummaryCountCall {
        public SummaryIndexCal(ICubeTableService ti, Node node, TargetAndKey targetAndKey, GroupValueIndex gvi, ICubeDataLoader loader, AtomicInteger count, AtomicInteger size,  Status calculated, Status allAdded) {
            super(ti, node, targetAndKey, gvi, loader, count, size, calculated, allAdded);
        }

        @Override
        public void cal() {
            node.setTargetIndex(targetAndKey.getTargetGettingKey(), gvi);
            super.cal();
        }
    }
}