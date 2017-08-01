package com.fr.bi.field.target.calculator.cal.configure;

import com.fr.bi.field.target.calculator.cal.CalCalculator;
import com.fr.bi.field.target.target.cal.target.configure.BIConfiguredCalculateTarget;
import com.fr.bi.report.key.TargetGettingKey;
import com.fr.bi.report.key.XTargetGettingKey;
import com.fr.bi.report.result.BINode;
import com.fr.bi.stable.constant.BIReportConstant;

import java.util.Set;

/**
 * Created by 小灰灰 on 2015/7/2.
 */
public abstract class AbstractConfigureCalculator extends CalCalculator {
    private static final long serialVersionUID = -7031889439937906167L;

    /**
     * 哪个分组的排名， 默认是全部值
     */
    protected int start_group = BIReportConstant.TARGET_TYPE.CAL_POSITION.ALL;

    protected TargetGettingKey calTargetKey;

    public AbstractConfigureCalculator(BIConfiguredCalculateTarget target, TargetGettingKey calTargetKey, int start_group) {
        super(target);
        this.start_group = start_group;
        this.calTargetKey = calTargetKey;
    }


    @Override
    public boolean isAllFieldsReady(Set<TargetGettingKey> targetSet) {
        if (calTargetKey == null) {
            return false;
        }
        return targetSet.contains(calTargetKey);
    }

    protected int getCalDeep(BINode rank_node) {
        int deep = 0;
        BINode node = rank_node;
        while (node.getFirstChild() != null) {
            deep++;
            node = node.getFirstChild();
        }

        return deep;
    }


    protected int getActualStart_Group(int start_group, BINode rank_node) {
        return start_group == 0 ? 0 : getCalDeep(rank_node) - 1;
    }

    public BINode getCalculatedRootNode(BINode rank_node) {
        BINode currentNode;
        BINode maxDeepNode = rank_node;
        do {
            currentNode = maxDeepNode;
            maxDeepNode = getMaxDeepNode(currentNode);
        } while (maxDeepNode.getDeep() > 1);
        return currentNode;

    }

    public BINode getMaxDeepNode(BINode rank_node) {
        BINode max = null;
        int maxDeep = 0;
        for (BINode node : rank_node.getChilds()) {
            int nodeDeep = node.getDeep();
            if (nodeDeep > maxDeep) {
                max = node;
                maxDeep = nodeDeep;
            }
        }
        return max;
    }

    /**
     * 子节点为空，不用进行计算指标计算。
     * BI-5299
     *
     * @param cursor_node
     * @return
     */
    public boolean shouldCalculate(BINode cursor_node) {
        return !cursor_node.getChilds().isEmpty();
    }

    /**
     * 获取要计算的指标的key
     * @param key
     * @return
     */
    protected TargetGettingKey getTargetGettingKey(XTargetGettingKey key) {
        return key == null ? createTargetGettingKey() : key;
    }

    /**
     * 获取参数的指标的key
     * @param key
     * @return
     */
    protected TargetGettingKey getCalTargetGettingKey(XTargetGettingKey key) {
        return key == null ? calTargetKey : new XTargetGettingKey(calTargetKey.getTargetIndex(), key.getSubIndex(), calTargetKey.getTargetName());
    }


}