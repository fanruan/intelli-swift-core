package com.fr.bi.cal.analyze.report.report.widget.chart.setting.radar;

import com.fr.bi.cal.analyze.report.report.widget.chart.setting.tiao.TiaoOneTargetChart;
import com.fr.bi.field.target.target.BISummaryTarget;
import com.fr.bi.stable.constant.BIExcutorConstant;

public class RadarOneTargetChart extends TiaoOneTargetChart {

    @Override
    protected int getChartType(BISummaryTarget target) {
        return BIExcutorConstant.CHART.RADAR;
    }

    @Override
	protected  boolean hasAlert(){
        return true;
    }
}