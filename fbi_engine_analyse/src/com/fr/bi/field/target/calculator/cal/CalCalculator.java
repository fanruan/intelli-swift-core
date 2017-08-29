package com.fr.bi.field.target.calculator.cal;

import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.report.result.BIXLeftNode;
import com.fr.bi.conf.report.widget.field.target.BITarget;
import com.fr.bi.field.target.target.cal.BICalculateTarget;
import com.fr.bi.report.key.TargetGettingKey;
import com.fr.bi.report.key.XTargetGettingKey;
import com.fr.bi.report.result.BINode;
import com.fr.bi.report.result.CalculatorType;
import com.fr.bi.report.result.SummaryContainer;
import com.fr.bi.report.result.TargetCalculator;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.general.ComparatorUtils;

import java.util.Map;
import java.util.Set;

/**
 * Created by 小灰灰 on 2015/7/2.
 */
public abstract class CalCalculator implements TargetCalculator {
    private static final long serialVersionUID = 453792138848574486L;

    protected BICalculateTarget target;

    protected Map<String, BITarget> targetMap;
    private TargetGettingKey targetGettingKey;

    public CalCalculator(BICalculateTarget target) {
        this.target = target;
        this.targetMap = target.getTargetMap();
        this.targetGettingKey = new TargetGettingKey(target.getSummaryIndex(), target.getName());
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
    public abstract void calCalculateTarget(BINode node);

    /**
     *
     * 计算
     * @param node 节点
     * @param key  关键字
     */
    public abstract void calCalculateTarget(BIXLeftNode node, XTargetGettingKey key);

    /**
     * 计算
     *
     * @param cr   索引
     * @param node node节点
     */
    @Override
    public void doCalculator(ICubeTableService cr, SummaryContainer node, GroupValueIndex gvi, TargetGettingKey key) {
        if (node instanceof BINode) {
            calCalculateTarget((BINode) node);
        }
    }

    @Override
    public TargetGettingKey createTargetGettingKey() {
        return targetGettingKey;
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
        return target.getName();
    }

    @Override
    public CalculatorType getCalculatorType() {
        return CalculatorType.CAL_SUM;
    }

    @Override
    public boolean isSumTypePlus() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CalCalculator that = (CalCalculator) o;
        return ComparatorUtils.equals(target, that.target);
    }
}