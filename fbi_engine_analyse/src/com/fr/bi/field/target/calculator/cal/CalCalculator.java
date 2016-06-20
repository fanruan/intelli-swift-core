package com.fr.bi.field.target.calculator.cal;

import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.field.target.target.cal.BICalculateTarget;
import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.stable.report.key.SummaryCalculator;
import com.fr.bi.stable.report.key.TargetGettingKey;
import com.fr.bi.stable.report.result.*;

import java.util.Map;
import java.util.Set;

/**
 * Created by 小灰灰 on 2015/7/2.
 */
public abstract class CalCalculator implements TargetCalculator {
    private static final long serialVersionUID = 453792138848574486L;

    protected BICalculateTarget target;

    protected String targetName;
    protected Map targetMap;
    private transient TargetGettingKey targetGettingKey;

    public CalCalculator(BICalculateTarget target) {
        this.target = target;
        this.targetName = target.getValue();
        this.targetMap = target.getTargetMap();
    }

    public CalCalculator() {
    }

    /**
     * field准备好了
     *
     * @param targetSet 目标set
     * @return true或fasle
     */
    public abstract boolean isAllFieldsReady(Set<TargetGettingKey> targetSet);

    /**
     * 计算
     *
     * @param node node节点
     */
    public abstract void calCalculateTarget(LightNode node);

    /**
     * 计算
     *
     * @param node 节点
     * @param key  关键字
     */
    public abstract void calCalculateTarget(BICrossNode node, TargetGettingKey key);

    /**
     * 计算
     *
     * @param cr   索引
     * @param node node节点
     */
    @Override
    public void doCalculator(ICubeTableService cr, SummaryContainer node) {
        if (node instanceof BINode) {
            calCalculateTarget((BINode) node);
        } else {
            calCalculateTarget((BICrossNode) node, createTargetGettingKey());
        }
    }

    /**
     * 计算
     *
     * @param cr   索引
     * @param node node节点
     */
    @Override
    public void doCalculator(ICubeTableService cr, SummaryContainer node, TargetGettingKey key) {
    }

    @Override
    public TargetGettingKey createTargetGettingKey() {
        if (targetGettingKey == null) {
            targetGettingKey = new TargetGettingKey(this.createTargetKey(), targetName);
        }
        return targetGettingKey;
    }

    /**
     * @deprecated no use
     */
    /**
     * 创建计算
     *
     * @param cr   索引
     * @param node node节点
     * @return SummaryCalculator
     */
    @Override
    public SummaryCalculator createSummaryCalculator(ICubeTableService cr, SummaryContainer node) {
        return null;
    }


    /**
     * @return 指标数组
     * @deprecated 创建计算指标
     */
    @Override
    public TargetCalculator[] createTargetCalculators() {
        return new TargetCalculator[0];
    }


    /**
     * 计算索引
     */
    @Override
    public void calculateFilterIndex(ICubeDataLoader loader) {
    }


    @Override
    public BusinessTable createTableKey() {
        return target.createTableKey();
    }

    @Override
    public String getName() {
        return targetName;
    }

}