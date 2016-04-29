package com.fr.bi.cal.analyze.report.report.widget.chart.setting.tiao;

import com.fr.bi.stable.constant.BIExcutorConstant;

public class AccumulatedTiaoMutiTargetChart extends TiaoMutiTargetChart {

    @Override
    protected int getChartType() {
        return BIExcutorConstant.CHART.ACCUMULATED_TIAO;
    }
}