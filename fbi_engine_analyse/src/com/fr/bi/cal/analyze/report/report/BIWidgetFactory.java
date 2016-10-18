package com.fr.bi.cal.analyze.report.report;

import com.finebi.cube.api.BICubeManager;
import com.finebi.cube.api.ICubeDataLoader;
import com.fr.bi.cal.analyze.report.report.widget.*;
import com.fr.bi.conf.report.BIWidget;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;

/**
 * Widget的静态方法
 *
 * @author Daniel-pc
 */
public class BIWidgetFactory {

    /**
     * 根据属性选择生成不同的widget
     *
     * @param jo JSONObject 对象
     * @return BIWidget
     * @throws Exception
     */
    public static BIWidget parseWidget(JSONObject jo, long userId, ICubeDataLoader loader) throws Exception {
        JSONObject view = jo.optJSONObject("view");
        if (view == null) {
            view = new JSONObject();
        }
        int type = jo.optInt("type");
        JSONArray viewTargets = getViewTarget(view);
        BIWidget widget = newWidgetByType(type, viewTargets);
        widget.parseJSON(jo, userId);
        return widget;
    }

    public static BIWidget newWidgetByType(int type, JSONArray viewTargets) throws Exception {
        switch (type) {
            case BIReportConstant.WIDGET.BAR:
            case BIReportConstant.WIDGET.AXIS:
            case BIReportConstant.WIDGET.ACCUMULATE_AXIS:
            case BIReportConstant.WIDGET.COMPARE_BAR:
            case BIReportConstant.WIDGET.COMPARE_AXIS:
            case BIReportConstant.WIDGET.COMPARE_AREA:
            case BIReportConstant.WIDGET.RANGE_AREA:
            case BIReportConstant.WIDGET.LINE:
            case BIReportConstant.WIDGET.AREA:
            case BIReportConstant.WIDGET.ACCUMULATE_AREA:
            case BIReportConstant.WIDGET.COMBINE_CHART:
            case BIReportConstant.WIDGET.MULTI_AXIS_COMBINE_CHART:
            case BIReportConstant.WIDGET.MAP:
            case BIReportConstant.WIDGET.ACCUMULATE_BAR:
            case BIReportConstant.WIDGET.PIE:
            case BIReportConstant.WIDGET.DASHBOARD:
            case BIReportConstant.WIDGET.DONUT:
            case BIReportConstant.WIDGET.RADAR:
            case BIReportConstant.WIDGET.SCATTER:
            case BIReportConstant.WIDGET.BUBBLE:
                return new MultiChartWidget();
            case BIReportConstant.WIDGET.TABLE:
            case BIReportConstant.WIDGET.CROSS_TABLE:
            case BIReportConstant.WIDGET.COMPLEX_TABLE:
                return new TableWidget();
            case BIReportConstant.WIDGET.DETAIL:
                return new BIDetailWidget();
            case BIReportConstant.WIDGET.STRING:
                return new StringControlWidget();
            case BIReportConstant.WIDGET.LIST_LABEL:
                return new ListLabelWidget();
            case BIReportConstant.WIDGET.TREE:
                return new TreeWidget();
            case BIReportConstant.WIDGET.TREE_LABEL:
                return new TreeLabelWidget();
            default:
                return new MultiChartWidget();
        }
    }

    public static JSONArray getViewTarget(JSONObject view) throws Exception {
        JSONArray ja = new JSONArray();
        if (view.optJSONArray(BIReportConstant.REGION.TARGET1) != null) {
            ja.put(view.optJSONArray(BIReportConstant.REGION.TARGET1));
        }
        if (view.optJSONArray(BIReportConstant.REGION.TARGET2) != null) {
            ja.put(view.optJSONArray(BIReportConstant.REGION.TARGET1));
        }
        if (view.optJSONArray(BIReportConstant.REGION.TARGET3) != null) {
            ja.put(view.optJSONArray(BIReportConstant.REGION.TARGET3));
        }
        return ja;
    }

    /**
     * 根据属性选择生成不同的widget
     *
     * @param jo JSONObject 对象
     * @return BIWidget
     * @throws Exception
     */
    public static BIWidget parseWidget(JSONObject jo, long userId) throws Exception {
        return parseWidget(jo, userId, BICubeManager.getInstance().fetchCubeLoader(userId));
    }
}