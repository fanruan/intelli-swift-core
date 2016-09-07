package com.fr.bi.cal.analyze.report.report.widget.chart;

import com.fr.bi.cal.analyze.report.report.widget.MultiChartWidget;
import com.fr.bi.conf.report.map.BIMapInfoManager;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.field.BIAbstractTargetAndDimension;
import com.fr.bi.field.target.target.BISummaryTarget;
import com.fr.bi.stable.constant.BIChartSettingConstant;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.operation.group.IGroup;
import com.fr.bi.stable.operation.group.group.NoGroup;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.general.Inter;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by User on 2016/8/31.
 */
public class BIChartDataConvertFactory {

    private static SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd");

    public static JSONObject convert(MultiChartWidget widget, JSONObject data){
        JSONObject options = new JSONObject();
        int type = widget.getType();
        JSONArray types = new JSONArray();
        BIDimension categoryDimension = widget.getCategoryDimension();
        BIDimension seriesDimension = widget.getSeriesDimension();
        IGroup categoryGroup = categoryDimension != null ? categoryDimension.getGroup() : new NoGroup();
        IGroup seriesGroup = seriesDimension != null ? seriesDimension.getGroup() : new NoGroup();
        try {
            JSONObject drill = widget.getWidgetDrill();
            if(categoryDimension != null && drill.length() != 0){
                BIDimension drillcataDimension = widget.getDrillDimension(widget.getWidgetDrill().getJSONArray(categoryDimension.getValue()));
                categoryGroup = drillcataDimension.getGroup();
            }
            if(seriesDimension != null && drill.length() != 0){
                BIDimension drillseriDimension = widget.getDrillDimension(widget.getWidgetDrill().getJSONArray(seriesDimension.getValue()));
                seriesGroup = drillseriDimension.getGroup();
            }
            BISummaryTarget[] showTargets = widget.getViewTargets();
            JSONArray convertedData = parseSNDataToXYZData(widget, data, seriesGroup, categoryGroup, showTargets);

            int count = 0;

            for(int i = 0; i < convertedData.length(); i++){
                JSONArray t = new JSONArray();
                JSONArray da = convertedData.getJSONArray(i);
                for(int j = 0; j < da.length(); j++){
                    if(type == BIReportConstant.WIDGET.MULTI_AXIS_COMBINE_CHART || type == BIReportConstant.WIDGET.COMBINE_CHART){
                        JSONObject chart = new JSONObject();
                        if(showTargets[count].getChartSetting().getStyleOfChart().length() != 0){
                            chart = showTargets[count].getChartSetting().getStyleOfChart();
                        }else{
                            if(showTargets[0].getChartSetting().getStyleOfChart().length() != 0){
                                chart = showTargets[0].getChartSetting().getStyleOfChart();
                            }
                        }
                        if(chart.has("type")){
                            t.put(chart.getInt("type"));
                        }else{
                            t.put(BIReportConstant.WIDGET.AXIS);
                        }
                    }else{
                        t.put(BIReportConstant.WIDGET.AXIS);
                    }
                    count++;
                }
                types.put(t);
            }
            if(types.length() == 0){
                types.put(new JSONArray().put(type));
            }
            for(int i = 0; i < convertedData.length(); i++){
                String uuid = UUID.randomUUID().toString();
                JSONArray t = types.getJSONArray(i);
                JSONArray item = convertedData.getJSONArray(i);
                for(int j = 0; j < item.length(); j++){
                    if(t.getInt(j) == BIReportConstant.WIDGET.ACCUMULATE_AREA || t.getInt(j) == BIReportConstant.WIDGET.ACCUMULATE_AXIS){
                        item.getJSONObject(j).put("stack", uuid);
                    }
                }
            }
            if(type == BIReportConstant.WIDGET.MAP){
                String subType = widget.getSubType();
                BIMapInfoManager manager = BIMapInfoManager.getInstance();
                if(subType != null){
                    for (Map.Entry<String, Integer> entry : manager.getinnerMapLayer().entrySet()) {
                        if(entry.getValue() == 0){
                            subType = entry.getKey();
                            break;
                        }
                    }
                }
                String name = manager.getinnerMapTypeName().get(subType);
                if(name == null){
                    name = manager.getCustomMapTypeName().get(subType);
                }
                options.put("initDrillPath", new JSONArray().put(name));
                if(drill.names() != null){
                    JSONArray drillValue = drill.getJSONArray(drill.names().getString(0));
                    for(int i = 0; i < drillValue.length(); i++){
                        JSONObject dValue = drillValue.getJSONObject(i);
                        options.getJSONArray("initDrillPath").put(dValue.getJSONArray("values").getJSONObject(0).getJSONArray("value").getString(0));
                    }
                }
                String d = manager.getinnerMapPath().get(subType);
                String n = manager.getinnerMapTypeName().get(subType);
                if(d == null){
                    d = manager.getCustomMapPath().get(subType);
                }
                if(n == null){
                    n = manager.getCustomMapTypeName().get(subType);
                }
                options.put("geo", new JSONObject().put("data", d).put("name", n));
            }
            if(type == BIReportConstant.WIDGET.GIS_MAP){
                options.put("geo", new JSONObject().put("tileLayer", BIChartSettingConstant.GIS_MAP_PATH).put("attribution", BIChartSettingConstant.KNOWLEDGE_RIGHT));
            }
            return new JSONObject().put("types", types).put("data", convertedData).put("options", options);
        } catch (JSONException e) {
            BILogger.getLogger().error(e.getMessage());
        }
        return new JSONObject();
    }

