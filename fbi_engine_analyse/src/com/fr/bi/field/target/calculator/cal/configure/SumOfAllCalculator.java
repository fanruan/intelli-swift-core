package com.fr.bi.field.target.calculator.cal.configure;

import com.fr.bi.field.target.key.cal.configuration.summary.BISumOfAllKey;
import com.fr.bi.field.target.target.cal.target.configure.BIConfiguredCalculateTarget;
import com.fr.bi.stable.report.result.BICrossNode;
import com.fr.bi.stable.report.result.BITargetKey;
import com.fr.bi.stable.report.result.LightNode;

import java.util.concurrent.Callable;

/**
 * Created by 小灰灰 on 2015/7/4.
 */
public class SumOfAllCalculator extends SummaryOfAllCalculator {

    private static final long serialVersionUID = 1511004295587426830L;

    public SumOfAllCalculator(BIConfiguredCalculateTarget target, String target_id, int start_group) {
        super(target, target_id, start_group);
    }


    @Override
    public Callable createNodeDealWith(LightNode node) {
        return new RankDealWith(node);
    }

    @Override
    public BITargetKey createTargetKey() {
        return new BISumOfAllKey(targetName, target_id, targetMap, start_group);
    }

    @Override
    public Callable createNodeDealWith(BICrossNode node) {
        return new RankDealWithCrossNode(node);
    }

    private class RankDealWith implements Callable {
        private LightNode rank_node;

        private RankDealWith(LightNode rank_node) {
            this.rank_node = rank_node;
        }


        @Override
        public Object call() throws Exception {
            Object key = getCalKey();
            int deep = getCalDeep();
            LightNode temp_node = getFirstCalNode(rank_node);
            LightNode cursor_node = temp_node;
            double sum = 0;
            while (isNotEnd(cursor_node, deep)) {
                Number value = cursor_node.getSummaryValue(key);
                if (value != null) {
                    sum += value.doubleValue();
                }
                cursor_node = cursor_node.getSibling();
            }
            cursor_node = temp_node;
            Object value = new Double(sum);
            while (isNotEnd(cursor_node, deep)) {
                cursor_node.setSummaryValue(createTargetGettingKey(), value);
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

    private class RankDealWithCrossNode implements Callable {
        private BICrossNode rank_node;

        private RankDealWithCrossNode(BICrossNode rank_node) {
            this.rank_node = rank_node;
        }


        @Override
        public Object call() throws Exception {
            Object key = getCalKey();
            int deep = getCalDeep();
            BICrossNode temp_node = getFirstCalCrossNode(rank_node);
            BICrossNode cursor_node = temp_node;
            double sum = 0;
            while (isNotEnd(cursor_node, deep)) {
                Number value = cursor_node.getSummaryValue(key);
                if (value != null) {
                    sum += value.doubleValue();
                }
                cursor_node = cursor_node.getBottomSibling();
            }
            cursor_node = temp_node;
            Object value = new Double(sum);
            while (isNotEnd(cursor_node, deep)) {
                cursor_node.setSummaryValue(createTargetKey(), value);
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