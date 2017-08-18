package com.fr.bi.field.target.calculator.sum;

import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.field.target.target.BISummaryTarget;
import com.fr.bi.field.target.target.SumType;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.io.newio.NIOConstant;

/**
 * Created by 小灰灰 on 2015/6/30.
 */
public class AvgCalculator extends AbstractSummaryCalculator {

    private static final long serialVersionUID = -2976871776170710571L;

    public AvgCalculator(BISummaryTarget target) {
        super(target);
    }

    @Override
    public double createSumValue(GroupValueIndex gvi, ICubeTableService ti) {
        if (gvi == null || gvi.isAllEmpty()){
            return NIOConstant.DOUBLE.NULL_VALUE;
        }
        return ti.getSUMValue(gvi, target.createKey(target.getStatisticElement())) / gvi.getRowsCountWithData();
    }

    @Override
    public double createSumValue(double v1, double v2) {
        throw new RuntimeException("not SumType.GVI");
    }

    @Override
    public SumType getSumType() {
        return SumType.GVI;
    }
}