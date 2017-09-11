package com.fr.bi.field.target.calculator.cal.configure;

import com.fr.bi.conf.report.widget.field.target.BITarget;
import com.fr.bi.field.target.target.BICounterTarget;
import com.fr.bi.field.target.target.SumType;
import com.fr.bi.field.target.target.cal.target.configure.BIConfiguredCalculateTarget;
import com.fr.bi.report.key.XTargetGettingKey;
import com.fr.bi.report.result.BINode;
import com.fr.bi.stable.constant.BIReportConstant;

import java.util.concurrent.Callable;

/**
 * Created by 小灰灰 on 2015/7/4.
 */
public class SumOfAllCalculator extends SummaryOfAllCalculator {

    private static final long serialVersionUID = 1511004295587426830L;

    private BITarget calTarget;

    public SumOfAllCalculator(BIConfiguredCalculateTarget target, BITarget calTarget, int start_group) {

        super(target, calTarget.createTargetGettingKey(), start_group);
        this.calTarget = calTarget;
    }


    @Override
    public Callable createNodeDealWith(BINode node, XTargetGettingKey key) {

        return new RankDealWith(node, key);
    }

    private class RankDealWith implements Callable {

        private BINode rank_node;

        private XTargetGettingKey key;

        public RankDealWith(BINode node, XTargetGettingKey key) {

            this.rank_node = node;
            this.key = key;
        }

        @Override
        public Object call() throws Exception {

            int deep = getCalDeep(rank_node);
            BINode temp_node = getDeepCalNode(rank_node);
            BINode cursor_node = temp_node;
            double sum = 0;
            if (calTarget.getSummaryType() == BIReportConstant.SUMMARY_TYPE.SUM
                    || ((calTarget.getSummaryType() == BIReportConstant.SUMMARY_TYPE.COUNT) && ((BICounterTarget) calTarget).getSumType() == SumType.PLUS)) {
                sum = rank_node.getSummaryValue(getCalTargetGettingKey(key)).doubleValue();
            } else {
                while (isNotEnd(cursor_node, deep)) {
                    Number value = cursor_node.getSummaryValue(getCalTargetGettingKey(key));
                    if (value != null) {
                        sum += value.doubleValue();
                    }
                    cursor_node = cursor_node.getSibling();
                }
            }
            cursor_node = temp_node;
            Double value = new Double(sum);
            while (isNotEnd(cursor_node, deep)) {
                cursor_node.setSummaryValue(getTargetGettingKey(key), value);
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

}