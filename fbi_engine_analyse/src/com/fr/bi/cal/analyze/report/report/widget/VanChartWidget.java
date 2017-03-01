package com.fr.bi.cal.analyze.report.report.widget;

import com.fr.bi.cal.analyze.executor.BIEngineExecutor;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.conf.session.BISessionProvider;
import com.fr.bi.field.target.target.BISummaryTarget;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.operation.group.IGroup;
import com.fr.bi.stable.operation.group.group.NoGroup;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.web.core.SessionDealWith;
import sun.misc.DoubleConsts;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by User on 2016/4/25.
 */
public class VanChartWidget extends TableWidget {

    private static SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

    public BIDimension getCategoryDimension() {
        List<String> dimensionIds = view.get(Integer.parseInt(BIReportConstant.REGION.DIMENSION1));
        if (dimensionIds == null) {
            return null;
        }
        for (BIDimension dimension : this.getDimensions()) {
            if (dimensionIds.contains(dimension.getValue()) && dimension.isUsed()) {
                return dimension;
            }
        }
        return null;
    }

    public BIDimension getSeriesDimension() {
        List<String> dimensionIds = view.get(Integer.parseInt(BIReportConstant.REGION.DIMENSION2));
        if (dimensionIds == null) {
            return null;
        }
        for (BIDimension dimension : this.getDimensions()) {
            if (dimensionIds.contains(dimension.getValue()) && dimension.isUsed()) {
                return dimension;
            }
        }
        return null;
    }

    public BIDimension getDimensionById(String id) {
        for (BIDimension dimension : this.getDimensions()) {
            if (ComparatorUtils.equals(dimension.getValue(), id)) {
                return dimension;
            }
        }
        return null;
    }

    public BISummaryTarget getTargetById(String id) {
        for (BISummaryTarget target : this.getTargets()) {
            if (ComparatorUtils.equals(target.getValue(), id)) {
                return target;
            }
        }
        return null;
    }

    public boolean isDimensionUsable(String id) {
        BIDimension dimension = getDimensionById(id);
        if (dimension != null) {
            return dimension.isUsed();
        }
        BISummaryTarget target = getTargetById(id);
        if (target != null) {
            return target.isUsed();
        }
        return false;
    }

    public List<BISummaryTarget> getUsedTargets() {
        List<BISummaryTarget> targets = new ArrayList<BISummaryTarget>();
        BISummaryTarget[] sourceTarget = this.getTargets();
        for (int i = 0; i < sourceTarget.length; i++) {
            if (sourceTarget[i].isUsed()) {
                targets.add(sourceTarget[i]);
            }
        }
        return targets;
    }

    public Integer getRegionTypeByDimension(BIDimension dimension) {
        for (Map.Entry<Integer, List<String>> entry : view.entrySet()) {
            if (entry.getKey() <= Integer.parseInt(BIReportConstant.REGION.DIMENSION2)) {
                Integer key = entry.getKey();
                List<String> dIds = entry.getValue();
                if (dIds.contains(dimension.getValue())) {
                    return key;
                }
            }
        }
        return null;
    }

    public Integer getRegionTypeByTarget(BISummaryTarget target) {
        for (Map.Entry<Integer, List<String>> entry : view.entrySet()) {
            if (entry.getKey() >= Integer.parseInt(BIReportConstant.REGION.TARGET1)) {
                Integer key = entry.getKey();
                List<String> dIds = entry.getValue();
                if (dIds.contains(target.getValue())) {
                    return key;
                }
            }
        }
        return null;
    }

    public JSONObject getPostOptions(String sessionId) throws Exception {
        JSONObject dataJSON = this.createDataJSON((BISessionProvider) SessionDealWith.getSessionIDInfor(sessionId));
        JSONObject chartOptions = createOptions(dataJSON.optJSONObject("data"));
        JSONObject plotOptions = chartOptions.optJSONObject("plotOptions");
        plotOptions.put("animation", false);
        chartOptions.put("plotOptions", plotOptions);
        return chartOptions;
    }

    public JSONObject createDataJSON(BISessionProvider session) throws Exception {
        //用来做测试
        return new JSONObject("{\n" +
                "                    \"plotOptions\":{\n" +
                "\n" +
                "                    },\n" +
                "                    \"chartType\": \"column\",\n" +
                "\n" +
                "                    \"series\": [\n" +
                "                        {\n" +
                "                            \"data\": [\n" +
                "                                {\n" +
                "                                    \"x\": \"孙林\",\n" +
                "                                    \"y\": 140\n" +
                "                                }\n" +
                "                            ],\n" +
                "                            \"name\": \"苹果汁\"\n" +
                "                        }\n" +
                "                    ]\n" +
                "                };");
    }

    public JSONObject createOptions(JSONObject data){
        return JSONObject.create();
    }

    protected JSONArray createXYSeries(JSONObject data) throws JSONException{
        JSONArray series = JSONArray.create();
        BIDimension categoryDimension = this.getCategoryDimension();
        BIDimension seriesDimension = this.getSeriesDimension();
        IGroup categoryGroup = categoryDimension != null ? categoryDimension.getGroup() : new NoGroup();
        IGroup seriesGroup = seriesDimension != null ? seriesDimension.getGroup() : new NoGroup();

        return series;
    }

}