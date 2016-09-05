package com.fr.bi.cal.analyze.report.report.widget.chart;

import com.fr.bi.cal.analyze.report.report.widget.MultiChartWidget;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.field.target.target.BISummaryTarget;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by User on 2016/8/31.
 */
public class BIChartDataConvertFactory {

    private static SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd");

    public static JSONObject convert(MultiChartWidget widget, JSONObject data){
        JSONObject options = new JSONObject();
        int type = widget.getType();

        BIDimension cataDimension = widget.getCategoryDimension();
        BIDimension seriesDimension = widget.getSeriesDimension();
        try {
            BIDimension drillcataDimension = widget.getDrillDimension(widget.getWidgetDrill().getJSONArray(cataDimension.getValue()));
            BIDimension drillseriDimension = widget.getDrillDimension(widget.getWidgetDrill().getJSONArray(seriesDimension.getValue()));
            BISummaryTarget[] showTargets = widget.getViewTargets();
            //parseChartData(data);
        } catch (JSONException e) {
            BILogger.getLogger().error(e.getMessage());
        }
//        var cataGroup = BI.isNull(widget.dimensions[cataDid]) ? null : widget.dimensions[cataDid].group;
//        var seriesGroup = BI.isNull(widget.dimensions[seriesDid]) ? null : widget.dimensions[seriesDid].group;
//        if (BI.isNotNull(drillcataDimId)) {
//            cataGroup = widget.dimensions[drillcataDimId].group;
//        }
//        if (BI.isNotNull(drillseriDimId)) {
//            seriesGroup = widget.dimensions[drillseriDimId].group;
//        }
//        var targetIds = getShowTarget();
//        var data = parseChartData(data);
//        var types = [];
//        var count = 0;
//        BI.each(data, function (idx, da) {
//            var t = [];
//            BI.each(da, function (id, d) {
//                if (type === BICst.WIDGET.MULTI_AXIS_COMBINE_CHART || type === BICst.WIDGET.COMBINE_CHART) {
//                    var chart = widget.dimensions[targetIds[count] || targetIds[0]].style_of_chart || {};
//                    t.push(chart.type || BICst.WIDGET.AXIS);
//                } else {
//                    t.push(type);
//                }
//                count++;
//            });
//            types.push(t);
//        });
//        if (BI.isEmptyArray(types)) {
//            types.push([type]);
//        }
//        BI.each(data, function (idx, item) {
//            var i = BI.UUID();
//            var type = types[idx];
//            BI.each(item, function (id, it) {
//                (type[id] === BICst.WIDGET.ACCUMULATE_AREA || type[id] === BICst.WIDGET.ACCUMULATE_AXIS) && BI.extend(it, {stack: i});
//            });
//        });
//        if (type === BICst.WIDGET.MAP) {
//            var subType = widget.sub_type || MapConst.INNER_MAP_INFO.MAP_NAME[BI.i18nText("BI-China")];
//            options.initDrillPath = [MapConst.INNER_MAP_INFO.MAP_TYPE_NAME[subType]];
//            var drill = BI.values(getDrill())[0];
//            BI.each(drill, function (idx, dri) {
//                options.initDrillPath.push(dri.values[0].value[0]);
//            });
//            options.geo = {
//                    data: MapConst.INNER_MAP_INFO.MAP_PATH[subType],
//                    name: MapConst.INNER_MAP_INFO.MAP_TYPE_NAME[subType] || MapConst.INNER_MAP_INFO.MAP_TYPE_NAME[BI.i18nText("BI-China")]
//            }
//        }
//        if (type === BICst.WIDGET.GIS_MAP) {
//            options.geo = {
//                    "tileLayer": "http://webrd01.is.autonavi.com/appmaptile?lang=zh_cn&size=1&scale=1&style=8&x={x}&y={y}&z={z}"
//            };
//        }
//        var dimensionIds = BI.keys(widget.dimensions);
//        var lnglat = null;
//        if (dimensionIds.length !== 0) {
//            lnglat = widget.dimensions[dimensionIds[0]].position;
//        }
//        var click = function (obj) {
//            op.click(obj);
//        };
//        var ws = widget.settings || {};
//        return {
//                types: types,
//                data: data,
//                options: BI.extend(ws, {
//                click: click
//        }, options, {
//                cordon: getCordon(),
//                tooltip: getToolTip(),
//                lnglat: BI.isNotNull(lnglat) ? lnglat.type : lnglat
//        })
//        };
        return null;
    }

