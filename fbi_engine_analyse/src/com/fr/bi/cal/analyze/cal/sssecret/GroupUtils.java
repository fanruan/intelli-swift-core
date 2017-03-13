package com.fr.bi.cal.analyze.cal.sssecret;

<<<<<<< HEAD
import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.cal.analyze.cal.index.loader.TargetAndKey;
import com.fr.bi.cal.analyze.cal.multithread.BISingleThreadCal;
import com.fr.bi.cal.analyze.cal.multithread.MultiThreadManagerImpl;
import com.fr.bi.cal.analyze.cal.multithread.SummaryCall;
import com.fr.bi.cal.analyze.cal.multithread.SummaryIndexCal;
=======
import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.table.BIBusinessTable;
import com.fr.bi.cal.analyze.cal.multithread.MultiThreadManagerImpl;
import com.fr.bi.cal.analyze.cal.multithread.SummaryCall;
import com.fr.bi.cal.analyze.cal.multithread.SummaryIndexCall;
>>>>>>> 67b55d486e769f445942f15883303ca839ffd092
import com.fr.bi.cal.analyze.cal.result.Node;
import com.fr.bi.cal.analyze.cal.result.NodeAndPageInfo;
import com.fr.bi.cal.analyze.cal.result.NodeUtils;
import com.fr.bi.cal.analyze.cal.result.operator.Operator;
import com.fr.bi.stable.constant.BIBaseConstant;
<<<<<<< HEAD
import com.fr.bi.stable.gvi.GroupValueIndex;
=======
import com.fr.bi.stable.report.result.LightNode;
import com.fr.bi.stable.report.result.TargetCalculator;
import com.fr.general.ComparatorUtils;
>>>>>>> 67b55d486e769f445942f15883303ca839ffd092

import java.util.List;

public class GroupUtils {


<<<<<<< HEAD
    public static NodeAndPageInfo createNextPageMergeNode(NodeDimensionIterator iterator, Operator op, boolean showSum, boolean shouldSetIndex) {
        return createMergePageNode(iterator, op, showSum, shouldSetIndex);
=======
    public static NodeAndPageInfo createNextPageMergeNode(IRootDimensionGroup[] roots, List<TargetCalculator[]> calculators, NodeExpander expander, Operator op) {
        NodeDimensionIterator[] iters = op.getPageIterator(roots, expander);
        MultiThreadManagerImpl.getInstance().refreshExecutorService();
        return createMergePageNode(iters, calculators, op, roots);
>>>>>>> 67b55d486e769f445942f15883303ca839ffd092
    }

    private static NodeAndPageInfo createMergePageNode(NodeDimensionIterator iterator, Operator op, boolean showSum, boolean shouldSetIndex) {
        Node node = new Node();
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
                    child = new Node(data);
                    if (data != BIBaseConstant.EMPTY_NODE_DATA || deep != 0) {
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


    private static int getPage(NodeDimensionIterator iterator) {
        return iterator == null ? 0 : iterator.getPageIndex();
    }

    /**
     * 获取维度对应指标的汇总值
     *
     * @param node
     * @param shouldSetIndex
     */
<<<<<<< HEAD
    private static void addSummaryValue(Node node, GroupConnectionValue gcv, boolean showSum, boolean shouldSetIndex) {
        if (showSum || gcv.getChild() == null) {
            NoneDimensionGroup group = gcv.getCurrentValue();
            if (group != null) {
                List<TargetAndKey>[] summaryLists = group.getSummaryLists();
                node.setSummaryValue(group.getSummaryValue());
                GroupValueIndex[] gvis = group.getGvis();
                if (gvis != null){
                    ICubeTableService[] tis = group.getTis();
                    for (int i = 0; i < summaryLists.length; i++) {
                        List<TargetAndKey> targetAndKeys = summaryLists[i];
                        for (TargetAndKey targetAndKey : targetAndKeys) {
                            BISingleThreadCal singleThreadCal = createSinlgeThreadCal(tis[i], node, targetAndKey, gvis[i], group.getLoader(), shouldSetIndex);
                            if (MultiThreadManagerImpl.isMultiCall()) {
                                MultiThreadManagerImpl.getInstance().getExecutorService().add(singleThreadCal);
                            } else {
                                singleThreadCal.cal();
                            }
=======
    private static void addSummaryValue(Node node, GroupConnectionValue[] gcvs, List<TargetCalculator[]> calculators) {
        NoneDimensionGroup[] groups = new NoneDimensionGroup[gcvs.length];
        for (int i = 0; i < gcvs.length; i++) {
            if (gcvs[i] != null) {
                groups[i] = gcvs[i].getCurrentValue();
            }
        }
        for (int i = 0; i < groups.length; i++) {
            if (groups[i] != null && !ComparatorUtils.equals(groups[i].getTableKey(), BIBusinessTable.createEmptyTable())) {
                if (groups[i] instanceof TreeNoneDimensionGroup) {
                    setSummaryValueMap(node, (TreeNoneDimensionGroup) groups[i]);
                    LightNode root = groups[i].getLightNode();
                    NodeUtils.copyIndexMap(node, root);
                    if (MultiThreadManagerImpl.isMultiCall()) {
                        for (TargetCalculator[] cs : calculators){
                            if (cs != null){
                                for (TargetCalculator c : cs){
                                    MultiThreadManagerImpl.getInstance().getExecutorService().add(new SummaryCall(node, groups[i],c));
                                }
                            }
                        }
                    } else {
                        for (TargetCalculator[] cs : calculators){
                            for (TargetCalculator calculator : cs){
                                Number v = groups[i].getSummaryValue(calculator);
                                if (v != null) {
                                    node.setSummaryValue(calculator.createTargetGettingKey(), v);
                                }
                            }
                        }
                    }
                    break;
                }
                if (MultiThreadManagerImpl.isMultiCall()) {
                    TargetCalculator[] cs = calculators.get(i);
                    if (cs != null){
                        for (TargetCalculator c : cs){
                            MultiThreadManagerImpl.getInstance().getExecutorService().add(new SummaryIndexCall(node, groups[i],c));
                        }
                    }
                } else {
                    for (TargetCalculator calculator : calculators.get(i)){
                        Number v = groups[i].getSummaryValue(calculator);
                        if (v != null) {
                            node.setTargetGetter(calculator.createTargetGettingKey(), groups[i].getRoot().getGroupValueIndex());
                            node.setTargetIndex(calculator.createTargetGettingKey(), groups[i].getRoot().getGroupValueIndex());
                            node.setSummaryValue(calculator.createTargetGettingKey(), v);
>>>>>>> 67b55d486e769f445942f15883303ca839ffd092
                        }
                    }
                }
            }
        }
    }

    private static BISingleThreadCal createSinlgeThreadCal(ICubeTableService ti, Node node, TargetAndKey targetAndKey, GroupValueIndex gvi, ICubeDataLoader loader, boolean shouldSetIndex) {
        if (shouldSetIndex) {
            return new SummaryIndexCal(ti, node, targetAndKey, gvi, loader);
        } else {
            return new SummaryCall(ti, node, targetAndKey, gvi, loader);
        }
    }

}