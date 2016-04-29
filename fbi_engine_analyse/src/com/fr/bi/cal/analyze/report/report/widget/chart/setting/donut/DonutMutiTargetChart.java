/**
 *
 */
package com.fr.bi.cal.analyze.report.report.widget.chart.setting.donut;

import com.fr.bi.cal.analyze.report.report.widget.chart.setting.tiao.TiaoMutiTargetChart;
import com.fr.bi.stable.constant.BIExcutorConstant;


public class DonutMutiTargetChart extends TiaoMutiTargetChart {

    @Override
    protected int getChartType() {
        return BIExcutorConstant.CHART.DONUT;
    }

    @Override
	protected boolean hasAlert(){
        return false;
    }

}