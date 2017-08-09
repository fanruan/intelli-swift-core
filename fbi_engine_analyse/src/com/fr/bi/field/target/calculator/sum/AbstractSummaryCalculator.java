package com.fr.bi.field.target.calculator.sum;

import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.field.target.target.BISummaryTarget;
import com.fr.bi.field.target.target.SumType;
import com.fr.bi.report.result.CalculatorType;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.report.key.TargetGettingKey;
import com.fr.bi.report.result.SummaryContainer;
import com.fr.bi.report.result.TargetCalculator;
import com.fr.general.ComparatorUtils;

/**
 * Created by 小灰灰 on 2015/6/30.
 */
public abstract class AbstractSummaryCalculator implements TargetCalculator {
    protected BISummaryTarget target;
    private TargetGettingKey targetGettingKey;
    /**
     * 计算索引
     */
    private Object filterLock = new Object();
    private transient GroupValueIndex filterIndex = null;

    public AbstractSummaryCalculator(BISummaryTarget target) {
        this.target = target;
        this.targetGettingKey = new TargetGettingKey(target.getSummaryIndex(), getName());
    }


    @Override
    public void calculateFilterIndex(ICubeDataLoader loader) {
        if (target.getTargetFilter() == null || filterIndex != null) {
            return;
        }
        synchronized (filterLock) {
            if (target.getTargetFilter() != null && filterIndex == null) {
                filterIndex = target.getTargetFilter().createFilterIndex(this.createTableKey(), loader, loader.getUserId());
            }
        }
    }

    @Override
    public BusinessTable createTableKey() {
        return target.createTableKey();
    }

    @Override
    public String getName() {
        return target.getValue();
    }

    @Override
    public TargetGettingKey createTargetGettingKey() {
        return targetGettingKey;
    }

    /**
     * 计算
     *
     * @param node node节点
     */
    @Override
    public void doCalculator(ICubeTableService ti, SummaryContainer node, GroupValueIndex gvi, TargetGettingKey key) {
        runTraversal(ti, node, gvi, key);
    }

    protected void runTraversal(ICubeTableService ti, SummaryContainer node, GroupValueIndex gvi, TargetGettingKey key) {
        if (gvi != null) {
            if (target.getTargetFilter() != null) {
                gvi = gvi.AND(filterIndex);
            }
            if (gvi != null && !gvi.isAllEmpty()) {
                node.setSummaryValue(key, createSumValue(gvi, ti));
            }
        }
    }

    /**
     * 创建sum值
     *
     * @param gvi 索引
     * @param ti  索引
     * @return double值
     */
    public abstract double createSumValue(GroupValueIndex gvi, ICubeTableService ti);

    public abstract double createSumValue(double v1, double v2);

    @Override
    public CalculatorType getCalculatorType() {
        return CalculatorType.SUM_DETAIL;
    }

    @Override
    public boolean isSumTypePlus() {
        return getSumType() == SumType.PLUS;
    }

    public SumType getSumType(){
        return SumType.PLUS;
    }

    public GroupValueIndex getFilterIndex() {
        return filterIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AbstractSummaryCalculator that = (AbstractSummaryCalculator) o;
        return ComparatorUtils.equals(target, that.target);
    }

}