    public static JSONArray parseSNDataToXYZData(int type, JSONObject data){
        JSONArray da;
        switch (type) {
            case BIReportConstant.WIDGET.ACCUMULATE_AXIS:
            case BIReportConstant.WIDGET.ACCUMULATE_AREA:
            case BIReportConstant.WIDGET.ACCUMULATE_RADAR:
            case BIReportConstant.WIDGET.AXIS:
            case BIReportConstant.WIDGET.LINE:
            case BIReportConstant.WIDGET.AREA:
            case BIReportConstant.WIDGET.PERCENT_ACCUMULATE_AXIS:
            case BIReportConstant.WIDGET.PERCENT_ACCUMULATE_AREA:
            case BIReportConstant.WIDGET.COMPARE_AXIS:
            case BIReportConstant.WIDGET.COMPARE_AREA:
            case BIReportConstant.WIDGET.FALL_AXIS:
            case BIReportConstant.WIDGET.RANGE_AREA:
            case BIReportConstant.WIDGET.BAR:
            case BIReportConstant.WIDGET.ACCUMULATE_BAR:
            case BIReportConstant.WIDGET.COMPARE_BAR:
            case BIReportConstant.WIDGET.COMBINE_CHART:
            case BIReportConstant.WIDGET.DONUT:
            case BIReportConstant.WIDGET.RADAR:
            case BIReportConstant.WIDGET.PIE:
            case BIReportConstant.WIDGET.MULTI_AXIS_COMBINE_CHART:
            case BIReportConstant.WIDGET.FORCE_BUBBLE:
            case BIReportConstant.WIDGET.DASHBOARD:
                return formatDataForAxis(data);
            case BIReportConstant.WIDGET.BUBBLE:
                return formatDataForBubble(data);
            case BIReportConstant.WIDGET.SCATTER:
                return formatDataForScatter(data);
            case BIReportConstant.WIDGET.MAP:
                da = formatDataForMap(data, 0);
                return da.length() == 0 ? da : new JSONArray().put(da);
            case BIReportConstant.WIDGET.GIS_MAP:
                da = formatDataForGISMap(data);
                return da.length() == 0 ? da : new JSONArray().put(da);
            default:
                return new JSONArray();
        }
    }

    private static JSONArray formatDataForGISMap(JSONObject data) {
        return new JSONArray();
    }

    private static JSONArray formatDataForMap(JSONObject data, int i) {
        return new JSONArray();
    }

    private static JSONArray formatDataForScatter(JSONObject data) {
        return new JSONArray();
    }

    private static JSONArray formatDataForBubble(JSONObject data) {
        return new JSONArray();
    }

    private static JSONArray formatDataForAxis(JSONObject data) {
        return new JSONArray();
    }

    private static JSONArray formatDataForCommon(MultiChartWidget widget, JSONObject data, BIDimension seriesDimension) throws JSONException {
        if (data.has("t")) {
            JSONObject top = data.getJSONObject("t"), left = data.getJSONObject("l");
            JSONArray result = new JSONArray();
            JSONArray topC = top.getJSONArray("c");
            for (int i = 0; i < topC.length(); i++) {
                JSONObject tObj = topC.getJSONObject(i);
                if(tObj.getJSONObject("c") == null){
                    result.put(new JSONObject().put("data", new JSONArray()).
                            put("name", widget.getDimensionById(tObj.getString("n"))));
                }
                String name = tObj.getString("n");
                String seriesName = tObj.getString("n");
                if (seriesDimension.getGroup().getType() == BIReportConstant.GROUP.YMD) {
                    Date date = new Date();
                    date.setTime(Long.parseLong(name));
                    name = dateFormater.format(date);
                }
                JSONArray da = new JSONArray();
                JSONArray leftC = left.getJSONArray("c");
                for (int j = 0; i < leftC.length(); i++) {
                    JSONObject lObj = leftC.getJSONObject(i);
                }

            }
//            return BI.map(top.c, function (id, tObj) {
//                var name = tObj.n, seriesName = tObj.n;
//                if (BI.isNotNull(seriesGroup) && seriesGroup.type === BICst.GROUP.YMD) {
//                    var date = new Date(BI.parseInt(name));
//                    name = date.print("%Y-%X-%d");
//                }
//                var data = BI.map(left.c, function (idx, obj) {
//                    var value = obj.n, x = obj.n;
//                    if (BI.isNotNull(cataGroup) && cataGroup.type === BICst.GROUP.YMD) {
//                        var date = new Date(BI.parseInt(x));
//                        x = date.print("%Y-%X-%d");
//                    }
//                    return {
//                            "x": x,
//                            "y": (BI.isFinite(obj.s.c[id].s[0]) ? obj.s.c[id].s[0] : 0),
//                            "value": value,
//                            seriesName: seriesName,
//                            targetIds: [targetIds[0]]
//                    };
//                });
//                var obj = {};
//                obj.data = data;
//                obj.name = name;
//                return obj;
//            });
//        }
//        if (BI.has(data, "c")) {
//            var obj = (data.c)[0];
//            var columnSizeArray = BI.makeArray(BI.isNull(obj) ? 0 : BI.size(obj.s), 0);
//            return BI.map(columnSizeArray, function (idx, value) {
//                var adjustData = BI.map(data.c, function (id, item) {
//                    var value = item.n, x = item.n;
//                    if (BI.isNotNull(cataGroup) && cataGroup.type === BICst.GROUP.YMD) {
//                        var date = new Date(BI.parseInt(x));
//                        x = date.print("%Y-%X-%d");
//                    }
//                    return {
//                            x: x,
//                            y: (BI.isFinite(item.s[idx]) ? item.s[idx] : 0),
//                            value: value,
//                            seriesName: widget.dimensions[targetIds[idx]].name,
//                            targetIds: [targetIds[idx]]
//                    };
//                });
//                var obj = {};
//                obj.data = adjustData;
//                obj.name = widget.dimensions[targetIds[idx]].name;
//                return obj;
//            });
//        }
//        if (BI.has(data, "s")) {
//            return BI.map(data.s, function (idx, value) {
//                return {
//                        name: widget.dimensions[targetIds[idx]].name,
//                        data: [{
//                    x: "",
//                            y: (BI.isFinite(value) ? value : 0),
//                            targetIds: [targetIds[idx]]
//                }]
//                };
//            });
//
//        }
//        return new JSONArray();
//
        }
        return new JSONArray();

    }
}
