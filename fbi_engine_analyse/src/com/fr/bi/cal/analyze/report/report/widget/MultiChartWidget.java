package com.fr.bi.cal.analyze.report.report.widget;

import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.field.target.target.BISummaryTarget;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.operation.sort.comp.ChinesePinyinComparator;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

import java.util.*;

/**
 * Created by User on 2016/4/25.
 */
public class MultiChartWidget extends TableWidget {

    private int type;
    private String subType;
    private Map<Integer, List<String>> view = new HashMap<Integer, List<String>>();
    private Map<String, BIDimension> dimensionsIdMap = new HashMap<String, BIDimension>();
    private Map<String, BISummaryTarget> targetsIdMap = new HashMap<String, BISummaryTarget>();
    private Map<String, JSONArray> clicked = new HashMap<String, JSONArray>();

    @Override
    public void parseJSON(JSONObject jo, long userId) throws Exception {
        if (jo.has("view")) {
            JSONObject vjo = jo.optJSONObject("view");
            parseView(vjo);
            JSONArray ja = new JSONArray();
            Iterator it = vjo.keys();
            List<String> sorted = new ArrayList<String>();
            while (it.hasNext()) {
                sorted.add(it.next().toString());
            }
            Collections.sort(sorted, new ChinesePinyinComparator());
            for(String region : sorted){
                if(ComparatorUtils.equals(region, BIReportConstant.REGION.DIMENSION1) ||
                        ComparatorUtils.equals(region, BIReportConstant.REGION.DIMENSION2)){
                    continue;
                }
                JSONArray tmp =  vjo.getJSONArray(region);
                for(int j = 0; j < tmp.length(); j++){
                    ja.put(tmp.getString(j));
                }
            }
            vjo.remove(BIReportConstant.REGION.TARGET2);
            vjo.remove(BIReportConstant.REGION.TARGET3);
            vjo.put(BIReportConstant.REGION.TARGET1, ja);
        }
        if(jo.has("type")){
            type = jo.getInt("type");
        }
        if(jo.has("sub_type")){
            subType = jo.getString("sub_type");
        }
        if(jo.has("clicked")){
            JSONObject c = jo.getJSONObject("clicked");
            Iterator it = c.keys();
            while (it.hasNext()){
                String key = it.next().toString();
                clicked.put(key, c.getJSONArray(key));
            }
        }
        super.parseJSON(jo, userId);
        createDimensionAndTargetMap();
    }

    private void createDimensionAndTargetMap() {
        for(BIDimension dimension : this.getDimensions()){
            for (Map.Entry<Integer, List<String>> entry : view.entrySet()) {
                Integer key = entry.getKey();
                if(key <= Integer.parseInt(BIReportConstant.REGION.DIMENSION2)){
                    List<String> dIds = entry.getValue();
                    if(dIds.contains(dimension.getValue())){
                        dimensionsIdMap.put(dimension.getValue(), dimension);
                        break;
                    }
                }
            }
        }
        for(BISummaryTarget target : this.getTargets()){
            for (Map.Entry<Integer, List<String>> entry : view.entrySet()) {
                Integer key = entry.getKey();
                if(key >= Integer.parseInt(BIReportConstant.REGION.TARGET1)){
                    List<String> dIds = entry.getValue();
                    if(dIds.contains(target.getValue())){
                        targetsIdMap.put(target.getValue(), target);
                        break;
                    }
                }
            }
        }
    }

    @Override
    public int getType() {
        return type;
    }

    private void parseView(JSONObject jo) throws Exception {
        Iterator it = jo.keys();
        while (it.hasNext()) {
            Integer region = Integer.parseInt(it.next().toString());
            List<String> dimensionIds = new ArrayList<String>();
            view.put(region, dimensionIds);
            JSONArray tmp =  jo.getJSONArray(region.toString());
            for(int j = 0; j < tmp.length(); j++){
                dimensionIds.add(tmp.getString(j));
            }
        }
    }

    public BIDimension getCategoryDimension(){
        List<String> dimensionIds = view.get(Integer.parseInt(BIReportConstant.REGION.DIMENSION1));
        if(dimensionIds == null){
            return null;
        }
        for(BIDimension dimension : this.getDimensions()){
            if(dimensionIds.contains(dimension.getValue()) && dimension.isUsed()){
                return dimension;
            }
        }
        return null;
    }

    public BIDimension getSeriesDimension(){
        List<String> dimensionIds = view.get(Integer.parseInt(BIReportConstant.REGION.DIMENSION2));
        if(dimensionIds == null){
            return null;
        }
        for(BIDimension dimension : this.getDimensions()){
            if(dimensionIds.contains(dimension.getValue()) && dimension.isUsed()){
                return dimension;
            }
        }
        return null;
    }

    public Set<String> getAllDimensionIds(){
        Set<String> dimensionIds = new HashSet<String>();
        for(BIDimension dimension : this.getDimensions()){
            dimensionIds.add(dimension.getValue());
        }
        return dimensionIds;
    }

    public Set<String> getAllTargetIds(){
        Set<String> targetIds = new HashSet<String>();
        for(BISummaryTarget target : this.getTargets()){
            targetIds.add(target.getValue());
        }
        return targetIds;
    }

    public JSONObject getWidgetDrill() throws JSONException {
        JSONObject drills = new JSONObject();
        Set<String> dimensionIds = this.getAllDimensionIds();

        for (Map.Entry<String, JSONArray> entry : clicked.entrySet()) {
            String dId = entry.getKey();
            if(dimensionIds.contains(dId)){
                drills.put(dId, entry.getValue());
            }
        }
        return drills;
    }

    public BIDimension getDrillDimension(JSONArray drill) throws JSONException {
        if (drill == null || drill.length() == 0) {
            return null;
        }
        String id = drill.getJSONObject(drill.length() - 1).getString("dId");
        return dimensionsIdMap.get(id);
    }

    public BISummaryTarget getSummaryTargetById(String targetId){
        return targetsIdMap.get(targetId);
    }

    public BIDimension getDimensionById(String id){
        for (BIDimension dimension : this.getDimensions()){
            if(ComparatorUtils.equals(dimension.getValue(), id)){
                return dimension;
            }
        }
        return null;
    }

    public BISummaryTarget getTargetById(String id){
        for (BISummaryTarget target : this.getTargets()){
            if(ComparatorUtils.equals(target.getValue(), id)){
                return target;
            }
        }
        return null;
    }

    public Map<Integer, List<String>> getWidgetView(){
        return view;
    }

    public Integer getRegionTypeByDimension(BIDimension dimension){
        for (Map.Entry<Integer, List<String>> entry : view.entrySet()) {
            if(entry.getKey() <= Integer.parseInt(BIReportConstant.REGION.DIMENSION2)){
                Integer key = entry.getKey();
                List<String> dIds = entry.getValue();
                if (dIds.contains(dimension.getValue())) {
                    return key;
                }
            }
        }
        return null;
    }

    public Integer getRegionTypeByTarget(BISummaryTarget target){
        for (Map.Entry<Integer, List<String>> entry : view.entrySet()) {
            if(entry.getKey() >= Integer.parseInt(BIReportConstant.REGION.TARGET1)){
                Integer key = entry.getKey();
                List<String> dIds = entry.getValue();
                if (dIds.contains(target.getValue())) {
                    return key;
                }
            }
        }
        return null;
    }

    public String getSubType(){
        return subType;
    }

}