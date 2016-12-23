package com.fr.bi.cal.analyze.cal.sssecret;

import com.fr.bi.cal.analyze.cal.result.Node;
import com.fr.bi.cal.analyze.cal.result.NodeAndPageInfo;
import com.fr.bi.cal.analyze.cal.result.NodeUtils;
import com.fr.bi.cal.analyze.cal.result.operator.Operator;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.report.result.TargetCalculator;

import java.util.List;
import java.util.Map;

public class GroupUtils {


    public static NodeAndPageInfo createNextPageMergeNode(IRootDimensionGroup root, Operator op, boolean showSum) {
        NodeDimensionIterator iterator = op.getPageIterator(root);
        return createMergePageNode(iterator, op, showSum);
    }


    private static NodeAndPageInfo createMergePageNode(NodeDimensionIterator iterator, Operator op, boolean showSum) {
        Node node = new Node(null, null);
        GroupConnectionValue gc =  iterator.next();
        if (gc == null) {
            return new NodeAndPageInfo(node, false, false, 0);
        }
        if (showSum){
            addSummaryValue(node, gc, null, showSum);
        }
        while (!op.isPageEnd() && gc != null){
            GroupConnectionValue gcvChild = gc.getChild();
            Node parent = node;
            int deep = 0;
            while (gcvChild != null){
                Object data = gcvChild.getData();
                Node child = parent.getChild(data);
                if (child == null) {
                    child = new Node(gcvChild.getCk(), data);
                    if (data != BIBaseConstant.EMPTY_NODE_DATA || deep != 0) {
                        parent.addChild(child);
                    }
                }
                parent = child;
                addSummaryValue(child, gcvChild, null, showSum);
                gcvChild = gcvChild.getChild();
                deep++;
            }
            op.addRow();
            iterator.moveNext();
            gc = iterator.next();
        }
        iterator.pageEnd();

        NodeUtils.setSiblingBetweenFirstAndLastChild(node);
        return new NodeAndPageInfo(node, iterator.hasPrevious(), iterator.hasNext(), getPage(iterator));
    }


    private static int getPage(NodeDimensionIterator iterator) {
        return iterator == null ? 0 : iterator.getPageIndex();
    }

    /**
     * 获取维度对应指标的汇总值
     *
     * @param node
     */
    private static void addSummaryValue(Node node, GroupConnectionValue gcv, List<TargetCalculator[]> calculators, boolean showSum) {
        if (showSum || gcv.getChild() == null){
            NoneDimensionGroup group = gcv.getCurrentValue();
//            if (group != null && !ComparatorUtils.equals(group.getTableKey(), BIBusinessTable.createEmptyTable())) {
//                if (MultiThreadManagerImpl.isMultiCall()) {
//                    MultiThreadManagerImpl.getInstance().getExecutorService().add(new SummaryCall(node, group));
//                } else {
//                    for (TargetCalculator calculator : calculators.get(i)){
//                        Number v = groups[i].getSummaryValue(calculator);
//                        if (v != null) {
//                            node.setTargetGetter(calculator.createTargetGettingKey(), groups[i].getRoot().getGroupValueIndex());
//                            node.setTargetIndex(calculator.createTargetGettingKey(), groups[i].getRoot().getGroupValueIndex());
//                            node.setSummaryValue(calculator.createTargetGettingKey(), v);
//                        }
//                    }
//                }
//            }
        }
    }


    private static void setSummaryValueMap(Node node, TreeNoneDimensionGroup group) {
        Map summaryValueMap = group.getTargetGettingKeyRoot().getSummaryValueMap();
        node.setSummaryValueMap(summaryValueMap);
    }

}