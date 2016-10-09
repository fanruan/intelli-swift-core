package com.fr.bi.cal.analyze.report.report.widget.chart;

import com.fr.bi.cal.analyze.report.report.widget.MultiChartWidget;
import com.fr.bi.cal.analyze.report.report.widget.chart.newstyle.*;
import com.fr.bi.cal.analyze.report.report.widget.chart.newstyle.BIChartSetting;
import com.fr.bi.stable.constant.BIChartSettingConstant;
import com.fr.bi.stable.constant.BIReportConstant;
import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.general.ComparatorUtils;
import com.fr.general.Inter;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;

import java.util.Iterator;

/**
 * Created by User on 2016/8/31.
 */
public class BIChartSettingFactory {

    public static JSONObject parseChartSetting(MultiChartWidget widget, JSONArray data, JSONObject options, JSONArray types){
        int type = widget.getType();
        BIChartSetting chartSetting = null;
        try {
            chartSetting = newChartSettingByType(type);
            JSONObject settings = widget.getChatSetting().getDetailChartSetting();
            boolean isMinimalistModel = false;
            if(settings.optBoolean("minimalist_model", false)){
                for(int i = 0; i < BIChartSettingConstant.MINIMALIST_WIDGET.length; i++){
                    if(BIChartSettingConstant.MINIMALIST_WIDGET[i] == type){
                        isMinimalistModel = true;
                        break;
                    }
                }
            }
            JSONObject op;
            JSONArray chartColor = settings.optJSONArray("chart_color");
            if(chartColor == null){
                chartColor = new JSONArray();
                for(int i = 0; i < BIChartSettingConstant.CHART_COLOR.length; i++){
                    chartColor.put(BIChartSettingConstant.CHART_COLOR[i]);
                }
            }
            if(isMinimalistModel){
                op = new JSONObject("{" +
                        "chart_color:" + chartColor + "," +
                        "chart_style:\"" + settings.optInt("chart_style", BIChartSettingConstant.CHART_STYLE.STYLE_NORMAL) + "\"," +
                        "chart_line_type:\"" + settings.optInt("chart_line_type", BIChartSettingConstant.CHART_SHAPE.NORMAL) + "\"," +
                        "transfer_filter:\"" + settings.optBoolean("transfer_filter", true) + "\"," +
                        "left_y_axis_reversed:\"" + settings.optBoolean("left_y_axis_reversed", false) + "\"," +
                        "right_y_axis_reversed:\"" + settings.optBoolean("right_y_axis_reversed", false) + "\"," +
                        "line_width:\"" + settings.optInt("line_width", BIChartSettingConstant.LINE_WIDTH.ZERO) + "\"," +
                        "show_label:\"" + settings.optBoolean("show_label", false) + "\"," +
                        "enable_tick:\"" + settings.optBoolean("enable_tick", false) + "\"," +
                        "left_y_axis_unit:\"" + settings.optString("left_y_axis_unit", "") + "\"," +
                        "show_x_axis_title:\"" + settings.optString("show_x_axis_title", "") + "\"," +
                        "show_left_y_axis_title:\"" + settings.optBoolean("show_left_y_axis_title", false) + "\"," +
                        "chart_legend:\"" + settings.optInt("chart_legend", BIChartSettingConstant.CHART_LEGENDS.NOT_SHOW) + "\"," +
                        "show_data_label:\"" + settings.optBoolean("show_data_label", true) + "\"," +
                        "enable_minor_tick:\"" + settings.optBoolean("enable_minor_tick", false) + "\"," +
                        "show_grid_line:" + settings.optBoolean("show_grid_line", false) + "}");
            }else{
                op = new JSONObject("{" +
                        "chart_color:" + chartColor + "," +
                        "chart_style:\"" + settings.optInt("chart_style", BIChartSettingConstant.CHART_STYLE.STYLE_NORMAL) + "\"," +
                        "chart_line_type:\"" + settings.optInt("chart_line_type", BIChartSettingConstant.CHART_SHAPE.NORMAL) + "\"," +
                        "line_width:\"" + settings.optInt("line_width", BIChartSettingConstant.LINE_WIDTH.ONE) + "\"," +
                        "show_label:\"" + settings.optBoolean("show_label", true) + "\"," +
                        "enable_tick:\"" + settings.optBoolean("enable_tick", true) + "\"," +
                        "chart_pie_type:\"" + settings.optInt("chart_pie_type", BIChartSettingConstant.CHART_SHAPE.NORMAL) + "\"," +
                        "chart_radar_type:\"" + settings.optInt("chart_radar_type", BIChartSettingConstant.CHART_SHAPE.POLYGON) + "\"," +
                        "chart_dashboard_type:\"" + settings.optInt("chart_dashboard_type", BIChartSettingConstant.CHART_SHAPE.NORMAL) + "\"," +
                        "chart_inner_radius:\"" + settings.optInt("chart_inner_radius", 0) + "\"," +
                        "chart_total_angle:\"" + settings.optInt("chart_total_angle", BIChartSettingConstant.PIE_ANGLES.TOTAL) + "\"," +
                        "left_y_axis_style:\"" + settings.optInt("left_y_axis_style", BIChartSettingConstant.CHART_TARGET_STYLE.FORMAT.NORMAL) + "\"," +
                        "x_axis_style:\"" + settings.optInt("x_axis_style", BIChartSettingConstant.CHART_TARGET_STYLE.FORMAT.NORMAL) + "\"," +
                        "right_y_axis_style:\"" + settings.optInt("right_y_axis_style", BIChartSettingConstant.CHART_TARGET_STYLE.FORMAT.NORMAL) + "\"," +
                        "right_y_axis_second_style:\"" + settings.optInt("right_y_axis_second_style", BIChartSettingConstant.CHART_TARGET_STYLE.FORMAT.NORMAL) + "\"," +
                        "left_y_axis_number_level:\"" + settings.optInt("left_y_axis_number_level", BIChartSettingConstant.CHART_TARGET_STYLE.FORMAT.NORMAL) + "\"," +
                        "number_of_pointer:\"" + settings.optInt("number_of_pointer", BIChartSettingConstant.POINTER.ONE) + "\"," +
                        "dashboard_number_level:\"" + settings.optInt("dashboard_number_level", BIChartSettingConstant.CHART_TARGET_STYLE.NUM_LEVEL.NORMAL) + "\"," +
                        "x_axis_number_level:\"" + settings.optInt("x_axis_number_level", BIChartSettingConstant.CHART_TARGET_STYLE.NUM_LEVEL.NORMAL) + "\"," +
                        "right_y_axis_number_level:\"" + settings.optInt("right_y_axis_number_level", BIChartSettingConstant.CHART_TARGET_STYLE.NUM_LEVEL.NORMAL) + "\"," +
                        "right_y_axis_second_number_level:\"" + settings.optInt("right_y_axis_second_number_level", BIChartSettingConstant.CHART_TARGET_STYLE.NUM_LEVEL.NORMAL) + "\"," +
                        "left_y_axis_unit:\"" + settings.optString("left_y_axis_unit", "") + "\"," +
                        "dashboard_unit:\"" + settings.optString("dashboard_unit", "") + "\"," +
                        "x_axis_unit:\"" + settings.optString("x_axis_unit", "") + "\"," +
                        "right_y_axis_unit:\"" + settings.optString("right_y_axis_unit", "") + "\"," +
                        "right_y_axis_second_unit:\"" + settings.optString("right_y_axis_second_unit", "") + "\"," +
                        "show_left_y_axis_title:\"" + settings.optString("show_left_y_axis_title", "") + "\"," +
                        "show_right_y_axis_second_title:\"" + settings.optString("show_right_y_axis_second_title", "") + "\"," +
                        "left_y_axis_title:\"" + settings.optString("left_y_axis_title", "") + "\"," +
                        "right_y_axis_title:\"" + settings.optString("right_y_axis_title", "") + "\"," +
                        "right_y_axis_second_title:\"" + settings.optString("right_y_axis_second_title", "") + "\"," +
                        "left_y_axis_reversed:\"" + settings.optBoolean("left_y_axis_reversed", false) + "\"," +
                        "right_y_axis_reversed:\"" + settings.optBoolean("right_y_axis_reversed", false) + "\"," +
                        "right_y_axis_second_reversed:\"" + settings.optBoolean("right_y_axis_second_reversed", false) + "\"," +
                        "show_x_axis_title:\"" + settings.optBoolean("show_x_axis_title", false) + "\"," +
                        "x_axis_title:\"" + settings.optString("x_axis_title", "") + "\"," +
                        "text_direction:\"" + settings.optString("text_direction", "0") + "\"," +
                        "chart_legend:\"" + settings.optInt("chart_legend", BIChartSettingConstant.CHART_LEGENDS.BOTTOM) + "\"," +
                        "show_data_label:\"" + settings.optBoolean("show_data_label", false) + "\"," +
                        "show_data_table:\"" + settings.optBoolean("show_data_table", false) + "\"," +
                        "enable_minor_tick:\"" + settings.optBoolean("enable_minor_tick", true) + "\"," +
                        "show_grid_line:\"" + settings.optBoolean("show_grid_line", true) + "\"," +
                        "show_zoom:\"" + settings.optBoolean("show_zoom", false) + "\"," +
                        "style_conditions:" + settings.optJSONArray("style_conditions") + "," +
                        "auto_custom:\"" + settings.optInt("auto_custom", BIChartSettingConstant.SCALE_SETTING.AUTO) + "\"," +
                        "theme_color:\"" + settings.optString("theme_color", "#65bce7") + "\"," +
                        "transfer_filter:\"" + settings.optBoolean("transfer_filter", true) + "\"," +
                        "rules_display:\"" + settings.optInt("rules_display", BIChartSettingConstant.DISPLAY_RULES.DIMENSION) + "\"," +
                        "bubble_style:\"" + settings.optInt("bubble_style", BIChartSettingConstant.CHART_SHAPE.NO_PROJECTOR) + "\"," +
                        "max_scale:\"" + settings.optString("max_scale", "") + "\"," +
                        "min_scale:\"" + settings.optString("min_scale", "") + "\"," +
                        "show_percentage:\"" + settings.optBoolean("show_percentage", false) + "\"," +
                        "show_background_layer:\"" + settings.optBoolean("show_background_layer", true) + "\"," +
                        "background_layer_info:\"" + settings.optString("background_layer_info", Inter.getLocText("BI-GAO_DE_MAP")) + "\"," +
                        "num_separators:\"" + settings.optBoolean("num_separators", false) + "\"," +
                        "right_num_separators:\"" + settings.optBoolean("right_num_separators", false) + "\"," +
                        "right2_num_separators:\"" + settings.optBoolean("right2_num_separators", false) + "\"," +
                        "map_styles:" + settings.optJSONArray("map_styles") + "}"
                );
            }
            Iterator it = options.keys();
            while (it.hasNext()){
                String key = it.next().toString();
                if(isMinimalistModel && ComparatorUtils.equals(key, "cordon")){
                    continue;
                }
                op.put(key, options.get(key));
            }
            return chartSetting.getConvertedDataAndSettings(data, types, op);
        }catch (Exception e){
            BILoggerFactory.getLogger().error(e.getMessage());
        }
        return new JSONObject();
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
