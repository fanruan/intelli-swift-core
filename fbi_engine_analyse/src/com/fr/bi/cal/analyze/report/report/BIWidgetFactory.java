package com.fr.bi.cal.analyze.report.report;

import com.finebi.cube.api.BICubeManager;
import com.finebi.cube.api.ICubeDataLoader;
import com.fr.bi.cal.analyze.report.report.widget.*;
import com.fr.bi.cal.analyze.report.report.widget.chart.VanChartUtils;
import com.fr.bi.conf.report.BIWidget;
import com.fr.bi.conf.report.WidgetType;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;

import java.util.HashMap;

/**
 * Widget的静态方法
 *
 * @author Daniel-pc
 */
public class BIWidgetFactory {


    private static HashMap widgets = new HashMap<WidgetType, BIWidget>();



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
        JSONArray viewTargets = getViewTarget(view);
        BIWidget widget = newWidgetByType(WidgetType.parse(jo.optInt("type")), viewTargets);
        widget.parseJSON(jo, userId);
        return widget;
    }

    public static BIWidget newWidgetByType(WidgetType type, JSONArray viewTargets) throws Exception {
        BIWidget biWidget = null;
        switch (type) {
            case TABLE:
            case CROSS_TABLE:
            case COMPLEX_TABLE:
                biWidget = new TableWidget();
                break;
            case DETAIL:
                biWidget = new BIDetailWidget();
                break;
            case STRING:
            case STRING_LIST:
                biWidget = new StringControlWidget();
                break;
            case LIST_LABEL:
                biWidget = new ListLabelWidget();
                break;
            case TREE:
            case TREE_LIST:
                biWidget = new TreeWidget();
                break;
            case TREE_LABEL:
                biWidget = new TreeLabelWidget();
                break;
            case SINGLE_SLIDER:
            case INTERVAL_SLIDER:
                biWidget = new SingleSliderWidget();
                break;
            default:
                biWidget = new VanChartWidget();
        }

        return biWidget;
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