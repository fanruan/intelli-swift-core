package com.fr.bi.field.target.calculator.sum;

import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.field.target.target.BISummaryTarget;
import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.report.key.SummaryCalculator;
import com.fr.bi.stable.report.key.TargetGettingKey;
import com.fr.bi.stable.report.result.SummaryContainer;
import com.fr.bi.stable.report.result.TargetCalculator;

/**
 * Created by 小灰灰 on 2015/6/30.
 */
public abstract class AbstractSummaryCalculator implements TargetCalculator {
    protected BISummaryTarget target;
    /**
     * 计算索引
     */
    private Object filterLock = new Object();
    private transient GroupValueIndex filterIndex = null;

    public AbstractSummaryCalculator(BISummaryTarget target) {
        this.target = target;
    }


    @Override
    public void calculateFilterIndex(ICubeDataLoader loader) {
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

    /**
     * 创建 SummaryCalculator
     *
     * @param cr   tableindex对象
     * @param node 节点
     * @return 创建的SummaryCalculator
     */
    @Override
    public SummaryCalculator createSummaryCalculator(ICubeTableService cr, SummaryContainer node) {
        return new SummaryCalculator(cr, this, node);
    }

    @Override
    public String getName() {
        return target.getValue();
    }

    @Override
    public TargetGettingKey createTargetGettingKey() {
        return new TargetGettingKey(createTargetKey(), getName());
    }

    /**
     * 计算
     *
     * @param node node节点
     */
    @Override
    public void doCalculator(ICubeTableService ti, SummaryContainer node) {
        runTraversal(ti, node, node.getIndex4Cal());
    }

    @Override
    public void doCalculator(ICubeTableService ti, SummaryContainer node, TargetGettingKey tk) {
        runTraversal(ti, node, node.getIndex4CalByTargetKey(tk));
    }

    protected void runTraversal(ICubeTableService ti, SummaryContainer node, GroupValueIndex gvi) {
        if (gvi != null) {
            if (target.getTargetFilter() != null) {
                gvi = gvi.AND(filterIndex);
            }
            if (gvi != null) {
                runValue(ti, node, gvi);
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

    protected void runValue(ICubeTableService ti, SummaryContainer node, GroupValueIndex gvi) {
        if (!gvi.isAllEmpty()) {
            node.setSummaryValue(createTargetGettingKey(), createSumValue(gvi, ti));
        }
    }


    /**
     * 创建指标
     *
     * @return 指标数组
     */
    @Override
    public TargetCalculator[] createTargetCalculators() {
        return new TargetCalculator[]{this};
    }
}