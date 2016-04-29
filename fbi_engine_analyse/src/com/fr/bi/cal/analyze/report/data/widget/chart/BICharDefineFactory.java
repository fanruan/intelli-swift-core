package com.fr.bi.cal.analyze.report.data.widget.chart;

import com.fr.bi.conf.fs.FBIConfig;
import com.fr.bi.stable.constant.BIExcutorConstant.CHART;

/**
 * Created by GUY on 2015/4/8.
 */
public class BICharDefineFactory {

    public static BIChartDefine getChartDefine(int type) {
        return FBIConfig.getInstance().getChartStyleAttr().getChartStyle() == 0 ? get3DChartDefine(type) : get2DChartDefine(type);
    }

    private static  BIChartDefine get2DChartDefine(int type) {
        switch (type) {
            case CHART.BAR: {
                return BIChartConstant.CHARTDEFINE.BAR1;
            }
            case CHART.ACCUMULATED_BAR: {
                return BIChartConstant.CHARTDEFINE.ACCUMULATED_BAR1;
            }
            case CHART.LINE: {
                return BIChartConstant.CHARTDEFINE.LINE;
            }
            case CHART.PIE: {
                return BIChartConstant.CHARTDEFINE.PIE1;
            }
            case CHART.TIAO: {
                return BIChartConstant.CHARTDEFINE.TIAO1;
            }
            case CHART.ACCUMULATED_TIAO: {
                return BIChartConstant.CHARTDEFINE.ACCUMULATED_TIAO1;
            }
            case CHART.SQUARE: {
                return BIChartConstant.CHARTDEFINE.SQUARE1;
            }
            case CHART.DASHBOARD: {
                return BIChartConstant.CHARTDEFINE.DASHBOARD;
            }
            case CHART.MAP: {
                return BIChartConstant.CHARTDEFINE.MAP;
            }
            case CHART.DONUT: {
                return BIChartConstant.CHARTDEFINE.DONUT1;
            }
            case CHART.RADAR: {
                return BIChartConstant.CHARTDEFINE.RADAR1;
            }
            default: {
                return BIChartConstant.CHARTDEFINE.BAR0;
            }
        }
    }

    private static  BIChartDefine get3DChartDefine(int type) {
        switch (type) {
            case CHART.BAR: {
                return BIChartConstant.CHARTDEFINE.BAR0;
            }
            case CHART.ACCUMULATED_BAR: {
                return BIChartConstant.CHARTDEFINE.ACCUMULATED_BAR0;
            }
            case CHART.LINE: {
                return BIChartConstant.CHARTDEFINE.LINE;
            }
            case CHART.PIE: {
                return BIChartConstant.CHARTDEFINE.PIE0;
            }
            case CHART.TIAO: {
                return BIChartConstant.CHARTDEFINE.TIAO0;
            }
            case CHART.ACCUMULATED_TIAO: {
                return BIChartConstant.CHARTDEFINE.ACCUMULATED_TIAO0;
            }
            case CHART.SQUARE: {
                return BIChartConstant.CHARTDEFINE.SQUARE0;
            }
            case CHART.DASHBOARD: {
                return BIChartConstant.CHARTDEFINE.DASHBOARD;
            }
            case CHART.MAP: {
                return BIChartConstant.CHARTDEFINE.MAP;
            }
            case CHART.DONUT: {
                return BIChartConstant.CHARTDEFINE.DONUT0;
            }
            case CHART.RADAR: {
                return BIChartConstant.CHARTDEFINE.RADAR1;
            }
            default: {
                return BIChartConstant.CHARTDEFINE.BAR0;
            }
        }
    }

}