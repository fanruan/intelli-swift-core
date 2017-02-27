package com.fr.bi.cal.analyze.report.report.widget;

import com.fr.bi.cal.analyze.report.report.widget.chart.BIChartDataConvertFactory;
import com.fr.bi.cal.analyze.report.report.widget.chart.BIChartSettingFactory;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.conf.session.BISessionProvider;
import com.fr.bi.field.target.target.BISummaryTarget;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.operation.sort.comp.ChinesePinyinComparator;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.web.core.SessionDealWith;

import java.util.*;

/**
 * Created by User on 2016/4/25.
 */
public class VanChartWidget extends TableWidget {

    private int type;
    private String subType;

    @Override
    public void parseJSON(JSONObject jo, long userId) throws Exception {
        if (jo.has("view")) {
            JSONObject vjo = jo.optJSONObject("view");
            JSONArray ja = new JSONArray();
            JSONArray rectJa = new JSONArray();
            Iterator it = vjo.keys();
            List<String> sorted = new ArrayList<String>();
            while (it.hasNext()) {
                sorted.add(it.next().toString());
            }
            Collections.sort(sorted, new ChinesePinyinComparator());
            for (String region : sorted) {
//                if(ComparatorUtils.equals(region, BIReportConstant.REGION.DIMENSION1) ||
//                        ComparatorUtils.equals(region, BIReportConstant.REGION.DIMENSION2)){
//                    continue;
//                }
                int regionValue = Integer.parseInt(region);
                if (regionValue >= Integer.parseInt(BIReportConstant.REGION.DIMENSION1) &&
                        regionValue < Integer.parseInt(BIReportConstant.REGION.TARGET1)) {
                    if (jo.optInt("type") == BIReportConstant.WIDGET.RECT_TREE) {
                        JSONArray tmp = vjo.getJSONArray(region);
                        for (int j = 0; j < tmp.length(); j++) {
                            rectJa.put(tmp.getString(j));
                        }
                    }
                    continue;
                }
                JSONArray tmp = vjo.getJSONArray(region);
                for (int j = 0; j < tmp.length(); j++) {
                    ja.put(tmp.getString(j));
                }
            }
            if (jo.optInt("type") == BIReportConstant.WIDGET.RECT_TREE) {
                vjo.remove(BIReportConstant.REGION.DIMENSION2);
                vjo.put(BIReportConstant.REGION.DIMENSION1, rectJa);
            }
            vjo.remove(BIReportConstant.REGION.TARGET2);
            vjo.remove(BIReportConstant.REGION.TARGET3);
            vjo.put(BIReportConstant.REGION.TARGET1, ja);
        }
        if (jo.has("type")) {
            type = jo.getInt("type");
        }
        if (jo.has("subType")) {
            subType = jo.getString("subType");
        }
        super.parseJSON(jo, userId);
    }


    @Override
    public int getType() {
        return type;
    }


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

    public String getSubType() {
        return subType;
    }

    public JSONObject getPostOptions(String sessionId) throws Exception {
        JSONObject dataJSON = this.createDataJSON((BISessionProvider) SessionDealWith.getSessionIDInfor(sessionId));
        JSONObject data = dataJSON.optJSONObject("data");
        JSONObject chartOptions = parseChartSetting(data);
        return resetAnimation(chartOptions);
    }

    private JSONObject resetAnimation(JSONObject chartOptions) throws Exception {
        //将plotOptions下的animation设为false否则不能截图（只截到网格线）
        JSONObject plotOptions = (JSONObject) chartOptions.get("plotOptions");
        plotOptions.put("animation", false);
        chartOptions.put("plotOptions", plotOptions);
        return chartOptions;
    }

    private JSONObject parseChartSetting(JSONObject data) throws Exception {
        JSONObject convert = BIChartDataConvertFactory.convert(this, data);
        return BIChartSettingFactory.parseChartSetting(this, convert.getJSONArray("data"), convert.optJSONObject("options"), convert.getJSONArray("types"));
    }
}