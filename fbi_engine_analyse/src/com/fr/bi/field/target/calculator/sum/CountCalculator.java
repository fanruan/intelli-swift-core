package com.fr.bi.field.target.calculator.sum;

import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.field.target.target.BISummaryTarget;
import com.fr.bi.field.target.target.NoneTargetCountTarget;
import com.fr.bi.field.target.target.SumType;
import com.fr.bi.stable.engine.index.key.IndexKey;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.stable.StringUtils;

/**
 * Created by 小灰灰 on 2015/7/2.
 */
public class CountCalculator extends AbstractSummaryCalculator {
    public static final CountCalculator NONE_TARGET_COUNT_CAL = new CountCalculator(new NoneTargetCountTarget(), "pony");
    private static final long serialVersionUID = -3263413185870966424L;
    private String countTarget = StringUtils.EMPTY;

    public CountCalculator(BISummaryTarget target, String countTarget) {
        super(target);
        this.countTarget = countTarget;
    }

    @Override
    public SumType getSumType() {
        return StringUtils.isNotEmpty(countTarget) && countTarget != NONE_TARGET_COUNT_CAL.countTarget ? SumType.GVI : SumType.PLUS;
    }

    @Override
    public double createSumValue(double v1, double v2) {
        if (StringUtils.isNotEmpty(countTarget) && countTarget != NONE_TARGET_COUNT_CAL.countTarget) {
            throw new RuntimeException("not SumType.PLUS");
        }
        return v1 + v2;
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
        if (StringUtils.isNotEmpty(countTarget) && countTarget != NONE_TARGET_COUNT_CAL.countTarget) {
            return ti.getDistinctCountValue(gvi, new IndexKey(countTarget));
        } else {
            return gvi == null ? 0 : gvi.getRowsCountWithData();
        }
    }
}