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
            case BIReportConstant.WIDGET.BAR: {
                return new TableWidget();
            }
            case BIReportConstant.WIDGET.MAP: {
                return new TableWidget();
            }
            case BIReportConstant.WIDGET.AXIS: {
                return new MultiChartWidget();
            }
            case BIReportConstant.WIDGET.ACCUMULATE_BAR: {
                return new TableWidget();
            }
            case BIReportConstant.WIDGET.PIE: {
                return new TableWidget();
            }
            case BIReportConstant.WIDGET.DASHBOARD: {
                return new TableWidget();
            }
            case BIReportConstant.WIDGET.DONUT: {
                return new TableWidget();
            }
            case BIReportConstant.WIDGET.TABLE: {
                return new TableWidget();
            }
            case BIReportConstant.WIDGET.DETAIL: {
                return new BIDetailWidget();
            }
            case BIReportConstant.WIDGET.BUBBLE: {
                return new MultiChartWidget();
            }
            case BIReportConstant.WIDGET.SCATTER: {
                return new MultiChartWidget();
            }
            case BIReportConstant.WIDGET.RADAR: {
                return new TableWidget();
            }
            case BIReportConstant.WIDGET.STRING: {
                return new StringControlWidget();
            }
            case BIReportConstant.WIDGET.TREE: {
                return new TreeWidget();
            }
            default: {
                return new TableWidget();
            }
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