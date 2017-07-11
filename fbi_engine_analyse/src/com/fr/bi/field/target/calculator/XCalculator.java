package com.fr.bi.field.target.calculator;

import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.cal.analyze.cal.result.XLeftNode;
import com.fr.bi.field.target.calculator.sum.AbstractSummaryCalculator;
import com.fr.bi.report.key.TargetGettingKey;
import com.fr.bi.report.key.XTargetGettingKey;
import com.fr.bi.report.result.CalculatorType;
import com.fr.bi.report.result.SummaryContainer;
import com.fr.bi.report.result.TargetCalculator;
import com.fr.bi.stable.gvi.GVIUtils;
import com.fr.bi.stable.gvi.GroupValueIndex;

/**
 * Created by 小灰灰 on 2017/7/7.
 * 交叉表的calculator，转化为列表头的指标过滤来做
 */
public class XCalculator implements TargetCalculator{
    private TargetCalculator calculator;
    private GroupValueIndex[] topIndex;
    private GroupValueIndex[] filterIndex;

    public XCalculator(TargetCalculator calculator, GroupValueIndex[] topIndex) {
        this.calculator = calculator;
        this.topIndex = topIndex;
    }

    @Override
    public void doCalculator(ICubeTableService cr, SummaryContainer node, GroupValueIndex gvi, TargetGettingKey key) {
        if (key instanceof XTargetGettingKey && calculator.getCalculatorType() == CalculatorType.SUM_DETAIL){
            XLeftNode xLeftNode = (XLeftNode) node;
            AbstractSummaryCalculator summaryCalculator = (AbstractSummaryCalculator) calculator;
            if (gvi != null) {
                int index = ((XTargetGettingKey) key).getSubIndex();
                GroupValueIndex groupValueIndex = null;
                if (filterIndex[index] != null) {
                    groupValueIndex = gvi.AND(filterIndex[index]);
                }
                if (groupValueIndex != null && !groupValueIndex.isAllEmpty()) {
                    xLeftNode.setXValue((XTargetGettingKey)key, summaryCalculator.createSumValue(groupValueIndex, cr));
                }
            }
        }
    }

    @Override
    public void calculateFilterIndex(ICubeDataLoader loader) {
        calculator.calculateFilterIndex(loader);
        if (filterIndex != null) {
            return;
        }
        synchronized (this) {
            if (filterIndex == null) {
                GroupValueIndex [] indexes = new GroupValueIndex[topIndex.length];
                if (calculator.getCalculatorType() == CalculatorType.SUM_DETAIL){
                    AbstractSummaryCalculator summaryCalculator = (AbstractSummaryCalculator) calculator;
                    for (int i = 0; i < topIndex.length; i++){
                        indexes[i] = GVIUtils.AND(topIndex[i], summaryCalculator.getFilterIndex());
                    }
                }
                filterIndex = indexes;
            }
        }
    }

    @Override
    public BusinessTable createTableKey() {
        return calculator.createTableKey();
    }

    @Override
    public String getName() {
        return calculator.getName();
    }

    @Override
    public TargetGettingKey createTargetGettingKey() {
        return calculator.createTargetGettingKey();
    }

    @Override
    public CalculatorType getCalculatorType() {
        return CalculatorType.X_SUM;
    }
}
