package com.fr.bi.field.target.calculator.sum;

import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.field.target.target.BISummaryTarget;
import com.fr.bi.field.target.target.SumType;
import com.fr.bi.stable.gvi.GroupValueIndex;

/**
 * Created by 小灰灰 on 2015/7/2.
 */
public class StringAppendCalculator extends AbstractSummaryCalculator {
    public StringAppendCalculator(BISummaryTarget target) {
        super(target);
    }

    /**
     * 创建sum值
     *
     * @param gvi 索引
     * @param ti  索引
     * @return double值
     */
    @Override
    public double createSumValue(GroupValueIndex gvi, ICubeTableService ti) {
        return 0;
    }

    @Override
    public SumType getSumType() {
        return SumType.GVI;
    }

    @Override
    public double createSumValue(double v1, double v2) {
        throw new RuntimeException("not SumType.GVI");
    }

}