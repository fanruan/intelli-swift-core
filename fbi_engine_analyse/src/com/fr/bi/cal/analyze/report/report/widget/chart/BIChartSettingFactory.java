package com.fr.bi.cal.analyze.report.report.widget.chart;

import com.fr.bi.cal.analyze.report.report.widget.MultiChartWidget;
import com.fr.bi.cal.analyze.report.report.widget.chart.newstyle.*;
import com.fr.bi.cal.analyze.report.report.widget.chart.newstyle.BIChartSetting;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.json.JSONObject;

/**
 * Created by User on 2016/8/31.
 */
public class BIChartSettingFactory {

    public static BIChartSetting parseChartSetting(MultiChartWidget widget, JSONObject data) throws Exception {
        int type = widget.getType();
        BIChartSetting chartSetting = newChartSettingByType(type);
        //chartSetting.parseJSON(jo);
        return chartSetting;
    }


    public static BIChartSetting newChartSettingByType(int type) throws Exception {
        switch (type) {
            case BIReportConstant.WIDGET.AXIS:
            case BIReportConstant.WIDGET.COMBINE_CHART:
                return new AxisChartSetting();
            case BIReportConstant.WIDGET.ACCUMULATE_AXIS:
                return new AccumulateAxisChartSetting();
            case BIReportConstant.WIDGET.PERCENT_ACCUMULATE_AXIS:
                return new PercentAccumulateAxisChartSetting();
            case BIReportConstant.WIDGET.COMPARE_AXIS:
                return new CompareAxisChartSetting();
            case BIReportConstant.WIDGET.FALL_AXIS:
                return new FallAxisChartSetting();
            case BIReportConstant.WIDGET.BAR:
                return new BarChartSetting();
            case BIReportConstant.WIDGET.COMPARE_BAR:
                return new CompareBarChartSetting();
            case BIReportConstant.WIDGET.ACCUMULATE_BAR:
                return new AccumulateBarChartSetting();
            case BIReportConstant.WIDGET.LINE:
                return new LineChartSetting();
            case BIReportConstant.WIDGET.AREA:
                return new AreaChartSetting();
            case BIReportConstant.WIDGET.COMPARE_AREA:
                return new CompareAreaChartSetting();
            case BIReportConstant.WIDGET.RANGE_AREA:
                return new RangeAreaChartSetting();
            case BIReportConstant.WIDGET.ACCUMULATE_AREA:
                return new AccumulateAreaChartSetting();
            case BIReportConstant.WIDGET.PERCENT_ACCUMULATE_AREA:
                return new PercentAccumulateAreaChartSetting();
            case BIReportConstant.WIDGET.MULTI_AXIS_COMBINE_CHART:
                return new MultiAxisChartSetting();
            case BIReportConstant.WIDGET.MAP:
                return new MapChartSetting();
            case BIReportConstant.WIDGET.GIS_MAP:
                return new GISMapChartSetting();
            case BIReportConstant.WIDGET.PIE:
                return new PieChartSetting();
            case BIReportConstant.WIDGET.DONUT:
                return new DonutChartSetting();
            case BIReportConstant.WIDGET.DASHBOARD:
                return new DashboardChartSetting();
            case BIReportConstant.WIDGET.RADAR:
                return new RadarChartSetting();
            case BIReportConstant.WIDGET.ACCUMULATE_RADAR:
                return new AccumulateRadarChartSetting();
            case BIReportConstant.WIDGET.BUBBLE:
                return new BubbleChartSetting();
            case BIReportConstant.WIDGET.FORCE_BUBBLE:
                return new ForceBubbleChartSetting();
            case BIReportConstant.WIDGET.SCATTER:
                return new ScatterChartSetting();
            default:
                return new AxisChartSetting();
        }
    }
}