    private static JSONArray parseSNDataToXYZData(MultiChartWidget widget, JSONObject data, IGroup seriesGroup, IGroup categoryGroup, BISummaryTarget[] showTarget) throws JSONException{
        JSONArray da;
        switch (widget.getType()) {
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
                return formatDataForAxis(widget, data, seriesGroup, categoryGroup, showTarget);
            case BIReportConstant.WIDGET.BUBBLE:
                return formatDataForBubble(widget, data, seriesGroup, categoryGroup, showTarget);
            case BIReportConstant.WIDGET.SCATTER:
                return formatDataForScatter(widget, data, seriesGroup, categoryGroup, showTarget);
            case BIReportConstant.WIDGET.MAP:
                da = formatDataForMap(widget, data, seriesGroup, categoryGroup, showTarget);
                return da.length() == 0 ? da : new JSONArray().put(da);
            case BIReportConstant.WIDGET.GIS_MAP:
                da = formatDataForGISMap(widget, data, seriesGroup, categoryGroup, showTarget);
                return da.length() == 0 ? da : new JSONArray().put(da);
            default:
                return new JSONArray();
        }
    }

    private static JSONArray formatDataForGISMap(MultiChartWidget widget, JSONObject data, IGroup seriesGroup, IGroup categoryGroup, BISummaryTarget[] showTarget) throws JSONException{
        if (data.has("t")) {
            JSONObject top = data.getJSONObject("t"), left = data.getJSONObject("l");
            JSONArray init = new JSONArray();
            JSONArray topC = top.getJSONArray("c");
            for (int i = 0; i < topC.length(); i++) {
                JSONObject tObj = topC.getJSONObject(i);
                JSONArray da = new JSONArray();
                JSONArray leftC = left.getJSONArray("c");
                for (int j = 0; j < leftC.length(); j++) {
                    JSONObject lObj = leftC.getJSONObject(j);
                    String x = lObj.getString("n");
                    JSONArray objSCS = lObj.getJSONObject("s").getJSONArray("c").getJSONObject(i).getJSONArray("s");
                    for(int k = 0; k < objSCS.length(); k++){
                        if(!objSCS.isNull(k) && x != null){
                            da.put(new JSONObject().put("x", x).put("y", objSCS.getDouble(k)).put("z", tObj.getString("n")));
                        }
                    }

                }
                init.put(new JSONObject().put("data", da).put("name", tObj.getString("n")));
            }
            JSONArray result = new JSONArray();
            int size = 0;
            if(init.length() > 0){
                size = showTarget.length;
            }
            for(int i = 0; i < size; i++){
                JSONObject res = new JSONObject().put("data", new JSONArray()).put("name", showTarget[i].getText());
                for(int j = 0; j < init.length(); j++){
                    JSONObject obj = init.getJSONObject(j);
                    res.getJSONArray("data").put(obj.getJSONArray("data").getJSONObject(i));
                }
                result.put(res);
            }
            return result;
        }
        if (data.has("c")) {
            JSONObject obj = data.getJSONArray("c").getJSONObject(0);
            int columnSizeArray = obj == null ? 0 : obj.getJSONArray("s").length();
            JSONArray result = new JSONArray();
            for(int i = 0; i < columnSizeArray; i++){
                JSONArray adjustData = new JSONArray();
                for(int j = 0; j < data.getJSONArray("c").length(); j++){
                    JSONObject item = data.getJSONArray("c").getJSONObject(j);
                    String x = item.getString("n");
                    double y = item.getJSONArray("s").getDouble(i);
                    adjustData.put(new JSONObject().put("x", x).put("y", (Double.isFinite(y) ? y : 0)));
                }
                result.put(new JSONObject().put("data", adjustData).put("name", showTarget[i].getText()));
            }
            return result;
        }
        return new JSONArray();
    }

