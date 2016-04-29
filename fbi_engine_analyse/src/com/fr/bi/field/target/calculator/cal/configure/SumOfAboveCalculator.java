package com.fr.bi.field.target.calculator.cal.configure;

import com.fr.base.FRContext;
import com.fr.bi.field.target.key.cal.configuration.BISumOfAboveCalTargetKey;
import com.fr.bi.field.target.target.cal.target.configure.BIConfiguredCalculateTarget;
import com.fr.bi.stable.report.key.TargetGettingKey;
import com.fr.bi.stable.report.result.BICrossNode;
import com.fr.bi.stable.report.result.BITargetKey;
import com.fr.bi.stable.report.result.LightNode;
import com.fr.bi.stable.utils.CubeBaseUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 小灰灰 on 2015/7/2.
 */
public class SumOfAboveCalculator extends AbstractConfigureCalulator {

    private static final long serialVersionUID = -3095522390932830159L;

    public SumOfAboveCalculator(BIConfiguredCalculateTarget target, String cal_target_name, int start_group) {
        super(target, cal_target_name, start_group);
    }

    @Override
    public void calCalculateTarget(LightNode node) {
        Object key = getCalKey();
        if (key == null) {
            return;
        }
        LightNode tempNode = node;
        for (int i = 0; i < start_group; i++) {
            if (tempNode.getFirstChild() == null) {
                break;
            }
            tempNode = tempNode.getFirstChild();
        }
        List nodeList = new ArrayList();
        LightNode cursor_node = tempNode;
        while (cursor_node != null) {
            nodeList.add(new RankDealWith(cursor_node));
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
        Object key = getCalKey();
        if (key == null) {
            return;
        }
        BICrossNode tempNode = node;
        for (int i = 0; i < start_group; i++) {
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

    @Override
    public BITargetKey createTargetKey() {
        return new BISumOfAboveCalTargetKey(targetName, cal_target_name, targetMap, start_group);
    }

    private class RankDealWith implements java.util.concurrent.Callable {
        private LightNode rank_node;

        private RankDealWith(LightNode rank_node) {
            this.rank_node = rank_node;
        }


        @Override
        public Object call() throws Exception {
            Object key = getCalKey();
            int deep = 0;
            LightNode temp_node = rank_node;
            while (temp_node.getFirstChild() != null) {
                temp_node = temp_node.getFirstChild();
                deep++;
            }
            LightNode cursor_node = temp_node;
            double sum = 0;
            while (isNotEnd(cursor_node, deep)) {
                Number value = cursor_node.getSummaryValue(key);
                sum += value == null ? 0 : value.doubleValue();
                cursor_node.setSummaryValue(createTargetGettingKey(), new Double(sum));
                cursor_node = cursor_node.getSibling();
            }
            return null;
        }

        private boolean isNotEnd(LightNode node, int deep) {
            if (node == null) {
                return false;
            }
            LightNode temp = node;
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
            Object key = getCalKey();
            int deep = 0;
            BICrossNode temp_node = rank_node;
            while (temp_node.getLeftFirstChild() != null) {
                temp_node = temp_node.getLeftFirstChild();
                deep++;
            }
            BICrossNode cursor_node = temp_node;
            double sum = 0;
            while (isNotEnd(cursor_node, deep)) {
                Number value = cursor_node.getSummaryValue(key);
                sum += value.doubleValue();
                cursor_node.setSummaryValue(createTargetKey(), new Double(sum));
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