package com.fr.bi.field.target.calculator.cal.configure;

import com.fr.base.FRContext;
import com.fr.bi.field.target.key.cal.configuration.BIRankCalTargetKey;
import com.fr.bi.field.target.key.sum.AvgKey;
import com.fr.bi.field.target.target.cal.target.configure.BIConfiguredCalculateTarget;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.operation.sort.comp.RankConfASCComparator;
import com.fr.bi.stable.operation.sort.comp.RankConfDSCComparator;
import com.fr.bi.stable.report.key.TargetGettingKey;
import com.fr.bi.stable.report.result.BICrossNode;
import com.fr.bi.stable.report.result.BINode;
import com.fr.bi.stable.report.result.BITargetKey;
import com.fr.bi.stable.utils.CubeBaseUtils;

import java.util.*;

/**
 * Created by 小灰灰 on 2015/7/2.
 */
public class RankConfigureCalculator extends AbstractConfigureCalulator {
    private static final long serialVersionUID = 7481595795461223558L;
    private int type = BIReportConstant.TARGET_TYPE.CAL_VALUE.RANK_TPYE.ASC;

    public RankConfigureCalculator(BIConfiguredCalculateTarget target, String target_id, int start_group, int rank_type) {
        super(target, target_id, start_group);
        this.type = rank_type;
    }

    @Override
    public void calCalculateTarget(BINode node) {
        Object key = getCalKey();
        if (key == null) {
            return;
        }
        BINode tempNode = node;
        int deep = getActualStart_Group(start_group, tempNode);
        for (int i = 0; i < deep; i++) {
            if (tempNode.getFirstChild() == null) {
                break;
            }
            if (tempNode.getFirstChild().getFirstChild() != null) {
                tempNode = tempNode.getFirstChild();
            }
        }
        List nodeList = new ArrayList();
        BINode cursor_node = tempNode;
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
        int deep = getActualStart_Group(start_group,tempNode);
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

    @Override
    public BITargetKey createTargetKey() {
        return new BIRankCalTargetKey(targetName, target_id, targetMap, start_group, type);
    }


    private class RankDealWith implements java.util.concurrent.Callable {
        private BINode rank_node;

        private RankDealWith(BINode rank_node) {
            this.rank_node = rank_node;
        }


        @Override
        public Object call() throws Exception {
            Object key = getCalKey();
            String targetName = ((TargetGettingKey) key).getTargetName();
            BITargetKey targetKey = ((TargetGettingKey) key).getTargetKey();
            int deep = 0;
            BINode temp_node = rank_node;
            while (temp_node.getFirstChild() != null) {
                temp_node = temp_node.getFirstChild();
                deep++;
            }
            Comparator c = type == BIReportConstant.TARGET_TYPE.CAL_VALUE.RANK_TPYE.ASC ?
                    new RankConfASCComparator() : new RankConfDSCComparator();
            TreeMap sortMap = new TreeMap(c);
            BINode cursor_node = temp_node;
            while (isNotEnd(cursor_node, deep)) {
                Comparable value;
                if (targetKey instanceof AvgKey) {
                    value = getAvgValue(targetName, (AvgKey) targetKey, cursor_node);
                } else {
                    value = (Comparable) cursor_node.getSummaryValue(key);
                }
                Integer time = (Integer) sortMap.get(value);
                if (time == null) {
                    time = new Integer(1);
                } else {
                    time = new Integer(time.intValue() + 1);
                }
                sortMap.put(value, time);
                cursor_node = cursor_node.getSibling();
            }
            Map result = new HashMap();
            int rank = 1;
            Iterator iter = sortMap.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                result.put(entry.getKey(), new Integer(rank));
                rank += ((Integer) entry.getValue()).intValue();
            }
            cursor_node = temp_node;
            while (isNotEnd(cursor_node, deep)) {
                Object value;
                if (targetKey instanceof AvgKey) {
                    value = getAvgValue(targetName, (AvgKey) targetKey, cursor_node);
                } else {
                    value = cursor_node.getSummaryValue(key);
                }
                cursor_node.setSummaryValue(createTargetGettingKey(), result.get(value));
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
            Object key = getCalKey();
            int deep = 0;
            BICrossNode temp_node = rank_node;
            while (temp_node.getLeftFirstChild() != null) {
                temp_node = temp_node.getLeftFirstChild();
                deep++;
            }
            Comparator c = type == BIReportConstant.TARGET_TYPE.CAL_VALUE.RANK_TPYE.ASC ?
                    new RankConfASCComparator() : new RankConfDSCComparator();
            TreeMap sortMap = new TreeMap(c);
            BICrossNode cursor_node = temp_node;
            while (isNotEnd(cursor_node, deep)) {
                Comparable value = (Comparable) cursor_node.getSummaryValue(key);
                Integer time = (Integer) sortMap.get(value);
                if (time == null) {
                    time = new Integer(1);
                } else {
                    time = new Integer(time.intValue() + 1);
                }
                sortMap.put(value, time);
                cursor_node = cursor_node.getBottomSibling();
            }
            Map result = new HashMap();
            int rank = 1;
            Iterator iter = sortMap.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                result.put(entry.getKey(), new Integer(rank));
                rank += ((Integer) entry.getValue()).intValue();
            }
            cursor_node = temp_node;
            while (isNotEnd(cursor_node, deep)) {
                Object value = cursor_node.getSummaryValue(key);
                cursor_node.setSummaryValue(createTargetGettingKey(), result.get(value));
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