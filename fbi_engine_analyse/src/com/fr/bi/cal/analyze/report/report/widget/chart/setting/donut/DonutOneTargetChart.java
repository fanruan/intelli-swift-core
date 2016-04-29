/**
 *
 */
package com.fr.bi.cal.analyze.report.report.widget.chart.setting.donut;

import com.fr.bi.cal.analyze.report.report.widget.chart.setting.tiao.TiaoOneTargetChart;
import com.fr.bi.field.target.target.BISummaryTarget;
import com.fr.bi.stable.constant.BIExcutorConstant;


public class DonutOneTargetChart extends TiaoOneTargetChart {

    @Override
    protected int getChartType(BISummaryTarget target) {
        return BIExcutorConstant.CHART.DONUT;
    }

    @Override
	protected  boolean hasAlert(){
        return false;
    }
}