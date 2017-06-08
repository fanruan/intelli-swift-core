package com.fr.bi.field.target.calculator.cal.configure;

import com.fr.base.FRContext;
import com.fr.bi.field.target.target.cal.target.configure.BIConfiguredCalculateTarget;
import com.fr.bi.stable.io.newio.NIOConstant;
import com.fr.bi.report.key.TargetGettingKey;
import com.fr.bi.report.result.BICrossNode;
import com.fr.bi.report.result.BINode;
import com.fr.bi.stable.utils.BICollectionUtils;
import com.fr.bi.stable.utils.CubeBaseUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 小灰灰 on 2015/7/2.
 */
public class SumOfAboveCalculator extends AbstractConfigureCalculator {

    private static final long serialVersionUID = -3095522390932830159L;

    public SumOfAboveCalculator(BIConfiguredCalculateTarget target, TargetGettingKey calTargetKey, int start_group) {
        super(target, calTargetKey, start_group);
    }

    @Override
    public void calCalculateTarget(BINode node) {
        if (calTargetKey == null) {
            return;
        }
        BINode tempNode = node;
        int deep = getActualStart_Group(start_group, tempNode);
        for (int i = 0; i < deep; i++) {
            if (tempNode.getFirstChild() == null) {
                break;
            }
            tempNode = tempNode.getFirstChild();
        }

        if (node.getDeep() > node.getFrameDeep()) {
            tempNode = getCalculatedRootNode(node);
        } else {
            return;
        }

        List nodeList = new ArrayList();
        BINode cursor_node = tempNode;
        while (cursor_node != null) {
            if (shouldCalculate(cursor_node)) {
                nodeList.add(new RankDealWith(cursor_node));
            }
            cursor_node = cursor_node.getSibling();
        }
        try {
            CubeBaseUtils.invokeCalculatorThreads(nodeList);
        } catch (InterruptedException e) {
            FRContext.getLogger().error(e.getMessage(), e);
        }
    }

    @Override
    public void calCalculateTarget(BICrossNode node, TargetGettingKey key1) {
        if (calTargetKey == null) {
            return;
        }
        BICrossNode tempNode = node;
        int deep = getActualStart_Group(start_group, tempNode);
        for (int i = 0; i < deep; i++) {
            if (tempNode.getLeftFirstChild() == null) {
                break;
            }
            tempNode = tempNode.getLeftFirstChild();
        }
        List nodeList = new ArrayList();
        BICrossNode cursor_node = tempNode;
        while (cursor_node != null) {
            nodeList.add(new RankDealWithCrossNode(cursor_node));
            cursor_node = cursor_node.getBottomSibling();
        }
        try {
            CubeBaseUtils.invokeCalculatorThreads(nodeList);
        } catch (InterruptedException e) {
            FRContext.getLogger().error(e.getMessage(), e);
        }
    }

    private class RankDealWith implements java.util.concurrent.Callable {
        private BINode rank_node;

        private RankDealWith(BINode rank_node) {
            this.rank_node = rank_node;
        }


        @Override
        public Object call() throws Exception {
            int deep = 0;
            BINode temp_node = rank_node;
            while (temp_node.getFirstChild() != null) {
                temp_node = temp_node.getFirstChild();
                deep++;
            }
            BINode cursor_node = temp_node;
            double sum = NIOConstant.DOUBLE.NULL_VALUE;
            while (isNotEnd(cursor_node, deep)) {
                Number value = cursor_node.getSummaryValue(calTargetKey);
                if (BICollectionUtils.isNotCubeNullKey(value)) {
                    if (sum == NIOConstant.DOUBLE.NULL_VALUE) {
                        sum = 0;
                    }
                    sum += value.doubleValue();
                }
                cursor_node.setSummaryValue(createTargetGettingKey(), new Double(sum));
                cursor_node = cursor_node.getSibling();
            }
            return null;
        }

        private boolean isNotEnd(BINode node, int deep) {
            if (node == null) {
                return false;
            }
            BINode temp = node;
            for (int i = 0; i < deep; i++) {
                temp = temp.getParent();
            }
            return temp == rank_node;
        }

    }

    private class RankDealWithCrossNode implements java.util.concurrent.Callable {
        private BICrossNode rank_node;

        private RankDealWithCrossNode(BICrossNode rank_node) {
            this.rank_node = rank_node;
        }


        @Override
        public Object call() throws Exception {
            int deep = 0;
            BICrossNode temp_node = rank_node;
            while (temp_node.getLeftFirstChild() != null) {
                temp_node = temp_node.getLeftFirstChild();
                deep++;
            }
            BICrossNode cursor_node = temp_node;
            double sum = 0;
            while (isNotEnd(cursor_node, deep)) {
                Number value = cursor_node.getSummaryValue(calTargetKey);
                if (value != null) {
                    sum += value.doubleValue();
                }
                cursor_node.setSummaryValue(createTargetGettingKey(), new Double(sum));
                cursor_node = cursor_node.getBottomSibling();
            }
            return null;
        }

        private boolean isNotEnd(BICrossNode node, int deep) {
            if (node == null) {
                return false;
            }
            BICrossNode temp = node;
            for (int i = 0; i < deep; i++) {
                temp = temp.getLeftParent();
            }
            return temp == rank_node;
        }

    }

}