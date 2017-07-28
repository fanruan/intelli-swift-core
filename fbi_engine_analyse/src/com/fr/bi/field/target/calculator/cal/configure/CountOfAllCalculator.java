package com.fr.bi.field.target.calculator.cal.configure;

import com.fr.bi.field.target.target.cal.target.configure.BIConfiguredCalculateTarget;
import com.fr.bi.report.key.TargetGettingKey;
import com.fr.bi.report.key.XTargetGettingKey;
import com.fr.bi.report.result.BINode;

import java.util.concurrent.Callable;

/**
 * Created by 小灰灰 on 2015/7/2.
 */
public class CountOfAllCalculator extends SummaryOfAllCalculator {
    private static final long serialVersionUID = 7574028302614199981L;

    public CountOfAllCalculator(BIConfiguredCalculateTarget target, TargetGettingKey calTargetKey, int start_group) {
        super(target, calTargetKey, start_group);
    }

    @Override
    public Callable createNodeDealWith(BINode node, XTargetGettingKey key) {
        return new RankDealWith(node, key);
    }

    private class RankDealWith implements Callable {
        private BINode rank_node;
        private XTargetGettingKey key;

        private RankDealWith(BINode node, XTargetGettingKey key) {
            this.rank_node = node;
            this.key = key;
        }

        @Override
        public Object call() throws Exception {
            int deep = getCalDeep(rank_node);
            BINode temp_node = getDeepCalNode(rank_node);
            BINode cursor_node = temp_node;
            int count = 0;
            while (isNotEnd(cursor_node, deep)) {
                count++;
                cursor_node = cursor_node.getSibling();
            }
            cursor_node = temp_node;
            Integer value = new Integer(count);
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