    private static JSONArray formatDataForMap(MultiChartWidget widget, JSONObject data, IGroup seriesGroup, IGroup categoryGroup, BISummaryTarget[] showTarget) throws JSONException{
        JSONArray result = new JSONArray();
        if (data.has("c")) {
            JSONObject obj = data.getJSONArray("c").getJSONObject(0);
            Map<Integer, List<String>> view = widget.getWidgetView();
            int columnSizeArray = obj == null ? 0 : obj.getJSONArray("s").length();
            for(int i = 0; i < columnSizeArray; i++){
                int type = 0;
                if(view.containsKey(Integer.parseInt(BIReportConstant.REGION.TARGET2)) && view.get(Integer.parseInt(BIReportConstant.REGION.TARGET2)).contains(showTarget[i].getValue())){
                    type = BIReportConstant.WIDGET.BUBBLE;
                }
                JSONArray adjustData = new JSONArray();
                for(int j = 0; j < data.getJSONArray("c").length(); j++){
                    JSONObject item = data.getJSONArray("c").getJSONObject(j);
                    JSONObject res;
                    if(item.getJSONArray("s").isNull(i)){
                        continue;
                    }
                    double y = item.getJSONArray("s").getDouble(i);
                    if(view.containsKey(Integer.parseInt(BIReportConstant.REGION.TARGET2)) && view.get(Integer.parseInt(BIReportConstant.REGION.TARGET2)).contains(showTarget[i].getValue())){
                        switch (type) {
                            case BIReportConstant.WIDGET.BUBBLE:
                            case BIReportConstant.WIDGET.AXIS:
                            case BIReportConstant.WIDGET.PIE:
                            default:
                                res = new JSONObject("{ " +
                                        "x:" + item.getString("n") + "," +
                                        "y:" + (Double.isFinite(y) ? y : 0) + "}");
                                break;
                        }
                    }else{
                        res = new JSONObject("{ " +
                                "x:" + item.getString("n") + "," +
                                "y:" + (Double.isFinite(y) ? y : 0) + "," +
                                "settings:" + showTarget[i].getChartSetting().getSettings().toString() +
                                "}");
                    }
                    if(item.has("c")){
                        res.put("drilldown", new JSONObject());
                        res.getJSONObject("drilldown").put("series", formatDataForMap(widget, item, seriesGroup, categoryGroup, showTarget));
                        BIMapInfoManager manager = BIMapInfoManager.getInstance();
                        String t = manager.getinnerMapName().get(res.getString("x"));
                        res.getJSONObject("drilldown").put("geo", new JSONObject().put("data", (t == null ? manager.getCustomMapPath().get(manager.getCustomMapName().get(res.getString("x"))) :
                                manager.getinnerMapName().get(res.getString("x")))).put("name", res.getString("x")));
                    }
                    adjustData.put(res);
                }
                JSONObject o = new JSONObject();
                o.put("data", adjustData).put("name", showTarget[i].getText());
                if(type != 0){
                    o.put("type", "bubble");
                }
                o.put("settings", showTarget[i].getChartSetting().getSettings());
                result.put(o);
            }
            return result;
        }
        return new JSONArray();
    }

    private static JSONArray formatDataForScatter(MultiChartWidget widget, JSONObject data, IGroup seriesGroup, IGroup categoryGroup, BISummaryTarget[] showTarget) throws JSONException{
        if(!checkScatterValid(widget)){
            return new JSONArray();
        }
        if (data.has("c")) {
            JSONArray result = new JSONArray();
            JSONArray dataC = data.getJSONArray("c");
            for (int i = 0; i < dataC.length(); i++) {
                JSONObject obj = new JSONObject();
                JSONObject item = dataC.getJSONObject(i);
                String name = item.getString("n"), seriesName = item.getString("n");
                if (categoryGroup.getType() == BIReportConstant.GROUP.YMD) {
                    Date date = new Date();
                    date.setTime(Long.parseLong(name));
                    name = dateFormater.format(date);
                }
                double y = item.getJSONArray("s").getDouble(0);
                double x = item.getJSONArray("s").getDouble(1);
                JSONArray adjustData = new JSONArray();
                adjustData.put(new JSONObject("{ " +
                        "x:" + (Double.isFinite(x) ? x : 0) + "," +
                        "y:" + (Double.isFinite(y) ? y : 0) + "," +
                        "seriesName:" + seriesName + "," + "}"));
                obj.put("data", adjustData).put("name", name);
                result.put(obj);
            }
            return new JSONArray().put(result);
        }
        return new JSONArray();
    }

