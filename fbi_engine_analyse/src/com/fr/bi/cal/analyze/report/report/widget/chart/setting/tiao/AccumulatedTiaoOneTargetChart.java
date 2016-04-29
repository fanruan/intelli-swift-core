package com.fr.bi.cal.analyze.report.report.widget.chart.setting.tiao;

import com.fr.bi.field.target.target.BISummaryTarget;
import com.fr.bi.stable.constant.BIExcutorConstant;

public class AccumulatedTiaoOneTargetChart extends TiaoOneTargetChart {
    @Override
    protected int getChartType(BISummaryTarget target) {
        return BIExcutorConstant.CHART.ACCUMULATED_TIAO;
    }

}