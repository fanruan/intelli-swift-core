package com.fr.bi.cal.analyze.cal.sssecret;

import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.cal.analyze.cal.index.loader.TargetAndKey;
import com.fr.bi.cal.analyze.cal.multithread.*;
import com.fr.bi.cal.analyze.cal.result.Node;
import com.fr.bi.cal.analyze.cal.result.NodeAndPageInfo;
import com.fr.bi.cal.analyze.cal.result.NodeUtils;
import com.fr.bi.cal.analyze.cal.result.operator.Operator;
import com.fr.bi.stable.gvi.GroupValueIndex;

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
        addSummaryValue(node, gc, showSum, shouldSetIndex, executor);
        while (!op.isPageEnd() && gc != null && gc.getChild() != null) {
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
            iterator.moveNext();
            gc = iterator.next();
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
                        } else if (shouldSetIndex && node.getTargetIndex(targetAndKey.getTargetGettingKey()) == null){
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