    private static JSONArray formatDataForBubble(MultiChartWidget widget, JSONObject data, IGroup seriesGroup, IGroup categoryGroup, BISummaryTarget[] showTarget) throws JSONException{
        if(!checkBubbleValid(widget)){
            return new JSONArray();
        }
        if (data.has("c")) {
            JSONArray result = new JSONArray();
            JSONArray dataC = data.getJSONArray("c");
            for (int i = 0; i < dataC.length(); i++) {
                JSONObject obj = new JSONObject();
                JSONObject item = dataC.getJSONObject(i);
                String name = item.getString("n"), seriesName = item.getString("n");
                if (categoryGroup.getType() == BIReportConstant.GROUP.YMD) {
                    Date date = new Date();
                    date.setTime(Long.parseLong(name));
                    name = dateFormater.format(date);
                }
                double z = item.getJSONArray("s").getDouble(2);
                double y = item.getJSONArray("s").getDouble(0);
                double x = item.getJSONArray("s").getDouble(1);
                JSONArray adjustData = new JSONArray();
                adjustData.put(new JSONObject("{ " +
                        "x:" + (Double.isFinite(x) ? x : 0) + "," +
                        "y:" + (Double.isFinite(y) ? y : 0) + "," +
                        "z:" + (Double.isFinite(z) ? z : 0) + "," +
                        "seriesName:" + seriesName + "," + "}"));
                obj.put("data", adjustData).put("name", name);
                result.put(obj);
            }
            return new JSONArray().put(result);
        }
        return new JSONArray();
    }

    private static JSONArray formatDataForAxis(MultiChartWidget widget, JSONObject data, IGroup seriesGroup, IGroup categoryGroup, BISummaryTarget[] showTarget) throws JSONException {
        JSONArray da = formatDataForCommon(widget, data, seriesGroup, categoryGroup, showTarget);
        if(da.length() == 0){
            return new JSONArray();
        }
        Map<Integer, List<String>> view = widget.getWidgetView();
        JSONArray array = new JSONArray();
        for(int i = 0; i < showTarget.length; i++){
            if(view.containsKey(Integer.parseInt(BIReportConstant.REGION.TARGET1)) && view.get(Integer.parseInt(BIReportConstant.REGION.TARGET1)).contains(showTarget[i].getValue())){
                if(widget.getSeriesDimension() != null){
                    if(array.length() == 0){
                        array.put(da);
                    }
                }else{
                    if(array.length() == 0){
                        array.put(new JSONArray().put(da.getJSONObject(i)));
                    }
                }
            }
            if(view.containsKey(Integer.parseInt(BIReportConstant.REGION.TARGET2)) && view.get(Integer.parseInt(BIReportConstant.REGION.TARGET2)).contains(showTarget[i].getValue())){
                if(widget.getSeriesDimension() != null){
                    if(array.length() < 2){
                        array.put(da);
                    }
                }else{
                    if(array.length() < 2){
                        array.put(new JSONArray().put(da.getJSONObject(i)));
                    }
                }
            }
            if(view.containsKey(Integer.parseInt(BIReportConstant.REGION.TARGET3)) && view.get(Integer.parseInt(BIReportConstant.REGION.TARGET3)).contains(showTarget[i].getValue())){
                if(widget.getSeriesDimension() != null){
                    if(array.length() < 3){
                        array.put(da);
                    }
                }else{
                    if(array.length() < 3){
                        array.put(new JSONArray().put(da.getJSONObject(i)));
                    }
                }
            }
        }
        return array;
    }

    private static boolean checkBubbleValid(MultiChartWidget widget){
        Map<Integer, List<String>> view = widget.getWidgetView();
        for (Map.Entry<Integer, List<String>> entry : view.entrySet()) {
            if(entry.getValue().size() == 0){
                return false;
            }
        }
        return view.size() == 4;
    }

