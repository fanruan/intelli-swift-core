package com.fr.bi.cal.analyze.cal.sssecret;

import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.cal.analyze.cal.index.loader.TargetAndKey;
import com.fr.bi.cal.analyze.cal.multithread.BIMultiThreadExecutor;
import com.fr.bi.cal.analyze.cal.multithread.BISingleThreadCal;
import com.fr.bi.cal.analyze.cal.multithread.SummaryCall;
import com.fr.bi.cal.analyze.cal.multithread.SummaryIndexCal;
import com.fr.bi.cal.analyze.cal.result.Node;
import com.fr.bi.cal.analyze.cal.result.NodeAndPageInfo;
import com.fr.bi.cal.analyze.cal.result.NodeUtils;
import com.fr.bi.cal.analyze.cal.result.operator.Operator;
import com.fr.bi.stable.gvi.GroupValueIndex;

import java.util.Iterator;
import java.util.List;

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
        List<GroupConnectionValue> display = gc.getDisplayGroupConnectionValue();
        Iterator<GroupConnectionValue> displayIter = display.iterator();
        if (displayIter.hasNext()) {
            gc = displayIter.next();
        }
        addSummaryValue(node, gc, showSum, shouldSetIndex, executor);
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
                addSummaryValue(child, gcvChild, showSum, shouldSetIndex, executor);
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
    private static void addSummaryValue(Node node, GroupConnectionValue gcv, boolean showSum, boolean shouldSetIndex, BIMultiThreadExecutor executor) {

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
                            BISingleThreadCal singleThreadCal = createSingleThreadCal(tis[i], node, targetAndKey, gvis[i], group.getLoader(), shouldSetIndex);
                            if (executor != null) {
                                executor.add(singleThreadCal);
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

    private static BISingleThreadCal createSingleThreadCal(ICubeTableService ti, Node node, TargetAndKey targetAndKey, GroupValueIndex gvi, ICubeDataLoader loader, boolean shouldSetIndex) {

        if (shouldSetIndex) {
            return new SummaryIndexCal(ti, node, targetAndKey, gvi, loader);
        } else {
            return new SummaryCall(ti, node, targetAndKey, gvi, loader);
        }
    }

}