package com.fr.bi.cal.analyze.cal.sssecret;

import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.cal.analyze.cal.index.loader.TargetAndKey;
import com.fr.bi.cal.analyze.cal.multithread.BISingleThreadCal;
import com.fr.bi.cal.analyze.cal.multithread.MultiThreadManagerImpl;
import com.fr.bi.cal.analyze.cal.multithread.SummaryCall;
import com.fr.bi.cal.analyze.cal.multithread.SummaryIndexCal;
import com.fr.bi.cal.analyze.cal.result.Node;
import com.fr.bi.cal.analyze.cal.result.NodeAndPageInfo;
import com.fr.bi.cal.analyze.cal.result.NodeUtils;
import com.fr.bi.cal.analyze.cal.result.operator.Operator;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.gvi.GroupValueIndex;

import java.util.List;

public class GroupUtils {


    public static NodeAndPageInfo createNextPageMergeNode(NodeDimensionIterator iterator, Operator op, boolean showSum, boolean shouldSetIndex, int sumLength) {
        return createMergePageNode(iterator, op, showSum, shouldSetIndex, sumLength);
    }

    private static NodeAndPageInfo createMergePageNode(NodeDimensionIterator iterator, Operator op, boolean showSum, boolean shouldSetIndex, int sumLength) {
        Node node = new Node(sumLength);
        GroupConnectionValue gc = iterator.next();
        if (gc == null) {
            return new NodeAndPageInfo(node, iterator);
        }
        addSummaryValue(node, gc, showSum, shouldSetIndex);
        while (!op.isPageEnd() && gc != null && gc.getChild() != null) {
            GroupConnectionValue gcvChild = gc.getChild();
            Node parent = node;
            int deep = 0;
            while (gcvChild != null) {
                Object data = gcvChild.getData();
                Node child = parent.getChild(data);
                if (child == null) {
                    child = new Node(data, sumLength);
                    if (deep != 0 || !isEmptyData(data)) {
                        parent.addChild(child);
                    }
                }
                parent = child;
                addSummaryValue(child, gcvChild, showSum, shouldSetIndex);
                gcvChild = gcvChild.getChild();
                deep++;
            }
            op.addRow();
            iterator.moveNext();
            gc = iterator.next();
        }
        iterator.pageEnd();
        NodeUtils.setSiblingBetweenFirstAndLastChild(node);
        return new NodeAndPageInfo(node, iterator);
    }

    private static boolean isEmptyData(Object data) {
        return data == BIBaseConstant.EMPTY_NODE_DATA || (data instanceof Double && Double.isNaN((Double)data));
    }

    /**
     * 获取维度对应指标的汇总值
     *
     * @param node
     * @param shouldSetIndex
     */
    private static void addSummaryValue(Node node, GroupConnectionValue gcv, boolean showSum, boolean shouldSetIndex) {
        if (showSum || gcv.getChild() == null) {
            NoneDimensionGroup group = gcv.getCurrentValue();
            if (group != null) {
                List<TargetAndKey>[] summaryLists = group.getSummaryLists();
                node.setSummaryValue(group.getSummaryValue());
                GroupValueIndex[] gvis = group.getGvis();
                if (gvis == null){
                    return;
                }
                ICubeTableService[] tis = group.getTis();
                for (int i = 0; i < summaryLists.length; i++) {
                    List<TargetAndKey> targetAndKeys = summaryLists[i];
                    for (TargetAndKey targetAndKey : targetAndKeys) {
                        BISingleThreadCal singleThreadCal = createSingleThreadCal(tis[i], node, targetAndKey, gvis[i], group.getLoader(), shouldSetIndex);
                        if (MultiThreadManagerImpl.isMultiCall()) {
                            MultiThreadManagerImpl.getInstance().getExecutorService().add(singleThreadCal);
                        } else {
                            singleThreadCal.cal();
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