    private static boolean checkScatterValid(MultiChartWidget widget){
        Map<Integer, List<String>> view = widget.getWidgetView();
        for (Map.Entry<Integer, List<String>> entry : view.entrySet()) {
            if(entry.getValue().size() == 0){
                return false;
            }
        }
        return view.size() == 3;
    }

    private static JSONArray formatDataForCommon(MultiChartWidget widget, JSONObject data, IGroup seriesGroup, IGroup categoryGroup, BISummaryTarget[] showTarget) throws JSONException {
        if (data.has("t")) {
            JSONObject top = data.getJSONObject("t"), left = data.getJSONObject("l");
            JSONArray result = new JSONArray();
            JSONArray topC = top.getJSONArray("c");
            for (int i = 0; i < topC.length(); i++) {
                JSONObject tObj = topC.getJSONObject(i);
                if(tObj.getJSONArray("c") == null){
                    result.put(new JSONObject().put("data", new JSONArray()).
                            put("name", widget.getDimensionById(tObj.getString("n"))));
                }
                String name = tObj.getString("n");
                String seriesName = tObj.getString("n");
                if (seriesGroup.getType() == BIReportConstant.GROUP.YMD) {
                    Date date = new Date();
                    date.setTime(Long.parseLong(name));
                    name = dateFormater.format(date);
                }
                JSONArray da = new JSONArray();
                JSONArray leftC = left.getJSONArray("c");
                for (int j = 0; j < leftC.length(); j++) {
                    JSONObject lObj = leftC.getJSONObject(j);
                    String value = lObj.getString("n"), x = lObj.getString("n");
                    if(categoryGroup.getType() == BIReportConstant.GROUP.YMD) {
                        Date date = new Date();
                        date.setTime(Long.parseLong(x));
                        x = dateFormater.format(date);
                    }
                    double y = lObj.getJSONObject("s").getJSONArray("c").getJSONObject(i).getJSONArray("s").getDouble(0);
                    da.put(new JSONObject("{ " +
                            "x:" + x + "," +
                            "y:" + (Double.isFinite(y) ? y : 0) + "," +
                            "value:" + value + "," +
                            "seriesName:" + seriesName + "}"));
                }
                result.put(new JSONObject().put("data", da).put("name", name));
            }
            return result;
        }
        if (data.has("c")) {
            JSONObject obj = data.getJSONArray("c").getJSONObject(0);
            int columnSizeArray = obj == null ? 0 : obj.getJSONArray("s").length();
            JSONArray result = new JSONArray();
            for(int i = 0; i < columnSizeArray; i++){
                JSONArray adjustData = new JSONArray();
                for(int j = 0; j < data.getJSONArray("c").length(); j++){
                    JSONObject item = data.getJSONArray("c").getJSONObject(j);
                    String value = item.getString("n"), x = item.getString("n");
                    if(categoryGroup.getType() == BIReportConstant.GROUP.YMD){
                        Date date = new Date();
                        date.setTime(Long.parseLong(x));
                        x = dateFormater.format(date);
                    }
                    double y = item.getJSONArray("s").getDouble(i);
                    adjustData.put(new JSONObject("{ " +
                            "x:" + x + "," +
                            "y:" + (Double.isFinite(y) ? y : 0) + "," +
                            "value:" + value + "," +
                            "seriesName:" + showTarget[i].getText() + "}"));
                }
                result.put(new JSONObject().put("data", adjustData).put("name", showTarget[i].getText()));
            }
            return result;
        }
        if (data.has("s")) {
            JSONArray result = new JSONArray();
            JSONArray sArray = data.getJSONArray("s");
            for(int i = 0; i < sArray.length(); i++){
                double y = sArray.getDouble(i);
                JSONArray da = new JSONArray();
                da.put(new JSONObject().put("x", "").put("y", Double.isInfinite(y) ? 0 : y));
                result.put(new JSONObject().put("name", showTarget[i].getText())
                .put("data", da));
            }
            return result;
        }
        return new JSONArray();
    }

