package com.fr.bi.cal.analyze.report.report.widget.chart.setting.radar;

import com.fr.bi.cal.analyze.report.report.widget.chart.setting.tiao.TiaoMutiTargetChart;
import com.fr.bi.stable.constant.BIExcutorConstant;

public class RadarMutiTargetChart extends TiaoMutiTargetChart {

    @Override
    protected int getChartType() {
        return BIExcutorConstant.CHART.RADAR;
    }

    @Override
	protected boolean hasAlert(){
        return false;
    }

}