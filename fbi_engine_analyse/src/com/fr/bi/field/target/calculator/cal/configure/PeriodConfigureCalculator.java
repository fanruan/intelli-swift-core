package com.fr.bi.field.target.calculator.cal.configure;

import com.fr.base.FRContext;
import com.fr.bi.field.target.target.cal.target.configure.BIConfiguredCalculateTarget;
import com.fr.bi.report.key.TargetGettingKey;
import com.fr.bi.report.key.XTargetGettingKey;
import com.fr.bi.report.result.BINode;
import com.fr.bi.report.result.BIXLeftNode;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.utils.CubeBaseUtils;
import com.fr.general.ComparatorUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by 小灰灰 on 2015/7/2.
 */
public class PeriodConfigureCalculator extends AbstractConfigureCalculator {
    private static final long serialVersionUID = 550160604753618347L;
    private int type = BIReportConstant.TARGET_TYPE.CAL_VALUE.PERIOD_TYPE.VALUE;

    public PeriodConfigureCalculator(BIConfiguredCalculateTarget target, TargetGettingKey calTargetKey, int start_group, int period_type) {
        super(target, calTargetKey, start_group);
        this.type = period_type;
    }

    /**
     * 计算target值
     *
     * @param node node节点
     */
    @Override
    public void calCalculateTarget(BINode node) {
        cal(node, null);
    }

    private void cal(BINode node, XTargetGettingKey key) {
        if (calTargetKey == null) {
            return;
        }
        int deep = getCalDeep(node);
        BINode tempNode = node;

        if (start_group == 0) {
            //从第几个纬度开始计算
            int calDeep = (deep == 1 ? deep : deep - 1);
            for (int i = 0; i < calDeep; i++) {
                if (tempNode.getFirstChild() == null) {
                    break;
                }
                tempNode = tempNode.getFirstChild();
            }
        } else {
            if (node.getDeep() > node.getFrameDeep()) {
                tempNode = getMaxDeepNode(getCalculatedRootNode(node));
            } else {
                return;
            }
        }
        List<RankDealWith> nodeList = new ArrayList<RankDealWith>();
        BINode cursor_node = tempNode;
        BINode last_node = null;
        while (cursor_node != null) {
            nodeList.add(new RankDealWith(last_node, cursor_node, key));
            last_node = cursor_node;
            cursor_node = cursor_node.getSibling();
            if (cursor_node != null && start_group == 0 && !ComparatorUtils.equals(last_node.getParent(), cursor_node.getParent())) {
                last_node = null;
            }
            try {
                CubeBaseUtils.invokeCalculatorThreads(nodeList);
            } catch (InterruptedException e) {
                FRContext.getLogger().error(e.getMessage(), e);
            }
        }

    }

    /**
     * 计算target值
     *
     * @param node 节点
     * @param key1 target的key
     */
    @Override
    public void calCalculateTarget(BIXLeftNode node, XTargetGettingKey key) {
        cal(node, key);
    }

    private class RankDealWith implements Callable<Object> {
        private BINode last_node;
        private BINode current_node;
        private XTargetGettingKey key;

        private RankDealWith(BINode last_node, BINode current_node, XTargetGettingKey key) {
            this.last_node = last_node;
            this.current_node = current_node;
            this.key = key;
        }


        @Override
        public Object call() throws Exception {
            int deep = 0;
            BINode temp_node = current_node;
            while (temp_node.getFirstChild() != null) {
                temp_node = temp_node.getFirstChild();
                deep++;
            }
            BINode cursor_node = temp_node;
            while (isNotEnd(cursor_node, deep)) {
                BINode n = cursor_node;
                Object[] way = new Object[deep];
                for (int i = way.length; i > 0; i--) {
                    way[i - 1] = n.getData();
                    n = n.getParent();
                }
                Number value = getValueFromLast(way);
                if (value != null && type == BIReportConstant.TARGET_TYPE.CAL_VALUE.PERIOD_TYPE.RATE) {
                    Number v = cursor_node.getSummaryValue(getCalTargetGettingKey(key));
                    double currentValue = v == null ? 0 : v.doubleValue();
                    cursor_node.setSummaryValue(getTargetGettingKey(key), (currentValue - (Double) value) / (Double) value);
                } else {
                    cursor_node.setSummaryValue(getTargetGettingKey(key), value);
                }

                cursor_node = cursor_node.getSibling();
            }
            return null;
        }

        private Number getValueFromLast(Object[] way) {
            if (last_node == null) {
                return null;
            }
            int i = 0;
            BINode n = last_node;
            while (i < way.length) {
                n = n.getChild(way[i++]);
                if (n == null) {
                    break;
                }
            }
            if (n == null) {
                return null;
            } else {
                return n.getSummaryValue(getCalTargetGettingKey(key));
            }
        }


        private boolean isNotEnd(BINode node, int deep) {
            if (node == null) {
                return false;
            }
            BINode temp = node;
            for (int i = 0; i < deep; i++) {
                temp = temp.getParent();
            }
            return temp == current_node;
        }

    }

}