    private String getToolTip(int type, BISummaryTarget[] showTargets){
        switch (type) {
            case BIReportConstant.WIDGET.SCATTER:
                if(showTargets.length < 2){
                    return "";
                }else{
                    return "function(){ return this.seriesName+'<div>(X)" + showTargets[1].getText() +":'+ this.x +'</div><div>(Y)"
                            + showTargets[0].getText() +":'+ this.y +'</div>'}";
                }
            case BIReportConstant.WIDGET.BUBBLE:
                if(showTargets.length < 3){
                    return "";
                }else{
                    return "function(){ return this.seriesName+'<div>(X)" + showTargets[1].getText() +":'+ this.x +'</div><div>(Y)"
                            + showTargets[0].getText() +":'+ this.y +'</div><div>(" + Inter.getLocText("BI-Size") + ")" + showTargets[2].getText()
                            + ":'+ this.size +'</div>'}";
                }
            default:
                return "";
        }
    }

    private JSONArray getCordon(MultiChartWidget widget, BIAbstractTargetAndDimension[] showDimensionAndTargets) throws JSONException{
        JSONObject cordon = new JSONObject();
        JSONArray result = new JSONArray();
        for(int i = 0; i < showDimensionAndTargets.length; i++){
            JSONArray items = new JSONArray();
            JSONArray cordons = showDimensionAndTargets[i].getChartSetting().getCordon();
            for(int j = 0; j < cordon.length(); j++){
                JSONObject cor = cordons.getJSONObject(j);
                items.put(new JSONObject("{" +
                    "text:" + cor.getString("cordon_name") + "," +
                    "value:" + cor.getString("cordon_value") + "," +
                    "color: " + cor.getString("cordon_color") + "}"
                ));
            }
            String regionType = widget.getRegionTypeByDimensionOrTarget(showDimensionAndTargets[i]).toString();
            if(items.length() > 0){
                if(!cordon.has(regionType)){
                    cordon.put(regionType, new JSONArray());
                }
                for(int j = 0; j < items.length(); j++){
                    cordon.getJSONArray(regionType).put(items.getJSONObject(j));
                }
            }

        }
        int type = widget.getType();
        if(type == BIReportConstant.WIDGET.SCATTER || type == BIReportConstant.WIDGET.BUBBLE){
            result.put(cordon.getJSONArray(BIReportConstant.REGION.TARGET2) == null ? new JSONArray() : cordon.getJSONArray(BIReportConstant.REGION.TARGET2));
            result.put(cordon.getJSONArray(BIReportConstant.REGION.TARGET1) == null ? new JSONArray() : cordon.getJSONArray(BIReportConstant.REGION.TARGET1));
            return result;
        }
        if(type == BIReportConstant.WIDGET.BAR || type == BIReportConstant.WIDGET.ACCUMULATE_BAR){
            result.put(cordon.getJSONArray(BIReportConstant.REGION.TARGET1) == null ? new JSONArray() : cordon.getJSONArray(BIReportConstant.REGION.TARGET1));
            result.put(cordon.getJSONArray(BIReportConstant.REGION.DIMENSION1) == null ? new JSONArray() : cordon.getJSONArray(BIReportConstant.REGION.DIMENSION1));
            return result;
        }
        if(type == BIReportConstant.WIDGET.COMPARE_BAR){
            JSONArray negativeAxis = cordon.getJSONArray(BIReportConstant.REGION.TARGET1) == null ? new JSONArray() : cordon.getJSONArray(BIReportConstant.REGION.TARGET1);
            JSONArray positiveAxis = cordon.getJSONArray(BIReportConstant.REGION.TARGET2) == null ? new JSONArray() : cordon.getJSONArray(BIReportConstant.REGION.TARGET2);
            for(int i = 0; i < positiveAxis.length(); i++){
                negativeAxis.put(positiveAxis.get(i));
            }
            result.put(negativeAxis);
            result.put(cordon.getJSONArray(BIReportConstant.REGION.DIMENSION1) == null ? new JSONArray() : cordon.getJSONArray(BIReportConstant.REGION.DIMENSION1));
            return result;
        }
        result.put(cordon.getJSONArray(BIReportConstant.REGION.DIMENSION1) == null ? new JSONArray() : cordon.getJSONArray(BIReportConstant.REGION.DIMENSION1));
        result.put(cordon.getJSONArray(BIReportConstant.REGION.TARGET1) == null ? new JSONArray() : cordon.getJSONArray(BIReportConstant.REGION.TARGET1));
        result.put(cordon.getJSONArray(BIReportConstant.REGION.TARGET2) == null ? new JSONArray() : cordon.getJSONArray(BIReportConstant.REGION.TARGET2));
        result.put(cordon.getJSONArray(BIReportConstant.REGION.TARGET3) == null ? new JSONArray() : cordon.getJSONArray(BIReportConstant.REGION.TARGET3));
        return result;
    }
}
